package hcents.lifefolders.video.tts;

import java.io.*;
import java.util.*;

import org.json.JSONObject;

public class Movie_v1 implements Serializable {

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
	
	public final static int INFO_STATUS_BASIC = 0;
	public final static int INFO_STATUS_GOOGLED = 1;
	
	public final static int REAL_DATA_STATUS_UNKNOWN = 0;
	public final static int REAL_DATA_STATUS_DOWNLOADED = 1;
	public final static int REAL_DATA_STATUS_REQUESTED = 2;

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

}
