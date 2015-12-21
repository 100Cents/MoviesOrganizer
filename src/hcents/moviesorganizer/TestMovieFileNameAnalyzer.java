package hcents.moviesorganizer;

import java.io.File;
import java.io.IOException;

public class TestMovieFileNameAnalyzer {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		for (File f : Utility.listMovieFiles(new File(Utility.BASE_DIRECTORY + "/45 Years [45 Anni] (2015)"), true)) {
		
			MovieFileName mfn = new MovieFileName(f);
			
			System.out.println(mfn.getDottedAppendix());
			System.out.println(mfn.getLanguageSection());
			System.out.println(mfn.getTagsSection());
			System.out.println(mfn.getExtension());
			System.out.println(mfn.getGarbage());
		}
	
	}

}
