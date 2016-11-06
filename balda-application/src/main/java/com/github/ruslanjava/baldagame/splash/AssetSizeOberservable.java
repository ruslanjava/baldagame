package com.github.ruslanjava.baldagame.splash;

import android.content.res.AssetManager;

import java.io.InputStream;
import java.util.concurrent.Callable;

import rx.Observable;

class AssetSizeOberservable {

    static Observable<Long> create(final AssetManager assetManager, final String fileName) {
        return Observable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                long size = 0;
                try (InputStream is = assetManager.open(fileName)) {
                    byte[] buffer = new byte[10 * 1024];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) >= 0) {
                        size += bytesRead;
                    }
                }
                return size;
            }
        });
    }

}
