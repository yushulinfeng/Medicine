package org.outing.medicine.personal_center;

import android.os.Handler;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.json.JSONObject;
import org.outing.medicine.LocationApplication;
import org.outing.medicine.R;
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
    private ToggleButton locationButton=null;
    private EditText editName,editSex,editAge,
            editIll,editLocation, editContact;
    private String name,age,sex,ill,location,contact;
    private String provider;
    private double latitude;
    private double longitude;
    private Handler hanSet;

    @Override
    public void onCreate() {
        setContentView(R.layout.activity_person_center);
        showBackButton();
        showMenuButton();
        showPersonalInfo();
        //设置完成的图片
        ((ImageButton) findViewById(R.id.top_menu))
                .setBackgroundResource(R.drawable.yes);
        init();
        mLocationClient = ((LocationApplication)getApplication()).mLocationClient;
        //设置地理信息
        locationButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("test", "isChecked" + isChecked);
                Boolean weatherOn = isChecked;
                if (weatherOn) {
                    initLocation();
                    mLocationClient.start();//定位SDK start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request

                }else {
                    mLocationClient.stop();
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
                    Log.e("TAG", "response:" + response);//一切尽在不言中
                    temp = new JSONObject(response);
                    Log.e("TAG", "response:" + temp.toString());
                    //得到数据
                    String name_in = temp.getString("name");
                    String sex_in = temp.getString("sex");
                    String age_in = temp.getString("age");
                    String ill_in = temp.getString("common_ill");
                    String contact_in = temp.getString("emer_contact");
                    String address_in = temp.getString("address");
                    Log.e("TAG", "name_in    " + name_in);
                    Log.e("TAG", "sex_in    " + sex_in);
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


                } catch (Exception e) {

                }
                Toast.makeText(PersonalCenterActivity.this, response, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(PersonalCenterActivity.this, response, Toast.LENGTH_SHORT).show();
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
        locationButton=(ToggleButton)findViewById(R.id.location_button);
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
