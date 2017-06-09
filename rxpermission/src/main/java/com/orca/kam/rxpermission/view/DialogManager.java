package com.orca.kam.rxpermission.view;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.orca.kam.rxpermission.model.DialogMessage;

import io.reactivex.Observable;

/**
 * @author kam6512
 * @create on 2017-05-18.
 */
class DialogManager {

    private MaterialDialog.Builder builder;
    private DialogMessage message;


    DialogManager(Context context, DialogMessage message) {
        builder = new MaterialDialog.Builder(context);
        this.message = message;
    }


    Observable<Boolean> showRationaleDialog() {
        return Observable.create(subscriber -> builder.content(message.getExplanationMessage())
                .negativeText(message.getExplanationConfirmButtonText())
                .onNegative((dialog, which) -> subscriber.onNext(true))
                .dismissListener(dialog -> subscriber.onComplete())
                .cancelable(false).show());
    }


    Observable<Boolean> showPermissionDenyDialog() {
        return Observable.create(subscriber -> builder.content(message.getDeniedMessage())
                .positiveText(message.getSettingButtonText())
                .negativeText(message.getDeniedCloseButtonText())
                .onPositive((dialog, which) -> subscriber.onNext(true))
                .onNegative((dialog, which) -> subscriber.onNext(false))
                .dismissListener(dialog -> subscriber.onComplete())
                .cancelable(false).show());
    }
}