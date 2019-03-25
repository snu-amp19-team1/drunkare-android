package com.drunkare.drunkare;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import static android.content.ContentValues.TAG;

public class WatchAppService extends AccessibilityService {

    public static WatchAppService instance;
    Context context = this;
    String[] WatchList = {};

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {

        Bundle b = intent.getExtras();
        WatchList = b.getStringArray("watchedApps");
        for (String app:WatchList){
            Log.d("onCreate: ", app);
        }
        return START_STICKY;
    }


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected: connected");
        //Configure these here for compatibility with API 13 and below.
        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

        if (Build.VERSION.SDK_INT >= 16)
            //Just in case this helps
            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;

        setServiceInfo(config);
        instance = this;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (event.getPackageName() != null && event.getClassName() != null) {

                ComponentName componentName = new ComponentName(
                        event.getPackageName().toString(),
                        event.getClassName().toString()
                );


                ActivityInfo activityInfo = tryGetActivity(componentName);
                boolean isActivity = activityInfo != null;
                if (isActivity)
                    Log.d("CurrentActivity", event.getPackageName().toString());

                    for (String app_name : WatchList){

                        if (componentName.flattenToShortString().contains(app_name)){

                            Intent smartLock = new Intent(this, SmartLockActivity.class);
                            smartLock.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(smartLock);
                        }
                    }
            }
        }
    }
    private ActivityInfo tryGetActivity(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @Override
    public void onInterrupt() {}
}