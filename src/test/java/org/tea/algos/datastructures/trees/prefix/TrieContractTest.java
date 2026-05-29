package org.tea.algos.datastructures.trees.prefix;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Contract test for the {@link Trie} interface.
 *
 * <p>Extend this class and implement {@link #createTrie()} to verify any
 * concrete {@code Trie} implementation against the full behavioural contract.
 *
 * <pre>{@code
 * class ArrayTrieTest extends TrieContractTest {
 *     @Override
 *     protected Trie createTrie() { return new ArrayTrie(); }
 * }
 * }</pre>
 */
abstract class TrieContractTest {

    // -----------------------------------------------------------------------
    // Factory
    // -----------------------------------------------------------------------

    /** @return a fresh, empty {@link Trie} instance for each test. */
    protected abstract Trie createTrie();

    // -----------------------------------------------------------------------
    // Fixture
    // -----------------------------------------------------------------------

    private Trie trie;

    @BeforeEach
    void setUp() {
        trie = createTrie();
    }

    // -----------------------------------------------------------------------
    // insert – validation
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("insert()")
    class Insert {

        @ParameterizedTest(name = "insert(\"{0}\") throws IllegalArgumentException")
        @NullAndEmptySource
        @DisplayName("rejects null and empty words")
        void rejectsNullAndEmpty(String word) {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> trie.insert(word));
        }

        @Test
        @DisplayName("inserting a word increases size by one")
        void increasesSizeByOne() {
            trie.insert("hello");
            assertThat(trie.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("inserting the same word twice does not increase size")
        void idempotent() {
            trie.insert("hello");
            trie.insert("hello");
            assertThat(trie.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("inserting multiple distinct words increases size accordingly")
        void multipleDistinctWords() {
            trie.insert("apple");
            trie.insert("app");
            trie.insert("application");
            assertThat(trie.size()).isEqualTo(3);
        }

        @Test
        @DisplayName("inserting a single-character word is accepted")
        void singleCharacter() {
            trie.insert("a");
            assertThat(trie.contains("a")).isTrue();
        }
    }

    // -----------------------------------------------------------------------
    // contains
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("contains()")
    class Contains {

        @Test
        @DisplayName("returns false on an empty trie")
        void emptyTrie() {
            assertThat(trie.contains("anything")).isFalse();
        }

        @Test
        @DisplayName("returns true for an inserted word")
        void exactMatch() {
            trie.insert("hello");
            assertThat(trie.contains("hello")).isTrue();
        }

        @Test
        @DisplayName("returns false for a prefix that was not inserted as a complete word")
        void prefixIsNotAWord() {
            trie.insert("apple");
            assertThat(trie.contains("app")).isFalse();
        }

        @Test
        @DisplayName("returns false for a word that extends beyond an inserted word")
        void extensionIsNotContained() {
            trie.insert("app");
            assertThat(trie.contains("apple")).isFalse();
        }

        @Test
        @DisplayName("correctly distinguishes sibling paths (cat vs car)")
        void siblingPaths() {
            trie.insert("cat");
            trie.insert("car");
            assertThat(trie.contains("cat")).isTrue();
            assertThat(trie.contains("car")).isTrue();
            assertThat(trie.contains("can")).isFalse();
        }

        @Test
        @DisplayName("returns false for a word that was never inserted")
        void wordNotInserted() {
            trie.insert("world");
            assertThat(trie.contains("word")).isFalse();
        }
    }

    // -----------------------------------------------------------------------
    // startsWith
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("startsWith()")
    class StartsWith {

        @Test
        @DisplayName("returns false on an empty trie")
        void emptyTrie() {
            assertThat(trie.startsWith("a")).isFalse();
        }

        @Test
        @DisplayName("returns true for the exact inserted word used as prefix")
        void exactWordIsPrefix() {
            trie.insert("hello");
            assertThat(trie.startsWith("hello")).isTrue();
        }

        @Test
        @DisplayName("returns true for a proper prefix of an inserted word")
        void properPrefix() {
            trie.insert("apple");
            assertThat(trie.startsWith("app")).isTrue();
            assertThat(trie.startsWith("a")).isTrue();
        }

        @Test
        @DisplayName("returns false for a prefix that no word shares")
        void noMatchingPrefix() {
            trie.insert("apple");
            assertThat(trie.startsWith("b")).isFalse();
        }

        @Test
        @DisplayName("empty prefix matches any non-empty trie")
        void emptyPrefixMatchesAll() {
            trie.insert("anything");
            assertThat(trie.startsWith("")).isTrue();
        }

        @Test
        @DisplayName("returns false for a prefix longer than any stored word")
        void prefixLongerThanWord() {
            trie.insert("hi");
            assertThat(trie.startsWith("highway")).isFalse();
        }
    }

    // -----------------------------------------------------------------------
    // wordsWithPrefix
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("wordsWithPrefix()")
    class WordsWithPrefix {

        @Test
        @DisplayName("returns empty list on an empty trie")
        void emptyTrie() {
            assertThat(trie.wordsWithPrefix("a")).isEmpty();
        }

        @Test
        @DisplayName("returns the exact word when prefix equals the word")
        void prefixEqualsWord() {
            trie.insert("app");
            assertThat(trie.wordsWithPrefix("app")).containsExactlyInAnyOrder("app");
        }

        @Test
        @DisplayName("returns all words sharing a common prefix")
        void commonPrefix() {
            trie.insert("apple");
            trie.insert("app");
            trie.insert("application");
            trie.insert("banana");

            assertThat(trie.wordsWithPrefix("app"))
                    .containsExactlyInAnyOrder("apple", "app", "application");
        }

        @Test
        @DisplayName("empty prefix returns all stored words")
        void emptyPrefixReturnsAll() {
            trie.insert("alpha");
            trie.insert("beta");
            trie.insert("gamma");

            assertThat(trie.wordsWithPrefix(""))
                    .containsExactlyInAnyOrder("alpha", "beta", "gamma");
        }

        @Test
        @DisplayName("returns empty list when no word matches the prefix")
        void noMatch() {
            trie.insert("apple");
            assertThat(trie.wordsWithPrefix("z")).isEmpty();
        }

        @Test
        @DisplayName("returned list is unmodifiable")
        void returnsUnmodifiableList() {
            trie.insert("apple");
            List<String> result = trie.wordsWithPrefix("app");
            assertThatExceptionOfType(UnsupportedOperationException.class)
                    .isThrownBy(() -> result.add("injected"));
        }

        @Test
        @DisplayName("returned list is never null")
        void neverNull() {
            assertThat(trie.wordsWithPrefix("xyz")).isNotNull();
        }
    }

    // -----------------------------------------------------------------------
    // delete
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("returns false when deleting from an empty trie")
        void emptyTrie() {
            assertThat(trie.delete("ghost")).isFalse();
        }

        @Test
        @DisplayName("returns false when the word is not present")
        void wordNotPresent() {
            trie.insert("hello");
            assertThat(trie.delete("world")).isFalse();
        }

        @Test
        @DisplayName("returns true and removes the word when present")
        void deletePresentWord() {
            trie.insert("hello");
            assertThat(trie.delete("hello")).isTrue();
            assertThat(trie.contains("hello")).isFalse();
        }

        @Test
        @DisplayName("decrements size after a successful delete")
        void decrementsSize() {
            trie.insert("hello");
            trie.insert("world");
            trie.delete("hello");
            assertThat(trie.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("deleting a prefix word does not affect longer words sharing that prefix")
        void deletePrefixWordLeavesExtension() {
            trie.insert("app");
            trie.insert("apple");
            trie.delete("app");

            assertThat(trie.contains("app")).isFalse();
            assertThat(trie.contains("apple")).isTrue();
            assertThat(trie.startsWith("app")).isTrue();
        }

        @Test
        @DisplayName("deleting a longer word does not affect its prefix word")
        void deleteLongWordLeavesPrefixWord() {
            trie.insert("app");
            trie.insert("apple");
            trie.delete("apple");

            assertThat(trie.contains("apple")).isFalse();
            assertThat(trie.contains("app")).isTrue();
        }

        @Test
        @DisplayName("deleted word no longer appears in wordsWithPrefix results")
        void deletedWordAbsentFromWordsWithPrefix() {
            trie.insert("app");
            trie.insert("apple");
            trie.delete("app");

            assertThat(trie.wordsWithPrefix("app")).containsExactly("apple");
        }

        @Test
        @DisplayName("deleting the only word leaves the trie empty")
        void deleteSoleWordEmptiesTrie() {
            trie.insert("solo");
            trie.delete("solo");
            assertThat(trie.size()).isZero();
            assertThat(trie.startsWith("s")).isFalse();
        }

        @Test
        @DisplayName("deleting a word twice returns false on the second call")
        void deleteTwice() {
            trie.insert("once");
            trie.delete("once");
            assertThat(trie.delete("once")).isFalse();
        }
    }

    // -----------------------------------------------------------------------
    // size
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("size()")
    class Size {

        @Test
        @DisplayName("empty trie has size zero")
        void emptyTrieIsZero() {
            assertThat(trie.size()).isZero();
        }

        @Test
        @DisplayName("size never goes negative")
        void neverNegative() {
            trie.insert("word");
            trie.delete("word");
            trie.delete("word"); // no-op second delete
            assertThat(trie.size()).isGreaterThanOrEqualTo(0);
        }

        @Test
        @DisplayName("size reflects only unique words")
        void uniqueWordsOnly() {
            trie.insert("a");
            trie.insert("a");
            trie.insert("b");
            assertThat(trie.size()).isEqualTo(2);
        }
    }

    // -----------------------------------------------------------------------
    // Cross-cutting / integration scenarios
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Integration scenarios")
    class Integration {

        @Test
        @DisplayName("autocomplete: only words with the given prefix are returned")
        void autocomplete() {
            List.of("cat", "car", "card", "care", "careful", "dog", "dot")
                    .forEach(trie::insert);

            assertThat(trie.wordsWithPrefix("car"))
                    .containsExactlyInAnyOrder("car", "card", "care", "careful")
                    .doesNotContain("cat", "dog", "dot");
        }

        @Test
        @DisplayName("full lifecycle: insert → contains → delete → not contained")
        void fullLifecycle() {
            trie.insert("lifecycle");

            assertThat(trie.contains("lifecycle")).isTrue();
            assertThat(trie.size()).isEqualTo(1);

            trie.delete("lifecycle");

            assertThat(trie.contains("lifecycle")).isFalse();
            assertThat(trie.size()).isZero();
        }

        @Test
        @DisplayName("shared-prefix siblings remain consistent after individual deletes")
        void sharedPrefixSiblings() {
            trie.insert("be");
            trie.insert("bee");
            trie.insert("beer");
            trie.insert("been");

            trie.delete("beer");
            trie.delete("be");

            assertThat(trie.contains("be")).isFalse();
            assertThat(trie.contains("beer")).isFalse();
            assertThat(trie.contains("bee")).isTrue();
            assertThat(trie.contains("been")).isTrue();
            assertThat(trie.size()).isEqualTo(2);
        }

        @ParameterizedTest(name = "unicode word \"{0}\" round-trips correctly")
        @ValueSource(strings = {"café", "naïve", "日本語", "emoji🎉"})
        @DisplayName("handles non-ASCII and Unicode words")
        void unicodeWords(String word) {
            trie.insert(word);
            assertThat(trie.contains(word)).isTrue();
            assertThat(trie.startsWith(word.substring(0, 1))).isTrue();
        }
    }
}
