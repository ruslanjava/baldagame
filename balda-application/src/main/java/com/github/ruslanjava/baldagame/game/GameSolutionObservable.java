package com.github.ruslanjava.baldagame.game;

import com.github.ruslanjava.baldagame.gameSolution.GameSolution;
import com.github.ruslanjava.baldagame.gameSolution.GameSolver;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

class GameSolutionObservable {

    static Observable<GameSolution> create(final GameSolver solver, final char[][] board) {
        return Observable.create(
                new Observable.OnSubscribe<GameSolution>() {

                    @Override
                    public void call(Subscriber<? super GameSolution> subscriber) {
                        List<GameSolution> solutions = solver.solve(board);
                        for (GameSolution solution : solutions) {
                            subscriber.onNext(solution);
                        }
                        subscriber.onCompleted();
                    }
                }
        );
    }

}
