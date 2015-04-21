package com.lg.when2meet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class JoinActivity extends Activity {
	ImageView join;
	EditText join_name;
	EditText join_phone;
	EditText join_pwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join);

		join = (ImageView)findViewById(R.id.join2);
		join_name = (EditText)findViewById(R.id.join_name);
		join_phone = (EditText)findViewById(R.id.join_phone);
		join_pwd = (EditText)findViewById(R.id.join_pwd);
		
		final Handler handler = new Handler(){
			public void handleMessage(android.os.Message msg){
				Toast.makeText(JoinActivity.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
			};
		};
		
		join.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String name = join_name.getText().toString();
				String phone = join_phone.getText().toString();
				String pwd = join_pwd.getText().toString();
				
				if (name.equals("") || name == null){
					Toast.makeText(getApplicationContext(), "이름을 입력하세요", Toast.LENGTH_SHORT).show();
				} else if (phone.equals("") || phone == null){
					Toast.makeText(getApplicationContext(), "핸드폰 번호를 입력하세요", Toast.LENGTH_SHORT).show();
				} else if (pwd.equals("") || pwd == null){
					Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
				}
				
				new Thread(){
					@Override
					public void run() {
						String name = join_name.getText().toString();
						String phone = join_phone.getText().toString();
						String pwd = join_pwd.getText().toString(); 
						
						String result = sendByHttpJoin(name, phone, pwd);
						
						try {			
							JSONObject json = new JSONObject(result);
							String isSuccess = json.getString("isSuccess");
							
							if (isSuccess.equals("true")){
								
								handler.sendMessage(Message.obtain());
								
								Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
								
							} 
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					};
				}.start();	
				
			}
		});
	}
	
	private String sendByHttpJoin(String name, String phone, String pwd){
		String URL = "http://192.168.0.130:8080/addMember";
		
		DefaultHttpClient client = new DefaultHttpClient();
		
		
		try {
			Log.d("phone", phone);
			HttpPost post = new HttpPost(URL+"?name=" + name + "&phoneNo=" + phone + "&pwd=" + pwd);
			
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000);
			HttpConnectionParams.setSoTimeout(params, 3000);
			
			HttpResponse response = client.execute(post);
			BufferedReader bufreader = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent(), "utf-8"));
			
			String line = null;
			String result = "";
			
			while ((line = bufreader.readLine()) != null){
				result += line;
			}
			
			return result;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			client.getConnectionManager().shutdown();
			
			return "";
		}
		
	}
}
