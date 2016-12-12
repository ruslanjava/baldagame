package com.github.ruslanjava.baldagame.game.gameBoard.keyboardDialog;

import com.github.ruslanjava.baldagame.R;

public enum KeyboardType {

    ENGLISH(R.layout.keyboard_en),
    RUSSIAN(R.layout.keyboard_ru);

    final int layoutId;

    KeyboardType(int layoutId) {
        this.layoutId = layoutId;
    }

}
