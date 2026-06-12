package org.tea.datastructures.queues;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.tea.datastructures.queues.HeapPriorityQueue;
import org.tea.datastructures.queues.PriorityQueue;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for any PriorityQueue<Integer> implementation.
 *
 * Replace HeapPriorityQueue with your own implementation class.
 * All tests are written against the PriorityQueue<E> interface only.
 */
@DisplayName("PriorityQueue")
class PriorityQueueTest {

    private PriorityQueue<Integer> pq;

    @BeforeEach
    void setUp() {
        pq = new HeapPriorityQueue<>();  // ← swap your implementation here
    }

    // ─────────────────────────────────────────────
    // isEmpty / size
    // ─────────────────────────────────────────────

    @Nested
    @DisplayName("when newly created")
    class WhenEmpty {

        @Test
        @DisplayName("is empty")
        void isEmpty() {
            assertThat(pq.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("has size 0")
        void sizeIsZero() {
            assertThat(pq.size()).isZero();
        }

        @Test
        @DisplayName("peek() throws NoSuchElementException")
        void peekThrows() {
            assertThatThrownBy(() -> pq.peek())
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("extractMin() throws NoSuchElementException")
        void extractMinThrows() {
            assertThatThrownBy(() -> pq.extractMin())
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    // ─────────────────────────────────────────────
    // insert
    // ─────────────────────────────────────────────

    @Nested
    @DisplayName("insert()")
    class Insert {

        @Test
        @DisplayName("increases size by 1")
        void increasesSizeByOne() {
            pq.insert(10);
            assertThat(pq.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("is no longer empty after one insert")
        void notEmptyAfterInsert() {
            pq.insert(5);
            assertThat(pq.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("null element throws IllegalArgumentException")
        void nullThrows() {
            assertThatThrownBy(() -> pq.insert(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("multiple inserts all reflected in size")
        void multipleinsertsUpdateSize() {
            pq.insert(3);
            pq.insert(1);
            pq.insert(2);
            assertThat(pq.size()).isEqualTo(3);
        }

        @ParameterizedTest(name = "insert({0}) is contained")
        @ValueSource(ints = {1, 5, 100, -3, 0})
        @DisplayName("inserted element is contained in the queue")
        void insertedElementIsContained(int value) {
            pq.insert(value);
            assertThat(pq.contains(value)).isTrue();
        }
    }

    // ─────────────────────────────────────────────
    // peek
    // ─────────────────────────────────────────────

    @Nested
    @DisplayName("peek()")
    class Peek {

        @Test
        @DisplayName("returns the minimum element")
        void returnsMinimum() {
            pq.insert(5);
            pq.insert(1);
            pq.insert(3);
            assertThat(pq.peek()).isEqualTo(1);
        }

        @Test
        @DisplayName("does not remove the element")
        void doesNotMutateSize() {
            pq.insert(7);
            pq.insert(2);
            pq.peek();
            assertThat(pq.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("returns same result on repeated calls")
        void isIdempotent() {
            pq.insert(4);
            pq.insert(9);
            assertThat(pq.peek()).isEqualTo(pq.peek());
        }

        @Test
        @DisplayName("single element — peek equals that element")
        void singleElement() {
            pq.insert(42);
            assertThat(pq.peek()).isEqualTo(42);
        }
    }

    // ─────────────────────────────────────────────
    // extractMin
    // ─────────────────────────────────────────────

    @Nested
    @DisplayName("extractMin()")
    class ExtractMin {

        @Test
        @DisplayName("returns the minimum element")
        void returnsMinimum() {
            pq.insert(5);
            pq.insert(1);
            pq.insert(3);
            assertThat(pq.extractMin()).isEqualTo(1);
        }

        @Test
        @DisplayName("removes the element — size decreases by 1")
        void decreasesSize() {
            pq.insert(5);
            pq.insert(1);
            pq.extractMin();
            assertThat(pq.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("repeated extractions return elements in ascending order")
        void extractsSortedAscending() {
            pq.insert(4);
            pq.insert(2);
            pq.insert(7);
            pq.insert(1);
            assertThat(pq.extractMin()).isEqualTo(1);
            assertThat(pq.extractMin()).isEqualTo(2);
            assertThat(pq.extractMin()).isEqualTo(4);
            assertThat(pq.extractMin()).isEqualTo(7);
        }

        @Test
        @DisplayName("queue is empty after extracting last element")
        void emptyAfterLastExtract() {
            pq.insert(9);
            pq.extractMin();
            assertThat(pq.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("extracted minimum is no longer contained")
        void extractedElementNotContained() {
            pq.insert(3);
            pq.insert(1);
            pq.insert(5);
            pq.extractMin();
            assertThat(pq.contains(1)).isFalse();
        }

        @Test
        @DisplayName("peek() and extractMin() agree on the minimum")
        void peekAndExtractAgree() {
            pq.insert(6);
            pq.insert(2);
            pq.insert(9);

            Integer peeked   = pq.peek();        // look without removing
            Integer extracted = pq.extractMin(); // now remove
            assertThat(peeked).isEqualTo(extracted);
        }
    }

    // ─────────────────────────────────────────────
    // changePriority
    // ─────────────────────────────────────────────

    @Nested
    @DisplayName("changePriority()")
    class ChangePriority {

        @Test
        @DisplayName("old element is no longer contained")
        void oldElementRemoved() {
            pq.insert(5);
            pq.insert(10);
            pq.changePriority(10, 1);
            assertThat(pq.contains(10)).isFalse();
        }

        @Test
        @DisplayName("new element is contained")
        void newElementPresent() {
            pq.insert(5);
            pq.insert(10);
            pq.changePriority(10, 1);
            assertThat(pq.contains(1)).isTrue();
        }

        @Test
        @DisplayName("size is unchanged after changePriority")
        void sizeUnchanged() {
            pq.insert(5);
            pq.insert(10);
            pq.changePriority(10, 1);
            assertThat(pq.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("promoting priority — new element becomes the minimum")
        void promotedElementBecomesMin() {
            pq.insert(5);
            pq.insert(10);
            pq.insert(8);
            pq.changePriority(10, 1);     // 1 is now the new minimum
            assertThat(pq.peek()).isEqualTo(1);
        }

        @Test
        @DisplayName("demoting priority — minimum shifts to next element")
        void demotedMinNoLongerAtTop() {
            pq.insert(1);
            pq.insert(5);
            pq.insert(8);
            pq.changePriority(1, 99);     // 1 becomes 99, new min should be 5
            assertThat(pq.peek()).isEqualTo(5);
        }

        @Test
        @DisplayName("element not found throws NoSuchElementException")
        void missingElementThrows() {
            pq.insert(5);
            assertThatThrownBy(() -> pq.changePriority(99, 1))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    // ─────────────────────────────────────────────
    // contains
    // ─────────────────────────────────────────────

    @Nested
    @DisplayName("contains()")
    class Contains {

        @Test
        @DisplayName("returns false on empty queue")
        void emptyQueueReturnsFalse() {
            assertThat(pq.contains(1)).isFalse();
        }

        @Test
        @DisplayName("returns false for element never inserted")
        void absentElementReturnsFalse() {
            pq.insert(3);
            assertThat(pq.contains(99)).isFalse();
        }

        @Test
        @DisplayName("returns true for inserted element")
        void presentElementReturnsTrue() {
            pq.insert(7);
            assertThat(pq.contains(7)).isTrue();
        }
    }
}