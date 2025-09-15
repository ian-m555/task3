package com.homework.lfu.helpers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EvictionPolicy {

    private final Map<Integer, Integer> frequencyMap;

    public EvictionPolicy() {
        this.frequencyMap = new ConcurrentHashMap<>();
    }

    public Integer doEviction() {
        return frequencyMap.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public void updateAccessFrequency(Integer key) {
        frequencyMap.put(key, frequencyMap.getOrDefault(key, 0) + 1);
    }

    public void removeFrequencyKey(Integer key) {
        frequencyMap.remove(key);
    }
}
