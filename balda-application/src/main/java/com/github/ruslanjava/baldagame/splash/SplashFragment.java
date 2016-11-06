package com.github.ruslanjava.baldagame.splash;

import android.content.Context;
import android.widget.ProgressBar;

import com.github.ruslanjava.baldagame.FragmentLayout;
import com.github.ruslanjava.baldagame.MainActivity;
import com.github.ruslanjava.baldagame.MainActivityFragment;
import com.github.ruslanjava.baldagame.R;
import com.github.ruslanjava.baldagame.cellBoard.CellBoardView;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@FragmentLayout(R.layout.splash_fragment)
public class SplashFragment extends MainActivityFragment {

    @BindView(R.id.cellBoardView)
    CellBoardView cellBoardView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    public void onResume() {
        super.onResume();
        cellBoardView.setInitialWord("БАЛДА");

        Context context = getActivity();
        AssetSizeOberservable.create(context.getAssets(), "dictionary.rdict")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new AssetSizeObserver());
    }

    private class AssetSizeObserver implements Observer<Long> {

        private Long size;

        @Override
        public void onError(Throwable e) {
            showError(e);
        }

        @Override
        public void onNext(Long size) {
            this.size = size;
        }

        @Override
        public void onCompleted() {
            Context context = getContext();
            CopyFileObservable.create(context.getAssets(), context.getFilesDir(), "dictionary.rdict", size)
                    .onBackpressureDrop()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new CopyFileObserver());
        }

    }

    private class CopyFileObserver implements Observer<Integer> {

        @Override
        public void onError(Throwable e) {
            showError(e);
        }

        @Override
        public void onNext(Integer percent) {
            progressBar.setProgress(percent);
        }

        @Override
        public void onCompleted() {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.showGameFragment();
        }

    }

}
