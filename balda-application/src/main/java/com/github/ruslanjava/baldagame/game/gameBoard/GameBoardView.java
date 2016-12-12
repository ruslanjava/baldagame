package com.github.ruslanjava.baldagame.game.gameBoard;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import com.github.ruslanjava.baldagame.cellBoard.CellBoardView;
import com.github.ruslanjava.baldagame.cellBoard.CellState;
import com.github.ruslanjava.baldagame.game.gameBoard.arrows.ArrowsView;
import com.github.ruslanjava.baldagame.game.gameBoard.keyboardDialog.KeyboardDialog;
import com.github.ruslanjava.baldagame.game.gameBoard.keyboardDialog.KeyboardType;
import com.github.ruslanjava.baldagame.game.gameBoard.keyboardDialog.OnLetterSelectedListener;

import java.util.List;

import clojure.lang.Obj;
import icepick.Icepick;
import icepick.State;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;

public class GameBoardView extends CellBoardView {

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

    private ArrowsView arrowsView;

    private ReplaySubject<String> wordSubject;

    public GameBoardView(Context context) {
        super(context);
        init(context, null);
    }

    public GameBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GameBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                setOnClickListener(x, y, new CellViewListener(x, y));
            }
        }

        arrowsView = new ArrowsView(context);
        cellPath = new CellPath();
        arrowsView.setCellPath(cellPath);

        keyboardType = KeyboardType.RUSSIAN;
        boardState = BoardState.IDLE;

        wordSubject = ReplaySubject.create();
    }

    public Observable<String> getSelectedWordObservable() {
        return wordSubject;
    }

    public String getWord() {
        StringBuilder builder = new StringBuilder();
        for (Cell cell : cellPath) {
            char letter = getLetter(cell.x, cell.y);
            builder.append(letter);
        }
        return builder.toString();
    }

    public void cancelWord() {
        if (boardState != BoardState.COMPUTER_MOVE) {
            clearSelection();
            setState(dialogLetterX, dialogLetterY, CellState.NORMAL);
            setLetter(dialogLetterX, dialogLetterY, ' ');
            boardState = BoardState.IDLE;
            invalidate();
        }
    }

    public void addHumanWord() {
        if (boardState != BoardState.COMPUTER_MOVE) {
            addWord();
        }
    }

    public void addComputerWord() {
        if (boardState == BoardState.COMPUTER_MOVE) {
            addWord();
        }
    }

    public void addComputerMove(int x, int y, char letter, List<int[]> path) {
        boardState = BoardState.COMPUTER_MOVE;
        setState(x, y, CellState.NEW_SELECTED);
        setLetter(x, y, Character.toUpperCase(letter));
        for (int[] xy : path) {
            int pathX = xy[0];
            int pathY = xy[1];
            cellPath.add(new Cell(pathX, pathY));
            setState(pathX, pathY, CellState.SELECTED);
        }
        invalidate();
    }

    public boolean hasInitialWord() {
        return getLetter(0, 2) != ' ';
    }

    public char[][] getBoard() {
        char[][] result = new char[5][5];
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                result[y][x] = Character.toLowerCase(getLetter(x, y));
            }
        }
        return result;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        if (keyboardDialog != null) {
            keyboardDialog.dismiss();
        }
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable restoredState) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, restoredState));
        arrowsView.setCellPath(cellPath);
        if (boardState == BoardState.NEW_LETTER_DIALOG) {
            showKeyboardDialog(dialogLetterX, dialogLetterY);
        }

        String word = getWord();
        wordSubject.onNext(word);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.save();
        arrowsView.draw(canvas, super.getWidth(), super.getHeight());
        canvas.restore();
    }

    private void addWord() {
        clearSelection();
        setState(dialogLetterX, dialogLetterY, CellState.NORMAL);
        boardState = BoardState.IDLE;
        invalidate();
    }

    private void showKeyboardDialog(final int x, final int y) {
        dialogLetterX = x;
        dialogLetterY = y;
        boardState = BoardState.NEW_LETTER_DIALOG;
        keyboardDialog = new KeyboardDialog(getContext(), keyboardType);
        keyboardDialog.setOnLetterSelectedListener(new OnLetterSelectedListener() {
            @Override
            public void onLetterSelected(char letter) {
                setLetter(dialogLetterX, dialogLetterY, letter);
                setState(dialogLetterX, dialogLetterY, CellState.NEW);
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

    private void addCellToPath(int x, int y) {
        CellState state = getState(x, y);

        Cell cell = new Cell(x, y);

        if (cellPath.isNextCell(cell)) {

            // простой случай - ячейка просто продолжает слово
            if (state == CellState.NEW) {
                setState(x, y, CellState.NEW_SELECTED);
            } else {
                setState(x, y, CellState.SELECTED);
            }
            cellPath.add(cell);

        } else {

            // сложный случай - слово прервали
            clearSelection();

            state = getState(x, y);
            if (state == CellState.NORMAL) {
                setState(x, y, CellState.SELECTED);
            } else if (state == CellState.NEW) {
                setState(x, y, CellState.NEW_SELECTED);
            }
            cellPath.add(new Cell(x, y));

        }

        invalidate();

        String selectedWord = getWord();
        wordSubject.onNext(selectedWord);
    }

    private void clearSelection() {
        for (Cell cell : cellPath) {
            CellState state = getState(cell.x, cell.y);

            if (state == CellState.SELECTED) {
                setState(cell.x, cell.y, CellState.NORMAL);
            } else if (state == CellState.NEW_SELECTED) {
                setState(cell.x, cell.y, CellState.NEW);
            }
        }
        cellPath.clear();
        arrowsView.setCellPath(cellPath);
        wordSubject.onNext("");
    }

    private class CellViewListener implements View.OnClickListener {

        private int x;
        private int y;

        CellViewListener(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void onClick(View view) {
            char letter = getLetter(x, y);

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
