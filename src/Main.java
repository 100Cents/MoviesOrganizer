import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
	private RSyntaxTextArea textArea;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5012568741736697767L;

	public Main() {
		//System.setProperty("http.proxyHost", "195.213.138.202");
		//System.setProperty("http.proxyPort", "8080");
		
		JPanel topWestPanel = new JPanel();
		topWestPanel.setLayout(new BorderLayout());
		JPanel bottomWestPanel = new JPanel();
		
		
		JPanel eastPanel = new JPanel();
		eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
		
		JSplitPane verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topWestPanel, bottomWestPanel);		
		
		JSplitPane horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, verticalSplitPane, eastPanel);
		horizontalSplitPane.setOneTouchExpandable(false);
		horizontalSplitPane.setEnabled(false);
		
		
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
		
		textArea = new RSyntaxTextArea(20, 120);
		//textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
		textArea.setCodeFoldingEnabled(false);
		textArea.setAnimateBracketMatching(false);
		textArea.setEditable(true);

		RTextScrollPane syntaxTextAreaScrollPane = new RTextScrollPane(textArea, false);
		syntaxTextAreaScrollPane.setAutoscrolls(true);

		redirectSystemStreams();
		
		bottomWestPanel.add(syntaxTextAreaScrollPane);
		
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setPreferredSize(new Dimension(150, 400));
		tabbedPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		JComponent panel1 = new JPanel(false);
		panel1.setLayout(new GridLayout(3, 1));
		
        JLabel label1 = new JLabel("BASE_DIRECTORY: " + new File(Utility.BASE_DIRECTORY).getAbsolutePath());
        label1.setHorizontalAlignment(JLabel.LEFT);
        panel1.add(label1);
        
        JLabel label2 = new JLabel("http.proxyHost: " + System.getProperty("http.proxyHost"));
        label2.setHorizontalAlignment(JLabel.LEFT);
        panel1.add(label2);
        
        JLabel label3 = new JLabel("http.proxyPort: " + System.getProperty("http.proxyPort"));
        label3.setHorizontalAlignment(JLabel.LEFT);
        panel1.add(label3);
        
        tabbedPane.add(panel1, "Main");
        
        topWestPanel.add(tabbedPane);
		

		setContentPane(horizontalSplitPane);
		setTitle("Text Editor Demo");
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
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