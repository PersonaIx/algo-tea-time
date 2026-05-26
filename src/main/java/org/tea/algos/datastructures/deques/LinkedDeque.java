package org.tea.algos.datastructures.deques;

import java.util.List;

public class LinkedDeque<T> implements DequeOperations<T> {
    private int head;
    private int tail;
    private int size;

    public LinkedDeque() {
    }

    @Override
    public boolean offerFront(T element) {
        return false;
    }

    @Override
    public boolean offerLast(T element) {
        return false;
    }

    @Override
    public T pollFront() {
        return null;
    }

    @Override
    public T pollLast() {
        return null;
    }

    @Override
    public T peekFront() {
        return null;
    }

    @Override
    public T peekLast() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public List<T> toList() {
        return List.of();
    }
}
