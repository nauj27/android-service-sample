package com.nauj27.android.myservice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void startService(View view) {
        Log.d(TAG, "Button start pressed");

        Intent intentService = new Intent(this, MyService.class);
        startService(intentService);
    }

    public void stopService(View view) {
        Log.d(TAG, "Button stop pressed");

        Intent intentService = new Intent(this, MyService.class);
        stopService(intentService);
    }

}
