package org.tea.algos.datastructures.lists.skiplists;

public final class SkipListNode<T> {
    T element;
    SkipListNode<T>[] next;

    @SuppressWarnings("unchecked")
    SkipListNode(T element, int height) {
        this.element = element;
        this.next = new SkipListNode[height];
    }
}