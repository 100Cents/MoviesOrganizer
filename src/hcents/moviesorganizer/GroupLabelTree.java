package hcents.moviesorganizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class GroupLabelTree extends JTree {
	
	private File baseDirectory;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3025657852697054227L;
	
	/**
	 * 
	 * @param path
	 */
	public GroupLabelTree(File baseDirectory) {
		
		super();
		this.baseDirectory = baseDirectory;
		
	}
	
	/**
	 * 
	 * @param node
	 * @return
	 * @throws IOException 
	 */
	public synchronized void scan() throws IOException {
		if (baseDirectory != null && baseDirectory.exists() && baseDirectory.isDirectory()) {
			
			List<String> tagsList = new ArrayList<String>();
			
			for (File movieDirectory : Utility.listMoviesDirectoriesFiles(baseDirectory, true)) {
				
				JSONObject jsonDescriptorObject = Utility.getDescriptorJson(movieDirectory);
				if (jsonDescriptorObject != null) {
					
					if (jsonDescriptorObject.has("myData")) {
						JSONObject myData = jsonDescriptorObject.getJSONObject("myData");
						
						if (myData.has("groups")) {
							
							JSONArray jsonGroupsArray = myData.getJSONArray("groups");
							
							for (Object s : jsonGroupsArray) {
								String group = (String) s;
								
								if (!tagsList.contains(group)) {
									tagsList.add(group);
								}
							}
							
						}
						
					}
					
				}
				
			}
			
			DefaultMutableTreeNode ret = new DefaultMutableTreeNode("tags");
			
			for (String tag : tagsList) {
				ret.add(new DefaultMutableTreeNode(tag));
			}
			
			
			
			DefaultTreeModel model = new DefaultTreeModel(ret);
			setModel(model);
			
		}
	}
	
}