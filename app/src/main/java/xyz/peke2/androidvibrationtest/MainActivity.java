package xyz.peke2.androidvibrationtest;

import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.UserHandle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.util.*;
import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        identifyDevice();

        Button buttonStart = findViewById(R.id.button_vib_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("vibration_test", "start");
                Context context = getApplicationContext();

                Vibrator vib = (Vibrator)context.getSystemService(Service.VIBRATOR_SERVICE);
                long[] timings = new long[]{100,100,100,100,100,100,100,100,100,100};
                int[] amplitudes = new int[]{250,10,180,200,0,120,180,30,30,200};

                //long[] timings = new long[]{10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,};
                //int[] amplitudes = new int[]{7,22,4,52,17,5,59,45,51,7,1,24,50,53,16,36,27,55,44,14,12,59,6,56,6,51,2,13,4,42,19,54,48,42,28,54,4,18,16,22,20,17,25,37,16,1,32,0,3,16,12,2,5,8,0,30,0,14,13,28,4,3,22,0,7,8,9,1,2,6,13,1,0,1,5,2,2,5,5,3,1,2,2,0,2,4,0,1,1,0,0,1,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,};
                /*
                for(int i=0; i<amplitudes.length; i++)
                {
                    amplitudes[i] *= 4;
                }*/

                VibrationEffect effect = VibrationEffect.createWaveform(timings, amplitudes,-1);

                long st, elapsed;

                st = System.currentTimeMillis();

                //vib.cancel();

                elapsed = System.currentTimeMillis() - st;
                Log.d("vibration_test", "cancel elapsed:"+ String.valueOf(elapsed)+"ms");
                st = System.currentTimeMillis();

                vib.vibrate(effect);

                elapsed = System.currentTimeMillis() - st;
                Log.d("vibration_test", "vibrate elapsed:"+ String.valueOf(elapsed)+"ms");
            }
        });

        Button buttonStop = findViewById(R.id.button_vib_stop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("vibration_test", "stop");
                Vibrator vib = (Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
                vib.cancel();
            }
        });
    }

    void identifyDevice()
    {
        Log.d("vibration_test", "MANUFACTURER["+Build.MANUFACTURER + "] MODEL["+Build.MODEL+"]");
    }
}
