package com.easylive.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easylive.base.entity.po.PlayHistory;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.mapper.PlayHistoryMapper;
import com.easylive.base.service.PlayHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author licheng
 * @since 2026-04-21
 */
@Service
public class PlayHistoryServiceImpl extends ServiceImpl<PlayHistoryMapper, PlayHistory> implements PlayHistoryService {
    @Override
    public List<PlayHistory> loadHistory(String userId, Integer pageNo, Integer pageSize) {
        Page<PlayHistory> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<PlayHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlayHistory::getUserId, userId).orderByDesc(PlayHistory::getUpdateTime);
        return this.page(page, wrapper).getRecords();
    }

    @Override
    @Transactional
    public void addOrUpdateHistory(String userId, String videoId, String episodeId, Integer watchTime) {
        LambdaQueryWrapper<PlayHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlayHistory::getUserId, userId).eq(PlayHistory::getVideoId, videoId);
        PlayHistory exist = this.getOne(wrapper);
        if (exist == null) {
            PlayHistory history = new PlayHistory();
            history.setUserId(userId);
            history.setVideoId(videoId);
            history.setEpisodeId(episodeId);
            history.setWatchTime(watchTime);
            history.setCreateTime(LocalDateTime.now());
            history.setUpdateTime(LocalDateTime.now());
            this.save(history);
        } else {
            exist.setEpisodeId(episodeId);
            exist.setWatchTime(watchTime);
            exist.setUpdateTime(LocalDateTime.now());
            this.updateById(exist);
        }
    }

    @Override
    @Transactional
    public void delHistory(Long historyId, String userId) {
        PlayHistory history = this.getById(historyId);
        if (history == null || !history.getUserId().equals(userId)) {
            throw new BusinessException("历史记录不存在或无权删除");
        }
        this.removeById(historyId);
    }

    @Override
    @Transactional
    public void clearHistory(String userId) {
        LambdaQueryWrapper<PlayHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlayHistory::getUserId, userId);
        this.remove(wrapper);
    }
}
