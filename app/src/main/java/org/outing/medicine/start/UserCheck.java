package org.outing.medicine.start;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.widget.Toast;

public class UserCheck {

	/**
	 * 判断是否为电话号码
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isPhoneNumber(String str) {
		if (str.length() != 11)
			return false;
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 判断是否为邮箱
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isMailAddress(String str) {
		Pattern pattern = Pattern
				.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher isMail = pattern.matcher(str);
		if (isMail.matches()) {
			return true;
		}
		return false;
	}

	/**
	 * 判断输入字符是否符合密码格式。自允许大小写、数字、下划线。
	 * 
	 * @param input
	 *            输入的字符串。
	 * @return 该字符串是否符合密码格式，true为符合。
	 */
	public static boolean isPass(String input) {
		Pattern pat = Pattern.compile("\\W");
		Matcher mat = pat.matcher(input);
		if (mat.find())
			return false;
		return true;
	}

	/**
	 * 验证用户两次密码输入是否相同
	 * 
	 * @param verifyCode
	 * @return
	 */
	public static boolean verifyPassword(Context context, String password,
			String passwordConfirm) {
		if (password == null || password.equals("")) {
			showToast(context, "密码不能为空");
			return false;
		}
		if (password.length() < 7) {
			showToast(context, "密码过短，请至少输入7位密码");
			return false;
		}
		if (!isPass(password)) {// 字符检测
			showToast(context, "密码含有非法字符");
			return false;
		}
		if (password.equals(passwordConfirm)) {
			return true;
		} else {
			showToast(context, "两次密码输入不一致");
			return false;
		}
	}

	/**
	 * 验证用户名是否为空以及是否已被注册
	 * 
	 * @param username
	 * @return
	 */
	public static boolean verifyUsername(Context context, String username) {
		if (username == null || username.equals("")) {
			showToast(context, "账户不能为空");
			return false;
		}
		if (isPhoneNumber(username) || isMailAddress(username)) {
			return true;
		} else {
			showToast(context, "账户格式不正确");
			return false;
		}
	}

	/**
	 * 验证验证码是否为空已经是否正确
	 * 
	 * @param verifyCode
	 * @return
	 */
	public static boolean verifyCode(Context context, String verifyCode) {
		if (verifyCode == null || verifyCode.equals("")) {
			showToast(context, "验证码不能为空");
			return false;
		}
		return true;
	}

	/** toast显示 */
	private static void showToast(Context context, String content) {
		Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
	}
}
