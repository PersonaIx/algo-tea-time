package org.tea.algos.datastructures.maps;

import java.util.Iterator;

public class SimpleHashMap<K, V> implements SimpleMap<K, V> {

    private static final float LOAD_FACTOR = 0.75f;
    public static final int INITIAL_CAPACITY = 16;

    private Node<K, V>[] buckets;
    private int size;
    private int capacity;

    public SimpleHashMap(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException();
        }

        int adjustedCapacity = Integer.highestOneBit(initialCapacity) << 1;
        this.capacity = adjustedCapacity;
        //noinspection unchecked
        this.buckets = (Node<K, V>[]) new Node[capacity];
    }

    public SimpleHashMap() {
        this(INITIAL_CAPACITY);
    }

    @Override
    public V put(K key, V value) {
        size++;
        return null;
    }

    @Override
    public V get(K key) {
        for (Node<K, V> bucket : buckets) {
            if (hash(key) == bucket.hash) {
                return bucket.value;
            }
        }
        return null;
    }

    @Override
    public V remove(K key) {
        size--;
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        size = 0;
    }

    @Override
    public SimpleSet<K> keySet() {
        return null;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return null;
    }

    public int capacity() {
        return capacity;
    }

    private int hash(K key) {
        return key.hashCode() & (capacity - 1);
    }

    private static class Node<K, V> {
        final K key;
        V value;
        final int hash;
        Node<K, V> next;

        Node(K key, V value, int hash, Node<K, V> next) {
            this.key   = key;
            this.value = value;
            this.hash  = hash;
            this.next  = next;
        }
    }
}
