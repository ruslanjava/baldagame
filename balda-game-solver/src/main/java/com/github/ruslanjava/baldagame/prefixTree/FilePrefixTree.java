package com.github.ruslanjava.baldagame.prefixTree;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Префиксное дерево, использующее RandomAccessFile для чтения отдельных узлов.
 * 
 * @author Руслан Щучинов
 */
public final class FilePrefixTree {
    
    private final FilePrefixTreeNode root;
    private final RandomAccessFile file;

    public FilePrefixTree(String fileName) {
        try {
            file = new RandomAccessFile(new File(fileName), "r");
            root = new FilePrefixTreeNode(file, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FilePrefixTreeNode getRoot() {
        return root;
    }

    public String getRandomFiveLetterWord() {
        List<String> words = getFiveLetterWords();
        Collections.shuffle(words);
        return words.get(0);
    }

    public List<String> getFiveLetterWords() {
        List<String> result = new ArrayList<>();
        addFiveLetterWords(result, root, 0);
        return result;
    }

    public void close() {
        try {
            file.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void addFiveLetterWords(List<String> result, FilePrefixTreeNode node, int level) {
        if (level < 5) {
            char[] letters = node.getChildLetters();
            for (char letter : letters) {
                FilePrefixTreeNode child = node.getChild(letter);
                addFiveLetterWords(result, child, level + 1);
            }
            return;
        }
        if (node.hasValue()) {
            result.add(node.toString());
        }
    }

}
