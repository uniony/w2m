package com.lg.when2meet;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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
	ArrayList<String> selected_room;
	
	public void changeVisibility(boolean checkBoxVisible) {
		this.checkBoxVisible = checkBoxVisible;
	}
	
	ArrayList<String> getSelectedRooms() {
		return selected_room;
	}
	
	public CustomAdapter(Context context, int resID, ArrayList<String> roomname, ArrayList<String> member, boolean checkBoxVisible) {
		super();
		this.context = context;
		this.resID = resID;
		this.roomname = roomname;
		this.member = member;
		this.checkBoxVisible = checkBoxVisible;
		selected_room = new ArrayList<String>();
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
		TextView name = (TextView)view.findViewById(R.id.room_name);
		TextView mem_list = (TextView)view.findViewById(R.id.room_mem);
		CheckBox check = (CheckBox)view.findViewById(R.id.check_delete);
		ImageView icon = (ImageView) view.findViewById(R.id.room_icon);
		
		layout.setBackgroundColor(Color.WHITE);
		name.setTextColor(Color.parseColor(pinkColor));
		name.setText(roomname.get(position));
		mem_list.setTextColor(Color.parseColor(pinkColor));
		icon.setImageResource(R.drawable.room_icon);
		
		if(checkBoxVisible)
			check.setVisibility(View.VISIBLE);
		else
			check.setVisibility(View.INVISIBLE);
		
		check.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String name = roomname.get(position);
				if(selected_room.contains(name)) {
					selected_room.remove(roomname.get(position));
					Log.d("check", "deleted");
				} else {
					selected_room.add(roomname.get(position));
					Log.d("check", "selected "+ position);
				}
			}
		});
		
		view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				String pinkColor = "#F5908D";
				LinearLayout layout = (LinearLayout)v.findViewById(R.id.room_bg);
				TextView name = (TextView)v.findViewById(R.id.room_name);
				TextView mem_list = (TextView)v.findViewById(R.id.room_mem);
				ImageView icon = (ImageView)v.findViewById(R.id.room_icon);
				
				switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Log.d("check", "touch");
					layout.setBackgroundColor(Color.parseColor(pinkColor));
					name.setTextColor(Color.WHITE);
					mem_list.setTextColor(Color.WHITE);
					icon.setImageResource(R.drawable.room_icon_click);
					return true;
					
				case MotionEvent.ACTION_UP:
					Log.d("check", "touch end");
					layout.setBackgroundColor(Color.WHITE);
					name.setTextColor(Color.parseColor(pinkColor));
					mem_list.setTextColor(Color.parseColor(pinkColor));
					icon.setImageResource(R.drawable.room_icon);
					break;
					
				default:
					break;
				}
				return false;		
			}
		});
		
		return view;
	}	
}
