package hcents.lifefolders.video.tts;

import hcents.lifefolders.video.tts.controller.Controller;
import hcents.lifefolders.video.tts.model.MainFrameModel;
import hcents.lifefolders.video.tts.view.View;

import java.awt.Dimension;

import javax.swing.*;



/**
 * @author rabbit
 *
 */
public class Main {
	
	/**
	 * Create the GUI and show it.  For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		MainFrameModel model = new MainFrameModel();
		
		MainFrame mainFrame = new MainFrame("Title");
		
		model.addObserver(mainFrame);
		
		Controller controller = new Controller(mainFrame, model);
		mainFrame.setSearchPerformedListener(controller);
		
		/*
		Model model = new Model();
		
		View view = new View(model);
		
		Controller controller = new Controller(view, model);
		
		view.setSearchPerformedListener(controller);
		
		model.addObserver(view);
		*/
		
		//frame.setSearchPerformedListener(controller);
		
		mainFrame.setSize(1200, 750);
		mainFrame.setMinimumSize(new Dimension(520, 600));
		mainFrame.setLocation(10, 10);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setVisible(true);
	}
	
	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread: creating and
		// showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

}
