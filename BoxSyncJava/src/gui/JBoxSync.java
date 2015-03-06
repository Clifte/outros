package gui;

import java.awt.Color;
import java.awt.Container;

import javax.swing.JFrame;

import com.sempresol.box.BoxManager;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import java.awt.BorderLayout;

public class JBoxSync extends JFrame{
	BoxSyncTreePanel bsp;
	InfoPanel ip;
	
	public JBoxSync() {
		super("File tree");
		
		this.setForeground(Color.black);
		this.setBackground(Color.lightGray);
		Container cp = this.getContentPane();

		
		BoxManager bm = new BoxManager();
		bm.connect();
		bm.setRootNodeID( "2360165495");
		getContentPane().setLayout(new GridLayout(1, 2, 0, 0));
		
		BoxSyncTreePanel bsp = new BoxSyncTreePanel(bm);
		
		cp.add(bsp);
		
		
		ip = new InfoPanel(bm);
		cp.add(ip);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		bm.loadFileTree();
	}
	
	public static void main(String[] args) {
		JBoxSync jbs = new JBoxSync();
		jbs.setVisible(true);
		jbs.pack();
	}

}
