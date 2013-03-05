package com.dailyphebus;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpResponse;


public class GetServerData {
	
	private static HttpClient httpclient = null;
	public static final int HTTP_TIMEOUT = 30000; // ms
	private final String serverURL = "http://phebus.debatz.fr/start.php?access=camchou";
	
	GetServerData () { }
	
	
	private static HttpClient getHttpClient() {  
		if (httpclient == null) {  
			httpclient = new DefaultHttpClient();  
			final HttpParams params = httpclient.getParams();  
			HttpConnectionParams.setConnectionTimeout(params, HTTP_TIMEOUT);  
			HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT);  
			ConnManagerParams.setTimeout(params, HTTP_TIMEOUT);  
		}  
		return httpclient;  
	}
	
	
	/*public String sendAndLoadPost (String queryString, List<NameValuePair> parameters) {
		HttpPost httppost = new HttpPost(this.serverURL + "&" + queryString);
		
		HttpResponse response = null;
		try {
			httppost.setEntity(new UrlEncodedFormEntity(parameters));
			HttpClient httpclient = getHttpClient();
			response = httpclient.execute(httppost);			
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		HttpEntity entity = response.getEntity();
		
		try {
			return (entity != null) ? EntityUtils.toString(entity) : null;
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return null;
	}*/
	
	
	public String sendAndLoadGet (String queryString) {
		HttpClient httpclient = getHttpClient();
		HttpGet httpget = new HttpGet(this.serverURL + "&" + queryString);
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpget);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpEntity entity = response.getEntity();
		
		try {
			return (entity != null) ? EntityUtils.toString(entity) : null;
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
