package org.tea.datastructures.lists.linkedlists;

public class DoubleListElement<T> {
    T value;
    DoubleListElement<T> next;
    DoubleListElement<T> prev;

    public DoubleListElement(T value) {
        this.value = value;
    }
}
