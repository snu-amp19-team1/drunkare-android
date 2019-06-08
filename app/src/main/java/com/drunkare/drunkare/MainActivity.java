package com.drunkare.drunkare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {


    public Context context = this;
    String[] watchAppList={};
    TextView tv0,tv1, tv2, tv3,tv4,tv5,tv6, tv_time, tv_loc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_loc = findViewById(R.id.tv_loc);
        tv0 = findViewById(R.id.tv0);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);
        tv5 = findViewById(R.id.tv5);
        tv6 = findViewById(R.id.tv6);
        tv_time = findViewById(R.id.tv_time);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent serviceIntent = new Intent(MainActivity.this, PhaseDetectorService.class);
        startService(serviceIntent);


        LocalBroadcastManager.getInstance(context).registerReceiver(
                mMessageReceiver, new IntentFilter("ContextUpdate"));

        Button btnSettings = findViewById(R.id.btnSettings);
        //Button btnKill = findViewById(R.id.btnKill);


        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent configurationIntent = new Intent(context, ConfigurationActivity.class);
                startActivity(configurationIntent);


            }
        });
/*


        btnKill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WatchAppService.instance.setServiceInfo(new AccessibilityServiceInfo()) ;
                Toast.makeText(context, "Forcing shutdown", Toast.LENGTH_LONG).show();
            }
        });
*/



    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String result = intent.getStringExtra("result");
            JSONObject json = null;
            try {

                json = new JSONObject(result);
                String phase = json.getString("context");
                JSONArray top3 = json.getJSONArray("top3");
                String time = json.getString("time");
                tv0.setText(phase.toUpperCase());
                String loc = json.getString("loc");
                tv1.setText(String.valueOf(top3.getJSONArray(0).get(0)));
                tv2.setText(String.valueOf(top3.getJSONArray(1).get(0)));
                tv3.setText(String.valueOf(top3.getJSONArray(2).get(0)));
                tv4.setText(String.valueOf(top3.getJSONArray(0).get(1)));
                tv5.setText(String.valueOf(top3.getJSONArray(1).get(1)));
                tv6.setText(String.valueOf(top3.getJSONArray(2).get(1)));
                tv_time.setText("Updated at "+time);
                tv_loc.setText(loc);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_configuration) {
            Intent goToAccessibilitySettings = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(goToAccessibilitySettings);
            Toast.makeText(context, "Please turn on the WatchAppService", Toast.LENGTH_LONG).show();

            Intent serviceIntent = new Intent(MainActivity.this, WatchAppService.class);

            // Create a bundle object
            Bundle b = new Bundle();
            b.putStringArray("watchedApps", watchAppList);
            serviceIntent.putExtras(b);
            startService(serviceIntent);


            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
