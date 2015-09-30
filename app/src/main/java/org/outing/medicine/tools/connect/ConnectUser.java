package org.outing.medicine.tools.connect;

import org.outing.medicine.logic.AnStatus;
import org.outing.medicine.tools.utils.DealPass;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;

/**
 * 用户相关的连接（登录注册密码处理等）
 */
public class ConnectUser extends Connect {

	public ConnectUser(Context context, boolean refreash) {
		super(context, refreash);
	}

	public ConnectUser(Context context) {// 用于退出登录
		super(context);
	}

	/**
	 * 验证码发送请求
	 * 
	 * @param name
	 *            用户名，已经过筛选处理的
	 * @return 状态信息
	 */
	public AnStatus getIDCode(String name) {
		JSONObject json = new JSONObject();
		json.put("account", name);
		String str_json = json.toString();
		String status_str = executePost(ServerURL.ID_CODE, str_json);
		if (status_str == null)
			return new AnStatus(false, "连接服务器失败");
		else if (status_str.equals("OK") || status_str.equals("OK\n"))
			return new AnStatus(true, "");
		else
			return new AnStatus(false, status_str);
	}

	/**
	 * 用户注册
	 * 
	 * @param name
	 *            用户名，已经过筛选处理的
	 * @param pass
	 *            明文密码
	 * @param time
	 *            请求时间
	 * @return 状态信息
	 */
	public AnStatus register(String name, String pass, String idcode) {
		String md5_pass = DealPass.getMD5(pass);
		String time = getTime();
		String safe_pass = DealPass.encodePass(md5_pass, time);
		JSONObject json = new JSONObject();
		json.put("account", name);
		json.put("pass", safe_pass);
		json.put("vCode", idcode);
		json.put("time", time);
		String str_json = json.toString();
		String status_str = executePost(ServerURL.REGISTER, str_json);
		if (status_str == null)
			return new AnStatus(false, "连接服务器失败");
		else if (status_str.equals("OK"))
			return new AnStatus(true, "");
		else
			return new AnStatus(false, status_str);
	}

	/**
	 * 用户登录
	 * 
	 * @param name
	 *            用户名
	 * @param pass
	 *            明文密码
	 * @param time
	 *            请求时间
	 * @return 状态信息
	 */
	public AnStatus login(String name, String pass) {
		String md5_pass = DealPass.getMD5(pass);
		return autoLogin(name, md5_pass);
	}

	/**
	 * 自动登录
	 * 
	 * @param name
	 *            用户名
	 * @param pass
	 *            本地读取的密码（已通过MD5加密）
	 * @param time
	 *            请求时间
	 * @return 状态信息
	 */
	public AnStatus autoLogin(String name, String pass) {
		String time = getTime();
		String safe_pass = DealPass.encodePass(pass, time);
		JSONObject json = new JSONObject();
		json.put("account", name);
		json.put("pass", safe_pass);
		json.put("time", time);
		String str_json = json.toString();
		String status_str = executePost(ServerURL.LOGIN, str_json);
		if (status_str == null)
			return new AnStatus(false, "连接服务器失败");
		else if (status_str.equals("OK"))
			return new AnStatus(true, "");
		else
			return new AnStatus(false, status_str);
	}

	/**
	 * 退出登录
	 * 
	 * @return 状态信息
	 */
	public void Logout() {
		executePost(ServerURL.LOGOUT, "");
	}

	/**
	 * 修改密码
	 * 
	 * @param oldpass
	 *            明文旧密码
	 * @param newpass
	 *            明文新密码
	 * @return 状态信息
	 */
	public AnStatus alterPass(String oldpass, String newpass) {
		String md5_oldpass = DealPass.getMD5(oldpass);
		String md5_newpass = DealPass.getMD5(newpass);
		String time = getTime();
		String safe_oldpass = DealPass.encodePass(md5_oldpass, time);
		String safe_newpass = DealPass.encodePass(md5_newpass, time);
		JSONObject json = new JSONObject();
		json.put("oldPass", safe_oldpass);
		json.put("newPass", safe_newpass);
		json.put("time", time);
		String str_json = json.toString();
		String status_str = executePost(ServerURL.ALERT_PASS, str_json);
		if (status_str == null)
			return new AnStatus(false, "连接服务器失败");
		else if (status_str.equals("OK"))
			return new AnStatus(true, "");
		else
			return new AnStatus(false, status_str);
	}

	/**
	 * 忘记密码
	 * 
	 * @param oldpass
	 *            验证码
	 * @param newpass
	 *            明文新密码
	 * @return 状态信息
	 */
	public AnStatus forgetPass(String name, String idcode, String newpass) {
		String md5_newpass = DealPass.getMD5(newpass);
		String time = getTime();
		String safe_newpass = DealPass.encodePass(md5_newpass, time);
		JSONObject json = new JSONObject();
		json.put("account", name);
		json.put("vCode", idcode);
		json.put("pass", safe_newpass);
		json.put("time", time);
		String str_json = json.toString();
		String status_str = executePost(ServerURL.FORGET_PASS, str_json);
		if (status_str == null)
			return new AnStatus(false, "连接服务器失败");
		else if (status_str.equals("OK"))
			return new AnStatus(true, "");
		else
			return new AnStatus(false, status_str);
	}

}
