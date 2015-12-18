package hcents.moviesorganizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.json.JSONObject;

public class MoviesListTableModel extends AbstractTableModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9215449894434276016L;
	
	private List<Movie> moviesList;

	public MoviesListTableModel() {
		moviesList = new ArrayList<Movie>();
	}
	
	public Movie getMovieAtRow(int row) {
		return moviesList.get(row);
	}
	
	public synchronized void updateRows() throws IOException {
		for (File movieDirectory : Utility.listMoviesDirectoriesFiles(new File(Utility.BASE_DIRECTORY))) {
			File descriptorFile = Utility.getDescriptorFile(movieDirectory);
			if (descriptorFile != null) {
				
				JSONObject jsonObject = Utility.readJSONObjectFromFile(descriptorFile);
				if (jsonObject != null) {
					Movie movie = new Movie(jsonObject);
					movie.setMovieDirectory(movieDirectory);
					movie.setFolderName(movieDirectory.getName());
					try {
						movie.setRealAvailableLanguages(Utility.showLanguagesByFileName(movieDirectory));
						movie.setAllFileNamesStartsWithDirectoryName(
								Utility.allFileNamesStartsWithDirectoryName(movieDirectory));
						movie.setAllTags(MoviesBackup.getAllTags(movieDirectory));
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					if (!moviesList.contains(movie)) {
						moviesList.add(movie);
						System.out.println("add: " + movie);
						fireTableRowsInserted(moviesList.size() - 1, moviesList.size());
					}
				}
				
			}
			
		}
		
	}

	@Override
	public int getRowCount() {
		return moviesList.size();
	}

	@Override
	public int getColumnCount() {
		return 3;
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		
		switch (columnIndex) {
		
			case 0:
				return "IMDB";
				
			case 1:
				return "Original title";
				
			case 2:
				return "OTLC";
				
			default:
				return "";
		
		}
		
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}
	
	

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		switch (columnIndex) {
		
			case 0:
				return moviesList.get(rowIndex).getImdbCode();
				
			case 1:
				return moviesList.get(rowIndex).getMovieTitle().getTitleForCatalog();
				
			case 2:
				return moviesList.get(rowIndex).getOtlc();
				
			default:
				return "";
		
		}
		
	}

	/*
	public void addRow() {
		int rowCount = getRowCount();
		String[] row = new String[getColumnCount()];
		for (int index = 0; index < getColumnCount(); index++) {
			row[index] = rowCount + "x" + index;
		}
		rows.add(row);
		fireTableRowsInserted(rowCount, rowCount);
	}
	*/
}