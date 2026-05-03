package com.easylive.interact.service;

import com.easylive.base.exception.BusinessException;
import com.easylive.base.service.UserCollectionService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class UserCollectionRedisService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserCollectionService collectionService;

    @Lazy
    @Resource
    private UserCollectionRedisService self;

    private static final String USER_COLLECTIONS = "user:collections:";
    private static final String VIDEO_COLLECTED_COUNT = "video:collected:count";
    private static final String USER_COLLECTION_TIME = "user:collection:time:";

    // Lua：收藏（原子）
    private final String collectScript =
            "if redis.call('sadd', KEYS[1], ARGV[1]) == 1 then " +
                    "   redis.call('hincrby', KEYS[2], ARGV[1], 1) " +
                    "   redis.call('zadd', KEYS[3], ARGV[2], ARGV[1]) " +
                    "   return 1 " +
                    "else " +
                    "   return 0 " +
                    "end";

    // Lua：取消收藏
    private final String uncollectScript =
            "if redis.call('srem', KEYS[1], ARGV[1]) == 1 then " +
                    "   redis.call('hincrby', KEYS[2], ARGV[1], -1) " +
                    "   redis.call('zrem', KEYS[3], ARGV[1]) " +
                    "   return 1 " +
                    "else " +
                    "   return 0 " +
                    "end";

    public void collect(String videoId, String userId) {
        long now = System.currentTimeMillis();
        List<String> keys = Arrays.asList(
                USER_COLLECTIONS + userId,
                VIDEO_COLLECTED_COUNT,
                USER_COLLECTION_TIME + userId
        );
        Long result = stringRedisTemplate.execute(
                new DefaultRedisScript<>(collectScript, Long.class),
                keys, videoId, String.valueOf(now)
        );
        if (result == 0) {
            throw new BusinessException("已经收藏过了");
        }
        self.syncCollectToDb(userId, videoId);
    }

    public void uncollect(String videoId, String userId) {
        List<String> keys = Arrays.asList(
                USER_COLLECTIONS + userId,
                VIDEO_COLLECTED_COUNT,
                USER_COLLECTION_TIME + userId
        );
        Long result = stringRedisTemplate.execute(
                new DefaultRedisScript<>(uncollectScript, Long.class),
                keys, videoId
        );
        if (result == 0) {
            throw new BusinessException("还没有收藏");
        }
        self.syncUncollectToDb(userId, videoId);
    }

    /**
     * 分页获取用户收藏的视频ID列表（按时间倒序）
     */
    public List<String> getUserCollectionIds(String userId, int pageNo, int pageSize) {
        int start = (pageNo - 1) * pageSize;
        int end = start + pageSize - 1;
        Set<String> ids = stringRedisTemplate.opsForZSet()
                .reverseRange(USER_COLLECTION_TIME + userId, start, end);
        return new ArrayList<>(ids);
    }

    @Async
    public void syncCollectToDb(String userId, String videoId) {
        collectionService.collect(videoId, userId);
    }

    @Async
    public void syncUncollectToDb(String userId, String videoId) {
        collectionService.uncollect(userId, videoId);
    }
}