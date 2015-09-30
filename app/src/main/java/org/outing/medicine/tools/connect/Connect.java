package org.outing.medicine.tools.connect;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;//API 19很正常，但到API 22就不推荐此类了
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;

import android.content.Context;

/**
 * 安卓与后台连接类，本类中所有方法必须在多线程中执行<br>
 * （静态方法将影响封装与后期拓展，此处用普通方法）
 * 
 * @author Sun Yu Lin
 */
@SuppressWarnings("deprecation")
public class Connect {
	private static final String BODY_JSON = "json";// 请求与响应的内容键
	private static String JSP_COOKIE = null;// 维持会话的cookie
	private Context context = null;

	/** 使用旧cookie */
	public Connect(Context context) {
		this.context = context;
		JSP_COOKIE = ConnectTool.getCookie(context);
	}

	/** 刷新cookie */
	public Connect(Context context, boolean refreash) {
		this.context = context;
	}

	/**
	 * 以协定方式执行post连接后台，发送head与body并接收后台返回。
	 * 
	 * @param url
	 *            连接地址
	 * @param head
	 *            头信息
	 * @param body
	 *            主体信息
	 * @return 后台返回的字符串信息
	 */
	protected String executePost(String url, String body) {
		final int COONECT_TIME_OUT = 15000;// 设定连接超时15秒
		final int READ_TIME_OUT = 30000;// 设定读取超时为30秒
		try {
			body = URLEncoder.encode(body, "UTF-8");
		} catch (Exception e) {
			return null;
		}
		BufferedReader in = null;
		try {
			// 定义HttpClient，实例化Post方法
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost(url);
			// 设定超时，超时将以异常形式提示
			client.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, COONECT_TIME_OUT);// 请求超时
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
					READ_TIME_OUT);// 读取超时
			// 添加cookie信息
			if (JSP_COOKIE != null)// 若为null，则不添加，等待服务器返回
				request.addHeader("Cookie", JSP_COOKIE);
			// 添加body信息
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair(BODY_JSON, body));
			UrlEncodedFormEntity formEntiry = new UrlEncodedFormEntity(
					parameters);
			request.setEntity(formEntiry);
			// 执行请求
			HttpResponse response = client.execute(request);

			// cookie处理，维护会话
			Header head = response.getFirstHeader("set-Cookie");
			if (head != null) {
				JSP_COOKIE = head.getValue();
				ConnectTool.saveCookie(context, JSP_COOKIE);
			}

			// 接收返回
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			while ((line = in.readLine()) != null) {
				sb.append(line + "\n");
			}
			in.close();
			String result = sb.toString();
			result = URLDecoder.decode(result, "UTF-8");
			if (result.equals(""))
				return null;// 后台返回""则返回null。
			return result.trim();
		} catch (Exception e) {// 很有可能是请求超时了
			// e.printStackTrace();
			return null;
		} finally {// 这个在finally中很有必要
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 获取当前系统时间
	 * 
	 * @return 当前系统时间
	 */
	protected String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
		String time = sdf.format(new Date());
		return time;
	}

}
