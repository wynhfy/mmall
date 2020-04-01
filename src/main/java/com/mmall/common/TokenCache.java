package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TokenCache {

    private static Logger logger= LoggerFactory.getLogger(TokenCache.class);

    public static final String TOKEN_PREFIX="token_";

    /**
     * 使用的是调用链的模式，参数调换顺序也没事
     * initialCapacity 设置缓存的初始化容量
     * maximumSize 最大缓存，超过最大缓存后guava会使用LRU算法，最少使用算法
     * expireAfterAccess(12, TimeUnit.HOURS) 设置有效期，第一个参数是个数，第二个是单位
     */
    private static LoadingCache<String,String> localCache= CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                //默认的数据加载实现，当调用get取值的时候，如果key没有对应的值，就调用这个方法进行加载。
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });

    public static void setKey(String key,String value){
        localCache.put(key,value);
    }

    public static String getKey(String key){
        String value=null;
        try{
            value=localCache.get(key);
            if("null".equals(value)){
                return null;
            }
            return value;
        }catch (Exception e){
            logger.error("localCache get error:",e);
        }
        return null;
    }



}
