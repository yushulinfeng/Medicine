package org.outing.medicine.start.welcome;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import org.outing.medicine.R;
import org.outing.medicine.start.login.Login;

import java.util.ArrayList;
import java.util.List;

public class FirstRun extends FragmentActivity {
    private ViewPager viewpager;
    private FragmentPagerAdapter adapter;
    private List<Fragment> fragment_list = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_firstrun);

        initView();
        initListener();
    }

    private void initView() {
        FirstRunFragment tab01 = new FirstRunFragment("首次运行\n欢迎您\n请向左滑动");// //////介绍图片
        FirstRunFragment tab02 = new FirstRunFragment("老友网\n←←←←←←");
        FirstRunFragment tab03 = new FirstRunFragment("向左滑动\n开启崭新之旅");
        FirstRunFragment tab04 = new FirstRunFragment(0);// 默认终止图片（建议使用主界面截图）

        fragment_list.add(tab01);
        fragment_list.add(tab02);
        fragment_list.add(tab03);
        fragment_list.add(tab04);

        viewpager = (ViewPager) findViewById(R.id.firstrun_viewpager);
        adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            public int getCount() {
                return fragment_list.size();
            }

            public Fragment getItem(int arg0) {
                return fragment_list.get(arg0);
            }
        };
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(0);
    }

    public void initListener() {
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageSelected(int position) {
                switch (position) {
                    case 3:
                        Intent intent = new Intent(FirstRun.this, Login.class);
                        startActivity(intent);
                        finish();
                        break;
                }
            }

            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            public void onPageScrollStateChanged(int state) {
            }
        });
    }

}
