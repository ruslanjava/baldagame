package com.github.ruslanjava.baldagame.game;

import java.util.concurrent.Callable;

import rx.Observable;

public class DelayedAdditionObservable {

    static Observable<Void> create(final long timeout) {
        return Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(timeout);
                return null;
            }
        });
    }

}
