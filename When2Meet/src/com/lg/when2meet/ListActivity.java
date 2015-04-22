package com.lg.when2meet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

public class ListActivity extends Activity {
	ListView listView;
	CustomAdapter adapter;
	ImageView add, del, btn_logout;
	ArrayList<String> deleteList;
//	ArrayList<DateClass> datelist;
	ArrayList<PartyClass> partylist = new ArrayList<PartyClass>();
	int clickTime=0;
	SharedPreferences setting;
	SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		final ArrayList<String> roomname = new ArrayList<String>();
		final ArrayList<String> member = new ArrayList<String>();
		String pinkColor = "#F5908D";

		setting = getSharedPreferences("LOGIN_PREFRENCE", 0);
		final String id = setting.getString("id", "");
		
		final Handler handler = new Handler(){
			public void handleMessage(android.os.Message msg){
				adapter.notifyDataSetChanged();
			};
		};
		
		new Thread(){
			@Override
			public void run() {
				String result = SendByHttpPartyList(id);
				String decode_str = "";
				try {
					decode_str = URLDecoder.decode(result, "UTF-8");
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					JSONObject json = new JSONObject(decode_str);
					JSONArray jsonArray = new JSONArray(json.getString("partyList"));
					JSONObject json_partyList, json_partyInfo, json_member;
					JSONArray json_memberList;
					
					for (int i = 0; i < jsonArray.length(); i++) {
						String member_name="";
						json_partyList = (JSONObject) jsonArray.get(i);
						json_partyInfo = (JSONObject) json_partyList.getJSONObject("partyInfo");
						roomname.add(json_partyInfo.getString("title").toString());
						json_memberList = new JSONArray(json_partyInfo.getString("memberList"));
						
						for(int j=0; j<json_memberList.length(); j++){
							json_member = (JSONObject) json_memberList.get(j);
							member_name+=(json_member.getString("name")+", ");
//							try {
//								member_name_tmp = URLEncoder.encode(member_name_tmp,"EUC-KR");
//								
//							} catch (UnsupportedEncodingException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
						}
						member_name="("+member_name.substring(0, member_name.length()-2)+")";
						member.add(member_name);
						
						PartyClass party_obj = new PartyClass(json_partyInfo.get("title").toString(),
								Integer.parseInt(json_partyList.get("partyId").toString()),
								
								"1988/11/22",
								
								Integer.parseInt(json_partyInfo.get("fromHour").toString()),
								Integer.parseInt(json_partyInfo.get("toHour").toString()), 
								member_name);
						partylist.add(party_obj);
						
						handler.sendMessage(Message.obtain());
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}.start();

		adapter = new CustomAdapter(this, R.layout.room_list, roomname, member, false);
		listView = (ListView) findViewById(R.id.room_list);
		listView.setDivider(new ColorDrawable(Color.parseColor(pinkColor)));
		listView.setDividerHeight(4);
		listView.setAdapter(adapter);

		add = (ImageView) findViewById(R.id.add_room);
		del = (ImageView) findViewById(R.id.del_room);
		btn_logout = (ImageView) findViewById(R.id.btn_logout);

		btn_logout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread() {
					@Override
					public void run() {
						editor = setting.edit();
						editor.putBoolean("isAuto", false);
						editor.commit();
					};
				}.start();

				Intent intent = new Intent(ListActivity.this, LoginActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
//				datelist = new ArrayList<DateClass>();
//				datelist.clear();
//				for(int i=0; i<partylist.size(); i++){
//					String date = partylist.get(position).getDate();
//					String parts[] = date.split("/");
//
//				    int date_year = Integer.parseInt(parts[0]);
//				    int date_month = Integer.parseInt(parts[1]);
//				    int date_day = Integer.parseInt(parts[2]);
//
//					datelist.add(new DateClass(date_day, date_month, date_year, 0));
//				}
				

				Intent intent = new Intent(ListActivity.this, RoomActivity.class);
				Bundle bundle = new Bundle();

				bundle.putParcelableArrayList("partylist", partylist);
				bundle.putInt("index", position);
				bundle.putString("s_time", partylist.get(position).getFromHour()+"");
				bundle.putString("e_time", partylist.get(position).getToHour()+"");
				bundle.putString("room_name", partylist.get(position).getTitle());
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
			//HttpGet get = new HttpGet(URL+"?id="+id);
			/* 지연시간 최대 5초  */
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000);
			HttpConnectionParams.setSoTimeout(params, 3000);

			/* 데이터 보낸 뒤 서버에서 데이터를 받아오는 과정 */
			HttpResponse response = client.execute(post);
			BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

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
