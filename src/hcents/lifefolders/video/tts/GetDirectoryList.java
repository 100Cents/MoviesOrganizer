package hcents.lifefolders.video.tts;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.cedarsoftware.util.io.JsonWriter;

public class GetDirectoryList {
	private static final Pattern pattern1 = Pattern.compile("^(.*) \\[(.*)\\] \\((\\d*)\\)$");
	private static final Pattern pattern2 = Pattern.compile("^([^\\[\\]]*) \\((\\d*)\\)$");
	private static final Pattern pattern3 = Pattern.compile("^([^\\[\\]]*) \\{T\\} \\((\\d*)\\)$");

	public static void main(String[] args) {
		JSONObject jo = new JSONObject();
		
		JSONArray jarray = new JSONArray();
		
		listFilesForFolder(new File("Z:\\+Video\\+Movies\\IT\\+_Movies_ 0-9"), jarray);
		listFilesForFolder(new File("Z:\\+Video\\+Movies\\IT\\+_Movies_ A-E"), jarray);
		listFilesForFolder(new File("Z:\\+Video\\+Movies\\IT\\+_Movies_ F-K"), jarray);
		listFilesForFolder(new File("Z:\\+Video\\+Movies\\IT\\+_Movies_ L-O"), jarray);
		listFilesForFolder(new File("Z:\\+Video\\+Movies\\IT\\+_Movies_ P-S"), jarray);
		listFilesForFolder(new File("Z:\\+Video\\+Movies\\IT\\+_Movies_ T-Z"), jarray);
		listFilesForFolder(new File("Z:\\+Video\\+Movies\\IT\\+_Movies_ А-Я"), jarray);
		listFilesForFolder(new File("Z:\\+Video\\+Movies\\IT\\+1895-1899"), jarray);
		listFilesForFolder(new File("Z:\\+Video\\+Movies\\IT\\+Agente 007"), jarray);
		listFilesForFolder(new File("Z:\\+Video\\+Movies\\IT\\+Alberto Sordi"), jarray);
		listFilesForFolder(new File("Z:\\+Video\\+Movies\\IT\\+American Pie Saga"), jarray);
		listFilesForFolder(new File("Z:\\+Video\\+Movies\\IT\\+Batman"), jarray);
		listFilesForFolder(new File("Z:\\+Video\\+Movies\\IT\\+Bud Spencer & Terence Hill"), jarray);
		listFilesForFolder(new File("Z:\\+Video\\+Movies\\IT\\+Fantozzi"), jarray);
		listFilesForFolder(new File("Z:\\+Video\\+Movies\\IT\\+Harry Potter"), jarray);
		listFilesForFolder(new File("Z:\\+Video\\+Movies\\IT\\+Stan Laurel & Oliver Hardy"), jarray);
		listFilesForFolder(new File("Z:\\+Video\\+Movies\\IT\\+Star Wars"), jarray);
		listFilesForFolder(new File("Z:\\+Video\\+Movies\\IT\\+Totò"), jarray);
		listFilesForFolder(new File("Z:\\+Video\\+Movies\\IT\\+Walt Disney"), jarray);
		
		
		jo.put("directory_list", jarray);
		
		writeFile(jo, "directorylist.txt");
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
	
	public static void listFilesForFolder(final File folder, JSONArray jarray) {
	    for (final File fileEntry : folder.listFiles()) {
	    	if (fileEntry.isDirectory()) {
	    		String dirname = fileEntry.getName();
	    		String originalTitle = "";
	    		String itTitle = "";
	    		String year = "";
	    		
	    		Matcher matcher1 = pattern1.matcher(dirname);
    			Matcher matcher2 = pattern2.matcher(dirname);
    			Matcher matcher3 = pattern3.matcher(dirname);
    			if (matcher3.find()) {
    				itTitle = matcher3.group(1);
    				year = matcher3.group(2);
    				System.out.printf("itTitle:%s, year:%s%n", itTitle, year);
     			} else {
     				
	    			if (matcher1.find()) {
	    				originalTitle = matcher1.group(1);
	    				itTitle = matcher1.group(2);
	    				year = matcher1.group(3);
	    				System.out.printf("originalTitle:%s, itTitle:%s, year:%s%n", originalTitle, itTitle, year);
	    			} 
	    			if (matcher2.find()) {
	    				originalTitle = matcher2.group(1);
	    				itTitle = matcher2.group(1);
	    				year = matcher2.group(2);
	    				System.out.printf("originalTitle:%s, year:%s%n", originalTitle, year);
	     			}
	    			
     			}
    			
    			
    			JSONObject jarrayo = new JSONObject();
    			jarrayo.put("originalTitle", originalTitle);
    			jarrayo.put("itTitle", itTitle);
    			jarrayo.put("year", year);
    			
    			jarray.put(jarrayo);
	        }
	    }
	}

}
