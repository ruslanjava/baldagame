package com.github.ruslanjava.baldagame.cellBoard;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.StringBuilderPrinter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import icepick.Icepick;
import icepick.State;

import com.github.ruslanjava.baldagame.R;
import com.github.ruslanjava.baldagame.cellBoard.cell.CellState;
import com.github.ruslanjava.baldagame.cellBoard.cell.CellView;
import com.github.ruslanjava.baldagame.cellBoard.confirmWordDialog.ConfirmWordDialog;
import com.github.ruslanjava.baldagame.cellBoard.keyboardDialog.KeyboardDialog;
import com.github.ruslanjava.baldagame.cellBoard.keyboardDialog.OnLetterSelectedListener;
import com.github.ruslanjava.baldagame.cellBoard.keyboardDialog.KeyboardType;
import com.github.ruslanjava.baldagame.cellBoard.arrows.ArrowsView;

import java.util.List;

public class CellBoardView extends FrameLayout {

    private static final int[][] IDS = {
            {R.id.cell00, R.id.cell10, R.id.cell20, R.id.cell30, R.id.cell40},
            {R.id.cell01, R.id.cell11, R.id.cell21, R.id.cell31, R.id.cell41},
            {R.id.cell02, R.id.cell12, R.id.cell22, R.id.cell32, R.id.cell42},
            {R.id.cell03, R.id.cell13, R.id.cell23, R.id.cell33, R.id.cell43},
            {R.id.cell04, R.id.cell14, R.id.cell24, R.id.cell34, R.id.cell44},
    };

    @State
    int dialogLetterX;
    @State
    int dialogLetterY;

    @State
    KeyboardType keyboardType;

    @State
    BoardState boardState;

    @State
    CellPath cellPath;

    private KeyboardDialog keyboardDialog;
    private ConfirmWordDialog confirmWordDialog;

    private ArrowsView arrowsView;

    private CellView[][] cellViews;

    private OnWordEnteredListener listener;

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
        cellPath = new CellPath();
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

    public void setOnWordEnteredListener(OnWordEnteredListener listener) {
        this.listener = listener;
    }

    public void addWord() {
        clearSelection();
        cellViews[dialogLetterY][dialogLetterX].setState(CellState.NORMAL);
        boardState = BoardState.IDLE;
        invalidate();
    }

    public void cancelWord() {
        clearSelection();
        cellViews[dialogLetterY][dialogLetterX].setState(CellState.NORMAL);
        cellViews[dialogLetterY][dialogLetterX].setLetter(' ');
        boardState = BoardState.IDLE;
        invalidate();
    }

    public boolean hasInitialWord() {
        return cellViews[2][0].getLetter() != ' ';
    }

    public void setInitialWord(String word) {
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                CellView cellView = cellViews[y][x];
                cellView.setState(CellState.NORMAL);
                cellView.setLetter(y == 2 ? Character.toUpperCase(word.charAt(x)) : ' ');
                cellView.invalidate();
            }
        }
        invalidate();
    }

    public char[][] getBoard() {
        char[][] result = new char[5][5];
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                result[y][x] = Character.toLowerCase(cellViews[y][x].getLetter());
            }
        }
        return result;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        if (keyboardDialog != null) {
            keyboardDialog.dismiss();
        }
        if (confirmWordDialog != null) {
            confirmWordDialog.dismiss();
        }
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable restoredState) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, restoredState));
        arrowsView.setCellPath(cellPath);
        if (boardState == BoardState.NEW_LETTER_DIALOG) {
            showKeyboardDialog(dialogLetterX, dialogLetterY);
        } else if (boardState == BoardState.CONFIRM_WORD_DIALOG) {
            showConfirmWordDialog();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.save();
        arrowsView.draw(canvas, super.getWidth(), super.getHeight());
        canvas.restore();
    }

    private void showKeyboardDialog(final int x, final int y) {
        dialogLetterX = x;
        dialogLetterY = y;
        boardState = BoardState.NEW_LETTER_DIALOG;
        keyboardDialog = new KeyboardDialog(getContext(), keyboardType);
        keyboardDialog.setOnLetterSelectedListener(new OnLetterSelectedListener() {
            @Override
            public void onLetterSelected(char letter) {
                CellView cellView = cellViews[y][x];
                cellView.setState(CellState.NEW);
                cellView.setLetter(letter);
                cellView.invalidate();
                boardState = BoardState.PATH;
            }
        });
        keyboardDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                boardState = BoardState.IDLE;
            }
        });
        keyboardDialog.show();
    }

    private void showConfirmWordDialog() {
        boardState = BoardState.CONFIRM_WORD_DIALOG;

        final String word = getWord();
        confirmWordDialog = new ConfirmWordDialog(getContext(), word);
        confirmWordDialog.setOkButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onWordEntered(word);
                }
            }
        });
        confirmWordDialog.setCancelButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelWord();
            }
        });
        confirmWordDialog.show();
    }

    private void addCellToPath(int x, int y) {
        CellState state = cellViews[y][x].getState();

        Cell lastCell = cellPath.getLastCell();
        Cell cell = new Cell(x, y);

        // простой случай A - ячейка просто продолжает слово
        if (cellPath.isNextCell(cell)) {
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
            if (cellViews[dialogLetterY][dialogLetterX].getState() == CellState.NEW_SELECTED) {
                showConfirmWordDialog();
            }
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
        arrowsView.setCellPath(cellPath);
    }

    private String getWord() {
        StringBuilder builder = new StringBuilder();
        for (Cell cell : cellPath) {
            char letter = cellViews[cell.y][cell.x].getLetter();
            builder.append(letter);
        }
        return builder.toString();
    }

    public void addComputerMove(int x, int y, char letter, List<int[]> path) {
        boardState = BoardState.COMPUTER_MOVE;
        cellViews[y][x].setState(CellState.NEW_SELECTED);
        cellViews[y][x].setLetter(Character.toUpperCase(letter));
        for (int[] xy : path) {
            cellPath.add(new Cell(xy[0], xy[1]));
        }
    }

    private class LetterViewListener implements View.OnClickListener {

        private int x;
        private int y;

        LetterViewListener(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void onClick(View view) {
            CellView cellView = (CellView) view;
            char letter = cellView.getLetter();

            switch (boardState) {
                case COMPUTER_MOVE:
                    return;
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

        private char getLetter(int x, int y) {
            return cellViews[y][x].getLetter();
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
