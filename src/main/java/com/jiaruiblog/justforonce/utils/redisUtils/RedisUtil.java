package com.jiaruiblog.justforonce.utils.redisUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.logging.Logger;

/**
 * @Author Jarrett Luo
 * @Date 2022/5/26 18:25
 * @Version 1.0
 */
public class RedisUtil {

    protected static final Logger logger = Logger.getLogger(String.valueOf(RedisUtil.class));

    private static JedisPool jedisPool;


    /**
     * 如果字段是哈希表中的一个新建字段，并且值设置成功，返回 1 。
     * 如果哈希表中域字段已经存在且旧值已被新值覆盖，返回 0 。
     * @param key
     * @param filed
     * @param value
     * @param indexdb
     * @return
     */
    public static Long hset(String key,String filed,String value,int indexdb) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(indexdb);
            return jedis.hset(key,filed,value);
        } catch (Exception e) {
            logger.info("hset错误日志："+e.getMessage());
            return 0L;
        } finally {
            jedis.close();
        }
    }

    /**
     * 获取存储在哈希表中指定字段的值
     * 也可以获取某个对象值
     * @param key
     * @param filed
     * @param indexdb
     * @return
     */
    public static String hget(String key,String filed,int indexdb) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(indexdb);
            return jedis.hget(key,filed);
        } catch (Exception e) {
            logger.info("hset错误日志："+e.getMessage());
            return null;
        } finally {
            jedis.close();
        }
    }

    /**
     * 获取哈希表中字段的数量
     * 也可以用于获取对象个数
     * @param key
     * @param indexdb
     * @return
     */
    public static Long hlen(String key,int indexdb) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(indexdb);
            return jedis.hlen(key);
        } catch (Exception e) {
            logger.info("hlen错误日志："+e.getMessage());
            return 0L;
        } finally {
            jedis.close();
        }
    }

    /**
     * 获取哈希表中所有值
     * 也可以获取所有对象的list集合
     * @param key
     * @param indexdb
     * @return
     */
    public static List<String> hvals(String key, int indexdb) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(indexdb);
            return jedis.hvals(key);
        } catch (Exception e) {
            logger.info("hvals错误日志："+e.getMessage());
            return null;
        } finally {
            jedis.close();
        }
    }
}
