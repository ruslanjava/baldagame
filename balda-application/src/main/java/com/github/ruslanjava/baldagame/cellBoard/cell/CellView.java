package com.github.ruslanjava.baldagame.cellBoard.cell;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import icepick.Icepick;
import icepick.State;

public class CellView extends FrameLayout {

    private Paint textPaint;
    private Paint shadowPaint;

    @State
    String letter;

    @State
    CellState state;

    private int topColor;
    private int bottomColor;

    public CellView(Context context) {
        super(context);
        init(context, null);
    }

    public CellView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public CellView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        letter = "A";

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);

        shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setTextAlign(Paint.Align.CENTER);
        shadowPaint.setColor(Color.GRAY);

        setState(CellState.NORMAL);
    }

    public char getLetter() {
        return letter.charAt(0);
    }

    public void setLetter(char letter) {
        this.letter = Character.toString(letter);
    }

    public CellState getState() {
        return state;
    }

    public void setState(CellState state) {
        this.state = state;
        updateTextPaint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateShadowPaint();
        updateTextPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int x = super.getWidth() / 2;
        int y = (int) ((super.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) ;
        canvas.drawText(letter, x + 1, y + 1, shadowPaint);
        canvas.drawText(letter, x - 1, y - 1, textPaint);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable restoredState) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, restoredState));
        updateTextPaint();
    }

    private void updateShadowPaint() {
        float textSize = Math.min(super.getWidth(), super.getHeight());
        shadowPaint.setTextSize(textSize);
    }

    private void updateTextPaint() {
        Resources resources = getResources();
        topColor = resources.getColor(state.topColorId);
        bottomColor = resources.getColor(state.bottomColorId);

        setBackgroundResource(state.backgroundId);

        float textSize = Math.min(super.getWidth(), super.getHeight());
        textPaint.setTextSize(textSize);
        Shader shader = new LinearGradient(
                0, 0, 0, super.getHeight(),
                topColor, bottomColor, Shader.TileMode.CLAMP
        );
        textPaint.setShader(shader);
    }

}
