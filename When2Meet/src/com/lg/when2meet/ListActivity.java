package com.lg.when2meet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
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
		final ArrayList<String> roomname = new ArrayList<String>();
		final ArrayList<String> member = new ArrayList<String>();
		String pinkColor = "#F5908D";

		Log.d("!!!!!!!!!!!!", "!!!!!!!!");
		SharedPreferences setting = getSharedPreferences("LOGIN_PREFRENCE", 0);
		final String id = setting.getString("id", "");
		Log.d("!!!!!!!!!!!!", "!!!!!!!!"+id);
		
		final Handler handler = new Handler(){
			public void handleMessage(android.os.Message msg){
				adapter.notifyDataSetChanged();
			};
		};
		
		new Thread(){
			@Override
			public void run() {
				String result = SendByHttpPartyList(id);
				try {
					JSONObject json = new JSONObject(result);
					JSONArray jsonArray = new JSONArray(json.getString("partyList"));
					JSONObject json_partyList, json_partyInfo, json_member;
					JSONArray json_memberList;
					
					for (int i = 0; i < jsonArray.length(); i++) {
						String member_name="";
						json_partyList = (JSONObject) jsonArray.get(i);
						json_partyInfo = (JSONObject) json_partyList.getJSONObject("partyInfo");
						roomname.add(json_partyInfo.get("title").toString());
						json_memberList = new JSONArray(json_partyInfo.getString("memberList"));
						
						for(int j=0; j<json_memberList.length(); j++){
							json_member = (JSONObject) json_memberList.get(j);
							member_name+=(json_member.get("name").toString()+", ");
						}
						member_name="("+member_name.substring(0, member_name.length()-2)+")";
						member.add(member_name);
//					Log.d("member_list", member_name.length()+"."+member_name);
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

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Toast.makeText(ListActivity.this, "clicked",
				// Toast.LENGTH_SHORT).show();

				/* datelist �ӽ� �߰� */
				datelist = new ArrayList<DateClass>();
				datelist.clear();
				datelist.add(new DateClass(21, 4, 2015, 1));
				datelist.add(new DateClass(22, 4, 2015, 2));

				Intent intent = new Intent(ListActivity.this, RoomActivity.class);
				Bundle bundle = new Bundle();

				bundle.putParcelableArrayList("datelist", datelist);
				bundle.putString("s_time", "4");
				bundle.putString("e_time", "7");
				bundle.putString("room_name", "name");//adapter���� �� �����;� �ҵ�
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
	 * ������ �����͸� ������ �޼ҵ�
	 */
	private String SendByHttpPartyList(String id) {
		String URL = "http://192.168.0.130:8080/getPartyList";
		
		DefaultHttpClient client = new DefaultHttpClient();
		try {
			/* üũ�� id�� pwd�� ������ ��� */
			HttpPost post = new HttpPost(URL+"?id="+id);
			//HttpGet get = new HttpGet(URL+"?id="+id);
			/* �����ð� �ִ� 5�� */
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000);
			HttpConnectionParams.setSoTimeout(params, 3000);

			/* ������ ���� �� �������� �����͸� �޾ƿ��� ���� */
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
			client.getConnectionManager().shutdown();	// ���� ���� ����
			return ""; 
		}
		
	}
}
