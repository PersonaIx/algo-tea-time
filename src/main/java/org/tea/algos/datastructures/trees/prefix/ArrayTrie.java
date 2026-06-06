package org.tea.algos.datastructures.trees.prefix;

import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArrayTrie implements Trie {

    private final TrieNode root;
    private int size;

    public ArrayTrie() {
        root = new TrieNode();
    }

    @Override
    public void insert(String word) {
        if (ObjectUtils.isEmpty(word)) {
            throw new IllegalArgumentException();
        }
        TrieNode currentNode = root;
        for (char character : word.toCharArray()) {
            int index = character - 'a'; // a - a is 0
            if (currentNode.children[index] == null) {
                currentNode.children[index] = new TrieNode();
            }
            currentNode = currentNode.children[index];
        }
        if (!currentNode.isWord) {
            size++;
        }
        currentNode.isWord = true;
    }

    @Override
    public boolean contains(String word) {
        TrieNode currentNode = root;
        for (char character : word.toCharArray()) {
            int index = character - 'a';
            if (currentNode.children[index] == null) {
                return false;
            }
            currentNode = currentNode.children[index];
        }
        if (currentNode.isWord) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean startsWith(String prefix) {
        TrieNode currentNode = root;
        for (char character : prefix.toCharArray()) {
            int index = character - 'a';
            if (currentNode.children[index] == null) {
                return false;
            }
            currentNode = currentNode.children[index];
        }
        return true;
    }

    @Override
    public List<String> wordsWithPrefix(String prefix) {
        TrieNode currentNode = root;
        List<String> result = new ArrayList<>();
        for (char character : prefix.toCharArray()) {
            int index = character - 'a';
            if (currentNode.children[index] == null) {
                return Collections.emptyList();
            }
            currentNode = currentNode.children[index];
        }

        findWords(currentNode, new StringBuilder(prefix), result);

        return Collections.unmodifiableList(result);
    }

    private void findWords(TrieNode currentNode, StringBuilder prefix, List<String> result) {
        if (currentNode.isWord) {
            result.add(prefix.toString());
        }

        for (int i = 0; i < currentNode.children.length; i++) {
            if (currentNode.children[i] != null) {
                prefix.append((char) ('a' + i));
                findWords(currentNode.children[i], prefix, result);
                // restore for the next iteration
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }
    }

    @Override
    public boolean delete(String word) {
        if (!contains(word)) {
            return false;
        }
        delete(root, word);
        size--;
        return true;
    }

    private boolean delete(TrieNode current, String remaining) {
        if (remaining.isEmpty()) {
            current.isWord = false;
            return !hasChildren(current);
        }

        char firstChar = remaining.charAt(0);
        int arrayIndex = firstChar - 'a';
        TrieNode child = current.children[arrayIndex];
        boolean deleted = delete(child, remaining.substring(1));
        if (deleted) {
            current.children[arrayIndex] = null;
            // do not delete if it is an existing word or part of an existing word
            return !current.isWord && !hasChildren(current);
        } else {
            return false;
        }
    }

    private boolean hasChildren(TrieNode current) {
        TrieNode[] children = current.children;
        for (TrieNode child : children) {
            if (child != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }
}
