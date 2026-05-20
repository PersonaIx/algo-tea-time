package org.tea.algos.datastructures.lists.linkedlists;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentSingleLinkedList<T> implements LinkedListOperations<T> {

    private SingleListElement<T> head;
    private volatile int size;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    @Override
    public void addFirst(T value) {
        try {
            writeLock.lock();
            if (value == null) {
                return;
            }

            SingleListElement<T> newHead = new SingleListElement<>(value);
            if (head == null) {
                head = newHead;
            } else {
                newHead.next = head;
                head = newHead;
            }
            size++;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void addLast(T value) {
        try {
            writeLock.lock();

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
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean remove(T value) {
        try {
            writeLock.lock();
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
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean contains(T value) {
        try {
            readLock.lock();
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
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean hasCycle() {
        try {
            readLock.lock();

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
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void reverse() {
        try {
            writeLock.lock();


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
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public List<T> toList() {
        try {
            readLock.lock();
            List<T> result = new ArrayList<>();
            SingleListElement<T> current = head;
            while (current != null) {
                result.add(current.value);
                current = current.next;
            }
            return result;
        } finally {
            readLock.unlock();
        }

    }
}
