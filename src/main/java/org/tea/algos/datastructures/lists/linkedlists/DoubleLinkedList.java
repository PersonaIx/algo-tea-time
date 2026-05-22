package org.tea.algos.datastructures.lists.linkedlists;

import java.util.ArrayList;
import java.util.List;

public class DoubleLinkedList<T> implements LinkedListOperations<T> {

    private DoubleListElement<T> head;
    private int size;

    @Override
    public void addFirst(T value) {

    }

    @Override
    public void addLast(T value) {

    }

    @Override
    public boolean remove(T value) {
        return false;
    }

    @Override
    public boolean contains(T value) {
        return false;
    }

    @Override
    public boolean hasCycle() {
        return false;
    }

    @Override
    public void reverse() {

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
        List<T> result = new ArrayList<>();
        DoubleListElement<T> current = head;
        while (current != null) {
            result.add(current.value);
            current = current.next;
        }
        return result;
    }
}
