package com.easylive.interact.service;

import com.easylive.base.exception.BusinessException;
import com.easylive.base.service.UserActionService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Service
public class UserActionRedisService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserActionService userActionService;

    // 注入自身代理，解决内部调用 @Async 不生效的问题
    @Lazy
    @Resource
    private UserActionRedisService self;

    private static final String LIKE_USERS_KEY = "video:like:users:";
    private static final String LIKE_COUNT_KEY = "video:like:count";
    private static final String USER_LIKES_KEY = "user:likes:";

    // Lua 脚本：点赞（原子操作）
    private final String likeScript =
            "if redis.call('sadd', KEYS[1], ARGV[1]) == 1 then " +
                    "   redis.call('hincrby', KEYS[2], ARGV[2], 1) " +
                    "   redis.call('sadd', KEYS[3], ARGV[2]) " +
                    "   return 1 " +
                    "else " +
                    "   return 0 " +
                    "end";

    // Lua 脚本：取消点赞
    private final String unlikeScript =
            "if redis.call('srem', KEYS[1], ARGV[1]) == 1 then " +
                    "   redis.call('hincrby', KEYS[2], ARGV[2], -1) " +
                    "   redis.call('srem', KEYS[3], ARGV[2]) " +
                    "   return 1 " +
                    "else " +
                    "   return 0 " +
                    "end";

    /**
     * 点赞
     */
    public void like(String videoId, String userId) {
        List<String> keys = Arrays.asList(
                LIKE_USERS_KEY + videoId,
                LIKE_COUNT_KEY,
                USER_LIKES_KEY + userId
        );
        Long result = stringRedisTemplate.execute(
                new DefaultRedisScript<>(likeScript, Long.class),
                keys, userId, videoId
        );
        if (result == 0) {
            throw new BusinessException("已经点过赞了");
        }
        // 通过代理调用异步方法，真正异步写库
        self.syncLikeToDb(videoId, userId);
    }

    /**
     * 取消点赞
     */
    public void unlike(String videoId, String userId) {
        List<String> keys = Arrays.asList(
                LIKE_USERS_KEY + videoId,
                LIKE_COUNT_KEY,
                USER_LIKES_KEY + userId
        );
        Long result = stringRedisTemplate.execute(
                new DefaultRedisScript<>(unlikeScript, Long.class),
                keys, userId, videoId
        );
        if (result == 0) {
            throw new BusinessException("还没有点过赞");
        }
        self.syncUnlikeToDb(videoId, userId);
    }

    // ---- 异步持久化方法（由代理调用） ----

    @Async
    public void syncLikeToDb(String videoId, String userId) {
        // 调用原有数据库操作，保证最终一致性
        userActionService.like(videoId, userId);
    }

    @Async
    public void syncUnlikeToDb(String videoId, String userId) {
        userActionService.unlike(videoId, userId);
    }
}