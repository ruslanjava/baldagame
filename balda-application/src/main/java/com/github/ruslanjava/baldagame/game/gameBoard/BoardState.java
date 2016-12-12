package com.github.ruslanjava.baldagame.game.gameBoard;

import java.io.Serializable;

enum BoardState implements Serializable {

    IDLE,
    NEW_LETTER_DIALOG,
    PATH,
    COMPUTER_MOVE

}
