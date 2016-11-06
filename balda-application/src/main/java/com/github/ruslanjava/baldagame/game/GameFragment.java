package com.github.ruslanjava.baldagame.game;

import com.github.ruslanjava.baldagame.FragmentLayout;
import com.github.ruslanjava.baldagame.MainActivityFragment;
import com.github.ruslanjava.baldagame.R;
import com.github.ruslanjava.baldagame.cellBoard.CellBoardView;
import com.github.ruslanjava.baldagame.gameSolution.GameSolution;
import com.github.ruslanjava.baldagame.gameSolution.GameSolver;
import com.github.ruslanjava.baldagame.prefixTree.FilePrefixTree;

import java.io.File;
import java.util.HashSet;

import butterknife.BindView;
import icepick.State;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@FragmentLayout(R.layout.game_fragment)
public class GameFragment extends MainActivityFragment {

    @BindView(R.id.cellBoardView)
    CellBoardView cellBoardView;

    private FilePrefixTree tree;
    private GameSolver gameSolver;

    @State
    HashSet<String> userWords;

    @Override
    public void onResume() {
        super.onResume();
        File file = new File(getActivity().getFilesDir(), "dictionary.rdict");
        tree = new FilePrefixTree(file.getAbsolutePath());
        gameSolver = new GameSolver(tree);
        userWords = new HashSet<>();

        if (!cellBoardView.hasInitialWord()) {
            InitialWordObservable.create(tree)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new InitialWordObserver());
        }
        cellBoardView.setOnWordEnteredListener(new WordListener());
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
                GameSolutionObservable.create(gameSolver, board)
                        .onBackpressureBuffer()
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

    private class GameDecisionSubscriber implements Observer<GameSolution> {

        private boolean solved;

        @Override
        public void onCompleted() {
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

            DelayedAdditionObservable.create(2000)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(
                    new DelayedAdditionObserver()
            );
        }

    }

    private class DelayedAdditionObserver implements Observer<Void> {

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(Void aVoid) {
            cellBoardView.addWord();
        }

    }

}