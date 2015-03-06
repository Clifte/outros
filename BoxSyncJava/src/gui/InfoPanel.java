package gui;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;

import com.sempresol.box.BoxManager;
import java.awt.GridLayout;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.AbstractListModel;

public class InfoPanel extends JPanel{
	JList<Object> changedLocal;
	JList<Object> changedRemote;
	JList<Object> newLocal;
	JList<Object> newRemote;
	BoxManager bm;
	private JPanel panel;
	private JPanel panel_1;
	private JPanel panel_2;
	private JPanel panel_3;
	private JScrollPane scrollPane1 = new JScrollPane();;
	private JScrollPane scrollPane2 = new JScrollPane();;
	private JScrollPane scrollPane3 = new JScrollPane();;
	private JScrollPane scrollPane4 = new JScrollPane();;
	
	public InfoPanel(BoxManager bm) {
		this.bm = bm;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		this.setLayout(new GridLayout(4, 1, 0, 0));
		
		panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Criado Remotamente", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		
		panel_1.add(scrollPane1);
		
		this.newRemote = new JList(bm.newRemoteNodes);
		scrollPane1.setViewportView(newRemote);
		
		
		
		
		
		panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Criado Localmente", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		
		this.newLocal = new JList(bm.newLocalNodes);
		scrollPane2.setViewportView(this.newLocal);
		panel_2.add(scrollPane2);
		
		panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(null, "Modificado Remotamente", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		this.changedRemote = new JList(bm.changedRemote);
		scrollPane3.setViewportView(this.changedRemote);
		panel_3.add(scrollPane3);
		
		panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Modificado Localmente", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		this.changedLocal = new JList(bm.changedLocal);
		scrollPane4.setViewportView(this.changedLocal);
		panel.add(scrollPane4);
	}
}
