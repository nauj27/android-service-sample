package com.nauj27.android.myservice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class MyService extends Service {

    private static final int ONGOING_NOTIFICATION_ID = 1;
    private static final String TAG = "MyService";

    static final int MSG_SAY_HELLO = 1;
    static final String MSG_HELLO_BACK = "MSG_HELLO_BACK";
    static final String MSG_HELLO_TEXT = "Hello from service through messenger";

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        return mMessenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Starting notification...");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new Notification.Builder(this)
                .setContentTitle(getText(R.string.notification_title))
                .setContentText(getText(R.string.notification_message))
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .setTicker(getText(R.string.ticker_text))
                .build();

        startForeground(ONGOING_NOTIFICATION_ID, notification);
    }

    static class IncomingHandler extends Handler {
        WeakReference<MyService> serviceWeakReference;

        IncomingHandler(MyService myService) {
            serviceWeakReference = new WeakReference<>(myService);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAY_HELLO:
                    serviceWeakReference.get().replyMessenger = msg.replyTo;
                    serviceWeakReference.get().sendMessageBack();

                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    void sendMessageBack() {
        Message msg = Message.obtain(null, MSG_SAY_HELLO);
        Bundle data = new Bundle();
        data.putString(MSG_HELLO_BACK, MSG_HELLO_TEXT);
        msg.setData(data);

        try {
            replyMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler(this));
    Messenger replyMessenger;
}
