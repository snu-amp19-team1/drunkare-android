package com.drunkare.drunkare;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

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

                handler.postDelayed(runnable, 3000);

                try{
                    phaseQueryTask=new PhaseQueryTask();
                    phaseQueryTask.delegate = ar;
                    phaseQueryTask.execute("http://10.0.2.2:8000/context/infer");
                }
                catch (Exception e){
                    Log.d(TAG, "error in handler ");
                }
                Toast.makeText(context, ""+response, Toast.LENGTH_LONG).show();
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

    @Override
    public void processFinish(String output) {
//        TODO: output -> state -> trigger WatchApp
        response = output;
    }
}

class PhaseQueryTask extends AsyncTask<String, Void, String> {
    AsyncResponse delegate = null;
    OkHttpClient client = new OkHttpClient();

    @Override
    protected String doInBackground(String... params) {


        try {
            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8000/context/infer")
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
        Log.d("onPostExecute: ", ""+result);
        //        Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
        delegate.processFinish(result);
    }
}

interface AsyncResponse {
    void processFinish(String output);
}