package hcents.lifefolders.video.tts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.*;

public class MoviesUpdaterAskLanguage {
	
	public static void main(String[] args) {
		JSONObject mainJSONObject = Utility.readJSON("movies_list19.txt");
		
		JSONArray moviesListJSONArray = mainJSONObject.getJSONArray("movies_list");
		
		for (Object myMovieObject : moviesListJSONArray) {
			JSONObject myMovieJSONObject = (JSONObject) myMovieObject;
			
			if (myMovieJSONObject.has("myData")) {
				
				JSONObject myDataJSONObject = myMovieJSONObject.getJSONObject("myData");
				int myYear = myDataJSONObject.getInt("year");
				
				if (myDataJSONObject.has("title")) {
					JSONObject myTitleJSONObject = myDataJSONObject.getJSONObject("title");
					
					if (myTitleJSONObject.has("nt")) {
						String t = myTitleJSONObject.getString("nt");
						
						if (t.equals("")) {
							myTitleJSONObject.remove("nt");
							continue;
						}
						
						System.out.println(t + ": what language (enter for en, o to escape, q to quit)? ");
						
						BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
						try {
							String s = br.readLine();
							
							if (s.equals("q")) {
								break;
							} else if (s.equals("o")) {
								continue;
							} else if (s.equals("")) {
								s = "en";
							} else if (s.equals("i")) {
								s = "it";
							}
							
							if (!myTitleJSONObject.has(s)) {
								myTitleJSONObject.remove("nt");
								myTitleJSONObject.put(s, t);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					

				}
			}
		}
		
		Utility.writeFile(mainJSONObject, "movies_list20.txt");
	}

	public static String getWellFormedTitle(JSONObject myDataJSONObject) {
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
			
			return title;
		}
		return null;
	}

}
