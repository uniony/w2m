package com.lg.when2meet;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


public class RoomActivity extends Activity {
	int total_size;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room);

		Bundle b = this.getIntent().getExtras();
		final String start_time = b.getString("s_time");
		int s_time = Integer.parseInt(start_time);
		final String end_time = b.getString("e_time");
		final ArrayList<DateClass> datelist = b.getParcelableArrayList("datelist");
		ArrayList<String> selectedlist = b.getStringArrayList("selectedlist");
		
		if(selectedlist==null){
			selectedlist= new ArrayList<String>();
		}
		int time_span = (Integer.parseInt(end_time) - Integer.parseInt(start_time));
		total_size = datelist.size()* time_span;

		TextView t1 = (TextView) findViewById(R.id.setting1);
		t1.setText(datelist.get(0).getDate().substring(0, 10)+" ~ "+datelist.get(datelist.size()-1).getDate().substring(0, 10));
		TextView t2 = (TextView) findViewById(R.id.setting2);
		t2.setText(start_time+"시 ~ "+end_time+"시 사이에 약속잡기");

		TableLayout tablelayout = (TableLayout)findViewById(R.id.table);
		//		tablelayout.removeAllViews();
		for(int i=0; i<time_span+1; i++){
			TableRow row = new TableRow(this);
			row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			for(int j=0; j<datelist.size()+1; j++){
				final TextView tv = new TextView(this);
				tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
				tv.setGravity(Gravity.CENTER);
				int pad_size=5;
				tv.setPadding(pad_size, pad_size, pad_size, pad_size);
				tv.setBackgroundResource(R.drawable.table_border); 
				tv.setHighlightColor(0);
				if(i==0){
					if(j>0){
						tv.setText(datelist.get(j-1).getDate().substring(0, 10));
					}
				}else{
					if(j==0){
						if(s_time<10){
							tv.setText("0"+(s_time++)+"시");
						}else{
							tv.setText((s_time++)+"시");
						}
					}else{
						tv.setText(".");
						tv.setTextColor(Color.parseColor("#bbeeff"));
						tv.setHint(datelist.get(j-1).getDate().substring(0, 10)+" "+(Integer.parseInt(start_time)+i-1));

						boolean check=false;
						for(int z=0; z<selectedlist.size(); z++){
							if(selectedlist.get(z).equals(tv.getHint())){
								check=true;
							}
						}
						if(check){
							tv.setBackgroundResource(R.drawable.table_sel);
//							tv.setAlpha((float) 0.2);
							tv.setAlpha((float) 0.4);		// 1~2명 이상
							tv.setAlpha((float) 0.6);		// 25%이상
							tv.setAlpha((float) 0.8);		// 50% 이상
							tv.setAlpha((float) 1.0);		// 75% 이상
						}

					}
				}
				row.addView(tv);
			}
			tablelayout.addView(row);
		}
		Button button = (Button)findViewById(R.id.vote);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(RoomActivity.this, RoomVoteActivity.class);
				Bundle b = new Bundle();
				b.putParcelableArrayList("datelist", datelist);
				b.putString("s_time", start_time);
				b.putString("e_time", end_time);
				i.putExtras(b);
				startActivity(i);
			}
		});

	}


}
