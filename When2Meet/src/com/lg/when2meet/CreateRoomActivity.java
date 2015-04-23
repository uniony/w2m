package com.lg.when2meet;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lg.util.MyHttpPost;

public class CreateRoomActivity extends Activity {

	ArrayList<DateClass> datelist = new ArrayList<DateClass>();
	static int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_room);

		final StoredDate date = new StoredDate();

		final CalendarView calendar = (CalendarView) findViewById(R.id.calendar);
		calendar.setOnDateChangeListener(new OnDateChangeListener() {
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

		ImageView btn_invite = (ImageView) findViewById(R.id.invite);
		btn_invite.setOnClickListener(new OnClickListener() {
			Spinner s1 = (Spinner) findViewById(R.id.start_time);
			Spinner s2 = (Spinner) findViewById(R.id.end_time);
			EditText room = (EditText) findViewById(R.id.room_name);

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

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
					SharedPreferences sharedPreferences = getSharedPreferences(
							"LOGIN_PREFRENCE", 0);

					CreatePartyThread createPartyThread = new CreatePartyThread(
							sharedPreferences.getString("id", ""),
							sharedPreferences.getString("pwd", ""), room
									.getText().toString(), Integer
									.parseInt(start_time), Integer
									.parseInt(end_time), datelist);
					createPartyThread.start();
				}
			}
		});
	}

	class CreatePartyThread extends Thread {
		String id;
		String pwd;
		String title;
		int fromHour;
		int toHour;
		ArrayList<DateClass> datelist;

		public CreatePartyThread(String id, String pwd, String title,
				int fromHour, int toHour, ArrayList<DateClass> datelist) {
			super();
			this.id = id;
			this.pwd = pwd;
			this.title = title;
			this.fromHour = fromHour;
			this.toHour = toHour;
			this.datelist = datelist;
		}

		@Override
		public void run() {

			String URL = "http://192.168.0.130:8080/createParty";
			JSONArray jsonArray = new JSONArray();

			for (int i = 0; i < datelist.size(); i++) {
				try {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("year", "" + datelist.get(i).getYear());
					jsonObject.put("month", "" + datelist.get(i).getMonth());
					jsonObject.put("day", "" + datelist.get(i).getDay());
					jsonArray.put(jsonObject);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			HttpPost httpPost = new HttpPost(URL + "?id=" + id + "&pwd=" + pwd
					+ "&title=" + title + "&fromHour=" + fromHour + "&toHour="
					+ toHour + "&partySchedule="
					+ URLEncoder.encode(jsonArray.toString()));

			String response = MyHttpPost.SendHttpPost(httpPost);
			JSONObject jsonObject = null;
			int partyId = 0;
			String isSuccess = "";
			try {
				jsonObject = new JSONObject(response);
				partyId = Integer.parseInt(jsonObject.getString("partyId"));
				isSuccess = jsonObject.getString("isSuccess");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			Intent intent = new Intent(CreateRoomActivity.this,
					InviteActivity.class);
			intent.putExtra("partyId", partyId);
			System.out.println();
			startActivity(intent);
		}
	}
}
