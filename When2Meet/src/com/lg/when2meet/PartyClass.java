package com.lg.when2meet;

import android.os.Parcel;
import android.os.Parcelable;

public class PartyClass implements Parcelable {
	private String title;
	private int id;
	private String date;
	private int fromHour;
	private int toHour;
	private String memberList;
	public PartyClass(String title, int id, 	String date, int fromHour, int toHour, String memberList) {
		super();
		this.title = title;
		this.id = id;
		this.date = date;
		this.fromHour = fromHour;
		this.toHour = toHour;
		this.memberList = memberList;
	}
	public PartyClass() {
		// TODO Auto-generated constructor stub
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getFromHour() {
		return fromHour;
	}
	public void setFromHour(int fromHour) {
		this.fromHour = fromHour;
	}
	public int getToHour() {
		return toHour;
	}
	public void setToHour(int toHour) {
		this.toHour = toHour;
	}
	public String getMemberList() {
		return memberList;
	}
	public void setMemberList(String memberList) {
		this.memberList = memberList;
	}
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(title);
		dest.writeInt(id);
		dest.writeString(date);
		dest.writeInt(fromHour);
		dest.writeInt(toHour);
		dest.writeString(memberList);
	}

	public static final Parcelable.Creator<PartyClass> CREATOR = new Parcelable.Creator<PartyClass>() {
		public PartyClass createFromParcel(Parcel in) {
			PartyClass pc = new PartyClass();
			pc.title = in.readString();
			pc.id = in.readInt();
			pc.date = in.readString();
			pc.fromHour = in.readInt();
			pc.toHour = in.readInt();
			pc.memberList = in.readString();
			return pc;
		}

		public PartyClass[] newArray(int size) {
			return new PartyClass[size];
		}
	};
}
