package org.outing.medicine.fun_know;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import org.outing.medicine.R;
import org.outing.medicine.tools.TActivity;

public class KnowMain extends TActivity implements OnClickListener {
    @Override
    public void onCreate() {
        setContentView(R.layout.fun_know_main);
        setTitle("健康知识");

        Button btn_gxy = (Button) findViewById(R.id.fun_know_gxy);
        btn_gxy.setOnClickListener(this);
        Button btn_gxz = (Button) findViewById(R.id.fun_know_gxz);
        btn_gxz.setOnClickListener(this);
        Button btn_gxt = (Button) findViewById(R.id.fun_know_gxt);
        btn_gxt.setOnClickListener(this);
        Button btn_jk = (Button) findViewById(R.id.fun_know_jk);
        btn_jk.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, KnowList.class);
        String file_name = "";
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
        }
        if (!file_name.equals("")) {
            file_name = "know/" + file_name + ".xml";
            intent.putExtra("file_name", file_name);
            startActivity(intent);
        }
    }

    @Override
    public void showContextMenu() {
    }
}
