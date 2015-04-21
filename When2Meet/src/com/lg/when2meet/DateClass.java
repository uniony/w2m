package com.lg.when2meet;

import android.os.Parcel;
import android.os.Parcelable;

public class DateClass implements Parcelable {
	private int day;
	private int month;
	private int year;
	private int count;

	public DateClass(){
		day=month=year=count=0;
	}
	public DateClass(int day, int month, int year, int count) {
		this.day = day;
		this.month = month;
		this.year = year;
		this.count=count;
	}

	public int getDay() {
		return day;
	}

	public int getMonth() {
		return month;
	}

	public int getYear() {
		return year;
	}
	public int getCount(){
		return count;
	}


	public String getDate() {
		if (month < 10) {
			if (day < 10) {
				return year + "/0" + month + "/0" + day+ "\n\n";
			}
			return year + "/0" + month + "/" + day + "\n\n";
		}
		return year + "/" + month + "/" + day + "\n\n";
	}


	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(year);
		dest.writeInt(month);
		dest.writeInt(day);
	}

	public static final Parcelable.Creator<DateClass> CREATOR = new Parcelable.Creator<DateClass>() {
		public DateClass createFromParcel(Parcel in) {
			DateClass dc = new DateClass();
			dc.year = in.readInt();
			dc.month = in.readInt();
			dc.day = in.readInt();
			return dc;
		}

		public DateClass[] newArray(int size) {
			return new DateClass[size];
		}
	};


}
