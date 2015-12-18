package hcents.lifefolders.video.tts;
import java.io.*;
import java.util.*;
import java.util.regex.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.w3c.dom.*;


public class ListFolders {
	private static final String baseFolder = "/home/rabbit/Documents/movies/";
	private static final String baseXML = "/home/rabbit/Documents/movies/lf-catalog.xml";
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		File baseXMLFile = new File(baseXML);
		if (baseXMLFile.exists() && !baseXMLFile.isDirectory()) {
			System.out.println("XML esistente.");
		} else {
			System.out.println("XML da creare.");
			createXML();
		}
		
		File file = new File(baseFolder);
		
		String[] directories = file.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
			
		});
		
		for (String s : directories) {
			System.out.println(s);
		}
	}
	
	public static void createXML() {
        DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder;
        try {
            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();
            Element mainRootElement = doc.createElementNS("", "movies");
            doc.appendChild(mainRootElement);
            
            File file = new File(baseFolder);
    		
    		String[] directories = file.list(new FilenameFilter() {
    			
    			@Override
    			public boolean accept(File current, String name) {
    				return new File(current, name).isDirectory();
    			}
    			
    		});
    		
    		Pattern pattern1 = Pattern.compile("^(.*) \\[(.*)\\] \\((\\d*)\\)$");
    		Pattern pattern2 = Pattern.compile("^(.*) \\((\\d*)\\)$");
    		
    		for (String s : directories) {
    			
    			Matcher matcher1 = pattern1.matcher(s);
    			Matcher matcher2 = pattern2.matcher(s);
    			if (matcher1.find()) {
    			   System.out.printf("originalTitle:%s, itTitle:%s, year:%s%n", matcher1.group(1), matcher1.group(2), matcher1.group(3));
    			   mainRootElement.appendChild(getMovie(doc, "1", matcher1.group(1), matcher1.group(3)));
    			} else if (matcher2.find()) {
    				System.out.printf("originalTitle:%s, year:%s%n", matcher2.group(1), matcher2.group(2));
    				mainRootElement.appendChild(getMovie(doc, "1", matcher2.group(1), matcher2.group(2)));
     			}
    			
    			//mainRootElement.appendChild(getMovie(doc, "1", "Once Upon A Time In America", "1982"));
    			
    		}
 
            // append child elements to root element
    		/*
            mainRootElement.appendChild(getMovie(doc, "1", "Once Upon A Time In America", "1982"));
            mainRootElement.appendChild(getMovie(doc, "2", "eBay", "1980"));
            mainRootElement.appendChild(getMovie(doc, "3", "Google", "1956"));*/
 
            // output DOM XML to console
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult console = new StreamResult(System.out);
            transformer.transform(source, console);
 
            System.out.println("\nXML DOM Created Successfully..");
 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    private static Node getMovie(Document doc, String id, String originalTitle, String year) {
        Element movie = doc.createElement("movie");
        movie.setAttribute("id", id);
        movie.appendChild(getCompanyElements(doc, movie, "originalTitle", originalTitle));
        movie.appendChild(getCompanyElements(doc, movie, "year", year));
        return movie;
    }
 
    // utility method to create text node
    private static Node getCompanyElements(Document doc, Element element, String name, String value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }

}
