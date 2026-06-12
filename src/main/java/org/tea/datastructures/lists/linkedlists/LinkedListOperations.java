package org.tea.datastructures.lists.linkedlists;


import org.tea.datastructures.lists.ListOperations;

public interface LinkedListOperations<T> extends ListOperations<T> {
    void addFirst(T value);
    boolean hasCycle();
    void reverse();
}