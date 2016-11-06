package com.github.ruslanjava.baldagame.game;

import com.github.ruslanjava.baldagame.prefixTree.FilePrefixTree;

import java.util.concurrent.Callable;

import rx.Observable;

class InitialWordObservable {

    static Observable<String> create(final FilePrefixTree tree) {
        return Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return tree.getRandomFiveLetterWord();
            }
        });
    }

}
