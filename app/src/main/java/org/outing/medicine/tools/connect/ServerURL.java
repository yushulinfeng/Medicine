package org.outing.medicine.tools.connect;

public class ServerURL {
    private static final String BASE_ADDRESS = "http://121.42.27.129/index.php";

    //

    public static final String ID_CODE = BASE_ADDRESS + "RequestVerifyCode";

    public static final String FORGET_PASS = BASE_ADDRESS + "ModifyServlet";
    public static final String ALERT_PASS = BASE_ADDRESS + "ModifyServlet";

    public static final String LOGIN = BASE_ADDRESS + "/login";
    public static final String LOGOUT = BASE_ADDRESS + "QuitServlet";
    public static final String REGISTER = BASE_ADDRESS + "RegisterUser";

    public static final String PLACE_ALL = BASE_ADDRESS + "getProvinces";
    public static final String PLACE_PRO = BASE_ADDRESS + "getCitiesByPro";
    public static final String PLACE_CITY = BASE_ADDRESS + "getAreaByCity";

    public static final String QUERY_WORK = BASE_ADDRESS + "";
    public static final String JOIN_WORK = BASE_ADDRESS + "";

    public static final String INFO_LOCATION = BASE_ADDRESS + "";

}
