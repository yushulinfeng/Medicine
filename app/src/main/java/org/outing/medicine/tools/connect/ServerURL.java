package org.outing.medicine.tools.connect;

public class ServerURL {
    private static final String BASE_ADDRESS = "http://121.42.27.129/index.php";

    //

    public static final String ID_CODE = BASE_ADDRESS + "/send_code";

    public static final String FORGET_PASS = BASE_ADDRESS + "/";
    public static final String ALERT_PASS = BASE_ADDRESS + "/";

    public static final String LOGIN = BASE_ADDRESS + "/login";
    public static final String LOGOUT = BASE_ADDRESS + "/";
    public static final String REGISTER = BASE_ADDRESS + "/register";

    public static final String SCORE_ADD = BASE_ADDRESS + "/add_score";

    //健康数据的接口
    public static final String Get_Body_Message=BASE_ADDRESS+"/get_data";
    public static final String Post_Body_Message=BASE_ADDRESS+"/post_data";

    //个人中心

    public static final String Get_Personal_Message=BASE_ADDRESS+"/get_profile";
    public static final String Set_Personal_Message=BASE_ADDRESS+"/set_profile";

    //防丢失
    public static final String Post_Location=BASE_ADDRESS+"/post_gps";



}
