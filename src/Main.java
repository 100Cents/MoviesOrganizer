import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;
import org.jsoup.Jsoup;
import org.pdfclown.files.SerializationModeEnum;

/**
 * A simple example showing how to use RSyntaxTextArea to add Java syntax
 * highlighting to a Swing application.<p>
 * 
 * This example uses RSyntaxTextArea 2.5.6.
 */
public class Main extends JFrame {
	private RTextArea textArea;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5012568741736697767L;

	public Main() {
		System.setProperty("http.proxyHost", "195.213.138.202");
		System.setProperty("http.proxyPort", "8080");
		
		JPanel topWestPanel = new JPanel();
		topWestPanel.setLayout(new BorderLayout());
		JPanel bottomWestPanel = new JPanel();
		bottomWestPanel.setLayout(new BorderLayout());
		
		
		JPanel eastPanel = new JPanel();
		eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
		
		
		JSplitPane verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topWestPanel, bottomWestPanel);
		verticalSplitPane.setResizeWeight(1.0);
		
		JSplitPane horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, verticalSplitPane, eastPanel);
		horizontalSplitPane.setOneTouchExpandable(false);
		horizontalSplitPane.setEnabled(false);
		horizontalSplitPane.setDividerSize(5);
		horizontalSplitPane.setResizeWeight(1.0);
		
		
		final String[] rightButtonNames = new String[] {
				"Generate PDF catalog",
				"Compact backup slices",
				"Rename files adding dots",
				"Format basic movie directory from directory names",
				"Rename directories reading descriptor",
				"Print all MediaInfo",
				"Print IMDB descriptors from web page",
				"Make IMDB dirs from tt_list.txt",
				"doBackup",
				"Print IMDB descriptors from search page",
		};
		final JButton[] rightButtons = new JButton[rightButtonNames.length];
		
		for (int i = 0; i < rightButtons.length; i++) {
			rightButtons[i] = new JButton(rightButtonNames[i]);
			eastPanel.add(rightButtons[i]);
			rightButtons[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, rightButtons[i].getMinimumSize().height));
		}
		
		rightButtons[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rightButtons[0].setEnabled(false);
				Thread th = new Thread() {

					public synchronized void run() {
						try {
							System.out.println("todo: " + rightButtonNames[0]);

							PdfCatalogWriter pdfCatalogWriter = new PdfCatalogWriter(new File(Utility.BASE_DIRECTORY));
							org.pdfclown.files.File pdfFile = pdfCatalogWriter.composePdf();

							SimpleDateFormat nowDateFormat = new SimpleDateFormat("yyyyMMdd");
							String now = nowDateFormat.format(new Date());

							String fileName = "catalog-" + now + ".pdf";
							pdfCatalogWriter.serialize(pdfFile, fileName, SerializationModeEnum.Standard, "Movies catalog", "Movies catalog", null);
						} catch (Exception e) {
							e.printStackTrace();
						}
						rightButtons[0].setEnabled(true);
						System.out.println("done");
					}

				};
				th.start();

			}
		});
		
		rightButtons[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rightButtons[1].setEnabled(false);
				Thread th = new Thread() {

					public synchronized void run() {
						try {
							System.out.println("todo: " + rightButtonNames[1]);
							MoviesBackup.compactSlices(new File(Utility.BASE_DIRECTORY));
						} catch (IOException e) {
							e.printStackTrace();
						}
						rightButtons[1].setEnabled(true);
						System.out.println("done");
					}

				};
				th.start();

			}
		});
		
		rightButtons[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rightButtons[2].setEnabled(false);
				Thread th = new Thread() {

					public synchronized void run() {
						try {
							System.out.println("todo: " + rightButtonNames[2]);
							Utility.renameMoviesFilesBaseDir(new File(Utility.BASE_DIRECTORY));
						} catch (IOException e) {
							e.printStackTrace();
						}
						rightButtons[2].setEnabled(true);
						System.out.println("done");
					}

				};
				th.start();

			}
		});
		
		rightButtons[3].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rightButtons[3].setEnabled(false);
				Thread th = new Thread() {

					public synchronized void run() {
						try {
							System.out.println("todo: " + rightButtonNames[3]);
							MakeDirectoriesFromImdbPage.formatBasicMovieDirectoryFromNameBaseDir(new File(Utility.BASE_DIRECTORY));
						} catch (IOException e) {
							e.printStackTrace();
						}
						rightButtons[3].setEnabled(true);
						System.out.println("done");
					}

				};
				th.start();

			}
		});
		
		rightButtons[4].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rightButtons[4].setEnabled(false);
				Thread th = new Thread() {

					public synchronized void run() {
						try {
							System.out.println("todo: " + rightButtonNames[4]);
							MakeDirectoriesFromImdbPage.renameDirectoryReadingDescriptorBaseDir(new File(Utility.BASE_DIRECTORY));
						} catch (IOException e) {
							e.printStackTrace();
						}
						rightButtons[4].setEnabled(true);
						System.out.println("done");
					}

				};
				th.start();

			}
		});
		
		rightButtons[5].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rightButtons[5].setEnabled(false);
				Thread th = new Thread() {

					public synchronized void run() {
						try {
							System.out.println("todo: " + rightButtonNames[5]);
							Utility.printMediaInfoBaseDir(new File(Utility.BASE_DIRECTORY));
						} catch (IOException e) {
							e.printStackTrace();
						}
						rightButtons[5].setEnabled(true);
						System.out.println("done");
					}

				};
				th.start();

			}
		});
		
		rightButtons[6].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rightButtons[6].setEnabled(false);
				Thread th = new Thread() {

					public synchronized void run() {
						try {
							System.out.println("todo: " + rightButtonNames[6]);
							String url = JOptionPane.showInputDialog(null, "Insert a web page", "Input Dialog", JOptionPane.PLAIN_MESSAGE);
							List<String> moviesList = Utility.getImdbDescriptorsFromWebPage(url);
							for (String s : moviesList) {
								System.out.println(s);
							}
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							rightButtons[6].setEnabled(true);
							System.out.println("done");
						}
						
					}

				};
				th.start();

			}
		});
		
		rightButtons[7].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rightButtons[7].setEnabled(false);
				Thread th = new Thread() {

					public synchronized void run() {
						try {
							System.out.println("todo: " + rightButtonNames[7]);
							Utility.makeImdbDirs(new File(Utility.BASE_DIRECTORY));
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							rightButtons[7].setEnabled(true);
							System.out.println("done");
						}
						
					}

				};
				th.start();

			}
		});
		
		rightButtons[8].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rightButtons[8].setEnabled(false);
				Thread th = new Thread() {

					public synchronized void run() {
						try {
							System.out.println("todo: " + rightButtonNames[8]);
							String slice = JOptionPane.showInputDialog(null, "Insert a slice number", "Input Dialog", JOptionPane.PLAIN_MESSAGE);
							if (slice.length() == 2) {
								MoviesBackup.doBackup(new File(Utility.BASE_DIRECTORY), new File("E:\\"), "BACKUP:SLICE:" + slice, true);
							}
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							rightButtons[8].setEnabled(true);
							System.out.println("done");
						}
						
					}

				};
				th.start();

			}
		});
		
		rightButtons[9].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rightButtons[9].setEnabled(false);
				Thread th = new Thread() {

					public synchronized void run() {
						try {
							System.out.println("todo: " + rightButtonNames[9]);
							List<String> moviesList = Utility.getImdbDescriptorsForSearchPages();
							for (String s : moviesList) {
								System.out.println(s);
							}
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							rightButtons[9].setEnabled(true);
							System.out.println("done");
						}
						
					}

				};
				th.start();

			}
		});
		
		textArea = new RTextArea(20, 120);
		//textArea = new RSyntaxTextArea();
		//textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
		//textArea.setCodeFoldingEnabled(false);
		//textArea.setAnimateBracketMatching(false);
		textArea.setEditable(true);

		RTextScrollPane syntaxTextAreaScrollPane = new RTextScrollPane(textArea, false);
		syntaxTextAreaScrollPane.setAutoscrolls(true);

		redirectSystemStreams();
		
		bottomWestPanel.add(syntaxTextAreaScrollPane, BorderLayout.CENTER);
		
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setPreferredSize(new Dimension(150, 400));
		tabbedPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

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
        JComponent backupByTagForm = new JPanel(new BorderLayout(5,5));
        backupByTagForm.add(new JLabel("Purchase Form", SwingConstants.CENTER), BorderLayout.PAGE_START);
        backupByTagForm.add(labelsAndFields, BorderLayout.CENTER);
        
        panel2.add(backupByTagForm);
        
        tabbedPane.add(panel2, "Backup by tag");
        
        
        topWestPanel.add(tabbedPane);
		

		setContentPane(horizontalSplitPane);
		setTitle("Movies Organizer");
		//setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
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

    private final static void addMnemonics(
            JLabel[] labels,
            JComponent[] fields) {
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