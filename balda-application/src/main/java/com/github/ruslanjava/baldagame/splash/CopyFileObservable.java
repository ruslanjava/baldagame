package com.github.ruslanjava.baldagame.splash;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

class CopyFileObservable {

    static Observable<Integer> create(Context context, final String fileName) {
        final AssetManager assetManager = context.getAssets();
        final File destinationDirectory = context.getFilesDir();
        return Observable.create(
                new ObservableOnSubscribe<Integer>() {

                    @Override
                    public void subscribe(ObservableEmitter<Integer> subscriber) {
                        try {
                            long expectedSize = 0;
                            try (InputStream is = assetManager.open(fileName)) {
                                byte[] buffer = new byte[10 * 1024];
                                int bytesRead;
                                while ((bytesRead = is.read(buffer)) >= 0) {
                                    expectedSize += bytesRead;
                                }
                            }

                            File destinationFile = new File(destinationDirectory, fileName);
                            if (destinationFile.exists() && destinationFile.length() == expectedSize) {
                                subscriber.onComplete();
                                return;
                            }

                            try (OutputStream os = new FileOutputStream(destinationFile)) {
                                try (InputStream is = assetManager.open(fileName)) {
                                    byte[] buffer = new byte[10 * 1024];
                                    int bytesRead;
                                    int lastPercent = 0;

                                    long size = 0;
                                    while ( (bytesRead = is.read(buffer)) >= 0) {
                                        os.write(buffer, 0, bytesRead);
                                        size += bytesRead;
                                        int percent = (int) ((size * 100) / expectedSize);
                                        if (percent > lastPercent) {
                                            lastPercent = percent;
                                            subscriber.onNext(percent);
                                        }
                                    }
                                }
                            }
                            subscriber.onComplete();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                }
        );
    }

}
