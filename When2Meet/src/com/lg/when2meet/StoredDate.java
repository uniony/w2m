package com.lg.when2meet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StoredDate {
	private String storedDates;

	public StoredDate() {
		storedDates = "";
	}

	public String getStoredDates() {
		return storedDates;
	}

	public String setStoredDates(ArrayList<DateClass> datelist) {
		// this.storedDates += storedDates;
		Collections.sort(datelist, new Comparator<DateClass>() {
	        @Override
	        public int compare(DateClass d1, DateClass d2){
	            return  d1.getDate().compareTo(d2.getDate());
	        }
	    });
		storedDates = "";
		for(int i=0; i<datelist.size(); i++){
			storedDates += datelist.get(i).getDate();
		}
		return storedDates;
	}

}
