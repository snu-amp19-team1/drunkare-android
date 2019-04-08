package com.drunkare.drunkare;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    public Context context = this;
    String[] watchAppList={};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try{
            Bundle b = getIntent().getExtras();
            watchAppList = b.getStringArray("watchedApps");
            for (String app:watchAppList){
                Log.d("watched app: ", app);
            }
        }
        catch (Exception e){
            Log.d("onCreate: ", "none");
        }


        Button btnStart = findViewById(R.id.btnStart);
        Button btnKill = findViewById(R.id.btnKill);
        Button btnQuery = findViewById(R.id.btnQuery);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToAccessibilitySettings = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(goToAccessibilitySettings);
                Toast.makeText(context, "Please turn on the WatchAppService", Toast.LENGTH_LONG).show();

                Intent serviceIntent = new Intent(MainActivity.this, WatchAppService.class);

                // Create a bundle object
                Bundle b = new Bundle();
                b.putStringArray("watchedApps", watchAppList);
                serviceIntent.putExtras(b);
                startService(serviceIntent);

            }
        });

        btnQuery.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(MainActivity.this, PhaseDetectorService.class);
                startService(serviceIntent);
            }
        });

        btnKill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WatchAppService.instance.setServiceInfo(new AccessibilityServiceInfo()) ;
                Toast.makeText(context, "Forcing shutdown", Toast.LENGTH_LONG).show();
            }
        });




    }

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

            Intent configurationIntent = new Intent(this, ConfigurationActivity.class);
            startActivity(configurationIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
