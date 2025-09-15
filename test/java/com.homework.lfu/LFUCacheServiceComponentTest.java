package com.homework.lfu;

import com.homework.lfu.helpers.InMemoryCacheMap;
import com.homework.model.CacheEntry;
import com.homework.model.CacheService;
import com.homework.lfu.services.LFUCacheServiceImpl;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class LFUCacheServiceComponentTest {
    private CacheService cacheService;

    private InMemoryCacheMap testCache;

    @BeforeEach
    void setUp() {
        testCache = new InMemoryCacheMap(10, 1000);
        cacheService = new LFUCacheServiceImpl(testCache);
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
        Thread.sleep(1051); // longer than default TTL (1000 ms)
        assertNull(cacheService.get(10), "Entry should expire after TTL");
    }

    @Test
    void testEvictionOnMaxSize() {
        //maxSize = 10
        for (int i = 0; i < 15; i++) {
            cacheService.put(i, new CacheEntry("City" + i));
        }
        // At least one eviction should have happened, so first  entries likely evicted
        assertNull(cacheService.get(0));
        assertNull(cacheService.get(1));
        assertNull(cacheService.get(2));
        assertNull(cacheService.get(3));
        assertNull(cacheService.get(4));
        assertEquals("City5", cacheService.get(5).getData());
    }

    @Test
    void testEvictionOnMaxSize_byHits() {
        //maxSize = 10
        for (int i = 0; i < 15; i++) {
            cacheService.put(i, new CacheEntry("City" + i));
            cacheService.get(0); // hit to increase frequency
            cacheService.get(1);
        }
        // At least one eviction should have happened, so first  entry likely evicted
        assertNull(cacheService.get(2));
        assertNull(cacheService.get(3));
        assertNull(cacheService.get(4));
        assertNull(cacheService.get(5));
        assertNull(cacheService.get(6));
        // first 2 got bigger frequency, not removed
        assertEquals("City0", cacheService.get(0).getData());
        assertEquals("City1", cacheService.get(1).getData());
    }
}
