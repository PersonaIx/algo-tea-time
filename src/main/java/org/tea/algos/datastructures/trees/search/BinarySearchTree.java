package org.tea.algos.datastructures.trees.search;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BinarySearchTree<K extends Comparable<K>, V> implements SearchTree<K, V> {

    private BinaryNode<K,V> root;

    @Override
    public Optional<V> insert(K key, V value) {
        if (root == null) {
            root = new BinaryNode<>(key, value);
            return Optional.empty();
        }
        return insertRecursive(root, key, value);
    }

    private Optional<V> insertRecursive(BinaryNode<K, V> current, K key, V value) {
        if (current.key.compareTo(key) == 0) {
            V prevValue = current.value;
            current.value = value;
            return Optional.of(prevValue);
        }
        if (key.compareTo(current.key) < 0) {
            if (current.left == null) {
                current.left = new BinaryNode<>(key, value);
                return Optional.empty();
            }
            return insertRecursive(current.left, key, value);
        } else {
            if (current.right == null) {
                current.right = new BinaryNode<>(key, value);
                return Optional.empty();
            }
            return insertRecursive(current.right, key, value);
        }
    }

    @Override
    public Optional<V> delete(K key) {
        if (root == null) {
            return Optional.empty();
        }
        DeleteResult<K, V> result = deleteRecursive(root, key);
        root = result.updatedNode;
        if (result.deletedValue == null) {
            return Optional.empty();
        }
        return Optional.of(result.deletedValue);
    }

    private record DeleteResult<K extends Comparable<K>, V>(BinaryNode<K, V> updatedNode, V deletedValue) {}

    private DeleteResult<K, V> deleteRecursive(BinaryNode<K, V> current, K key) {
        if (current == null) {
            return new DeleteResult<>(null, null);
        }

        if (current.key.compareTo(key) == 0) {
            if (current.isLeaf()) {
                return new DeleteResult<>(null, current.value);
            }
            if (current.left == null && current.right != null) {
                return new DeleteResult<>(current.right, current.value);
            }
            if (current.right == null && current.left != null) {
                return new DeleteResult<>(current.left, current.value);
            }
            V oldValue = current.value;
            BinaryNode<K, V> minimum = findMin(current.right);
            current.value = minimum.value;
            current.key = minimum.key;
            DeleteResult<K, V> deletedRightSubtree = deleteRecursive(current.right, minimum.key);
            current.right = deletedRightSubtree.updatedNode;
            return new DeleteResult<>(current, oldValue);
        }
        if (key.compareTo(current.key) < 0) {
            DeleteResult<K, V> deleteResult = deleteRecursive(current.left, key);
            current.left = deleteResult.updatedNode;
            return new DeleteResult<>(current, deleteResult.deletedValue);
        } else {
            DeleteResult<K, V> deleteResult = deleteRecursive(current.right, key);
            current.right = deleteResult.updatedNode;
            return new DeleteResult<>(current, deleteResult.deletedValue);
        }
    }

    private BinaryNode<K, V> findMin(BinaryNode<K, V> currentNode) {
        BinaryNode<K, V> minimum = currentNode;
        while (minimum.left != null) {
            minimum = minimum.left;
        }
        return minimum;
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
