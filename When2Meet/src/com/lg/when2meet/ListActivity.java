package com.lg.when2meet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListActivity extends Activity {
	ListView listView;
	CustomAdapter adapter;
	ImageView add;
	ImageView del;
	ArrayList<String> deleteList;
	ArrayList<DateClass> datelist;
	int clickTime=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		ArrayList<String> roomname = new ArrayList<String>();
		ArrayList<String> member = new ArrayList<String>();
		String pinkColor = "#F5908D";
		
		SharedPreferences setting = getSharedPreferences("login", 0);
		final String id = setting.getString("id", "");
		
		new Thread(){
			@Override
			public void run() {
				String result = SendByHttpPartyList(id);
				try {
					JSONObject json = new JSONObject(result);
					JSONArray jsonArray = new JSONArray(json.getString("partyList"));
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}.start();
		
		roomname.add("room2");
		roomname.add("room3");

		adapter = new CustomAdapter(this, R.layout.room_list, roomname, member, false);
		listView = (ListView) findViewById(R.id.room_list);
		listView.setDivider(new ColorDrawable(Color.parseColor(pinkColor)));
		listView.setDividerHeight(4);
		listView.setAdapter(adapter);

		add = (ImageView) findViewById(R.id.add_room);
		del = (ImageView) findViewById(R.id.del_room);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Toast.makeText(ListActivity.this, "clicked",
				// Toast.LENGTH_SHORT).show();

				/* datelist 임시 추가 */
				datelist = new ArrayList<DateClass>();
				datelist.clear();
				datelist.add(new DateClass(21, 4, 2015, 1));
				datelist.add(new DateClass(22, 4, 2015, 2));

				Intent intent = new Intent(ListActivity.this, RoomActivity.class);
				Bundle bundle = new Bundle();

				bundle.putParcelableArrayList("datelist", datelist);
				bundle.putString("s_time", "4");
				bundle.putString("e_time", "7");
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ListActivity.this,
						CreateRoomActivity.class);
				startActivity(intent);
			}
		});

		del.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
//				if(clickTime == 0) {
					del.setImageResource(R.drawable.button_del_click);
					adapter.changeVisibility(true);
//					clickTime++;
//				} else if(clickTime == 1) {
//					del.setImageResource(R.drawable.button_del);
//					adapter.changeVisibility(false);
//					deleteList = adapter.getSelectedRooms();
//					clickTime = 0;
//				}
				adapter.notifyDataSetChanged();
				listView.setAdapter(adapter);
			}
		});
	}
	
	/**
	 * 서버에 데이터를 보내는 메소드
	 */
	private String SendByHttpPartyList(String id) {
		String URL = "http://192.168.0.130:8080/getPartyList";
		
		DefaultHttpClient client = new DefaultHttpClient();
		try {
			/* 체크할 id와 pwd값 서버로 전송 */
			HttpPost post = new HttpPost(URL+"?id="+id);

			/* 지연시간 최대 5초 */
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000);
			HttpConnectionParams.setSoTimeout(params, 3000);

			/* 데이터 보낸 뒤 서버에서 데이터를 받아오는 과정 */
			HttpResponse response = client.execute(post);
			BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"utf-8"));

			String line = null;
			String result = "";

			while ((line = bufreader.readLine()) != null) {
				result += line;
			}
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			client.getConnectionManager().shutdown();	// 연결 지연 종료
			return ""; 
		}
		
	}
}
