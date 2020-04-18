package com.ecnu.wwl.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;

@Configuration
public class RedisConfig extends CachingConfigurerSupport {

//    /*
//    * 自定义缓存key的生成策略，默认的生成策略是看不懂的（乱码）。
//    * 通过Spring 的依赖注入特性进行自定义的配置注入并且此类是一个配置类可以更多程度的自定义配置
//    * */
//    @Bean
//    @Override
//    public KeyGenerator keyGenerator() {
//        return new KeyGenerator() {
//            @Override
//            public Object generate(Object target, Method method, Object... params) {
//                StringBuilder sb = new StringBuilder();
//                sb.append(target.getClass().getName());
//                for (Object obj : params){
//                    sb.append(obj.toString());
//                }
//                return sb.toString();
//            }
//        };
//    }
//
//        /*
//        * 缓存配置管理器
//        * */
//    @Bean
//    public CacheManager cacheManager(LettuceConnectionFactory factory){
//        //以锁写入的方式创建RedisCachewriter对象
//        RedisCacheWriter writer = RedisCacheWriter.lockingRedisCacheWriter(factory);
//        //创建默认缓存配置对象
//        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
//        RedisCacheManager cacheManager = new RedisCacheManager(writer,config);
//        return cacheManager;
//    }


    //    上面这些都可以不要，只要template
    @Bean
    public RedisTemplate<String,Object> redisTemplate(LettuceConnectionFactory factory){
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
//        下面是配置key和value序列化的配置。
//        因为默认模版的序列化采用的是jdkSerial，存到数据库里会乱码
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
//        在使用注解@Bean返回RedisTemplate的时候，同时配置hashKey与hashValue的序列化方式。
//        key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
//        value采用jackson序列化方式
        template.setValueSerializer(jackson2JsonRedisSerializer);

//        hash的key也采用String
        template.setHashKeySerializer(stringRedisSerializer);
//        hash的value采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }
}