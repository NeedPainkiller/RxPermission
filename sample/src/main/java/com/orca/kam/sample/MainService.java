package com.orca.kam.sample;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.orca.kam.rxpermission.commons.AndroidPermission;

import hugo.weaving.DebugLog;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ADD_VOICEMAIL;
import static android.Manifest.permission.BODY_SENSORS;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.GET_ACCOUNTS;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.PROCESS_OUTGOING_CALLS;
import static android.Manifest.permission.READ_CALENDAR;
import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_MMS;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.RECEIVE_WAP_PUSH;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.USE_SIP;
import static android.Manifest.permission.WRITE_CALENDAR;
import static android.Manifest.permission.WRITE_CALL_LOG;
import static android.Manifest.permission.WRITE_CONTACTS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * @author kam6512
 * @Create on 2017-05-06.
 */
public class MainService extends Service {
    private CompositeDisposable disposables = new CompositeDisposable();
    private final IBinder binder = new LocalBinder();
    private IMain iMain;

    public class LocalBinder extends Binder {
        @DebugLog MainService getService() {
            // Return this instance of MyService so clients can call public methods
            return MainService.this;
        }
    }


    @DebugLog @Nullable @Override public IBinder onBind(Intent intent) {
        return binder;
    }


    @DebugLog public void setiMain(IMain iMain) {
        this.iMain = iMain;
        requestBluetoothPermission();
    }



    @Override public void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }


    @DebugLog private void requestBluetoothPermission() {
        AndroidPermission androidPermission = new AndroidPermission(this);
        Disposable disposable = androidPermission
                .request(READ_CALENDAR)
                .request(WRITE_CALENDAR)
                .request(CAMERA)
                .request(READ_CONTACTS)
                .request(WRITE_CONTACTS)
                .request(GET_ACCOUNTS)
                .request(ACCESS_FINE_LOCATION)
                .request(ACCESS_COARSE_LOCATION)
                .request(RECORD_AUDIO)
                .request(READ_PHONE_STATE)
                .request(CALL_PHONE)
                .request(READ_CALL_LOG)
                .request(WRITE_CALL_LOG)
                .request(ADD_VOICEMAIL)
                .request(USE_SIP)
                .request(PROCESS_OUTGOING_CALLS)
                .request(BODY_SENSORS)
                .request(SEND_SMS)
                .request(RECEIVE_SMS)
                .request(READ_SMS)
                .request(RECEIVE_WAP_PUSH)
                .request(RECEIVE_MMS)
                .request(READ_EXTERNAL_STORAGE)
                .request(WRITE_EXTERNAL_STORAGE)
                .request(INTERNET)
                .requestPermission()
                .subscribe(deniedPermissions -> {
                            for (String deniedPermission : deniedPermissions) {
                                iMain.updateConsoleLog("onNext : Permission Denial : " + deniedPermission);
                            }
                        },
                        throwable -> iMain.updateConsoleLog("errorOccurred : Request Error : " + throwable.getMessage()),
                        () -> iMain.updateConsoleLog("onComplete : Permissions All Granted"));
        disposables.add(disposable);
    }
}
