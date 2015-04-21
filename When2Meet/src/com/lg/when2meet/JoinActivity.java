package com.lg.when2meet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class JoinActivity extends Activity {
	ImageView join;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join);

		join = (ImageView)findViewById(R.id.join2);
		
		join.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(JoinActivity.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
				
				Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
	}
}
