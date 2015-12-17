import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.json.JSONArray;
import org.json.JSONObject;

import com.cedarsoftware.util.io.JsonWriter;


public class MoviesBackup {
	
	/**
	 * Aggiunge il tag a movieDirectory.
	 * 
	 * @param movieDirectory
	 * @param tag
	 * @throws IOException
	 */
	public static void addMovieTag(File movieDirectory, String tag) throws IOException {
		Utility.checkMovieDirectory(movieDirectory);
		
		if (tag == null) throw new NullPointerException("tag is null");
		
		File descriptorFile = Utility.getDescriptorFile(movieDirectory);
		
		if (descriptorFile != null) {
			
			boolean doTag = true;
			JSONObject jsonDescriptorObject = Utility.readJSON(descriptorFile);
			
			if (jsonDescriptorObject != null && jsonDescriptorObject.has("myData")) {
				JSONObject myData = jsonDescriptorObject.getJSONObject("myData");
				if (myData.has("groups")) {
					JSONArray jsonGroupsArray = myData.getJSONArray("groups");
					
					for (Object s : jsonGroupsArray) {
						String group = (String) s;
						
						if (group.equals(tag)) {
							doTag = false;
							continue;
						}
					}
					
					jsonGroupsArray.put(tag);
				} else {
					List<String> groupsList = new ArrayList<String>();
					groupsList.add(tag);
					myData.put("groups", groupsList);
				}
			}
			
			if (doTag) {
				// System.out.println(jsonDescriptorObject);
				Utility.writeJSONObjectToFile(jsonDescriptorObject, descriptorFile, true);
			}
			
		}
	}
	
	/**
	 * Aggiunge il tag a tutti i Movies in moviesBaseDirectory.
	 * 
	 * @param moviesBaseDirectory
	 * @param tag
	 * @throws IOException
	 */
	public static void addMovieTagBaseDir(File moviesBaseDirectory, String tag) throws IOException {
		Utility.checkMoviesBaseDirectory(moviesBaseDirectory);
		
		if (tag == null) throw new NullPointerException("tag is null");
		
		for (File movieDirectory : Utility.listMoviesDirectoriesFiles(moviesBaseDirectory)) {
			
			addMovieTag(movieDirectory, tag);
			
		}
		
	}
	
	/**
	 * Aggiunge il tag ai Movies di moviesBaseDirectory che hanno i nomi dei descrittori indicati.
	 * 
	 * @param moviesBaseDirectory
	 * @param tag
	 * @param descriptorNames
	 * @throws IOException
	 */
	public static void addMovieTagBaseDir(File moviesBaseDirectory, String tag, String[] descriptorNames) throws IOException {
		Utility.checkMoviesBaseDirectory(moviesBaseDirectory);
		
		if (tag == null) throw new NullPointerException("tag is null");
		
		if (descriptorNames == null) throw new NullPointerException("descriptors is null");
		
		for (File movieDirectory : Utility.listMoviesDirectoriesFiles(moviesBaseDirectory)) {
			
			File movieDescriptor = Utility.getDescriptorFile(movieDirectory);
			
			for (String descriptorName : descriptorNames) {
				if (movieDescriptor != null && movieDescriptor.getName().equals(descriptorName)) {
					addMovieTag(movieDirectory, tag);
				}
			}
			
		}
	}
	
	/**
	 * Mi dice se un Movie ha un determinato tag.
	 * 
	 * @param movieDirectory
	 * @param tag
	 * @return
	 * @throws IOException
	 */
	public static boolean hasTag(File movieDirectory, String tag) throws IOException {
		Utility.checkMovieDirectory(movieDirectory);
		
		if (tag == null) throw new NullPointerException("tag is null");
		
		File descriptorFile = Utility.getDescriptorFile(movieDirectory);
		
		if (descriptorFile != null) {
			
			JSONObject jsonDescriptorObject = Utility.readJSON(descriptorFile);
			
			if (jsonDescriptorObject != null && jsonDescriptorObject.has("myData")) {
				JSONObject myData = jsonDescriptorObject.getJSONObject("myData");
				if (myData.has("groups")) {
					JSONArray jsonGroupsArray = myData.getJSONArray("groups");
					
					for (Object s : jsonGroupsArray) {
						String group = (String) s;
						
						if (group.equals(tag)) {
							return true;
						}
					}
				}
			}
			
		}
		
		return false;
		
	}
	
	public static boolean hasTagStartingWith(File movieDirectory, String tag) throws IOException {
		Utility.checkMovieDirectory(movieDirectory);
		
		if (tag == null) throw new NullPointerException("tag is null");
		
		File descriptorFile = Utility.getDescriptorFile(movieDirectory);
		
		if (descriptorFile != null) {
			
			JSONObject jsonDescriptorObject = Utility.readJSON(descriptorFile);
			
			if (jsonDescriptorObject != null && jsonDescriptorObject.has("myData")) {
				JSONObject myData = jsonDescriptorObject.getJSONObject("myData");
				if (myData.has("groups")) {
					JSONArray jsonGroupsArray = myData.getJSONArray("groups");
					
					for (Object s : jsonGroupsArray) {
						String group = (String) s;
						
						if (group.startsWith(tag)) {
							return true;
						}
					}
				}
			}
			
		}
		
		return false;
		
	}
	
	/**
	 * Cancella tutti i tag del Movie in movieDirectory.
	 * 
	 * @param movieDirectory
	 * @throws IOException
	 */
	public static void resetAllMovieTags(File movieDirectory) throws IOException {
		File descriptorFile = Utility.getDescriptorFile(movieDirectory);
		
		if (descriptorFile != null) {
			
			JSONObject jsonDescriptorObject = Utility.readJSON(descriptorFile);
			
			if (jsonDescriptorObject.has("myData")) {
				JSONObject myData = jsonDescriptorObject.getJSONObject("myData");
				
				if (myData.has("groups")) {
					myData.remove("groups");
					Utility.writeJSONObjectToFile(jsonDescriptorObject, descriptorFile, true);
				}
				
			}
			
		}
	}
	
	/**
	 * Cancella tutti i tag di tutti i Movies in moviesBaseDirectory.
	 * 
	 * @param moviesBaseDirectory
	 * @throws IOException
	 */
	public static void resetAllMovieTagsBaseDir(File moviesBaseDirectory) throws IOException {
		Utility.checkMoviesBaseDirectory(moviesBaseDirectory);
		
		for (File movieDirectory : Utility.listMoviesDirectoriesFiles(moviesBaseDirectory)) {
			
			resetAllMovieTags(movieDirectory);
			
		}
	}
	
	/**
	 * Cancella tutti i tag che iniziano con la stringa data.
	 * 
	 * @param movieDirectory
	 * @param tag
	 * @throws IOException
	 */
	public static void removeTagsStartingWith(File movieDirectory, String tag) throws IOException {
		Utility.checkMovieDirectory(movieDirectory);
		
		if (tag == null) throw new NullPointerException("tag is null");
		
		File descriptorFile = Utility.getDescriptorFile(movieDirectory);
		
		if (descriptorFile != null) {
			
			boolean doTag = true;
			JSONObject jsonDescriptorObject = Utility.readJSON(descriptorFile);
			
			if (jsonDescriptorObject != null && jsonDescriptorObject.has("myData")) {
				JSONObject myData = jsonDescriptorObject.getJSONObject("myData");
				if (myData.has("groups")) {
					JSONArray jsonGroupsArray = myData.getJSONArray("groups");
					
					for (int i = 0; i < jsonGroupsArray.length(); i++) {
						String group = (String) jsonGroupsArray.get(i);
						
						if (group.startsWith(tag)) {
							jsonGroupsArray.remove(i);
							doTag = true;
						}
					}
				}
			}
			
			if (doTag) {
				// System.out.println(jsonDescriptorObject);
				Utility.writeJSONObjectToFile(jsonDescriptorObject, descriptorFile, true);
			}
			
		}
	}
	
	/**
	 * Cancella tutti i tag che iniziano con la stringa data per tutti i Movie.
	 * 
	 * @param moviesBaseDirectory
	 * @param tag
	 * @throws IOException
	 */
	public static void removeTagsStartingWithBaseDir(File moviesBaseDirectory, String tag) throws IOException {
		Utility.checkMoviesBaseDirectory(moviesBaseDirectory);
		
		if (tag == null) throw new NullPointerException("tag is null");
		
		for (File movieDirectory : Utility.listMoviesDirectoriesFiles(moviesBaseDirectory)) {
			
			removeTagsStartingWith(movieDirectory, tag);
			
		}
	}
	
	public static String[] getAllTags(File movieDirectory) throws IOException {
		Utility.checkMovieDirectory(movieDirectory);
		
		JSONObject jsonDescriptorObject = Utility.getDescriptorJson(movieDirectory);
		
		if (jsonDescriptorObject != null && jsonDescriptorObject.has("myData")) {
			JSONObject myData = jsonDescriptorObject.getJSONObject("myData");
			if (myData.has("groups")) {
				JSONArray jsonGroupsArray = myData.getJSONArray("groups");
				
				String[] returnArray = new String[jsonGroupsArray.length()];
				
				for (int i = 0; i < jsonGroupsArray.length(); i++) {
					String group = (String) jsonGroupsArray.get(i);
					
					returnArray[i] = group;
				}
				
				return returnArray;
			}
		}
		
		return null;
	}
	
	/**
	 * Stampa una lista di directory che contengono il tag dato.
	 * 
	 * @param moviesBaseDirectory
	 * @param tag
	 * @throws IOException
	 */
	public static void printMoviesDirectoryByTag(File moviesBaseDirectory, String tag) throws IOException {
		Utility.checkMoviesBaseDirectory(moviesBaseDirectory);
		
		if (tag == null) throw new NullPointerException("tag is null");
		
		for (File movieDirectory : Utility.listMoviesDirectoriesFiles(moviesBaseDirectory)) {
			
			if (hasTag(movieDirectory, tag)) {
				
				System.out.println(movieDirectory.getName());
				
			}
			
		}
	}
	
	/**
	 * 
	 * @param movieDirectory
	 * @return
	 * @throws IOException
	 */
	public static JSONObject movieDirectoryJSONObject(File movieDirectory) throws IOException {
		if (movieDirectory == null) {
            throw new NullPointerException("directory must not be null");
        }
		if (!movieDirectory.exists()) {
			throw new IOException("Directory '" + movieDirectory + "' does not exist");
		}
		
		Collection<File> fileList = FileUtils.listFiles(movieDirectory, null, true);
		
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		jsonObject.put("contents", jsonArray);
		
		for (File f : fileList) {
			if (f.isFile()) {
				try {
					BigInteger size = FileUtils.sizeOfAsBigInteger(f);
					
					long checkSum = -1;
					
					if (size.compareTo(new BigInteger("1000000")) < 0) {
						checkSum = FileUtils.checksumCRC32(f);
					}
					
					JSONObject fileJsonObject = new JSONObject();
					Path path = f.toPath();
					path = path.subpath(1, path.getNameCount());
					fileJsonObject.put("path", path);
					fileJsonObject.put("checksum", checkSum);
					fileJsonObject.put("size", size);
					
					jsonArray.put(fileJsonObject);
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
		return jsonObject;
	}
	
	/**
	 * 
	 * @param directory
	 * @return
	 * @throws IOException
	 */
	public static JSONObject directoryContentJSONObject(File directory) throws IOException {
		if (directory == null) {
            throw new NullPointerException("directory must not be null");
        }
		if (!directory.exists()) {
			throw new IOException("Directory '" + directory + "' does not exist");
		}
		
		Collection<File> fileList = FileUtils.listFilesAndDirs(directory, TrueFileFilter.INSTANCE, null);
		
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		jsonObject.put("fileList", jsonArray);
		
		for (File f : fileList) {
			if (f.isDirectory()) {
				jsonArray.put(movieDirectoryJSONObject(f));
			}
		}
		
		System.out.println(JsonWriter.formatJson(jsonObject.toString()));
		
		return jsonObject;
	}
	
	/*
	public static void normalizeBackupTagsStartingWith(File moviesBaseDirectory, String tag, BigInteger maxSize) throws IOException {
		Utility.checkMoviesBaseDirectory(moviesBaseDirectory);
		
		if (tag == null) throw new NullPointerException("tag is null");
		
		if (maxSize == null) throw new NullPointerException("maxSize is null");
		
		// Prima conto quanto 
	}
	
	
	
	private void listFiles(File directory) {
		Collection<File> fileList = FileUtils.listFiles(directory, null, true);
		
		for (File f : fileList) {
			System.out.println(f);
			if (f.isFile()) {
				try {
					long checkSum = FileUtils.checksumCRC32(f);
					System.out.println("Checksum: " + checkSum);
					
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("pippo", f);
					System.out.println(jsonObject);
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	
	private void distributeBackupOnHardDisksTagging(String path, int hardDisksNumber) {
		String sw = "BACKUP:USBHD:";
		
		File[] subDirectories = Utility.listMoviesDirectoriesFiles(path);
		
		for (File movieDirectoryFile : subDirectories) {
			Random rand = new Random();
		    int randomNum = rand.nextInt((hardDisksNumber - 1) + 1) + 1;
			System.out.println(randomNum);
			
			String tag = "BACKUP:USBHD:";
			if (randomNum >= 10 && randomNum < 100) tag += randomNum;
			if (randomNum >= 0 && randomNum < 10) tag += "0" + randomNum;
			
			boolean doTag = true;
			
			File[] descriptorFiles = Utility.listDirectoriesWithPatternFiles(movieDirectoryFile.getPath(), Utility.IMDB_DESCRIPTOR_FILE_PATTERN_STRING);
			
			if (descriptorFiles.length == 1) {
				
				JSONObject jsonDescriptorObject = Utility.readJSON(descriptorFiles[0].getPath());
				
				if (jsonDescriptorObject.has("myData")) {
					JSONObject myData = jsonDescriptorObject.getJSONObject("myData");
					
					if (myData.has("groups")) {
						JSONArray jsonGroupsArray = myData.getJSONArray("groups");
						
						for (Object s : jsonGroupsArray) {
							String group = (String) s;
							System.out.println(group);
							
							if (group.startsWith(sw)) {
								System.out.println(group);
								doTag = false;
								continue;
							}
						}
						
						jsonGroupsArray.put(tag);
					} else {
						List<String> groupsList = new ArrayList<String>();
						groupsList.add(tag);
						myData.put("groups", groupsList);
					}
					
				}
				
				if (doTag) {
					System.out.println(jsonDescriptorObject);
					Utility.writeJSONObjectToFile(jsonDescriptorObject, descriptorFiles[0].getPath());
				}
				
				
			} else if (descriptorFiles.length > 1) {
				System.out.println("Troppi descrittori nella directory");
			} else if (descriptorFiles.length < 1) {
				System.out.println("Nessun descrittore nella directory");
			}
			
		}
		
	}
	*/
	
	
	
	/**
	 * Copia una directory Movie in un'altra posizione.
	 * 
	 * @param fromDirectory
	 * @param toDirectory
	 * @param checkCRC
	 * @return
	 * @throws IOException
	 */
	private static boolean copyMovieDirectory(File fromDirectory, File toDirectory, boolean checkCRC) throws IOException {
		if (fromDirectory == null) {
            throw new NullPointerException("fromDirectory must not be null");
        }
		if (toDirectory == null) {
            throw new NullPointerException("toDirectory must not be null");
        }
		if (!fromDirectory.exists()) {
			throw new IOException("Directory '" + fromDirectory + "' does not exist");
		}
		if (!fromDirectory.isDirectory()) {
			throw new IOException("fromDirectory is not a directory");
		}
		if (toDirectory.exists() && !toDirectory.isDirectory()) {
			throw new IOException("toDirectory is not a directory");
		}
		
		
		Utility.getDiff(fromDirectory, toDirectory);
		
		
		File parentDirectory = toDirectory.getParentFile();
		
		boolean directoryJustcreated = false;
		if (!toDirectory.exists()) {
			directoryJustcreated = toDirectory.mkdir();
		}
		
		boolean copyDirectory = false;
		
		BigInteger fromDirectorySize = FileUtils.sizeOfDirectoryAsBigInteger(fromDirectory);
		if (fromDirectorySize.equals(BigInteger.ZERO)) {
			return false;
		}
		
		BigInteger toDirectorySize = FileUtils.sizeOfAsBigInteger(toDirectory);
		if (fromDirectorySize.compareTo(toDirectorySize) == 0) {
			
			System.out.println("Le directory 'from' e 'to' occupano lo stesso spazio in byte.");
			return false;
			
		} else {
			
			// Copio solo se c'� spazio.
			long usableSpace = parentDirectory.getUsableSpace();
			usableSpace += toDirectorySize.longValue();
			
			if (usableSpace > fromDirectorySize.longValue()) {
			
				if (!directoryJustcreated) {
					System.out.println("Cancello la cartella " + toDirectory);
					FileUtils.deleteDirectory(toDirectory);
				}
				
				copyDirectory = true;
				
			} else {
				
				throw new IOException("Spazio insufficiente per aggiungere questa directory.");
			}
		}
		
		if (copyDirectory) {
			try {
				System.out.println("Copia della cartella: " + fromDirectory.getName());
				long usableSpace = parentDirectory.getUsableSpace();
				//long freeSpace = parentDirectory.getFreeSpace();
				System.out.println("Usable space: " + usableSpace);
				if (usableSpace > fromDirectorySize.longValue()) {
					FileUtils.copyDirectory(fromDirectory, toDirectory);
				} else {
					throw new IOException("Spazio insufficiente per aggiungere questa directory.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return copyDirectory;
	}
	
	/**
	 * Performs backup.
	 * 
	 * @param fromDirectory
	 * @param toDirectory
	 * @param backupTag
	 * @throws IOException
	 */
	public static void doBackup(File fromDirectory, File toDirectory, String backupTag, boolean reallyDoIt) throws IOException {
		if (fromDirectory == null) {
            throw new NullPointerException("fromDirectory must not be null");
        }
		if (toDirectory == null) {
            throw new NullPointerException("toDirectory must not be null");
        }
		if (!fromDirectory.exists()) {
			throw new IOException("Directory '" + fromDirectory + "' does not exist");
		}
		
		if (!toDirectory.exists()) {
			toDirectory.mkdir();
		}
		if (fromDirectory.equals(toDirectory)) {
			throw new IOException("le cartelle di origine e destinazione coincidono, impossibile procedere");
		}
		
		File[] fromMovieDirectoryList = Utility.listMoviesDirectoriesFiles(fromDirectory);
		File[] toMovieDirectoryList = Utility.listMoviesDirectoriesFiles(toDirectory);
		
		BigInteger totalSize = new BigInteger("0");
		
		ArrayList<File> fromMovieDirectoryArrayList = new ArrayList<File>();
		for (File fromMovieDirectory : fromMovieDirectoryList) {
			
			if (!fromMovieDirectory.isDirectory()) continue;
			
			JSONObject jsonDescriptor = Utility.getDescriptorJson(fromMovieDirectory);
			if (jsonDescriptor != null && jsonDescriptor.has("myData")) {
				JSONObject myData = jsonDescriptor.getJSONObject("myData");
				
				if (myData.has("groups")) {
					
					JSONArray jsonGroupsArray = myData.getJSONArray("groups");
					
					for (Object s : jsonGroupsArray) {
						String group = (String) s;
						
						if (group.equals(backupTag)) {
							fromMovieDirectoryArrayList.add(fromMovieDirectory);
							
							BigInteger x = FileUtils.sizeOfAsBigInteger(fromMovieDirectory);
							System.out.println("Aggiungo al backup " + fromMovieDirectory.getName() + ": " + x);
							totalSize = totalSize.add(x);
							
							break;
						}
					}
					
					
				}
				
			}
			
			
		}
		
		// Cancello le cartelle di toDirectory che non serviranno.
		/*
		for (File toMovieDirectory : toMovieDirectoryList) {
			
			boolean deleteDirectory = true;
			for (File fromMovieDirectory : fromMovieDirectoryArrayList) {
				
				if (toMovieDirectory.getName().equals(fromMovieDirectory.getName())) {
					deleteDirectory = false;
					break;
				}
				
			}
			
			if (deleteDirectory) {
				System.out.println("Cancello la cartella " + toMovieDirectory);
				try {
					FileUtils.deleteDirectory(toMovieDirectory);
				} catch (IOException ioe) {
				}
			}
			
		}*/
		
		System.out.println("Total size of backup: " + totalSize);
		
		if (reallyDoIt) {
		
			if (totalSize.compareTo(new BigInteger("1000000000000")) < 0) {
				for (File fromMovieDirectory : fromMovieDirectoryArrayList) {
					File toMovieDirectory = new File(toDirectory, fromMovieDirectory.getName());
					MoviesBackup.copyMovieDirectory(fromMovieDirectory, toMovieDirectory, false);
				}
			} else {
				throw new IOException("Il totale delle cartelle supera 1 TB.");
			}
			
		}
		
		
	}
	
	public static File[] orderByPriority(File[] movieDirectoriesArray) throws IOException {
		
		File[] temp = new File[movieDirectoriesArray.length];
		JSONObject[] jsonObject = new JSONObject[movieDirectoriesArray.length];
		int[] priorities = new int[movieDirectoriesArray.length];
		
		
		for (int i = 0; i < movieDirectoriesArray.length; i++) {
			jsonObject[i] = Utility.getDescriptorJson(movieDirectoriesArray[i]);
			
			if (jsonObject[i] != null && jsonObject[i].has("myData")) {
				
				JSONObject myData = jsonObject[i].getJSONObject("myData");
				if (myData.has("priority")) {
					priorities[i] =  myData.getInt("priority");
				} else {
					priorities[i] = 29;
				}
			} else {
				priorities[i] = 29;
			}
		}
		
		int index = 0;
		for (int p = 1; p <= 99; p++) {
			System.out.println(p + ":");
			for (int i = 0; i < movieDirectoriesArray.length; i++) {
				if (priorities[i] == p) {
					System.out.println(" :" + i);
					temp[index] = movieDirectoriesArray[i];
					index++;
				}
			}
		}
		return temp;
	}
	
	public static void compactSlices(File moviesBaseDirectory) throws IOException {
		Utility.checkMoviesBaseDirectory(moviesBaseDirectory);
		
		BigInteger totalSize = new BigInteger("0");
		BigInteger thresholdSize = new BigInteger("1000000000000");
		int sliceNumber = 1;
		File[] listMoviesDirectoriesFiles = Utility.listMoviesDirectoriesFiles(moviesBaseDirectory);
		File[] listMoviesDirectoriesFilesPriority = orderByPriority(listMoviesDirectoriesFiles);
		for (File movieDirectory : listMoviesDirectoriesFilesPriority) {
			BigInteger x = FileUtils.sizeOfAsBigInteger(movieDirectory);
			System.out.println(x);
			
			removeTagsStartingWith(movieDirectory, "BACKUP:SLICE:");
			
			totalSize = totalSize.add(x);
			if (totalSize.compareTo(thresholdSize) > 0) {
				// Se aggiungo supero il threshold
				sliceNumber++;
				totalSize =  new BigInteger("0").add(x);
			}
			
			String sliceNumberString = "";
			if (sliceNumber < 10) {
				sliceNumberString += "0" + sliceNumber;
			}
			if (sliceNumber >= 10 && sliceNumber < 100) {
				sliceNumberString += "" + sliceNumber;
			}
			
			addMovieTag(movieDirectory, "BACKUP:SLICE:" + sliceNumberString);
		}
	}
	
	public static void deprioritizeNotItalianLanguage(File moviesBaseDirectory) throws IOException {
		Utility.checkMoviesBaseDirectory(moviesBaseDirectory);
		
		for (File movieDirectory : Utility.listMoviesDirectoriesFiles(moviesBaseDirectory)) {
			String[] languages = Utility.showLanguagesByFileName(movieDirectory);
			
			if (Utility.getPriority(movieDirectory) < 0) {
			
				if (languages.length > 0) {
					boolean deprioritize = true;
					for (String language : languages) {
						
						if (language.equals("IT")) {
							deprioritize = false;
							break;
						}
						
					}
					
					if (deprioritize) {
						System.out.println(movieDirectory.getName() + ": 50");
						Utility.changePriority(movieDirectory, 50);
					}
					
				}
				
			}
		
		}
		
		
		
	}
	
	public static void verifySlice(File moviesBaseDirectory, String tag) throws IOException {
		Utility.checkMoviesBaseDirectory(moviesBaseDirectory);
		
		BigInteger totalSize = new BigInteger("0");
		BigInteger thresholdSize = new BigInteger("1000000000000");
		
		for (File movieDirectory : Utility.listMoviesDirectoriesFiles(moviesBaseDirectory)) {
			
			if (hasTag(movieDirectory, tag)) {
				BigInteger x = FileUtils.sizeOfAsBigInteger(movieDirectory);
				System.out.println("Add: " + x);
				totalSize = totalSize.add(x);
			}
			
		}
		
		System.out.println("Total size: " + totalSize);
	}
	
	
	public static void renameFilesAddingDirNameBaseDir(File moviesBaseDirectory) throws IOException {
		Utility.checkMoviesBaseDirectory(moviesBaseDirectory);
		
		for (File movieDirectory : Utility.listMoviesDirectoriesFiles(moviesBaseDirectory)) {
			
			for (File movieFile : Utility.listMovieFiles(movieDirectory)) {
				
				String pointedDirectoryName = movieDirectory.getName().replaceAll(" ", ".");
				if (!movieFile.getName().startsWith(pointedDirectoryName)) {
					File newFile = new File(movieDirectory, pointedDirectoryName + "{}{}" + movieFile.getName());
					
					if (newFile.getAbsolutePath().length() < 250) {
						System.out.println(newFile.getAbsolutePath());
						System.out.println("Nuovo nome: " + newFile.getName());
						movieFile.renameTo(newFile);
					}
				}
				
			}
			
		}
	}
	
	public static void renameFilesRemovingStringBaseDir(File moviesBaseDirectory) throws IOException {
		Utility.checkMoviesBaseDirectory(moviesBaseDirectory);
		
		for (File movieDirectory : Utility.listMoviesDirectoriesFiles(moviesBaseDirectory)) {
			
			for (File movieFile : Utility.listMovieFiles(movieDirectory)) {
				
				if (movieFile.getName().contains("bluray.1080p.x264")) {
					//String newString = movieFile.getName().replace("Ita.Eng", "EN-IT");
					String newString = movieFile.getName();
					File newFile = new File(movieDirectory, newString);
					System.out.println(newFile.getAbsolutePath());
					//movieFile.renameTo(newFile);
				}
				
			}
			
		}
	}
	
	public static void moveFilesInOthersFolder(File moviesBaseDirectory) throws IOException {
		Utility.checkMoviesBaseDirectory(moviesBaseDirectory);
		
		for (File movieDirectory : Utility.listMoviesDirectoriesFiles(moviesBaseDirectory)) {
			
			File othersFolder = new File(movieDirectory, "_others_");
			if (!othersFolder.exists()) {
				othersFolder.mkdir();
			}
			
			File[] myFiles = movieDirectory.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File current, String name) {
					
					File f = new File(current, name);
					boolean accept = f.isFile() && f.getName().toUpperCase().endsWith(".IDX");
					
					
					
					return accept;
				}
				
			});
			
			for (File f : myFiles) {
				f.renameTo(new File(othersFolder, f.getName()));
			}
			
		}
	}
	
	
	
	public static void ripristinaDalCasino() {
		File baseDir = new File("Z:\\+Video\\+Movies\\IT\\__A__\\");
		
		File[] movieFiles = baseDir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File current, String name) {
				
				File f = new File(current, name);
				boolean accept = f.isFile();
				
				return accept;
			}
			
		});
		Arrays.sort(movieFiles);
		
		
		File[] movieDirectories = baseDir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File current, String name) {
				
				File f = new File(current, name);
				boolean accept = f.isDirectory();
				
				return accept;
			}
			
		});
		Arrays.sort(movieDirectories);
		
		for (File movieFile : movieFiles) {
			//System.out.println(movieFile.getName());
			
			for (File movieDirectory : movieDirectories) {
				
				String pointedDirectoryName = movieDirectory.getName().replaceAll(" ", ".");
				
				if (movieFile.getName().startsWith(pointedDirectoryName)) {
					System.out.println(movieDirectory.getName() + " --- " + movieFile.getName());
					movieFile.renameTo(new File(movieDirectory, movieFile.getName()));
					//System.out.println(new File(movieDirectory, movieFile.getName()));
				}
				
			}
			
		}
	}
	

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		moveFilesInOthersFolder(new File(Utility.BASE_DIRECTORY));
		/*
		MoviesBackup mb = new MoviesBackup();
		
		//String fromDirectoryName = "Z:\\+Video\\+Movies\\IT\\__A__\\";
		String toDirectoryName = "E:\\";
		//String fromDirectoryName = "movies_dir";
		//String toDirectoryName = "movies_dir2";
		
		//File fromDirectory = new File(fromDirectoryName);
		File toDirectory = new File(toDirectoryName);
		
		//mb.copyDirectory(fromDirectory, toDirectory);
		//mb.listMoviesDirectory(toDirectoryName);
		
		//mb.resetAllDescriptorsTags(fromDirectoryName);
		//mb.addTagToAllDescriptors(fromDirectoryName, "BACKUP:USBHD:01");
		//mb.resetAllDescriptorsTagsStartingWith(fromDirectoryName, "BACKUP:USBHD:");
		//mb.addTagToDefinedDescriptors(new String[]{"tt0078748"}, fromDirectoryName, "PROVATAG11");
		
		//mb.resetAllDescriptorsTagsStartingWith(fromDirectoryName, "BACKUP:USBHD:");
		
		//mb.distributeBackupOnHardDisksTagging(fromDirectoryName, 64);
		
		try {
			// L'ultimo backup che ho fatto
			
			
			MoviesBackup.verifySlice(new File(Utility.BASE_DIRECTORY), "BACKUP:SLICE:01");
			MoviesBackup.verifySlice(new File(Utility.BASE_DIRECTORY), "BACKUP:SLICE:02");
			MoviesBackup.verifySlice(new File(Utility.BASE_DIRECTORY), "BACKUP:SLICE:03");
			MoviesBackup.verifySlice(new File(Utility.BASE_DIRECTORY), "BACKUP:SLICE:04");
			
			System.exit(0);
			
			MoviesBackup.doBackup(new File(Utility.BASE_DIRECTORY), toDirectory, "BACKUP:SLICE:01", true);
			MoviesBackup.doBackup(new File(Utility.BASE_DIRECTORY), toDirectory, "BACKUP:SLICE:02", true);
			MoviesBackup.doBackup(new File(Utility.BASE_DIRECTORY), toDirectory, "BACKUP:SLICE:03", true);
			MoviesBackup.doBackup(new File(Utility.BASE_DIRECTORY), toDirectory, "BACKUP:SLICE:04", true);
			
			
			for (File movieDirectory : Utility.listMoviesDirectoriesFiles(fromDirectory)) {
				
				
				if (hasTag(movieDirectory, "BACKUP:USBHD:02")) {
					
					Utility.changePriority(movieDirectory, 2);
					
				}
				
			}
			
			
			
			//MoviesBackup.compactSlices(fromDirectory);
			
			//MoviesBackup.deprioritizeNotItalianLanguage(fromDirectory);
			
			//File moviesBaseDirectory = new File("/media/rabbit/USBHD-01");
			
			//MoviesBackup.addMovieTagBaseDir(moviesBaseDirectory, "BACKUP:EXTHD:01");
			//MoviesBackup.printMoviesDirectoryByTag(fromDirectory, "BACKUP:SLICE:10");
			
			//MoviesBackup.removeTagsStartingWithBaseDir(fromDirectory, "BACKUP:USBHD:");
			
			
			
			for (File f : Utility.listMoviesDirectoriesFiles("/media/rabbit/USBHD-01")) {
				
				String[] sArray = Utility.showLanguagesByFileName(f);
				
				System.out.println(f);
				
				for (String s : sArray) {
					System.out.println(s);
				}
				
				System.out.println("---");
				
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}

}
