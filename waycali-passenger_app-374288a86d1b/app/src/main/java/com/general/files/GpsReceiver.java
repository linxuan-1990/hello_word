package com.general.files;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.ViewGroup;

import com.caliway.user.MainActivity;

import java.util.List;

/**
 * Created by Admin on 23-11-2016.
 */
public class GpsReceiver extends BroadcastReceiver {
    Context context;
    MyApp mApplication;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        mApplication = ((MyApp) context.getApplicationContext());
        checkGps(context);


    }

    public void checkGps(Context context) {
        checkGPSSettings();
    }

    private void checkGPSSettings() {
        Activity currentActivity = MyApp.getInstance().getCurrentAct();

        if (currentActivity != null) {

            if (currentActivity instanceof MainActivity) {

                ViewGroup viewGroup = (ViewGroup) currentActivity.findViewById(android.R.id.content);
                handleGPSView(currentActivity, viewGroup);
            }
        }
    }

    private void handleGPSView(Activity activity, ViewGroup viewGroup) {
        try {
            OpenNoLocationView.getInstance(activity, viewGroup).configView(false);
        } catch (Exception e) {

        }
    }
}
