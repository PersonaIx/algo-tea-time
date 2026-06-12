package org.tea.datastructures.trees.prefix;

public class ArrayTrieTest extends TrieContractTest {
    @Override
    protected Trie createTrie() {
        return new ArrayTrie();
    }
}
