package org.tea.algos.datastructures.lists.linkedlists;


import java.util.List;

public interface LinkedListOperations<T> {
    void addFirst(T value);
    void addLast(T value);
    boolean remove(T value);
    boolean contains(T value);
    boolean hasCycle();
    void reverse();
    int size();
    boolean isEmpty();
    List<T> toList(); //for testing
}