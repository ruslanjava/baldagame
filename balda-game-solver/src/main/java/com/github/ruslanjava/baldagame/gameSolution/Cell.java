package com.github.ruslanjava.baldagame.gameSolution;

class Cell {

    final int x;
    final int y;

    char letter;

    Cell left;
    Cell up;
    Cell right;
    Cell down;

    boolean visited;

    Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

}
