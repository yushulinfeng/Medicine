package org.outing.medicine.start.tool;


import android.content.Context;

import org.outing.medicine.tools.utils.ToastTool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SafeCheck {

    //字母数字下划线
    public static boolean isSafe(String input) {
        Pattern pat = Pattern.compile("\\W");
        Matcher mat = pat.matcher(input);
        if (mat.find())
            return false;
        return true;
    }

    // 判断是否为数字的方法
    public static boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static boolean isPhone(String input) {
        if (input.length() != 11)
            return false;
        return isNumber(input);
    }

    public static boolean checkLoginUser(Context context, String username) {
        if (username == null || username.equals("")) {
            showToast(context, "用户名不能为空");
            return false;
        }
        if (!isPhone(username)) {
            showToast(context, "登录失败。\n用户名或密码错误");
            return false;
        }
        if (!isSafe(username)) {// 字符检测
            showToast(context, "登录失败。\n用户名或密码错误");
            return false;
        }
        return true;
    }

    public static boolean checkLoginPass(Context context, String password) {
        if (password == null || password.equals("")) {
            showToast(context, "密码不能为空");
            return false;
        }
        if (password.length() < 6 || password.length() > 24) {
            showToast(context, "登录失败。\n用户名或密码错误");
            return false;
        }
        if (!isSafe(password)) {// 字符检测
            showToast(context, "登录失败。\n用户名或密码错误");
            return false;
        }
        return true;
    }

    public static boolean checkPass(Context context, String password,
                                    String passwordConfirm) {
        if (password == null || password.equals("")) {
            showToast(context, "密码不能为空");
            return false;
        }
        if (password.length() < 6) {
            showToast(context, "密码过短，\n请至少输入6位");
            return false;
        }
        if (password.length() > 20) {
            showToast(context, "密码过长，\n不能超过20位");
            return false;
        }
        if (!isSafe(password)) {// 字符检测
            showToast(context, "密码含有非法字符");
            return false;
        }
        if (password.equals(passwordConfirm)) {
            return true;
        } else {
            showToast(context, "两次密码不一致");
            return false;
        }
    }

    /**
     * 验证验证码是否为空已经是否正确
     */
    public static boolean checkCode(Context context, String verifyCode) {
        if (verifyCode == null || verifyCode.equals("")) {
            showToast(context, "验证码不能为空");
            return false;
        }
        return true;
    }

    private static void showToast(Context context, String content) {
        ToastTool.showToast(context, content);
    }
}
