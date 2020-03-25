package com.general.files;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.caliway.user.ChatActivity;
import com.caliway.user.ConfirmEmergencyTapActivity;
import com.caliway.user.RatingActivity;
import com.utils.AppFunctions;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.GenerateAlertBox;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by Admin on 20/03/18.
 */

public class FireTripStatusMsg {

    Context mContext;
    private static String tmp_msg_chk = "";


    public FireTripStatusMsg() {
    }

    public FireTripStatusMsg(Context mContext) {
        this.mContext = mContext;
    }

    public void fireTripMsg(String message) {

        Utils.printLog("fireTripMsg", ":: called");
        if (tmp_msg_chk.equals(message)) {
            return;
        }
        tmp_msg_chk = message;

        Utils.printELog("SocketApp", "::MsgReceived::" + message);
        String finalMsg = message;

        if (!GeneralFunctions.isJsonObj(finalMsg)) {
            try {
                finalMsg = new JSONTokener(message).nextValue().toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (!GeneralFunctions.isJsonObj(finalMsg)) {
                finalMsg = finalMsg.replaceAll("^\"|\"$", "");

                if (!GeneralFunctions.isJsonObj(finalMsg)) {
                    finalMsg = message.replaceAll("\\\\", "");

                    finalMsg = finalMsg.replaceAll("^\"|\"$", "");

                    if (!GeneralFunctions.isJsonObj(finalMsg)) {
                        finalMsg = message.replace("\\\"", "\"").replaceAll("^\"|\"$", "");
                    }

                    finalMsg = finalMsg.replace("\\\\\"", "\\\"");
                }
            }
        }

        if (MyApp.getInstance() == null) {
            if (mContext != null) {
                dispatchNotification(finalMsg);
            }
            return;
        }

        if (MyApp.getInstance().getCurrentAct() != null) {
            mContext = MyApp.getInstance().getCurrentAct();
        }

        if (mContext == null) {
            dispatchNotification(finalMsg);
            return;
        }

        GeneralFunctions generalFunc = new GeneralFunctions(mContext);
        JSONObject obj_msg = generalFunc.getJsonObject(finalMsg);
        String tSessionId = generalFunc.getJsonValueStr("tSessionId", obj_msg);

        if (!tSessionId.equals("") && !tSessionId.equals(generalFunc.retrieveValue(Utils.SESSION_ID_KEY))) {
            return;
        }

        if (!GeneralFunctions.isJsonObj(finalMsg)) {
            LocalNotification.dispatchLocalNotification(mContext, message, true);
            generalFunc.showGeneralMessage("", message);
            return;
        }

        boolean isMsgExist = new AppFunctions(mContext).isTripStatusMsgExist(finalMsg);

        if (isMsgExist == true) {
            return;
        }

        if (mContext instanceof Activity) {
            ((Activity) mContext).runOnUiThread(() -> continueDispatchMsg(generalFunc, obj_msg));
        } else {
            dispatchNotification(finalMsg);
        }

    }

    private void continueDispatchMsg(GeneralFunctions generalFunc, JSONObject obj_msg) {
        String messageStr = generalFunc.getJsonValueStr("Message", obj_msg);

        String vTitle = generalFunc.getJsonValueStr("vTitle", obj_msg);
        String eType = generalFunc.getJsonValueStr("eType", obj_msg);

        if (messageStr.equals("")) {

            String msgTypeStr = generalFunc.getJsonValueStr("MsgType", obj_msg);
            //   String messageType_str = generalFunc.getJsonValueStr("MessageType", obj_msg);

            if (msgTypeStr.equalsIgnoreCase("CHAT")) {
                LocalNotification.dispatchLocalNotification(mContext, generalFunc.getJsonValueStr("Msg", obj_msg), true);


                if (MyApp.getInstance().getCurrentAct() instanceof ChatActivity == false) {

                    Bundle bn = new Bundle();

                    bn.putString("iFromMemberId", generalFunc.getJsonValueStr("iFromMemberId", obj_msg));
                    bn.putString("FromMemberImageName", generalFunc.getJsonValueStr("FromMemberImageName", obj_msg));
                    bn.putString("iTripId", generalFunc.getJsonValueStr("iTripId", obj_msg));
                    bn.putString("FromMemberName", generalFunc.getJsonValueStr("FromMemberName", obj_msg));

                    Intent chatActInt = new Intent(MyApp.getInstance().getApplicationContext(), ChatActivity.class);

                    chatActInt.putExtras(bn);

                    chatActInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    MyApp.getInstance().getApplicationContext().startActivity(chatActInt);
                }

            }

        } else {

            if (messageStr.equalsIgnoreCase("TripCancelledByDriver") || messageStr.equalsIgnoreCase("DestinationAdded") || messageStr.equalsIgnoreCase("TripEnd")) {


                if (messageStr.equalsIgnoreCase("TripEnd") || messageStr.equalsIgnoreCase("TripCancelledByDriver")) {
                    generalFunc.storeData(Utils.ISWALLETBALNCECHANGE, "Yes");
                }

                if (eType.equalsIgnoreCase(Utils.CabGeneralType_UberX)) {

                    String iDriverId = generalFunc.getJsonValueStr("iDriverId", obj_msg);
                    String iTripId = generalFunc.getJsonValueStr("iTripId", obj_msg);
                    String showTripFare = generalFunc.getJsonValueStr("ShowTripFare", obj_msg);

                    if (messageStr.equalsIgnoreCase("TripEnd") || showTripFare.equalsIgnoreCase("true")) {
                        showPubnubGeneralMessage(generalFunc, iTripId, vTitle, false, true);
                    } else {

                        if (MyApp.getInstance().getCurrentAct() instanceof ChatActivity || MyApp.getInstance().getCurrentAct() instanceof ConfirmEmergencyTapActivity) {

                            String tripId = "";
                            if (MyApp.getInstance().getCurrentAct() instanceof ChatActivity) {
                                ChatActivity activity = (ChatActivity) MyApp.getInstance().getCurrentAct();
                                tripId = activity.data_trip_ada.get("iTripId");
                            } else if (MyApp.getInstance().getCurrentAct() instanceof ConfirmEmergencyTapActivity) {
                                ConfirmEmergencyTapActivity activity = (ConfirmEmergencyTapActivity) MyApp.getInstance().getCurrentAct();
                                tripId = activity.iTripId;
                            }

                            if (!tripId.equalsIgnoreCase("") && iTripId.equalsIgnoreCase(tripId)) {
                                generalFunc.showGeneralMessage("", vTitle, "", generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"),
                                        buttonId -> {
                                            MyApp.getInstance().restartWithGetDataApp();

                                        });
                            } else {
                                generalFunc.showGeneralMessage("", vTitle);
                            }

                        } else {
                            generalFunc.showGeneralMessage("", vTitle);
                        }
                    }


                } else {
                    final GenerateAlertBox generateAlert = new GenerateAlertBox(mContext);
                    generateAlert.setCancelable(false);
//                    generateAlert.setSystemAlertWindow(true);
                    generateAlert.setBtnClickList(btn_id -> MyApp.getInstance().restartWithGetDataApp());
                    generateAlert.setContentMessage("", vTitle);
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                    generateAlert.showAlertBox();
                }


                return;
            } else if (messageStr.equalsIgnoreCase("TripStarted") || messageStr.equalsIgnoreCase("DriverArrived")) {
                generalFunc.showGeneralMessage("", vTitle);
            }

        }

        if (MyApp.getInstance().mainAct != null && (!eType.equalsIgnoreCase(Utils.CabGeneralType_UberX) || (eType.equalsIgnoreCase(Utils.CabGeneralType_UberX) && messageStr.equalsIgnoreCase("CabRequestAccepted")))) {
            MyApp.getInstance().mainAct.pubNubMsgArrived(obj_msg.toString());
        }
    }

    public void showPubnubGeneralMessage(GeneralFunctions generalFunc, final String iTripId, final String message, final boolean isrestart, final boolean isufxrate) {
        try {

            final GenerateAlertBox generateAlert = new GenerateAlertBox(MyApp.getInstance().getCurrentAct());
            generateAlert.setContentMessage("", message);
            generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("Ok", "LBL_BTN_OK_TXT"));
            generateAlert.setBtnClickList(btn_id -> {
                generateAlert.closeAlertBox();

                if (isrestart) {
                    MyApp.getInstance().restartWithGetDataApp();
                }

                if (isufxrate) {
                    Bundle bn = new Bundle();
                    bn.putBoolean("isUfx", true);
                    bn.putString("iTripId", iTripId);
                    new StartActProcess(mContext).startActWithData(RatingActivity.class, bn);
                }


            });

            generateAlert.showAlertBox();


        } catch (Exception e) {
            Utils.printLog("AlertEx", e.toString());
        }
    }

    private void dispatchNotification(String message) {
        Context mLocContext = this.mContext;

        if (mLocContext == null && MyApp.getInstance() != null && MyApp.getInstance().getCurrentAct() == null) {
            mLocContext = MyApp.getInstance().getApplicationContext();
        }

//        if (mLocContext != null && MyApp.getInstance().getCurrentAct() == null) {
        if (mLocContext != null) {
            GeneralFunctions generalFunc = new GeneralFunctions(mLocContext);

            if (!GeneralFunctions.isJsonObj(message)) {
                LocalNotification.dispatchLocalNotification(mLocContext, message, true);

                return;
            }

            JSONObject obj_msg = generalFunc.getJsonObject(message);

            String message_str = generalFunc.getJsonValueStr("Message", obj_msg);

            if (message_str.equals("")) {
                String msgType_str = generalFunc.getJsonValueStr("MsgType", obj_msg);

                switch (msgType_str) {
                    case "CHAT":
                        generalFunc.storeData("OPEN_CHAT", obj_msg.toString());
                        LocalNotification.dispatchLocalNotification(mLocContext, generalFunc.getJsonValueStr("Msg", obj_msg), false);
                        break;
                }

            } else {
                String title_msg = generalFunc.getJsonValueStr("vTitle", obj_msg);
                switch (message) {

                    case "TripCancelledByDriver":
                    case "DriverArrived":
                    case "DestinationAdded":
                    case "TripStarted":
                    case "TripEnd":
                        LocalNotification.dispatchLocalNotification(mLocContext, title_msg, false);
                        break;
                }
            }
        }
    }
}
