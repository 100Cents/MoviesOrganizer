package hcents.moviesorganizer;

import java.io.File;
import java.io.IOException;

public class PrintCatalogTest {
	
	public static void main(String[] args) throws IOException {
		
		Utility.makeClassicPdfCatalog(new File(Utility.BASE_DIRECTORY), "BACKUP:SLICE:02", "SLICE02");
		
	}
	
}
