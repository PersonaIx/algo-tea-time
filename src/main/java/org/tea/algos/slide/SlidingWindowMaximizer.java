package org.tea.algos.slide;

import org.tea.datastructures.queues.deques.CircularArrayDeque;
import org.tea.datastructures.queues.deques.DequeOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * The SlidingWindowMaximizer knows that a window is meant for sliding, not slipping.
 */
public class SlidingWindowMaximizer<E extends Comparable<E>> {


    public List<E> findMaximum(E[] data, int k) {
        List<E> result = new ArrayList<>();
        if (data == null) {
            throw new NullPointerException();
        }
        if (k <= 0 || k > data.length) {
            throw new IllegalArgumentException();
        }
        DequeOperations<Integer> deque = new CircularArrayDeque<>();
        for (int i = 0; i < data.length; i++) {
            if (!deque.isEmpty() && deque.peekFront() <= (i - k)) {
                deque.pollFront();
            }
            while (!deque.isEmpty() && data[i].compareTo(data[deque.peekLast()]) >= 0) {
                deque.pollLast();
            }
            deque.offerLast(i);
            if ((i + 1) >= k) {
                result.add(data[deque.peekFront()]);
            }
        }
        return result;
    }
}
