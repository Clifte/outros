package gui;

import java.awt.Color;
import java.awt.Container;

import javax.swing.JFrame;

import com.sempresol.box.BoxManager;
import com.sempresol.box.BoxManager.TreeItem;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.tree.DefaultTreeModel;

import java.awt.GridBagConstraints;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class JBoxSync extends JFrame{
	BoxSyncTreePanel bsp;
	InfoPanel ip;
	
	public JBoxSync() throws IOException {
		super("File tree");
		
		this.setForeground(Color.black);
		this.setBackground(Color.lightGray);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		
		BoxManager bm = new BoxManager();
		bm.connect(); 
		bm.setRootNodeID( "2360165495");
		//bm.setRootNodeID( "0");
		bsp = new BoxSyncTreePanel(bm);
		ip = new InfoPanel(bm);
		
		Container cp = this.getContentPane();
		cp.setLayout(new GridLayout(1, 2, 0, 0));
		cp.add(bsp);
		cp.add(ip);
		
		//Load File Tree
		bm.loadFileTree();
		bm.folderLoadedActionListenner.add(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				TreeItem curDir = (TreeItem) e.getSource();
				System.out.println(curDir + " loaded");
				
				((DefaultTreeModel)bsp.tree.getModel()).reload();
				
				bsp.invalidate();
			}
		});
	}
	
	public static void main(String[] args) throws IOException {
		JBoxSync jbs = new JBoxSync();
		jbs.setVisible(true);
		jbs.pack();
	}

}
