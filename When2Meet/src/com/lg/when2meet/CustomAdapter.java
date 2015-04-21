package com.lg.when2meet;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter{
	Context context;
	int resID;
	ArrayList<String> data;
	boolean checkBoxVisible;
	
	public void changeVisibility(boolean checkBoxVisible) {
		this.checkBoxVisible = checkBoxVisible;
	}
	
	public CustomAdapter(Context context, int resID, ArrayList<String> data, boolean checkBoxVisible) {
		super();
		this.context = context;
		this.resID = resID;
		this.data = data;
		this.checkBoxVisible = checkBoxVisible;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout view = (LinearLayout)convertView;
		String pinkColor = "#F5908D";
		
		if(convertView == null) {
			view = (LinearLayout)View.inflate(context, resID, null);
		}
		LinearLayout layout = (LinearLayout)view.findViewById(R.id.room_bg);
		layout.setBackgroundColor(Color.WHITE);
		TextView name = (TextView)view.findViewById(R.id.room_name);
		name.setTextColor(Color.parseColor(pinkColor));
		name.setText(data.get(position));
		TextView mem_list = (TextView)view.findViewById(R.id.room_mem);
		mem_list.setTextColor(Color.parseColor(pinkColor));
		CheckBox check = (CheckBox)view.findViewById(R.id.check_delete);
		if(checkBoxVisible)
			check.setVisibility(View.VISIBLE);
		else
			check.setVisibility(View.INVISIBLE);
		
		return view;
	}

}
