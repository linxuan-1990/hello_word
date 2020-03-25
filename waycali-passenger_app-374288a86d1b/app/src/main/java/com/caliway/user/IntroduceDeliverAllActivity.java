package com.caliway.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import org.json.JSONObject;

public class IntroduceDeliverAllActivity extends AppCompatActivity {

    GeneralFunctions generalFunc;

    ImageView backImgView;
    MTextView titleTxt;
    MTextView introducingTxtView;
    MTextView noteTxtView;

    JSONObject userProfileJsonObj;
    MButton downloadNowBtn;

    ImageView detailBannerImgView;
    ImageView iconImgView;

    LinearLayout contentView;

    String screenType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce_deliver_all);

        generalFunc = new GeneralFunctions(getActContext());
        userProfileJsonObj = generalFunc.getJsonObject(generalFunc.retrieveValue(Utils.USER_PROFILE_JSON));

        backImgView = (ImageView) findViewById(R.id.backImgView);
        detailBannerImgView = (ImageView) findViewById(R.id.detailBannerImgView);
        iconImgView = (ImageView) findViewById(R.id.iconImgView);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        introducingTxtView = (MTextView) findViewById(R.id.introducingTxtView);
        noteTxtView = (MTextView) findViewById(R.id.noteTxtView);
        contentView = (LinearLayout) findViewById(R.id.contentView);

        downloadNowBtn = ((MaterialRippleLayout) findViewById(R.id.btn_type_1)).getChildView();

        screenType = getIntent().getStringExtra("ScreenType");

        setViewBG();

        backImgView.setOnClickListener(new setOnClick());

        downloadNowBtn.setId(Utils.generateViewId());
        downloadNowBtn.setOnClickListener(new setOnClick());
        setLabels();
        setData();
    }

    private void setViewBG() {
        switch (screenType.toLowerCase()) {
            case "food":
                contentView.setBackground(getResources().getDrawable(R.drawable.introduce_food_app_bg_repeat));
                break;
            case "grocery":
                contentView.setBackground(getResources().getDrawable(R.drawable.introduce_grocery_app_bg_repeat));
                break;
            default:
                contentView.setBackgroundColor(Color.parseColor("#363439"));
        }
    }

    private void setLabels() {
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_" + screenType.toUpperCase() + "_APP_DELIVERY"));
        noteTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_" + screenType.toUpperCase() + "_APP_DETAIL_NOTE"));
        downloadNowBtn.setText(generalFunc.retrieveLangLBl("", "LBL_" + screenType.toUpperCase() + "_APP_DOWNLOAD_NOW"));
        introducingTxtView.setText(generalFunc.retrieveLangLBl("", "LBL_" + screenType.toUpperCase() + "_APP_INTRODUCING"));
    }

    public void setData() {

        String bannerImageURL = Utils.getResizeImgURL(getActContext(), generalFunc.getJsonValueStr(screenType.toUpperCase() + "_APP_DETAIL_BANNER_IMG_NAME", userProfileJsonObj), Utils.getWidthOfBanner(getActContext(), 0), Utils.getHeightOfBanner(getActContext(), 0, "4:3"));

        String imageURL = Utils.getResizeImgURL(getActContext(), generalFunc.getJsonValueStr(screenType.toUpperCase() + "_APP_DETAIL_GRID_ICON_NAME", userProfileJsonObj), getResources().getDimensionPixelSize(R.dimen.category_detail_icon_size), getResources().getDimensionPixelSize(R.dimen.category_detail_icon_size));

        Picasso.with(getActContext()).load(bannerImageURL)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .fit().into(detailBannerImgView);

        Picasso.with(getActContext()).load(imageURL)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .fit().into(iconImgView);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) detailBannerImgView.getLayoutParams();

        layoutParams.height = Utils.getHeightOfBanner(getActContext(), Utils.dipToPixels(getActContext(), 20), "4:3");

    }

    private Context getActContext() {
        return IntroduceDeliverAllActivity.this;
    }

    public class setOnClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.backImgView) {
                IntroduceDeliverAllActivity.super.onBackPressed();
            } else if (i == downloadNowBtn.getId()) {

                String deliver_all_package_name = generalFunc.getJsonValueStr(screenType.toUpperCase() + "_APP_PACKAGE_NAME", userProfileJsonObj);
                String deliver_all_service_id = generalFunc.getJsonValueStr(screenType.toUpperCase() + "_APP_SERVICE_ID", userProfileJsonObj);

                if (Utils.isAppInstalled(getActContext(), deliver_all_package_name)) {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(deliver_all_package_name);
                    launchIntent.putExtra("DEFAULT_SERVICE_CATEGORY_ID", deliver_all_service_id);

                    if (launchIntent != null) {
                        try {
                            startActivity(launchIntent);
                        } catch (Exception e) {
                            downloadApp(deliver_all_package_name);
                        }
                    } else {
                        downloadApp(deliver_all_package_name);
                    }
                } else {
                    downloadApp(deliver_all_package_name);
                }
            }
        }
    }

    private void downloadApp(String deliver_all_package_name) {
        try {
            boolean isSuccessfulOpen = new StartActProcess(getActContext()).openURL("market://details?id=" + deliver_all_package_name);
            if (isSuccessfulOpen == false) {
                new StartActProcess(getActContext()).openURL("http://play.google.com/store/apps/details?id=" + deliver_all_package_name);
            }
        } catch (Exception e) {

        }
    }
}
