package org.tea.algos.datastructures.lists.arraylists;

import org.tea.algos.datastructures.lists.ListOperations;

public interface ArrayListOperations<T> extends ListOperations<T> {
    void add(int index, T value);

    T get(int index);

    T remove(int index);
}