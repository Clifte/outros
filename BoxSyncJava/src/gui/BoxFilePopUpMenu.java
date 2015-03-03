package gui;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;
import com.sempresol.box.BoxManager;
import com.sempresol.box.BoxManager.TreeItem;



public class BoxFilePopUpMenu extends JPopupMenu {

	JMenuItem download = new JMenuItem("Download");
	JMenuItem properties = new JMenuItem("Properties");
	
	
	private BoxManager.TreeItem selectedItem;
	private BoxManager bm;
	
	public BoxFilePopUpMenu(BoxManager bm) {
		super();
		this.add(download);
		this.add(properties);
		this.bm = bm;
		download.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				downloadPath();
			}


		});
		properties.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showProperties();
				
			}


		});
	}

	public void setSelectedItem(BoxManager.TreeItem id) {
		selectedItem = id;
	}
	
	private void downloadPath() {
		try {
			TreeItem rsc = selectedItem;
			
			if(rsc.res instanceof BoxFolder){
				System.out.println("Downloading Tree");
				bm.downloadFileTree(rsc, rsc.res.getID());
				
			}else if(rsc.res instanceof BoxItem){
				System.out.println("Download Single File");
				bm.download(selectedItem.id);
			}else{
				System.out.println("Error");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void showProperties() {
		String message = "";
		TreeItem rsc = selectedItem;
		File f = new File(rsc.path);
		BoxItem bi = (BoxItem) rsc.res;
		
		
		
		
		
		message+= "Local Path:" + rsc.path + "\n";
		message+= "ID: " + rsc.id + "\n";
		
		
		long remoteFileModificationDate = bi.getInfo().getModifiedAt().getTime();
		message+= "Remote modfication date: " + new Date(remoteFileModificationDate) + "\n";
		
		
		if(f.exists()){
			long localFileModificationDate = f.lastModified();
			message+= "Local modfication date: " + new Date(localFileModificationDate) + "\n";
		}else{
			message+="The file doesn't exist in local disk";
		}
		
		JOptionPane.showMessageDialog(this, message);
	}
}
