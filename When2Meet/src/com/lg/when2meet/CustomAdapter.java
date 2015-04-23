package com.lg.when2meet;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter{
	Context context;
	int resID;
	ArrayList<String> roomname;
	ArrayList<String> member;
	boolean checkBoxVisible;
	ArrayList<Integer> selected_room;
	
	public void changeVisibility(boolean checkBoxVisible) {
		this.checkBoxVisible = checkBoxVisible;
	}
	
	ArrayList<Integer> getSelectedRooms() {
		return selected_room;
	}
	
	public void clearSelectedRooms() {
		selected_room = new ArrayList<Integer>();
	}
	
	public CustomAdapter(Context context, int resID, ArrayList<String> roomname, ArrayList<String> member, boolean checkBoxVisible) {
		super();
		this.context = context;
		this.resID = resID;
		this.roomname = roomname;
		this.member = member;
		this.checkBoxVisible = checkBoxVisible;
		selected_room = new ArrayList<Integer>();
	}

	@Override
	public int getCount() {
		return roomname.size();
	}

	@Override
	public Object getItem(int position) {
		return roomname.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LinearLayout view = (LinearLayout)convertView;
		String pinkColor = "#F5908D";
		
		if(convertView == null) {
			view = (LinearLayout)View.inflate(context, resID, null);
		}
		LinearLayout layout = (LinearLayout)view.findViewById(R.id.room_bg);
		TextView room_name = (TextView)view.findViewById(R.id.room_name);
		TextView mem_list = (TextView)view.findViewById(R.id.room_mem);
		CheckBox check = (CheckBox)view.findViewById(R.id.check_delete);
		ImageView icon = (ImageView) view.findViewById(R.id.room_icon);
		
		layout.setBackgroundColor(Color.WHITE);
		room_name.setTextColor(Color.parseColor(pinkColor));
		room_name.setText(roomname.get(position));
		icon.setImageResource(R.drawable.room_icon);
		mem_list.setTextColor(Color.parseColor(pinkColor));
		mem_list.setText(member.get(position));
		boolean isCheck = false;
		for(int i = 0 ; i < selected_room.size() ; i++) {
			if (selected_room.get(i) == position)
				isCheck = true;
		}
		check.setChecked(isCheck);
		if(checkBoxVisible)
			check.setVisibility(View.VISIBLE);
		else
			check.setVisibility(View.INVISIBLE);
		
		check.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(selected_room.contains(position)) {
					selected_room.remove(selected_room.indexOf(position));
				} else {
					selected_room.add(selected_room.size(), position);
				}
			}
		});
		
		return view;
	}	
}
