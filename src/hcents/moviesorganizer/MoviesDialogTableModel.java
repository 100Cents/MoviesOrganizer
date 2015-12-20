package hcents.moviesorganizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.json.JSONObject;

public class MoviesDialogTableModel extends AbstractTableModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9215449894434276016L;
	
	//private List<Movie> moviesList;
	private File[] moviesFiles = new File[0];

	public MoviesDialogTableModel() {
	}
	
	public synchronized void updateRows(File movieDirectory) throws IOException {
		moviesFiles = Utility.listMovieFiles(movieDirectory, true);
	}

	@Override
	public int getRowCount() {
		return moviesFiles.length;
	}

	@Override
	public int getColumnCount() {
		return 1;
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		
		switch (columnIndex) {
		
			case 0:
				return "File name";
				
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
				return moviesFiles[rowIndex].getName();
				
			default:
				return "";
		
		}
		
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		return true;
	}
	
	public File getMovieFileAtRow(int row) {
		return moviesFiles[row];
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