package com.github.ruslanjava.baldagame.cellBoard.keyboardDialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.ruslanjava.baldagame.R;

public class KeyboardDialog extends Dialog {

    private KeyboardDialogListener listener;

    public KeyboardDialog(Context context, KeyboardType type, KeyboardDialogListener listener) {
        super(context, R.style.AppTheme);
        Window window = getWindow();
        window.setBackgroundDrawableResource(R.color.keyboardDialogBackground);
        setContentView(R.layout.keyboard);
        FrameLayout keyboardContainer = (FrameLayout) findViewById(R.id.keyboard_container);

        int layoutId = type.getLayoutId();
        View keyboardView = LayoutInflater.from(context).inflate(layoutId, null);
        keyboardContainer.addView(keyboardView);

        this.listener = listener;

        View.OnClickListener clickListener = new KeyboardLetterListener();
        addClickListeners(keyboardView, clickListener);
    }

    private void addClickListeners(View view, View.OnClickListener clickListener) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                addClickListeners(viewGroup.getChildAt(i), clickListener);
            }
        } else if (view instanceof TextView) {
            view.setOnClickListener(clickListener);
        }
    }

    private class KeyboardLetterListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            TextView textView = (TextView) view;
            char letter = textView.getText().charAt(0);
            KeyboardDialog.this.dismiss();

            listener.onLetterSelected(letter);
        }

    }

}
