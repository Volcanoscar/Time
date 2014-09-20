package com.beryleo.time;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class fontbutton extends Button{
	static Typeface tf;
	public fontbutton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public fontbutton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public fontbutton(Context context) {
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
