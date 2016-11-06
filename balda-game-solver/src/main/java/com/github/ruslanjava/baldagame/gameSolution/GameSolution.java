package com.github.ruslanjava.baldagame.gameSolution;

import java.util.List;

public class GameSolution implements Comparable<GameSolution> {

    private int newLetterX;
    private int newLetterY;
    private char newLetter;

    private List<int[]> path;
    private String word;

    public int getNewLetterX() {
        return newLetterX;
    }

    public int getNewLetterY() {
        return newLetterY;
    }

    void setNewLetterPosition(int newLetterX, int newLetterY) {
        this.newLetterX = newLetterX;
        this.newLetterY = newLetterY;
    }

    public char getNewLetter() {
        return newLetter;
    }

    void setNewLetter(char newLetter) {
        this.newLetter = newLetter;
    }

    public List<int[]> getPath() {
        return path;
    }

    void setPath(List<int[]> path) {
        this.path = path;
    }

    public String getWord() {
        return word;
    }

    void setWord(String word) {
        this.word = word;
    }

    @Override
    public String toString() {
        return word;
    }

    @Override
    public int compareTo(GameSolution decision) {
        if (word.length() > decision.word.length()) {
            return -1;
        }
        if (word.length() < decision.word.length()) {
            return 1;
        }
        return word.compareTo(decision.word);
    }

}
