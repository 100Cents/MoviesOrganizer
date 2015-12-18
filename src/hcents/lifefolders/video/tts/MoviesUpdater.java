package hcents.lifefolders.video.tts;

import hcents.lifefolders.video.tts.controller.Controller;

import java.io.*;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.json.*;

import com.cedarsoftware.util.io.JsonWriter;


public class MoviesUpdater {
	static JSONObject akatitlesJSON = readJSON("akaTitleJSON.txt");
	private static JSONArray akatitlesjarray;

	public static void main(String[] args) {
		JSONObject jo = readJSON("movies_list4.txt");
		
		JSONArray jarray = jo.getJSONArray("movies_list");
		
		int i = 0;
		for (Object myobj : jarray) {
			
			
			JSONObject myJsonObject = (JSONObject)myobj;
			
			JSONObject myData = myJsonObject.getJSONObject("myData");
			
			//if (!myJsonObject.has("omdb")) {
				
				JSONObject titleObject = myData.getJSONObject("title");
				
				
				if (titleObject.has("nt")) {
					String nt_title = titleObject.getString("nt");
					// System.out.println(nt_title);
					int year = myData.getInt("year");
					
					// Cerco il titolo che ora � in nt.
					String t = searchOriginalTitleInIT(nt_title, year);
					/*
					if (t != null) {
						System.out.println("nt: " + nt_title + " - it: " + t);
						if (!titleObject.has("it")) {
							titleObject.put("it", t);
						}
					}*/
					
					String t2 = searchItalianTitleInOriginal(nt_title, year);
					
					if (t2 != null) {
						System.out.println("it: " + nt_title + " - nt: " + t2);
						if (!titleObject.has("it")) {
							titleObject.put("it", nt_title);
							titleObject.remove("nt");
							titleObject.put("nt", t2);
						}
					}
					
					
					/*
					try {
						//http://www.omdbapi.com/?t=once+upon+a+time+in+america&y=2003&plot=short&r=json
						
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
								if (titleObject.getString("nt").equals(e_title)) {
									myJsonObject.put("omdb", json);
								}
							} else if (response.equals(("False"))) {
								System.out.println("Non esiste.");
							}
						}
					} catch (JSONException | IOException e) {
						e.printStackTrace();
					}*/
				}
				
			//}
			
		}
		
		writeFile(jo, "movies_list5.txt");
	}
	
	
	public static JSONObject readJSON(String path) {
		try (FileInputStream inputStream = new FileInputStream(path)) {
		    String jsonString = IOUtils.toString(inputStream);
		    
		    JSONObject jo = new JSONObject(jsonString);
		    
		    return jo;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void writeFile(JSONObject jo, String path) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
			String jsonString = jo.toString();
			bw.write(JsonWriter.formatJson(jsonString));
		} catch (IOException e) {
			// Print out all exceptions, including suppressed ones.
			System.err.println("thrown exception: " + e.toString());
			Throwable[] suppressed = e.getSuppressed();
			for (int i = 0; i < suppressed.length; i++) {
				System.err.println("suppressed exception: " + suppressed[i].toString());
			}
		}
	}
	
	private static String searchOriginalTitleInIT(String originalTitle, int year) {
		//JSONObject jo = readJSON("akaTitleJSON.txt");
		//JSONArray ja = akatitlesJSON.getJSONArray("aka-titles");
		if (akatitlesjarray == null)
			akatitlesjarray = akatitlesJSON.getJSONArray("aka-titles");
		
		for (Object myobj : akatitlesjarray) {
			JSONObject job = (JSONObject) myobj;
			
			String myOriginalTitle = job.getString("originalTitle");
			String italianTitle = job.getString("itTitle");
			int myYear = job.getInt("year");
			
			if (year == myYear && myOriginalTitle.toLowerCase().equals(originalTitle.toLowerCase())) {
				return italianTitle;
			}
		}
		return null;
	}
	
	private static String searchItalianTitleInOriginal(String italianTitle, int year) {
		if (akatitlesjarray == null)
			akatitlesjarray = akatitlesJSON.getJSONArray("aka-titles");
		
		for (Object myobj : akatitlesjarray) {
			JSONObject job = (JSONObject) myobj;
			
			String originalTitle = job.getString("originalTitle");
			String myItalianTitle = job.getString("itTitle");
			int myYear = job.getInt("year");
			
			if (year == myYear && myItalianTitle.toLowerCase().equals(italianTitle.toLowerCase())) {
				return originalTitle;
			}
		}
		return null;
	}

}
