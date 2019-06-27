package com.shoppingproject.service;

/**
 * Created by Tricia on 2019/6/26
 */
//封装本地缓存的方法们
public interface CacheService {
    //存方法
    void setCommonCache(String key,Object value);
    //取方法
    Object getFromCommonCache(String key);
}
