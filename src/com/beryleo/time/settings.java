package com.beryleo.time;
//free of localisation issues
import android.view.View;

public class settings {
	//settings class
	//2012-7-23: has a little group of views to control precision of decimal format for stopwatch
	//2012-7-23: has five groups of views to toggle features such as swiping and switching view buttons, and other stuff
	protected timeactivity context;
	//add a constructor with the Context of your activity
	public settings(timeactivity _context){
		context = _context;
	}
	public void settingsmain()
	{
		context.runOnUiThread(new Thread() {
			@Override
			public void run() {
				//pretty much self-explanatory for this whole class
				//just check what values are, edit style, and record new values the user provides
				setstartscreennumber();
				setclocktypenumber();
				context.numberdecimalplacesvaluetextview.setText("" + context.settings.getInt("places", -1));
				if (context.settings.getInt("places", -1) <= 0) {
					context.lessdecimalplacesbutton.setEnabled(false);
					context.moredecimalplacesbutton.setEnabled(true);
				}
				else if (context.settings.getInt("places", -1) >= 9) {
					context.lessdecimalplacesbutton.setEnabled(true);
					context.moredecimalplacesbutton.setEnabled(false);
				}
				if (context.settings.getInt("startscreen", -1) <= 0) {
					context.lowerstartscreenbutton.setEnabled(false);
					context.higherstartscreenbutton.setEnabled(true);
				}
				else if (context.settings.getInt("startscreen", -1) >= 6) {
					context.lowerstartscreenbutton.setEnabled(true);
					context.higherstartscreenbutton.setEnabled(false);
				}
				if (context.settings.getInt("clocktype", -1) <= 0) {
					context.lowerclocktypebutton.setEnabled(false);
					context.higherclocktypebutton.setEnabled(true);
				}
				else if (context.settings.getInt("clocktype", -1) >= 3) {
					context.lowerclocktypebutton.setEnabled(true);
					context.higherclocktypebutton.setEnabled(false);
				}
				context.bottomclocktogglebutton.setChecked(context.settings.getBoolean("bottomclock", false));
				context.topnavigationtogglebutton.setChecked(context.settings.getBoolean("topnavigation", false));
				context.popupstopwatchtogglebutton.setChecked(context.settings.getBoolean("popupstopwatch", false));
				context.newfonttogglebutton.setChecked(context.settings.getBoolean("newfont", false));
				context.clocksecondhandtogglebutton.setChecked(context.settings.getBoolean("clocksecondhand", false));
				context.lessdecimalplacesbutton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						int places = context.settings.getInt("places", -1);
						context.moredecimalplacesbutton.setEnabled(true);
						if (places > 0) {
							context.editor.putInt("places", places - 1);
							context.editor.commit();
							context.numberdecimalplacesvaluetextview.setText("" + context.settings.getInt("places", -1));
							if (places - 1 <= 0) {
								context.lessdecimalplacesbutton.setEnabled(false);
								context.moredecimalplacesbutton.setEnabled(true);
							}
						}
					}
				});
				context.moredecimalplacesbutton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						int places = context.settings.getInt("places", -1);
						context.lessdecimalplacesbutton.setEnabled(true);
						if (places < 9) {
							context.editor.putInt("places", places + 1);
							context.editor.commit();
							context.numberdecimalplacesvaluetextview.setText("" + context.settings.getInt("places", -1));
							if (places + 1 >= 9) {
								context.lessdecimalplacesbutton.setEnabled(true);
								context.moredecimalplacesbutton.setEnabled(false);
							}
						}
					}
				});
				context.lowerstartscreenbutton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						int startscreen = context.settings.getInt("startscreen", -1);
						context.higherstartscreenbutton.setEnabled(true);
						if (startscreen > 0) {
							context.editor.putInt("startscreen", startscreen - 1);
							context.editor.commit();
							setstartscreennumber();
							if (startscreen - 1 <= 0) {
								context.lowerstartscreenbutton.setEnabled(false);
								context.higherstartscreenbutton.setEnabled(true);
							}
						}
					}
				});
				context.higherstartscreenbutton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						int startscreen = context.settings.getInt("startscreen", -1);
						context.lowerstartscreenbutton.setEnabled(true);
						if (startscreen < 6) {
							context.editor.putInt("startscreen", startscreen + 1);
							context.editor.commit();
							setstartscreennumber();
							if (startscreen + 1 >= 6) {
								context.lowerstartscreenbutton.setEnabled(true);
								context.higherstartscreenbutton.setEnabled(false);
							}
						}
					}
				});
				//this controls clock type for choosing what kind of dial 
				context.lowerclocktypebutton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						int clocktype = context.settings.getInt("clocktype", -1);
						context.higherclocktypebutton.setEnabled(true);
						if (clocktype > 0) {
							context.editor.putInt("clocktype", clocktype - 1);
							context.editor.commit();
							setclocktypenumber();
							if (clocktype - 1 <= 0) {
								context.lowerclocktypebutton.setEnabled(false);
								context.higherclocktypebutton.setEnabled(true);
							}
						}
					}
				});
				context.higherclocktypebutton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						int clocktype = context.settings.getInt("clocktype", -1);
						context.lowerclocktypebutton.setEnabled(true);
						if (clocktype < 3) {
							context.editor.putInt("clocktype", clocktype + 1);
							context.editor.commit();
							setclocktypenumber();
							if (clocktype + 1 >= 3) {
								context.lowerclocktypebutton.setEnabled(true);
								context.higherclocktypebutton.setEnabled(false);
							}
						}
					}
				});
				context.bottomclocktogglebutton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						context.editor.putBoolean("bottomclock", context.bottomclocktogglebutton.isChecked());
						context.editor.commit();
						context.init();
					}
				});
				context.topnavigationtogglebutton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						context.editor.putBoolean("topnavigation", context.topnavigationtogglebutton.isChecked());
						context.editor.commit();
						context.init();
					}
				});
				context.popupstopwatchtogglebutton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						context.editor.putBoolean("popupstopwatch", context.popupstopwatchtogglebutton.isChecked());
						context.editor.commit();
						context.init();
					}
				});
				context.newfonttogglebutton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						context.editor.putBoolean("newfont", context.newfonttogglebutton.isChecked());
						context.editor.commit();
						context.init();
					}
				});
				context.clocksecondhandtogglebutton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						context.editor.putBoolean("clocksecondhand", context.clocksecondhandtogglebutton.isChecked());
						context.editor.commit();
						context.init();
					}
				});
			}
		});
	}
	private void setstartscreennumber() {
		switch(context.settings.getInt("startscreen", -1))
		{
		case 0:
			context.startscreenvaluetextview.setText(R.string.clock);
			break;
		case 1:
			context.startscreenvaluetextview.setText(R.string.alarms);
			break;
		case 2:
			context.startscreenvaluetextview.setText(R.string.stopwatch);
			break;
		case 3:
			context.startscreenvaluetextview.setText(R.string.timer);
			break;
		case 4:
			context.startscreenvaluetextview.setText(R.string.intervals);
			break;
		case 5:
			context.startscreenvaluetextview.setText(R.string.settings);
			break;
		case 6:
			context.startscreenvaluetextview.setText(R.string.about);
			break;
		}
	}
	private void setclocktypenumber() {
		switch(context.settings.getInt("clocktype", -1))
		{
		case 0:
			context.clocktypevaluetextview.setText(R.string.widget_circle);
			break;
		case 1:
			context.clocktypevaluetextview.setText(R.string.widget_square);
			break;
		case 2:
			context.clocktypevaluetextview.setText(R.string.widget_roundedsquare);
			break;
		case 3:
			context.clocktypevaluetextview.setText(R.string.widget_intersections);
			break;
		}  	
	}

}
