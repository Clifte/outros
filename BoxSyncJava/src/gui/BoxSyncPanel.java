package gui;

/*
 * Copyright (c) Ian F. Darwin, http://www.darwinsys.com/, 1996-2002.
 * All rights reserved. Software written by Ian F. Darwin and others.
 * $Id: LICENSE,v 1.8 2004/02/09 03:33:38 ian Exp $
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * Java, the Duke mascot, and all variants of Sun's Java "steaming coffee
 * cup" logo are trademarks of Sun Microsystems. Sun's, and James Gosling's,
 * pioneering role in inventing and promulgating (and standardizing) the Java 
 * language and environment is gratefully acknowledged.
 * 
 * The pioneering role of Dennis Ritchie and Bjarne Stroustrup, of AT&T, for
 * inventing predecessor languages C and C++ is also gratefully acknowledged.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;

import com.sempresol.box.BoxManager;

/**
 * Display a file system in a JTree view
 * 
 * @version $Id: FileTree.java,v 1.9 2004/02/23 03:39:22 ian Exp $
 * @author Ian Darwin
 */
public class BoxSyncPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	BoxFilePopUpMenu mouseMenu;
	BoxManager bm;
	
	public BoxSyncPanel(BoxManager bm) {
		setLayout(new BorderLayout());
		this.bm = bm;
		mouseMenu = new BoxFilePopUpMenu(bm);
		
		// Make a tree list with all the nodes, and make it a JTree
		final JTree tree = new JTree(bm.rootNode);
		loadTreeListener(tree);
				 
		// Lastly, put the JTree into a JScrollPane.
		JScrollPane scrollpane = new JScrollPane();
		scrollpane.getViewport().add(tree);
		add(BorderLayout.CENTER, scrollpane);
		
		
		
		bm.loadFileTree("2360165495");
		
	}

	private void loadTreeListener(final JTree tree) {
		MouseListener ml = new MouseAdapter() {
		     public void mousePressed(MouseEvent e) { 	 
		         int selRow = tree.getRowForLocation(e.getX(), e.getY());
		         
		         if(selRow != -1 && e.getButton()==e.BUTTON3) {
		        	 System.out.println("Selected row: " + selRow);
			         tree.setSelectionPath(tree.getPathForLocation(e.getX(), e.getY()));
			         BoxManager.TreeItem it = (BoxManager.TreeItem) tree.getPathForLocation(e.getX(), e.getY()).getLastPathComponent();		        	 
		        	 
		        	 mouseMenu.setSelectedItem(it);
		        	 mouseMenu.show((Component) e.getSource(), e.getX(), e.getY());	 
		         }
		     }
		 };
		 
		 
		 
		tree.addMouseListener(ml);
	}

	public Dimension getMinimumSize() {
		return new Dimension(200, 400);
	}

	public Dimension getPreferredSize() {
		return new Dimension(200, 400);
	}

	
	/**
	 * Main: make a Frame, add a FileTree
	 * 
	 * @throws AuthFatalFailureException
	 * @throws BoxServerException
	 * @throws Exception
	 */
	public static void main(String[] av) throws Exception {

		JFrame frame = new JFrame("FileTree");
		frame.setForeground(Color.black);
		frame.setBackground(Color.lightGray);
		Container cp = frame.getContentPane();

		
		BoxManager bm = new BoxManager();
		bm.connect();
		bm.setRootNodeID( "2360165495");
		
		
		cp.add(new BoxSyncPanel(bm));

		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
