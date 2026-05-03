package com.easylive.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easylive.base.entity.po.Danmu;
import com.easylive.base.mapper.DanmuMapper;
import com.easylive.base.service.DanmuService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DanmuServiceImpl extends ServiceImpl<DanmuMapper, Danmu> implements DanmuService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private DanmuMapper danmuMapper;

    @Lazy
    @Resource
    private DanmuServiceImpl self;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // Redis 缓存 Key
    private static final String DANMU_LIST_PREFIX = "danmu:list:";          // 按视频缓存最新弹幕（List）
    private static final String DANMU_TEMP_SET = "danmu:temp:ids";          // 临时存放待入库弹幕的ID（避免重复处理）
    private static final int BATCH_SIZE = 50;                               // 批量入库大小

    /**
     * 发送弹幕（先写 Redis，再异步批量落库）
     */
    @Override
    public void saveDanmu(Danmu danmu) {
        // 补全必要字段
        if (danmu.getCreateTime() == null) {
            danmu.setCreateTime(LocalDateTime.now());
        }
        if (danmu.getType() == null) {
            danmu.setType(1); // 默认滚动弹幕
        }
        if (danmu.getColor() == null) {
            danmu.setColor("#FFFFFF");
        }

        // 将弹幕对象序列化为 JSON，推入对应视频的 Redis List
        String videoKey = DANMU_LIST_PREFIX + danmu.getVideoId();
        try {
            String json = OBJECT_MAPPER.writeValueAsString(danmu);
            stringRedisTemplate.opsForList().rightPush(videoKey, json);
            // 同时保留列表最大长度，避免内存溢出（例如保留最新 500 条）
            stringRedisTemplate.opsForList().trim(videoKey, -500, -1);

            // 标记为待入库
            String tempId = danmu.getVideoId() + ":" + System.nanoTime();
            stringRedisTemplate.opsForSet().add(DANMU_TEMP_SET, tempId);
        } catch (JsonProcessingException e) {
            log.error("弹幕序列化失败", e);
            // 降级：直接写库
            danmuMapper.insert(danmu);
        }

        // 异步批量入库由定时任务处理，也可以在这里触发
    }

    /**
     * 获取弹幕列表（最近弹幕从 Redis 取，历史从 DB 取，保证实时性和完整性）
     */
    @Override
    public List<Danmu> getDanmuList(String videoId, String episodeId) {
        // 1. 先取 Redis 中最近的弹幕（最多 200 条）
        String videoKey = DANMU_LIST_PREFIX + videoId;
        List<String> redisDanmuJson = stringRedisTemplate.opsForList().range(videoKey, -200, -1);
        List<Danmu> redisDanmus = new ArrayList<>();
        if (!CollectionUtils.isEmpty(redisDanmuJson)) {
            for (String json : redisDanmuJson) {
                try {
                    Danmu danmu = OBJECT_MAPPER.readValue(json, Danmu.class);
                    if (episodeId == null || episodeId.equals(danmu.getEpisodeId())) {
                        redisDanmus.add(danmu);
                    }
                } catch (Exception e) {
                    log.error("弹幕反序列化失败", e);
                }
            }
        }

        // 2. 如果 Redis 数据足够（>=200），直接返回；否则补查数据库历史弹幕
        if (redisDanmus.size() >= 200) {
            return redisDanmus;
        }

        // 3. 从 DB 查询历史弹幕，按时间升序，取最近 500 条
        LambdaQueryWrapper<Danmu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Danmu::getVideoId, videoId)
                .eq(episodeId != null, Danmu::getEpisodeId, episodeId)
                .orderByDesc(Danmu::getCreateTime)
                .last("LIMIT 500");
        List<Danmu> dbDanmus = danmuMapper.selectList(wrapper);

        // 合并并去重（以 id 或 time 去重）
        Set<String> existIds = redisDanmus.stream()
                .map(d -> d.getVideoId() + ":" + d.getCreateTime().toString())
                .collect(Collectors.toSet());
        for (Danmu db : dbDanmus) {
            if (!existIds.contains(db.getVideoId() + ":" + db.getCreateTime().toString())) {
                redisDanmus.add(db);
            }
        }
        return redisDanmus;
    }

    /**
     * 管理员分页查询弹幕（直接查库，无需 Redis）
     */
    @Override
    public List<Danmu> getDanmuListForAdmin(String videoId, Integer pageNo, Integer pageSize) {
        Page<Danmu> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<Danmu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Danmu::getVideoId, videoId).orderByDesc(Danmu::getCreateTime);
        IPage<Danmu> result = danmuMapper.selectPage(page, wrapper);
        return result.getRecords();
    }

    /**
     * 管理员删除弹幕（直接操作 DB，并尝试从 Redis 中移除）
     */
    @Override
    @Transactional
    public void deleteDanmuByAdmin(Long danmuId) {
        Danmu danmu = danmuMapper.selectById(danmuId);
        if (danmu != null) {
            danmuMapper.deleteById(danmuId);
            // 尝试从 Redis 列表中移除该弹幕（遍历复杂度较高，可忽略或采用标记删除）
            removeFromRedisIfExists(danmu);
        }
    }

    // ==================== 异步批量入库 ====================

    /**
     * 定时任务：每 5 秒执行一次，将 Redis 中待入库的弹幕批量写入数据库
     */
    @Async
    @Scheduled(fixedDelay = 5000)
    public void batchInsertDanmuToDb() {
        // 获取所有待处理的临时标记
        Set<String> tempIds = stringRedisTemplate.opsForSet().members(DANMU_TEMP_SET);
        if (CollectionUtils.isEmpty(tempIds)) {
            return;
        }

        // 遍历每个视频的弹幕列表，批量入库
        List<Danmu> toInsert = new ArrayList<>();
        for (String tempId : tempIds) {
            // tempId 格式： videoId:timestamp，我们只需要 videoId
            String videoId = tempId.split(":")[0];
            String videoKey = DANMU_LIST_PREFIX + videoId;
            // 从 Redis List 中取出批量大小的弹幕（不移除，先插入再裁剪）
            List<String> jsonList = stringRedisTemplate.opsForList().range(videoKey, 0, BATCH_SIZE - 1);
            if (CollectionUtils.isEmpty(jsonList)) {
                continue;
            }
            for (String json : jsonList) {
                try {
                    Danmu danmu = OBJECT_MAPPER.readValue(json, Danmu.class);
                    // 防止重复插入：检查数据库中是否已存在（简单去重）
                    if (danmu.getDanmuId() == null || danmuMapper.selectById(danmu.getDanmuId()) == null) {
                        toInsert.add(danmu);
                    }
                } catch (Exception e) {
                    log.error("批量入库解析失败", e);
                }
            }
        }

        if (!CollectionUtils.isEmpty(toInsert)) {
            try {
                // 批量插入（使用 MyBatis-Plus 的批量插入需配置，或逐条插入）
                for (Danmu danmu : toInsert) {
                    danmuMapper.insert(danmu);
                }
                log.info("批量入库弹幕 {} 条", toInsert.size());
            } catch (Exception e) {
                log.error("批量入库异常", e);
            }
        }

        // 清理临时标记
        stringRedisTemplate.delete(DANMU_TEMP_SET);
    }

    // 辅助：从 Redis 移除指定弹幕（简单遍历，生产环境可优化）
    private void removeFromRedisIfExists(Danmu target) {
        String videoKey = DANMU_LIST_PREFIX + target.getVideoId();
        List<String> list = stringRedisTemplate.opsForList().range(videoKey, 0, -1);
        if (list != null) {
            for (String json : list) {
                try {
                    Danmu d = OBJECT_MAPPER.readValue(json, Danmu.class);
                    if (d.getDanmuId().equals(target.getDanmuId())) {
                        stringRedisTemplate.opsForList().remove(videoKey, 1, json);
                        break;
                    }
                } catch (Exception ignored) {}
            }
        }
    }
}