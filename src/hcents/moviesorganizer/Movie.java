package hcents.moviesorganizer;


import java.io.*;
import java.util.*;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONObject;

public class Movie implements Serializable {

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (allFilesCorrectlyNamed ? 1231 : 1237);
		result = prime * result + Arrays.hashCode(allTags);
		result = prime * result
				+ ((folderName == null) ? 0 : folderName.hashCode());
		result = prime * result
				+ ((imdbCode == null) ? 0 : imdbCode.hashCode());
		result = prime * result + infoStatus;
		result = prime * result
				+ ((movieDirectory == null) ? 0 : movieDirectory.hashCode());
		result = prime * result
				+ ((movieTitle == null) ? 0 : movieTitle.hashCode());
		result = prime * result + ((otlc == null) ? 0 : otlc.hashCode());
		result = prime * result + Arrays.hashCode(realAvailableLanguages);
		result = prime * result + realDataStatus;
		result = prime * result
				+ ((titleHashMap == null) ? 0 : titleHashMap.hashCode());
		result = prime * result + year;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Movie other = (Movie) obj;
		if (allFilesCorrectlyNamed != other.allFilesCorrectlyNamed)
			return false;
		if (!Arrays.equals(allTags, other.allTags))
			return false;
		if (folderName == null) {
			if (other.folderName != null)
				return false;
		} else if (!folderName.equals(other.folderName))
			return false;
		if (imdbCode == null) {
			if (other.imdbCode != null)
				return false;
		} else if (!imdbCode.equals(other.imdbCode))
			return false;
		if (infoStatus != other.infoStatus)
			return false;
		if (movieDirectory == null) {
			if (other.movieDirectory != null)
				return false;
		} else if (!movieDirectory.equals(other.movieDirectory))
			return false;
		if (movieTitle == null) {
			if (other.movieTitle != null)
				return false;
		} else if (!movieTitle.equals(other.movieTitle))
			return false;
		if (otlc == null) {
			if (other.otlc != null)
				return false;
		} else if (!otlc.equals(other.otlc))
			return false;
		if (!Arrays
				.equals(realAvailableLanguages, other.realAvailableLanguages))
			return false;
		if (realDataStatus != other.realDataStatus)
			return false;
		if (titleHashMap == null) {
			if (other.titleHashMap != null)
				return false;
		} else if (!titleHashMap.equals(other.titleHashMap))
			return false;
		if (year != other.year)
			return false;
		return true;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2843364281213643138L;
	
	private int infoStatus;
	private int realDataStatus;
	private String otlc;
	private HashMap<String, String> titleHashMap = new HashMap<String, String>();
	private int year;
	private String imdbCode;
	private MovieTitle movieTitle;
	
	private File movieDirectory;

	private String folderName;

	private String[] realAvailableLanguages;

	private boolean allFilesCorrectlyNamed;

	private String[] allTags;
	
	public String[] getAllTags() {
		return allTags;
	}

	public void setAllTags(String[] allTags) {
		this.allTags = allTags;
	}

	public boolean isAllFileNamesStartsWithDirectoryName() {
		return allFilesCorrectlyNamed;
	}

	public String[] getRealAvailableLanguages() {
		return realAvailableLanguages;
	}

	public final static int INFO_STATUS_BASIC = 0;
	public final static int INFO_STATUS_GOOGLED = 1;
	
	public final static int REAL_DATA_STATUS_UNKNOWN = 0;
	public final static int REAL_DATA_STATUS_DOWNLOADED = 1;
	public final static int REAL_DATA_STATUS_REQUESTED = 2;
	
	public Movie() {
		
	}
	
	public Movie(JSONObject jsonObject) {
		
		if (jsonObject != null && jsonObject.has("myData")) {
			JSONObject myData = jsonObject.getJSONObject("myData");
			
			setMovieTitle(parseMovieTitle(myData));
			
			if (myData.has("imdbCode")) {
				imdbCode = myData.getString("imdbCode");
			}
			if (myData.has("year")) {
				year = myData.getInt("year");
			}
			if (myData.has("otlc")) {
				this.otlc = myData.getString("otlc");
			}
		}
	}

	/**
	 * @return the infoStatus
	 */
	public int getInfoStatus() {
		return infoStatus;
	}

	/**
	 * @param infoStatus the infoStatus to set
	 */
	public void setInfoStatus(int infoStatus) {
		this.infoStatus = infoStatus;
	}

	/**
	 * @return the realDataStatus
	 */
	public int getRealDataStatus() {
		return realDataStatus;
	}

	/**
	 * @param realDataStatus the realDataStatus to set
	 */
	public void setRealDataStatus(int realDataStatus) {
		this.realDataStatus = realDataStatus;
	}

	/**
	 * @return the otlc (Original Title Language Code)
	 */
	public String getOtlc() {
		return otlc;
	}

	/**
	 * @param otlc the otlc to set (Original Title Language Code)
	 */
	public void setOtlc(String otlc) {
		this.otlc = otlc;
	}
	
	public String getTitle(String lc) {
		return titleHashMap.get(lc);
	}
	
	public void addTitle(String title, String lc) {
		titleHashMap.put(lc, title);
	}

	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * @return the imdbCode
	 */
	public String getImdbCode() {
		return imdbCode;
	}

	/**
	 * @param imdbCode the imdbCode to set
	 */
	public void setImdbCode(String imdbCode) {
		this.imdbCode = imdbCode;
	}
	
	public JSONObject getJSON() {
		JSONObject jo = new JSONObject();
		
		JSONObject myData = new JSONObject();
		myData.put("infoStatus", getInfoStatus());
		myData.put("infoStatus", getInfoStatus());
		myData.put("dataStatus", getRealDataStatus());
		myData.put("imdbCode", getImdbCode());
		myData.put("year", getYear());
		
		JSONObject title = new JSONObject();
		
		for (Map.Entry<String, String> entry : titleHashMap.entrySet()) {
		    String key = entry.getKey();
		    String value = entry.getValue();
		    
		    title.put(key, value);
		}
		
		myData.put("title", title);
		
		if (getOtlc() != null) {
			myData.put("otlc", getOtlc());
		} else {
			myData.put("otlc", "");
		}
		
		jo.put("myData", myData);
		
		return jo;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Movie_v1 [infoStatus=" + infoStatus + ", realDataStatus=" + realDataStatus + ", otlc=" + otlc
				+ ", titleHashMap=" + titleHashMap + ", year=" + year + ", imdbCode=" + imdbCode + "]";
	}
	
	private static MovieTitle parseMovieTitle(JSONObject myDataJSONObject) {
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
				// Considero i titoli originali solo se sono in en, it, ru, es, pt, fr e ot
				if (myOtlc.equals("en") || myOtlc.equals("it") || myOtlc.equals("ru") || myOtlc.equals("es") || myOtlc.equals("pt") || myOtlc.equals("fr") || myOtlc.equals("ot")) {
					originalTitle = myTitleJSONObject.getString(myOtlc);
					if (myTitleJSONObject.has("it") && !myOtlc.equals("it")) {
						translatedTitle = myTitleJSONObject.getString("it");
					}
				} else {
					if (myTitleJSONObject.has("it")) {
						//originalTitle = myTitleJSONObject.getString("it");
						translatedTitle = myTitleJSONObject.getString("it");
					} else if (myTitleJSONObject.has("en")) {
						//originalTitle = myTitleJSONObject.getString("en");
						translatedTitle = myTitleJSONObject.getString("en");
					} else {
						String[] s = JSONObject.getNames(myTitleJSONObject);
						translatedTitle = myTitleJSONObject.getString(s[0]);
					}
				}
				
				
			}
			MovieTitle t = new MovieTitle(originalTitle, translatedTitle, myYear);
			return t;
		} else return null;
	}

	/**
	 * @return the movieTitle
	 */
	public MovieTitle getMovieTitle() {
		return movieTitle;
	}

	/**
	 * @param movieTitle the movieTitle to set
	 */
	public void setMovieTitle(MovieTitle movieTitle) {
		this.movieTitle = movieTitle;
	}
	
	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
		
	}

	public void setRealAvailableLanguages(String[] showLanguagesByFileName) {
		this.realAvailableLanguages = showLanguagesByFileName;
	}

	public void setAllFilesCorrectlyNamed(boolean allFilesCorrectlyNamed) {
		this.allFilesCorrectlyNamed = allFilesCorrectlyNamed;
	}

	public File getMovieDirectory() {
		return movieDirectory;
	}

	public void setMovieDirectory(File movieDirectory) {
		this.movieDirectory = movieDirectory;
	}

}


