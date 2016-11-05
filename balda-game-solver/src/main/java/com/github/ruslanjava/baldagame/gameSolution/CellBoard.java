package com.github.ruslanjava.baldagame.gameSolution;

class CellBoard {

    private Cell[][] cells;

    CellBoard() {
        cells = new Cell[5][5];
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                cells[y][x] = new Cell(x, y);
            }
        }

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                Cell cell = cells[y][x];
                if (y > 0) {
                    cell.up = cells[y - 1][x];
                }
                if (x > 0) {
                    cell.left = cells[y][x - 1];
                }
                if (y < 4) {
                    cell.down = cells[y + 1][x];
                }
                if (x < 4) {
                    cell.right = cells[y][x + 1];
                }
            }
        }
    }

    void update(char[][] board) {
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                Cell cell = cells[y][x];
                cell.letter = board[y][x];
                cell.visited = false;
            }
        }
    }

    Cell get(int x, int y) {
        return cells[y][x];
    }

}
