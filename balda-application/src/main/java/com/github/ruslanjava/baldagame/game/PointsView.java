package com.github.ruslanjava.baldagame.game;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;

import com.github.ruslanjava.baldagame.R;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PointsView extends TextView implements Observer<Integer> {

    private String text;
    private Disposable disposable;

    public PointsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PointsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    public void onDetachedFromWindow() {
        if (disposable != null) {
            disposable.dispose();
        }
        super.onDetachedFromWindow();
    }

    private void init(Context context) {
        text = getText().toString();
        Shader textShader = new LinearGradient(
                0, 0, 0, 20,
                new int[] {
                        context.getColor(R.color.pointsTextTop),
                        context.getColor(R.color.pointsTextBottom)
                },
                new float[] { 0, 1 },
                Shader.TileMode.CLAMP
        );
        getPaint().setShader(textShader);
    }

    public void observe(Observable<Integer> pointsObservable) {
        pointsObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @Override
    public void onSubscribe(Disposable disposable) {
        this.disposable = disposable;
    }

    @Override
    public void onNext(Integer p) {
        setText(String.format("%s %s", text, p));
    }

    @Override
    public void onError(Throwable e) {
    }

    @Override
    public void onComplete() {
    }

}
