package hcents.moviesorganizer;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class MakeDirectoriesFromImdbPage {
	
	private Pattern pattern = Pattern.compile(Utility.IMDB_DESCRIPTOR_FILE_PATTERN_STRING);
	
	private static void addLanguageInDescriptor(String languageCode, String nationName, File movieDirectory) throws IOException {
		
		// Se c'è un file akainfo e non esiste il file descrittore, lo creo da questo.
		File[] akaInfoFile = movieDirectory.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(java.io.File current, String name) {
				
				Pattern pattern = Pattern.compile(Utility.AKAINFO_FILE_PATTERN_STRING);
				Matcher matcher = pattern.matcher(name);
				java.io.File f = new java.io.File(current, name);
				
				return matcher.find() && f.isFile();
			}
			
		});
		
		if (akaInfoFile == null || akaInfoFile.length == 0) {
			throw new IOException("eccezione impropria: non c'è il file -akainfo");
		}
		
		if (akaInfoFile.length == 1) {
			String imdbCode = akaInfoFile[0].getName().substring(0, 9);
			
			File descriptorFile = Utility.getDescriptorFile(movieDirectory);
			
			if (descriptorFile == null) {
				descriptorFile = new File(movieDirectory, imdbCode);
				descriptorFile.createNewFile();
			}
			
			JSONObject jsonAkaObject = Utility.readJSONObjectFromFile(akaInfoFile[0]);
			JSONObject jsonDescriptorObject = Utility.readJSONObjectFromFile(descriptorFile);
			
			if (jsonAkaObject.has("titles-imdb")) {
				JSONArray titles = jsonAkaObject.getJSONArray("titles-imdb");
				
				for (Object obj : titles) {
					JSONObject jsonObj = (JSONObject) obj;
					
					if (jsonDescriptorObject.has("myData")) {
						JSONObject myData = jsonDescriptorObject.getJSONObject("myData");
						
						if (myData.has("title")) {
							JSONObject title = myData.getJSONObject("title");
							
							if (!title.has(languageCode)) {
								if (jsonObj.has(nationName)) {
									System.out.println(jsonObj.getString(nationName));
									
									title.put(languageCode, jsonObj.getString(nationName));
									Utility.writeJSONObjectToFile(jsonDescriptorObject, descriptorFile, true);
								}
							}
							
						}
					}
				}
			}
			
		}
		
	}
	
	public static void createBasicDescriptorFile(File movieDirectory) throws IOException {
		Utility.checkMovieDirectory(movieDirectory);
		
		File[] movieDescriptorFileNames = movieDirectory.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File current, String name) {
				
				Pattern pattern = Pattern.compile("^tt\\d*-akainfo$");
				Matcher matcher = pattern.matcher(name);
				java.io.File f = new java.io.File(current, name);
				
				return matcher.find() && f.isFile();
			}
			
		});
		
		
		if (movieDescriptorFileNames.length == 1) {
			String imdbCode = movieDescriptorFileNames[0].getName().substring(0, 9);
			System.out.println(imdbCode);
			
			JSONObject jsonAkaObject = Utility.readJSONObjectFromFile(movieDescriptorFileNames[0]);
			
			String originalTitle = "";
			if (jsonAkaObject.has("originalTitle")) {
				originalTitle = jsonAkaObject.getString("originalTitle");
			}
			
			int year = 0;
			if (jsonAkaObject.has("year")) {
				year = jsonAkaObject.getInt("year");
			}
			
			
			// Creo un oggetto JSON per i dati
			JSONObject jso = new JSONObject();
			JSONObject myData = new JSONObject();
			
			myData.put("imdbCode", imdbCode);
			myData.put("year", year);
			myData.put("otlc", "ot");
			
			JSONObject titles = new JSONObject();
			titles.put("ot", originalTitle);
			//titles.put("it", itTitle);
			
			myData.put("title", titles);
			
			jso.put("myData", myData);
			
			System.out.println(jso);
			
			Utility.writeJSONObjectToFile(jso, new File(movieDirectory, imdbCode), true);
		}
	}
	
	public static void formatBasicMovieDirectoryFromName(File movieDirectory) throws IOException {
		Utility.checkMovieDirectory(movieDirectory);
		
		String imdbCode = movieDirectory.getName();
		String realeaseInfoUrl = "http://www.imdb.com/title/" + imdbCode + "/releaseinfo";
		
		System.out.println("directory imdbCode: " + imdbCode);
		System.out.println("connect to: " + realeaseInfoUrl);
		
		File releaseInfo = new File(movieDirectory, movieDirectory.getName() + "-releaseinfo");
		File akaInfo = new File(movieDirectory, movieDirectory.getName() + "-akainfo");
		
		Utility.writeUrlContentToFile(realeaseInfoUrl, releaseInfo);
		
		JSONObject jsonObject = Utility.extractAkaInfoFromReleaseInfoFile(releaseInfo);
		if (jsonObject != null) {
			Utility.writeJSONObjectToFile(jsonObject, akaInfo, false);
			
			System.out.println("written file: " + akaInfo.getName());
		}
		
		createBasicDescriptorFile(movieDirectory);
		addKnownLanguagesInDescriptor(movieDirectory);
		renameDirectoryReadingDescriptor(movieDirectory);
	}
	
	public static void formatBasicMovieDirectoryFromNameBaseDir(File moviesBaseDirectory) throws IOException {
		Utility.checkMoviesBaseDirectory(moviesBaseDirectory);
		for (File movieDirectory : Utility.listDirectoriesWithPatternFiles(moviesBaseDirectory, Utility.IMDB_DESCRIPTOR_FILE_PATTERN_STRING)) {
			System.out.println("format basic movie directory for: " + movieDirectory.getName());
			formatBasicMovieDirectoryFromName(movieDirectory);
		}
	}
	
	public static void addKnownLanguagesInDescriptor(File movieDirectory) throws IOException {
		addLanguageInDescriptor("it", "Italy", movieDirectory);
		addLanguageInDescriptor("it", "Italy (DVD title)", movieDirectory);
		addLanguageInDescriptor("it", "Italy (DVD box title)", movieDirectory);
		addLanguageInDescriptor("it", "Italy (dubbed version)", movieDirectory);
		addLanguageInDescriptor("it", "Italy (long title)", movieDirectory);
		addLanguageInDescriptor("it", "Italy (new title)", movieDirectory);
		addLanguageInDescriptor("en", "USA", movieDirectory);
		addLanguageInDescriptor("en", "UK", movieDirectory);
		addLanguageInDescriptor("en", "UK (DVD title)", movieDirectory);
		addLanguageInDescriptor("ww-en", "Europe (English title)", movieDirectory);
		addLanguageInDescriptor("de", "Germany", movieDirectory);
		addLanguageInDescriptor("fr", "France", movieDirectory);
		addLanguageInDescriptor("ru", "Russia", movieDirectory);
		addLanguageInDescriptor("ru", "Soviet Union (Russian title)", movieDirectory);
	}
	
	private void compareDirectories(final String folder_from, final String folder_to) {
		System.out.println("Scansione directory FROM.");
		
		List<String> descriptorListRemoteDirectory = new ArrayList<String>();
		List<ImdbFolder> descriptorListImdbRemoteDirectory = new ArrayList<ImdbFolder>();
		
		File file = new File(folder_to);
		
		String[] directories = file.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
			
		});
		Arrays.sort(directories);
		
		for (String movieFolderName : directories) {
			
			
			// Per ogni cartella dovrebbe esserci un film.
			File movieFolder = new File(folder_to + File.separator + movieFolderName);
			
			// Dentro ogni cartella dovrebbe esserci un file ttnnnnnnn
			String[] movieDescriptorFileNames = movieFolder.list(new FilenameFilter() {
				
				@Override
				public boolean accept(java.io.File current, String name) {
					
					Pattern pattern = Pattern.compile("^tt\\d*$");
					Matcher matcher = pattern.matcher(name);
					File f = new File(current, name);
					
					return matcher.find() && f.isFile();
				}
				
			});
			
			if (movieDescriptorFileNames.length == 1) {
				
				if (!descriptorListRemoteDirectory.contains(movieDescriptorFileNames[0])) {
					descriptorListRemoteDirectory.add(movieDescriptorFileNames[0]);
					descriptorListImdbRemoteDirectory.add(new ImdbFolder(movieDescriptorFileNames[0], movieFolderName));
					//System.out.println(movieDescriptorFileNames[0] + ":" + movieFolderName);
				} else {
					System.out.println("Duplicato: " + movieDescriptorFileNames[0] + ":" + movieFolderName);
				}
			} else if (movieDescriptorFileNames.length > 1) {
				System.out.println("Ci sono troppi file descrittori: " + movieFolderName);
			} else if (movieDescriptorFileNames.length == 0) {
				System.out.println("Non ci sono file descrittori: " + movieFolderName);
			}
		}
		
		
		System.out.println("Scansione directory TO.");
		
		List<String> descriptorListLocalDirectory = new ArrayList<String>();
		List<ImdbFolder> descriptorListImdbLocalDirectory = new ArrayList<ImdbFolder>();
		
		File file2 = new File(folder_from);
		
		String[] directories2 = file2.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
			
		});
		Arrays.sort(directories2);
		
		for (String movieFolderName : directories2) {
			
			// Per ogni cartella dovrebbe esserci un film.
			File movieFolder = new File(folder_from + java.io.File.separator + movieFolderName);
			
			// Dentro ogni cartella dovrebbe esserci un file ttnnnnnnn
			String[] movieDescriptorFileNames = movieFolder.list(new FilenameFilter() {
				
				@Override
				public boolean accept(java.io.File current, String name) {
					
					Pattern pattern = Pattern.compile("^tt\\d*$");
					Matcher matcher = pattern.matcher(name);
					java.io.File f = new java.io.File(current, name);
					
					return matcher.find() && f.isFile();
				}
				
			});
			
			if (movieDescriptorFileNames.length == 1) {
				
				if (!descriptorListLocalDirectory.contains(movieDescriptorFileNames[0])) {
					descriptorListLocalDirectory.add(movieDescriptorFileNames[0]);
					descriptorListImdbLocalDirectory.add(new ImdbFolder(movieDescriptorFileNames[0], movieFolderName));
				} else {
					System.out.println("Duplicato: " + movieDescriptorFileNames[0]);
				}
			} else if (movieDescriptorFileNames.length > 1) {
				System.out.println("Ci sono troppi file descrittori: " + movieFolderName);
			} else if (movieDescriptorFileNames.length == 0) {
				System.out.println("Non ci sono file descrittori: " + movieFolderName);
			}
			
			
		}
		
		
		
		
		System.out.println("Confronta directory.");
		
		for (String s : descriptorListRemoteDirectory) {
			if (!descriptorListLocalDirectory.contains(s)) {
				//System.out.println(s);
			} else {
				System.out.print("Duplicato: " + s);
				for (ImdbFolder q : descriptorListImdbLocalDirectory) {
					if (q.imdb.equals(s)) {
						System.out.println(": FROM directory: " + q.folder);
						break;
					}
				}
			}
		}
		
		System.out.println("Fine confronto.");
		
		/*
		System.out.println("Confronta directory.");
		for (ImdbFolder s : descriptorListImdbRemoteDirectory) {
			if (!descriptorListImdbLocalDirectory.contains(s)) {
				System.out.println(s);
				for (ImdbFolder q : descriptorListImdbLocalDirectory) {
					if (q.imdb.equals(s.imdb)) {
						System.out.println("Local: " + q.folder);
						break;
					}
				}
			}
		}
		*/
	}
	
	/**
	 * Rinomina le directory dei Movie leggendo il descriptor.
	 * 
	 * @param movieDirectory
	 * @throws IOException
	 */
	public static void renameDirectoryReadingDescriptor(File movieDirectory) throws IOException {
		
		Utility.checkMovieDirectory(movieDirectory);
		
		JSONObject jsonDescriptorObject = Utility.getDescriptorJson(movieDirectory);
		
		if (jsonDescriptorObject != null && jsonDescriptorObject.has("myData")) {
			JSONObject myData = jsonDescriptorObject.getJSONObject("myData");
			
			String newName = null;
			if (myData.has("directory-name")) {
				newName = myData.getString("directory-name");
			} else {
				MovieTitle movieTitle = MoviesUtility.getMovieTitle(myData);
				newName = movieTitle.getTitleForDirectory();
			}
			
			if (newName != null) {
				File renamedFile = new File(movieDirectory.getParentFile(), newName);
				if (renamedFile != null && !renamedFile.equals(movieDirectory)) {
					
					System.out.println("absolute: " + renamedFile.getAbsolutePath());
					
					if (movieDirectory.getParentFile().canWrite()) {
						boolean renamed = movieDirectory.renameTo(renamedFile);
						if (renamed) {
							System.out.println("Rinominata directory: " + newName);
						} else {
							System.out.println("Non riuscito a rinominare directory: " + newName);
						}
					} else {
						System.out.println("Mancano permessi in scrittura sulla directory: " + movieDirectory.getName());
					}
					
					
				}
			}
			
		}
		
	}
	
	/**
	 * 
	 * @param moviesBaseDirectory
	 * @throws IOException
	 */
	public static void renameDirectoryReadingDescriptorBaseDir(File moviesBaseDirectory) throws IOException {
		
		Utility.checkMoviesBaseDirectory(moviesBaseDirectory);
		for (File movieDirectory : Utility.listMoviesDirectoriesFiles(moviesBaseDirectory)) {
			renameDirectoryReadingDescriptor(movieDirectory);
		}
		
	}
	
	/*
	private static void addLanguageInDescriptorBaseDir(String languageCode, String nationName, File file) {
		
		File[] directories = Utility.listMoviesDirectoriesFiles(file);
		
		for (File movieFolderName : directories) {
			
			//File movieFolder = new java.io.File(dir + java.io.File.separator + movieFolderName);
			
			// Dentro ogni cartella dovrebbe esserci un file ttnnnnnnn
			File[] movieDescriptorFileNames = movieFolderName.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(java.io.File current, String name) {
					
					Pattern pattern = Pattern.compile("^tt\\d*-akainfo$");
					Matcher matcher = pattern.matcher(name);
					java.io.File f = new java.io.File(current, name);
					
					return matcher.find() && f.isFile();
				}
				
			});
			
			
			if (movieDescriptorFileNames.length == 1) {
				String imdbCode = movieDescriptorFileNames[0].getName().substring(0, 9);
				//System.out.println(imdbCode);
				
				JSONObject jsonAkaObject = Utility.readJSON(movieDescriptorFileNames[0]);
				JSONObject jsonDescriptorObject = Utility.readJSON(new File(movieFolderName, imdbCode));
				
				
				
				String itTitle = "";
				if (jsonAkaObject.has("titles-imdb")) {
					JSONArray titles = jsonAkaObject.getJSONArray("titles-imdb");
					
					for (Object obj : titles) {
						JSONObject jsonObj = (JSONObject) obj;
						
						if (jsonDescriptorObject.has("myData")) {
							JSONObject myData = jsonDescriptorObject.getJSONObject("myData");
							
							if (myData.has("title")) {
								JSONObject title = myData.getJSONObject("title");
								
								if (!title.has(languageCode)) {
									if (jsonObj.has(nationName)) {
										System.out.println(jsonObj.getString(nationName));
										
										title.put(languageCode, jsonObj.getString(nationName));
										Utility.writeJSONObjectToFile(jsonDescriptorObject, new File(movieFolderName, imdbCode), true);
									}
								}
								
							}
						}
					}
				}
			}
			
			
		}
		
	}
	*/
	
	private void removeTVSeries(String dir) {
		
		File file = new File(dir);
		
		String[] directories = file.list(new FilenameFilter() {
			
			@Override
			public boolean accept(java.io.File current, String name) {
				return new java.io.File(current, name).isDirectory();
			}
			
		});
		Arrays.sort(directories);
		
		
		for (String movieFolderName : directories) {
			
			File movieFolder = new java.io.File(dir + java.io.File.separator + movieFolderName);
			
			// Dentro ogni cartella dovrebbe esserci un file ttnnnnnnn
			File[] movieDescriptorFileNames = movieFolder.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(java.io.File current, String name) {
					
					Pattern pattern = Pattern.compile("^tt\\d*-releaseinfo$");
					Matcher matcher = pattern.matcher(name);
					java.io.File f = new java.io.File(current, name);
					
					return matcher.find() && f.isFile();
				}
				
			});
			
			
			if (movieDescriptorFileNames.length == 1) {
				//System.out.println(movieDescriptorFileNames[0]);
				
				String text = Utility.readTextFromFile(movieDescriptorFileNames[0]);
				
				Pattern pattern = Pattern.compile("TV Mini-Series");
				Matcher matcher = pattern.matcher(text);
				
				if (matcher.find()) {
					System.out.println("TV Series: " + movieFolderName);
					File f = new File(dir + java.io.File.separator + movieFolderName);
					File renamedFile = new File(dir + java.io.File.separator + "_TVSERIES_" + movieFolderName);
					f.renameTo(renamedFile);
				}
			}
			
			
		}
		
	}
	
	/*
	private void createDirsAndDownloadFromWeb(String dir) {
		
		List<String> allLines = null;
		try {
			allLines = Files.readAllLines(Paths.get("tt_list.txt"), Charset.defaultCharset());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (allLines != null) {
			for (String s : allLines) {
				File directory = new File(dir, s);
				if (directory.mkdir()) {
					System.out.println("Directory creata.");
				}
				
				if (directory.exists()) { 
				
					System.out.println(s);
					
					try {
						Response response;
					
						response = Jsoup.connect("http://www.imdb.com/title/" + s + "/releaseinfo")
						           .ignoreContentType(true)
						           .userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:38.0) Gecko/20100101 Firefox/38.0 Iceweasel/38.4.0")
						           .header("Accept-Language", "it")
						           .referrer("http://www.google.com")   
						           .timeout(12000) 
						           .followRedirects(true)
						           .execute();
					
					
						Document doc = response.parse();
						
						Utility.writeTextToFile(doc.toString(), dir + java.io.File.separator + s + java.io.File.separator + s + "-releaseinfo");
						
						JSONObject jsonObject = Utility.extractAKAInfo(s);
						
						Utility.writeJSONObjectToFile(jsonObject, dir + java.io.File.separator + s + java.io.File.separator + s + "-akainfo", false);
						
						String originalTitle = "";
						if (jsonObject.has("originalTitle")) {
							originalTitle = jsonObject.getString("originalTitle");
						}
						
						String itTitle = "";
						if (jsonObject.has("itTitle")) {
							itTitle = jsonObject.getString("itTitle");
						}
						
						int year = 0;
						if (jsonObject.has("year")) {
							year = jsonObject.getInt("year");
						}
						
						if (!originalTitle.equals("") && !itTitle.equals("") && year != 0) {
							String newName = "";
							
							MovieTitle mt = new MovieTitle(originalTitle, itTitle, year);
							
							
							newName = mt.getOriginalTitleCapitalized() + " (" + year + ")";
							
							File renamedFile = new File(dir + java.io.File.separator + newName);
							directory.renameTo(renamedFile);
						}
						
						
						
						
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}
		}
		
	}
	*/
	
	
	
	private void removeDuplicates() {
		
		List<String> allLines = null;
		try {
			allLines = Files.readAllLines(Paths.get("tt_list.txt"), Charset.defaultCharset());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		List<String> newLines = new ArrayList<String>();
		
		for (String s : allLines) {
			if (!newLines.contains(s)) {
				System.out.println(s);
			}
		}
		
	}
	
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		//MakeDirectoriesFromImdbPage mdfip = new MakeDirectoriesFromImdbPage();
		
		// Se voglio aggiungere una lingua nei descriptor di tutti i film della cartella
		//mdfip.addAllLanguagesInDescriptor("Z:\\+Video\\+Movies\\IT\\__B__\\");
		
		// Se voglio sistemare contenuto e nome directory in una cartella che contiene cartelle ttxxxxxxx
		//mdfip.createBasicDescriptorFromDirectoryName("Z:\\+Video\\+Movies\\IT\\__B__\\");
		//mdfip.createBasicDescriptorFromDirectoryName("movies_dir");
		
		// Se voglio passare cartelle gi� preparate da __B__ ad __A__
		//mdfip.compareDirectories("Z:\\+Video\\+Movies\\IT\\__B__\\", "Z:\\+Video\\+Movies\\IT\\__A__\\");
		
		// Se voglio rinominare le directory dei film leggendo il descriptor
		//mdfip.renameFolderReadingDescriptor("Z:\\+Video\\+Movies\\IT\\__B__\\");
		//mdfip.renameFolderReadingDescriptor("movies_dir");
		
		// Se voglio la lista dei ttxxxxxxx da pagine di imdb
		/*
		String[] pages = new String[] {
			"http://www.imdb.com/list/ls006266261/",
			"http://www.imdb.com/list/ls006266261/?start=101&view=detail&sort=listorian:asc",
			"http://www.imdb.com/list/ls006266261/?start=201&view=detail&sort=listorian:asc",
			"http://www.imdb.com/list/ls006266261/?start=301&view=detail&sort=listorian:asc",
			"http://www.imdb.com/list/ls006266261/?start=401&view=detail&sort=listorian:asc",
			"http://www.imdb.com/list/ls006266261/?start=501&view=detail&sort=listorian:asc",
			"http://www.imdb.com/list/ls006266261/?start=601&view=detail&sort=listorian:asc",
			"http://www.imdb.com/list/ls006266261/?start=701&view=detail&sort=listorian:asc",
			"http://www.imdb.com/list/ls006266261/?start=801&view=detail&sort=listorian:asc",
			"http://www.imdb.com/list/ls006266261/?start=901&view=detail&sort=listorian:asc"
		};*/
		//mdfip.readImdbPage(pages);
		
		// Se voglio creare tutte le cartelle dal file tt_list.txt
		//mdfip.makeDirs("movies_dir");
		
	}
	
}

class ImdbFolder {
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((folder == null) ? 0 : folder.hashCode());
		result = prime * result + ((imdb == null) ? 0 : imdb.hashCode());
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
		ImdbFolder other = (ImdbFolder) obj;
		if (folder == null) {
			if (other.folder != null)
				return false;
		} else if (!folder.equals(other.folder))
			return false;
		if (imdb == null) {
			if (other.imdb != null)
				return false;
		} else if (!imdb.equals(other.imdb))
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ImdbFolder [imdb=" + imdb + ", folder=" + folder + "]";
	}
	public ImdbFolder(String imdb, String folder) {
		this.imdb = imdb;
		this.folder = folder;
	}
	
	public String imdb;
	public String folder;
}
