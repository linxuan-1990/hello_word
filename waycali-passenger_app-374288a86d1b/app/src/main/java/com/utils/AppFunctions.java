package com.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.caliway.user.BuildConfig;
import com.caliway.user.R;
import com.general.files.GeneralFunctions;
import com.general.files.LocalNotification;
import com.general.files.MyApp;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.view.SelectableRoundedImageView;

import org.json.JSONObject;

public class AppFunctions {

    Context mContext;
    GeneralFunctions generalFunc;

    public AppFunctions(Context mContext) {
        this.mContext = mContext;
        generalFunc = MyApp.getInstance().getGeneralFun(mContext);
    }

    public void checkProfileImage(SelectableRoundedImageView userProfileImgView, String userProfileJson, String imageKey) {
        String vImgName_str = generalFunc.getJsonValue(imageKey, userProfileJson);

        Picasso.with(mContext).load(CommonUtilities.USER_PHOTO_PATH + generalFunc.getMemberId() + "/" + vImgName_str).placeholder(R.mipmap.ic_no_pic_user).error(R.mipmap.ic_no_pic_user).into(userProfileImgView);
    }

    public static String getAppVersion() {
        return BuildConfig.VERSION_NAME;
    }

    public void checkProfileImage(SelectableRoundedImageView userProfileImgView, String userProfileJson, String imageKey, ImageView profilebackimage) {
        String vImgName_str = generalFunc.getJsonValue(imageKey, userProfileJson);

        Picasso.with(mContext).load(CommonUtilities.USER_PHOTO_PATH + generalFunc.getMemberId() + "/" + vImgName_str).placeholder(R.mipmap.ic_no_pic_user).error(R.mipmap.ic_no_pic_user).into(userProfileImgView);

        Picasso.with(mContext).load(CommonUtilities.USER_PHOTO_PATH + generalFunc.getMemberId() + "/" + vImgName_str).placeholder(R.mipmap.ic_no_pic_user).error(R.mipmap.ic_no_pic_user).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Utils.setBlurImage(bitmap, profilebackimage);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    public boolean isTripStatusMsgExist(String msg) {

        if (generalFunc.getJsonValue("iTripId", msg) != "") {

            String key = Utils.TRIP_REQ_CODE_PREFIX_KEY + generalFunc.getJsonValue("iTripId", msg) + generalFunc.getJsonValue("Message", msg);
            String data = generalFunc.retrieveValue(key);

            if (data == null || data.equals("")) {
                if (MyApp.getInstance().isMyAppInBackGround()) {


                    if (!generalFunc.getJsonValue("MsgType", msg).equalsIgnoreCase("TripRequestCancel")) {
                        LocalNotification.dispatchLocalNotification(MyApp.getCurrentAct(), generalFunc.getJsonValue("vTitle", msg), false);
                    }

                }
                generalFunc.storeData(key, System.currentTimeMillis() + "");
                return false;
            } else {
                return true;
            }
        }
        return false;
    }


    public boolean isTripStatusMsgExist(String msg, Context mContext) {

        JSONObject obj_tmp = generalFunc.getJsonObject(msg);

        if (obj_tmp != null) {

            String message = generalFunc.getJsonValueStr("Message", obj_tmp);

            if (!message.equals("")) {
                String iTripId = generalFunc.getJsonValueStr("iTripId", obj_tmp);

                if (!iTripId.equals("")) {
                    String vTitle = generalFunc.getJsonValueStr("vTitle", obj_tmp);
                    String time = generalFunc.getJsonValueStr("time", obj_tmp);
                    String key = Utils.TRIP_REQ_CODE_PREFIX_KEY + iTripId + "_" + message;
                    if (message.equals("DestinationAdded")) {
                        String destKey = key;

                        Long newMsgTime = generalFunc.parseLongValue(0, time);

                        String destKeyValueStr = generalFunc.retrieveValue(destKey, mContext);
                        if (!destKeyValueStr.equals("")) {

                            Long destKeyValue = generalFunc.parseLongValue(0, destKeyValueStr);

                            if (newMsgTime > destKeyValue) {
                                generalFunc.removeValue(destKey);
                            } else {
                                return true;
                            }
                        }
                    }

                    String data = generalFunc.retrieveValue(key);

                    if (data.equals("")) {
                        if (!message.equalsIgnoreCase("TripRequestCancel")) {
                            LocalNotification.dispatchLocalNotification(mContext, vTitle, true);
                        }
                        if (time.equals("")) {
                            generalFunc.storeData(key, "" + System.currentTimeMillis());
                        } else {
                            generalFunc.storeData(key, "" + time);
                        }
                        return false;
                    } else {
                        return true;
                    }
                }
            } else {
                String msgType = generalFunc.getJsonValueStr("MsgType", obj_tmp);
                if (!msgType.equals("") && msgType.equals("TripRequestCancel")) {
                    String iTripId = generalFunc.getJsonValueStr("iTripId", obj_tmp);

                    String key = Utils.TRIP_REQ_CODE_PREFIX_KEY + iTripId + "_" + msgType;
                    String data = generalFunc.retrieveValue(key);
                    if (!data.equals("")) {
                        return true;
                    }
                }

            }

        }

        return false;
    }
}
