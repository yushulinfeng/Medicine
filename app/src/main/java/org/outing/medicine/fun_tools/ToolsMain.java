package org.outing.medicine.fun_tools;

import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.outing.medicine.LocationApplication;
import org.outing.medicine.R;
import org.outing.medicine.tools.base.TActivity;

//小工具界面  
public class ToolsMain extends TActivity implements OnClickListener {
    private LocationApplication locationApplication;
    private LocationClient mLocationClient;

    @Override
    public void onCreate() {
        setContentView(R.layout.fun_tools_main);
        setTitle("常用工具");
        setTitleBackColor(R.color.btn_5_normal);
        locationApplication = (LocationApplication) getApplication();
        mLocationClient = ((LocationApplication) getApplication()).mLocationClient;
        showBackButton();

        initView();
    }

    private void initView() {
        Button btn_award = (Button) findViewById(R.id.fun_tools_award);
        btn_award.setOnClickListener(this);
        Button btn_calculator = (Button) findViewById(R.id.fun_tools_calculator);
        btn_calculator.setOnClickListener(this);
        Button btn_flashlight = (Button) findViewById(R.id.fun_tools_flashlight);
        btn_flashlight.setOnClickListener(this);
        Button btn_magnifiter = (Button) findViewById(R.id.fun_tools_magnifiter);
        btn_magnifiter.setOnClickListener(this);
        Button btn_where = (Button) findViewById(R.id.fun_tools_where);
        btn_where.setOnClickListener(this);
        Button btn_sos = (Button) findViewById(R.id.fun_tools_sos);
        btn_sos.setOnClickListener(this);
    }


    private void clickWhere() {
        initLocation();
        locationApplication.setMylocation(true);
        mLocationClient.start();//定位SDK start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request

        SharedPreferences.Editor editor = getSharedPreferences("JudgeTwoToggle", MODE_PRIVATE).edit();
        editor.putBoolean("locationButton", true);
        editor.commit();

        /*
        这里建议两种方式：
        1.使用百度地图的回调方法（推荐）
        mLocationClient.registerLocationListener(this); // 注册监听函数
        2.开启新线程等待
        在new Thread中while。
        3.有一点没看明白：
        百度地图这么灵活，为什么要写在Application中，只在里面initialize，
        其他的写个工具类多好。
        */
        int i = 0;
        while (i > -1) {
            try {
                if (locationApplication.getMylocationok() == true) {

                    Intent intent = new Intent(ToolsMain.this, MyLocation.class);
                    startActivity(intent);

                    locationApplication.setMylocationok(false);
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
            if (i == 5) {
                showToast("当前网络有问题，请重试");
                break;
            }
        }
    }

    private void clickSos() {
        initLocation();
        mLocationClient.start();//定位SDK start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request

        SharedPreferences.Editor editor = getSharedPreferences("JudgeTwoToggle", MODE_PRIVATE).edit();
        editor.putBoolean("locationButton", true);
        editor.commit();

        //获得联系人
        SharedPreferences pref1 = getSharedPreferences("PersonalCenter", MODE_PRIVATE);
        String usualContact = pref1.getString("contact", "");
        if (usualContact.equals("")) {
            Toast.makeText(getApplicationContext(), "请设置紧急联系人", Toast.LENGTH_SHORT).show();
        } else {
            SmsManager smsManager = SmsManager.getDefault();

            /*这里建议注册短信发送结果监听。
            BroadcastReceiver--SENT_SMS_ACTION和DELIVERED_SMS_ACTION
            这样发送被拒绝以及网络不好等导致发送失败就可以监听到，
            这些应该告知用户。
            */
            smsManager.sendTextMessage(usualContact, null, "sos!(不要慌。。这只是测试用短信)", null, null);
            Log.d("test", "usualContact" + usualContact);
            Toast.makeText(getApplicationContext(), "已发送sos短信", Toast.LENGTH_SHORT).show();
            Log.d("test", "已出地理围栏");

            locationApplication.setMessage(false);
        }
    }


    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.fun_tools_award:
                intent = new Intent(ToolsMain.this, Award.class);
                break;
            case R.id.fun_tools_calculator:
                intent = new Intent(ToolsMain.this, Calculator.class);
                break;
            case R.id.fun_tools_flashlight:
                intent = new Intent(ToolsMain.this, Flashlight.class);
                break;
            case R.id.fun_tools_magnifiter:
                intent = new Intent(ToolsMain.this, Magnifiter.class);
                break;
            case R.id.fun_tools_where:
                clickWhere();
                break;
            case R.id.fun_tools_sos:
                clickSos();
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    public void showContextMenu() {
    }
}
