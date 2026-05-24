package org.tea.algos.datastructures.trees.search;

public class BinaryNode<K extends Comparable<K>, V> {

    K key;
    V value;
    BinaryNode<K, V> left;
    BinaryNode<K, V> right;

    BinaryNode(K key, V value) {
        this.key   = key;
        this.value = value;
    }

    public K key()   { return key; }

    public V value() { return value; }

    public BinaryNode<K, V> left()  { return left; }

    public BinaryNode<K, V> right() { return right; }

    public boolean isLeaf() { return left == null && right == null; }

    @Override
    public String toString() {
        return "BinaryNode{key=%s, value=%s}".formatted(key, value);
    }
}
