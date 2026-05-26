package org.tea.algos.datastructures.deques;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Concrete test class for CircularArrayDeque.
 *
 * Inherits the full shared behavioural contract from DequeOperationsContract,
 * and adds tests that are specific to the circular-array backing structure:
 *   - Custom-capacity construction
 *   - Wrap-around (head/tail crossing the array boundary)
 *   - Dynamic resizing
 */
@DisplayName("CircularArrayDeque")
class CircularArrayDequeTest extends DequeOperationsContract {

    @Override
    DequeOperations<Integer> deque() {
        return new CircularArrayDeque<>();
    }

    private static <T> DequeOperations<T> deque(int initialCapacity) {
        return new CircularArrayDeque<>(initialCapacity);
    }

    // ======================================================================
    @Nested
    @DisplayName("Construction (capacity-specific)")
    class CapacityConstruction {

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
    @DisplayName("Wrap-around (circular-array boundary)")
    class WrapAround {

        @Test
        @DisplayName("Correct order after head moves past the end of the backing array")
        void headWrapsAroundViaPollThenOfferFront() {
            var dq = CircularArrayDequeTest.<Integer>deque(4);
            dq.offerLast(1);
            dq.offerLast(2);
            dq.offerLast(3);
            dq.offerLast(4);
            dq.pollFront(); // [2, 3, 4]  — head advances right
            dq.pollFront(); // [3, 4]     — head advances right again
            dq.offerLast(5);  // [3, 4, 5]
            dq.offerLast(6);  // [3, 4, 5, 6] — tail wraps at capacity 4
            assertThat(dq.toList()).containsExactly(3, 4, 5, 6);
        }

        @Test
        @DisplayName("Correct order after tail wraps around the backing array boundary")
        void tailWrapsAroundViaOfferLast() {
            var dq = CircularArrayDequeTest.<Integer>deque(4);
            dq.offerLast(10);
            dq.offerLast(20);
            dq.pollFront();   // head moves to index 1; slot 0 is dead
            dq.offerLast(30);
            dq.offerLast(40);
            dq.offerLast(50); // tail wraps around to slot 0
            assertThat(dq.toList()).containsExactly(20, 30, 40, 50);
        }

        @Test
        @DisplayName("offerFront into a partially-consumed deque wraps head correctly")
        void offerFrontWrapsHead() {
            var dq = CircularArrayDequeTest.<Integer>deque(4);
            dq.offerLast(1);
            dq.offerLast(2);
            dq.offerLast(3);
            dq.pollFront();   // head moves right; slot 0 is now free
            dq.offerFront(9); // head wraps back to slot 0 (or the last slot)
            assertThat(dq.peekFront()).isEqualTo(9);
            assertThat(dq.toList()).containsExactly(9, 2, 3);
        }
    }

    // ======================================================================
    @Nested
    @DisplayName("Dynamic resizing")
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
            dq.offerFront(1);
            dq.offerFront(2);
            dq.offerFront(3); // triggers resize
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
}
