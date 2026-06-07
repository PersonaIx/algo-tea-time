package org.tea.algos.datastructures.lists.arraylists;

import java.util.Arrays;
import java.util.List;

public class ArrayList<T> implements ArrayListOperations<T> {

    private static final int DEFAULT_CAPACITY = 16;

    private T[] array;
    private int size;

    public ArrayList() {
        //noinspection unchecked
        array = (T[]) new Object[DEFAULT_CAPACITY];
    }

    @Override
    public void add(int index, T value) {
        if (index > size) {
            throw new IndexOutOfBoundsException();
        }
        T previousValue = array[index];
        array[index] = value;
        size++;
        shiftElementsRight(index, previousValue);
    }

    private void shiftElementsRight(int index, T previousValue) {
        int shiftIndex = index + 1;
        T newValue;
        while (shiftIndex < size) {
            newValue = array[shiftIndex];
            array[shiftIndex] = previousValue;
            previousValue = newValue;
            shiftIndex++;
        }
    }

    @Override
    public T get(int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return array[index];
    }

    @Override
    public T remove(int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException();
        }

        T deleted = array[index];
        array[index] = null;
        shiftElementsLeft(index);
        size--;
        return deleted;
    }

    private void shiftElementsLeft(int index) {
        int shiftIndex = index + 1;
        while (shiftIndex < size) {
            array[shiftIndex - 1] = array[shiftIndex];
            shiftIndex++;
        }
    }

    @Override
    public void add(T value) {
        if (array.length - 1 == size) {
            adjustCapacity();
        }
        array[size] = value;
        size++;
    }

    @Override
    public boolean remove(T value) {
        for (int i = 0; i < size; i++) {
            if (value.equals(array[i])) {
                array[i] = null;
                shiftElementsLeft(i);
                size--;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean contains(T value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
        int index = 0;

        while (index < array.length) {
            if (value.equals(array[index])) {
                return true;
            }
            index++;
        }
        return false;
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
        java.util.ArrayList<T> result = new java.util.ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(array[i]);
        }
        return result;
    }

    private void adjustCapacity() {
        int newCapacity = array.length * 2;
        array = Arrays.copyOf(array, newCapacity);
    }
}
