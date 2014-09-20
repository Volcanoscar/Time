package com.beryleo.time;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.RemoteViews.RemoteView;

@RemoteView
public class analogclock_square extends analogclock {
	public analogclock_square(Context context) {
		super(context);
	}
	public analogclock_square(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public analogclock_square(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		Resources r = context.getResources();
		this.drawabledial = r.getDrawable(R.drawable.clockdial_square);
	}
}
