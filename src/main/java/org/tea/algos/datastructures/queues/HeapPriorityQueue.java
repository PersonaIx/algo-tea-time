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
                swap(prevIndex, currentIndex);
                prevIndex = currentIndex;
                // a bit ugly, having a mental block here
                if (currentIndex == 0) {
                    break;
                }
                currentIndex = (currentIndex - 1) / 2;
                currentElement = heap[currentIndex];
            } else {
                break;
            }

        }

        size++;
    }

    private void swap(int prevIndex, int currentIndex) {
        E temp = heap[prevIndex];
        heap[prevIndex] = heap[currentIndex];
        heap[currentIndex] = temp;
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
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;
        fixHeap();
        return min;
    }

    private void fixHeap() {
        int currentIndex = 0;
        while (true) {
            int smallestChildIndex = findSmallestChild(currentIndex);
            if (smallestChildIndex == -1) {
                break;
            }
            if (heap[currentIndex].compareTo(heap[smallestChildIndex]) < 0) {
                break;
            }
            swap(currentIndex, smallestChildIndex);
            currentIndex = smallestChildIndex;
        }
    }

    private int findSmallestChild(int currentIndex) {
        int leftIndex = currentIndex * 2 + 1;
        int rightIndex = currentIndex * 2 + 2;

        if (leftIndex >= size) {
            return -1;
        }
        if (rightIndex >= size) {
            return leftIndex;
        }
        if (heap[leftIndex].compareTo(heap[rightIndex]) <= 0) {
            return leftIndex;
        } else {
            return rightIndex;
        }
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
