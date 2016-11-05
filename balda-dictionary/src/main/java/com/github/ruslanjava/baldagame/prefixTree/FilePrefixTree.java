package com.github.ruslanjava.baldagame.prefixTree;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

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

    public void printNodes() {
        printNodes(root);
    }

    public void close() {
        try {
            file.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void printNodes(FilePrefixTreeNode node) {
        if (node.hasValue()) {
            System.out.println(node.toString());
        }
        char[] letters = node.getChildLetters();
        for (char letter : letters) {
            FilePrefixTreeNode child = node.getChild(letter);
            printNodes(child);
        }
    }

}
