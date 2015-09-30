package org.outing.medicine;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/////////////////////////////////////////////////////////
public class FirstRun extends FragmentActivity {
	private ViewPager viewpager;
	private FragmentPagerAdapter adapter;
	private List<Fragment> fragment_list = new ArrayList<Fragment>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_run);

		initView();
		initListener();

	}

	// 初始化控件，初始化Fragment
	private void initView() {
		SubTab tab01 = new SubTab(R.mipmap.ic_launcher);// //////介绍图片
		SubTab tab02 = new SubTab(R.mipmap.ic_launcher);
		SubTab tab03 = new SubTab(R.mipmap.ic_launcher);
		SubTab tab04 = new SubTab(0);// 默认终止图片（建议使用主界面截图）

		fragment_list.add(tab01);
		fragment_list.add(tab02);
		fragment_list.add(tab03);
		fragment_list.add(tab04);
		// 初始化tab的页面
		viewpager = (ViewPager) findViewById(R.id.firstrun_viewpager);
		// 初始化Adapter
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
		// 设置监听
		viewpager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int position) {
				switch (position) {
				case 3:
					// //////////////////////////////////start+finish
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

class SubTab extends Fragment {
	private int img_id;

	public SubTab(int img_id) {
		this.img_id = img_id;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ImageView view = new ImageView(getActivity());
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		if (img_id != 0)
			view.setBackgroundResource(img_id);// 自动拉伸
		else
			view.setBackgroundColor(Color.WHITE);
		// view.setImageResource(img_id);//是否考虑拉伸
		return view;
	}

}
