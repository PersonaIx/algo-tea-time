package org.tea.algos.datastructures.queues.deques;

import java.util.List;


public class CircularArrayDeque<T> implements DequeOperations<T> {
    private T[] deque;
    private int head;
    private int tail;
    private int size;

    public CircularArrayDeque() {
        //noinspection unchecked
        this.deque = (T[]) new Object[16];
    }

    public CircularArrayDeque(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }
        // take only the highest bit and shift it left one time to get a power of two
        int adjustedCapacity = Integer.highestOneBit(capacity) << 1;
        //noinspection unchecked
        this.deque = (T[]) new Object[adjustedCapacity];
    }

    @Override
    public boolean offerFront(T element) {
        if (element == null) {
            return false;
        }
        if (size + 1 >= deque.length) {
            adjustCapacity();
        }
        head = (head - 1) & (deque.length - 1);
        deque[head] = element;
        size++;
        return true;
    }

    @Override
    public boolean offerLast(T element) {
        if (element == null) {
            return false;
        }
        if (size + 1 >= deque.length) {
            adjustCapacity();
        }
        deque[tail] = element;
        tail = (tail + 1) & (deque.length - 1);
        size++;
        return true;
    }

    @Override
    public T pollFront() {
        if (isEmpty()) {
            return null;
        }
        T firstItem = deque[head];
        deque[head] = null;
        head = (head + 1) & (deque.length - 1);
        if (firstItem != null) {
            size--;
        }
        return firstItem;
    }

    @Override
    public T pollLast() {
        if (isEmpty()) {
            return null;
        }
        int lastIndex = (tail - 1) & (deque.length - 1);
        T lastItem = deque[lastIndex];
        deque[lastIndex] = null;
        tail = lastIndex;
        if (lastItem != null) {
            size--;
        }
        return lastItem;
    }

    @Override
    public T peekFront() {
        return deque[head];
    }

    @Override
    public T peekLast() {
        int lastIndex = (tail - 1)  & (deque.length - 1);
        return deque[lastIndex];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }


    private void adjustCapacity() {
        int newSize = deque.length * 2;
        @SuppressWarnings("unchecked")
        T[] result = (T[]) new Object[newSize];
        System.arraycopy(deque, 0, result, 0, deque.length);
        this.deque = result;
    }

    @Override
    public List<T> toList() {
        if (isEmpty()) {
            return List.of();
        }

        @SuppressWarnings("unchecked")
        T[] result = (T[]) new Object[size];
        int mask = deque.length - 1;

        for (int i = 0; i < size; i++) {
            result[i] = deque[(head + i) & mask];
        }
        return List.of(result);
    }
}
