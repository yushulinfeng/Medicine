package org.outing.medicine.fun_know;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import org.outing.medicine.R;
import org.outing.medicine.tools.base.TActivity;

public class KnowMain extends TActivity implements OnClickListener {
    @Override
    public void onCreate() {
        setContentView(R.layout.fun_know_main);
        setTitle("健康知识");
        setTitleBackColor(R.color.btn_4_normal);
        showBackButton();

        Button btn_gxy = (Button) findViewById(R.id.fun_know_gxy);
        btn_gxy.setOnClickListener(this);
        Button btn_gxz = (Button) findViewById(R.id.fun_know_gxz);
        btn_gxz.setOnClickListener(this);
        Button btn_gxt = (Button) findViewById(R.id.fun_know_gxt);
        btn_gxt.setOnClickListener(this);
        Button btn_jk = (Button) findViewById(R.id.fun_know_jk);
        btn_jk.setOnClickListener(this);
        Button btn_net = (Button) findViewById(R.id.fun_know_net);
        btn_net.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, KnowList.class);
        String file_name = "";
        boolean is_net = false;
        switch (v.getId()) {
            case R.id.fun_know_gxy:
                file_name = "gao_xue_ya";
                break;
            case R.id.fun_know_gxz:
                file_name = "gao_xue_zhi";
                break;
            case R.id.fun_know_gxt:
                file_name = "gao_xue_tang";
                break;
            case R.id.fun_know_jk:
                file_name = "jian_kang";
                break;
            case R.id.fun_know_net:
                file_name = "NET";
                is_net = true;
                break;
        }
        if (!file_name.equals("")) {
            file_name = "know/" + file_name + ".xml";
            intent.putExtra("file_name", file_name);
            intent.putExtra("is_net", is_net);
            startActivity(intent);
        }
    }

    @Override
    public void showContextMenu() {
    }
}
