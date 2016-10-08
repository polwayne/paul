package de.paulwein.paul.apis;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

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

import android.text.TextUtils;
import android.util.Log;

public class Wikipedia {
	
	private static final String WIKIPEDIA_URL = "https://de.wikipedia.org/w/api.php?action=opensearch&format=xml&limit=1&search=";
	
	
	public String search(String search){
		String response = "Keine Ahnung";
		String xml = getXmlFromUrl(search);
		Document dom = getDomElement(xml);
		Log.e("xml", "xml " + xml);
		NodeList nl = dom.getElementsByTagName("SearchSuggestion");
		try{
		Element searchSuggestion = (Element) nl.item(0);
		Element section = (Element) searchSuggestion.getElementsByTagName("Section").item(0);
		Element item = (Element) section.getElementsByTagName("Item").item(0);
		Element description = (Element) item.getElementsByTagName("Description").item(0);
		response = description.getFirstChild().getNodeValue();
		if(TextUtils.isEmpty(response)) 
			response = "Keine Ahnung";
		} catch(Exception e){}
		return response;
	}
	
	public String getXmlFromUrl(String search) {
        String xml = null;
 
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(WIKIPEDIA_URL + search);
 
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
}
