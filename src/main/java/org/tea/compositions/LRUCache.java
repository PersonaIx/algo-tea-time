package org.tea.compositions;

import java.util.Collections;
import java.util.List;

public class LRUCache<K, V> implements Cache<K, V> {

    public LRUCache(K capacity) {

    }

    @Override
    public V get(K key) {
        return null;
    }

    @Override
    public void put(K key, V value) {

    }

    @Override
    public V remove(K key) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int capacity() {
        return 0;
    }

    @Override
    public boolean containsKey(K key) {
        return false;
    }

    @Override
    public void clear() {

    }
    List<K> keysInEvictionOrder() {
        return Collections.emptyList();
    }

}
