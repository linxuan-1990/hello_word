package com.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caliway.user.BookingSummaryActivity;
import com.caliway.user.CardPaymentActivity;
import com.caliway.user.R;
import com.caliway.user.VerifyCardTokenActivity;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.paymaya.sdk.android.payment.PayMayaPayment;
import com.paymaya.sdk.android.payment.models.PaymentToken;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.utils.CommonUtilities;
import com.utils.ModelUtils;
import com.utils.Utils;
import com.view.MButton;
import com.view.MaterialRippleLayout;
import com.view.MyProgressDialog;
import com.view.editBox.MaterialEditText;
import com.xendit.Models.XenditError;
import com.xendit.Xendit;

import java.util.Calendar;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddCardFragment extends Fragment implements TextWatcher {

    private static final char SPACE_CHAR = ' ';

    GeneralFunctions generalFunc;
    View view;

    CardPaymentActivity cardPayAct;

    BookingSummaryActivity bookingSummaryActivity;

    String userProfileJson;
    MButton btn_type2;
    MaterialEditText creditCardBox;
    MaterialEditText cvvBox;
    MaterialEditText mmBox;
    MaterialEditText yyBox;

    String required_str = "";
    public boolean isInProcessMode = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_card, container, false);

        if (getActivity() instanceof CardPaymentActivity) {
            cardPayAct = (CardPaymentActivity) getActivity();
            generalFunc = cardPayAct.generalFunc;
            userProfileJson = cardPayAct.userProfileJson;
        }
        if (getActivity() instanceof BookingSummaryActivity) {

            bookingSummaryActivity = (BookingSummaryActivity) getActivity();
            generalFunc = bookingSummaryActivity.generalFunc;
            userProfileJson = bookingSummaryActivity.userProfileJson;

        }

        btn_type2 = ((MaterialRippleLayout) view.findViewById(R.id.btn_type2)).getChildView();
        creditCardBox = (MaterialEditText) view.findViewById(R.id.creditCardBox);
        cvvBox = (MaterialEditText) view.findViewById(R.id.cvvBox);
        mmBox = (MaterialEditText) view.findViewById(R.id.mmBox);
        yyBox = (MaterialEditText) view.findViewById(R.id.yyBox);

        if (cardPayAct != null) {
            if (getArguments().getString("PAGE_MODE").equals("ADD_CARD")) {
                cardPayAct.changePageTitle(generalFunc.retrieveLangLBl("", "LBL_ADD_CARD"));
                btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_CARD"));
            } else {
                cardPayAct.changePageTitle(generalFunc.retrieveLangLBl("Change Card", "LBL_CHANGE_CARD"));
                btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_CHANGE_CARD"));
            }
        } else {

            if (getArguments().getString("PAGE_MODE").equals("ADD_CARD")) {
                btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_CARD"));
            } else {
                btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_CHANGE_CARD"));
            }

        }

        btn_type2.setId(Utils.generateViewId());
        btn_type2.setOnClickListener(new setOnClickList());

        setLabels();

        mmBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        yyBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        cvvBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        creditCardBox.setInputType(InputType.TYPE_CLASS_PHONE);

        creditCardBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(24)});
        mmBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
        cvvBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        yyBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

        creditCardBox.addTextChangedListener(this);


        return view;
    }

    public void setLabels() {

        creditCardBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_CARD_NUMBER_HEADER_TXT"), generalFunc.retrieveLangLBl("", "LBL_CARD_NUMBER_HINT_TXT"));
        cvvBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_CVV_HEADER_TXT"), generalFunc.retrieveLangLBl("", "LBL_CVV_HINT_TXT"));
        mmBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_EXP_MONTH_HINT_TXT"), generalFunc.retrieveLangLBl("", "LBL_EXP_MONTH_HINT_TXT"));
        yyBox.setBothText(generalFunc.retrieveLangLBl("", "LBL_EXP_YEAR_HINT_TXT"), generalFunc.retrieveLangLBl("", "LBL_EXP_YEAR_HINT_TXT"));

        required_str = generalFunc.retrieveLangLBl("", "LBL_FEILD_REQUIRD_ERROR_TXT");
    }

    public boolean validateExpYear(Calendar now) {
        return yyBox.getText().toString() != null && !ModelUtils.hasYearPassed(GeneralFunctions.parseIntegerValue(0,yyBox.getText().toString()), now);
    }

    public void checkDetails() {
        Card card = new Card(Utils.getText(creditCardBox), generalFunc.parseIntegerValue(0, Utils.getText(mmBox)),
                generalFunc.parseIntegerValue(0, Utils.getText(yyBox)), Utils.getText(cvvBox));

        Utils.printLog("Card No", ":" + card.validateNumber() + "::num::" + card.getNumber());
        boolean cardNoEntered = Utils.checkText(creditCardBox) ? (card.validateNumber() ? true :
                Utils.setErrorFields(creditCardBox, generalFunc.retrieveLangLBl("", "LBL_INVALID")))
                : Utils.setErrorFields(creditCardBox, required_str);
        boolean cvvEntered = Utils.checkText(cvvBox) ? (card.validateCVC() ? true :
                Utils.setErrorFields(cvvBox, generalFunc.retrieveLangLBl("", "LBL_INVALID"))) : Utils.setErrorFields(cvvBox, required_str);
        boolean monthEntered = Utils.checkText(mmBox) ? (card.validateExpMonth() ? true :
                Utils.setErrorFields(mmBox, generalFunc.retrieveLangLBl("", "LBL_INVALID"))) : Utils.setErrorFields(mmBox, required_str);
        boolean yearEntered = Utils.checkText(yyBox) ? (validateExpYear(Calendar.getInstance())? true :
                Utils.setErrorFields(yyBox, generalFunc.retrieveLangLBl("", "LBL_INVALID"))) : Utils.setErrorFields(yyBox, required_str);
        boolean yearEntedcount = true;
        if (yearEntered == true) {
            yearEntedcount = yyBox.getText().toString().length() == 4 ? true : Utils.setErrorFields(yyBox, generalFunc.retrieveLangLBl("", "LBL_INVALID"));
        }


        if (cardNoEntered == false || cvvEntered == false || monthEntered == false || yearEntered == false || yearEntedcount == false) {
            return;
        }


        if (generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Stripe")) {
            if (card.validateCard()) {
                generateStripeToken(card);
            }
        } else if (generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Paymaya")) {
            setProcessingMode();

            com.paymaya.sdk.android.payment.models.Card cardpaymaya = new com.paymaya.sdk.android.payment.models.Card(Utils.getText(creditCardBox).replaceAll(" ", ""), Utils.getText(mmBox),
                    Utils.getText(yyBox), Utils.getText(cvvBox));
            generatePayMayaToken(cardpaymaya);

        } else if (generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Xendit")) {

            com.xendit.Models.Card xenditcard = new com.xendit.Models.Card(Utils.getText(creditCardBox).replace(" ", ""),
                    Utils.getText(mmBox),
                    Utils.getText(yyBox),
                    Utils.getText(cvvBox));

            generateXenditToken(xenditcard);
        }
    }


    public void generateXenditToken(final com.xendit.Models.Card card) {

        final MyProgressDialog myPDialog = showLoader();
        String XENDIT_PUBLIC_KEY = generalFunc.getJsonValue("XENDIT_PUBLIC_KEY", userProfileJson);

        if (card == null) {

            return;
        }


        final Xendit xendit = new Xendit(getActContext(), XENDIT_PUBLIC_KEY);

        xendit.createMultipleUseToken(card, new com.xendit.TokenCallback() {
            @Override
            public void onSuccess(com.xendit.Models.Token token) {

                myPDialog.close();
                CreateCustomer(null, null, card, token.getId());

            }

            @Override
            public void onError(XenditError error) {
                myPDialog.close();

                generalFunc.showMessage(btn_type2, error.getErrorMessage());


            }
        });


    }

    public void generatePayMayaToken(final com.paymaya.sdk.android.payment.models.Card card) {

        new AsyncTask<String, String, PaymentToken>() {
            MyProgressDialog myPDialog = null;

            @Override
            protected PaymentToken doInBackground(String... strings) {
                String STRIPE_PUBLISH_KEY = generalFunc.getJsonValue("PAYMAYA_PUBLISH_KEY", userProfileJson);

                PayMayaPayment payMayaPayment = new PayMayaPayment(STRIPE_PUBLISH_KEY, card);
                PaymentToken paymentToken = payMayaPayment.getPaymentToken();

                return paymentToken;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                myPDialog = showLoader();
            }

            @Override
            protected void onPostExecute(PaymentToken paymentToken) {
//                super.onPostExecute(paymentToken);

                if (paymentToken != null) {
                    myPDialog.close();

                    CreateCustomer(null, card, null, paymentToken.getPaymentTokenId());
                } else {
                    closeProcessingMode();
                    myPDialog.close();
                    generalFunc.showError();


                }
            }

            /* @Override
            protected PaymentToken doInBackground(String... strings) {
                String STRIPE_PUBLISH_KEY = generalFunc.getJsonValue("PAYMAYA_PUBLISH_KEY", userProfileJson);

                PayMayaPayment payMayaPayment = new PayMayaPayment(STRIPE_PUBLISH_KEY, card);
                PaymentToken paymentToken = payMayaPayment.getPaymentToken();

                Utils.printLog("paymentToken","::"+paymentToken.getPaymentTokenId());
                return paymentToken.toString();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }*/
        }.execute();
    }

    public void setProcessingMode() {
        isInProcessMode = true;
        btn_type2.setText(generalFunc.retrieveLangLBl("Processing Payment", "LBL_PROCESS_PAYMENT_TXT"));
        creditCardBox.setEnabled(false);
        mmBox.setEnabled(false);
        yyBox.setEnabled(false);
        cvvBox.setEnabled(false);
        btn_type2.setEnabled(false);
    }


    public void closeProcessingMode() {
        try {
            isInProcessMode = false;
            if (getArguments().getString("PAGE_MODE").equals("ADD_CARD")) {
                btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_ADD_CARD"));
            } else {
                btn_type2.setText(generalFunc.retrieveLangLBl("", "LBL_CHANGE_CARD"));
            }
            creditCardBox.setEnabled(true);
            mmBox.setEnabled(true);
            yyBox.setEnabled(true);
            cvvBox.setEnabled(true);
            btn_type2.setEnabled(true);
        } catch (Exception e) {

        }
    }

    public MyProgressDialog showLoader() {
        MyProgressDialog myPDialog = new MyProgressDialog(getActContext(), false, generalFunc.retrieveLangLBl("Loading", "LBL_LOADING_TXT"));
        myPDialog.show();

        return myPDialog;
    }

    public void generateStripeToken(final Card card) {

        final MyProgressDialog myPDialog = showLoader();

        String STRIPE_PUBLISH_KEY = generalFunc.getJsonValue("STRIPE_PUBLISH_KEY", userProfileJson);
        Stripe stripe = new Stripe(getActContext());

        stripe.createToken(card, STRIPE_PUBLISH_KEY, new TokenCallback() {
            public void onSuccess(Token token) {
                // TODO: Send Token information to your backend to initiate a charge
                myPDialog.close();
                CreateCustomer(card, null, null, token.getId());
            }

            public void onError(Exception error) {
                myPDialog.close();
                generalFunc.showError();
            }
        });
    }

    public void CreateCustomer(Card card, com.paymaya.sdk.android.payment.models.Card payMayaCard, com.xendit.Models.Card xenditCard, String vStripeToken) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "GenerateCustomer");
        parameters.put("iUserId", generalFunc.getMemberId());

        if (card != null) {
            parameters.put("vStripeToken", vStripeToken);
            parameters.put("CardNo", Utils.maskCardNumber(card.getNumber()));
        }
        if (payMayaCard != null) {
            parameters.put("vPaymayaToken", vStripeToken);
            parameters.put("CardNo", Utils.maskCardNumber(payMayaCard.getNumber()));
        }
        if (xenditCard != null) {
            parameters.put("vXenditToken", vStripeToken);
            parameters.put("CardNo", Utils.maskCardNumber(xenditCard.getCreditCardNumber()));

        }

        parameters.put("UserType", Utils.app_type);


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Paymaya")) {
                closeProcessingMode();
            }

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);


                if (isDataAvail == true) {

                    if (generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Stripe") ||
                            generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Xendit")) {
                        generalFunc.storeData(Utils.USER_PROFILE_JSON, generalFunc.getJsonValue(Utils.message_str, responseString));
                        cardPayAct.changeUserProfileJson(generalFunc.getJsonValue(Utils.message_str, responseString));
                    } else if (generalFunc.getJsonValue("APP_PAYMENT_METHOD", userProfileJson).equalsIgnoreCase("Paymaya")) {

                        Bundle bn = new Bundle();

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("URL", generalFunc.getJsonValue(Utils.message_str, responseString));
                        map.put("card", card);
                        map.put("vPaymayaToken", vStripeToken);
                        map.put("vPageTitle", btn_type2.getText());
                        bn.putSerializable("data", map);

                        new StartActProcess(getActivity()).startActForResult(VerifyCardTokenActivity.class, bn, Utils.REQ_VERIFY_CARD_PIN_CODE);


                    }
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


    public void setdata(int requestCode, int resultCode, Intent data) {

        if (requestCode == Utils.REQ_VERIFY_CARD_PIN_CODE && resultCode == cardPayAct.RESULT_OK && data != null) {

            UpdateCustomerToken((HashMap<String, Object>) data.getSerializableExtra("data"));
        }
    }

    private void UpdateCustomerToken(HashMap<String, Object> data) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "UpdateCustomerToken");
        parameters.put("iUserId", generalFunc.getMemberId());
        parameters.put("vPaymayaToken", data.get("vPaymayaToken").toString());
        parameters.put("UserType", Utils.app_type);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            if (responseString != null && !responseString.equals("")) {

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                if (isDataAvail == true) {
                    generalFunc.storeData(Utils.USER_PROFILE_JSON, generalFunc.getJsonValue(Utils.message_str, responseString));
                    cardPayAct.changeUserProfileJson(generalFunc.getJsonValue(Utils.message_str, responseString));
                } else {
                    closeProcessingMode();
                    generalFunc.showGeneralMessage("",
                            generalFunc.retrieveLangLBl("", generalFunc.getJsonValue(Utils.message_str, responseString)));
                }
            } else {
                closeProcessingMode();
                generalFunc.showError();
            }
        });
        exeWebServer.execute();
    }


    public Context getActContext() {
        if (cardPayAct != null) {
            return cardPayAct.getActContext();
        }
        if (bookingSummaryActivity != null) {
            return bookingSummaryActivity.getActContext();
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.hideKeyboard(getActContext());
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0 && (s.length() % 5) == 0) {
            final char c = s.charAt(s.length() - 1);
            if (SPACE_CHAR == c) {
                s.delete(s.length() - 1, s.length());
            }
        }
        // Insert char where needed.
        if (s.length() > 0 && (s.length() % 5) == 0) {
            char c = s.charAt(s.length() - 1);
            // Only if its a digit where there should be a space we insert a space
            if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(SPACE_CHAR)).length <= 3) {
                s.insert(s.length() - 1, String.valueOf(SPACE_CHAR));
            }
        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(getActContext());
            int i = view.getId();
            if (i == btn_type2.getId()) {
                checkDetails();
            }
        }
    }
}
