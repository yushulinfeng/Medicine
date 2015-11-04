package org.outing.medicine.main_main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import org.outing.medicine.R;
import org.outing.medicine.fun_drug.DrugMain;
import org.outing.medicine.fun_know.KnowMain;
import org.outing.medicine.fun_remind.RemindMain;
import org.outing.medicine.fun_tools.ToolsMain;
import org.outing.medicine.illness_manage.IllnessManageActivity;
import org.outing.medicine.personal_center.PersonalCenterActivity;
import org.outing.medicine.start.Login;
import org.outing.medicine.start.UserTool;
import org.outing.medicine.tools.connect.ConnectTool;

public class MainTabIndex extends Fragment implements OnClickListener {
    public static final int LOG_OUT = 1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_center_index, container,
                false);

        ((Button) view.findViewById(R.id.index_tools)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.index_remind)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.index_illness)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.index_query))
                .setOnClickListener(this);
        ((Button) view.findViewById(R.id.index_knowledge))
                .setOnClickListener(this);
        ((Button) view.findViewById(R.id.index_my))
                .setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.index_tools:
                intent = new Intent(getActivity(), ToolsMain.class);
                startActivity(intent);
                break;
            case R.id.index_remind:
                intent = new Intent(getActivity(), RemindMain.class);
                startActivity(intent);
                break;
            case R.id.index_illness:
                intent = new Intent(getActivity(), IllnessManageActivity.class);
                startActivity(intent);
                break;
            case R.id.index_query:
                intent = new Intent(getActivity(), DrugMain.class);
                startActivity(intent);
                break;
            case R.id.index_knowledge:
                intent = new Intent(getActivity(), KnowMain.class);
                startActivity(intent);
                break;
            case R.id.index_my:
                intent = new Intent(getActivity(), PersonalCenterActivity.class);
                startActivityForResult(intent, 0);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == LOG_OUT) {
            UserTool.saveUser(getActivity(), "", "");
            ConnectTool.saveCookie(getActivity(), "");
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
            getActivity().finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
