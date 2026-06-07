package org.tea.algos.datastructures.maps;

import java.util.Iterator;
import java.util.Set;

/**
 * A minimal, educational map interface.
 *
 * Design goals:
 *  - Small enough to implement from scratch in one sitting
 *  - Large enough to be genuinely useful and test real understanding
 *  - Extensible: a TreeMap, LinkedHashMap, etc. can implement this too
 *
 * Intentional omissions (add later as exercises):
 *  - putAll(), entrySet(), values(), putIfAbsent(), compute*, merge()
 *  - equals() / hashCode() contracts (discuss separately)
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public interface SimpleMap<K, V> extends Iterable<SimpleMap.Entry<K, V>> {

    V put(K key, V value);

    V get(K key);

    default V getOrDefault(K key, V defaultValue) {
        V value = get(key);
        return (value != null || containsKey(key)) ? value : defaultValue;
    }

    V remove(K key);

    boolean containsKey(K key);

    int size();

    default boolean isEmpty() {
        return size() == 0;
    }

    void clear();

    Set<K> keySet();

    @Override
    Iterator<Entry<K, V>> iterator();

    sealed interface Entry<K, V> permits SimpleMap.MutableEntry, SimpleMap.ImmutableEntry {
        K key();
        V value();
    }

    final class MutableEntry<K, V> implements Entry<K, V> {
        private final K key;
        private V value;

        public MutableEntry(K key, V value) {
            this.key   = key;
            this.value = value;
        }

        @Override public K key()   { return key; }
        @Override public V value() { return value; }

        public void setValue(V newValue) { this.value = newValue; }

        @Override
        public String toString() { return key + "=" + value; }
    }

    record ImmutableEntry<K, V>(K key, V value) implements Entry<K, V> {}
}