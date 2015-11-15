package org.outing.medicine.tools.map;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;

public class Location implements BDLocationListener {
	private LocationClient mLocationClient = null;
	private LocationListener listener;
	private Context context;
	private String location_name;
	double x, y;

	private Location(Context applationContext, LocationListener listener) {
		this.context = applationContext;
		this.listener = listener;
	}

	private void initLocation() {
		SDKInitializer.initialize(context);// 请在界面之前调用一次
		mLocationClient = new LocationClient(context); // 声明LocationClient类
		mLocationClient.registerLocationListener(this); // 注册监听函数
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系(要么是百度坐标，或者是国测局02坐标)
		option.setScanSpan(0);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		if (location.getLocType() == BDLocation.TypeGpsLocation
				|| location.getLocType() == BDLocation.TypeNetWorkLocation
				|| location.getLocType() == BDLocation.TypeOffLineLocation) {// 定位成功
			location_name = location.getAddrStr();
			x = location.getLongitude();
			y = location.getLatitude();
		} else {
			location_name = "";
			x = 0;
			y = 0;
		}
		if (listener != null)
			listener.locationRespose(location_name, x, y);
	}

	// ///////////////////////基于回调的方法///////////////////////
	/**
	 * 获取位置信息
	 * 
	 * @param applationContext
	 *            全局的context
	 * @param listener
	 *            监听回调
	 */
	public static void getLocation(Context applationContext,
			LocationListener listener) {
		new Location(applationContext, listener).initLocation();
	}
}