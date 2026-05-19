package org.tea.algos.search;

public record SearchResult(boolean found, int index) {
    public static SearchResult of(int index) {
        return new SearchResult(true, index);
    }

    public static SearchResult notFound() {
        return new SearchResult(false, -1);
    }
}
