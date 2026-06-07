package org.tea.algos.datastructures.lists.arraylists;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Test suite for ArrayListOperations<T>.
 * Assumes a concrete implementation class: MyArrayList<T>
 * that implements ArrayListOperations<T>.
 * Run with JUnit 5 + AssertJ on the classpath.
 */
@DisplayName("ArrayListOperations")
class ArrayListOperationsTest {

    // -----------------------------------------------------------------------
    // Shared fixture
    // -----------------------------------------------------------------------

    private ArrayListOperations<Integer> list;

    @BeforeEach
    void setUp() {
        list = new ArrayList<>();   // swap in your concrete class
    }

    // -----------------------------------------------------------------------
    // isEmpty() / size()
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("isEmpty() and size()")
    class EmptyAndSize {

        @Test
        @DisplayName("new list is empty and has size 0")
        void newListIsEmpty() {
            assertThat(list.isEmpty()).isTrue();
            assertThat(list.size()).isZero();
        }

        @Test
        @DisplayName("list is not empty after first add")
        void notEmptyAfterAdd() {
            list.add(1);
            assertThat(list.isEmpty()).isFalse();
            assertThat(list.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("size tracks multiple insertions correctly")
        void sizeTracksInsertions() {
            list.add(1);
            list.add(2);
            list.add(3);
            assertThat(list.size()).isEqualTo(3);
        }
    }

    // -----------------------------------------------------------------------
    // add(T value)  — append / addLast semantics
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("add(T value)")
    class Add {

        @Test
        @DisplayName("appended elements appear in insertion order")
        void appendsInOrder() {
            list.add(10);
            list.add(20);
            list.add(30);
            assertThat(list.toList()).containsExactly(10, 20, 30);
        }

        @Test
        @DisplayName("allows duplicate values")
        void allowsDuplicates() {
            list.add(5);
            list.add(5);
            assertThat(list.size()).isEqualTo(2);
            assertThat(list.toList()).containsExactly(5, 5);
        }

        @Test
        @DisplayName("allows null values")
        void allowsNull() {
            ArrayListOperations<String> strings = new ArrayList<>();
            strings.add(null);
            assertThat(strings.size()).isEqualTo(1);
            assertThat(strings.toList()).containsExactly((String) null);
        }
    }

    // -----------------------------------------------------------------------
    // add(int index, T value)  — index-based insertion
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("add(int index, T value)")
    class AddAtIndex {

        @Test
        @DisplayName("insert at index 0 prepends the element")
        void insertAtHead() {
            list.add(2);
            list.add(3);
            list.add(0, 1);
            assertThat(list.toList()).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("insert at last valid index appends the element")
        void insertAtTail() {
            list.add(1);
            list.add(2);
            list.add(2, 3);   // index == size
            assertThat(list.toList()).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("insert in the middle shifts elements right")
        void insertInMiddle() {
            list.add(1);
            list.add(3);
            list.add(1, 2);
            assertThat(list.toList()).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("insert into an empty list at index 0 works")
        void insertIntoEmptyList() {
            list.add(0, 42);
            assertThat(list.toList()).containsExactly(42);
        }

        @Test
        @DisplayName("negative index throws IndexOutOfBoundsException")
        void negativeIndexThrows() {
            assertThatExceptionOfType(IndexOutOfBoundsException.class)
                    .isThrownBy(() -> list.add(-1, 99));
        }

        @Test
        @DisplayName("index > size throws IndexOutOfBoundsException")
        void indexBeyondSizeThrows() {
            list.add(1);
            assertThatExceptionOfType(IndexOutOfBoundsException.class)
                    .isThrownBy(() -> list.add(5, 99));
        }
    }

    // -----------------------------------------------------------------------
    // get(int index)  — O(1) random access
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("get(int index)")
    class Get {

        @Test
        @DisplayName("retrieves the correct element at each index")
        void retrievesCorrectElements() {
            list.add(10);
            list.add(20);
            list.add(30);
            assertThat(list.get(0)).isEqualTo(10);
            assertThat(list.get(1)).isEqualTo(20);
            assertThat(list.get(2)).isEqualTo(30);
        }

        @Test
        @DisplayName("get on empty list throws IndexOutOfBoundsException")
        void getOnEmptyListThrows() {
            assertThatExceptionOfType(IndexOutOfBoundsException.class)
                    .isThrownBy(() -> list.get(0));
        }

        @Test
        @DisplayName("negative index throws IndexOutOfBoundsException")
        void negativeIndexThrows() {
            list.add(1);
            assertThatExceptionOfType(IndexOutOfBoundsException.class)
                    .isThrownBy(() -> list.get(-1));
        }

        @Test
        @DisplayName("index equal to size throws IndexOutOfBoundsException")
        void indexEqualToSizeThrows() {
            list.add(1);
            assertThatExceptionOfType(IndexOutOfBoundsException.class)
                    .isThrownBy(() -> list.get(1));
        }
    }

    // -----------------------------------------------------------------------
    // remove(T value)  — remove by value
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("remove(T value)")
    class RemoveByValue {

        @Test
        @DisplayName("returns true and removes the first matching element")
        void removesFirstMatch() {
            list.add(1);
            list.add(2);
            list.add(2);
            list.add(3);

            boolean removed = list.remove(Integer.valueOf(2));

            assertThat(removed).isTrue();
            assertThat(list.toList()).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("returns false when value is absent")
        void returnsFalseForAbsentValue() {
            list.add(1);
            assertThat(list.remove(Integer.valueOf(99))).isFalse();
            assertThat(list.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("can remove the sole element, leaving list empty")
        void removeSoleElement() {
            list.add(42);
            assertThat(list.remove(Integer.valueOf(42))).isTrue();
            assertThat(list.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("removes element at head, shifts remaining left")
        void removeFromHead() {
            list.add(1);
            list.add(2);
            list.add(3);
            Integer removed = list.remove(0);
            assertThat(removed).isEqualTo(1);
            assertThat(list.toList()).containsExactly(2, 3);
        }


        @Test
        @DisplayName("removes element at tail without affecting others")
        void removeFromTail() {
            list.add(1);
            list.add(2);
            list.add(3);
            Integer removed = list.remove(2);
            assertThat(removed).isEqualTo(3);
            assertThat(list.toList()).containsExactly(1, 2);
        }
    }

    // -----------------------------------------------------------------------
    // remove(int index)  — remove by index
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("remove(int index)")
    class RemoveByIndex {

        @Test
        @DisplayName("returns the element that was removed")
        void returnsRemovedElement() {
            list.add(10);
            list.add(20);
            list.add(30);
            Integer removed = list.remove(1);
            assertThat(removed).isEqualTo(20);
            assertThat(list.toList()).containsExactly(10, 30);
        }

        @Test
        @DisplayName("remove at index 0 shifts remaining elements left")
        void removeAtHead() {
            list.add(1);
            list.add(2);
            list.add(3);
            list.remove(0);
            assertThat(list.toList()).containsExactly(2, 3);
        }

        @Test
        @DisplayName("remove at last index leaves preceding elements intact")
        void removeAtTail() {
            list.add(1);
            list.add(2);
            list.add(3);
            list.remove(2);
            assertThat(list.toList()).containsExactly(1, 2);
        }

        @Test
        @DisplayName("negative index throws IndexOutOfBoundsException")
        void negativeIndexThrows() {
            list.add(1);
            assertThatExceptionOfType(IndexOutOfBoundsException.class)
                    .isThrownBy(() -> list.remove(-1));
        }

        @Test
        @DisplayName("index equal to size throws IndexOutOfBoundsException")
        void indexEqualToSizeThrows() {
            list.add(1);
            assertThatExceptionOfType(IndexOutOfBoundsException.class)
                    .isThrownBy(() -> list.remove(1));
        }
    }

    // -----------------------------------------------------------------------
    // contains(T value)
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("contains(T value)")
    class Contains {

        @Test
        @DisplayName("returns false on empty list")
        void emptyList() {
            assertThat(list.contains(1)).isFalse();
        }

        @Test
        @DisplayName("returns true for an element present in the list")
        void presentElement() {
            list.add(42);
            assertThat(list.contains(42)).isTrue();
        }

        @Test
        @DisplayName("returns false for an element not in the list")
        void absentElement() {
            list.add(1);
            list.add(2);
            assertThat(list.contains(99)).isFalse();
        }

        @Test
        @DisplayName("finds element at first index")
        void findsAtHead() {
            list.add(1);
            list.add(2);
            list.add(3);
            assertThat(list.contains(1)).isTrue();
        }

        @Test
        @DisplayName("finds element at last index")
        void findsAtTail() {
            list.add(1);
            list.add(2);
            list.add(3);
            assertThat(list.contains(3)).isTrue();
        }
    }

    // -----------------------------------------------------------------------
    // Capacity / dynamic resizing  (internal behaviour, observable via size())
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Dynamic resizing")
    class DynamicResizing {

        @ParameterizedTest(name = "add {0} elements without errors")
        @ValueSource(ints = {1, 10, 100, 1_000, 10_000})
        @DisplayName("handles various element counts without throwing")
        void handlesGrowth(int n) {
            assertThatNoException().isThrownBy(() -> {
                for (int i = 0; i < n; i++) list.add(i);
            });
            assertThat(list.size()).isEqualTo(n);
        }

        @Test
        @DisplayName("all elements remain correct and ordered after many adds")
        void dataIntegrityAfterGrowth() {
            int n = 1_000;
            for (int i = 0; i < n; i++) list.add(i);

            List<Integer> snapshot = list.toList();
            assertThat(snapshot).hasSize(n);
            for (int i = 0; i < n; i++) {
                assertThat(snapshot.get(i)).isEqualTo(i);
            }
        }
    }

    // -----------------------------------------------------------------------
    // toList()  — contract for the testing helper
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("toList()")
    class ToList {

        @Test
        @DisplayName("returns an empty list for an empty ArrayList")
        void emptyReturnsEmptyList() {
            assertThat(list.toList()).isEmpty();
        }

        @Test
        @DisplayName("returned list is a snapshot and not backed by the ArrayList")
        void returnsSnapshot() {
            list.add(1);
            list.add(2);
            List<Integer> snapshot = list.toList();

            list.add(3);   // mutate the original

            assertThat(snapshot).hasSize(2);    // snapshot must be unaffected
            assertThat(list.toList()).hasSize(3);
        }
    }
}