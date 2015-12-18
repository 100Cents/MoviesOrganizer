package hcents.lifefolders.video.tts;

import java.io.*;
import java.net.*;

import org.json.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class ImdbDownloader {

	public static void main(String[] args) {
		JSONObject jo = new JSONObject();
		JSONArray ja = new JSONArray();
		
		String title = "";
		for (int i = 1; i < 20; i++) {
			if (i >= 0 && i < 10) title = "tt000000" + i;
			if (i >= 10 && i < 100) title = "tt00000" + i;
			
			try {
				JSONObject myObject = extractAKAInfo(title);
				
				myObject.put("imdbCode", title);
				//myObject.put("titles-imdb", extractAKAInfo(title));
				
				ja.put(myObject);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		
		
		jo.put("imdb-extraction", ja);
		Utility.writeFileNoPretty(jo, "imdb-extraction1.txt");
	}
	
	public static JSONObject extractAKAInfo(String imdbCode) throws IOException {
		String html = "http://www.imdb.com/title/" + imdbCode + "/releaseinfo";
		JSONObject titles = new JSONObject();
		
		System.setProperty("http.proxyHost", "195.213.138.202");
		System.setProperty("http.proxyPort", "8080");
		
		
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
			System.setProperty("http.proxyHost", "195.213.138.202");
			System.setProperty("http.proxyPort", "8080");
			
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
