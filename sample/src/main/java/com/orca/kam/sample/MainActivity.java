package com.orca.kam.sample;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.orca.kam.rxpermission.commons.AndroidPermission;
import com.orca.kam.sample.databinding.ActivityMainBinding;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static android.Manifest.permission.*;

/**
 * Project RxPermission
 *
 * @author Kang Young Won
 * @create 2016-10-10 - 오후 1:11
 */

public class MainActivity extends AppCompatActivity {

    private AndroidPermission androidPermission;
    private CompositeDisposable disposables = new CompositeDisposable();

    private ViewModel viewModel = new ViewModel();
    private ActivityMainBinding binding;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDataBinding();
        setToolbar();
        setConsole();
        viewModel.updateConsoleLog("onCreate");
        initAndroidPermission(this);
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


    private void initAndroidPermission(Context context) {
        if (androidPermission == null) {
            androidPermission = new AndroidPermission(context);
        }
    }


    @Override protected void onResume() {
        super.onResume();
        viewModel.updateConsoleLog("onResume");
    }


    @Override protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }


    private void requestBluetoothPermission() {
        Disposable disposable = androidPermission.requestPermission(
                READ_CALENDAR, WRITE_CALENDAR,
                CAMERA,
                READ_CONTACTS, WRITE_CONTACTS, GET_ACCOUNTS,
                ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION,
                RECORD_AUDIO,
                READ_PHONE_STATE, CALL_PHONE,
                READ_CALL_LOG, WRITE_CALL_LOG,
                ADD_VOICEMAIL, USE_SIP,
                PROCESS_OUTGOING_CALLS,
                BODY_SENSORS,
                SEND_SMS, RECEIVE_SMS, READ_SMS,
                RECEIVE_WAP_PUSH, RECEIVE_MMS,
                READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE,
                INTERNET)
                .doOnSubscribe(disposable1 -> viewModel.updateConsoleLog("Request Bluetooth Permission"))
                .subscribe(deniedPermissions -> {
                            for (String deniedPermission : deniedPermissions) {
                                viewModel.updateConsoleLog("onNext : Permission Denial : " + deniedPermission);
                            }
                        },
                        throwable -> viewModel.updateConsoleLog("errorOccurred : Request Error : " + throwable.getMessage()),
                        () -> viewModel.updateConsoleLog("onComplete : Permissions All Granted"));
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
        if (id == R.id.action_request) {
            requestBluetoothPermission();
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
}
