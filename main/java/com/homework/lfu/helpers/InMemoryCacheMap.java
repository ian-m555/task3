package com.homework.lfu.helpers;

import com.homework.model.CacheEntry;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryCacheMap {

    private final int maxSize;

    private final int timeToLive;

    private final EvictionPolicy evictionPolicy;

    public InMemoryCacheMap(int maxSize, int timeToLive) {
        this.maxSize = maxSize;
        this.timeToLive = timeToLive;
        this.evictionPolicy = new EvictionPolicy();
    }

    public InMemoryCacheMap() {
        this.maxSize = 100000;
        this.timeToLive = 5000;
        this.evictionPolicy = new EvictionPolicy();
    }

    private final AtomicLong totalPutTime = new AtomicLong(0);
    private final AtomicInteger putCount = new AtomicInteger(0);
    private final AtomicInteger cacheEvictions = new AtomicInteger(0);

    private final Map<Integer, CacheEntry> cacheMap = new ConcurrentHashMap<>();

    public CacheEntry get(Integer key) {
        CacheEntry cacheEntry = cacheMap.get(key);
        if (cacheEntry == null) {
            return null;
        }

        long timeInCache = System.currentTimeMillis() - cacheEntry.getLastAccessed();
        if (timeInCache  > timeToLive) {
            System.out.println("Key " + key + " time in cache: " + timeInCache);
            removeCacheEntry(key);
            return null;
        }

        evictionPolicy.updateAccessFrequency(key);
        cacheEntry.setLastAccessed(System.currentTimeMillis());
        return cacheEntry;
    }

    public void put(Integer key, CacheEntry value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key or value cannot be null");
        }

        long start = System.currentTimeMillis();

        if (cacheMap.size() >= maxSize) {
            Integer evictKey = evictionPolicy.doEviction();
            if (evictKey != null) {
                removeCacheEntry(evictKey);
            }
        }

        cacheMap.put(key, value);
        evictionPolicy.updateAccessFrequency(key);

        long duration = System.currentTimeMillis() - start;
        totalPutTime.addAndGet(duration);
        putCount.incrementAndGet();
    }

    public void removeCacheEntry(Integer key) {
        cacheMap.remove(key);
        evictionPolicy.removeFrequencyKey(key);
        cacheEvictions.getAndIncrement();
        System.out.println("Removed entry: Key = " + key);
    }

    public void getStatistics() {
        int count = putCount.get();
        double avgMillis = count == 0 ? 0 : (totalPutTime.get()) / count;
        System.out.println("Average put time, ms: " + avgMillis);
        System.out.println("Total cache evictions: " + cacheEvictions.get());
    }
}