package com.homework.lfu.services;

import com.homework.lfu.helpers.InMemoryCacheMap;
import com.homework.model.CacheEntry;
import com.homework.model.CacheService;

public class LFUCacheServiceImpl implements CacheService {

    private static CacheService service;
    public final InMemoryCacheMap cache;

    private LFUCacheServiceImpl() {
        this.cache = new InMemoryCacheMap();
    }

    public LFUCacheServiceImpl(InMemoryCacheMap cache) {
        this.cache = cache;
    }

    public static synchronized CacheService getServiceInstance() {
        if (service == null) {
            service = new LFUCacheServiceImpl();
        }

        return service;
    }

    @Override
    public CacheEntry get(Integer key) {
        return cache.get(key);
    }

    @Override
    public void put(Integer key, CacheEntry cacheEntry) {
        cache.put(key, cacheEntry);
    }
}
