package com.dailyphebus;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class GetAndTreatSchedules {
	
	private BusLine busLine = null;
	private BusStop busStop = null;
	
	GetAndTreatSchedules () {}
	
	
	public ArrayList<BusLine> getBusLines () {
		ArrayList<BusLine> resultList = new ArrayList<BusLine>();
		GetServerData gsd = new GetServerData();
		try {
			JSONObject jObject = new JSONObject(gsd.sendAndLoadGet("mode=getBusLines"));
			JSONArray jArray = jObject.getJSONArray("busLineList");
			resultList.add(new BusLine("0", "0", "0"));
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject jData = jArray.getJSONObject(i);
				resultList.add(new BusLine((String) jData.get("letter"), (String) jData.get("firstExtremity"), (String) jData.get("secondExtremity")));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return resultList;
	}
	
	
	public ArrayList<BusStop> getBusStops (BusLine busLine) {
		this.busLine = busLine;
		ArrayList<BusStop> resultList = new ArrayList<BusStop>();
		GetServerData gsd = new GetServerData();
		try {
			JSONObject jObject = new JSONObject(gsd.sendAndLoadGet("mode=getBusStops&letter=" + busLine.getLetter()));
			JSONArray jArray = jObject.getJSONArray("busStopList");
			resultList.add(new BusStop("0", "0"));
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject jData = jArray.getJSONObject(i);
				resultList.add(new BusStop((String) jData.get("name"), (String) jData.get("code")));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return resultList;
	}
	
	
	
	public String getBusSchedules (BusStop busStop, String date, String hour, short direction) {
		this.busStop = busStop;
        
        String hourQueryString = "";
        hourQueryString = "&hour=" + hour;

		GetServerData gsd = new GetServerData();
		Log.e("data", "mode=getBusSchedules&letter=" + this.busLine.getLetter() + "&codeBusStop=" + busStop.getCode() + "&direction=" + direction + "&date=" + date + hourQueryString);
		return gsd.sendAndLoadGet("mode=getBusSchedules&letter=" + this.busLine.getLetter() + "&codeBusStop=" + busStop.getCode() + "&direction=" + direction + "&date=" + date + hourQueryString);
	}
	
	

	public BusStop getBusStop() {
		return busStop;
	}

	public void setBusStop(BusStop busStop) {
		this.busStop = busStop;
	}

	public BusLine getBusLine() {
		return busLine;
	}


	public void setBusLine(BusLine busLine) {
		this.busLine = busLine;
	}
	
}
