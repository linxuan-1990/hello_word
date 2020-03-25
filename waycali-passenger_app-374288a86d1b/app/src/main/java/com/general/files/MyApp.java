package com.general.files;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.caliway.user.BuildConfig;
import com.caliway.user.LauncherActivity;
import com.caliway.user.MainActivity;
import com.caliway.user.NetworkChangeReceiver;
import com.caliway.user.R;
import com.facebook.appevents.AppEventsLogger;

import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.GenerateAlertBox;

import java.util.HashMap;

/**
 * Created by Admin on 28-06-2016.
 */
public class MyApp extends Application {
    private static MyApp mMyApp;
    private static Activity currentAct;

    GeneralFunctions generalFun;
    boolean isAppInBackground = true;

    private GpsReceiver mGpsReceiver;
    private NetworkChangeReceiver mNetWorkReceiver = null;

    public MainActivity mainAct;

    public static synchronized MyApp getInstance() {
        return mMyApp;
    }

    GenerateAlertBox generateSessionAlert;

    @Override
    public void onCreate() {
        super.onCreate();

        GeneralFunctions.storeData("SERVERURL", CommonUtilities.SERVER_URL, this);
        GeneralFunctions.storeData("SERVERWEBSERVICEPATH", CommonUtilities.SERVER_WEBSERVICE_PATH, this);
        GeneralFunctions.storeData("USERTYPE", BuildConfig.USER_TYPE, this);
        Utils.userType = BuildConfig.USER_TYPE;
        Utils.app_type = BuildConfig.USER_TYPE;
        Utils.USER_ID_KEY = BuildConfig.USER_ID_KEY;
        setScreenOrientation();
        mMyApp = (MyApp) this.getApplicationContext();

        try {
            AppEventsLogger.activateApp(this);
        } catch (Exception e) {
            Utils.printLog("FBError", "::" + e.toString());
        }

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        generalFun = MyApp.getInstance().getGeneralFun(this);
        if (mGpsReceiver == null)
            registerReceiver();

    }

    public GeneralFunctions getGeneralFun(Context mContext) {
        return new GeneralFunctions(mContext, R.id.backImgView);
    }

    public boolean isMyAppInBackGround() {
        return this.isAppInBackground;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();

        Utils.printLog("Api", "Object Destroyed >> MYAPP onLowMemory");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        Utils.printLog("Api", "Object Destroyed >> MYAPP onTrimMemory");

    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        Utils.printLog("Api", "Object Destroyed >> MYAPP onTerminate");
        removePubSub();
    }

    public void removePubSub() {
        releaseGpsReceiver();
        removeAllRunningInstances();
        terminatePuSubInstance();
    }


    private void releaseGpsReceiver() {
        if (mGpsReceiver != null)
            this.unregisterReceiver(mGpsReceiver);
        this.mGpsReceiver = null;
    }


    private void registerReceiver() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {

            IntentFilter mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);

            this.mGpsReceiver = new GpsReceiver();
            this.registerReceiver(this.mGpsReceiver, mIntentFilter);
        }
    }

    private void removeAllRunningInstances() {
        Utils.printELog("NetWorkDEMO", "removeAllRunningInstances called");
        connectReceiver(false);
    }

    private void registerNetWorkReceiver() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO && mNetWorkReceiver == null) {
            try {
                Utils.printELog("NetWorkDemo", "Network connectivity registered");
                IntentFilter mIntentFilter = new IntentFilter();
                mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                mIntentFilter.addAction(ConnectivityManager.EXTRA_NO_CONNECTIVITY);
                /*Extra Filter Started */
                mIntentFilter.addAction(ConnectivityManager.EXTRA_IS_FAILOVER);
                mIntentFilter.addAction(ConnectivityManager.EXTRA_REASON);
                mIntentFilter.addAction(ConnectivityManager.EXTRA_EXTRA_INFO);
                /*Extra Filter Ended */
//                mIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//                mIntentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");

                this.mNetWorkReceiver = new NetworkChangeReceiver();
                this.registerReceiver(this.mNetWorkReceiver, mIntentFilter);
            } catch (Exception e) {
                Utils.printELog("NetWorkDemo", "Network connectivity register error occurred");
            }
        }
    }

    private void unregisterNetWorkReceiver() {

        if (mNetWorkReceiver != null)
            try {
                Utils.printELog("NetWorkDemo", "Network connectivity unregistered");
                this.unregisterReceiver(mNetWorkReceiver);
                this.mNetWorkReceiver = null;
            } catch (Exception e) {
                Utils.printELog("NetWorkDemo", "Network connectivity register error occurred");
                e.printStackTrace();
            }

    }


    public void notifySessionTimeOut() {
        if (generateSessionAlert != null) {
            return;
        }

        generateSessionAlert = new GenerateAlertBox(MyApp.getInstance().getCurrentAct());
        generateSessionAlert.setContentMessage(generalFun.retrieveLangLBl("", "LBL_BTN_TRIP_CANCEL_CONFIRM_TXT"),
                generalFun.retrieveLangLBl("Your session is expired. Please login again.", "LBL_SESSION_TIME_OUT"));
        generateSessionAlert.setPositiveBtn(generalFun.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));
        generateSessionAlert.setCancelable(false);
        generateSessionAlert.setBtnClickList(btn_id -> {

            if (btn_id == 1) {
                onTerminate();
                generalFun.logOutUser(MyApp.this);

                (MyApp.getInstance().getGeneralFun(this)).restartApp();
            }
        });

        generateSessionAlert.showSessionOutAlertBox();
    }

    public void restartWithGetDataApp() {
        GetUserData objRefresh = new GetUserData(generalFun, MyApp.getInstance().getCurrentAct());
        objRefresh.getData();
    }

    public void restartWithGetDataApp(String tripId) {
        GetUserData objRefresh = new GetUserData(generalFun, MyApp.getInstance().getCurrentAct(), tripId);
        objRefresh.getData();
    }


    public void logOutFromDevice(boolean isForceLogout) {

        if (generalFun != null) {
            final HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("type", "callOnLogout");
            parameters.put("iMemberId", generalFun.getMemberId());
            parameters.put("UserType", Utils.userType);

            ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getCurrentAct(), parameters);
            exeWebServer.setLoaderConfig(getCurrentAct(), true, generalFun);

            exeWebServer.setDataResponseListener(responseString -> {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail == true) {

                        if (getCurrentAct() instanceof MainActivity) {
                            ((MainActivity) getCurrentAct()).releaseScheduleNotificationTask();
                        }

                        onTerminate();
                        generalFun.logOutUser(MyApp.this);

                        (new GeneralFunctions(getCurrentAct())).restartApp();

                    } else {
                        if (isForceLogout) {
                            generalFun.showGeneralMessage("",
                                    generalFun.retrieveLangLBl("", generalFun.getJsonValue(Utils.message_str, responseString)), buttonId -> (new GeneralFunctions(getCurrentAct())).restartApp());
                        } else {
                            generalFun.showGeneralMessage("",
                                    generalFun.retrieveLangLBl("", generalFun.getJsonValue(Utils.message_str, responseString)));
                        }
                    }
                } else {
                    if (isForceLogout) {
                        generalFun.showError(buttonId -> (new GeneralFunctions(getCurrentAct())).restartApp());
                    } else {
                        generalFun.showError();
                    }
                }
            });
            exeWebServer.execute();
        }
    }


    public void setScreenOrientation() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Utils.runGC();
                // new activity created; force its orientation to portrait
                activity.setRequestedOrientation(
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                activity.setTitle(getResources().getString(R.string.app_name));
                setCurrentAct(activity);
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                if (activity instanceof MainActivity) {
                    //Reset PubNub instance
                    configPuSubInstance();
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Utils.runGC();
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Utils.runGC();
                setCurrentAct(activity);
                isAppInBackground = false;

                LocalNotification.clearAllNotifications();

                if (currentAct instanceof MainActivity) {
                    ViewGroup viewGroup = (ViewGroup) currentAct.findViewById(android.R.id.content);
                    new Handler().postDelayed(() -> {
                        OpenNoLocationView.getInstance(currentAct, viewGroup).configView(false);
                    }, 1000);
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Utils.runGC();
                isAppInBackground = true;
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Utils.runGC();
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                /*Called to retrieve per-instance state from an activity before being killed so that the state can be restored in onCreate(Bundle) or onRestoreInstanceState(Bundle) (the Bundle populated by this method will be passed to both).*/
                removeAllRunningInstances();
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Utils.runGC();
                Utils.hideKeyboard(activity);
                if (activity instanceof MainActivity && mainAct == activity) {
                    mainAct = null;
                }
            }
        });
    }

    private void connectReceiver(boolean isConnect) {
        if (isConnect && mNetWorkReceiver == null) {
            registerNetWorkReceiver();
        } else if (!isConnect && mNetWorkReceiver != null) {
            unregisterNetWorkReceiver();
        }
    }


    public static Activity getCurrentAct() {
        return currentAct;
    }

    private void setCurrentAct(Activity currentAct) {
        this.currentAct = currentAct;

        if (currentAct instanceof LauncherActivity) {
            mainAct = null;
        }

        if (currentAct instanceof MainActivity) {
            mainAct = (MainActivity) currentAct;
        }

        connectReceiver(true);
    }

    private void configPuSubInstance() {
        ConfigPubNub.getInstance(true).buildPubSub();
    }

    private void terminatePuSubInstance() {
        if (ConfigPubNub.retrieveInstance() != null) {
            ConfigPubNub.getInstance().releasePubSubInstance();
        }
    }
}
