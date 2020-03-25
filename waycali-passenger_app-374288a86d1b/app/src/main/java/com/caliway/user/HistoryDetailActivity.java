package com.caliway.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.SelectableRoundedImageView;
import com.view.editBox.MaterialEditText;
import com.view.simpleratingbar.SimpleRatingBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.caliway.user.R.id.ratingBar;
import static com.utils.Utils.APP_DESTINATION_MODE;

public class HistoryDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    public GeneralFunctions generalFunc;
    MTextView titleTxt;
    MTextView subTitleTxt;
    ImageView backImgView;
    GoogleMap gMap;
    LinearLayout fareDetailDisplayArea;

    LinearLayout beforeServiceArea, afterServiceArea;
    String before_serviceImg_url = "";
    String after_serviceImg_url = "";
    String isRatingDone = "";
    MButton btn_type2;
    String userProfileJson;
    MTextView ratingDriverHTxt;
    LinearLayout profilebgarea;
    MTextView cartypeTxt;
    MTextView ufxratingDriverHTxt;
    SimpleRatingBar ufxratingBar;
    MTextView tipHTxt, tipamtTxt, tipmsgTxt;
    CardView tiparea;
    private int rateBtnId;
    private MaterialEditText commentBox;
    MTextView helpTxt;
    ImageView tipPluseImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);


        generalFunc = new GeneralFunctions(getActContext());


        helpTxt = (MTextView) findViewById(R.id.helpTxt);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        subTitleTxt = (MTextView) findViewById(R.id.subTitleTxt);
        commentBox = (MaterialEditText) findViewById(R.id.commentBox);
        View commentArea = findViewById(R.id.commentArea);
        tipPluseImage = (ImageView) findViewById(R.id.tipPluseImage);
        commentBox.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
        commentBox.setSingleLine(false);
        commentBox.setHideUnderline(true);
        commentBox.setGravity(GravityCompat.START | Gravity.TOP);
        commentBox.setLines(5);

        commentBox.setBothText("", generalFunc.retrieveLangLBl("", "LBL_WRITE_COMMENT_HINT_TXT"));

        new CreateRoundedView(Color.parseColor("#FFFFFF"), 0, Utils.dipToPixels(getActContext(), 1), Color.parseColor("#F2F2F2"), commentArea);

        backImgView = (ImageView) findViewById(R.id.backImgView);
        fareDetailDisplayArea = (LinearLayout) findViewById(R.id.fareDetailDisplayArea);
        afterServiceArea = (LinearLayout) findViewById(R.id.afterServiceArea);
        beforeServiceArea = (LinearLayout) findViewById(R.id.beforeServiceArea);
        ratingDriverHTxt = (MTextView) findViewById(R.id.ratingDriverHTxt);
        profilebgarea = (LinearLayout) findViewById(R.id.profilebgarea);
        cartypeTxt = (MTextView) findViewById(R.id.cartypeTxt);

        ufxratingDriverHTxt = (MTextView) findViewById(R.id.ufxratingDriverHTxt);
        ufxratingBar = (SimpleRatingBar) findViewById(R.id.ufxratingBar);

        tipHTxt = (MTextView) findViewById(R.id.tipHTxt);
        tipamtTxt = (MTextView) findViewById(R.id.tipamtTxt);
        tipmsgTxt = (MTextView) findViewById(R.id.tipmsgTxt);


        tiparea = (CardView) findViewById(R.id.tiparea);


        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        rateBtnId = Utils.generateViewId();
        btn_type2.setId(rateBtnId);

        btn_type2.setOnClickListener(new setOnClickList());


        setLabels();
        setData();


        commentBox.setTextColor(getResources().getColor(R.color.mdtp_transparent_black));

        backImgView.setOnClickListener(new setOnClickList());
        subTitleTxt.setOnClickListener(new setOnClickList());
        afterServiceArea.setOnClickListener(new setOnClickList());
        beforeServiceArea.setOnClickListener(new setOnClickList());
        helpTxt.setOnClickListener(new setOnClickList());

        commentBox.setOnTouchListener((v, event) -> {
            ((NestedScrollView) findViewById(R.id.scrollContainer)).requestDisallowInterceptTouchEvent(true);
            return false;
        });

    }


    public void setLabels() {
        String tripData = getIntent().getStringExtra("TripData");
        helpTxt.setText(generalFunc.retrieveLangLBl("Help?", "LBL_NEED_HELP")); //LBL_NEED_HELP_TXT

        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RECEIPT_HEADER_TXT"));
        subTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GET_RECEIPT_TXT"));


        String headerLable = "", noVal = "", driverhVal = "";

        if (generalFunc.getJsonValue("eType", tripData).equals(Utils.CabGeneralType_UberX)) {
            headerLable = generalFunc.retrieveLangLBl("", "LBL_THANKS_JOB_TXT");
            noVal = generalFunc.retrieveLangLBl("", "LBL_SERVICES") + "#";
            driverhVal = generalFunc.retrieveLangLBl("", "LBL_SERVICE_PROVIDER_TXT");
        } else if (generalFunc.getJsonValue("eType", tripData).equals("Deliver")) {
            headerLable = generalFunc.retrieveLangLBl("", "LBL_THANKS_DELIVERY_TXT");
            noVal = generalFunc.retrieveLangLBl("", "LBL_DELIVERY") + "#";
            driverhVal = generalFunc.retrieveLangLBl("", "LBL_CARRIER");
        } else {
            headerLable = generalFunc.retrieveLangLBl("", "LBL_THANKS_RIDING_TXT");
            noVal = generalFunc.retrieveLangLBl("", "LBL_RIDE") + "#";
            driverhVal = generalFunc.retrieveLangLBl("", "LBL_DRIVER");
        }

        ((MTextView) findViewById(R.id.headerTxt)).setText(generalFunc.retrieveLangLBl("", headerLable));


        ((MTextView) findViewById(R.id.rideNoHTxt)).setText(noVal);
        ((MTextView) findViewById(R.id.ratingHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_RATING"));
        ((MTextView) findViewById(R.id.driverHTxt)).setText(driverhVal);
        String dateLable = "";
        String pickupHval = "";

        if (generalFunc.getJsonValue("eType", tripData).equals(Utils.CabGeneralType_UberX)) {
            dateLable = generalFunc.retrieveLangLBl("", "LBL_JOB_REQ_DATE");
            pickupHval = generalFunc.retrieveLangLBl("", "LBL_JOB_LOCATION_TXT");
        } else if (generalFunc.getJsonValue("eType", tripData).equals("Deliver")) {
            dateLable = generalFunc.retrieveLangLBl("", "LBL_DELIVERY_REQUEST_DATE");
            pickupHval = generalFunc.retrieveLangLBl("", "LBL_SENDER_LOCATION");
        } else {
            dateLable = generalFunc.retrieveLangLBl("", "LBL_TRIP_REQUEST_DATE_TXT");
            pickupHval = generalFunc.retrieveLangLBl("", "LBL_PICKUP_LOCATION_TXT");
        }


        ((MTextView) findViewById(R.id.tripdateHTxt)).setText(generalFunc.retrieveLangLBl("", dateLable));


        ((MTextView) findViewById(R.id.pickUpHTxt)).setText(pickupHval);
        if (generalFunc.getJsonValue("eType", tripData).equals("Deliver")) {
            ((MTextView) findViewById(R.id.dropOffHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_DELIVERY_DETAILS_TXT"));
        } else if (generalFunc.getJsonValue("eType", tripData).equals(Utils.CabGeneralType_Ride)) {
            ((MTextView) findViewById(R.id.dropOffHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_DEST_LOCATION"));
        } else {
            ((MTextView) findViewById(R.id.dropOffHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_DEST_LOCATION"));

        }

        ((MTextView) findViewById(R.id.chargesHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_CHARGES_TXT"));
        btn_type2.setText(generalFunc.retrieveLangLBl("Rate", "LBL_RATE_DRIVER_TXT"));
        ratingDriverHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RATING"));
        ufxratingDriverHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RATE_HEADING_DRIVER_TXT"));

        tipHTxt.setText(generalFunc.retrieveLangLBl("Tip Amount", "LBL_TIP_AMOUNT"));
        tipmsgTxt.setText(generalFunc.retrieveLangLBl("Thank you for giving tip for this trip.", "LBL_TIP_INFO_SHOW_RIDER"));


    }

    public void setData() {
        String tripData = getIntent().getStringExtra("TripData");


        ((MTextView) findViewById(R.id.rideNoVTxt)).setText(generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("vRideNo", tripData)));
        ((MTextView) findViewById(R.id.nameDriverVTxt)).setText(generalFunc.getJsonValue("vName", generalFunc.getJsonValue("DriverDetails", tripData)) + " " +
                generalFunc.getJsonValue("vLastName", generalFunc.getJsonValue("DriverDetails", tripData)));
        ((MTextView) findViewById(R.id.tripdateVTxt)).setText(generalFunc.getDateFormatedType(generalFunc.getJsonValue("tTripRequestDateOrig", tripData), Utils.OriginalDateFormate, Utils.getDetailDateFormat(getActContext())));

        ((MTextView) findViewById(R.id.pickUpVTxt)).setText(generalFunc.getJsonValue("tSaddress", tripData));

        if (generalFunc.getJsonValue("eType", tripData).equals("Deliver")) {

            ((MTextView) findViewById(R.id.dropOffVTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_RECEIVER_NAME") + ": " + generalFunc.getJsonValue("vReceiverName", tripData) + "\n\n" +
                    generalFunc.retrieveLangLBl("", "LBL_RECEIVER_LOCATION") + ": " + generalFunc.getJsonValue("tDaddress", tripData) + "\n\n" +
                    generalFunc.retrieveLangLBl("", "LBL_PACKAGE_TYPE_TXT") + ": " + generalFunc.getJsonValue("PackageType", tripData) + "\n\n" +
                    generalFunc.retrieveLangLBl("", "LBL_PACKAGE_DETAILS") + ": " + generalFunc.getJsonValue("tPackageDetails", tripData)
            );
        } else {
            ((MTextView) findViewById(R.id.dropOffVTxt)).setText(generalFunc.getJsonValue("tDaddress", tripData));
        }

        if (generalFunc.getJsonValue("tDaddress", tripData).equals("")) {
            (findViewById(R.id.dropOffVTxt)).setVisibility(View.GONE);
            (findViewById(R.id.dropOffHTxt)).setVisibility(View.GONE);
        }

        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);

        if (!generalFunc.getJsonValue("fTipPrice", tripData).equals("0") && !generalFunc.getJsonValue("fTipPrice", tripData).equals("0.0") &&
                !generalFunc.getJsonValue("fTipPrice", tripData).equals("0.00") &&
                !generalFunc.getJsonValue("fTipPrice", tripData).equals("")) {
            tiparea.setVisibility(View.VISIBLE);
            tipPluseImage.setVisibility(View.VISIBLE);

            tipamtTxt.setText(generalFunc.getJsonValue("fTipPrice", tripData));

        } else {
            tiparea.setVisibility(View.GONE);
            tipPluseImage.setVisibility(View.GONE);
        }


        if (generalFunc.getJsonValue("vVehicleCategory", tripData) != null && !generalFunc.getJsonValue("vVehicleCategory", tripData).equals("")) {
            cartypeTxt.setText(generalFunc.getJsonValue("vVehicleCategory", tripData) + "-" + generalFunc.getJsonValue("carTypeName", tripData));
        } else {
            cartypeTxt.setText(generalFunc.getJsonValue("carTypeName", tripData));
        }

        String trip_status_str = generalFunc.getJsonValue("iActive", tripData);

        isRatingDone = generalFunc.getJsonValue("is_rating", tripData);

        if (isRatingDone.equalsIgnoreCase("No") && trip_status_str.contains("Finished")) {
            findViewById(R.id.rateDriverArea).setVisibility(View.VISIBLE);
            findViewById(R.id.rateCardDriverArea).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.rateDriverArea).setVisibility(View.GONE);
            findViewById(R.id.rateCardDriverArea).setVisibility(View.GONE);
        }

        if (trip_status_str.contains("Canceled")) {


            String cancelLable = "";
            String cancelableReason = generalFunc.getJsonValue("vCancelReason", tripData);


            if (generalFunc.getJsonValue("eCancelledBy", tripData).equalsIgnoreCase("DRIVER")) {
                if (generalFunc.getJsonValue("eType", tripData).equals("Deliver")) {
                    cancelLable = generalFunc.retrieveLangLBl("", "LBL_PREFIX_DELIVERY_CANCEL_DRIVER") + " " + cancelableReason;
                } else if (generalFunc.getJsonValue("eType", tripData).equals(Utils.CabGeneralType_UberX)) {
                    cancelLable = generalFunc.retrieveLangLBl("", "LBL_PREFIX_JOB_CANCEL_PROVIDER") + " " + cancelableReason;
                } else {
                    cancelLable = generalFunc.retrieveLangLBl("", "LBL_PREFIX_TRIP_CANCEL_DRIVER") + " " + cancelableReason;
                }

            } else {

                if (generalFunc.getJsonValue("eType", tripData).equals(Utils.CabGeneralType_UberX)) {
                    cancelLable = generalFunc.retrieveLangLBl("", "LBL_CANCELED_JOB");
                } else if (generalFunc.getJsonValue("eType", tripData).equals("Deliver")) {
                    cancelLable = generalFunc.retrieveLangLBl("", "LBL_CANCELED_DELIVERY_TXT");
                } else {
                    cancelLable = generalFunc.retrieveLangLBl("", "LBL_CANCELED_TRIP_TXT");
                }
            }

            ((MTextView) findViewById(R.id.tripStatusTxt)).setText(generalFunc.retrieveLangLBl("", cancelLable));
            (findViewById(R.id.tripDetailArea)).setVisibility(View.VISIBLE);
        } else if (trip_status_str.contains("Finished")) {

            String finishLable = "";
            if (generalFunc.getJsonValue("eType", tripData).equals(Utils.CabGeneralType_UberX)) {
                finishLable = generalFunc.retrieveLangLBl("", "LBL_FINISHED_JOB_TXT");
            } else if (generalFunc.getJsonValue("eType", tripData).equals("Deliver")) {
                finishLable = generalFunc.retrieveLangLBl("", "LBL_FINISHED_DELIVERY_TXT");
            } else {
                finishLable = generalFunc.retrieveLangLBl("", "LBL_FINISHED_TRIP_TXT");
            }

            ((MTextView) findViewById(R.id.tripStatusTxt)).setText(generalFunc.retrieveLangLBl("", finishLable));

            (findViewById(R.id.tripDetailArea)).setVisibility(View.VISIBLE);
            subTitleTxt.setVisibility(View.VISIBLE);
        } else {
            ((MTextView) findViewById(R.id.tripStatusTxt)).setText(trip_status_str);

        }

        if (generalFunc.getJsonValue("vTripPaymentMode", tripData).equals("Cash")) {
            ((MTextView) findViewById(R.id.paymentTypeTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_CASH_PAYMENT_TXT"));
        } else {
            ((MTextView) findViewById(R.id.paymentTypeTxt)).setText(generalFunc.retrieveLangLBl("Card Payment", "LBL_CARD_PAYMENT"));
            ((ImageView) findViewById(R.id.paymentTypeImgeView)).setImageResource(R.mipmap.ic_card_new);
        }

        if (generalFunc.getJsonValue("eCancelled", tripData).equals("Yes")) {

            String cancelledLable = "";
            String cancelableReason = generalFunc.getJsonValue("vCancelReason", tripData);

            if (generalFunc.getJsonValue("eCancelledBy", tripData).equalsIgnoreCase("DRIVER")) {

                if (generalFunc.getJsonValue("eType", tripData).equals(Utils.CabGeneralType_UberX)) {
                    cancelledLable = generalFunc.retrieveLangLBl("", "LBL_PREFIX_JOB_CANCEL_PROVIDER") + " " + cancelableReason;
                } else if (generalFunc.getJsonValue("eType", tripData).equals("Deliver")) {
                    cancelledLable = generalFunc.retrieveLangLBl("", "LBL_PREFIX_DELIVERY_CANCEL_DRIVER") + " " + cancelableReason;
                } else {
                    cancelledLable = generalFunc.retrieveLangLBl("", "LBL_PREFIX_TRIP_CANCEL_DRIVER") + " " + cancelableReason;
                }

            } else {

                if (generalFunc.getJsonValue("eType", tripData).equals(Utils.CabGeneralType_UberX)) {
                    cancelledLable = generalFunc.retrieveLangLBl("", "LBL_CANCELED_JOB");
                } else if (generalFunc.getJsonValue("eType", tripData).equals("Deliver")) {
                    cancelledLable = generalFunc.retrieveLangLBl("", "LBL_CANCELED_DELIVERY_TXT");
                } else {
                    cancelledLable = generalFunc.retrieveLangLBl("", "LBL_CANCELED_TRIP_TXT");
                }
            }

            ((MTextView) findViewById(R.id.tripStatusTxt)).setText(generalFunc.retrieveLangLBl("", cancelledLable));
        }

        ((SimpleRatingBar) findViewById(ratingBar)).setRating(generalFunc.parseFloatValue(0, generalFunc.getJsonValue("TripRating", tripData)));


        final ImageView profilebackImage = (ImageView) findViewById(R.id.profileimageback);
        final ImageView driverImageview = (SelectableRoundedImageView) findViewById(R.id.driverImgView);

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (profilebackImage != null) {
                    Utils.setBlurImage(bitmap, profilebackImage);
                }
                driverImageview.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        String driverImageName = generalFunc.getJsonValue("vImage", generalFunc.getJsonValue("DriverDetails", tripData));
        if (driverImageName == null || driverImageName.equals("") || driverImageName.equals("NONE")) {
            (driverImageview).setImageResource(R.mipmap.ic_no_pic_user);
        } else {
            String image_url = CommonUtilities.SERVER_URL_PHOTOS + "upload/Driver/" + generalFunc.getJsonValue("iDriverId", tripData) + "/"
                    + driverImageName;
            Picasso.with(getActContext())
                    .load(image_url)
                    .placeholder(R.mipmap.ic_no_pic_user)
                    .error(R.mipmap.ic_no_pic_user)
                    .into(target);


        }


        if (generalFunc.retrieveValue(APP_DESTINATION_MODE).equalsIgnoreCase(Utils.NONE_DESTINATION)) {
//            (findViewById(R.id.dropOffHTxt)).setVisibility(View.GONE);
//            (findViewById(R.id.dropOffVTxt)).setVisibility(View.GONE);
        }

        if (generalFunc.getJsonValue("eType", tripData).equalsIgnoreCase("UberX") || generalFunc.getJsonValue("eFareType", tripData).equalsIgnoreCase("Fixed")) {
//            findViewById(R.id.tripDetailArea).setVisibility(View.GONE);
            findViewById(R.id.service_area).setVisibility(View.GONE);
            findViewById(R.id.serviceHTxt).setVisibility(View.GONE);
            findViewById(R.id.photoArea).setVisibility(View.VISIBLE);


            ((MTextView) findViewById(R.id.beforeImgHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_BEFORE_SERVICE"));
            ((MTextView) findViewById(R.id.afterImgHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_AFTER_SERVICE"));

            if (!TextUtils.isEmpty(generalFunc.getJsonValue("vBeforeImage", tripData))) {
                findViewById(R.id.beforeServiceArea).setVisibility(View.VISIBLE);
                before_serviceImg_url = generalFunc.getJsonValue("vBeforeImage", tripData);

                String vBeforeImage = Utils.getResizeImgURL(getActContext(), before_serviceImg_url, getResources().getDimensionPixelSize(R.dimen.before_after_img_size), getResources().getDimensionPixelSize(R.dimen.before_after_img_size));

                displayPic(vBeforeImage, (ImageView) findViewById(R.id.iv_before_img), "before");
            } else {
                findViewById(R.id.beforeServiceArea).setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(generalFunc.getJsonValue("vAfterImage", tripData))) {
                findViewById(R.id.afterServiceArea).setVisibility(View.VISIBLE);
                after_serviceImg_url = generalFunc.getJsonValue("vAfterImage", tripData);

                String vAfterImage = Utils.getResizeImgURL(getActContext(), after_serviceImg_url, getResources().getDimensionPixelSize(R.dimen.before_after_img_size), getResources().getDimensionPixelSize(R.dimen.before_after_img_size));
                displayPic(vAfterImage, (ImageView) findViewById(R.id.iv_after_img), "after");
            } else {
                findViewById(R.id.afterServiceArea).setVisibility(View.GONE);
            }

            if (TextUtils.isEmpty(generalFunc.getJsonValue("vBeforeImage", tripData)) && TextUtils.isEmpty(generalFunc.getJsonValue("vAfterImage", tripData))) {

                findViewById(R.id.photoArea).setVisibility(View.GONE);

            }
            ((MTextView) findViewById(R.id.pickUpVTxt)).setText(generalFunc.getJsonValue("tSaddress", tripData));
            ((MTextView) findViewById(R.id.serviceTypeVTxt)).setText(generalFunc.getJsonValue("vVehicleCategory", tripData) + " - " + generalFunc.getJsonValue("vVehicleType", tripData));
            ((MTextView) findViewById(R.id.serviceTypeHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_Car_Type"));


        } else {
            findViewById(R.id.tripDetailArea).setVisibility(View.VISIBLE);
            findViewById(R.id.service_area).setVisibility(View.GONE);
            findViewById(R.id.serviceHTxt).setVisibility(View.GONE);
            findViewById(R.id.photoArea).setVisibility(View.GONE);
        }

        boolean FareDetailsArrNew = generalFunc.isJSONkeyAvail("HistoryFareDetailsNewArr", tripData);

        JSONArray FareDetailsArrNewObj = null;
        if (FareDetailsArrNew == true) {
            FareDetailsArrNewObj = generalFunc.getJsonArray("HistoryFareDetailsNewArr", tripData);
        }
        if (FareDetailsArrNewObj != null) {
            addFareDetailLayout(FareDetailsArrNewObj);
        }
//        addFareDetailLayout(FareDetailsArrNewObj);
    }


    public void displayPic(String image_url, ImageView view, final String imgType) {

        Picasso.with(getActContext())
                .load(image_url)
                .placeholder(R.mipmap.ic_no_icon)
                .into(view, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        if (imgType.equalsIgnoreCase("before")) {
                            findViewById(R.id.before_loading).setVisibility(View.GONE);
                            findViewById(R.id.iv_before_img).setVisibility(View.VISIBLE);
                        } else if (imgType.equalsIgnoreCase("after")) {
                            findViewById(R.id.after_loading).setVisibility(View.GONE);
                            findViewById(R.id.iv_after_img).setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onError() {
                        if (imgType.equalsIgnoreCase("before")) {
                            findViewById(R.id.before_loading).setVisibility(View.VISIBLE);
                            findViewById(R.id.iv_before_img).setVisibility(View.GONE);
                        } else if (imgType.equalsIgnoreCase("after")) {
                            findViewById(R.id.after_loading).setVisibility(View.VISIBLE);
                            findViewById(R.id.iv_after_img).setVisibility(View.GONE);

                        }
                    }
                });

    }

    private void addFareDetailLayout(JSONArray jobjArray) {

        if (fareDetailDisplayArea.getChildCount() > 0) {
            fareDetailDisplayArea.removeAllViewsInLayout();
        }

        for (int i = 0; i < jobjArray.length(); i++) {
            JSONObject jobject = generalFunc.getJsonObject(jobjArray, i);
            try {
                addFareDetailRow(jobject.names().getString(0), jobject.get(jobject.names().getString(0)).toString(), (jobjArray.length() - 1) == i ? true : false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void addFareDetailRow(String row_name, String row_value, boolean isLast) {
        View convertView = null;
        if (row_name.equalsIgnoreCase("eDisplaySeperator")) {
            convertView = new View(getActContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dipToPixels(getActContext(), 1));
            params.setMarginStart(Utils.dipToPixels(getActContext(), 10));
            params.setMarginEnd(Utils.dipToPixels(getActContext(), 10));
            convertView.setBackgroundColor(Color.parseColor("#dedede"));
            convertView.setLayoutParams(params);
        } else {
            LayoutInflater infalInflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.design_fare_deatil_row, null);

            convertView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            convertView.setPaddingRelative(Utils.dipToPixels(getActContext(), 10), 0, Utils.dipToPixels(getActContext(), 10), 0);

            convertView.setMinimumHeight(Utils.dipToPixels(getActContext(), 40));

            MTextView titleHTxt = (MTextView) convertView.findViewById(R.id.titleHTxt);
            MTextView titleVTxt = (MTextView) convertView.findViewById(R.id.titleVTxt);

            titleHTxt.setText(generalFunc.convertNumberWithRTL(row_name));
            titleVTxt.setText(generalFunc.convertNumberWithRTL(row_value));

            titleHTxt.setTextColor(Color.parseColor("#303030"));
            titleVTxt.setTextColor(Color.parseColor("#111111"));
        }

        if (convertView != null)
            fareDetailDisplayArea.addView(convertView);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;

        String tripData = getIntent().getStringExtra("TripData");

        String tStartLat = generalFunc.getJsonValue("tStartLat", tripData);
        String tStartLong = generalFunc.getJsonValue("tStartLong", tripData);
        String tEndLat = generalFunc.getJsonValue("tEndLat", tripData);
        String tEndLong = generalFunc.getJsonValue("tEndLong", tripData);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        Marker pickUpMarker = null;
        Marker destMarker = null;
        if (!tStartLat.equals("") && !tStartLat.equals("0.0") && !tStartLong.equals("") && !tStartLong.equals("0.0")) {
            LatLng pickUpLoc = new LatLng(generalFunc.parseDoubleValue(0.0, tStartLat), generalFunc.parseDoubleValue(0.0, tStartLong));
            MarkerOptions marker_opt = new MarkerOptions().position(pickUpLoc);
            marker_opt.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_source_marker)).anchor(0.5f, 0.5f);
            pickUpMarker = this.gMap.addMarker(marker_opt);

            builder.include(pickUpLoc);

            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickUpLoc, 10));
        }

        if (generalFunc.getJsonValue("iActive", tripData).equals("Finished")) {
            if (!tEndLat.equals("") && !tEndLat.equals("0.0") && !tEndLong.equals("") && !tEndLong.equals("0.0")) {
                LatLng destLoc = new LatLng(generalFunc.parseDoubleValue(0.0, tEndLat), generalFunc.parseDoubleValue(0.0, tEndLong));
                MarkerOptions marker_opt = new MarkerOptions().position(destLoc);
                marker_opt.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_dest_marker)).anchor(0.5f, 0.5f);
                destMarker = this.gMap.addMarker(marker_opt);

                builder.include(destLoc);

                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destLoc, 10));
            }
        }


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), width, Utils.dipToPixels(getActContext(), 200), 100));
        gMap.setOnMarkerClickListener(marker -> {
            // TODO Auto-generated method stub
            marker.hideInfoWindow();

            return true;
        });

        if (pickUpMarker != null && destMarker != null) {
            drawRoute(pickUpMarker.getPosition(), destMarker.getPosition());
        }

    }

    public void drawRoute(LatLng pickUpLoc, LatLng destinationLoc) {
        String originLoc = pickUpLoc.latitude + "," + pickUpLoc.longitude;
        String destLoc = destinationLoc.latitude + "," + destinationLoc.longitude;
        String serverKey = generalFunc.retrieveValue(Utils.GOOGLE_SERVER_ANDROID_PASSENGER_APP_KEY);
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + originLoc + "&destination=" + destLoc + "&sensor=true&key=" + serverKey + "&language=" + generalFunc.retrieveValue(Utils.GOOGLE_MAP_LANGUAGE_CODE_KEY) + "&sensor=true";

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), url, true);

        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                String status = generalFunc.getJsonValue("status", responseString);

                if (status.equals("OK")) {

                    JSONArray obj_routes = generalFunc.getJsonArray("routes", responseString);
                    if (obj_routes != null && obj_routes.length() > 0) {

                        PolylineOptions lineOptions = generalFunc.getGoogleRouteOptions(responseString, Utils.dipToPixels(getActContext(), 5), getActContext().getResources().getColor(R.color.black));

                        if (lineOptions != null) {
                            gMap.addPolyline(lineOptions);
                        }
                    }

                }

            }
        });
        exeWebServer.execute();
    }

    public void sendReceipt() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getReceipt");
        parameters.put("UserType", Utils.app_type);
        parameters.put("iTripId", generalFunc.getJsonValue("iTripId", getIntent().getStringExtra("TripData")));

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                } else {
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    public Context getActContext() {
        return HistoryDetailActivity.this;
    }

    public void submitRating() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "submitRating");
        parameters.put("iGeneralUserId", generalFunc.getMemberId());
        parameters.put("tripID", generalFunc.getJsonValue("iTripId", getIntent().getStringExtra("TripData")));
        parameters.put("rating", "" + ufxratingBar.getRating());
        parameters.put("message", Utils.getText(commentBox));
        parameters.put("UserType", Utils.app_type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {

                    final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                    generateAlert.setCancelable(true);
                    generateAlert.setBtnClickList(btn_id -> {
                        generateAlert.closeAlertBox();

                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    });
                    generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_SUCCESS_RATING_SUBMIT_TXT"));
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                    generateAlert.showAlertBox();
                    generateAlert.setCancelable(false);


                } else {
                    resetRatingData();
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    private void resetRatingData() {
        commentBox.setText("");
        ((RatingBar) findViewById(ratingBar)).setRating(0);
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(getActContext());
            Bundle bn = new Bundle();

            switch (view.getId()) {

                case R.id.backImgView:
                    HistoryDetailActivity.super.onBackPressed();
                    break;

                case R.id.subTitleTxt:
                    sendReceipt();
                    break;

                case R.id.beforeServiceArea:
                    new StartActProcess(getActContext()).openURL(before_serviceImg_url);
                    break;

                case R.id.afterServiceArea:
                    new StartActProcess(getActContext()).openURL(after_serviceImg_url);
                    break;

                case R.id.helpTxt:
                    //new StartActProcess(getActContext()).startActWithData(UberXSelectServiceActivity.class, bundle);

                    bn.putString("iTripId", generalFunc.getJsonValue("iTripId", getIntent().getStringExtra("TripData")));
                    new StartActProcess(getActContext()).startActWithData(Help_MainCategory.class, bn);
                    break;
            }

            if (view.getId() == rateBtnId) {
                if (((SimpleRatingBar) findViewById(R.id.ufxratingBar)).getRating() < 1) {
                    generalFunc.showMessage(generalFunc.getCurrentView(HistoryDetailActivity.this), generalFunc.retrieveLangLBl("", "LBL_ERROR_RATING_DIALOG_TXT"));
                    return;
                }
                submitRating();
            }
        }
    }
}
