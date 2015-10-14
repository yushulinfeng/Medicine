package org.outing.medicine.tools.connect;

public interface ConnectListener {
    /**
     * 网络请求的参数
     *
     * @param list 默认的参数列表（空表，直接put然后返回即可）
     * @return 添加参数后的参数列表
     */
    public ConnectList setParam(ConnectList list);

    /**
     * 是否显示忙碌对话框
     *
     * @param dialog 默认的对话框（不显示，调用config将显示并在onResponse结束后自动隐藏）
     * @return 配置后的对话框
     */
    public ConnectDialog showDialog(ConnectDialog dialog);

    /**
     * 网络执行完毕后自动回调
     *
     * @param response 服务器返回的数据，错误将返回null
     */
    public void onResponse(String response);


}
