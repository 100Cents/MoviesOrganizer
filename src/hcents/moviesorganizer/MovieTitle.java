package hcents.moviesorganizer;


import org.apache.commons.lang3.text.WordUtils;

public class MovieTitle {
	private String originalTitle;
	private String translatedTitle;
	private int year;
	
	/**
	 * @param originalTitle
	 * @param translatedTitle
	 * @param year
	 */
	public MovieTitle(String originalTitle, String translatedTitle, int year) {
		super();
		
		if (originalTitle == null && translatedTitle == null) throw new NullPointerException();
		
		this.originalTitle = originalTitle;
		this.translatedTitle = translatedTitle;
		this.year = year;
	}
	
	/**
	 * @return the originalTitle
	 */
	public String getOriginalTitle() {
		return originalTitle;
	}
	

	public String getOriginalTitleCapitalized() {
		String r = WordUtils.capitalizeFully(originalTitle);
		r = r.trim();
		
		for (int i = 0; i < originalTitle.length(); i++) {
			char c = originalTitle.charAt(i);
			String myChar = c + "";
			if (myChar.equals(myChar.toUpperCase())) {
				// Il carattere � maiuscolo e lo voglio maiuscolo anche in r
				if (i == 0) {
					r = myChar + r.substring(1);
				}
				if (i > 0) {
					r = r.substring(0, i) + myChar + r.substring(i + 1);
				}
			}
		}
		
		r = r.replaceAll("\\?", "_");
		r = r.replaceAll(":", "_");
		
		return r;
	}
	
	/**
	 * @param originalTitle the originalTitle to set
	 */
	public void setOriginalTitle(String originalTitle) {
		this.originalTitle = originalTitle;
	}
	
	
	

	/**
	 * @return the translatedTitle
	 */
	public String getTranslatedTitle() {
		return translatedTitle;
	}
	
	public String getTranslatedTitleCapitalized() {
		if (translatedTitle != null) {
			String r = WordUtils.capitalizeFully(translatedTitle);
			r = r.trim();
			
			for (int i = 0; i < translatedTitle.length(); i++) {
				char c = translatedTitle.charAt(i);
				String myChar = c + "";
				if (myChar.equals(myChar.toUpperCase())) {
					// Il carattere � maiuscolo e lo voglio maiuscolo anche in r
					if (i == 0) {
						r = myChar + r.substring(1);
					}
					if (i > 0) {
						r = r.substring(0, i) + myChar + r.substring(i + 1);
					}
				}
			}
			
			r = r.replaceAll("\\?", "_");
			r = r.replaceAll(":", "_");
			
			return r;
		} else return null;
	}
	
	/**
	 * @param translatedTitle the translatedTitle to set
	 */
	public void setTranslatedTitle(String translatedTitle) {
		this.translatedTitle = translatedTitle;
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
	
	
	public String printPrettyTitle() {
		if (getTranslatedTitleCapitalized() == null) {
			return getOriginalTitleCapitalized() + " (" + year + ")";
		} else {
			if (getOriginalTitleCapitalized().equals(getTranslatedTitleCapitalized())) {
				return getOriginalTitleCapitalized() + " (" + year + ")";
			} else {
				return getOriginalTitleCapitalized() + " [" + getTranslatedTitleCapitalized() + "] (" + year + ")";
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MovieTitle [originalTitle=" + originalTitle + ", translatedTitle=" + translatedTitle + ", year=" + year
				+ "]";
	}
	
	public String getTitleForCatalog() {
		String originalTitle = getOriginalTitle();
		String translatedTitle = getTranslatedTitle();
		
		if (originalTitle != null) {
			if (translatedTitle != null) {
				if (originalTitle.equals(translatedTitle)) {
					return originalTitle + " (" + getYearString() + ")";
				} else {
					return originalTitle + " [" + translatedTitle + "] (" + getYearString() + ")";
				}
			} else {
				return originalTitle + " (" + getYearString() + ")";
			}
		} else {
			if (translatedTitle != null) {
				return translatedTitle + " (" + getYearString() + ")";
			}
		}
		return null;
	}
	
	public static String correctForDirectory(String s) {
		while (s.startsWith(".")) {
			s = s.substring(1);
		}
		s = s.replaceAll("\\?", "_");
		s = s.replaceAll(":", "_");
		s = s.replaceAll("/", "_");
		return s;
	}
	
	public String getYearString() {
		if (getYear() > 0) {
			return "" + getYear();
		} else {
			return "-";
		}
	}
	
	public String getTitleForDirectory() {
		String originalTitle = getOriginalTitleCapitalized();
		String translatedTitle = getTranslatedTitleCapitalized();
		
		if (originalTitle != null) {
			if (translatedTitle != null) {
				if (originalTitle.equals(translatedTitle) || translatedTitle.equals("")) {
					return correctForDirectory(originalTitle + " (" + getYearString() + ")");
				} else {
					return correctForDirectory(originalTitle + " [" + translatedTitle + "] (" + getYearString() + ")");
				}
			} else {
				return correctForDirectory(originalTitle + " (" + getYearString() + ")");
			}
		} else {
			if (translatedTitle != null && !translatedTitle.equals("")) {
				return correctForDirectory(translatedTitle + " (" + getYearString() + ")");
			}
		}
		return null;
	}
	
	
}
