package org.outing.medicine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.outing.medicine.tools.LogoutTask;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnClickListener {
    private ViewPager viewpager;
    private FragmentPagerAdapter adapter;
    private List<Fragment> fragment_list = new ArrayList<Fragment>();
    // 底部三个标签（LinearLayout包含iv与tv）
    private LinearLayout tab_linear_index;
    private LinearLayout tab_linear_my;
    private TextView tab_tv_index;
    private TextView tab_tv_my;
    private ImageView tab_iv_index;
    private ImageView tab_iv_my;
    // Tab的那个引导线
    private ImageView tab_line;
    // ViewPager的当前选中页
    private int current_index;
    // 屏幕的宽度
    private int screen_width;
    //设置activity,为了以后在其他activity中关闭
    public static MainActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;
        initView();
        initTabLine();
        initListener();
        //设置从ContantAdd直接跳转到联系人
        Intent intent = getIntent();
        int page = intent.getIntExtra("page", 0);
        viewpager.setCurrentItem(page);

    }


    // 根据屏幕的宽度，初始化引导线的宽度
    private void initTabLine() {
        tab_line = (ImageView) findViewById(R.id.tab_iv_line);
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay()
                .getMetrics(outMetrics);
        screen_width = outMetrics.widthPixels;
        LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) tab_line
                .getLayoutParams();
        lp.width = screen_width / 2;
        lp.leftMargin = screen_width / 2;
        tab_line.setLayoutParams(lp);

        // 初始化tab的页面
        viewpager = (ViewPager) findViewById(R.id.main_viewpager);
        // 初始化Adapter
        adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return fragment_list.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return fragment_list.get(arg0);
            }
        };
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(0);
        current_index = 0;
    }

    // 重置颜色
    protected void resetTextView() {
        tab_tv_index
                .setTextColor(getResources().getColor(R.color.tab_btn_dark));
        tab_tv_my.setTextColor(getResources().getColor(R.color.tab_btn_dark));
        tab_iv_index.setImageResource(R.mipmap.tab_btn_index_dark);
        tab_iv_my.setImageResource(R.mipmap.tab_btn_my_dark);
    }

    // 初始化控件，初始化Fragment
    private void initView() {
        tab_linear_index = (LinearLayout) findViewById(R.id.tab_linear_index);
        tab_linear_my = (LinearLayout) findViewById(R.id.tab_linear_my);

        tab_tv_index = (TextView) findViewById(R.id.tab_tv_index);
        tab_tv_my = (TextView) findViewById(R.id.tab_tv_my);

        tab_iv_index = (ImageView) findViewById(R.id.tab_iv_index);
        tab_iv_my = (ImageView) findViewById(R.id.tab_iv_my);

        MainTabIndex tab01 = new MainTabIndex();
        MainTabContact tab02 = new MainTabContact();

        fragment_list.add(tab01);
        fragment_list.add(tab02);
    }

    public void initListener() {
        // 设置顶部三个标签页点击事件
        tab_linear_index.setOnClickListener(this);
        tab_linear_my.setOnClickListener(this);
        // 设置监听
        viewpager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // 重置所有TextView的字体颜色
                resetTextView();
                switch (position) {
                    case 0:
                        tab_tv_index.setTextColor(getResources().getColor(
                                R.color.tab_btn_light));
                        tab_iv_index
                                .setImageResource(R.mipmap.tab_btn_index_light);
                        break;
                    case 1:
                        tab_tv_my.setTextColor(getResources().getColor(
                                R.color.tab_btn_light));
                        tab_iv_my.setImageResource(R.mipmap.tab_btn_my_light);
                        break;
                }
                current_index = position;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                // 利用position和currentIndex判断用户的操作是哪一页往哪一页滑动
                // 然后改变根据positionOffset动态改变TabLine的leftMargin
                // currentIndex:当前行 ;positionOffsetPixels:位置偏移像素;
                // positionOffset:位置偏移
                if (current_index == 0 && position == 0) {// 0->1
                    LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) tab_line
                            .getLayoutParams();
                    lp.leftMargin = (int) (positionOffset
                            * (screen_width * 1.0 / 2) + current_index
                            * (screen_width / 2));
                    tab_line.setLayoutParams(lp);// 从位置0平移到位置1
                } else if (current_index == 1 && position == 0) { // 1->0
                    LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) tab_line
                            .getLayoutParams();
                    lp.leftMargin = (int) (-(1 - positionOffset)
                            * (screen_width * 1.0 / 2) + current_index
                            * (screen_width / 2));
                    tab_line.setLayoutParams(lp);// 从位置1平移到位置0
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_linear_index:
                viewpager.setCurrentItem(0);
                break;
            case R.id.tab_linear_my:
                viewpager.setCurrentItem(1);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        new LogoutTask(this).execute();// 退出登录
        super.onDestroy();
    }

}
