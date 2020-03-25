package com.general.files;

import android.content.Context;

import com.caliway.user.AppLoignRegisterActivity;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.MyProgressDialog;

import java.util.HashMap;

import retrofit2.Call;

/**
 * Created by Admin on 29-06-2016.
 */
public class RegisterTwitterLoginResCallBack extends Callback<TwitterSession> {
    Context mContext;
    GeneralFunctions generalFunc;

    MyProgressDialog myPDialog;
    AppLoignRegisterActivity appLoginAct;

    public RegisterTwitterLoginResCallBack(Context mContext) {
        this.mContext = mContext;
        generalFunc = new GeneralFunctions(mContext);
        appLoginAct = (AppLoignRegisterActivity) mContext;

    }


    public void registerTwitterUser(final String email, final String fName, final String lName, final String fbId, String imageUrl) {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "LoginWithFB");
        parameters.put("vFirstName", fName);
        parameters.put("vLastName", lName);
        parameters.put("vEmail", email);
        parameters.put("iFBId", fbId);
        parameters.put("eLoginType", "Twitter");
        parameters.put("vDeviceType", Utils.deviceType);
        parameters.put("UserType", Utils.userType);
        parameters.put("vCurrency", generalFunc.retrieveValue(Utils.DEFAULT_CURRENCY_VALUE));
        parameters.put("vLang", generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY));
        parameters.put("vImageURL", imageUrl);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(mContext, parameters);
        exeWebServer.setLoaderConfig(mContext, true, generalFunc);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken", generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {
                    new SetUserData(responseString, generalFunc, mContext, true);
                    generalFunc.storeData(Utils.USER_PROFILE_JSON, generalFunc.getJsonValue(Utils.message_str, responseString));
                    new OpenMainProfile(mContext,
                            generalFunc.getJsonValue(Utils.message_str, responseString), false, generalFunc).startProcess();
                } else {
                    if (!generalFunc.getJsonValue(Utils.message_str, responseString).equals("DO_REGISTER")) {
                        generalFunc.showGeneralMessage("",
                                generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                    } else {


                        signupUser(email, fName, lName, fbId, imageUrl);

                    }

                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    @Override
    public void success(Result<TwitterSession> result) {
        try {
            final TwitterSession session = result.data;
            // TODO: Remove toast and use the TwitterSession's userID
            // with your app's user model
            String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";

            Call<User> user = TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(true, true, true);
            user.enqueue(new Callback<User>() {
                @Override
                public void success(Result<User> userResult) {
                    String name = userResult.data.name;
                    String email = userResult.data.email;


                    if (email == null) {
                        email = "";
                    }

                    String photoUrlBiggerSize = userResult.data.profileImageUrl.replace("_normal", "");

                    registerTwitterUser(email, name, "", session.getUserId() + "", photoUrlBiggerSize);
                }

                @Override
                public void failure(TwitterException exc) {
                    //   Log.d("TwitterKit", "Verify Credentials Failure", exc);
                }
            });


        } catch (Exception e) {
            Utils.printLog("twitter exception", e.toString());
        }


    }

    @Override
    public void failure(TwitterException exception) {
        Utils.printLog("twitter exception::", exception.toString());

    }

    public void signupUser(final String email, final String fName, final String lName, final String fbId, String imageUrl) {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "signup");
        parameters.put("vFirstName", fName);
        parameters.put("vLastName", lName);
        parameters.put("vEmail", email);
        parameters.put("vFbId", fbId);
        parameters.put("vDeviceType", Utils.deviceType);
        parameters.put("UserType", Utils.userType);
        parameters.put("vCurrency", generalFunc.retrieveValue(Utils.DEFAULT_CURRENCY_VALUE));
        parameters.put("vLang", generalFunc.retrieveValue(Utils.LANGUAGE_CODE_KEY));
        parameters.put("eSignUpType", "Twitter");
        parameters.put("vImageURL", imageUrl);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(mContext, parameters);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken", generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {
                    new SetUserData(responseString, generalFunc, mContext, true);
                    generalFunc.storeData(Utils.USER_PROFILE_JSON, generalFunc.getJsonValue(Utils.message_str, responseString));
                    new OpenMainProfile(mContext,
                            generalFunc.getJsonValue(Utils.message_str, responseString), false, generalFunc).startProcess();
                } else {


                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }
}
