package org.tea.algos.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tea.algos.shared.AlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class BinarySearchTest {

    private BinarySearch binarySearch;
    private static final int[] SORTED_ARRAY = {1, 3, 5, 7, 9};

    @BeforeEach
    void setUp() {
        binarySearch = new BinarySearch();
    }

    @Test
    void findsElementInMiddle() {
        assertThat(binarySearch.search(SORTED_ARRAY, 5).index()).isEqualTo(2);
    }

    @Test
    void findsElementAtFirstIndex() {
        assertThat(binarySearch.search(SORTED_ARRAY, 1).index()).isEqualTo(0);
    }

    @Test
    void findsElementAtLastIndex() {
        assertThat(binarySearch.search(SORTED_ARRAY, 9).index()).isEqualTo(4);
    }

    @Test
    void returnsNotFoundForMissingElement() {
        assertThat(binarySearch.search(SORTED_ARRAY, 4).found()).isFalse();
    }

    @Test
    void returnsNotFoundForEmptyArray() {
        assertThatThrownBy(() -> binarySearch.search(new int[]{}, 1))
                .isInstanceOf(AlgorithmException.class);
    }

    @Test
    void throwsOnNullArray() {
        assertThatThrownBy(() -> binarySearch.search(null, 5))
                .isInstanceOf(AlgorithmException.class);
    }
}
