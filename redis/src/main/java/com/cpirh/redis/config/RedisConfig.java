package com.cpirh.redis.config;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Objects;


/**
 * @author ronghui
 * @Description 需要重写一下redis的实现，保证redis序列化与反序列化正常转换
 * @date 2023/2/21 11:13
 */
@Configuration
public class RedisConfig {
    @Autowired
    private RedisProperties redisProperties;


    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        String address = new StringBuilder("redis://").append(redisProperties.getHost()).append(":").append(redisProperties.getPort()).toString();
        config.useSingleServer().setAddress(address);
        if (StrUtil.isNotEmpty(redisProperties.getPassword())) {
            config.useSingleServer().setPassword(redisProperties.getPassword());
        }
        if (Objects.nonNull(redisProperties.getDatabase())) {
            config.useSingleServer().setDatabase(redisProperties.getDatabase());
        }
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;

    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> getRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(factory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer); // key的序列化类型
        redisTemplate.setHashKeySerializer(stringRedisSerializer);// hash key的序列化类型
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 方法过期，改为下面代码
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer); // value的序列化类型
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer); // hash value的序列化类型
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}