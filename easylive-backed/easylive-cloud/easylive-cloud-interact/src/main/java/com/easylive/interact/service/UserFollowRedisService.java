package com.easylive.interact.service;

import com.easylive.base.exception.BusinessException;
import com.easylive.base.service.UserFollowService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Service
public class UserFollowRedisService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserFollowService userFollowService;

    @Lazy
    @Resource
    private UserFollowRedisService self;

    private static final String FOLLOWING_SET = "user:following:";
    private static final String FANS_SET = "user:fans:";
    private static final String FOLLOW_COUNT_KEY = "user:follow:count";
    private static final String FANS_COUNT_KEY = "user:fans:count";

    // Lua：关注（原子）
    private final String followScript =
            "if redis.call('sadd', KEYS[1], ARGV[1]) == 1 then " +
                    "   redis.call('sadd', KEYS[2], ARGV[2]) " +
                    "   redis.call('hincrby', KEYS[3], ARGV[2], 1) " +
                    "   redis.call('hincrby', KEYS[4], ARGV[1], 1) " +
                    "   return 1 " +
                    "else " +
                    "   return 0 " +
                    "end";

    // Lua：取消关注
    private final String unfollowScript =
            "if redis.call('srem', KEYS[1], ARGV[1]) == 1 then " +
                    "   redis.call('srem', KEYS[2], ARGV[2]) " +
                    "   redis.call('hincrby', KEYS[3], ARGV[2], -1) " +
                    "   redis.call('hincrby', KEYS[4], ARGV[1], -1) " +
                    "   return 1 " +
                    "else " +
                    "   return 0 " +
                    "end";

    public void follow(String followerId, String followingId) {
        List<String> keys = Arrays.asList(
                FOLLOWING_SET + followerId,
                FANS_SET + followingId,
                FOLLOW_COUNT_KEY,
                FANS_COUNT_KEY
        );
        Long result = stringRedisTemplate.execute(
                new DefaultRedisScript<>(followScript, Long.class),
                keys, followingId, followerId
        );
        if (result == 0) {
            throw new BusinessException("已经关注过了");
        }
        self.syncFollowToDb(followerId, followingId);
    }

    public void unfollow(String followerId, String followingId) {
        List<String> keys = Arrays.asList(
                FOLLOWING_SET + followerId,
                FANS_SET + followingId,
                FOLLOW_COUNT_KEY,
                FANS_COUNT_KEY
        );
        Long result = stringRedisTemplate.execute(
                new DefaultRedisScript<>(unfollowScript, Long.class),
                keys, followingId, followerId
        );
        if (result == 0) {
            throw new BusinessException("还没有关注");
        }
        self.syncUnfollowToDb(followerId, followingId);
    }

    @Async
    public void syncFollowToDb(String followerId, String followingId) {
        userFollowService.followUser(followerId, followingId);
    }

    @Async
    public void syncUnfollowToDb(String followerId, String followingId) {
        userFollowService.unfollowUser(followerId, followingId);
    }

    // ========= 查询方法 =========

    public boolean isFollowing(String followerId, String followingId) {
        return Boolean.TRUE.equals(
                stringRedisTemplate.opsForSet().isMember(FOLLOWING_SET + followerId, followingId)
        );
    }

    public long getFollowCount(String userId) {
        String count = (String) stringRedisTemplate.opsForHash().get(FOLLOW_COUNT_KEY, userId);
        return count == null ? 0 : Long.parseLong(count);
    }

    public long getFansCount(String userId) {
        String count = (String) stringRedisTemplate.opsForHash().get(FANS_COUNT_KEY, userId);
        return count == null ? 0 : Long.parseLong(count);
    }
}