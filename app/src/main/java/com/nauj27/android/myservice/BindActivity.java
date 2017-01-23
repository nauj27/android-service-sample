package com.nauj27.android.myservice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.lang.ref.WeakReference;

public class BindActivity extends AppCompatActivity {

    Boolean serviceConnected = false;
    ToggleButton toggleButton;

    static final String TAG = "BindActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind);

        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (serviceConnected) {
            unbindService(serviceConnection);

            mService = null;
            serviceConnection = null;
            serviceConnected = false;
            toggleButton.setActivated(false);
        }
    }

    public void toggle(View view) {
        if (serviceConnected) {
            unbindService(serviceConnection);
            serviceConnected = false;
            mService = null;

            toggleButton.setActivated(false);

        } else {

            Intent intentService = new Intent(this, MyService.class);
            bindService(intentService, serviceConnection, BIND_NOT_FOREGROUND);
        }
    }

    public void doWork(View view) {
        if (mService == null) {
            Toast.makeText(getApplicationContext(), "Service not running!", Toast.LENGTH_SHORT).show();
            return;
        }

        Message msg = Message.obtain(null, MyService.MSG_SAY_HELLO);
        msg.replyTo = mMessenger;
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private static class IncomingHandler extends Handler {
        WeakReference<BindActivity> activityWeakReference;

        IncomingHandler(BindActivity bindActivity) {
            activityWeakReference = new WeakReference<>(bindActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MyService.MSG_SAY_HELLO:
                    TextView textView = (TextView) activityWeakReference
                            .get()
                            .findViewById(R.id.textView);

                    textView.setText(msg.getData().getString(MyService.MSG_HELLO_BACK));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    Messenger mService = null;
    final Messenger mMessenger = new Messenger(new IncomingHandler(this));


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            serviceConnected = true;
            mService = new Messenger(iBinder);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serviceConnected = false;
            mService = null;

            toggleButton.setActivated(false);
        }
    };

}
