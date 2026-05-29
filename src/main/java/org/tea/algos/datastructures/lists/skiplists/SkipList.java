package org.tea.algos.datastructures.lists.skiplists;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * An ordered, probabilistic data structure that maintains elements in sorted
 * order using a tower of linked lists. Expected O(log n) for search, insert,
 * and delete; O(1) for min/max access.
 *
 * <p>Duplicate elements are not permitted. All operations use the natural
 * ordering defined by {@link Comparable}.
 *
 * <p>This interface is designed for educational use and exposes structural
 * introspection methods ({@link #levels()}, {@link #levelSize(int)}) that
 * would normally be omitted from a production API.
 *
 * @param <T> the type of elements, must implement {@link Comparable}
 */
public interface SkipList<T extends Comparable<T>> {

    /**
     * Inserts {@code element} into the skip list in sorted order.
     *
     * <p>A random tower height is chosen with geometric probability p = 0.5.
     * The element is linked into every level up to its tower height.
     *
     * @param element the element to insert; must not be {@code null}
     * @return {@code true} if the element was inserted;
     *         {@code false} if it was already present (no-op)
     * @throws NullPointerException if {@code element} is {@code null}
     */
    boolean add(T element);

    /**
     * Removes {@code element} from the skip list if it is present.
     *
     * <p>Forward pointers are repaired at every level that contained the node.
     * Empty levels above the new highest node are collapsed.
     *
     * @param element the element to remove; must not be {@code null}
     * @return {@code true} if the element was found and removed;
     *         {@code false} if it was not present
     * @throws NullPointerException if {@code element} is {@code null}
     */
    boolean remove(T element);

    /**
     * Removes all elements from the skip list.
     * After this call, {@link #size()} returns {@code 0} and
     * {@link #levels()} returns {@code 1}.
     */
    void clear();

    /**
     * Returns {@code true} if this skip list contains {@code element}.
     *
     * <p>Search begins at the highest level and descends, skipping
     * large spans at upper levels before arriving at level 0.
     *
     * @param element the element to search for; must not be {@code null}
     * @return {@code true} if found
     * @throws NullPointerException if {@code element} is {@code null}
     */
    boolean contains(T element);

    /**
     *
     * @return the element count; always &ge; 0
     */
    int size();

    /**
     * Returns {@code true} if the skip list contains no elements.
     *
     * @return {@code true} if {@link #size()} == 0
     */
    boolean isEmpty();

    /**
     * Returns the smallest element in the skip list.
     *
     *
     * @return the minimum element
     * @throws NoSuchElementException if the skip list is empty
     */
    T first();

    /**
     * Returns the largest element in the skip list.
     *
     *
     * @return the maximum element
     * @throws NoSuchElementException if the skip list is empty
     */
    T last();

    /**
     * Returns all elements {@code e} such that
     * {@code lo.compareTo(e) <= 0 && e.compareTo(hi) <= 0},
     * in ascending sorted order.
     *
     * @param lo the inclusive lower bound; must not be {@code null}
     * @param hi the inclusive upper bound; must not be {@code null}
     * @return a {@link List} of matching elements, possibly empty
     * @throws NullPointerException     if {@code lo} or {@code hi} is {@code null}
     * @throws IllegalArgumentException if {@code lo.compareTo(hi) > 0}
     */
    List<T> range(T lo, T hi);

    // -------------------------------------------------------------------------
    // Introspection (educational)
    // -------------------------------------------------------------------------

    /**
     * Returns the current number of levels (tower height) in the skip list.
     *
     * @return the current level count; always &ge; 1
     */
    int levels();

    /**
     * Returns the number of nodes linked at the given {@code level}.
     *
     * <p>Level 0 is the base lane and always equals {@link #size()}.
     * Each higher level is expected to contain roughly half the nodes
     * of the level below it (for p = 0.5).
     *
     * <p><em>This method exists for educational observation of the
     * geometric distribution across levels.</em>
     *
     * @param level the level index; 0 is the base lane
     * @return the number of nodes at that level
     * @throws IndexOutOfBoundsException if {@code level < 0} or
     *                                   {@code level >= levels()}
     */
    int levelSize(int level);

    /**
     * Returns a snapshot of all elements in ascending sorted order.
     *
     * <p>Equivalent to walking the level-0 lane from head to tail. The
     * returned list is independent of the skip list — mutations to either
     * do not affect the other.
     *
     * <p>Primarily intended for unit tests and debugging:
     * <pre>{@code
     *   assertThat(skipList.toList())
     *       .containsExactly(1, 3, 5, 7, 9);
     * }</pre>
     *
     * @return a new, mutable {@link List} of all elements; empty if the
     *         skip list contains no elements
     */
    List<T> toList();
}