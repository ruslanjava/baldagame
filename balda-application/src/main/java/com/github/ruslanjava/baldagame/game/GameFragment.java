package com.github.ruslanjava.baldagame.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ruslanjava.baldagame.FragmentLayout;
import com.github.ruslanjava.baldagame.MainActivityFragment;
import com.github.ruslanjava.baldagame.R;
import com.github.ruslanjava.baldagame.cellBoard.CellBoardView;
import com.github.ruslanjava.baldagame.gameSolution.GameSolution;
import com.github.ruslanjava.baldagame.gameSolution.GameSolver;
import com.github.ruslanjava.baldagame.prefixTree.FilePrefixTree;

import java.io.File;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import icepick.State;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

@FragmentLayout(R.layout.game_fragment)
public class GameFragment extends MainActivityFragment {

    @BindView(R.id.cellBoardView)
    CellBoardView cellBoardView;

    private FilePrefixTree tree;
    private GameSolver gameSolver;

    @State
    HashSet<String> userWords;

    private InitialWordObserver initialWordObserver;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = super.onCreateView(inflater, container, savedInstanceState);
        setRetainInstance(true);
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        File file = new File(getActivity().getFilesDir(), "dictionary.rdict");
        tree = new FilePrefixTree(file.getAbsolutePath());
        gameSolver = new GameSolver(tree);
        userWords = new HashSet<>();
        cellBoardView.setOnWordEnteredListener(new WordListener());

        if (!cellBoardView.hasInitialWord()) {
            initialWordObserver = new InitialWordObserver();
            tree.getRandomFiveLetterWord()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(initialWordObserver);
        }
    }

    @Override
    public void onPause() {
        if (initialWordObserver != null) {
            initialWordObserver.dispose();
            initialWordObserver = null;
        }
        tree.close();
        super.onPause();
    }

    private class InitialWordObserver extends DisposableObserver<String> {

        @Override
        public void onComplete() {
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onNext(String word) {
            userWords.add(word);
            cellBoardView.setInitialWord(word.toUpperCase());
        }

    }

    private class WordListener implements com.github.ruslanjava.baldagame.cellBoard.OnWordEnteredListener {

        @Override
        public void onWordEntered(String word) {
            String newWord = word.toLowerCase();
            if (tree.containsWord(newWord)) {
                userWords.add(newWord);
                cellBoardView.addWord();
                final char[][] board = cellBoardView.getBoard();
                gameSolver.getSolutions(board)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new GameDecisionSubscriber());
            } else {
                cellBoardView.cancelWord();
                String message = getString(R.string.word_does_not_exist, word);
                showError(message);
            }
        }

    }

    private class GameDecisionSubscriber extends DisposableObserver<GameSolution> {

        private boolean solved;

        @Override
        public void onComplete() {
        }

        @Override
        public void onError(Throwable e) {
            showError(e);
        }

        @Override
        public void onNext(GameSolution gameSolution) {
            if (solved) {
                return;
            }
            if (userWords.contains(gameSolution.getWord())) {
                return;
            }

            solved = true;
            userWords.add(gameSolution.getWord());

            int x = gameSolution.getNewLetterX();
            int y = gameSolution.getNewLetterY();
            char letter = gameSolution.getNewLetter();
            cellBoardView.addComputerMove(x, y, letter, gameSolution.getPath());

            Observable.empty().delay(2, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DelayedAdditionObserver());
        }

    }

    private class DelayedAdditionObserver extends DisposableObserver<Object> {

        @Override
        public void onComplete() {
            cellBoardView.addWord();
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onNext(Object object) {
        }

    }

}
