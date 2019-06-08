package com.drunkare.drunkare;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import org.json.JSONException;
import org.json.JSONObject;

public class WatchAppService extends AccessibilityService {

    public static WatchAppService instance;
    Context context = this;
    int is_drunk;
    String[] WatchList = {};

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        LocalBroadcastManager.getInstance(context).registerReceiver(
                mMessageReceiver, new IntentFilter("ContextUpdate"));

        Bundle b = intent.getExtras();
        WatchList = b.getStringArray("watchedApps");
        return START_STICKY;
    }


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        //Configure these here for compatibility with API 13 and below.
        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
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

                if(is_drunk==1){
                    //Log.d("MyTagGoesHere", componentName.flattenToShortString());
                    for (String app_name : WatchList){
                        //Log.d("MyTagGoesHere", app_name);
                        if ( componentName.flattenToShortString().contains(app_name)){
                            Intent smartLock = new Intent(this, SmartLockActivity.class);
                            smartLock.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(smartLock);
                        }
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

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String result = intent.getStringExtra("result");
            JSONObject json = null;
            try {
                json = new JSONObject(result);
                String phase = json.getString("context");
                if (phase.equals("drinking")){
                    is_drunk=1;
                }
                else{
                    is_drunk=0;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
}