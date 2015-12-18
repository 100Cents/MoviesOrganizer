package hcents.lifefolders.video.tts.controller;

import hcents.lifefolders.video.tts.*;
import hcents.lifefolders.video.tts.model.MainFrameModel;
import hcents.lifefolders.video.tts.view.SearchEvent;
import hcents.lifefolders.video.tts.view.SearchListener;
import hcents.lifefolders.video.tts.view.View;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

import javax.swing.JFrame;

import org.apache.http.client.utils.URIUtils;
import org.json.*;
import org.unbescape.html.HtmlEscape;


public class Controller implements SearchListener {
	private MainFrame mainFrame;
	private MainFrameModel mainFrameModel;
	
	/**
	 * @param view
	 * @param model
	 */
	public Controller(MainFrame mainFrame, MainFrameModel mainFrameModel) {
		super();
		this.mainFrame = mainFrame;
		this.mainFrameModel = mainFrameModel;
	}
	
	@Override
	public void searchPerformed(SearchEvent event) {
		System.out.println("Search Performed! " + event.getSearchString());
		try {
			mainFrameModel.setMoviesList(getDataFromInternet(event.getSearchString()));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> getDataFromInternet(String search) throws JSONException, IOException {
		ArrayList<String> fakeArrayList = new ArrayList<String>();
		
		String encodedSearchString = URLEncoder.encode(search, "UTF-8");
		JSONObject json = readJsonFromUrl("http://www.imdb.com/xml/find?json=1&nr=1&tt=on&q=" + encodedSearchString);
		
		if (json.has("title_exact")) {
		
			JSONArray title_exact_json_array = json.getJSONArray("title_exact");
			for (int i = 0; i < title_exact_json_array.length(); i++) {
			    String id = title_exact_json_array.getJSONObject(i).getString("id");
			    String title = HtmlEscape.unescapeHtml(title_exact_json_array.getJSONObject(i).getString("title"));
			    String title_description = title_exact_json_array.getJSONObject(i).getString("title_description");
			    fakeArrayList.add(id + ":" + title + ":" + title_description);
			}
		}
		
		if (json.has("title_popular")) {
		
			JSONArray title_popular_json_array = json.getJSONArray("title_popular");
			for (int i = 0; i < title_popular_json_array.length(); i++) {
			    String id = title_popular_json_array.getJSONObject(i).getString("id");
			    String title = HtmlEscape.unescapeHtml(title_popular_json_array.getJSONObject(i).getString("title"));
			    String title_description = title_popular_json_array.getJSONObject(i).getString("title_description");
			    fakeArrayList.add(id + ":" + title + ":" + title_description);
			}
		}
		
		if (json.has("title_substring")) {
			
			JSONArray title_substring_json_array = json.getJSONArray("title_substring");
			for (int i = 0; i < title_substring_json_array.length(); i++) {
			    String id = title_substring_json_array.getJSONObject(i).getString("id");
			    String title = HtmlEscape.unescapeHtml(title_substring_json_array.getJSONObject(i).getString("title"));
			    String title_description = title_substring_json_array.getJSONObject(i).getString("title_description");
			    fakeArrayList.add(id + ":" + title + ":" + title_description);
			}
		}
		
		if (json.has("title_approx")) {
			
			JSONArray title_approx_json_array = json.getJSONArray("title_approx");
			for (int i = 0; i < title_approx_json_array.length(); i++) {
			    String id = title_approx_json_array.getJSONObject(i).getString("id");
			    String title = HtmlEscape.unescapeHtml(title_approx_json_array.getJSONObject(i).getString("title"));
			    String title_description = title_approx_json_array.getJSONObject(i).getString("title_description");
			    fakeArrayList.add(id + ":" + title + ":" + title_description);
			}
		}
		
		
		
		

		return fakeArrayList;
	}
	
	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		//System.setProperty("http.proxyHost", "195.213.138.202");
		//System.setProperty("http.proxyPort", "8080");
		
		//InputStream is = new URL(url).openStream();
		URL u = new URL(url);
		URLConnection con = u.openConnection();
		con.setConnectTimeout(15000);
		con.setReadTimeout(15000);
		InputStream is = con.getInputStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			System.out.println(jsonText);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}
	
	private static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	  }

}
