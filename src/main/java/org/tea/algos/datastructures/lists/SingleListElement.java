package org.tea.algos.datastructures.lists;

public class SingleListElement<T> {
    T value;
    SingleListElement<T> next;

    public SingleListElement(T value) {
        this.value = value;
    }
}
