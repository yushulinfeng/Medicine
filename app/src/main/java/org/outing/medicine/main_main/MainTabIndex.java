package org.outing.medicine.main_main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import org.outing.medicine.R;
import org.outing.medicine.fun_drug.DrugMain;
import org.outing.medicine.fun_know.KnowMain;
import org.outing.medicine.fun_remind.RemindMain;
import org.outing.medicine.fun_tools.ToolsMain;
import org.outing.medicine.illness_manage.IllnessManageActivity;
import org.outing.medicine.personal_center.PersonalCenterActivity;

public class MainTabIndex extends Fragment implements OnClickListener {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_center_index, container,
                false);

        //这是六个button
        view.findViewById(R.id.index_tools).setOnClickListener(this);
        view.findViewById(R.id.index_remind).setOnClickListener(this);
        view.findViewById(R.id.index_illness).setOnClickListener(this);
        view.findViewById(R.id.index_query)
                .setOnClickListener(this);
        view.findViewById(R.id.index_knowledge)
                .setOnClickListener(this);
        view.findViewById(R.id.index_my)
                .setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.index_tools:
                intent = new Intent(getActivity(), ToolsMain.class);
                break;
            case R.id.index_remind:
                intent = new Intent(getActivity(), RemindMain.class);
                break;
            case R.id.index_illness:
                intent = new Intent(getActivity(), IllnessManageActivity.class);
                break;
            case R.id.index_query:
                intent = new Intent(getActivity(), DrugMain.class);
                break;
            case R.id.index_knowledge:
                intent = new Intent(getActivity(), KnowMain.class);
                break;
            case R.id.index_my:
                intent = new Intent(getActivity(), PersonalCenterActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

}
