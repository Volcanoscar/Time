package com.beryleo.time;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ToggleButton;

public class fonttogglebutton extends ToggleButton{
	static Typeface tf;
	public fonttogglebutton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public fonttogglebutton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public fonttogglebutton(Context context) {
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
