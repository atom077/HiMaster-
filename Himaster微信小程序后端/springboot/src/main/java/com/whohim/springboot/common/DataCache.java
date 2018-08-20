package com.whohim.springboot.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


public class DataCache {

    private static Logger logger = LoggerFactory.getLogger(DataCache.class);


    //initialCapacity //初始缓存大小 .maximumSize(10)  //最多存放十个数据 expireAfterAccess //缓存超时时间（起点：缓存被创建或被修改或被访问）
    private static LoadingCache<String, String> localCache = CacheBuilder.newBuilder().initialCapacity(10).maximumSize(100).expireAfterAccess(24, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                //默认的数据加载实现,当调用get取值的时候,如果key没有对应的值,就调用这个方法进行加载.
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });

    public static void setKey(String key, String value) {
        localCache.put(key, value);
        System.out.println("已将" + key + ":" + value + "放入缓存");
    }


    public static String getKey(String key) {
        String value = null;
        try {
            value = localCache.get(key);
            if ("null".equals(value)) {
                return null;
            }
            System.out.println("已从缓存中拿到" + key + ":" + value);
            return value;
        } catch (Exception e) {
            logger.error("localCache get error", e);
        }
        return null;
    }


}
