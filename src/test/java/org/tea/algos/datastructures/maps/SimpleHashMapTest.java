package org.tea.algos.datastructures.maps;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

/**
 * Specification-level tests for SimpleHashMap.
 *
 * Structure mirrors the contract sections of SimpleMap:
 *   1. Initial state
 *   2. put
 *   3. get / getOrDefault
 *   4. remove
 *   5. containsKey
 *   6. size / isEmpty
 *   7. clear
 *   8. keySet (contents + liveness)
 *   9. Iteration
 *  10. Collisions  ← requires capacity()
 *  11. Resize / rehash
 *  12. Null semantics
 *
 * Assumption: SimpleHashMap(int initialCapacity) constructor exists,
 * and SimpleHashMap implements capacity() for structural tests.
 */
@DisplayName("SimpleHashMap")
class SimpleHashMapTest {

    // A helper that forces collisions: every key hashes to the same bucket.
    // Usage: new SimpleHashMap<>(16, k -> 0)
    // Assumes a secondary constructor SimpleHashMap(int capacity, ToIntFunction<K> hashFn).
    // If your impl does not support injecting a hash function, replace CollidingKey below.

    /**
     * A key type whose hashCode() always returns the same value,
     * so no custom constructor is needed to force collisions.
     */
    record CollidingKey(String name) {
        @Override public int hashCode() { return 42; }  // every instance → same bucket
        @Override public boolean equals(Object o) {
            return o instanceof CollidingKey ck && name.equals(ck.name);
        }
    }

    private SimpleHashMap<String, Integer> map;

    @BeforeEach
    void setUp() {
        map = new SimpleHashMap<>();
    }

    // ================================================================== //
    //  1. Initial state                                                   //
    // ================================================================== //

    @Nested
    @DisplayName("Initial state")
    class InitialState {

        @Test
        @DisplayName("new map is empty")
        void newMapIsEmpty() {
            assertThat(map.isEmpty()).isTrue();
            assertThat(map.size()).isZero();
        }

        @Test
        @DisplayName("get on empty map returns null")
        void getOnEmptyMapReturnsNull() {
            assertThat(map.get("missing")).isNull();
        }

        @Test
        @DisplayName("containsKey on empty map returns false")
        void containsKeyOnEmptyMapReturnsFalse() {
            assertThat(map.containsKey("missing")).isFalse();
        }

        @Test
        @DisplayName("keySet of empty map is empty")
        void keySetOfEmptyMapIsEmpty() {
            assertThat(map.keySet().isEmpty()).isTrue();
            assertThat(map.keySet().size()).isZero();
        }

        @Test
        @DisplayName("iterator on empty map has no elements")
        void iteratorOnEmptyMapHasNoElements() {
            assertThat(map.iterator().hasNext()).isFalse();
        }
    }

    // ================================================================== //
    //  2. put                                                             //
    // ================================================================== //

    @Nested
    @DisplayName("put")
    class Put {

        @Test
        @DisplayName("returns null when key is new")
        void returnsNullForNewKey() {
            assertThat(map.put("a", 1)).isNull();
        }

        @Test
        @DisplayName("returns old value when key already exists")
        void returnsOldValueOnUpdate() {
            map.put("a", 1);
            assertThat(map.put("a", 2)).isEqualTo(1);
        }

        @Test
        @DisplayName("updates value for existing key")
        void updatesValueForExistingKey() {
            map.put("a", 1);
            map.put("a", 99);
            assertThat(map.get("a")).isEqualTo(99);
        }

        @Test
        @DisplayName("does not increase size on update")
        void doesNotIncreaseSizeOnUpdate() {
            map.put("a", 1);
            map.put("a", 2);
            assertThat(map.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("increases size on new key")
        void increasesSizeOnNewKey() {
            map.put("a", 1);
            map.put("b", 2);
            assertThat(map.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("multiple distinct keys are all retrievable")
        void multipleDistinctKeys() {
            map.put("x", 10);
            map.put("y", 20);
            map.put("z", 30);

            assertThat(map.get("x")).isEqualTo(10);
            assertThat(map.get("y")).isEqualTo(20);
            assertThat(map.get("z")).isEqualTo(30);
        }
    }

    // ================================================================== //
    //  3. get / getOrDefault                                              //
    // ================================================================== //

    @Nested
    @DisplayName("get / getOrDefault")
    class GetAndGetOrDefault {

        @Test
        @DisplayName("get returns correct value for existing key")
        void getExistingKey() {
            map.put("k", 7);
            assertThat(map.get("k")).isEqualTo(7);
        }

        @Test
        @DisplayName("get returns null for absent key")
        void getAbsentKey() {
            assertThat(map.get("nope")).isNull();
        }

        @Test
        @DisplayName("getOrDefault returns value when key is present")
        void getOrDefaultKeyPresent() {
            map.put("k", 5);
            assertThat(map.getOrDefault("k", 99)).isEqualTo(5);
        }

        @Test
        @DisplayName("getOrDefault returns default when key is absent")
        void getOrDefaultKeyAbsent() {
            assertThat(map.getOrDefault("missing", 42)).isEqualTo(42);
        }

        @Test
        @DisplayName("getOrDefault returns stored null when key maps to null explicitly")
        void getOrDefaultWithExplicitNullValue() {
            map.put("k", null);
            // key exists but value is null — must return null, NOT the default
            assertThat(map.getOrDefault("k", 99)).isNull();
        }
    }

    // ================================================================== //
    //  4. remove                                                          //
    // ================================================================== //

    @Nested
    @DisplayName("remove")
    class Remove {

        @Test
        @DisplayName("returns removed value")
        void returnsRemovedValue() {
            map.put("a", 3);
            assertThat(map.remove("a")).isEqualTo(3);
        }

        @Test
        @DisplayName("returns null for absent key")
        void returnsNullForAbsentKey() {
            assertThat(map.remove("ghost")).isNull();
        }

        @Test
        @DisplayName("key is no longer findable after removal")
        void keyGoneAfterRemoval() {
            map.put("a", 1);
            map.remove("a");

            assertThat(map.get("a")).isNull();
            assertThat(map.containsKey("a")).isFalse();
        }

        @Test
        @DisplayName("size decreases after removal")
        void sizeDecreasesAfterRemoval() {
            map.put("a", 1);
            map.put("b", 2);
            map.remove("a");

            assertThat(map.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("removing absent key does not change size")
        void removeAbsentKeyDoesNotChangeSize() {
            map.put("a", 1);
            map.remove("z");

            assertThat(map.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("other keys survive a removal")
        void otherKeysSurviveRemoval() {
            map.put("a", 1);
            map.put("b", 2);
            map.put("c", 3);
            map.remove("b");

            assertThat(map.get("a")).isEqualTo(1);
            assertThat(map.get("c")).isEqualTo(3);
        }
    }

    // ================================================================== //
    //  5. containsKey                                                     //
    // ================================================================== //

    @Nested
    @DisplayName("containsKey")
    class ContainsKey {

        @Test
        @DisplayName("true after put")
        void trueAfterPut() {
            map.put("present", 1);
            assertThat(map.containsKey("present")).isTrue();
        }

        @Test
        @DisplayName("false for absent key")
        void falseForAbsentKey() {
            assertThat(map.containsKey("absent")).isFalse();
        }

        @Test
        @DisplayName("false after remove")
        void falseAfterRemove() {
            map.put("k", 1);
            map.remove("k");
            assertThat(map.containsKey("k")).isFalse();
        }

        @Test
        @DisplayName("true even when value is null")
        void trueWhenValueIsNull() {
            map.put("nullVal", null);
            assertThat(map.containsKey("nullVal")).isTrue();
        }
    }

    // ================================================================== //
    //  6. size / isEmpty                                                  //
    // ================================================================== //

    @Nested
    @DisplayName("size / isEmpty")
    class SizeAndIsEmpty {

        @Test
        @DisplayName("isEmpty transitions correctly across operations")
        void isEmptyTransitions() {
            assertThat(map.isEmpty()).isTrue();
            map.put("a", 1);
            assertThat(map.isEmpty()).isFalse();
            map.remove("a");
            assertThat(map.isEmpty()).isTrue();
        }

        @ParameterizedTest(name = "size after {0} puts")
        @ValueSource(ints = {1, 5, 16, 100})
        @DisplayName("size reflects number of unique keys")
        void sizeReflectsUniqueKeys(int n) {
            for (int i = 0; i < n; i++) {
                map.put("key-" + i, i);
            }
            assertThat(map.size()).isEqualTo(n);
        }
    }

    // ================================================================== //
    //  7. clear                                                           //
    // ================================================================== //

    @Nested
    @DisplayName("clear")
    class Clear {

        @Test
        @DisplayName("empties a populated map")
        void emptiesPopulatedMap() {
            map.put("a", 1);
            map.put("b", 2);
            map.clear();

            assertThat(map.isEmpty()).isTrue();
            assertThat(map.size()).isZero();
        }

        @Test
        @DisplayName("get returns null after clear")
        void getReturnsNullAfterClear() {
            map.put("a", 1);
            map.clear();
            assertThat(map.get("a")).isNull();
        }

        @Test
        @DisplayName("map is usable after clear")
        void mapIsUsableAfterClear() {
            map.put("a", 1);
            map.clear();
            map.put("b", 2);

            assertThat(map.size()).isEqualTo(1);
            assertThat(map.get("b")).isEqualTo(2);
        }

        @Test
        @DisplayName("clear on empty map is a no-op")
        void clearOnEmptyMapIsNoOp() {
            assertThatCode(() -> map.clear()).doesNotThrowAnyException();
            assertThat(map.isEmpty()).isTrue();
        }
    }

    // ================================================================== //
    //  8. keySet                                                          //
    // ================================================================== //

    @Nested
    @DisplayName("keySet")
    class KeySet {

        @Test
        @DisplayName("contains all inserted keys")
        void containsAllInsertedKeys() {
            map.put("a", 1);
            map.put("b", 2);
            map.put("c", 3);

            var keys = map.keySet();
            assertThat(keys.contains("a")).isTrue();
            assertThat(keys.contains("b")).isTrue();
            assertThat(keys.contains("c")).isTrue();
        }

        @Test
        @DisplayName("does not contain absent key")
        void doesNotContainAbsentKey() {
            map.put("a", 1);
            assertThat(map.keySet().contains("z")).isFalse();
        }

        @Test
        @DisplayName("size matches map size")
        void sizeMatchesMapSize() {
            map.put("a", 1);
            map.put("b", 2);
            assertThat(map.keySet().size()).isEqualTo(map.size());
        }

        @Test
        @DisplayName("keySet is a live view — reflects subsequent put")
        void liveViewReflectsPut() {
            map.put("a", 1);
            var keys = map.keySet();  // capture reference BEFORE second put

            map.put("b", 2);          // mutate map AFTER capturing

            assertThat(keys.contains("b")).isTrue();
            assertThat(keys.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("keySet is a live view — reflects subsequent remove")
        void liveViewReflectsRemove() {
            map.put("a", 1);
            map.put("b", 2);
            var keys = map.keySet();

            map.remove("a");

            assertThat(keys.contains("a")).isFalse();
            assertThat(keys.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("iterating keySet yields all keys exactly once")
        void iteratingKeySetYieldsAllKeys() {
            map.put("x", 1);
            map.put("y", 2);
            map.put("z", 3);

            List<String> collected = new ArrayList<>();
            for (String k : map.keySet()) collected.add(k);

            assertThat(collected)
                    .hasSize(3)
                    .containsExactlyInAnyOrder("x", "y", "z");
        }
    }

    // ================================================================== //
    //  9. Iteration                                                       //
    // ================================================================== //

    @Nested
    @DisplayName("Iteration")
    class Iteration {

        @Test
        @DisplayName("iterates all entries exactly once")
        void iteratesAllEntries() {
            map.put("a", 1);
            map.put("b", 2);
            map.put("c", 3);

            List<String> keys   = new ArrayList<>();
            List<Integer> values = new ArrayList<>();

            for (var entry : map) {
                keys.add(entry.key());
                values.add(entry.value());
            }

            assertThat(keys).containsExactlyInAnyOrder("a", "b", "c");
            assertThat(values).containsExactlyInAnyOrder(1, 2, 3);
        }

        @Test
        @DisplayName("each entry's key resolves to its value via get()")
        void entryKeyMatchesMapGet() {
            map.put("p", 10);
            map.put("q", 20);

            for (var entry : map) {
                assertThat(map.get(entry.key())).isEqualTo(entry.value());
            }
        }

        @Test
        @DisplayName("iterator.next() throws NoSuchElementException when exhausted")
        void nextThrowsWhenExhausted() {
            map.put("only", 1);
            var it = map.iterator();
            it.next(); // consume the single element

            assertThatThrownBy(it::next)
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    // ================================================================== //
    //  10. Collisions                                                     //
    // ================================================================== //

    @Nested
    @DisplayName("Collisions")
    class Collisions {

        // All CollidingKey instances share hashCode() == 42, so they must
        // land in the same bucket. A correct impl uses chaining or probing
        // to keep all of them alive and independently retrievable.

        private SimpleHashMap<CollidingKey, String> collidingMap;

        @BeforeEach
        void setUp() {
            collidingMap = new SimpleHashMap<>();
        }

        @Test
        @DisplayName("all colliding keys are stored and retrievable")
        void allCollidingKeysStoredAndRetrievable() {
            var k1 = new CollidingKey("one");
            var k2 = new CollidingKey("two");
            var k3 = new CollidingKey("three");

            collidingMap.put(k1, "v1");
            collidingMap.put(k2, "v2");
            collidingMap.put(k3, "v3");

            assertThat(collidingMap.get(k1)).isEqualTo("v1");
            assertThat(collidingMap.get(k2)).isEqualTo("v2");
            assertThat(collidingMap.get(k3)).isEqualTo("v3");
        }

        @Test
        @DisplayName("size is correct with all collisions")
        void sizeCorrectWithAllCollisions() {
            collidingMap.put(new CollidingKey("a"), "va");
            collidingMap.put(new CollidingKey("b"), "vb");
            collidingMap.put(new CollidingKey("c"), "vc");

            assertThat(collidingMap.size()).isEqualTo(3);
        }

        @Test
        @DisplayName("removing one colliding key does not remove siblings")
        void removingOneCollidingKeyDoesNotRemoveSiblings() {
            var k1 = new CollidingKey("x");
            var k2 = new CollidingKey("y");

            collidingMap.put(k1, "vx");
            collidingMap.put(k2, "vy");
            collidingMap.remove(k1);

            assertThat(collidingMap.containsKey(k1)).isFalse();
            assertThat(collidingMap.get(k2)).isEqualTo("vy");
        }

        @Test
        @DisplayName("update on colliding key does not affect sibling")
        void updateCollidingKeyDoesNotAffectSibling() {
            var k1 = new CollidingKey("x");
            var k2 = new CollidingKey("y");

            collidingMap.put(k1, "original");
            collidingMap.put(k2, "other");
            collidingMap.put(k1, "updated");  // overwrite k1

            assertThat(collidingMap.get(k1)).isEqualTo("updated");
            assertThat(collidingMap.get(k2)).isEqualTo("other");
        }
    }

    // ================================================================== //
    //  11. Resize / rehash                                                //
    // ================================================================== //

    @Nested
    @DisplayName("Resize / rehash")
    class ResizeAndRehash {

        // Requires: int capacity() on SimpleHashMap
        // Assumes default load factor 0.75 and initial capacity 16,
        // so rehash triggers at put #13.  Adjust if your defaults differ.

        private static final int INITIAL_CAPACITY = 16;
        private static final int TRIGGER_COUNT    = 13; // floor(16 * 0.75) + 1

        @Test
        @DisplayName("capacity doubles after exceeding load factor")
        void capacityDoublesAfterLoadFactor() {
            var sized = new SimpleHashMap<String, Integer>(INITIAL_CAPACITY);
            assertThat(sized.capacity()).isEqualTo(INITIAL_CAPACITY);

            for (int i = 0; i < TRIGGER_COUNT; i++) {
                sized.put("key-" + i, i);
            }

            assertThat(sized.capacity()).isEqualTo(INITIAL_CAPACITY * 2);
        }

        @Test
        @DisplayName("all entries survive a rehash")
        void allEntriesSurviveRehash() {
            var sized = new SimpleHashMap<String, Integer>(INITIAL_CAPACITY);

            for (int i = 0; i < TRIGGER_COUNT; i++) {
                sized.put("key-" + i, i);
            }

            // Verify every inserted entry is still present after resize
            for (int i = 0; i < TRIGGER_COUNT; i++) {
                assertThat(sized.get("key-" + i))
                        .as("key-%d should survive rehash", i)
                        .isEqualTo(i);
            }
        }

        @Test
        @DisplayName("size is unchanged after rehash")
        void sizeUnchangedAfterRehash() {
            var sized = new SimpleHashMap<String, Integer>(INITIAL_CAPACITY);

            for (int i = 0; i < TRIGGER_COUNT; i++) {
                sized.put("key-" + i, i);
            }

            assertThat(sized.size()).isEqualTo(TRIGGER_COUNT);
        }
    }

    // ================================================================== //
    //  12. Null semantics                                                 //
    // ================================================================== //

    @Nested
    @DisplayName("Null semantics")
    class NullSemantics {

        @Test
        @DisplayName("null value can be stored and retrieved")
        void nullValueStoredAndRetrieved() {
            map.put("k", null);
            assertThat(map.get("k")).isNull();
            assertThat(map.containsKey("k")).isTrue();
        }

        @Test
        @DisplayName("get returns null for absent key (same as null value)")
        void getReturnsSameNullForAbsentAndNullValue() {
            map.put("nullVal", null);
            // Both return null — containsKey is the only way to distinguish
            assertThat(map.get("nullVal")).isNull();
            assertThat(map.get("absent")).isNull();
            // But containsKey correctly distinguishes them
            assertThat(map.containsKey("nullVal")).isTrue();
            assertThat(map.containsKey("absent")).isFalse();
        }

        @Test
        @DisplayName("null key is rejected with NullPointerException")
        void nullKeyIsRejected() {
            // Standard HashMap forbids null keys; your impl should too.
            assertThatThrownBy(() -> map.put(null, 1))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> map.get(null))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> map.containsKey(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}