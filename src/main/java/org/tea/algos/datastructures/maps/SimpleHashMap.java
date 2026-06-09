package org.tea.algos.datastructures.maps;

import java.util.*;

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

        int adjustedCapacity = Integer.bitCount(initialCapacity) == 1
                ? initialCapacity
                : Integer.highestOneBit(initialCapacity) << 1;
        this.capacity = adjustedCapacity;
        //noinspection unchecked
        this.buckets = (Node<K, V>[]) new Node[capacity];
    }

    public SimpleHashMap() {
        this(INITIAL_CAPACITY);
    }

    @Override
    public V put(K key, V value) {
        int hash = hash(key);
        if (size >= capacity * LOAD_FACTOR) {
            adjustCapacity();
        }
        Node<K, V> existingNode = buckets[hash];
        if (existingNode != null) {
            Node<K, V> targetNode = findNode(existingNode, key);
            if (targetNode == null) {
                buckets[hash] = new Node<>(key, value, hash, buckets[hash]);
                size++;
                return value;
            }
            V oldValue = targetNode.value;
            targetNode.value = value;
            return oldValue;
        }
        buckets[hash] = new Node<>(key, value, hash, null);
        size++;
        return null;
    }

    @Override
    public V get(K key) {
        int hash = hash(key);
        Node<K, V> bucket = buckets[hash];
        Node<K, V> node = findNode(bucket, key);
        if (node != null) {
            return node.value;
        }
        return null;
    }

    @Override
    public V remove(K key) {
        int hash = hash(key);
        if (buckets[hash] == null) {
            return null;
        }
        if (buckets[hash].key.equals(key)) {
            V oldValue = buckets[hash].value;
            buckets[hash] = buckets[hash].next;
            size--;
            return oldValue;
        }
        Node<K, V> previous = buckets[hash];
        Node<K, V> node = buckets[hash].next;
        while (node != null) {
            if (node.key.equals(key)) {
                previous.next = node.next;
                size--;
                return node.value;
            }
            node = node.next;
        }
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        int hash = hash(key);
        Node<K, V> bucket = buckets[hash];
        Node<K, V> node = findNode(bucket, key);
        if (node != null) {
            return true;
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
        buckets = (Node<K, V>[]) new Node[INITIAL_CAPACITY];
        capacity = INITIAL_CAPACITY;
        size = 0;
    }

    @Override
    public SimpleMap.SimpleSet<K> keySet() {
        return new SimpleMap.SimpleSet<K>() {
            @Override
            public boolean contains(K key) {
                return containsKey(key);
            }

            @Override
            public int size() {
                return size;
            }

            @Override
            public Iterator<K> iterator() {
                Iterator<Entry<K, V>> entryIterator = SimpleHashMap.this.iterator();
                return new Iterator<>() {
                    @Override
                    public boolean hasNext() {
                        return entryIterator.hasNext();
                    }

                    @Override
                    public K next() {
                        return entryIterator.next().key();
                    }
                };
            }
        };
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new SimpleHashmapIterator();
    }

    public int capacity() {
        return capacity;
    }

    private int hash(K key) {
        return key.hashCode() & (capacity - 1);
    }

    private void adjustCapacity() {
        capacity = capacity * 2;
        this.buckets = Arrays.copyOf(buckets, capacity);
    }

    private Node<K, V> findNode(Node<K, V> node, K key) {
        while (node != null) {
            if (node.key.equals(key)) {
                return node;
            }
            node = node.next;
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

    private class SimpleHashmapIterator implements Iterator<Entry<K, V>> {
        Node<K, V> currentNode;
        int bucketNumber;

        SimpleHashmapIterator() {
            findNextBucketWithNode();
        }


        private void findNextBucketWithNode() {
            while (bucketNumber < capacity && buckets[bucketNumber] == null) {
                bucketNumber++;
            }
            currentNode = (bucketNumber < capacity) ? buckets[bucketNumber] : null;
        }

        @Override
        public boolean hasNext() {
            return currentNode != null;
        }

        @Override
        public Entry<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Node<K, V> result = currentNode;

            if (currentNode.next != null) {
                currentNode = currentNode.next;
            } else {
                // go to next bucket and start from there
                bucketNumber++;
                findNextBucketWithNode();
            }
            return new SimpleMap.Entry<>(result.key, result.value);
        }
    }
}
