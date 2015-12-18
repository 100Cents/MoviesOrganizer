package hcents.lifefolders.video.tts;

import hcents.lifefolders.video.tts.controller.Controller;

import java.io.*;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.json.*;

import com.cedarsoftware.util.io.JsonWriter;


public class MoviesUpdaterFromWeb {

	public static void main(String[] args) {
		/*
		JSONObject jo = Utility.readJSON("movies_list28.txt");
		
		JSONArray jarray = jo.getJSONArray("movies_list");
		
		
		for (Object myobj : jarray) {
			
			JSONObject myJsonObject = (JSONObject)myobj;
			
			JSONObject myData = myJsonObject.getJSONObject("myData");
			
			if (!myJsonObject.has("omdb")) {
				
				JSONObject titleObject = myData.getJSONObject("title");
				
				if (titleObject.has("en")) {
				
					int year = myData.getInt("year");
					
					new Thread(
							new Runnable() {
								public void run() {
									
									try {
										//http://www.omdbapi.com/?t=once+upon+a+time+in+america&y=2003&plot=short&r=json
										String nt_title = titleObject.getString("en");
										nt_title = URLEncoder.encode(nt_title, "UTF-8");
										
										System.out.println("Try: " + nt_title);
										//System.setProperty("http.proxyHost", "195.213.138.202");
										//System.setProperty("http.proxyPort", "8080");
										JSONObject json = Controller.readJsonFromUrl("http://www.omdbapi.com/?t=" + nt_title + "&y=" + year + "&plot=short&r=json");
										
										System.out.println("RES");
										
										// Devo capire cosa mi risponde:
										String response = (String) json.get("Response");
										if (response != null) {
											if (response.equals(("True"))) {
												String e_title = json.getString("Title");
												if (nt_title.equals(e_title)) {
													myJsonObject.put("omdb", json);
													
													myData.remove("imdbCode");
													myData.put("imdbCode", json.getString("imdbID"));
													System.out.println(json.getString("imdbID") + ":OK");
												}
											} else if (response.equals(("False"))) {
												System.out.println("Non esiste.");
											}
										}
										
										
										
										
									} catch (JSONException | IOException e) {
										e.printStackTrace();
									}
									
									
								}
							}).start();
					
					
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}
			
		}
		
		try {
			Thread.sleep(60000);
			Utility.writeFile(jo, "movies_list29.txt");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	


}
