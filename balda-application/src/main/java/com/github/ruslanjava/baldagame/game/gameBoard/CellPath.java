package com.github.ruslanjava.baldagame.game.gameBoard;

import java.util.ArrayList;

class CellPath extends ArrayList<Cell> {

    Cell getLastCell() {
        return isEmpty() ? null : get(size() - 1);
    }

    boolean isNextCell(Cell cell) {
        if (isEmpty()) {
            return true;
        }
        if (contains(cell)) {
            return false;
        }
        Cell lastCell = get(size() - 1);
        return lastCell.isNeighbour(cell);
    }

}
