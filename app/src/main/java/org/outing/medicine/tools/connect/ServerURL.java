package org.outing.medicine.tools.connect;

public class ServerURL {
    private static final String BASE_ADDRESS = "http://121.42.27.129/";

    //

    public static final String ID_CODE = BASE_ADDRESS + "send_code";

    public static final String FORGET_PASS = BASE_ADDRESS + "";
    public static final String ALERT_PASS = BASE_ADDRESS + "";

    public static final String LOGIN = BASE_ADDRESS + "login";
    public static final String LOGOUT = BASE_ADDRESS + "";
    public static final String REGISTER = BASE_ADDRESS + "register";

    //增加积分
    public static final String SCORE_ADD = BASE_ADDRESS + "add_score";

    //收藏药品
    public static final String DRUG_PUT_COLLECT = BASE_ADDRESS + "like_mdc";
	public static final String DRUG_GET_COLLECT = BASE_ADDRESS + "get_like_mdc";
	public static final String DRUG_CLEAR_COLLECT = BASE_ADDRESS + "drop_like_mdc";

    //文章获取
    public static final String KNOW_GET_ESSAYLIST = BASE_ADDRESS + "get_docs_list";
    public static final String KNOW_GET_ESSAYITEM = BASE_ADDRESS + "get_doc";

    //健康数据的接口
    public static final String Get_Body_Message=BASE_ADDRESS+"/get_data";
    public static final String Post_Body_Message=BASE_ADDRESS+"/post_data";
    public static final String Drop_Body_Message=BASE_ADDRESS+"/drop_data";
    public static final String Drop_All_Body_Message=BASE_ADDRESS+"/drop_all_data";

    //个人中心
    public static final String Get_Personal_Message=BASE_ADDRESS+"/get_profile";
    public static final String Set_Personal_Message=BASE_ADDRESS+"/set_profile";

    //防丢失
    public static final String Post_Location=BASE_ADDRESS+"/post_gps";

    //问卷
//    public static final String Questionnaire_Url="http://www.wenjuan.com/s/73a6Rz/";
    public static final String Questionnaire_Url="http://www.wenjuan.com/s/JJvUji/";
    public static final String Questionnaire_Result_Url="http://www.wenjuan.com/r/NJZr2q?pid=5629b6def7405b5c4a4b61d4&vcode=ddbe395a03040fe428ca10f73b140510";



}
