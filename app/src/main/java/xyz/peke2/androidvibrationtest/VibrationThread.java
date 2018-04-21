package xyz.peke2.androidvibrationtest;

import android.app.Service;
import android.content.Context;
import android.os.Parcel;
import android.os.VibrationEffect;
import android.os.Vibrator;

import java.lang.Thread;
import java.util.ArrayList;
import android.util.Log;

public class VibrationThread extends Thread {

    Vibrator vibrator;
    boolean isEnd = false;

    Object syncObject;
    Object syncEnd;

    class Param{
        public long[] timings;
        public int[] amplitudes;

        public Param(long[] timings, int[] amplitudes)
        {
            this.timings = timings;
            this.amplitudes = amplitudes;
        }
    }

    ArrayList<Param> paramList = new ArrayList<Param>();

    public VibrationThread(Context context)
    {
        vibrator = (Vibrator)context.getSystemService(Service.VIBRATOR_SERVICE);

        boolean hasAmp = vibrator.hasAmplitudeControl();
        Log.d("vibration_test", "振動強弱設定:"+((hasAmp==true)?"有効":"無効"));

        syncEnd = new Object();
        syncObject = new Object();

        start();
    }

    public void add(long[] timings, int[] amplitudes)
    {
        synchronized (syncObject){
            paramList.add(new Param(timings, amplitudes));
        }
    }

    public void end()
    {
        synchronized (syncEnd){
            isEnd = true;
        }
    }

    public void run()
    {
        while(true)
        {
            try{
                Thread.sleep(1);
            }
            catch(InterruptedException e){
                break;
            }

            Param param = null;
            synchronized (syncObject){
                if( paramList.size() > 0 )
                {
                    param = paramList.remove(0);
                }
            }

            if( param != null )
            {
                VibrationEffect effect = VibrationEffect.createWaveform(param.timings, param.amplitudes, -1);
                vibrator.vibrate(effect);
            }

            synchronized (syncEnd) {
                if (isEnd == true)
                {
                    break;
                }
            }
        }
    }
}
