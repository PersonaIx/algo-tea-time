package org.tea.algos.search;

import org.springframework.stereotype.Component;
import org.tea.algos.shared.AlgorithmException;

@Component
public class BinarySearch implements Searchable {

    @Override
    public SearchResult search(int[] sortedArray, int target) {
        if (sortedArray == null || sortedArray.length == 0) {
            throw new AlgorithmException("Array must not be null or empty");
        }

        int lowerBound = 0;
        int upperBound = sortedArray.length - 1;

        while (lowerBound <= upperBound) {
            int offset = (upperBound - lowerBound) / 2;
            int middle = lowerBound + offset;
            int currentValue = sortedArray[middle];
            if (currentValue == target) {
                return SearchResult.of(middle);
            }
            if (target > currentValue) {
                lowerBound = middle + 1;
            } else {
                upperBound = middle - 1;
            }
        }

        return SearchResult.notFound();
    }
}