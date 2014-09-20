package com.beryleo.time;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class fonttextview extends TextView{
	static Typeface tf;
	public fonttextview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public fonttextview(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public fonttextview(Context context) {
		super(context);
		init();
	}

	private void init() {
		if (!isInEditMode()) {
			tf = Typeface.createFromAsset(this.getContext().getAssets(),"Sony_Sketch_EF.ttf");
			setTypeface(tf);
		}
	}

}
