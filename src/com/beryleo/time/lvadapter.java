package com.beryleo.time;
//free of localisation issues

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
//this is a custom listview adaptor 
//unique features are syntax 
//and the styling of the text in each list item 
public class lvadapter extends BaseAdapter {

	private String[] strings;  
	private final Context   context;
	private Typeface font;
	private Drawable image;
	private boolean[] ons = null;
	private float textsize;

	public lvadapter(Context c, String[] s, Typeface f, Drawable i, boolean[] b, float ts) {
		//this sets all of the data for the listview 
		this.context = c;
		this.strings = 	s;
		this.font = f;
		this.image = i;
		this.ons = b;
		this.textsize = ts;
	}

	public int getCount() {
		return strings.length;
	}

	public Object getItem(int position) {
		return strings[position];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewGroup view = (ViewGroup) View.inflate(context, R.layout.listitem, null);
		String text = strings[position];

		TextView tv = (TextView) view.findViewById(R.id.textView1);
		ToggleButton tb = (ToggleButton) view.findViewById(R.id.toggleButton1);
		if(ons==null) {
			tb.setVisibility(View.INVISIBLE);
		}
		else {
			boolean on = ons[position];
			tb.setChecked(on);
		}

		ImageView iv = (ImageView) view.findViewById(R.id.imageView1);
		if (image==null) {
			iv.setVisibility(View.INVISIBLE);
		}
		if (image!=null) {
			iv.setImageDrawable(image);
		}
		tv.setGravity(0x01);
		//that int is for horizontal centre gravity
		tv.setText(text);
		tv.setTextSize((float) (textsize / 1.5));
		//sets custom font
		tv.setTypeface(font);
		tb.setTypeface(font);
		return view;
	}
}
