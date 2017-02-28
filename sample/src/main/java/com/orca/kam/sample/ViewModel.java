package com.orca.kam.sample;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.util.Log;

/**
 * Project RxPermission
 *
 * @author Kang Young Won
 * @create 2016-10-10 - 오후 1:11
 */
public class ViewModel extends BaseObservable {

    private static final String NEW_LINE = "\n -> ";

    public ObservableField<String> consoleTextObservableField = new ObservableField<>("READY" + NEW_LINE);


    void updateConsoleLog(String log) {
        Log.e("AndroidPermission",log);
        this.consoleTextObservableField.set(consoleTextObservableField.get() + log + NEW_LINE);
    }
}