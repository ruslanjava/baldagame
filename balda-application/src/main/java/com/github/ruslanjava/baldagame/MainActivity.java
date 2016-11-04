package com.github.ruslanjava.baldagame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;

import com.github.ruslanjava.baldagame.cellBoard.CellBoardView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.letterBoardView)
    CellBoardView letterBoardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Icepick.restoreInstanceState(this, savedInstanceState);

        letterBoardView = (CellBoardView) findViewById(R.id.letterBoardView);
        letterBoardView.clear();
        letterBoardView.setInitialWord("БАЛДА");
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

}
