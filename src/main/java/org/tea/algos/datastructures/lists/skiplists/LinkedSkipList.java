package org.tea.algos.datastructures.lists.skiplists;

import java.util.List;
import java.util.Random;

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

        if (predecessors[0] != null && predecessors[0].element != null  && predecessors[0].element.compareTo(element) == 0) {
            // nothing to do?
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
            while (current.next[i] != null && current.next[i].element.compareTo(element) <= 0) {
                current = current.next[i];
            }
            predecessors[i] = current;
        }
        return predecessors;
    }

    @Override
    public boolean remove(T element) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean contains(T element) {
        return false;
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
        return null;
    }

    @Override
    public T last() {
        return null;
    }

    @Override
    public List<T> range(T lo, T hi) {
        return List.of();
    }

    @Override
    public int levels() {
        return 0;
    }

    @Override
    public int levelSize(int level) {
        return 0;
    }

    @Override
    public List<T> toList() {
        return List.of();
    }
}
