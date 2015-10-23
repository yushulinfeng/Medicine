package org.outing.medicine.illness_manage;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.outing.medicine.R;
import org.outing.medicine.tools.connect.ServerURL;

/**
 * Created by apple on 15/10/19.
 */
public class Questionnaire extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
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
}
