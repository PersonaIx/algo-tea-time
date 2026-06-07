package org.tea.algos.datastructures.lists;

import java.util.List;

public interface ListOperations<T> {
    void add(T value);
    boolean remove(T value);
    boolean contains(T value);
    int size();
    boolean isEmpty();
    List<T> toList();
}