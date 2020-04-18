package com.ecnu.wwl.service.impl;

import com.ecnu.wwl.pojo.User;
import com.ecnu.wwl.utils.KeyNameUtil;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl {
    /* Redis String 类型
     * 需求：用户输入一个key
     * 先判断Redis中是否存在该数据
     * 如果存在，在Redis中进行查询并返回
     * 如果不存在，在MySQL数据库查询，将结果赋给Redis并返回
     */
    @Autowired
    RedisTemplate redisTemplate;
//    ValueOperations<String, String> string = redisTemplate.opsForValue();
    @Resource(name = "redisTemplate") //跟 配置文件定义的 redisTemplate方法 名字一样，因为@Resource是按名字搜索装配的
    private ValueOperations<String, String> string;
//    HashOperations<String, String, User> hash = redisTemplate.opsForHash();
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, User> hash;
    public String getString(String key){
        if (redisTemplate.hasKey(key)) {    //exists
            log.info("from Redis");
//            return (String)redisTemplate.opsForValue().get(key);
            return  string.get(key); //这里的string是ValueOperations类，并且范型已经指定，返回的就是String类型无需强制转化
        }else{
            String val = "RedisTemplate模版学习--lettuce客户端";
            log.info("from MySQL");
//            redisTemplate.opsForValue().set(key,val); // set key value
            string.set(key,val);
            return val;
        }
    }

    /*
     * 测试String类型
     * 需求：用户输入一个redis数据，该key的有效期为28小时
     * */
    public void expireStr(String key,String value){
//        redisTemplate.opsForValue().set(key,value);
        string.set(key, value);
        redisTemplate.expire(key,28, TimeUnit.HOURS);
    }

    /*
     * 测试Hash类型
     * */
    public User selectById(String id){
//        String key = "User:"+id; Jedis要这么做
//        但是Lettuce对命名规范的操作做了封装
//        opsForHash中 @Param h 就是用户实体 user，就是存的hash的key
//        @Param o 就是主键 id，就是hash的key
        if (hash.hasKey(KeyNameUtil.USER,id)){
            log.info("select from Redis");
            return hash.get(KeyNameUtil.USER,id);
        }else{
            User u = new User(id,"wwl",20);
            log.info("select from MySQL");
            /*
            * @Param h 用户实体(hash) 是user存在数据库里的key就是user，是student存在数据库里的key就是student
            * @Param hk 用户主键（hash-key） id 是存在数据库的hash数据类型的key
            * @Param hv 整个对象 (hash-value) 是hash数据类型的value，是一个对象
            * */
            hash.put(User.getKeyName(),id,u);
            return u;
        }
    }
}

/*
* 问题1:如果存的是hash类型的话对于不同的对象就要填入不同的字符串
*   答1：声明一个工具类定义各种name
*   答2：在实体bean声明里声明一个方法获取name
* 问题2:强制类型转换问题 redisTemplate.opsForValue() 返回的是 ValueOperations<K,V>类型，可以将范型确定类型并给一个别名
*   答：
* 问题3:redisTemplate.opsForHash(),每次都写这样一长串
* */
