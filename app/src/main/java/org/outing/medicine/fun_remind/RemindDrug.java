package org.outing.medicine.fun_remind;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.outing.medicine.R;
import org.outing.medicine.tools.TActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RemindDrug extends TActivity {
    private ListView list;
    private ArrayList<AnRemind> array;
    private List<Map<String, Object>> items;
    private SimpleAdapter adapter;
    private TextView show;
    private String drug_id, drug_name;

    @Override
    public void onCreate() {
        setContentView(R.layout.fun_remind_drug);
        initMessage();
        setTitle(drug_name);
        showBackButton();

        initView();
    }

    private void initMessage() {
        try {
            drug_id = getIntent().getStringExtra("drug_id");
            drug_name = getIntent().getStringExtra("drug_name");
        } catch (Exception e) {
            drug_id = null;
        }
    }

    private void initView() {
        show = (TextView) findViewById(R.id.remind_drug_tv_show);
        list = (ListView) findViewById(R.id.remind_drug_list);

        Button btn_add = (Button) findViewById(R.id.remind_drug_btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTimer();
            }
        });

    }
private void AddTimer(){
    Intent intent = new Intent(this, AddTimer.class);
    startActivityForResult(intent, 0);//用0就行
}
    @Override
    public void showContextMenu() {
    }
}
