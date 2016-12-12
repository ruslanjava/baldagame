package com.github.ruslanjava.baldagame.cellBoard;


import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.github.ruslanjava.baldagame.R;

import icepick.Icepick;

public class CellBoardView extends FrameLayout {

    private static final int[][] IDS = {
            {R.id.cell00, R.id.cell10, R.id.cell20, R.id.cell30, R.id.cell40},
            {R.id.cell01, R.id.cell11, R.id.cell21, R.id.cell31, R.id.cell41},
            {R.id.cell02, R.id.cell12, R.id.cell22, R.id.cell32, R.id.cell42},
            {R.id.cell03, R.id.cell13, R.id.cell23, R.id.cell33, R.id.cell43},
            {R.id.cell04, R.id.cell14, R.id.cell24, R.id.cell34, R.id.cell44},
    };

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

        cellViews = new CellView[5][5];
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                CellView cellView = (CellView) boardLayout.findViewById(IDS[y][x]);
                cellView.setLetter(' ');
                cellViews[y][x] = cellView;
            }
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable restoredState) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, restoredState));
    }

    public void setInitialWord(String word) {
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                setState(x, y, CellState.NORMAL);
                setLetter(x, y, y == 2 ? Character.toUpperCase(word.charAt(x)) : ' ');
            }
        }
        // обновляем полностью доску, чтобы стереть путь
        invalidate();
    }

    protected char getLetter(int x, int y) {
        return cellViews[y][x].getLetter();
    }

    protected void setLetter(int x, int y, char letter) {
        CellView cellView = cellViews[y][x];
        cellView.setLetter(letter);
        cellView.invalidate();
    }

    protected CellState getState(int x, int y) {
        return cellViews[y][x].getState();
    }

    protected void setState(int x, int y, CellState state) {
        CellView cellView = cellViews[y][x];
        cellView.setState(state);
        cellView.invalidate();
    }

    protected void setOnClickListener(int x, int y, View.OnClickListener listener) {
        cellViews[y][x].setOnClickListener(listener);
    }

}
