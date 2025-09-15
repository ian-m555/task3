package com.homework.model;

import lombok.Data;

@Data
public class CacheEntry {
    private final String data;

    private long lastAccessed = System.currentTimeMillis();
}
