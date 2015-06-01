package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import com.box.sdk.BoxItem;
import com.sempresol.box.BoxManager;
import com.sempresol.box.BoxManager.TreeItem;



public class BoxFilePopUpMenu extends JPopupMenu {

	//Rigth mouse button options
	JMenuItem properties = new JMenuItem("Properties");
	JMenuItem download = new JMenuItem("Download");
	JMenuItem upload = new JMenuItem("Upload");
	
	
	private List<BoxManager.TreeItem> selectedItemList = new ArrayList<BoxManager.TreeItem>();
	
	private BoxManager bm = null;
	
	/** Create a boxfile popup menu.
	 * This menu is usually added to rigth mouse button.
	 * The opc vector controls witch option will be avaible in the menu
	 * */
	public BoxFilePopUpMenu(BoxManager bm,int[] opc) {
		super();
		JMenuItem opcoes[] = { properties,
							   download,
							   upload
							  };

		if(opc==null){
			this.add(download);
			this.add(properties);
		}else{
			for (int i = 0; i < opc.length; i++) {
				this.add(opcoes[opc[i]]);
			}			
		}
		
		this.bm = bm;
		//Adding listenners to menu itens
		download.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//FIXME Adicionar opção de selecionar todos
				TreeItem selectedItem = selectedItemList.get(0);

				try {
					BoxFilePopUpMenu.this.bm.download(selectedItem);
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		upload.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				uploadFiles();
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
		selectedItemList.clear();
		selectedItemList.add(id);
	}
	
	private void uploadFiles() {
		
		for (TreeItem treeItem : selectedItemList) {
			System.out.println("UploadFiles" + treeItem);
			try {
				this.bm.upload(treeItem);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void showProperties() {
		String message = "";
		TreeItem selectedItem = selectedItemList.get(0);
		TreeItem rsc = selectedItem;
		
		
		File f = new File(rsc.path);
		BoxItem bi = (BoxItem) rsc.res;
		long remoteFileModificationDate;
		long localFileModificationDate;
		
		message+= "Local Path:" + rsc.path + "\n";
		message+= "ID: " + rsc.id + "\n";
		
		
		
		if(bi==null){
			message+="The file doesn't exists in remote server\n";
		}else{
			remoteFileModificationDate = bi.getInfo().getModifiedAt().getTime();
			message+= "Remote modfication date: " + new Date(remoteFileModificationDate) + "\n";	
		}
	
		
		if(f.exists()){
			localFileModificationDate = f.lastModified();
			message+= "Local modfication date: " + new Date(localFileModificationDate) + "\n";
		}else{
			message+="The file doesn't exist in local disk";
		}
		
		JOptionPane.showMessageDialog(this, message,"File Info",JOptionPane.INFORMATION_MESSAGE);
	}
}
