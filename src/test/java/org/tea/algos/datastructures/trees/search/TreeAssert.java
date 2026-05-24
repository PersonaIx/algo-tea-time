package org.tea.algos.datastructures.trees.search;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Fluent, domain-aware assertion object for {@link SearchTree}.
 *
 * <h2>Why a custom assertion object?</h2>
 * Raw AssertJ on an {@code Optional<Entry>} produces failures like:
 * <pre>
 *   expected: Optional[Entry[key=4, value="four"]]
 *    but was: Optional.empty
 * </pre>
 * A domain assertion produces:
 * <pre>
 *   [tree.min()] BST minimum — tree had 7 nodes but min() returned empty
 * </pre>
 * The message names the BST concept that was violated, not just the raw value.
 *
 * <h2>Usage</h2>
 * <pre>
 *   assertThatTree(tree)
 *       .hasSize(7)
 *       .hasHeight(2)
 *       .satisfiesBstOrdering()
 *       .hasInOrderKeys(1, 2, 3, 4, 5, 6, 7);
 * </pre>
 *
 * Every method returns {@code this} for chaining and is deliberately short so
 * test methods read as a specification, not a wall of assertions.
 */
public final class TreeAssert<K extends Comparable<K>, V> {

    private final SearchTree<K, V> tree;

    private TreeAssert(SearchTree<K, V> tree) {
        assertThat(tree).as("tree under test must not be null").isNotNull();
        this.tree = tree;
    }

    /** Entry point — mirrors AssertJ's {@code assertThat} naming convention. */
    public static <K extends Comparable<K>, V> TreeAssert<K, V> assertThatTree(SearchTree<K, V> tree) {
        return new TreeAssert<>(tree);
    }

    // ── Size & emptiness ───────────────────────────────────────────────────────

    public TreeAssert<K, V> isEmpty() {
        assertThat(tree.isEmpty())
            .as("[tree.isEmpty()] expected tree to be empty")
            .isTrue();
        assertThat(tree.size())
            .as("[tree.size()] expected 0 for an empty tree")
            .isZero();
        return this;
    }

    public TreeAssert<K, V> isNotEmpty() {
        assertThat(tree.isEmpty())
            .as("[tree.isEmpty()] tree should have at least one node")
            .isFalse();
        return this;
    }

    public TreeAssert<K, V> hasSize(int expected) {
        assertThat(tree.size())
            .as("[tree.size()] number of nodes")
            .isEqualTo(expected);
        return this;
    }

    // ── Height ─────────────────────────────────────────────────────────────────

    public TreeAssert<K, V> hasHeight(int expected) {
        assertThat(tree.height())
            .as("[tree.height()] longest root-to-leaf edge count  "
              + "(empty tree = -1, single node = 0)")
            .isEqualTo(expected);
        return this;
    }

    public TreeAssert<K, V> hasHeightAtMost(int max) {
        assertThat(tree.height())
            .as("[tree.height()] tree is too tall — expected ≤ %d".formatted(max))
            .isLessThanOrEqualTo(max);
        return this;
    }

    // ── Structural invariants ──────────────────────────────────────────────────

    /**
     * Asserts the BST ordering property: every node's key is strictly greater
     * than all keys in its left subtree and strictly less than all keys in its
     * right subtree.
     *
     * Call this after every mutation in thorough tests. It is the single most
     * important invariant of any BST.
     */
    public TreeAssert<K, V> satisfiesBstOrdering() {
        assertThat(tree.isValid())
            .as("[tree.isValid()] BST ordering property violated: "
              + "some node's key is outside the allowed range for its position")
            .isTrue();
        return this;
    }

    /**
     * Asserts height balance: no node has subtrees whose heights differ by more
     * than 1. For a plain BST this is diagnostic; for AVL/Red-Black it is a
     * hard post-condition after every mutation.
     */
    public TreeAssert<K, V> isHeightBalanced() {
        assertThat(tree.isBalanced())
            .as("[tree.isBalanced()] height difference between some pair of "
              + "subtrees exceeds 1")
            .isTrue();
        return this;
    }

    public TreeAssert<K, V> isNotHeightBalanced() {
        assertThat(tree.isBalanced())
            .as("[tree.isBalanced()] expected tree to be unbalanced "
              + "(e.g. after skewed insertion)")
            .isFalse();
        return this;
    }

    // ── Traversal ─────────────────────────────────────────────────────────────

    /**
     * The canonical BST invariant expressed as a test: in-order traversal of a
     * valid BST must always yield keys in strictly ascending order.
     */
    public TreeAssert<K, V> hasInOrderKeys(@SuppressWarnings("unchecked") K... expected) {
        assertThat(keys(tree.inOrder()))
            .as("[tree.inOrder()] keys must be in strictly ascending order "
              + "for any valid BST")
            .containsExactly(expected);
        return this;
    }

    public TreeAssert<K, V> hasInOrderKeysSorted() {
        assertThat(keys(tree.inOrder()))
            .as("[tree.inOrder()] keys must be sorted ascending in a valid BST")
            .isSorted();
        return this;
    }

    public TreeAssert<K, V> hasInOrderEntries(List<SearchTree.Entry<K, V>> expected) {
        assertThat(tree.inOrder())
            .as("[tree.inOrder()] entries")
            .containsExactlyElementsOf(expected);
        return this;
    }

    public TreeAssert<K, V> hasPreOrderKeys(@SuppressWarnings("unchecked") K... expected) {
        assertThat(keys(tree.preOrder()))
            .as("[tree.preOrder()] node → left → right; first element must be the root")
            .containsExactly(expected);
        return this;
    }

    public TreeAssert<K, V> hasPostOrderKeys(@SuppressWarnings("unchecked") K... expected) {
        assertThat(keys(tree.postOrder()))
            .as("[tree.postOrder()] left → right → node; last element must be the root")
            .containsExactly(expected);
        return this;
    }

    public TreeAssert<K, V> hasLevelOrderKeys(@SuppressWarnings("unchecked") K... expected) {
        assertThat(keys(tree.levelOrder()))
            .as("[tree.levelOrder()] breadth-first: root first, then left-to-right per level")
            .containsExactly(expected);
        return this;
    }

    public TreeAssert<K, V> allTraversalsHaveSize(int expectedSize) {
        assertThat(tree.inOrder())   .as("[tree.inOrder().size()]")   .hasSize(expectedSize);
        assertThat(tree.preOrder())  .as("[tree.preOrder().size()]")  .hasSize(expectedSize);
        assertThat(tree.postOrder()) .as("[tree.postOrder().size()]") .hasSize(expectedSize);
        assertThat(tree.levelOrder()).as("[tree.levelOrder().size()]").hasSize(expectedSize);
        return this;
    }

    // ── Search & contains ──────────────────────────────────────────────────────

    public TreeAssert<K, V> containsKey(K key) {
        assertThat(tree.contains(key))
            .as("[tree.contains(%s)] key should be present".formatted(key))
            .isTrue();
        return this;
    }

    public TreeAssert<K, V> doesNotContainKey(K key) {
        assertThat(tree.contains(key))
            .as("[tree.contains(%s)] key should be absent".formatted(key))
            .isFalse();
        return this;
    }

    public TreeAssert<K, V> searchFinds(K key, V expectedValue) {
        assertThat(tree.search(key))
            .as("[tree.search(%s)] should find the key and return its value".formatted(key))
            .isPresent()
            .hasValue(expectedValue);
        return this;
    }

    public TreeAssert<K, V> searchFindsNothing(K key) {
        assertThat(tree.search(key))
            .as("[tree.search(%s)] key is absent; Optional must be empty".formatted(key))
            .isEmpty();
        return this;
    }

    // ── Min / max ──────────────────────────────────────────────────────────────

    public TreeAssert<K, V> hasMin(K key, V value) {
        assertThat(tree.min())
            .as("[tree.min()] entry with the smallest key")
            .isPresent()
            .hasValue(new SearchTree.Entry<>(key, value));
        return this;
    }

    public TreeAssert<K, V> hasMax(K key, V value) {
        assertThat(tree.max())
            .as("[tree.max()] entry with the largest key")
            .isPresent()
            .hasValue(new SearchTree.Entry<>(key, value));
        return this;
    }

    public TreeAssert<K, V> hasNoMin() {
        assertThat(tree.min())
            .as("[tree.min()] empty tree has no minimum")
            .isEmpty();
        return this;
    }

    public TreeAssert<K, V> hasNoMax() {
        assertThat(tree.max())
            .as("[tree.max()] empty tree has no maximum")
            .isEmpty();
        return this;
    }

    // ── Floor / ceiling ────────────────────────────────────────────────────────

    public TreeAssert<K, V> hasFloor(K query, K expectedKey, V expectedValue) {
        assertThat(tree.floor(query))
            .as("[tree.floor(%s)] greatest key ≤ %s".formatted(query, query))
            .isPresent()
            .hasValue(new SearchTree.Entry<>(expectedKey, expectedValue));
        return this;
    }

    public TreeAssert<K, V> hasNoFloor(K query) {
        assertThat(tree.floor(query))
            .as("[tree.floor(%s)] no key ≤ %s exists in the tree".formatted(query, query))
            .isEmpty();
        return this;
    }

    public TreeAssert<K, V> hasCeiling(K query, K expectedKey, V expectedValue) {
        assertThat(tree.ceiling(query))
            .as("[tree.ceiling(%s)] smallest key ≥ %s".formatted(query, query))
            .isPresent()
            .hasValue(new SearchTree.Entry<>(expectedKey, expectedValue));
        return this;
    }

    public TreeAssert<K, V> hasNoCeiling(K query) {
        assertThat(tree.ceiling(query))
            .as("[tree.ceiling(%s)] no key ≥ %s exists in the tree".formatted(query, query))
            .isEmpty();
        return this;
    }

    // ── Range search ───────────────────────────────────────────────────────────

    public TreeAssert<K, V> rangeSearchReturnsKeys(K from, K to,
                                                   @SuppressWarnings("unchecked") K... expected) {
        assertThat(keys(tree.rangeSearch(from, to)))
            .as("[tree.rangeSearch(%s, %s)] keys in [from, to] inclusive, ascending"
                .formatted(from, to))
            .containsExactly(expected);
        return this;
    }

    public TreeAssert<K, V> rangeSearchIsEmpty(K from, K to) {
        assertThat(tree.rangeSearch(from, to))
            .as("[tree.rangeSearch(%s, %s)] expected no entries in this range"
                .formatted(from, to))
            .isEmpty();
        return this;
    }

    // ── Internal helper ────────────────────────────────────────────────────────

    private List<K> keys(List<SearchTree.Entry<K, V>> entries) {
        return entries.stream().map(SearchTree.Entry::key).toList();
    }
}
