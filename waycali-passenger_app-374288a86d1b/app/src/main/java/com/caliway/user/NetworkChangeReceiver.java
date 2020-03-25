package com.caliway.user;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import com.general.files.MyApp;
import com.general.files.OpenNoLocationView;
import com.utils.Utils;

/**
 * Created by Admin on 31-08-2017.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Utils.printELog("NetworkStauts", "::stcheck::");
        checkNetworkSettings();

    }

    private void checkNetworkSettings() {
        Activity currentActivity = MyApp.getInstance().getCurrentAct();

        if (currentActivity != null) {

            if (currentActivity instanceof MainActivity) {

                ViewGroup viewGroup = (ViewGroup) currentActivity.findViewById(android.R.id.content);
                handleNetworkView(currentActivity, viewGroup);
            }
        }
    }

    private void handleNetworkView(Activity activity, ViewGroup viewGroup) {
        try {
            OpenNoLocationView.getInstance(activity, viewGroup).configView(true);
        } catch (Exception e) {

        }
    }
}