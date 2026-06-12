package org.tea.algos.slide;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SlidingWindowMaximizer")
class SlidingWindowMaximizerTest {

    private final SlidingWindowMaximizer<Integer> maximizer = new SlidingWindowMaximizer<>();

    @Test
    @DisplayName("returns the maximum of each window for a typical input")
    void findMaximum_typicalInput_returnsExpectedMaximums() {
        Integer[] data = {3, 1, 3, 2, 6, 0, 5, 4};

        List<Integer> result = maximizer.findMaximum(data, 3);

        assertThat(result).containsExactly(3, 3, 6, 6, 6, 5);
    }

    @Test
    @DisplayName("returns a single window result when k equals array length")
    void findMaximum_kEqualsArrayLength_returnsSingleMaximum() {
        Integer[] data = {4, 2, 9, 1};

        List<Integer> result = maximizer.findMaximum(data, 4);

        assertThat(result).containsExactly(9);
    }

    @Test
    @DisplayName("returns the original array when k equals 1")
    void findMaximum_kEqualsOne_returnsOriginalElements() {
        Integer[] data = {5, 7, 2, 8};

        List<Integer> result = maximizer.findMaximum(data, 1);

        assertThat(result).containsExactly(5, 7, 2, 8);
    }

    @Test
    @DisplayName("handles arrays containing duplicate maximum values")
    void findMaximum_duplicateMaximums_returnsCorrectMaximums() {
        Integer[] data = {1, 3, 3, 1, 1};

        List<Integer> result = maximizer.findMaximum(data, 2);

        assertThat(result).containsExactly(3, 3, 3, 1);
    }

    @Test
    @DisplayName("handles arrays containing negative numbers")
    void findMaximum_negativeNumbers_returnsCorrectMaximums() {
        Integer[] data = {-4, -2, -7, -1, -3};

        List<Integer> result = maximizer.findMaximum(data, 2);

        assertThat(result).containsExactly(-2, -2, -1, -1);
    }

    @Test
    @DisplayName("works correctly with a non-Integer comparable type (String)")
    void findMaximum_stringElements_returnsLexicographicMaximums() {
        String[] data = {"banana", "apple", "cherry", "date"};

        List<String> result = new SlidingWindowMaximizer<String>().findMaximum(data, 2);

        assertThat(result).containsExactly("banana", "cherry", "date");
    }

    @Nested
    @DisplayName("invalid arguments")
    class InvalidArguments {

        @Test
        @DisplayName("throws when k is larger than the array length")
        void findMaximum_kGreaterThanLength_throwsIllegalArgumentException() {
            Integer[] data = {1, 2, 3};

            assertThatThrownBy(() -> maximizer.findMaximum(data, 4))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest(name = "k = {0} is invalid")
        @CsvSource({"0", "-1", "-5"})
        @DisplayName("throws when k is zero or negative")
        void findMaximum_nonPositiveK_throwsIllegalArgumentException(int k) {
            Integer[] data = {1, 2, 3};

            assertThatThrownBy(() -> maximizer.findMaximum(data, k))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("throws when data array is null")
        void findMaximum_nullArray_throwsNullPointerException() {
            assertThatThrownBy(() -> maximizer.findMaximum(null, 2))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    @DisplayName("returned list size equals data.length - k + 1")
    void findMaximum_resultSize_matchesExpectedWindowCount() {
        Integer[] data = {1, 2, 3, 4, 5, 6, 7};
        int k = 3;

        List<Integer> result = maximizer.findMaximum(data, k);

        assertThat(result).hasSize(data.length - k + 1);
    }
}