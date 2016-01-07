package de.paulwein.paul.apis;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class GoogleImages {
	
	public static final String IMAGE_SEARCH_URL = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=";
	
	public GoogleImages(){
		super();
	}
	
	
	public Bitmap searchImage(String query){
		
		String json_string = searchImages(query);
		try {
			JSONObject responseData = new JSONObject(json_string);
			JSONObject data = responseData.getJSONObject("responseData");
			JSONArray results = data.getJSONArray("results");
			if(results.length() > 0){
				JSONObject imageJson = results.getJSONObject(0);
				String imageURL = imageJson.getString("tbUrl");
				Log.e("TAG", imageURL);
				Bitmap bmp = null;
					try {
						bmp = BitmapFactory.decodeStream((InputStream) new URL(imageURL).getContent());
					} catch (MalformedURLException e) {	e.printStackTrace(); } 
					  catch (IOException e) { e.printStackTrace(); }
					return bmp;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private String searchImages(String query){
		String response = null;
		
	       try {
	            // defaultHttpClient
	            DefaultHttpClient httpClient = new DefaultHttpClient();
	            HttpGet httpGet = new HttpGet(IMAGE_SEARCH_URL + query);
	 
	            HttpResponse httpResponse = httpClient.execute(httpGet);
	            HttpEntity httpEntity = httpResponse.getEntity();
	            response = EntityUtils.toString(httpEntity);
	 
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        } catch (ClientProtocolException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        // return XML
	        return response;
	}

}
