package org.tea.algos.datastructures.lists.skiplists;

import java.util.*;

public class LinkedSkipList<T extends Comparable<T>> implements SkipList<T> {
    private static final int MAX_LEVEL   = 32;
    private static final double PROBABILITY = 0.5;

    private final Random random;
    private final SkipListNode<T>  head;
    private SkipListNode<T>  tail;
    private int currentLevels;
    private int size;

    public LinkedSkipList() {
        this(new Random());
    }

    public LinkedSkipList(Random random) {
        this.random = random;
        this.head = new SkipListNode<>(null, MAX_LEVEL);
    }

    @Override
    public boolean add(T element) {
        if (element == null) {
            throw new NullPointerException("element");
        }
        int heightForNewNode = randomHeightForNewNode();
        SkipListNode<T>[] predecessors = findPredecessor(element);

        if (predecessors[0] != null && predecessors[0].next[0] != null && predecessors[0].next[0].element != null  && predecessors[0].next[0].element.compareTo(element) == 0) {
            // duplicate
            return false;
        }

        SkipListNode<T> newNode = new SkipListNode<>(element, heightForNewNode);
        size++;

        if (heightForNewNode > currentLevels) {
            for (int i = currentLevels; i < heightForNewNode; i++) {
                predecessors[i] = head;
            }
            currentLevels = heightForNewNode;
        }

        for (int i = 0; i < heightForNewNode; i++) {
            if (predecessors[i] != null) {
                newNode.next[i] = predecessors[i].next[i];
                predecessors[i].next[i] = newNode;
            }
        }

        if (newNode.next[0] == null) {
            tail = newNode;
        }

        return true;
    }

    private int randomHeightForNewNode() {
        int height = 1;
        for (int i=0; i < MAX_LEVEL - 1; i++) {
            double randomProbability = random.nextDouble();
            if (randomProbability < PROBABILITY) {
                height++;
            } else {
                // stop once the probably does not match, break is not so nice though
                break;
            }
        }
        return height;
    }

    private SkipListNode<T>[] findPredecessor(T element) {
        SkipListNode<T> current = this.head;
        @SuppressWarnings("unchecked")
        SkipListNode<T>[] predecessors = (SkipListNode<T>[]) new SkipListNode[MAX_LEVEL];
        for (int i = currentLevels - 1; i >= 0; i--) {
            while (current.next[i] != null && current.next[i].element.compareTo(element) < 0) {
                current = current.next[i];
            }
            predecessors[i] = current;
        }
        return predecessors;
    }

    @Override
    public boolean remove(T element) {
        if (element == null) {
            throw new NullPointerException("element");
        }

        SkipListNode<T> currentNode = findCurrentElement(element);
        if (currentNode == null) {
            return false;
        }
        SkipListNode<T>[] predecessors = findPredecessor(element);

        if (currentNode.next[0] == null) {
            tail = (predecessors[0] == head) ? null : predecessors[0];
        }

        for (int i = 0; i < currentNode.next.length; i++) {
            // if the next node is null and the node before head, no other node exists on this level
            if (currentNode.next[i] == null && predecessors[i].element == null) {
                currentLevels--;
            }
            predecessors[i].next[i] = currentNode.next[i];
        }



        size--;
        return true;
    }

    @Override
    public void clear() {
        for (int i = 0; i <currentLevels; i++) {
            head.next[i] = null;
        }
        size = 0;
        currentLevels = 0;
    }

    @Override
    public boolean contains(T element) {
        if (element == null) {
            throw new NullPointerException("element");
        }

        SkipListNode<T> currentElement = findCurrentElement(element);
        return currentElement != null;
    }

    private SkipListNode<T> findCurrentElement(T element) {
        SkipListNode<T> current = head;
        for (int i = currentLevels - 1; i >= 0; i--) {
            while (current.next[i] != null && current.next[i].element.compareTo(element) <= 0) {
                current = current.next[i];
            }
            if (current.element != null && current.element.compareTo(element) == 0) {
                return current;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public T first() {
        if (head.next[0] == null) {
            throw new NoSuchElementException();
        }
        return head.next[0].element;
    }

    @Override
    public T last() {
        if (tail == null) {
            throw new NoSuchElementException();
        }
        return tail.element;
    }

    @Override
    public List<T> range(T lo, T hi) {
        if (lo.compareTo(hi) > 0) {
            throw new IllegalArgumentException();
        }

        List<T> result = new ArrayList<>();

        SkipListNode<T> current = head;
        SkipListNode<T> lowerBoundStartNode;
        for (int i = currentLevels - 1; i >= 0; i--) {
            while (current.next[i] != null && current.next[i].element.compareTo(lo) < 0) {
                current = current.next[i];
            }
        }
        if (current.next[0] == null || current.next[0].element.compareTo(lo) < 0 || current.next[0].element.compareTo(hi) > 0) {
            return Collections.emptyList();
        }

        lowerBoundStartNode = current.next[0];
        result.add(lowerBoundStartNode.element);


        while (lowerBoundStartNode.next[0] != null && lowerBoundStartNode.next[0].element.compareTo(hi) <= 0) {
            result.add(lowerBoundStartNode.next[0].element);
            lowerBoundStartNode = lowerBoundStartNode.next[0];
        }


        return result;
    }

    @Override
    public int levels() {
        return currentLevels;
    }

    @Override
    public int levelSize(int level) {
        if (level >= currentLevels) {
            throw new IndexOutOfBoundsException();
        }
        int count = 0;
        SkipListNode<T> currentNode = head;
        while (currentNode.next[level] != null) {
            count++;
            currentNode = currentNode.next[level];
        }
        return count;
    }

    @Override
    public List<T> toList() {
        List<T> result = new ArrayList<>();
        SkipListNode<T> currentNode = head;
        while (currentNode.next[0] != null) {
            result.add(currentNode.next[0].element);
            currentNode = currentNode.next[0];
        }
        return result;
    }
}
