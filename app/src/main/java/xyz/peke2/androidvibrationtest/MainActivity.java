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

    VibrationThread vibThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vibThread = new VibrationThread(getApplicationContext());

        identifyDevice();

        Button buttonStart = findViewById(R.id.button_vib_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("vibration_test", "start");
                Context context = getApplicationContext();

                Vibrator vib = (Vibrator)context.getSystemService(Service.VIBRATOR_SERVICE);
                //long[] timings = new long[]{100,100,100,100,100,100,100,100,100,100};
                //int[] amplitudes = new int[]{250,10,180,200,0,120,180,30,30,200};

                //[スレッドでの振動再生で確認]
                //どちらも約5.2〜5.4秒ほど振動が再生
                //左右の手で振動とストップウォッチを開始するので多少の誤差はあるが、
                //確実に5秒は超える
                //10秒でも15秒でも同様に0.3〜0.5秒ほどしかずれがない

                //同じ値の振動は2000まで連続できない？？
                //
                int n = 1000;   //1500まではいけた　→　20秒を超える再生はできない？
                long[] timings = new long[n];
                int[] amplitudes = new int[n];
                for(int i=0; i<n; i++){
                    timings[i] = 10;
                    amplitudes[i] = 255;      //最初一瞬振動して停止、しばらくしたら少し振動しておしまい
                    //amplitudes[i] = 255 * (((i & 1) == 1)? 1:0);    //振動が続く、ただし20秒想定で約22秒振動した
                                                                    //30秒想定で32.64秒くらい
                    //amplitudes[i] = (int)(255 * (10 - i%10) * 0.1f);    //これだとだめ？？？
                }
                /*
                long[] timings = new long[]{15000}; //  1つだけでも20000(20秒)は無理
                int[] amplitudes = new int[]{255};
                */

                VibrationEffect effect = VibrationEffect.createWaveform(timings, amplitudes, -1);
                vib.vibrate(effect);

            /*
                if( true ) {
                    //  スレッドを使わない処理
                    VibrationEffect effect = VibrationEffect.createWaveform(timings, amplitudes, -1);

                    long st, elapsed;

                    st = System.currentTimeMillis();

                    //vib.cancel();

                    elapsed = System.currentTimeMillis() - st;
                    Log.d("vibration_test", "cancel elapsed:" + String.valueOf(elapsed) + "ms");
                    st = System.currentTimeMillis();

                    vib.vibrate(effect);

                    elapsed = System.currentTimeMillis() - st;
                    Log.d("vibration_test", "vibrate elapsed:" + String.valueOf(elapsed) + "ms");
                }
                else
                {
                    //  スレッドで振動を再生
                    long st, elapsed;

                    st = System.currentTimeMillis();

                    elapsed = System.currentTimeMillis() - st;
                    Log.d("vibration_test", "cancel elapsed:" + String.valueOf(elapsed) + "ms");
                    st = System.currentTimeMillis();

                    vibThread.add(timings, amplitudes);

                    elapsed = System.currentTimeMillis() - st;
                    Log.d("vibration_test", "vibrate elapsed:" + String.valueOf(elapsed) + "ms");
                }
            */
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
