package org.tea.datastructures.queues.deques;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.tea.datastructures.queues.deques.DequeOperations;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Shared behavioural contract for every DequeOperations<T> implementation.
 *
 * Subclasses supply a fresh deque instance via {@link #deque()} and, optionally,
 * override {@link #supportsToListSnapshotMutation()} to document their toList contract.
 *
 * JUnit 5 automatically discovers and runs all @Test methods defined here in every
 * concrete subclass — no explicit delegation needed.
 *
 * Deliberately excluded from this base class (implementation-specific concerns):
 *   - CircularArrayDeque: WrapAround, CapacityGrowth, custom-capacity Construction
 *   - LinkedDeque:        NodeLinkage, SingleNodeEdgeCases
 */
@TestInstance(Lifecycle.PER_METHOD)
abstract class DequeOperationsContract {

    /** Returns a fresh, empty deque for each test. */
    abstract DequeOperations<Integer> deque();

    /**
     * Override and return false if your toList() returns an unmodifiable list,
     * so the snapshot-mutation test skips the clear() branch gracefully.
     */
    boolean supportsToListSnapshotMutation() { return true; }

    // ======================================================================
    @Nested
    @DisplayName("Construction")
    class Construction {

        @Test
        @DisplayName("A new deque is empty and has size 0")
        void newDequeIsEmpty() {
            var dq = deque();
            assertThat(dq.isEmpty()).isTrue();
            assertThat(dq.size()).isZero();
        }

        @Test
        @DisplayName("A new deque returns empty list from toList")
        void newDequeToListIsEmpty() {
            assertThat(deque().toList()).isEmpty();
        }
    }

    // ======================================================================
    @Nested
    @DisplayName("offerFront")
    class OfferFront {

        @Test
        @DisplayName("Returns true on success")
        void returnsTrue() {
            assertThat(deque().offerFront(1)).isTrue();
        }

        @Test
        @DisplayName("Single element: front == last == that element")
        void singleElement() {
            var dq = deque();
            dq.offerFront(42);
            assertThat(dq.peekFront()).isEqualTo(42);
            assertThat(dq.peekLast()).isEqualTo(42);
        }

        @Test
        @DisplayName("Elements offered to front appear in reverse-insertion order from front")
        void orderedFrontInsertion() {
            var dq = deque();
            dq.offerFront(1);
            dq.offerFront(2);
            dq.offerFront(3);
            assertThat(dq.toList()).containsExactly(3, 2, 1);
        }

        @Test
        @DisplayName("Size increments with each offerFront")
        void sizeGrows() {
            var dq = deque();
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
            assertThat(deque().offerLast(1)).isTrue();
        }

        @Test
        @DisplayName("Single element: front == last == that element")
        void singleElement() {
            var dq = deque();
            dq.offerLast(99);
            assertThat(dq.peekFront()).isEqualTo(99);
            assertThat(dq.peekLast()).isEqualTo(99);
        }

        @Test
        @DisplayName("Elements offered to last appear in insertion order from front")
        void orderedLastInsertion() {
            var dq = deque();
            dq.offerLast(1);
            dq.offerLast(2);
            dq.offerLast(3);
            assertThat(dq.toList()).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("Size increments with each offerLast")
        void sizeGrows() {
            var dq = deque();
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
            assertThat(deque().pollFront()).isNull();
        }

        @Test
        @DisplayName("Returns and removes the front element")
        void returnsAndRemovesFront() {
            var dq = deque();
            dq.offerLast(1);
            dq.offerLast(2);
            assertThat(dq.pollFront()).isEqualTo(1);
            assertThat(dq.peekFront()).isEqualTo(2);
        }

        @Test
        @DisplayName("Decrements size")
        void decrementsSize() {
            var dq = deque();
            dq.offerLast(10);
            dq.offerLast(20);
            dq.pollFront();
            assertThat(dq.size()).isOne();
        }

        @Test
        @DisplayName("Deque is empty after polling the only element")
        void emptyAfterLastPoll() {
            var dq = deque();
            dq.offerLast(42);
            dq.pollFront();
            assertThat(dq.isEmpty()).isTrue();
            assertThat(dq.size()).isZero();
        }

        @Test
        @DisplayName("Successive polls return elements in FIFO order")
        void fifoOrder() {
            var dq = deque();
            List.of(1, 2, 3, 4, 5).forEach(dq::offerLast);
            assertThat(dq.pollFront()).isEqualTo(1);
            assertThat(dq.pollFront()).isEqualTo(2);
            assertThat(dq.pollFront()).isEqualTo(3);
        }

        @Test
        @DisplayName("Returns null after the deque is drained by polling")
        void nullAfterDrained() {
            var dq = deque();
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
            assertThat(deque().pollLast()).isNull();
        }

        @Test
        @DisplayName("Returns and removes the last element")
        void returnsAndRemovesLast() {
            var dq = deque();
            dq.offerLast(1);
            dq.offerLast(2);
            assertThat(dq.pollLast()).isEqualTo(2);
            assertThat(dq.peekLast()).isEqualTo(1);
        }

        @Test
        @DisplayName("Decrements size")
        void decrementsSize() {
            var dq = deque();
            dq.offerLast(10);
            dq.offerLast(20);
            dq.pollLast();
            assertThat(dq.size()).isOne();
        }

        @Test
        @DisplayName("Deque is empty after polling the only element")
        void emptyAfterLastPoll() {
            var dq = deque();
            dq.offerLast(99);
            dq.pollLast();
            assertThat(dq.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("Successive polls return elements in LIFO order from the back")
        void lifoOrderFromBack() {
            var dq = deque();
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
            assertThat(deque().peekFront()).isNull();
        }

        @Test
        @DisplayName("Does not remove the element")
        void nonDestructive() {
            var dq = deque();
            dq.offerLast(7);
            dq.peekFront();
            assertThat(dq.size()).isOne();
            assertThat(dq.peekFront()).isEqualTo(7);
        }

        @Test
        @DisplayName("Returns the most-recently offered front element")
        void reflectsOfferFront() {
            var dq = deque();
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
            assertThat(deque().peekLast()).isNull();
        }

        @Test
        @DisplayName("Does not remove the element")
        void nonDestructive() {
            var dq = deque();
            dq.offerLast(8);
            dq.peekLast();
            assertThat(dq.size()).isOne();
            assertThat(dq.peekLast()).isEqualTo(8);
        }

        @Test
        @DisplayName("Returns the most-recently offered last element")
        void reflectsOfferLast() {
            var dq = deque();
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
            var dq = deque();
            dq.offerLast(1);
            assertThat(dq.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("isEmpty is true again after all elements are removed")
        void emptyAfterAllPolled() {
            var dq = deque();
            dq.offerLast(1);
            dq.offerLast(2);
            dq.pollFront();
            dq.pollFront();
            assertThat(dq.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("size tracks mixed offer/poll operations correctly")
        void sizeMixedOps() {
            var dq = deque();
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
            var dq = deque();
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
            var dq = deque();
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
            var dq = deque();
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
            var dq = deque();
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
            var dq = deque();
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
    @DisplayName("Mixed operation sequences")
    class MixedOperations {

        @Test
        @DisplayName("Interleaved offer/poll from both ends keeps size consistent")
        void interleavedOpsKeepSizeConsistent() {
            var dq = deque();
            int size = 0;
            for (int i = 0; i < 20; i++) {
                dq.offerLast(i);
                size++;
                if (i % 3 == 0) { dq.pollFront(); size--; }
                if (i % 5 == 0) { dq.pollLast();  size--; }
                assertThat(dq.size()).isEqualTo(Math.max(size, 0));
                size = dq.size();
            }
        }

        @Test
        @DisplayName("toList always matches manual front-to-last poll sequence")
        void toListMatchesPollSequence() {
            var dq = deque();
            List.of(5, 3, 1, 8, 2).forEach(dq::offerLast);
            dq.pollFront();
            dq.offerFront(99);

            List<Integer> snapshot = dq.toList();
            List<Integer> polled   = IntStream.range(0, dq.size())
                    .mapToObj(_ -> dq.pollFront())
                    .toList();

            assertThat(polled).isEqualTo(snapshot);
        }

        @Test
        @DisplayName("Re-fill after full drain works correctly")
        void refillAfterDrain() {
            var dq = deque();
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
            assertThat(deque().toList()).isEmpty();
        }

        @Test
        @DisplayName("Returns elements in front-to-last order")
        void frontToLastOrder() {
            var dq = deque();
            dq.offerLast(1);
            dq.offerLast(2);
            dq.offerFront(0);
            assertThat(dq.toList()).containsExactly(0, 1, 2);
        }

        @Test
        @DisplayName("Does not mutate the deque")
        void doesNotMutate() {
            var dq = deque();
            List.of(1, 2, 3).forEach(dq::offerLast);
            dq.toList();
            assertThat(dq.size()).isEqualTo(3);
            assertThat(dq.peekFront()).isEqualTo(1);
        }

        @Test
        @DisplayName("Returned list is independent of the deque")
        void returnedListIsSnapshot() {
            var dq = deque();
            dq.offerLast(1);
            dq.offerLast(2);
            List<Integer> list = dq.toList();
            try { list.clear(); } catch (UnsupportedOperationException ignored) { }
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
            var dq = deque();
            assertThatCode(() -> dq.offerFront(null))
                    .satisfiesAnyOf(
                            code -> assertThat(dq.peekFront()).isNull(),
                            code -> assertThat(code).isInstanceOf(NullPointerException.class)
                    );
        }

        @Test
        @DisplayName("offerLast(null) either accepts or throws NullPointerException")
        void offerLastNull() {
            var dq = deque();
            assertThatCode(() -> dq.offerLast(null))
                    .satisfiesAnyOf(
                            code -> assertThat(dq.peekLast()).isNull(),
                            code -> assertThat(code).isInstanceOf(NullPointerException.class)
                    );
        }
    }
}
