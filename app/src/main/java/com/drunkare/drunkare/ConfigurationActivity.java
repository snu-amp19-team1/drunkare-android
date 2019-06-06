package com.drunkare.drunkare;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static android.widget.AbsListView.CHOICE_MODE_MULTIPLE;

public class ConfigurationActivity extends AppCompatActivity {

    Context context = this;
    ArrayList<String> WatchList=new ArrayList<String>();
    ArrayList<Drawable> IconList = new ArrayList<>();
    ListView lv_watch_list;
    ArrayAdapter<String> watch_list_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        Intent getAppsIntent = new Intent(Intent.ACTION_MAIN, null);
        getAppsIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> pkgAppsList = pm.queryIntentActivities( getAppsIntent, 0);

        // Get application name
        //ArrayList<String> app_name_list = new ArrayList<String>();
        ArrayList<String> app_package_list = new ArrayList<String>();

        for(ResolveInfo resolve_info : pkgAppsList) {
            try {
                String package_name = resolve_info.activityInfo.packageName;
                String app_name = (String)pm.getApplicationLabel(
                        pm.getApplicationInfo(package_name
                                , PackageManager.GET_META_DATA));
                boolean same = false;
                for(int i = 0 ; i < WatchList.size() ; i++) {
                    if(package_name.equals(app_package_list.get(i)))
                        same = true;
                }
                if(!same) {
                    WatchList.add(app_name);
                    app_package_list.add(package_name);
                }
                //Log.i("Check", "package = <" + package_name + "> name = <" + app_name + ">");
            } catch(Exception e) { }
        }

        for (ResolveInfo app_resolve_info:pkgAppsList){
            IconList.add(app_resolve_info.loadIcon(pm));
            //WatchList.add((String) app_resolve_info.activityInfo.packageName);
        }

        lv_watch_list = (ListView)findViewById(R.id.watch_list);
        watch_list_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, WatchList);

        lv_watch_list.setAdapter(watch_list_adapter);
        lv_watch_list.setChoiceMode(CHOICE_MODE_MULTIPLE);

        FloatingActionButton fab_check = (FloatingActionButton) findViewById(R.id.fab_check);
        fab_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SparseBooleanArray checked = lv_watch_list.getCheckedItemPositions();
                ArrayList<String> selectedItems = new ArrayList<String>();

                for (int i = 0; i < checked.size(); i++)
                {
                    // Item position in adapter
                    int position = checked.keyAt(i);

                    // Add sport if it is checked i.e.) == TRUE!
                    if (checked.valueAt(i)) selectedItems.add(watch_list_adapter.getItem(position));
                }

                String[] watchedApps = new String[selectedItems.size()];

                for (int i = 0; i < selectedItems.size(); i++)
                {
                    watchedApps[i] = selectedItems.get(i);
                }

                Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
                Intent serviceIntent = new Intent(getApplicationContext(), WatchAppService.class);

                // Create a bundle object
                Bundle b = new Bundle();
                b.putStringArray("watchedApps", watchedApps);
                serviceIntent.putExtras(b);
                startService(serviceIntent);

                finish();
            }
        });
    }
}
