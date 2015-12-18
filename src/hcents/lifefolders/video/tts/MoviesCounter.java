package hcents.lifefolders.video.tts;

import hcents.lifefolders.video.tts.controller.Controller;

import java.io.*;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.json.*;

import com.cedarsoftware.util.io.JsonWriter;


public class MoviesCounter {

	public static void main(String[] args) {
		JSONObject mainJSONObject = readJSON("movies_list10.txt");
		
		JSONArray moviesListJSONArray = mainJSONObject.getJSONArray("movies_list");
		
		int withOmdb = 0;
		int withoutOmdb = 0;
		
		for (Object myMovieObject : moviesListJSONArray) {
			
			JSONObject myMovieJSONObject = (JSONObject) myMovieObject;
			
			if (myMovieJSONObject.has("myData")) {
				
				JSONObject myDataJSONObject = myMovieJSONObject.getJSONObject("myData");
				//JSONObject myOmdbJSONObject = myMovieJSONObject.getJSONObject("omdb");
				
				if (myDataJSONObject.has("title") && myDataJSONObject.has("otlc")) {
					String otlc = myDataJSONObject.getString("otlc");
					JSONObject myTitleJSONObject = myDataJSONObject.getJSONObject("title");
					String title = "";
					
					if (!otlc.equals("")) {
						if (otlc.equals("xx") || otlc.equals("cn") || otlc.equals("de")) {
							if (myTitleJSONObject.has("en")) {
								title = myTitleJSONObject.getString("en");
								//System.out.println(otlc + ": " + myTitleJSONObject.getString("en"));
							} else if (myTitleJSONObject.has("it")) {
								title = myTitleJSONObject.getString("it");
								//System.out.println(otlc + ": " + myTitleJSONObject.getString("it"));
							} else {
								System.out.println("Eccezione: manca una versione del titolo in inglese o italiano.");
							}
						} else {
							if (myTitleJSONObject.has(otlc)) {
								title = myTitleJSONObject.getString(otlc);
								//System.out.println(otlc + ": " + myTitleJSONObject.getString(otlc));
								if (myTitleJSONObject.has("it")) {
									if (!myTitleJSONObject.getString("it").equals(title)) {
										title += " [" + myTitleJSONObject.getString("it") + "]";
									}
								}
							}
						}
					} else {
						if (myTitleJSONObject.has("nt")) {
							title = myTitleJSONObject.getString("nt");
						} else if (myTitleJSONObject.has("en")) {
							title = myTitleJSONObject.getString("en");
						} else if (myTitleJSONObject.has("it")) {
							title = myTitleJSONObject.getString("it");
						}
					}
					
					System.out.println(title);
				}
				
				
				
				withOmdb++;
			}
			
			
			
		}
		
		System.out.println("With Omdb: " + withOmdb);
		System.out.println("Without Omdb: " + withoutOmdb);
		
		//writeFile(jo, "movies_list10.txt");
	}
	
	
	public static JSONObject readJSON(String path) {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"))) {
			
		    String jsonString = IOUtils.toString(in);
		    
		    JSONObject jo = new JSONObject(jsonString);
		    
		    return jo;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void writeFile(JSONObject jo, String path) {
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"))) {

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

}
