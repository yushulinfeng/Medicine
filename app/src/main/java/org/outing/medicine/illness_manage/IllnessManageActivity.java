package org.outing.medicine.illness_manage;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;

import org.outing.medicine.R;

/**
 * Created by apple on 15/10/6.
 */
public class IllnessManageActivity extends Activity {
    private ImageButton buttonAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_illness_manage);

        init();
    }

    private void init() {
        buttonAdd.findViewById(R.id.con_add_btn_album);
    }
}
