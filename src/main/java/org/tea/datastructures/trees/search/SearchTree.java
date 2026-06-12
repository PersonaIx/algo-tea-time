package org.tea.datastructures.trees.search;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Generic ordered search tree mapping comparable keys to values.
 *
 * Designed to be the shared contract for BST, AVL, Red-Black, B-Tree, etc.
 * Every method that may produce no result returns Optional — never null.
 *
 * @param <K> key type; must be Comparable so the tree can maintain order
 * @param <V> value type; arbitrary payload attached to each key
 */
public interface SearchTree<K extends Comparable<K>, V> {

    // ── Core mutation ──────────────────────────────────────────────────────────

    /**
     * Inserts or replaces the mapping for the given key.
     *
     * @return the previous value if the key already existed, otherwise empty
     */
    Optional<V> insert(K key, V value);

    /**
     * Removes the node with the given key.
     *
     * @return the removed value, or empty if the key was not present
     */
    Optional<V> delete(K key);

    /** Removes every node from the tree. After this call, size() == 0. */
    void clear();


    // ── Search & access ────────────────────────────────────────────────────────

    /** Returns the value for the given key, or empty if not found. */
    Optional<V> search(K key);

    /** True iff the tree contains a mapping for the given key. */
    boolean contains(K key);

    /** Entry with the smallest key, or empty when the tree is empty. */
    Optional<Entry<K, V>> min();

    /** Entry with the largest key, or empty when the tree is empty. */
    Optional<Entry<K, V>> max();

    /**
     * Greatest key ≤ the given key ("round down").
     * Mirrors {@code TreeMap.floorEntry()}.
     */
    Optional<Entry<K, V>> floor(K key);

    /**
     * Smallest key ≥ the given key ("round up").
     * Mirrors {@code TreeMap.ceilingEntry()}.
     */
    Optional<Entry<K, V>> ceiling(K key);


    // ── Traversal ─────────────────────────────────────────────────────────────

    /**
     * Left → node → right.
     * For a valid BST this is always sorted by key — a fundamental invariant
     * that every test suite should verify.
     */
    List<Entry<K, V>> inOrder();

    /**
     * Node → left → right.
     * Re-inserting entries in this order recreates the exact same tree shape.
     */
    List<Entry<K, V>> preOrder();

    /**
     * Left → right → node.
     * Natural ordering for deletion and post-processing passes.
     */
    List<Entry<K, V>> postOrder();

    /**
     * Breadth-first, level by level.
     * Makes the layer structure visible — essential for diagnosing balance.
     */
    List<Entry<K, V>> levelOrder();


    // ── Structural inspection ──────────────────────────────────────────────────

    /** Number of nodes in the tree. */
    int size();

    /** True iff size() == 0. */
    boolean isEmpty();

    /**
     * Number of edges on the longest root-to-leaf path.
     * Empty tree → -1. Single-node tree → 0.
     * (Knuth/CLRS convention; makes recursive height formula work without
     * special-casing: height = 1 + max(height(left), height(right)).)
     */
    int height();

    /**
     * True iff every node satisfies the BST ordering property.
     * Implementations may override to add balancing constraints (AVL, RB).
     */
    boolean isValid();

    /**
     * True iff the tree is height-balanced.
     * For a plain BST this is diagnostic; for AVL/RB it is a hard invariant.
     */
    boolean isBalanced();


    // ── Bulk & utility ─────────────────────────────────────────────────────────

    /**
     * Inserts all entries from the map.
     * For duplicate keys the new value overwrites the old one.
     */
    void insertAll(Map<K, V> entries);

    /**
     * All entries whose keys fall in [from, to], inclusive, in key order.
     * This is the operation that distinguishes search trees from hash maps.
     */
    List<Entry<K, V>> rangeSearch(K from, K to);


    // ── Entry value object ─────────────────────────────────────────────────────

    /**
     * Immutable key-value pair.
     * A record gives equals/hashCode/toString for free — essential for clean
     * AssertJ assertions like {@code containsExactly(new Entry<>("a", 1))}.
     */
    record Entry<K, V>(K key, V value) {}
}
