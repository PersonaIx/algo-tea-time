package org.tea.algos.datastructures.trees.prefix;

import java.util.List;

/**
 * An educational Trie (prefix tree) interface.
 *
 * <p>A Trie stores strings character-by-character, where each node represents
 * one character and shares prefixes across all stored words. This makes prefix
 * lookups extremely efficient — O(L) where L is the string length, independent
 * of how many strings are stored.
 *
 * <p>Null keys are not permitted.
 */
public interface Trie {

    /**
     * Inserts a word into the trie.
     *
     * @param word the word to insert; must not be null or empty
     * @throws IllegalArgumentException if word is null or empty
     */
    void insert(String word);

    /**
     * Returns true if the trie contains the given word as a complete entry.
     *
     * <p>A trie may contain the prefix "app" without containing the word "app"
     * itself — this method only returns true for complete words.
     *
     * @param word the word to look up
     * @return true if the exact word was previously inserted
     */
    boolean contains(String word);

    /**
     * Returns true if any word in the trie begins with the given prefix.
     *
     * <p>This is the core operation that makes tries useful — it is no more
     * expensive than {@code contains} but is far more powerful for autocomplete
     * and spell-checking scenarios.
     *
     * @param prefix the prefix to search for
     * @return true if at least one stored word starts with {@code prefix}
     */
    boolean startsWith(String prefix);

    /**
     * Returns all words in the trie that begin with the given prefix.
     *
     * <p>If {@code prefix} is an empty string, all words are returned.
     *
     * @param prefix the prefix to match
     * @return an unmodifiable list of matching words, in no guaranteed order;
     *         never null, but may be empty
     */
    List<String> wordsWithPrefix(String prefix);

    /**
     * Removes a word from the trie if it is present.
     *
     * <p>Removing a word must not affect other words that share its prefix.
     * For example, removing "app" must not affect "apple".
     *
     * @param word the word to remove
     * @return true if the word was present and removed; false if not found
     */
    boolean delete(String word);

    /**
     * Returns the number of complete words stored in the trie.
     *
     * @return the word count; always ≥ 0
     */
    int size();
}