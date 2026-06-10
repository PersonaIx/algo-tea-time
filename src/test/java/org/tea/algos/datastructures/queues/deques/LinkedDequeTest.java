package org.tea.algos.datastructures.queues.deques;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Concrete test class for LinkedDeque.
 *
 * Inherits the full shared behavioural contract from DequeOperationsContract.
 * The nested classes below target failure modes that are unique to a doubly-linked
 * implementation and would never surface in an array-based one:
 *
 *   - SingleNodeEdgeCases  — the one-node state where head and tail point at the
 *                            same node; pointer corruption here is the #1 linked-
 *                            deque bug.
 *   - NodeLinkage          — prev/next pointers must stay coherent after every
 *                            mutation so that traversal in both directions is correct.
 *   - NoDanglingReferences — after the last element is removed, internal head/tail
 *                            must be null so subsequent peeks return null rather
 *                            than throwing NullPointerException.
 *   - LargeSequences       — linked structures have no resize event; correctness
 *                            under large N replaces the array's CapacityGrowth tests.
 */
@DisplayName("LinkedDeque")
class LinkedDequeTest extends DequeOperationsContract {

    @Override
    DequeOperations<Integer> deque() {
        return new LinkedDeque<>();
    }

    // ======================================================================
    @Nested
    @DisplayName("Single-node edge cases")
    class SingleNodeEdgeCases {

        /**
         * When size == 1, head and tail point at the same node.
         * Offering to the front of a one-element deque must set both prev
         * on the existing node and next on the new node correctly.
         */
        @Test
        @DisplayName("offerFront onto a one-element deque: both ends are reachable")
        void offerFrontOntoOneElement() {
            var dq = deque();
            dq.offerLast(2);    // [2]
            dq.offerFront(1);   // [1, 2]
            assertThat(dq.peekFront()).isEqualTo(1);
            assertThat(dq.peekLast()).isEqualTo(2);
            assertThat(dq.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("offerLast onto a one-element deque: both ends are reachable")
        void offerLastOntoOneElement() {
            var dq = deque();
            dq.offerFront(1);   // [1]
            dq.offerLast(2);    // [1, 2]
            assertThat(dq.peekFront()).isEqualTo(1);
            assertThat(dq.peekLast()).isEqualTo(2);
            assertThat(dq.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("pollFront on a two-element deque leaves a valid one-element deque")
        void pollFrontLeavesValidSingleNode() {
            var dq = deque();
            dq.offerLast(1);
            dq.offerLast(2);
            dq.pollFront();                           // removes 1; only node is 2
            assertThat(dq.peekFront()).isEqualTo(2);  // head → node(2)
            assertThat(dq.peekLast()).isEqualTo(2);   // tail → same node
            assertThat(dq.size()).isOne();
        }

        @Test
        @DisplayName("pollLast on a two-element deque leaves a valid one-element deque")
        void pollLastLeavesValidSingleNode() {
            var dq = deque();
            dq.offerLast(1);
            dq.offerLast(2);
            dq.pollLast();                            // removes 2; only node is 1
            assertThat(dq.peekFront()).isEqualTo(1);  // head → node(1)
            assertThat(dq.peekLast()).isEqualTo(1);   // tail → same node
            assertThat(dq.size()).isOne();
        }

        @Test
        @DisplayName("Offering to both ends of a one-element deque produces correct three-element order")
        void offerBothEndsOfSingleNode() {
            var dq = deque();
            dq.offerLast(2);    // [2]
            dq.offerFront(1);   // [1, 2]
            dq.offerLast(3);    // [1, 2, 3]
            assertThat(dq.toList()).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("Polling from both ends of a three-element deque returns to valid single-node state")
        void drainToSingleNodeFromBothEnds() {
            var dq = deque();
            List.of(1, 2, 3).forEach(dq::offerLast);
            dq.pollFront(); // [2, 3]
            dq.pollLast();  // [2]  — single-node state
            assertThat(dq.peekFront()).isEqualTo(2);
            assertThat(dq.peekLast()).isEqualTo(2);
            // Verify the single remaining node is still operable
            dq.offerFront(0); // [0, 2]
            assertThat(dq.toList()).containsExactly(0, 2);
        }
    }

    // ======================================================================
    @Nested
    @DisplayName("Node linkage coherence")
    class NodeLinkage {

        /**
         * Traversal from the front (via repeated pollFront) and observation from the back
         * (via peekLast) must always agree — the prev/next chain must be consistent.
         */
        @Test
        @DisplayName("Forward traversal order matches toList after mixed offers")
        void forwardTraversalMatchesToList() {
            var dq = deque();
            dq.offerLast(2);
            dq.offerFront(1);  // [1, 2]
            dq.offerLast(3);   // [1, 2, 3]
            dq.offerFront(0);  // [0, 1, 2, 3]

            List<Integer> snapshot = dq.toList();
            List<Integer> polled   = IntStream.range(0, snapshot.size())
                    .mapToObj(_ -> dq.pollFront())
                    .toList();

            assertThat(polled).isEqualTo(snapshot);
        }

        @Test
        @DisplayName("Backward traversal order matches reversed toList after mixed offers")
        void backwardTraversalMatchesReversedToList() {
            var dq = deque();
            dq.offerLast(2);
            dq.offerFront(1);  // [1, 2]
            dq.offerLast(3);   // [1, 2, 3]
            dq.offerFront(0);  // [0, 1, 2, 3]

            List<Integer> snapshot = dq.toList();
            List<Integer> polled   = IntStream.range(0, snapshot.size())
                    .mapToObj(_ -> dq.pollLast())
                    .toList();

            assertThat(polled).isEqualTo(snapshot.reversed());
        }

        @Test
        @DisplayName("peekFront and peekLast are consistent with toList after interleaved ops")
        void peekConsistentWithToListAfterInterleavedOps() {
            var dq = deque();
            dq.offerLast(1);
            dq.offerLast(2);
            dq.offerFront(0);
            dq.pollFront();   // removes 0
            dq.offerLast(3);
            dq.pollLast();    // removes 3
            // expected: [1, 2]
            List<Integer> list = dq.toList();
            assertThat(dq.peekFront()).isEqualTo(list.getFirst());
            assertThat(dq.peekLast()).isEqualTo(list.getLast());
        }

        @Test
        @DisplayName("Alternating offerFront / pollLast maintains coherent linkage")
        void alternatingOfferFrontPollLast() {
            var dq = deque();
            // Build [5, 4, 3, 2, 1] via offerFront, then drain via pollLast
            IntStream.rangeClosed(1, 5).forEach(dq::offerFront);
            // front → 5, 4, 3, 2, 1 ← last
            assertThat(dq.pollLast()).isEqualTo(1);
            assertThat(dq.pollLast()).isEqualTo(2);
            assertThat(dq.pollLast()).isEqualTo(3);
            assertThat(dq.peekFront()).isEqualTo(5);
            assertThat(dq.peekLast()).isEqualTo(4);
        }

        @Test
        @DisplayName("Alternating offerLast / pollFront maintains coherent linkage")
        void alternatingOfferLastPollFront() {
            var dq = deque();
            IntStream.rangeClosed(1, 5).forEach(dq::offerLast);
            // Interleave: offer one, poll one, check ends each time
            dq.offerLast(6);
            assertThat(dq.pollFront()).isEqualTo(1);
            dq.offerLast(7);
            assertThat(dq.pollFront()).isEqualTo(2);
            // Remaining: [3, 4, 5, 6, 7]
            assertThat(dq.peekFront()).isEqualTo(3);
            assertThat(dq.peekLast()).isEqualTo(7);
            assertThat(dq.size()).isEqualTo(5);
        }
    }

    // ======================================================================
    @Nested
    @DisplayName("No dangling references after drain")
    class NoDanglingReferences {

        /**
         * After the last element is removed the internal head and tail pointers
         * must be null. If they still reference the removed node, subsequent peeks
         * will return stale values or throw NullPointerException.
         */
        @Test
        @DisplayName("peekFront returns null after draining via pollFront")
        void peekFrontNullAfterDrainViaPollFront() {
            var dq = deque();
            dq.offerLast(1);
            dq.pollFront();
            assertThat(dq.peekFront()).isNull();
        }

        @Test
        @DisplayName("peekLast returns null after draining via pollLast")
        void peekLastNullAfterDrainViaPollLast() {
            var dq = deque();
            dq.offerLast(1);
            dq.pollLast();
            assertThat(dq.peekLast()).isNull();
        }

        @Test
        @DisplayName("pollFront returns null (not stale data) after draining via pollFront")
        void pollFrontNullAfterDrain() {
            var dq = deque();
            dq.offerLast(42);
            dq.pollFront();
            assertThat(dq.pollFront()).isNull();
        }

        @Test
        @DisplayName("pollLast returns null (not stale data) after draining via pollLast")
        void pollLastNullAfterDrain() {
            var dq = deque();
            dq.offerLast(42);
            dq.pollLast();
            assertThat(dq.pollLast()).isNull();
        }

        @Test
        @DisplayName("Deque is fully operable after drain and re-fill")
        void operableAfterDrainAndRefill() {
            var dq = deque();
            List.of(1, 2, 3).forEach(dq::offerLast);
            IntStream.range(0, 3).forEach(_ -> dq.pollFront());

            // At this point head and tail must be null
            assertThat(dq.isEmpty()).isTrue();
            assertThat(dq.peekFront()).isNull();
            assertThat(dq.peekLast()).isNull();

            // Re-fill and verify normal operation resumes
            dq.offerFront(9);
            dq.offerLast(10);
            assertThat(dq.peekFront()).isEqualTo(9);
            assertThat(dq.peekLast()).isEqualTo(10);
            assertThat(dq.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("Cross-drain: draining via pollFront then pollLast leaves clean state")
        void crossDrainLeavesCleanState() {
            var dq = deque();
            dq.offerLast(1);
            dq.offerLast(2);
            dq.pollFront(); // removes 1
            dq.pollLast();  // removes 2 — deque is empty
            assertThat(dq.isEmpty()).isTrue();
            assertThat(dq.peekFront()).isNull();
            assertThat(dq.peekLast()).isNull();
        }
    }

    // ======================================================================
    @Nested
    @DisplayName("Large sequences (no resize events)")
    class LargeSequences {

        @Test
        @DisplayName("offerLast / pollFront round-trip for 10 000 elements")
        void largeFifoRoundTrip() {
            var dq = deque();
            int n = 10_000;
            IntStream.rangeClosed(1, n).forEach(dq::offerLast);
            for (int i = 1; i <= n; i++) {
                assertThat(dq.pollFront()).isEqualTo(i);
            }
            assertThat(dq.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("offerFront / pollFront round-trip preserves LIFO order for 10 000 elements")
        void largeLifoRoundTrip() {
            var dq = deque();
            int n = 10_000;
            IntStream.rangeClosed(1, n).forEach(dq::offerFront);
            // front → n … 1 ← last
            for (int i = n; i >= 1; i--) {
                assertThat(dq.pollFront()).isEqualTo(i);
            }
            assertThat(dq.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("Alternating offer/poll from both ends stays coherent over many iterations")
        void largeAlternatingOps() {
            var dq = deque();
            // Maintain a sliding window: always keep exactly 5 elements
            List.of(1, 2, 3, 4, 5).forEach(dq::offerLast);
            for (int i = 6; i <= 1_000; i++) {
                dq.offerLast(i);
                dq.pollFront();
                assertThat(dq.size()).isEqualTo(5);
                assertThat(dq.peekLast()).isEqualTo(i);
            }
        }
    }
}
