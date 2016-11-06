package com.github.ruslanjava.baldagame;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public class MainActivityFragment extends Fragment {

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentLayout layout = getClass().getAnnotation(FragmentLayout.class);
        if (layout == null) {
            throw new IllegalStateException("Missed annotation: " + FragmentLayout.class.getName());
        }
        View view = inflater.inflate(layout.value(), container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    protected void showError(Throwable t) {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.showError(t);
    }

}
