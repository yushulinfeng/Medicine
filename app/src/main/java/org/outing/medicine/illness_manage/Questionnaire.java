package org.outing.medicine.illness_manage;

import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.outing.medicine.R;
import org.outing.medicine.tools.TActivity;
import org.outing.medicine.tools.connect.ServerURL;


public class Questionnaire extends TActivity {
    @Override
    public void onCreate() {
        setContentView(R.layout.activity_questionnaire);
        setTitle("健康测试");
        setTitleBackColor(R.color.btn_2_normal);
        showBackButton();
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.getSettings().setJavaScriptEnabled(true);
        //添加缓存
//        myWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        myWebView.setWebViewClient(new WebViewClient() {
                                       @Override
                                       public boolean shouldOverrideUrlLoading(WebView view, String url) {

                                           Log.d("test", "url" + url);
                                           if (url.equals(ServerURL.Questionnaire_Result_Url)){
                                               Log.d("test", "答题成功，积分+3");
                                               Toast.makeText(Questionnaire.this, "答题成功，积分+3",
                                                       Toast.LENGTH_SHORT).show();
                                           }
                                           finish();
                                           //报错退出
                                           // view.loadUrl("www.baidu.com");
                                           return true;
                                       }
                                   }
        );
        myWebView.loadUrl(ServerURL.Questionnaire_Url);
    }

    @Override
    public void showContextMenu() {
    }
}
