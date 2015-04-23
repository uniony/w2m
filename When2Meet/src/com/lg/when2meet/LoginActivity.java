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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	ImageView login;
	ImageView join;
	EditText login_id;
	EditText login_pwd;
	CheckBox auto_login;

	String loginId;
	String loginPwd;

	SharedPreferences setting;
	SharedPreferences.Editor editor;
	String result;
	Boolean isAuto;

	public static final String LOGIN_PREFRENCE = "login_pref";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		login = (ImageView) findViewById(R.id.login);
		join = (ImageView) findViewById(R.id.join);
		login_id = (EditText) findViewById(R.id.login_id);
		login_pwd = (EditText) findViewById(R.id.login_pwd);
		auto_login = (CheckBox) findViewById(R.id.auto_login);

		final Handler handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				Toast.makeText(LoginActivity.this, "아이디나 비밀번호가 일치하지 않습니다",
						Toast.LENGTH_SHORT).show();
			};
		};

//		final Handler connectErr = new Handler() {
//			public void handleMessage(android.os.Message msg) {
//				Toast.makeText(LoginActivity.this, "접속 오류 입니다 잠시 후 시도해주세요",
//						Toast.LENGTH_SHORT).show();
//			};
//		};

		setting = getSharedPreferences("LOGIN_PREFRENCE", 0);
		isAuto = setting.getBoolean("isAuto", false);
//		Log.d("check", "bool " + isAuto);

		if (isAuto) {
			new Thread() {
				@Override
				public void run() {

					loginId = setting.getString("id", "");
					loginPwd = setting.getString("pwd", "");
					result = SendByHttpLogin(loginId, loginPwd);

					try {
						JSONObject json = new JSONObject(result);
						String isSuccess = json.getString("isSuccess");

						if (isSuccess.equals("true")) {
							goListActivity();
//						} else {
//							connectErr.sendMessage(Message.obtain());
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				};
			}.start();
		}

		login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				loginId = login_id.getText().toString();
				loginPwd = login_pwd.getText().toString();

				if (loginId.equals("") || loginId == null) {
					Toast.makeText(LoginActivity.this, "아이디를 입력하세요",
							Toast.LENGTH_SHORT).show();
				} else if (loginPwd.equals("") || loginPwd == null) {
					Toast.makeText(LoginActivity.this, "비밀번호를 입력하세요",
							Toast.LENGTH_SHORT).show();
				} else {

					new Thread() {
						@Override
						public void run() {
							result = SendByHttpLogin(loginId, loginPwd);
							editor = setting.edit();

							try {
								JSONObject json = new JSONObject(result);
								String isSuccess = json.getString("isSuccess");

								if (isSuccess.equals("true")) {
									editor.putString("id", loginId);
									editor.putString("pwd", loginPwd);
									editor.putString("name", json.getString("name"));
									if (auto_login.isChecked()) {
										editor.putBoolean("isAuto", true);
										Log.d("check", "after commit: " + setting.getBoolean("isAuto", false));
									}
									editor.commit();
									goListActivity();
								} else {
									handler.sendMessage(Message.obtain());
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
						};
					}.start();
				}
			}
		});

		join.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						JoinActivity.class);
				startActivity(intent);
			}
		});
	}

	void goListActivity() {
		Intent intent = new Intent(getApplicationContext(), ListActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	/**
	 * 서버에 데이터를 보내는 메소드	 */
	private String SendByHttpLogin(String phoneNo, String pwd) {
		String URL = "http://192.168.0.130:8080/checkJoin";

		DefaultHttpClient client = new DefaultHttpClient();
		try {
			/* 체크할 id와 pwd값 서버로 전송 */
			HttpPost post = new HttpPost(URL + "?phoneNo=" + phoneNo + "&pwd="
					+ pwd);

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
			client.getConnectionManager().shutdown(); // 연결 지연 종료
			return "";
		}
	}
}
