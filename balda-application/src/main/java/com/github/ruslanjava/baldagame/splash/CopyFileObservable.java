package com.github.ruslanjava.baldagame.splash;

import android.content.res.AssetManager;

import java.io.File;
import java.io.InputStream;

import rx.Observable;
import rx.Subscriber;

class CopyFileObservable {

    static Observable<Integer> create(final AssetManager assetManager,
                                 final File destinationDirectory,
                                 final String fileName,
                                 final long expectedSize) {
        return Observable.create(
                new Observable.OnSubscribe<Integer>() {

                    @Override
                    public void call(Subscriber<? super Integer> subscriber) {
                        File destinationFile = new File(destinationDirectory, fileName);
                        if (destinationFile.exists() && destinationFile.isFile() &&
                                destinationFile.length() == expectedSize) {
                            subscriber.onNext(100);
                            subscriber.onCompleted();
                            return;
                        }

                        int lastPercent = 0;
                        long size = 0;
                        try {
                            try (InputStream is = assetManager.open(fileName)) {
                                byte[] buffer = new byte[10 * 1024];
                                int bytesRead;
                                while ( (bytesRead = is.read(buffer)) >= 0) {
                                    size += bytesRead;
                                    int percent = (int) ((size * 100) / expectedSize);
                                    if (percent != lastPercent) {
                                        subscriber.onNext(percent);
                                        lastPercent = percent;
                                    }
                                }
                            }
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                }
        );
    }

}
