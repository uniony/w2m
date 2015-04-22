package com.lg.when2meet;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class RoomVoteActivity extends Activity {
	int total_size;
	Bundle b;
	String start_time, end_time, room_name;
	ArrayList<DateClass> datelist;
	ArrayList<String> selectedlist;
	TextView room;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room_vote);

		room = (TextView)findViewById(R.id.room_name);
		selectedlist = new ArrayList<String>();
		b = this.getIntent().getExtras();
		start_time = b.getString("s_time");
		end_time = b.getString("e_time");
		room_name = b.getString("room_name");
		datelist = b.getParcelableArrayList("datelist");

		room.setText(room_name);
		int s_time = Integer.parseInt(start_time);
		int time_span = (Integer.parseInt(end_time) - Integer.parseInt(start_time));
		total_size = datelist.size() * time_span;

		TextView t1 = (TextView) findViewById(R.id.setting1);
		t1.setText(datelist.get(0).getDate().substring(0, 10) + " ~ "
				+ datelist.get(datelist.size() - 1).getDate().substring(0, 10));
		TextView t2 = (TextView) findViewById(R.id.setting2);
		t2.setText("약속 시간: " + start_time + "시 ~ " + end_time + "시");

		final TableLayout tablelayout = (TableLayout) findViewById(R.id.table);
		// tablelayout.removeAllViews();
		for (int i = 0; i < time_span + 1; i++) {
			TableRow row = new TableRow(this);
			row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			for (int j = 0; j < datelist.size() + 1; j++) {
				final TextView tv = new TextView(this);
				tv.setLayoutParams(new TableRow.LayoutParams(
						TableRow.LayoutParams.WRAP_CONTENT,
						TableRow.LayoutParams.WRAP_CONTENT));
				tv.setGravity(Gravity.CENTER);
				int pad_size = 5;
				tv.setPadding(pad_size, pad_size, pad_size, pad_size);
				tv.setBackgroundResource(R.drawable.table_border);
				tv.setHighlightColor(0);
				if (i == 0) {
					if (j > 0) {
						tv.setText(datelist.get(j - 1).getDate().substring(0, 10));
						tv.setTextColor(Color.parseColor("#F5908D"));
					}
				} else {
					if (j == 0) {
						if (s_time < 9) {
							tv.setText("0" + (s_time++) + "~0"+s_time+"시");
						} else if(s_time==9){
							tv.setText("0"+(s_time++) + "~"+s_time+"시");
						} else{
							tv.setText((s_time++)+"~"+s_time+"시");
						}
						tv.setTextColor(Color.parseColor("#F5908D"));
					} else {
						tv.setText(".");
						tv.setTextColor(Color.parseColor("#bbeeff"));
						tv.setHint(datelist.get(j - 1).getDate().substring(0, 10) + " " + (Integer.parseInt(start_time) + i - 1));

						tv.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								if (tv.getHighlightColor() != 7) {
									tv.setBackgroundResource(R.drawable.table_selected);
									tv.setHighlightColor(7);
									selectedlist.add((String) tv.getHint());
								} else {
									tv.setBackgroundResource(R.drawable.table_border);
									tv.setHighlightColor(0);
									for(int z=0; z<selectedlist.size(); z++){
										if(selectedlist.get(z).equals(tv.getHint())){
											selectedlist.remove(z);
										}
									}
								}
							}
						});
					}
				}
				row.addView(tv);
			}
			tablelayout.addView(row);
		}

		ImageView button1 = (ImageView) findViewById(R.id.complete);
		ImageView button2 = (ImageView) findViewById(R.id.clear);
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(RoomVoteActivity.this, RoomActivity.class);
				b.putParcelableArrayList("datelist", datelist);
				b.putStringArrayList("selectedlist", selectedlist);
				b.putString("s_time", start_time);
				b.putString("e_time", end_time);
				b.putString("room_name", room_name);
				i.putExtras(b);
				startActivity(i);
			}
		});

		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				for (int i = 0; i < tablelayout.getChildCount(); i++) {
					for (int j = 0; j < ((TableRow) tablelayout.getChildAt(i)).getChildCount(); j++){
						if (((TableRow) tablelayout.getChildAt(i)).getChildAt(j) instanceof TextView) {
							((TextView)((TableRow) tablelayout.getChildAt(i)).getChildAt(j)).setBackgroundResource(R.drawable.table_border);
							((TextView)((TableRow) tablelayout.getChildAt(i)).getChildAt(j)).setHighlightColor(0);
						}
					}
				}
				for(int z=0; z<selectedlist.size();z++){
					selectedlist.clear();
				}
			}
		});
	}
}
