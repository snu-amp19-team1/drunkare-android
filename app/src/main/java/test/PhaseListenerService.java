package test;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

public class PhaseListenerService extends Service {

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {

                handler.postDelayed(runnable, 3000);
                ActivityManager mActivityManager =(ActivityManager)PhaseListenerService.this.getSystemService(Context.ACTIVITY_SERVICE);
                String mPackageName;
                if(Build.VERSION.SDK_INT > 20){
                    mPackageName = mActivityManager.getRunningAppProcesses().get(0).processName;
                }
                else{
                    mPackageName = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
                }
                Toast.makeText(context, "current app:"+mPackageName, Toast.LENGTH_LONG).show();
            }
        };
        handler.postDelayed(runnable, 3000);
    }

    @Override
    public void onDestroy() {
//        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
    }

}
