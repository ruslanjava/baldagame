package com.github.ruslanjava.baldagame.cellBoard.confirmWordDialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.github.ruslanjava.baldagame.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfirmWordDialog extends Dialog {

    @BindView(R.id.confirm_dialog_word)
    TextView wordView;

    private View.OnClickListener okButtonListener;
    private View.OnClickListener cancelButtonListener;

    public ConfirmWordDialog(Context context, String word) {
        super(context);
        getWindow().setBackgroundDrawableResource(R.drawable.confirm_dialog_background);
        setContentView(R.layout.confirm_dialog);
        ButterKnife.bind(this);
        wordView.setText(word);
    }

    public void setOkButtonListener(View.OnClickListener listener) {
        okButtonListener = listener;
    }

    public void setCancelButtonListener(View.OnClickListener listener) {
        cancelButtonListener = listener;
    }

    @OnClick(R.id.ok_button)
    void onConfirmed(View view) {
        dismiss();
        if (okButtonListener != null) {
            okButtonListener.onClick(view);
        }
    }

    @OnClick(R.id.cancel_button)
    void onCancelled(View view) {
        dismiss();
        if (cancelButtonListener != null) {
            cancelButtonListener.onClick(view);
        }
    }

}
