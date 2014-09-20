package com.beryleo.time.stopwatchp;

//localisation issues have NOT been fixed DONE
//unknown if user will even ever see them though
import java.util.ArrayList;
import java.util.List;

import com.beryleo.time.R;

import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * This implementation provides multiple windows. You may extend this class or
 * use it as a reference for a basic foundation for your own windows.
 * 
 * <p>
 * Functionality includes system window decorators, moveable, resizeable,
 * hideable, closeable, and bring-to-frontable.
 * 
 * @author Mark Wei <markwei@gmail.com>
 * 
 */
// edited out any action taken when notification hit
// this is taken from the standoutexample project, created by mark wei
// is used as a base class for standout windows, e.g. standoutstopwatch extends
// this
// 2012-7-24: used for stopwatch, but not proprietary for that; can be used for
// any other type of window
public class multiwindow extends StandOutWindow {

	@Override
	public String getAppName() {
		return "MultiWindow";
	}

	@Override
	public int getAppIcon() {
		return R.drawable.icon_thick;
	}

	@Override
	public String getTitle(int id) {
		return getAppName();
	}

	@Override
	public void createAndAttachView(int id, FrameLayout frame) {
		// create a new layout from body.xml
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.main, frame, true);

		TextView idText = (TextView) view.findViewById(R.id.id);
		idText.setText(String.valueOf(id));
	}

	// every window is initially same size
	@Override
	public StandOutLayoutParams getParams(int id, Window window) {
		return new StandOutLayoutParams(id, 400, 300,
				StandOutLayoutParams.AUTO_POSITION,
				StandOutLayoutParams.AUTO_POSITION, 100, 100);
	}

	// we want the system window decorations, we want to drag the body, we want
	// the ability to hide windows, and we want to tap the window to bring to
	// front
	@Override
	public int getFlags(int id) {
		return StandOutFlags.FLAG_DECORATION_SYSTEM
				| StandOutFlags.FLAG_BODY_MOVE_ENABLE
				| StandOutFlags.FLAG_WINDOW_HIDE_ENABLE
				| StandOutFlags.FLAG_WINDOW_BRING_TO_FRONT_ON_TAP
				| StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE
				| StandOutFlags.FLAG_WINDOW_PINCH_RESIZE_ENABLE;
	}

	@Override
	public String getPersistentNotificationTitle(int id) {
		return getAppName() + " " + getResources().getString(R.string.running);
	}

	@Override
	public String getPersistentNotificationMessage(int id) {
		return getAppName();
	}

	// return an Intent that creates a new MultiWindow
	@Override
	public Intent getPersistentNotificationIntent(int id) {
		Intent intent = new Intent();
		return intent;
		// return StandOutWindow.getShowIntent(this, getClass(), getUniqueId());
	}

	@Override
	public int getHiddenIcon() {
		return android.R.drawable.ic_menu_info_details;
	}

	@Override
	public String getHiddenNotificationTitle(int id) {
		return getAppName() + " " + getResources().getString(R.string.hidden);
	}

	@Override
	public String getHiddenNotificationMessage(int id) {
		return getResources().getString(R.string.clicktorestore) + id;
	}

	// return an Intent that restores the MultiWindow
	@Override
	public Intent getHiddenNotificationIntent(int id) {
		return StandOutWindow.getShowIntent(this, getClass(), id);
	}

	@Override
	public Animation getShowAnimation(int id) {
		if (isExistingId(id)) {
			// restore
			return AnimationUtils.loadAnimation(this,
					android.R.anim.slide_in_left);
		} else {
			// show
			return super.getShowAnimation(id);
		}
	}

	@Override
	public Animation getHideAnimation(int id) {
		return AnimationUtils.loadAnimation(this,
				android.R.anim.slide_out_right);
	}

	@Override
	public List<DropDownListItem> getDropDownItems(int id) {
		List<DropDownListItem> items = new ArrayList<DropDownListItem>();
		return items;
	}

	@Override
	public void onReceiveData(int id, int requestCode, Bundle data,
			Class<? extends StandOutWindow> fromCls, int fromId) {
	}

	@Override
	public boolean onClose(int id, Window window) {
		return false;
	}

	@Override
	public boolean onCloseAll() {
		return false;
	}

	@Override
	public boolean onHide(int id, Window window) {
		return false;
	}

}
