package com.general.files;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.caliway.user.AccountverificationActivity;
import com.caliway.user.MainActivity;
import com.caliway.user.RatingActivity;
import com.utils.CommonUtilities;
import com.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Admin on 29-06-2016.
 */
public class
OpenMainProfile {
    Context mContext;
    String responseString;
    boolean isCloseOnError;
    GeneralFunctions generalFun;
    String tripId = "";
    String eType = "";
    boolean isnotification = false;
    JSONObject userProfileJsonObj;

    public OpenMainProfile(Context mContext, String responseString, boolean isCloseOnError, GeneralFunctions generalFun) {
        this.mContext = mContext;
        //  this.responseString = responseString;
        this.isCloseOnError = isCloseOnError;
        this.generalFun = generalFun;

        this.responseString = generalFun.retrieveValue(Utils.USER_PROFILE_JSON);

        userProfileJsonObj = generalFun.getJsonObject(this.responseString);

        generalFun.storeData(Utils.DefaultCountry, generalFun.getJsonValueStr("vDefaultCountry", userProfileJsonObj));
        generalFun.storeData(Utils.DefaultCountryCode, generalFun.getJsonValueStr("vDefaultCountryCode", userProfileJsonObj));
        generalFun.storeData(Utils.DefaultPhoneCode, generalFun.getJsonValueStr("vDefaultPhoneCode", userProfileJsonObj));
    }

    public OpenMainProfile(Context mContext, String responseString, boolean isCloseOnError, GeneralFunctions generalFun, String tripId) {
        this.mContext = mContext;
        this.responseString = responseString;
        this.isCloseOnError = isCloseOnError;
        this.generalFun = generalFun;
        this.tripId = tripId;

        this.responseString = generalFun.retrieveValue(Utils.USER_PROFILE_JSON);

        userProfileJsonObj = generalFun.getJsonObject(this.responseString);
    }

    public void startProcess() {

        if (generalFun == null)
            return;

        generalFun.sendHeartBeat();

        setGeneralData();

        String vTripStatus = generalFun.getJsonValueStr("vTripStatus", userProfileJsonObj);
        String PaymentStatus_From_Passenger_str = "";
        String Ratings_From_Passenger_str = "";
        String vTripPaymentMode_str = "";
        String eVerified_str = "";

        JSONObject Last_trip_data = generalFun.getJsonObject("TripDetails", userProfileJsonObj);
        eType = generalFun.getJsonValueStr("eType", Last_trip_data);

        PaymentStatus_From_Passenger_str = generalFun.getJsonValueStr("PaymentStatus_From_Passenger", userProfileJsonObj);
        Ratings_From_Passenger_str = generalFun.getJsonValueStr("Ratings_From_Passenger", userProfileJsonObj);
        eVerified_str = generalFun.getJsonValueStr("eVerified", Last_trip_data);
        vTripPaymentMode_str = generalFun.getJsonValueStr("vTripPaymentMode", Last_trip_data);

        vTripPaymentMode_str = "Cash";// to remove paypal
        PaymentStatus_From_Passenger_str = "Approved"; // to remove paypal
        //  }

        Bundle bn = new Bundle();

        if (generalFun.getJsonValue("vPhone", userProfileJsonObj).equals("") || generalFun.getJsonValue("vEmail", userProfileJsonObj).equals("")) {
            //open account verification screen
            if (generalFun.getMemberId() != null && !generalFun.getMemberId().equals("")) {
                if (!generalFun.getMemberId().equals("")) {
                    new StartActProcess(mContext).startActWithData(AccountverificationActivity.class, bn);
                } else {
                    generalFun.restartApp();
                }
            }
        } else if (!vTripStatus.equals("Not Active") || ((PaymentStatus_From_Passenger_str.equals("Approved")
                || vTripPaymentMode_str.equals("Cash")) && Ratings_From_Passenger_str.equals("Done"))) {
                bn.putBoolean("isnotification", isnotification);
                new StartActProcess(mContext).startActWithData(MainActivity.class, bn);
        } else {
            if (!eType.equals("")) {
                 if (generalFun.getJsonValue("APP_TYPE", responseString).equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX)) {
                    if (eType.equals(Utils.CabGeneralType_UberX)) {
                        new StartActProcess(mContext).startActWithData(MainActivity.class, bn);
                    } else {
                        new StartActProcess(mContext).startActWithData(RatingActivity.class, bn);
                    }
                } else {
                    new StartActProcess(mContext).startActWithData(RatingActivity.class, bn);
                }
            }
        }
        try {
            ActivityCompat.finishAffinity((Activity) mContext);
        } catch (Exception e) {
        }
    }

    public void setGeneralData() {
        generalFun.storeData(Utils.PUBNUB_PUB_KEY, generalFun.getJsonValueStr("PUBNUB_PUBLISH_KEY", userProfileJsonObj));
        generalFun.storeData(Utils.PUBNUB_SUB_KEY, generalFun.getJsonValueStr("PUBNUB_SUBSCRIBE_KEY", userProfileJsonObj));
        generalFun.storeData(Utils.PUBNUB_SEC_KEY, generalFun.getJsonValueStr("PUBNUB_SECRET_KEY", userProfileJsonObj));
        generalFun.storeData(Utils.SESSION_ID_KEY, generalFun.getJsonValueStr("tSessionId", userProfileJsonObj));
        generalFun.storeData(Utils.RIDER_REQUEST_ACCEPT_TIME_KEY, generalFun.getJsonValueStr("RIDER_REQUEST_ACCEPT_TIME", userProfileJsonObj));
        generalFun.storeData(Utils.DEVICE_SESSION_ID_KEY, generalFun.getJsonValueStr("tDeviceSessionId", userProfileJsonObj));

        generalFun.storeData(Utils.SMS_BODY_KEY, generalFun.getJsonValueStr(Utils.SMS_BODY_KEY, userProfileJsonObj));
        generalFun.storeData("DESTINATION_UPDATE_TIME_INTERVAL", generalFun.getJsonValueStr("DESTINATION_UPDATE_TIME_INTERVAL", userProfileJsonObj));

        generalFun.storeData(Utils.FETCH_TRIP_STATUS_TIME_INTERVAL_KEY, generalFun.getJsonValueStr("FETCH_TRIP_STATUS_TIME_INTERVAL", userProfileJsonObj));

        generalFun.storeData(Utils.VERIFICATION_CODE_RESEND_TIME_IN_SECONDS_KEY, generalFun.getJsonValueStr(Utils.VERIFICATION_CODE_RESEND_TIME_IN_SECONDS_KEY, userProfileJsonObj));
        generalFun.storeData(Utils.VERIFICATION_CODE_RESEND_COUNT_KEY, generalFun.getJsonValueStr(Utils.VERIFICATION_CODE_RESEND_COUNT_KEY, userProfileJsonObj));
        generalFun.storeData(Utils.VERIFICATION_CODE_RESEND_COUNT_RESTRICTION_KEY, generalFun.getJsonValueStr(Utils.VERIFICATION_CODE_RESEND_COUNT_RESTRICTION_KEY, userProfileJsonObj));

        generalFun.storeData(Utils.APP_DESTINATION_MODE, generalFun.getJsonValueStr("APP_DESTINATION_MODE", userProfileJsonObj));
        generalFun.storeData(Utils.APP_TYPE, generalFun.getJsonValueStr("APP_TYPE", userProfileJsonObj));
        generalFun.storeData(Utils.SITE_TYPE_KEY, generalFun.getJsonValueStr("SITE_TYPE", userProfileJsonObj));
        generalFun.storeData(Utils.ENABLE_TOLL_COST, generalFun.getJsonValueStr("ENABLE_TOLL_COST", userProfileJsonObj));
        generalFun.storeData(Utils.TOLL_COST_APP_ID, generalFun.getJsonValueStr("TOLL_COST_APP_ID", userProfileJsonObj));
        generalFun.storeData(Utils.TOLL_COST_APP_CODE, generalFun.getJsonValueStr("TOLL_COST_APP_CODE", userProfileJsonObj));
        generalFun.storeData(Utils.HANDICAP_ACCESSIBILITY_OPTION, generalFun.getJsonValueStr("HANDICAP_ACCESSIBILITY_OPTION", userProfileJsonObj));
        generalFun.storeData(Utils.FEMALE_RIDE_REQ_ENABLE, generalFun.getJsonValueStr("FEMALE_RIDE_REQ_ENABLE", userProfileJsonObj));
        generalFun.storeData(Utils.PUBNUB_DISABLED_KEY, generalFun.getJsonValueStr("PUBNUB_DISABLED", userProfileJsonObj));
        generalFun.storeData(Utils.ENABLE_SOCKET_CLUSTER_KEY, generalFun.getJsonValueStr("ENABLE_SOCKET_CLUSTER", userProfileJsonObj));

        generalFun.storeData(Utils.SC_CONNECT_URL_KEY, generalFun.getJsonValueStr("SC_CONNECT_URL", userProfileJsonObj));
        generalFun.storeData(Utils.GOOGLE_SERVER_ANDROID_PASSENGER_APP_KEY, generalFun.getJsonValueStr("GOOGLE_SERVER_ANDROID_PASSENGER_APP_KEY", userProfileJsonObj));

        generalFun.storeData(Utils.ISWALLETBALNCECHANGE, "No");

        generalFun.removeValue("userHomeLocationLatitude");
        generalFun.removeValue("userHomeLocationLongitude");
        generalFun.removeValue("userHomeLocationAddress");
        generalFun.removeValue("userWorkLocationLatitude");
        generalFun.removeValue("userWorkLocationLongitude");
        generalFun.removeValue("userWorkLocationAddress");

        JSONArray userFavouriteAddressArr = generalFun.getJsonArray("UserFavouriteAddress", responseString);
        if (userFavouriteAddressArr != null && userFavouriteAddressArr.length() > 0) {

            for (int i = 0; i < userFavouriteAddressArr.length(); i++) {
                JSONObject dataItem = generalFun.getJsonObject(userFavouriteAddressArr, i);

                if (generalFun.getJsonValueStr("eType", dataItem).equalsIgnoreCase("HOME")) {

                    generalFun.storeData("userHomeLocationLatitude", generalFun.getJsonValueStr("vLatitude", dataItem));
                    generalFun.storeData("userHomeLocationLongitude", generalFun.getJsonValueStr("vLongitude", dataItem));
                    generalFun.storeData("userHomeLocationAddress", generalFun.getJsonValueStr("vAddress", dataItem));

                } else if (generalFun.getJsonValueStr("eType", dataItem).equalsIgnoreCase("WORK")) {
                    generalFun.storeData("userWorkLocationLatitude", generalFun.getJsonValueStr("vLatitude", dataItem));
                    generalFun.storeData("userWorkLocationLongitude", generalFun.getJsonValueStr("vLongitude", dataItem));
                    generalFun.storeData("userWorkLocationAddress", generalFun.getJsonValueStr("vAddress", dataItem));
                }

            }
        }


    }
}
