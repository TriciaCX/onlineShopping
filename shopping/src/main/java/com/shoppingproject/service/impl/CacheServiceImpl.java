package com.shoppingproject.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.shoppingproject.service.CacheService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * Create by Tricia on 2019/6/26
 */
@Service
public class CacheServiceImpl implements CacheService {

    private Cache<Object, Object> commonCache =null;

    @PostConstruct
    public void init(){   //这种写法清楚多了
        commonCache =  CacheBuilder.newBuilder()
                //设置缓存容器的初始容量为10
                .initialCapacity(10)
                //设置缓存中最大可以存储100个key，超出100个key之后会按照LRU的策略移除缓存项
                .maximumSize(100)
                //设置写入数据多少时间后过期
                .expireAfterWrite(1, TimeUnit.MINUTES).build();
    }

    @Override
    public void setCommonCache(String key, Object value) {
        commonCache.put(key,value);
    }

    @Override
    public Object getFromCommonCache(String key) {
        return commonCache.getIfPresent(key);
    }
}
