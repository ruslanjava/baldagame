package com.github.ruslanjava.baldagame.game;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class EraseButton extends Button implements Observer<String> {

    private Disposable disposable;

    public EraseButton(Context context) {
        super(context);
    }

    public EraseButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EraseButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EraseButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onDetachedFromWindow() {
        if (disposable != null) {
            disposable.dispose();
        }
        super.onDetachedFromWindow();
    }

    @Override
    public void onSubscribe(Disposable disposable) {
        this.disposable = disposable;
    }

    @Override
    public void onNext(String word) {
        if (word.length() == 0) {
            setVisibility(View.INVISIBLE);
        } else {
            setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onError(Throwable e) {
    }

    @Override
    public void onComplete() {
    }


}
