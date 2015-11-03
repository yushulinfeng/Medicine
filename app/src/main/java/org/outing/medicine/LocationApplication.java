package org.outing.medicine;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.Poi;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.outing.medicine.tools.connect.Connect;
import org.outing.medicine.tools.connect.ConnectDialog;
import org.outing.medicine.tools.connect.ConnectList;
import org.outing.medicine.tools.connect.ConnectListener;
import org.outing.medicine.tools.connect.ServerURL;

import java.util.List;

/**
 * 主Application，所有百度定位SDK的接口说明请参考线上文档：http://developer.baidu.com/map/loc_refer/index.html
 *
 * 百度定位SDK官方网站：http://developer.baidu.com/map/index.php?title=android-locsdk
 */
public class LocationApplication extends Application {
    public LocationClient mLocationClient;
    public MyLocationListener mMyLocationListener;

    public TextView mLocationResult,logMsg;
    public TextView trigger,exit;
    private String  radius;
    private Boolean message,center=false;
    public void setCenter(Boolean center) {this.center = center;}
    public Vibrator mVibrator;

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
        //是否发了信息
        message=true;
        //获得半径
        SharedPreferences pref=getSharedPreferences("Radius", MODE_PRIVATE);
        radius=pref.getString("radius","100000000");
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
    }


    /**
     * 实现实时位置回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation location) {
            Log.d("test", "center"+center);
            //设置定位中心
            if (center){
                SharedPreferences.Editor editor=getSharedPreferences("Center", MODE_PRIVATE).edit();
                editor.putString("mLat1",""+location.getLatitude());
                editor.putString("mLon1",""+location.getLongitude());
                editor.commit();
                center=false;
                Log.d("test", "在设置了");
            }
            //获得中心坐标
            SharedPreferences pref=getSharedPreferences("Center", MODE_PRIVATE);
            double mLat1=Double.parseDouble(pref.getString("mLat1","39.915291"));
            double mLon1 = Double.parseDouble(pref.getString("mLon1","116.403857"));
            Log.d("test","mLat1"+mLat1+"    "+"mLon1"+mLon1);
            // 现在坐标
            double mLat2 = location.getLatitude();
            double mLon2 = location.getLongitude();
            LatLng pt_start = new LatLng(mLat1, mLon1);
            LatLng pt_end = new LatLng(mLat2, mLon2);
            Double distance= DistanceUtil.getDistance(pt_start, pt_end);
            Log.d("test",""+Double.parseDouble(radius));
            if (distance>Double.parseDouble(radius)&&message){
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("17865197355", null, "测试用短信", null, null);
                Log.d("test","已出地理围栏");
                message=false;
            }
            Log.d("test","DistanceUtil.getDistance(pt_start,pt_end);"+DistanceUtil.getDistance(pt_start,pt_end));
            //上传经纬度
            Connect.POST(getApplicationContext(), ServerURL.Post_Location, new ConnectListener() {


                @Override
                public ConnectList setParam(ConnectList list) {
                    list.put("longitude", "" + location.getLongitude());
                    list.put("latitude", "" + location.getLatitude());
                    return list;
                }

                @Override
                public void onResponse(String response) {

                }

                @Override
                public ConnectDialog showDialog(ConnectDialog dialog) {
                    return dialog;
                }
            });

            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlongtitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");// 位置语义化信息
            sb.append(location.getLocationDescribe());
            List<Poi> list = location.getPoiList();// POI信息
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            logMsg(sb.toString());
            Log.i("BaiduLocationApiDem", sb.toString());
        }


    }


    /**
     * 显示请求字符串
     * @param str
     */
    public void logMsg(String str) {
        try {
            if (mLocationResult != null)
                mLocationResult.setText(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
