package org.outing.medicine.personal_center;

import android.app.ProgressDialog;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.outing.medicine.R;
import org.outing.medicine.tools.TActivity;
import org.outing.medicine.tools.connect.ConnectTool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private String geturl="http://121.42.27.129/index.php/get_profile";

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

    private void showPersonalInfo() {
        //volley试验
        RequestQueue mQueue = Volley.newRequestQueue(PersonalCenterActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,geturl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", response);
                        //获取JSON对象
                        JSONObject temp = null;
                        try {
                            temp = new JSONObject(response);
                            //得到数据
                            String name_in = temp.getString("name");
                            String sex_in=temp.getString("sex");
                            String age_in=temp.getString("age");
                            String ill_in=temp.getString("common_ill");
                            String contact_in=temp.getString("emer_contact");
                            String address_in=temp.getString("address");
                            Log.e("TAG", "name_in    "+name_in);
                            Log.e("TAG", "sex_in    "+sex_in);
                            if (!(name_in .equals("null"))){
                                editName.setText(name_in);
                            }
                            if (!(sex_in.equals("null"))){
                                editSex.setText(sex_in);
                            }
                            if (!age_in.equals("null")){
                                editAge.setText(age_in);
                            }
                            if (!ill_in.equals("null")){
                                editIll.setText(ill_in);
                            }
                            if (!contact_in.equals("null")){
                                editContact.setText(contact_in);
                            }
                            if (!address_in.equals("null")){
                                editLocation.setText(address_in
                                );
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("cookie", ConnectTool.getCookie(PersonalCenterActivity.this));
                Log.e("TAG", " ConnectTool.getCookie(PersonalCenterActivity.this)    " +  ConnectTool.getCookie(PersonalCenterActivity.this));
                // MyLog.d(TAG, "headers=" + headers);
                return headers;
            }
        };
        mQueue.add(stringRequest);
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

//volley试验
        RequestQueue mQueue = Volley.newRequestQueue(PersonalCenterActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,seturl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("cookie", ConnectTool.getCookie(PersonalCenterActivity.this));
                Log.e("TAG", " ConnectTool.getCookie(PersonalCenterActivity.this)    " +  ConnectTool.getCookie(PersonalCenterActivity.this));
                // MyLog.d(TAG, "headers=" + headers);
                return headers;
            }

                        @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("age",age);
                params.put("sex",sex);
                params.put("common_ill",ill);
                params.put("emer_contact",contact);
                params.put("position",location);
                return params;
            }
        };
        mQueue.add(stringRequest);
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("name", name);
//                params.put("age",age);
//                params.put("sex",sex);
//                params.put("common_ill",ill);
//                params.put("emer_contact",contact);
//                params.put("position",location);
//                return params;
//            }



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
