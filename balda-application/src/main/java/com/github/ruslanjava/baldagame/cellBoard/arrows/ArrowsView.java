package com.github.ruslanjava.baldagame.cellBoard.arrows;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import com.github.ruslanjava.baldagame.cellBoard.Cell;

public class ArrowsView extends FrameLayout {

    private Paint paint;
    private Paint textPaint;

    private List<Cell> path;

    public ArrowsView(Context context) {
        super(context);
        init(context);
    }

    public ArrowsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ArrowsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        path = new ArrayList<>();

        paint = new Paint();
        paint.setColor(0x33FFFFFF);
        paint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(0x220000FF);
        textPaint.setStrokeWidth(6.0f);
        textPaint.setStyle(Paint.Style.STROKE);
    }

    public List<Cell> getCellPath() {
        return path;
    }

    public void setCellPath(List<Cell> path) {
        this.path = path;
    }

    public void draw(Canvas canvas, int width, int height) {
        if (path.isEmpty()) {
            return;
        }

        int cellWidth = width / 5;
        int cellHeight = height / 5;

        for (int i = 0; i < path.size() - 1; i++) {
            Cell a = path.get(i);
            Cell b = path.get(i + 1);

            int x1 = Math.min(a.x, b.x) * cellWidth;
            int x2 = (Math.max(a.x, b.x) + 1) * cellWidth;

            int y1 = Math.min(a.y, b.y) * cellHeight;
            int y2 = (Math.max(a.y, b.y) + 1) * cellHeight;

            if (a.x < b.x) {
                Arrow.RIGHT.draw(canvas, x1, y1, x2, y2);
            } else if (a.x > b.x) {
                Arrow.LEFT.draw(canvas, x1, y1, x2, y2);
            } else if (a.y < b.y) {
                Arrow.DOWN.draw(canvas, x1, y1, x2, y2);
            } else {
                Arrow.UP.draw(canvas, x1, y1, x2, y2);
            }
        }
    }

}
