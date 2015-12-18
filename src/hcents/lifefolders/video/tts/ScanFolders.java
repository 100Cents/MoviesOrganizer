package hcents.lifefolders.video.tts;
import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class ScanFolders {
	private static final String baseDirName = "/home/rabbit/Documents/movies/";
	private static final String tempDirName = "_TEMP_/";
	
	private static final Pattern pattern1 = Pattern.compile("^(.*) \\[(.*)\\] \\((\\d*)\\)$");
	private static final Pattern pattern2 = Pattern.compile("^([^\\[\\]]*) \\((\\d*)\\)$");
	private static final Pattern pattern3 = Pattern.compile("^([^\\[\\]]*) \\{T\\} \\((\\d*)\\)$");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File tempDir = new File(baseDirName + tempDirName);
		if (tempDir.exists() && tempDir.isDirectory()) {
			System.out.println("Trovata la directory _TEMP_.");
			
			/* Nella directory _TEMP_ trovo directory e file, devo ciclare tra
			 * di essi e decidere cosa fare per ognuno.
			 * Se si tratta di directory c'è la possibilità che abbia già un nome dal
			 * quale posso dedurre i dati che mi servono per costruire il file MLInfo.txt. */
			String[] tempDirListOfDirectories = tempDir.list(new FilenameFilter() {
				
				@Override
				public boolean accept(File current, String name) {
					return new File(current, name).isDirectory();
				}
				
			});
			
			String[] tempDirListOfFiles = tempDir.list(new FilenameFilter() {
				
				@Override
				public boolean accept(File current, String name) {
					return new File(current, name).isFile();
				}
				
			});
			
			// Ciclo tra le directory.
			System.out.println("Escursione tra le directory.");
			for (String s : tempDirListOfDirectories) {
				/* Per le cartelle prima dovrei stampare a video tutto l'albero. */
				System.out.println(tempDirName);
				System.out.println(printDirectoryTree(baseDirName + tempDirName + s));
				
				System.out.println(FileUtils.sizeOfDirectory(new File(baseDirName + tempDirName + s)) + "bytes.");
				
				/* Dal titolo della cartella cerco di dedurre i dati. */
				String originalTitle = null, itTitle = null, year = null;
				
    			Matcher matcher1 = pattern1.matcher(s);
    			Matcher matcher2 = pattern2.matcher(s);
    			Matcher matcher3 = pattern3.matcher(s);
    			if (matcher1.find()) {
    				originalTitle = matcher1.group(1);
    				itTitle = matcher1.group(2);
    				year = matcher1.group(3);
    				System.out.printf("originalTitle:%s, itTitle:%s, year:%s%n", originalTitle, itTitle, year);
    			} 
    			if (matcher2.find()) {
    				originalTitle = matcher2.group(1);
    				year = matcher2.group(2);
    				System.out.printf("originalTitle:%s, year:%s%n", originalTitle, year);
     			}
    			if (matcher3.find()) {
    				itTitle = matcher3.group(1);
    				year = matcher3.group(2);
    				System.out.printf("itTitle:%s, year:%s%n", itTitle, year);
     			}
    			
    			/* Guardato l'albero chiedo se posso creare l'XML con i dati dedotti.
    			 */
    			
    			System.out.println("Continuare con i dati dedotti (y/n)? ");
    			Scanner reader = new Scanner(System.in);
    			char c = reader.findInLine(".").charAt(0);
    			
    			if (c == 'y') {
    				createXML(baseDirName + tempDirName + s + "/MLInfo.txt", originalTitle, itTitle, year);
    			}
			}
			
			
			
			// Ciclo tra i file.
			System.out.println("Escursione tra i file.");
			for (String s : tempDirListOfFiles) {
				System.out.println(s);
			}
			
		} else {
			System.out.println("La directory _TEMP_ non esiste. Impossibile procedere.");
		}
		
		
		
	}
	
	
	public static void createXML(String fileName, String originalTitle, String itTitle, String year) {
        DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder;
        try {
            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();
            Element mainRootElement = doc.createElementNS("", "movies");
            doc.appendChild(mainRootElement);
            
            File file = new File(fileName);
            
            mainRootElement.appendChild(getMovie(doc, "1", originalTitle, itTitle, year));
 
            // output DOM XML to console
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult console = new StreamResult(System.out);
            transformer.transform(source, console);
 
            System.out.println("\nXML DOM Created Successfully...");
 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	private static Node getMovie(Document doc, String id, String originalTitle, String itTitle, String year) {
        Element movie = doc.createElement("movie");
        movie.setAttribute("id", id);
        movie.appendChild(getCompanyElements(doc, movie, "originalTitle", originalTitle));
        if (itTitle != null) movie.appendChild(getCompanyElements(doc, movie, "itTitle", itTitle));
        movie.appendChild(getCompanyElements(doc, movie, "year", year));
        return movie;
    }
 
    // utility method to create text node
    private static Node getCompanyElements(Document doc, Element element, String name, String value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }
	
	public static String printDirectoryTree(String folderName) {
		File folder = new File(folderName);
	    if (!folder.isDirectory()) {
	        throw new IllegalArgumentException("folder is not a Directory");
	    }
	    int indent = 0;
	    StringBuilder sb = new StringBuilder();
	    printDirectoryTree(folder, indent, sb);
	    return sb.toString();
	}
	
	private static void printDirectoryTree(File folder, int indent, StringBuilder sb) {
	    if (!folder.isDirectory()) {
	        throw new IllegalArgumentException("folder is not a Directory");
	    }
	    sb.append(getIndentString(indent));
	    sb.append("├── ");
	    sb.append(folder.getName());
	    sb.append("");
	    sb.append("\n");
	    for (File file : folder.listFiles()) {
	        if (file.isDirectory()) {
	            printDirectoryTree(file, indent + 1, sb);
	        } else {
	            printFile(file, indent + 1, sb);
	        }
	    }

	}

	private static void printFile(File file, int indent, StringBuilder sb) {
	    sb.append(getIndentString(indent));
	    sb.append("├── ");
	    sb.append(file.getName());
	    sb.append("\n");
	}

	private static String getIndentString(int indent) {
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < indent; i++) {
	        sb.append("│   ");
	    }
	    return sb.toString();
	}

}
