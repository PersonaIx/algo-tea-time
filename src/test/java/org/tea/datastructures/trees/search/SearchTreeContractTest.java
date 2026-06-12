package org.tea.datastructures.trees.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.tea.datastructures.trees.search.TreeAssert.assertThatTree;
import static org.tea.datastructures.trees.search.TreeFixtures.*;

/**
 * Abstract contract test suite for {@link SearchTree}.
 *
 * <h2>Purpose</h2>
 * This class defines the <em>behavioural contract</em> that every
 * {@code SearchTree} implementation must satisfy. It is intentionally abstract:
 * subclasses supply a factory method and nothing else.
 *
 * <pre>
 * class AvlTreeTest extends SearchTreeContractTest {
 *     {@literal @}Override protected SearchTree<Integer, String> createTree() {
 *         return new AvlTree<>();
 *     }
 * }
 * </pre>
 *
 * JUnit 5 discovers the concrete subclass and runs all {@code @Test} methods
 * defined here against that implementation automatically.
 *
 * <h2>Structure</h2>
 * Tests are grouped into {@code @Nested} inner classes by feature area.
 * Each group is independently runnable and small enough to read in one sitting.
 * Within a group, test names are written as sentences that state the
 * <em>expected behaviour</em>, not the implementation detail.
 *
 * <h2>Extending for a new implementation</h2>
 * <ol>
 *   <li>Extend this class — all contract tests run automatically.</li>
 *   <li>Override {@link #treeSupportsBalanceInvariant()} to return {@code true}
 *       if the implementation guarantees balance (AVL, Red-Black); the contract
 *       suite will then also assert {@code isBalanced()} after every mutation.</li>
 *   <li>Add an implementation-specific {@code @Nested} class in the subclass
 *       for any behaviour that goes beyond the shared contract.</li>
 * </ol>
 */
@DisplayName("SearchTree contract")
public abstract class SearchTreeContractTest {

    // ── Extension points ───────────────────────────────────────────────────────

    /** Supplies a fresh, empty tree for each test. */
    protected abstract SearchTree<Integer, String> createTree();

    /**
     * Override and return {@code true} for self-balancing trees (AVL, Red-Black).
     * The contract suite will then enforce {@code isBalanced()} as a hard
     * post-condition after every mutation, not just as a diagnostic.
     */
    protected boolean treeSupportsBalanceInvariant() {
        return false;
    }

    // ── Shared setup ───────────────────────────────────────────────────────────

    private SearchTree<Integer, String> tree;

    @BeforeEach
    void setUp() {
        tree = createTree();
    }

    /** Shared post-mutation invariant check used throughout the suite. */
    private void assertBstInvariant() {
        assertThatTree(tree).satisfiesBstOrdering().hasInOrderKeysSorted();
        if (treeSupportsBalanceInvariant()) {
            assertThatTree(tree).isHeightBalanced();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 1  Empty tree
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("1 · Empty tree")
    class EmptyTree {

        @Test
        @DisplayName("isEmpty() is true and size() is 0 on a brand-new tree")
        void newTreeIsEmpty() {
            assertThatTree(tree)
                .isEmpty()
                .hasSize(0)
                .hasHeight(-1);
        }

        @Test
        @DisplayName("search() returns Optional.empty() on an empty tree")
        void searchReturnsEmptyOnEmptyTree() {
            assertThatTree(tree).searchFindsNothing(42);
        }

        @Test
        @DisplayName("contains() returns false on an empty tree")
        void containsReturnsFalseOnEmptyTree() {
            assertThatTree(tree).doesNotContainKey(42);
        }

        @Test
        @DisplayName("min() and max() return Optional.empty() on an empty tree")
        void minAndMaxAreEmptyOnEmptyTree() {
            assertThatTree(tree).hasNoMin().hasNoMax();
        }

        @Test
        @DisplayName("all four traversals return an empty list on an empty tree")
        void allTraversalsReturnEmptyListOnEmptyTree() {
            assertThat(tree.inOrder())   .as("inOrder on empty tree")   .isEmpty();
            assertThat(tree.preOrder())  .as("preOrder on empty tree")  .isEmpty();
            assertThat(tree.postOrder()) .as("postOrder on empty tree") .isEmpty();
            assertThat(tree.levelOrder()).as("levelOrder on empty tree") .isEmpty();
        }

        @Test
        @DisplayName("delete() on an empty tree returns Optional.empty() and does not throw")
        void deleteOnEmptyTreeReturnsEmpty() {
            assertThat(tree.delete(99))
                .as("[tree.delete(99)] deleting from an empty tree must return Optional.empty()")
                .isEmpty();
            assertThatTree(tree).isEmpty();
        }

        @Test
        @DisplayName("isValid() is true on an empty tree (vacuously)")
        void emptyTreeIsValid() {
            assertThatTree(tree).satisfiesBstOrdering();
        }

        @Test
        @DisplayName("floor() and ceiling() return Optional.empty() on an empty tree")
        void floorAndCeilingAreEmptyOnEmptyTree() {
            assertThatTree(tree).hasNoFloor(5).hasNoCeiling(5);
        }

        @Test
        @DisplayName("rangeSearch() returns an empty list on an empty tree")
        void rangeSearchReturnsEmptyOnEmptyTree() {
            assertThatTree(tree).rangeSearchIsEmpty(1, 10);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 2  Insert
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("2 · Insert")
    class Insert {

        @Test
        @DisplayName("inserting a new key returns Optional.empty()")
        void insertNewKeyReturnsEmpty() {
            assertThat(tree.insert(1, "one"))
                .as("[tree.insert(1,'one')] first insertion of a key must return Optional.empty()")
                .isEmpty();
        }

        @Test
        @DisplayName("inserting a duplicate key returns the previous value")
        void insertDuplicateKeyReturnsPreviousValue() {
            tree.insert(1, "original");

            var returned = tree.insert(1, "updated");

            assertThat(returned)
                .as("[tree.insert(1,'updated')] must return the value that was replaced")
                .isPresent()
                .hasValue("original");
        }

        @Test
        @DisplayName("inserting a duplicate key updates the stored value")
        void insertDuplicateKeyOverwritesStoredValue() {
            tree.insert(1, "original");
            tree.insert(1, "updated");

            assertThatTree(tree)
                .hasSize(1)
                .searchFinds(1, "updated");
        }

        @Test
        @DisplayName("size increments by 1 for each distinct key inserted")
        void sizeIncrementsForEachDistinctKey() {
            for (int key = 1; key <= 7; key++) {
                tree.insert(key, word(key));
                assertThatTree(tree).hasSize(key);
            }
        }

        @Test
        @DisplayName("BST ordering invariant holds after every individual insert")
        void bstOrderingHoldsAfterEveryInsert() {
            int[] insertOrder = {5, 3, 7, 1, 4, 6, 8};
            for (int key : insertOrder) {
                tree.insert(key, String.valueOf(key));
                assertBstInvariant();
            }
        }

        @Test
        @DisplayName("inserting a null key throws NullPointerException with descriptive message")
        void insertNullKeyThrowsNpe() {
            assertThatNullPointerException()
                .isThrownBy(() -> tree.insert(null, "value"))
                .withMessage("key");
        }

        @Test
        @DisplayName("inserting a null value throws NullPointerException with descriptive message")
        void insertNullValueThrowsNpe() {
            assertThatNullPointerException()
                .isThrownBy(() -> tree.insert(1, null))
                .withMessage("value");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 3  Search & contains
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("3 · Search and contains")
    class SearchAndContains {

        @BeforeEach
        void fill() { balanced(tree); }

        @Test
        @DisplayName("search() finds every key that was inserted")
        void searchFindsEveryInsertedKey() {
            for (int key : BALANCED_INORDER) {
                assertThatTree(tree).searchFinds(key, word(key));
            }
        }

        @Test
        @DisplayName("search() returns Optional.empty() for a key never inserted")
        void searchReturnEmptyForAbsentKey() {
            assertThatTree(tree).searchFindsNothing(99);
        }

        @Test
        @DisplayName("contains() agrees with search().isPresent() for every key")
        void containsAgreesWithSearch() {
            for (int key : BALANCED_INORDER) {
                assertThat(tree.contains(key))
                    .as("[tree.contains(%d)] must agree with search(%d).isPresent()".formatted(key, key))
                    .isEqualTo(tree.search(key).isPresent());
            }
            assertThat(tree.contains(99)).isFalse();
        }

        @Test
        @DisplayName("search() returns false for a key deleted after insertion")
        void searchReturnsFalseAfterDeletion() {
            tree.delete(4);
            assertThatTree(tree).searchFindsNothing(4).doesNotContainKey(4);
        }

        @Test
        @DisplayName("search() with a null key throws NullPointerException")
        void searchNullKeyThrowsNpe() {
            assertThatNullPointerException()
                .isThrownBy(() -> tree.search(null))
                .withMessage("key");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 4  Delete
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("4 · Delete")
    class Delete {

        @BeforeEach
        void fill() { balanced(tree); }
        //         [4]
        //        /    \
        //      [2]    [6]
        //      / \    / \
        //    [1] [3] [5] [7]

        @Test
        @DisplayName("delete() returns the value that was stored at the key")
        void deleteReturnsStoredValue() {
            assertThat(tree.delete(4))
                .as("[tree.delete(4)] must return the value previously stored at key 4")
                .isPresent()
                .hasValue("four");
        }

        @Test
        @DisplayName("delete() returns Optional.empty() for a key not in the tree")
        void deleteAbsentKeyReturnsEmpty() {
            assertThat(tree.delete(99))
                .as("[tree.delete(99)] key does not exist; must return Optional.empty()")
                .isEmpty();
        }

        @Test
        @DisplayName("deleted key is not found by subsequent search() or contains()")
        void deletedKeyIsNoLongerAccessible() {
            tree.delete(4);
            assertThatTree(tree).searchFindsNothing(4).doesNotContainKey(4);
        }

        @Test
        @DisplayName("deleting a leaf: BST ordering holds and size decrements")
        void deleteLeafPreservesOrdering() {
            tree.delete(1);          // [1] is a leaf
            assertThatTree(tree).hasSize(6);
            assertBstInvariant();
        }

        @Test
        @DisplayName("deleting a node with one child: BST ordering holds")
        void deleteNodeWithOneChildPreservesOrdering() {
            // Build a tree where 6 has only a right child [7]
            tree.clear();
            tree.insert(4, "four");
            tree.insert(2, "two");
            tree.insert(6, "six");
            tree.insert(7, "seven");

            tree.delete(6);

            assertThatTree(tree)
                .hasSize(3)
                .hasInOrderKeys(2, 4, 7);
            assertBstInvariant();
        }

        @Test
        @DisplayName("deleting a node with two children: BST ordering holds")
        void deleteNodeWithTwoChildrenPreservesOrdering() {
            tree.delete(2);          // [2] has children [1] and [3]
            assertThatTree(tree).hasSize(6).hasInOrderKeys(1, 3, 4, 5, 6, 7);
            assertBstInvariant();
        }

        @Test
        @DisplayName("deleting the root: BST ordering holds")
        void deleteRootPreservesOrdering() {
            tree.delete(4);          // root of the balanced fixture
            assertThatTree(tree).hasSize(6);
            assertBstInvariant();
        }

        @Test
        @DisplayName("BST ordering holds after deleting every node one at a time")
        void bstOrderingHoldsAfterEveryDeletion() {
            for (int key : BALANCED_INORDER) {
                tree.delete(key);
                assertBstInvariant();
            }
            assertThatTree(tree).isEmpty();
        }

        @Test
        @DisplayName("size decrements by exactly 1 for each successful delete")
        void sizeDecrementsOnSuccessfulDelete() {
            int expected = tree.size();
            for (int key : List.of(7, 5, 3, 1, 6, 2, 4)) {
                tree.delete(key);
                assertThatTree(tree).hasSize(--expected);
            }
        }

        @Test
        @DisplayName("second delete of the same key returns Optional.empty()")
        void doubleDeleteReturnsEmptySecondTime() {
            tree.delete(3);
            assertThat(tree.delete(3))
                .as("[tree.delete(3)] second deletion of an absent key must return Optional.empty()")
                .isEmpty();
        }

        @Test
        @DisplayName("delete() with a null key throws NullPointerException")
        void deleteNullKeyThrowsNpe() {
            assertThatNullPointerException()
                .isThrownBy(() -> tree.delete(null))
                .withMessage("key");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 5  Clear
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("5 · Clear")
    class Clear {

        @Test
        @DisplayName("clear() on an empty tree is a safe no-op")
        void clearEmptyTreeIsNoOp() {
            tree.clear();
            assertThatTree(tree).isEmpty().hasSize(0).hasHeight(-1);
        }

        @Test
        @DisplayName("clear() resets size, height, and isEmpty() to their initial values")
        void clearResetsAllMetrics() {
            balanced(tree);
            tree.clear();
            assertThatTree(tree).isEmpty().hasSize(0).hasHeight(-1);
        }

        @Test
        @DisplayName("tree is fully usable again after clear()")
        void treeIsReusableAfterClear() {
            balanced(tree);
            tree.clear();
            tree.insert(42, "the-answer");
            assertThatTree(tree)
                .hasSize(1)
                .searchFinds(42, "the-answer")
                .satisfiesBstOrdering();
        }

        @Test
        @DisplayName("all traversals return empty lists after clear()")
        void allTraversalsEmptyAfterClear() {
            balanced(tree);
            tree.clear();
            assertThat(tree.inOrder())   .isEmpty();
            assertThat(tree.preOrder())  .isEmpty();
            assertThat(tree.postOrder()) .isEmpty();
            assertThat(tree.levelOrder()).isEmpty();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 6  Min and max
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("6 · Min and max")
    class MinAndMax {

        @BeforeEach
        void fill() { balanced(tree); }

        @Test
        @DisplayName("min() returns the entry with the smallest key")
        void minReturnsSmallestKey() {
            assertThatTree(tree).hasMin(1, "one");
        }

        @Test
        @DisplayName("max() returns the entry with the largest key")
        void maxReturnsLargestKey() {
            assertThatTree(tree).hasMax(7, "seven");
        }

        @Test
        @DisplayName("min() tracks the new minimum after deleting the current minimum")
        void minUpdatesAfterDeletingCurrentMin() {
            tree.delete(1);
            assertThatTree(tree).hasMin(2, "two");
        }

        @Test
        @DisplayName("max() tracks the new maximum after deleting the current maximum")
        void maxUpdatesAfterDeletingCurrentMax() {
            tree.delete(7);
            assertThatTree(tree).hasMax(6, "six");
        }

        @Test
        @DisplayName("min() and max() return the same entry when only one node remains")
        void minEqualsMaxForSingleNode() {
            tree.clear();
            tree.insert(42, "only");
            assertThat(tree.min())
                .as("[tree.min()] single-node tree: min and max should be equal")
                .isEqualTo(tree.max());
        }

        @Test
        @DisplayName("inserting a smaller key updates min()")
        void minUpdatesAfterInsertingSmallerKey() {
            tree.insert(0, "zero");
            assertThatTree(tree).hasMin(0, "zero");
        }

        @Test
        @DisplayName("inserting a larger key updates max()")
        void maxUpdatesAfterInsertingLargerKey() {
            tree.insert(8, "eight");
            assertThatTree(tree).hasMax(8, "eight");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 7  Floor and ceiling
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("7 · Floor and ceiling")
    class FloorAndCeiling {

        @BeforeEach
        void fill() { sparse(tree); }
        // Sparse keys: 3, 7, 10, 15, 20

        // ── floor ─────────────────────────────────────────────────────────────

        @Test
        @DisplayName("floor() returns the entry itself when the key is present")
        void floorOfPresentKeyReturnsThatEntry() {
            assertThatTree(tree).hasFloor(10, 10, "ten");
        }

        @Test
        @DisplayName("floor() returns the greatest key strictly less than the query")
        void floorOfAbsentKeyReturnsBestCandidate() {
            assertThatTree(tree).hasFloor(9, 7, "seven");   // 9 not in tree; floor = 7
            assertThatTree(tree).hasFloor(14, 10, "ten");   // 14 not in tree; floor = 10
            assertThatTree(tree).hasFloor(18, 15, "fifteen"); // 18 not; floor = 15
        }

        @Test
        @DisplayName("floor() returns the maximum when the query exceeds all keys")
        void floorAboveMaxReturnsMax() {
            assertThatTree(tree).hasFloor(99, 20, "twenty");
        }

        @Test
        @DisplayName("floor() returns Optional.empty() when the query is below all keys")
        void floorBelowMinReturnsEmpty() {
            assertThatTree(tree).hasNoFloor(0).hasNoFloor(2);
        }

        // ── ceiling ───────────────────────────────────────────────────────────

        @Test
        @DisplayName("ceiling() returns the entry itself when the key is present")
        void ceilingOfPresentKeyReturnsThatEntry() {
            assertThatTree(tree).hasCeiling(10, 10, "ten");
        }

        @Test
        @DisplayName("ceiling() returns the smallest key strictly greater than the query")
        void ceilingOfAbsentKeyReturnsBestCandidate() {
            assertThatTree(tree).hasCeiling(4,  7,  "seven");    // 4 not in tree; ceiling = 7
            assertThatTree(tree).hasCeiling(11, 15, "fifteen");  // 11 not; ceiling = 15
            assertThatTree(tree).hasCeiling(16, 20, "twenty");   // 16 not; ceiling = 20
        }

        @Test
        @DisplayName("ceiling() returns the minimum when the query is below all keys")
        void ceilingBelowMinReturnsMin() {
            assertThatTree(tree).hasCeiling(0, 3, "three");
        }

        @Test
        @DisplayName("ceiling() returns Optional.empty() when the query exceeds all keys")
        void ceilingAboveMaxReturnsEmpty() {
            assertThatTree(tree).hasNoCeiling(21).hasNoCeiling(99);
        }

        @Test
        @DisplayName("floor() and ceiling() agree on keys present in the tree")
        void floorAndCeilingAgreeOnPresentKeys() {
            for (int key : SPARSE_INORDER) {
                assertThat(tree.floor(key))
                    .as("[floor/ceiling agree] floor(%d) and ceiling(%d) should both return key %d"
                        .formatted(key, key, key))
                    .isEqualTo(tree.ceiling(key));
            }
        }

        @Test
        @DisplayName("floor() null key throws NullPointerException")
        void floorNullKeyThrowsNpe() {
            assertThatNullPointerException().isThrownBy(() -> tree.floor(null));
        }

        @Test
        @DisplayName("ceiling() null key throws NullPointerException")
        void ceilingNullKeyThrowsNpe() {
            assertThatNullPointerException().isThrownBy(() -> tree.ceiling(null));
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 8  Traversals
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("8 · Traversals")
    class Traversals {

        @BeforeEach
        void fill() { balanced(tree); }
        //         [4]
        //        /    \
        //      [2]    [6]
        //      / \    / \
        //    [1] [3] [5] [7]

        @Test
        @DisplayName("inOrder() produces keys in strictly ascending order (core BST invariant)")
        void inOrderIsStrictlyAscending() {
            assertThatTree(tree).hasInOrderKeys(1, 2, 3, 4, 5, 6, 7);
        }

        @Test
        @DisplayName("preOrder() starts with the root and visits each node before its children")
        void preOrderStartsWithRootAndHasCorrectSequence() {
            var keys = tree.preOrder().stream().map(SearchTree.Entry::key).toList();
            assertThat(keys.getFirst())
                .as("[tree.preOrder()] first element must be the root")
                .isEqualTo(4);
            assertThatTree(tree).hasPreOrderKeys(4, 2, 1, 3, 6, 5, 7);
        }

        @Test
        @DisplayName("postOrder() ends with the root and visits each node after its children")
        void postOrderEndsWithRootAndHasCorrectSequence() {
            var keys = tree.postOrder().stream().map(SearchTree.Entry::key).toList();
            assertThat(keys.getLast())
                .as("[tree.postOrder()] last element must be the root")
                .isEqualTo(4);
            assertThatTree(tree).hasPostOrderKeys(1, 3, 2, 5, 7, 6, 4);
        }

        @Test
        @DisplayName("levelOrder() visits nodes breadth-first, left before right per level")
        void levelOrderIsCorrectBreadthFirst() {
            assertThatTree(tree).hasLevelOrderKeys(4, 2, 6, 1, 3, 5, 7);
        }

        @Test
        @DisplayName("all four traversals contain exactly size() entries")
        void allTraversalsHaveSizeEqualToTreeSize() {
            assertThatTree(tree).allTraversalsHaveSize(tree.size());
        }

        @Test
        @DisplayName("traversal results are unmodifiable lists")
        void traversalResultsAreUnmodifiable() {
            assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> tree.inOrder().add(new SearchTree.Entry<>(99, "x")));
            assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> tree.preOrder().add(new SearchTree.Entry<>(99, "x")));
            assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> tree.postOrder().add(new SearchTree.Entry<>(99, "x")));
            assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> tree.levelOrder().add(new SearchTree.Entry<>(99, "x")));
        }

        @Test
        @DisplayName("rebuilding the tree from preOrder insertion reproduces the same inOrder")
        void rebuildFromPreOrderGivesSameInOrder() {
            var preOrderSnapshot = tree.preOrder();
            var rebuilt          = createTree();
            preOrderSnapshot.forEach(e -> rebuilt.insert(e.key(), e.value()));

            assertThat(rebuilt.inOrder())
                .as("Rebuilding from preOrder must produce an identical inOrder sequence")
                .isEqualTo(tree.inOrder());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 9  Height and balance
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("9 · Height and balance")
    class HeightAndBalance {

        @Test
        @DisplayName("height is -1 for an empty tree")
        void heightIsMinusOneForEmptyTree() {
            assertThatTree(tree).hasHeight(-1);
        }

        @Test
        @DisplayName("height is 0 for a single-node tree")
        void heightIsZeroForSingleNode() {
            tree.insert(1, "one");
            assertThatTree(tree).hasHeight(0);
        }

        @Test
        @DisplayName("perfectly balanced 7-node tree has height 2 and isBalanced() == true")
        void balancedFixtureHasCorrectHeightAndIsBalanced() {
            balanced(tree);
            assertThatTree(tree).hasHeight(2).isHeightBalanced();
        }

        @Test
        @DisplayName("left-skewed tree has height n-1 and isBalanced() == false")
        void leftSkewedTreeIsUnbalancedWithMaxHeight() {
            leftSkewed(tree);    // 5 nodes inserted descending
            assertThatTree(tree).hasHeight(4).isNotHeightBalanced();
        }

        @Test
        @DisplayName("right-skewed tree has height n-1 and isBalanced() == false")
        void rightSkewedTreeIsUnbalancedWithMaxHeight() {
            rightSkewed(tree);   // 5 nodes inserted ascending
            assertThatTree(tree).hasHeight(4).isNotHeightBalanced();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 10  InsertAll
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("10 · InsertAll")
    class InsertAll {

        @Test
        @DisplayName("insertAll() inserts all entries and preserves BST ordering")
        void insertAllFillsTreeAndPreservesOrdering() {
            tree.insertAll(bulkMap());

            assertThatTree(tree)
                .hasSize(BULK_INORDER.size())
                .satisfiesBstOrdering()
                .hasInOrderKeysSorted();

            for (int key : BULK_INORDER) {
                assertThatTree(tree).containsKey(key);
            }
        }

        @Test
        @DisplayName("insertAll() with an empty map is a safe no-op")
        void insertAllEmptyMapIsNoOp() {
            tree.insertAll(java.util.Map.of());
            assertThatTree(tree).isEmpty();
        }

        @Test
        @DisplayName("insertAll() with a null argument throws NullPointerException")
        void insertAllNullThrowsNpe() {
            assertThatNullPointerException()
                .isThrownBy(() -> tree.insertAll(null))
                .withMessage("entries");
        }

        @Test
        @DisplayName("insertAll() overwrites existing entries for duplicate keys")
        void insertAllOverwritesDuplicateKeys() {
            tree.insert(10, "old-ten");
            tree.insertAll(java.util.Map.of(10, "new-ten", 20, "twenty"));

            assertThatTree(tree)
                .hasSize(2)
                .searchFinds(10, "new-ten");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 11  Range search
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("11 · Range search")
    class RangeSearch {

        @BeforeEach
        void fill() { balanced(tree); }
        // keys: 1, 2, 3, 4, 5, 6, 7

        @Test
        @DisplayName("rangeSearch([min, max]) returns all entries in ascending key order")
        void rangeSpanningAllKeysReturnsAll() {
            assertThatTree(tree).rangeSearchReturnsKeys(1, 7, 1, 2, 3, 4, 5, 6, 7);
        }

        @Test
        @DisplayName("rangeSearch([k, k]) returns exactly one entry for a present key")
        void singleKeyRangeReturnsExactlyOneEntry() {
            assertThatTree(tree).rangeSearchReturnsKeys(4, 4, 4);
        }

        @Test
        @DisplayName("rangeSearch() results are always in ascending key order")
        void rangeSearchResultsAreSortedAscending() {
            var keys = tree.rangeSearch(2, 6).stream().map(SearchTree.Entry::key).toList();
            assertThat(keys)
                .as("[tree.rangeSearch(2,6)] must return keys in ascending order")
                .isSorted()
                .containsExactly(2, 3, 4, 5, 6);
        }

        @Test
        @DisplayName("rangeSearch([from, to]) where from > to returns an empty list")
        void invertedRangeReturnsEmpty() {
            assertThatTree(tree).rangeSearchIsEmpty(7, 1);
        }

        @Test
        @DisplayName("rangeSearch() with a range entirely below all keys returns empty")
        void rangeBelowAllKeysReturnsEmpty() {
            assertThatTree(tree).rangeSearchIsEmpty(-10, 0);
        }

        @Test
        @DisplayName("rangeSearch() with a range entirely above all keys returns empty")
        void rangeAboveAllKeysReturnsEmpty() {
            assertThatTree(tree).rangeSearchIsEmpty(100, 200);
        }

        @ParameterizedTest(name = "rangeSearch([{0},{0}]) — single-point query for key {0}")
        @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
        @DisplayName("single-point range returns exactly the matching entry")
        void singlePointRangeForEveryKey(int key) {
            var result = tree.rangeSearch(key, key);
            assertThat(result)
                .as("[tree.rangeSearch(%d,%d)]".formatted(key, key))
                .hasSize(1)
                .first()
                .extracting(SearchTree.Entry::key)
                .isEqualTo(key);
        }

        @Test
        @DisplayName("rangeSearch() null 'from' throws NullPointerException")
        void rangeSearchNullFromThrowsNpe() {
            assertThatNullPointerException().isThrownBy(() -> tree.rangeSearch(null, 5));
        }

        @Test
        @DisplayName("rangeSearch() null 'to' throws NullPointerException")
        void rangeSearchNullToThrowsNpe() {
            assertThatNullPointerException().isThrownBy(() -> tree.rangeSearch(1, null));
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // 12  Mixed-operation invariant checks
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("12 · BST invariant under mixed operations")
    class MixedOperationInvariant {

        @Test
        @DisplayName("BST ordering holds after alternating inserts and deletes")
        void orderingHoldsAfterAlternatingInsertAndDelete() {
            tree.insert(5, "five");
            tree.insert(3, "three");
            tree.insert(7, "seven");
            assertBstInvariant();

            tree.delete(3);
            assertBstInvariant();

            tree.insert(2, "two");
            tree.insert(4, "four");
            assertBstInvariant();

            tree.delete(5);
            assertBstInvariant();
        }

        @Test
        @DisplayName("BST ordering holds after deleting leaf, one-child, and two-children nodes in sequence")
        void orderingHoldsAfterAllThreeDeletionCases() {
            int[] insertOrder = {50, 25, 75, 10, 40, 60, 90, 5, 15, 35, 45};
            for (int k : insertOrder) tree.insert(k, String.valueOf(k));
            assertBstInvariant();

            tree.delete(5);   // leaf
            assertBstInvariant();

            tree.delete(75);  // node with children [60] and [90]
            assertBstInvariant();

            tree.delete(50);  // root (two children)
            assertBstInvariant();
        }

        @Test
        @DisplayName("inserting the same key set in different orders produces the same inOrder")
        void differentInsertionOrdersProduceSameInOrder() {
            int[] ascending  = {1, 2, 3, 4, 5, 6, 7};
            int[] descending = {7, 6, 5, 4, 3, 2, 1};
            int[] mixed      = {4, 2, 6, 1, 3, 5, 7};

            var treeA = createTree();
            var treeB = createTree();
            var treeC = createTree();

            for (int k : ascending)  treeA.insert(k, word(k));
            for (int k : descending) treeB.insert(k, word(k));
            for (int k : mixed)      treeC.insert(k, word(k));

            assertThat(treeA.inOrder())
                .as("Different insertion orders must produce identical inOrder sequences")
                .isEqualTo(treeB.inOrder())
                .isEqualTo(treeC.inOrder());
        }
    }
}
