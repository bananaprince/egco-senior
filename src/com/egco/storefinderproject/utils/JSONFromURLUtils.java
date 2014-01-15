package com.egco.storefinderproject.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONFromURLUtils {

	public static JSONObject getJSONFromURL(String url) {

		InputStream in = null;
		String data = null;
		JSONObject result = null;

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext httpContext = new BasicHttpContext();
			HttpPost httpPost = new HttpPost(url);
			HttpResponse httpRespose = httpClient
					.execute(httpPost, httpContext);
			in = httpRespose.getEntity().getContent();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader bReader = new BufferedReader(new InputStreamReader(
					in));
			StringBuilder sBuilder = new StringBuilder();
			String line = null;
			while ((line = bReader.readLine()) != null) {
				sBuilder.append(line + "\n");
			}
			in.close();
			data = sBuilder.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			result = new JSONObject(data);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}

}
