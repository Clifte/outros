package box;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;









import com.box.boxjavalibv2.BoxClient;
import com.box.boxjavalibv2.dao.*;
import com.box.boxjavalibv2.exceptions.AuthFatalFailureException;
import com.box.boxjavalibv2.exceptions.BoxServerException;
import com.box.boxjavalibv2.filetransfer.IFileTransferListener;
import com.box.restclientv2.exceptions.BoxRestException;

public class BoxSyncSyncronizer {

	BoxClient client;
	public BoxSyncSyncronizer(BoxClient aclient) {
		client = aclient;
	}
	
	public void syncTree(String id,String dest) throws Exception{
		
		//Criando diretório
		File destFolder = new File(dest);
		destFolder.mkdirs();
		
		
		BoxFolder boxFolder = client.getFoldersManager().getFolder(id,null);
		System.out.println("Baixando nó:" + boxFolder.getName());
		    
		ArrayList<BoxTypedObject> folderEntries = boxFolder.getItemCollection().getEntries();
		    
		int folderSize = folderEntries.size();
		for (int i = 0; i <= folderSize-1; i++){
		    BoxTypedObject folderEntry = folderEntries.get(i);
		    if(folderEntry.getType().equals("folder")){
		    	String folderName = ((BoxItem)folderEntry).getName();
		      	syncTree(folderEntry.getId(), dest + "/" + folderName);
		    }else{
		    	File destFile = new File(destFolder.getAbsolutePath() + "/" + ((BoxItem)folderEntry).getName());
		    	
		        System.out.println("Baixando: " + destFile.getAbsolutePath());
		        client.getFilesManager().downloadFile(folderEntry.getId(), destFile,new DownloadListener(),null);

		    }

		}
	}
	
	
	
	public static void main(String[] args) throws Exception {
		BoxSyncConnector boxCon = new BoxSyncConnector();
	    BoxClient client = boxCon.connect();
	    
	    BoxSyncSyncronizer boxSync = new BoxSyncSyncronizer(client);
	    boxSync.syncTree("1271362628", "Sync");
	    
	    
	}
	private class DownloadListener implements IFileTransferListener{

		@Override
		public void onComplete(String status) {
			System.out.println("Downloaded");
		}

		@Override
		public void onCanceled() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProgress(long bytesTransferred) {
			System.out.print(".");
		}

		@Override
		public void onIOException(IOException e) {
			System.out.println("Erro!");
			
		}
		
	}
}
