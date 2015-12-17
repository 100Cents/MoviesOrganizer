import java.io.File;

import org.json.*;

public class MoviesUtility {
	
	private static void changeDirectoryNamesWithImdbFile(File path) {
		for (final File fileEntry : path.listFiles()) {
	    	if (fileEntry.isDirectory()) {
	    		
	    	}
		}
	}
	
	/*
	private static MovieTitle getMovieTitle(String imdbCode) {
		if (imdbCode != null) {
			for (Object myMovieObject : moviesListJSONArray) {
				JSONObject myMovieJSONObject = (JSONObject) myMovieObject;
				
				if (myMovieJSONObject.has("myData")) {
					JSONObject myDataJSONObject = myMovieJSONObject.getJSONObject("myData");
					String dataImdbCode = myDataJSONObject.getString("imdbCode");
					
					if (dataImdbCode.equals(imdbCode)) {
						System.out.println("Trovato.");
						
						return getMovieTitle(myDataJSONObject);
					}
				}
			}
			return null;
		} else return null;
	}*/
	
	public static MovieTitle getMovieTitle(JSONObject myDataJSONObject) {
		int myYear = myDataJSONObject.getInt("year");
		String translatedTitle = null;
		String originalTitle = null;
		String myOtlc = myDataJSONObject.getString("otlc");
		
		if (myDataJSONObject.has("title")) {
			JSONObject myTitleJSONObject = myDataJSONObject.getJSONObject("title");
			
			if (myOtlc.equals("")) {
				if (myTitleJSONObject.has("it")) {
					originalTitle = myTitleJSONObject.getString("it");
				} else if (myTitleJSONObject.has("en")) {
					originalTitle = myTitleJSONObject.getString("en");
				} else if (myTitleJSONObject.has("nt")) {
					originalTitle = myTitleJSONObject.getString("nt");
				} else {
					String[] s = JSONObject.getNames(myTitleJSONObject);
					originalTitle = myTitleJSONObject.getString(s[0]);
				}
			} else {
				// Considero i titoli originali solo se sono in en, it, ru, es, pt, fr, ot
				if (myOtlc.equals("en") || myOtlc.equals("it") || myOtlc.equals("ru") || myOtlc.equals("es") || myOtlc.equals("pt") || myOtlc.equals("fr") ||  myOtlc.equals("ot")) {
					originalTitle = myTitleJSONObject.getString(myOtlc);
					if (myTitleJSONObject.has("it")) {
						translatedTitle = myTitleJSONObject.getString("it");
					}
				} else {
					if (myTitleJSONObject.has("it")) {
						originalTitle = myTitleJSONObject.getString("it");
						translatedTitle = myTitleJSONObject.getString("it");
					} else if (myTitleJSONObject.has("en")) {
						originalTitle = myTitleJSONObject.getString("en");
					} else if (myTitleJSONObject.has("ww-en")) {
						originalTitle = myTitleJSONObject.getString("ww-en");
					} else {
						String[] s = JSONObject.getNames(myTitleJSONObject);
						originalTitle = myTitleJSONObject.getString(s[0]);
					}
				}
				
				
			}
			MovieTitle t = new MovieTitle(originalTitle, translatedTitle, myYear);
			//System.out.println(t);
			return t;
		} else return null;
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
	
	/*
	public static void createPrettyDirectory(JSONObject myMovieJSONObject) {
		if (myMovieJSONObject.has("myData")) {
			JSONObject myDataJSONObject = myMovieJSONObject.getJSONObject("myData");
			
			String imdbCode = myDataJSONObject.getString("imdbCode");
			int dataStatus = myDataJSONObject.getInt("dataStatus");
			
			if (dataStatus == 1) {
			
				MovieTitle movieTitle = getMovieTitle(myDataJSONObject); // deve diventare new MovieTitle(myDataJSONObject);
				
				String prettyTitle = movieTitle.printPrettyTitle();
				
				File imdbFile = new File(MAIN_PATH + System.getProperty("file.separator") + prettyTitle + System.getProperty("file.separator") + imdbCode);
				
				try {
					Files.createParentDirs(imdbFile);
					Files.touch(imdbFile);
					BufferedWriter output = new BufferedWriter(new FileWriter(imdbFile));
					output.write(imdbCode);
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
		
	}
	
	
	public static void renamePrettyDirectoryIfBadlyCased(JSONObject myMovieJSONObject) {
		if (myMovieJSONObject.has("myData")) {
			JSONObject myDataJSONObject = myMovieJSONObject.getJSONObject("myData");
			
			int dataStatus = myDataJSONObject.getInt("dataStatus");
			
			if (dataStatus == 1) {
		
				MovieTitle movieTitle = getMovieTitle(myDataJSONObject);
				
				String prettyTitle = movieTitle.printPrettyTitle();
				
				File f = new File(MAIN_PATH + System.getProperty("file.separator") + prettyTitle);
				if (f.exists() && f.isDirectory()) {
					if (changeFolders) {
						
						File tempDir = new File(MAIN_PATH + System.getProperty("file.separator") + prettyTitle + "-" + System.currentTimeMillis());
						
						f.renameTo(tempDir);
						
						tempDir.renameTo(f);
					}
				}
				
			}
			
		}
	}
	
	
	public static File createTempDir(File baseDir, String prettyTitle) {
		String baseName = prettyTitle + "-" + System.currentTimeMillis() + "-";

	    for (int counter = 0; counter < 10000; counter++) {
	      File tempDir = new File(baseDir, baseName + counter);
	      if (tempDir.mkdir()) {
	        return tempDir;
	      }
	    }
	    throw new IllegalStateException("Failed to create directory within "
	        + 10000 + " attempts (tried "
	        + baseName + "0 to " + baseName + (10000 - 1) + ')');
	  }
	  */

}
