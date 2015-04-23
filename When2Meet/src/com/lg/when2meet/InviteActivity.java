package com.lg.when2meet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lg.util.MyHttpPost;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class InviteActivity extends Activity {
	Bundle bundle;
	SearchAdapter adp_add, adp_mem;
	ArrayList<String> memberList;
	ArrayList<String> selectList;
	ArrayList<String> selectMemberIdList;
	ListView add_listView, mem_listView;
	Button btn_search, btn_add;
	ImageView create;
	EditText search_id;
	int partyId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite);
		Intent gintent = getIntent();
		partyId = gintent.getIntExtra("partyId", 0);
		if (partyId == 0) {
			Toast.makeText(this, "방 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT)
					.show();
			finish();
		}
		memberList = new ArrayList<String>();
		selectList = new ArrayList<String>();
		selectMemberIdList = new ArrayList<String>();
		// bundle = this.getIntent().getExtras();//날짜, 방 이름 정보 받아옴
		// intent = new Intent(InviteActivity.this, CreateRoomActivity.class);
		btn_search = (Button) findViewById(R.id.btn_search);
		btn_add = (Button) findViewById(R.id.btn_add);
		search_id = (EditText) findViewById(R.id.search_id);
		create = (ImageView) findViewById(R.id.invite);
		String mintColor = "#87D3DC";

		adp_add = new SearchAdapter(this, R.layout.member_list, selectList,
				false);
		add_listView = (ListView) findViewById(R.id.add_list);
		add_listView.setDivider(new ColorDrawable(Color.WHITE));
		add_listView.setDividerHeight(4);
		add_listView.setAdapter(adp_add);

		adp_mem = new SearchAdapter(InviteActivity.this, R.layout.member_list,
				memberList, true);
		mem_listView = (ListView) findViewById(R.id.mem_list);
		mem_listView.setDivider(new ColorDrawable(Color.parseColor(mintColor)));
		mem_listView.setDividerHeight(4);
		mem_listView.setAdapter(adp_mem);

		final Handler handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				adp_mem.notifyDataSetChanged();
				mem_listView.setAdapter(adp_mem);
			};
		};

		btn_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (adp_mem.getAddNum() == 0) {
					Toast.makeText(InviteActivity.this, "추가할 멤버를 먼저 선택하세요",
							Toast.LENGTH_SHORT).show();
				} else if (adp_mem.getAddNum() > 1) {
					Toast.makeText(InviteActivity.this, "한 명만 선택해주세요",
							Toast.LENGTH_SHORT).show();
				} else {
					String selected_mem = adp_mem.getaddMemName();

					if (selectList.contains(selected_mem)) {
						Toast.makeText(InviteActivity.this, "이미 선택한 ID입니다",
								Toast.LENGTH_SHORT).show();
					} else {
						StringTokenizer stringTokenizer = new StringTokenizer(selected_mem, "()");
						String member_id = stringTokenizer.nextToken();
						selectMemberIdList.add(member_id);

						selectList.add(selected_mem);
						adp_add.notifyDataSetChanged();
						add_listView.setAdapter(adp_add);

						adp_mem.clearAddList();
						memberList.clear();
						adp_mem.notifyDataSetChanged();
						mem_listView.setAdapter(adp_mem);
					}
				}
			}
		});

		btn_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				adp_mem.clearAddList() ;
				// db에서 search_id가 포함된 멤버 목록 검색해서 selectList에 넣기
				new Thread() {
					String id = search_id.getText().toString();

					public void run() {
						String result = SendByHttpSearchMember(id);
						String decode_str = "";
						try {
							decode_str = URLDecoder.decode(result, "UTF-8");
						} catch (UnsupportedEncodingException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						Log.d("invite_search", decode_str);
						try {
							JSONObject json = new JSONObject(decode_str);
							JSONArray jsonArray = new JSONArray(json
									.getString("memberList"));
							JSONObject json_member;
							String mem_id;
							String mem_name;
							String mem_result;

							memberList.clear();
							for (int i = 0; i < jsonArray.length(); i++) {
								json_member = (JSONObject) jsonArray.get(i);
								mem_id = json_member.getString("id");
								mem_name = json_member.getString("name");
								mem_result = mem_id + "(" + mem_name + ")";
								memberList.add(mem_result);
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}

						handler.sendMessage(Message.obtain());
					}
				}.start();
			}
		});

		create.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InsertMemberThread insertMemberThread = new InsertMemberThread(); 
				insertMemberThread.start();
			}
		});
	}

	class InsertMemberThread extends Thread {
		
		@Override
		public void run() {
			String response;
			JSONObject jsonObject = null;
			for(int i = 0 ; i < selectMemberIdList.size() ; i++) {
				String URL = "http://192.168.0.130:8080/inviteMember";
				HttpPost httpPost = new HttpPost(URL + "?memberId=" + selectMemberIdList.get(i) + "&partyId=" + partyId);
				response = MyHttpPost.SendHttpPost(httpPost);
				try {
					jsonObject = new JSONObject(response);
					Log.d("머지", jsonObject.getString("isSuccess"));
					if("true".equals(jsonObject.getString("isSuccess"))) {
						Log.d("!@@@@@@@@@@", "added!!" + selectMemberIdList.get(i));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			Intent intent = new Intent(InviteActivity.this,
					ListActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}

	private String SendByHttpSearchMember(String id) {
		String URL = "http://192.168.0.130:8080/getMembersById";

		DefaultHttpClient client = new DefaultHttpClient();
		try {
			HttpPost post = new HttpPost(URL + "?memberId=" + id);

			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000);
			HttpConnectionParams.setSoTimeout(params, 3000);

			HttpResponse response = client.execute(post);
			BufferedReader bufreader = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent(),
							"UTF-8"));

			String line = null;
			String result = "";

			while ((line = bufreader.readLine()) != null) {
				result += line;
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			client.getConnectionManager().shutdown();
			return "";
		}

	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
	}
}

class SearchAdapter extends BaseAdapter {
	Context context;
	int resID;
	ArrayList<String> list;
	ArrayList<String> addlist;
	boolean isMem;

	String getaddMemName() {
		if (addlist.size() == 0)
			return "";
		else
			return addlist.get(0);
	}

	public void clearAddList() {
		addlist.clear();
	}

	ArrayList<String> getMemList() {
		return list;
	}

	public int getAddNum() {
		return addlist.size();
	}
	
	public SearchAdapter(Context context, int resID, ArrayList<String> list,
			boolean isMem) {
		super();
		this.context = context;
		this.resID = resID;
		this.list = list;
		this.isMem = isMem;
		this.addlist = new ArrayList<String>();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LinearLayout view = (LinearLayout) convertView;
		final String mintColor = "#87D3DC";

		if (convertView == null) {
			view = (LinearLayout) View.inflate(context, resID, null);
		}
		final TextView mem = (TextView) view.findViewById(R.id.member_name);
		mem.setText(list.get(position));

		if (isMem) {// mem list
			mem.setBackgroundColor(Color.WHITE);
			mem.setTextColor(Color.parseColor(mintColor));
		} else {// add list
			mem.setBackgroundColor(Color.parseColor(mintColor));
			mem.setTextColor(Color.WHITE);
		}

		mem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mem.getCurrentTextColor() == Color.WHITE) {	//선택 된 것.
					mem.setBackgroundColor(Color.WHITE);
					mem.setTextColor(Color.parseColor(mintColor));
					addlist.remove(getItem(position).toString());
				} else {
					if (!(addlist.contains(getItem(position).toString()))) {
						addlist.add(getItem(position).toString());
					}

					mem.setBackgroundColor(Color.parseColor(mintColor));
					mem.setTextColor(Color.WHITE);
				}
			}
		});
		return view;
	}
	

}