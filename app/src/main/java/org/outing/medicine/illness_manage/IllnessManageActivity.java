package org.outing.medicine.illness_manage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.outing.medicine.R;
import org.outing.medicine.fun_drug.DrugMain;
import org.outing.medicine.fun_know.KnowMain;
import org.outing.medicine.fun_tools.ToolsMain;
import org.outing.medicine.personal_center.PersonalCenterActivity;

/**
 * Created by apple on 15/10/6.
 */
public class IllnessManageActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_illness_manage);

        ((Button) findViewById(R.id.index_blood_sugar)).setOnClickListener(this);
        ((Button) findViewById(R.id.index_blood_pressure)).setOnClickListener(this);
        ((Button) findViewById(R.id.index_blood_fat)).setOnClickListener(this);
        ((Button) findViewById(R.id.index_search)).setOnClickListener(this);

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
