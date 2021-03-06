package com.general.files;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.adapter.files.DrawerAdapter;
import com.caliway.user.CardPaymentActivity;
import com.caliway.user.ContactUsActivity;
import com.caliway.user.EmergencyContactActivity;
import com.caliway.user.HelpActivity;
import com.caliway.user.HistoryActivity;
import com.caliway.user.InviteFriendsActivity;
import com.caliway.user.MainActivity;
import com.caliway.user.MyBookingsActivity;
import com.caliway.user.MyProfileActivity;
import com.caliway.user.MyWalletActivity;
import com.caliway.user.R;
import com.caliway.user.StaticPageActivity;
import com.caliway.user.SupportActivity;
import com.caliway.user.VerifyInfoActivity;
import com.utils.AppFunctions;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MTextView;
import com.view.SelectableRoundedImageView;

import org.json.JSONObject;

import java.util.ArrayList;

public class AddDrawer implements AdapterView.OnItemClickListener {

    public String userProfileJson;
    public MTextView walletbalncetxt;
    Context mContext;
    View view;
    DrawerLayout mDrawerLayout;
    ListView menuListView;
    DrawerAdapter drawerAdapter;
    ArrayList<String[]> list_menu_items;
    GeneralFunctions generalFunc;
    DrawerClickListener drawerClickListener;
    boolean isMenuState = true;
    boolean isDriverAssigned = false;
    LinearLayout logoutarea;
    ImageView logoutimage;
    MTextView logoutTxt;
    ImageView imgSetting;
    LinearLayout left_linear;
    MainActivity mainActivity;
    public JSONObject obj_userProfile;
    boolean isback = false;

    public AddDrawer(Context mContext, String userProfileJson, boolean isback) {
        this.mContext = mContext;
        this.userProfileJson = userProfileJson;
        this.isback = isback;
        view = ((Activity) mContext).findViewById(android.R.id.content);
        mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        menuListView = (ListView) view.findViewById(R.id.menuListView);
        logoutarea = (LinearLayout) view.findViewById(R.id.logoutarea);
        logoutimage = (ImageView) view.findViewById(R.id.logoutimage);
        logoutTxt = (MTextView) view.findViewById(R.id.logoutTxt);
        imgSetting = (ImageView) view.findViewById(R.id.imgSetting);
        left_linear = (LinearLayout) view.findViewById(R.id.left_linear);
        imgSetting.setOnClickListener(new setOnClickLst());
        logoutarea.setOnClickListener(new setOnClickLst());
        generalFunc = new GeneralFunctions(mContext);
        logoutimage.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_menu_logout));
        logoutTxt.setText(generalFunc.retrieveLangLBl("", "LBL_SIGNOUT_TXT"));
        walletbalncetxt = (MTextView) view.findViewById(R.id.walletbalncetxt);


        android.view.Display display = ((android.view.WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        left_linear.getLayoutParams().width = display.getWidth() * 75 / 100;
        left_linear.requestLayout();

        if (mContext instanceof MainActivity) {
            mainActivity = (MainActivity) mContext;
        }
        obj_userProfile = generalFunc.getJsonObject(userProfileJson);


        if (isback) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }


        buildDrawer();
        setUserInfo();
    }

    public void setMenuImgClick(View view, boolean isDriverAssigned) {
//        isMenuState = true;
        if (isDriverAssigned) {
            (view.findViewById(R.id.backImgView)).setOnClickListener(new setOnClickLst());

        } else {
            (view.findViewById(R.id.menuImgView)).setOnClickListener(new setOnClickLst());

        }
    }

    public void changeUserProfileJson(String userProfileJson) {
        this.userProfileJson = userProfileJson;
        setUserInfo();
    }

    public void configDrawer(boolean isHide) {

        (view.findViewById(R.id.left_linear)).setVisibility(View.GONE);
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) (view.findViewById(R.id.left_linear)).getLayoutParams();
        params.gravity = isHide == true ? Gravity.NO_GRAVITY : GravityCompat.START;
        (view.findViewById(R.id.left_linear)).setLayoutParams(params);
    }

    public void setMenuState(boolean isMenuState) {
        this.isMenuState = isMenuState;

        if (this.isMenuState == false) {
            ((ImageView) view.findViewById(R.id.menuImgView)).setImageResource(R.mipmap.ic_back_arrow);

            configDrawer(true);

        } else {
            ((ImageView) view.findViewById(R.id.menuImgView)).setImageResource(R.mipmap.ic_drawer_menu);

            configDrawer(false);
        }
    }

    public void setIsDriverAssigned(boolean isDriverAssigned) {
        this.isDriverAssigned = isDriverAssigned;
    }

    public void buildDrawer() {
        list_menu_items = new ArrayList();
        drawerAdapter = new DrawerAdapter(list_menu_items, mContext);

        menuListView.setAdapter(drawerAdapter);
        menuListView.setOnItemClickListener(this);

        list_menu_items.add(new String[]{"" + R.mipmap.ic_menu_profile, generalFunc.retrieveLangLBl("", "LBL_MY_PROFILE_HEADER_TXT"), "" + Utils.MENU_PROFILE});
        //  list_menu_items.add(new String[]{"" + R.mipmap.ic_menu_profile, generalFunc.retrieveLangLBl("Add vehicles", "LBL_ADD_VEHICLE"), "" + Utils.MENU_VEHICLE});


        String menuMsgYourTrips = "";
        if (generalFunc.getJsonValueStr("APP_TYPE", obj_userProfile).equalsIgnoreCase(Utils.CabGeneralType_Ride)) {
            menuMsgYourTrips = generalFunc.retrieveLangLBl("", "LBL_YOUR_TRIPS");
        } else if (generalFunc.getJsonValueStr("APP_TYPE", obj_userProfile).equalsIgnoreCase("Delivery")) {
            menuMsgYourTrips = generalFunc.retrieveLangLBl("", "LBL_YOUR_DELIVERY");
        } else if (generalFunc.getJsonValueStr("APP_TYPE", obj_userProfile).equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
            menuMsgYourTrips = generalFunc.retrieveLangLBl("", "LBL_YOUR_BOOKING");
        } else {
            menuMsgYourTrips = generalFunc.retrieveLangLBl("", "LBL_YOUR_BOOKING");
        }
        list_menu_items.add(new String[]{"" + R.mipmap.ic_menu_yourtrip, menuMsgYourTrips, "" + Utils.MENU_YOUR_TRIPS});

        if (!generalFunc.getJsonValueStr("APP_PAYMENT_MODE", obj_userProfile).equalsIgnoreCase("Cash")) {
            list_menu_items.add(new String[]{"" + R.mipmap.ic_menu_card, generalFunc.retrieveLangLBl("Payment", "LBL_PAYMENT"), "" + Utils.MENU_PAYMENT});
        }

        if (!generalFunc.getJsonValueStr(Utils.WALLET_ENABLE, obj_userProfile).equals("") &&
                generalFunc.getJsonValueStr(Utils.WALLET_ENABLE, obj_userProfile).equalsIgnoreCase("Yes")) {
            list_menu_items.add(new String[]{"" + R.mipmap.ic_menu_wallet, generalFunc.retrieveLangLBl("", "LBL_LEFT_MENU_WALLET"), "" + Utils.MENU_WALLET});
        }

        if (!generalFunc.getJsonValueStr("eEmailVerified", obj_userProfile).equalsIgnoreCase("YES") ||
                !generalFunc.getJsonValueStr("ePhoneVerified", obj_userProfile).equalsIgnoreCase("YES")) {
            list_menu_items.add(new String[]{"" + R.mipmap.ic_menu_privacy, generalFunc.retrieveLangLBl("", "LBL_ACCOUNT_VERIFY_TXT"), "" + Utils.MENU_ACCOUNT_VERIFY});
        }

        list_menu_items.add(new String[]{"" + R.mipmap.ic_menu_emergency, generalFunc.retrieveLangLBl("Emergency Contact", "LBL_EMERGENCY_CONTACT"), "" + Utils.MENU_EMERGENCY_CONTACT});
        if (!generalFunc.getJsonValueStr(Utils.REFERRAL_SCHEME_ENABLE, obj_userProfile).equals("") && generalFunc.getJsonValueStr(Utils.REFERRAL_SCHEME_ENABLE, obj_userProfile).equalsIgnoreCase("Yes")) {
            list_menu_items.add(new String[]{"" + R.mipmap.ic_menu_invite, generalFunc.retrieveLangLBl("Invite Friends", "LBL_INVITE_FRIEND_TXT"), "" + Utils.MENU_INVITE_FRIEND});
        }

        list_menu_items.add(new String[]{"" + R.mipmap.ic_menu_support, generalFunc.retrieveLangLBl("Support", "LBL_SUPPORT_HEADER_TXT"), "" + Utils.MENU_SUPPORT});

        drawerAdapter.notifyDataSetChanged();
    }

    public void setUserInfo() {
        ((MTextView) view.findViewById(R.id.userNameTxt)).setText(generalFunc.getJsonValueStr("vName", obj_userProfile) + " "
                + generalFunc.getJsonValueStr("vLastName", obj_userProfile));
        ((MTextView) view.findViewById(R.id.walletbalncetxt)).setText(generalFunc.retrieveLangLBl("", "LBL_WALLET_BALANCE") + ": " + generalFunc.convertNumberWithRTL(generalFunc.getJsonValueStr("user_available_balance", obj_userProfile)));

        new AppFunctions(mContext).checkProfileImage((SelectableRoundedImageView) view.findViewById(R.id.userImgView), userProfileJson, "vImgName");
    }

    public void openMenuProfile() {
        Bundle bn = new Bundle();
        //   bn.putString("UserProfileJson", userProfileJson);
        bn.putString("isDriverAssigned", "" + isDriverAssigned);
        new StartActProcess(mContext).startActForResult(MyProfileActivity.class, bn, Utils.MY_PROFILE_REQ_CODE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
        int itemId = generalFunc.parseIntegerValue(0, list_menu_items.get(position)[2]);
        Bundle bn = new Bundle();
        switch (itemId) {
            case Utils.MENU_PROFILE:
                openMenuProfile();
                break;
            case Utils.MENU_VEHICLE:

                break;
            case Utils.MENU_RIDE_HISTORY:
                new StartActProcess(mContext).startActWithData(HistoryActivity.class, bn);
                break;
            case Utils.MENU_BOOKINGS:
                new StartActProcess(mContext).startActWithData(MyBookingsActivity.class, bn);
                break;

            case Utils.MENU_ABOUT_US:
                new StartActProcess(mContext).startAct(StaticPageActivity.class);
                break;
            case Utils.MENU_POLICY:
                (new StartActProcess(mContext)).openURL(CommonUtilities.SERVER_URL + "privacy-policy");
                break;
            case Utils.MENU_PAYMENT:

                bn.putBoolean("fromcabselection", false);
                new StartActProcess(mContext).startActForResult(CardPaymentActivity.class, bn, Utils.CARD_PAYMENT_REQ_CODE);
                break;
            case Utils.MENU_CONTACT_US:
                new StartActProcess(mContext).startAct(ContactUsActivity.class);
                break;
            case Utils.MENU_HELP:
                new StartActProcess(mContext).startAct(HelpActivity.class);
                break;
            case Utils.MENU_EMERGENCY_CONTACT:
                new StartActProcess(mContext).startAct(EmergencyContactActivity.class);
                break;

            case Utils.MENU_SUPPORT:
                new StartActProcess(mContext).startAct(SupportActivity.class);

                break;
            case Utils.MENU_WALLET:
                new StartActProcess(mContext).startActWithData(MyWalletActivity.class, bn);
                break;


            case Utils.MENU_ACCOUNT_VERIFY:
                if (!generalFunc.getJsonValue("eEmailVerified", userProfileJson).equalsIgnoreCase("YES") ||
                        !generalFunc.getJsonValue("ePhoneVerified", userProfileJson).equalsIgnoreCase("YES")) {

                    Bundle bn1 = new Bundle();
                    if (!generalFunc.getJsonValue("eEmailVerified", userProfileJson).equalsIgnoreCase("YES") &&
                            !generalFunc.getJsonValue("ePhoneVerified", userProfileJson).equalsIgnoreCase("YES")) {
                        bn1.putString("msg", "DO_EMAIL_PHONE_VERIFY");
                    } else if (!generalFunc.getJsonValue("eEmailVerified", userProfileJson).equalsIgnoreCase("YES")) {
                        bn1.putString("msg", "DO_EMAIL_VERIFY");
                    } else if (!generalFunc.getJsonValue("ePhoneVerified", userProfileJson).equalsIgnoreCase("YES")) {
                        bn1.putString("msg", "DO_PHONE_VERIFY");
                    }

                    // bn1.putString("UserProfileJson", userProfileJson);
                    new StartActProcess(mContext).startActForResult(VerifyInfoActivity.class, bn1, Utils.VERIFY_INFO_REQ_CODE);

                }
                break;
            case Utils.MENU_YOUR_TRIPS:
                bn.putBoolean("isrestart", false);
                new StartActProcess(mContext).startActWithData(HistoryActivity.class, bn);
                break;
            case Utils.MENU_INVITE_FRIEND:
                new StartActProcess(mContext).startActWithData(InviteFriendsActivity.class, bn);
                break;
            case Utils.MENU_SIGN_OUT:


                final GenerateAlertBox generateAlert = new GenerateAlertBox(mContext);
                generateAlert.setCancelable(false);
                generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                    @Override
                    public void handleBtnClick(int btn_id) {
                        if (btn_id == 0) {
                            generateAlert.closeAlertBox();
                        } else {
                            MyApp.getInstance().logOutFromDevice(false);
                        }

                    }
                });
                generateAlert.setContentMessage(generalFunc.retrieveLangLBl("Logout", "LBL_LOGOUT"), generalFunc.retrieveLangLBl("Are you sure you want to logout?", "LBL_WANT_LOGOUT_APP_TXT"));
                generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_YES"));
                generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_NO"));
                generateAlert.showAlertBox();
                break;
        }
        closeDrawer();
    }

    public void closeDrawer() {
        if (isback) {
            return;
        }
        (view.findViewById(R.id.left_linear)).setVisibility(View.GONE);
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    public boolean checkDrawerState(boolean isOpenDrawer) {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START) == true) {
            closeDrawer();
            return true;
        } else if (isOpenDrawer == true) {
            openDrawer();
        }
        return false;
    }

    public void openDrawer() {

        (view.findViewById(R.id.left_linear)).setVisibility(View.VISIBLE);
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    private void setMenuAction() {
        if (isMenuState) {
            openDrawer();
        } else {
            setMenuState(true);
            if (drawerClickListener != null) {
                drawerClickListener.onClick();
            }
        }
    }

    public void setItemClickList(DrawerClickListener itemClickList) {
        this.drawerClickListener = itemClickList;
    }

    public interface DrawerClickListener {
        void onClick();
    }

    public class setOnClickLst implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.menuImgView:
                    setMenuAction();
                    break;
                case R.id.backImgView:
                    setMenuAction();
                    break;

                case R.id.imgSetting:
                    menuListView.performItemClick(view, 0, Utils.MENU_PROFILE);
                    break;

                case R.id.logoutarea:
                    final GenerateAlertBox generateAlert = new GenerateAlertBox(mContext);
                    generateAlert.setCancelable(false);
                    generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                        @Override
                        public void handleBtnClick(int btn_id) {
                            if (btn_id == 0) {
                                generateAlert.closeAlertBox();
                            } else {
                                MyApp.getInstance().logOutFromDevice(false);
                            }

                        }
                    });
                    generateAlert.setContentMessage(generalFunc.retrieveLangLBl("Logout", "LBL_LOGOUT"), generalFunc.retrieveLangLBl("Are you sure you want to logout?", "LBL_WANT_LOGOUT_APP_TXT"));
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_YES"));
                    generateAlert.setNegativeBtn(generalFunc.retrieveLangLBl("", "LBL_NO"));
                    generateAlert.showAlertBox();

                    break;
            }
        }
    }
}
