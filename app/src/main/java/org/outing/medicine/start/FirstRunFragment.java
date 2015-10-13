package org.outing.medicine.start;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FirstRunFragment extends Fragment {
    private int img_id;
    private String text;

    public FirstRunFragment(int img_id) {
        this.img_id = img_id;
        this.text = null;
    }

    public FirstRunFragment(String text) {//////后期可删除
        this.text = text;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(text!=null){
            TextView view=new TextView(getActivity());
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            view.setGravity(Gravity.CENTER);
            view.setTextSize(40);
            view.setText(text);
            return view;
        }
        ImageView view = new ImageView(getActivity());
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        if (img_id != 0)
            view.setBackgroundResource(img_id);// 自动拉伸
        else
            view.setBackgroundColor(Color.WHITE);
        // view.setImageResource(img_id);//是否考虑拉伸
        return view;
    }

}
