package com.github.ruslanjava.baldagame;

import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;

import com.github.ruslanjava.baldagame.game.GameFragment;
import com.github.ruslanjava.baldagame.splash.SplashFragment;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @State
    Class<? extends MainActivityFragment> mainFragmentClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Icepick.restoreInstanceState(this, savedInstanceState);
        if (mainFragmentClass == null) {
            showSplashFragment();
        }
    }

    public void showSplashFragment() {
        showFragment(new SplashFragment());
    }

    public void showGameFragment() {
        showFragment(new GameFragment());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    void showError(Throwable t) {
        t.printStackTrace();
        showError(t.toString());
    }

    void showError(String message) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    private void showFragment(MainActivityFragment fragment) {
        this.mainFragmentClass = fragment.getClass();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }

}
