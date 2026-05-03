package com.easylive.base.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Redis 通用工具类
 * 优化点：
 * 1. 统一泛型为 Object，适配所有类型
 * 2. 替换 e.printStackTrace() 为日志打印
 * 3. 增加参数校验，提高健壮性
 * 4. 优化方法命名和注释，提高可读性
 */
@Component("redisUtils")
public class RedisUtils {

    // 按名称注入，与 RedisConfig 中的 Bean 名称保持一致
    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    /**
     * 删除缓存
     * @param key 可以传一个或多个 key
     */
    public void delete(String... key) {
        if (key == null || key.length == 0) {
            logger.warn("删除缓存失败：key 不能为空");
            return;
        }
        try {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));
            }
            logger.debug("删除缓存成功，keys：{}", (Object) key);
        } catch (Exception e) {
            logger.error("删除缓存失败，keys：{}", (Object) key, e);
        }
    }

    /**
     * 获取缓存
     * @param key 键
     * @return 值（null 表示不存在）
     */
    public Object get(String key) {
        if (key == null) {
            logger.warn("获取缓存失败：key 不能为空");
            return null;
        }
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            logger.error("获取缓存失败，key：{}", key, e);
            return null;
        }
    }

    /**
     * 普通缓存放入（无过期时间）
     * @param key 键
     * @param value 值
     * @return true 成功 false 失败
     */
    public boolean set(String key, Object value) {
        if (key == null || value == null) {
            logger.warn("设置缓存失败：key 或 value 不能为空");
            return false;
        }
        try {
            redisTemplate.opsForValue().set(key, value);
            logger.debug("设置缓存成功，key：{}", key);
            return true;
        } catch (Exception e) {
            logger.error("设置缓存失败，key：{}", key, e);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置过期时间
     * @param key 键
     * @param value 值
     * @param expireSeconds 过期时间（秒），<=0 时无过期时间
     * @return true 成功 false 失败
     */
    public boolean setex(String key, Object value, long expireSeconds) {
        if (key == null || value == null) {
            logger.warn("设置缓存失败：key 或 value 不能为空");
            return false;
        }
        try {
            if (expireSeconds > 0) {
                redisTemplate.opsForValue().set(key, value, expireSeconds, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            logger.debug("设置缓存成功，key：{}，过期时间：{}秒", key, expireSeconds);
            return true;
        } catch (Exception e) {
            logger.error("设置缓存失败，key：{}，过期时间：{}秒", key, expireSeconds, e);
            return false;
        }
    }

    /**
     * 设置缓存过期时间
     * @param key 键
     * @param expireSeconds 过期时间（秒）
     * @return true 成功 false 失败
     */
    public boolean expire(String key, long expireSeconds) {
        if (key == null || expireSeconds <= 0) {
            logger.warn("设置过期时间失败：key 不能为空且过期时间必须大于0");
            return false;
        }
        try {
            redisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
            logger.debug("设置缓存过期时间成功，key：{}，过期时间：{}秒", key, expireSeconds);
            return true;
        } catch (Exception e) {
            logger.error("设置缓存过期时间失败，key：{}", key, e);
            return false;
        }
    }

    /**
     * List 缓存移除指定元素
     * @param key 键
     * @param value 要移除的元素
     * @return 移除的数量
     */
    public long remove(String key, Object value) {
        if (key == null || value == null) {
            logger.warn("移除List元素失败：key 或 value 不能为空");
            return 0;
        }
        try {
            Long removeCount = redisTemplate.opsForList().remove(key, 1, value);
            logger.debug("移除List元素成功，key：{}，移除数量：{}", key, removeCount);
            return removeCount == null ? 0 : removeCount;
        } catch (Exception e) {
            logger.error("移除List元素失败，key：{}", key, e);
            return 0;
        }
    }

    /**
     * List 缓存批量左推元素并设置过期时间
     * @param key 键
     * @param values 元素列表
     * @param expireSeconds 过期时间（秒），<=0 时无过期时间
     * @return true 成功 false 失败
     */
    public boolean lpushAll(String key, List<Object> values, long expireSeconds) {
        if (key == null || CollectionUtils.isEmpty(values)) {
            logger.warn("批量左推List失败：key 不能为空且元素列表不能为空");
            return false;
        }
        try {
            redisTemplate.opsForList().leftPushAll(key, values);
            if (expireSeconds > 0) {
                expire(key, expireSeconds);
            }
            logger.debug("批量左推List成功，key：{}，元素数量：{}", key, values.size());
            return true;
        } catch (Exception e) {
            logger.error("批量左推List失败，key：{}", key, e);
            return false;
        }
    }

    /**
     * List 缓存单个元素左推并设置过期时间
     * @param key 键
     * @param value 单个元素
     * @param expireSeconds 过期时间（秒），<=0 时无过期时间
     * @return true 成功 false 失败
     */
    public boolean lpush(String key, Object value, long expireSeconds) {
        if (key == null || value == null) {
            logger.warn("左推List元素失败：key 或 value 不能为空");
            return false;
        }
        try {
            redisTemplate.opsForList().leftPush(key, value);
            if (expireSeconds > 0) {
                expire(key, expireSeconds);
            }
            logger.debug("左推List元素成功，key：{}", key);
            return true;
        } catch (Exception e) {
            logger.error("左推List元素失败，key：{}", key, e);
            return false;
        }
    }

    /**
     * 判断 key 是否存在
     * @param key 键
     * @return true 存在 false 不存在
     */
    public boolean exists(String key) {
        if (key == null) {
            return false;
        }
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            logger.error("判断key是否存在失败，key：{}", key, e);
            return false;
        }
    }

    /**
     * 获取 List 缓存所有元素
     * @param key 键
     * @return 元素列表（空列表表示无数据）
     */
    public List<Object> getQueueList(String key) {
        if (key == null) {
            logger.warn("获取List缓存失败：key 不能为空");
            return Collections.emptyList(); // 返回空列表而非 null，避免空指针
        }
        try {
            return redisTemplate.opsForList().range(key, 0, -1);
        } catch (Exception e) {
            logger.error("获取List缓存失败，key：{}", key, e);
            return Collections.emptyList();
        }
    }
}