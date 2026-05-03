package com.easylive.base.redis;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 核心配置类
 */
@Configuration
public class RedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${spring.redis.host:127.0.0.1}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private Integer redisPort;

    @Value("${spring.redis.password:}")
    private String redisPassword;

    /**
     * 创建 RedissonClient Bean
     * destroyMethod = "shutdown"：Spring 容器销毁时自动关闭 Redisson 连接
     */
    @Bean(name = "redissonClient", destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        try {
            Config config = new Config();
            // 拼接 Redis 地址（必须以 redis:// 开头）
            String redisAddress = String.format("redis://%s:%d", redisHost, redisPort);

            // 单节点配置
            config.useSingleServer()
                    .setAddress(redisAddress)
                    .setPassword(redisPassword.isEmpty() ? null : redisPassword)
                    .setTimeout(2000)          // 连接超时时间
                    .setRetryAttempts(3)       // 重试次数
                    .setRetryInterval(1000);   // 重试间隔

            RedissonClient redissonClient = Redisson.create(config);
            logger.info("Redisson 连接成功，地址：{}", redisAddress);
            return redissonClient;
        } catch (Exception e) {
            logger.error("Redisson 配置错误，请检查 Redis 连接信息", e);
            // 抛出运行时异常，让 Spring 明确感知配置失败，避免注入 null
            throw new RuntimeException("Redisson 客户端初始化失败", e);
        }
    }

    /**
     * 配置 RedisTemplate
     * 优化序列化方式，解决 JSON 序列化类型丢失问题
     */
    @Bean("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 1. 字符串序列化器（Key/HashKey）
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        // 2. JSON 序列化器（Value/HashValue），支持复杂对象类型转换
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        // 设置 Key 序列化
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        // 设置 Value 序列化
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
}