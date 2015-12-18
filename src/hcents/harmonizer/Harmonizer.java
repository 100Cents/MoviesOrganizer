package hcents.harmonizer;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;


public class Harmonizer {

	private JFrame frame;
	private	JPanel topPanel;
	private	JTable table;
	private	JScrollPane scrollPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Harmonizer window = new Harmonizer();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Harmonizer() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 850, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Harmonizer");
		frame.setBackground(Color.gray);
		
		
		JMenuBar menubar = new JMenuBar();

        JMenu file = new JMenu("File");

        JMenuItem eMenuItem = new JMenuItem("Add item...");
        eMenuItem.setToolTipText("Add item");
        eMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
            	JDialog dialog = new AddItemDialog();
            	//JLabel label = new JLabel("Please wait...");
            	dialog.setLocationRelativeTo(frame);
            	dialog.setTitle("Please Wait...");
            	//dialog.add(label);
            	dialog.pack();
            	dialog.setVisible(true);
            }
        });

        file.add(eMenuItem);

        menubar.add(file);

        frame.setJMenuBar(menubar);
		
		
		
		// Create a panel to hold all other components.
		topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		frame.getContentPane().add(topPanel);
		
		// Create columns names
		String columnNames[] = { "Codice", "Descrizione", "Quantità" };
		
		// Create some data
		String dataValues[][] =
				{
					{ "00001", "Toner Stampante HP 01", "67" },
					{ "00002", "Penna a sfera rossa", "853" },
					{ "00003", "Evidenziatore a boccanaglie", "109" },
					{ "00004", "Penna stilografica", "3092" },
					{ "00001", "Toner Stampante HP 01", "67" },
					{ "00002", "Penna a sfera rossa", "853" },
					{ "00003", "Evidenziatore a boccanaglie", "109" },
					{ "00004", "Penna stilografica", "3092" },
					{ "00001", "Toner Stampante HP 01", "67" },
					{ "00002", "Penna a sfera rossa", "853" },
					{ "00003", "Evidenziatore a boccanaglie", "109" },
					{ "00004", "Penna stilografica", "3092" },
					{ "00001", "Toner Stampante HP 01", "67" },
					{ "00002", "Penna a sfera rossa", "853" },
					{ "00003", "Evidenziatore a boccanaglie", "109" },
					{ "00004", "Penna stilografica", "3092" },
					{ "00001", "Toner Stampante HP 01", "67" },
					{ "00002", "Penna a sfera rossa", "853" },
					{ "00003", "Evidenziatore a boccanaglie", "109" },
					{ "00004", "Penna stilografica", "3092" },
					{ "00001", "Toner Stampante HP 01", "67" },
					{ "00002", "Penna a sfera rossa", "853" },
					{ "00003", "Evidenziatore a boccanaglie", "109" },
					{ "00004", "Penna stilografica", "3092" },
					{ "00001", "Toner Stampante HP 01", "67" },
					{ "00002", "Penna a sfera rossa", "853" },
					{ "00003", "Evidenziatore a boccanaglie", "109" },
					{ "00004", "Penna stilografica", "3092" },
					{ "00001", "Toner Stampante HP 01", "67" },
					{ "00002", "Penna a sfera rossa", "853" },
					{ "00003", "Evidenziatore a boccanaglie", "109" },
					{ "00004", "Penna stilografica", "3092" },
					{ "00001", "Toner Stampante HP 01", "67" },
					{ "00002", "Penna a sfera rossa", "853" },
					{ "00003", "Evidenziatore a boccanaglie", "109" },
					{ "00004", "Penna stilografica", "3092" },
					{ "00001", "Toner Stampante HP 01", "67" },
					{ "00002", "Penna a sfera rossa", "853" },
					{ "00003", "Evidenziatore a boccanaglie", "109" },
					{ "00004", "Penna stilografica", "3092" },
					{ "00001", "Toner Stampante HP 01", "67" },
					{ "00002", "Penna a sfera rossa", "853" },
					{ "00003", "Evidenziatore a boccanaglie", "109" },
					{ "00004", "Penna stilografica", "3092" },
					{ "00001", "Toner Stampante HP 01", "67" },
					{ "00002", "Penna a sfera rossa", "853" },
					{ "00003", "Evidenziatore a boccanaglie", "109" },
					{ "00004", "Penna stilografica", "3092" },
					{ "00001", "Toner Stampante HP 01", "67" },
					{ "00002", "Penna a sfera rossa", "853" },
					{ "00003", "Evidenziatore a boccanaglie", "109" },
					{ "00004", "Penna stilografica", "3092" },
					{ "00001", "Toner Stampante HP 01", "67" },
					{ "00002", "Penna a sfera rossa", "853" },
					{ "00003", "Evidenziatore a boccanaglie", "109" },
					{ "00004", "Penna stilografica", "3092" },
					{ "00001", "Toner Stampante HP 01", "67" },
					{ "00002", "Penna a sfera rossa", "853" },
					{ "00003", "Evidenziatore a boccanaglie", "109" },
					{ "00004", "Penna stilografica", "3092" },
					{ "00001", "Toner Stampante HP 01", "67" },
					{ "00002", "Penna a sfera rossa", "853" },
					{ "00003", "Evidenziatore a boccanaglie", "109" },
					{ "00004", "Penna stilografica", "3092" },
					{ "00001", "Toner Stampante HP 01", "67" },
					{ "00002", "Penna a sfera rossa", "853" },
					{ "00003", "Evidenziatore a boccanaglie", "109" },
					{ "00004", "Penna stilografica", "3092" },
					{ "00001", "Toner Stampante HP 01", "67" },
					{ "00002", "Penna a sfera rossa", "853" },
					{ "00003", "Evidenziatore a boccanaglie", "109" },
					{ "00004", "Penna stilografica", "3092" },
					{ "00001", "Toner Stampante HP 01", "67" },
					{ "00002", "Penna a sfera rossa", "853" },
					{ "00003", "Evidenziatore a boccanaglie", "109" },
					{ "00004", "Penna stilografica", "3092" },
					{ "00001", "Toner Stampante HP 01", "67" },
					{ "00002", "Penna a sfera rossa", "853" },
					{ "00003", "Evidenziatore a boccanaglie", "109" },
					{ "00004", "Penna stilografica", "3092" }
				};
		
		// Create a new table instance
		table = new JTable(dataValues, columnNames);
		//table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.getColumnModel().getColumn(0).setMaxWidth(100);
		table.getColumnModel().getColumn(2).setMaxWidth(100);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		
		// Add the table to a scrolling pane
		scrollPane = new JScrollPane(table);
		topPanel.add(scrollPane, BorderLayout.CENTER);
	}

}
