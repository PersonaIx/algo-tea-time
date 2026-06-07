package org.tea.algos.datastructures.lists.linkedlists;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

class ConcurrentSingleLinkedListTest {

    private ConcurrentSingleLinkedList<Integer> list;

    private static final int THREAD_COUNT = 20;
    private static final int OPS_PER_THREAD = 100;
    private static final int TOTAL_OPS = THREAD_COUNT * OPS_PER_THREAD;

    @BeforeEach
    void setUp() {
        list = new ConcurrentSingleLinkedList<>();
    }

    @RepeatedTest(10)
    void concurrentaddProducesExactElements() throws InterruptedException {
        runConcurrently(() -> {
            for (int i = 0; i < OPS_PER_THREAD; i++) list.add(i);
        });

        // Size must be exactly right — no lost updates
        assertThat(list.size()).isEqualTo(TOTAL_OPS);

        // Every value 0–99 must appear exactly THREAD_COUNT times
        List<Integer> result = list.toList();
        for (int i = 0; i < OPS_PER_THREAD; i++) {
            final int value = i;
            assertThat(Collections.frequency(result, value))
                    .as("value %d should appear exactly %d times", value, THREAD_COUNT)
                    .isEqualTo(THREAD_COUNT);
        }
    }

    // --- addFirst ---

    @RepeatedTest(10)
    void concurrentAddFirstProducesExactElements() throws InterruptedException {
        runConcurrently(() -> {
            for (int i = 0; i < OPS_PER_THREAD; i++) list.addFirst(i);
        });

        assertThat(list.size()).isEqualTo(TOTAL_OPS);

        List<Integer> result = list.toList();
        for (int i = 0; i < OPS_PER_THREAD; i++) {
            final int value = i;
            assertThat(Collections.frequency(result, value))
                    .as("value %d should appear exactly %d times", value, THREAD_COUNT)
                    .isEqualTo(THREAD_COUNT);
        }
    }

    // --- remove ---

    @RepeatedTest(10)
    void concurrentRemovesNeverLoseOrDuplicateElements() throws InterruptedException {
        // Each thread owns a unique range — no two threads compete for the same value
        List<Integer> allValues = new ArrayList<>();
        for (int i = 0; i < TOTAL_OPS; i++) {
            list.add(i);
            allValues.add(i);
        }

        AtomicInteger successfulRemoves = new AtomicInteger(0);

        runConcurrently(threadIndex -> {
            int base = threadIndex * OPS_PER_THREAD;
            for (int i = 0; i < OPS_PER_THREAD; i++) {
                if (list.remove(base + i)) successfulRemoves.incrementAndGet();
            }
        });

        // Every element was unique and targeted once — all removes must succeed
        assertThat(successfulRemoves.get()).isEqualTo(TOTAL_OPS);
        assertThat(list.isEmpty()).isTrue();
    }

    // Overload for tasks that don't need their thread index
    private void runConcurrently(Runnable task) throws InterruptedException {
        runConcurrently(ignored -> task.run());
    }

    /**
     * Runs the given task on THREAD_COUNT virtual threads, all released simultaneously
     * via a start latch. Waits for all threads to finish before returning.
     * The task receives its thread index (0-based) for partition-based scenarios.
     */
    private void runConcurrently(java.util.function.IntConsumer task) throws InterruptedException {
        CountDownLatch ready = new CountDownLatch(THREAD_COUNT);
        CountDownLatch start = new CountDownLatch(1);

        // try-with-resources on the virtual thread executor blocks until all tasks finish
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int t = 0; t < THREAD_COUNT; t++) {
                final int threadIndex = t;
                executor.submit(() -> {
                    ready.countDown();
                    awaitQuietly(start);
                    task.accept(threadIndex);
                });
            }
            ready.await();   // wait until all threads are staged
            start.countDown(); // release all at once
        }
    }

    private void awaitQuietly(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}