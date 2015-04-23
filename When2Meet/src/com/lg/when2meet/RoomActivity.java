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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


public class RoomActivity extends Activity {
	int total_size;
	ArrayList<String> votelist = new ArrayList<String>();
	static int count=0;
	String start_time, end_time, room_name;
	//	ArrayList<String> memlist;
	ArrayList<DateClass> datelist = new ArrayList<DateClass>();
	TextView room;
	ArrayList<PartyClass> partylist;
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(RoomActivity.this, ListActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room);

		votelist.clear();
		final int partyId;

		Bundle bundle = this.getIntent().getExtras();
		room = (TextView)findViewById(R.id.room_name);

		start_time = bundle.getString("s_time");
		end_time = bundle.getString("e_time");
		room_name = bundle.getString("room_name");
		final int index = bundle.getInt("index");
		final int time_span = (Integer.parseInt(end_time) - Integer.parseInt(start_time));

		room.setText(room_name);
		final int s_time = Integer.parseInt(start_time);
		partylist = bundle.getParcelableArrayList("partylist");
		partyId = partylist.get(index).getId();
		//		memlist = bundle.getStringArrayList("mem_name");
		ArrayList<String> selectedlist = bundle.getStringArrayList("selectedlist");

//		Log.d("party info in room", partyId+", "+room_name+", "+ index);
		
		final Handler handler = new Handler(){
			public void handleMessage(android.os.Message msg){
				TextView date = (TextView) findViewById(R.id.date_set);
				date.setText(datelist.get(0).getDate().substring(0, 10)+" ~ "+datelist.get(datelist.size()-1).getDate().substring(0, 10));
				TextView time = (TextView) findViewById(R.id.time_set);
				time.setText("약속 시간: " + start_time + "시 ~ " + end_time + "시");

				TableLayout tablelayout = (TableLayout)findViewById(R.id.table);
				int s_time_tmp = s_time;
				for(int i=0; i<time_span+1; i++){
					TableRow row = new TableRow(RoomActivity.this);
					row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					for(int j=0; j<datelist.size()+1; j++){	
						final TextView cell = new TextView(RoomActivity.this);
						cell.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
						cell.setGravity(Gravity.CENTER);
						int pad_size=5;
						cell.setPadding(pad_size, pad_size, pad_size, pad_size);
						cell.setBackgroundResource(R.drawable.table_border); 
						cell.setHighlightColor(0);

						if(i==0){
							if(j>0){
								cell.setText(datelist.get(j-1).getDate().substring(0, 10));
								cell.setTextColor(Color.parseColor("#F5908D"));
							}
						}else{
							if(j==0){
								if (s_time_tmp < 9) {
									cell.setText("0" + (s_time_tmp++) + "~0"+s_time_tmp+"시");
								} else if(s_time==9){
									cell.setText("0"+(s_time_tmp++) + "~"+s_time_tmp+"시");
								} else{
									cell.setText((s_time_tmp++)+"~"+s_time_tmp+"시");
								}
								cell.setTextColor(Color.parseColor("#F5908D"));
							}else{
								SharedPreferences setting = getSharedPreferences("LOGIN_PREFRENCE", 0);
								String mem_name = setting.getString("name", "");
								cell.setText(".");
								cell.setTextColor(Color.parseColor("#bbeeff"));
								String str = datelist.get(j-1).getDate().substring(0, 10)+" "+(Integer.parseInt(start_time)+i-1);
								cell.setHint(str);
								cell.setTextColor(Color.parseColor("#F5908D"));

								int occurrences=0;

								boolean check=false;
								for(int z=0; z<votelist.size(); z++){
									if(votelist.get(z).split("@")[0].equals(cell.getHint())){
										occurrences++;
										check=true;
									}
								}
								if(check){
//									cell.setBackgroundResource(R.drawable.table_sel);
									cell.setBackgroundResource(R.drawable.cell_shape_sel);

//									int occurrences = Collections.frequency(votelist, str);
									count = partylist.get(index).getMemberList().length();
									cell.setText(occurrences+"");
									if(occurrences>0)
										cell.setAlpha((float) 0.2);		// 1~2명 이상...?
									if(occurrences>=count*0.2)
										cell.setAlpha((float) 0.4);		// 20%이상
									if(occurrences>=count*0.4)
										cell.setAlpha((float) 0.6);		// 40% 이상
									if(occurrences>=count*0.6)
										cell.setAlpha((float) 0.8);		// 65% 이상
									if(occurrences>=count*0.8)
										cell.setAlpha((float) 1.0);		// 80% 이상
								}
							}
							cell.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub

									String memberlist_of_schedule="";
									for(int k=0;k<votelist.size();k++){
										String votelist_elements[] = votelist.get(k).split("@");
										if(votelist.get(k).split("@")[0].equals(cell.getHint()) && !(memberlist_of_schedule.contains(votelist_elements[1]))){
											memberlist_of_schedule+=votelist_elements[1]+", ";
										}
									}
									if(memberlist_of_schedule.length()!=0){
										memberlist_of_schedule = memberlist_of_schedule.substring(0, memberlist_of_schedule.length()-2);
									}
									// Log.d("memberlist of schedule", cell.getHint()+" : "+memberlist_of_schedule);
									Toast.makeText(RoomActivity.this, memberlist_of_schedule, Toast.LENGTH_SHORT).show();
								}
							});
						}
						row.addView(cell);
					}
					tablelayout.addView(row);
				}
			};
		};

		new Thread(){
			@Override
			public void run() {
				String result1 = SendByHttpVoteList(partyId);
				try {
					JSONObject json = new JSONObject(result1);
					JSONArray jsonArray = new JSONArray(json.getString("voteList"));
					JSONObject json_voteList ;

					for (int i = 0; i < jsonArray.length(); i++) {
						json_voteList = (JSONObject) jsonArray.get(i);
						String vote_month = json_voteList.getString("month");
						if(Integer.parseInt(vote_month)<10){
							vote_month="0"+vote_month;
						}
						String vote_day = json_voteList.getString("day");
						if(Integer.parseInt(vote_day)<10){
							vote_day="0"+vote_day;
						}
						String vote_tmp = json_voteList.getString("year")+"/"+vote_month+"/"+vote_day+" "+json_voteList.getString("hour")+"@"+json_voteList.getString("name");
						// Log.d("room_Votes!", vote_tmp+""+jsonArray.length());
						votelist.add(vote_tmp);

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				String result2 = SendByHttpDateList(partyId);
				String decode_str = "";
				try {
					decode_str = URLDecoder.decode(result2, "UTF-8");
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					JSONObject json = new JSONObject(decode_str);
					JSONArray jsonArray = new JSONArray(json.getString("partyInfoList"));
					JSONObject json_date ;
					for (int i = 0; i < jsonArray.length(); i++) {
						json_date = (JSONObject) jsonArray.get(i);
						DateClass date_tmp = new DateClass(Integer.parseInt(json_date.getString("day")),
								Integer.parseInt(json_date.getString("month")),
								Integer.parseInt(json_date.getString("year")), 0); 
						datelist.add(date_tmp);

					}
					handler.sendMessage(Message.obtain());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}.start();

		ImageView vote = (ImageView)findViewById(R.id.vote);
		vote.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(RoomActivity.this, RoomVoteActivity.class);
				Bundle b = new Bundle();
				b.putParcelableArrayList("datelist", datelist);
				b.putParcelableArrayList("partylist", partylist);
				b.putInt("index", index);
				b.putString("s_time", start_time);
				b.putString("e_time", end_time);
				b.putString("room_name", room_name);
				i.putExtras(b);
				startActivity(i);
			}
		});
	}

	private String SendByHttpVoteList(int id) {
		String URL = "http://192.168.0.130:8080/getMemberSchedule";
		
		DefaultHttpClient client = new DefaultHttpClient();
		try {
			HttpPost post = new HttpPost(URL+"?partyId="+id);

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

	private String SendByHttpDateList(int id) {
		String URL = "http://192.168.0.130:8080/getPartyInfo";

		DefaultHttpClient client = new DefaultHttpClient();
		try {
			HttpPost post = new HttpPost(URL+"?partyId="+id);

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