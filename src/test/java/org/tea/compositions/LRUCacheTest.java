package org.tea.compositions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LRUCacheTest {

    private LRUCache<Integer, String> cache;
    private static final int CAPACITY = 3;

    @BeforeEach
    void setUp() {
        cache = new LRUCache<>(CAPACITY); // replace with actual implementation
    }

    @Nested
    @DisplayName("Basic put/get behavior")
    class BasicOperations {

        @Test
        @DisplayName("put and get return correct value")
        void putAndGet() {
            cache.put(1, "one");

            assertThat(cache.get(1)).isEqualTo("one");
        }

        @Test
        @DisplayName("get on missing key returns null")
        void getMissingKey() {
            assertThat(cache.get(99)).isNull();
        }

        @Test
        @DisplayName("put on existing key updates value, not eviction order changes size")
        void putExistingKeyUpdatesValue() {
            cache.put(1, "one");
            cache.put(1, "ONE");

            assertThat(cache.get(1)).isEqualTo("ONE");
            assertThat(cache.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("containsKey reflects presence without affecting order")
        void containsKeyDoesNotAffectOrder() {
            cache.put(1, "one");
            cache.put(2, "two");

            assertThat(cache.containsKey(1)).isTrue();
            assertThat(cache.containsKey(99)).isFalse();
            assertThat(cache.keysInEvictionOrder()).containsExactly(1, 2);
        }

        @Test
        @DisplayName("remove deletes entry and reduces size")
        void removeEntry() {
            cache.put(1, "one");
            cache.put(2, "two");

            String removedVal = cache.remove(1);

            assertThat(removedVal).isEqualTo("one");
            assertThat(cache.containsKey(1)).isFalse();
            assertThat(cache.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("clear empties the cache")
        void clearCache() {
            cache.put(1, "one");
            cache.put(2, "two");

            cache.clear();

            assertThat(cache.size()).isZero();
            assertThat(cache.containsKey(1)).isFalse();
        }

        @Test
        @DisplayName("capacity returns configured capacity")
        void capacityIsCorrect() {
            assertThat(cache.capacity()).isEqualTo(CAPACITY);
        }
    }

    @Nested
    @DisplayName("Eviction behavior")
    class EvictionBehavior {

        @Test
        @DisplayName("evicts least recently used entry when capacity exceeded")
        void evictsLeastRecentlyUsed() {
            cache.put(1, "one");
            cache.put(2, "two");
            cache.put(3, "three");
            cache.put(4, "four"); // exceeds capacity, evicts 1

            assertThat(cache.size()).isEqualTo(CAPACITY);
            assertThat(cache.containsKey(1)).isFalse();
            assertThat(cache.containsKey(4)).isTrue();
        }

        @Test
        @DisplayName("get refreshes recency, preventing eviction")
        void getRefreshesRecency() {
            cache.put(1, "one");
            cache.put(2, "two");
            cache.put(3, "three");

            cache.get(1); // 1 becomes most recently used

            cache.put(4, "four"); // should evict 2, not 1

            assertThat(cache.containsKey(1)).isTrue();
            assertThat(cache.containsKey(2)).isFalse();
            assertThat(cache.containsKey(4)).isTrue();
        }

        @Test
        @DisplayName("put on existing key refreshes recency")
        void putExistingKeyRefreshesRecency() {
            cache.put(1, "one");
            cache.put(2, "two");
            cache.put(3, "three");

            cache.put(1, "ONE"); // 1 becomes most recently used

            cache.put(4, "four"); // should evict 2

            assertThat(cache.containsKey(1)).isTrue();
            assertThat(cache.containsKey(2)).isFalse();
            assertThat(cache.get(1)).isEqualTo("ONE");
        }

        @Test
        @DisplayName("eviction order is correctly maintained")
        void evictionOrderMaintained() {
            cache.put(1, "one");
            cache.put(2, "two");
            cache.put(3, "three");

            assertThat(cache.keysInEvictionOrder()).containsExactly(1, 2, 3);

            cache.get(1);

            assertThat(cache.keysInEvictionOrder()).containsExactly(2, 3, 1);
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("cache with capacity 1 evicts on every put")
        void capacityOneEvictsImmediately() {
            LRUCache<Integer, String> tinyCache = new LRUCache<>(1);

            tinyCache.put(1, "one");
            tinyCache.put(2, "two");

            assertThat(tinyCache.containsKey(1)).isFalse();
            assertThat(tinyCache.containsKey(2)).isTrue();
            assertThat(tinyCache.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("null value handling")
        void nullValueHandling() {
            cache.put(1, null);

            assertThat(cache.containsKey(1)).isTrue();
            assertThat(cache.get(1)).isNull();
        }

        @Test
        @DisplayName("remove on missing key returns null without error")
        void removeMissingKey() {
            String result = cache.remove(99);

            assertThat(result).isNull();
        }
    }
}
