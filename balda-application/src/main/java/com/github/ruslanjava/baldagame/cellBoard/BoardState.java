package com.github.ruslanjava.baldagame.cellBoard;

import java.io.Serializable;

enum BoardState implements Serializable {

    IDLE,
    NEW_LETTER_DIALOG,
    PATH,
    CONFIRM_WORD_DIALOG,
    COMPUTER_MOVE

}
