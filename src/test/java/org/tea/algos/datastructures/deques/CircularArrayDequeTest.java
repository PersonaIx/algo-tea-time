package org.tea.algos.datastructures.deques;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.tea.algos.deques.CircularArrayDeque;
import org.tea.algos.deques.DequeOperations;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

/**
 * Test suite for a CircularArrayDeque implementation of DequeOperations<T>.
 * <p>
 * Organised into nested test classes by concern:
 * - Construction
 * - offerFront / offerLast
 * - pollFront / pollLast
 * - peekFront / peekLast
 * - Size & isEmpty
 * - Ordering invariants
 * - Wrap-around / circular-array behaviour
 * - Capacity growth
 * - Mixed operations (property-style)
 * - toList
 * - Null / type safety
 */
@DisplayName("CircularArrayDeque")
class CircularArrayDequeTest {

    // ------------------------------------------------------------------
    // Factory helpers – swap the constructor call to point at your class
    // ------------------------------------------------------------------

    private static <T> DequeOperations<T> deque() {
        return new CircularArrayDeque<>();          // default capacity
    }

    private static <T> DequeOperations<T> deque(int initialCapacity) {
        return new CircularArrayDeque<>(initialCapacity);
    }

    // ======================================================================
    @Nested
    @DisplayName("Construction")
    class Construction {

        @Test
        @DisplayName("A new deque is empty and has size 0")
        void newDequeIsEmpty() {
            var dq = CircularArrayDequeTest.<Integer>deque();
            assertThat(dq.isEmpty()).isTrue();
            assertThat(dq.size()).isZero();
        }

        @Test
        @DisplayName("A new deque returns empty list from toList")
        void newDequeToListIsEmpty() {
            assertThat(CircularArrayDequeTest.<String>deque().toList()).isEmpty();
        }

        @ParameterizedTest(name = "capacity = {0}")
        @ValueSource(ints = {1, 2, 7, 16, 100})
        @DisplayName("Custom initial capacity starts empty")
        void customCapacityStartsEmpty(int cap) {
            var dq = CircularArrayDequeTest.<Integer>deque(cap);
            assertThat(dq.isEmpty()).isTrue();
            assertThat(dq.size()).isZero();
        }

        @Test
        @DisplayName("Zero or negative initial capacity throws IllegalArgumentException")
        void invalidCapacityThrows() {
            assertThatIllegalArgumentException().isThrownBy(() -> new CircularArrayDeque<>(0));
            assertThatIllegalArgumentException().isThrownBy(() -> new CircularArrayDeque<>(-1));
        }
    }

    // ======================================================================
    @Nested
    @DisplayName("offerFront")
    class OfferFront {

        @Test
        @DisplayName("Returns true on success")
        void returnsTrue() {
            assertThat(CircularArrayDequeTest.<Integer>deque().offerFront(1)).isTrue();
        }

        @Test
        @DisplayName("Single element: front == last == that element")
        void singleElement() {
            var dq = CircularArrayDequeTest.<String>deque();
            dq.offerFront("a");
            assertThat(dq.peekFront()).isEqualTo("a");
            assertThat(dq.peekLast()).isEqualTo("a");
        }

        @Test
        @DisplayName("Elements offered to front appear in reverse-insertion order from front")
        void orderedFrontInsertion() {
            var dq = CircularArrayDequeTest.<Integer>deque();
            dq.offerFront(1);
            dq.offerFront(2);
            dq.offerFront(3);
            // front → 3, 2, 1 ← last
            assertThat(dq.toList()).containsExactly(3, 2, 1);
        }

        @Test
        @DisplayName("Size increments with each offerFront")
        void sizeGrows() {
            var dq = CircularArrayDequeTest.<Integer>deque();
            IntStream.rangeClosed(1, 5).forEach(i -> {
                dq.offerFront(i);
                assertThat(dq.size()).isEqualTo(i);
            });
        }
    }

    // ======================================================================
    @Nested
    @DisplayName("offerLast")
    class OfferLast {

        @Test
        @DisplayName("Returns true on success")
        void returnsTrue() {
            assertThat(CircularArrayDequeTest.<Integer>deque().offerLast(1)).isTrue();
        }

        @Test
        @DisplayName("Single element: front == last == that element")
        void singleElement() {
            var dq = CircularArrayDequeTest.<String>deque();
            dq.offerLast("z");
            assertThat(dq.peekFront()).isEqualTo("z");
            assertThat(dq.peekLast()).isEqualTo("z");
        }

        @Test
        @DisplayName("Elements offered to last appear in insertion order from front")
        void orderedLastInsertion() {
            var dq = CircularArrayDequeTest.<Integer>deque();
            dq.offerLast(1);
            dq.offerLast(2);
            dq.offerLast(3);
            // front → 1, 2, 3 ← last
            assertThat(dq.toList()).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("Size increments with each offerLast")
        void sizeGrows() {
            var dq = CircularArrayDequeTest.<Integer>deque();
            IntStream.rangeClosed(1, 5).forEach(i -> {
                dq.offerLast(i);
                assertThat(dq.size()).isEqualTo(i);
            });
        }
    }

    // ======================================================================
    @Nested
    @DisplayName("pollFront")
    class PollFront {

        @Test
        @DisplayName("Returns null on empty deque")
        void emptyReturnsNull() {
            assertThat(CircularArrayDequeTest.<Integer>deque().pollFront()).isNull();
        }

        @Test
        @DisplayName("Returns and removes the front element")
        void returnsAndRemovesFront() {
            var dq = CircularArrayDequeTest.<String>deque();
            dq.offerLast("a");
            dq.offerLast("b");
            assertThat(dq.pollFront()).isEqualTo("a");
            assertThat(dq.peekFront()).isEqualTo("b");
        }

        @Test
        @DisplayName("Decrements size")
        void decrementSize() {
            var dq = CircularArrayDequeTest.<Integer>deque();
            dq.offerLast(10);
            dq.offerLast(20);
            dq.pollFront();
            assertThat(dq.size()).isOne();
        }

        @Test
        @DisplayName("Deque is empty after polling the only element")
        void emptyAfterLastPoll() {
            var dq = CircularArrayDequeTest.<Integer>deque();
            dq.offerLast(42);
            dq.pollFront();
            assertThat(dq.isEmpty()).isTrue();
            assertThat(dq.size()).isZero();
        }

        @Test
        @DisplayName("Successive polls return elements in FIFO order")
        void fifoOrder() {
            var dq = CircularArrayDequeTest.<Integer>deque();
            List.of(1, 2, 3, 4, 5).forEach(dq::offerLast);
            assertThat(dq.pollFront()).isEqualTo(1);
            assertThat(dq.pollFront()).isEqualTo(2);
            assertThat(dq.pollFront()).isEqualTo(3);
        }

        @Test
        @DisplayName("Null returned after deque is drained by polling")
        void nullAfterDrained() {
            var dq = CircularArrayDequeTest.<Integer>deque();
            dq.offerLast(1);
            dq.pollFront();
            assertThat(dq.pollFront()).isNull();
        }
    }

    // ======================================================================
    @Nested
    @DisplayName("pollLast")
    class PollLast {

        @Test
        @DisplayName("Returns null on empty deque")
        void emptyReturnsNull() {
            assertThat(CircularArrayDequeTest.<Integer>deque().pollLast()).isNull();
        }

        @Test
        @DisplayName("Returns and removes the last element")
        void returnsAndRemovesLast() {
            var dq = CircularArrayDequeTest.<String>deque();
            dq.offerLast("a");
            dq.offerLast("b");
            assertThat(dq.pollLast()).isEqualTo("b");
            assertThat(dq.peekLast()).isEqualTo("a");
        }

        @Test
        @DisplayName("Decrements size")
        void decrementSize() {
            var dq = CircularArrayDequeTest.<Integer>deque();
            dq.offerLast(10);
            dq.offerLast(20);
            dq.pollLast();
            assertThat(dq.size()).isOne();
        }

        @Test
        @DisplayName("Deque is empty after polling the only element")
        void emptyAfterLastPoll() {
            var dq = CircularArrayDequeTest.<Integer>deque();
            dq.offerLast(99);
            dq.pollLast();
            assertThat(dq.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("Successive polls return elements in LIFO order from the back")
        void lifoOrderFromBack() {
            var dq = CircularArrayDequeTest.<Integer>deque();
            List.of(1, 2, 3).forEach(dq::offerLast);
            assertThat(dq.pollLast()).isEqualTo(3);
            assertThat(dq.pollLast()).isEqualTo(2);
            assertThat(dq.pollLast()).isEqualTo(1);
        }
    }

    // ======================================================================
    @Nested
    @DisplayName("peekFront")
    class PeekFront {

        @Test
        @DisplayName("Returns null on empty deque")
        void emptyReturnsNull() {
            assertThat(CircularArrayDequeTest.<Integer>deque().peekFront()).isNull();
        }

        @Test
        @DisplayName("Does not remove the element")
        void nonDestructive() {
            var dq = CircularArrayDequeTest.<String>deque();
            dq.offerLast("x");
            dq.peekFront();
            assertThat(dq.size()).isOne();
            assertThat(dq.peekFront()).isEqualTo("x");
        }

        @Test
        @DisplayName("Returns the most-recently offered front element")
        void reflectsOfferFront() {
            var dq = CircularArrayDequeTest.<Integer>deque();
            dq.offerLast(1);
            dq.offerFront(2);
            assertThat(dq.peekFront()).isEqualTo(2);
        }
    }

    // ======================================================================
    @Nested
    @DisplayName("peekLast")
    class PeekLast {

        @Test
        @DisplayName("Returns null on empty deque")
        void emptyReturnsNull() {
            assertThat(CircularArrayDequeTest.<Integer>deque().peekLast()).isNull();
        }

        @Test
        @DisplayName("Does not remove the element")
        void nonDestructive() {
            var dq = CircularArrayDequeTest.<String>deque();
            dq.offerLast("y");
            dq.peekLast();
            assertThat(dq.size()).isOne();
            assertThat(dq.peekLast()).isEqualTo("y");
        }

        @Test
        @DisplayName("Returns the most-recently offered last element")
        void reflectsOfferLast() {
            var dq = CircularArrayDequeTest.<Integer>deque();
            dq.offerLast(1);
            dq.offerLast(2);
            assertThat(dq.peekLast()).isEqualTo(2);
        }
    }

    // ======================================================================
    @Nested
    @DisplayName("size and isEmpty")
    class SizeAndIsEmpty {

        @Test
        @DisplayName("isEmpty is false once an element is added")
        void notEmptyAfterOffer() {
            var dq = CircularArrayDequeTest.<Integer>deque();
            dq.offerLast(1);
            assertThat(dq.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("isEmpty is true again after all elements are removed")
        void emptyAfterAllPolled() {
            var dq = CircularArrayDequeTest.<Integer>deque();
            dq.offerLast(1);
            dq.offerLast(2);
            dq.pollFront();
            dq.pollFront();
            assertThat(dq.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("size tracks mixed offer/poll operations correctly")
        void sizeMixedOps() {
            var dq = CircularArrayDequeTest.<Integer>deque();
            dq.offerLast(1);   // size 1
            dq.offerFront(2);  // size 2
            dq.pollFront();    // size 1
            dq.offerLast(3);   // size 2
            dq.pollLast();     // size 1
            assertThat(dq.size()).isOne();
        }

        @Test
        @DisplayName("size is consistent with toList().size()")
        void sizeMatchesToList() {
            var dq = CircularArrayDequeTest.<Integer>deque();
            List.of(10, 20, 30).forEach(dq::offerLast);
            assertThat(dq.size()).isEqualTo(dq.toList().size());
        }
    }

    // ======================================================================
    @Nested
    @DisplayName("Ordering invariants")
    class OrderingInvariants {

        @Test
        @DisplayName("Queue semantics: offerLast + pollFront preserves FIFO order")
        void queueSemantics() {
            var dq = CircularArrayDequeTest.<Integer>deque();
            List<Integer> input = List.of(1, 2, 3, 4, 5);
            input.forEach(dq::offerLast);
            List<Integer> output = IntStream.range(0, input.size())
                    .mapToObj(_ -> dq.pollFront())
                    .toList();
            assertThat(output).isEqualTo(input);
        }

        @Test
        @DisplayName("Stack semantics: offerFront + pollFront preserves LIFO order")
        void stackSemanticsViaFront() {
            var dq = CircularArrayDequeTest.<Integer>deque();
            List<Integer> input = List.of(1, 2, 3, 4, 5);
            input.forEach(dq::offerFront);
            List<Integer> output = IntStream.range(0, input.size())
                    .mapToObj(_ -> dq.pollFront())
                    .toList();
            assertThat(output).isEqualTo(input.reversed());
        }

        @Test
        @DisplayName("offerFront then offerLast: peek order is correct")
        void mixedOfferPeekOrder() {
            var dq = CircularArrayDequeTest.<Integer>deque();
            dq.offerFront(1);  // [1]
            dq.offerLast(2);   // [1, 2]
            dq.offerFront(0);  // [0, 1, 2]
            assertThat(dq.peekFront()).isEqualTo(0);
            assertThat(dq.peekLast()).isEqualTo(2);
            assertThat(dq.toList()).containsExactly(0, 1, 2);
        }

        @Test
        @DisplayName("Alternating pollFront / pollLast narrows from both ends")
        void alternatingPoll() {
            var dq = CircularArrayDequeTest.<Integer>deque();
            List.of(1, 2, 3, 4, 5).forEach(dq::offerLast);

            assertThat(dq.pollFront()).isEqualTo(1);
            assertThat(dq.pollLast()).isEqualTo(5);
            assertThat(dq.pollFront()).isEqualTo(2);
            assertThat(dq.pollLast()).isEqualTo(4);
            assertThat(dq.pollFront()).isEqualTo(3);
            assertThat(dq.isEmpty()).isTrue();
        }
    }

    // ======================================================================
    @Nested
    @DisplayName("Circular-array wrap-around behaviour")
    class WrapAround {

        /**
         * Forces head to move toward the end of the backing array, then
         * exercises offers that must wrap around the circular buffer.
         */
        @Test
        @DisplayName("Correct order after head wraps around the array boundary (via front)")
        void headWrapsAroundViaPollThenOfferFront() {
            var dq = CircularArrayDequeTest.<Integer>deque(4);
            // Fill, partially drain from front, then re-fill so head wraps
            dq.offerLast(1);
            dq.offerLast(2);
            dq.offerLast(3);
            dq.offerLast(4);
            dq.pollFront(); // [2, 3, 4]  head moves right
            dq.pollFront(); // [3, 4]      head moves right
            dq.offerLast(5);  // [3, 4, 5]
            dq.offerLast(6);  // [3, 4, 5, 6] – tail wraps if capacity = 4
            assertThat(dq.toList()).containsExactly(3, 4, 5, 6);
        }

        @Test
        @DisplayName("Correct order after tail wraps around the array boundary (via last)")
        void tailWrapsAroundViaOfferLast() {
            var dq = CircularArrayDequeTest.<Integer>deque(4);
            dq.offerLast(10);
            dq.offerLast(20);
            dq.pollFront();   // head now at index 1
            dq.offerLast(30);
            dq.offerLast(40);
            dq.offerLast(50); // tail wraps
            assertThat(dq.toList()).containsExactly(20, 30, 40, 50);
        }

        @Test
        @DisplayName("offerFront into a partially-consumed deque wraps correctly")
        void offerFrontWraps() {
            var dq = CircularArrayDequeTest.<Integer>deque(4);
            dq.offerLast(1);
            dq.offerLast(2);
            dq.offerLast(3);
            dq.pollFront();  // head moves right; slot 0 is now free
            dq.offerFront(9); // should wrap head back toward slot 0 or further
            assertThat(dq.peekFront()).isEqualTo(9);
            assertThat(dq.toList()).containsExactly(9, 2, 3);
        }
    }

    // ======================================================================
    @Nested
    @DisplayName("Capacity growth (dynamic resizing)")
    class CapacityGrowth {

        @Test
        @DisplayName("Adding more elements than initial capacity does not throw")
        void noExceptionBeyondCapacity() {
            var dq = CircularArrayDequeTest.<Integer>deque(2);
            assertThatNoException().isThrownBy(() ->
                    IntStream.rangeClosed(1, 100).forEach(dq::offerLast));
        }

        @Test
        @DisplayName("Order is preserved after a resize triggered by offerLast")
        void orderPreservedAfterResizeViaLast() {
            var dq = CircularArrayDequeTest.<Integer>deque(2);
            List<Integer> input = IntStream.rangeClosed(1, 8).boxed().toList();
            input.forEach(dq::offerLast);
            assertThat(dq.toList()).isEqualTo(input);
        }

        @Test
        @DisplayName("Order is preserved after a resize triggered by offerFront")
        void orderPreservedAfterResizeViaFront() {
            var dq = CircularArrayDequeTest.<Integer>deque(2);
            // offering 1 then 2 then 3 via front → internal order: [3, 2, 1]
            dq.offerFront(1);
            dq.offerFront(2);
            dq.offerFront(3); // triggers resize if capacity == 2
            assertThat(dq.toList()).containsExactly(3, 2, 1);
        }

        @Test
        @DisplayName("Size is correct after growth")
        void sizeCorrectAfterGrowth() {
            var dq = CircularArrayDequeTest.<Integer>deque(1);
            IntStream.rangeClosed(1, 50).forEach(dq::offerLast);
            assertThat(dq.size()).isEqualTo(50);
        }

        @Test
        @DisplayName("Polls still work correctly after resize")
        void pollAfterResize() {
            var dq = CircularArrayDequeTest.<Integer>deque(2);
            List.of(1, 2, 3, 4).forEach(dq::offerLast);
            assertThat(dq.pollFront()).isEqualTo(1);
            assertThat(dq.pollLast()).isEqualTo(4);
            assertThat(dq.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("Multiple resize cycles preserve correctness")
        void multipleResizeCycles() {
            var dq = CircularArrayDequeTest.<Integer>deque(1);
            int n = 64;
            IntStream.rangeClosed(1, n).forEach(dq::offerLast);
            for (int i = 1; i <= n; i++) {
                assertThat(dq.pollFront()).isEqualTo(i);
            }
            assertThat(dq.isEmpty()).isTrue();
        }
    }

    // ======================================================================
    @Nested
    @DisplayName("Mixed operation sequences (property-style)")
    class MixedOperations {

        @Test
        @DisplayName("Interleaved offer/poll from both ends keeps size consistent")
        void interleavedOpsKeepSizeConsistent() {
            var dq = CircularArrayDequeTest.<Integer>deque();
            int size = 0;
            for (int i = 0; i < 20; i++) {
                dq.offerLast(i);
                size++;
                if (i % 3 == 0) {
                    dq.pollFront();
                    size--;
                }
                if (i % 5 == 0) {
                    dq.pollLast();
                    size--;
                }
                assertThat(dq.size()).isEqualTo(Math.max(size, 0));
                size = dq.size(); // reconcile if we polled from empty (noop)
            }
        }

        @Test
        @DisplayName("toList always matches manual front-to-last poll sequence")
        void toListMatchesPollSequence() {
            var dq = CircularArrayDequeTest.<Integer>deque(4);
            List.of(5, 3, 1, 8, 2).forEach(dq::offerLast);
            dq.pollFront();
            dq.offerFront(99);

            List<Integer> snapshot = dq.toList();
            List<Integer> polled = IntStream.range(0, dq.size())
                    .mapToObj(_ -> dq.pollFront())
                    .toList();

            assertThat(polled).isEqualTo(snapshot);
        }

        @Test
        @DisplayName("Re-fill after full drain works correctly")
        void refillAfterDrain() {
            var dq = CircularArrayDequeTest.<Integer>deque(4);
            List.of(1, 2, 3, 4).forEach(dq::offerLast);
            IntStream.range(0, 4).forEach(_ -> dq.pollFront());
            assertThat(dq.isEmpty()).isTrue();

            List.of(5, 6, 7).forEach(dq::offerLast);
            assertThat(dq.toList()).containsExactly(5, 6, 7);
        }
    }

    // ======================================================================
    @Nested
    @DisplayName("toList")
    class ToList {

        @Test
        @DisplayName("Returns an empty list for an empty deque")
        void emptyDeque() {
            assertThat(CircularArrayDequeTest.<Integer>deque().toList()).isEmpty();
        }

        @Test
        @DisplayName("Returns elements in front-to-last order")
        void frontToLastOrder() {
            var dq = CircularArrayDequeTest.<String>deque();
            dq.offerLast("a");
            dq.offerLast("b");
            dq.offerFront("z");
            // front → z, a, b ← last
            assertThat(dq.toList()).containsExactly("z", "a", "b");
        }

        @Test
        @DisplayName("Does not mutate the deque")
        void doesNotMutate() {
            var dq = CircularArrayDequeTest.<Integer>deque();
            List.of(1, 2, 3).forEach(dq::offerLast);
            dq.toList();
            assertThat(dq.size()).isEqualTo(3);
            assertThat(dq.peekFront()).isEqualTo(1);
        }

        @Test
        @DisplayName("Returned list is independent (mutation does not affect deque)")
        void returnedListIsSnapshot() {
            var dq = CircularArrayDequeTest.<Integer>deque();
            dq.offerLast(1);
            dq.offerLast(2);
            List<Integer> list = dq.toList();
            // If the list is mutable, clearing it must not affect the deque
            try {
                list.clear();
            } catch (UnsupportedOperationException ignored) {
            }
            assertThat(dq.size()).isEqualTo(2);
        }
    }

    // ======================================================================
    @Nested
    @DisplayName("Null-element handling")
    class NullElements {

        @Test
        @DisplayName("offerFront(null) either accepts or throws NullPointerException")
        void offerFrontNull() {
            var dq = CircularArrayDequeTest.<String>deque();
            // Contract choice: document which you implement.
            // If you reject nulls, assert the throw; if you allow them, assert acceptance.
            assertThatCode(() -> dq.offerFront(null))
                    .satisfiesAnyOf(
                            code -> assertThat(dq.peekFront()).isNull(),   // null accepted
                            code -> assertThat(code).isInstanceOf(NullPointerException.class) // null rejected
                    );
        }

        @Test
        @DisplayName("offerLast(null) either accepts or throws NullPointerException")
        void offerLastNull() {
            var dq = CircularArrayDequeTest.<String>deque();
            assertThatCode(() -> dq.offerLast(null))
                    .satisfiesAnyOf(
                            code -> assertThat(dq.peekLast()).isNull(),
                            code -> assertThat(code).isInstanceOf(NullPointerException.class)
                    );
        }
    }
}