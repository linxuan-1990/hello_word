package com.general.files;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;

import com.caliway.user.MainActivity;
import com.caliway.user.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rest.RestClient;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.SelectableRoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by Admin on 05-07-2016.
 */
public class LoadAvailableCab implements UpdateFrequentTask.OnTaskRunCalled {
    public ArrayList<HashMap<String, String>> listOfDrivers;
    public String pickUpAddress = "";
    public String currentGeoCodeResult = "";
    public String sortby = "";
    public boolean isAvailableCab = false;
    public String selectProviderId = "";
    Context mContext;
    GeneralFunctions generalFunc;
    String selectedCabTypeId = "";
    //    Location pickUpLocation;
    GoogleMap gMapView;
    View parentView;
    ExecuteWebServerUrl currentWebTask;
    MainActivity mainAct;
    public ArrayList<Marker> driverMarkerList;
    String userProfileJson;
    int RESTRICTION_KM_NEAREST_TAXI = 4;
    int ONLINE_DRIVER_LIST_UPDATE_TIME_INTERVAL = 1 * 60 * 1000;
    int DRIVER_ARRIVED_MIN_TIME_PER_MINUTE = 3;
    UpdateFrequentTask updateDriverListTask;
    Dialog dialog = null;
    boolean isTaskKilled = false;
    boolean isSessionOut = false;


    public LoadAvailableCab(Context mContext, GeneralFunctions generalFunc, String selectedCabTypeId, Location pickUpLocation, GoogleMap gMapView, String userProfileJson) {
        this.mContext = mContext;
        this.generalFunc = generalFunc;
        this.selectedCabTypeId = selectedCabTypeId;
//        this.pickUpLocation = pickUpLocation;
        this.gMapView = gMapView;
        this.userProfileJson = userProfileJson;

        if (mContext instanceof MainActivity) {
            mainAct = (MainActivity) mContext;
            parentView = generalFunc.getCurrentView(mainAct);
        }

        listOfDrivers = new ArrayList<>();
        driverMarkerList = new ArrayList<>();

        RESTRICTION_KM_NEAREST_TAXI = generalFunc.parseIntegerValue(4, generalFunc.getJsonValue("RESTRICTION_KM_NEAREST_TAXI", userProfileJson));
        ONLINE_DRIVER_LIST_UPDATE_TIME_INTERVAL = (generalFunc.parseIntegerValue(1, generalFunc.getJsonValue("ONLINE_DRIVER_LIST_UPDATE_TIME_INTERVAL", userProfileJson))) * 60 * 1000;
        DRIVER_ARRIVED_MIN_TIME_PER_MINUTE = generalFunc.parseIntegerValue(3, generalFunc.getJsonValue("DRIVER_ARRIVED_MIN_TIME_PER_MINUTE", userProfileJson));

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

    public void setPickUpLocation(Location pickUpLocation) {
//        this.pickUpLocation = pickUpLocation;
    }

    public void setCabTypeId(String selectedCabTypeId) {
        this.selectedCabTypeId = selectedCabTypeId;
    }

    public void changeCabs() {

        if (driverMarkerList.size() > 0) {
            filterDrivers(true);
        } else {
            checkAvailableCabs();
        }
    }

    public void checkAvailableCabs() {

        if (mainAct.llFilter != null) {
            mainAct.llFilter.setVisibility(View.GONE);
        }

        if (ConfigPubNub.getInstance().isSessionout) {
            return;
        }

        if (mainAct.pickUpLocation == null) {
            return;
        }

        if (gMapView == null) {
            return;
        }

        if (updateDriverListTask == null) {
            updateDriverListTask = new UpdateFrequentTask(ONLINE_DRIVER_LIST_UPDATE_TIME_INTERVAL);
            onResumeCalled();
            updateDriverListTask.setTaskRunListener(this);
        }

        if (currentWebTask != null) {
            currentWebTask.cancel(true);
            currentWebTask = null;
        }

        if (mainAct != null) {
            mainAct.notifyCarSearching();
        }

        if (listOfDrivers != null) {
            if (listOfDrivers.size() > 0) {
                listOfDrivers.clear();
            }
        }
        if (mainAct.cabSelectionFrag != null) {
            mainAct.cabSelectionFrag.showLoader();
        }
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "loadAvailableCab");
        parameters.put("PassengerLat", "" + mainAct.pickUpLocation.getLatitude());
        parameters.put("PassengerLon", "" + mainAct.pickUpLocation.getLongitude());
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("PickUpAddress", pickUpAddress);
        parameters.put("scheduleDate", mainAct.SelectDate);
        parameters.put("SelectedCabType", mainAct.getSelectedCabTypeId());
        parameters.put("sortby", sortby);

        if (!mainAct.eShowOnlyMoto.equalsIgnoreCase("")) {
            parameters.put("eShowOnlyMoto", mainAct.eShowOnlyMoto);
        }

        parameters.put("eType", mainAct.getCurrentCabGeneralType());


        //  parameters.put("currentGeoCodeResult", Utils.removeWithSpace(currentGeoCodeResult));

        if (mainAct != null) {
            if (mainAct.iscubejekRental) {
                parameters.put("eRental", "Yes");

            }
            if (mainAct.cabSelectionFrag != null) {
                if (mainAct.isUfx) {
                    parameters.put("iVehicleTypeId", mainAct.getSelectedCabTypeId());
                }
            } else {
                if (mainAct.app_type.equals(Utils.CabGeneralType_UberX) || (mainAct.app_type.equals(Utils.CabGeneralTypeRide_Delivery_UberX) || mainAct.isUfx) || mainAct.getCurrentCabGeneralType().equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
                    parameters.put("iVehicleTypeId", mainAct.getSelectedCabTypeId());

                }
            }
        }

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(mContext, parameters);
        this.currentWebTask = exeWebServer;
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                if (Utils.checkText(responseString) && generalFunc.getJsonValue(Utils.message_str, responseString).equals("SESSION_OUT")) {
                    isSessionOut = true;
                    if (currentWebTask != null) {
                        currentWebTask.cancel(true);
                        currentWebTask = null;
                    }
                    setTaskKilledValue(true);
                    updateDriverListTask.setTaskRunListener(null);
                    MyApp.getInstance().notifySessionTimeOut();
                    Utils.runGC();
                    return;
                }

                JSONArray vehicleTypesArr = generalFunc.getJsonArray("VehicleTypes", responseString);
                ArrayList<HashMap<String, String>> tempCabTypesArrList = new ArrayList<>();

                if (vehicleTypesArr != null) {
                    for (int i = 0; i < vehicleTypesArr.length(); i++) {
                        JSONObject tempObj = generalFunc.getJsonObject(vehicleTypesArr, i);

                        String type = mainAct.isDeliver(mainAct.getCurrentCabGeneralType()) ? "Deliver" : mainAct.getCurrentCabGeneralType();
                        if (type.equalsIgnoreCase("rental")) {
                            type = Utils.CabGeneralType_Ride;
                        }
                        if (generalFunc.getJsonValue("eType", tempObj.toString()).equals(type)) {

                            Gson gson = RestClient.getGSONBuilder();
                            HashMap<String, String> dataMap = gson.fromJson(
                                    tempObj.toString(), new TypeToken<HashMap<String, Object>>() {
                                    }.getType()
                            );
                            tempCabTypesArrList.add(dataMap);
                        }
                    }
                }

                boolean isCarTypeChanged = isCarTypesArrChanged(tempCabTypesArrList);

                if (isCarTypeChanged) {

                    mainAct.cabTypesArrList.clear();
                    mainAct.cabTypesArrList.addAll(tempCabTypesArrList);
                    mainAct.selectedCabTypeId = getFirstCarTypeID();
                    selectedCabTypeId = getFirstCarTypeID();

                    if (mainAct.cabSelectionFrag != null) {
                        mainAct.cabSelectionFrag.generateCarType();
                    }
                }

                if (mainAct.cabTypesArrList.size() > 0) {
                    mainAct.cabTypesArrList.clear();
                    mainAct.cabTypesArrList.addAll(tempCabTypesArrList);
                }

                if (mainAct.cabTypesArrList.size() == 0) {
                    mainAct.cabTypesArrList.addAll(tempCabTypesArrList);
                }

                if (mainAct.cabSelectionFrag != null && mainAct.cabTypesArrList != null && mainAct.cabTypesArrList.size() > 0) {
                    mainAct.cabSelectionFrag.closeLoadernTxt();
                } else {
                    if (mainAct.cabSelectionFrag != null && mainAct.cabTypesArrList != null) {
                        mainAct.cabSelectionFrag.closeLoader();
                    }
                }

                JSONArray availCabArr = generalFunc.getJsonArray("AvailableCabList", responseString);

                if (availCabArr != null) {
                    for (int i = 0; i < availCabArr.length(); i++) {
                        JSONObject obj_temp = generalFunc.getJsonObject(availCabArr, i);

                        JSONObject carDetailsJson = generalFunc.getJsonObject("DriverCarDetails", obj_temp);
                        HashMap<String, String> driverDataMap = new HashMap<String, String>();
                        driverDataMap.put("driver_id", generalFunc.getJsonValueStr("iDriverId", obj_temp));
                        driverDataMap.put("Name", generalFunc.getJsonValueStr("vName", obj_temp));
                        driverDataMap.put("eIsFeatured", generalFunc.getJsonValueStr("eIsFeatured", obj_temp));
                        driverDataMap.put("LastName", generalFunc.getJsonValueStr("vLastName", obj_temp));
                        driverDataMap.put("Latitude", generalFunc.getJsonValueStr("vLatitude", obj_temp));
                        driverDataMap.put("Longitude", generalFunc.getJsonValueStr("vLongitude", obj_temp));
                        driverDataMap.put("GCMID", generalFunc.getJsonValueStr("iGcmRegId", obj_temp));
                        driverDataMap.put("iAppVersion", generalFunc.getJsonValueStr("iAppVersion", obj_temp));
                        driverDataMap.put("driver_img", generalFunc.getJsonValueStr("vImage", obj_temp));
                        driverDataMap.put("average_rating", generalFunc.getJsonValueStr("vAvgRating", obj_temp));
                        driverDataMap.put("DIST_TO_PICKUP_INT", generalFunc.getJsonValueStr("distance", obj_temp));
                        driverDataMap.put("vPhone_driver", generalFunc.getJsonValueStr("vPhone", obj_temp));
                        driverDataMap.put("vPhoneCode_driver", generalFunc.getJsonValueStr("vCode", obj_temp));
                        driverDataMap.put("tProfileDescription", generalFunc.getJsonValueStr("tProfileDescription", obj_temp));
                        driverDataMap.put("ACCEPT_CASH_TRIPS", generalFunc.getJsonValueStr("ACCEPT_CASH_TRIPS", obj_temp));
                        driverDataMap.put("vWorkLocationRadius", generalFunc.getJsonValueStr("vWorkLocationRadius", obj_temp));
                        driverDataMap.put("PROVIDER_RADIUS", generalFunc.getJsonValueStr("vWorkLocationRadius", obj_temp));


                        driverDataMap.put("DriverGender", generalFunc.getJsonValueStr("eGender", obj_temp));
                        driverDataMap.put("eFemaleOnlyReqAccept", generalFunc.getJsonValueStr("eFemaleOnlyReqAccept", obj_temp));


                        driverDataMap.put("eHandiCapAccessibility", generalFunc.getJsonValueStr("eHandiCapAccessibility", carDetailsJson));
                        driverDataMap.put("vCarType", generalFunc.getJsonValueStr("vCarType", carDetailsJson));
                        driverDataMap.put("vColour", generalFunc.getJsonValueStr("vColour", carDetailsJson));
                        driverDataMap.put("vLicencePlate", generalFunc.getJsonValueStr("vLicencePlate", carDetailsJson));
                        driverDataMap.put("make_title", generalFunc.getJsonValueStr("make_title", carDetailsJson));
                        driverDataMap.put("model_title", generalFunc.getJsonValueStr("model_title", carDetailsJson));
                        driverDataMap.put("fAmount", generalFunc.getJsonValueStr("fAmount", carDetailsJson));
                        driverDataMap.put("eRental", generalFunc.getJsonValueStr("vRentalCarType", carDetailsJson));


                        driverDataMap.put("vCurrencySymbol", generalFunc.getJsonValueStr("vCurrencySymbol", carDetailsJson));

                        driverDataMap.put("PROVIDER_RATING_COUNT", generalFunc.getJsonValue("PROVIDER_RATING_COUNT", obj_temp.toString()));
                        listOfDrivers.add(driverDataMap);
                    }

                }


                if (availCabArr == null || availCabArr.length() == 0) {
                    removeDriversFromMap(true);
                    if (mainAct != null) {
                        mainAct.notifyNoCabs();
                    }
                } else {
                    filterDrivers(false);
                }


            } else {
                removeDriversFromMap(true);
                if (parentView != null) {
                    generalFunc.showMessage(parentView, generalFunc.retrieveLangLBl("", "LBL_NO_INTERNET_TXT"));
                }

                if (mainAct != null) {
                    mainAct.notifyNoCabs();
                }
            }

        });
        exeWebServer.execute();
    }

    public boolean isCarTypesArrChanged(ArrayList<HashMap<String, String>> carTypeList) {
        if (mainAct.cabTypesArrList.size() != carTypeList.size()) {
            return true;
        }

        for (int i = 0; i < carTypeList.size(); i++) {
            String iVehicleTypeId = mainAct.cabTypesArrList.get(i).get("iVehicleTypeId");
            String newVehicleTypeId = carTypeList.get(i).get("iVehicleTypeId");

            if (!iVehicleTypeId.equals(newVehicleTypeId)) {
                return true;
            }
        }
        return false;
    }

    public String getFirstCarTypeID() {

        if (mainAct.app_type.equalsIgnoreCase(Utils.CabGeneralType_UberX) || mainAct.isUfx) {
            return selectedCabTypeId;
        }
        for (int i = 0; i < mainAct.cabTypesArrList.size(); i++) {
            String iVehicleTypeId = mainAct.cabTypesArrList.get(i).get("iVehicleTypeId");

            return iVehicleTypeId;
        }
        return "";
    }

    public void setTaskKilledValue(boolean isTaskKilled) {
        this.isTaskKilled = isTaskKilled;

        if (isTaskKilled == true) {
            onPauseCalled();
        }
    }

    public void removeDriversFromMap(boolean isUnSubscribeAll) {
        if (driverMarkerList.size() > 0) {
            ArrayList<Marker> tempDriverMarkerList = new ArrayList<>();
            tempDriverMarkerList.addAll(driverMarkerList);
            for (int i = 0; i < tempDriverMarkerList.size(); i++) {
                Marker marker_temp = driverMarkerList.get(0);
                marker_temp.remove();
                driverMarkerList.remove(0);

            }
        }

        if (mainAct != null && isUnSubscribeAll == true) {
            ConfigPubNub.getInstance().unSubscribeToChannels(mainAct.getDriverLocationChannelList());
        }
    }


    public ArrayList<Marker> getDriverMarkerList() {
        return this.driverMarkerList;
    }

    public void filterDrivers(boolean isCheckAgain) {

        if (mainAct.pickUpLocation == null) {
            generalFunc.restartApp();
            return;
        }

        if (gMapView == null) {
            return;
        }

        double lowestKM = 0.0;
        boolean isFirst_lowestKM = true;

        ArrayList<HashMap<String, String>> currentLoadedDrivers = new ArrayList<>();

        ArrayList<Marker> driverMarkerList_temp = new ArrayList<>();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();


        if (mainAct.selectedSort == "eIsFeatured") {

            Collections.sort(listOfDrivers, new Comparator<HashMap<String, String>>() {
                @Override
                public int compare(HashMap<String, String> map1, HashMap<String, String> map2) {

                    boolean b1;
                    boolean b2;

                    if (map1.get("eIsFeatured").equalsIgnoreCase("Yes")) {
                        b1 = true;
                    } else {
                        b1 = false;
                    }

                    if (map2.get("eIsFeatured").equalsIgnoreCase("Yes")) {
                        b2 = true;
                    } else {
                        b2 = false;
                    }

                    return (b1 != b2) ? (b1) ? -1 : 1 : 0;

                }
            });
        } else if (mainAct.selectedSort == "distance") {
            Collections.sort(listOfDrivers, new Comparator<HashMap<String, String>>() {
                @Override
                public int compare(HashMap<String, String> map1, HashMap<String, String> map2) {
                    return map1.get("DIST_TO_PICKUP_INT").compareTo(map2.get("DIST_TO_PICKUP_INT"));
                }
            });
        } else if (mainAct.selectedSort == "vAvgRating") {
            Collections.sort(listOfDrivers, Collections.reverseOrder(new Comparator<HashMap<String, String>>() {
                @Override
                public int compare(HashMap<String, String> t1, HashMap<String, String> t2) {
                    return t1.get("average_rating").compareTo(t2.get("average_rating"));
                }
            }));
        }

        for (int i = 0; i < listOfDrivers.size(); i++) {
            HashMap<String, String> driverData = listOfDrivers.get(i);

            String driverName = driverData.get("Name");
            String[] vCarType = driverData.get("vCarType").split(",");

            boolean isCarSelected = Arrays.asList(vCarType).contains(selectedCabTypeId);

            String eHandiCapAccessibility = driverData.get("eHandiCapAccessibility");
            String eFemaleOnlyReqAccept = driverData.get("eFemaleOnlyReqAccept");
            String DriverGender = driverData.get("DriverGender");

            boolean isCarRental = true;

            if (mainAct.isRental || mainAct.iscubejekRental) {
                String[] vRentalCarType = driverData.get("eRental").split(",");
                if (vRentalCarType != null && vRentalCarType.length > 0) {
                    isCarRental = Arrays.asList(vRentalCarType).contains(selectedCabTypeId);
                    isCarSelected = Arrays.asList(vCarType).contains(selectedCabTypeId);
                }
            }

            if (isCarRental == false || (isCarSelected == false) ||
                    (mainAct.ishandicap == true && !eHandiCapAccessibility.equalsIgnoreCase("yes") && mainAct.getCurrentCabGeneralType().equalsIgnoreCase(Utils.CabGeneralType_Ride)) ||
                    (eFemaleOnlyReqAccept.equalsIgnoreCase("yes") && generalFunc.getJsonValue("eGender", mainAct.userProfileJson).equalsIgnoreCase("Male") && mainAct.getCurrentCabGeneralType().equalsIgnoreCase(Utils.CabGeneralType_Ride)) ||
                    (mainAct.isfemale == true && !DriverGender.equalsIgnoreCase("FeMale") && mainAct.getCurrentCabGeneralType().equalsIgnoreCase(Utils.CabGeneralType_Ride)) || (driverData.get("ACCEPT_CASH_TRIPS").equalsIgnoreCase("No") && mainAct.isCashSelected == true && !mainAct.getCurrentCabGeneralType().equalsIgnoreCase(Utils.CabGeneralType_UberX)) ||
                    (mainAct.getCurrentCabGeneralType().equalsIgnoreCase(Utils.CabGeneralType_UberX) && driverData.get("ACCEPT_CASH_TRIPS").equalsIgnoreCase("No") && mainAct.isCashSelected == true && generalFunc.getJsonValueStr("APP_PAYMENT_MODE", mainAct.obj_userProfile).equalsIgnoreCase("CASH"))
                    || (!selectProviderId.equals("") && !selectProviderId.equals(driverData.get("driver_id")))) {
                continue;
            }

            double driverLocLatitude = generalFunc.parseDoubleValue(0.0, driverData.get("Latitude"));
            double driverLocLongitude = generalFunc.parseDoubleValue(0.0, driverData.get("Longitude"));

            if (mainAct.pickUpLocation == null) {
                return;
            }

            double distance = Utils.CalculationByLocation(mainAct.pickUpLocation.getLatitude(), mainAct.pickUpLocation.getLongitude(), driverLocLatitude, driverLocLongitude, "");
            if (mainAct.isUfx) {
                if (mainAct.pickUpLocation != null) {
                    distance = Utils.CalculationByLocation(mainAct.pickUpLocation.getLatitude(), mainAct.pickUpLocation.getLongitude(), driverLocLatitude, driverLocLongitude, "");
                }
            }
            if (isFirst_lowestKM == true) {
                lowestKM = distance;
                isFirst_lowestKM = false;
            } else {
                if (distance < lowestKM) {
                    lowestKM = distance;
                }
            }

            float PROVIDER_RADIUS_int = GeneralFunctions.parseFloatValue(-1, driverData.get("PROVIDER_RADIUS"));

            if ((PROVIDER_RADIUS_int != -1 && distance < PROVIDER_RADIUS_int && mainAct.getCurrentCabGeneralType().equalsIgnoreCase(Utils.CabGeneralType_UberX)) || (distance < RESTRICTION_KM_NEAREST_TAXI && !mainAct.getCurrentCabGeneralType().equalsIgnoreCase(Utils.CabGeneralType_UberX))) {
                driverData.put("DIST_TO_PICKUP", "" + distance);


                driverData.put("DIST_TO_PICKUP_INT", "" + String.format("%.2f", (float) distance));

                if (generalFunc.getJsonValue("eUnit", userProfileJson).equals("KMs")) {
                    driverData.put("DIST_TO_PICKUP_INT_ROW", String.format("%.2f", (float) distance) + " " + generalFunc.retrieveLangLBl("", "LBL_KM_DISTANCE_TXT") + " " + generalFunc.retrieveLangLBl("", "LBL_AWAY"));
                    driverData.put("LBL_KM_DISTANCE_TXT", "" + generalFunc.retrieveLangLBl("", "LBL_KM_DISTANCE_TXT"));

                } else {
                    driverData.put("DIST_TO_PICKUP_INT_ROW", String.format("%.2f", (float) distance) + " " + generalFunc.retrieveLangLBl("", "LBL_MILE_DISTANCE_TXT") + " " + generalFunc.retrieveLangLBl("", "LBL_AWAY"));
                    driverData.put("LBL_KM_DISTANCE_TXT", "" + generalFunc.retrieveLangLBl("", "LBL_MILE_DISTANCE_TXT"));

                }


                driverData.put("LBL_BTN_REQUEST_PICKUP_TXT", "" + generalFunc.retrieveLangLBl("", "LBL_BTN_REQUEST_PICKUP_TXT"));
                driverData.put("LBL_SEND_REQUEST", "" + generalFunc.retrieveLangLBl("", "LBL_SEND_REQ"));
                driverData.put("LBL_MORE_INFO_TXT", "" + generalFunc.retrieveLangLBl("More info", "LBL_MORE_INFO"));
                driverData.put("LBL_AWAY", "" + generalFunc.retrieveLangLBl("away", "LBL_AWAY"));
                // driverData.put("LBL_KM_DISTANCE_TXT", "" + generalFunc.retrieveLangLBl("", "LBL_KM_DISTANCE_TXT"));
                currentLoadedDrivers.add(driverData);

                Marker driverMarker_temp = mainAct.getDriverMarkerOnPubNubMsg(driverData.get("driver_id"), true);

                if (driverMarker_temp != null) {
                    driverMarker_temp.remove();
                }

                builder.include(new LatLng(driverLocLatitude, driverLocLongitude));
                Marker driverMarker = drawMarker(new LatLng(driverLocLatitude, driverLocLongitude), driverName, driverData);
                driverMarkerList_temp.add(driverMarker);
            }
        }

        if (mainAct.pickUpLocation != null) {
            builder.include(new LatLng(mainAct.pickUpLocation.getLatitude(), mainAct.pickUpLocation.getLongitude()));
        }

        removeDriversFromMap(false);


        driverMarkerList.addAll(driverMarkerList_temp);


        if (mainAct != null) {

            if (lowestKM > 1.5) {
                if (mainAct.isFirstZoomlevel) {
                    try {
                        mainAct.isFirstZoomlevel = false;
                        gMapView.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 10));

                    } catch (Exception e) {

                    }
                }

            }

            int lowestTime = ((int) (lowestKM * DRIVER_ARRIVED_MIN_TIME_PER_MINUTE));

            if (lowestTime < 1) {
                lowestTime = 1;
            }

            isAvailableCab = true;
            mainAct.setETA("" + lowestTime + "\n" + generalFunc.retrieveLangLBl("", "LBL_MIN_SMALL_TXT"));
        }

        if (mainAct != null) {

            ArrayList<String> unSubscribeChannelList = new ArrayList<>();
            ArrayList<String> subscribeChannelList = new ArrayList<>();

            ArrayList<String> currentDriverChannelsList = mainAct.getDriverLocationChannelList();
            ArrayList<String> newDriverChannelsList = mainAct.getDriverLocationChannelList(currentLoadedDrivers);

            for (int i = 0; i < currentDriverChannelsList.size(); i++) {
                String channel_name = currentDriverChannelsList.get(i);
                if (!newDriverChannelsList.contains(channel_name)) {
                    unSubscribeChannelList.add(channel_name);
                }
            }

            for (int i = 0; i < newDriverChannelsList.size(); i++) {
                String channel_name = newDriverChannelsList.get(i);
                if (!currentDriverChannelsList.contains(channel_name)) {
                    subscribeChannelList.add(channel_name);
                }
            }

            mainAct.setCurrentLoadedDriverList(currentLoadedDrivers);

            //changes
            ConfigPubNub.getInstance().subscribeToChannels(subscribeChannelList);
            ConfigPubNub.getInstance().unSubscribeToChannels(unSubscribeChannelList);
        }

        if (currentLoadedDrivers.size() == 0) {
            if (mainAct != null) {
                mainAct.notifyNoCabs();
            }

            if (isCheckAgain == true) {
                checkAvailableCabs();
            }
        } else {

            if (mainAct != null) {
                mainAct.notifyCabsAvailable();
            }
        }
    }

    public Marker drawMarker(LatLng point, String Name, HashMap<String, String> driverData) {

        MarkerOptions markerOptions = new MarkerOptions();
        String eIconType = generalFunc.getSelectedCarTypeData(selectedCabTypeId, mainAct.cabTypesArrList, "eIconType");

        int iconId = R.mipmap.car_driver;
        if (eIconType.equalsIgnoreCase("Bike")) {
            iconId = R.mipmap.car_driver_1;
        } else if (eIconType.equalsIgnoreCase("Cycle")) {
            iconId = R.mipmap.car_driver_2;
        } else if (eIconType.equalsIgnoreCase("Truck")) {
            iconId = R.mipmap.car_driver_4;
        }

        SelectableRoundedImageView providerImgView = null;
        View marker_view = null;

        if (mainAct != null) {
            markerOptions.position(point).title("DriverId" + driverData.get("driver_id")).icon(BitmapDescriptorFactory.fromResource(iconId))
                    .anchor(0.5f, 0.5f).flat(true);
        } else {
            markerOptions.position(point).title("DriverId" + driverData.get("driver_id")).icon(BitmapDescriptorFactory.fromResource(iconId))
                    .anchor(0.5f, 0.5f).flat(true);
        }

        // Adding marker on the Google Map
        final Marker marker = gMapView.addMarker(markerOptions);
        marker.setRotation(0);
        marker.setVisible(true);

        if (generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase("UberX") &&
                !driverData.get("driver_img").equals("") && !driverData.get("driver_img").equals("NONE") && providerImgView != null && marker_view != null) {

            String image_url = CommonUtilities.SERVER_URL_PHOTOS + "upload/Driver/" + driverData.get("driver_id") + "/"
                    + driverData.get("driver_img");

            final View finalMarker_view = marker_view;
            Picasso.with(mContext)
                    .load(image_url/*"http://www.hellocle.com/wp-content/themes/hello/images/hello-logo-stone.png"*/)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(providerImgView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            try {

                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(mContext, finalMarker_view)));
                            } catch (Exception e) {

                            }
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }

        if (mainAct != null) {
            if (mainAct.isUfx) {
                if (generalFunc.getJsonValue("APP_TYPE", userProfileJson).equalsIgnoreCase(Utils.CabGeneralTypeRide_Delivery_UberX) &&
                        !driverData.get("driver_img").equals("") && !driverData.get("driver_img").equals("NONE") && providerImgView != null && marker_view != null) {

                    String image_url = CommonUtilities.SERVER_URL_PHOTOS + "upload/Driver/" + driverData.get("driver_id") + "/"
                            + driverData.get("driver_img");

                    final View finalMarker_view = marker_view;
                    Picasso.with(mContext)
                            .load(image_url/*"http://www.hellocle.com/wp-content/themes/hello/images/hello-logo-stone.png"*/)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .into(providerImgView, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    try {
                                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(mContext, finalMarker_view)));

                                    } catch (Exception e) {

                                    }
                                }

                                @Override
                                public void onError() {

                                }
                            });
                }
            }
        }

        return marker;
    }

    public void onPauseCalled() {

        if (updateDriverListTask != null) {
            updateDriverListTask.stopRepeatingTask();
        }
    }

    public void onResumeCalled() {
        if (updateDriverListTask != null && isTaskKilled == false) {
            updateDriverListTask.startRepeatingTask();
        }
    }

    @Override
    public void onTaskRun() {
        checkAvailableCabs();
    }

//                              OVER MARKERS AND DRIVER DETAILS
//    ======================================================================================================

}
