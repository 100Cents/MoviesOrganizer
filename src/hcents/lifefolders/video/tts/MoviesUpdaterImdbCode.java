package hcents.lifefolders.video.tts;

import hcents.lifefolders.video.tts.controller.Controller;

import java.io.*;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.json.*;

import com.cedarsoftware.util.io.JsonWriter;


public class MoviesUpdaterImdbCode {
	static JSONObject akatitlesJSON = readJSON("akaTitleJSON.txt");
	private static JSONArray akatitlesjarray;

	public static void main(String[] args) {
		JSONObject jo = readJSON("movies_list6.txt");
		
		JSONArray jarray = jo.getJSONArray("movies_list");
		
		for (Object myobj : jarray) {
			
			JSONObject myJsonObject = (JSONObject)myobj;
			JSONObject myData = myJsonObject.getJSONObject("myData");
			
			if (!myData.has("imdbCode")) {
				if (myJsonObject.has("omdb")) {
					JSONObject omdb = myJsonObject.getJSONObject("omdb");
					if (omdb.has("imdbID")) {
						String imdbid = omdb.getString("imdbID");
						myData.put("imdbCode", imdbid);
						
					}
				} else {
					myData.put("imdbCode", "");
				}
				
				
				
				
			}
			
		}
		
		writeFile(jo, "movies_list7.txt");
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
