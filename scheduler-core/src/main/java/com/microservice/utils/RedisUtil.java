package com.microservice.utils;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author: zhangwei
 * @date: 2019-12-09
 * @description: redis工具类
 */
@Service
@Slf4j
public class RedisUtil {

    private final Object OBJ = new Object();

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private RedisTemplate<String, Object> objectRedisTemplate;

    /**
     * 查询key,支持模糊查询
     *
     * @param key 传过来时key的前后端已经加入了*，或者根据具体处理
     */
    public Set<String> keys(String key) {
        return redisTemplate.keys(key);
    }

    /**
     * 字符串获取值
     *
     * @param key
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 字符串存入值
     * 默认过期时间为2小时
     *
     * @param key
     */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value, Constants.REDIS_CACHE_INVALID_TIME, TimeUnit.MILLISECONDS);
    }

    /**
     * 字符串存入值
     *
     * @param expire 过期时间（毫秒计）
     * @param key
     */
    public void set(String key, String value, Long expire) {
        redisTemplate.opsForValue().set(key, value, expire, TimeUnit.MILLISECONDS);
    }

    /**
     * 删出key
     * 这里跟下边deleteKey（）最底层实现都是一样的，应该可以通用
     *
     * @param key
     */
    public void delete(String key) {
        redisTemplate.opsForValue().getOperations().delete(key);
    }

    /**
     * 添加单个
     * 默认过期时间为两小时
     *
     * @param key    key
     * @param filed  filed
     * @param domain 对象
     */
    public void hset(String key, String filed, Object domain) {
        redisTemplate.opsForHash().put(key, filed, domain);
    }

    /**
     * 添加单个
     *
     * @param key    key
     * @param filed  filed
     * @param domain 对象
     * @param expire 过期时间（毫秒计）
     */
    public void hset(String key, String filed, Object domain, Integer expire) {
        redisTemplate.opsForHash().put(key, filed, domain);
        redisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }

    /**
     * 添加HashMap
     *
     * @param key key
     * @param hm  要存入的hash表
     */
    public void hset(String key, HashMap<String, Object> hm) {
        redisTemplate.opsForHash().putAll(key, hm);
    }

    /**
     * 如果key存在就不覆盖
     *
     * @param key
     * @param filed
     * @param domain
     */
    public void hsetAbsent(String key, String filed, Object domain) {
        redisTemplate.opsForHash().putIfAbsent(key, filed, domain);
    }

    /**
     * 查询key和field所确定的值
     *
     * @param key   查询的key
     * @param field 查询的field
     * @return HV
     */
    public Object hget(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 查询该key下所有值
     *
     * @param key 查询的key
     * @return Map<HK, HV>
     */
    public Object hget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 删除key下所有值
     *
     * @param key 查询的key
     */
    public void deleteKey(String key) {
        redisTemplate.opsForHash().getOperations().delete(key);
    }

    /**
     * 判断key和field下是否有值
     *
     * @param key   判断的key
     * @param field 判断的field
     */
    public Boolean hasKey(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    /**
     * 判断key下是否有值
     *
     * @param key 判断的key
     */
    public Boolean hasKey(String key) {
        return redisTemplate.opsForHash().getOperations().hasKey(key);
    }

    /**
     * 判断此token是否在黑名单中
     *
     * @param token
     * @return
     */
    public Boolean isBlackList(String token) {
        return hasKey("blacklist", token);
    }

    /**
     * 将token加入到redis黑名单中
     *
     * @param token
     */
    public void addBlackList(String token) {
        hset("blacklist", token, "true");
    }


    /**
     * 查询token下的刷新时间
     *
     * @param token 查询的key
     * @return HV
     */
    public Object getTokenValidTimeByToken(String token) {
        return redisTemplate.opsForHash().get(token, "tokenValidTime");
    }

    /**
     * 查询token下的刷新时间
     *
     * @param token 查询的key
     * @return HV
     */
    public Object getUsernameByToken(String token) {
        return redisTemplate.opsForHash().get(token, "username");
    }

    /**
     * 查询token下的刷新时间
     *
     * @param token 查询的key
     * @return HV
     */
    public Object getIPByToken(String token) {
        return redisTemplate.opsForHash().get(token, "ip");
    }

    /**
     * 查询token下的过期时间
     *
     * @param token 查询的key
     * @return HV
     */
    public Object getExpirationTimeByToken(String token) {
        return redisTemplate.opsForHash().get(token, "expirationTime");
    }

    public void setTokenRefresh(String token, String username, String ip) {
        // 刷新时间
        Integer expire = 7 * 24 * 60 * 60 * 1000;

        hset(token, "ip", ip, expire);
        hset(token, "tokenValidTime", DateUtils.getAddDayTime(7), expire);
        hset(token, "expirationTime", DateUtils.getAddDaySecond(8600), expire);
        hset(token, "username", username, expire);
    }

    /**
     * 缓存队列 list 从右压栈，返回当前队列总数
     *
     * @param key
     * @param object
     * @return
     */
    public Long rightPush(String key, Object object) {
        ListOperations<String, Object> redisList = objectRedisTemplate.opsForList();
        Long push = redisList.rightPush(key, object);
        if (null == push || push <= 0) {
            throw new RuntimeException("缓存队列添加失败");
        }
        return push;
    }

    /**
     * 缓存队列 list 指定读取, 返回集合
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<Object> range(String key, Integer start, Integer end, boolean deleteAll) {
        ListOperations<String, Object> redisList = objectRedisTemplate.opsForList();
        if (null == start) {
            start = 0;
        }
        if (null == end) {
            end = -1;
        }
        List<Object> range = redisList.range(key, start, end);
        if (deleteAll) {
            objectRedisTemplate.delete(key);
        }
        return range;
    }


    public void rightPushAll(String key, List<Object> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        ListOperations<String, Object> redisList = objectRedisTemplate.opsForList();
        Long aLong = redisList.rightPushAll(key, list, 100);
        if (null == aLong || aLong <= 0) {
            throw new RuntimeException("缓存队列添加失败");
        }
    }

    /**
     * 加锁
     *
     * @param key              key - 键
     * @param timeOutTimeStamp 当前时间 + 超时时间 也就是时间戳
     * @return
     */
    public boolean locked(String key, String timeOutTimeStamp) {
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
        if (opsForValue.setIfAbsent(key, timeOutTimeStamp)) {
            return true;
        }

        String currentLock = opsForValue.get(key);
        if (!Strings.isNullOrEmpty(currentLock) && Long.parseLong(currentLock) < System.currentTimeMillis()) {
            String preLock = opsForValue.getAndSet(key, timeOutTimeStamp);

            if (!Strings.isNullOrEmpty(preLock) && preLock.equals(currentLock)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 解锁
     *
     * @param target
     * @param timeStamp
     */
    public void unlocked(String target, String timeStamp) {
        try {
            ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
            String currentValue = opsForValue.get(target);
            if (!Strings.isNullOrEmpty(currentValue) && currentValue.equals(timeStamp)) {
                opsForValue.getOperations().delete(target);
            }
        } catch (Exception e) {
            log.error("解锁异常", e);
            throw new RuntimeException("系统异常");
        }
    }
}