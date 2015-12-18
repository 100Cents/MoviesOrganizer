package hcents.lifefolders.video.tts;
import hcents.lifefolders.video.tts.controller.Controller;
import hcents.lifefolders.video.tts.model.MainFrameModel;
import hcents.lifefolders.video.tts.view.SearchEvent;
import hcents.lifefolders.video.tts.view.SearchListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.unbescape.html.HtmlEscape;



public class MainFrame extends JFrame implements Observer { /* Da cambiare in MainFrame */

	/**
	 * 
	 */
	private static final long serialVersionUID = -6957570022849716891L;

	private JTextField searchTextField;
	private JButton searchButton;
	private JList list;
	private JTextField resultTextField;
	private JButton resultButton;
	
	private SearchListener searchListener;

	private DefaultListModel listModel;

	private FileTree fileTree;

	public TreePath selectedPath;

	public String selectedMovieId;

	private JTextField itTextField;

	private JTextField yearTextField;

	private JButton capsButton;
	
	public MainFrame(String title) throws HeadlessException {
		super(title);
		
		setLayout(new BorderLayout());
		
		/* Da qui faccio tutta l'interfaccia */
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		
		fileTree = new FileTree();
		
		DefaultMutableTreeNode top = (DefaultMutableTreeNode) FileTree.scan(new File("X:\\__TORRENT__\\+Video\\__TO_SORT__"));
		DefaultTreeModel model = new DefaultTreeModel(top);
		fileTree.setModel(model);
		
		model.reload();

		//fileTree = new FileTree("X:\\__TORRENT__\\+Video\\__TO_SORT__");
		
		//MutableTreeNode root = FileTree.scan(new File("X:\\__TORRENT__\\+Video\\__TO_SORT__"));
		
		
		
		
		
		//fileTree = new FileTree("/home/rabbit/movies");
		JScrollPane fileTreeScrollPane = new JScrollPane(fileTree);
		fileTree.addTreeSelectionListener(new MyTreeSelectionListener());
		
		
		/* Search Panel */
		JPanel searchPane = new JPanel();
		searchPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		GridLayout searchPaneLayout = new GridLayout(2, 0);
		searchPane.setLayout(searchPaneLayout);
		searchPaneLayout.setVgap(2);
		
		searchTextField = new JTextField();
		searchTextField.setSize(300, 50);
		
		searchButton = new JButton("Search");
		searchButton.setSize(new Dimension(300, 300));
		searchButton.addActionListener(new SearchButtonListener());
		
		JPanel searchButtonPanel = new JPanel();
		searchButtonPanel.add(searchButton);
		
		searchPane.add(searchTextField, BorderLayout.NORTH);
		searchPane.add(searchButtonPanel, BorderLayout.CENTER);
		/* End - Search Panel */
		
		
		listModel = new DefaultListModel();
        
 
        //Create the list and put it in a scroll pane.
        list = new JList(listModel);
        list.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(new MyListSelectionListener());
        
        JScrollPane listScrollPane = new JScrollPane(list);
        listScrollPane.setBorder(BorderFactory.createLineBorder(Color.black));
        
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BorderLayout());
        listPanel.add(listScrollPane, BorderLayout.CENTER);
        listPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        
        
        
        /* Result Panel */
		JPanel resultPane = new JPanel();
		resultPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		GridLayout resultPaneLayout = new GridLayout(2, 0);
		resultPane.setLayout(resultPaneLayout);
		resultPaneLayout.setVgap(2);
		
		resultTextField = new JTextField();
		yearTextField = new JTextField();
		itTextField = new JTextField();
		
        resultButton = new JButton("Result");
        resultButton.addActionListener(new ResultButtonListener());
        
        capsButton = new JButton("Caps");
        capsButton.addActionListener(new CapsButtonListener());
		
		JPanel resultButtonPanel = new JPanel();
		resultButtonPanel.add(resultButton);		
		
		resultPane.add(resultTextField, BorderLayout.NORTH);
		resultPane.add(yearTextField, BorderLayout.NORTH);
		resultPane.add(itTextField, BorderLayout.CENTER);
		resultPane.add(resultButton, BorderLayout.SOUTH);
		resultPane.add(capsButton, BorderLayout.SOUTH);
		/* End - Result Panel */
        
        
        
        
        
        
        
        
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(searchPane, BorderLayout.NORTH);
        rightPanel.add(listPanel, BorderLayout.CENTER);
        rightPanel.add(resultPane, BorderLayout.SOUTH);
		
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fileTreeScrollPane, rightPanel);
        splitPane.setResizeWeight(0);
        splitPane.setDividerLocation(450);

        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);

        mainPanel.add(splitPane, BorderLayout.CENTER);
        
		
		add(mainPanel);
	}
	
	public void setSearchPerformedListener(SearchListener searchListener) {
		this.searchListener = searchListener;
	}
	
	@Override
	public void update(Observable obs, Object obj) {
		System.out.println("Update.");
		ArrayList<String> moviesList = (ArrayList<String>)obj;
		listModel.removeAllElements();
		for (String movie : moviesList) {
			listModel.addElement(movie);
		}
	}
	
	class MyListSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() == false) {
				System.out.println("List selection changed: " + list.getSelectedIndex());
				
				if (list.getSelectedIndex() >= 0) {
				
					String stringa = (String)list.getSelectedValue();
					System.out.println(stringa.split(":")[0]);
					selectedMovieId = stringa.split(":")[0];
					
					JSONObject json;
					try {
						json = Controller.readJsonFromUrl("http://www.omdbapi.com/?i=" + stringa.split(":")[0] + "&plot=short&r=json");
						//http://www.omdbapi.com/?t=once+upon+a+time+in+america&y=2003&plot=short&r=json
						
						if (json.has("Title"))    System.out.println("Title:    " + json.get("Title"));
						if (json.has("Year"))     System.out.println("Year:     " + json.get("Year"));
						if (json.has("Rated"))    System.out.println("Rated:    " + json.get("Rated"));
						if (json.has("Genre"))    System.out.println("Genre:    " + json.get("Genre"));
						if (json.has("Director")) System.out.println("Director: " + json.get("Director"));
						
						String title = WordUtils.capitalizeFully((String)json.get("Title"));
						title = title.replaceAll("\\?", "_");
						title = title.replaceAll(":", "_");
						
						//String folderName = title + " (" + json.get("Year") + ")";
						//System.out.println("Folder name: " + folderName);
						resultTextField.setText(title);
						yearTextField.setText(json.getString("Year"));
					} catch (JSONException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
				}
				
				
				
				
				
			}
			
		}
		
	}
	
	class MyTreeSelectionListener implements TreeSelectionListener {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)fileTree.getLastSelectedPathComponent();
			
			selectedPath = e.getPath();
			
			String path = e.getPath().toString();
			System.out.println("Tree selection changed: " + path);
			
			// If nothing is selected.
			if (node == null) return;
			
			// Retrieve the node that was selected.
			Object nodeInfo = node.getUserObject();
	        
	        //System.out.println((String)nodeInfo);
	        //model.setSelectedFileFromTree((String)nodeInfo);
		}
		
	}
	
	class SearchButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String searchString = new String(searchTextField.getText()).trim();
			if (!searchString.equals("")) {
				System.out.println("Search button pressed. Text was: " + searchString);
				listModel.addElement(searchString);
				if (searchListener != null) {
					searchListener.searchPerformed(new SearchEvent(searchString));
				}
			}
			searchTextField.setText("");
		}
		
	}
	
	class ResultButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String resultString = new String(resultTextField.getText()).trim();
			String yearString = new String(yearTextField.getText()).trim();
			String itString = new String(itTextField.getText()).trim();
			
			String directoryString = resultString;
			if (itString.equals("")) {
				directoryString = resultString + " (" + yearString + ")";
			} else {
				directoryString = resultString + " [" + itString + "] (" + yearString + ")";
			}
			
			if (!directoryString.equals("")) {
				Object[] p = selectedPath.getPath();
				String sPath = "X:\\__TORRENT__\\+Video";
				for (Object pp : p) {
					System.out.println(pp);
					sPath += System.getProperty("file.separator") + pp.toString();
				}
				System.out.println("sPATH:" + sPath);
				File file = new File(sPath);
				System.out.println(file.exists());
				
				if (file.isDirectory()) {
					System.out.println("Directory");
					
					try {
						File f = new File(sPath + System.getProperty("file.separator") + selectedMovieId);
				        BufferedWriter output = new BufferedWriter(new FileWriter(f));
			            output.write(selectedMovieId);
			            output.close();
						
						
						FileUtils.moveDirectory(
								FileUtils.getFile(sPath),
								FileUtils.getFile("X:\\__TORRENT__\\+Video\\" + directoryString));
						
						
						DefaultMutableTreeNode top = (DefaultMutableTreeNode) FileTree.scan(new File("X:\\__TORRENT__\\+Video\\__TO_SORT__"));
						DefaultTreeModel model = new DefaultTreeModel(top);
						fileTree.setModel(model);
						
						model.reload();
						
						
					} catch (IOException ioe) {
						// TODO Auto-generated catch block
						ioe.printStackTrace();
					}
				} else if (file.isFile()) {
					System.out.println("File");
					new File("X:\\__TORRENT__\\+Video\\" + directoryString).mkdir();
					
					File f = new File("X:\\__TORRENT__\\+Video\\" + directoryString + System.getProperty("file.separator") + selectedMovieId);
			        BufferedWriter output;
					try {
						output = new BufferedWriter(new FileWriter(f));
						output.write(selectedMovieId);
			            output.close();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
		            
					
					
					File file2 = FileUtils.getFile(sPath);
					String nameFile = file2.getName();
					
					try {
						System.out.println("1:" + sPath);
						System.out.println("2:" + "X:\\__TORRENT__\\+Video\\" + directoryString + System.getProperty("file.separator") + nameFile);
						
						FileUtils.moveFile(FileUtils.getFile(sPath), FileUtils.getFile("X:\\__TORRENT__\\+Video\\" + directoryString + System.getProperty("file.separator") + nameFile));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					DefaultMutableTreeNode top = (DefaultMutableTreeNode) FileTree.scan(new File("X:\\__TORRENT__\\+Video\\__TO_SORT__"));
					DefaultTreeModel model = new DefaultTreeModel(top);
					fileTree.setModel(model);
					
					model.reload();
				}
					
				resultTextField.setText("");
				yearTextField.setText("");
				itTextField.setText("");
				
				
			}
			
		}
		
	}
	
	class CapsButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String itString = new String(itTextField.getText()).trim();
			itTextField.setText(WordUtils.capitalizeFully(itString));
			
		}
		
	}

}
