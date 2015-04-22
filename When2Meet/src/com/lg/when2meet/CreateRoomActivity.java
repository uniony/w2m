package com.lg.when2meet;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CreateRoomActivity extends Activity {

	ArrayList<DateClass> datelist = new ArrayList<DateClass>();
	static int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_room);

		final StoredDate date = new StoredDate();

		final CalendarView cv = (CalendarView) findViewById(R.id.calendarView1);
		cv.setOnDateChangeListener(new OnDateChangeListener() {
			@Override
			public void onSelectedDayChange(CalendarView view, int year,
					int month, int dayOfMonth) {
				// TODO Auto-generated method stub
				int d = dayOfMonth;
				int m = month+1;
				int y = year;
				int index = -1;
				int check_unecessary_change = -1;
				boolean stored = false;
				DateClass newDate = new DateClass(d, m, y, count++);

				for (int i = 0; i < datelist.size(); i++) {
					if (d == datelist.get(i).getDay()
							&& m == datelist.get(i).getMonth()
							&& y == datelist.get(i).getYear()) {
						stored = true;
						index = i;
					}
					if (check_unecessary_change < ((DateClass) datelist.get(i))
							.getCount()) {
						check_unecessary_change = ((DateClass) datelist.get(i))
								.getCount();
					}
				}

				if (stored == false) {
					datelist.add(newDate);
				} else if (stored == true
						&& ((DateClass) datelist.get(index)).getCount() != check_unecessary_change) {
					datelist.remove(index);
				}
				TextView t = (TextView) findViewById(R.id.storedDates);
				t.setText(date.setStoredDates(datelist));
				t.setTextColor(Color.parseColor("#F5908D"));
			}
		});

		ImageView b = (ImageView) findViewById(R.id.invite);
		b.setOnClickListener(new OnClickListener() {
			Spinner s1 = (Spinner) findViewById(R.id.start_time);
			Spinner s2 = (Spinner) findViewById(R.id.end_time);
			EditText room = (EditText) findViewById(R.id.room_name);

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(CreateRoomActivity.this,
						InviteActivity.class);
				Bundle b = new Bundle();

				String start_time = (String.valueOf(s1.getSelectedItem()))
						.substring(0, 2);
				String end_time = (String.valueOf(s2.getSelectedItem()))
						.substring(0, 2);

				if (Integer.parseInt(start_time) >= Integer.parseInt(end_time)) {
					Toast.makeText(CreateRoomActivity.this,
							"종료시간이 시작시간보다 빠릅니다", Toast.LENGTH_SHORT).show();
				} else if (datelist.size() == 0){
					Toast.makeText(CreateRoomActivity.this,
							"날짜를 선택하세요", Toast.LENGTH_SHORT).show();
				} else {
					b.putParcelableArrayList("datelist", datelist);
					b.putString("s_time", start_time);
					b.putString("e_time", end_time);
					b.putString("room_name", room.getText().toString());
					i.putExtras(b);
					startActivity(i);
				}
			}
		});
	}
}
