package com.github.ruslanjava.baldagame.cellBoard.keyboardDialog;

import com.github.ruslanjava.baldagame.R;

public enum KeyboardType {

    ENGLISH(R.layout.keyboard_en),
    RUSSIAN(R.layout.keyboard_ru);

    private final int layoutId;

    KeyboardType(int layoutId) {
        this.layoutId = layoutId;
    }

    public int getLayoutId() {
        return layoutId;
    }

}
