package com.beryleo.time;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RemoteViews.RemoteView;

import java.util.TimeZone;

/**
 * This widget display an analogue clock with three hands for hours and
 * minutes and seconds.
 */
@RemoteView
public class analogclock extends View {
	public analogclock(Context context) {
		super(context);
	}
	private Time calender;
	private Drawable drawablehandhour;
	private Drawable drawablehandminute;
	private Drawable drawablehandsecond;
	protected Drawable drawabledial;
	private int dialwidth;
	private int dialheight;
	private boolean attached;
	private final Handler handler = new Handler();
	private float minutes;
	private float hours;
	private boolean changed;
	Context contextlocal;
	public analogclock(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public analogclock(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		Resources r = context.getResources();
		contextlocal=context;
		drawabledial = r.getDrawable(R.drawable.clockdial);
		drawablehandhour = r.getDrawable(R.drawable.hourhand);
		drawablehandminute = r.getDrawable(R.drawable.minutehand);
		drawablehandsecond = r.getDrawable(R.drawable.secondhand);
		calender = new Time();
		dialwidth = drawabledial.getIntrinsicWidth();
		dialheight = drawabledial.getIntrinsicHeight();
	}
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (!attached) {
			attached = true;
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_TIME_TICK);
			filter.addAction(Intent.ACTION_TIME_CHANGED);
			filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
			getContext().registerReceiver(mIntentReceiver, filter, null, handler);
		}
		// NOTE: It's safe to do these after registering the receiver since the receiver always runs
		// in the main thread, therefore the receiver can't run before this method returns.
		// The time zone may have changed while the receiver wasn't registered, so update the Time
		calender = new Time();
		// Make sure we update to the current time
		onTimeChanged();
		counter.start();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (attached) {
			counter.cancel();
			getContext().unregisterReceiver(mIntentReceiver);
			attached = false;
		}
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize =  MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize =  MeasureSpec.getSize(heightMeasureSpec);
		float hScale = 1.0f;
		float vScale = 1.0f;
		if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < dialwidth) {
			hScale = (float) widthSize / (float) dialwidth;
		}
		if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < dialheight) {
			vScale = (float )heightSize / (float) dialheight;
		}
		float scale = Math.min(hScale, vScale);
		setMeasuredDimension(resolveSize((int) (dialwidth * scale), widthMeasureSpec),
				resolveSize((int) (dialheight * scale), heightMeasureSpec));
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		changed = true;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		onTimeChanged();
		super.onDraw(canvas);
		boolean changed1 = changed;
		if (changed1) {
			changed = false;
		}
		boolean seconds = mSeconds;
		if (seconds ) {
			mSeconds = false;
		}
		int availableWidth = 0;
		int availableHeight = 0;
		if (canvas.getWidth() > canvas.getHeight())  {
			availableWidth = canvas.getHeight();
			availableHeight = canvas.getHeight();
		}
		else {
			availableWidth = canvas.getWidth();
			availableHeight = canvas.getWidth();
		}
		if (availableWidth > 500) {
			//this is for larger screens, where I don't want the clock to run out of room
			availableWidth = 500;
					availableHeight = 500;
		}
		int x = availableWidth / 2;
		int y = availableHeight / 2;
		final Drawable dial = drawabledial;
		int w = dial.getIntrinsicWidth();
		int h = dial.getIntrinsicHeight();
		boolean scaled = false;
		if (availableWidth < w || availableHeight < h) {
			scaled = true;
			float scale = Math.min((float) availableWidth / (float) w,
					(float) availableHeight / (float) h);
			canvas.save();
			canvas.scale(scale, scale, x, y);
		}
		if (changed1) {
			dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
		}
		dial.draw(canvas);
		canvas.save();
		canvas.rotate(hours / 12.0f * 360.0f, x, y);
		final Drawable hourHand = drawablehandhour;
		if (changed1) {
			w = hourHand.getIntrinsicWidth();
			h = hourHand.getIntrinsicHeight();
			hourHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
		}
		hourHand.draw(canvas);
		canvas.restore();
		canvas.save();
		canvas.rotate(minutes / 60.0f * 360.0f, x, y);
		final Drawable minuteHand = drawablehandminute;
		if (changed1) {
			w = minuteHand.getIntrinsicWidth();
			h = minuteHand.getIntrinsicHeight();
			minuteHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
		}
		minuteHand.draw(canvas);
		canvas.restore();
		canvas.save();
		canvas.rotate(mSecond, x, y);
		if (seconds) {
			w = drawablehandsecond.getIntrinsicWidth();
			h = drawablehandsecond.getIntrinsicHeight();
			drawablehandsecond.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
		}
		drawablehandsecond.draw(canvas);
		canvas.restore();
		if (scaled) {
			canvas.restore();
		}
	}
	MyCount counter = new MyCount(10000, 1000);
	public class MyCount extends CountDownTimer{
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}
		@Override
		public void onFinish() {
			counter.start();
		}
		@Override
		public void onTick(long millisUntilFinished) {
			calender.setToNow();
			int second = calender.second;    
			mSecond=6.0f*second;
			mSeconds=true;
			analogclock.this.invalidate();
		}
	}
	boolean mSeconds=false;
	float mSecond=0;
	private void onTimeChanged() {
		calender.setToNow();
		int hour = calender.hour;
		int minute = calender.minute;
		int second = calender.second;
		minutes = minute + second / 60.0f;
		hours = hour + minutes / 60.0f;
		changed = true;
	}
	private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
				String tz = intent.getStringExtra("time-zone");
				calender = new Time(TimeZone.getTimeZone(tz).getID());
			}
			onTimeChanged();         
			invalidate();
		}
	};
}
