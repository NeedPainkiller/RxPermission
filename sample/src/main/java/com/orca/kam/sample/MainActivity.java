package com.orca.kam.sample;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.orca.kam.rxpermission.commons.AndroidPermission;
import com.orca.kam.sample.databinding.ActivityMainBinding;

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
import static com.orca.kam.rxpermission.util.PermissionUtil.TAG_ACTIVITY;

/**
 * Project RxPermission
 *
 * @author Kang Young Won
 * @create 2016-10-10 - 오후 1:11
 */

public class MainActivity extends AppCompatActivity implements IMain {

    private CompositeDisposable disposables = new CompositeDisposable();

    private ViewModel viewModel = new ViewModel();
    private ActivityMainBinding binding;

    private MainService mainService;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBinding();
        setToolbar();
        setConsole();
    }


    private void setDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setViewModel(viewModel);
    }


    private void setToolbar() {
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
    }


    private void setConsole() {
        TextView console = binding.console;
        console.setMaxLines(25);
        console.setVerticalScrollBarEnabled(true);
        console.setMovementMethod(new ScrollingMovementMethod());
    }


    @Override protected void onDestroy() {
        super.onDestroy();
        disposables.clear();

        if (mainService != null){
            mainService.setiMain(null); // unregister
            unbindService(serviceConnection);
        }

    }


    private void requestBluetoothPermission() {
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
                .doOnSubscribe(disposable1 -> updateConsoleLog("Request Bluetooth Permission"))
                .subscribe(deniedPermissions -> {
                            for (String deniedPermission : deniedPermissions) {
                                updateConsoleLog("onNext : Permission Denial : " + deniedPermission);
                            }
                        },
                        throwable -> updateConsoleLog("errorOccurred : Request Error : " + throwable.getMessage()),
                        () -> updateConsoleLog("onComplete : Permissions All Granted"));
        disposables.add(disposable);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        if (id == R.id.action_request_activity) {
            requestBluetoothPermission();
        }else if (id == R.id.action_request_service) {
            startService();
        } else if (id == R.id.action_settings) {
            try {
                intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.parse("package:" + getPackageName()));
            } catch (ActivityNotFoundException e) {
                intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            }
            startActivityForResult(intent, 0);
        } else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.repo_link)));
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void startService(){
        Intent intent = new Intent(this, MainService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // cast the IBinder and get MyService instance
            MainService.LocalBinder binder = (MainService.LocalBinder) service;
            mainService = binder.getService();
            updateConsoleLog("onServiceConnected");
            mainService.setiMain(MainActivity.this); // register
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            updateConsoleLog("onServiceDisconnected");
        }
    };


    @Override public void updateConsoleLog(String log) {
        viewModel.updateConsoleLog(log);
        Log.e(TAG_ACTIVITY,log);
    }
}
