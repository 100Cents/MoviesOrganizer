package hcents.lifefolders.video.tts.view;

public class SearchEvent {
	
	private String searchString;

	/**
	 * @param searchString
	 */
	public SearchEvent(String searchString) {
		this.setSearchString(searchString);
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

}
