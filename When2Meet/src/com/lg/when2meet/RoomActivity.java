package com.lg.when2meet;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RoomActivity extends Activity {
	int total_size;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room);

		Bundle b = this.getIntent().getExtras();
		String start_time = b.getString("s_time");
		String end_time = b.getString("e_time");
		ArrayList<DateClass> datelist = b.getParcelableArrayList("datelist");
		TextView t = (TextView) findViewById(R.id.setting);

		total_size = datelist.size()
				* (Integer.parseInt(end_time) - Integer.parseInt(start_time));

		GridView gridview = (GridView) findViewById(R.id.grid);
		gridview.setAdapter(new ImageAdapter(this));
		gridview.setNumColumns(datelist.size());
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Toast.makeText(RoomActivity.this, "" + position,
						Toast.LENGTH_SHORT).show();
				ImageView imageView = (ImageView) v;
				imageView.setImageResource(R.drawable.icon1);

			}
		});
	}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return total_size;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;

			int[] imgs = new int[total_size];
			for (int i = 0; i < total_size; i++) {
				imgs[i] = R.drawable.icon2;
			}

			if (convertView == null) {
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(8, 8, 8, 8);
			} else {
				imageView = (ImageView) convertView;
			}
			imageView.setImageResource(imgs[position]);
			return imageView;
		}
	}
}
