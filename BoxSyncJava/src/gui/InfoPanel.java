package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import com.sempresol.box.BoxManager;
import com.sempresol.box.BoxManager.TreeItem;

public class InfoPanel extends JPanel{
	JList<Object> changedLocal;
	JList<Object> changedRemote;
	JList<Object> newLocal;
	JList<Object> newRemote;
	BoxManager bm;
	private JPanel panel_4;
	private JPanel panel_1;
	private JPanel panel_2;
	private JPanel panel_3;
	
	//TODO Renomear os paineis e scroll panes apropriadamente
	private JScrollPane scrollPane1 = new JScrollPane();;
	private JScrollPane scrollPane2 = new JScrollPane();;
	private JScrollPane scrollPane3 = new JScrollPane();;
	private JScrollPane scrollPane4 = new JScrollPane();;
	
	public InfoPanel(final BoxManager bm) {
		this.bm = bm;
		this.newRemote = new JList(bm.newRemoteNodes);
		this.newLocal = new JList(bm.newLocalNodes);
		this.changedRemote = new JList(bm.changedRemote);
		this.changedLocal = new JList(bm.changedLocal);
		
		
		
		this.setLayout(new GridLayout(4, 1, 0, 0));
		
		panel_1 = new JPanel();
		panel_2 = new JPanel();
		panel_3 = new JPanel();
		panel_4 = new JPanel();
		
		
		
		panel_1.setBorder(new TitledBorder(null, "Criado Remotamente", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setLayout(new BorderLayout(0, 0));
		panel_2.setBorder(new TitledBorder(null, "Criado Localmente", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setLayout(new BorderLayout(0, 0));
		panel_3.setBorder(new TitledBorder(null, "Modificado Remotamente", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_3.setLayout(new BorderLayout(0, 0));		
		panel_4.setBorder(new TitledBorder(null, "Modificado Localmente", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_4.setLayout(new BorderLayout(0, 0));		
		
		
		
		panel_1.add(scrollPane1);
		panel_2.add(scrollPane2);
		panel_3.add(scrollPane3);
		panel_4.add(scrollPane4);
		
		scrollPane1.setViewportView(newRemote);
		scrollPane2.setViewportView(this.newLocal);
		scrollPane3.setViewportView(this.changedRemote);
		scrollPane4.setViewportView(this.changedLocal);		
		
		add(panel_1);
		add(panel_2);
		add(panel_3);
		add(panel_4);
		
		
		
		int newLocalOpc[] = {0,2};
		addListenners(this.newLocal,newLocalOpc);
		
		int newRemoteOpc[] = {0,1};
		addListenners(this.newRemote,newRemoteOpc);
		
		
		addListenners(this.changedLocal,newLocalOpc);
		addListenners(this.changedRemote,newRemoteOpc);
		
		
		bm.downloadedEvents.add(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int id = bm.newRemoteNodes.indexOf(e.getSource());
				if(id != -1)
					bm.newRemoteNodes.remove(id);
			}
		});
	}

	private void addListenners(final JList<Object> list,final int[] opc) {
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int index = list.getSelectedIndex();
				
				if(index!=-1 && e.getButton()==e.BUTTON3){
					
					BoxFilePopUpMenu bfpm = new BoxFilePopUpMenu(bm, opc);
					bfpm.setSelectedItem((TreeItem) list.getSelectedValue());
					bfpm.show(list, e.getX(), e.getY());
				}
			}
		});
	}
	
}
