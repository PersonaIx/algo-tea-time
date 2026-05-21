package org.tea.algos.deques;

import java.util.List;

public interface DequeOperations<T> {
    boolean offerFront(T element);
    boolean offerLast(T element);

    T pollFront();
    T pollLast();

    T peekFront();
    T peekLast();

    int size();
    boolean isEmpty();
    List<T> toList(); // For testing
}