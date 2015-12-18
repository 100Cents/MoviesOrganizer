package hcents.lifefolders.video.tts.view;

import hcents.lifefolders.video.tts.*;
import hcents.lifefolders.video.tts.model.*;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;


public class View extends JFrame implements ActionListener, ListSelectionListener, Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7330826335570816222L;
	
	private MainFrameModel model;

	private JButton searchButton;
	private JTextField searchTextField;
	
	private SearchListener searchListener;

	private DefaultListModel listModel;

	private JList list;

	private JButton resultButton;

	private JTextField resultTextField;

	/**
	 * @param model
	 * @throws HeadlessException
	 */
	public View(final MainFrameModel model) throws HeadlessException {
		super("Torrents");
		
		this.model = model;
		
		//final FileTree fileTree = new FileTree("X:\\__TORRENT__\\+Video\\__TO_SORT__");
		final FileTree fileTree = new FileTree("/home/rabbit/movies");
		JScrollPane fileTreeScrollPane = new JScrollPane(fileTree);
		fileTree.addTreeSelectionListener(new TreeSelectionListener() {
		    public void valueChanged(TreeSelectionEvent e) {
		        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
		        		fileTree.getLastSelectedPathComponent();

		    /* if nothing is selected */ 
		        if (node == null) return;

		    /* retrieve the node that was selected */ 
		        Object nodeInfo = node.getUserObject();
		        
		        System.out.println((String)nodeInfo);
		        //model.setSelectedFileFromTree((String)nodeInfo);
		    }
		});
		
		
		JPanel searchPane = new JPanel();
		
		searchTextField = new JTextField();
		searchTextField.setSize(300, 70);
		
		searchButton = new JButton("Search");
		searchButton.addActionListener(this);
		
		GroupLayout groupLayout01 = new GroupLayout(searchPane);
		searchPane.setLayout(groupLayout01);
		
		groupLayout01.setHorizontalGroup(
				groupLayout01.createSequentialGroup()
				      .addComponent(searchTextField)
				      .addComponent(searchButton)
				);
				groupLayout01.setVerticalGroup(
						groupLayout01.createParallelGroup(GroupLayout.Alignment.BASELINE)
				           .addComponent(searchTextField)
				           .addComponent(searchButton)
				);
		
		
		
		searchPane.add(searchTextField);
		
		
		//Create and populate the list model.
        listModel = new DefaultListModel();
        listModel.addElement("Whistler, Canada");
        listModel.addElement("Jackson Hole, Wyoming");
        listModel.addElement("Squaw Valley, California");
        listModel.addElement("Telluride, Colorado");
        listModel.addElement("Taos, New Mexico");
        listModel.addElement("Snowbird, Utah");
        listModel.addElement("Chamonix, France");
        listModel.addElement("Banff, Canada");
        listModel.addElement("Arapahoe Basin, Colorado");
        listModel.addElement("Kirkwood, California");
        listModel.addElement("Sun Valley, Idaho");
        //listModel.addListDataListener(new MyListDataListener());
 
        //Create the list and put it in a scroll pane.
        list = new JList(listModel);
        list.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        JScrollPane listScrollPane = new JScrollPane(list);
        
        
        
        JPanel resultPane = new JPanel();
		
        resultTextField = new JTextField();
        resultTextField.setSize(300, 70);
		
        resultButton = new JButton("Result");
        resultButton.addActionListener(this);
		
		GroupLayout groupLayout02 = new GroupLayout(resultPane);
		resultPane.setLayout(groupLayout02);
		
		groupLayout02.setHorizontalGroup(
				groupLayout02.createSequentialGroup()
				      .addComponent(resultTextField)
				      .addComponent(resultButton)
				);
		groupLayout02.setVerticalGroup(
				groupLayout02.createParallelGroup(GroupLayout.Alignment.BASELINE)
				           .addComponent(resultTextField)
				           .addComponent(resultButton)
				);
		
		
		
		resultPane.add(resultTextField);
        
        
        
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(searchPane, BorderLayout.NORTH);
        rightPanel.add(listScrollPane, BorderLayout.CENTER);
        rightPanel.add(resultPane, BorderLayout.SOUTH);
		
		
		/*JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, searchPane, listScrollPane);
		splitPane2.setResizeWeight(0);
        splitPane2.setOneTouchExpandable(true);
        splitPane2.setContinuousLayout(true);*/
		
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fileTreeScrollPane, rightPanel);
        splitPane.setResizeWeight(0);
        splitPane.setDividerLocation(250);

        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);

        add(splitPane, BorderLayout.CENTER);
        
        //setContentPane(content);
		setSize(500, 500);
		setLocation(100, 100);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton sourceButton = (JButton)e.getSource();
		if (sourceButton == searchButton) {
			String searchString = new String(searchTextField.getText());
			System.out.println("Search button pressed. Text was: " + searchString);
			if (searchListener != null) {
				searchListener.searchPerformed(new SearchEvent(searchString));
			}
		}
	}

	public void setSearchPerformedListener(SearchListener searchListener) {
		this.searchListener = searchListener;
	}
	

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {
			 
            if (list.getSelectedIndex() == -1) {
            //No selection: disable delete, up, and down buttons.
                /*deleteButton.setEnabled(false);
                upButton.setEnabled(false);
                downButton.setEnabled(false);
                nameField.setText("");*/
 
            } else if (list.getSelectedIndex() >= 0) {
            	System.out.println("Cambiata selezione su: " + list.getSelectedIndex());
            //Multiple selection: disable up and down buttons.
                /*deleteButton.setEnabled(true);
                upButton.setEnabled(false);
                downButton.setEnabled(false);*/
 
            } else {
            //Single selection: permit all operations.
                /*deleteButton.setEnabled(true);
                upButton.setEnabled(true);
                downButton.setEnabled(true);
                nameField.setText(list.getSelectedValue().toString());*/
            }
        }
		
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		@SuppressWarnings("unchecked")
		ArrayList<String> moviesList = (ArrayList<String>)arg1;
		
		searchTextField.setText(moviesList.get(0));
		
	}

}