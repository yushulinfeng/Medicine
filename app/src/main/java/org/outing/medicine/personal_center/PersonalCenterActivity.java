package org.outing.medicine.personal_center;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.outing.medicine.R;

import java.util.List;

/**
 * Created by apple on 15/10/4.
 */
public class PersonalCenterActivity extends Activity {
    private LocationManager locationManager;
    private ToggleButton locationButton=null;
    private String provider;
    private double latitude;
    private double longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_center);

        init();
        //设置地理信息
        locationButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("test","isChecked"+isChecked);
                Boolean weatherOn=isChecked;
                if (weatherOn){
                    locationManager=(LocationManager)getSystemService(PersonalCenterActivity.this.LOCATION_SERVICE);
                    //获取所有可用位置提供器
                    List<String> providerList=locationManager.getProviders(true);
                    if (providerList.contains(LocationManager.GPS_PROVIDER)){
                        provider=LocationManager.GPS_PROVIDER;
                    }else if(providerList.contains(LocationManager.NETWORK_PROVIDER)){
                        provider=LocationManager.NETWORK_PROVIDER;
                    }else {
                        //当没有可用的位置提供器时，弹出Toast提示
                        Toast.makeText(PersonalCenterActivity.this,"请打开GPS或者网路",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Location location=locationManager.getLastKnownLocation(provider);
                    if (location!=null){
                        //得到经纬度
                        latitude=location.getLatitude();
                        longitude=location.getLongitude();
                        Log.d("test","latitude"+latitude+"longitude"+longitude);
                    }
                    locationManager.requestLocationUpdates(provider,5000,1,locationListener);
                }else {

                    if (locationManager!=null){
                        //关闭位置监听器
                        locationManager.removeUpdates(locationListener);
                        Toast.makeText(PersonalCenterActivity.this, "已关闭",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });





    }

    private void init() {
        locationButton=(ToggleButton)findViewById(R.id.location_button);
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
