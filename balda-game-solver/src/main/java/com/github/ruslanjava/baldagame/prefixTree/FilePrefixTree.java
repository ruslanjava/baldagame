package com.github.ruslanjava.baldagame.prefixTree;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

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

    public Observable<String> getRandomFiveLetterWord() {
        return Observable.fromCallable(
                new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        List<String> words = getFiveLetterWords().toList().blockingGet();
                        int size = words.size();
                        Random random = new Random(System.currentTimeMillis());
                        int randomIndex = random.nextInt(size);
                        return words.get(randomIndex);
                    }
                }
        );
    }

    public Observable<String> getFiveLetterWords() {
        return Observable.create(
                new ObservableOnSubscribe<String>() {

                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) {
                        addFiveLetterWords(emitter, root, 0);
                        emitter.onComplete();
                    }
                }
        );
    }

    public void close() {
        try {
            file.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void addFiveLetterWords(ObservableEmitter<String> emitter,
                                    FilePrefixTreeNode node, int level) {
        if (level < 5) {
            char[] letters = node.getChildLetters();
            for (char letter : letters) {
                FilePrefixTreeNode child = node.getChild(letter);
                addFiveLetterWords(emitter, child, level + 1);
            }
            return;
        }
        if (node.hasValue()) {
            emitter.onNext(node.toString());
        }
    }

    public boolean containsWord(String word) {
        FilePrefixTreeNode node = root;
        for (int i = 0; i < word.length(); i++) {
            char letter = word.charAt(i);
            FilePrefixTreeNode child = node.getChild(letter);
            if (child == null) {
                return false;
            }
            node = child;
        }
        return node.hasValue();
    }

}
