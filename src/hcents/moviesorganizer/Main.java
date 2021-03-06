package hcents.moviesorganizer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.fife.ui.rtextarea.*;
import org.json.JSONObject;
import com.alee.laf.WebLookAndFeel;

/**
 * A simple example showing how to use RSyntaxTextArea to add Java syntax
 * highlighting to a Swing application.<p>
 * 
 * This example uses RSyntaxTextArea 2.5.6.
 */
public class Main extends JFrame {
	private RTextArea textArea;
	private FileTree fileTree;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5012568741736697767L;
	
	private final MoviesListTableModel mltm = new MoviesListTableModel();

	public Main() {
		WebLookAndFeel.install();
		
		System.setProperty("http.proxyHost", "195.213.138.202");
		System.setProperty("http.proxyPort", "8080");
		
		//
		// MenuBar
		// Creates a menubar for a JFrame
        JMenuBar menuBar = new JMenuBar();
         
        // Add the menubar to the frame
        setJMenuBar(menuBar);
         
        // Define and add two drop down menu to the menubar
        JMenu fileMenu = new JMenu("File");
        JMenu todoMenu = new JMenu("Todo");
        //JMenu editMenu = new JMenu("Edit");
        menuBar.add(fileMenu);
        menuBar.add(todoMenu);
        //menuBar.add(editMenu);
         
        // Create and add simple menu item to one of the drop down menu
        JMenuItem newAction = new JMenuItem("New");
        JMenuItem openAction = new JMenuItem("Open");
        JMenuItem exitAction = new JMenuItem("Exit");
        
        JMenuItem makePDFCatalogAction = new JMenuItem("Make PDF Catalog");
        JMenuItem compactBackupSlices = new JMenuItem("Compact backup slices");
        JMenuItem renameFilesAddingDots = new JMenuItem("Rename files adding dots");
        JMenuItem formatFromDirectoryNames = new JMenuItem("Format basic movie directory from directory names");
        JMenuItem renameDirectoryReadingDescriptor = new JMenuItem("Rename directories reading descriptor");
        JMenuItem printAllMediainfo = new JMenuItem("Print all MediaInfo");
        JMenuItem makeImdbDirFromTtlist = new JMenuItem("Make IMDB dirs from tt_list.txt");
        JMenuItem doBackup = new JMenuItem("doBackup");
        JMenuItem printImdbDescriptorFromSearchPage = new JMenuItem("Print IMDB descriptors from search page");
        JMenuItem printImdbDescriptorFromWebPage = new JMenuItem("Print IMDB descriptors from web page");
        JMenuItem updateMoviesTableAction = new JMenuItem("Update Movies table");
        JMenuItem adjustPriorityWithLanguageAction = new JMenuItem("Adjust priority with language");
        /*
        JMenuItem cutAction = new JMenuItem("Cut");
        JMenuItem copyAction = new JMenuItem("Copy");
        JMenuItem pasteAction = new JMenuItem("Paste");
        */
        // Create and add CheckButton as a menu item to one of the drop down
        // menu
        //JCheckBoxMenuItem checkAction = new JCheckBoxMenuItem("Check Action");
        // Create and add Radio Buttons as simple menu items to one of the drop
        // down menu
        //JRadioButtonMenuItem radioAction1 = new JRadioButtonMenuItem("Radio Button1");
        //JRadioButtonMenuItem radioAction2 = new JRadioButtonMenuItem("Radio Button2");
        // Create a ButtonGroup and add both radio Button to it. Only one radio
        // button in a ButtonGroup can be selected at a time.
        /*
        ButtonGroup bg = new ButtonGroup();
        bg.add(radioAction1);
        bg.add(radioAction2);
        */
        fileMenu.add(newAction);
        fileMenu.add(openAction);
        //fileMenu.add(checkAction);
        fileMenu.addSeparator();
        fileMenu.add(exitAction);
        
        todoMenu.add(makePDFCatalogAction);
        todoMenu.add(compactBackupSlices);
        todoMenu.add(renameFilesAddingDots);
        todoMenu.add(formatFromDirectoryNames);
        todoMenu.add(renameDirectoryReadingDescriptor);
        todoMenu.add(printAllMediainfo);
        todoMenu.add(makeImdbDirFromTtlist);
        todoMenu.add(doBackup);
        todoMenu.add(printImdbDescriptorFromSearchPage);
        todoMenu.add(printImdbDescriptorFromWebPage);
        todoMenu.add(updateMoviesTableAction);
        todoMenu.add(adjustPriorityWithLanguageAction);
        /*
        editMenu.add(cutAction);
        editMenu.add(copyAction);
        editMenu.add(pasteAction);
        editMenu.addSeparator();
        editMenu.add(radioAction1);
        editMenu.add(radioAction2);
        */
        // Add a listener to the New menu item. actionPerformed() method will
        // invoked, if user triggred this menu item
        makePDFCatalogAction.addActionListener(new MakePdfCatalogActionListener());
        compactBackupSlices.addActionListener(new CompactBackupSlicesActionListener());
        renameFilesAddingDots.addActionListener(new RenameFilesAddingDotsActionListener());
        formatFromDirectoryNames.addActionListener(new FormatFromDirectoryNamesActionListener());
        renameDirectoryReadingDescriptor.addActionListener(new RenameDirectoryReadingDescriptorActionListener());
        printAllMediainfo.addActionListener(new PrintAllMediainfoActionListener());
        makeImdbDirFromTtlist.addActionListener(new MakeImdbDirFromTtlistActionListener());
        doBackup.addActionListener(new DoBackupActionListener());
        printImdbDescriptorFromSearchPage.addActionListener(new PrintImdbDescriptorFromSearchPageActionListener());
        printImdbDescriptorFromWebPage.addActionListener(new PrintImdbDescriptorFromWebPageActionListener());
        adjustPriorityWithLanguageAction.addActionListener(new AdjustPriorityWithLanguageActionActionListener());
        
        updateMoviesTableAction.addActionListener(new ActionListener() {
        	
        	@Override
        	public void actionPerformed(ActionEvent event) {
        		final JMenuItem source = (JMenuItem) event.getSource();
        		
        		source.setEnabled(false);
        		Thread th = new Thread() {

        			public synchronized void run() {
        				try {
        					System.out.println("todo: " + source.getText());
        					
        					mltm.updateRows();
        				} catch (Exception e) {
        					e.printStackTrace();
        				}
        				source.setEnabled(true);
        				System.out.println("done");
        			}

        		};
        		th.start();
        	}
        	
        });
        
        //
        // End Menubar
        
        
		
		///////////////////////////////////////////////////////////////////////
		//
		// Begin: Backup setup panel
		//
		JPanel panel01 = new JPanel();
		panel01.setLayout(new BorderLayout());
				
		final GroupLabelTree tt = new GroupLabelTree(new File(Utility.BASE_DIRECTORY));
		
		new Thread() {

			public synchronized void run() {
				
				try {
					tt.scan();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}

		}.start();
		
		
		
		
		JScrollPane scrollPane3 = new JScrollPane(tt);
		scrollPane3.setBorder(null);
		
		

		//
		// End: Backup setup panel
		//
		///////////////////////////////////////////////////////////////////////
		
		
		JPanel topWestPanel = new JPanel();
		topWestPanel.setLayout(new BorderLayout());
		JPanel bottomWestPanel = new JPanel();
		bottomWestPanel.setLayout(new BorderLayout());
		
		
		JPanel eastPanel = new JPanel();
		eastPanel.setLayout(new BorderLayout());
		
		
		// Begin: tabbed pane on the left
		//
		final JTabbedPane tp = new JTabbedPane();
		tp.setPreferredSize(new Dimension(400, 400));
		tp.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		//tp.setUI(new AquaBarTabbedPaneUI());
		tp.setTabPlacement(JTabbedPane.LEFT);
		tp.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		//tp.add(fileTreeScrollPane, "File tree");
		tp.add(scrollPane3, "Backup setup");
		//tp.add(new JPanel(), "Pluto");
		//tp.add(new JPanel(), "Paperino");
		//tp.add(new JPanel(), "Pippo");
		//tp.add(new JPanel(), "Pippo 2");
		//tp.add(new JPanel(), "Pluto");
		//tp.add(new JPanel(), "Paperino");
		
		eastPanel.add(tp);
		//
		// End: tabbed pane on the left
		
		
		JSplitPane verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topWestPanel, bottomWestPanel);
		verticalSplitPane.setResizeWeight(1.0);
		verticalSplitPane.setDividerSize(2);
		
		JSplitPane horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, eastPanel, verticalSplitPane);
		horizontalSplitPane.setOneTouchExpandable(false);
		horizontalSplitPane.setEnabled(false);
		horizontalSplitPane.setDividerSize(2);
		horizontalSplitPane.setResizeWeight(0);
		
		textArea = new RTextArea(20, 120);
		textArea.setEditable(false);

		RTextScrollPane syntaxTextAreaScrollPane = new RTextScrollPane(textArea, false);
		syntaxTextAreaScrollPane.setAutoscrolls(true);

		redirectSystemStreams();
		
		bottomWestPanel.add(syntaxTextAreaScrollPane, BorderLayout.CENTER);
		
		
		final JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setPreferredSize(new Dimension(150, 400));
		tabbedPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		//tabbedPane.setUI(new AquaBarTabbedPaneUI());

		JComponent panel1 = new JPanel(false);
		
		
		JComponent[] panelComponents1 = new JComponent[3];
		
		JTextField x = new JTextField(60);
		x.setText(new File(Utility.BASE_DIRECTORY).getAbsolutePath());
		panelComponents1[0] = x;
		
		JTextField y = new JTextField(60);
		y.setText(System.getProperty("http.proxyHost"));
		panelComponents1[1] = y;
		
		JTextField z = new JTextField(60);
		z.setText(System.getProperty("http.proxyPort"));
		panelComponents1[2] = z;
		
		String[] labels1 = { "BASE_DIRECTORY:", "http.proxyHost:", "http.proxyPort:" };
		
		JComponent labelsAndFields1 = getTwoColumnLayout(labels1, panelComponents1);
        JComponent backupByTagForm1 = new JPanel(new BorderLayout(5,5));
        backupByTagForm1.add(new JLabel("Purchase Form", SwingConstants.CENTER), BorderLayout.PAGE_START);
        backupByTagForm1.add(labelsAndFields1, BorderLayout.CENTER);
        
        panel1.add(backupByTagForm1);
        tabbedPane.add(panel1, "Main");
        
        
        //
        // Panel 2
        //
        //
        //
        
        JComponent panel2 = new JPanel(false);
        
        
        JComponent[] components = {
                new JTextField(30),
                new JTextField(30),
                new JTextField(30)
            };

        String[] labels = {
            "Product Name:",
            "Product Unit Name:",
            "Purchase Date:"
        };

        JComponent labelsAndFields = getTwoColumnLayout(labels, components);
        JComponent backupByTagForm = new JPanel(new BorderLayout(5, 5));
        backupByTagForm.add(new JLabel("Purchase Form", SwingConstants.CENTER), BorderLayout.PAGE_START);
        backupByTagForm.add(labelsAndFields, BorderLayout.CENTER);
        
        panel2.add(backupByTagForm);
        
        tabbedPane.add(panel2, "Backup by tag");
        
        
        ///////////////////////////////////////////////////////////////////////
        //
        // Panel 3
        //
        JComponent panel3 = new JPanel(false);
        panel3.setLayout(new BorderLayout());
        
        Thread th2 = new Thread() {

			public synchronized void run() {
				try {
					mltm.updateRows();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		};
		th2.start();
		
 		JTable table = new JTable(mltm);
 		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
 		table.getColumnModel().getColumn(0).setMaxWidth(100);
 		table.getColumnModel().getColumn(0).setPreferredWidth(90);
 		table.getColumnModel().getColumn(2).setMaxWidth(100);
        table.setBorder(null);
        
        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                
                if (me.getClickCount() == 2) {

                    JTable table = (JTable) me.getSource();
                    Point p = me.getPoint();
                    int row = table.rowAtPoint(p);
                    
                	MoviesListTableModel mltm = (MoviesListTableModel) table.getModel();
                    File movieDirectory = mltm.getMovieAtRow(row).getMovieDirectory();
                    
                    System.out.println("Double click on row " + row);
                    JDialog dialog = new JDialog();
                    dialog.setLayout(new BorderLayout());
                    
                    MoviesDialogTableModel mdtm = new MoviesDialogTableModel();
                    try {
						mdtm.updateRows(movieDirectory);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    final JTable dialogTable = new JTable(mdtm);
                    dialogTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
                    dialogTable.getColumnModel().getColumn(0).setPreferredWidth(700);
                    
                    
                    JScrollPane dialogTableScrollPane = new JScrollPane(dialogTable);
             		dialogTableScrollPane.setBorder(null);
             		dialogTableScrollPane.setPreferredSize(new Dimension(700, 400));
                    
                    JButton saveButton = new JButton("Save");
                    JButton playButton = new JButton("Play");
                    JButton cancelButton = new JButton("Cancel");
                    
                    
                    playButton.addActionListener(new ActionListener() {
                    	
                    	@Override
                    	public void actionPerformed(ActionEvent event) {
                    		final JButton source = (JButton) event.getSource();
                    		
                    		source.setEnabled(false);
                    		Thread th = new Thread() {

                    			public synchronized void run() {
                    				try {
                    					int selectedRowIndex = dialogTable.getSelectedRow();
                    					MoviesDialogTableModel mdtm = (MoviesDialogTableModel) dialogTable.getModel();
                    					File selectedFile = mdtm.getMovieFileAtRow(selectedRowIndex);
                    					
                    					System.out.println(selectedFile);
                    					
                    					System.out.println("opening file");
                    					
                    					if (Desktop.isDesktopSupported()) {
                    			            Desktop desktop = Desktop.getDesktop();
                    			            desktop.open(selectedFile);
                    			        }
                    					
                    					try {
                    						Thread.sleep(5000);
                    					} catch (InterruptedException e) {
                    						e.printStackTrace();
                    					}
                    				} catch (IOException e) {
                    					e.printStackTrace();
                    				}
                    				source.setEnabled(true);
                    				System.out.println("done");
                    				/*
                    				try {
                    					System.out.println("todo: " + source.getText());
                    					
                    					mltm.updateRows();
                    				} catch (Exception e) {
                    					e.printStackTrace();
                    				}
                    				source.setEnabled(true);
                    				System.out.println("done");
                    				*/
                    			}

                    		};
                    		th.start();
                    	}
                    	
                    });
                    
                    
                    JPanel dialogButtonPanel = new JPanel();
                    dialogButtonPanel.setLayout(new GridLayout(1, 3));
                    dialogButtonPanel.setPreferredSize(new Dimension(700, 25));
                    
                    
                    dialogButtonPanel.add(cancelButton);
                    dialogButtonPanel.add(playButton);
                    dialogButtonPanel.add(saveButton);
                    
                    
                    JPanel backPanel = new JPanel();
                    backPanel.setLayout(new BorderLayout());
                    
                    
                    backPanel.add(dialogTableScrollPane, BorderLayout.NORTH);
                    backPanel.add(dialogButtonPanel, BorderLayout.SOUTH);
                    
                    dialog.add(backPanel);
                    
                    dialog.pack();
                    
                    dialog.setLocationRelativeTo(null);
                    
                    dialog.setModal(true);
                    //dialog.add(newPane("Label in dialog"));
                    
                    dialog.setVisible(true);
                }
            }
        });
        
        final JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //JOptionPane.showMessageDialog(frame, "Right-click performed on table and choose DELETE");
            }
        });
        popupMenu.add(deleteItem);
        table.setComponentPopupMenu(popupMenu);
        
        
 		
 		DefaultTableCellRenderer centerRenderer =
 				new DefaultTableCellRenderer();
 		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
 		table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
 		
 		// Add the table to a scrolling pane
 		
 		//JXTreeTable treeTable = new JXTreeTable(new FileSystemModel());
 		
 		JScrollPane scrollPane = new JScrollPane(table);
 		scrollPane.setBorder(null);
 		panel3.add(scrollPane, BorderLayout.CENTER);
 		
 		tabbedPane.add(panel3, "Table");
 		//
 		// End Panel3
 		//
 		///////////////////////////////////////////////////////////////////////
 		
 		
 		///////////////////////////////////////////////////////////////////////
 		//
 		// Panel 4
 		//
		fileTree = new FileTree();
		
		new Thread() {
			public synchronized void run() {
				DefaultMutableTreeNode top = (DefaultMutableTreeNode) FileTree.scan(new File(Utility.BASE_DIRECTORY));
				DefaultTreeModel model = new DefaultTreeModel(top);
				fileTree.setModel(model);
				model.reload();
			}
		}.start();
		
		JScrollPane fileTreeScrollPane = new JScrollPane(fileTree);
		fileTreeScrollPane.setBorder(null);
		
		tabbedPane.add(fileTreeScrollPane, "File tree");
 		//
 		// End Panel 4
 		//
 		///////////////////////////////////////////////////////////////////////
        
        
        topWestPanel.add(tabbedPane);
		
		setContentPane(horizontalSplitPane);
		setTitle("Movies Organizer");
		//setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
	}
	
	public DefaultTableModel getModel() {
		DefaultTableModel model = null;
        boolean isFirstRow = true;
        try {
            List<String[]> dataList = new ArrayList<String[]>();
            
            for (File movieDirectory : Utility.listMoviesDirectoriesFiles(new File(Utility.BASE_DIRECTORY), true)) {
            	
            	JSONObject jsonObject = Utility.getDescriptorJson(movieDirectory);
            	
            	if (jsonObject != null) {
	            	Movie movie = new Movie(jsonObject);
	            	MovieTitle movieTitle = movie.getMovieTitle();
	            	
	            	dataList.add(new String[] { movie.getImdbCode(), movieTitle.getOriginalTitle(), movie.getYear() + "" });
            	}
            }
            
            for (String[] row: dataList) {
                if (isFirstRow) {
                    model = new DefaultTableModel(getTableColumnHeaders(row.length), 0);
                    model.addRow(row);
                    isFirstRow = false;
                }
                else {
                    if (model != null) {
                        model.addRow(row);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return model;
	}
	
	public Object[] getTableColumnHeaders(int size) {
        Object[] header = new Object[size];
        for (int i = 0; i < header.length; i++) {
            header[i] = i + 1;
        }
        return header;
    }
	
	/**
     * Provides a JPanel with two columns (labels & fields) laid out using
     * GroupLayout. The arrays must be of equal size.
     *
     * @param labelStrings Strings that will be used for labels.
     * @param fields The corresponding fields.
     * @return JComponent A JPanel with two columns of the components provided.
     */
    public static JComponent getTwoColumnLayout(
            String[] labelStrings,
            JComponent[] fields) {
        JLabel[] labels = new JLabel[labelStrings.length];
        for (int ii = 0; ii < labels.length; ii++) {
            labels[ii] = new JLabel(labelStrings[ii]);
        }
        return getTwoColumnLayout(labels, fields);
    }

    /**
     * Provides a JPanel with two columns (labels & fields) laid out using
     * GroupLayout. The arrays must be of equal size.
     *
     * @param labels The first column contains labels.
     * @param fields The last column contains fields.
     * @return JComponent A JPanel with two columns of the components provided.
     */
    public static JComponent getTwoColumnLayout(
            JLabel[] labels,
            JComponent[] fields) {
        return getTwoColumnLayout(labels, fields, true);
    }
    
    /**
     * Provides a JPanel with two columns (labels & fields) laid out using
     * GroupLayout. The arrays must be of equal size.
     *
     * Typical fields would be single line textual/input components such as
     * JTextField, JPasswordField, JFormattedTextField, JSpinner, JComboBox,
     * JCheckBox.. & the multi-line components wrapped in a JScrollPane -
     * JTextArea or (at a stretch) JList or JTable.
     *
     * @param labels The first column contains labels.
     * @param fields The last column contains fields.
     * @param addMnemonics Add mnemonic by next available letter in label text.
     * @return JComponent A JPanel with two columns of the components provided.
     */
    public static JComponent getTwoColumnLayout(
            JLabel[] labels,
            JComponent[] fields,
            boolean addMnemonics) {
        if (labels.length != fields.length) {
            String s = labels.length + " labels supplied for "
                    + fields.length + " fields!";
            throw new IllegalArgumentException(s);
        }
        JComponent panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        // Turn on automatically adding gaps between components
        layout.setAutoCreateGaps(true);
        // Create a sequential group for the horizontal axis.
        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        GroupLayout.Group yLabelGroup = layout.createParallelGroup(GroupLayout.Alignment.TRAILING);
        hGroup.addGroup(yLabelGroup);
        GroupLayout.Group yFieldGroup = layout.createParallelGroup();
        hGroup.addGroup(yFieldGroup);
        layout.setHorizontalGroup(hGroup);
        // Create a sequential group for the vertical axis.
        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
        layout.setVerticalGroup(vGroup);

        int p = GroupLayout.PREFERRED_SIZE;
        // add the components to the groups
        for (JLabel label : labels) {
            yLabelGroup.addComponent(label);
        }
        for (Component field : fields) {
            yFieldGroup.addComponent(field, p, p, p);
        }
        for (int ii = 0; ii < labels.length; ii++) {
            vGroup.addGroup(layout.createParallelGroup().
                    addComponent(labels[ii]).
                    addComponent(fields[ii], p, p, p));
        }

        if (addMnemonics) {
            addMnemonics(labels, fields);
        }

        return panel;
    }

    private final static void addMnemonics(JLabel[] labels, JComponent[] fields) {
        Map<Character, Object> m = new HashMap<Character, Object>();
        for (int ii = 0; ii < labels.length; ii++) {
            labels[ii].setLabelFor(fields[ii]);
            String lwr = labels[ii].getText().toLowerCase();
            for (int jj = 0; jj < lwr.length(); jj++) {
                char ch = lwr.charAt(jj);
                if (m.get(ch) == null && Character.isLetterOrDigit(ch)) {
                    m.put(ch, ch);
                    labels[ii].setDisplayedMnemonic(ch);
                    break;
                }
            }
        }
    }

	private void updateTextArea(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				textArea.append(text);


			}
		});
	}

	private void redirectSystemStreams() {
		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				updateTextArea(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				updateTextArea(new String(b, off, len));
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};

		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(out, true));
	}

	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Main().setVisible(true);
			}
		});
		
	}

}

class MakePdfCatalogActionListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent event) {
		final JMenuItem source = (JMenuItem) event.getSource();
		
		source.setEnabled(false);
		Thread th = new Thread() {

			public synchronized void run() {
				try {
					System.out.println("todo: " + source.getText());
					
					Utility.makeClassicPdfCatalog(new File(Utility.BASE_DIRECTORY));
				} catch (Exception e) {
					e.printStackTrace();
				}
				source.setEnabled(true);
				System.out.println("done");
			}

		};
		th.start();
	}
	
}

class CompactBackupSlicesActionListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent event) {
		final JMenuItem source = (JMenuItem) event.getSource();
		
		source.setEnabled(false);
		Thread th = new Thread() {

			public synchronized void run() {				
				try {
					System.out.println("todo: " + source.getText());
					MoviesBackup.compactSlices(new File(Utility.BASE_DIRECTORY));
				} catch (IOException e) {
					e.printStackTrace();
				}
				source.setEnabled(true);
				System.out.println("done");
			}

		};
		th.start();
	}
	
}

class RenameFilesAddingDotsActionListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent event) {
		final JMenuItem source = (JMenuItem) event.getSource();
		
		source.setEnabled(false);
		Thread th = new Thread() {

			public synchronized void run() {
				try {
					System.out.println("todo: " + source.getText());
					Utility.renameMoviesAddingDotsFilesBaseDir(new File(Utility.BASE_DIRECTORY));
				} catch (IOException e) {
					e.printStackTrace();
				}
				source.setEnabled(true);
				System.out.println("done");
			}

		};
		th.start();
	}
	
}

class FormatFromDirectoryNamesActionListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent event) {
		final JMenuItem source = (JMenuItem) event.getSource();
		
		source.setEnabled(false);
		Thread th = new Thread() {

			public synchronized void run() {
				try {
					System.out.println("todo: " + source.getText());
					MakeDirectoriesFromImdbPage.formatBasicMovieDirectoryFromNameBaseDir(new File(Utility.BASE_DIRECTORY));
				} catch (IOException e) {
					e.printStackTrace();
				}
				source.setEnabled(true);
				System.out.println("done");
			}

		};
		th.start();
	}
	
}

class RenameDirectoryReadingDescriptorActionListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent event) {
		final JMenuItem source = (JMenuItem) event.getSource();
		
		source.setEnabled(false);
		Thread th = new Thread() {

			public synchronized void run() {
				try {
					System.out.println("todo: " + source.getText());
					MakeDirectoriesFromImdbPage.renameDirectoryReadingDescriptorBaseDir(new File(Utility.BASE_DIRECTORY));
				} catch (IOException e) {
					e.printStackTrace();
				}
				source.setEnabled(true);
				System.out.println("done");
			}

		};
		th.start();
	}
	
}

class PrintAllMediainfoActionListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent event) {
		final JMenuItem source = (JMenuItem) event.getSource();
		
		source.setEnabled(false);
		Thread th = new Thread() {

			public synchronized void run() {
				
				try {
					System.out.println("todo: " + source.getText());
					Utility.printMediaInfoBaseDir(new File(Utility.BASE_DIRECTORY));
				} catch (IOException e) {
					e.printStackTrace();
				}
				source.setEnabled(true);
				System.out.println("done");
			}

		};
		th.start();
	}
	
}

class MakeImdbDirFromTtlistActionListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent event) {
		final JMenuItem source = (JMenuItem) event.getSource();
		
		source.setEnabled(false);
		Thread th = new Thread() {

			public synchronized void run() {
				try {
					System.out.println("todo: " + source.getText());
					Utility.makeImdbDirs(new File(Utility.BASE_DIRECTORY));
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					source.setEnabled(true);
					System.out.println("done");
				}
			}

		};
		th.start();
	}
	
}

class DoBackupActionListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent event) {
		final JMenuItem source = (JMenuItem) event.getSource();
		
		source.setEnabled(false);
		Thread th = new Thread() {

			public synchronized void run() {
				try {
					System.out.println("todo: " + source.getText());
					String slice = JOptionPane.showInputDialog(null, "Insert a slice number", "Input Dialog", JOptionPane.PLAIN_MESSAGE);
					if (slice.length() == 2) {
						MoviesBackup.doBackup(new File(Utility.BASE_DIRECTORY), new File("E:\\"), "BACKUP:SLICE:" + slice, true);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					source.setEnabled(true);
					System.out.println("done");
				}
			}

		};
		th.start();
	}
	
}

class PrintImdbDescriptorFromSearchPageActionListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent event) {
		final JMenuItem source = (JMenuItem) event.getSource();
		
		source.setEnabled(false);
		Thread th = new Thread() {

			public synchronized void run() {
				try {
					System.out.println("todo: " + source.getText());
					List<String> moviesList = Utility.getImdbDescriptorsForSearchPages();
					for (String s : moviesList) {
						System.out.println(s);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					source.setEnabled(true);
					System.out.println("done");
				}
			}

		};
		th.start();
	}
	
}

/**
 * 
 * @author rabbit
 *
 */
class PrintImdbDescriptorFromWebPageActionListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent event) {
		final JMenuItem source = (JMenuItem) event.getSource();
		
		source.setEnabled(false);
		Thread th = new Thread() {

			public synchronized void run() {
				try {
					System.out.println("todo: " + source.getText());
					String url = JOptionPane.showInputDialog(null, "Insert a web page", "Input Dialog", JOptionPane.PLAIN_MESSAGE);
					List<String> moviesList = Utility.getImdbDescriptorsFromWebPage(url);
					for (String s : moviesList) {
						System.out.println(s);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					source.setEnabled(true);
					System.out.println("done");
				}
			}

		};
		th.start();
	}
	
}

/**
 * 
 * @author rabbit
 *
 */
class AdjustPriorityWithLanguageActionActionListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent event) {
		final JMenuItem source = (JMenuItem) event.getSource();
		
		source.setEnabled(false);
		Thread th = new Thread() {

			public synchronized void run() {
				try {
					System.out.println("todo: " + source.getText());
					MoviesBackup.adjustPriorityWithLanguages(new File(Utility.BASE_DIRECTORY));
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					source.setEnabled(true);
					System.out.println("done");
				}
			}

		};
		th.start();
	}
	
}
