package org.outing.medicine.tools.connect;

import android.content.Context;

/**
 * 用户相关的连接（登录注册密码处理等）
 */
public class ConnectUser extends Connect {

    public ConnectUser(Context context, boolean refresh) {
        super(context, refresh);
    }

    public ConnectUser(Context context) {// 用于退出登录
        super(context);
    }

    /**
     * 验证码发送请求
     *
     * @param phone 用户名，已经过筛选处理的
     * @return 状态信息
     */
    public AnStatus getIDCode(String phone) {
        ConnectList list = ConnectList.getSimpleList("phone", phone);//////////////////
        String status_str = executePost(ServerURL.ID_CODE, list);
        if (status_str == null)
            return new AnStatus(false, "连接服务器失败");
        else if (status_str.equals("1"))
            return new AnStatus(true, "");
        else
            return new AnStatus(false, status_str);
    }

    /**
     * 用户注册
     *
     * @param name 用户名，已经过筛选处理的
     * @param pass 明文密码
     * @param code 验证码
     * @return 状态信息
     */
    public AnStatus register(String name, String pass, String code) {
        ConnectList list = new ConnectList();
        list.put("phone", name);
        list.put("password", pass);
        list.put("code", code);
        String status_str = executePost(ServerURL.REGISTER, list);
        return dealRegisterResult(status_str);
    }

    /**
     * 用户登录
     *
     * @param name 用户名
     * @param pass 明文密码
     * @return 状态信息
     */
    public AnStatus login(String name, String pass) {
//		String md5_pass = DealPass.getMD5(pass);
        String md5_pass = (pass);
        return autoLogin(name, md5_pass);
    }

    /**
     * 自动登录
     *
     * @param name 用户名
     * @param pass 本地读取的密码//（已通过解密）
     * @return 状态信息
     */
    public AnStatus autoLogin(String name, String pass) {
        ConnectList list = new ConnectList();
        list.put("phone", name);
        list.put("password", pass);
        String status_str = executePost(ServerURL.LOGIN, list);
        return dealLoginResult(status_str);
    }

    /**
     * 退出登录
     *
     * @return 状态信息
     */
    public void Logout() {
        executePost(ServerURL.LOGOUT, null);////////////////////////////////
    }

    /**
     * 修改密码
     *
     * @param oldpass 明文旧密码
     * @param newpass 明文新密码
     * @return 状态信息
     */
    public AnStatus alterPass(String oldpass, String newpass) {
        return null;
        //////////////////////////////////////
    }

    /**
     * 忘记密码
     *
     * @param name    账号
     * @param code    验证码
     * @param newpass 明文新密码
     * @return 状态信息
     */
    public AnStatus forgetPass(String name, String code, String newpass) {
        return null;
        ///////////////////////////////////////
    }

    private AnStatus dealLoginResult(String postReturn) {
        if (postReturn == null)
            return new AnStatus(false, "连接服务器失败");
        else {
            try {
                int result = Integer.parseInt(postReturn);
                if (result > 0)
                    return new AnStatus(true, "");
                else if (result == -1)
                    return new AnStatus(true, "登录失败");
                else//-2
                    return new AnStatus(true, "用户名或密码错误");
            } catch (Exception e) {
                return new AnStatus(false, "系统错误");
            }
        }
    }

    private AnStatus dealRegisterResult(String postReturn) {
        if (postReturn == null)
            return new AnStatus(false, "连接服务器失败");
        else {
            try {
                int result = Integer.parseInt(postReturn);
                if (result > 0)
                    return new AnStatus(true, "");
                else if (result == -1) {
                    return new AnStatus(true, "注册失败");
                } else if (result == -2)
                    return new AnStatus(true, "手机号已被注册");
                else//-3
                    return new AnStatus(true, "密码过短");
            } catch (Exception e) {
                return new AnStatus(false, "系统错误");
            }
        }
    }
}
