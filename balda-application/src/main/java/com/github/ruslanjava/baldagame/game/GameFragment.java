package com.github.ruslanjava.baldagame.game;

import android.support.v4.app.Fragment;

import com.github.ruslanjava.baldagame.FragmentLayout;
import com.github.ruslanjava.baldagame.MainActivityFragment;
import com.github.ruslanjava.baldagame.R;
import com.github.ruslanjava.baldagame.cellBoard.CellBoardView;
import com.github.ruslanjava.baldagame.prefixTree.FilePrefixTree;

import java.io.File;

import butterknife.BindView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@FragmentLayout(R.layout.game_fragment)
public class GameFragment extends MainActivityFragment {

    @BindView(R.id.cellBoardView)
    CellBoardView cellBoardView;

    private FilePrefixTree tree;

    @Override
    public void onResume() {
        super.onResume();
        File file = new File(getActivity().getFilesDir(), "dictionary.rdict");
        tree = new FilePrefixTree(file.getAbsolutePath());

        if (cellBoardView.getLetter(0, 2) == ' ') {
            InitialWordObservable.create(tree)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new InitialWordObserver());
        }
    }

    @Override
    public void onPause() {
        tree.close();
        super.onPause();
    }

    private class InitialWordObserver implements Observer<String> {

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(String word) {
            cellBoardView.setInitialWord(word.toUpperCase());
        }

    }

}
