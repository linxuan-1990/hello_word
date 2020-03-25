package com.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.adapter.files.CabTypeAdapter;

import com.caliway.user.FareBreakDownActivity;
import com.caliway.user.MainActivity;
import com.caliway.user.R;
import com.caliway.user.RentalDetailsActivity;
import com.drawRoute.DirectionsJSONParser;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MapAnimator;
import com.general.files.StartActProcess;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.SelectableRoundedImageView;
import com.view.anim.loader.AVLoadingIndicatorView;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CabSelectionFragment extends Fragment implements CabTypeAdapter.OnItemClickList, ViewTreeObserver.OnGlobalLayoutListener {


    static MainActivity mainAct;
    static GeneralFunctions generalFunc;
    static MTextView payTypeTxt;
    static RadioButton cardRadioBtn;
    static ImageView payImgView;
    // public LinearLayout rideBtnContainer;
    public MButton ride_now_btn;
    public int currentPanelDefaultStateHeight = 100;
    public String currentCabGeneralType = "";
    public CabTypeAdapter adapter;
    public ArrayList<HashMap<String, String>> cabTypeList;
    public ArrayList<HashMap<String, String>> rentalTypeList;
    public ArrayList<HashMap<String, String>> tempCabTypeList = new ArrayList<>();
    public String app_type = "Ride";
    public ImageView img_ridelater;
    LinearLayout imageLaterarea;
    //    public int isSelcted = -1;
    public boolean isclickableridebtn = false;
    public boolean isroutefound = false;
    public int selpos = 0;
    public View view = null;
    String userProfileJson = "";
    RecyclerView carTypeRecyclerView;
    ArrayList<HashMap<String, String>> cabCategoryList;

    String currency_sign = "";
    boolean isKilled = false;
    LinearLayout paymentArea;
    LinearLayout promoArea;
    View payTypeSelectArea;
    String appliedPromoCode = "";
    public boolean isCardValidated = true;
    RadioButton cashRadioBtn;
    LinearLayout casharea;
    LinearLayout cardarea;
    LinearLayout cashcardarea;
    String distance = "";
    String time = "";
    AVLoadingIndicatorView loaderView;
    MTextView noServiceTxt;
    boolean isCardnowselcted = false;
    boolean isCardlaterselcted = false;
    //    boolean dialogShowOnce = true;
    String RideDeliveryType = "";
    MTextView promoTxt;
    //  boolean ridelaterclick = false;
    //   boolean ridenowclick = false;
    int i = 0;
    int j = 0;
    Location tempDestLocation;
    Location tempPickUpLocation;
    ExecuteWebServerUrl estimateFareTask;
    Polyline route_polyLine;

    public boolean isSkip = false;

    // PolylineOptions polyLineOptions;


    public LatLng sourceLocation = null;
    public LatLng destLocation = null;

    boolean isRouteFail = false;

    int height = 0;
    int width = 0;
    int maxX = 0;
    int maxY = 0;
    public Marker sourceMarker, destMarker, sourceDotMarker, destDotMarker;
    MarkerOptions source_dot_option, dest_dot_option;

    String required_str = "";
    ProgressBar mProgressBar;
    android.support.v7.app.AlertDialog outstanding_dialog;

    boolean isOutStandingDailogShow = false;


    public static int RENTAL_REQ_CODE = 1234;

    public String iRentalPackageId = "";

    ImageView rentalBackImage;
    MTextView rentalPkg;
    public MTextView rentalPkgDesc;
    SelectableRoundedImageView rentPkgImage, rentBackPkgImage;
    RelativeLayout rentalarea;
    public int fragmentWidth = 0;
    public int fragmentHeight = 0;


    public static void setCardSelection() {
        if (generalFunc == null) {
            generalFunc = mainAct.generalFunc;
        }


        payTypeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CARD"));


        mainAct.setCashSelection(false);

        cardRadioBtn.setChecked(true);

        payImgView.setImageResource(R.mipmap.ic_card_new);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view != null) {
            return view;
        }


        view = inflater.inflate(R.layout.fragment_new_cab_selection, container, false);
        mainAct = (MainActivity) getActivity();
        generalFunc = mainAct.generalFunc;
        rentalBackImage = (ImageView) view.findViewById(R.id.rentalBackImage);
        rentalarea = (RelativeLayout) view.findViewById(R.id.rentalarea);
        rentPkgImage = (SelectableRoundedImageView) view.findViewById(R.id.rentPkgImage);
        rentBackPkgImage = (SelectableRoundedImageView) view.findViewById(R.id.rentBackPkgImage);
        rentalPkg = (MTextView) view.findViewById(R.id.rentalPkg);
        rentalPkgDesc = (MTextView) view.findViewById(R.id.rentalPkgDesc);
        rentalBackImage.setOnClickListener(new setOnClickList());
        rentalPkg.setOnClickListener(new setOnClickList());
        rentPkgImage.setOnClickListener(new setOnClickList());
        if (generalFunc.isRTLmode()) {
            rentPkgImage.setScaleX(1);
            rentPkgImage.setScaleY(-1);
            rentPkgImage.setRotation(180);
        }


        if (mainAct.eShowOnlyMoto != null && mainAct.eShowOnlyMoto.equalsIgnoreCase("Yes")) {
            rentalPkg.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_MOTO_TITLE_TXT"));
            rentalPkgDesc.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_MOTO_PKG_INFO"));
        } else {
            rentalPkg.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_A_CAR"));
            rentalPkgDesc.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_PKG_INFO"));

        }


        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;


        height = displayMetrics.heightPixels - Utils.dpToPx(getActContext(), 300);

        ride_now_btn = ((MaterialRippleLayout) view.findViewById(R.id.ride_now_btn)).getChildView();
        ride_now_btn.setId(Utils.generateViewId());
        mProgressBar = (ProgressBar) view.findViewById(R.id.mProgressBar);
        mProgressBar.getIndeterminateDrawable().setColorFilter(
                getActContext().getResources().getColor(R.color.appThemeColor_2), android.graphics.PorterDuff.Mode.SRC_IN);
        findRoute("--");
        RideDeliveryType = getArguments().getString("RideDeliveryType");

        carTypeRecyclerView = (RecyclerView) view.findViewById(R.id.carTypeRecyclerView);
        loaderView = (AVLoadingIndicatorView) view.findViewById(R.id.loaderView);
        payTypeSelectArea = view.findViewById(R.id.payTypeSelectArea);
        payTypeTxt = (MTextView) view.findViewById(R.id.payTypeTxt);
        promoTxt = (MTextView) view.findViewById(R.id.promoTxt);
        promoTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PRMO_TXT"));


        img_ridelater = (ImageView) view.findViewById(R.id.img_ridelater);
        imageLaterarea = (LinearLayout) view.findViewById(R.id.imageLaterarea);
        noServiceTxt = (MTextView) view.findViewById(R.id.noServiceTxt);


        casharea = (LinearLayout) view.findViewById(R.id.casharea);
        cardarea = (LinearLayout) view.findViewById(R.id.cardarea);

        casharea.setOnClickListener(new setOnClickList());
        cardarea.setOnClickListener(new setOnClickList());

        img_ridelater.setOnClickListener(new setOnClickList());


        new CreateRoundedView(getActContext().getResources().getColor(R.color.white), Utils.dipToPixels(getActContext(), 14), 1,
                getActContext().getResources().getColor(R.color.gray), rentBackPkgImage);
        new CreateRoundedView(getActContext().getResources().getColor(R.color.appThemeColor_1), Utils.dipToPixels(getActContext(), 12), 0,
                getActContext().getResources().getColor(R.color.appThemeColor_2), rentPkgImage);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_car);
        Drawable d = new BitmapDrawable(getResources(), bitmap);
        d.setColorFilter(getActContext().getResources().getColor(R.color.appThemeColor_TXT_1), PorterDuff.Mode.MULTIPLY);
        rentPkgImage.setImageDrawable(d);

        paymentArea = (LinearLayout) view.findViewById(R.id.paymentArea);
        promoArea = (LinearLayout) view.findViewById(R.id.promoArea);
        promoArea.setOnClickListener(new setOnClickList());
        paymentArea.setOnClickListener(new setOnClickList());
        cashRadioBtn = (RadioButton) view.findViewById(R.id.cashRadioBtn);
        cardRadioBtn = (RadioButton) view.findViewById(R.id.cardRadioBtn);

        payImgView = (ImageView) view.findViewById(R.id.payImgView);

        cashcardarea = (LinearLayout) view.findViewById(R.id.cashcardarea);

        userProfileJson = mainAct.userProfileJson;

        currency_sign = generalFunc.getJsonValue("CurrencySymbol", userProfileJson);
        app_type = generalFunc.getJsonValue("APP_TYPE", userProfileJson);

        if (generalFunc.getJsonValue("RIDE_LATER_BOOKING_ENABLED", userProfileJson).equalsIgnoreCase("Yes")) {

            if (app_type.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX) && !mainAct.iscubejekRental) {
                imageLaterarea.setVisibility(View.VISIBLE);

            } else if (mainAct.iscubejekRental) {
                imageLaterarea.setVisibility(View.GONE);
            } else {
                imageLaterarea.setVisibility(View.VISIBLE);
            }


        } else {
            imageLaterarea.setVisibility(View.GONE);
        }

        if (mainAct.isDeliver(mainAct.getCurrentCabGeneralType())) {
            img_ridelater.setImageResource(R.mipmap.ride_later_delivery);
        }

        if (app_type.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX)) {
            app_type = "Ride";
        }

        if (app_type.equals(Utils.CabGeneralType_UberX)) {
            view.setVisibility(View.GONE);
            return view;
        }

        isKilled = false;


        if (generalFunc.getJsonValue("APP_PAYMENT_MODE", userProfileJson).equalsIgnoreCase("Cash")) {
            cashRadioBtn.setVisibility(View.VISIBLE);
            payTypeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CASH_TXT"));
            cardRadioBtn.setVisibility(View.GONE);

            cardRadioBtn.setText(generalFunc.retrieveLangLBl("", "LBL_CASH_TXT"));
        } else if (generalFunc.getJsonValue("APP_PAYMENT_MODE", userProfileJson).equalsIgnoreCase("Card")) {
            cashRadioBtn.setVisibility(View.GONE);
            cardRadioBtn.setVisibility(View.VISIBLE);
            payTypeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CARD"));
            isCardValidated = true;

            setCardSelection();
            isCardValidated = false;

            cardRadioBtn.setText(generalFunc.retrieveLangLBl("", "LBL_CARD"));
        } else if (generalFunc.getJsonValue("APP_PAYMENT_MODE", userProfileJson).equalsIgnoreCase("Cash-Card")) {
            payTypeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CASH_TXT"));

            cashRadioBtn.setVisibility(View.VISIBLE);
            cardRadioBtn.setVisibility(View.VISIBLE);

            cashRadioBtn.setText(generalFunc.retrieveLangLBl("", "LBL_CASH_TXT"));
            cardRadioBtn.setText(generalFunc.retrieveLangLBl("", "LBL_CARD"));
        }

        setLabels(true);

        ride_now_btn.setOnClickListener(new setOnClickList());


        configRideLaterBtnArea(false);

        addGlobalLayoutListner();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        addGlobalLayoutListner();
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

    public void showLoader() {
        loaderView.setVisibility(View.VISIBLE);
        closeNoServiceText();
    }

    public void showNoServiceText() {
        noServiceTxt.setVisibility(View.VISIBLE);
    }

    public void closeNoServiceText() {
        noServiceTxt.setVisibility(View.GONE);
    }

    public void closeLoader() {
        try {
            loaderView.setVisibility(View.GONE);
            if (mainAct.cabTypesArrList.size() == 0) {
                showNoServiceText();
            } else {
                closeNoServiceText();
            }
        } catch (Exception e) {

        }
    }

    public void setUserProfileJson() {
        userProfileJson = mainAct.userProfileJson;
    }

    public void checkCardConfig() {
        setUserProfileJson();


        if (generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Stripe")) {
            String vStripeCusId = generalFunc.getJsonValue("vStripeCusId", userProfileJson);
            if (vStripeCusId.equals("")) {
                mainAct.OpenCardPaymentAct(true);
            } else {
                showPaymentBox(false, false);
            }
        } else if (generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Braintree")) {

            String vCreditCard = generalFunc.getJsonValue("vCreditCard", userProfileJson);
            String vBrainTreeCustEmail = generalFunc.getJsonValue("vBrainTreeCustEmail", userProfileJson);
            if (vCreditCard.equals("") && vBrainTreeCustEmail.equalsIgnoreCase("")) {
                mainAct.OpenCardPaymentAct(true);
            } else {
                showPaymentBox(false, false);
            }

        } else if (generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Paymaya") ||
                generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Omise") ||
                generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Adyen") ||
                generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Xendit")) {
            String vCreditCard = generalFunc.getJsonValue("vCreditCard", userProfileJson);
            if (vCreditCard.equals("")) {
                mainAct.OpenCardPaymentAct(true);
            } else {
                showPaymentBox(false, false);
            }
        }

    }

    public void checkCardConfig(boolean isOutstanding, boolean isReqNow) {
        mainAct.isOutStanding = isOutstanding;
        setUserProfileJson();


        if (generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Stripe")) {
            String vStripeCusId = generalFunc.getJsonValue("vStripeCusId", userProfileJson);
            if (vStripeCusId.equals("")) {
                mainAct.OpenCardPaymentAct(true);
            } else {
                showPaymentBox(isOutstanding, isReqNow);
            }
        } else if (generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Braintree")) {

            String vCreditCard = generalFunc.getJsonValue("vCreditCard", userProfileJson);
            String vBrainTreeCustEmail = generalFunc.getJsonValue("vBrainTreeCustEmail", userProfileJson);
            if (vCreditCard.equals("") && vBrainTreeCustEmail.equalsIgnoreCase("")) {
                mainAct.OpenCardPaymentAct(true);
            } else {
                showPaymentBox(isOutstanding, isReqNow);
            }

        } else if (generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Paymaya") ||
                generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Omise") ||
                generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Adyen") ||
                generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Xendit")) {
            String vCreditCard = generalFunc.getJsonValue("vCreditCard", userProfileJson);
            if (vCreditCard.equals("")) {
                mainAct.OpenCardPaymentAct(true);
            } else {
                showPaymentBox(isOutstanding, isReqNow);
            }
        }

    }

    public void showPaymentBox(boolean isOutstanding, boolean isReqNow) {
        android.support.v7.app.AlertDialog alertDialog;
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        builder.setTitle("");
        builder.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.input_box_view, null);
        builder.setView(dialogView);

        final MaterialEditText input = (MaterialEditText) dialogView.findViewById(R.id.editBox);
        final MTextView subTitleTxt = (MTextView) dialogView.findViewById(R.id.subTitleTxt);

        Utils.removeInput(input);

        subTitleTxt.setVisibility(View.VISIBLE);
        subTitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_TITLE_PAYMENT_ALERT"));
        input.setText(generalFunc.getJsonValue("vCreditCard", userProfileJson));

        builder.setPositiveButton(generalFunc.retrieveLangLBl("Confirm", "LBL_BTN_TRIP_CANCEL_CONFIRM_TXT"), (dialog, which) -> {
            dialog.cancel();
            if (isOutstanding) {
                callOutStandingPayAmout(isReqNow);

            } else {
                checkPaymentCard();
            }
        });
        builder.setNeutralButton(generalFunc.retrieveLangLBl("Change", "LBL_CHANGE"), (dialog, which) -> {
            dialog.cancel();
            mainAct.OpenCardPaymentAct(true);
            //ridelaterclick = false;

        });
        builder.setNegativeButton(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"), (dialog, which) -> {
            dialog.cancel();
            //ridelaterclick = false;

        });


        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void setCashSelection() {
        payTypeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CASH_TXT"));

        isCardValidated = false;
        mainAct.setCashSelection(true);
        cashRadioBtn.setChecked(true);

        payImgView.setImageResource(R.mipmap.ic_cash_new);
    }

    public void checkPaymentCard() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "CheckCard");
        parameters.put("iUserId", generalFunc.getMemberId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    String action = generalFunc.getJsonValue(Utils.action_str, responseString);
                    if (action.equals("1")) {

                        if (mainAct.pickUpLocation == null) {
                            return;
                        }
                        isCardValidated = true;
                        setCardSelection();

                        if (isCardnowselcted) {
                            isCardnowselcted = false;


                            if (mainAct.isDeliver(mainAct.getCurrentCabGeneralType())) {
                                if (mainAct.getDestinationStatus() == false) {
                                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Please add your destination location " +
                                            "to deliver your package.", "LBL_ADD_DEST_MSG_DELIVER_ITEM"));
                                    return;
                                }
                                mainAct.continueDeliveryProcess();
                                return;
                            } else {
                                if (!mainAct.getCabReqType().equals(Utils.CabReqType_Later)) {

                                    if (cabTypeList.get(selpos).get("eRental") != null && !cabTypeList.get(selpos).get("eRental").equalsIgnoreCase("") &&
                                            cabTypeList.get(selpos).get("eRental").equalsIgnoreCase("Yes")) {

                                        Bundle bn = new Bundle();
                                        bn.putString("address", mainAct.pickUpLocationAddress);
                                        bn.putString("vVehicleType", cabTypeList.get(selpos).get("vRentalVehicleTypeName"));
                                        bn.putString("iVehicleTypeId", cabTypeList.get(selpos).get("iVehicleTypeId"));
                                        bn.putString("vLogo", cabTypeList.get(selpos).get("vLogo1"));
                                        bn.putString("eta", etaTxt.getText().toString());
                                        bn.putString("eMoto", mainAct.eShowOnlyMoto);
                                        bn.putString("PromoCode", appliedPromoCode);


                                        new StartActProcess(getActContext()).startActForResult(
                                                RentalDetailsActivity.class, bn, RENTAL_REQ_CODE);
                                        return;

                                    }
                                    mainAct.continuePickUpProcess();
                                } else {
                                    mainAct.setRideSchedule();
                                }

                            }
                        }

                        if (isCardlaterselcted) {
                            isCardlaterselcted = false;
                            mainAct.chooseDateTime();
                        }

                    } else {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                    }
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    public void setLabels(boolean isCallGenerateType) {

        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT");

        if ((mainAct.currentLoadedDriverList != null && mainAct.currentLoadedDriverList.size() < 1) || mainAct.currentLoadedDriverList == null) {
            ride_now_btn.setText(generalFunc.retrieveLangLBl("No Car available.", "LBL_NO_CARS"));
            if (isCallGenerateType) {
                generateCarType();
            }
            return;

        }

        noServiceTxt.setText(generalFunc.retrieveLangLBl("service not available in this location", "LBL_NO_SERVICE_AVAILABLE_TXT"));


        if (app_type.equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
            currentCabGeneralType = Utils.CabGeneralType_UberX;
        } else {
            String type = mainAct.isDeliver(app_type) || mainAct.isDeliver(RideDeliveryType) ? "Deliver" : Utils.CabGeneralType_Ride;
            if (type.equals("Deliver")) {
                if (mainAct.getCabReqType().equals(Utils.CabReqType_Now)) {
                    ride_now_btn.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_NEXT_TXT"));
                } else if (mainAct.getCabReqType().equals(Utils.CabReqType_Later)) {
                    ride_now_btn.setText(generalFunc.retrieveLangLBl("", "LBL_BTN_NEXT_TXT"));
                }
            } else {
                ride_now_btn.setText(generalFunc.retrieveLangLBl("Request Now", "LBL_REQUEST_NOW"));
            }
        }
        if (isCallGenerateType) {
            generateCarType();
        }

    }

    public boolean calculateDistnace(Location start, Location end) {


        float distance = start.distanceTo(end);

        if (distance > 200) {
            return true;
        } else {
            return false;
        }
    }

    public void releaseResources() {
        isKilled = true;
    }

    public void changeCabGeneralType(String currentCabGeneralType) {
        this.currentCabGeneralType = currentCabGeneralType;
    }

    public String getCurrentCabGeneralType() {
        return this.currentCabGeneralType;
    }

    public void configRideLaterBtnArea(boolean isGone) {
        if (isGone == true || app_type.equalsIgnoreCase("Ride-Delivery")) {
            mainAct.setPanelHeight(237);
            if (!app_type.equalsIgnoreCase("Ride-Delivery")) {
                mainAct.setUserLocImgBtnMargin(105);
            }
            return;
        }
        if (!generalFunc.getJsonValue("RIIDE_LATER", userProfileJson).equalsIgnoreCase("YES") && !app_type.equalsIgnoreCase("Ride-Delivery")) {
            mainAct.setUserLocImgBtnMargin(105);
            mainAct.setPanelHeight(237);
        } else {
            mainAct.setPanelHeight(237);
            currentPanelDefaultStateHeight = 237;
            mainAct.setUserLocImgBtnMargin(164);
        }
    }

    public void generateCarType() {

        if (cabTypeList == null) {
            cabTypeList = new ArrayList<>();
            rentalTypeList = new ArrayList<>();
            if (adapter == null) {
                adapter = new CabTypeAdapter(getActContext(), cabTypeList, generalFunc);
                adapter.setSelectedVehicleTypeId(mainAct.getSelectedCabTypeId());
                carTypeRecyclerView.setAdapter(adapter);
                adapter.setOnItemClickList(this);
            } else {
                adapter.notifyDataSetChanged();
            }
        } else {
            cabTypeList.clear();
            rentalTypeList.clear();
        }

        if (mainAct.isDeliver(currentCabGeneralType)) {
            this.currentCabGeneralType = "Deliver";
        }
//        showLoader();

        for (int i = 0; i < mainAct.cabTypesArrList.size(); i++) {
            //  JSONObject obj_temp = generalFunc.getJsonObject(vehicleTypesArr, i);

            HashMap<String, String> map = new HashMap<>();
            String iVehicleTypeId = mainAct.cabTypesArrList.get(i).get("iVehicleTypeId");

            String vVehicleType = mainAct.cabTypesArrList.get(i).get("vVehicleType");
            String vRentalVehicleTypeName = mainAct.cabTypesArrList.get(i).get("vRentalVehicleTypeName");
            String fPricePerKM = mainAct.cabTypesArrList.get(i).get("fPricePerKM");
            String fPricePerMin = mainAct.cabTypesArrList.get(i).get("fPricePerMin");
            String iBaseFare = mainAct.cabTypesArrList.get(i).get("iBaseFare");
            String fCommision = mainAct.cabTypesArrList.get(i).get("fCommision");
            String iPersonSize = mainAct.cabTypesArrList.get(i).get("iPersonSize");
            String vLogo = mainAct.cabTypesArrList.get(i).get("vLogo");
            String vLogo1 = mainAct.cabTypesArrList.get(i).get("vLogo1");
            String eType = mainAct.cabTypesArrList.get(i).get("eType");

            String eRental = mainAct.cabTypesArrList.get(i).get("eRental");


            if (!eType.equalsIgnoreCase(currentCabGeneralType)) {
                continue;
            }
            map.put("iVehicleTypeId", iVehicleTypeId);
            map.put("vVehicleType", vVehicleType);
            map.put("vRentalVehicleTypeName", vRentalVehicleTypeName);
            map.put("fPricePerKM", fPricePerKM);
            map.put("fPricePerMin", fPricePerMin);
            map.put("iBaseFare", iBaseFare);
            map.put("fCommision", fCommision);
            map.put("iPersonSize", iPersonSize);
            map.put("vLogo", vLogo);
            map.put("vLogo1", vLogo1);
            if (generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX) && mainAct.iscubejekRental) {
                map.put("eRental", eRental);
            } else {
                map.put("eRental", "No");
            }


            if (i == 0) {
                adapter.setSelectedVehicleTypeId(iVehicleTypeId);
            }
//            if (eRental != null && !eRental.equalsIgnoreCase("") && eRental.equalsIgnoreCase("No")) {
            cabTypeList.add(map);
            //  } else
            if (eRental != null && eRental.equalsIgnoreCase("Yes")) {
                HashMap<String, String> rentalmap = (HashMap<String, String>) map.clone();
                rentalmap.put("eRental", "Yes");
                rentalTypeList.add(rentalmap);
            }

        }

        if (!mainAct.iscubejekRental) {
            if (rentalTypeList.size() > 0) {
                if (generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase(Utils.CabGeneralType_Ride) || generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase("Ride-Delivery") || (RideDeliveryType.equalsIgnoreCase(Utils.CabGeneralType_Ride) && mainAct.iscubejekRental == false)) {
                    rentalPkg.setVisibility(View.VISIBLE);
                    rentalarea.setVisibility(View.VISIBLE);
                    rentPkgImage.setVisibility(View.VISIBLE);
                    rentBackPkgImage.setVisibility(View.VISIBLE);


                    Runnable r = new Runnable() {

                        @Override
                        public void run() {
                            try {
                                mainAct.setPanelHeight(280);
                                /*RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) (mainAct.userLocBtnImgView).getLayoutParams();
                                params.bottomMargin = Utils.dipToPixels(getActContext(), 300);
*/
                            } catch (Exception e2) {

                                new Handler().postDelayed(this, 20);
                            }
                        }
                    };
                    new Handler().postDelayed(r, 20);
                    setShadow();
                }

            }
        }

        mainAct.setCabTypeList(cabTypeList);
        adapter.notifyDataSetChanged();

        if (cabTypeList.size() > 0) {
            onItemClick(0);
        }
    }

    public void closeLoadernTxt() {
        loaderView.setVisibility(View.GONE);
        closeNoServiceText();

    }

    public void setShadow() {
        (view.findViewById(R.id.shadowView)).setVisibility(View.GONE);
    }


    public Context getActContext() {
        return mainAct.getActContext();
    }

    @Override
    public void onItemClick(int position) {

        selpos = position;
        String iVehicleTypeId = cabTypeList.get(position).get("iVehicleTypeId");


        if (!iVehicleTypeId.equals(mainAct.getSelectedCabTypeId())) {
            mainAct.selectedCabTypeId = iVehicleTypeId;
            adapter.setSelectedVehicleTypeId(iVehicleTypeId);
            adapter.notifyDataSetChanged();
            mainAct.changeCabType(iVehicleTypeId);

            if (cabTypeList.get(position).get("eFlatTrip") != null &&
                    (!cabTypeList.get(position).get("eFlatTrip").equalsIgnoreCase(""))
                    && cabTypeList.get(position).get("eFlatTrip").equalsIgnoreCase("Yes")) {
                mainAct.isFixFare = true;
            } else {
                mainAct.isFixFare = false;
            }
        } else {
            openFareDetailsDilaog(position);
        }


    }

    public void openFareEstimateDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        builder.setTitle("");

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.fare_detail_design, null);
        builder.setView(dialogView);

        ((MTextView) dialogView.findViewById(R.id.fareDetailHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_FARE_DETAIL_TXT"));
        ((MTextView) dialogView.findViewById(R.id.baseFareHTxt)).setText(" " + generalFunc.retrieveLangLBl("", "LBL_BASE_FARE_TXT"));
        ((MTextView) dialogView.findViewById(R.id.parMinHTxt)).setText(" / " + generalFunc.retrieveLangLBl("", "LBL_MIN_TXT"));
        ((MTextView) dialogView.findViewById(R.id.parMinHTxt)).setVisibility(View.GONE);
        ((MTextView) dialogView.findViewById(R.id.andTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_AND_TXT"));
        ((MTextView) dialogView.findViewById(R.id.parKmHTxt)).setText(" / " + generalFunc.retrieveLangLBl("", "LBL_KM_TXT"));
        ((MTextView) dialogView.findViewById(R.id.parKmHTxt)).setVisibility(View.GONE);

        ((MTextView) dialogView.findViewById(R.id.baseFareVTxt)).setText(currency_sign + " " +
                generalFunc.getSelectedCarTypeData(mainAct.getSelectedCabTypeId(), mainAct.cabTypesArrList, "iBaseFare"));

        ((MTextView) dialogView.findViewById(R.id.parMinVTxt)).setText(currency_sign + " " +
                generalFunc.getSelectedCarTypeData(mainAct.getSelectedCabTypeId(), mainAct.cabTypesArrList, "fPricePerMin") + " / " + generalFunc.retrieveLangLBl("", "LBL_MIN_TXT"));

        ((MTextView) dialogView.findViewById(R.id.parKmVTxt)).setText(currency_sign + " " +
                generalFunc.getSelectedCarTypeData(mainAct.getSelectedCabTypeId(), mainAct.cabTypesArrList, "fPricePerKM") + " / " + generalFunc.retrieveLangLBl("", "LBL_KM_TXT"));

        builder.show();
    }

    public void hidePayTypeSelectionArea() {
        payTypeSelectArea.setVisibility(View.GONE);
        cashcardarea.setVisibility(View.VISIBLE);
        mainAct.setPanelHeight(240 - 10);

        if (!mainAct.iscubejekRental) {

            if (rentalTypeList.size() > 0) {
                Runnable r = new Runnable() {

                    @Override
                    public void run() {
                        try {
                            mainAct.setPanelHeight(290 - 10);
                        } catch (Exception e2) {
                            new Handler().postDelayed(this, 20);
                        }
                    }
                };
                new Handler().postDelayed(r, 20);
            }
        }
    }

    public void checkPromoCode(final String promoCode) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "CheckPromoCode");
        parameters.put("PromoCode", promoCode);
        parameters.put("iUserId", generalFunc.getMemberId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                String action = generalFunc.getJsonValue(Utils.action_str, responseString);
                if (action.equals("1")) {
                    appliedPromoCode = promoCode;

                    findRoute("--");
                }
                generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    public void showPromoBox() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        builder.setTitle(generalFunc.retrieveLangLBl("", "LBL_PROMO_CODE_ENTER_TITLE"));

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.input_box_view, null);
        builder.setView(dialogView);

        final MaterialEditText input = (MaterialEditText) dialogView.findViewById(R.id.editBox);


        if (!appliedPromoCode.equals("")) {
            input.setText(appliedPromoCode);
        }
        builder.setPositiveButton(generalFunc.retrieveLangLBl("OK", "LBL_BTN_OK_TXT"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString().trim().equals("") && appliedPromoCode.equals("")) {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_ENTER_PROMO"));
                } else if (input.getText().toString().trim().equals("") && !appliedPromoCode.equals("")) {
                    appliedPromoCode = "";
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMO_REMOVED"));
                    findRoute("--");
                } else if (input.getText().toString().contains(" ")) {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PROMO_INVALIED"));
                } else {
                    checkPromoCode(input.getText().toString().trim());
                }
            }
        });
        builder.setNegativeButton(generalFunc.retrieveLangLBl("", "LBL_CANCEL_GENERAL"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        android.support.v7.app.AlertDialog alertDialog = builder.create();
        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(alertDialog);
        }
        alertDialog.show();
        alertDialog.setOnCancelListener(dialogInterface -> Utils.hideKeyboard(mainAct));

    }

    public void buildNoCabMessage(String message, String positiveBtn) {

        if (mainAct.noCabAvailAlertBox != null) {
            mainAct.noCabAvailAlertBox.closeAlertBox();
            mainAct.noCabAvailAlertBox = null;
        }

        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(true);
        generateAlert.setBtnClickList(btn_id -> generateAlert.closeAlertBox());
        generateAlert.setContentMessage("", message);
        generateAlert.setPositiveBtn(positiveBtn);
        generateAlert.showAlertBox();

        mainAct.noCabAvailAlertBox = generateAlert;
    }

    public String getAppliedPromoCode() {
        return this.appliedPromoCode;
    }

    public void findRoute(String etaVal) {
        try {

            String originLoc = mainAct.getPickUpLocation().getLatitude() + "," + mainAct.getPickUpLocation().getLongitude();
            String destLoc = null;
            if (mainAct.destLocation != null) {
                destLoc = mainAct.getDestLocLatitude() + "," + mainAct.getDestLocLongitude();
            } else {
                destLoc = mainAct.getPickUpLocation().getLatitude() + "," + mainAct.getPickUpLocation().getLongitude();

            }

            mProgressBar.setIndeterminate(true);
            mProgressBar.setVisibility(View.VISIBLE);


            String serverKey = generalFunc.retrieveValue(Utils.GOOGLE_SERVER_ANDROID_PASSENGER_APP_KEY);
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + originLoc + "&destination=" + destLoc + "&sensor=true&key=" + serverKey + "&language=" + generalFunc.retrieveValue(Utils.GOOGLE_MAP_LANGUAGE_CODE_KEY) + "&sensor=true";

            ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), url, true);
            exeWebServer.setLoaderConfig(getActContext(), false, generalFunc);
            exeWebServer.setDataResponseListener(responseString -> {

                mProgressBar.setIndeterminate(false);
                mProgressBar.setVisibility(View.INVISIBLE);

                if (responseString != null && !responseString.equals("")) {
                    String status = generalFunc.getJsonValue("status", responseString);
                    if (status.equals("OK")) {
                        isRouteFail = false;

                        JSONArray obj_routes = generalFunc.getJsonArray("routes", responseString);
                        if (obj_routes != null && obj_routes.length() > 0) {
                            JSONObject obj_legs = generalFunc.getJsonObject(generalFunc.getJsonArray("legs", generalFunc.getJsonObject(obj_routes, 0).toString()), 0);


                            distance = "" + (generalFunc.parseDoubleValue(0, generalFunc.getJsonValue("value",
                                    generalFunc.getJsonValue("distance", obj_legs.toString()).toString())));

                            time = "" + (generalFunc.parseDoubleValue(0, generalFunc.getJsonValue("value",
                                    generalFunc.getJsonValue("duration", obj_legs.toString()).toString())));

                            sourceLocation = new LatLng(generalFunc.parseDoubleValue(0.0, generalFunc.getJsonValue("lat", generalFunc.getJsonValue("start_location", obj_legs.toString()))),
                                    generalFunc.parseDoubleValue(0.0, generalFunc.getJsonValue("lng", generalFunc.getJsonValue("start_location", obj_legs.toString()))));

                            destLocation = new LatLng(generalFunc.parseDoubleValue(0.0, generalFunc.getJsonValue("lat", generalFunc.getJsonValue("end_location", obj_legs.toString()))),
                                    generalFunc.parseDoubleValue(0.0, generalFunc.getJsonValue("lng", generalFunc.getJsonValue("end_location", obj_legs.toString()))));

                            if (getActivity() != null) {
                                estimateFare(distance, time);
                            }

                            //temp animation test


                            handleMapAnimation(responseString, sourceLocation, destLocation, etaVal);
                        }

                    } else {


                        isRouteFail = true;
                        if (!isSkip)

                        {

                            GenerateAlertBox alertBox = new GenerateAlertBox(getActContext());
                            alertBox.setContentMessage("", generalFunc.retrieveLangLBl("Route not found", "LBL_DEST_ROUTE_NOT_FOUND"));
                            alertBox.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                            alertBox.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                                @Override
                                public void handleBtnClick(int btn_id) {
                                    alertBox.closeAlertBox();
                                    mainAct.userLocBtnImgView.performClick();

                                }
                            });
                            alertBox.showAlertBox();

                        }

                        if (isSkip) {
                            isRouteFail = false;
                            if (mainAct.destLocation != null && mainAct.pickUpLocation != null) {
                                handleMapAnimation(responseString, new LatLng(mainAct.pickUpLocation.getLatitude(), mainAct.pickUpLocation.getLongitude()), new LatLng(mainAct.destLocation.getLatitude(), mainAct.destLocation.getLongitude()), "--");
                            }
                        } else {
                            mainAct.userLocBtnImgView.performClick();
                        }
//
                        isSkip = true;
                        if (getActivity() != null) {
                            estimateFare(null, null);
                        }

//                            if (mainAct.destLocation != null) {
//                                ride_now_btn.setEnabled(false);
//                                ride_now_btn.setTextColor(Color.parseColor("#BABABA"));
//                                ride_now_btn.setClickable(false);
//                            }
                    }

                }
            });
            exeWebServer.execute();
        } catch (Exception e) {

        }
    }

    View marker_view;
    MTextView addressTxt, etaTxt;


    public void setEta(String time) {
        if (etaTxt != null) {
            etaTxt.setText(time);
        }


    }


    public void mangeMrakerPostion() {
        try {

            if (mainAct.pickUpLocation != null) {
                Point PickupPoint = mainAct.getMap().getProjection().toScreenLocation(new LatLng(mainAct.pickUpLocation.getLatitude(), mainAct.pickUpLocation.getLongitude()));
                if (sourceMarker != null) {
                    sourceMarker.setAnchor(PickupPoint.x < Utils.dpToPx(getActContext(), 200) ? 0.00f : 1.00f, PickupPoint.y < Utils.dpToPx(getActContext(), 100) ? 0.20f : 1.20f);
                }
            }
            if (destLocation != null) {
                Point DestinationPoint = mainAct.getMap().getProjection().toScreenLocation(destLocation);
                //dest
                if (destMarker != null) {
                    destMarker.setAnchor(DestinationPoint.x < Utils.dpToPx(getActContext(), 200) ? 0.00f : 1.00f, DestinationPoint.y < Utils.dpToPx(getActContext(), 100) ? 0.20f : 1.20f);
                }
            }
        } catch (Exception e) {

        }


    }


    public void handleSourceMarker(String etaVal) {
        if (!isSkip) {
            if (mainAct.pickUpLocation == null) {
                return;
            }
        }

        if (marker_view == null) {
            marker_view = ((LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.custom_marker, null);
            addressTxt = (MTextView) marker_view
                    .findViewById(R.id.addressTxt);
            etaTxt = (MTextView) marker_view.findViewById(R.id.etaTxt);
        }

        addressTxt.setTextColor(getActContext().getResources().getColor(R.color.sourceAddressTxt));

        LatLng fromLnt;
        if (isSkip) {
            estimateFare(null, null);
            if (destMarker != null) {
                destMarker.remove();
            }
            if (destDotMarker != null) {
                destDotMarker.remove();
            }
            if (route_polyLine != null) {
                route_polyLine.remove();
            }

            destLocation = null;
            mainAct.destLocation = null;

            fromLnt = new LatLng(mainAct.pickUpLocation.getLatitude(), mainAct.pickUpLocation.getLongitude());

        } else {
            fromLnt = new LatLng(mainAct.pickUpLocation.getLatitude(), mainAct.pickUpLocation.getLongitude());

            if (sourceLocation != null) {
                fromLnt = sourceLocation;
            }


        }


        etaTxt.setVisibility(View.VISIBLE);
        etaTxt.setText(etaVal);

        if (sourceMarker != null) {
            sourceMarker.remove();
        }

        if (source_dot_option != null) {
            sourceDotMarker.remove();
        }

        source_dot_option = new MarkerOptions().position(fromLnt).icon(BitmapDescriptorFactory.fromResource(R.mipmap.dot));

        if (mainAct.getMap() != null) {
            sourceDotMarker = mainAct.getMap().addMarker(source_dot_option);
        }
        addressTxt.setText(mainAct.pickUpLocationAddress);
        MarkerOptions marker_opt_source = new MarkerOptions().position(fromLnt).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActContext(), marker_view))).anchor(0.00f, 0.20f);
        if (mainAct.getMap() != null) {
            sourceMarker = mainAct.getMap().addMarker(marker_opt_source);
            sourceMarker.setTag("1");
        }

        buildBuilder();

        if (isSkip) {
         /*   if (mainAct.getMap() != null) {
                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        new LatLng(mainAct.pickUpLocation.getLatitude(), mainAct.pickUpLocation.getLongitude()))
                        .zoom(Utils.defaultZomLevel).build();
                mainAct.getMap().moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }*/
        }


    }


    public void handleMapAnimation(String responseString, LatLng sourceLocation, LatLng destLocation, String etaVal) {


        //    mainAct.getMap().clear();
        if (mainAct.cabSelectionFrag == null) {
            return;
        }
        MapAnimator.getInstance().stopRouteAnim();

        LatLng fromLnt = new LatLng(sourceLocation.latitude, sourceLocation.longitude);
        LatLng toLnt = new LatLng(destLocation.latitude, destLocation.longitude);


        if (marker_view == null) {

            marker_view = ((LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.custom_marker, null);
            addressTxt = (MTextView) marker_view
                    .findViewById(R.id.addressTxt);
            etaTxt = (MTextView) marker_view.findViewById(R.id.etaTxt);
        }

        addressTxt.setTextColor(getActContext().getResources().getColor(R.color.destAddressTxt));


        addressTxt.setText(mainAct.destAddress);

        MarkerOptions marker_opt_dest = new MarkerOptions().position(toLnt);
        etaTxt.setVisibility(View.GONE);

        marker_opt_dest.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActContext(), marker_view))).anchor(0.00f, 0.20f);
        if (dest_dot_option != null) {
            destDotMarker.remove();
        }
        dest_dot_option = new MarkerOptions().position(toLnt).icon(BitmapDescriptorFactory.fromResource(R.mipmap.dot));
        destDotMarker = mainAct.getMap().addMarker(dest_dot_option);

        if (destMarker != null) {
            destMarker.remove();
        }
        destMarker = mainAct.getMap().addMarker(marker_opt_dest);
        destMarker.setTag("2");
       /* LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(fromLnt);
        builder.include(toLnt);*/

        handleSourceMarker(etaVal);

        JSONArray obj_routes1 = generalFunc.getJsonArray("routes", responseString);


        if (obj_routes1 != null && obj_routes1.length() > 0) {
            PolylineOptions lineOptions = getGoogleRouteOptions(responseString, Utils.dipToPixels(getActContext(), 5), getActContext().getResources().getColor(android.R.color.black));

            if (lineOptions != null) {
                if (route_polyLine != null) {
                    route_polyLine.remove();
                    route_polyLine = null;

                }
                route_polyLine = mainAct.getMap().addPolyline(lineOptions);
                route_polyLine.remove();
            }
        }

        DisplayMetrics metrics = new DisplayMetrics();
        mainAct.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
//        mainAct.getMap().moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), width - Utils.dpToPx(getActContext(), 80), metrics.heightPixels - Utils.dipToPixels(getActContext(), 300), 0));

        if (route_polyLine != null && route_polyLine.getPoints().size() > 1) {
            MapAnimator.getInstance().animateRoute(mainAct.getMap(), route_polyLine.getPoints(), getActContext());
        }

        mainAct.getMap().setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {

                DisplayMetrics displaymetrics = new DisplayMetrics();
                mainAct.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int height = displaymetrics.heightPixels;
                int width = displaymetrics.widthPixels;


            }
        });


//        mainAct.getMap().setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                if (marker == null) {
//                    return false;
//                }
//
//                if (marker.getTag().equals("1")) {
//                    if (mainAct.mainHeaderFrag != null) {
//                        mainAct.mainHeaderFrag.pickupLocArea1.performClick();
//                    }
//
//                } else if (marker.getTag().equals("2")) {
//                    if (mainAct.mainHeaderFrag != null) {
//                        mainAct.mainHeaderFrag.destarea.performClick();
//                    }
//
//                }
//
//                return false;
//            }
//        });


        if (mainAct.loadAvailCabs != null) {
            mainAct.loadAvailCabs.changeCabs();
        }


    }

    public void buildBuilder() {
        if (mainAct == null) {
            return;
        }
        if (sourceMarker != null && (destMarker == null || isSkip)) {

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            if (sourceMarker != null) {
                builder.include(sourceMarker.getPosition());

                DisplayMetrics metrics = new DisplayMetrics();
                mainAct.getWindowManager().getDefaultDisplay().getMetrics(metrics);

                int width = metrics.widthPixels;
                int height = metrics.heightPixels;
                int padding = 0; // offset from edges of the map in pixels

                LatLngBounds bounds = builder.build();
                LatLng center = bounds.getCenter();
                LatLng northEast = SphericalUtil.computeOffset(center, 30 * Math.sqrt(2.0), SphericalUtil.computeHeading(center, bounds.northeast));
                LatLng southWest = SphericalUtil.computeOffset(center, 30 * Math.sqrt(2.0), (180 + (180 + SphericalUtil.computeHeading(center, bounds.southwest))));
                builder.include(southWest);
                builder.include(northEast);

                mainAct.getMap().moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), width - Utils.dipToPixels(getActContext(), 80), height - ((fragmentHeight + 5) + Utils.dipToPixels(getActContext(), 60)), padding));


            }

            } else if (mainAct.map != null && mainAct.map.getView().getViewTreeObserver().isAlive()) {
            mainAct.map.getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressLint("NewApi") // We check which build version we are using.
                @Override
                public void onGlobalLayout() {

                    boolean isBoundIncluded = false;

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    if (sourceMarker != null) {
                        isBoundIncluded = true;
                        builder.include(sourceMarker.getPosition());
                    }


                    if (destMarker != null) {
                        isBoundIncluded = true;
                        builder.include(destMarker.getPosition());
                    }


                    if (isBoundIncluded) {

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            mainAct.map.getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            mainAct.map.getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }


                        LatLngBounds bounds = builder.build();


                        LatLng center = bounds.getCenter();

                        LatLng northEast = SphericalUtil.computeOffset(center, 10 * Math.sqrt(2.0), SphericalUtil.computeHeading(center, bounds.northeast));
                        LatLng southWest = SphericalUtil.computeOffset(center, 10 * Math.sqrt(2.0), (180 + (180 + SphericalUtil.computeHeading(center, bounds.southwest))));

                        builder.include(southWest);
                        builder.include(northEast);

                        /*  Method 1 */
//                            mainAct.getMap().moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

                        DisplayMetrics metrics = new DisplayMetrics();
                        mainAct.getWindowManager().getDefaultDisplay().getMetrics(metrics);

                        int width = metrics.widthPixels;
                        int height = metrics.heightPixels;
                        // Set Padding according to included bounds

                        int padding = (int) (width * 0.25); // offset from edges of the map 10% of screen


                        /*  Method 2 */
                            /*Utils.printELog("MapHeight","newLatLngZoom");
                            mainAct.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(builder.build().getCenter(),16));*/

                        try {
                            /*  Method 3 */
                            int screenWidth = getResources().getDisplayMetrics().widthPixels;
                            int screenHeight = getResources().getDisplayMetrics().heightPixels;
                            padding = (height - ((fragmentHeight + 5) + Utils.dipToPixels(getActContext(), 60))) / 3;
                            Utils.printELog("MapHeight", "cameraUpdate" + padding);
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,
                                    screenWidth, screenHeight, padding);
                            mainAct.getMap().animateCamera(cameraUpdate);
                        } catch (Exception e) {
                            e.printStackTrace();

                            /*  Method 1 */
                            mainAct.getMap().moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), width - Utils.dipToPixels(getActContext(), 80), height - ((fragmentHeight + 5) + Utils.dipToPixels(getActContext(), 60)), padding));
                        }


                    }

                }
            });
        }
    }

    private boolean areBoundsTooSmall(LatLngBounds bounds, int minDistanceInMeter) {
        Utils.printELog("MapHeight", "minDistanceInMeter" + minDistanceInMeter);
        float[] result = new float[1];
        Location.distanceBetween(bounds.southwest.latitude, bounds.southwest.longitude, bounds.northeast.latitude, bounds.northeast.longitude, result);
        return result[0] < minDistanceInMeter;
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public PolylineOptions getGoogleRouteOptions(String directionJson, int width, int color) {
        PolylineOptions lineOptions = new PolylineOptions();

        try {
            DirectionsJSONParser parser = new DirectionsJSONParser();
            List<List<HashMap<String, String>>> routes_list = parser.parse(new JSONObject(directionJson));

            ArrayList<LatLng> points = new ArrayList<LatLng>();

            if (routes_list.size() > 0) {
                // Fetching i-th route
                List<HashMap<String, String>> path = routes_list.get(0);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);

                }

                lineOptions.addAll(points);
                lineOptions.width(width);
                lineOptions.color(color);

                return lineOptions;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public String getAvailableCarTypesIds() {
        String carTypesIds = "";
        for (int i = 0; i < mainAct.cabTypesArrList.size(); i++) {
            String iVehicleTypeId = mainAct.cabTypesArrList.get(i).get("iVehicleTypeId");

            carTypesIds = carTypesIds.equals("") ? iVehicleTypeId : (carTypesIds + "," + iVehicleTypeId);
        }
        return carTypesIds;
    }

    public void estimateFare(final String distance, String time) {


        //  loaderView.setVisibility(View.VISIBLE);

        if (estimateFareTask != null) {
            estimateFareTask.cancel(true);
            estimateFareTask = null;
        }
        if (distance == null && time == null) {
            //  mainAct.noCabAvail = false;
            // isroutefound = false;

        } else {
            if (mainAct.loadAvailCabs != null) {
                if (mainAct.loadAvailCabs.isAvailableCab) {
                    isroutefound = true;
                    if (!mainAct.timeval.equalsIgnoreCase("\n" + "--")) {
                        mainAct.noCabAvail = false;
                    }
                }
            }

        }

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "estimateFareNew");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("SelectedCarTypeID", getAvailableCarTypesIds());
        if (distance != null && time != null) {
            parameters.put("distance", distance);
            parameters.put("time", time);
        }
        parameters.put("SelectedCar", mainAct.getSelectedCabTypeId());
        parameters.put("PromoCode", getAppliedPromoCode());

        if (mainAct.getPickUpLocation() != null) {
            parameters.put("StartLatitude", "" + mainAct.getPickUpLocation().getLatitude());
            parameters.put("EndLongitude", "" + mainAct.getPickUpLocation().getLongitude());
        }

        if (mainAct.getDestLocLatitude() != null && !mainAct.getDestLocLatitude().equalsIgnoreCase("")) {
            parameters.put("DestLatitude", "" + mainAct.getDestLocLatitude());
            parameters.put("DestLongitude", "" + mainAct.getDestLocLongitude());
        }

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        estimateFareTask = exeWebServer;
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {

                    JSONArray vehicleTypesArr = generalFunc.getJsonArray(Utils.message_str, responseString);
                    for (int i = 0; i < vehicleTypesArr.length(); i++) {

                        JSONObject obj_temp = generalFunc.getJsonObject(vehicleTypesArr, i);

                        if (distance != null) {

                            String type = mainAct.getCurrentCabGeneralType();
                            if (type.equalsIgnoreCase("rental")) {
                                type = Utils.CabGeneralType_Ride;
                            }
                            if (generalFunc.getJsonValue("eType", obj_temp.toString()).
                                    contains(type)) {


                                if (cabTypeList != null) {
                                    for (int k = 0; k < cabTypeList.size(); k++) {
                                        HashMap<String, String> map = cabTypeList.get(k);

                                        if (/*map.get("vVehicleType").equalsIgnoreCase(generalFunc.getJsonValue("vVehicleType", obj_temp.toString()))
                                                && */map.get("iVehicleTypeId").equalsIgnoreCase(generalFunc.getJsonValue("iVehicleTypeId", obj_temp.toString()))) {

                                            String totalfare = "";

                                            if (generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX)) {
                                                if (map.get("eRental").equalsIgnoreCase("Yes") && mainAct.iscubejekRental) {
                                                    totalfare = generalFunc.getJsonValue("eRental_total_fare", obj_temp.toString());
                                                } else {
                                                    totalfare = generalFunc.getJsonValue("total_fare", obj_temp.toString());
                                                }
                                            } else {
                                                totalfare = generalFunc.getJsonValue("total_fare", obj_temp.toString());
                                            }

                                            if (!totalfare.equals("") && totalfare != null) {
                                                map.put("total_fare", totalfare);
                                                map.put("eFlatTrip", generalFunc.getJsonValue("eFlatTrip", obj_temp.toString()));
                                                cabTypeList.set(k, map);
                                            } else {
                                                map.put("eFlatTrip", generalFunc.getJsonValue("eFlatTrip", obj_temp.toString()));
                                                cabTypeList.set(k, map);
                                            }
                                        } else {

                                        }

                                    }
                                }

                                if (rentalTypeList != null) {
                                    for (int k = 0; k < rentalTypeList.size(); k++) {
                                        HashMap<String, String> map = rentalTypeList.get(k);

                                        if (/*map.get("vVehicleType").equalsIgnoreCase(generalFunc.getJsonValue("vVehicleType", obj_temp.toString()))
                                                && */map.get("iVehicleTypeId").equalsIgnoreCase(generalFunc.getJsonValue("iVehicleTypeId", obj_temp.toString()))) {

                                            String totalfare = generalFunc.getJsonValue("eRental_total_fare", obj_temp.toString());
                                            if (!totalfare.equals("") && totalfare != null) {
                                                map.put("total_fare", totalfare);
                                                map.put("eFlatTrip", generalFunc.getJsonValue("eFlatTrip", obj_temp.toString()));
                                                rentalTypeList.set(k, map);
                                            } else {
                                                map.put("eFlatTrip", generalFunc.getJsonValue("eFlatTrip", obj_temp.toString()));
                                                rentalTypeList.set(k, map);
                                            }
                                        } else {

                                        }

                                    }
                                }


                            }
                        } else {


                            if (generalFunc.getJsonValue("eType", obj_temp.toString()).equalsIgnoreCase(mainAct.getCurrentCabGeneralType())) {

                                for (int k = 0; k < cabTypeList.size(); k++) {
                                    HashMap<String, String> map = cabTypeList.get(k);

                                    if (mainAct.iscubejekRental) {
                                        if (/*map.get("vVehicleType").equalsIgnoreCase(generalFunc.getJsonValue("vVehicleType", obj_temp.toString()))
                                            &&*/ map.get("iVehicleTypeId").equalsIgnoreCase(generalFunc.getJsonValue("iVehicleTypeId", obj_temp.toString()))) {
                                            String totalfare = generalFunc.getJsonValue("eRental_total_fare", obj_temp.toString());
                                            if (!totalfare.equals("") && totalfare != null) {
                                                map.put("total_fare", totalfare);
                                                map.put("eFlatTrip", generalFunc.getJsonValue("eFlatTrip", obj_temp.toString()));
                                                rentalTypeList.set(k, map);
                                            } else {
                                                map.put("eFlatTrip", generalFunc.getJsonValue("eFlatTrip", obj_temp.toString()));
                                                rentalTypeList.set(k, map);
                                            }

                                        }

                                    } else {

                                        if (/*map.get("vVehicleType").equalsIgnoreCase(generalFunc.getJsonValue("vVehicleType", obj_temp.toString()))
                                            &&*/ map.get("iVehicleTypeId").equalsIgnoreCase(generalFunc.getJsonValue("iVehicleTypeId", obj_temp.toString()))) {
                                            map.put("total_fare", "");
                                            cabTypeList.set(k, map);

                                            Utils.printELog("cabTypeList", ":: " + cabTypeList);
                                        }
                                    }
                                }

                                if (rentalTypeList != null) {
                                    for (int k = 0; k < rentalTypeList.size(); k++) {
                                        HashMap<String, String> map = rentalTypeList.get(k);

                                        if (/*map.get("vVehicleType").equalsIgnoreCase(generalFunc.getJsonValue("vVehicleType", obj_temp.toString()))
                                                && */map.get("iVehicleTypeId").equalsIgnoreCase(generalFunc.getJsonValue("iVehicleTypeId", obj_temp.toString()))) {

                                            String totalfare = generalFunc.getJsonValue("eRental_total_fare", obj_temp.toString());
                                            if (!totalfare.equals("") && totalfare != null) {
                                                map.put("total_fare", totalfare);
                                                map.put("eFlatTrip", generalFunc.getJsonValue("eFlatTrip", obj_temp.toString()));
                                                rentalTypeList.set(k, map);
                                            } else {
                                                map.put("eFlatTrip", generalFunc.getJsonValue("eFlatTrip", obj_temp.toString()));
                                                rentalTypeList.set(k, map);
                                            }
                                        } else {

                                        }

                                    }
                                }
                            }
                        }

//                            if (generalFunc.getJsonValue("eFlatTrip", responseString).equalsIgnoreCase("Yes")) {
//                                mainAct.isFixFare = true;
//                            } else {
//                                mainAct.isFixFare = false;
//
//                            }

                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        exeWebServer.execute();
    }

    public void openFareDetailsDilaog(final int pos) {

        // if (cabTypeList.get(pos).get("total_fare") != null && !cabTypeList.get(pos).get("total_fare").equalsIgnoreCase("")) {
        if (cabTypeList.get(pos).get("total_fare") != null) {
            String vehicleIconPath = CommonUtilities.SERVER_URL + "webimages/icons/VehicleType/";
            String vehicleDefaultIconPath = CommonUtilities.SERVER_URL + "webimages/icons/DefaultImg/";
            final BottomSheetDialog faredialog = new BottomSheetDialog(getActContext());

            View contentView = View.inflate(getContext(), R.layout.dailog_faredetails, null);
            faredialog.setContentView(contentView);
            BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) contentView.getParent());
            mBehavior.setPeekHeight(1500);
            View bottomSheetView = faredialog.getWindow().getDecorView().findViewById(android.support.design.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheetView).setHideable(false);
            setCancelable(faredialog, false);

            ImageView imagecar;
            final MTextView carTypeTitle, capacityHTxt, capacityVTxt, fareHTxt, fareVTxt, mordetailsTxt, farenoteTxt, pkgMsgTxt;
            MButton btn_type2;
            int submitBtnId;
            imagecar = (ImageView) faredialog.findViewById(R.id.imagecar);
            carTypeTitle = (MTextView) faredialog.findViewById(R.id.carTypeTitle);
            capacityHTxt = (MTextView) faredialog.findViewById(R.id.capacityHTxt);
            capacityVTxt = (MTextView) faredialog.findViewById(R.id.capacityVTxt);
            fareHTxt = (MTextView) faredialog.findViewById(R.id.fareHTxt);
            fareVTxt = (MTextView) faredialog.findViewById(R.id.fareVTxt);
            mordetailsTxt = (MTextView) faredialog.findViewById(R.id.mordetailsTxt);
            farenoteTxt = (MTextView) faredialog.findViewById(R.id.farenoteTxt);
            pkgMsgTxt = (MTextView) faredialog.findViewById(R.id.pkgMsgTxt);

            btn_type2 = ((MaterialRippleLayout) faredialog.findViewById(R.id.btn_type2)).getChildView();
            submitBtnId = Utils.generateViewId();
            btn_type2.setId(submitBtnId);


            capacityHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CAPACITY"));
            fareHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_FARE_TXT"));
            mordetailsTxt.setText(generalFunc.retrieveLangLBl("", "LBL_MORE_DETAILS"));

            if (mainAct.isFixFare) {
                farenoteTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GENERAL_NOTE_FLAT_FARE_EST"));
            } else {
                farenoteTxt.setText(generalFunc.retrieveLangLBl("", "LBL_GENERAL_NOTE_FARE_EST"));
            }
            btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_DONE"));

            if (cabTypeList.get(pos).get("eRental") != null && cabTypeList.get(pos).get("eRental").equalsIgnoreCase("Yes")) {
                mordetailsTxt.setVisibility(View.GONE);
                pkgMsgTxt.setVisibility(View.VISIBLE);
                fareHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PKG_STARTING_AT"));

                if (mainAct.eShowOnlyMoto != null && mainAct.eShowOnlyMoto.equalsIgnoreCase("Yes")) {
                    pkgMsgTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_MOTO_PKG_MSG"));
                } else {
                    pkgMsgTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_PKG_MSG"));
                }
                farenoteTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RENT_PKG_DETAILS"));
            }


            if (!cabTypeList.get(pos).get("eRental").equals("") && cabTypeList.get(pos).get("eRental").equals("Yes")) {
                carTypeTitle.setText(cabTypeList.get(pos).get("vRentalVehicleTypeName"));
            } else {
                carTypeTitle.setText(cabTypeList.get(pos).get("vVehicleType"));
            }
            if (cabTypeList.get(pos).get("total_fare") != null && !cabTypeList.get(pos).get("total_fare").equalsIgnoreCase("")) {
                fareVTxt.setText(generalFunc.convertNumberWithRTL(cabTypeList.get(pos).get("total_fare")));
            } else {
                fareVTxt.setText("--");
            }
            if (mainAct.getCurrentCabGeneralType().equals(Utils.CabGeneralType_Ride)) {
                capacityVTxt.setText(generalFunc.convertNumberWithRTL(cabTypeList.get(pos).get("iPersonSize")) + " " + generalFunc.retrieveLangLBl("", "LBL_PEOPLE_TXT"));

            } else {
                capacityVTxt.setText("---");
            }

            String imgName = cabTypeList.get(pos).get("vLogo1");
            if (imgName.equals("")) {
                imgName = vehicleDefaultIconPath + "hover_ic_car.png";
            } else {
                imgName = vehicleIconPath + cabTypeList.get(pos).get("iVehicleTypeId") + "/android/" + "xxxhdpi_" +
                        cabTypeList.get(pos).get("vLogo1");


            }

            Picasso.with(getActContext())
                    .load(imgName)
                    .into(imagecar, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                        }
                    });


            btn_type2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    faredialog.dismiss();

                }
            });

            mordetailsTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    dialogShowOnce = true;
                    Bundle bn = new Bundle();
                    bn.putString("SelectedCar", cabTypeList.get(pos).get("iVehicleTypeId"));
                    bn.putString("iUserId", generalFunc.getMemberId());
                    bn.putString("distance", distance);
                    bn.putString("time", time);
                    bn.putString("PromoCode", appliedPromoCode);
                    if (cabTypeList.get(pos).get("eRental").equals("yes")) {
                        bn.putString("vVehicleType", cabTypeList.get(pos).get("vRentalVehicleTypeName"));
                    } else {
                        bn.putString("vVehicleType", cabTypeList.get(pos).get("vVehicleType"));
                    }
                    bn.putBoolean("isSkip", isSkip);
                    if (mainAct.getPickUpLocation() != null) {
                        bn.putString("picupLat", mainAct.getPickUpLocation().getLatitude() + "");
                        bn.putString("pickUpLong", mainAct.getPickUpLocation().getLongitude() + "");
                    }
                    if (mainAct.destLocation != null) {
                        bn.putString("destLat", mainAct.destLocLatitude + "");
                        bn.putString("destLong", mainAct.destLocLongitude + "");
                    }
                    if (mainAct.isFixFare) {
                        bn.putBoolean("isFixFare", true);
                    } else {
                        bn.putBoolean("isFixFare", false);
                    }

                    new StartActProcess(getActContext()).startActWithData(FareBreakDownActivity.class, bn);
                    faredialog.dismiss();
                }
            });


            faredialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                }
            });
            faredialog.show();
        }


    }

    public void setCancelable(Dialog dialogview, boolean cancelable) {
        final Dialog dialog = dialogview;
        View touchOutsideView = dialog.getWindow().getDecorView().findViewById(android.support.design.R.id.touch_outside);
        View bottomSheetView = dialog.getWindow().getDecorView().findViewById(android.support.design.R.id.design_bottom_sheet);

        if (cancelable) {
            touchOutsideView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog.isShowing()) {
                        dialog.cancel();
                    }
                }
            });
            BottomSheetBehavior.from(bottomSheetView).setHideable(true);
        } else {
            touchOutsideView.setOnClickListener(null);
            BottomSheetBehavior.from(bottomSheetView).setHideable(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        releseInstances();
    }

    private void releseInstances() {
        Utils.hideKeyboard(getActContext());
        if (estimateFareTask != null) {
            estimateFareTask.cancel(true);
            estimateFareTask = null;
        }
    }

    public void Checkpickupdropoffrestriction() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "Checkpickupdropoffrestriction");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("PickUpLatitude", "" + mainAct.getPickUpLocation().getLatitude());
        parameters.put("PickUpLongitude", "" + mainAct.getPickUpLocation().getLongitude());
        parameters.put("DestLatitude", mainAct.getDestLocLatitude());
        parameters.put("DestLongitude", mainAct.getDestLocLongitude());
        parameters.put("UserType", Utils.userType);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                String message = generalFunc.getJsonValue(Utils.message_str, responseString);
                if (responseString != null && !responseString.equals("")) {
                    if (generalFunc.getJsonValue("Action", responseString).equalsIgnoreCase("0")) {
                        if (message.equalsIgnoreCase("LBL_DROP_LOCATION_NOT_ALLOW")) {
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_DROP_LOCATION_NOT_ALLOW"));
                        } else if (message.equalsIgnoreCase("LBL_PICKUP_LOCATION_NOT_ALLOW")) {
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_PICKUP_LOCATION_NOT_ALLOW"));
                        }
                    } else if (generalFunc.getJsonValue("Action", responseString).equalsIgnoreCase("1")) {
                        mainAct.continueDeliveryProcess();
                    }

                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        releseInstances();
    }

    public void outstandingDialog(boolean isReqNow) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dailog_outstanding, null);

        final MTextView outStandingTitle = (MTextView) dialogView.findViewById(R.id.outStandingTitle);
        final MTextView outStandingValue = (MTextView) dialogView.findViewById(R.id.outStandingValue);
        final MTextView cardtitleTxt = (MTextView) dialogView.findViewById(R.id.cardtitleTxt);
        final MTextView adjustTitleTxt = (MTextView) dialogView.findViewById(R.id.adjustTitleTxt);
        final LinearLayout cardArea = (LinearLayout) dialogView.findViewById(R.id.cardArea);
        final LinearLayout adjustarea = (LinearLayout) dialogView.findViewById(R.id.adjustarea);
        outStandingTitle.setText(generalFunc.retrieveLangLBl("", "LBL_OUTSTANDING_AMOUNT_TXT"));
        String type = mainAct.getCurrentCabGeneralType();
        adjustTitleTxt.setText(generalFunc.retrieveLangLBl("Adjust in Your trip", "LBL_ADJUST_OUT_AMT_RIDE_TXT"));
        if(type.equalsIgnoreCase("Ride")) {
            adjustTitleTxt.setText(generalFunc.retrieveLangLBl("Adjust in Your trip", "LBL_ADJUST_OUT_AMT_RIDE_TXT"));
        }
        else if(type.equalsIgnoreCase("Deliver"))
        {
            adjustTitleTxt.setText(generalFunc.retrieveLangLBl("Adjust in Your trip", "LBL_ADJUST_OUT_AMT_DELIVERY_TXT"));
        }
        outStandingValue.setText(generalFunc.getJsonValue("fOutStandingAmountWithSymbol", userProfileJson));
        cardtitleTxt.setText(generalFunc.retrieveLangLBl("Pay Now", "LBL_PAY_NOW"));

        if (generalFunc.getJsonValue("APP_PAYMENT_MODE", userProfileJson).equalsIgnoreCase("Cash-Card") ||
                generalFunc.getJsonValue("APP_PAYMENT_MODE", userProfileJson).equalsIgnoreCase("Card")) {
            cardArea.setVisibility(View.VISIBLE);

        }
        cardArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ridenowclick = false;
                outstanding_dialog.dismiss();
                checkCardConfig(true, isReqNow);
                //callOutStandingPayAmout(isReqNow);

            }
        });
        adjustarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outstanding_dialog.dismiss();

                //ridenowclick = false;
                if (isReqNow) {
                    isOutStandingDailogShow = true;
                    ride_now_btn.performClick();
                } else {
                    isOutStandingDailogShow = true;
                    img_ridelater.performClick();

                }
            }
        });


        MButton btn_type2 = ((MaterialRippleLayout) dialogView.findViewById(R.id.btn_type2)).getChildView();
        int submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));
        btn_type2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ridenowclick = false;
                outstanding_dialog.dismiss();
            }
        });

        builder.setView(dialogView);
        outstanding_dialog = builder.create();
        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(outstanding_dialog);
        }
        outstanding_dialog.setCancelable(false);
        outstanding_dialog.show();
    }

    public void callOutStandingPayAmout(boolean isReqNow) {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "ChargePassengerOutstandingAmount");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.app_type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setCancelAble(false);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {
                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {
                    String message = generalFunc.getJsonValue(Utils.message_str, responseString);
                    generalFunc.storeData(Utils.USER_PROFILE_JSON, message);
                    userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
                    mainAct.userProfileJson = userProfileJson;
                    final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                    generateAlert.setCancelable(false);
                    generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                        @Override
                        public void handleBtnClick(int btn_id) {

                            if (isReqNow) {
                                isOutStandingDailogShow = true;
                                ride_now_btn.performClick();
                            } else {
                                isOutStandingDailogShow = true;
                                img_ridelater.performClick();
                            }
                        }
                    });
                    generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str_one, responseString)));
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                    generateAlert.showAlertBox();
                }
            } else {
                generalFunc.showGeneralMessage("",
                        generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
            }
        });
        exeWebServer.execute();

    }

    boolean isRental = false;
    int lstSelectpos = 0;

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


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard(getActContext());
            if (i == R.id.minFareArea) {
                openFareEstimateDialog();
            } else if (i == ride_now_btn.getId()) {

                if (mProgressBar.getVisibility() == View.VISIBLE) {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Route not found", "LBL_DEST_ROUTE_NOT_FOUND"));
                    return;
                }
                if ((mainAct.currentLoadedDriverList != null && mainAct.currentLoadedDriverList.size() < 1) || mainAct.currentLoadedDriverList == null || (cabTypeList != null && cabTypeList.size() < 1) || cabTypeList == null) {

                    buildNoCabMessage(generalFunc.retrieveLangLBl("", "LBL_NO_CARS_AVAIL_IN_TYPE"),
                            generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                    return;
                }

                if (isRouteFail) {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Route not found", "LBL_DEST_ROUTE_NOT_FOUND"));
                    return;
                }

                // if (!ridenowclick) {
                if (!isOutStandingDailogShow) {

                    if (generalFunc.getJsonValue("fOutStandingAmount", userProfileJson) != null &&
                            GeneralFunctions.parseDoubleValue(0, generalFunc.getJsonValue("fOutStandingAmount", userProfileJson)) > 0) {
                        outstandingDialog(true);
                        //  ridenowclick = true;
                        return;

                    }
                }
                //  }

                isOutStandingDailogShow = false;


                // if (!ridenowclick) {

                mainAct.setCabReqType(Utils.CabReqType_Now);


//                    if (mainAct.getDestinationStatus()) {
//                        String destLocAdd = mainAct != null ? (mainAct.getDestAddress().equals(
//                                generalFunc.retrieveLangLBl("", "LBL_SELECTING_LOCATION_TXT")) ? "" : mainAct.getDestAddress()) : "";
//                        if (destLocAdd.equals("")) {
//                            return;
//                        }
//                    }

                if (isCardValidated == false && generalFunc.getJsonValue("APP_PAYMENT_MODE", userProfileJson).equalsIgnoreCase("Card")) {
                    isCardnowselcted = true;
                    isCardlaterselcted = false;
                    checkCardConfig();
                    return;
                }


                if (mainAct.isDeliver(mainAct.getCurrentCabGeneralType())) {
                    if (mainAct.getDestinationStatus() == false) {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Please add your destination location " +
                                "to deliver your package.", "LBL_ADD_DEST_MSG_DELIVER_ITEM"));
                        return;
                    }
                    Checkpickupdropoffrestriction();
                    // mainAct.setDeliverySchedule();
                    return;
                }

                // ridenowclick = true;


                if (!mainAct.getCabReqType().equals(Utils.CabReqType_Later)) {
                    //  mainAct.requestPickUp();


                    if (cabTypeList.get(selpos).get("eRental") != null && !cabTypeList.get(selpos).get("eRental").equalsIgnoreCase("") &&
                            cabTypeList.get(selpos).get("eRental").equalsIgnoreCase("Yes")) {

                        Bundle bn = new Bundle();
                        bn.putString("address", mainAct.pickUpLocationAddress);
                        bn.putString("vVehicleType", cabTypeList.get(selpos).get("vRentalVehicleTypeName"));
                        bn.putString("iVehicleTypeId", cabTypeList.get(selpos).get("iVehicleTypeId"));
                        bn.putString("vLogo", cabTypeList.get(selpos).get("vLogo1"));
                        bn.putString("eta", etaTxt.getText().toString());
                        bn.putString("eMoto", mainAct.eShowOnlyMoto);
                        bn.putString("PromoCode", appliedPromoCode);


                        new StartActProcess(getActContext()).startActForResult(
                                RentalDetailsActivity.class, bn, RENTAL_REQ_CODE);
                        return;

                    }

                    // ride_now_btn.setEnabled(false);
                    //  ride_now_btn.setClickable(false);


                    mainAct.continuePickUpProcess();
                } else {
                    //   ride_now_btn.setEnabled(false);
                    // ride_now_btn.setClickable(false);


                    mainAct.setRideSchedule();
                }

//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            ridenowclick = false;
//                        }
//                    }, 500);
                // }
            } else if (i == img_ridelater.getId()) {
                try {

                    if (mProgressBar.getVisibility() == View.VISIBLE) {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Route not found", "LBL_DEST_ROUTE_NOT_FOUND"));
                        return;
                    }


                    if (!cabTypeList.get(selpos).get("eRental").equalsIgnoreCase("Yes")) {
                        if (mainAct.destAddress == null || mainAct.destAddress.equalsIgnoreCase("")) {
                            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Destination is required to create scheduled booking.", "LBL_DEST_REQ_FOR_LATER"));

                            return;
                        }
                    }

                    if (isRouteFail) {
                        generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Route not found", "LBL_DEST_ROUTE_NOT_FOUND"));
                        return;
                    }
                    if (!isOutStandingDailogShow) {
                        if (generalFunc.getJsonValue("fOutStandingAmount", userProfileJson) != null &&
                                GeneralFunctions.parseIntegerValue(0, generalFunc.getJsonValue("fOutStandingAmount", userProfileJson)) > 0) {
                            outstandingDialog(false);
                            return;

                        }
                    }


//                if (!ridelaterclick) {
//                    ridelaterclick = true;
                    if (cabTypeList.size() > 0) {
                        if (isCardValidated == false && generalFunc.getJsonValue("APP_PAYMENT_MODE", userProfileJson).equalsIgnoreCase("Card")) {
                            isCardlaterselcted = true;
                            isCardnowselcted = false;
                            checkCardConfig();
                            return;
                        }
                        //  ride_now_btn.setEnabled(false);
                        // ride_now_btn.setTextColor(Color.parseColor("#BABABA"));
                        //  ride_now_btn.setClickable(false);
                        mainAct.chooseDateTime();
                        //      }
//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            ridelaterclick = false;
//                        }
//                    }, 200);
                    }
                } catch (Exception e) {

                }
            } else if (i == R.id.paymentArea) {

                if (payTypeSelectArea.getVisibility() == View.VISIBLE) {
                    hidePayTypeSelectionArea();
                } else {
                    if (generalFunc.getJsonValue("APP_PAYMENT_MODE", userProfileJson).equalsIgnoreCase("Cash-Card")) {

                        if (rentalTypeList.size() > 0 && !mainAct.iscubejekRental) {
                            Runnable r = new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (rentPkgImage.getVisibility() == View.VISIBLE) {
                                            mainAct.setPanelHeight(335);
                                        } else {
                                            mainAct.setPanelHeight(280);

                                        }
                                    } catch (Exception e2) {
                                        new Handler().postDelayed(this, 20);
                                    }
                                }
                            };
                            new Handler().postDelayed(r, 20);
                        } else {
                            mainAct.setPanelHeight(283);
                        }
                        payTypeSelectArea.setVisibility(View.VISIBLE);
                        cashcardarea.setVisibility(View.GONE);
                    } else {


                        if (rentalTypeList.size() > 0 && !mainAct.iscubejekRental) {
                            Runnable r = new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        mainAct.setPanelHeight(335 - 55);
                                    } catch (Exception e2) {
                                        new Handler().postDelayed(this, 20);
                                    }
                                }
                            };
                            new Handler().postDelayed(r, 20);
                        } else {
                            mainAct.setPanelHeight(283 - 48);

                        }

                    }
                }

            } else if (i == R.id.promoArea) {
                showPromoBox();
            } else if (i == R.id.cardarea) {
                hidePayTypeSelectionArea();
                setCashSelection();
                checkCardConfig();
                //   }

            } else if (i == R.id.casharea) {
                hidePayTypeSelectionArea();
                setCashSelection();
            } else if (i == R.id.rentalBackImage) {

                mainAct.isRental = false;

                if (mainAct.loadAvailCabs != null) {
                    mainAct.loadAvailCabs.checkAvailableCabs();
                }
                selpos = 0;
                iRentalPackageId = "";
                lstSelectpos = 0;
                cabTypeList = (ArrayList<HashMap<String, String>>) tempCabTypeList.clone();
                tempCabTypeList.clear();
                tempCabTypeList = (ArrayList<HashMap<String, String>>) cabTypeList.clone();
                isRental = false;
                adapter.setSelectedVehicleTypeId(cabTypeList.get(0).get("iVehicleTypeId"));
                mainAct.selectedCabTypeId = cabTypeList.get(0).get("iVehicleTypeId");
                adapter.setRentalItem(cabTypeList);
                adapter.notifyDataSetChanged();
                rentalBackImage.setVisibility(View.GONE);
                rentalPkgDesc.setVisibility(View.GONE);


                rentalPkg.setVisibility(View.VISIBLE);
                rentalarea.setVisibility(View.VISIBLE);
                rentPkgImage.setVisibility(View.VISIBLE);
                rentBackPkgImage.setVisibility(View.VISIBLE);
                android.view.animation.Animation bottomUp = AnimationUtils.loadAnimation(getActContext(),
                        R.anim.slide_up_anim);
                carTypeRecyclerView.startAnimation(bottomUp);

                if (generalFunc.getJsonValue("RIDE_LATER_BOOKING_ENABLED", userProfileJson).equalsIgnoreCase("Yes")) {
                    imageLaterarea.setVisibility(View.VISIBLE);
                } else {
                    imageLaterarea.setVisibility(View.GONE);
                }

                if (!mainAct.iscubejekRental) {
                    Runnable r = new Runnable() {

                        @Override
                        public void run() {
                            try {
                                mainAct.setPanelHeight(280);

                               /* RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) (mainAct.userLocBtnImgView).getLayoutParams();
                                params.bottomMargin = Utils.dipToPixels(getActContext(), 300);*/
                            } catch (Exception e2) {
                                new Handler().postDelayed(this, 20);
                            }
                        }
                    };
                    new Handler().postDelayed(r, 20);
                }

            } else if (i == R.id.rentalPkg) {


                mainAct.isRental = true;

                if (mainAct.loadAvailCabs != null) {
                    mainAct.loadAvailCabs.checkAvailableCabs();
                }
                selpos = 0;
                iRentalPackageId = "";
                lstSelectpos = 1;
                tempCabTypeList.clear();
                tempCabTypeList = (ArrayList<HashMap<String, String>>) cabTypeList.clone();
                cabTypeList.clear();
                cabTypeList = (ArrayList<HashMap<String, String>>) rentalTypeList.clone();
                adapter.setRentalItem(cabTypeList);
                isRental = true;
                adapter.setSelectedVehicleTypeId(cabTypeList.get(0).get("iVehicleTypeId"));
                mainAct.selectedCabTypeId = cabTypeList.get(0).get("iVehicleTypeId");
                adapter.notifyDataSetChanged();
                rentalPkgDesc.setVisibility(View.VISIBLE);

                rentalBackImage.setVisibility(View.VISIBLE);
                rentalPkg.setVisibility(View.GONE);
                rentalarea.setVisibility(View.GONE);
                rentPkgImage.setVisibility(View.GONE);
                rentBackPkgImage.setVisibility(View.GONE);

                android.view.animation.Animation bottomUp = AnimationUtils.loadAnimation(getActContext(),
                        R.anim.slide_up_anim);
                carTypeRecyclerView.startAnimation(bottomUp);

                imageLaterarea.setVisibility(View.GONE);

                Runnable r = new Runnable() {

                    @Override
                    public void run() {
                        try {
                            mainAct.setPanelHeight(270);

                        } catch (Exception e2) {
                            new Handler().postDelayed(this, 20);
                        }
                    }
                };
                new Handler().postDelayed(r, 20);
            } else if (i == R.id.rentPkgImage) {
                rentalPkg.performClick();

            }
        }
    }


    public boolean handleRnetalView(String selectedTime) {
        if (cabTypeList.get(selpos).get("eRental") != null && !cabTypeList.get(selpos).get("eRental").equalsIgnoreCase("") &&
                cabTypeList.get(selpos).get("eRental").equalsIgnoreCase("Yes")) {

            Bundle bn = new Bundle();
            bn.putString("address", mainAct.pickUpLocationAddress);
            bn.putString("vVehicleType", cabTypeList.get(selpos).get("vRentalVehicleTypeName"));
            bn.putString("iVehicleTypeId", cabTypeList.get(selpos).get("iVehicleTypeId"));
            bn.putString("vLogo", cabTypeList.get(selpos).get("vLogo1"));
            bn.putString("eta", etaTxt.getText().toString());
            bn.putString("selectedTime", selectedTime);
            bn.putString("eMoto", mainAct.eShowOnlyMoto);
            bn.putString("PromoCode", appliedPromoCode);
            new StartActProcess(getActContext()).startActForResult(
                    RentalDetailsActivity.class, bn, RENTAL_REQ_CODE);
            return true;


        }
        return false;
    }

}


