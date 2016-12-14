package com.github.ruslanjava.baldagame.game;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import icepick.Icepick;
import icepick.State;
import io.reactivex.Observable;
import io.reactivex.subjects.ReplaySubject;

public class WordsListView extends ListView {

    @State
    ArrayList<String> words;

    private int points;

    private ArrayAdapter<String> adapter;

    private ReplaySubject<Integer> pointsSubject;

    public WordsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WordsListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable restoredState) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, restoredState));
        adapter.addAll(words);

        points = 0;
        for (String word : words) {
            points += word.length();
        }
        notifyPointsObserver(points);
    }

    public Observable<Integer> getPointsObservable() {
        return pointsSubject;
    }

    public void add(String word) {
        adapter.add(word);
        points += word.length();
        notifyPointsObserver(points);
    }

    private void init(Context context) {
        pointsSubject = ReplaySubject.create();
        if (words == null) {
            words = new ArrayList<>();
        }

        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, words);
        setAdapter(adapter);
    }

    private void notifyPointsObserver(int points) {
        setSelection(words.size() - 1);
        pointsSubject.onNext(points);
    }

}
