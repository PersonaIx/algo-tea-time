package org.tea.datastructures.lists.linkedlists;

import java.util.ArrayList;
import java.util.List;

public class DoubleLinkedList<T> implements LinkedListOperations<T> {

    private DoubleListElement<T> head;
    private DoubleListElement<T> tail;
    private int size;

    @Override
    public void addFirst(T value) {
        if (value == null) {
            return;
        }
        DoubleListElement<T> newHead = new DoubleListElement<>(value);
        if (head != null) {
            head.prev = newHead;
            newHead.next = head;
        }
        head = newHead;
        if (tail == null) {
            tail = newHead;
        }
        size++;
    }

    @Override
    public void add(T value) {
        if (value == null) {
            return;
        }

        DoubleListElement<T> newTail = new DoubleListElement<>(value);
        if (tail != null) {
            tail.next = newTail;
            newTail.prev = tail;
        }
        tail = newTail;
        if (head == null) {
            head = newTail;
        }
        size++;
    }

    @Override
    public boolean remove(T value) {
        if (head == null || value == null) {
            return false;
        }
        if (value.equals(head.value)) {
            // only node left
            if (head.prev == null && head.next == null) {
                head = null;
                size--;
                return true;
            }

            head = head.next;
            head.prev = null;
            size--;
            return true;
        }

        DoubleListElement<T> current = head.next;
        while (current != null) {
            if (value.equals(current.value)) {
                size--;
                if (current == tail) {
                    tail = current.prev;
                    tail.next = null;
                    return true;
                }
                current.next.prev = current.prev;
                current.prev.next = current.next;
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

        if (value.equals(head.value)) {
            return true;
        }

        DoubleListElement<T> current = head.next;
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

        DoubleListElement<T> rabbit = head;
        DoubleListElement<T> tortoise = head;

        while (rabbit.next != null && rabbit.next.next != null) {
            rabbit = rabbit.next.next;
            tortoise = tortoise.next;

            if (rabbit == tortoise) {
                return true;
            }
        }

        DoubleListElement<T> rabbitBackward = tail;
        DoubleListElement<T> tortoiseBackward = tail;

        while (rabbitBackward.prev != null && rabbitBackward.prev.prev != null) {
            rabbitBackward = rabbitBackward.prev.prev;
            tortoiseBackward = tortoiseBackward.prev;
            if (rabbitBackward == tortoiseBackward) {
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
        DoubleListElement<T> current = head;
        tail = head;

        while (current.next != null) {
            DoubleListElement<T> previous = current.prev;
            DoubleListElement<T> next = current.next;
            current.prev = next;
            current.next = previous;
            current = next;
        }

        current.next = current.prev;
        current.prev = null;
        head = current;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
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
