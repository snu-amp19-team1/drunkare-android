package com.drunkare.drunkare;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;


public class PhaseDetectorService extends Service implements AsyncResponse {

    public Context context = this;
    public AsyncResponse ar = this;
    public Handler handler = null;
    public static Runnable runnable = null;
    PhaseQueryTask phaseQueryTask;
    public int state;
    String response;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {


        handler = new Handler();
        runnable = new Runnable() {
            public void run() {

                handler.postDelayed(runnable, 10000);

                try{
                    phaseQueryTask=new PhaseQueryTask();
                    phaseQueryTask.delegate = ar;
                    phaseQueryTask.execute("http://lynx.snu.ac.kr:8081/custom_user/app?user_id=0");
                }
                catch (Exception e){
                    Log.d(TAG, "error in handler ");
                }




            }
        };
        handler.postDelayed(runnable, 10000);
    }

    @Override
    public void onDestroy() {
//        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startid) {

    }

    @Override
    public void processFinish(String output) {

        response = output;
        try {
            Intent intent = new Intent("ContextUpdate");
            intent.putExtra("result", response);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } catch (Exception e) {
            Log.d(TAG, "error in run");
        }

    }
}

class PhaseQueryTask extends AsyncTask<String, Void, String> {
    AsyncResponse delegate = null;
    OkHttpClient client = new OkHttpClient();

    @Override
    protected String doInBackground(String... params) {


        try {
            Request request = new Request.Builder()
                    .url("http://lynx.snu.ac.kr:8081/custom_user/app?user_id=0")
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            Log.d(TAG, "error in doInBackground");

        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        delegate.processFinish(result);
    }
}

interface AsyncResponse {
    void processFinish(String output);
}