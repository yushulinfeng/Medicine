package org.outing.medicine.illness_manage;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import org.outing.medicine.R;
import org.outing.medicine.tools.base.TActivity;

/**
 * Created by apple on 15/10/6.
 */
public class IllnessManageActivity extends TActivity implements View.OnClickListener {


    @Override
    public void onCreate() {
        setContentView(R.layout.activity_illness_manage);
        setTitle("慢病管理");
        setTitleBackColor(R.color.btn_2_normal);
        showBackButton();

        ((Button) findViewById(R.id.index_blood_sugar)).setOnClickListener(this);
        ((Button) findViewById(R.id.index_blood_pressure)).setOnClickListener(this);
        ((Button) findViewById(R.id.index_blood_fat)).setOnClickListener(this);
        ((Button) findViewById(R.id.index_search)).setOnClickListener(this);
    }

    @Override
    public void showContextMenu() {

    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.index_blood_sugar:
                intent = new Intent(this, BloodSuger.class);
                break;
            case R.id.index_blood_pressure:
                intent = new Intent(this, BloodPressure.class);
                break;
            case R.id.index_blood_fat:
                intent = new Intent(this, BloodFat.class);
                break;
            case R.id.index_search:
                intent = new Intent(this, Questionnaire.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}
