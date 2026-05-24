package org.tea.algos.datastructures.trees.search;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BinarySearchTree<K extends Comparable<K>, V> implements SearchTree<K, V> {

    @Override
    public Optional<V> insert(K key, V value) {
        return Optional.empty();
    }

    @Override
    public Optional<V> delete(K key) {
        return Optional.empty();
    }

    @Override
    public void clear() {

    }

    @Override
    public Optional<V> search(K key) {
        return Optional.empty();
    }

    @Override
    public boolean contains(K key) {
        return false;
    }

    @Override
    public Optional<Entry<K, V>> min() {
        return Optional.empty();
    }

    @Override
    public Optional<Entry<K, V>> max() {
        return Optional.empty();
    }

    @Override
    public Optional<Entry<K, V>> floor(K key) {
        return Optional.empty();
    }

    @Override
    public Optional<Entry<K, V>> ceiling(K key) {
        return Optional.empty();
    }

    @Override
    public List<Entry<K, V>> inOrder() {
        return List.of();
    }

    @Override
    public List<Entry<K, V>> preOrder() {
        return List.of();
    }

    @Override
    public List<Entry<K, V>> postOrder() {
        return List.of();
    }

    @Override
    public List<Entry<K, V>> levelOrder() {
        return List.of();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int height() {
        return 0;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public boolean isBalanced() {
        return false;
    }

    @Override
    public void insertAll(Map<K, V> entries) {

    }

    @Override
    public List<Entry<K, V>> rangeSearch(K from, K to) {
        return List.of();
    }
}
