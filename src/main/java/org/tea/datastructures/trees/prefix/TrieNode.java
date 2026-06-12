package org.tea.datastructures.trees.prefix;

public class TrieNode {
    TrieNode[] children = new TrieNode[26];
    boolean isWord;
}
