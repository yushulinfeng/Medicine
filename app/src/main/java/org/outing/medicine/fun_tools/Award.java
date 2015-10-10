package org.outing.medicine.fun_tools;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import org.outing.medicine.R;
import org.outing.medicine.tools.TActivity;

public class Award extends TActivity {

	@Override
	public void onCreate() {
		setContentView(R.layout.fun_tools_award);
		setTitle("抽奖啰");
		((Button) findViewById(R.id.tools_award_btn))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						showToast("开发中……");
					}
				});
		;

	}

	@Override
	public void showContextMenu() {
	}

}
