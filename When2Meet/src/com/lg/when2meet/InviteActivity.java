package com.lg.when2meet;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
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
	Intent intent;
	SearchAdapter adp_add, adp_mem;
	ArrayList<String> memberList;
	ArrayList<String> selectList;
	ListView add_listView, mem_listView;
	Button btn_search, btn_add;
	ImageView create;
	EditText search_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite);
		memberList = new ArrayList<String>();
		selectList = new ArrayList<String>();
		bundle = this.getIntent().getExtras();//날짜, 방 이름 정보 받아옴
		intent = new Intent(InviteActivity.this, CreateRoomActivity.class);
		btn_search = (Button) findViewById(R.id.btn_search);
		btn_add = (Button) findViewById(R.id.btn_add);
		search_id = (EditText) findViewById(R.id.search_id);
		create = (ImageView) findViewById(R.id.invite);
		String mintColor = "#87D3DC";

		adp_add = new SearchAdapter(this, R.layout.member_list, selectList, false);//같은거 써볼까
		add_listView = (ListView) findViewById(R.id.add_list);
		add_listView.setDivider(new ColorDrawable(Color.WHITE));
		add_listView.setDividerHeight(4);
		add_listView.setAdapter(adp_add);

		adp_mem = new SearchAdapter(InviteActivity.this, R.layout.member_list, memberList, true);
		mem_listView = (ListView) findViewById(R.id.mem_list);
		mem_listView.setDivider(new ColorDrawable(Color.parseColor(mintColor)));
		mem_listView.setDividerHeight(4);
		mem_listView.setAdapter(adp_mem);

		btn_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(adp_mem.getAddNum() == 0) {
					Toast.makeText(InviteActivity.this, "추가할 멤버를 먼저 선택하세요", Toast.LENGTH_SHORT).show();
				} else if(adp_mem.getAddNum() > 1) {
					Toast.makeText(InviteActivity.this, "한 명만 선택해주세요", Toast.LENGTH_SHORT).show();	
				} else {
					String selected_mem = adp_mem.getaddMemName();
					
					if(selectList.contains(selected_mem)) {
						Toast.makeText(InviteActivity.this, "이미 선택한 ID입니다", Toast.LENGTH_SHORT).show();
					} else {
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

				//db에서 search_id가 포함된 멤버 목록 검색해서 selectList에 넣기
				memberList.add("yhj");
				memberList.add("ljh");
				memberList.add("kys");
				memberList.add("ygy");
				memberList.add("cjy");
				adp_mem.notifyDataSetChanged();
				mem_listView.setAdapter(adp_mem);
			}
		});

		create.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent = new Intent(InviteActivity.this,
						RoomActivity.class);

				// bundle.putParcelableArrayList("mem_name", adp_add.getMemList);
				// bundle.putString("mem_name", adp_add.getaddMemName());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}
}

class SearchAdapter extends BaseAdapter{
	Context context;
	int resID;
	ArrayList<String> list;
	ArrayList<String> addlist;
	boolean isMem;

	String getaddMemName() {
		if(addlist.size() == 0)	return "";
		else	return addlist.get(0);
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

	public SearchAdapter(Context context, int resID, ArrayList<String> list, boolean isMem) {
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
		LinearLayout view = (LinearLayout)convertView;
		final String mintColor = "#87D3DC";

		if(convertView == null) {
			view = (LinearLayout)View.inflate(context, resID, null);
		}
		final TextView mem = (TextView)view.findViewById(R.id.member_name);
		mem.setText(list.get(position));

		if(isMem) {//mem list
			mem.setBackgroundColor(Color.WHITE);
			mem.setTextColor(Color.parseColor(mintColor));
		} else {//add list
			mem.setBackgroundColor(Color.parseColor(mintColor));
			mem.setTextColor(Color.WHITE);
		}			

		mem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(mem.getCurrentTextColor() == Color.WHITE) {
					mem.setBackgroundColor(Color.WHITE);
					mem.setTextColor(Color.parseColor(mintColor));
					addlist.remove(getItem(position).toString());
				} else {
					if(!(addlist.contains(getItem(position).toString()))) {
						addlist.add(getItem(position).toString());
						Log.d("check", "clicked " + addlist.get(getAddNum()-1));
					}
					
					mem.setBackgroundColor(Color.parseColor(mintColor));
					mem.setTextColor(Color.WHITE);
				}
			}
		});
		return view;
	}	
}