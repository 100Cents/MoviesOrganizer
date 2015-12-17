import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.json.*;
import org.pdfclown.documents.*;
import org.pdfclown.documents.contents.LineCapEnum;
import org.pdfclown.documents.contents.colorSpaces.DeviceRGBColor;
import org.pdfclown.documents.contents.composition.*;
import org.pdfclown.documents.contents.fonts.Font;
import org.pdfclown.documents.interaction.viewer.ViewerPreferences;
import org.pdfclown.documents.interchange.metadata.Information;
import org.pdfclown.files.File;
import org.pdfclown.files.SerializationModeEnum;
import org.pdfclown.tools.PageStamper;

public class PdfCatalogWriter {
	
	private static final double MARGIN = 30;
	private static final double MARGIN_TOP = 20;
	
	private ArrayList<Movie> moviesList = new ArrayList<Movie>();
	
	public PdfCatalogWriter(java.io.File baseMoviesDirectory) throws IOException {
		if (baseMoviesDirectory == null) {
			throw new NullPointerException("baseMoviesDirectory can't be null");
		}
		if (!baseMoviesDirectory.exists()) {
			throw new IOException("directory baseMoviesDirectory does not exist");
		}
		
		java.io.File[] movieDirectories = Utility.listMoviesDirectoriesFiles(baseMoviesDirectory);
		
		Arrays.sort(movieDirectories);
		
		int descriptorAnomalies = 0;
		
		for (java.io.File movieDirectory : movieDirectories) {
			
			java.io.File descriptorFile = Utility.getDescriptorFile(movieDirectory);
			if (descriptorFile != null) {
				
				JSONObject jsonObject = Utility.readJSON(descriptorFile);
				
				if (jsonObject == null) {
					System.out.println("Il descrittore non contiene dati: " + movieDirectory);
					
					jsonObject = Utility.getJsonFromDirectoryName(movieDirectory, descriptorFile.getName());
					
					Utility.writeJSONObjectToFile(jsonObject, descriptorFile, true);
				}
				
				Movie movie = new Movie(jsonObject);
				movie.setFolderName(movieDirectory.getName());
				try {
					movie.setRealAvailableLanguages(Utility.showLanguagesByFileName(movieDirectory));
					movie.setAllFileNamesStartsWithDirectoryName(
							Utility.allFileNamesStartsWithDirectoryName(movieDirectory));
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
		System.out.println("Descriptor anomalies: " + descriptorAnomalies);
	}

	public File composePdf() {
		
		int startY = 40;
		
		int rowsPerPage = 20;
		double distance = 38.5;
		
		File pdfFile = new File();
		Document document = pdfFile.getDocument();
		
		Font bodyFont = Font.get(document, new java.io.File("fonts", "Cousine-Regular.ttf"));
		Font cuprumBoldFont = Font.get(document, new java.io.File("fonts", "Cuprum-Bold.ttf"));
		Font cuprumRegularFont = Font.get(document, new java.io.File("fonts", "Cuprum-Regular.ttf"));

		Pages pages = document.getPages();
		
		PrimitiveComposer composer = null;
		
		for (int r = 0; r < moviesList.size(); r++) {
			
			Movie movie = moviesList.get(r);
			
			Page page = null;
			Dimension2D pageSize = PageFormat.getSize(PageFormat.SizeEnum.A4, PageFormat.OrientationEnum.Portrait);
			
			if (r % rowsPerPage == 0) {
				
				page = new Page(document, pageSize);
				pages.add(page);
				
				composer = new PrimitiveComposer(page);
			}
			
			if (movie.getMovieTitle() != null) {
				String originalTitle = movie.getMovieTitle().getOriginalTitle();
				String translatedTitle = movie.getMovieTitle().getTranslatedTitle();
				
				String text = movie.getMovieTitle().getTitleForCatalog();
				
				if (!movie.isAllFileNamesStartsWithDirectoryName()) {
					text += " *";
				}
				
				composer.setFont(cuprumBoldFont, 7);
				composer.setFillColor(new DeviceRGBColor(0, 0, 0));
				//System.out.println(text);
				composer.showText(text, new Point2D.Double(0 + MARGIN, (distance*(r % rowsPerPage)) + startY));
				composer.flush();
				
				composer.setLineWidth(0.05d);
				composer.setStrokeColor(new DeviceRGBColor(0, 0, 0));
				
				composer.setLineCap(LineCapEnum.Square);
				composer.drawLine(new Point2D.Double(MARGIN, (distance*(r % rowsPerPage)) + startY + 7.5), new Point2D.Double(pageSize.getWidth() - MARGIN, (distance*(r % rowsPerPage)) + startY + 7.5));
				
				composer.stroke();
				composer.flush();
				
				
				composer.setFont(cuprumBoldFont, 6);
				composer.setFillColor(new DeviceRGBColor(0, 0, 0));
				
				composer.showText("Titolo originale", new Point2D.Double(5 + MARGIN, (distance*(r % rowsPerPage)) + startY + 9));
				composer.setFont(cuprumRegularFont, 6);
				if (originalTitle != null) {
					composer.showText(originalTitle, new Point2D.Double(50 + MARGIN, (distance*(r % rowsPerPage)) + startY + 9));
				} else {
					composer.showText("-", new Point2D.Double(50 + MARGIN, (distance*(r % rowsPerPage)) + startY + 9));
					
				}
				composer.flush();
				
				// Lingue disponibili
				String languages = "";
				String[] array = movie.getRealAvailableLanguages();
				if (array != null && array.length >= 1) {
					for (int i = 0; i < ((array.length) - 1); i++) {
						languages += array[i] + "-";
					}
					languages += array[array.length - 1];
				}
				
				composer.setFont(cuprumRegularFont, 6);
				if (languages != null) {
					System.out.println(languages);
					composer.showText(languages, new Point2D.Double((pageSize.getWidth() - MARGIN), (distance*(r % rowsPerPage)) + startY + 9), XAlignmentEnum.Right, YAlignmentEnum.Top, 0);
					//composer.showText(languages, new Point2D.Double(450 + startX, (distance*(r % rowPerPage)) + startY + 9), XAlignmentEnum.Right, YAlignmentEnum.Top, 0);
				}
				composer.flush();
				
				
				// Titolo tradotto
				composer.setFont(cuprumBoldFont, 6);
				composer.showText("Titolo tradotto", new Point2D.Double(5 + MARGIN, (distance*(r % rowsPerPage)) + startY + 16));
				composer.setFont(cuprumRegularFont, 6);
				if (translatedTitle != null) {
					
					if (originalTitle != null && originalTitle.equals(translatedTitle)) {
						composer.showText("-", new Point2D.Double(50 + MARGIN, (distance*(r % rowsPerPage)) + startY + 16));
					} else {
						composer.showText(translatedTitle, new Point2D.Double(50 + MARGIN, (distance*(r % rowsPerPage)) + startY + 16));
					}
					
				} else {
					composer.showText("-", new Point2D.Double(50 + MARGIN, (distance*(r % rowsPerPage)) + startY + 16));
				}
				composer.flush();
				
				// tag
				String tagString = "";
				String[] tags = movie.getAllTags();
				if (tags != null && tags.length > 0) {
					for (int i = 0; i < tags.length - 1; i++) {
						tagString += tags[i] + " / ";
					}
					tagString += tags[tags.length - 1];
					if (tagString != null) {
						composer.setFont(bodyFont, 4);
						composer.showText(tagString, new Point2D.Double((pageSize.getWidth() - MARGIN), (distance*(r % rowsPerPage)) + startY + 16 + 1.7), XAlignmentEnum.Right, YAlignmentEnum.Top, 0);
					}
					composer.flush();
				}
				
				composer.setFont(cuprumBoldFont, 6);
				String imdbCode = movie.getImdbCode();
				composer.setFont(cuprumRegularFont, 6);
				if (imdbCode != null) {
					//composer.showText(imdbCode, new Point2D.Double(50 + startX, (distance*(r % rowPerPage)) + startY + 23));
					composer.showText(imdbCode, new Point2D.Double((pageSize.getWidth() - MARGIN), (distance*(r % rowsPerPage)) + startY), XAlignmentEnum.Right, YAlignmentEnum.Top, 0);
				}
				composer.flush();
				
				composer.setFont(cuprumBoldFont, 6);
				double dirY = (distance*(r % rowsPerPage)) + startY + 23;
				composer.showText("Directory", new Point2D.Double(5 + MARGIN, dirY));
				String folderName = movie.getFolderName();
				
				if (folderName != null) {
					composer.setFont(bodyFont, 4);
					composer.showText(folderName, new Point2D.Double(50 + MARGIN, dirY + 1.7));
				} else {
					composer.setFont(cuprumRegularFont, 6);
					composer.showText("-", new Point2D.Double(50 + MARGIN, dirY));
				}
				composer.flush();
				
			} else {
				
				String folderName = movie.getFolderName();
				if (folderName != null) {
				
					composer.setFont(cuprumBoldFont, 7);
					composer.setFillColor(new DeviceRGBColor(0, 0, 0));
					composer.showText(folderName, new Point2D.Double(0 + MARGIN, (distance*(r % rowsPerPage)) + startY));
					composer.flush();
					
					composer.setLineWidth(0.05d);
					composer.setStrokeColor(new DeviceRGBColor(0, 0, 0));
					
					composer.setLineCap(LineCapEnum.Square);
					composer.drawLine(new Point2D.Double(MARGIN, (distance*(r % rowsPerPage)) + startY + 7.5), new Point2D.Double(pageSize.getWidth() - MARGIN, (distance*(r % rowsPerPage)) + startY + 7.5));
					
					composer.stroke();
					composer.flush();
					
					composer.setFont(bodyFont, 6);
					composer.setFillColor(new DeviceRGBColor(0, 0, 0));
						
					String text2 = "Directory       : " + folderName;
					composer.showText(text2, new Point2D.Double(5 + MARGIN, (distance*(r % rowsPerPage)) + startY + 9));
					composer.flush();
					
				} else {
					
					String text2 = "Directory       : -";
					composer.showText(text2, new Point2D.Double(5 + MARGIN, (distance*(r % rowsPerPage)) + startY + 9));
					composer.flush();
					
				}
			}
			
			
			
			
			
		}
		
		composer.flush();
		
		// Aggiungo i numeri di pagina.
		
		/* NOTE: The PageStamper is optimized for dealing with pages. */
		PageStamper stamper = new PageStamper();
		
		for (Page page : document.getPages()) {
			// 1. Associate the page to the stamper!
			stamper.setPage(page);

			// 2. Stamping the page number on the foreground...
			PrimitiveComposer foreground = stamper.getForeground();

			foreground.setFont(cuprumBoldFont, 10);
			
			String title = "Catalogo stampato il " + Utility.MY_DATE_FORMAT.format(Utility.NOW_DATE) + " alle ore " + Utility.MY_TIME_FORMAT.format(Utility.NOW_DATE);
			
			foreground.showText(title, new Point2D.Double(MARGIN, startY - 20));

			Dimension2D pageSize = page.getSize();
			int pageNumber = page.getIndex() + 1;
			foreground.showText("Pagina " + pageNumber + " di " + document.getPages().size(),
					new Point2D.Double((pageSize.getWidth() - MARGIN), MARGIN_TOP), XAlignmentEnum.Right, YAlignmentEnum.Top, 0);
			
			// 3. End the stamping!
			stamper.flush();
		}
		
		return pdfFile;
	}

	protected String serialize(File file) {
		return serialize(file, null, null, null);
	}

	/**
	 * Serializes the given PDF Clown file object.
	 * 
	 * @param file PDF file to serialize.
	 * @param serializationMode Serialization mode.
	 * @return Serialization path.
	 */
	protected String serialize(File file, SerializationModeEnum serializationMode) {
		return serialize(file, serializationMode, null, null, null);
	}

	/**
	 * Serializes the given PDF Clown file object.
	 * 
	 * @param file PDF file to serialize.
	 * @param fileName Output file name.
	 * @return Serialization path.
	 */
	protected String serialize(File file, String fileName) {
		return serialize(file, fileName, null, null);
	}

	/**
	 * Serializes the given PDF Clown file object.
	 * 
	 * @param file PDF file to serialize.
	 * @param fileName Output file name.
	 * @param serializationMode Serialization mode.
	 * @return Serialization path.
	 */
	protected String serialize(File file, String fileName, SerializationModeEnum serializationMode) {
		return serialize(file, fileName, serializationMode, null, null, null);
	}

	/**
	 * Serializes the given PDF Clown file object.
	 * 
	 * @param file PDF file to serialize.
	 * @param title Document title.
	 * @param subject Document subject.
	 * @param keywords Document keywords.
	 * @return Serialization path.
	 */
	protected String serialize(File file, String title, String subject, String keywords) {
		return serialize(file, null, title, subject, keywords);
	}

	/**
	 * Serializes the given PDF Clown file object.
	 * 
	 * @param file PDF file to serialize.
	 * @param serializationMode Serialization mode.
	 * @param title Document title.
	 * @param subject Document subject.
	 * @param keywords Document keywords.
	 * @return Serialization path.
	 */
	protected String serialize(File file, SerializationModeEnum serializationMode, String title, String subject, String keywords) {
		return serialize(file, getClass().getSimpleName(), serializationMode, title, subject, keywords);
	}

	/**
	 * Serializes the given PDF Clown file object.
	 * 
	 * @param file PDF file to serialize.
	 * @param fileName Output file name.
	 * @param serializationMode Serialization mode.
	 * @param title Document title.
	 * @param subject Document subject.
	 * @param keywords Document keywords.
	 * @return Serialization path.
	 */
	protected String serialize(File file, String fileName, SerializationModeEnum serializationMode, String title, String subject, String keywords) {
		applyDocumentSettings(file.getDocument(), title, subject, keywords);
		
		java.io.File outputFile = new java.io.File("catalogs", fileName);
		
		try {
			file.save(outputFile, serializationMode);
		} catch(Exception e) {
			System.out.println("File writing failed: " + e.getMessage());
			e.printStackTrace();
		}
		System.out.println("Output: " + outputFile.getPath());

		return outputFile.getPath();
	}


	private void applyDocumentSettings(Document document, String title, String subject, String keywords) {
		if (title == null) {
			return;
		}

		// Viewer preferences.
		ViewerPreferences view = new ViewerPreferences(document); // Instantiates viewer preferences inside the document context.
		document.setViewerPreferences(view); // Assigns the viewer preferences object to the viewer preferences function.
		view.setDisplayDocTitle(true);

		// Document metadata.
		Information info = document.getInformation();
		info.clear();
		//info.setAuthor("Stefano Chizzolini");
		info.setCreationDate(new Date());
		//info.setCreator(getClass().getName());
		info.setTitle(title);
		//info.setSubject("Sample about " + subject + " using PDF Clown");
		//info.setKeywords(keywords);
	}
	
	public static void main(String[] args) {
		
		try {
			PdfCatalogWriter pdfCatalogWriter = new PdfCatalogWriter(new java.io.File(Utility.BASE_DIRECTORY));
			File pdfFile = pdfCatalogWriter.composePdf();
			
			SimpleDateFormat nowDateFormat = new SimpleDateFormat("yyyyMMdd");
			String now = nowDateFormat.format(new Date());
			
			String fileName = "catalog-" + now + ".pdf";
			pdfCatalogWriter.serialize(pdfFile, fileName, SerializationModeEnum.Standard, "Movies catalog", "Movies catalog", null);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
}