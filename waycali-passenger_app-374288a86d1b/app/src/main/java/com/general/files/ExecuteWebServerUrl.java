package com.general.files;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.rest.RestClient;
import com.utils.AppFunctions;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.MyProgressDialog;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 22-02-2016.
 */
public class ExecuteWebServerUrl {

    SetDataResponse setDataRes;

    HashMap<String, String> parameters;

    GeneralFunctions generalFunc;

    String responseString = "";

    boolean directUrl_value = false;
    String directUrl = "";

    boolean isLoaderShown = false;
    Context mContext;

    MyProgressDialog myPDialog;

    boolean isGenerateDeviceToken = false;
    String key_DeviceToken_param;
    InternetConnection intCheck;
    boolean isSetCancelable = true;

    boolean isTaskKilled = false;

    Call<Object> currentCall;

    public ExecuteWebServerUrl(Context mContext, HashMap<String, String> parameters) {
        this.parameters = parameters;
        this.mContext = mContext;
    }

    public ExecuteWebServerUrl(Context mContext, String directUrl, boolean directUrl_value) {
        this.directUrl = directUrl;
        this.directUrl_value = directUrl_value;
        this.mContext = mContext;
    }

    public void setLoaderConfig(Context mContext, boolean isLoaderShown, GeneralFunctions generalFunc) {
        this.isLoaderShown = isLoaderShown;
        this.generalFunc = generalFunc;
        this.mContext = mContext;
    }


    public void setIsDeviceTokenGenerate(boolean isGenerateDeviceToken, String key_DeviceToken_param, GeneralFunctions generalFunc) {
        this.isGenerateDeviceToken = isGenerateDeviceToken;
        this.key_DeviceToken_param = key_DeviceToken_param;
        this.generalFunc = generalFunc;
    }

    public void setCancelAble(boolean isSetCancelable) {
        this.isSetCancelable = isSetCancelable;
    }

    public void execute() {
        Utils.runGC();
        intCheck = new InternetConnection(mContext);

        if (!intCheck.isNetworkConnected() && !intCheck.check_int()) {
            fireResponse(); //0207
            return;
        }

        if (isLoaderShown == true) {
            myPDialog = new MyProgressDialog(mContext, isSetCancelable, generalFunc.retrieveLangLBl("Loading", "LBL_LOADING_TXT"));
            //  isSetCancelable = true;
            try {

                myPDialog.show();
            } catch (Exception e) {

            }
        }

        if (parameters != null) {
            GeneralFunctions generalFunc = new GeneralFunctions(mContext);
            parameters.put("tSessionId", generalFunc.getMemberId().equals("") ? "" : generalFunc.retrieveValue(Utils.SESSION_ID_KEY));

            parameters.put("GeneralUserType", Utils.app_type);
            parameters.put("GeneralMemberId", generalFunc.getMemberId());
            parameters.put("GeneralDeviceType", "" + Utils.deviceType);
            parameters.put("GeneralAppVersion", new AppFunctions(mContext).getAppVersion());
            parameters.put("vTimeZone", generalFunc.getTimezone());
            parameters.put("vUserDeviceCountry", Utils.getUserDeviceCountryCode(mContext));

//            parameters.put("WidthHeightOfGrid", "" + mContext.getResources().getDimensionPixelSize(R.dimen.category_grid_size));
//            parameters.put("WidthHeightOfFoodDetailIcon", "" + mContext.getResources().getDimensionPixelSize(R.dimen.food_detail_icon_size));
//            parameters.put("WidthOfBanner", "" + Utils.getWidthOfBanner(mContext, mContext.getResources().getDimensionPixelSize(R.dimen.category_banner_left_right_margin) * 2));
//            parameters.put("HeightOfBanner", "" + Utils.getHeightOfBanner(mContext, mContext.getResources().getDimensionPixelSize(R.dimen.category_banner_left_right_margin) * 2));
//
//            parameters.put("WidthOfFoodDetailBanner", "" + Utils.getWidthOfBanner(mContext, 0));
//            parameters.put("HeightOfFoodDetailBanner", "" + Utils.getHeightOfBanner(mContext, 0, "4:3"));
        }

        if (generalFunc != null) {
            GetDeviceToken getDeviceToken = new GetDeviceToken(generalFunc);

            getDeviceToken.setDataResponseListener(new GetDeviceToken.SetTokenResponse() {
                @Override
                public void onTokenFound(String vDeviceToken) {

                    if (isGenerateDeviceToken) {
                        if (!vDeviceToken.equals("")) {

                            if (parameters != null) {
                                parameters.put(key_DeviceToken_param, "" + vDeviceToken);
                                parameters.put("vFirebaseDeviceToken", vDeviceToken);
                            }
                            performPostCall();
                        } else {
                            responseString = "";
                            fireResponse();
                        }
                    } else {

                        if (parameters != null) {
                            parameters.put("vFirebaseDeviceToken", vDeviceToken);
                        }
                        performPostCall();

                    }

                }
            });
            getDeviceToken.execute();

        } else {
            performPostCall();
        }
    }

    public void performGetCall(String directUrl) {
        Call<Object> call = RestClient.getClient("GET", CommonUtilities.SERVER).getResponse(directUrl);

        Utils.printLog("Url", "::" + directUrl);
        currentCall = call;
        call.enqueue(new Callback<Object>() {

            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {

                    responseString = RestClient.getGSONBuilder().toJson(response.body());

                    fireResponse();
                } else {
                    if (response.errorBody() != null) {
                        responseString = RestClient.getGSONBuilder().toJson(response.errorBody());
                    } else {
                        responseString = "";
                    }
                    fireResponse();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Utils.printLog("DataError", "::" + t.getMessage());
                responseString = "";
                fireResponse();
            }

        });
    }

    public void performPostCall() {
        if (directUrl_value == true) {
            performGetCall(directUrl);
            return;
        }

       /* try {
            String webserviceUrl = CommonUtilities.SERVER_URL_WEBSERVICE + parameters.toString().replace(", ", "&");
            webserviceUrl = webserviceUrl.replace("{", "");
            webserviceUrl = webserviceUrl.replace("}", "");
            Utils.printLog("Api", webserviceUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        Call<Object> call = RestClient.getClient("POST",CommonUtilities.SERVER).getResponse(CommonUtilities.SERVER_WEBSERVICE_PATH,parameters);
        currentCall = call;
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Utils.printLog("Data", "response = " + new Gson().toJson(response.body()));
                if (response.isSuccessful()) {
                    // request successful (status code 200, 201)


                    responseString = RestClient.getGSONBuilder().toJson(response.body());

                    fireResponse();
                } else {
                    if (response.errorBody() != null) {
                        responseString = RestClient.getGSONBuilder().toJson(response.errorBody());
                    } else {
                        responseString = "";
                    }
                    fireResponse();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Utils.printLog("DataError", "::" + t.getMessage());
                responseString = "";
                fireResponse();
            }

        });
    }


    public void fireResponse() {
        if (myPDialog != null) {
            myPDialog.close();
        }

        if (setDataRes != null && isTaskKilled == false) {

            GeneralFunctions generalFunc = new GeneralFunctions(mContext);
            String message = Utils.checkText(responseString) ? generalFunc.getJsonValue(Utils.message_str, responseString) : null;

            if (message != null && message.equals("DO_RESTART")) {
                generalFunc.restartApp();
                Utils.runGC();
                return;
            } else {
                try {


                    if (mContext != null && mContext instanceof Activity) {
                        Activity act = (Activity) mContext;
                        if (!act.isFinishing()) {
                            if (message != null && message.equals("SESSION_OUT")) {
                                MyApp.getInstance().notifySessionTimeOut();
                                Utils.runGC();
                                return;
                            }

                        }
                    }


                } catch (Exception e) {

                }
                setDataRes.setResponse(responseString);
            }
        }
    }

    public void cancel(boolean value) {

        this.isTaskKilled = value;
        if (currentCall != null) {

            new AsyncTask<String, String, String>() {
                @Override
                protected String doInBackground(String... params) {
                    currentCall.cancel();
                    return "";
                }
            }.execute();
        }
    }


    public void setDataResponseListener(SetDataResponse setDataRes) {
        this.setDataRes = setDataRes;
    }

    public interface SetDataResponse {
        void setResponse(String responseString);
    }
}
