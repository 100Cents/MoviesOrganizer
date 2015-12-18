package hcents.lifefolders.video.tts;
import java.io.*;
import java.util.regex.*;

import org.json.*;

import com.cedarsoftware.util.io.JsonWriter;


public class AKACreator {
	
	private static final Pattern pattern1 = Pattern.compile("^(.*) \\((\\d*)\\)");
	private static final Pattern pattern2 = Pattern.compile("^   \\(aka (.*) \\((\\d*)\\) (.*)");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		BufferedReader br = null;

		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader("aka-titles.list"));
			
			int i = 0;
			
			
			JSONObject jo = new JSONObject();
			
			JSONArray jsonArray = new JSONArray();
			
			
			// Cerco un titolo.
			
			String search = "america";
			
			
			while ((sCurrentLine = br.readLine()) != null) {
				Matcher matcher1 = pattern1.matcher(sCurrentLine);
				if (!sCurrentLine.equals("")) {
					if (matcher1.find()) {
						if (!sCurrentLine.startsWith("   ")) {
							// Sto pensando che sono il titolo originale.
							String originalTitle = matcher1.group(1);
							
							if (originalTitle.startsWith("\"")) {
								originalTitle = originalTitle.substring(1);
							}
							if (originalTitle.endsWith("\"")) {
								originalTitle = originalTitle.substring(0, originalTitle.length() - 1);
							}
							
							String date = matcher1.group(2);
							String italianTitle = null;
							
							
							boolean found = false;
							
							// Controllo le righe successive fino a quella vuota.
							while ((sCurrentLine = br.readLine()) != null && !sCurrentLine.equals("")) {
								if (sCurrentLine.startsWith("   ") && sCurrentLine.contains("(Italy)")) {
									Matcher matcher2 = pattern2.matcher(sCurrentLine);
									if (matcher2.find()) {
										found = true;
										italianTitle = matcher2.group(1);
										
										if (italianTitle.startsWith("\"")) {
											italianTitle = italianTitle.substring(1);
										}
										if (italianTitle.endsWith("\"")) {
											italianTitle = italianTitle.substring(0, italianTitle.length() - 1);
										}
										
										break;
									}
								} else {
									
								}
							}
							
							if (found) {
								System.out.println(date + ":" + originalTitle + ":" + italianTitle);
								JSONObject o = new JSONObject();
								o.put("year", date);
								o.put("originalTitle", originalTitle);
								o.put("itTitle", italianTitle);
								jsonArray.put(o);
							}
						}
						
						
						
						
						//System.out.println(sCurrentLine);
						//System.out.println(matcher1.group(1));
					} else {
						//System.out.println(sCurrentLine);
					}
				}
				
				
				/*
				if (sCurrentLine.toLowerCase().contains(search.toLowerCase())) {
					System.out.println(sCurrentLine);
					
				}
				*/
				
				/*
				//System.out.println(sCurrentLine);
				if (!sCurrentLine.startsWith("   ") && !sCurrentLine.equals("")) {
					// Inizia un record.
					System.out.println(sCurrentLine);
					i++;
				} else {
					
				}*/
			}
			
			jo.put("aka-titles", jsonArray);
			
			System.out.println(i);
			
			
			String jsonString = jo.toString();
			System.out.println(JsonWriter.formatJson(jsonString));
			
			MoviesUpdater.writeFile(jo, "akaTitleJSON.txt");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}
