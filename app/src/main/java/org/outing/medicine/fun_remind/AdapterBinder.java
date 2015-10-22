package org.outing.medicine.fun_remind;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter.ViewBinder;

public class AdapterBinder implements ViewBinder {

	/** 将bitmap与视图绑定 */
	public boolean setViewValue(View view, Object data,
								String textRepresentation) {
		if ((view instanceof ImageView) & (data instanceof Bitmap)) {
			ImageView iv = (ImageView) view;
			Bitmap bmp = (Bitmap) data;
			iv.setImageBitmap(bmp);
			return true;
		}
		return false;
	}

}
