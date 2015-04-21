package com.lg.when2meet;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	ImageView login;
	ImageView join;
	EditText login_id;
	EditText login_pwd;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		login = (ImageView) findViewById(R.id.login);
		join = (ImageView) findViewById(R.id.join);
		login_id = (EditText) findViewById(R.id.login_id);
		login_pwd = (EditText) findViewById(R.id.login_pwd);
		
		final Handler handler = new Handler(){
			public void handleMessage(android.os.Message msg) {
				Toast.makeText(LoginActivity.this, "아이디나 비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
			};
		};
		
		login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				login.setImageResource(R.drawable.button_login_click);
				
				String loginId = login_id.getText().toString();
				String loginPwd = login_pwd.getText().toString();
				
				if(loginId.equals("") || loginId == null){
					Toast.makeText(LoginActivity.this, "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
				}else if(loginPwd.equals("") || loginPwd == null){
					Toast.makeText(LoginActivity.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
				}
				
				new Thread(){
					@Override
					public void run() {
						String loginId = login_id.getText().toString();
						String loginPwd = login_pwd.getText().toString();
						
						String result = SendByHttpLogin(loginId, loginPwd);
						
						SharedPreferences setting = getSharedPreferences("login", 0);
						SharedPreferences.Editor editor = setting.edit();
						
						try {
							JSONObject json = new JSONObject(result);
							String isSuccess = json.getString("isSuccess");
							
							if(isSuccess.equals("true")){
								editor.putString("id", loginId);
								editor.putString("pwd", loginPwd);
								
								editor.commit();
								Intent intent = new Intent(getApplicationContext(),
										ListActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
							}else{
								handler.sendMessage(Message.obtain());
							}
						
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//String[][] parsedData = jsonParserList(result);
					};
				}.start();
				
			}
		});

		join.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				join.setImageResource(R.drawable.button_join_click);
				Intent intent = new Intent(getApplicationContext(),
						JoinActivity.class);
				startActivity(intent);
			}
		});
	}
	
	/**
	 * 서버에 데이터를 보내는 메소드
	 */
	private String SendByHttpLogin(String phoneNo, String pwd) {
		String URL = "http://192.168.0.130:8080/checkJoin";
		
		DefaultHttpClient client = new DefaultHttpClient();
		try {
			/* 체크할 id와 pwd값 서버로 전송 */
			HttpPost post = new HttpPost(URL+"?phoneNo="+phoneNo+"&pwd="+pwd);

			/* 지연시간 최대 5초 */
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000);
			HttpConnectionParams.setSoTimeout(params, 3000);

			/* 데이터 보낸 뒤 서버에서 데이터를 받아오는 과정 */
			HttpResponse response = client.execute(post);
			BufferedReader bufreader = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent(),
							"utf-8"));

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
