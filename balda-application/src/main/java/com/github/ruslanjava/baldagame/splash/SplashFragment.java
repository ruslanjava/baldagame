package com.github.ruslanjava.baldagame.splash;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.ruslanjava.baldagame.FragmentLayout;
import com.github.ruslanjava.baldagame.MainActivity;
import com.github.ruslanjava.baldagame.MainActivityFragment;
import com.github.ruslanjava.baldagame.R;
import com.github.ruslanjava.baldagame.cellBoard.CellBoardView;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.ReplaySubject;

@FragmentLayout(R.layout.splash_fragment)
public class SplashFragment extends MainActivityFragment {

    @BindView(R.id.cellBoardView)
    CellBoardView cellBoardView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private ReplaySubject<Integer> subject;
    private CopyFileObserver observer;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, container, savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState == null) {
            Context context = getActivity();
            subject = ReplaySubject.create();
            CopyFileObservable.create(context, "dictionary.rdict")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subject);

        }
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setMax(100);
        cellBoardView.setInitialWord("БАЛДА");

        observer = new CopyFileObserver();
        subject.subscribe(observer);
    }

    @Override
    public void onPause() {
        super.onPause();
        observer.dispose();
    }

    private class CopyFileObserver extends DisposableObserver<Integer> {

        @Override
        public void onError(Throwable e) {
            showError(e);
        }

        @Override
        public void onNext(final Integer percent) {
            progressBar.setProgress(percent);
        }

        @Override
        public void onComplete() {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.showGameFragment();
        }

    }

}
