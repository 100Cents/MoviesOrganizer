package hcents.lifefolders.video.tts.model;

import java.util.*;

public class MainFrameModel extends Observable {
	
	private ArrayList<String> moviesList = new ArrayList<String>();
	//private String selectedFileFromTree = null;

	public ArrayList<String> getMoviesList() {
		return moviesList;
	}

	public void setMoviesList(ArrayList<String> moviesList) {
		this.moviesList = moviesList;
		setChanged();
		notifyObservers(moviesList);
	}

	/*public void setSelectedFileFromTree(String selectedFileFromTree) {
		this.selectedFileFromTree = selectedFileFromTree;
	}*/

}