package com.github.ruslanjava.baldagame.game.gameBoard;

public class Cell {

    public int x;
    public int y;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Cell) {
            Cell cell = (Cell) o;
            return x == cell.x && y == cell.y;
        }
        return false;
    }

    public boolean isNeighbour(Cell cell) {
        return x == cell.x && Math.abs(y - cell.y) == 1 ||
                y == cell.y && Math.abs(x - cell.x) == 1;
    }

}
