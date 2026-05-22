package org.tea.algos.datastructures.lists.linkedlists;

import java.util.ArrayList;
import java.util.List;

public class SingleLinkedList<T> implements LinkedListOperations<T> {

    SingleListElement<T> head;
    private int size;

    @Override
    public void addFirst(T value) {
        if (value == null) {
            return;
        }

        SingleListElement<T> newHead = new SingleListElement<>(value);
        if (head != null) {
            newHead.next = head;
        }
        head = newHead;
        size++;
    }

    @Override
    public void addLast(T value) {
        if (value == null) {
            return;
        }

        SingleListElement<T> newTail = new SingleListElement<>(value);
        if (head == null) {
            head = newTail;
        } else if (head.next == null) {
            head.next = newTail;
        } else {
            SingleListElement<T> element = head.next;
            while (element.next != null) {
                element = element.next;
            }
            element.next = newTail;
        }
        size++;
    }

    @Override
    public boolean remove(T value) {
        if (head == null || value == null) {
            return false;
        }
        if (value.equals(head.value)) {
            head = head.next;
            size--;
            return true;
        }
        SingleListElement<T> current = head;
        while (current.next != null) {
            if (value.equals(current.next.value)) {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public boolean contains(T value) {
        if (head == null || value == null) {
            return false;
        }

        SingleListElement<T> current = head;
        while (current != null) {
            if (value.equals(current.value)) {
                return true;
            }
            current = current.next;
        }

        return false;
    }

    @Override
    public boolean hasCycle() {
        if (head == null) {
            return false;
        }

        SingleListElement<T> tortoise = head;
        SingleListElement<T> rabbit = head;

        while (rabbit.next != null && rabbit.next.next != null) {
            rabbit = rabbit.next.next;
            tortoise = tortoise.next;

            if (tortoise == rabbit) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void reverse() {
        if (head == null) {
            return;
        }
        SingleListElement<T> previous = null;
        SingleListElement<T> current = head;
        SingleListElement<T> next = head.next;

        while (next != null) {
            current.next = previous;
            previous = current;
            current = next;
            next = current.next;
        }
        current.next = previous;
        head = current;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return head == null;
    }

    @Override
    public List<T> toList() {
        List<T> result = new ArrayList<>();
        SingleListElement<T> current = head;
        while (current != null) {
            result.add(current.value);
            current = current.next;
        }
        return result;
    }
}
