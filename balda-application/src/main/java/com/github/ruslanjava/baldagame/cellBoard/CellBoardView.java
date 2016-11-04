package com.github.ruslanjava.baldagame.cellBoard;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;

import icepick.Icepick;
import icepick.State;

import com.github.ruslanjava.baldagame.R;
import com.github.ruslanjava.baldagame.cellBoard.cell.CellState;
import com.github.ruslanjava.baldagame.cellBoard.cell.CellView;
import com.github.ruslanjava.baldagame.cellBoard.keyboard.KeyboardDialog;
import com.github.ruslanjava.baldagame.cellBoard.keyboard.KeyboardDialogListener;
import com.github.ruslanjava.baldagame.cellBoard.keyboard.KeyboardType;
import com.github.ruslanjava.baldagame.cellBoard.arrows.ArrowsView;

public class CellBoardView extends FrameLayout {

    private static final int[][] IDS = {
            {R.id.cell00, R.id.cell10, R.id.cell20, R.id.cell30, R.id.cell40},
            {R.id.cell01, R.id.cell11, R.id.cell21, R.id.cell31, R.id.cell41},
            {R.id.cell02, R.id.cell12, R.id.cell22, R.id.cell32, R.id.cell42},
            {R.id.cell03, R.id.cell13, R.id.cell23, R.id.cell33, R.id.cell43},
            {R.id.cell04, R.id.cell14, R.id.cell24, R.id.cell34, R.id.cell44},
    };

    @State
    int[] dialogLetterXY;

    @State
    KeyboardType keyboardType;

    @State
    BoardState boardState;

    @State
    ArrayList<Cell> cellPath;

    private KeyboardDialog dialog;

    private ArrowsView arrowsView;

    private CellView[][] cellViews;

    public CellBoardView(Context context) {
        super(context);
        init(context, null);
    }

    public CellBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CellBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setWillNotDraw(false);

        LayoutInflater inflater = LayoutInflater.from(context);
        View boardLayout = inflater.inflate(R.layout.cell_board_view, null);
        addView(boardLayout);

        arrowsView = new ArrowsView(context);
        cellPath = new ArrayList<>();
        arrowsView.setCellPath(cellPath);

        cellViews = new CellView[5][5];
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                CellView cellView = (CellView) boardLayout.findViewById(IDS[y][x]);
                cellView.setLetter(' ');
                cellView.setOnClickListener(new LetterViewListener(x, y));
                cellViews[y][x] = cellView;
            }
        }
        keyboardType = KeyboardType.RUSSIAN;
        boardState = BoardState.IDLE;
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.save();
        arrowsView.draw(canvas, super.getWidth(), super.getHeight());
        canvas.restore();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        if (dialog != null) {
            dialog.dismiss();
        }
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable restoredState) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, restoredState));
        arrowsView.setCellPath(cellPath);
        if (boardState == BoardState.NEW_LETTER_DIALOG) {
            showKeyboardDialog(dialogLetterXY[0], dialogLetterXY[1]);
        }
    }

    public char getLetter(int x, int y) {
        return cellViews[y][x].getLetter();
    }

    public void setLetter(int x, int y, char letter) {
        CellView cellView = cellViews[y][x];
        cellView.setLetter(letter);
        cellView.setState(CellState.NORMAL);
        cellView.invalidate();
    }

    public void setInitialWord(String word) {
        clear();
        for (int i = 0; i < 5; i++) {
            setLetter(i, 2, word.charAt(i));
        }
    }

    public void clear() {
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                cellViews[y][x].setLetter(' ');
            }
        }
        invalidate();
    }

    private void showKeyboardDialog(final int x, final int y) {
        dialogLetterXY = new int[]{x, y};
        boardState = BoardState.NEW_LETTER_DIALOG;
        dialog = new KeyboardDialog(getContext(), keyboardType, new KeyboardDialogListener() {
            @Override
            public void onLetterSelected(char letter) {
                CellView cellView = cellViews[y][x];
                cellView.setState(CellState.NEW);
                cellView.setLetter(letter);
                cellView.invalidate();
                boardState = BoardState.PATH;
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                boardState = BoardState.IDLE;
            }
        });
        dialog.show();
    }

    private void addCellToPath(int x, int y) {
        CellState state = cellViews[y][x].getState();

        Cell lastCell = cellPath.isEmpty() ? null : cellPath.get(cellPath.size() - 1);
        Cell cell = new Cell(x, y);

        // простой случай A - ячейка просто продолжает слово
        if (isNextCell(cell)) {
            if (state == CellState.NEW) {
                cellViews[y][x].setState(CellState.NEW_SELECTED);
            } else {
                cellViews[y][x].setState(CellState.SELECTED);
            }
            cellPath.add(cell);
            invalidate();
            return;
        }

        // простой случай B - щелкнули по той же самой букве цепочки
        if (cellPath.size() > 1 && cell.equals(lastCell)) {
            onWordSelected();
            return;
        }

        // сложный случай - слово прервали
        int oldPathLength = cellPath.size();
        clearSelection();

        state = cellViews[y][x].getState();
        if (state == CellState.NORMAL) {
            cellViews[y][x].setState(CellState.SELECTED);
        } else if (state == CellState.NEW) {
            if (oldPathLength == 1) {
                cellViews[y][x].setLetter(' ');
                boardState = BoardState.IDLE;
                invalidate();
                return;
            }
            cellViews[y][x].setState(CellState.NEW_SELECTED);
        }
        cellPath.add(new Cell(x, y));

        invalidate();
    }

    private boolean isNextCell(Cell cell) {
        if (cellPath.isEmpty()) {
            return true;
        }
        if (cellPath.contains(cell)) {
            return false;
        }
        Cell lastCell = cellPath.get(cellPath.size() - 1);
        return lastCell.isNeighbour(cell);
    }

    private void clearSelection() {
        for (Cell cell : cellPath) {
            CellView cellView = cellViews[cell.y][cell.x];
            CellState state = cellView.getState();

            if (state == CellState.SELECTED) {
                cellView.setState(CellState.NORMAL);
            } else if (state == CellState.NEW_SELECTED) {
                cellView.setState(CellState.NEW);
            }
        }
        cellPath.clear();
    }

    private void onWordSelected() {
    }

    private class LetterViewListener implements View.OnClickListener {

        private int x;
        private int y;

        public LetterViewListener(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void onClick(View view) {
            CellView cellView = (CellView) view;
            char letter = cellView.getLetter();

            switch (boardState) {
                case IDLE:
                    if (letter == ' ' && hasNonEmptyNeighbour(x, y)) {
                        showKeyboardDialog(x, y);
                    }
                    return;
                case NEW_LETTER_DIALOG:
                    throw new IllegalStateException("Unable to intercept clicks behind dialog");
                case PATH:
                    if (letter != ' ') {
                        addCellToPath(x, y);
                    }
                    return;
            }

            cellView.invalidate();
        }

        private boolean hasNonEmptyNeighbour(int x, int y) {
            if (x > 0 && getLetter(x - 1, y) != ' ') {
                return true;
            }
            if (x < 4 && getLetter(x + 1, y) != ' ') {
                return true;
            }
            if (y > 0 && getLetter(x, y - 1) != ' ') {
                return true;
            }
            if (y < 4 && getLetter(x, y + 1) != ' ') {
                return true;
            }
            return false;
        }

    }

}
