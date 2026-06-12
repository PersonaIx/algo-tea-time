package org.tea.datastructures.lists.skiplists;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;


@DisplayName("SkipList")
class SkipListTest {

    private SkipList<Integer> list;

    @BeforeEach
    void setUp() {
        list = new LinkedSkipList<>();
    }

    private void populate(Integer... elements) {
        for (Integer e : elements) {
            list.add(e);
        }
    }

    @Nested
    @DisplayName("Given a new skip list")
    class GivenNewSkipList {

        @Test
        @DisplayName("size is zero")
        void sizeIsZero() {
            assertThat(list.size()).isZero();
        }

        @Test
        @DisplayName("isEmpty returns true")
        void isEmptyReturnsTrue() {
            assertThat(list.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("levels is zero for an empty list")
        void levelsIsZero() {
            assertThat(list.levels()).isZero();
        }

        @Test
        @DisplayName("toList returns an empty list")
        void toListReturnsEmptyList() {
            assertThat(list.toList()).isEmpty();
        }

        @Test
        @DisplayName("contains returns false for any element")
        void containsReturnsFalse() {
            assertThat(list.contains(42)).isFalse();
        }

        @Test
        @DisplayName("first throws NoSuchElementException")
        void firstThrows() {
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> list.first());
        }

        @Test
        @DisplayName("last throws NoSuchElementException")
        void lastThrows() {
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> list.last());
        }
    }

    @Nested
    @DisplayName("add()")
    class Add {

        @Test
        @DisplayName("returns true when element is new")
        void returnsTrueForNewElement() {
            assertThat(list.add(10)).isTrue();
        }

        @Test
        @DisplayName("returns false for a duplicate")
        void returnsFalseForDuplicate() {
            list.add(10);
            assertThat(list.add(10)).isFalse();
        }

        @Test
        @DisplayName("increases size by one per unique element")
        void increasesSizeByOne() {
            list.add(1);
            list.add(2);
            list.add(3);
            assertThat(list.size()).isEqualTo(3);
        }

        @Test
        @DisplayName("duplicate does not change size")
        void duplicateDoesNotChangeSize() {
            list.add(5);
            list.add(5);
            assertThat(list.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("elements are stored in sorted order regardless of insertion order")
        void elementsSortedRegardlessOfInsertionOrder() {
            populate(5, 3, 9, 1, 7);
            assertThat(list.toList()).containsExactly(1, 3, 5, 7, 9);
        }

        @Test
        @DisplayName("null element throws NullPointerException")
        void nullThrows() {
            assertThatNullPointerException().isThrownBy(() -> list.add(null));
        }
    }

    @Nested
    @DisplayName("remove()")
    class Remove {

        @Test
        @DisplayName("returns true when element is present")
        void returnsTrueWhenPresent() {
            list.add(10);
            assertThat(list.remove(10)).isTrue();
        }

        @Test
        @DisplayName("returns false when element is absent")
        void returnsFalseWhenAbsent() {
            assertThat(list.remove(99)).isFalse();
        }

        @Test
        @DisplayName("decreases size by one")
        void decreasesSizeByOne() {
            populate(1, 2, 3);
            list.remove(2);
            assertThat(list.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("removed element is no longer contained")
        void removedElementNotContained() {
            list.add(7);
            list.remove(7);
            assertThat(list.contains(7)).isFalse();
        }

        @Test
        @DisplayName("remaining elements stay sorted after removal")
        void remainingElementsStaySorted() {
            populate(1, 2, 3, 4, 5);
            list.remove(3);
            assertThat(list.toList()).containsExactly(1, 2, 4, 5);
        }

        @Test
        @DisplayName("removing the only element leaves the list empty")
        void removeOnlyElementLeavesEmpty() {
            list.add(1);
            list.remove(1);
            assertThat(list.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("null element throws NullPointerException")
        void nullThrows() {
            assertThatNullPointerException().isThrownBy(() -> list.remove(null));
        }
    }

    @Nested
    @DisplayName("contains()")
    class Contains {

        @Test
        @DisplayName("returns true for a present element")
        void returnsTrueWhenPresent() {
            list.add(42);
            assertThat(list.contains(42)).isTrue();
        }

        @Test
        @DisplayName("returns false for an absent element")
        void returnsFalseWhenAbsent() {
            populate(1, 2, 3);
            assertThat(list.contains(99)).isFalse();
        }

        @Test
        @DisplayName("null element throws NullPointerException")
        void nullThrows() {
            assertThatNullPointerException().isThrownBy(() -> list.contains(null));
        }
    }

    @Nested
    @DisplayName("first() and last()")
    class FirstAndLast {

        @Test
        @DisplayName("first returns the smallest element")
        void firstReturnsSmallest() {
            populate(5, 3, 9, 1, 7);
            assertThat(list.first()).isEqualTo(1);
        }

        @Test
        @DisplayName("last returns the largest element")
        void lastReturnsLargest() {
            populate(5, 3, 9, 1, 7);
            assertThat(list.last()).isEqualTo(9);
        }

        @Test
        @DisplayName("first and last are equal for a single-element list")
        void singleElementFirstEqualsLast() {
            list.add(42);
            assertThat(list.first()).isEqualTo(list.last());
        }

        @Test
        @DisplayName("first updates correctly after removing the minimum")
        void firstUpdatesAfterRemovingMinimum() {
            populate(1, 2, 3);
            list.remove(1);
            assertThat(list.first()).isEqualTo(2);
        }

        @Test
        @DisplayName("last updates correctly after removing the maximum")
        void lastUpdatesAfterRemovingMaximum() {
            populate(1, 2, 3);
            list.remove(3);
            assertThat(list.last()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("range()")
    class Range {

        @BeforeEach
        void populate() {
            SkipListTest.this.populate(1, 3, 5, 7, 9, 11);
        }

        @Test
        @DisplayName("returns all elements within inclusive bounds")
        void returnsElementsWithinBounds() {
            assertThat(list.range(3, 9)).containsExactly(3, 5, 7, 9);
        }

        @Test
        @DisplayName("returns empty list when no elements fall in range")
        void returnsEmptyWhenNoMatch() {
            assertThat(list.range(4, 4)).isEmpty();
        }

        @Test
        @DisplayName("returns single element when bounds match exactly one element")
        void returnsSingleElementOnExactMatch() {
            assertThat(list.range(5, 5)).containsExactly(5);
        }

        @Test
        @DisplayName("returns all elements when bounds span the full list")
        void returnsAllElementsForFullSpan() {
            assertThat(list.range(1, 11)).containsExactly(1, 3, 5, 7, 9, 11);
        }

        @Test
        @DisplayName("throws IllegalArgumentException when lo is greater than hi")
        void throwsWhenLoGreaterThanHi() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> list.range(9, 3));
        }

        @Test
        @DisplayName("null lo throws NullPointerException")
        void nullLoThrows() {
            assertThatNullPointerException().isThrownBy(() -> list.range(null, 5));
        }

        @Test
        @DisplayName("null hi throws NullPointerException")
        void nullHiThrows() {
            assertThatNullPointerException().isThrownBy(() -> list.range(1, null));
        }
    }

    @Nested
    @DisplayName("toList()")
    class ToList {

        @Test
        @DisplayName("returns elements in ascending sorted order")
        void returnsSortedAscending() {
            populate(9, 3, 6, 1, 7);
            assertThat(list.toList()).containsExactly(1, 3, 6, 7, 9);
        }

        @Test
        @DisplayName("returns empty list when skip list is empty")
        void returnsEmptyList() {
            assertThat(list.toList()).isEmpty();
        }

        @Test
        @DisplayName("returned list is a snapshot — mutations do not affect it")
        void returnedListIsSnapshot() {
            populate(1, 2, 3);
            List<Integer> snapshot = list.toList();
            list.add(99);
            assertThat(snapshot).containsExactly(1, 2, 3);
        }
    }

    @Nested
    @DisplayName("clear()")
    class Clear {

        @Test
        @DisplayName("size becomes zero")
        void sizeBecomesZero() {
            populate(1, 2, 3);
            list.clear();
            assertThat(list.size()).isZero();
        }

        @Test
        @DisplayName("isEmpty returns true")
        void isEmptyReturnsTrue() {
            populate(1, 2, 3);
            list.clear();
            assertThat(list.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("levels resets to zero after clear")
        void levelsResetsToZero() {
            populate(1, 2, 3, 4, 5);
            list.clear();
            assertThat(list.levels()).isZero();
        }

        @Test
        @DisplayName("elements can be added again after clear")
        void canAddAfterClear() {
            populate(1, 2, 3);
            list.clear();
            list.add(7);
            assertThat(list.toList()).containsExactly(7);
        }
    }

    @Nested
    @DisplayName("Introspection — levels() and levelSize()")
    class Introspection {

        @Test
        @DisplayName("levelSize(0) always equals size()")
        void levelZeroSizeEqualsSize() {
            populate(1, 2, 3, 4, 5);
            assertThat(list.levelSize(0)).isEqualTo(list.size());
        }

        @Test
        @DisplayName("every level above 0 has fewer or equal nodes than the level below")
        void higherLevelsHaveFewerOrEqualNodes() {
            populate(1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                    11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
            for (int lvl = 1; lvl < list.levels(); lvl++) {
                assertThat(list.levelSize(lvl))
                        .as("level %d should have <= nodes than level %d", lvl, lvl - 1)
                        .isLessThanOrEqualTo(list.levelSize(lvl - 1));
            }
        }

        @Test
        @DisplayName("levelSize throws IndexOutOfBoundsException for negative level")
        void negativeIndexThrows() {
            assertThatExceptionOfType(IndexOutOfBoundsException.class)
                    .isThrownBy(() -> list.levelSize(-1));
        }

        @Test
        @DisplayName("levelSize throws IndexOutOfBoundsException for level >= levels()")
        void outOfRangeIndexThrows() {
            assertThatExceptionOfType(IndexOutOfBoundsException.class)
                    .isThrownBy(() -> list.levelSize(list.levels()));
        }

        @ParameterizedTest(name = "with {0} elements, levelSize(0) == {0}")
        @ValueSource(ints = {1, 5, 10, 50})
        @DisplayName("levelSize(0) equals n for n inserted elements")
        void levelZeroMatchesSizeForVariousN(int n) {
            for (int i = 1; i <= n; i++) list.add(i);
            assertThat(list.levelSize(0)).isEqualTo(n);
        }
    }
}