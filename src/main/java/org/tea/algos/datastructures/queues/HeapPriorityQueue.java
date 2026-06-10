package org.tea.algos.datastructures.queues;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class HeapPriorityQueue<E extends Comparable<E>> implements PriorityQueue<E> {

    private E[] heap;
    private int size;
    private int capacity;

    public HeapPriorityQueue() {
        this(16);
    }

    public HeapPriorityQueue(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException();
        }
        this.capacity = initialCapacity;
        //noinspection unchecked
        heap = (E[]) new Comparable[capacity];
    }

    @Override
    public void insert(E element) {
        if (element == null) {
            throw new IllegalArgumentException();
        }
        if (size >= capacity) {
            adjustCapacity();
        }
        if (size == 0) {
            heap[0] = element;
            size++;
            return;
        }
        int prevIndex = size;
        heap[prevIndex] = element;
        int currentIndex = (prevIndex - 1) / 2;
        E currentElement = heap[currentIndex];
        while (true) {
            if (element.compareTo(currentElement) < 0) {
                E temp = heap[prevIndex];
                heap[prevIndex] = heap[currentIndex];
                heap[currentIndex] = temp;
                prevIndex = currentIndex;
                currentIndex = (currentIndex - 1) / 2;
                currentElement = heap[currentIndex];
            } else {
                break;
            }
            // a bit ugly, having a mental block here
            if (currentIndex == 0) {
                break;
            }
        }

        size++;
    }

    private void adjustCapacity() {
        this.capacity = capacity * 2;
        this.heap = Arrays.copyOf(heap, capacity);
    }

    @Override
    public E peek() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return heap[0];
    }

    @Override
    public E extractMin() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        E min = heap[0];
        size--;
        // TODO: Need to think about it
        return min;
    }

    @Override
    public void changePriority(E element, E newElement) {

    }

    @Override
    public boolean contains(E element) {
        for (int i = 0; i < size; i++) {
            if (element.compareTo(heap[i]) == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }
}
