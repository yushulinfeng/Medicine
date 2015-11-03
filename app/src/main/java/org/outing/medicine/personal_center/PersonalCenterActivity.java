package org.outing.medicine.personal_center;


import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ToggleButton;


import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;


import org.json.JSONObject;
import org.outing.medicine.LocationApplication;
import org.outing.medicine.R;
import org.outing.medicine.contact.AnContact;
import org.outing.medicine.contact.ContactTool;
import org.outing.medicine.fun_tools.WidgetImage;
import org.outing.medicine.fun_tools.WidgetShow;
import org.outing.medicine.fun_tools.WidgetTool;
import org.outing.medicine.tools.TActivity;
import org.outing.medicine.tools.connect.Connect;
import org.outing.medicine.tools.connect.ConnectDialog;
import org.outing.medicine.tools.connect.ConnectList;
import org.outing.medicine.tools.connect.ConnectListener;
import org.outing.medicine.tools.connect.ServerURL;


/**
 * Created by apple on 15/10/4.
 */
public class PersonalCenterActivity extends TActivity {
    private LocationClient mLocationClient;
    private ToggleButton locationButton=null,contactButton=null;
    private Button centerBtn;
    private EditText editName,editSex,editAge,
            editIll,editLocation, editContact,editradius;
    private String name,age,sex,ill,location,contact,radius;
    private String provider;
    private double latitude;
    private double longitude;
    private Handler hanSet;
    private WidgetImage wid;
    private LocationApplication locationApplication;

    @Override
    public void onCreate() {
        setContentView(R.layout.activity_person_center);
        setTitle("我的设置");
        locationApplication= (LocationApplication) getApplication();
        showBackButton();
        showMenuButton();
        try{
            showPersonalInfo();
        }catch (Exception e){
            showToast("加载个人信息异常，请联网使用");
        }
        //设置完成的图片
        ((ImageButton) findViewById(R.id.top_menu))
                .setBackgroundResource(R.drawable.yes);
        init();
        mLocationClient = ((LocationApplication)getApplication()).mLocationClient;
        SharedPreferences pref=getSharedPreferences("JudgeTwoToggle", MODE_PRIVATE);
        //设置两个按钮初始值
        locationButton.setChecked( pref.getBoolean("locationButton",false));
        contactButton.setChecked(pref.getBoolean("contactButton",false));
        //设置地理信息
        locationButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("test", "isChecked" + isChecked);
                Boolean weatherOn = isChecked;
                if (weatherOn) {

                    try{
                        initLocation();
                        mLocationClient.start();//定位SDK start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request
                        SharedPreferences.Editor editor=getSharedPreferences("JudgeTwoToggle", MODE_PRIVATE).edit();
                        editor.putBoolean("locationButton", true);
                        editor.commit();

                    }catch (Exception e){
                        showToast("定位功能请联网使用");
                    }


                }else {
                    mLocationClient.stop();
                    SharedPreferences.Editor editor=getSharedPreferences("JudgeTwoToggle", MODE_PRIVATE).edit();
                    editor.putBoolean("locationButton", false);
                    editor.commit();
                }
            }
        });

        //设置联系人桌面
        contactButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("test", "isChecked" + isChecked);
                Boolean weatherOn = isChecked;
                AnContact contact = ContactTool.getAnContact(PersonalCenterActivity.this,0 );
                Log.d("test", "contact" +contact.getName());
                if (weatherOn) {
                    WidgetTool.saveWidText(PersonalCenterActivity.this, "紧急联系人电话"+"\n"+editContact.getText().toString());
                    WidgetShow.updatewidget(PersonalCenterActivity.this);
                    wid.showTextOnWallPaper(PersonalCenterActivity.this,  "紧急联系人电话", editContact.getText().toString());
                    SharedPreferences.Editor editor=getSharedPreferences("JudgeTwoToggle", MODE_PRIVATE).edit();
                    editor.putBoolean("contactButton", true);
                    editor.commit();
                }else {
                    WidgetTool.saveWidText(PersonalCenterActivity.this, "老友网");
                    WidgetShow.updatewidget(PersonalCenterActivity.this);
                    wid.hideTextOnWallPaper(PersonalCenterActivity.this);
                    SharedPreferences.Editor editor=getSharedPreferences("JudgeTwoToggle", MODE_PRIVATE).edit();
                    editor.putBoolean("contactButton", false);
                    editor.commit();
                }
            }
        });

    }

    private void showPersonalInfo() {

        Connect.POST(this, ServerURL.Get_Personal_Message, new ConnectListener() {
            @Override
            public ConnectList setParam(ConnectList list) {
                list.put("type", "3");
                return list;
            }

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "" + response);
                JSONObject temp = null;
                try {
                    temp = new JSONObject(response);
                    //得到数据
                    String name_in = temp.getString("name");
                    String sex_in = temp.getString("sex");
                    String age_in = temp.getString("age");
                    String ill_in = temp.getString("common_ill");
                    String contact_in = temp.getString("emer_contact");
                    String address_in = temp.getString("address");
                    //获得半径
                    SharedPreferences pref=getSharedPreferences("Radius", MODE_PRIVATE);
                    radius= pref.getString("radius", "100000000");
                    if (!(name_in.equals("null"))) {
                        editName.setText(name_in);
                    }
                    if (!(sex_in.equals("null"))) {
                        editSex.setText(sex_in);
                    }
                    if (!age_in.equals("null")) {
                        editAge.setText(age_in);
                    }
                    if (!ill_in.equals("null")) {
                        editIll.setText(ill_in);
                    }
                    if (!contact_in.equals("null")) {
                        editContact.setText(contact_in);
                    }
                    if (!address_in.equals("null")) {
                        editLocation.setText(address_in
                        );
                    }
                    if (!radius.equals("100000000")) {
                       editradius.setText(radius);
                    }


                } catch (Exception e) {

                }
            }

            @Override
            public ConnectDialog showDialog(ConnectDialog dialog) {
                dialog.config(PersonalCenterActivity.this, "标题", "内容", true);
                return dialog;
            }
        });
    }

    @Override
    public void showContextMenu() {
        showToast("按下按钮");
        name=editName.getText().toString();
        sex= editSex.getText().toString();
        age= editAge.getText().toString();
        ill=editIll.getText().toString();
        location=editLocation.getText().toString();
        contact=editContact.getText().toString();
        radius=editradius.getText().toString();
        try{
            Double.parseDouble(radius);
            SharedPreferences.Editor editor=getSharedPreferences("Radius", MODE_PRIVATE).edit();
            editor.putString("radius", radius);
        }catch (Exception e){
            showToast("请输入正确半径");
        }



        Connect.POST(this, ServerURL.Set_Personal_Message, new ConnectListener() {


            @Override
            public ConnectList setParam(ConnectList list) {
                list.put("name",name);
                list.put("age",age);
                list.put("sex",sex);
                list.put("common_ill",ill);
                list.put("emer_contact",contact);
                list.put("address",location);
                return list;
            }

            @Override
            public void onResponse(String response) {
                Log.d("TAG", response);
                if (response.equals("0")){
                    finish();
                }
            }

            @Override
            public ConnectDialog showDialog(ConnectDialog dialog) {
                dialog.config(PersonalCenterActivity.this, "标题", "内容", true);
                return dialog;
            }
        });


    }


    private void init() {
        editName=(EditText)findViewById(R.id.edit_name);
        editSex=(EditText)findViewById(R.id.edit_sex);
        editAge= (EditText) findViewById(R.id.edit_age);
        editIll= (EditText) findViewById(R.id.edit_ill);
        editLocation= (EditText) findViewById(R.id.edit_location);
        editContact=(EditText)findViewById(R.id.edit_contact);
        editradius= (EditText) findViewById(R.id.edit_radius);
        locationButton=(ToggleButton)findViewById(R.id.location_button);
        contactButton= (ToggleButton) findViewById(R.id.contact_button);
        centerBtn= (Button) findViewById(R.id.center_button);
        wid = new WidgetImage();
        centerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationApplication.setCenter(true);

            }
        });
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mLocationClient.setLocOption(option) ;
    }

}
