package org.tea.compositions;

public interface Cache<K, V> {
    V get(K key);
    void put(K key, V value);
    V remove(K key);
    int size();
    int capacity();
    boolean containsKey(K key);
    void clear();
}