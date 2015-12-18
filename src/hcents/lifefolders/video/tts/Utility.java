package hcents.lifefolders.video.tts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.cedarsoftware.util.io.JsonWriter;

public class Utility {
	
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
			
			//System.out.println(jsonString);
			
			String formattedString = JsonWriter.formatJson(jsonString);
			bw.write(formattedString);
		} catch (IOException e) {
			// Print out all exceptions, including suppressed ones.
			System.err.println("thrown exception: " + e.toString());
			Throwable[] suppressed = e.getSuppressed();
			for (int i = 0; i < suppressed.length; i++) {
				System.err.println("suppressed exception: " + suppressed[i].toString());
			}
		}
	}
	
	public static void writeFileNoPretty(JSONObject jo, String path) {
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"))) {

			String jsonString = jo.toString();
			bw.write(jsonString);
		} catch (IOException e) {
			// Print out all exceptions, including suppressed ones.
			System.err.println("thrown exception: " + e.toString());
			Throwable[] suppressed = e.getSuppressed();
			for (int i = 0; i < suppressed.length; i++) {
				System.err.println("suppressed exception: " + suppressed[i].toString());
			}
		}
	}
	
	public static JSONObject extractAKAInfo(String imdbCode) throws IOException {
		String html = "http://www.imdb.com/title/" + imdbCode + "/releaseinfo";
		JSONObject titles = new JSONObject();
		
		JSONArray titlesArray = new JSONArray();
		
		Document doc = Jsoup.connect(html).get();
		
		Elements titleElements = doc.select("h3[itemprop=name] a[itemprop=url]");
		String originalTitle = titleElements.get(0).text();
		
		Elements yearElements = doc.select("h3[itemprop=name] span");
		String year = yearElements.get(0).text();
		year = year.substring(1, year.length() - 1);
		int y = Integer.parseInt(year);
		titles.put("year", y);
		
		System.out.println("0: " + year);
		
		System.out.println("1: " + originalTitle);
		
		Elements tableElements = doc.select("table#akas");
		
		Elements tableRowElements = tableElements.select("tbody tr");
		
		for (int i = 0; i < tableRowElements.size(); i++) {
			Element row = tableRowElements.get(i);
			Elements rowItems = row.select("td");
			
			String nazione = rowItems.get(0).text();
			String titolo = rowItems.get(1).text();
			
			if (nazione.equals("(original title)")) {
				originalTitle = titolo;
			} else {
				titlesArray.put(new JSONObject().put(nazione, titolo));
			}
		}
		
		System.out.println("2: " + originalTitle);
		
		titlesArray.put(new JSONObject().put("(original title)", originalTitle));
		
		titles.put("titles-imdb", titlesArray);
		
		System.out.println(titles);
			
		return titles;
	}
	
	public static void readWebPage(String imdbCode) {
		URL url;
		try {
			url = new URL("http://www.imdb.com/title/" + imdbCode + "/releaseinfo");
			
			try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
				String inputLine;
				while ((inputLine = in.readLine()) != null)
				    System.out.println(inputLine);
				
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
