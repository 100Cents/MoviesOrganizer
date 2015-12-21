package hcents.moviesorganizer;
import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.*;

public class FileTree extends JTree {   
	/**
	 * 
	 */
	private static final long serialVersionUID = -2251228834619159465L;

	public FileTree(String path) {
	}

	public FileTree() {
		super();
	}

	public static MutableTreeNode scan(File node) {
		DefaultMutableTreeNode ret = new DefaultMutableTreeNode(node.getName());
		if (node.isDirectory())
			for (File child: node.listFiles())
				ret.add(scan(child));
		return ret;
	}
	
	private void createNodes(DefaultMutableTreeNode top) {
		
		/*
	    userList = new ArrayList();
	    for(int i=0;i<10;i++) {
	        User u1 = new User("User " + (i+1));
	        u1.setStatus("Online");
	         top.add(u1);
	         userList.add(u1);
	    }
	    */
	}
}