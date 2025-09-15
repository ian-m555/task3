package com.homework.model;

public interface CacheService {

    CacheEntry get(Integer key);

    void put(Integer key, CacheEntry cacheEntry);
}
