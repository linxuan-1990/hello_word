package com.caliway.user;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.InternetConnection;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 08-03-18.
 */

public class Help_DetailsActivity extends AppCompatActivity {
    public GeneralFunctions generalFunc;
    MTextView titleTxt;
    ImageView backImgView;
    MTextView headerTitleTxt;
    MTextView contactTxt;
    MTextView descriptionTxt;
    MTextView categoryText;
    MTextView additionalCommentTxt;
    MTextView reasonContactTxt;
    MaterialEditText contentBox;
    MButton btn_type2;
    String required_str = "";

    LinearLayout categoryarea;
    View helpContactslayout;
    View view;

    String iHelpDetailId = "";
    String vTitle = "";
    String tAnswer = "";
    String eShowFrom = "";

    ArrayList<String> items_txt_category = new ArrayList<String>();
    android.support.v7.app.AlertDialog list_category;
    CardView cardView;
    InternetConnection intCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_details);

        generalFunc = new GeneralFunctions(getActContext());
        intCheck = new InternetConnection(getActContext());

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        descriptionTxt = (MTextView) findViewById(R.id.descriptionTxt);
        headerTitleTxt = (MTextView) findViewById(R.id.headerTitleTxt);
        contactTxt = (MTextView) findViewById(R.id.contactTxt);
        categoryText = (MTextView) findViewById(R.id.categoryText);
        additionalCommentTxt = (MTextView) findViewById(R.id.additionalCommentTxt);
        reasonContactTxt = (MTextView) findViewById(R.id.reasonContactTxt);
        contentBox = (MaterialEditText) findViewById(R.id.contentBox);
        categoryarea = (LinearLayout) findViewById(R.id.categoryarea);
        helpContactslayout = findViewById(R.id.helpContactslayout);

        //cardView = (CardView) findViewById(R.id.contactCardViewArea);
        view = (View) findViewById(R.id.view);
        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();

        btn_type2.setId(Utils.generateViewId());
        btn_type2.setOnClickListener(new setOnClickList());
        backImgView.setOnClickListener(new setOnClickList());
        categoryarea.setOnClickListener(new setOnClickList());

        iHelpDetailId = getIntent().getStringExtra("iHelpDetailId");
        vTitle = getIntent().getStringExtra("vTitle");
        tAnswer = getIntent().getStringExtra("tAnswer");
        eShowFrom = getIntent().getStringExtra("eShowFrom");

        if (eShowFrom.equalsIgnoreCase("Yes")) {
            helpContactslayout.setVisibility(View.VISIBLE);
        } else {
            helpContactslayout.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
            //cardView.setVisibility(View.GONE);
        }

        setLabels();
        getCategoryTitleList();

    }

    public void setLabels() {

        headerTitleTxt.setText(getIntent().getStringExtra("vTitle"));
        descriptionTxt.setText(Html.fromHtml(getIntent().getStringExtra("tAnswer")).toString());

        reasonContactTxt.setText(generalFunc.retrieveLangLBl("", "LBL_RES_TO_CONTACT") + ":");//LBL_SELECT_RES_TO_CONTACT
        titleTxt.setText(generalFunc.retrieveLangLBl("", "LBL_HEADER_HELP_TXT"));
        contactTxt.setText(generalFunc.retrieveLangLBl("", "LBL_CONTACT_SUPPORT_ASSISTANCE_TXT"));
        additionalCommentTxt.setText(generalFunc.retrieveLangLBl("", "LBL_ADDITIONAL_COMMENTS"));
        btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_SUBMIT_TXT")); //LBL_SEND_QUERY_BTN_TXT

        categoryText.setText(vTitle); //generalFunc.retrieveValue(Utils.vTitle)

        contentBox.setHint(generalFunc.retrieveLangLBl("", "LBL_CONTACT_US_WRITE_EMAIL_TXT"));
        //contentBox.setFloatingLabelText(generalFunc.retrieveLangLBl("Your Query", "LBL_YOUR_QUERY"));
        contentBox.setFloatingLabelAlwaysShown(true);

        contentBox.setSingleLine(false);
        contentBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        contentBox.setGravity(Gravity.TOP);

        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT");

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 2), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#989898"), categoryarea);
    }

    public void getCategoryTitleList() {
        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getHelpDetail");
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("appType", Utils.app_type);

        final ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                if (generalFunc.checkDataAvail(Utils.action_str, responseString) == true) {

                    JSONArray obj_arr = generalFunc.getJsonArray(Utils.message_str, responseString);

                    for (int i = 0; i < obj_arr.length(); i++) {
                        JSONObject obj_temp = generalFunc.getJsonObject(obj_arr, i);

                        items_txt_category.add(generalFunc.getJsonValue("vTitle", obj_temp.toString()));
                    }

                    CharSequence[] cs_category_txt = items_txt_category.toArray(new CharSequence[items_txt_category.size()]);

                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());

                    builder.setTitle(getSelectCategoryText());

                    builder.setItems(cs_category_txt, (dialog, item) -> {
                        // Do something with the selection

                        if (list_category != null) {
                            list_category.dismiss();
                        }

                        categoryText.setText(items_txt_category.get(item));

                    });
                    list_category = builder.create();
                } else {

                }
            } else {

            }
        });
        exeWebServer.execute();
    }

    public void submitQuery() {
        boolean contentEntered = Utils.checkText(contentBox) ? true : Utils.setErrorFields(contentBox, required_str);

        if (contentEntered == false) {
            return;
        }

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "submitTripHelpDetail");
        parameters.put("TripId", getIntent().getStringExtra("iTripId"));
        parameters.put("iMemberId", generalFunc.getMemberId());
        parameters.put("iHelpDetailId", iHelpDetailId);
        parameters.put("UserType", Utils.app_type);
        parameters.put("UserId", generalFunc.getMemberId());
        parameters.put("vComment", Utils.getText(contentBox));

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {
                    //generalFunc.showGeneralMessage("",
                    //        generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));

                    final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                    generateAlert.setCancelable(false);
                    generateAlert.setBtnClickList(btn_id -> generateAlert.closeAlertBox());
                    generateAlert.setContentMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                    generateAlert.showAlertBox();

                    contentBox.setText("");
                } else {
                    //generalFunc.showGeneralMessage("",
                    //        generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));

                    final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                    generateAlert.setCancelable(false);
                    generateAlert.setBtnClickList(btn_id -> generateAlert.closeAlertBox());
                    generateAlert.setContentMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                    generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));
                    generateAlert.showAlertBox();
                }
            } else {
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }

    public void showLanguageList() {
        list_category.show();
    }

    public String getSelectCategoryText() {
        return ("" + generalFunc.retrieveLangLBl("", "LBL_SELECT_RES_TO_CONTACT"));
    }

    public Context getActContext() {
        return Help_DetailsActivity.this;
    }

    public class setOnClickList implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard(getActContext());
            if (i == R.id.backImgView) {
                Help_DetailsActivity.super.onBackPressed();
            } else if (i == btn_type2.getId()) {
                submitQuery();
            } else if (i == R.id.categoryarea) {
                showLanguageList();
            }
        }
    }

}
