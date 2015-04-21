package com.lg.when2meet;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class JoinActivity extends Activity {
	ImageView join;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join);

		join = (ImageView)findViewById(R.id.join);
		
		join.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(ListActivity.this, ListActivity.class);
//				startActivity(intent);
			}
		});
	}
}
