package com.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.caliway.user.MainActivity;
import com.caliway.user.R;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.GetAddressFromLocation;
import com.general.files.MyApp;
import com.squareup.picasso.Picasso;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.GenerateAlertBox;
import com.view.MTextView;
import com.view.SelectableRoundedImageView;
import com.view.simpleratingbar.SimpleRatingBar;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class DriverDetailFragment extends Fragment implements GetAddressFromLocation.AddressFound, ViewTreeObserver.OnGlobalLayoutListener {

    int PICK_CONTACT = 2121;

    View view;
    MainActivity mainAct;
    GeneralFunctions generalFunc;

    String driverPhoneNum = "";

    DriverDetailFragment driverDetailFragment;

    String userProfileJson;

    String vDeliveryConfirmCode = "";
    LinearLayout cancelarea, contactarea;
    View contactview;
    SimpleRatingBar ratingBar;
    GetAddressFromLocation getAddressFromLocation;
    HashMap<String, String> tripDataMap;
    public int fragmentWidth = 0;
    public int fragmentHeight = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view != null) {
            return view;
        }
        view = inflater.inflate(R.layout.fragment_driver_detail, container, false);

        cancelarea = (LinearLayout) view.findViewById(R.id.cancelarea);
        contactarea = (LinearLayout) view.findViewById(R.id.contactarea);
        contactview = (View) view.findViewById(R.id.contactview);
        ratingBar = (SimpleRatingBar) view.findViewById(R.id.ratingBar);

        mainAct = (MainActivity) getActivity();
        userProfileJson = mainAct.userProfileJson;
        generalFunc = mainAct.generalFunc;

        getAddressFromLocation = new GetAddressFromLocation(getActivity(), generalFunc);
        getAddressFromLocation.setAddressList(this);


        setLabels();
        setData();

        addGlobalLayoutListner();

        driverDetailFragment = mainAct.getDriverDetailFragment();

        mainAct.setDriverImgView(((SelectableRoundedImageView) view.findViewById(R.id.driverImgView)));

        if (generalFunc.getJsonValue("vTripStatus", userProfileJson).equals("On Going Trip")) {

            configTripStartView(vDeliveryConfirmCode);

        }

        new CreateRoundedView(Color.parseColor("#535353"), Utils.dipToPixels(mainAct.getActContext(), 5), 2,
                mainAct.getActContext().getResources().getColor(android.R.color.transparent), (view.findViewById(R.id.numberPlateArea)));
        return view;
    }


    public void getMaskNumber() {
        HashMap<String, String> tripDataMap = (HashMap<String, String>) getArguments().getSerializable("TripData");
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getCallMaskNumber");
        parameters.put("iTripid", tripDataMap.get("iTripId"));
        parameters.put("UserType", Utils.userType);
        parameters.put("iMemberId", generalFunc.getMemberId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(mainAct.getActContext(), parameters);
        exeWebServer.setLoaderConfig(mainAct.getActContext(), true, generalFunc);

        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {
                    String message = generalFunc.getJsonValue(Utils.message_str, responseString);
                    call(message);
                } else {
                    call(driverPhoneNum);

                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    private void setRatingStarColor(Drawable drawable, @ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DrawableCompat.setTint(drawable, color);
        } else {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    public HashMap<String, String> getTripData() {

        HashMap<String, String> tripDataMap = (HashMap<String, String>) getArguments().getSerializable("TripData");


        return tripDataMap;
    }

    public void setLabels() {
        ((MTextView) view.findViewById(R.id.slideUpForDetailTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_SLIDE_UP_DETAIL"));
        ((MTextView) view.findViewById(R.id.contact_btn)).setText(generalFunc.retrieveLangLBl("", "LBL_CALL_TXT"));
        ((MTextView) view.findViewById(R.id.btn_share_txt)).setText(generalFunc.retrieveLangLBl("", "LBL_SHARE_BTN_TXT"));
        ((MTextView) view.findViewById(R.id.btn_cancle_trip)).setText(generalFunc.retrieveLangLBl("", "LBL_BTN_CANCEL_TRIP_TXT"));
        ((MTextView) view.findViewById(R.id.btn_message)).setText(generalFunc.retrieveLangLBl("", "LBL_MESSAGE_TXT"));
    }

    public void setData() {
        tripDataMap = (HashMap<String, String>) getArguments().getSerializable("TripData");

        ((MTextView) view.findViewById(R.id.driver_car_model)).setText(tripDataMap.get("DriverCarModelName"));

        if (tripDataMap.get("DriverCarColour") != null && !tripDataMap.get("DriverCarColour").equals("")) {
            ((MTextView) view.findViewById(R.id.driver_car_type)).setText("(" + tripDataMap.get("DriverCarColour").toUpperCase() + ")");
        } else {


            if (!generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase("UberX")) {
                ((LinearLayout) view.findViewById(R.id.driverCarDetailArea)).setVisibility(View.VISIBLE);
                ((MTextView) view.findViewById(R.id.driver_car_type)).setText("(" + tripDataMap.get("vVehicleType") + ")");
            } else {
                ((LinearLayout) view.findViewById(R.id.driverCarDetailArea)).setVisibility(View.GONE);
//            ((MTextView) view.findViewById(R.id.driver_car_type)).setText(tripDataMap.get("vVehicleType") + "-" + tripDataMap.get("iVehicleCatName"));
            }

        }


        ((MTextView) view.findViewById(R.id.driver_name)).setText(tripDataMap.get("DriverName"));
        //  ((MTextView) view.findViewById(R.id.driver_car_type)).setText("("+tripDataMap.get("vVehicleType")+")");
        ((MTextView) view.findViewById(R.id.txt_rating)).setText(tripDataMap.get("DriverRating"));
        ratingBar.setRating(generalFunc.parseFloatValue(0, tripDataMap.get("DriverRating")));
        ((MTextView) view.findViewById(R.id.driver_car_name)).setText(tripDataMap.get("DriverCarName"));
        ((MTextView) view.findViewById(R.id.driver_car_model)).setText(tripDataMap.get("DriverCarModelName"));


        ((MTextView) view.findViewById(R.id.numberPlate_txt)).setText(tripDataMap.get("DriverCarPlateNum"));

        // driverPhoneNum = tripDataMap.get("vCode") + tripDataMap.get("DriverPhone");

        String phoneCode = tripDataMap.get("DriverPhoneCode") != null && Utils.checkText(tripDataMap.get("DriverPhoneCode")) ? "+" + tripDataMap.get("DriverPhoneCode") : "";

        driverPhoneNum = tripDataMap.get("DriverPhone");
        vDeliveryConfirmCode = tripDataMap.get("vDeliveryConfirmCode");
        String driverImageName = tripDataMap.get("DriverImage");
        if (driverImageName == null || driverImageName.equals("") || driverImageName.equals("NONE")) {
            ((SelectableRoundedImageView) view.findViewById(R.id.driverImgView)).setImageResource(R.mipmap.ic_no_pic_user);
        } else {
            String image_url = CommonUtilities.SERVER_URL_PHOTOS + "upload/Driver/" + tripDataMap.get("iDriverId") + "/"
                    + tripDataMap.get("DriverImage");
            Picasso.with(mainAct.getActContext())
                    .load(image_url)
                    .placeholder(R.mipmap.ic_no_pic_user)
                    .error(R.mipmap.ic_no_pic_user)
                    .into(((SelectableRoundedImageView) view.findViewById(R.id.driverImgView)));
        }

        mainAct.registerForContextMenu(view.findViewById(R.id.contact_btn));
        (view.findViewById(R.id.contactarea)).setOnClickListener(new setOnClickList());
        (view.findViewById(R.id.sharearea)).setOnClickListener(new setOnClickList());
        (view.findViewById(R.id.cancelarea)).setOnClickListener(new setOnClickList());
        (view.findViewById(R.id.msgarea)).setOnClickListener(new setOnClickList());
    }

    public String getDriverPhone() {
        return driverPhoneNum;
    }

    public void configTripStartView(String vDeliveryConfirmCode) {

        (view.findViewById(R.id.btn_cancle_trip)).setVisibility(View.GONE);
        cancelarea.setVisibility(View.GONE);

        if (!vDeliveryConfirmCode.trim().equals("") && !generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase("UberX")) {

            mainAct.setUserLocImgBtnMargin(100);
            mainAct.setPanelHeight(205);
            this.vDeliveryConfirmCode = vDeliveryConfirmCode;
            ((MTextView) view.findViewById(R.id.deliveryConfirmCodeTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_DELIVERY_CONFIRMATION_CODE_TXT") + ": " + vDeliveryConfirmCode);
            ((MTextView) view.findViewById(R.id.deliveryConfirmCodeTxt)).setVisibility(View.VISIBLE);
        }
    }

    public void sendMsg(String phoneNumber) {
        try {

            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", "" + phoneNumber);
            startActivity(smsIntent);

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void call(String phoneNumber) {
        try {

            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void buildWarningMessage(String message, String positiveBtn, String negativeBtn, final boolean isCancelTripWarning) {

        android.support.v7.app.AlertDialog alertDialog;
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mainAct.getActContext());
        builder.setTitle("");
        builder.setCancelable(false);
        builder.setMessage(message);

        builder.setPositiveButton(positiveBtn, (dialog, which) -> {
            dialog.cancel();
            if (isCancelTripWarning == true) {
                cancelTrip("No");
            } else {
                //generalFunc.restartApp();
                MyApp.getInstance().restartWithGetDataApp();
            }
        });
        builder.setNegativeButton(negativeBtn, (dialog, which) -> dialog.cancel());


        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void cancelTrip(String eConfirmByUser) {
        HashMap<String, String> tripDataMap = (HashMap<String, String>) getArguments().getSerializable("TripData");


        if (tripDataMap.get("DriverCarColour") != null && !tripDataMap.get("DriverCarColour").equals("")) {
            ((MTextView) view.findViewById(R.id.driver_car_type)).setText("(" + tripDataMap.get("DriverCarColour").toUpperCase() + ")");
        } else {


            if (!generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase("UberX")) {
                ((LinearLayout) view.findViewById(R.id.driverCarDetailArea)).setVisibility(View.VISIBLE);
                ((MTextView) view.findViewById(R.id.driver_car_type)).setText("(" + tripDataMap.get("vVehicleType") + ")");
            } else {
                ((LinearLayout) view.findViewById(R.id.driverCarDetailArea)).setVisibility(View.GONE);
//            ((MTextView) view.findViewById(R.id.driver_car_type)).setText(tripDataMap.get("vVehicleType") + "-" + tripDataMap.get("iVehicleCatName"));
            }

        }

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "cancelTrip");
        parameters.put("UserType", Utils.app_type);
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("iDriverId", tripDataMap.get("iDriverId"));
        parameters.put("iTripId", tripDataMap.get("iTripId"));
        parameters.put("eConfirmByUser", eConfirmByUser);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActivity(), parameters);
        exeWebServer.setLoaderConfig(mainAct.getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {
                    GenerateAlertBox generateAlert = new GenerateAlertBox(mainAct.getActContext());
                    generateAlert.setCancelable(false);
                    generateAlert.setBtnClickList(btn_id -> MyApp.getInstance().restartWithGetDataApp())
                    ;
                    String msg = "";

                    if (tripDataMap.get("eType").equalsIgnoreCase(Utils.CabGeneralType_Ride)) {
                        msg = generalFunc.retrieveLangLBl("", "LBL_SUCCESS_TRIP_CANCELED");
                    } else if (tripDataMap.get("eType").equalsIgnoreCase("Deliver")) {
                        msg = generalFunc.retrieveLangLBl("", "LBL_SUCCESS_DELIVERY_CANCELED");

                    } else {
                        msg = generalFunc.retrieveLangLBl("", "LBL_SUCCESS_TRIP_CANCELED");
                    }
                    generateAlert.setContentMessage("", msg);
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                    generateAlert.showAlertBox();

                } else {

                    if (generalFunc.getJsonValue("isCancelChargePopUpShow", responseString).equalsIgnoreCase("Yes")) {

                        final GenerateAlertBox generateAlert = new GenerateAlertBox(mainAct.getActContext());
                        generateAlert.setCancelable(false);
                        generateAlert.setBtnClickList(btn_id -> {
                            if (btn_id == 0) {
                                generateAlert.closeAlertBox();

                            } else {
                                generateAlert.closeAlertBox();
                                cancelTrip("Yes");

                            }

                        });
                        generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_YES"));
                        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_NO"));
                        generateAlert.showAlertBox();

                        return;
                    }
                    buildWarningMessage(generalFunc.retrieveLangLBl("", "LBL_REQUEST_FAILED_PROCESS"),
                            generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), "", false);
                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CONTACT && data != null) {
            Uri uri = data.getData();

            if (uri != null) {
                Cursor c = null;
                try {
                    c = mainAct.getContentResolver().query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.TYPE}, null, null, null);

                    if (c != null && c.moveToFirst()) {
                        String number = c.getString(0);

                        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                        smsIntent.setType("vnd.android-dir/mms-sms");
                        smsIntent.putExtra("address", "" + number);

                        String link_location = "http://maps.google.com/?q=" + mainAct.userLocation.getLatitude() + "," + mainAct.userLocation.getLongitude();
                        smsIntent.putExtra("sms_body", generalFunc.retrieveLangLBl("", "LBL_SEND_STATUS_CONTENT_TXT") + " " + link_location);
                        startActivity(smsIntent);
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.hideKeyboard(getActivity());
    }

    @Override
    public void onAddressFound(String address, double latitude, double longitude, String geocodeobject) {

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "");

        String link_location = "http://maps.google.com/?q=" + address.replace(" ", "%20");

        sharingIntent.putExtra(Intent.EXTRA_TEXT, generalFunc.retrieveLangLBl("", "LBL_SEND_STATUS_CONTENT_TXT") + " " + link_location);
        startActivity(Intent.createChooser(sharingIntent, "Share using"));

    }

    @Override
    public void onResume() {
        super.onResume();
        addGlobalLayoutListner();
    }

    @Override
    public void onGlobalLayout() {
        boolean heightChanged = false;
        if (getView() != null || view != null) {
            if (getView() != null) {

                if (getView().getHeight() != 0 && getView().getHeight() != fragmentHeight) {
                    heightChanged = true;
                }
                fragmentWidth = getView().getWidth();
                fragmentHeight = getView().getHeight();
            } else if (view != null) {

                if (view.getHeight() != 0 && view.getHeight() != fragmentHeight) {
                    heightChanged = true;
                }

                fragmentWidth = view.getWidth();
                fragmentHeight = view.getHeight();
            }

            Utils.printELog("FragHeight", "is :::" + fragmentHeight + "\n" + "Frag Width is :::" + fragmentWidth);

            if (heightChanged && fragmentWidth != 0 && fragmentHeight != 0) {
                mainAct.setPanelHeight(fragmentHeight);
            }
        }
    }

    private void addGlobalLayoutListner() {
        if (getView() != null) {
            getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
        if (view != null) {
            view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }

        if (getView() != null) {

            getView().getViewTreeObserver().addOnGlobalLayoutListener(this);
        } else if (view != null) {
            view.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }
    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(getActivity());
            switch (view.getId()) {
                case R.id.contactarea:
                    getMaskNumber();
                    break;
                case R.id.sharearea:
                    if (mainAct != null && mainAct.driverAssignedHeaderFrag != null && mainAct.driverAssignedHeaderFrag.driverLocation != null) {
                        getAddressFromLocation.setLocation(mainAct.driverAssignedHeaderFrag.driverLocation.latitude, mainAct.driverAssignedHeaderFrag.driverLocation.longitude);
                        getAddressFromLocation.setLoaderEnable(true);
                        getAddressFromLocation.execute();
                    }

                    break;

                case R.id.cancelarea:
                    String msg = "";

                    if (tripDataMap.get("eType").equalsIgnoreCase(Utils.CabGeneralType_Ride)) {
                        msg = generalFunc.retrieveLangLBl("", "LBL_TRIP_CANCEL_TXT");
                    } else {
                        msg = generalFunc.retrieveLangLBl("", "LBL_DELIVERY_CANCEL_TXT");
                    }
                    buildWarningMessage(
                            msg, generalFunc.retrieveLangLBl("", "LBL_YES"), generalFunc.retrieveLangLBl("", "LBL_NO")
                            , true);
                    break;

                case R.id.msgarea:
                    mainAct.chatMsg();
                    break;
            }
        }

    }

}
