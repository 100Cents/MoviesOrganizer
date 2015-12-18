package hcents.lifefolders.video.tts;

import hcents.moviesorganizer.Utility;

import org.json.*;

public class MoviesUpdaterDirectoryList {
	
	public static void main(String[] args) {
		JSONObject mainJSONObject = Utility.readJSON("movies_list13.txt");
		JSONObject directoryListJSONObject = Utility.readJSON("directorylist.txt");
		
		JSONArray moviesListJSONArray = mainJSONObject.getJSONArray("movies_list");
		JSONArray directoryListJSONArray = directoryListJSONObject.getJSONArray("directory_list");
		
		for (Object myMovieObject : moviesListJSONArray) {
			JSONObject myMovieJSONObject = (JSONObject) myMovieObject;
			
			if (myMovieJSONObject.has("myData")) {
				
				JSONObject myDataJSONObject = myMovieJSONObject.getJSONObject("myData");
				int myYear = myDataJSONObject.getInt("year");
				
				if (myDataJSONObject.has("title")) {
					JSONObject myTitleJSONObject = myDataJSONObject.getJSONObject("title");
					
					String[] languageCodes = JSONObject.getNames(myTitleJSONObject);
					
					if (languageCodes == null) {
						
						System.out.println("Siamo matti?");
						System.out.println(myDataJSONObject);
						//myDataJSONObject.put("deletexxx", "deletexxx");
					}
					
					boolean found = false;
					for (String languageCode : languageCodes) {
						if (found) {
							found = false;
							break;
						}
						String title = myTitleJSONObject.getString(languageCode);
						
						for (Object myDirectoryObject : directoryListJSONArray) {
							JSONObject myDirectoryJSONObject = (JSONObject) myDirectoryObject;
							
							String year = myDirectoryJSONObject.getString("year");
							String originalTitle = myDirectoryJSONObject.getString("originalTitle");
							String itTitle = myDirectoryJSONObject.getString("itTitle");
							
							if (title.toLowerCase().equals(itTitle.toLowerCase())) {
								if (myYear == Integer.parseInt(year)) {
									
									if (!originalTitle.equals(itTitle)) {
										System.out.println("Trovato: " + title);
										
										
										myDataJSONObject.remove("dataStatus");
										myDataJSONObject.put("dataStatus", 1);
										
										
										if (!myTitleJSONObject.has("it")) {
											myTitleJSONObject.put("it", itTitle);
											if (myTitleJSONObject.has("nt")) {
												myTitleJSONObject.remove("nt");
												myTitleJSONObject.put("nt", originalTitle);
											}
										}
										
									}
									
									
									found = true;
									break;
								}
							}
						}
						
						
						
					}
				}
				
				
				
				
				
			}
			
			
			
		}
		
		Utility.writeFile(mainJSONObject, "movies_list14.txt");
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
