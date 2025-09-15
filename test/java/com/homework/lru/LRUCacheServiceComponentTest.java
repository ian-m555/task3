package com.homework.lru;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.homework.lfu.services.LFUCacheServiceImpl;
import com.homework.lru.guava.services.LRUGuavaCacheServiceImpl;
import com.homework.model.CacheEntry;
import com.homework.model.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LRUCacheServiceComponentTest {

    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new LRUGuavaCacheServiceImpl(100, 1);
    }

    @Test
    void testPutAndGetCities() {
        cacheService.put(1, new CacheEntry("London"));
        cacheService.put(2, new CacheEntry("Paris"));
        cacheService.put(3, new CacheEntry("Berlin"));

        assertEquals("London", cacheService.get(1).getData());
        assertEquals("Paris", cacheService.get(2).getData());
        assertEquals("Berlin", cacheService.get(3).getData());
    }

    @Test
    void testTtlExpiry() throws InterruptedException {
        cacheService.put(10, new CacheEntry("Rome"));
        Thread.sleep(1010); // longer than default TTL (5000 ms)
        assertNull(cacheService.get(10), "Entry should expire after TTL");
    }

    @Test
    void testEvictionOnMaxSize() {
        //maxSize = 100
        for (int i = 0; i < 103; i++) {
            cacheService.put(i, new CacheEntry("City" + i));
        }
        // At least one eviction should have happened, so first 3 entries likely evicted
        assertNull(cacheService.get(0));
        assertNull(cacheService.get(1));
        assertNull(cacheService.get(2));
        assertEquals("City3", cacheService.get(3).getData());
    }

    @Test
    void testEvictionOnMaxSize_byLastAccessed() throws InterruptedException {
        //maxSize = 10
        for (int i = 0; i < 15; i++) {
            cacheService.put(i, new CacheEntry("City" + i));
            Thread.sleep(100); // Interval for different last access
            cacheService.get(0); // hit to refresh access
            cacheService.get(1);
        }
        // At least one eviction should have happened, OLDEST entries likely evicted
        assertNull(cacheService.get(2));
        assertNull(cacheService.get(3));
        assertNull(cacheService.get(4));
        assertNull(cacheService.get(5));
        // first 2 got latest last access, not removed
        assertEquals("City0", cacheService.get(0).getData());
        assertEquals("City1", cacheService.get(1).getData());
    }
}
