package hcents.moviesorganizer;
import java.io.File;


public class BackupElement {
	
	/**
	 * @param from
	 * @param to
	 * @param equals
	 * @param transferFrom
	 * @param deleteTo
	 */
	public BackupElement(File from, File to, boolean equals, boolean transferFrom, boolean deleteTo) {
		super();
		this.from = from;
		this.to = to;
		this.equals = equals;
		this.transferFrom = transferFrom;
		this.deleteTo = deleteTo;
	}

	public File getFrom() {
		return from;
	}

	public void setFrom(File from) {
		this.from = from;
	}

	public File getTo() {
		return to;
	}

	public void setTo(File to) {
		this.to = to;
	}

	public boolean isEquals() {
		return equals;
	}

	public void setEquals(boolean equals) {
		this.equals = equals;
	}

	public boolean isTransferFrom() {
		return transferFrom;
	}

	public void setTransferFrom(boolean transferFrom) {
		this.transferFrom = transferFrom;
	}

	public boolean isDeleteTo() {
		return deleteTo;
	}

	public void setDeleteTo(boolean deleteTo) {
		this.deleteTo = deleteTo;
	}

	private File from;
	private File to;
	
	private boolean equals;
	private boolean transferFrom;
	private boolean deleteTo;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}

}
