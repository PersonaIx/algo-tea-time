package org.tea.compositions;

import org.tea.datastructures.maps.SimpleHashMap;

import java.util.ArrayList;
import java.util.List;

public class LRUCache<K, V> implements Cache<K, V> {

    private final int capacity;
    private final SimpleHashMap<K, Node< K, V>> map;
    private final Node<K, V> head;
    private final Node<K, V> tail;
    private int size;


    public LRUCache(int capacity) {
        this.map = new SimpleHashMap<>();
        this.capacity = capacity;
        head = new Node<>(null, null);
        tail = new Node<>(null, null);
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public V get(K key) {
        if (!map.containsKey(key)) {
            return null;
        }
        Node<K, V> kvNode = map.get(key);
        becomeMostRecentlyUsed(kvNode);
        return kvNode.value;
    }

    @Override
    public void put(K key, V value) {
        if (map.containsKey(key)) {
            Node<K, V> kvNode = map.get(key);
            kvNode.value = value;
            becomeMostRecentlyUsed(kvNode);
        } else {
            Node<K, V> kvNode = new Node<>(key, value);
            insertAtEnd(kvNode);
            map.put(key, kvNode);
            size++;
        }
        if (size > capacity) {
            Node<K, V> removed = head.next;
            Node<K, V> nextNext = head.next.next;
            head.next = nextNext;
            nextNext.prev = head;
            map.remove(removed.key);
            size--;
        }
    }

    private void insertAtEnd(Node<K, V> kvNode) {
        Node<K, V> prev = tail.prev;
        tail.prev = kvNode;
        kvNode.next = tail;
        kvNode.prev = prev;
        prev.next = kvNode;
    }

    private void becomeMostRecentlyUsed(Node<K, V> kvNode) {
        kvNode.prev.next = kvNode.next;
        kvNode.next.prev = kvNode.prev;
        insertAtEnd(kvNode);
    }

    @Override
    public V remove(K key) {
        if (!map.containsKey(key)) {
            return null;
        }
        Node<K, V> kvNode = map.get(key);
        map.remove(key);
        Node<K, V> prev = kvNode.prev;
        Node<K, V> next = kvNode.next;
        prev.next = next;
        next.prev = prev;
        size--;
        return kvNode.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    @Override
    public void clear() {
        size = 0;
        map.clear();
        head.next = tail;
        tail.prev = head;
    }
    List<K> keysInEvictionOrder() {
        List<K> result = new ArrayList<>();
        Node<K, V> current = head.next;
        while (current != tail) {
            result.add(current.key);
            current = current.next;
        }
        return result;
    }

    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

}
