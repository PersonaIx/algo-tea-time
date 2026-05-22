package org.tea.algos.datastructures.lists.linkedlists;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Comprehensive test suite for LinkedListOperations<T>.
 *
 * Replace `new YourDoublyLinkedList<>()` with your concrete implementation.
 * The suite assumes a doubly-linked list but is valid for any correct implementation.
 *
 * Test categories (JUnit 5 @Tag):
 *   - "core"      : fundamental contract tests
 *   - "mutation"  : addFirst / addLast / remove / reverse
 *   - "query"     : contains / hasCycle / size / isEmpty / toList
 *   - "edge"      : empty list, single element, duplicates, nulls
 *   - "order"     : structural ordering guarantees
 *   - "stress"    : larger inputs
 */
@DisplayName("DoubleLinkedListTest")
class DoubleLinkedListTest {

    // -----------------------------------------------------------------------
    // Helper – replace with your concrete class
    // -----------------------------------------------------------------------
    private LinkedListOperations<Integer> list;
    private LinkedListOperations<String>  strList;

    @BeforeEach
    void setUp() {
        list    = new DoubleLinkedList<>();   // <-- swap in your class
        strList = new DoubleLinkedList<>();
    }

    // =======================================================================
    // isEmpty / size – empty list invariants
    // =======================================================================
    @Nested
    @DisplayName("isEmpty()")
    @Tag("query") @Tag("edge")
    class IsEmptyTests {

        @Test
        @DisplayName("newly created list is empty")
        void newListIsEmpty() {
            assertThat(list.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("list is not empty after addFirst")
        void notEmptyAfterAddFirst() {
            list.addFirst(1);
            assertThat(list.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("list is not empty after addLast")
        void notEmptyAfterAddLast() {
            list.addLast(1);
            assertThat(list.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("list becomes empty again after sole element is removed")
        void emptyAfterSoleElementRemoved() {
            list.addFirst(42);
            list.remove(42);
            assertThat(list.isEmpty()).isTrue();
        }
    }

    // =======================================================================
    // size()
    // =======================================================================
    @Nested
    @DisplayName("size()")
    @Tag("query") @Tag("core")
    class SizeTests {

        @Test
        @DisplayName("size of empty list is 0")
        void emptyListHasSizeZero() {
            assertThat(list.size()).isZero();
        }

        @Test
        @DisplayName("size increments by 1 per addFirst")
        void sizeGrowsWithAddFirst() {
            for (int i = 1; i <= 5; i++) {
                list.addFirst(i);
                assertThat(list.size()).isEqualTo(i);
            }
        }

        @Test
        @DisplayName("size increments by 1 per addLast")
        void sizeGrowsWithAddLast() {
            for (int i = 1; i <= 5; i++) {
                list.addLast(i);
                assertThat(list.size()).isEqualTo(i);
            }
        }

        @Test
        @DisplayName("size decrements by 1 on successful remove")
        void sizeShrinksOnRemove() {
            list.addLast(10);
            list.addLast(20);
            list.addLast(30);
            list.remove(20);
            assertThat(list.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("size is unchanged on failed remove (value absent)")
        void sizeUnchangedOnFailedRemove() {
            list.addLast(10);
            list.remove(99);
            assertThat(list.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("size is unchanged after reverse")
        void sizeUnchangedAfterReverse() {
            list.addLast(1);
            list.addLast(2);
            list.addLast(3);
            list.reverse();
            assertThat(list.size()).isEqualTo(3);
        }
    }

    // =======================================================================
    // addFirst()
    // =======================================================================
    @Nested
    @DisplayName("addFirst()")
    @Tag("mutation") @Tag("order")
    class AddFirstTests {

        @Test
        @DisplayName("single addFirst – element becomes head and tail")
        void singleAddFirstBecomesHeadAndTail() {
            list.addFirst(7);
            assertThat(list.toList()).containsExactly(7);
        }

        @Test
        @DisplayName("multiple addFirst – elements are prepended (LIFO order)")
        void multipleAddFirstPrependsInLIFOOrder() {
            list.addFirst(1);
            list.addFirst(2);
            list.addFirst(3);
            // head → 3 → 2 → 1 → tail
            assertThat(list.toList()).containsExactly(3, 2, 1);
        }

        @Test
        @DisplayName("addFirst on non-empty list places value before existing head")
        void addFirstPlacesValueBeforeExistingHead() {
            list.addLast(10);
            list.addLast(20);
            list.addFirst(5);
            assertThat(list.toList()).startsWith(5);
        }

        @Test
        @DisplayName("addFirst with null value – accepted without exception (null support)")
        void addFirstNullDoesNotThrow() {
            LinkedListOperations<String> nullList = new DoubleLinkedList<>();
            assertThatCode(() -> nullList.addFirst(null)).doesNotThrowAnyException();
            assertThat(nullList.size()).isEqualTo(1);
        }
    }

    // =======================================================================
    // addLast()
    // =======================================================================
    @Nested
    @DisplayName("addLast()")
    @Tag("mutation") @Tag("order")
    class AddLastTests {

        @Test
        @DisplayName("single addLast – element becomes head and tail")
        void singleAddLastBecomesHeadAndTail() {
            list.addLast(7);
            assertThat(list.toList()).containsExactly(7);
        }

        @Test
        @DisplayName("multiple addLast – elements are appended (FIFO order)")
        void multipleAddLastAppendsInFIFOOrder() {
            list.addLast(1);
            list.addLast(2);
            list.addLast(3);
            assertThat(list.toList()).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("addLast on non-empty list places value after existing tail")
        void addLastPlacesValueAfterExistingTail() {
            list.addFirst(10);
            list.addFirst(20);
            list.addLast(99);
            assertThat(list.toList()).endsWith(99);
        }
    }

    // =======================================================================
    // remove()
    // =======================================================================
    @Nested
    @DisplayName("remove()")
    @Tag("mutation") @Tag("core")
    class RemoveTests {

        @Test
        @DisplayName("returns false when list is empty")
        void returnsFalseOnEmptyList() {
            assertThat(list.remove(1)).isFalse();
        }

        @Test
        @DisplayName("returns false when value is absent")
        void returnsFalseWhenAbsent() {
            list.addLast(1);
            list.addLast(2);
            assertThat(list.remove(99)).isFalse();
        }

        @Test
        @DisplayName("returns true when value is present")
        void returnsTrueWhenPresent() {
            list.addLast(5);
            assertThat(list.remove(5)).isTrue();
        }

        @Test
        @DisplayName("removes head element correctly")
        void removesHead() {
            list.addLast(1);
            list.addLast(2);
            list.addLast(3);
            list.remove(1);
            assertThat(list.toList()).containsExactly(2, 3);
        }

        @Test
        @DisplayName("removes tail element correctly")
        void removesTail() {
            list.addLast(1);
            list.addLast(2);
            list.addLast(3);
            list.remove(3);
            assertThat(list.toList()).containsExactly(1, 2);
        }

        @Test
        @DisplayName("removes middle element correctly")
        void removesMiddle() {
            list.addLast(1);
            list.addLast(2);
            list.addLast(3);
            list.remove(2);
            assertThat(list.toList()).containsExactly(1, 3);
        }

        @Test
        @DisplayName("removes only the first occurrence of a duplicate value")
        void removesFirstOccurrenceOfDuplicate() {
            list.addLast(5);
            list.addLast(5);
            list.addLast(5);
            list.remove(5);
            assertThat(list.size()).isEqualTo(2);
            assertThat(list.toList()).containsExactly(5, 5);
        }

        @Test
        @DisplayName("remove sole element leaves list empty")
        void removeSoleElementLeavesEmpty() {
            list.addFirst(42);
            list.remove(42);
            assertThat(list.isEmpty()).isTrue();
            assertThat(list.toList()).isEmpty();
        }

        @Test
        @DisplayName("list is still traversable (no broken links) after removing middle")
        void listTraversableAfterMiddleRemoval() {
            list.addLast(1);
            list.addLast(2);
            list.addLast(3);
            list.addLast(4);
            list.addLast(5);
            list.remove(3);
            // Verify both forward traversal integrity and size
            assertThat(list.toList())
                    .hasSize(4)
                    .containsExactly(1, 2, 4, 5);
        }
    }

    // =======================================================================
    // contains()
    // =======================================================================
    @Nested
    @DisplayName("contains()")
    @Tag("query") @Tag("core")
    class ContainsTests {

        @Test
        @DisplayName("empty list contains nothing")
        void emptyListContainsNothing() {
            assertThat(list.contains(1)).isFalse();
        }

        @Test
        @DisplayName("contains head element")
        void containsHead() {
            list.addLast(10);
            list.addLast(20);
            assertThat(list.contains(10)).isTrue();
        }

        @Test
        @DisplayName("contains tail element")
        void containsTail() {
            list.addLast(10);
            list.addLast(20);
            assertThat(list.contains(20)).isTrue();
        }

        @Test
        @DisplayName("contains middle element")
        void containsMiddle() {
            list.addLast(10);
            list.addLast(15);
            list.addLast(20);
            assertThat(list.contains(15)).isTrue();
        }

        @Test
        @DisplayName("does not contain absent value")
        void doesNotContainAbsent() {
            list.addLast(10);
            list.addLast(20);
            assertThat(list.contains(99)).isFalse();
        }

        @Test
        @DisplayName("contains returns false after the only element is removed")
        void falseAfterElementRemoved() {
            list.addFirst(7);
            list.remove(7);
            assertThat(list.contains(7)).isFalse();
        }

        @Test
        @DisplayName("contains still true when duplicate value present and one removed")
        void trueWhenDuplicatePartiallyRemoved() {
            list.addLast(5);
            list.addLast(5);
            list.remove(5);
            assertThat(list.contains(5)).isTrue();
        }

        @Test
        @DisplayName("contains works with String values using equals semantics")
        void containsUsesEquality() {
            strList.addLast("hello");
            strList.addLast("world");
            assertThat(strList.contains(new String("hello"))).isTrue(); // not same reference
        }
    }

    // =======================================================================
    // reverse()
    // =======================================================================
    @Nested
    @DisplayName("reverse()")
    @Tag("mutation") @Tag("order")
    class ReverseTests {

        @Test
        @DisplayName("reverse on empty list does not throw")
        void reverseEmptyIsNoOp() {
            assertThatCode(() -> list.reverse()).doesNotThrowAnyException();
            assertThat(list.toList()).isEmpty();
        }

        @Test
        @DisplayName("reverse single-element list is identity")
        void reverseSingleElement() {
            list.addFirst(1);
            list.reverse();
            assertThat(list.toList()).containsExactly(1);
        }

        @Test
        @DisplayName("reverse two-element list swaps elements")
        void reverseTwoElements() {
            list.addLast(1);
            list.addLast(2);
            list.reverse();
            assertThat(list.toList()).containsExactly(2, 1);
        }

        @Test
        @DisplayName("reverse odd-length list")
        void reverseOddLength() {
            list.addLast(1);
            list.addLast(2);
            list.addLast(3);
            list.addLast(4);
            list.addLast(5);
            list.reverse();
            assertThat(list.toList()).containsExactly(5, 4, 3, 2, 1);
        }

        @Test
        @DisplayName("reverse even-length list")
        void reverseEvenLength() {
            list.addLast(10);
            list.addLast(20);
            list.addLast(30);
            list.addLast(40);
            list.reverse();
            assertThat(list.toList()).containsExactly(40, 30, 20, 10);
        }

        @Test
        @DisplayName("double reverse restores original order")
        void doubleReverseIsIdentity() {
            list.addLast(1);
            list.addLast(2);
            list.addLast(3);
            list.reverse();
            list.reverse();
            assertThat(list.toList()).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("reverse preserves size")
        void reversePreservesSize() {
            list.addLast(1);
            list.addLast(2);
            list.addLast(3);
            int before = list.size();
            list.reverse();
            assertThat(list.size()).isEqualTo(before);
        }

        @Test
        @DisplayName("addLast after reverse appends to new tail")
        void addLastAfterReverseAppendsToNewTail() {
            list.addLast(1);
            list.addLast(2);
            list.addLast(3);
            list.reverse();            // now [3, 2, 1]
            list.addLast(99);
            assertThat(list.toList()).containsExactly(3, 2, 1, 99);
        }

        @Test
        @DisplayName("addFirst after reverse prepends to new head")
        void addFirstAfterReversePrependToNewHead() {
            list.addLast(1);
            list.addLast(2);
            list.addLast(3);
            list.reverse();            // now [3, 2, 1]
            list.addFirst(0);
            assertThat(list.toList()).containsExactly(0, 3, 2, 1);
        }
    }

    // =======================================================================
    // hasCycle()
    // =======================================================================
    @Nested
    @DisplayName("hasCycle()")
    @Tag("query") @Tag("edge")
    class HasCycleTests {

        @Test
        @DisplayName("empty list has no cycle")
        void emptyListHasNoCycle() {
            assertThat(list.hasCycle()).isFalse();
        }

        @Test
        @DisplayName("single-element list has no cycle")
        void singleElementHasNoCycle() {
            list.addFirst(1);
            assertThat(list.hasCycle()).isFalse();
        }

        @Test
        @DisplayName("standard multi-element list has no cycle")
        void standardListHasNoCycle() {
            list.addLast(1);
            list.addLast(2);
            list.addLast(3);
            assertThat(list.hasCycle()).isFalse();
        }

        /*
         * To test a true cycle you need package/friend access or a test-only
         * helper. The snippet below assumes your concrete class exposes a
         * method `injectCycle()` (or similar) for testing purposes.
         * If it does not, remove or adapt these tests.
         */
        @Test
        @DisplayName("list with artificially injected cycle returns true")
        void injectedCycleDetected() {
            // Precondition: your implementation exposes a way to create a cycle.
            // Example (adapt to your class):
            //   YourDoublyLinkedList<Integer> concrete = (YourDoublyLinkedList<Integer>) list;
            //   concrete.addLast(1); concrete.addLast(2); concrete.addLast(3);
            //   concrete.createCycleForTesting(); // points tail.next → head
            //   assertThat(list.hasCycle()).isTrue();
            //
            // If you cannot inject a cycle, this test is a documentation placeholder.
            assumeTrue(false, "Cycle injection requires concrete-class access — adapt as needed.");
        }
    }

    // =======================================================================
    // toList()
    // =======================================================================
    @Nested
    @DisplayName("toList()")
    @Tag("query") @Tag("core")
    class ToListTests {

        @Test
        @DisplayName("empty list returns empty List")
        void emptyListReturnsEmptyList() {
            assertThat(list.toList()).isEmpty();
        }

        @Test
        @DisplayName("toList returns a snapshot – mutations do not affect returned list")
        void toListReturnsSnapshot() {
            list.addLast(1);
            list.addLast(2);
            List<Integer> snapshot = list.toList();
            list.addLast(3);
            assertThat(snapshot).hasSize(2); // unchanged
        }

        @Test
        @DisplayName("toList preserves insertion order (addLast)")
        void toListPreservesAddLastOrder() {
            list.addLast(10);
            list.addLast(20);
            list.addLast(30);
            assertThat(list.toList()).containsExactly(10, 20, 30);
        }

        @Test
        @DisplayName("toList size matches size()")
        void toListSizeMatchesSizeMethod() {
            list.addLast(1);
            list.addLast(2);
            list.addLast(3);
            assertThat(list.toList()).hasSize(list.size());
        }

        @Test
        @DisplayName("toList does not return null")
        void toListNeverReturnsNull() {
            assertThat(list.toList()).isNotNull();
        }
    }

    // =======================================================================
    // Interaction / combined behaviour
    // =======================================================================
    @Nested
    @DisplayName("Combined / interaction scenarios")
    @Tag("core")
    class InteractionTests {

        @Test
        @DisplayName("interleaved addFirst and addLast produce correct order")
        void interleavedAdds() {
            list.addLast(2);
            list.addFirst(1);
            list.addLast(3);
            list.addFirst(0);
            assertThat(list.toList()).containsExactly(0, 1, 2, 3);
        }

        @Test
        @DisplayName("contains is false for every element after all are removed")
        void containsFalseAfterAllRemoved() {
            int[] values = {1, 2, 3, 4, 5};
            for (int v : values) list.addLast(v);
            for (int v : values) list.remove(v);
            for (int v : values) {
                assertThat(list.contains(v)).isFalse();
            }
            assertThat(list.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("reverse then remove leaves list consistent")
        void reverseThenRemove() {
            list.addLast(1);
            list.addLast(2);
            list.addLast(3);
            list.reverse();            // [3, 2, 1]
            list.remove(2);            // [3, 1]
            assertThat(list.toList()).containsExactly(3, 1);
            assertThat(list.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("remove head repeatedly until empty – size and isEmpty consistent")
        void removeHeadRepeatedly() {
            list.addLast(1);
            list.addLast(2);
            list.addLast(3);
            List<Integer> expected = List.of(1, 2, 3);
            for (int i = 0; i < expected.size(); i++) {
                assertThat(list.remove(expected.get(i))).isTrue();
                assertThat(list.size()).isEqualTo(expected.size() - i - 1);
            }
            assertThat(list.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("remove tail repeatedly until empty – order intact")
        void removeTailRepeatedly() {
            list.addLast(10);
            list.addLast(20);
            list.addLast(30);
            list.remove(30);
            list.remove(20);
            list.remove(10);
            assertThat(list.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("alternating add/remove keeps internal pointers consistent")
        void alternatingAddRemove() {
            list.addLast(1);
            list.addLast(2);
            list.remove(1);
            list.addFirst(0);
            list.addLast(3);
            list.remove(2);
            assertThat(list.toList()).containsExactly(0, 3);
        }
    }

    // =======================================================================
    // Duplicate values
    // =======================================================================
    @Nested
    @DisplayName("Duplicate values")
    @Tag("edge")
    class DuplicateTests {

        @Test
        @DisplayName("all duplicates are stored (list allows duplicates)")
        void allDuplicatesStored() {
            list.addLast(7);
            list.addLast(7);
            list.addLast(7);
            assertThat(list.size()).isEqualTo(3);
            assertThat(list.toList()).containsExactly(7, 7, 7);
        }

        @Test
        @DisplayName("remove on duplicates removes exactly one occurrence per call")
        void removeEachDuplicateOneAtATime() {
            list.addLast(5);
            list.addLast(5);
            list.addLast(5);
            list.remove(5);
            assertThat(list.size()).isEqualTo(2);
            list.remove(5);
            assertThat(list.size()).isEqualTo(1);
            list.remove(5);
            assertThat(list.isEmpty()).isTrue();
        }
    }

    // =======================================================================
    // Stress / larger inputs
    // =======================================================================
    @Nested
    @DisplayName("Stress tests")
    @Tag("stress")
    class StressTests {

        private static final int N = 10_000;

        @Test
        @DisplayName("addLast N elements – size and order correct")
        void addLastNElements() {
            for (int i = 0; i < N; i++) list.addLast(i);
            assertThat(list.size()).isEqualTo(N);
            assertThat(list.toList()).first().isEqualTo(0);
            assertThat(list.toList()).last().isEqualTo(N - 1);
        }

        @Test
        @DisplayName("addFirst N elements – size and order correct")
        void addFirstNElements() {
            for (int i = 0; i < N; i++) list.addFirst(i);
            assertThat(list.size()).isEqualTo(N);
            assertThat(list.toList()).first().isEqualTo(N - 1);
            assertThat(list.toList()).last().isEqualTo(0);
        }

        @Test
        @DisplayName("remove all N elements – ends empty")
        void removeAllNElements() {
            for (int i = 0; i < N; i++) list.addLast(i);
            for (int i = 0; i < N; i++) list.remove(i);
            assertThat(list.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("reverse N-element list – O(n) operation completes, order correct")
        void reverseNElements() {
            for (int i = 0; i < N; i++) list.addLast(i);
            list.reverse();
            assertThat(list.toList()).first().isEqualTo(N - 1);
            assertThat(list.toList()).last().isEqualTo(0);
        }

        @ParameterizedTest(name = "size after {0} addLast calls")
        @ValueSource(ints = {1, 10, 100, 1000})
        @DisplayName("parameterised – size grows correctly")
        void parameterisedSize(int n) {
            for (int i = 0; i < n; i++) list.addLast(i);
            assertThat(list.size()).isEqualTo(n);
        }
    }

    // =======================================================================
    // Generic / type safety smoke tests
    // =======================================================================
    @Nested
    @DisplayName("Generic type smoke tests")
    @Tag("core")
    class GenericTypeTests {

        @Test
        @DisplayName("String list operations work correctly")
        void stringListWorks() {
            strList.addLast("alpha");
            strList.addLast("beta");
            strList.addLast("gamma");
            strList.remove("beta");
            assertThat(strList.toList()).containsExactly("alpha", "gamma");
        }

        @Test
        @DisplayName("Double list operations work correctly")
        void doubleListWorks() {
            LinkedListOperations<Double> dList = new DoubleLinkedList<>();
            dList.addFirst(3.14);
            dList.addLast(2.71);
            assertThat(dList.contains(3.14)).isTrue();
            assertThat(dList.size()).isEqualTo(2);
        }
    }

    // =======================================================================
    // Utility
    // =======================================================================

    /** Skips a test gracefully when a prerequisite cannot be met (e.g. cycle injection). */
    private static void assumeTrue(boolean condition, String reason) {
        org.junit.jupiter.api.Assumptions.assumeTrue(condition, reason);
    }
}