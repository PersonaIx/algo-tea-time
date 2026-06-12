package org.tea.datastructures.trees.search;

import org.tea.datastructures.trees.search.SearchTree;

import java.util.List;
import java.util.Map;

/**
 * Named, self-documenting tree fixtures shared across the entire test suite.
 *
 * <h2>Design rules</h2>
 * <ul>
 *   <li>Every fixture documents its exact shape with an ASCII diagram.</li>
 *   <li>Fixtures are stateless factory methods — they write into a caller-supplied
 *       tree so each test gets a fresh instance with no shared state.</li>
 *   <li>Companion constants (KEYS_*, ENTRIES_*) give tests a single source of
 *       truth for expected values instead of repeating magic numbers.</li>
 *   <li>Value strings always match the English word for the integer key so
 *       assertions read naturally: {@code entry(4, "four")}.</li>
 * </ul>
 */
public final class TreeFixtures {

    private TreeFixtures() {}

    // ── Helpers ────────────────────────────────────────────────────────────────

    /** Convenience factory for readable assertion arguments. */
    public static <K, V> SearchTree.Entry<K, V> entry(K key, V value) {
        return new SearchTree.Entry<>(key, value);
    }

    /** Canonical English word for keys 1–9. */
    public static String word(int key) {
        return switch (key) {
            case 1 -> "one";   case 2 -> "two";   case 3 -> "three";
            case 4 -> "four";  case 5 -> "five";  case 6 -> "six";
            case 7 -> "seven"; case 8 -> "eight"; case 9 -> "nine";
            default -> throw new IllegalArgumentException("No word for " + key);
        };
    }

    // ══════════════════════════════════════════════════════════════════════════
    // EMPTY
    // ══════════════════════════════════════════════════════════════════════════

    /** Leaves the tree untouched — used to test brand-new tree behaviour. */
    public static void empty(SearchTree<Integer, String> tree) { /* nothing */ }

    // ══════════════════════════════════════════════════════════════════════════
    // SINGLE
    //   [5]
    // ══════════════════════════════════════════════════════════════════════════

    public static void single(SearchTree<Integer, String> tree) {
        tree.insert(5, "five");
    }

    public static final List<Integer>          SINGLE_KEYS    = List.of(5);
    public static final List<SearchTree.Entry<Integer,String>> SINGLE_ENTRIES = List.of(entry(5,"five"));

    // ══════════════════════════════════════════════════════════════════════════
    // BALANCED — perfect binary tree, depth 2, 7 nodes
    //
    //          [4]
    //        /     \
    //      [2]     [6]
    //      / \     / \
    //    [1] [3] [5] [7]
    //
    // height=2, isBalanced=true
    // inOrder  = [1,2,3,4,5,6,7]
    // preOrder = [4,2,1,3,6,5,7]
    // postOrder= [1,3,2,5,7,6,4]
    // levelOrder=[4,2,6,1,3,5,7]
    // ══════════════════════════════════════════════════════════════════════════

    public static void balanced(SearchTree<Integer, String> tree) {
        tree.insert(4, "four");
        tree.insert(2, "two");
        tree.insert(6, "six");
        tree.insert(1, "one");
        tree.insert(3, "three");
        tree.insert(5, "five");
        tree.insert(7, "seven");
    }

    public static final List<Integer> BALANCED_INORDER     = List.of(1, 2, 3, 4, 5, 6, 7);
    public static final List<Integer> BALANCED_PREORDER    = List.of(4, 2, 1, 3, 6, 5, 7);
    public static final List<Integer> BALANCED_POSTORDER   = List.of(1, 3, 2, 5, 7, 6, 4);
    public static final List<Integer> BALANCED_LEVELORDER  = List.of(4, 2, 6, 1, 3, 5, 7);

    public static final List<SearchTree.Entry<Integer,String>> BALANCED_ENTRIES_INORDER = List.of(
        entry(1,"one"), entry(2,"two"),   entry(3,"three"), entry(4,"four"),
        entry(5,"five"), entry(6,"six"),   entry(7,"seven")
    );

    // ══════════════════════════════════════════════════════════════════════════
    // LEFT-SKEWED — worst-case BST, descending insertion
    //
    //   [5]
    //   /
    // [4]
    //  /
    // [3]
    //  /
    // [2]
    //  /
    // [1]
    //
    // height=4, isBalanced=false
    // ══════════════════════════════════════════════════════════════════════════

    public static void leftSkewed(SearchTree<Integer, String> tree) {
        for (int k = 5; k >= 1; k--) tree.insert(k, word(k));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // RIGHT-SKEWED — worst-case BST, ascending insertion
    //
    // [1]
    //   \
    //   [2]
    //     \
    //     [3]
    //       \
    //       [4]
    //         \
    //         [5]
    //
    // height=4, isBalanced=false
    // ══════════════════════════════════════════════════════════════════════════

    public static void rightSkewed(SearchTree<Integer, String> tree) {
        for (int k = 1; k <= 5; k++) tree.insert(k, word(k));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SPARSE — gap-heavy keys; essential for floor/ceiling and range tests
    //
    //       [10]
    //      /    \
    //    [3]   [20]
    //      \   /
    //      [7][15]
    //
    // Keys: 3, 7, 10, 15, 20
    // ══════════════════════════════════════════════════════════════════════════

    public static void sparse(SearchTree<Integer, String> tree) {
        tree.insert(10, "ten");
        tree.insert(3,  "three");
        tree.insert(20, "twenty");
        tree.insert(7,  "seven");
        tree.insert(15, "fifteen");
    }

    public static final List<Integer> SPARSE_INORDER = List.of(3, 7, 10, 15, 20);

    // ══════════════════════════════════════════════════════════════════════════
    // STRING-KEYED — verifies Comparable works beyond Integer
    //
    //         [mango]
    //         /      \
    //    [banana]  [orange]
    //    /
    // [apple]
    //
    // inOrder (alphabetical) = [apple, banana, mango, orange]
    // ══════════════════════════════════════════════════════════════════════════

    public static void fruits(SearchTree<String, Integer> tree) {
        tree.insert("mango",  3);
        tree.insert("banana", 2);
        tree.insert("orange", 4);
        tree.insert("apple",  1);
    }

    public static final List<String>            FRUITS_INORDER  = List.of("apple","banana","mango","orange");
    public static final List<SearchTree.Entry<String,Integer>> FRUITS_ENTRIES = List.of(
        entry("apple",1), entry("banana",2), entry("mango",3), entry("orange",4)
    );

    // ══════════════════════════════════════════════════════════════════════════
    // BULK MAP — for insertAll tests; out-of-order by design
    // ══════════════════════════════════════════════════════════════════════════

    public static Map<Integer, String> bulkMap() {
        return Map.of(
            10, "ten",   5, "five",   15, "fifteen",
             3, "three", 7, "seven",  12, "twelve",  18, "eighteen"
        );
    }

    public static final List<Integer> BULK_INORDER = List.of(3, 5, 7, 10, 12, 15, 18);
}
