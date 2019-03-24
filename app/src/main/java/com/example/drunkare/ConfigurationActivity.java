package com.example.drunkare;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class ConfigurationActivity extends AppCompatActivity {

    public Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button btnStart = findViewById(R.id.btnStart);
        Button btnKill = findViewById(R.id.btnKill);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToAccessibilitySettings = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(goToAccessibilitySettings);
                Toast.makeText(context, "AppWatch Triggered", Toast.LENGTH_LONG).show();
                startService(new Intent(ConfigurationActivity.this, WatchAppService.class));
            }
        });

        btnKill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WatchAppService.instance.setServiceInfo(new AccessibilityServiceInfo()) ;
                Toast.makeText(context, "AppWatch Terminated", Toast.LENGTH_LONG).show();            }
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
