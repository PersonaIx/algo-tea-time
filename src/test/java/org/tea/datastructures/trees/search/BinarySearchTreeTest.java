package org.tea.datastructures.trees.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.tea.datastructures.trees.search.TreeAssert.assertThatTree;
import static org.tea.datastructures.trees.search.TreeFixtures.*;

/**
 * Test class for {@link BinarySearchTree}.
 *
 * <h2>Structure</h2>
 * <ul>
 *   <li>Extends {@link SearchTreeContractTest} — the full shared contract
 *       runs automatically against this implementation.</li>
 *   <li>Adds {@code @Nested} classes for behaviour that is specific to an
 *       <em>unbalanced</em> BST and therefore has no place in the contract:
 *       worst-case height, exact tree shapes, and skewed insertion paths.</li>
 * </ul>
 *
 * <h2>How to add a second implementation (e.g. AVL)</h2>
 * <pre>
 * class AvlTreeTest extends SearchTreeContractTest {
 *     {@literal @}Override protected SearchTree<Integer,String> createTree() {
 *         return new AvlTree<>();
 *     }
 *     {@literal @}Override protected boolean treeSupportsBalanceInvariant() {
 *         return true;   // contract suite also checks isBalanced() after every mutation
 *     }
 *     // add AVL-specific @Nested classes here
 * }
 * </pre>
 */
@DisplayName("BinarySearchTree")
class BinarySearchTreeTest extends SearchTreeContractTest {

    // ── Contract wiring ────────────────────────────────────────────────────────

    @Override
    protected SearchTree<Integer, String> createTree() {
        return new BinarySearchTree<>();
    }

    // Plain BST gives no balance guarantee — leave treeSupportsBalanceInvariant()
    // returning false (the default), so the contract suite treats isBalanced() as
    // diagnostic only, not a hard post-condition.

    // ══════════════════════════════════════════════════════════════════════════
    // BST-specific: skewed insertion (worst-case O(n) height)
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("BST · Skewed insertion (worst-case height)")
    class SkewedInsertion {

        private BinarySearchTree<Integer, String> bst;

        @BeforeEach
        void setUp() { bst = new BinarySearchTree<>(); }

        @Test
        @DisplayName("ascending insertion produces a right-skewed tree with height n-1")
        void ascendingInsertionIsRightSkewed() {
            rightSkewed(bst);                 // inserts 1,2,3,4,5

            assertThatTree(bst)
                .hasHeight(4)                 // n=5, every node only has a right child
                .hasSize(5)
                .satisfiesBstOrdering()
                .isNotHeightBalanced()
                .hasInOrderKeys(1, 2, 3, 4, 5)
                .hasLevelOrderKeys(1, 2, 3, 4, 5);  // level-order == insertion order for right-skew
        }

        @Test
        @DisplayName("descending insertion produces a left-skewed tree with height n-1")
        void descendingInsertionIsLeftSkewed() {
            leftSkewed(bst);                  // inserts 5,4,3,2,1

            assertThatTree(bst)
                .hasHeight(4)
                .hasSize(5)
                .satisfiesBstOrdering()
                .isNotHeightBalanced()
                .hasInOrderKeys(1, 2, 3, 4, 5)
                .hasLevelOrderKeys(5, 4, 3, 2, 1);  // level-order == insertion order for left-skew
        }

        @Test
        @DisplayName("100 keys inserted in ascending order degenerate to height 99")
        void largeAscendingInsertionDegeneratesToLinearHeight() {
            for (int i = 1; i <= 100; i++) bst.insert(i, String.valueOf(i));

            assertThatTree(bst)
                .hasHeight(99)
                .hasSize(100)
                .satisfiesBstOrdering()
                .hasInOrderKeysSorted();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // BST-specific: exact tree-shape verification via level-order
    //
    // Level-order uniquely identifies the tree shape (unlike inOrder, which
    // only confirms ordering). These tests pin the exact structure that results
    // from a given insertion sequence — valuable for regression and for
    // understanding how the unbalanced BST behaves.
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("BST · Exact tree shapes (level-order pinning)")
    class ExactTreeShape {

        private BinarySearchTree<Integer, String> bst;

        @BeforeEach
        void setUp() { bst = new BinarySearchTree<>(); }

        @Test
        @DisplayName("balanced fixture produces the expected perfect binary tree shape")
        void balancedFixtureHasExpectedShape() {
            balanced(bst);
            //          [4]
            //        /     \
            //      [2]     [6]
            //      / \     / \
            //    [1] [3] [5] [7]
            assertThatTree(bst).hasLevelOrderKeys(4, 2, 6, 1, 3, 5, 7);
        }

        @Test
        @DisplayName("insert into balanced tree on left side grows left subtree correctly")
        void insertOnLeftSideExtendsLeftSubtree() {
            balanced(bst);
            bst.insert(0, "zero");
            //          [4]
            //        /     \
            //      [2]     [6]
            //      / \     / \
            //    [1] [3] [5] [7]
            //    /
            //  [0]
            assertThatTree(bst)
                .hasSize(8)
                .satisfiesBstOrdering()
                .hasLevelOrderKeys(4, 2, 6, 1, 3, 5, 7, 0);
        }

        @Test
        @DisplayName("insert into balanced tree on right side grows right subtree correctly")
        void insertOnRightSideExtendsRightSubtree() {
            balanced(bst);
            bst.insert(8, "eight");
            //          [4]
            //        /     \
            //      [2]     [6]
            //      / \     / \
            //    [1] [3] [5] [7]
            //                  \
            //                  [8]
            assertThatTree(bst)
                .hasSize(8)
                .satisfiesBstOrdering()
                .hasLevelOrderKeys(4, 2, 6, 1, 3, 5, 7, 8);
        }

        @Test
        @DisplayName("deleting a two-children node replaces it with the in-order successor")
        void deleteTwoChildrenNodeUsesInOrderSuccessor() {
            balanced(bst);
            bst.delete(2);  // [2] has children [1] and [3]; successor is [3]
            //          [4]
            //        /     \
            //      [3]     [6]
            //      /       / \
            //    [1]     [5] [7]
            assertThatTree(bst)
                .hasSize(6)
                .satisfiesBstOrdering()
                .hasInOrderKeys(1, 3, 4, 5, 6, 7)
                .hasLevelOrderKeys(4, 3, 6, 1, 5, 7);
        }

        @Test
        @DisplayName("deleting the root replaces it with its in-order successor")
        void deleteRootUsesInOrderSuccessor() {
            balanced(bst);
            bst.delete(4);  // root; successor (min of right subtree) is [5]
            //          [5]
            //        /     \
            //      [2]     [6]
            //      / \       \
            //    [1] [3]     [7]
            assertThatTree(bst)
                .hasSize(6)
                .satisfiesBstOrdering()
                .hasInOrderKeys(1, 2, 3, 5, 6, 7)
                .hasLevelOrderKeys(5, 2, 6, 1, 3, 7);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // BST-specific: floor and ceiling — exact boundary precision
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("BST · Floor and ceiling — boundary precision")
    class FloorCeilingPrecision {

        private BinarySearchTree<Integer, String> bst;

        @BeforeEach
        void setUp() {
            bst = new BinarySearchTree<>();
            balanced(bst);  // keys 1..7
        }

        @ParameterizedTest(name = "floor({0}) → key {1}")
        @CsvSource({
            "1,  1",   // exact match, leftmost
            "2,  2",   // exact match, internal
            "4,  4",   // exact match, root
            "7,  7",   // exact match, rightmost
            "0, -1",   // below min — no floor (sentinel -1 = empty)
            "8,  7",   // above max — floor is max
        })
        @DisplayName("floor() returns the greatest key ≤ the query (exhaustive)")
        void floorPrecision(int query, int expectedKey) {
            if (expectedKey == -1) {
                assertThat(bst.floor(query))
                    .as("[bst.floor(%d)] should be empty".formatted(query))
                    .isEmpty();
            } else {
                assertThat(bst.floor(query))
                    .as("[bst.floor(%d)] should return key %d".formatted(query, expectedKey))
                    .isPresent()
                    .map(SearchTree.Entry::key)
                    .hasValue(expectedKey);
            }
        }

        @ParameterizedTest(name = "ceiling({0}) → key {1}")
        @CsvSource({
            "1,  1",   // exact match, leftmost
            "4,  4",   // exact match, root
            "7,  7",   // exact match, rightmost
            "0,  1",   // below min — ceiling is min
            "8, -1",   // above max — no ceiling (sentinel -1 = empty)
        })
        @DisplayName("ceiling() returns the smallest key ≥ the query (exhaustive)")
        void ceilingPrecision(int query, int expectedKey) {
            if (expectedKey == -1) {
                assertThat(bst.ceiling(query))
                    .as("[bst.ceiling(%d)] should be empty".formatted(query))
                    .isEmpty();
            } else {
                assertThat(bst.ceiling(query))
                    .as("[bst.ceiling(%d)] should return key %d".formatted(query, expectedKey))
                    .isPresent()
                    .map(SearchTree.Entry::key)
                    .hasValue(expectedKey);
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // BST-specific: non-integer key type (String)
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("BST · String keys — Comparable coverage beyond Integer")
    class StringKeys {

        private BinarySearchTree<String, Integer> bst;

        @BeforeEach
        void setUp() {
            bst = new BinarySearchTree<>();
            fruits(bst);  // apple(1), banana(2), mango(3), orange(4)
        }

        @Test
        @DisplayName("inOrder() with String keys is alphabetically sorted")
        void inOrderIsAlphabetical() {
            assertThat(bst.inOrder().stream().map(SearchTree.Entry::key).toList())
                .as("[bst.inOrder()] String keys must sort alphabetically")
                .containsExactlyElementsOf(FRUITS_INORDER)
                .isSorted();
        }

        @Test
        @DisplayName("min() and max() are correct with String keys")
        void minAndMaxCorrectWithStringKeys() {
            assertThatTree(bst).hasMin("apple", 1).hasMax("orange", 4);
        }

        @Test
        @DisplayName("search() and contains() work correctly with String keys")
        void searchAndContainsWithStringKeys() {
            assertThatTree(bst)
                .searchFinds("mango", 3)
                .containsKey("banana")
                .doesNotContainKey("grape");
        }

        @Test
        @DisplayName("rangeSearch() returns alphabetical slice with String keys")
        void rangeSearchWithStringKeys() {
            var result = bst.rangeSearch("banana", "mango");
            assertThat(result.stream().map(SearchTree.Entry::key).toList())
                .as("[bst.rangeSearch('banana','mango')] alphabetical range")
                .containsExactly("banana", "mango");
        }

        @Test
        @DisplayName("floor() and ceiling() work correctly with String keys")
        void floorAndCeilingWithStringKeys() {
            // "grape" is between "banana" and "mango" alphabetically
            assertThatTree(bst)
                .hasFloor("grape", "banana", 2)
                .hasCeiling("grape", "mango", 3);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // BST-specific: large-dataset stress test
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("BST · Large dataset stress")
    class LargeDataset {

        @Test
        @DisplayName("1000-node insert and 500-node delete maintain BST ordering throughout")
        void largeInsertAndDeleteMaintainOrdering() {
            var bst = new BinarySearchTree<Integer, String>();

            // Interleave from both ends to avoid extreme skew
            int lo = 1, hi = 1000;
            boolean fromLo = true;
            while (lo <= hi) {
                int k = fromLo ? lo++ : hi--;
                bst.insert(k, String.valueOf(k));
                fromLo = !fromLo;
            }

            assertThatTree(bst)
                .hasSize(1000)
                .satisfiesBstOrdering()
                .hasInOrderKeysSorted();

            // Delete all even keys
            for (int k = 2; k <= 1000; k += 2) bst.delete(k);

            assertThatTree(bst)
                .hasSize(500)
                .satisfiesBstOrdering()
                .hasInOrderKeysSorted();

            var remaining = bst.inOrder().stream().map(SearchTree.Entry::key).toList();
            assertThat(remaining)
                .as("After deleting all even keys, every remaining key must be odd")
                .allMatch(k -> k % 2 == 1);
        }
    }
}
