package com.github.ruslanjava.baldagame.cellBoard.cell;

import com.github.ruslanjava.baldagame.R;

import java.io.Serializable;

public enum CellState implements Serializable {

    NORMAL(R.color.normalCellTextTop, R.color.normalCellTextBottom, R.drawable.normal_cell_background),
    SELECTED(R.color.selectedCellTextTop, R.color.selectedCellTextBottom, R.drawable.selected_cell_background),

    NEW(R.color.newCellTextTop, R.color.newCellTextBottom, R.drawable.normal_cell_background),
    NEW_SELECTED(R.color.newCellTextTop, R.color.newCellTextBottom, R.drawable.selected_cell_background);

    final int topColorId;
    final int bottomColorId;
    final int backgroundId;

    CellState(int topColorId, int bottomColorId, int backgroundId) {
        this.topColorId = topColorId;
        this.bottomColorId = bottomColorId;
        this.backgroundId = backgroundId;
    }

}
