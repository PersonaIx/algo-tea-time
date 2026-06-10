package org.tea.algos.datastructures.trees.search;

import org.tea.algos.datastructures.queues.deques.CircularArrayDeque;
import org.tea.algos.datastructures.queues.deques.DequeOperations;

import java.util.*;

public class BinarySearchTree<K extends Comparable<K>, V> implements SearchTree<K, V> {

    private BinaryNode<K,V> root;
    private int size;

    @Override
    public Optional<V> insert(K key, V value) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        if (value == null) {
            throw new NullPointerException("value");
        }
        if (root == null) {
            root = new BinaryNode<>(key, value);
            size++;
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
                size++;
                return Optional.empty();
            }
            return insertRecursive(current.left, key, value);
        } else {
            if (current.right == null) {
                current.right = new BinaryNode<>(key, value);
                size++;
                return Optional.empty();
            }
            return insertRecursive(current.right, key, value);
        }
    }

    @Override
    public Optional<V> delete(K key) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        if (root == null) {
            return Optional.empty();
        }
        DeleteResult<K, V> result = deleteRecursive(root, key);
        root = result.updatedNode;
        if (result.deletedValue == null) {
            return Optional.empty();
        }
        size--;
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

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public Optional<V> search(K key) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        return search(root, key);
    }

    private Optional<V> search(BinaryNode<K, V> currentNode, K key) {
        if (currentNode == null) {
            return Optional.empty();
        }
        if (key.compareTo(currentNode.key) == 0) {
            return Optional.of(currentNode.value);
        }
        if (key.compareTo(currentNode.key) < 0) {
            return search(currentNode.left, key);
        } else {
            return search(currentNode.right, key);
        }
    }

    @Override
    public boolean contains(K key) {
        if (key == null) {
            return false;
        }
        return contains(root, key);
    }

    private boolean contains(BinaryNode<K,V> currentNode, K key) {
        if (currentNode == null) {
            return false;
        }
        if (key.compareTo(currentNode.key) == 0) {
            return true;
        }
        if (key.compareTo(currentNode.key) < 0) {
            return contains(currentNode.left, key);
        } else {
            return contains(currentNode.right, key);
        }
    }

    @Override
    public Optional<Entry<K, V>> min() {
        if (root == null) {
            return Optional.empty();
        }
        BinaryNode<K, V> min = findMin(root);
        return Optional.of(new SearchTree.Entry<>(min.key, min.value));
    }

    private BinaryNode<K, V> findMin(BinaryNode<K, V> currentNode) {
        BinaryNode<K, V> minimum = currentNode;
        while (minimum.left != null) {
            minimum = minimum.left;
        }
        return minimum;
    }

    @Override
    public Optional<Entry<K, V>> max() {
        if (root == null) {
            return Optional.empty();
        }
        BinaryNode<K, V> min = findMax(root);
        return Optional.of(new SearchTree.Entry<>(min.key, min.value));
    }

    private BinaryNode<K, V> findMax(BinaryNode<K, V> currentNode) {
        BinaryNode<K, V> maximum = currentNode;
        while (maximum.right != null) {
            maximum = maximum.right;
        }
        return maximum;
    }

    @Override
    public Optional<Entry<K, V>> floor(K key) {
        BinaryNode<K, V> node = floor(root, key);
        if (node == null) {
            return Optional.empty();
        }
        return Optional.of(new Entry<>(node.key, node.value));
    }

    private BinaryNode<K, V> floor(BinaryNode<K, V> currentNode, K key) {
        if (currentNode == null) {
            return null;
        }
        if (currentNode.key.compareTo(key) <= 0) {
            BinaryNode<K, V> biggerFloor = floor(currentNode.right, key);
            if (biggerFloor != null) {
                return biggerFloor;
            } else {
                return currentNode;
            }
        } else {
            return floor(currentNode.left, key);

        }
    }

    @Override
    public Optional<Entry<K, V>> ceiling(K key) {
        BinaryNode<K, V> node = ceiling(root, key);
        if (node == null) {
            return Optional.empty();
        }
        return Optional.of(new Entry<>(node.key, node.value));
    }

    private BinaryNode<K, V> ceiling(BinaryNode<K, V> currentNode, K key) {
        if (currentNode == null) {
            return null;
        }
        if (currentNode.key.compareTo(key) >= 0) {
            BinaryNode<K, V> smallerCeiling = ceiling(currentNode.left, key);
            if (smallerCeiling != null) {
                return smallerCeiling;
            } else {
                return currentNode;
            }
        } else {
            return ceiling(currentNode.right, key);
        }
    }

    @Override
    public List<Entry<K, V>> inOrder() {
        List<Entry<K, V>> result = new ArrayList<>();
        inOrder(root, result);
        return Collections.unmodifiableList(result);
    }

    private void inOrder(BinaryNode<K, V> currentNode, List<Entry<K, V>> result) {
        if (currentNode == null) {
            return;
        }
        inOrder(currentNode.left, result);
        result.add(new Entry<>(currentNode.key, currentNode.value));
        inOrder(currentNode.right, result);
    }

    @Override
    public List<Entry<K, V>> preOrder() {
        List<Entry<K, V>> result = new ArrayList<>();
        preOrder(root, result);
        return Collections.unmodifiableList(result);
    }

    private void preOrder(BinaryNode<K, V> currentNode, List<Entry<K, V>> result) {
        if (currentNode == null) {
            return;
        }
        result.add(new Entry<>(currentNode.key, currentNode.value));
        preOrder(currentNode.left, result);
        preOrder(currentNode.right, result);
    }

    @Override
    public List<Entry<K, V>> postOrder() {
        List<Entry<K, V>> result = new ArrayList<>();
        postOrder(root, result);
        return Collections.unmodifiableList(result);
    }

    private void postOrder(BinaryNode<K, V> currentNode, List<Entry<K, V>> result) {
        if (currentNode == null) {
            return;
        }
        postOrder(currentNode.left, result);
        postOrder(currentNode.right, result);
        result.add(new Entry<>(currentNode.key, currentNode.value));
    }

    @Override
    public List<Entry<K, V>> levelOrder() {
        if (root == null) {
            return Collections.emptyList();
        }
        List<Entry<K, V>> result = new ArrayList<>();
        // using the deque I implemented
        DequeOperations<BinaryNode<K, V>> deque = new CircularArrayDeque<>();
        deque.offerFront(root);
        while (!deque.isEmpty()) {
            BinaryNode<K, V> currentNode = deque.pollFront();
            result.add(new Entry<>(currentNode.key, currentNode.value));
            if (currentNode.left != null) {
                deque.offerLast(currentNode.left);
            }
            if (currentNode.right != null) {
                deque.offerLast(currentNode.right);
            }
        }

        return Collections.unmodifiableList(result);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public int height() {
        return height(root);
    }

    private int height(BinaryNode<K, V> currentNode) {
        if (currentNode == null) {
            return -1;
        }
        return 1 + Math.max(height(currentNode.left), height(currentNode.right));
    }

    @Override
    public boolean isValid() {
        return isValid(root, null, null);
    }

    private boolean isValid(BinaryNode<K, V> currentNode, K lowerBound, K upperBound) {
        if (currentNode == null) {
            return true;
        }
        if (isInRange(currentNode.key, lowerBound, upperBound)) {
            return isValid(currentNode.left, lowerBound, currentNode.key)
                    && isValid(currentNode.right, currentNode.key, upperBound);
        }
        return false;
    }

    private boolean isInRange(K key, K from, K to) {
        if (from != null && to != null) {
            return from.compareTo(key) <= 0 && to.compareTo(key) >= 0;
        } else if (from != null) {
            return from.compareTo(key) <= 0;
        } else if (to != null) {
            return to.compareTo(key) >= 0;
        } else {
            // both ranges are not set yet
            return true;
        }
    }

    @Override
    public boolean isBalanced() {
        return isBalanced(root);
    }

    private boolean isBalanced(BinaryNode<K,V> currentNode) {
        if (currentNode == null) {
            return true;
        }
        int diff = Math.abs(height(currentNode.left) - height(currentNode.right));
        if (diff <= 1) {
            return isBalanced(currentNode.left) && isBalanced(currentNode.right);
        } else {
            return false;
        }
    }

    @Override
    public void insertAll(Map<K, V> entries) {
        if (entries == null) {
            throw new NullPointerException("entries");
        }

        entries.forEach(this::insert);
    }

    @Override
    public List<Entry<K, V>> rangeSearch(K from, K to) {
        List<Entry<K, V>> result = new ArrayList<>();
        rangeSearch(root, from, to, result);
        return result;
    }

    private void rangeSearch(BinaryNode<K, V> currentNode, K from, K to, List<Entry<K, V>> result) {
        if (currentNode == null) {
            return;
        }
        if (from.compareTo(currentNode.key) <= 0 && to.compareTo(currentNode.key) >= 0) {
            rangeSearch(currentNode.left, from, to, result);
            result.add(new Entry<>(currentNode.key, currentNode.value));
            rangeSearch(currentNode.right, from, to, result);
        } else if (from.compareTo(currentNode.key)  <= 0) {
            rangeSearch(currentNode.left, from, to, result);
        } else if (to.compareTo(currentNode.key) >= 0) {
            rangeSearch(currentNode.right, from, to, result);
        }
    }
}
