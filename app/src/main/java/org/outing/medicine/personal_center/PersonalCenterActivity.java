package org.outing.medicine.personal_center;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import org.outing.medicine.R;

import java.util.List;

/**
 * Created by apple on 15/10/4.
 */
public class PersonalCenterActivity extends Activity {
    private LocationManager locationManager;
    private String provider;
    private double latitude;
    private double longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_center);

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
        }


//        if (locationManager!=null){
//            //关闭位置监听器
//            locationManager.removeUpdates(locationListener);
//        }
    }

    LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //更新经纬度
            latitude=location.getLatitude();
            longitude=location.getLongitude();
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
