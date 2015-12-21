package hcents.moviesorganizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * We want every movie file name to be well formed. They should start with the
 * dotted appendix, continue with the language section and the the tags
 * section, if exists tags.
 * 
 * @author rabbit
 *
 */
public class MovieFileName {
	
	private File movieFile;
	private String originalName;
	private File parentDirectory;
	private String dottedAppendix;
	private String languageSection;
	private String tagsSection;
	private String garbage;
	private String extension;
	
	private List<String> languagesList = new ArrayList<String>();
	private List<String> tagsList = new ArrayList<String>();
	
	/**
	 * 
	 * @param movieFile
	 * @throws IOException
	 */
	public MovieFileName(File movieFile) throws IOException {
		if (movieFile == null) {
			throw new NullPointerException("movieFile can not be null");
		}
		if (!movieFile.exists()) {
			throw new IOException("movieFile does not exist");
		}
		if (!movieFile.isFile()) {
			throw new IOException("movieFile is not a file");
		}
		
		this.movieFile = movieFile;
		this.originalName = movieFile.getName();
		
		this.parentDirectory = movieFile.getParentFile();
		
		analyzeMovieFileName();
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean startsWithDottedAppendix() {
		return originalName.startsWith(dottedAppendix);
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDottedAppendix() {
		if (dottedAppendix == null) {
			dottedAppendix = parentDirectory.getName().replaceAll(" ", ".");
		}
		return dottedAppendix;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getLanguageSection() {
		return languageSection;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getTagsSection() {
		return tagsSection;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getGarbage() {
		return garbage;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getExtension() {
		return extension;
	}
	
	/**
	 * 
	 */
	private void analyzeMovieFileName() {
		
		this.extension = originalName.substring(originalName.length() - 4);
		String dottedAppendix = getDottedAppendix();
		
		if (originalName.startsWith(dottedAppendix)) {
			System.out.println("Il file inizia bene.");
			
			String movieFileNameNa = originalName.substring(dottedAppendix.length(), originalName.length() - 4);
			
			System.out.println(movieFileNameNa);
			
			Pattern pattern = Pattern.compile("\\{[A-Z\\-]*}");
			Matcher matcher = pattern.matcher(movieFileNameNa);
			
			while (matcher.find()) {
				System.out.println("I found the text " + matcher.group() + " starting at " + "index " + matcher.start() + " and ending at index " + matcher.end());
				
				String g = matcher.group();
				
				if (g.startsWith("{") && g.endsWith("}")) {
					
					this.languageSection = g;
					
					String n = g.substring(1, g.length() - 1);
					
					String[] tempStringArray = n.split("-");
					
					for (String s : tempStringArray) {
						if (s.length() > 0) {
							if (!languagesList.contains(s)) {
								languagesList.add(s);
							} else {
								throw new IllegalArgumentException("language duplicato");
							}
						}
					}
					
				}
			}
			
			Pattern pattern2 = Pattern.compile("\\{[a-z0-9\\._]*}");
			Matcher matcher2 = pattern2.matcher(movieFileNameNa);
			
			int lastEnd = 0;
			while (matcher2.find()) {
				System.out.println("I found the text " + matcher2.group() + " starting at " + "index " + matcher2.start() + " and ending at index " + matcher2.end());
				
				String g = matcher2.group();
				
				if (g.startsWith("{") && g.endsWith("}")) {
					
					lastEnd = matcher2.end();
					this.tagsSection = g;
					
					String n = g.substring(1, g.length() - 1);
					
					String[] tempStringArray = n.split(".");
					
					for (String s : tempStringArray) {
						if (s.length() > 0) {
							if (!tagsList.contains(s)) {
								tagsList.add(s);
							} else {
								throw new IllegalArgumentException("tag duplicato");
							}
						}
					}
					
				}
			}
			
			if (lastEnd > 0) {
				String lastPart = movieFileNameNa.substring(lastEnd);
				
				this.garbage = lastPart;
				
				if (lastPart.length() == 0) this.garbage = null;
			}
			
			
			
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public String[] getLanguagesList() {
		return languagesList.toArray(new String[] {});
	}
	
	/**
	 * 
	 * @return
	 */
	public String[] getTagsList() {
		return tagsList.toArray(new String[] {});
	}

}
