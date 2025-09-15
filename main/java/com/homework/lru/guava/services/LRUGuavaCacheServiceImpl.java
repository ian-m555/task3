package com.homework.lru.guava.services;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.homework.model.CacheEntry;
import com.homework.model.CacheService;
import java.util.concurrent.TimeUnit;

public class LRUGuavaCacheServiceImpl implements CacheService {

    private static CacheService service;

    private final Cache<Integer, CacheEntry> cache;

    public LRUGuavaCacheServiceImpl(int capacity, int ttlSec) {
        this.cache = CacheBuilder.newBuilder()
                .concurrencyLevel(12)
                .maximumSize(capacity)
                .expireAfterAccess(ttlSec, TimeUnit.SECONDS)
                .removalListener(new MyRemovalListener())
                .build();
    }

    public LRUGuavaCacheServiceImpl() {
        this.cache = CacheBuilder.newBuilder()
                .concurrencyLevel(12)
                .maximumSize(100000)
                .expireAfterAccess(5, TimeUnit.SECONDS)
                .removalListener(new MyRemovalListener())
                .build();
    }


    public static synchronized CacheService getInstance() {
        if (service == null) {
            service = new LRUGuavaCacheServiceImpl();
        }

        return service;
    }

    @Override
    public CacheEntry get(Integer key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void put(Integer key, CacheEntry cacheEntry) {
        cache.put(key, cacheEntry);
    }

    public class MyRemovalListener implements RemovalListener<Integer, CacheEntry> {
        @Override
        public void onRemoval(RemovalNotification<Integer, CacheEntry> notification) {
            System.out.println("Entry removed: Key=" + notification.getKey() +
                    ", Value=" + notification.getValue() +
                    ", Cause=" + notification.getCause());
        }
    }
}