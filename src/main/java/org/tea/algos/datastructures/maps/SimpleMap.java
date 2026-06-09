package org.tea.algos.datastructures.maps;

import java.util.Iterator;

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

    SimpleMap.SimpleSet<K> keySet();

    @Override
    Iterator<Entry<K, V>> iterator();

    interface SimpleSet<E> extends Iterable<E> {
        boolean contains(E element);
        int size();
        default boolean isEmpty() { return size() == 0; }
    }

    record Entry<K, V>(K key, V value) {}
}