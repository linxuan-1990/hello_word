package com.caliway.user;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.datepicker.files.SlideDateTimeListener;
import com.datepicker.files.SlideDateTimePicker;
import com.dialogs.RequestNearestCab;
import com.fragments.CabSelectionFragment;
import com.fragments.DriverAssignedHeaderFragment;
import com.fragments.DriverDetailFragment;
import com.fragments.MainHeaderFragment;
import com.fragments.PickUpLocSelectedFragment;
import com.fragments.RequestPickUpFragment;
import com.general.files.AddDrawer;
import com.general.files.ConfigPubNub;
import com.general.files.CreateAnimation;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.GetLocationUpdates;
import com.general.files.HashMapComparator;
import com.general.files.InternetConnection;
import com.general.files.LoadAvailableCab;
import com.general.files.LocalNotification;
import com.general.files.MapAnimator;
import com.general.files.MyApp;
import com.general.files.StartActProcess;
import com.general.files.UpdateFrequentTask;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.SphericalUtil;
import com.pubnub.api.enums.PNStatusCategory;
import com.utils.AnimateMarker;
import com.utils.AppFunctions;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.SelectableRoundedImageView;
import com.view.anim.loader.AVLoadingIndicatorView;
import com.view.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GetLocationUpdates.LocationUpdates {

    public GeneralFunctions generalFunc;
    public static int RENTAL_REQ_CODE = 1234;
    public String userProfileJson = "";
    public String currentGeoCodeObject = "";
    public SlidingUpPanelLayout sliding_layout;
    public ImageView userLocBtnImgView;
    public ImageView userTripBtnImgView;
    public Location userLocation;
    public ArrayList<HashMap<String, String>> currentLoadedDriverList;
    public ImageView emeTapImgView;

    public AddDrawer addDrawer;
    public CabSelectionFragment cabSelectionFrag;
    public LoadAvailableCab loadAvailCabs;
    public Location pickUpLocation;
    public String selectedCabTypeId = "";
    public boolean isDestinationAdded = false;
    public String destLocLatitude = "";
    public String destLocLongitude = "";
    public String destAddress = "";
    public boolean isCashSelected = true;
    public String pickUpLocationAddress = "";
    public String app_type = "Ride";
    public DrawerLayout mDrawerLayout;
    public AVLoadingIndicatorView loaderView;
    public ImageView pinImgView;
    public ArrayList<HashMap<String, String>> cabTypesArrList = new ArrayList<>();
    public boolean iswallet = false;
    public boolean isUserLocbtnclik = false;
    public String tempPickupGeoCode = "";
    public String tempDestGeoCode = "";
    public boolean isUfx = false;
    public String uberXAddress = "";
    public double uberXlat = 0.0;
    public double uberXlong = 0.0;
    public boolean ishandicap = false;
    public boolean isfemale = false;
    public String timeval = "";
    public DriverAssignedHeaderFragment driverAssignedHeaderFrag;
    public RequestNearestCab requestNearestCab;
    public boolean isDestinationMode = false;
    public LinearLayout ridelaterHandleView;
    public boolean isUfxRideLater = false;
    public String bookingtype = "";
    public String selectedprovidername = "";
    public String vCurrencySymbol = "";
    public String UfxAmount = "";
    public boolean noCabAvail = false;
    public Location destLocation;
    public boolean isDriverAssigned = false;
    public GenerateAlertBox noCabAvailAlertBox;
    public JSONObject obj_userProfile;
    public String SelectDate = "";
    public String sdate = "";
    public String Stime = "";
    public boolean isFirstTime = true;
    public String ACCEPT_CASH_TRIPS = "";
    MTextView titleTxt;
    public SupportMapFragment map;
    GetLocationUpdates getLastLocation;
    GoogleMap gMap;
    boolean isFirstLocation = true;
    RelativeLayout dragView;
    RelativeLayout mainArea;
    View otherArea;
    FrameLayout mainContent;
    RelativeLayout uberXDriverListArea;
    public MainHeaderFragment mainHeaderFrag;
    RequestPickUpFragment reqPickUpFrag;
    DriverDetailFragment driverDetailFrag;
    ArrayList<HashMap<String, String>> cabTypeList;
    ArrayList<HashMap<String, String>> uberXDriverList = new ArrayList<>();
    public HashMap<String, String> driverAssignedData;
    public String assignedDriverId = "";
    public String assignedTripId = "";
    String DRIVER_REQUEST_METHOD = "All";
    MTextView uberXNoDriverTxt;
    SelectableRoundedImageView driverImgView;
    UpdateFrequentTask allCabRequestTask;
    SendNotificationsToDriverByDist sendNotificationToDriverByDist;
    String selectedDateTime = "";
    String selectedDateTimeZone = "";
    public String cabRquestType = Utils.CabReqType_Now; // Later OR Now
    View rideArea;
    View deliverArea;

    Intent deliveryData;
    String eTripType = "";
    android.support.v7.app.AlertDialog alertDialog_surgeConfirm;
    String required_str = "";

    RecyclerView uberXOnlineDriversRecyclerView;
    LinearLayout driver_detail_bottomView;
    String markerId = "";
    boolean isMarkerClickable = true;
    String currentUberXChoiceType = Utils.Cab_UberX_Type_List;
    String vUberXCategoryName = "";
    Handler ufxFreqTask = null;
    String tripId = "";
    String RideDeliveryType = "";
    SelectableRoundedImageView deliverImgView, deliverImgViewsel, rideImgView, rideImgViewsel, otherImageView, otherImageViewsel;
    PickUpLocSelectedFragment pickUpLocSelectedFrag;
    double tollamount = 0.0;
    String tollcurrancy = "";
    boolean isrideschedule = false;
    boolean isreqnow = false;
    ImageView prefBtnImageView;
    android.support.v7.app.AlertDialog pref_dialog;
    android.support.v7.app.AlertDialog tolltax_dialog;

    boolean isTollCostdilaogshow = false;
    boolean istollIgnore = false;
    boolean isnotification = false;
    boolean isdelivernow = false;
    boolean isdeliverlater = false;
    LinearLayout ridelaterView;
    MTextView rideLaterTxt;
    MTextView btn_type_ridelater;
    public boolean isTripStarted = false;
    boolean isTripEnded = false;
    boolean isDriverArrived = false;
    InternetConnection intCheck;
    boolean isfirstsearch = true;
    boolean isufxpayment = false;
    String appliedPromoCode = "";
    String userComment = "";
    boolean schedulrefresh = false;
    String iCabBookingId = "";
    boolean isRebooking = false;
    String type = "";
    //Noti
    boolean isufxbackview = false;
    String payableAmount = "";
    private String SelectedDriverId = "";
    private String tripStatus = "";
    private String currentTripId = "";
    private ActionBarDrawerToggle mDrawerToggle;

    public boolean isOutStanding = false;

    public RelativeLayout rootRelView;
    public static String PACKAGE_TYPE_ID_KEY = "PACKAGE_TYPE_ID";

    public boolean isUserTripClick = false;
    boolean isTripActive = false;

    public boolean isFirstZoomlevel = true;

    LinearLayout rduTollbar;
    ImageView backImgView;
    public boolean isMenuImageShow = true;

    public boolean isRental = false;
    public boolean iscubejekRental = false;
    public String eShowOnlyMoto = "";

    public double pickUp_tmpLatitude = 0.0;
    public double pickUp_tmpLongitude = 0.0;
    public String pickUp_tmpAddress = "";

    GenerateAlertBox reqSentErrorDialog = null;
    String eWalletDebitAllow = "No";
    boolean isWalletPopupFirst = false;
    MTextView filterTxtView;
    public LinearLayout llFilter;


    public String selectedSortValue = "";
    public String selectedSort = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        generalFunc = new GeneralFunctions(getActContext());
        cabSelectionFrag = null;

        rootRelView = (RelativeLayout) findViewById(R.id.rootRelView);

        isTripActive = getIntent().getBooleanExtra("isTripActive", false);
        rduTollbar = (LinearLayout) findViewById(R.id.rduTollbar);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        prefBtnImageView = (ImageView) findViewById(R.id.prefBtnImageView);
        backImgView.setOnClickListener(new setOnClickList());

        filterTxtView = (MTextView) findViewById(R.id.filterTxtView);
        llFilter = (LinearLayout) findViewById(R.id.llFilter);
        filterTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_FEATURED_TXT"));
        filterTxtView.setOnClickListener(new setOnClickList());

        selectedSortValue = generalFunc.retrieveLangLBl("", "LBL_FEATURED_TXT");


        if (getIntent().getStringExtra("iCabBookingId") != null) {
            iCabBookingId = getIntent().getStringExtra("iCabBookingId");
        }

        if (getIntent().getStringExtra("type") != null) {
            type = getIntent().getStringExtra("type");
            bookingtype = getIntent().getStringExtra("type");
        }

        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        obj_userProfile = generalFunc.getJsonObject(userProfileJson);

        isRebooking = getIntent().getBooleanExtra("isRebooking", false);
        intCheck = new InternetConnection(getActContext());
        isufxpayment = getIntent().getBooleanExtra("isufxpayment", false);

        isUfx = getIntent().getBooleanExtra("isufx", false);

        isnotification = getIntent().getBooleanExtra("isnotification", false);

        app_type = generalFunc.getJsonValueStr("APP_TYPE", obj_userProfile);

        if (generalFunc.getJsonValueStr("APP_TYPE", obj_userProfile).equals(Utils.CabGeneralTypeRide_Delivery_UberX)) {
            RideDeliveryType = Utils.CabGeneralType_Ride;
        }

        if (getIntent().getStringExtra("selType") != null && !isTripActive) {

            if (getIntent().getBooleanExtra("emoto", false)) {
                eShowOnlyMoto = "Yes";
            }
            RideDeliveryType = getIntent().getStringExtra("selType");
            rduTollbar.setVisibility(View.GONE);
            //bug_002 start
            if (getIntent().getStringExtra("selType").equalsIgnoreCase(Utils.CabGeneralType_Ride)) {
                titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_HEADER_RDU_RIDE"));

                if (getIntent().getBooleanExtra("emoto", false)) {
                    titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_HEADER_RDU_MOTO_RIDE"));
                }
                rduTollbar.setVisibility(View.VISIBLE);
            } else if (getIntent().getStringExtra("selType").equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
                titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SERVICE_PROVIDER_TXT"));


            } else if (getIntent().getStringExtra("selType").equalsIgnoreCase("rental")) {
                titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_HEADER_RDU_RENTAL"));
                if (getIntent().getBooleanExtra("emoto", false)) {
                    titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_HEADER_RDU_MOTO_RENTAL"));
                }
                RideDeliveryType = Utils.CabGeneralType_Ride;
                iscubejekRental = true;
                rduTollbar.setVisibility(View.VISIBLE);
            } else {
                titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_HEADER_RDU_DELIVERY"));
                prefBtnImageView.setVisibility(View.GONE);
                rduTollbar.setVisibility(View.VISIBLE);

                if (getIntent().getBooleanExtra("emoto", false)) {
                    titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_HEADER_RDU_MOTO_DELIVERY"));
                }
            }
            isMenuImageShow = false;
        }

        if (getIntent().hasExtra("tripId")) {
            tripId = getIntent().getStringExtra("tripId");
        }
        String TripDetails = generalFunc.getJsonValueStr("TripDetails", obj_userProfile);

        if (TripDetails != null && !TripDetails.equals("")) {
            tripId = generalFunc.getJsonValue("iTripId", TripDetails);
        }

        mainContent = (FrameLayout) findViewById(R.id.mainContent);
        userLocBtnImgView = (ImageView) findViewById(R.id.userLocBtnImgView);
        userTripBtnImgView = (ImageView) findViewById(R.id.userTripBtnImgView);

        prefrenceButtonEnable();

        if (!isUfx) {
            mainContent.setVisibility(View.VISIBLE);
            userLocBtnImgView.setVisibility(View.VISIBLE);
        } else {
            prefBtnImageView.setVisibility(View.GONE);
        }

        addDrawer = new AddDrawer(getActContext(), userProfileJson, false);

        if (app_type.equalsIgnoreCase("UberX")) {
            addDrawer.configDrawer(true);
            selectedCabTypeId = getIntent().getStringExtra("SelectedVehicleTypeId");
            vUberXCategoryName = getIntent().getStringExtra("vCategoryName");
        } else {
            addDrawer.configDrawer(false);
        }


        if (app_type.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX)) {
            if (isUfx) {
                selectedCabTypeId = getIntent().getStringExtra("SelectedVehicleTypeId");
                vUberXCategoryName = getIntent().getStringExtra("vCategoryName");

                setMainHeaderView(true);
            }
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (rduTollbar.getVisibility() == View.VISIBLE) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                1, 2) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // getActionBar().setTitle("Closed");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // getActionBar().setTitle("Opened");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);


        ridelaterView = (LinearLayout) findViewById(R.id.ridelaterView);

        uberXNoDriverTxt = (MTextView) findViewById(R.id.uberXNoDriverTxt);
        deliverImgView = (SelectableRoundedImageView) findViewById(R.id.deliverImgView);
        deliverImgViewsel = (SelectableRoundedImageView) findViewById(R.id.deliverImgViewsel);
        rideImgView = (SelectableRoundedImageView) findViewById(R.id.rideImgView);
        rideImgViewsel = (SelectableRoundedImageView) findViewById(R.id.rideImgViewsel);
        otherImageView = (SelectableRoundedImageView) findViewById(R.id.otherImageView);
        otherImageViewsel = (SelectableRoundedImageView) findViewById(R.id.otherImageViewsel);

        rideLaterTxt = (MTextView) findViewById(R.id.rideLaterTxt);

        ridelaterHandleView = (LinearLayout) findViewById(R.id.ridelaterHandleView);

        btn_type_ridelater = (MTextView) findViewById(R.id.btn_type_ridelater);

        if (type.equals(Utils.CabReqType_Now)) {
            btn_type_ridelater.setText(generalFunc.retrieveLangLBl("", "LBL_BOOK_LATER"));
        } else {
            btn_type_ridelater.setText(generalFunc.retrieveLangLBl("", "LBL_CHANGE"));
        }


        btn_type_ridelater.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("latitude", getIntent().getStringExtra("latitude"));
            bundle.putString("longitude", getIntent().getStringExtra("longitude"));
            bundle.putString("address", getIntent().getStringExtra("address"));
            bundle.putString("iUserAddressId", getIntent().getStringExtra("iUserAddressId"));
            bundle.putString("SelectedVehicleTypeId", getIntent().getStringExtra("SelectedVehicleTypeId"));
            bundle.putString("SelectvVehicleType", getIntent().getStringExtra("SelectvVehicleType"));
            bundle.putString("SelectvVehiclePrice", getIntent().getStringExtra("SelectvVehiclePrice"));

            bundle.putBoolean("isMain", true);
            new StartActProcess(getActContext()).startActForResult(ScheduleDateSelectActivity.class, bundle, Utils.SCHEDULE_REQUEST_CODE);

            schedulrefresh = true;
        });

        new CreateRoundedView(getActContext().getResources().getColor(R.color.white), Utils.dipToPixels(getActContext(), 35), 2,
                getActContext().getResources().getColor(R.color.white), deliverImgViewsel);

        deliverImgViewsel.setColorFilter(getActContext().getResources().getColor(R.color.black));

        new CreateRoundedView(getActContext().getResources().getColor(R.color.white), Utils.dipToPixels(getActContext(), 30), 2,
                getActContext().getResources().getColor(R.color.white), deliverImgView);

        deliverImgView.setColorFilter(getActContext().getResources().getColor(R.color.black));

        new CreateRoundedView(getActContext().getResources().getColor(R.color.white), Utils.dipToPixels(getActContext(), 35), 2,
                getActContext().getResources().getColor(R.color.white), rideImgViewsel);

        new CreateRoundedView(getActContext().getResources().getColor(R.color.white), Utils.dipToPixels(getActContext(), 30), 2,
                getActContext().getResources().getColor(R.color.white), rideImgView);

        new CreateRoundedView(getActContext().getResources().getColor(R.color.white), Utils.dipToPixels(getActContext(), 35), 2,
                getActContext().getResources().getColor(R.color.white), otherImageViewsel);

        new CreateRoundedView(getActContext().getResources().getColor(R.color.white), Utils.dipToPixels(getActContext(), 30), 2,
                getActContext().getResources().getColor(R.color.white), otherImageView);

        loaderView = (AVLoadingIndicatorView) findViewById(R.id.loaderView);
        uberXOnlineDriversRecyclerView = (RecyclerView) findViewById(R.id.uberXOnlineDriversRecyclerView);

        userLocBtnImgView = (ImageView) findViewById(R.id.userLocBtnImgView);
        map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapV2);
        sliding_layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        dragView = (RelativeLayout) findViewById(R.id.dragView);
        mainArea = (RelativeLayout) findViewById(R.id.mainArea);
        otherArea = findViewById(R.id.otherArea);
        mainContent = (FrameLayout) findViewById(R.id.mainContent);
        driver_detail_bottomView = (LinearLayout) findViewById(R.id.driver_detail_bottomView);
        pinImgView = (ImageView) findViewById(R.id.pinImgView);

        uberXDriverListArea = (RelativeLayout) findViewById(R.id.uberXDriverListArea);
        emeTapImgView = (ImageView) findViewById(R.id.emeTapImgView);
        rideArea = findViewById(R.id.rideArea);
        deliverArea = findViewById(R.id.deliverArea);

        prefBtnImageView.setOnClickListener(new setOnClickList());

        map.getMapAsync(MainActivity.this);

        setGeneralData();
        setLabels();

        if (generalFunc.isRTLmode()) {
            ((ImageView) findViewById(R.id.deliverImg)).setRotation(-180);
            ((ImageView) findViewById(R.id.rideImg)).setRotation(-180);
            ((ImageView) findViewById(R.id.rideImg)).setScaleY(-1);
            ((ImageView) findViewById(R.id.deliverImg)).setScaleY(-1);
        }


        new CreateAnimation(dragView, getActContext(), R.anim.design_bottom_sheet_slide_in, 100, true).startAnimation();


        userTripBtnImgView.setOnClickListener(new setOnClickList());
        userLocBtnImgView.setOnClickListener(new setOnClickList());
        emeTapImgView.setOnClickListener(new setOnClickList());
        rideArea.setOnClickListener(new setOnClickList());
        deliverArea.setOnClickListener(new setOnClickList());
        otherArea.setOnClickListener(new setOnClickList());

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            String restratValue_str = savedInstanceState.getString("RESTART_STATE");

            if (restratValue_str != null && !restratValue_str.equals("") && restratValue_str.trim().equals("true")) {
                releaseScheduleNotificationTask();
                generalFunc.restartApp();
            }
        }

        generalFunc.deleteTripStatusMessages();


        String eEmailVerified = generalFunc.getJsonValueStr("eEmailVerified", obj_userProfile);
        String ePhoneVerified = generalFunc.getJsonValueStr("ePhoneVerified", obj_userProfile);
        String RIDER_EMAIL_VERIFICATION = generalFunc.getJsonValueStr("RIDER_EMAIL_VERIFICATION", obj_userProfile);
        String RIDER_PHONE_VERIFICATION = generalFunc.getJsonValueStr("ePhoneVerified", obj_userProfile);

        if ((!eEmailVerified.equalsIgnoreCase("YES") && RIDER_EMAIL_VERIFICATION.equalsIgnoreCase("Yes")) ||
                (!ePhoneVerified.equalsIgnoreCase("YES") && RIDER_PHONE_VERIFICATION.equalsIgnoreCase("Yes"))) {

            Bundle bn = new Bundle();
            if (!eEmailVerified.equalsIgnoreCase("YES") &&
                    !ePhoneVerified.equalsIgnoreCase("YES")) {
                bn.putString("msg", "DO_EMAIL_PHONE_VERIFY");
            } else if (!eEmailVerified.equalsIgnoreCase("YES")) {
                bn.putString("msg", "DO_EMAIL_VERIFY");
            } else if (!ePhoneVerified.equalsIgnoreCase("YES")) {
                bn.putString("msg", "DO_PHONE_VERIFY");
            }

            showMessageWithAction(mainArea, generalFunc.retrieveLangLBl("", "LBL_ACCOUNT_VERIFY_ALERT_RIDER_TXT"), bn);
        }
    }

    public void addcabselectionfragment() {
        setRiderDefaultView();
       /* //handle map height
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        ViewGroup.LayoutParams params = map.getView().getLayoutParams();
        params.height = height - Utils.dpToPx(getActContext(), 280);
        Utils.printLog("height", "::" + params.height);
        map.getView().setLayoutParams(params);
*/

        resetMapView();
        gMap.setPadding(0, 0, 0, Utils.dipToPixels(getActContext(), 280));

        map.getView().requestLayout();
    }

    public void setSelectedDriverId(String driver_id) {
        SelectedDriverId = driver_id;
    }

    public void setLabels() {
        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT");
        ((MTextView) findViewById(R.id.rideTxt)).setText(generalFunc.retrieveLangLBl("Ride", "LBL_RIDE"));
        ((MTextView) findViewById(R.id.selrideTxt)).setText(generalFunc.retrieveLangLBl("Ride", "LBL_RIDE"));
        ((MTextView) findViewById(R.id.deliverTxt)).setText(generalFunc.retrieveLangLBl("Deliver", "LBL_DELIVER"));
        ((MTextView) findViewById(R.id.otherTxt)).setText(generalFunc.retrieveLangLBl("Other", "LBL_SERVICES"));

        if (type.equals(Utils.CabReqType_Now)) {
            if (generalFunc.getJsonValue("RIDE_LATER_BOOKING_ENABLED", userProfileJson).equalsIgnoreCase("Yes")) {
                rideLaterTxt.setText(generalFunc.retrieveLangLBl("", "LBL_NO_PROVIDERS_AVAIL_NOW"));
                btn_type_ridelater.setVisibility(View.VISIBLE);
            } else {

                rideLaterTxt.setText(generalFunc.retrieveLangLBl("", "LBL_NO_PROVIDERS_AVAIL"));
                btn_type_ridelater.setVisibility(View.GONE);
            }
        } else {
            rideLaterTxt.setText(generalFunc.retrieveLangLBl("", "LBL_NO_PROVIDERS_AVAIL_LATER"));
            btn_type_ridelater.setVisibility(View.GONE);
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        try {
            outState.putString("RESTART_STATE", "true");
            super.onSaveInstanceState(outState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setGeneralData() {
        generalFunc.storeData(Utils.MOBILE_VERIFICATION_ENABLE_KEY, generalFunc.getJsonValueStr("MOBILE_VERIFICATION_ENABLE", obj_userProfile));
        String DRIVER_REQUEST_METHOD = generalFunc.getJsonValueStr("DRIVER_REQUEST_METHOD", obj_userProfile);

        this.DRIVER_REQUEST_METHOD = DRIVER_REQUEST_METHOD.equals("") ? "All" : DRIVER_REQUEST_METHOD;

        generalFunc.storeData(Utils.REFERRAL_SCHEME_ENABLE, generalFunc.getJsonValueStr("REFERRAL_SCHEME_ENABLE", obj_userProfile));
        generalFunc.storeData(Utils.WALLET_ENABLE, generalFunc.getJsonValueStr("WALLET_ENABLE", obj_userProfile));
        generalFunc.storeData(Utils.SMS_BODY_KEY, generalFunc.getJsonValueStr(Utils.SMS_BODY_KEY, obj_userProfile));

    }

    public MainHeaderFragment getMainHeaderFrag() {
        return mainHeaderFrag;
    }

    boolean isFirst = true;

    @Override
    public void onMapReady(GoogleMap googleMap) {

        (findViewById(R.id.LoadingMapProgressBar)).setVisibility(View.GONE);


        if (googleMap == null) {
            return;
        }

        this.gMap = googleMap;

        if (isUfx) {
            if (getIntent().getStringExtra("SelectDate") != null) {
                SelectDate = getIntent().getStringExtra("SelectDate");
            }
            if (pickUpLocation == null) {
                Location temploc = new Location("PickupLoc");
                if (getIntent().getStringExtra("latitude") != null) {
                    temploc.setLatitude(generalFunc.parseDoubleValue(0.0, getIntent().getStringExtra("latitude")));
                    temploc.setLongitude(generalFunc.parseDoubleValue(0.0, getIntent().getStringExtra("longitude")));
                    onLocationUpdate(temploc);
//                    pickUpLocation = temploc;
//                    pickUpLocationAddress = getIntent().getStringExtra("address");
                }
            }
        }


        if (generalFunc.checkLocationPermission(true) == true) {
            getMap().setMyLocationEnabled(true);
            getMap().getUiSettings().setTiltGesturesEnabled(false);
            getMap().getUiSettings().setCompassEnabled(false);
            getMap().getUiSettings().setMyLocationButtonEnabled(false);

            getMap().setOnMarkerClickListener(marker -> {
                marker.hideInfoWindow();

                if (isUfx) {
                    if (isMarkerClickable == true) {
                        markerId = marker.getId();
                    }
                } else {
                    try {

                        getMap().getUiSettings().setMapToolbarEnabled(false);
                        if (marker.getTag().equals("1")) {
                            if (mainHeaderFrag != null) {
                                mainHeaderFrag.pickupLocArea1.performClick();
                            }

                        } else if (marker.getTag().equals("2")) {
                            if (mainHeaderFrag != null) {
                                mainHeaderFrag.destarea.performClick();
                            }
                        }
                    } catch (Exception e) {

                    }

                }
                return true;

            });


            getMap().setOnMapClickListener(this);


        }

        if (isUfx) {
            if (isFirst) {
                isFirst = false;
                initializeLoadCab();
            }
        }

        String vTripStatus = generalFunc.getJsonValueStr("vTripStatus", obj_userProfile);

        if (vTripStatus != null && (vTripStatus.equals("Active") || vTripStatus.equals("On Going Trip"))) {
            getMap().setMyLocationEnabled(false);
            String tripDetailJson = generalFunc.getJsonValueStr("TripDetails", obj_userProfile);

            if (tripDetailJson != null && !tripDetailJson.trim().equals("")) {
                double latitude = generalFunc.parseDoubleValue(0.0, generalFunc.getJsonValue("tStartLat", tripDetailJson));
                double longitude = generalFunc.parseDoubleValue(0.0, generalFunc.getJsonValue("tStartLong", tripDetailJson));
                Location loc = new Location("gps");
                loc.setLatitude(latitude);
                loc.setLongitude(longitude);
                onLocationUpdate(loc);
            }
        }

        initializeViews();

        if (getLastLocation != null) {
            getLastLocation.stopLocationUpdates();
            getLastLocation = null;
        }

        GetLocationUpdates.locationResolutionAsked = false;
        getLastLocation = new GetLocationUpdates(getActContext(), Utils.LOCATION_UPDATE_MIN_DISTANCE_IN_MITERS, true, this);

    }

    public void checkDrawerState() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START) == true) {
            closeDrawer();
        } else {
            openDrawer();
        }
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onMapClick(LatLng latLng) {

        sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    public GoogleMap getMap() {
        return this.gMap;
    }

    public void setShadow() {
        if (cabSelectionFrag != null) {
            cabSelectionFrag.setShadow();
        }
    }

    public void setUserLocImgBtnMargin(int margin) {
    }

    public void initializeLoadCab() {
        if (isDriverAssigned == true) {
            return;
        }

        loadAvailCabs = new LoadAvailableCab(getActContext(), generalFunc, selectedCabTypeId, userLocation,
                getMap(), userProfileJson);

        loadAvailCabs.pickUpAddress = pickUpLocationAddress;
        loadAvailCabs.currentGeoCodeResult = currentGeoCodeObject;
        loadAvailCabs.checkAvailableCabs();
    }

    public void getWalletBalDetails() {


        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "GetMemberWalletBalance");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.app_type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), false, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {


            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail) {
                    try {
                        generalFunc.storeData(Utils.ISWALLETBALNCECHANGE, "No");
                        String userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
                        JSONObject object = generalFunc.getJsonObject(userProfileJson);
                        object.put("user_available_balance", generalFunc.getJsonValue("MemberBalance", responseString));
                        generalFunc.storeData(Utils.USER_PROFILE_JSON, object.toString());

                        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
                        obj_userProfile = generalFunc.getJsonObject(userProfileJson);


                        setUserInfo();
                    } catch (Exception e) {

                    }
                }
            }
        });
        exeWebServer.execute();
    }


    public void showMessageWithAction(View view, String message, final Bundle bn) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_INDEFINITE).setAction(generalFunc.retrieveLangLBl("", "LBL_BTN_VERIFY_TXT"), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new StartActProcess(getActContext()).startActForResult(VerifyInfoActivity.class, bn, Utils.VERIFY_INFO_REQ_CODE);

                    }
                });
        snackbar.setActionTextColor(getActContext().getResources().getColor(R.color.verfiybtncolor));
        snackbar.setDuration(10000);
        snackbar.show();
    }

    boolean isIinitializeViewsCall = false;

    public void initializeViews() {
        if (isIinitializeViewsCall) {
            if (pickUpLocation != null && mainHeaderFrag != null) {
                mainHeaderFrag.setSourceAddress(pickUpLocation.getLatitude(), pickUpLocation.getLongitude());
                return;
            }
            return;
        }
        Utils.printLog("initializeViews", "::call::" + pickUpLocation);
        if (pickUpLocation != null && mainHeaderFrag != null) {
            mainHeaderFrag.setSourceAddress(pickUpLocation.getLatitude(), pickUpLocation.getLongitude());
            return;
        }

        isIinitializeViewsCall = true;

        String vTripStatus = generalFunc.getJsonValueStr("vTripStatus", obj_userProfile);


        if (vTripStatus != null && (vTripStatus.equals("Active") || vTripStatus.equals("On Going Trip"))) {

            JSONObject tripDetailJson = generalFunc.getJsonObject("TripDetails", obj_userProfile);

            if (tripDetailJson != null) {
                eTripType = generalFunc.getJsonValueStr("eType", tripDetailJson);
                String tripId = generalFunc.getJsonValueStr("iTripId", tripDetailJson);
                this.tripId = tripId;

                if (eTripType.equals("Deliver")) {
                    eTripType = Utils.CabGeneralType_Deliver;
                }

                if (!eTripType.equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
                    configureAssignedDriver(true);
                    configureDeliveryView(true);

                    return;
                }
            }
        }

        setMainHeaderView(isUfx);

        Utils.runGC();
    }

    private void setMainHeaderView(boolean isUfx) {
        try {
            if (mainHeaderFrag == null) {

                mainHeaderFrag = new MainHeaderFragment();

                Bundle bundle = new Bundle();
                bundle.putBoolean("isUfx", isUfx);
                bundle.putBoolean("isRedirectMenu", true);
                mainHeaderFrag.setArguments(bundle);
                if (getMap() != null) {
                    mainHeaderFrag.setGoogleMapInstance(getMap());
                }
            }
            if (mainHeaderFrag != null) {
                if (getMap() != null) {
                    mainHeaderFrag.releaseAddressFinder();
                }
            }
            try {
                super.onPostResume();
            } catch (Exception e) {
                Utils.printLog("Exception", e.toString());
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.headerContainer, mainHeaderFrag).commit();

            configureDeliveryView(false);


        } catch (Exception e) {
            Utils.printLog("Exception", e.toString());

        }

    }

    private void setRiderDefaultView() {
        if (cabSelectionFrag == null) {
            Bundle bundle = new Bundle();
            bundle.putString("RideDeliveryType", RideDeliveryType);
            cabSelectionFrag = new CabSelectionFragment();
            cabSelectionFrag.setArguments(bundle);
            pinImgView.setVisibility(View.GONE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) (userLocBtnImgView).getLayoutParams();
            params.bottomMargin = Utils.dipToPixels(getActContext(), 240);

            if (driverAssignedHeaderFrag != null) {
                userTripBtnImgView.setVisibility(View.VISIBLE);
            }
        }

        if (mainHeaderFrag != null) {
            mainHeaderFrag.addAddressFinder();
        }

        if (driverAssignedHeaderFrag != null) {
            pinImgView.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) (userLocBtnImgView).getLayoutParams();
            params.bottomMargin = Utils.dipToPixels(getActContext(), 200);
            userTripBtnImgView.setVisibility(View.VISIBLE);
        }

        setCurrentType();

        try {
            super.onPostResume();
        } catch (Exception e) {
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.dragView, cabSelectionFrag).commit();

        configureDeliveryView(false);
    }

    private void setCurrentType() {

        if (cabSelectionFrag == null) {
            return;
        }
        if (app_type.equalsIgnoreCase("Delivery")) {
            cabSelectionFrag.currentCabGeneralType = "Deliver";
        } else if (app_type.equalsIgnoreCase("UberX")) {
            cabSelectionFrag.currentCabGeneralType = Utils.CabGeneralType_UberX;
        } else if (app_type.equalsIgnoreCase("Ride-Delivery") || app_type.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX)) {
            if (isDeliver(RideDeliveryType)) {
                cabSelectionFrag.currentCabGeneralType = "Deliver";
            } else {
                cabSelectionFrag.currentCabGeneralType = Utils.CabGeneralType_Ride;
            }
        } else {
            cabSelectionFrag.currentCabGeneralType = Utils.CabGeneralType_Ride;
        }
    }

    public void configureDeliveryView(boolean isHidden) {
        if (generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX)) {

        } else if (generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase("Ride-Delivery") && isHidden == false) {
            (findViewById(R.id.deliveryArea)).setVisibility(View.VISIBLE);
            setUserLocImgBtnMargin(190);
        } else {
            (findViewById(R.id.deliveryArea)).setVisibility(View.GONE);
            setUserLocImgBtnMargin(105);
        }

    }

    public void configDestinationMode(boolean isDestinationMode) {
        this.isDestinationMode = isDestinationMode;
        try {
            if (isDestinationMode == false) {
                if (loadAvailCabs != null) {
                    loadAvailCabs.filterDrivers(false);
                }
                animateToLocation(getPickUpLocation().getLatitude(), getPickUpLocation().getLongitude());
                if (cabSelectionFrag != null) {
                    noCabAvail = false;
                    changeLable();
                }
            } else {
                pinImgView.setImageResource(R.drawable.pin_dest_select);
                if (cabSelectionFrag != null) {
                    if (loadAvailCabs != null) {
                        if (loadAvailCabs.isAvailableCab) {
                            changeLable();
                            noCabAvail = true;
                        }
                    }
                }

                if (timeval.equalsIgnoreCase("\n" + "--")) {
                    noCabAvail = false;
                } else {
                    noCabAvail = true;
                }
                changeLable();
                pinImgView.setImageResource(R.drawable.pin_dest_select);
                if (isDestinationAdded == true && !getDestLocLatitude().trim().equals("") && !getDestLocLongitude().trim().equals("")) {
                    animateToLocation(generalFunc.parseDoubleValue(0.0, getDestLocLatitude()), generalFunc.parseDoubleValue(0.0, getDestLocLongitude()));
                }

            }
            changeLable();

            if (mainHeaderFrag != null) {
                mainHeaderFrag.configDestinationMode(isDestinationMode);
            }
        } catch (Exception e) {

        }
    }

    private void changeLable() {
        if (cabSelectionFrag != null) {
            cabSelectionFrag.setLabels(false);
        }
    }

    public void animateToLocation(double latitude, double longitude) {
        if (latitude != 0.0 && longitude != 0.0) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(latitude, longitude))
                    .zoom(gMap.getCameraPosition().zoom).build();
            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void animateToLocation(double latitude, double longitude, float zoom) {
        try {
            if (latitude != 0.0 && longitude != 0.0) {
                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        new LatLng(latitude, longitude))
                        .zoom(zoom).build();
                gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        } catch (Exception e) {

        }
    }

    public void configureAssignedDriver(boolean isAppRestarted) {
        isDriverAssigned = true;
        addDrawer.setIsDriverAssigned(isDriverAssigned);

        if (driverAssignedHeaderFrag != null) {
            driverAssignedHeaderFrag.releaseAllTask();
            driverAssignedHeaderFrag = null;
        }

        driverDetailFrag = new DriverDetailFragment();
        driverAssignedHeaderFrag = new DriverAssignedHeaderFragment();

        Bundle bn = new Bundle();
        bn.putString("isAppRestarted", "" + isAppRestarted);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) (userLocBtnImgView).getLayoutParams();
        params.bottomMargin = Utils.dipToPixels(getActContext(), 200);

        if (driverAssignedHeaderFrag != null) {
            userTripBtnImgView.setVisibility(View.VISIBLE);
        }

        driverAssignedData = new HashMap<>();
        releaseScheduleNotificationTask();
        if (isAppRestarted == true) {

            JSONObject tripDetailJson = generalFunc.getJsonObject("TripDetails", userProfileJson);
            JSONObject driverDetailJson = generalFunc.getJsonObject("DriverDetails", userProfileJson);
            JSONObject driverCarDetailJson = generalFunc.getJsonObject("DriverCarDetails", userProfileJson);

            String vTripPaymentMode = generalFunc.getJsonValueStr("vTripPaymentMode", tripDetailJson);
            String tEndLat = generalFunc.getJsonValueStr("tEndLat", tripDetailJson);
            String tEndLong = generalFunc.getJsonValueStr("tEndLong", tripDetailJson);
            String tDaddress = generalFunc.getJsonValueStr("tDaddress", tripDetailJson);

            if (vTripPaymentMode.equals("Cash")) {
                isCashSelected = true;
            } else {
                isCashSelected = false;
            }

            assignedDriverId = generalFunc.getJsonValueStr("iDriverId", tripDetailJson);
            assignedTripId = generalFunc.getJsonValueStr("iTripId", tripDetailJson);
            eTripType = generalFunc.getJsonValueStr("eType", tripDetailJson);

            if (!tEndLat.equals("0.0") && !tEndLong.equals("0.0")
                    && !tDaddress.equals("Not Set") && !tEndLat.equals("") && !tEndLong.equals("")
                    && !tDaddress.equals("")) {
                isDestinationAdded = true;
                destAddress = tDaddress;
                destLocLatitude = tEndLat;
                destLocLongitude = tEndLong;
            }

            driverAssignedData.put("destLatitude", generalFunc.getJsonValueStr("tEndLat", tripDetailJson));
            driverAssignedData.put("eRental", generalFunc.getJsonValueStr("eRental", tripDetailJson));
            driverAssignedData.put("destLongitude", generalFunc.getJsonValueStr("tEndLong", tripDetailJson));
            driverAssignedData.put("PickUpLatitude", generalFunc.getJsonValueStr("tStartLat", tripDetailJson));
            driverAssignedData.put("PickUpLongitude", generalFunc.getJsonValueStr("tStartLong", tripDetailJson));
            driverAssignedData.put("eFlatTrip", generalFunc.getJsonValueStr("eFlatTrip", tripDetailJson));
            driverAssignedData.put("vDeliveryConfirmCode", generalFunc.getJsonValueStr("vDeliveryConfirmCode", tripDetailJson));
            driverAssignedData.put("PickUpAddress", generalFunc.getJsonValueStr("tSaddress", tripDetailJson));
            driverAssignedData.put("vVehicleType", generalFunc.getJsonValueStr("vVehicleType", tripDetailJson));
            driverAssignedData.put("eIconType", generalFunc.getJsonValueStr("eIconType", tripDetailJson));
            driverAssignedData.put("eType", generalFunc.getJsonValueStr("eType", tripDetailJson));
            driverAssignedData.put("DriverTripStatus", generalFunc.getJsonValueStr("vTripStatus", driverDetailJson));
            driverAssignedData.put("DriverPhone", generalFunc.getJsonValueStr("vPhone", driverDetailJson));
            driverAssignedData.put("DriverPhoneCode", generalFunc.getJsonValueStr("vCode", driverDetailJson));
            driverAssignedData.put("DriverRating", generalFunc.getJsonValueStr("vAvgRating", driverDetailJson));
            driverAssignedData.put("DriverAppVersion", generalFunc.getJsonValueStr("iAppVersion", driverDetailJson));
            driverAssignedData.put("DriverLatitude", generalFunc.getJsonValueStr("vLatitude", driverDetailJson));
            driverAssignedData.put("DriverLongitude", generalFunc.getJsonValueStr("vLongitude", driverDetailJson));
            driverAssignedData.put("DriverImage", generalFunc.getJsonValueStr("vImage", driverDetailJson));
            driverAssignedData.put("DriverName", generalFunc.getJsonValueStr("vName", driverDetailJson));
            driverAssignedData.put("DriverCarPlateNum", generalFunc.getJsonValueStr("vLicencePlate", driverCarDetailJson));
            driverAssignedData.put("DriverCarName", generalFunc.getJsonValueStr("make_title", driverCarDetailJson));
            driverAssignedData.put("DriverCarModelName", generalFunc.getJsonValueStr("model_title", driverCarDetailJson));
            driverAssignedData.put("DriverCarColour", generalFunc.getJsonValueStr("vColour", driverCarDetailJson));
            driverAssignedData.put("vCode", generalFunc.getJsonValueStr("vCode", driverDetailJson));


        } else {

            if (currentLoadedDriverList == null) {
                generalFunc.restartApp();
                return;
            }

            boolean isDriverIdMatch = false;
            for (int i = 0; i < currentLoadedDriverList.size(); i++) {
                HashMap<String, String> driverDataMap = currentLoadedDriverList.get(i);
                String iDriverId = driverDataMap.get("driver_id");

                if (iDriverId.equals(assignedDriverId)) {
                    isDriverIdMatch = true;

                    if (destLocation != null) {

                        driverAssignedData.put("destLatitude", destLocation.getLatitude() + "");
                        driverAssignedData.put("destLongitude", destLocation.getLongitude() + "");
                    }
                    driverAssignedData.put("PickUpLatitude", "" + getPickUpLocation().getLatitude());
                    driverAssignedData.put("PickUpLongitude", "" + getPickUpLocation().getLongitude());

                    if (mainHeaderFrag != null) {
                        driverAssignedData.put("PickUpAddress", mainHeaderFrag.getPickUpAddress());
                    } else {
                        driverAssignedData.put("PickUpAddress", pickUpLocationAddress);
                    }

                    driverAssignedData.put("vVehicleType", generalFunc.getSelectedCarTypeData(selectedCabTypeId, cabTypesArrList, "vVehicleType"));
                    driverAssignedData.put("eIconType", generalFunc.getSelectedCarTypeData(selectedCabTypeId, cabTypesArrList, "eIconType"));
                    driverAssignedData.put("vDeliveryConfirmCode", "");

                    driverAssignedData.put("DriverTripStatus", "");
                    driverAssignedData.put("DriverPhone", driverDataMap.get("vPhone_driver"));
                    driverAssignedData.put("DriverPhoneCode", driverDataMap.get("vPhoneCode_driver"));
                    driverAssignedData.put("DriverRating", driverDataMap.get("average_rating"));
                    driverAssignedData.put("DriverAppVersion", driverDataMap.get("iAppVersion"));
                    driverAssignedData.put("DriverLatitude", driverDataMap.get("Latitude"));
                    driverAssignedData.put("DriverLongitude", driverDataMap.get("Longitude"));
                    driverAssignedData.put("DriverImage", driverDataMap.get("driver_img"));
                    driverAssignedData.put("DriverName", driverDataMap.get("Name"));
                    driverAssignedData.put("DriverCarPlateNum", driverDataMap.get("vLicencePlate"));
                    driverAssignedData.put("DriverCarName", driverDataMap.get("make_title"));
                    driverAssignedData.put("DriverCarModelName", driverDataMap.get("model_title"));
                    driverAssignedData.put("DriverCarColour", driverDataMap.get("vColour"));
                    driverAssignedData.put("eType", getCurrentCabGeneralType());

                    break;
                }
            }

            if (isDriverIdMatch == false) {
                generalFunc.restartApp();
                return;
            }
        }

        driverAssignedData.put("iDriverId", assignedDriverId);
        driverAssignedData.put("iTripId", assignedTripId);

        driverAssignedData.put("PassengerName", generalFunc.getJsonValueStr("vName", obj_userProfile));
        driverAssignedData.put("PassengerImageName", generalFunc.getJsonValueStr("vImgName", obj_userProfile));

        bn.putSerializable("TripData", driverAssignedData);
        driverAssignedHeaderFrag.setArguments(bn);
        driverAssignedHeaderFrag.setGoogleMap(getMap());
        if (!TextUtils.isEmpty(tripId)) {
            driverAssignedHeaderFrag.isBackVisible = true;
        }

        driverDetailFrag.setArguments(bn);


        Location pickUpLoc = new Location("");
        pickUpLoc.setLatitude(generalFunc.parseDoubleValue(0.0, driverAssignedData.get("PickUpLatitude")));
        pickUpLoc.setLongitude(generalFunc.parseDoubleValue(0.0, driverAssignedData.get("PickUpLongitude")));
        this.pickUpLocation = pickUpLoc;

        if (mainHeaderFrag != null) {
            mainHeaderFrag.releaseResources();
            mainHeaderFrag = null;
        }

        if (cabSelectionFrag != null) {
            cabSelectionFrag.releaseResources();
            cabSelectionFrag = null;
        }

        Utils.runGC();

        if (isnotification) {
            chatMsg();
        }

        setPanelHeight(175);

        try {
            super.onPostResume();
        } catch (Exception e) {

        }

        if (driverDetailFrag != null) {
            deliverArea.setVisibility(View.GONE);
            otherArea.setEnabled(false);
            deliverArea.setEnabled(false);
            rideArea.setEnabled(false);
        }

        if (!isFinishing()) {
            gMap.clear();
            getMap().setMyLocationEnabled(false);

            resetMapView();
            getMap().setPadding(0, 0, 0, Utils.dpToPx(getActContext(), 232));
            map.getView().requestLayout();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.headerContainer, driverAssignedHeaderFrag).commit();

            if (!isAppRestarted) {
                if (isFixFare) {
                    if (driverAssignedHeaderFrag != null) {
                        driverAssignedHeaderFrag.eConfirmByUser = "Yes";
                        driverAssignedHeaderFrag.handleEditDest();
                    }
                }
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.dragView, driverDetailFrag).commit();

            if (driverAssignedHeaderFrag != null) {
                userTripBtnImgView.setVisibility(View.VISIBLE);
            }

            if (Utils.checkText(generalFunc.retrieveValue("OPEN_CHAT"))) {
                JSONObject OPEN_CHAT_DATA_OBJ = generalFunc.getJsonObject(generalFunc.retrieveValue("OPEN_CHAT"));
                generalFunc.removeValue("OPEN_CHAT");
               /*
                generalFunc.storeData("OPEN_CHAT", "No");
                Bundle bnChat = new Bundle();

                bnChat.putString("iFromMemberId", driverAssignedData.get("iDriverId"));
                bnChat.putString("FromMemberImageName", driverAssignedData.get("DriverImage"));
                bnChat.putString("iTripId", driverAssignedData.get("iTripId"));
                bnChat.putString("FromMemberName", driverAssignedData.get("DriverName"));

                new StartActProcess(getActContext()).startActWithData(ChatActivity.class, bnChat);
*/
                if (OPEN_CHAT_DATA_OBJ != null)
                    new StartActProcess(getActContext()).startActWithData(ChatActivity.class, generalFunc.createChatBundle(OPEN_CHAT_DATA_OBJ));
            }

        } else {
            generalFunc.restartApp();
        }


    }

    private void resetMapView() {
        map.getView().invalidate();
        gMap.setPadding(0, 0, 0, 0);
        map.getView().requestLayout();
    }

    private void resetUserLocBtnView() {
        userLocBtnImgView.invalidate();
        userLocBtnImgView.requestLayout();
    }

    @Override
    public void onLocationUpdate(Location location) {

        if (location == null) {
            return;
        }

        if (getIntent().getStringExtra("latitude") != null && getIntent().getStringExtra("longitude") != null) {
            Location loc_ufx = new Location("gps");
            loc_ufx.setLatitude(GeneralFunctions.parseDoubleValue(0.0, getIntent().getStringExtra("latitude")));
            loc_ufx.setLongitude(GeneralFunctions.parseDoubleValue(0.0, getIntent().getStringExtra("longitude")));
            this.userLocation = loc_ufx;
        } else {
            this.userLocation = location;
        }

        if (isFirstLocation == true) {

            double currentZoomLevel = Utils.defaultZomLevel;

            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude()))
                    .zoom((float) currentZoomLevel).build();

            if (cameraPosition != null) {
                getMap().moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

            if (pickUpLocation == null) {
                pickUpLocation = this.userLocation;
                initializeViews();
            }

            isFirstLocation = false;
        }
    }


    public void setETA(String time) {

        timeval = time;

        if (cabSelectionFrag != null) {

            cabSelectionFrag.handleSourceMarker(time);
            cabSelectionFrag.mangeMrakerPostion();
        }
    }

    public CameraPosition cameraForUserPosition() {

        try {
            if (cabSelectionFrag != null) {
                return null;
            }

            double currentZoomLevel = getMap() == null ? Utils.defaultZomLevel : getMap().getCameraPosition().zoom;
            // if (Utils.defaultZomLevel > currentZoomLevel) {
            currentZoomLevel = Utils.defaultZomLevel;
            // }
            String TripDetails = generalFunc.getJsonValue("TripDetails", userProfileJson);

            String vTripStatus = generalFunc.getJsonValue("vTripStatus", userProfileJson);
            if (generalFunc.isLocationEnabled()) {

                double startLat = 0.0;
                double startLong = 0.0;

                if (vTripStatus != null && startLat != 0.0 && startLong != 0.0 && ((vTripStatus.equals("Active") || vTripStatus.equals("On Going Trip")))) {

                    Location tempickuploc = new Location("temppickkup");

                    tempickuploc.setLatitude(startLat);
                    tempickuploc.setLongitude(startLong);

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(tempickuploc.getLatitude(), tempickuploc.getLongitude()))
                            .zoom((float) currentZoomLevel).build();


                    return cameraPosition;


                } else {
//
                    // if (Utils.defaultZomLevel > currentZoomLevel) {
                    currentZoomLevel = Utils.defaultZomLevel;
                    //}
                    if (userLocation != null) {
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude()))
                                .zoom((float) currentZoomLevel).build();

//                        pickUpLocation = userLocation;

                        return cameraPosition;
                    } else {
                        return null;
                    }
                }
            } else if (userLocation != null) {
                if (Utils.defaultZomLevel > currentZoomLevel) {
                    currentZoomLevel = Utils.defaultZomLevel;
                }
                if (userLocation != null) {
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(this.userLocation.getLatitude(), this.userLocation.getLongitude()))
                            .zoom((float) currentZoomLevel).build();

//                    pickUpLocation = userLocation;

                    return cameraPosition;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {

        }
        return null;

    }

    public void redirectToMapOrList(String choiceType, boolean autoLoad) {

        if (autoLoad == true && currentUberXChoiceType.equalsIgnoreCase(Utils.Cab_UberX_Type_Map)) {
            return;
        }

        this.currentUberXChoiceType = choiceType;

        mainHeaderFrag.listTxt.setBackgroundColor(choiceType.equalsIgnoreCase(Utils.Cab_UberX_Type_List) ?
                Color.parseColor("#FFFFFF") : getResources().getColor(R.color.appThemeColor_1));
        mainHeaderFrag.mapTxt.setBackgroundColor(choiceType.equalsIgnoreCase(Utils.Cab_UberX_Type_List) ?
                getResources().getColor(R.color.appThemeColor_1) : Color.parseColor(
                "#FFFFFF"));

        mainHeaderFrag.mapTxt.setTextColor(choiceType.equalsIgnoreCase(Utils.Cab_UberX_Type_List) ?
                Color.parseColor("#FFFFFF") : Color.parseColor("#1C1C1C"));
        mainHeaderFrag.listTxt.setTextColor(choiceType.equalsIgnoreCase(Utils.Cab_UberX_Type_List) ?
                Color.parseColor("#1C1C1C") : Color.parseColor("#FFFFFF"));
        if (driver_detail_bottomView != null || driver_detail_bottomView.getVisibility() == View.VISIBLE) {

            driver_detail_bottomView.setVisibility(View.GONE);
        }
        if (choiceType.equalsIgnoreCase(Utils.Cab_UberX_Type_List)) {

            uberXNoDriverTxt.setText(generalFunc.retrieveLangLBl("No Provider Available", "LBL_NO_PROVIDER_AVAIL_TXT"));

            if (!isUfxRideLater) {

                uberXDriverListArea.setVisibility(View.VISIBLE);
                uberXNoDriverTxt.setVisibility(View.GONE);
                ridelaterView.setVisibility(View.GONE);
                uberXDriverList.clear();
            }

        } else {
            (findViewById(R.id.driverListAreaLoader)).setVisibility(View.GONE);
            mainContent.setVisibility(View.VISIBLE);
            uberXDriverListArea.setVisibility(View.GONE);
        }
    }

    public void OpenCardPaymentAct(boolean fromcabselection) {
        iswallet = true;
        Bundle bn = new Bundle();
        // bn.putString("UserProfileJson", userProfileJson);
        bn.putBoolean("fromcabselection", fromcabselection);
        new StartActProcess(getActContext()).startActForResult(CardPaymentActivity.class, bn, Utils.CARD_PAYMENT_REQ_CODE);
    }

    public boolean isPickUpLocationCorrect() {
        String pickUpLocAdd = mainHeaderFrag != null ? (mainHeaderFrag.getPickUpAddress().equals(
                generalFunc.retrieveLangLBl("", "LBL_SELECTING_LOCATION_TXT")) ? "" : mainHeaderFrag.getPickUpAddress()) : "";

        if (isUfx) {
            return true;
        }

        if (!pickUpLocAdd.equals("")) {
            return true;
        }
        return false;
    }

    public void continuePickUpProcess() {
        String pickUpLocAdd = mainHeaderFrag != null ? (mainHeaderFrag.getPickUpAddress().equals(
                generalFunc.retrieveLangLBl("", "LBL_SELECTING_LOCATION_TXT")) ? "" : mainHeaderFrag.getPickUpAddress()) : "";

        if (!pickUpLocAdd.equals("")) {
            if (isUfx) {
                checkSurgePrice("", null);
            } else if (generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase("UberX")) {
                checkSurgePrice("", null);
            } else {
                setCabReqType(Utils.CabReqType_Now);
                checkSurgePrice("", null);
            }
        } else {
            if (isUfx) {
                checkSurgePrice("", null);
            } else if (generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase("UberX")) {
                checkSurgePrice("", null);
            }
        }
    }

    public String getCurrentCabGeneralType() {

        if (app_type.equalsIgnoreCase("Ride-Delivery") || app_type.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX)) {
            if (!RideDeliveryType.equals("")) {
                if (isUfx) {
                    return Utils.CabGeneralType_UberX;
                }


                if (isDeliver(RideDeliveryType)) {
                    return "Deliver";
                } else {
                    return RideDeliveryType;
                }

            } else {
                if (isUfx) {
                    return Utils.CabGeneralType_UberX;
                }

                return Utils.CabGeneralType_Ride;
            }
        }


        if (cabSelectionFrag != null) {
            return cabSelectionFrag.getCurrentCabGeneralType();
        } else if (!eTripType.trim().equals("")) {
            return eTripType;
        }

        if (isUfx) {
            return Utils.CabGeneralType_UberX;
        }
        return app_type;
    }

    String selectedTime = "";

    public void chooseDateTime() {


        if (isPickUpLocationCorrect() == false) {
            return;
        }

        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                .setListener(new SlideDateTimeListener() {
                    @Override
                    public void onDateTimeSet(Date date) {

                        selectedDateTime = Utils.convertDateToFormat("yyyy-MM-dd HH:mm:ss", date);
                        selectedDateTimeZone = Calendar.getInstance().getTimeZone().getID();

                        if (Utils.isValidTimeSelect(date, TimeUnit.HOURS.toMillis(1)) == false) {
                            generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl("Invalid pickup time", "LBL_INVALID_PICKUP_TIME"),
                                    generalFunc.retrieveLangLBl("Please make sure that pickup time is after atleast an hour from now.", "LBL_INVALID_PICKUP_NOTE_MSG"));
                            return;
                        }

                        if (Utils.isValidTimeSelectForLater(date, TimeUnit.DAYS.toMillis(30)) == false) {
                            generalFunc.showGeneralMessage(generalFunc.retrieveLangLBl("Invalid pickup time", "LBL_INVALID_PICKUP_TIME"),
                                    generalFunc.retrieveLangLBl("Please make sure that pickup time is after atleast an 1 month from now.", "LBL_INVALID_PICKUP_NOTE_MONTH_MSG"));
                            return;
                        }


                        setCabReqType(Utils.CabReqType_Later);

                        selectedTime = Utils.convertDateToFormat("yyyy-MM-dd HH:mm:ss", date);

                        if (isDeliver(getCurrentCabGeneralType())) {
                            setDeliverySchedule();

                        } else {

                            if (!cabSelectionFrag.handleRnetalView(Utils.convertDateToFormat(Utils.getDetailDateFormat(getActContext()), date))) {
                                checkSurgePrice(selectedTime, deliveryData);
                            }
                        }
                    }

                    @Override
                    public void onDateTimeCancel() {

                        if (cabSelectionFrag != null) {
                            //cabSelectionFrag.ride_now_btn.setClickable(true);
                            // cabSelectionFrag.ride_now_btn.setEnabled(true);

                        }

                    }

                })

                .setInitialDate(new Date())
                .setMinDate(Calendar.getInstance().getTime())
//                .setMinDate(getCurrentDate1hrAfter())
                //.setMaxDate(maxDate)
//                .setIs24HourTime(true)
                .setIs24HourTime(false)
                //.setTheme(SlideDateTimePicker.HOLO_DARK)
                .setIndicatorColor(getResources().getColor(R.color.appThemeColor_2))
                .build()
                .show();
    }

    public void setCabTypeList(ArrayList<HashMap<String, String>> cabTypeList) {
        this.cabTypeList = cabTypeList;
    }

    public void changeCabType(String selectedCabTypeId) {
        this.selectedCabTypeId = selectedCabTypeId;
        if (loadAvailCabs != null) {
            loadAvailCabs.setCabTypeId(this.selectedCabTypeId);
            loadAvailCabs.setPickUpLocation(pickUpLocation);
            loadAvailCabs.changeCabs();
        }
    }

    public String getSelectedCabTypeId() {

        return this.selectedCabTypeId;

    }

    public boolean isFixFare = false;

    public void checkSurgePrice(final String selectedTime, final Intent data) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "checkSurgePrice");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("UserType", Utils.userType);
        parameters.put("SelectedCarTypeID", "" + getSelectedCabTypeId());

        if (cabSelectionFrag != null && !cabSelectionFrag.iRentalPackageId.equalsIgnoreCase("")) {
            parameters.put("iRentalPackageId", cabSelectionFrag.iRentalPackageId);
        }
        if (!selectedTime.trim().equals("")) {
            parameters.put("SelectedTime", selectedTime);
        }

        if (getPickUpLocation() != null) {
            parameters.put("PickUpLatitude", "" + getPickUpLocation().getLatitude());
            parameters.put("PickUpLongitude", "" + getPickUpLocation().getLongitude());
        }

        if (getDestLocLatitude() != null && !getDestLocLatitude().equalsIgnoreCase("")) {
            parameters.put("DestLatitude", "" + getDestLocLatitude());
            parameters.put("DestLongitude", "" + getDestLocLongitude());
        }

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                generalFunc.sendHeartBeat();

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {

                    if (!selectedTime.trim().equals("")) {

                        if (app_type.equalsIgnoreCase(Utils.CabGeneralType_UberX) || isUfx) {
                            ridelaterView.setVisibility(View.GONE);
                            uberXDriverListArea.setVisibility(View.GONE);
                            pickUpLocClicked();
                        } else {

                            if (generalFunc.getJsonValue("eFlatTrip", responseString).equalsIgnoreCase("Yes")) {
                                isFixFare = true;
                                openFixChargeDialog(responseString, false, data);
                            } else {
                                handleRequest(data);
                            }

                        }
                    } else {
                        if (generalFunc.getJsonValue("eFlatTrip", responseString).equalsIgnoreCase("Yes")) {
                            isFixFare = true;
                            openFixChargeDialog(responseString, false, data);
                        } else {
                            if (!isUfx) {
                                handleRequest(data);
                            }
                        }

                        if (app_type.equalsIgnoreCase(Utils.CabGeneralType_UberX) || isUfx) {
                            ridelaterView.setVisibility(View.GONE);
                            uberXDriverListArea.setVisibility(View.GONE);
                            pickUpLocClicked();
                        }
                    }

                } else {

                    if (app_type.equalsIgnoreCase(Utils.CabGeneralType_UberX) || isUfx) {
                        ridelaterView.setVisibility(View.GONE);
                        uberXDriverListArea.setVisibility(View.GONE);
                        pickUpLocClicked();
                    }

                    if (!isUfx) {
                        if (generalFunc.getJsonValue("eFlatTrip", responseString).equalsIgnoreCase("Yes")) {
                            isFixFare = true;
                            openFixChargeDialog(responseString, true, data);

                        } else {
                            openSurgeConfirmDialog(responseString, selectedTime, data);
                        }
                    }
                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }


    private void handleRequest(Intent data) {


        String driverIds = getAvailableDriverIds();

        JSONObject cabRequestedJson = new JSONObject();
        try {
            cabRequestedJson.put("Message", "CabRequested");
            cabRequestedJson.put("sourceLatitude", "" + getPickUpLocation().getLatitude());
            cabRequestedJson.put("sourceLongitude", "" + getPickUpLocation().getLongitude());
            cabRequestedJson.put("PassengerId", generalFunc.getMemberId());
            cabRequestedJson.put("PName", generalFunc.getJsonValue("vName", userProfileJson) + " "
                    + generalFunc.getJsonValue("vLastName", userProfileJson));
            cabRequestedJson.put("PPicName", generalFunc.getJsonValue("vImgName", userProfileJson));
            cabRequestedJson.put("PFId", generalFunc.getJsonValue("vFbId", userProfileJson));
            cabRequestedJson.put("PRating", generalFunc.getJsonValue("vAvgRating", userProfileJson));
            cabRequestedJson.put("PPhone", generalFunc.getJsonValue("vPhone", userProfileJson));
            cabRequestedJson.put("PPhoneC", generalFunc.getJsonValue("vPhoneCode", userProfileJson));
            cabRequestedJson.put("REQUEST_TYPE", getCurrentCabGeneralType());

            cabRequestedJson.put("selectedCatType", vUberXCategoryName);
            if (getDestinationStatus() == true) {
                cabRequestedJson.put("destLatitude", "" + getDestLocLatitude());
                cabRequestedJson.put("destLongitude", "" + getDestLocLongitude());
            } else {
                cabRequestedJson.put("destLatitude", "");
                cabRequestedJson.put("destLongitude", "");
            }

            getTollcostValue(driverIds, cabRequestedJson.toString(), data);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    public void openFixChargeDialog(String responseString, boolean isSurCharge, Intent data) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        builder.setTitle("");
        builder.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.surge_confirm_design, null);
        builder.setView(dialogView);

        MTextView payableAmountTxt;
        MTextView payableTxt;

        ((MTextView) dialogView.findViewById(R.id.headerMsgTxt)).setText(generalFunc.retrieveLangLBl("", generalFunc.retrieveLangLBl("", "LBL_FIX_FARE_HEADER")));


        ((MTextView) dialogView.findViewById(R.id.tryLaterTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_TRY_LATER"));

        payableTxt = (MTextView) dialogView.findViewById(R.id.payableTxt);
        payableAmountTxt = (MTextView) dialogView.findViewById(R.id.payableAmountTxt);
        if (!generalFunc.getJsonValue("fFlatTripPricewithsymbol", responseString).equalsIgnoreCase("")) {
            payableAmountTxt.setVisibility(View.VISIBLE);
            payableTxt.setVisibility(View.GONE);

            if (isSurCharge) {

                payableAmount = generalFunc.getJsonValue("fFlatTripPricewithsymbol", responseString) + " " + "(" + generalFunc.retrieveLangLBl("", "LBL_AT_TXT") + " " +
                        generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("SurgePrice", responseString)) + ")";
                ((MTextView) dialogView.findViewById(R.id.surgePriceTxt)).setText(generalFunc.convertNumberWithRTL(payableAmount));
            } else {
                payableAmount = generalFunc.getJsonValue("fFlatTripPricewithsymbol", responseString);
                ((MTextView) dialogView.findViewById(R.id.surgePriceTxt)).setText(generalFunc.convertNumberWithRTL(payableAmount));
            }
        } else {
            payableAmountTxt.setVisibility(View.GONE);
            payableTxt.setVisibility(View.VISIBLE);
        }

        MButton btn_type2 = ((MaterialRippleLayout) dialogView.findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_ACCEPT_TXT"));
        btn_type2.setId(Utils.generateViewId());

        btn_type2.setOnClickListener(view -> {
            alertDialog_surgeConfirm.dismiss();
            handleRequest(data);
        });

        (dialogView.findViewById(R.id.tryLaterTxt)).setOnClickListener(view -> {
            isFixFare = false;
            alertDialog_surgeConfirm.dismiss();
            closeRequestDialog(false);
        });


        alertDialog_surgeConfirm = builder.create();
        alertDialog_surgeConfirm.setCancelable(false);
        alertDialog_surgeConfirm.setCanceledOnTouchOutside(false);
        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(alertDialog_surgeConfirm);
        }

        alertDialog_surgeConfirm.show();
    }

    public void openSurgeConfirmDialog(String responseString, final String selectedTime, Intent data) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        builder.setTitle("");
        builder.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.surge_confirm_design, null);
        builder.setView(dialogView);

        MTextView payableAmountTxt;
        MTextView payableTxt;

        ((MTextView) dialogView.findViewById(R.id.headerMsgTxt)).setText(generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
        ((MTextView) dialogView.findViewById(R.id.surgePriceTxt)).setText(generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("SurgePrice", responseString)));

        ((MTextView) dialogView.findViewById(R.id.tryLaterTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_TRY_LATER"));

        payableTxt = (MTextView) dialogView.findViewById(R.id.payableTxt);
        payableAmountTxt = (MTextView) dialogView.findViewById(R.id.payableAmountTxt);
        payableTxt.setText(generalFunc.retrieveLangLBl("", "LBL_PAYABLE_AMOUNT"));


        if (cabSelectionFrag != null && cabTypeList != null && cabTypeList.size() > 0 && cabTypeList.get(cabSelectionFrag.selpos).get("total_fare") != null && !cabTypeList.get(cabSelectionFrag.selpos).get("total_fare").equals("") && !cabTypeList.get(cabSelectionFrag.selpos).get("eRental").equals("Yes")) {

            payableAmountTxt.setVisibility(View.VISIBLE);
            payableTxt.setVisibility(View.GONE);
            payableAmount = generalFunc.convertNumberWithRTL(cabTypeList.get(cabSelectionFrag.selpos).get("total_fare"));

            payableAmountTxt.setText(generalFunc.retrieveLangLBl("Approx payable amount", "LBL_APPROX_PAY_AMOUNT") + ": " + payableAmount);
        } else {
            payableAmountTxt.setVisibility(View.GONE);
            payableTxt.setVisibility(View.VISIBLE);

        }

        MButton btn_type2 = ((MaterialRippleLayout) dialogView.findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_ACCEPT_SURGE"));
        btn_type2.setId(Utils.generateViewId());

        btn_type2.setOnClickListener(view -> {

            alertDialog_surgeConfirm.dismiss();
            handleRequest(data);
        });

        (dialogView.findViewById(R.id.tryLaterTxt)).setOnClickListener(view -> {
            alertDialog_surgeConfirm.dismiss();
            closeRequestDialog(false);
            cabSelectionFrag.ride_now_btn.setClickable(true);
            isdelivernow = false;
            isdeliverlater = false;

        });


        alertDialog_surgeConfirm = builder.create();
        alertDialog_surgeConfirm.setCancelable(false);
        alertDialog_surgeConfirm.setCanceledOnTouchOutside(false);
        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(alertDialog_surgeConfirm);
        }

        alertDialog_surgeConfirm.show();
    }

    public void pickUpLocClicked() {

        configureDeliveryView(true);
        redirectToMapOrList(Utils.Cab_UberX_Type_List, false);

        Bundle bundle = new Bundle();
        bundle.putString("latitude", getIntent().getStringExtra("latitude"));
        bundle.putString("longitude", getIntent().getStringExtra("longitude"));
        bundle.putString("address", getIntent().getStringExtra("address"));
        bundle.putString("SelectvVehicleType", getIntent().getStringExtra("SelectvVehicleType"));

        bundle.putString("type", bookingtype);
        bundle.putString("Quantity", getIntent().getStringExtra("Quantity"));

        bundle.putString("Pname", selectedprovidername);
        if (sdate.equals("")) {
            sdate = getIntent().getStringExtra("Sdate");

        }
        if (Stime.equals("")) {
            Stime = getIntent().getStringExtra("Stime");

        }
        bundle.putString("Sdate", sdate);
        bundle.putString("Stime", Stime);

        if (UfxAmount.equals("")) {
            bundle.putString("SelectvVehiclePrice", getIntent().getStringExtra("SelectvVehiclePrice"));
            bundle.putString("Quantityprice", getIntent().getStringExtra("Quantityprice"));
        } else {

            bundle.putString("SelectvVehiclePrice", UfxAmount + "");


            if (!getIntent().getStringExtra("Quantity").equals("0")) {
                UfxAmount = UfxAmount.replace(vCurrencySymbol, "");
                int qty = GeneralFunctions.parseIntegerValue(0, getIntent().getStringExtra("Quantity"));
                float amount = GeneralFunctions.parseFloatValue(0, UfxAmount);
                bundle.putString("Quantityprice", vCurrencySymbol + (qty * amount) + "");
            } else {
                bundle.putString("Quantityprice", UfxAmount + "");
            }


            UfxAmount = "";
        }

        bundle.putString("ACCEPT_CASH_TRIPS", ACCEPT_CASH_TRIPS);
        new StartActProcess(getActContext()).startActForResult(BookingSummaryActivity.class, bundle, Utils.UFX_REQUEST_CODE);
    }

    public void setDefaultView() {

        try {
            super.onPostResume();
        } catch (Exception e) {

        }


        try {


            cabRquestType = Utils.CabReqType_Now;


            if (mainHeaderFrag != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.headerContainer, mainHeaderFrag).commit();
            }


            if (!app_type.equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
                if (mainHeaderFrag != null) {
                    mainHeaderFrag.releaseAddressFinder();
                }

            } else if (app_type.equalsIgnoreCase("UberX")) {


                if (reqPickUpFrag != null) {
                    getSupportFragmentManager().beginTransaction().remove(reqPickUpFrag).commit();
                }

                (findViewById(R.id.dragView)).setVisibility(View.GONE);
                setUserLocImgBtnMargin(5);
            }


            configDestinationMode(false);
            userLocBtnImgView.performClick();
            reqPickUpFrag = null;
            Utils.runGC();

            if (!app_type.equalsIgnoreCase(Utils.CabGeneralType_UberX)) {

                configureDeliveryView(false);
            }

            sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            try {
                new CreateAnimation(dragView, getActContext(), R.anim.design_bottom_sheet_slide_in, 600, true).startAnimation();
            } catch (Exception e) {

            }


            if (loadAvailCabs != null) {
                loadAvailCabs.setTaskKilledValue(false);
                loadAvailCabs.onResumeCalled();
            }
        } catch (Exception e) {

        }


    }

    public void setPanelHeight(int value) {

        sliding_layout.setPanelHeight((cabSelectionFrag != null && cabSelectionFrag.fragmentHeight != 0) ? cabSelectionFrag.fragmentHeight : driverDetailFrag != null ? value : Utils.dipToPixels(getActContext(), value));

        //resize map padding/height according panel height

        resetMapView();
        gMap.setPadding(0, 0, 0, sliding_layout.getPanelHeight() + 5);
        map.getView().requestLayout();

        if (userLocBtnImgView != null && (cabSelectionFrag != null || driverDetailFrag != null || driverAssignedHeaderFrag != null)) {
            resetUserLocBtnView();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) (userLocBtnImgView).getLayoutParams();
            params.bottomMargin = sliding_layout.getPanelHeight() + 5;
            userLocBtnImgView.requestLayout();
        }
    }

    public Location getPickUpLocation() {
        return this.pickUpLocation;
    }

    public String getPickUpLocationAddress() {
        return this.pickUpLocationAddress;
    }

    public void notifyCarSearching() {
        setETA("\n" + "--");

        if (reqPickUpFrag != null) {
            if (reqPickUpFrag.requestPickUpBtn != null) {
                if (!isUfxRideLater) {
                    reqPickUpFrag.requestPickUpBtn.setEnabled(false);
                    reqPickUpFrag.requestPickUpBtn.setTextColor(Color.parseColor("#BABABA"));
                }
            }
        }
    }

    public void notifyNoCabs() {

        if (isufxbackview) {
            return;
        }

        setETA("\n" + "--");
        setCurrentLoadedDriverList(new ArrayList<HashMap<String, String>>());

        if (cabSelectionFrag != null) {
            noCabAvail = false;
            changeLable();
        }


        if (reqPickUpFrag != null) {
            if (!isUfxRideLater) {
                noCabAvail = false;
                reqPickUpFrag.requestPickUpBtn.setEnabled(false);
                reqPickUpFrag.requestPickUpBtn.setTextColor(Color.parseColor("#BABABA"));
            }
        }

        changeLable();

    }


    public void notifyCabsAvailable() {
        if (cabSelectionFrag != null && loadAvailCabs != null && loadAvailCabs.listOfDrivers != null && loadAvailCabs.listOfDrivers.size() > 0) {
            if (cabSelectionFrag.isroutefound) {
                if (loadAvailCabs.isAvailableCab) {
                    if (!timeval.equalsIgnoreCase("\n" + "--")) {
                        noCabAvail = true;
                    }
                }
            }
        }

        if (reqPickUpFrag != null) {
            if (loadAvailCabs != null && loadAvailCabs.listOfDrivers != null) {

                if (loadAvailCabs.listOfDrivers.size() > 0) {
                    if (reqPickUpFrag.requestPickUpBtn != null) {
                        reqPickUpFrag.requestPickUpBtn.setEnabled(true);
                        reqPickUpFrag.requestPickUpBtn.setTextColor(getResources().getColor(R.color.btn_text_color_type2));
                    }
                }
            }
        }

        if (cabSelectionFrag != null) {
            cabSelectionFrag.setLabels(false);
        }
    }

    public void onMapCameraChanged() {
        if (cabSelectionFrag != null) {

            if (loadAvailCabs != null) {
                loadAvailCabs.filterDrivers(true);
            }

            if (mainHeaderFrag != null) {
                //notifyCarSearching();
                cabSelectionFrag.img_ridelater.setEnabled(false);


                if (isDestinationMode == true) {
                    mainHeaderFrag.setDestinationAddress(generalFunc.retrieveLangLBl("", "LBL_SELECTING_LOCATION_TXT"));
                } else {
                    mainHeaderFrag.setPickUpAddress(generalFunc.retrieveLangLBl("", "LBL_SELECTING_LOCATION_TXT"));
                }
            }
        }
    }

    public void onAddressFound(String address) {
        if (cabSelectionFrag != null) {
            notifyCabsAvailable();
            cabSelectionFrag.img_ridelater.setEnabled(true);
            if (mainHeaderFrag != null) {

                if (isDestinationMode == true) {
                    mainHeaderFrag.setDestinationAddress(address);
                } else {
                    mainHeaderFrag.setPickUpAddress(address);
                }
            }

            // cabSelectionFrag.findRoute("--");
        } else {
            if (isUserLocbtnclik) {
                isUserLocbtnclik = false;
                mainHeaderFrag.setPickUpAddress(address);
            }
        }


    }

    public void setDestinationPoint(String destLocLatitude, String destLocLongitude, String destAddress, boolean isDestinationAdded) {

        if (destLocation == null) {
            destLocation = new Location("dest");
        }
        destLocation.setLatitude(GeneralFunctions.parseDoubleValue(0.0, destLocLatitude));
        destLocation.setLongitude(GeneralFunctions.parseDoubleValue(0.0, destLocLongitude));


        this.isDestinationAdded = isDestinationAdded;
        this.destLocLatitude = destLocLatitude;
        this.destLocLongitude = destLocLongitude;
        this.destAddress = destAddress;
    }

    public boolean getDestinationStatus() {
        return this.isDestinationAdded;
    }

    public String getDestLocLatitude() {
        return this.destLocLatitude;
    }

    public String getDestLocLongitude() {
        return this.destLocLongitude;
    }

    public String getDestAddress() {
        return this.destAddress;
    }

    public void setCashSelection(boolean isCashSelected) {
        this.isCashSelected = isCashSelected;
        if (loadAvailCabs != null) {
            loadAvailCabs.changeCabs();
        }
    }

    public String getCabReqType() {
        return this.cabRquestType;
    }

    public void setCabReqType(String cabRquestType) {
        this.cabRquestType = cabRquestType;
    }

    public Bundle getFareEstimateBundle() {
        Bundle bn = new Bundle();
        bn.putString("PickUpLatitude", "" + getPickUpLocation().getLatitude());
        bn.putString("PickUpLongitude", "" + getPickUpLocation().getLongitude());
        bn.putString("isDestinationAdded", "" + getDestinationStatus());
        bn.putString("DestLocLatitude", "" + getDestLocLatitude());
        bn.putString("DestLocLongitude", "" + getDestLocLongitude());
        bn.putString("DestLocAddress", "" + getDestAddress());
        bn.putString("SelectedCarId", "" + getSelectedCabTypeId());
        bn.putString("SelectedCabType", "" + generalFunc.getSelectedCarTypeData(getSelectedCabTypeId(), cabTypesArrList, "vVehicleType"));
//        bn.putString("UserProfileJson", "" + userProfileJson);

        return bn;
    }

    public void continueDeliveryProcess() {
        String pickUpLocAdd = mainHeaderFrag != null ? (mainHeaderFrag.getPickUpAddress().equals(
                generalFunc.retrieveLangLBl("", "LBL_SELECTING_LOCATION_TXT")) ? "" : mainHeaderFrag.getPickUpAddress()) : "";

        if (!pickUpLocAdd.equals("")) {

            if (isDeliver(getCurrentCabGeneralType())) {
                setDeliverySchedule();
            } else {
                checkSurgePrice("", null);
            }
        }
    }

    public void setRideSchedule() {
        isrideschedule = true;

        if (getDestinationStatus() == false && generalFunc.retrieveValue(Utils.APP_DESTINATION_MODE).equalsIgnoreCase(Utils.STRICT_DESTINATION)) {
            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", "LBL_ADD_DEST_MSG_BOOK_RIDE"));
        }
    }

    public void setDeliverySchedule() {

        if (getDestinationStatus() == false) {
            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Please add your destination location " +
                    "to deliver your package.", "LBL_ADD_DEST_MSG_DELIVER_ITEM"));
        } else {
            Bundle bn = new Bundle();
            bn.putString("isDeliverNow", "" + getCabReqType().equals(Utils.CabReqType_Now));

        }
    }

    public void bookRide() {

        if (!isWalletPopupFirst) {
            if (generalFunc.getJsonValue("eWalletBalanceAvailable", userProfileJson).equalsIgnoreCase("Yes")) {


                final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                generateAlert.setCancelable(false);
                generateAlert.setBtnClickList(btn_id -> {

                    if (btn_id == 1) {
                        eWalletDebitAllow = "Yes";
                        isWalletPopupFirst = true;
                        bookRide();
                    } else {
                        isWalletPopupFirst = true;
                        eWalletDebitAllow = "No";
                        bookRide();

                    }

                });


                if (getCurrentCabGeneralType().equalsIgnoreCase(Utils.CabGeneralType_Ride)) {

                    generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_WALLET_BALANCE_EXIST_RIDE").replace("####", generalFunc.getJsonValue("user_available_balance", userProfileJson)));
                } else if (getCurrentCabGeneralType().equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
                    generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_WALLET_BALANCE_EXIST_JOB").replace("####", generalFunc.getJsonValue("user_available_balance", userProfileJson)));
                } else {
                    generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_WALLET_BALANCE_EXIST_DELIVERY").replace("####", generalFunc.getJsonValue("user_available_balance", userProfileJson)));
                }
                generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_YES"));
                generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_NO"));
                generateAlert.showAlertBox();

                return;
            }
        }
        isWalletPopupFirst = false;

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "ScheduleARide");

        if (mainHeaderFrag != null) {
            if (!app_type.equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
                if (isUfx) {
                    parameters.put("pickUpLocAdd", pickUpLocationAddress);
                } else {
                    parameters.put("pickUpLocAdd", mainHeaderFrag != null ? mainHeaderFrag.getPickUpAddress() : "");
                }

            } else {
                parameters.put("pickUpLocAdd", pickUpLocationAddress);
            }
        }

        if (cabSelectionFrag != null && !cabSelectionFrag.iRentalPackageId.equalsIgnoreCase("")) {
            parameters.put("iRentalPackageId", cabSelectionFrag.iRentalPackageId);

        }
        parameters.put("iUserId", generalFunc.getMemberId());
        if (isUfx) {
            parameters.put("pickUpLatitude", getIntent().getStringExtra("latitude"));
            parameters.put("pickUpLongitude", getIntent().getStringExtra("longitude"));
        } else {
            parameters.put("pickUpLatitude", "" + getPickUpLocation().getLatitude());
            parameters.put("pickUpLongitude", "" + getPickUpLocation().getLongitude());
        }
        parameters.put("destLocAdd", getDestAddress());
        parameters.put("destLatitude", getDestLocLatitude());
        parameters.put("destLongitude", getDestLocLongitude());
        parameters.put("iCabBookingId", iCabBookingId);
        parameters.put("scheduleDate", selectedDateTime);
        parameters.put("iVehicleTypeId", getSelectedCabTypeId());
        parameters.put("SelectedDriverId", SelectedDriverId);
        // parameters.put("TimeZone", selectedDateTimeZone);
        parameters.put("CashPayment", "" + isCashSelected);
        parameters.put("eWalletDebitAllow", eWalletDebitAllow);
        // parameters.put("PickUpAddGeoCodeResult", tempPickupGeoCode);
        // parameters.put("DestAddGeoCodeResult", tempDestGeoCode);

        String handicapval = "";
        String femaleval = "";
        if (ishandicap) {
            handicapval = "Yes";


        } else {
            handicapval = "No";
        }
        if (isfemale) {
            femaleval = "Yes";

        } else {
            femaleval = "No";
        }

        parameters.put("HandicapPrefEnabled", handicapval);
        parameters.put("PreferFemaleDriverEnable", femaleval);
        parameters.put("vTollPriceCurrencyCode", tollcurrancy);
        String tollskiptxt = "";

        if (istollIgnore) {
            tollamount = 0;
            tollskiptxt = "Yes";

        } else {
            tollskiptxt = "No";
        }
        parameters.put("fTollPrice", tollamount + "");
        parameters.put("eTollSkipped", tollskiptxt);


        parameters.put("eType", getCurrentCabGeneralType());
        if (reqPickUpFrag != null) {
            parameters.put("PromoCode", reqPickUpFrag.getAppliedPromoCode());
        }

        if (cabSelectionFrag != null) {
            parameters.put("PromoCode", cabSelectionFrag.getAppliedPromoCode());
        }
        if (app_type.equalsIgnoreCase("UberX") || isUfx) {
            parameters.put("PromoCode", appliedPromoCode);
            parameters.put("eType", Utils.CabGeneralType_UberX);
            if (getIntent().getStringExtra("Quantity").equals("0")) {
                parameters.put("Quantity", "1");
            } else {
                parameters.put("Quantity", getIntent().getStringExtra("Quantity"));
            }

            parameters.put("iUserAddressId", getIntent().getStringExtra("iUserAddressId"));
            parameters.put("tUserComment", userComment);
            parameters.put("scheduleDate", SelectDate);
        } else {
            parameters.put("scheduleDate", selectedDateTime);
        }

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setCancelAble(false);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {


                if (generalFunc.getJsonValue(Utils.message_str, responseString).equals("DO_EMAIL_PHONE_VERIFY") ||
                        generalFunc.getJsonValue(Utils.message_str, responseString).equals("DO_PHONE_VERIFY") ||
                        generalFunc.getJsonValue(Utils.message_str, responseString).equals("DO_EMAIL_VERIFY")) {
                    Bundle bn = new Bundle();
                    bn.putString("msg", "" + generalFunc.getJsonValue(Utils.message_str, responseString));
                    //  bn.putString("UserProfileJson", userProfileJson);
                    accountVerificationAlert(generalFunc.retrieveLangLBl("", "LBL_ACCOUNT_VERIFY_ALERT_RIDER_TXT"), bn);

                    return;
                }

                String action = generalFunc.getJsonValue(Utils.action_str, responseString);

                if (action.equals("1")) {
                    setDestinationPoint("", "", "", false);
                    setDefaultView();
                    isrideschedule = false;

                    if (isRebooking) {


                        showBookingAlert();
                    } else {
                        showBookingAlert(generalFunc.retrieveLangLBl("",
                                generalFunc.getJsonValue(Utils.message_str, responseString)), false);
                    }

                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("",
                            generalFunc.getJsonValue(Utils.message_str, responseString)));
                }

            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    public void chatMsg() {
        Bundle bn = new Bundle();
        bn.putString("iFromMemberId", driverDetailFrag.getTripData().get("iDriverId"));
        bn.putString("FromMemberImageName", driverDetailFrag.getTripData().get("DriverImage"));
        bn.putString("iTripId", driverDetailFrag.getTripData().get("iTripId"));
        bn.putString("FromMemberName", driverDetailFrag.getTripData().get("DriverName"));

        new StartActProcess(getActContext()).startActWithData(ChatActivity.class, bn);
    }


    public void showBookingAlert() {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());

        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(btn_id -> {
            generateAlert.closeAlertBox();
            Bundle bn = new Bundle();
            bn.putBoolean("isrestart", true);
            new StartActProcess(getActContext()).startActWithData(HistoryActivity.class, bn);

            finish();
        });
        generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("Your selected booking has been updated.", "LBL_BOOKING_UPDATED"));
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));

        generateAlert.showAlertBox();
    }

    public void showBookingAlert(String message, boolean isongoing) {
        android.support.v7.app.AlertDialog alertDialog;
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        builder.setTitle("");
        builder.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_booking_view, null);
        builder.setView(dialogView);

        final MTextView titleTxt = (MTextView) dialogView.findViewById(R.id.titleTxt);
        final MTextView mesasgeTxt = (MTextView) dialogView.findViewById(R.id.mesasgeTxt);

        titleTxt.setText(generalFunc.retrieveLangLBl("Booking Successful", "LBL_BOOKING_ACCEPTED"));

        mesasgeTxt.setText(message);

        builder.setNegativeButton(generalFunc.retrieveLangLBl("Cancel", "LBL_CANCEL_TXT"), (dialog, which) -> {
            dialog.cancel();
            Bundle bn = new Bundle();
            new StartActProcess(getActContext()).startActWithData(MainActivity.class, bn);
            finishAffinity();
        });

        if (isongoing) {

            builder.setPositiveButton(generalFunc.retrieveLangLBl("", "LBL_VIEW_ON_GOING_TRIPS"), (dialog, which) -> {
                dialog.cancel();
            });

        } else {

            builder.setPositiveButton(generalFunc.retrieveLangLBl("Done", "LBL_VIEW_BOOKINGS"), (dialog, which) -> {
                dialog.cancel();
                Bundle bn = new Bundle();
                bn.putBoolean("isrestart", true);
                if (getIntent().getStringExtra("selType") != null) {
                    bn.putString("selType", getIntent().getStringExtra("selType"));
                }
                new StartActProcess(getActContext()).startActWithData(HistoryActivity.class, bn);
                finish();
            });
        }


        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    public void scheduleDelivery(Intent data) {


        if (!isWalletPopupFirst) {
            if (generalFunc.getJsonValue("eWalletBalanceAvailable", userProfileJson).equalsIgnoreCase("Yes")) {


                final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                generateAlert.setCancelable(false);
                generateAlert.setBtnClickList(btn_id -> {

                    if (btn_id == 1) {
                        eWalletDebitAllow = "Yes";
                        isWalletPopupFirst = true;
                        scheduleDelivery(data);
                    } else {
                        isWalletPopupFirst = true;
                        eWalletDebitAllow = "No";
                        scheduleDelivery(data);

                    }

                });


                if (getCurrentCabGeneralType().equalsIgnoreCase(Utils.CabGeneralType_Ride)) {

                    generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_WALLET_BALANCE_EXIST_RIDE").replace("####", generalFunc.getJsonValue("user_available_balance", userProfileJson)));
                } else if (getCurrentCabGeneralType().equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
                    generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_WALLET_BALANCE_EXIST_JOB").replace("####", generalFunc.getJsonValue("user_available_balance", userProfileJson)));
                } else {
                    generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_WALLET_BALANCE_EXIST_DELIVERY").replace("####", generalFunc.getJsonValue("user_available_balance", userProfileJson)));
                }
                generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_YES"));
                generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_NO"));
                generateAlert.showAlertBox();

                return;
            }
        }
        isWalletPopupFirst = false;


        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "ScheduleARide");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("pickUpLocAdd", mainHeaderFrag != null ? mainHeaderFrag.getPickUpAddress() : "");
        parameters.put("pickUpLatitude", "" + getPickUpLocation().getLatitude());
        parameters.put("pickUpLongitude", "" + getPickUpLocation().getLongitude());
        parameters.put("destLocAdd", getDestAddress());
        parameters.put("destLatitude", getDestLocLatitude());
        parameters.put("destLongitude", getDestLocLongitude());
        parameters.put("scheduleDate", selectedDateTime);
        parameters.put("iVehicleTypeId", getSelectedCabTypeId());
        //  parameters.put("TimeZone", selectedDateTimeZone);
        parameters.put("CashPayment", "" + isCashSelected);
        parameters.put("eType", "Deliver");
        parameters.put("eWalletDebitAllow", eWalletDebitAllow);

//        parameters.put("PickUpAddGeoCodeResult", tempPickupGeoCode);
//        parameters.put("DestAddGeoCodeResult", tempDestGeoCode);
        String data1 = generalFunc.retrieveValue(Utils.DELIVERY_DETAILS_KEY);
        JSONArray deliveriesArr = generalFunc.getJsonArray("deliveries", data1);
        if (deliveriesArr != null) {
            for (int j = 0; j < deliveriesArr.length(); j++) {
                JSONObject ja = generalFunc.getJsonObject(deliveriesArr, j);
                parameters.put("iPackageTypeId", generalFunc.getJsonValue(PACKAGE_TYPE_ID_KEY, ja.toString()));
            }
        }

        String tollskiptxt = "";

        if (istollIgnore) {
            tollskiptxt = "Yes";
            tollamount = 0;
        } else {
            tollskiptxt = "No";
        }
        parameters.put("fTollPrice", tollamount + "");
        parameters.put("vTollPriceCurrencyCode", tollcurrancy);
        parameters.put("eTollSkipped", tollskiptxt);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setCancelAble(false);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                if (generalFunc.getJsonValue(Utils.message_str, responseString).equals("DO_EMAIL_PHONE_VERIFY") ||
                        generalFunc.getJsonValue(Utils.message_str, responseString).equals("DO_PHONE_VERIFY") ||
                        generalFunc.getJsonValue(Utils.message_str, responseString).equals("DO_EMAIL_VERIFY")) {
                    Bundle bn = new Bundle();
                    bn.putString("msg", "" + generalFunc.getJsonValue(Utils.message_str, responseString));
                    accountVerificationAlert(generalFunc.retrieveLangLBl("", "LBL_ACCOUNT_VERIFY_ALERT_RIDER_TXT"), bn);
                    return;
                }

                String action = generalFunc.getJsonValue(Utils.action_str, responseString);

                if (action.equals("1")) {

                    generalFunc.removeValue(Utils.DELIVERY_DETAILS_KEY);
                    setDestinationPoint("", "", "", false);
                    setDefaultView();

                    if (isRebooking) {
                        showBookingAlert();
                    } else {
                        showBookingAlert(generalFunc.retrieveLangLBl("",
                                generalFunc.getJsonValue(Utils.message_str, responseString)), false);
                    }
                } else {
                    generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("",
                            generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();

    }

    public void deliverNow(Intent data) {

        this.deliveryData = data;


        requestPickUp();
    }

    public void requestPickUp() {


        if (!isWalletPopupFirst) {
            if (generalFunc.getJsonValue("eWalletBalanceAvailable", userProfileJson).equalsIgnoreCase("Yes")) {


                final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                generateAlert.setCancelable(false);
                generateAlert.setBtnClickList(btn_id -> {

                    if (btn_id == 1) {
                        eWalletDebitAllow = "Yes";
                        isWalletPopupFirst = true;
                        requestPickUp();
                    } else {
                        isWalletPopupFirst = true;
                        eWalletDebitAllow = "No";
                        requestPickUp();

                    }

                });


                if (getCurrentCabGeneralType().equalsIgnoreCase(Utils.CabGeneralType_Ride)) {

                    generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_WALLET_BALANCE_EXIST_RIDE").replace("####", generalFunc.getJsonValue("user_available_balance", userProfileJson)));
                } else if (getCurrentCabGeneralType().equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
                    generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_WALLET_BALANCE_EXIST_JOB").replace("####", generalFunc.getJsonValue("user_available_balance", userProfileJson)));
                } else {
                    generateAlert.setContentMessage("", generalFunc.retrieveLangLBl("", "LBL_WALLET_BALANCE_EXIST_DELIVERY").replace("####", generalFunc.getJsonValue("user_available_balance", userProfileJson)));
                }
                generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_YES"));
                generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_NO"));
                generateAlert.showAlertBox();

                return;
            }
        }
        isWalletPopupFirst = false;

        setLoadAvailCabTaskValue(true);
        requestNearestCab = new RequestNearestCab(getActContext(), generalFunc);
        requestNearestCab.run();

        String driverIds = getAvailableDriverIds();

        JSONObject cabRequestedJson = new JSONObject();
        try {
            cabRequestedJson.put("Message", "CabRequested");
            cabRequestedJson.put("sourceLatitude", "" + getPickUpLocation().getLatitude());
            cabRequestedJson.put("sourceLongitude", "" + getPickUpLocation().getLongitude());
            cabRequestedJson.put("PassengerId", generalFunc.getMemberId());
            cabRequestedJson.put("PName", generalFunc.getJsonValue("vName", userProfileJson) + " "
                    + generalFunc.getJsonValue("vLastName", userProfileJson));
            cabRequestedJson.put("PPicName", generalFunc.getJsonValue("vImgName", userProfileJson));
            cabRequestedJson.put("PFId", generalFunc.getJsonValue("vFbId", userProfileJson));
            cabRequestedJson.put("PRating", generalFunc.getJsonValue("vAvgRating", userProfileJson));
            cabRequestedJson.put("PPhone", generalFunc.getJsonValue("vPhone", userProfileJson));
            cabRequestedJson.put("PPhoneC", generalFunc.getJsonValue("vPhoneCode", userProfileJson));
            cabRequestedJson.put("REQUEST_TYPE", getCurrentCabGeneralType());


            cabRequestedJson.put("selectedCatType", vUberXCategoryName);
            if (getDestinationStatus() == true) {
                cabRequestedJson.put("destLatitude", "" + getDestLocLatitude());
                cabRequestedJson.put("destLongitude", "" + getDestLocLongitude());
            } else {
                cabRequestedJson.put("destLatitude", "");
                cabRequestedJson.put("destLongitude", "");
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (!generalFunc.getJsonValue("Message", cabRequestedJson.toString()).equals("")) {
            requestNearestCab.setRequestData(driverIds, cabRequestedJson.toString());

            if (DRIVER_REQUEST_METHOD.equals("All")) {
                sendReqToAll(driverIds, cabRequestedJson.toString());
            } else if (DRIVER_REQUEST_METHOD.equals("Distance") || DRIVER_REQUEST_METHOD.equals("Time")) {
                sendReqByDist(driverIds, cabRequestedJson.toString());
            } else {
                sendReqToAll(driverIds, cabRequestedJson.toString());
            }
        }


    }

    public void sendReqToAll(String driverIds, String cabRequestedJson) {
        isreqnow = true;
        sendRequestToDrivers(driverIds, cabRequestedJson);
        if (allCabRequestTask != null) {
            allCabRequestTask.stopRepeatingTask();
            allCabRequestTask = null;
        }

        int interval = generalFunc.parseIntegerValue(30, generalFunc.getJsonValue("RIDER_REQUEST_ACCEPT_TIME", generalFunc.retrieveValue(Utils.USER_PROFILE_JSON)));

        allCabRequestTask = new UpdateFrequentTask((interval + 5) * 1000);
        allCabRequestTask.startRepeatingTask();
        allCabRequestTask.setTaskRunListener(() -> {
            setRetryReqBtn(true);
            allCabRequestTask.stopRepeatingTask();
        });

    }

    public void sendReqByDist(String driverIds, String cabRequestedJson) {
        if (sendNotificationToDriverByDist == null) {
            sendNotificationToDriverByDist = new SendNotificationsToDriverByDist(driverIds, cabRequestedJson);
        } else {
            sendNotificationToDriverByDist.startRepeatingTask();
        }
    }

    public void setRetryReqBtn(boolean isVisible) {
        if (isVisible == true) {
            if (requestNearestCab != null) {
                requestNearestCab.setVisibilityOfRetryArea(View.VISIBLE);
            }
        } else {
            if (requestNearestCab != null) {
                requestNearestCab.setVisibilityOfRetryArea(View.GONE);
            }
        }
    }

    public void retryReqBtnPressed(String driverIds, String cabRequestedJson) {

        if (DRIVER_REQUEST_METHOD.equals("All")) {
            sendReqToAll(driverIds, cabRequestedJson.toString());
        } else if (DRIVER_REQUEST_METHOD.equals("Distance") || DRIVER_REQUEST_METHOD.equals("Time")) {
            sendReqByDist(driverIds, cabRequestedJson.toString());
        } else {
            sendReqToAll(driverIds, cabRequestedJson.toString());
        }

        setRetryReqBtn(false);
    }

    public void setLoadAvailCabTaskValue(boolean value) {
        if (loadAvailCabs != null) {
            loadAvailCabs.setTaskKilledValue(value);
        }
    }

    public void setCurrentLoadedDriverList(ArrayList<HashMap<String, String>> currentLoadedDriverList) {
        this.currentLoadedDriverList = currentLoadedDriverList;
        if (app_type.equalsIgnoreCase("UberX") || isUfx) {
            // load list here but wait for 5 seconds
            redirectToMapOrList(Utils.Cab_UberX_Type_List, true);

        }
    }

    public ArrayList<String> getDriverLocationChannelList() {

        ArrayList<String> channels_update_loc = new ArrayList<>();

        if (currentLoadedDriverList != null) {

            for (int i = 0; i < currentLoadedDriverList.size(); i++) {
                channels_update_loc.add(Utils.pubNub_Update_Loc_Channel_Prefix + "" + (currentLoadedDriverList.get(i).get("driver_id")));
            }

        }
        return channels_update_loc;
    }

    public ArrayList<String> getDriverLocationChannelList(ArrayList<HashMap<String, String>> listData) {

        ArrayList<String> channels_update_loc = new ArrayList<>();

        if (listData != null) {

            for (int i = 0; i < listData.size(); i++) {
                channels_update_loc.add(Utils.pubNub_Update_Loc_Channel_Prefix + "" + (listData.get(i).get("driver_id")));
            }

        }
        return channels_update_loc;
    }

    public String getAvailableDriverIds() {
        String driverIds = "";

        if (currentLoadedDriverList == null) {
            return driverIds;
        }

        ArrayList<HashMap<String, String>> finalLoadedDriverList = new ArrayList<HashMap<String, String>>();
        finalLoadedDriverList.addAll(currentLoadedDriverList);

        if (DRIVER_REQUEST_METHOD.equals("Distance")) {
            Collections.sort(finalLoadedDriverList, new HashMapComparator("DIST_TO_PICKUP"));
        }

        for (int i = 0; i < finalLoadedDriverList.size(); i++) {
            String iDriverId = finalLoadedDriverList.get(i).get("driver_id");

            driverIds = driverIds.equals("") ? iDriverId : (driverIds + "," + iDriverId);
        }

        return driverIds;
    }


    public void sendRequestToDrivers(String driverIds, String cabRequestedJson) {

        HashMap<String, String> requestCabData = new HashMap<String, String>();
        requestCabData.put("type", "sendRequestToDrivers");
        requestCabData.put("message", cabRequestedJson);
        requestCabData.put("userId", generalFunc.getMemberId());
        requestCabData.put("CashPayment", "" + isCashSelected);
        requestCabData.put("PickUpAddress", getPickUpLocationAddress());
        requestCabData.put("vTollPriceCurrencyCode", tollcurrancy);
        requestCabData.put("eWalletDebitAllow", eWalletDebitAllow);


        String tollskiptxt = "";

        if (istollIgnore) {
            tollamount = 0;
            tollskiptxt = "Yes";
        } else {
            tollskiptxt = "No";
        }

        requestCabData.put("fTollPrice", tollamount + "");
        requestCabData.put("eTollSkipped", tollskiptxt);

        requestCabData.put("eType", getCurrentCabGeneralType());

        if ((app_type.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX))) {
            if (isUfx) {
                requestCabData.put("eType", Utils.CabGeneralType_UberX);
                requestCabData.put("driverIds", generalFunc.retrieveValue(Utils.SELECTEDRIVERID));
            } else {
                requestCabData.put("driverIds", driverIds);
            }
        }

        if ((app_type.equalsIgnoreCase("UberX") || isUfx)) {
            requestCabData.put("driverIds", generalFunc.retrieveValue(Utils.SELECTEDRIVERID));
        } else {
            requestCabData.put("driverIds", driverIds);
        }

        requestCabData.put("SelectedCarTypeID", "" + selectedCabTypeId);
        requestCabData.put("DestLatitude", getDestLocLatitude());
        requestCabData.put("DestLongitude", getDestLocLongitude());
        requestCabData.put("DestAddress", getDestAddress());

        if (isUfx) {
            requestCabData.put("PickUpLatitude", getIntent().getStringExtra("latitude"));
            requestCabData.put("PickUpLongitude", getIntent().getStringExtra("longitude"));
        } else {
            requestCabData.put("PickUpLatitude", "" + getPickUpLocation().getLatitude());
            requestCabData.put("PickUpLongitude", "" + getPickUpLocation().getLongitude());
        }


        if ((app_type.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX))) {
            if (isUfx) {
                requestCabData.put("Quantity", getIntent().getStringExtra("Quantity"));
            }
        }

        if (app_type.equalsIgnoreCase("UberX") || isUfx) {
            requestCabData.put("PromoCode", appliedPromoCode);
            requestCabData.put("iUserAddressId", getIntent().getStringExtra("iUserAddressId"));
            requestCabData.put("tUserComment", userComment);

            if (getIntent().getStringExtra("Quantity").equals("0")) {
                requestCabData.put("Quantity", "1");
            } else {
                requestCabData.put("Quantity", getIntent().getStringExtra("Quantity"));
            }
        }

        if (cabSelectionFrag != null) {
            requestCabData.put("PromoCode", cabSelectionFrag.getAppliedPromoCode());
            if (!cabSelectionFrag.iRentalPackageId.equalsIgnoreCase("")) {
                requestCabData.put("iRentalPackageId", cabSelectionFrag.iRentalPackageId);
            }
        }

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), requestCabData);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken", generalFunc);
        exeWebServer.setCancelAble(false);

        exeWebServer.setDataResponseListener(responseString -> {

            if (cabSelectionFrag != null) {
                cabSelectionFrag.isclickableridebtn = false;
            }

            if (responseString != null && !responseString.equals("")) {

                generalFunc.sendHeartBeat();

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == false) {
                    Bundle bn = new Bundle();
                    bn.putString("msg", "" + generalFunc.getJsonValue(Utils.message_str, responseString));

                    String message = generalFunc.getJsonValue(Utils.message_str, responseString);

                    if (message.equals("SESSION_OUT")) {
                        closeRequestDialog(false);
                        MyApp.getInstance().notifySessionTimeOut();
                        Utils.runGC();
                        return;
                    }

                    if (message.equals("NO_CARS") && !DRIVER_REQUEST_METHOD.equalsIgnoreCase("ALL") && sendNotificationToDriverByDist != null) {
                        sendNotificationToDriverByDist.incTask();
                        return;

                    }
                    if (message.equals("NO_CARS") || message.equals("LBL_PICK_DROP_LOCATION_NOT_ALLOW")
                            || message.equals("LBL_DROP_LOCATION_NOT_ALLOW") || message.equals("LBL_PICKUP_LOCATION_NOT_ALLOW")) {
                        closeRequestDialog(false);
                        String messageLabel = "";

                        if (getCurrentCabGeneralType().equalsIgnoreCase(Utils.CabGeneralType_Ride)) {
                            messageLabel = "LBL_NO_CAR_AVAIL_TXT";

                        } else if (getCurrentCabGeneralType().equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
                            messageLabel = "LBL_NO_PROVIDERS_AVAIL_TXT";
                        } else {
                            messageLabel = "LBL_NO_CARRIERS_AVAIL_TXT";
                        }
                        buildMessage(generalFunc.retrieveLangLBl("", message.equals("NO_CARS") ? messageLabel : message),
                                generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), false);

                        if (loadAvailCabs != null) {
                            isufxbackview = false;
                            loadAvailCabs.onResumeCalled();
                        }

                    } else if (message.equals(Utils.GCM_FAILED_KEY) || message.equals(Utils.APNS_FAILED_KEY)) {
                        releaseScheduleNotificationTask();
                        generalFunc.restartApp();
                    } else if (generalFunc.getJsonValue(Utils.message_str, responseString).equals("DO_EMAIL_PHONE_VERIFY") ||
                            generalFunc.getJsonValue(Utils.message_str, responseString).equals("DO_PHONE_VERIFY") ||
                            generalFunc.getJsonValue(Utils.message_str, responseString).equals("DO_EMAIL_VERIFY")) {
                        closeRequestDialog(true);
                        accountVerificationAlert(generalFunc.retrieveLangLBl("", "LBL_ACCOUNT_VERIFY_ALERT_RIDER_TXT"), bn);

                        if (loadAvailCabs != null) {
                            isufxbackview = false;
                            loadAvailCabs.onResumeCalled();

                        }


                    }
//                    else {
//                        closeRequestDialog(false);
//                        buildMessage(generalFunc.retrieveLangLBl("", "LBL_REQUEST_FAILED_PROCESS"), generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), true);
//                    }

                }
            } else {
                if (reqSentErrorDialog != null) {
                    reqSentErrorDialog.closeAlertBox();
                    reqSentErrorDialog = null;
                }

                InternetConnection intConnection = new InternetConnection(getActContext());

                reqSentErrorDialog = generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("", intConnection.isNetworkConnected() ? "LBL_TRY_AGAIN_TXT" : "LBL_NO_INTERNET_TXT"), generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"), generalFunc.retrieveLangLBl("", "LBL_RETRY_TXT"), buttonId -> {
                    if (buttonId == 1) {
                        sendRequestToDrivers(driverIds, cabRequestedJson);
                    } else {
                        //Negative
                        closeRequestDialog(true);

                        MyApp.getInstance().restartWithGetDataApp();
                    }
                });
//                closeRequestDialog(true);
//                buildMessage(generalFunc.retrieveLangLBl("", "LBL_REQUEST_FAILED_PROCESS"), generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), false);
            }
        });
        exeWebServer.execute();

        generalFunc.sendHeartBeat();
    }

    public void accountVerificationAlert(String message, final Bundle bn) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(btn_id -> {
            if (btn_id == 1) {
                generateAlert.closeAlertBox();
                (new StartActProcess(getActContext())).startActForResult(VerifyInfoActivity.class, bn, Utils.VERIFY_INFO_REQ_CODE);
            } else if (btn_id == 0) {
                generateAlert.closeAlertBox();
            }
        });
        generateAlert.setContentMessage("", message);
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
        generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_CANCEL_TRIP_TXT"));
        generateAlert.showAlertBox();

    }

    public void closeRequestDialog(boolean isSetDefault) {
        if (requestNearestCab != null) {
            requestNearestCab.dismissDialog();
        }

        if (loadAvailCabs != null) {
            loadAvailCabs.selectProviderId = "";

        }

        if (!isDriverAssigned) {
            setLoadAvailCabTaskValue(false);
        }

        releaseScheduleNotificationTask();

        if (isSetDefault == true) {
            setDefaultView();
        }

    }

    public void releaseScheduleNotificationTask() {
        if (allCabRequestTask != null) {
            allCabRequestTask.stopRepeatingTask();
            allCabRequestTask = null;
        }

        if (sendNotificationToDriverByDist != null) {
            sendNotificationToDriverByDist.stopRepeatingTask();
            sendNotificationToDriverByDist = null;
        }
    }

    public DriverDetailFragment getDriverDetailFragment() {
        return driverDetailFrag;
    }

    public void buildMessage(String message, String positiveBtn, final boolean isRestart) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(btn_id -> {
            generateAlert.closeAlertBox();
            if (isRestart == true) {
                generalFunc.restartApp();
            } else if (!TextUtils.isEmpty(tripId) && eTripType.equals(Utils.CabGeneralType_Deliver)) {

                MyApp.getInstance().restartWithGetDataApp(tripId);
            }
        });
        generateAlert.setContentMessage("", message);
        generateAlert.setPositiveBtn(positiveBtn);
        generateAlert.showAlertBox();
    }


    public void onGcmMessageArrived(String message) {

        String driverMsg = generalFunc.getJsonValue("Message", message);

        if (!assignedTripId.equals("") && !generalFunc.getJsonValue("iTripId", message).equalsIgnoreCase("") && !generalFunc.getJsonValue("iTripId", message).equalsIgnoreCase(assignedTripId)) {
            return;
        }
        currentTripId = generalFunc.getJsonValue("iTripId", message);

        if (driverMsg.equals("CabRequestAccepted")) {
            if (isDriverAssigned == true) {
                return;
            }

            isDriverAssigned = true;
            addDrawer.setIsDriverAssigned(isDriverAssigned);
            userLocBtnImgView.setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) (userLocBtnImgView).getLayoutParams();
            params.bottomMargin = Utils.dipToPixels(getActContext(), 200);
            assignedDriverId = generalFunc.getJsonValue("iDriverId", message);
            assignedTripId = generalFunc.getJsonValue("iTripId", message);

            generalFunc.removeValue(Utils.DELIVERY_DETAILS_KEY);

            boolean isRestart = getIntent().getBooleanExtra("isRestart", true);
            if (app_type != null && app_type.equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX)) {

                if (!generalFunc.getJsonValue("eType", message).equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
                    MyApp.getInstance().restartWithGetDataApp();
                    return;
                }
            }
            if (generalFunc.isJSONkeyAvail("iCabBookingId", message) == true && !generalFunc.getJsonValue("iCabBookingId", message).trim().equals("")) {
                MyApp.getInstance().restartWithGetDataApp();
            } else {
                if (generalFunc.getJsonValue("eType", message).equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
                    isDriverAssigned = false;
                    pinImgView.setVisibility(View.GONE);
                    setDestinationPoint("", "", "", false);
                    closeRequestDialog(true);
                    showBookingAlert(generalFunc.retrieveLangLBl("", "LBL_ONGOING_TRIP_TXT"), true);
                } else {
                    configureAssignedDriver(false);
                    pinImgView.setVisibility(View.GONE);
                    closeRequestDialog(false);
                    configureDeliveryView(true);
                }
            }


            tripStatus = "Assigned";

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (userLocBtnImgView.getVisibility() == View.VISIBLE) {
                        userLocBtnImgView.performClick();
                    }
                }
            }, 1500);


        } else if (driverMsg.equals("TripEnd")) {
            if (isDriverAssigned == false) {
                return;
            }

            if (isTripEnded == true && isDriverAssigned == false) {
                generalFunc.restartApp();
                return;
            }

            if (isTripEnded) {
                return;
            }

            tripStatus = "TripEnd";
            if (driverAssignedHeaderFrag != null) {

                if ((!TextUtils.isEmpty(tripId) && (getCurrentCabGeneralType().equalsIgnoreCase(Utils.CabGeneralType_Deliver)))) {
                    isTripEnded = true;
                } else {
                    isTripEnded = true;
                }

                if (driverAssignedHeaderFrag != null) {
                    driverAssignedHeaderFrag.setTaskKilledValue(true);
                }
            }

        } else if (driverMsg.equals("TripStarted")) {
            try {
                if (isDriverAssigned == false) {
                    return;
                }

                if (isDriverAssigned == false && isTripStarted == true) {
                    generalFunc.restartApp();
                    return;
                }

                if (isTripStarted) {
                    return;
                }

                tripStatus = "TripStarted";


                isTripStarted = true;
                if (driverAssignedHeaderFrag != null) {
                    driverAssignedHeaderFrag.setTripStartValue(true);
                }
                if (driverAssignedHeaderFrag.sourceMarker != null) {
                    driverAssignedHeaderFrag.sourceMarker.remove();
                }

                if (driverDetailFrag != null) {
                    driverDetailFrag.configTripStartView(generalFunc.getJsonValue("VerificationCode", message));
                }
                userLocBtnImgView.performClick();
            } catch (Exception e) {

            }


        } else if (driverMsg.equals("DestinationAdded")) {
            if (isDriverAssigned == false) {
                return;
            }


            LocalNotification.dispatchLocalNotification(getActContext(), generalFunc.retrieveLangLBl("Destination is added by driver.", "LBL_DEST_ADD_BY_DRIVER"), true);

            buildMessage(generalFunc.retrieveLangLBl("Destination is added by driver.", "LBL_DEST_ADD_BY_DRIVER"), generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"), false);

            String destLatitude = generalFunc.getJsonValue("DLatitude", message);
            String destLongitude = generalFunc.getJsonValue("DLongitude", message);
            String destAddress = generalFunc.getJsonValue("DAddress", message);
            String eFlatTrip = generalFunc.getJsonValue("eFlatTrip", message);

            setDestinationPoint(destLatitude, destLongitude, destAddress, true);
            if (driverAssignedHeaderFrag != null) {
                driverAssignedHeaderFrag.setDestinationAddress(eFlatTrip);
                driverAssignedHeaderFrag.configDestinationView();
            }
        } else if (driverMsg.equals("TripCancelledByDriver")) {

            if (!generalFunc.getJsonValue("eType", message).equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
                if (isDriverAssigned == false) {
                    generalFunc.restartApp();
                    return;
                }
            }

            if (tripStatus.equals("TripCanelled")) {
                return;
            }

            tripStatus = "TripCanelled";
            if (driverAssignedHeaderFrag != null) {
                if (driverAssignedHeaderFrag != null) {
                    driverAssignedHeaderFrag.setTaskKilledValue(true);
                }
            }
        }
    }

    public DriverAssignedHeaderFragment getDriverAssignedHeaderFrag() {
        return driverAssignedHeaderFrag;
    }

    public void unSubscribeCurrentDriverChannels() {
        if (currentLoadedDriverList != null) {
            ConfigPubNub.getInstance().unSubscribeToChannels(getDriverLocationChannelList());
        }
    }

    public boolean isDeliver(String selctedType) {
        return (selctedType.equalsIgnoreCase(Utils.CabGeneralType_Deliver) || selctedType.equalsIgnoreCase("Deliver"));
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (loadAvailCabs != null) {
            loadAvailCabs.onPauseCalled();
        }

        if (driverAssignedHeaderFrag != null) {
            driverAssignedHeaderFrag.onPauseCalled();
        }

        unSubscribeCurrentDriverChannels();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (generalFunc.retrieveValue(Utils.ISWALLETBALNCECHANGE).equalsIgnoreCase("Yes")) {
            getWalletBalDetails();
        }

        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        obj_userProfile = generalFunc.getJsonObject(userProfileJson);

        setUserInfo();


        if (addDrawer != null) {
            addDrawer.userProfileJson = userProfileJson;
        }


        if (iswallet) {
            obj_userProfile = generalFunc.getJsonObject(userProfileJson);
            if (addDrawer != null) {
                addDrawer.changeUserProfileJson(userProfileJson);
            }
            iswallet = false;
        }

        if (addDrawer != null) {
            addDrawer.walletbalncetxt.setText(generalFunc.retrieveLangLBl("wallet Balance", "LBL_WALLET_BALANCE") + ": " + generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("user_available_balance", userProfileJson)));

        }

        if (!schedulrefresh) {
            if (loadAvailCabs != null) {

                loadAvailCabs.onResumeCalled();
//                if (cabSelectionFrag != null) {
//                    cabSelectionFrag.ride_now_btn.setEnabled(true);
//                    cabSelectionFrag.ride_now_btn.setTextColor(getResources().getColor(R.color.btn_text_color_type2));
//                    cabSelectionFrag.ride_now_btn.setClickable(true);
//                }
            }
        }
        app_type = generalFunc.getJsonValueStr("APP_TYPE", obj_userProfile);


        if (driverAssignedHeaderFrag != null) {
            driverAssignedHeaderFrag.onResumeCalled();
            pinImgView.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) (userLocBtnImgView).getLayoutParams();
            params.bottomMargin = Utils.dipToPixels(getActContext(), 200);
        }

        if (!isufxbackview) {

            if (currentLoadedDriverList != null) {
                ConfigPubNub.getInstance().subscribeToChannels(getDriverLocationChannelList());
            }
        }
    }

    public void setUserInfo() {
        View view = ((Activity) getActContext()).findViewById(android.R.id.content);
        ((MTextView) view.findViewById(R.id.userNameTxt)).setText(generalFunc.getJsonValueStr("vName", obj_userProfile) + " "
                + generalFunc.getJsonValueStr("vLastName", obj_userProfile));
        ((MTextView) view.findViewById(R.id.walletbalncetxt)).setText(generalFunc.retrieveLangLBl("", "LBL_WALLET_BALANCE") + ": " + generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("user_available_balance", obj_userProfile)));

        new AppFunctions(getActContext()).checkProfileImage((SelectableRoundedImageView) view.findViewById(R.id.userImgView), userProfileJson, "vImgName");
    }

    /*private void resetView(String from) {
        try {


            String vTripStatus = generalFunc.getJsonValueStr("vTripStatus", obj_userProfile);

            if (intCheck.isNetworkConnected() && intCheck.check_int()) {
                setNoLocViewEnableOrDisabled(false);
            }

            if (generalFunc.isLocationEnabled()) {

                if (noloactionview.getVisibility() == View.VISIBLE) {
                    noloactionview.setVisibility(View.GONE);
                    enableDisableViewGroup((RelativeLayout) findViewById(R.id.rootRelView), true);
                }


                if (from.equals("gps")) {
                    NoLocationView();
                }

            } else {
                if (vTripStatus != null && !vTripStatus.equals("Active") && !vTripStatus.equals("On Going Trip")) {

                    NoLocationView();
                }

            }

            if (driverAssignedHeaderFrag != null) {
                if (getMap() != null) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    getMap().setMyLocationEnabled(false);
                }
            }

            if (vTripStatus != null && !(vTripStatus.contains("Not Active") && !(vTripStatus.contains("NONE")))) {

                try {
                    if (!vTripStatus.contains("Not Requesting")) {
                        if (gMap != null) {

                        }
                    } else {
                        if (!isgpsview) {
                            NoLocationView();
                        }
                    }
                } catch (Exception e) {

                }


            } else {
                if (!isgpsview) {
                    NoLocationView();
                }

            }
        } catch (Exception e) {

        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            //  stopReceivingPrivateMsg();
            releaseScheduleNotificationTask();
            if (getLastLocation != null) {
                getLastLocation.stopLocationUpdates();
                getLastLocation = null;
            }

            if (gMap != null) {
                gMap.clear();
                gMap = null;
            }

            Utils.runGC();

        } catch (Exception e) {

        }

    }

    public void setDriverImgView(SelectableRoundedImageView driverImgView) {
        this.driverImgView = driverImgView;
    }

    public Bitmap getDriverImg() {

        try {
            if (driverImgView != null) {
                driverImgView.buildDrawingCache();
                Bitmap driverBitmap = driverImgView.getDrawingCache();

                if (driverBitmap != null) {
                    return driverBitmap;
                } else {
                    return BitmapFactory.decodeResource(getResources(), R.mipmap.ic_no_pic_user);
                }
            }

            return BitmapFactory.decodeResource(getResources(), R.mipmap.ic_no_pic_user);
        } catch (Exception e) {
            return BitmapFactory.decodeResource(getResources(), R.mipmap.ic_no_pic_user);
        }
    }

    public Bitmap getUserImg() {
        try {
            ((SelectableRoundedImageView) findViewById(R.id.userImgView)).buildDrawingCache();
            Bitmap userBitmap = ((SelectableRoundedImageView) findViewById(R.id.userImgView)).getDrawingCache();

            if (userBitmap != null) {
                return userBitmap;
            } else {
                return BitmapFactory.decodeResource(getResources(), R.mipmap.ic_no_pic_user);
            }
        } catch (Exception e) {
            return BitmapFactory.decodeResource(getResources(), R.mipmap.ic_no_pic_user);
        }

    }

    public void pubNubStatus(PNStatusCategory status) {

    }

    public void pubNubMsgArrived(final String message) {

        currentTripId = generalFunc.getJsonValue("iTripId", message);
        runOnUiThread(() -> {

            String msgType = generalFunc.getJsonValue("MsgType", message);

            if (msgType.equals("TripEnd")) {

                if (isDriverAssigned == false) {
                    generalFunc.restartApp();
                    return;
                }
            }
            if (msgType.equals("LocationUpdate")) {
                if (loadAvailCabs == null) {
                    return;
                }

                String iDriverId = generalFunc.getJsonValue("iDriverId", message);
                String vLatitude = generalFunc.getJsonValue("vLatitude", message);
                String vLongitude = generalFunc.getJsonValue("vLongitude", message);

                Marker driverMarker = getDriverMarkerOnPubNubMsg(iDriverId, false);

                LatLng driverLocation_update = new LatLng(generalFunc.parseDoubleValue(0.0, vLatitude),
                        generalFunc.parseDoubleValue(0.0, vLongitude));
                Location driver_loc = new Location("gps");
                driver_loc.setLatitude(driverLocation_update.latitude);
                driver_loc.setLongitude(driverLocation_update.longitude);

                if (driverMarker != null) {
                    float rotation = (float) SphericalUtil.computeHeading(driverMarker.getPosition(), driverLocation_update);

                    if (generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase("UberX") || isUfx) {
                        rotation = 0;
                    }

                    AnimateMarker.animateMarker(driverMarker, gMap, driver_loc, rotation, 1200);
                }

            } else if (msgType.equals("TripRequestCancel")) {

                tripStatus = "TripCanelled";
                if (TextUtils.isEmpty(tripId) && eTripType.equals(Utils.CabGeneralType_Deliver) && getCurrentCabGeneralType().equals(Utils.CabGeneralType_Deliver)) {
                    if (tripId.equalsIgnoreCase(currentTripId)) {
                        if (DRIVER_REQUEST_METHOD.equals("Distance") || DRIVER_REQUEST_METHOD.equals("Time")) {
                            if (sendNotificationToDriverByDist != null) {
                                sendNotificationToDriverByDist.incTask();
                            }
                        }
                    }
                } else {
                    if (DRIVER_REQUEST_METHOD.equals("Distance") || DRIVER_REQUEST_METHOD.equals("Time")) {
                        if (sendNotificationToDriverByDist != null) {
                            sendNotificationToDriverByDist.incTask();
                        }
                    }
                }
            } else if (msgType.equals("LocationUpdateOnTrip")) {

                if (!isDriverAssigned) {
                    return;
                }

                if (generalFunc.checkLocationPermission(true)) {
                    getMap().setMyLocationEnabled(false);
                }
                if (driverAssignedHeaderFrag != null) {
                    driverAssignedHeaderFrag.updateDriverLocation(message);
                }

            } else if (msgType.equals("DriverArrived")) {

                if (isDriverAssigned == false) {
                    generalFunc.restartApp();
                    return;
                }

                if (!generalFunc.getJsonValue("iTripId", message).equalsIgnoreCase("") && !generalFunc.getJsonValue("iTripId", message).equalsIgnoreCase(assignedTripId)) {
                    return;
                }

                tripStatus = "DriverArrived";
                if (driverAssignedHeaderFrag != null) {
                    driverAssignedHeaderFrag.isDriverArrived = true;
                    if (generalFunc.getJsonValue("eType", message).equalsIgnoreCase("Deliver")) {
                        driverAssignedHeaderFrag.setDriverStatusTitle(generalFunc.retrieveLangLBl("", "LBL_CARRIER_ARRIVED_TXT"));
                    } else {
                        driverAssignedHeaderFrag.setDriverStatusTitle(generalFunc.retrieveLangLBl("", "LBL_DRIVER_ARRIVED_TXT"));
                    }
                    gMap.clear();


                    if (driverAssignedHeaderFrag.updateDestMarkerTask != null) {
                        driverAssignedHeaderFrag.updateDestMarkerTask.stopRepeatingTask();
                        driverAssignedHeaderFrag.updateDestMarkerTask = null;
                        if (driverAssignedHeaderFrag.time_marker != null) {
                            driverAssignedHeaderFrag.time_marker.remove();
                            driverAssignedHeaderFrag.time_marker = null;
                        }
                        if (driverAssignedHeaderFrag.route_polyLine != null) {
                            driverAssignedHeaderFrag.route_polyLine.remove();
                        }
                    }
                    if (driverAssignedHeaderFrag.driverMarker != null) {
                        driverAssignedHeaderFrag.driverMarker.remove();
                        driverAssignedHeaderFrag.driverMarker = null;
                    }
                    if (driverAssignedHeaderFrag.driverData != null) {
                        driverAssignedHeaderFrag.driverData.get("DriverTripStatus");
                        driverAssignedHeaderFrag.driverData.put("DriverTripStatus", "Arrived");
                    }
                    driverAssignedHeaderFrag.configDriverLoc();
                    driverAssignedHeaderFrag.addPickupMarker();

                }

                userLocBtnImgView.performClick();

                if (driverAssignedHeaderFrag != null) {
                    if (driverAssignedHeaderFrag.isDriverArrived || driverAssignedHeaderFrag.isDriverArrivedNotGenerated) {
                        return;
                    }
                }

            } else {

                onGcmMessageArrived(message);

            }

        });

    }

    public Marker getDriverMarkerOnPubNubMsg(String iDriverId, boolean isRemoveFromList) {

        if (loadAvailCabs != null) {
            ArrayList<Marker> currentDriverMarkerList = loadAvailCabs.getDriverMarkerList();

            if (currentDriverMarkerList != null) {
                for (int i = 0; i < currentDriverMarkerList.size(); i++) {
                    Marker marker = currentDriverMarkerList.get(i);

                    String driver_id = marker.getTitle().replace("DriverId", "");

                    if (driver_id.equals(iDriverId)) {

                        if (isRemoveFromList) {
                            loadAvailCabs.getDriverMarkerList().remove(i);
                        }

                        return marker;
                    }

                }
            }
        }


        return null;
    }

    public Integer getDriverMarkerPosition(String iDriverId) {
        ArrayList<Marker> currentDriverMarkerList = loadAvailCabs.getDriverMarkerList();

        for (int i = 0; i < currentDriverMarkerList.size(); i++) {
            Marker marker = currentDriverMarkerList.get(i);
            String driver_id = marker.getTitle().replace("DriverId----------DriverId----------", "");
            if (driver_id.equals(iDriverId)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onBackPressed() {
        callBackEvent(false);
    }

    public void callBackEvent(boolean status) {
        try {

            if (pickUpLocSelectedFrag != null) {
                pickUpLocSelectedFrag = null;

                if (loadAvailCabs != null) {
                    loadAvailCabs.selectProviderId = "";
                    loadAvailCabs.changeCabs();
                }

                if (reqPickUpFrag != null) {
                    getSupportFragmentManager().beginTransaction().
                            remove(reqPickUpFrag).commit();
                    reqPickUpFrag = null;
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) (userLocBtnImgView).getLayoutParams();
                    params.bottomMargin = Utils.dipToPixels(getActContext(), 20);

                    ridelaterView.setVisibility(View.GONE);

                    isUfxRideLater = false;

                    isMarkerClickable = true;

                    try {
                        LinearLayout.LayoutParams paramsRide = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        paramsRide.gravity = Gravity.TOP;
                        ridelaterHandleView.setLayoutParams(paramsRide);
                    } catch (Exception e) {

                    }

                }

                if (mainHeaderFrag != null) {
                    mainHeaderFrag = null;
                }

                setMainHeaderView(false);
                // setDefaultView();
                return;

            }
            if (status) {
                if (requestNearestCab != null) {
                    requestNearestCab.dismissDialog();
                }

                releaseScheduleNotificationTask();
            }


            if (addDrawer.checkDrawerState(false)) {
                return;
            }

            if (cabSelectionFrag == null) {

            } else {
                MapAnimator.getInstance().stopRouteAnim();
                getSupportFragmentManager().beginTransaction().remove(cabSelectionFrag).commit();
                cabSelectionFrag = null;

                gMap.clear();


                configDestinationMode(false);

                isRental = false;
                if (loadAvailCabs != null) {
                    loadAvailCabs.changeCabs();
                }

                if (isMenuImageShow) {
                    mainHeaderFrag.menuImgView.setVisibility(View.VISIBLE);
                    mainHeaderFrag.backImgView.setVisibility(View.GONE);
                }

                mainHeaderFrag.handleDestAddIcon();
                cabTypesArrList.clear();
                //  mainHeaderFrag.setDestinationAddress(generalFunc.retrieveLangLBl("", "LBL_ADD_DESTINATION_BTN_TXT"));
                mainHeaderFrag.setDefaultView();

                pinImgView.setVisibility(View.GONE);
                if (loadAvailCabs != null) {
                    selectedCabTypeId = loadAvailCabs.getFirstCarTypeID();
                }
                resetUserLocBtnView();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) (userLocBtnImgView).getLayoutParams();
                params.bottomMargin = Utils.dipToPixels(getActContext(), 10);
                userLocBtnImgView.requestLayout();

                if (mainHeaderFrag != null) {
                    mainHeaderFrag.releaseAddressFinder();
                }

                resetMapView();

                if (pickUpLocation != null) {
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(this.pickUpLocation.getLatitude(), this.pickUpLocation.getLongitude()))
                            .zoom(Utils.defaultZomLevel).build();
                    getMap().moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } else if (userLocation != null) {
                    getMap().moveCamera(CameraUpdateFactory.newCameraPosition(cameraForUserPosition()));
                }
//                userLocBtnImgView.performClick();
                return;
            }

            super.onBackPressed();
        } catch (Exception e) {
            Log.e("Exception", "::" + e.toString());
        }
    }

    public Context getActContext() {
        return MainActivity.this;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add(0, 1, 0, "" + generalFunc.retrieveLangLBl("", "LBL_CALL_TXT"));
        menu.add(0, 2, 0, "" + generalFunc.retrieveLangLBl("", "LBL_MESSAGE_TXT"));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId() == 1) {

            try {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + driverDetailFrag.getDriverPhone()));
                startActivity(callIntent);
            } catch (Exception e) {
                // TODO: handle exception
            }

        } else if (item.getItemId() == 2) {

            try {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", "" + driverDetailFrag.getDriverPhone());
                startActivity(smsIntent);
            } catch (Exception e) {
                // TODO: handle exception
            }

        }

        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.MY_PROFILE_REQ_CODE && resultCode == RESULT_OK && data != null) {
            String userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
            obj_userProfile = generalFunc.getJsonObject(userProfileJson);
            this.userProfileJson = userProfileJson;
            addDrawer.changeUserProfileJson(this.userProfileJson);
        } else if (requestCode == Utils.VERIFY_INFO_REQ_CODE && resultCode == RESULT_OK && data != null) {

            String msgType = data.getStringExtra("MSG_TYPE");

            if (msgType.equalsIgnoreCase("EDIT_PROFILE")) {
                addDrawer.openMenuProfile();
            }
            this.userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
            obj_userProfile = generalFunc.getJsonObject(userProfileJson);
            addDrawer.userProfileJson = this.userProfileJson;
            addDrawer.obj_userProfile = generalFunc.getJsonObject(this.userProfileJson);
            addDrawer.buildDrawer();
        } else if (requestCode == Utils.VERIFY_INFO_REQ_CODE) {

            this.userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
            obj_userProfile = generalFunc.getJsonObject(userProfileJson);
            addDrawer.userProfileJson = this.userProfileJson;
            addDrawer.obj_userProfile = generalFunc.getJsonObject(this.userProfileJson);
            addDrawer.buildDrawer();
        } else if (requestCode == Utils.CARD_PAYMENT_REQ_CODE && resultCode == RESULT_OK && data != null) {

            iswallet = true;
            String userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
            obj_userProfile = generalFunc.getJsonObject(userProfileJson);
            this.userProfileJson = userProfileJson;

            if (cabSelectionFrag != null) {
                cabSelectionFrag.isCardValidated = true;
            }

            addDrawer.changeUserProfileJson(this.userProfileJson);
        } else if (requestCode == Utils.DELIVERY_DETAILS_REQ_CODE && resultCode == RESULT_OK && data != null) {
            try {
                if (!getCabReqType().equals(Utils.CabReqType_Later)) {
                    isdelivernow = true;
                } else {
                    isdeliverlater = true;
                }

                deliveryData = data;
                checkSurgePrice("", data);

            } catch (Exception e) {

            }
        } else if (requestCode == Utils.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                LatLng placeLocation = place.getLatLng();

                setDestinationPoint(placeLocation.latitude + "", placeLocation.longitude + "", place.getAddress().toString(), true);
                mainHeaderFrag.setDestinationAddress(place.getAddress().toString());

                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(placeLocation, Utils.defaultZomLevel);

                if (gMap != null) {
                    gMap.clear();
                    gMap.moveCamera(cu);
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);

                generalFunc.showMessage(generalFunc.getCurrentView(MainActivity.this),
                        status.getStatusMessage());
            } else if (requestCode == RESULT_CANCELED) {

            }


        } else if (requestCode == Utils.ASSIGN_DRIVER_CODE) {

        } else if (requestCode == Utils.REQUEST_CODE_GPS_ON) {

//            gpsEnabled();

        } else if (requestCode == Utils.SEARCH_PICKUP_LOC_REQ_CODE && resultCode == RESULT_OK && data != null && gMap != null) {

            if (data.getStringExtra("Address") != null) {
                pickUp_tmpAddress = data.getStringExtra("Address");
            }

            pickUp_tmpLatitude = generalFunc.parseDoubleValue(0.0, data.getStringExtra("Latitude"));
            pickUp_tmpLongitude = generalFunc.parseDoubleValue(0.0, data.getStringExtra("Longitude"));

            final Location location = new Location("gps");
            location.setLatitude(generalFunc.parseDoubleValue(0.0, data.getStringExtra("Latitude")));
            location.setLongitude(generalFunc.parseDoubleValue(0.0, data.getStringExtra("Longitude")));
            onLocationUpdate(location);

        } else if (requestCode == Utils.UFX_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {


                schedulrefresh = true;
                isufxbackview = true;
                ridelaterView.setVisibility(View.GONE);

                if (loadAvailCabs != null) {
                    loadAvailCabs.setTaskKilledValue(true);
                }

                appliedPromoCode = data.getStringExtra("promocode");
                userComment = data.getStringExtra("comment");

                if (data.getStringExtra("paymenttype").equalsIgnoreCase("cash")) {
                    isCashSelected = true;

                } else {
                    isCashSelected = false;

                }
                if (bookingtype.equals(Utils.CabReqType_Now)) {
                    requestPickUp();
                } else {
                    setRideSchedule();
                    bookRide();
                }
            } else {
                loadAvailCabs.selectProviderId = "";
            }
        } else if (requestCode == Utils.SCHEDULE_REQUEST_CODE && resultCode == RESULT_OK) {

            SelectDate = data.getStringExtra("SelectDate");
            sdate = data.getStringExtra("Sdate");
            Stime = data.getStringExtra("Stime");
//
            bookingtype = Utils.CabReqType_Later;

            uberXDriverListArea.setVisibility(View.VISIBLE);
            uberXNoDriverTxt.setVisibility(View.GONE);
            ridelaterView.setVisibility(View.GONE);
            (findViewById(R.id.driverListAreaLoader)).setVisibility(View.VISIBLE);
            (findViewById(R.id.searchingDriverTxt)).setVisibility(View.VISIBLE);

            if (loadAvailCabs != null) {
                loadAvailCabs.changeCabs();
            }
            schedulrefresh = false;

        } else if (requestCode == Utils.OTHER_AREA_CLICKED_CODE) {

            rideArea.performClick();
        } else if (requestCode == RENTAL_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null && !data.getStringExtra("iRentalPackageId").equalsIgnoreCase("")) {
                    cabSelectionFrag.iRentalPackageId = data.getStringExtra("iRentalPackageId");
                }

                if (cabRquestType.equalsIgnoreCase(Utils.CabReqType_Now)) {
                    continuePickUpProcess();
                } else {
                    checkSurgePrice(selectedTime, deliveryData);

                }
            }
        }
    }

    public void openPrefrancedailog() {


        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.activity_prefrance, null);

        final MTextView TitleTxt = (MTextView) dialogView.findViewById(R.id.TitleTxt);

        final CheckBox checkboxHandicap = (CheckBox) dialogView.findViewById(R.id.checkboxHandicap);
        final CheckBox checkboxFemale = (CheckBox) dialogView.findViewById(R.id.checkboxFemale);

        if (generalFunc.retrieveValue(Utils.HANDICAP_ACCESSIBILITY_OPTION).equalsIgnoreCase("yes")) {
            checkboxHandicap.setVisibility(View.VISIBLE);
        } else {
            checkboxHandicap.setVisibility(View.GONE);
        }

        if (generalFunc.retrieveValue(Utils.FEMALE_RIDE_REQ_ENABLE).equalsIgnoreCase("yes")) {
            if (!generalFunc.getJsonValue("eGender", userProfileJson).equalsIgnoreCase("Male")) {
                checkboxFemale.setVisibility(View.VISIBLE);
            } else {
                checkboxFemale.setVisibility(View.GONE);
            }
        } else {
            checkboxFemale.setVisibility(View.GONE);
        }
        if (isfemale) {
            checkboxFemale.setChecked(true);
        }

        if (ishandicap) {
            checkboxHandicap.setChecked(true);
        }
        MButton btn_type2 = btn_type2 = ((MaterialRippleLayout) dialogView.findViewById(R.id.btn_type2)).getChildView();
        int submitBtnId = Utils.generateViewId();
        btn_type2.setId(submitBtnId);
        btn_type2.setText(generalFunc.retrieveLangLBl("Update", "LBL_UPDATE"));
        btn_type2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref_dialog.dismiss();
                if (checkboxFemale.isChecked()) {
                    isfemale = true;

                } else {
                    isfemale = false;

                }
                if (checkboxHandicap.isChecked()) {
                    ishandicap = true;

                } else {
                    ishandicap = false;
                }

                if (loadAvailCabs != null) {
                    loadAvailCabs.changeCabs();
                }

            }
        });


        builder.setView(dialogView);
        TitleTxt.setText(generalFunc.retrieveLangLBl("Prefrance", "LBL_PREFRANCE_TXT"));
        checkboxHandicap.setText(generalFunc.retrieveLangLBl("Filter handicap accessibility drivers only", "LBL_MUST_HAVE_HANDICAP_ASS_CAR"));
        checkboxFemale.setText(generalFunc.retrieveLangLBl("Accept Female Only trip request", "LBL_ACCEPT_FEMALE_REQ_ONLY_PASSENGER"));


        pref_dialog = builder.create();
        if (generalFunc.isRTLmode() == true) {
            generalFunc.forceRTLIfSupported(pref_dialog);
        }
        pref_dialog.show();

    }

    public void getTollcostValue(final String driverIds, final String cabRequestedJson, final Intent data) {

        if (isFixFare) {
            setDeliverOrRideReq(driverIds, cabRequestedJson, data);
            return;
        }


        if (cabSelectionFrag != null) {
            if (cabSelectionFrag.isSkip) {
                setDeliverOrRideReq(driverIds, cabRequestedJson, data);
                return;
            }
        }

        if (generalFunc.retrieveValue(Utils.ENABLE_TOLL_COST).equalsIgnoreCase("Yes")) {

            String url = CommonUtilities.TOLLURL + generalFunc.retrieveValue(Utils.TOLL_COST_APP_ID)
                    + "&app_code=" + generalFunc.retrieveValue(Utils.TOLL_COST_APP_CODE) + "&waypoint0=" + getPickUpLocation().getLatitude()
                    + "," + getPickUpLocation().getLongitude() + "&waypoint1=" + getDestLocLatitude() + "," + getDestLocLongitude() + "&mode=fastest;car";

            ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), url, true);
            exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
            exeWebServer.setDataResponseListener(responseString -> {


                if (responseString != null && !responseString.equals("")) {

                    if (generalFunc.getJsonValue("onError", responseString).equalsIgnoreCase("FALSE")) {
                        try {

                            String costs = generalFunc.getJsonValue("costs", responseString);

                            //  String details=generalFunc.getJsonValue("details",c)

                            String currency = generalFunc.getJsonValue("currency", costs);
                            String details = generalFunc.getJsonValue("details", costs);
                            String tollCost = generalFunc.getJsonValue("tollCost", details);
                            if (!currency.equals("") && currency != null) {
                                tollcurrancy = currency;
                            }
                            tollamount = 0.0;
                            if (!tollCost.equals("") && tollCost != null && !tollCost.equals("0.0")) {
                                tollamount = generalFunc.parseDoubleValue(0.0, tollCost);
                            }


                            TollTaxDialog(driverIds, cabRequestedJson, data);


                        } catch (Exception e) {

                            TollTaxDialog(driverIds, cabRequestedJson, data);
                        }

                    } else {
                        TollTaxDialog(driverIds, cabRequestedJson, data);
                    }


                } else {
                    generalFunc.showError();
                }

            });
            exeWebServer.execute();


        } else {
            setDeliverOrRideReq(driverIds, cabRequestedJson, data);
        }

    }

    private void setDeliverOrRideReq(String driverIds, String cabRequestedJson, Intent data) {

        if (isDeliver(getCurrentCabGeneralType()) && isDeliver(app_type)) {
            // setDeliverySchedule();
        } else {

            if (app_type.equals(Utils.CabGeneralType_UberX)) {
                pickUpLocClicked();
            } else {

                if (getCabReqType().equals(Utils.CabReqType_Later)) {
                    isrideschedule = true;

                } else {
                    isreqnow = true;

                }
                // requestPickUp();
            }
        }


        if (data != null) {
            if (isdelivernow) {
                isdelivernow = false;
                deliverNow(data);
            } else if (isdeliverlater) {
                isdeliverlater = false;
                scheduleDelivery(data);
            }


        } else {
            if (isrideschedule) {
                isrideschedule = false;
                bookRide();
            } else if (isreqnow) {
                isreqnow = false;
                //sendRequestToDrivers(driverIds, cabRequestedJson);
                requestPickUp();
            }

        }
    }


    public void TollTaxDialog(final String driverIds, final String cabRequestedJson, final Intent data) {

        if (!isTollCostdilaogshow) {
            if (tollamount != 0.0 && tollamount != 0 && tollamount != 0.00) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());

                LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.dialog_tolltax, null);

                final MTextView tolltaxTitle = (MTextView) dialogView.findViewById(R.id.tolltaxTitle);
                final MTextView tollTaxMsg = (MTextView) dialogView.findViewById(R.id.tollTaxMsg);
                final MTextView tollTaxpriceTxt = (MTextView) dialogView.findViewById(R.id.tollTaxpriceTxt);
                final MTextView cancelTxt = (MTextView) dialogView.findViewById(R.id.cancelTxt);

                final CheckBox checkboxTolltax = (CheckBox) dialogView.findViewById(R.id.checkboxTolltax);

                checkboxTolltax.setOnCheckedChangeListener((buttonView, isChecked) -> {

                    if (checkboxTolltax.isChecked()) {
                        istollIgnore = true;
                    } else {
                        istollIgnore = false;
                    }

                });


                MButton btn_type2 = ((MaterialRippleLayout) dialogView.findViewById(R.id.btn_type2)).getChildView();
                int submitBtnId = Utils.generateViewId();
                btn_type2.setId(submitBtnId);
                btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_CONTINUE_BTN"));
                btn_type2.setOnClickListener(v -> {
                    tolltax_dialog.dismiss();
                    isTollCostdilaogshow = true;
                    setDeliverOrRideReq(driverIds, cabRequestedJson, data);


                });


                builder.setView(dialogView);
                tolltaxTitle.setText(generalFunc.retrieveLangLBl("", "LBL_TOLL_ROUTE"));
                tollTaxMsg.setText(generalFunc.retrieveLangLBl("", "LBL_TOLL_PRICE_DESC"));

                tollTaxMsg.setText(generalFunc.retrieveLangLBl("", "LBL_TOLL_PRICE_DESC"));

                String payAmount = payableAmount;
                if (cabSelectionFrag != null && cabTypeList != null && cabTypeList.size() > 0 && cabTypeList.get(cabSelectionFrag.selpos).get("total_fare") != null && !cabTypeList.get(cabSelectionFrag.selpos).get("total_fare").equals("") && !cabTypeList.get(cabSelectionFrag.selpos).get("eRental").equals("Yes") /*&& payAmount.equalsIgnoreCase("")*/) {
                    try {
                        payAmount = generalFunc.convertNumberWithRTL(cabTypeList.get(cabSelectionFrag.selpos).get("total_fare"));
                    } catch (Exception e) {

                    }
                }

                if (payAmount.equalsIgnoreCase("")) {
                    tollTaxpriceTxt.setText(generalFunc.retrieveLangLBl("Total toll price", "LBL_TOLL_PRICE_TOTAL") + ": " + tollcurrancy + " " + tollamount);
                } else {
                    tollTaxpriceTxt.setText(generalFunc.retrieveLangLBl(
                            "Current Fare", "LBL_CURRENT_FARE") + ": " + payAmount + "\n" + "+" + "\n" +
                            generalFunc.retrieveLangLBl("Total toll price", "LBL_TOLL_PRICE_TOTAL") + ": " + tollcurrancy + " " + tollamount);
                }

                checkboxTolltax.setText(generalFunc.retrieveLangLBl("", "LBL_IGNORE_TOLL_ROUTE"));
                cancelTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CANCEL_TXT"));

                cancelTxt.setOnClickListener(v -> {
                    tolltax_dialog.dismiss();
                    isreqnow = false;
                    // cabSelectionFrag.ride_now_btn.setEnabled(true);
                    // cabSelectionFrag.ride_now_btn.setClickable(true);

                    // closeRequestDialog(true);
                });


                tolltax_dialog = builder.create();
                if (generalFunc.isRTLmode() == true) {
                    generalFunc.forceRTLIfSupported(tolltax_dialog);
                }
                tolltax_dialog.setCancelable(false);
                tolltax_dialog.show();
            } else {
                setDeliverOrRideReq(driverIds, cabRequestedJson, data);
            }
        } else {
            setDeliverOrRideReq(driverIds, cabRequestedJson, data);

        }
    }

    public void callgederApi(String egender)

    {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "updateUserGender");
        parameters.put("UserType", Utils.userType);
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("eGender", egender);


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);


            String message = generalFunc.getJsonValue(Utils.message_str, responseString);
            if (isDataAvail) {
                generalFunc.storeData(Utils.USER_PROFILE_JSON, message);
                userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
                obj_userProfile = generalFunc.getJsonObject(userProfileJson);
                prefBtnImageView.performClick();
            }


        });
        exeWebServer.execute();
    }

    public void genderDailog() {
        closeDrawer();
        final Dialog builder = new Dialog(getActContext(), R.style.Theme_Dialog);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setContentView(R.layout.gender_view);
        builder.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        final MTextView genderTitleTxt = (MTextView) builder.findViewById(R.id.genderTitleTxt);
        final MTextView maleTxt = (MTextView) builder.findViewById(R.id.maleTxt);
        final MTextView femaleTxt = (MTextView) builder.findViewById(R.id.femaleTxt);
        final ImageView gendercancel = (ImageView) builder.findViewById(R.id.gendercancel);
        final ImageView gendermale = (ImageView) builder.findViewById(R.id.gendermale);
        final ImageView genderfemale = (ImageView) builder.findViewById(R.id.genderfemale);
        final LinearLayout male_area = (LinearLayout) builder.findViewById(R.id.male_area);
        final LinearLayout female_area = (LinearLayout) builder.findViewById(R.id.female_area);

        genderTitleTxt.setText(generalFunc.retrieveLangLBl("Select your gender to continue", "LBL_SELECT_GENDER"));
        maleTxt.setText(generalFunc.retrieveLangLBl("Male", "LBL_MALE_TXT"));
        femaleTxt.setText(generalFunc.retrieveLangLBl("FeMale", "LBL_FEMALE_TXT"));

        gendercancel.setOnClickListener(v -> builder.dismiss());

        male_area.setOnClickListener(v -> {
            callgederApi("Male");
            builder.dismiss();

        });
        female_area.setOnClickListener(v -> {
            callgederApi("Female");
            builder.dismiss();

        });

        builder.show();

    }

    private void prefrenceButtonEnable() {
        if (generalFunc.retrieveValue(Utils.FEMALE_RIDE_REQ_ENABLE).equalsIgnoreCase("No") &&
                generalFunc.retrieveValue(Utils.HANDICAP_ACCESSIBILITY_OPTION).equalsIgnoreCase("No")) {
            prefBtnImageView.setVisibility(View.GONE);

        } else if (generalFunc.retrieveValue(Utils.HANDICAP_ACCESSIBILITY_OPTION).equalsIgnoreCase("No") &&
                !generalFunc.retrieveValue(Utils.FEMALE_RIDE_REQ_ENABLE).equalsIgnoreCase("Yes")
                || (generalFunc.retrieveValue(Utils.FEMALE_RIDE_REQ_ENABLE).equalsIgnoreCase("Yes") &&
                generalFunc.getJsonValue("eGender", userProfileJson).equals("Male")
                && !generalFunc.retrieveValue(Utils.HANDICAP_ACCESSIBILITY_OPTION).equalsIgnoreCase("Yes"))) {
            prefBtnImageView.setVisibility(View.GONE);
        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard(getActContext());
            if (i == userLocBtnImgView.getId()) {
                moveToCurrentLoc();

            } else if (i == userTripBtnImgView.getId()) {

                if (!isUserTripClick) {
                    isUserTripClick = true;
                    userTripBtnImgView.setColorFilter(getActContext().getResources().getColor(R.color.btnnavtripselcolor));
                    if (driverAssignedHeaderFrag != null && driverAssignedHeaderFrag.tempdriverLocation_update != null) {
                        animateToLocation(driverAssignedHeaderFrag.tempdriverLocation_update.latitude, driverAssignedHeaderFrag.tempdriverLocation_update.longitude, Utils.defaultZomLevel);
                    }
                } else {
                    isUserTripClick = false;
                    userTripBtnImgView.setColorFilter(getActContext().getResources().getColor(R.color.black));
                }
            } else if (i == emeTapImgView.getId()) {
                Bundle bn = new Bundle();
                bn.putString("UserProfileJson", userProfileJson);
                bn.putString("TripId", assignedTripId);
                new StartActProcess(getActContext()).startActWithData(ConfirmEmergencyTapActivity.class, bn);
            } else if (i == rideArea.getId()) {
                ((ImageView) findViewById(R.id.rideImg)).setImageResource(R.mipmap.ride_on);
                rideImgViewsel.setVisibility(View.VISIBLE);
                ((MTextView) findViewById(R.id.selrideTxt)).setVisibility(View.VISIBLE);
                ((MTextView) findViewById(R.id.rideTxt)).setVisibility(View.GONE);
                rideImgView.setVisibility(View.GONE);
                deliverImgView.setVisibility(View.VISIBLE);
                deliverImgViewsel.setVisibility(View.GONE);
                otherImageView.setVisibility(View.VISIBLE);
                otherImageViewsel.setVisibility(View.GONE);

                ((ImageView) findViewById(R.id.deliverImg)).setImageResource(R.mipmap.delivery_off);
                ((MTextView) findViewById(R.id.rideTxt)).setTextColor(Color.parseColor("#000000"));
                ((MTextView) findViewById(R.id.deliverTxt)).setTextColor(Color.parseColor("#000000"));

                RideDeliveryType = Utils.CabGeneralType_Ride;
                prefBtnImageView.setVisibility(View.VISIBLE);
                prefrenceButtonEnable();

                if (cabSelectionFrag != null) {
                    cabSelectionFrag.changeCabGeneralType(Utils.CabGeneralType_Ride);
                    cabSelectionFrag.currentCabGeneralType = Utils.CabGeneralType_Ride;

                    if (cabSelectionFrag.cabTypeList != null) {
                        cabSelectionFrag.cabTypeList.clear();
                        cabSelectionFrag.adapter.notifyDataSetChanged();
                    }
                }

                if (loadAvailCabs != null) {
                    loadAvailCabs.checkAvailableCabs();
                }

            } else if (i == deliverArea.getId()) {

                rideImgViewsel.setVisibility(View.GONE);
                ((MTextView) findViewById(R.id.selrideTxt)).setVisibility(View.GONE);
                ((MTextView) findViewById(R.id.rideTxt)).setVisibility(View.VISIBLE);
                rideImgView.setVisibility(View.VISIBLE);
                deliverImgView.setVisibility(View.GONE);
                deliverImgViewsel.setVisibility(View.VISIBLE);
                otherImageView.setVisibility(View.VISIBLE);
                otherImageViewsel.setVisibility(View.GONE);

                ((ImageView) findViewById(R.id.rideImg)).setImageResource(R.mipmap.ride_off);
                ((ImageView) findViewById(R.id.deliverImg)).setImageResource(R.mipmap.delivery_on);

                ((MTextView) findViewById(R.id.rideTxt)).setTextColor(Color.parseColor("#000000"));

                ((MTextView) findViewById(R.id.deliverTxt)).setTextColor(Color.parseColor("#000000"));

                RideDeliveryType = Utils.CabGeneralType_Deliver;

                isfemale = false;
                ishandicap = false;
                prefBtnImageView.setVisibility(View.GONE);

                if (cabSelectionFrag != null) {
                    cabSelectionFrag.changeCabGeneralType(Utils.CabGeneralType_Deliver);
                    cabSelectionFrag.currentCabGeneralType = Utils.CabGeneralType_Deliver;

                    if (cabSelectionFrag.cabTypeList != null) {
                        cabSelectionFrag.cabTypeList.clear();
                        cabSelectionFrag.adapter.notifyDataSetChanged();
                    }
                }

                if (loadAvailCabs != null) {
                    loadAvailCabs.checkAvailableCabs();
                }

            } else if (i == otherArea.getId()) {
                rideImgViewsel.setVisibility(View.GONE);
                ((MTextView) findViewById(R.id.selrideTxt)).setVisibility(View.GONE);
                ((MTextView) findViewById(R.id.rideTxt)).setVisibility(View.VISIBLE);
                rideImgView.setVisibility(View.VISIBLE);
                deliverImgView.setVisibility(View.VISIBLE);
                deliverImgViewsel.setVisibility(View.GONE);
                otherImageView.setVisibility(View.GONE);
                otherImageViewsel.setVisibility(View.VISIBLE);


                RideDeliveryType = Utils.CabGeneralType_UberX;
                if (cabSelectionFrag != null) {
                    cabSelectionFrag.changeCabGeneralType(Utils.CabGeneralType_UberX);
                    cabSelectionFrag.currentCabGeneralType = Utils.CabGeneralType_UberX;

                }
            } else if (i == prefBtnImageView.getId()) {

                userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
                obj_userProfile = generalFunc.getJsonObject(userProfileJson);
                if (generalFunc.retrieveValue(Utils.FEMALE_RIDE_REQ_ENABLE).equalsIgnoreCase("Yes") && generalFunc.getJsonValue("eGender", userProfileJson).equals("")) {
                    genderDailog();

                } else {
                    openPrefrancedailog();
                }
            } else if (i == backImgView.getId()) {
                onBackPressed();
            } else if (i == filterTxtView.getId()) {
                openFilterDilaog();
            }
        }


    }


    public void openFilterDilaog() {

        // if (cabTypeList.get(pos).get("total_fare") != null && !cabTypeList.get(pos).get("total_fare").equalsIgnoreCase("")) {

        final BottomSheetDialog faredialog = new BottomSheetDialog(getActContext());


        View contentView = View.inflate(getActContext(), R.layout.dialog_filter, null);


        faredialog.setContentView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                Utils.dpToPx(getActContext(), 200)));
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) contentView.getParent());
        mBehavior.setPeekHeight(Utils.dpToPx(getActContext(), 200));

        MTextView menuTitle = (MTextView) faredialog.findViewById(R.id.menuTitle);
        MTextView closeTxt = (MTextView) faredialog.findViewById(R.id.closeTxt);
        MTextView TitleTxt = (MTextView) faredialog.findViewById(R.id.TitleTxt);
        LinearLayout detailsArea = (LinearLayout) faredialog.findViewById(R.id.detailsArea);
        TitleTxt.setText("sort by");
        // menuTitle.setText(generalFunc.retrieveLangLBl("Sort By", "LBL_SORT_BY"));
        // TitleTxt.setText(generalFunc.retrieveLangLBl("Sort By", "LBL_SORT_BY"));
        closeTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CLOSE_TXT"));

        menuTitle.setText(generalFunc.retrieveLangLBl("", "LBL_SORT_BY_TXT"));
        TitleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SORT_BY_TXT"));


        if (detailsArea.getChildCount() > 0) {
            detailsArea.removeAllViewsInLayout();
        }

        ArrayList<String> sortby_List = new ArrayList<String>();
        sortby_List.add(generalFunc.retrieveLangLBl("", "LBL_FEATURED_TXT"));
        sortby_List.add(generalFunc.retrieveLangLBl("", "LBL_NEAR_BY_TXT"));
        sortby_List.add(generalFunc.retrieveLangLBl("", "LBL_RATING"));


        for (int i = 0; i < sortby_List.size(); i++) {
            int pos = i;
            LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_sort, null);
            MTextView rowTitleTxtView = (MTextView) view.findViewById(R.id.rowTitleTxtView);

            rowTitleTxtView.setText(sortby_List.get(i));

            if (!selectedSortValue.equals("") && selectedSortValue.equals(sortby_List.get(i))) {
                rowTitleTxtView.setTextColor(getActContext().getResources().getColor(R.color.appThemeColor_1));
                rowTitleTxtView.setTypeface(rowTitleTxtView.getTypeface(), Typeface.BOLD);
            }

            rowTitleTxtView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    faredialog.dismiss();

                    filterTxtView.setText(rowTitleTxtView.getText().toString());

                    if (pos == 0) {
                        selectedSort = "eIsFeatured";
                        selectedSortValue = sortby_List.get(0);
                    } else if (pos == 1) {
                        selectedSort = "distance";
                        selectedSortValue = sortby_List.get(1);
                    } else if (pos == 2) {
                        selectedSort = "vAvgRating";
                        selectedSortValue = sortby_List.get(2);
                    }

                    if (loadAvailCabs != null) {
                        loadAvailCabs.sortby = selectedSort;
                        loadAvailCabs.changeCabs();


                        // loadAvailCabs.sortby = selectedSort;
                        //  loadAvailCabs.checkAvailableCabs();
                    }
                }
            });
            detailsArea.addView(view);
        }

        closeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faredialog.dismiss();
            }
        });

        faredialog.show();
    }

    private void moveToCurrentLoc() {
        if (!generalFunc.isLocationEnabled()) {
            generalFunc.showGeneralMessage("", generalFunc.retrieveLangLBl("Please enable you GPS location service", "LBL_GPSENABLE_TXT"));
            return;
        }

        isUserLocbtnclik = true;

        if (cabSelectionFrag == null) {

            if (driverAssignedHeaderFrag != null) {
                if (driverAssignedHeaderFrag.sourceMarker != null) {
                    driverAssignedHeaderFrag.sourceMarker.remove();
                    driverAssignedHeaderFrag.sourceMarker = null;
                }

                if (driverAssignedHeaderFrag.destinationPointMarker_temp != null) {
                    driverAssignedHeaderFrag.destinationPointMarker_temp.remove();
                    driverAssignedHeaderFrag.destinationPointMarker_temp = null;
                }
            }

            if (isDriverAssigned && !isTripStarted && driverAssignedHeaderFrag != null) {
                //driver topickup
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                if (driverAssignedHeaderFrag.driverMarker != null) {
                    builder.include(driverAssignedHeaderFrag.driverMarker.getPosition());
                }
                if (driverAssignedHeaderFrag.time_marker != null) {
                    builder.include(driverAssignedHeaderFrag.time_marker.getPosition());
                } else {
                    driverAssignedHeaderFrag.addPickupMarker();
                    if (driverAssignedHeaderFrag.sourceMarker != null) {
                        builder.include(driverAssignedHeaderFrag.sourceMarker.getPosition());
                    }
                }

                if (driverAssignedHeaderFrag.driverMarker != null) {
                    LatLngBounds bounds = builder.build();

                    LatLng center = bounds.getCenter();
                    LatLng northEast = SphericalUtil.computeOffset(center, 10 * Math.sqrt(2.0), SphericalUtil.computeHeading(center, bounds.northeast));
                    LatLng southWest = SphericalUtil.computeOffset(center, 10 * Math.sqrt(2.0), (180 + (180 + SphericalUtil.computeHeading(center, bounds.southwest))));
                    builder.include(southWest);
                    builder.include(northEast);


                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    int width = metrics.widthPixels;
                    int height = metrics.heightPixels;
                    int padding = (int) (width * 0.25); // offset from edges of the map 10% of screen

                    /*  Method 3 */
                    int screenWidth = getResources().getDisplayMetrics().widthPixels;
                    int screenHeight = getResources().getDisplayMetrics().heightPixels;
                    padding = (int) ((height - ((driverAssignedHeaderFrag.fragmentHeight + 5) + Utils.dipToPixels(getActContext(), 170))) / (4.5));
                    Utils.printELog("MapHeight", "cameraUpdate" + padding);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,
                            screenWidth, screenHeight, padding);

                    float maxZoomLevel = gMap.getMaxZoomLevel();

                    try {
                        gMap.setPadding(0, 320, 0, sliding_layout.getPanelHeight() + 5);
                        gMap.setMaxZoomPreference(maxZoomLevel - 5);
                        int finalPadding = padding;
                        gMap.animateCamera(cameraUpdate, new GoogleMap.CancelableCallback() {
                            @Override
                            public void onFinish() {
                                try {

                                    // gMap.animateCamera(CameraUpdateFactory.scrollBy(0, Utils.dpToPx(getActContext(), -200)));
                                    gMap.setMaxZoomPreference(maxZoomLevel);
                                    gMap.setPadding(0, 0, 0, sliding_layout.getPanelHeight() + 5);
                                } catch (Exception e) {
                                    Utils.printLog("ExceptionGMapAnim", ":onFinish:IF:" + e.getMessage().toString());

                                }

                            }

                            @Override
                            public void onCancel() {

                                try {
                                    gMap.setMaxZoomPreference(maxZoomLevel);
                                    gMap.setPadding(0, 0, 0, sliding_layout.getPanelHeight() + 5);

                                    gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), width - Utils.dipToPixels(getActContext(), 80), height - ((driverAssignedHeaderFrag.fragmentHeight + 5) + Utils.dipToPixels(getActContext(), 170)), finalPadding));

                                } catch (Exception e) {
                                    Utils.printLog("ExceptionGMapAnim", ":OnCancel:IF:" + e.getMessage().toString());

                                }
                            }
                        });
                    } catch (Exception e) {
                        Utils.printLog("ExceptionGMapAnim", ":MainSubCatch:IF:" + e.getMessage().toString());

                        try {
                            gMap.setMaxZoomPreference(maxZoomLevel);
                            gMap.setPadding(0, 0, 0, sliding_layout.getPanelHeight() + 5);

                            gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), width - Utils.dipToPixels(getActContext(), 80), height - ((driverAssignedHeaderFrag.fragmentHeight + 5) + Utils.dipToPixels(getActContext(), 170)), padding));

                        } catch (Exception e1) {
                            Utils.printLog("ExceptionGMapAnim", ":MainSubCatch:IF:" + e1.getMessage().toString());

                        }
                    }
                }


            } else if (isDriverAssigned && isTripStarted && driverAssignedHeaderFrag != null) {
                //driver to dest;
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                if (driverAssignedHeaderFrag.driverMarker != null) {
                    builder.include(driverAssignedHeaderFrag.driverMarker.getPosition());
                }
                if (driverAssignedHeaderFrag.destLocation != null) {
                    builder.include(driverAssignedHeaderFrag.destLocation);
                }
                if (driverAssignedHeaderFrag.driverMarker != null) {
                    LatLngBounds bounds = builder.build();

                    LatLng center = bounds.getCenter();
                    LatLng northEast = SphericalUtil.computeOffset(center, 10 * Math.sqrt(2.0), SphericalUtil.computeHeading(center, bounds.northeast));
                    LatLng southWest = SphericalUtil.computeOffset(center, 10 * Math.sqrt(2.0), (180 + (180 + SphericalUtil.computeHeading(center, bounds.southwest))));
                    builder.include(southWest);
                    builder.include(northEast);


                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    int width = metrics.widthPixels;
                    int height = metrics.heightPixels;
                    int padding = (int) (width * 0.25); // offset from edges of the map 10% of screen

                    /*  Method 3 */
                    int screenWidth = getResources().getDisplayMetrics().widthPixels;
                    int screenHeight = getResources().getDisplayMetrics().heightPixels;
                    padding = (int) ((height - ((driverAssignedHeaderFrag.fragmentHeight + 5) + Utils.dipToPixels(getActContext(), 170))) / (4.5));
                    Utils.printELog("MapHeight", "cameraUpdate" + padding);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,
                            screenWidth, screenHeight, padding);


                    float maxZoomLevel = gMap.getMaxZoomLevel();

                    try {


                        gMap.setPadding(0, 320, 0, sliding_layout.getPanelHeight() + 5);

                        gMap.setMaxZoomPreference(maxZoomLevel - 5);
                        int finalPadding = padding;
                        gMap.animateCamera(cameraUpdate, new GoogleMap.CancelableCallback() {
                            @Override
                            public void onFinish() {
                                try {

//                                        gMap.animateCamera(CameraUpdateFactory.scrollBy(0, Utils.dpToPx(getActContext(), -200)));
                                    gMap.setMaxZoomPreference(maxZoomLevel);
                                    gMap.setPadding(0, 0, 0, sliding_layout.getPanelHeight() + 5);
                                } catch (Exception e) {
                                    Utils.printLog("ExceptionGMapAnim", ":OnFinish:" + e.getMessage().toString());

                                }
                            }

                            @Override
                            public void onCancel() {
                                try {

                                    gMap.setMaxZoomPreference(maxZoomLevel);
                                    gMap.setPadding(0, 0, 0, sliding_layout.getPanelHeight() + 5);
                                    gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), width - Utils.dipToPixels(getActContext(), 80), height - ((driverAssignedHeaderFrag.fragmentHeight + 5) + Utils.dipToPixels(getActContext(), 170)), finalPadding));
                                    //  gMap.animateCamera(CameraUpdateFactory.scrollBy(0, Utils.dpToPx(getActContext(), -200)));

                                } catch (Exception e) {

                                    Utils.printLog("ExceptionGMapAnim", ":OnCancel:" + e.getMessage().toString());
                                }

                            }
                        });
                    } catch (Exception e) {
                        Utils.printLog("ExceptionGMapAnim", ":MainCatch:" + e.getMessage().toString());


                        try {

                            gMap.setMaxZoomPreference(maxZoomLevel);
                            gMap.setPadding(0, 0, 0, sliding_layout.getPanelHeight() + 5);
                            gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), width - Utils.dipToPixels(getActContext(), 80), height - ((driverAssignedHeaderFrag.fragmentHeight + 5) + Utils.dipToPixels(getActContext(), 170)), padding));

//                                gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), width - Utils.dipToPixels(getActContext(), 80), metrics.heightPixels - Utils.dpToPx(getActContext(), 200), 100));
                            //  gMap.animateCamera(CameraUpdateFactory.scrollBy(0, Utils.dpToPx(getActContext(), -200)));

                        } catch (Exception e1) {
                            Utils.printLog("ExceptionGMapAnim", ":MainSubCatch:" + e1.getMessage().toString());

                        }
                    }
                }
            } else {
                try {
                    CameraPosition cameraPosition = cameraForUserPosition();
                    if (cameraPosition != null) {
                        getMap().moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        if (mainHeaderFrag != null && mainHeaderFrag.getAddressFromLocation != null && userLocation != null) {
                            mainHeaderFrag.getAddressFromLocation.setLocation(userLocation.getLatitude(), userLocation.getLongitude());
                            mainHeaderFrag.getAddressFromLocation.execute();
                        }
                    }
                } catch (Exception e) {

                }
            }


        } else if (cabSelectionFrag != null) {

            if (cabSelectionFrag.isSkip) {
                cabSelectionFrag.handleSourceMarker(timeval);
                return;
            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            if (cabSelectionFrag.sourceMarker != null) {
                builder.include(cabSelectionFrag.sourceMarker.getPosition());
            }
            if (cabSelectionFrag.destDotMarker != null) {
                builder.include(cabSelectionFrag.destDotMarker.getPosition());
            }

            if (cabSelectionFrag.sourceDotMarker != null && cabSelectionFrag.destDotMarker != null && gMap != null) {
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int width = metrics.widthPixels;
                gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), width - Utils.dipToPixels(getActContext(), 80), (metrics.heightPixels - Utils.dipToPixels(getActContext(), 300)), 0));

            }


        }


    }

    public class SendNotificationsToDriverByDist implements Runnable {

        String[] list_drivers_ids;
        String cabRequestedJson;

        int interval = generalFunc.parseIntegerValue(30, generalFunc.getJsonValue("RIDER_REQUEST_ACCEPT_TIME", userProfileJson));

        int mInterval = (interval + 5) * 1000;

        int current_position_driver_id = 0;
        private Handler mHandler_sendNotification;

        public SendNotificationsToDriverByDist(String list_drivers_ids, String cabRequestedJson) {
            this.list_drivers_ids = list_drivers_ids.split(",");
            this.cabRequestedJson = cabRequestedJson;
            mHandler_sendNotification = new Handler();

            startRepeatingTask();
        }

        @Override
        public void run() {
            setRetryReqBtn(false);

            if ((current_position_driver_id + 1) <= list_drivers_ids.length) {
                sendRequestToDrivers(list_drivers_ids[current_position_driver_id], cabRequestedJson);
                current_position_driver_id = current_position_driver_id + 1;
                mHandler_sendNotification.postDelayed(this, mInterval);
            } else {
                setRetryReqBtn(true);
                stopRepeatingTask();
            }

        }


        public void stopRepeatingTask() {
            mHandler_sendNotification.removeCallbacks(this);
            mHandler_sendNotification.removeCallbacksAndMessages(null);
            current_position_driver_id = 0;
        }

        public void incTask() {
            mHandler_sendNotification.removeCallbacks(this);
            mHandler_sendNotification.removeCallbacksAndMessages(null);
            this.run();
        }

        public void startRepeatingTask() {
            stopRepeatingTask();

            this.run();
        }
    }
}
