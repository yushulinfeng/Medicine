package org.outing.medicine.personal_center;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.outing.medicine.R;
import org.outing.medicine.tools.TActivity;
import org.outing.medicine.tools.net.AutoString;
import org.outing.medicine.tools.net.NetThread;

import java.util.List;

/**
 * Created by apple on 15/10/4.
 */
public class PersonalCenterActivity extends TActivity {
    private LocationManager locationManager;
    private ToggleButton locationButton=null;
    private EditText editName,editSex,editAge,
            editIll,editLocation, editContact;
    private String name,age,sex,ill,location,contact;
    private String provider;
    private double latitude;
    private double longitude;
    private Handler hanSet;
    private String seturl="http://121.42.27.129/index.php/set_profile";

    @Override
    public void onCreate() {
        setContentView(R.layout.activity_person_center);
        showBackButton();
        showMenuButton();
        //设置完成的图片
        ((ImageButton) findViewById(R.id.top_menu))
                .setBackgroundResource(R.drawable.yes);
        init();

        //设置地理信息
        locationButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("test", "isChecked" + isChecked);
                Boolean weatherOn = isChecked;
                if (weatherOn) {
                    locationManager = (LocationManager) getSystemService(PersonalCenterActivity.this.LOCATION_SERVICE);
                    //获取所有可用位置提供器
                    List<String> providerList = locationManager.getProviders(true);
                    if (providerList.contains(LocationManager.GPS_PROVIDER)) {
                        provider = LocationManager.GPS_PROVIDER;
                    } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
                        provider = LocationManager.NETWORK_PROVIDER;
                    } else {
                        //当没有可用的位置提供器时，弹出Toast提示
                        Toast.makeText(PersonalCenterActivity.this, "请打开GPS或者网路", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Location location = locationManager.getLastKnownLocation(provider);
                    if (location != null) {
                        //得到经纬度
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.d("test", "latitude" + latitude + "longitude" + longitude);
                    }
                    locationManager.requestLocationUpdates(provider, 5000, 1, locationListener);
                } else {

                    if (locationManager != null) {
                        //关闭位置监听器
                        locationManager.removeUpdates(locationListener);
                        Toast.makeText(PersonalCenterActivity.this, "已关闭",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    @Override
    public void showContextMenu() {
        name=editName.getText().toString();
        sex= editSex.getText().toString();
        age= editAge.getText().toString();
        ill=editIll.getText().toString();
        location=editLocation.getText().toString();
        contact=editContact.getText().toString();
        AutoString autoString=new AutoString("name",name);
        autoString.addToResult("age",age);
        autoString.addToResult("sex",sex);
        autoString.addToResult("common_ill",ill);
        autoString.addToResult("emer_contact",contact);
        autoString.addToResult("position",location);
        String params=autoString.getResult();
        NetThread nt=new NetThread(hanSet,seturl,params);
        nt.start();

    }

    private void init() {
        editName=(EditText)findViewById(R.id.edit_name);
        editSex=(EditText)findViewById(R.id.edit_sex);
        editAge= (EditText) findViewById(R.id.edit_age);
        editIll= (EditText) findViewById(R.id.edit_ill);
        editLocation= (EditText) findViewById(R.id.edit_location);
        editContact=(EditText)findViewById(R.id.edit_contact);
        locationButton=(ToggleButton)findViewById(R.id.location_button);
        hanSet = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                String Jsmess = (String) msg.obj;
                super.handleMessage(msg);
                if(Jsmess.equals("0")){
                    showToast("上传成功");
                }else if (Jsmess.equals("-1")){
                    showToast("表单数据错误（未获取到phone数据）");
                }else if (Jsmess.equals("-2")){
                    showToast("找不到此用户");
                }
            }


        };
    }



    LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //更新经纬度
            latitude=location.getLatitude();
            longitude=location.getLongitude();
            Toast.makeText(PersonalCenterActivity.this,"实时监测",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };



}
