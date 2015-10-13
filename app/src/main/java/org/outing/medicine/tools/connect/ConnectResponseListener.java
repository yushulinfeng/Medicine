package org.outing.medicine.tools.connect;

public interface ConnectResponseListener {
    /**
     * 网络执行完毕后自动回调
     * @param response 服务器返回的数据，错误将返回null
     */
    public void onResponse(String response);
}
