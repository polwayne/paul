package de.paulwein.paul.apis;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

public class GoogleWeather {
	
	private static final String GOOGLE_URL = "http://www.google.com";
	private static final String GOOGLE_WEATHER_URL = GOOGLE_URL + "/ig/api?hl=de&weather=";
	
	
	public String[] getCurrentWeather(String search){
		String[] response = {"Keine Ahnung","","/ig/images/weather/chance_of_rain.gif"};
		String xml = getXmlFromUrl(search);
		Document dom = getDomElement(xml);
		NodeList nl = dom.getElementsByTagName("xml_api_reply");
		try{
		Element xml_api_reply = (Element) nl.item(0);
		Element weather = (Element) xml_api_reply.getElementsByTagName("weather").item(0);
		Element current_conditions = (Element) weather.getElementsByTagName("current_conditions").item(0);
		Element condition = (Element) current_conditions.getElementsByTagName("condition").item(0);
		Element temp = (Element) current_conditions.getElementsByTagName("temp_c").item(0);
		Element icon = (Element) current_conditions.getElementsByTagName("icon").item(0);
		response[0] = condition.getAttribute("data");
		response[1] = temp.getAttribute("data");
		response[2] = icon.getAttribute("data");
		if(TextUtils.isEmpty(response[0])){
			response[0] = "Keine Ahnung";
			response[1] = "";
			response[2] = "/ig/images/weather/chance_of_rain.gif";
		}
		} catch(Exception e){}
		return response;
	}
	
	public String[] getCurrentWeather(){
		return getCurrentWeather("regensburg");
	}
	
	public String[] getForecastWeather(String search,int day){
		String[] response = {"Keine Ahnung","","","/ig/images/weather/chance_of_rain.gif"};
		String xml = getXmlFromUrl(search);
		Document dom = getDomElement(xml);
		NodeList nl = dom.getElementsByTagName("xml_api_reply");
		try{
		Element xml_api_reply = (Element) nl.item(0);
		Element weather = (Element) xml_api_reply.getElementsByTagName("weather").item(0);
		Element forecast_conditions = (Element) weather.getElementsByTagName("forecast_conditions").item(day);
		Element condition = (Element) forecast_conditions.getElementsByTagName("condition").item(0);
		Element icon = (Element) forecast_conditions.getElementsByTagName("icon").item(0);
		Element low = (Element) forecast_conditions.getElementsByTagName("low").item(0);
		Element high = (Element) forecast_conditions.getElementsByTagName("high").item(0);
		response[0] = condition.getAttribute("data");
		response[1] = low.getAttribute("data");
		response[2] = high.getAttribute("data");
		response[3] = icon.getAttribute("data");
		if(TextUtils.isEmpty(response[0])){
			response[0] = "Keine Ahnung";
			response[1] = "";
			response[2] = "";
			response[3] = "/ig/images/weather/chance_of_rain.gif";
		}
		} catch(Exception e){}
		return response;
	}
	
	public String[] getForecastWeather(int day){
		return getForecastWeather("regensburg",day);
	}
	
	public String getXmlFromUrl(String search) {
        String xml = null;
 
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(GOOGLE_WEATHER_URL + search);
 
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            xml = EntityUtils.toString(httpEntity);
 
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // return XML
        return xml;
    }
	
	public Document getDomElement(String xml){
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
 
            DocumentBuilder db = dbf.newDocumentBuilder();
 
            InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(xml));
                doc = db.parse(is); 
 
            } catch (ParserConfigurationException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (SAXException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (IOException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            }
                // return DOM
            return doc;
    }
	
	public Bitmap getWeatherIcon(String weatherIcon){
		Bitmap bmp = null;
		try {
			bmp = BitmapFactory.decodeStream((InputStream) new URL(GOOGLE_URL + weatherIcon).getContent());
		} catch (MalformedURLException e) {	e.printStackTrace(); } 
		  catch (IOException e) { e.printStackTrace(); }
		return bmp;
	}
}
