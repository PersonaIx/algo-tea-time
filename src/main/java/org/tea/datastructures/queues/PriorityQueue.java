package org.tea.datastructures.queues;

public interface PriorityQueue<E> {

    void insert(E element);

    E peek();


    E extractMin();

    void changePriority(E element, E newElement);

    boolean contains(E element);

    int size();

    default boolean isEmpty() {
        return size() == 0;
    }
}
