package com.utils;

public class CommonUtilities {
    public static final String app_name = "PassengerApp";
    public static final String MINT_APP_ID = "b91e6fa3";
    public static final String package_name = "com.caliway.user";

    public static final String TOLLURL = "https://tce.cit.api.here.com/2/calculateroute.json?app_id=";

    public static final String SERVER = "https://www.caliwayapp.com/";
    public static final String SERVER_FOLDER_PATH = "";
    public static final String SERVER_WEBSERVICE_PATH = SERVER_FOLDER_PATH + "webservice.php";

    public static final String SERVER_URL = SERVER + SERVER_FOLDER_PATH;
    public static final String SERVER_URL_WEBSERVICE = SERVER + SERVER_WEBSERVICE_PATH + "?";
    public static final String SERVER_URL_PHOTOS = SERVER_URL + "webimages/";

    public static final String USER_PHOTO_PATH = SERVER_URL_PHOTOS + "upload/Passenger/";
    public static final String PROVIDER_PHOTO_PATH = SERVER_URL_PHOTOS + "upload/Driver/";
}
