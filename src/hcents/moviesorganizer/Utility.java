package hcents.moviesorganizer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.filebot.mediainfo.MediaInfo;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.pdfclown.files.SerializationModeEnum;

import com.cedarsoftware.util.io.JsonWriter;

public final class Utility {
	//public static final String BASE_DIRECTORY = "/home/rabbit/VMShared/__PRIVATE__/movies_dir/";
	//public static final String BASE_DIRECTORY = "Z:\\+Video\\+Movies\\_D_\\";
	//public static final String BASE_DIRECTORY = "/media/rabbit/USBHD-01/";
	//public static final String BASE_DIRECTORY = "C:\\Users\\Rabbit\\Desktop\\MoviesPDF\\movies_dir\\";
	public static final String BASE_DIRECTORY = "movies_dir";
	//public static final String BASE_DIRECTORY = "Z:\\+Video\\+Movies\\RU\\__TO_SORT__\\";
	
		
	public static final SimpleDateFormat MY_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	public static final SimpleDateFormat MY_TIME_FORMAT = new SimpleDateFormat("HH:mm");
	
	public static final Date NOW_DATE = new Date();
	
	public static final String IMDB_DESCRIPTOR_FILE_PATTERN_STRING = "^tt\\d{7}$";
	public static final String IMDB_DESCRIPTOR_FILE_PATTERN_STRING_2 = "tt\\d{7}";
	public static final String AKAINFO_FILE_PATTERN_STRING = "^tt\\d{7}-akainfo$";
	
	
	private static final Pattern pattern1 = Pattern.compile("^(.*) \\[(.*)\\] \\((\\d*)\\)$");
	private static final Pattern pattern2 = Pattern.compile("^([^\\[\\]]*) \\((\\d*)\\)$");
	private static final Pattern pattern3 = Pattern.compile("^([^\\[\\]]*) \\{T\\} \\((\\d*)\\)$");
	
	private Utility() {}
	
	/**
	 * 
	 * @param inputFile
	 * @return
	 * @throws IOException
	 */
	public static JSONObject readJSONObjectFromFile(File inputFile) throws IOException {
		if (inputFile == null) {
			throw new NullPointerException("parameter inputFile must not be null");
		}
		
		try
		(
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(inputFile), "UTF-8"))
		)
		{
		    String jsonString = IOUtils.toString(in);
		    return new JSONObject(jsonString);
		}
	}
	
	public static void writeTextToFile(String text, File path) {
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"))) {
			bw.write(text);
		} catch (IOException e) {
			// Print out all exceptions, including suppressed ones.
			System.err.println("thrown exception: " + e.toString());
			Throwable[] suppressed = e.getSuppressed();
			for (int i = 0; i < suppressed.length; i++) {
				System.err.println("suppressed exception: " + suppressed[i].toString());
			}
		}
	}
	
	public static void writeUrlContentToFile(String url, File file) throws IOException {
		Utility.writeTextToFile(getUrlContent(url), file);
	}
	
	public static String getUrlContent(String url) throws IOException {
		Response response = Jsoup.connect(url)
		           .ignoreContentType(true)
		           .userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:38.0) Gecko/20100101 Firefox/38.0 Iceweasel/38.4.0")
		           .header("Accept-Language", "it")
		           .referrer("http://www.google.com")
		           .timeout(12000)
		           .followRedirects(true)
		           .execute();
		
		Document doc = response.parse();
		return doc.toString();
	}
	
	public static void changePriority(File movieDirectory, int priority) throws IOException {
		Utility.checkMovieDirectory(movieDirectory);
		
		if (priority <= 0) throw new InvalidParameterException("priority must be > 0");
		
		File descriptorFile = Utility.getDescriptorFile(movieDirectory);
		
		if (descriptorFile != null) {
			
			JSONObject jsonDescriptorObject = Utility.readJSONObjectFromFile(descriptorFile);
			
			if (jsonDescriptorObject != null && jsonDescriptorObject.has("myData")) {
				JSONObject myData = jsonDescriptorObject.getJSONObject("myData");
				
				if (myData.has("priority")) {
					myData.remove("priority");
				}
				
				myData.put("priority", priority);
				
				Utility.writeJSONObjectToFile(jsonDescriptorObject, descriptorFile, true);
			}
			
		}
	}
	
	public static int getPriority(File movieDirectory) throws IOException {
		Utility.checkMovieDirectory(movieDirectory);
		
		File descriptorFile = Utility.getDescriptorFile(movieDirectory);
		
		if (descriptorFile != null) {
			
			JSONObject jsonDescriptorObject = Utility.readJSONObjectFromFile(descriptorFile);
			
			if (jsonDescriptorObject != null && jsonDescriptorObject.has("myData")) {
				JSONObject myData = jsonDescriptorObject.getJSONObject("myData");
				
				if (myData.has("priority")) {
					return myData.getInt("priority");
				}
			}
			
		}
		
		return -1;
	}
	
	public static String readTextFromFile(File file) {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
			
		    String s = IOUtils.toString(in);
		    return s;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param jo
	 * @param path
	 * @param pretty
	 */
	public static void writeJSONObjectToFile(JSONObject jo, File path, boolean pretty) {
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"))) {
			String jsonString = jo.toString();
			
			if (pretty) {
				String formattedString = JsonWriter.formatJson(jsonString);
				bw.write(formattedString);
			} else {
				bw.write(jsonString);
			}
			
		} catch (IOException e) {
			// Print out all exceptions, including suppressed ones.
			System.err.println("thrown exception: " + e.toString());
			Throwable[] suppressed = e.getSuppressed();
			for (int i = 0; i < suppressed.length; i++) {
				System.err.println("suppressed exception: " + suppressed[i].toString());
			}
		}
	}
	
	/**
	 * 
	 * @param releaseInfoFile
	 * @return
	 * @throws IOException 
	 */
	public static JSONObject extractAkaInfoFromReleaseInfoFile(File releaseInfoFile) throws IOException {
		if (releaseInfoFile == null) {
			throw new NullPointerException("parameter releaseInfoFile cannot be null");
		}
		
		if (!releaseInfoFile.exists()) {
			throw new IOException("file" + releaseInfoFile.getName() + "does not exist");
		}
		
		if (!releaseInfoFile.isFile()) {
			throw new IOException(releaseInfoFile.getName() + "is not a file");
		}
		
		
		String content = readTextFromFile(releaseInfoFile);
		
		JSONObject titles = new JSONObject();
		
		JSONArray titlesArray = new JSONArray();
		
		Document doc = Jsoup.parse(content);
		
		Elements titleElements = doc.select("h3[itemprop=name] a[itemprop=url]");
		String originalTitle = titleElements.get(0).text();
		
		Elements yearElements = doc.select("h3[itemprop=name] span");
		String yearString = yearElements.get(0).text();
		
		yearString = yearString.replaceAll("[^0-9]", "");
		
		int year = 0;
		if (yearString.length() >= 4) {
			yearString = yearString.substring(0, 4);
			try {
				year = Integer.parseInt(yearString);
			} catch (NumberFormatException nfe) {}
		}
		
		if (year < 1850 || year > 2020) {
			year = -1;
		}
		
		titles.put("year", year);
		titles.put("itTitle", originalTitle);
		
		Elements tableElements = doc.select("table#akas");
		
		Elements tableRowElements = tableElements.select("tbody tr");
		
		for (int i = 0; i < tableRowElements.size(); i++) {
			Element row = tableRowElements.get(i);
			Elements rowItems = row.select("td");
			
			String nazione = rowItems.get(0).text();
			String title = rowItems.get(1).text();
			
			if (nazione.equals("(original title)")) {
				originalTitle = title;
			} else {
				titlesArray.put(new JSONObject().put(nazione, title));
			}
		}
		
		titles.put("originalTitle", originalTitle);
		
		titlesArray.put(new JSONObject().put("(original title)", originalTitle));
		
		titles.put("titles-imdb", titlesArray);
			
		return titles;
	}
	
	/**
	 * 
	 * @param directory
	 * @return
	 */
	public static File[] listMoviesDirectoriesFiles(File directory, boolean sort) {
		File[] directories = directory.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File current, String name) {
				
				File f = new File(current, name);
				boolean accept = f.isDirectory();
				
				if (f.getName().startsWith("$") || f.getName().startsWith(".")) accept = false;
				
				return accept;
			}
			
		});
		
		if (directories != null && directories.length > 0) {
			if (sort) Arrays.sort(directories);
			return directories;
		} else {
			return new File[] {};
		}
	}
	
	public static File[] listMovieFiles(File directory, boolean sort) {
		File[] files = directory.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File current, String name) {
				
				File f = new File(current, name);
				
				if (!f.isFile()) return false;
				
				if
				(
					f.getName().endsWith(".mkv")
					||
					f.getName().endsWith(".avi")
					||
					f.getName().endsWith(".mp4")
					||
					f.getName().endsWith(".m4v")
					||
					f.getName().endsWith(".iso")
				)
				{
					return true;
				}
				
				return false;
			}
			
		});
		
		if (files != null && files.length > 0) {
			if (sort) Arrays.sort(files);
			return files;
		} else {
			return new File[] {};
		}
	}
	
	/**
	 * 
	 * @param directory
	 * @param pattern
	 * @return
	 */
	public static File[] listFilesWithPatternFiles(File directory, String pattern) {
		final String myPattern = pattern;
		
		File[] directories = directory.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File current, String name) {
				
				Pattern pattern = Pattern.compile(myPattern);
				Matcher matcher = pattern.matcher(name);
				File f = new File(current, name);
				
				return matcher.find() && f.isFile();
			}
			
		});
		Arrays.sort(directories);
		
		return directories;
	}
	
	/**
	 * 
	 * @param directory
	 * @param pattern
	 * @return
	 */
	public static File[] listDirectoriesWithPatternFiles(File directory, String pattern) {
		final String myPattern = pattern;
		
		File[] directories = directory.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File current, String name) {
				
				Pattern pattern = Pattern.compile(myPattern);
				Matcher matcher = pattern.matcher(name);
				File f = new File(current, name);
				
				return matcher.find() && f.isDirectory();
			}
			
		});
		Arrays.sort(directories);
		
		return directories;
	}
	
	/**
	 * 
	 * @param directory
	 * @param jarray
	 */
	public static void listFilesForFolder(File directory, JSONArray jarray) {
	    for (File fileEntry : directory.listFiles()) {
	    	if (fileEntry.isDirectory()) {
	    		String dirname = fileEntry.getName();
	    		String originalTitle = "";
	    		String itTitle = "";
	    		String year = "";
	    		
	    		java.util.regex.Matcher matcher1 = pattern1.matcher(dirname);
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
	
	/**
	 * 
	 * @param movieDirectory
	 * @param imdbCode
	 * @return
	 */
	public static JSONObject getJsonFromDirectoryName(File movieDirectory, String imdbCode) {
		String originalTitle = "";
		String itTitle = "";
		String year = "";
		
		java.util.regex.Matcher matcher1 = pattern1.matcher(movieDirectory.getName());
		Matcher matcher2 = pattern2.matcher(movieDirectory.getName());
		Matcher matcher3 = pattern3.matcher(movieDirectory.getName());
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
		
		// Creo un oggetto JSON per i dati
		
		JSONObject jso = new JSONObject();
		JSONObject myData = new JSONObject();
		
		myData.put("imdbCode", imdbCode);
		myData.put("year", year);
		myData.put("otlc", "ot");
		
		JSONObject titles = new JSONObject();
		titles.put("ot", originalTitle);
		titles.put("it", itTitle);
		
		myData.put("title", titles);
		
		jso.put("myData", myData);
		
		return jso;
	}
	
	/**
	 * 
	 * @param movieDirectory
	 * @return
	 * @throws IOException
	 */
	public static String[] showLanguagesByFileName(File movieDirectory) throws IOException {
		if (movieDirectory == null) {
            throw new NullPointerException("movieDirectory must not be null");
        }
		if (!movieDirectory.exists()) {
			throw new IOException("movieDirectory does not exist");
		}
		if (!movieDirectory.isDirectory()) {
			throw new IOException("movieDirectory is not a directory");
		}
		
		ArrayList<String> outputList = new ArrayList<String>();
		
		for (File movieFile : Utility.listMovieFiles(movieDirectory, true)) {
			MovieFileName mfn = new MovieFileName(movieFile);
			
			for (String s : mfn.getLanguagesList()) {
				
				if (!outputList.contains(s)) {
					outputList.add(s);
				}
				
			}
			
		}
		
		String[] o = outputList.toArray(new String[] {});
		Arrays.sort(o);
		
		return o;
	}
	
	/**
	 * 
	 * @param movieDirectory
	 * @return
	 * @throws IOException
	 */
	public static boolean allFilesCorrectlyNamed(File movieDirectory) throws IOException {
		if (movieDirectory == null) {
            throw new NullPointerException("movieDirectory must not be null");
        }
		if (!movieDirectory.exists()) {
			throw new IOException("movieDirectory does not exist");
		}
		if (!movieDirectory.isDirectory()) {
			throw new IOException("movieDirectory is not a directory");
		}
		
		for (File movieFile : Utility.listMovieFiles(movieDirectory, true)) {
			MovieFileName mfn = new MovieFileName(movieFile);
			
			if
			(
				!mfn.startsWithDottedAppendix()
				||
				mfn.getGarbage() != null
			)
			{
				return false;
			}
			
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param movieDirectory
	 * @return
	 * @throws IOException
	 */
	public static File getDescriptorFile(File movieDirectory) throws IOException {
		
		if (movieDirectory == null) {
            throw new NullPointerException("movieDirectory must not be null");
        }
		if (!movieDirectory.exists()) {
			throw new IOException("Directory '" + movieDirectory + "' does not exist");
		}
		
		File[] descriptorFiles = Utility.listFilesWithPatternFiles(movieDirectory, Utility.IMDB_DESCRIPTOR_FILE_PATTERN_STRING);
		
		if (descriptorFiles != null && descriptorFiles.length == 1) {
			
			return descriptorFiles[0];
			
		} else {
			
			System.out.println("numero errato di descrittori nella directory: " + movieDirectory.getName());
			return null;
		}
		
	}
	
	/**
	 * 
	 * @param movieDirectory
	 * @return
	 * @throws IOException
	 */
	public static JSONObject getDescriptorJson(File movieDirectory) throws IOException {
		Utility.checkMovieDirectory(movieDirectory);
		
		File descriptorFile = Utility.getDescriptorFile(movieDirectory);
		
		if (descriptorFile != null) {
			
			JSONObject jsonDescriptorObject = Utility.readJSONObjectFromFile(descriptorFile);
			
			return jsonDescriptorObject;
		}
		
		return null;
		
	}
	
	/**
	 * 
	 * @param moviesBaseDirectory
	 * @throws IOException
	 */
	public static void checkMoviesBaseDirectory(File moviesBaseDirectory) throws IOException {
		
		if (moviesBaseDirectory == null) {
            throw new NullPointerException("moviesBaseDirectory must not be null");
        }
		if (!moviesBaseDirectory.exists()) {
			throw new IOException("Directory '" + moviesBaseDirectory + "' does not exist");
		}
		
	}
	
	public static void checkMovieDirectory(File movieDirectory) throws IOException {
		
		if (movieDirectory == null) {
            throw new NullPointerException("movieDirectory must not be null");
        }
		if (!movieDirectory.exists()) {
			throw new IOException("Directory '" + movieDirectory + "' does not exist");
		}
		
	}
	
	public static BigInteger sizeOfByTag(File moviesBaseDirectory, String tag) throws IOException {
		
		Utility.checkMoviesBaseDirectory(moviesBaseDirectory);
		
		BigInteger sum = new BigInteger("0");
		
		for (File movieDirectory : Utility.listMoviesDirectoriesFiles(moviesBaseDirectory, true)) {
			
			File descriptorFile = Utility.getDescriptorFile(movieDirectory);
			if (descriptorFile != null) {
				
				JSONObject jsonDescriptorObject = Utility.readJSONObjectFromFile(descriptorFile);
				
				if (jsonDescriptorObject.has("myData")) {
					JSONObject myData = jsonDescriptorObject.getJSONObject("myData");
					
					if (myData.has("groups")) {
						
						JSONArray jsonGroupsArray = myData.getJSONArray("groups");
						
						for (Object s : jsonGroupsArray) {
							String group = (String) s;
							
							if (group.equals(tag)) {
								
								BigInteger movieDirectorySize = FileUtils.sizeOfDirectoryAsBigInteger(movieDirectory);
								sum = sum.add(movieDirectorySize);
								break;
								
							}
						}
						
					}
					
				}
			}
			
		}
		
		return sum;
		
	}
	
	/**
	 * Rinomina i file dei Movie.
	 * 
	 * @param moviesBaseDirectory
	 * @throws IOException
	 */
	public static void renameMoviesAddingDotsFilesBaseDir(File moviesBaseDirectory) throws IOException {
		
		Utility.checkMoviesBaseDirectory(moviesBaseDirectory);
		
		File[] movieDirectories = Utility.listMoviesDirectoriesFiles(moviesBaseDirectory, true);

		for (File movieDirectory : movieDirectories) {
			File[] movieFiles = Utility.listMovieFiles(movieDirectory, true);
			for (File movieFile : movieFiles) {
				Collection<String> languages = new LinkedList<String>();

				String fileRename = movieDirectory.getName();
				fileRename = fileRename.replaceAll(" ", ".");

				//System.out.println(movieFile.getPath());

				String[] languagesArray = languages.toArray(new String[] {});
				Arrays.sort(languagesArray);

				String langString = "";
				if (languagesArray.length > 0) {
					langString += "{";

					for (String ln : languagesArray) {

						langString += (ln + "-");

					}

					langString += "}";
				}
				
				if (movieFile.getName().startsWith(movieDirectory.getName())) {
					System.out.println("Rinomino 1");
					File newFile = new File(movieFile.getParent(), movieFile.getName().replace(movieDirectory.getName(), fileRename));
					boolean b = movieFile.renameTo(newFile);
					System.out.println(b);
				}

				System.out.println(fileRename + langString);

			}
		}
	}
	
	public static void printMediaInfo(File movieDirectory) throws IOException {
		
		Utility.checkMovieDirectory(movieDirectory);
		
		for (File movieFile : Utility.listMovieFiles(movieDirectory, true)) {
			Collection<String> languages = new LinkedList<String>();

			String fileRename = movieDirectory.getName();
			fileRename = fileRename.replaceAll(" ", ".");

			System.out.println(movieFile.getPath());
			
			MediaInfo info = new MediaInfo();
			info.open(movieFile);

			int videoStreamCount = info.streamCount(MediaInfo.StreamKind.Video);
			int audioStreamCount = info.streamCount(MediaInfo.StreamKind.Audio);

			System.out.println("Video stream count: " + videoStreamCount);
			System.out.println("Audio stream count: " + audioStreamCount);

			
			for (int i = 0; i < videoStreamCount; i++) {

				System.out.println("++++++++++");
				System.out.println("Video stream: " + i);

				String format = info.get(MediaInfo.StreamKind.Video, i, "Format", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
				String frameRate = info.get(MediaInfo.StreamKind.Video, i, "FrameRate", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
				String width = info.get(MediaInfo.StreamKind.Video, i, "Width", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);

				System.out.println("Format: " + format);
				System.out.println("FrameRate: " + frameRate);
				System.out.println("Width: " + width);
				System.out.println("++++++++++");

			}

			for (int i = 0; i < audioStreamCount; i++) {

				System.out.println("++++++++++");
				System.out.println("Audio stream: " + i);

				String format = info.get(MediaInfo.StreamKind.Audio, i, "Format", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
				String frameRate = info.get(MediaInfo.StreamKind.Audio, i, "FrameRate", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
				String language = info.get(MediaInfo.StreamKind.Audio, i, "Language", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);

				System.out.println("Format: " + format);
				System.out.println("FrameRate: " + frameRate);
				System.out.println("Language: " + language);
				System.out.println("++++++++++");

				if (!languages.contains(language))
					languages.add(language);

			}
			
			
			info.close();

			String[] languagesArray = languages.toArray(new String[] {});
			Arrays.sort(languagesArray);

			String langString = "";
			if (languagesArray.length > 0) {
				langString += "{";

				for (String ln : languagesArray) {

					langString += (ln + "-");

				}

				langString += "}";
			}
			
			System.out.println(fileRename + langString);

			System.out.println();
			System.out.println();
			System.out.println();
		}
		
	}
	
	public static void printMediaInfoBaseDir(File moviesBaseDirectory) throws IOException {
		
		Utility.checkMoviesBaseDirectory(moviesBaseDirectory);

		for (File movieDirectory : Utility.listMoviesDirectoriesFiles(moviesBaseDirectory, true)) {
			printMediaInfo(movieDirectory);
		}

	}
	
	public static final void printImdbDescriptorsFromWebPage(String url) throws IOException {
		String urlContent = Utility.getUrlContent(url);
		
		Pattern pattern = Pattern.compile(Utility.IMDB_DESCRIPTOR_FILE_PATTERN_STRING_2);
		Matcher matcher = pattern.matcher(urlContent);
		
		List<String> movieTags = new ArrayList<String>();
		while (matcher.find()) {
			String s = matcher.group();
			
			if (s.length() == 9 && !movieTags.contains(s)) {
				System.out.println(s);
				movieTags.add(matcher.group());
			}
		}
	}
	
	public static final void printImdbDescriptorsFromWebPage(String[] urls) throws IOException {
		
		for (String url : urls) {
		
			String urlContent = Utility.getUrlContent(url);
			
			Pattern pattern = Pattern.compile(Utility.IMDB_DESCRIPTOR_FILE_PATTERN_STRING_2);
			Matcher matcher = pattern.matcher(urlContent);
			
			List<String> movieTags = new ArrayList<String>();
			while (matcher.find()) {
				String s = matcher.group();
				
				if (s.length() == 9 && !movieTags.contains(s)) {
					movieTags.add(matcher.group());
				}
			}
			
		}
		
	}
	
	public static final List<String> getImdbDescriptorsFromWebPage(String url) throws IOException {
		String urlContent = Utility.getUrlContent(url);
		
		Pattern pattern = Pattern.compile(Utility.IMDB_DESCRIPTOR_FILE_PATTERN_STRING_2);
		Matcher matcher = pattern.matcher(urlContent);
		
		List<String> movieTags = new ArrayList<String>();
		while (matcher.find()) {
			String s = matcher.group();
			
			if (s.length() == 9 && !movieTags.contains(s)) {
				movieTags.add(matcher.group());
			}
		}
		
		return movieTags;
	}
	
	public static final List<String> getImdbDescriptorsFromWebPage(String[] urls) throws IOException {
		
		List<String> movieTags = new ArrayList<String>();
		
		for (String url : urls) {
			System.out.println("mi collego all'url: " + url);
			
			String urlContent = Utility.getUrlContent(url);
			
			Pattern pattern = Pattern.compile(Utility.IMDB_DESCRIPTOR_FILE_PATTERN_STRING_2);
			Matcher matcher = pattern.matcher(urlContent);
			
			while (matcher.find()) {
				String s = matcher.group();
				
				if (s.length() == 9 && !movieTags.contains(s)) {
					movieTags.add(matcher.group());
				}
			}
			
		}
		return movieTags;
	}
	
	public static void makeImdbDirs(File moviesBaseDirectory) throws IOException {
		
		Utility.checkMoviesBaseDirectory(moviesBaseDirectory);
		
		try {
			for (String s : Files.readAllLines(Paths.get("tt_list.txt"), Charset.defaultCharset())) {
				
				File dirName = new File(moviesBaseDirectory, s);
				if (dirName.mkdir()) {
					System.out.println("nuova directory creata: " + s);
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void printImdbDescriptorsForSearchPages() throws IOException {
		
		List<String> mainURLs = new ArrayList<String>();
		for (int n = 1; n < 10000; n += 250) {
			//mainURLs.add("http://www.imdb.com/search/title?at=0&count=100&sort=num_votes,desc&start=" + n + "&feature,tv_movie,documentary,video");
			mainURLs.add("http://www.imdb.com/search/title?count=250&release_date=2014,2014&start=" + n + "&title_type=feature&view=simple");
		}
		
		String[] urls = mainURLs.toArray(new String[] {});
		
		Utility.printImdbDescriptorsFromWebPage(urls);
	}
	
	public static List<String> getImdbDescriptorsForSearchPages() throws IOException {
		
		List<String> mainURLs = new ArrayList<String>();
		for (int n = 1; n < 1000; n += 250) {
			//mainURLs.add("http://www.imdb.com/search/title?at=0&count=100&sort=num_votes,desc&start=" + n + "&feature,tv_movie,documentary,video");
			//String addUrl = "http://www.imdb.com/search/title?count=250&release_date=2014,2014&start=" + n + "&title_type=feature&view=simple";
			String addUrl = "http://www.imdb.com/list/ls006266261/?start=" + n + "&view=compact&sort=listorian:asc&defaults=1";
			
			System.out.println("aggiungo url: " + addUrl);
			mainURLs.add(addUrl);
		}
		
		String[] urls = mainURLs.toArray(new String[] {});
		
		return Utility.getImdbDescriptorsFromWebPage(urls);
	}
	
	/**
	 * 
	 * @param baseMoviesDirectory
	 * @throws IOException
	 */
	public static void makeClassicPdfCatalog(File baseMoviesDirectory) throws IOException {
		
		if (baseMoviesDirectory == null) {
			throw new NullPointerException("argument baseMoviesDirectory can't be null");
		}
		if (!baseMoviesDirectory.exists()) {
			throw new IOException("directory baseMoviesDirectory does not exist");
		}
		
		List<Movie> moviesList = new ArrayList<Movie>();
		
		int descriptorAnomalies = 0;
		for (File movieDirectory : Utility.listMoviesDirectoriesFiles(baseMoviesDirectory, true)) {
			
			File descriptorFile = Utility.getDescriptorFile(movieDirectory);
			if (descriptorFile != null) {
				
				JSONObject jsonObject = Utility.readJSONObjectFromFile(descriptorFile);
				
				if (jsonObject == null) {
					System.out.println("empty or not valid descriptor: " + movieDirectory);
					
					jsonObject = Utility.getJsonFromDirectoryName(movieDirectory, descriptorFile.getName());
					
					Utility.writeJSONObjectToFile(jsonObject, descriptorFile, true);
				}
				
				Movie movie = new Movie(jsonObject);
				movie.setFolderName(movieDirectory.getName());
				try {
					movie.setRealAvailableLanguages(Utility.showLanguagesByFileName(movieDirectory));
					movie.setAllFilesCorrectlyNamed(Utility.allFilesCorrectlyNamed(movieDirectory));
					movie.setAllTags(MoviesBackup.getAllTags(movieDirectory));
				} catch (IOException e) {
					e.printStackTrace();
				}
				moviesList.add(movie);
				
			} else {
				
				descriptorAnomalies++;
				Movie movie = new Movie();
				movie.setFolderName(movieDirectory.getName());
				moviesList.add(movie);
				
			}
			
		}
		System.out.println("descriptor anomalies: " + descriptorAnomalies);
		
		MoviesCatalog moviesCatalog = new MoviesCatalog(moviesList);
		
		String fileName = "catalog-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".pdf";
		moviesCatalog.serialize(moviesCatalog.makePDF(), fileName, SerializationModeEnum.Standard, "Movies catalog", "Movies catalog", null);
		
	}
	
	public static void getDiff(File dirA, File dirB) throws IOException	{
		File[] fileList1 = dirA.listFiles();
		File[] fileList2 = dirB.listFiles();
		Arrays.sort(fileList1);
		Arrays.sort(fileList2);
		HashMap<String, File> map1;
		if (fileList1.length < fileList2.length) {
			map1 = new HashMap<String, File>();
			for (int i = 0;i < fileList1.length; i++) {
				map1.put(fileList1[i].getName(), fileList1[i]);
			}
			
			compareNow(fileList2, map1);
		} else {
			map1 = new HashMap<String, File>();
			for(int i=0;i<fileList2.length;i++) {
				map1.put(fileList2[i].getName(),fileList2[i]);
			}
			compareNow(fileList1, map1);
		}
	}
	
	public static void compareNow(File[] fileArr, HashMap<String, File> map) throws IOException {
		for (int i = 0; i < fileArr.length; i++) {
			String fName = fileArr[i].getName();
			File fComp = map.get(fName);
			map.remove(fName);
			
			if (fComp != null) {
				if(fComp.isDirectory()) {
					
					getDiff(fileArr[i], fComp);
					
				} else {
					
					String cSum1 = checksum(fileArr[i]);
					String cSum2 = checksum(fComp);
					if(!cSum1.equals(cSum2)) {
						System.out.println(fileArr[i].getName()+"\t\t"+ "different");
					} else {
						System.out.println(fileArr[i].getName()+"\t\t"+"identical");
					}
					
				}
			}
			else
			{
				if(fileArr[i].isDirectory())
				{
					traverseDirectory(fileArr[i]);
				}
				else
				{
					System.out.println(fileArr[i].getName()+"\t\t"+"only in "+fileArr[i].getParent());
				}
			}
		}
		Set<String> set = map.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext())
		{
			String n = it.next();
			File fileFrmMap = map.get(n);
			map.remove(n);
			if(fileFrmMap.isDirectory())
			{
				traverseDirectory(fileFrmMap);
			}
			else
			{
				System.out.println(fileFrmMap.getName() +"\t\t"+"only in "+ fileFrmMap.getParent());
			}
		}
	}
	
	public static void traverseDirectory(File dir)
	{
		File[] list = dir.listFiles();
		for(int k=0;k<list.length;k++)
		{
			if(list[k].isDirectory())
			{
				traverseDirectory(list[k]);
			}
			else
			{
				System.out.println(list[k].getName() +"\t\t"+"only in "+ list[k].getParent());
			}
		}
	}
	
	public static String checksum(File file) 
	{
		try 
		{
		    InputStream fin = new FileInputStream(file);
		    java.security.MessageDigest md5er = MessageDigest.getInstance("MD5");
		    byte[] buffer = new byte[1024];
		    int read;
		    do 
		    {
		    	read = fin.read(buffer);
		    	if (read > 0)
		    		md5er.update(buffer, 0, read);
		    } while (read != -1);
		    fin.close();
		    byte[] digest = md5er.digest();
		    if (digest == null)
		      return null;
		    String strDigest = "0x";
		    for (int i = 0; i < digest.length; i++) 
		    {
		    	strDigest += Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1).toUpperCase();
		    }
		    return strDigest;
		} 
		catch (Exception e) 
		{
		    return null;
		}
	}
	
	
	
	
	/* OLD METHODS TO DELETE */
	
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
