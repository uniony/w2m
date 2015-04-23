package com.lg.when2meet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
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
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
	//	Bundle bundle;
	String start_time, end_time, room_name;
	ArrayList<DateClass> datelist;
	ArrayList<String> selectedlist;
	TextView room;
	ArrayList<PartyClass> partylist;
	TableLayout table;
	boolean already_voted;
	ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room_vote);

		room = (TextView)findViewById(R.id.room_name);
		table = (TableLayout) findViewById(R.id.table);		
		selectedlist = new ArrayList<String>();

		Bundle bundle = this.getIntent().getExtras();
		start_time = bundle.getString("s_time");
		end_time = bundle.getString("e_time");
		room_name = bundle.getString("room_name");
		final int index = bundle.getInt("index");
		partylist = bundle.getParcelableArrayList("partylist");
		datelist = bundle.getParcelableArrayList("datelist");
		selectedlist = bundle.getStringArrayList("selectedlist");
		already_voted = bundle.getBoolean("already_voted");

		room.setText(room_name);
		int s_time = Integer.parseInt(start_time);
		int time_span = (Integer.parseInt(end_time) - Integer.parseInt(start_time));
		total_size = datelist.size() * time_span;

		TextView date = (TextView) findViewById(R.id.date_set);
		date.setText(datelist.get(0).getDate().substring(0, 10) + " ~ "
				+ datelist.get(datelist.size() - 1).getDate().substring(0, 10));
		TextView time = (TextView) findViewById(R.id.time_set);
		time.setText("약속 시간: " + start_time + "시 ~ " + end_time + "시");

		// tablelayout.removeAllViews();
		for (int i = 0; i < time_span + 1; i++) {
			TableRow row = new TableRow(this);
			row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			for (int j = 0; j < datelist.size() + 1; j++) {
				final TextView cell = new TextView(this);
				cell.setLayoutParams(new TableRow.LayoutParams(
						TableRow.LayoutParams.WRAP_CONTENT,
						TableRow.LayoutParams.WRAP_CONTENT));
				cell.setGravity(Gravity.CENTER);
				int pad_size = 5;
				cell.setPadding(pad_size, pad_size, pad_size, pad_size);
				cell.setBackgroundResource(R.drawable.cell_shape);
				cell.setHighlightColor(0);
				if (i == 0) {
					if (j > 0) {
						cell.setText(datelist.get(j - 1).getDate().substring(0, 10));
						cell.setTextColor(Color.parseColor("#F5908D"));
					}
				} else {
					if (j == 0) {
						if (s_time < 9) {
							cell.setText("0" + (s_time++) + "~0"+s_time+"시");
						} else if(s_time==9){
							cell.setText("0"+(s_time++) + "~"+s_time+"시");
						} else{
							cell.setText((s_time++)+"~"+s_time+"시");
						}
						cell.setTextColor(Color.parseColor("#F5908D"));
					} else {
						cell.setText(".");
						cell.setTextColor(Color.parseColor("#BBEEFF"));
						cell.setHint(datelist.get(j - 1).getDate().substring(0, 10) + " " + (Integer.parseInt(start_time) + i - 1));

						boolean check=false;
						for(int z=0; z<selectedlist.size(); z++){
							if(selectedlist.get(z).equals(cell.getHint())){
//								Log.d("check seleted", cell.getHint()+", "+selectedlist.get(z));
								check=true;
							}
						}
						if(check){
							cell.setBackgroundResource(R.drawable.cell_shape_sel1);
							cell.setHighlightColor(7);
						}
						
						cell.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								if (cell.getHighlightColor() != 7) {
									cell.setBackgroundResource(R.drawable.cell_shape_sel1);
									cell.setHighlightColor(7);
									selectedlist.add((String) cell.getHint());
								} else {
									cell.setBackgroundResource(R.drawable.cell_shape);
									cell.setHighlightColor(0);
									for(int z=0; z<selectedlist.size(); z++){
										if(selectedlist.get(z).equals(cell.getHint())){
											selectedlist.remove(z);
										}
									}
								}
							}
						});
					}
				}
				row.addView(cell);
			}
			table.addView(row);
		}

		ImageView finish = (ImageView) findViewById(R.id.complete);
		ImageView reset = (ImageView) findViewById(R.id.clear);
		finish.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = ProgressDialog.show(RoomVoteActivity.this, "",
						"잠시만 기다려 주세요", true);
				// TODO Auto-generated method stub
				SharedPreferences setting = getSharedPreferences("LOGIN_PREFRENCE", 0);
				final String id = setting.getString("id", "");
				final String pwd = setting.getString("pwd", "");

				new Thread(){
					String data ="";
					public void run() {
						JSONArray jarray = new JSONArray();
						for(int i=0; i<selectedlist.size(); i++){
							String sel[] = selectedlist.get(i).split("/");
							String sel_year = sel[0];
							String sel_month = sel[1];
							String sel_[] = sel[2].split(" ");
							String sel_day = sel_[0];
							String sel_hour = sel_[1];

							if(Integer.parseInt(sel_month)<10){
								sel_month = sel_month.substring(1);
							}
							if(Integer.parseInt(sel_day)<10){
								sel_day = sel_day.substring(1);
							}
							try {
								JSONObject json = new JSONObject();
								json.put("partyId", (partylist.get(index)
										.getId() + ""));
								json.put("memberId", id);
								json.put("year", sel_year);
								json.put("month", sel_month);
								json.put("day", sel_day);
								json.put("hour", sel_hour);
								jarray.put(json);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						data = jarray.toString();
						if(already_voted==false){
							SendByHttpSelectedList(id, pwd, data);
						}else{
							SendByHttpModifySelectedList(partylist.get(index).getId()+"",id,data);
						}
//						Log.d("sel" , already_voted+"@"+data);
					}					
				}.start();

				Intent i = new Intent(RoomVoteActivity.this, RoomActivity.class);
				Bundle bundle = new Bundle();

				//				bundle.putParcelableArrayList("datelist", datelist);
				bundle.putParcelableArrayList("partylist", partylist);
				bundle.putInt("index",index);
//				bundle.putStringArrayList("selectedlist", selectedlist);
				bundle.putString("s_time", start_time);
				bundle.putString("e_time", end_time);
				bundle.putString("room_name", room_name);
				i.putExtras(bundle);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dialog.dismiss();
				startActivity(i);
			}
		});

		reset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				for (int i = 0; i < table.getChildCount(); i++) {
					for (int j = 0; j < ((TableRow) table.getChildAt(i)).getChildCount(); j++){
						if (((TableRow) table.getChildAt(i)).getChildAt(j) instanceof TextView) {
							((TextView)((TableRow) table.getChildAt(i)).getChildAt(j)).setBackgroundResource(R.drawable.table_border);
							((TextView)((TableRow) table.getChildAt(i)).getChildAt(j)).setHighlightColor(0);
						}
					}
				}
				for(int z=0; z<selectedlist.size();z++){
					selectedlist.clear();
				}
			}
		});
	}

	private String SendByHttpSelectedList(String id, String pwd, String data) {
		String URL = "http://192.168.0.130:8080/insertMemberSchedule";

		DefaultHttpClient client = new DefaultHttpClient();
		try {
			String url = URL+"?phoneNo="+id+"&pwd="+pwd+"&data="+URLEncoder.encode(data);
			//			String url = URL+"?phoneNo="+id+"&pwd="+pwd+"&data="+URLEncoder.encode(data, "UTF-8");
			HttpPost post = new HttpPost(url);

			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000);
			HttpConnectionParams.setSoTimeout(params, 3000);

			HttpResponse response = client.execute(post);
			BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"));

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
	
	private String SendByHttpModifySelectedList(String pid, String memId, String data) {
		String URL = "http://192.168.0.130:8080/updateMemberSchedule";
		
		DefaultHttpClient client = new DefaultHttpClient();
		try {
			String url = URL+"?partyId="+pid+"&memberId="+memId+"&data="+URLEncoder.encode(data);
			//			String url = URL+"?phoneNo="+id+"&pwd="+pwd+"&data="+URLEncoder.encode(data, "UTF-8");
			HttpPost post = new HttpPost(url);
			
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000);
			HttpConnectionParams.setSoTimeout(params, 3000);
			
			HttpResponse response = client.execute(post);
			BufferedReader bufreader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"));
			
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
}
