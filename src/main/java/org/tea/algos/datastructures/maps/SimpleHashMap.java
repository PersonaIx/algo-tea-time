package org.tea.algos.datastructures.maps;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SimpleHashMap<K, V> implements SimpleMap<K, V> {

    private static final float LOAD_FACTOR = 0.75f;
    public static final int INITIAL_CAPACITY = 16;

    private Node<K, V>[] nodes;
    private int size;
    private int capacity;

    public SimpleHashMap(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException();
        }

        int adjustedCapacity = Integer.highestOneBit(initialCapacity) << 1;
        this.capacity = adjustedCapacity;
        //noinspection unchecked
        this.nodes = (Node<K, V>[]) new Node[capacity];
    }

    public SimpleHashMap() {
        this(INITIAL_CAPACITY);
    }

    @Override
    public V put(K key, V value) {
        int hash = hash(key);
        if (capacity == size) {
            adjustCapacity();
        }
        Node<K, V> node = findBucket(hash);
        if (node != null) {
            V oldValue = node.value;
            node.value = value;
            return oldValue;
        }

        size++;
        return null;
    }

    @Override
    public V get(K key) {
        int hash = hash(key);
        for (Node<K, V> node : nodes) {
            if (node != null) {
                if (hash == node.hash) {
                    return node.value;
                }
            }
        }
        return null;
    }

    @Override
    public V remove(K key) {
        int hash = hash(key);
        size--;
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        int hash = hash(key);
        for (Node<K, V> node : nodes) {
            if (node != null && hash == node.hash) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        //noinspection unchecked
        nodes = (Node<K, V>[]) new Node[INITIAL_CAPACITY];
        capacity = INITIAL_CAPACITY;
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        Set<K> result = new HashSet<>();
        for (Node<K, V> node : nodes) {
            if (node != null) {
                result.add(node.key);
            }
        }
        return result;
    }

    // need to implement my own iterator here, difficult?
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

    private void adjustCapacity() {
        capacity = capacity * 2;
        this.nodes = Arrays.copyOf(nodes, capacity);
    }

    private Node<K, V> findBucket(int hash) {
        for (Node<K, V> node : nodes) {
            if (node != null) {
                if (hash == node.hash) {
                    return node;
                }
            }
        }
        return null;
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
