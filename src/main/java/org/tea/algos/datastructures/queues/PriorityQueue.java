package org.tea.algos.datastructures.queues;

public interface PriorityQueue<E> {

    /**
     * Inserts an element into the priority queue.
     *
     * <p>Typical complexity: O(log n) for heap-based implementations.
     *
     * @param element the element to insert; must not be null
     * @throws IllegalArgumentException if the element is null
     */
    void insert(E element);

    /**
     * Retrieves, but does NOT remove, the element with the highest priority.
     *
     * <p>Typical complexity: O(1) — the top element is always accessible
     * without any traversal.
     *
     * @return the highest-priority element
     * @throws java.util.NoSuchElementException if the queue is empty
     */
    E peek();

    /**
     * Retrieves AND removes the element with the highest priority.
     *
     * <p>After removal, the queue reorganizes itself (e.g., "heapify-down")
     * to restore the heap property.
     *
     * <p>Typical complexity: O(log n) for heap-based implementations.
     *
     * @return the highest-priority element, which is then removed
     * @throws java.util.NoSuchElementException if the queue is empty
     */
    E extractMin();

    /**
     * Updates the priority of an existing element.
     *
     * <p>This is the operation that makes priority queues especially useful
     * in graph algorithms like Dijkstra's shortest path. After the update,
     * the queue restructures itself to maintain correct ordering.
     *
     * <p>Typical complexity: O(log n) for heap-based implementations.
     *
     * @param element      the element whose priority is being changed
     * @param newElement   the replacement element with the updated priority
     * @throws java.util.NoSuchElementException if {@code element} is not found
     */
    void changePriority(E element, E newElement);

    /**
     * Returns true if the priority queue contains the specified element.
     * Typical complexity: O(n) — requires a scan of all elements.
     *
     * @param element the element to search for
     * @return true if found, false otherwise
     */
    boolean contains(E element);

    /**
     * Returns the number of elements currently in the priority queue.
     *
     * <p>Typical complexity: O(1).
     *
     * @return the number of elements
     */
    int size();

    /**
     * Returns {@code true} if the priority queue contains no elements.
     *
     * <p>Typical complexity: O(1).
     *
     * @return {@code true} if empty, {@code false} otherwise
     */
    default boolean isEmpty() {
        return size() == 0;
    }
}
