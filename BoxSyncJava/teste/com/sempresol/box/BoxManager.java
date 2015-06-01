package com.sempresol.box;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.swing.DefaultListModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import org.junit.Test;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;
import com.box.sdk.BoxResource;
import com.box.sdk.FileUploadParams;
import com.box.sdk.ProgressListener;


public class BoxManager {
    public  final String key = "lpm2a16zwqguuv7f4hu3lr1d9znoed9x";
    public  final String secret = "w5rp5Pgm2PPMSejrU1wtvutu6rzuOnfO";
    public final int PORT = 4000;
    private BoxAPIConnection api = null;
    private String syncDir = System.getProperty("user.home") + "/Box";
    private final int SYSTEM = 1;                          //1=unix with touch command
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm.ss");
    
    public TreeItem rootNode = null;
    
    /*List with informations about the files. After the remote directory is scanned the created and modified remote
     * files are added to theirs respectives list. The local modifications are added to theirs respectives lists too*/
    public DefaultListModel<TreeItem> changedLocal = new DefaultListModel<BoxManager.TreeItem>();
    public DefaultListModel<TreeItem> changedRemote = new DefaultListModel<BoxManager.TreeItem>();
    public DefaultListModel<TreeItem> newRemoteNodes = new DefaultListModel<BoxManager.TreeItem>();
    public DefaultListModel<TreeItem> newLocalNodes = new DefaultListModel<BoxManager.TreeItem>();
    
    public List<ActionListener> folderLoadedActionListenner = new ArrayList<ActionListener>();
	public List<ActionListener> downloadedEvents = new ArrayList<ActionListener>();
	public List<ActionListener> uploadedEvents = new ArrayList<ActionListener>();
	
	
    private static final int MAX_AVAILABLE = 5;
    private final Semaphore treeCreationSemaphore = new Semaphore(MAX_AVAILABLE, true);
    private final Semaphore treeDownloadSemaphore = new Semaphore(MAX_AVAILABLE, true);
    private  int nInstances = 0;
  //-------------------------------------------------------------------------------------------------------
  //METHODS-------------------------------------------------------------------------------------------------
  //-------------------------------------------------------------------------------------------------------
    /*Connects and retrieves the access code to box account. A browser is called to get user informations and
     * procced with logon*/
    public void connect() {

    		String code = "";
            String url = "https://www.box.com/api/oauth2/authorize?" +
            			 "response_type=code" +
            			 "&client_id=" + key;
            try {
                Desktop.getDesktop().browse(java.net.URI.create(url));
                //WebViewApplication.show(url);
            	
            	code = getCode();
            } catch (IOException e) {
                e.printStackTrace();
            }

            api = new BoxAPIConnection(key, secret, code);
	}
    
    /*Creates the server to retrieve the access code*/
    private  String getCode() throws IOException {

        ServerSocket serverSocket = new ServerSocket(PORT);
        Socket socket = serverSocket.accept();
        BufferedReader in = new BufferedReader (new InputStreamReader (socket.getInputStream ()));
        while (true)
        {
            String code = "";
            
            try
            {
                BufferedWriter out = new BufferedWriter (new OutputStreamWriter (socket.getOutputStream ()));
                out.write("HTTP/1.1 200 OK\r\n");
                out.write("Content-Type: text/html\r\n");
                out.write("\r\n");

                code = in.readLine ();
                System.out.println (code);
                String match = "code";
                int loc = code.indexOf(match);

                if( loc >0 ) {
                    int httpstr = code.indexOf("HTTP")-1;
                    code = code.substring(code.indexOf(match), httpstr);
                    String parts[] = code.split("=");
                    code=parts[1];
                    out.write("Close this window!");
                } else {
                    // It doesn't have a code
                    out.write("Code not found in the URL!");
                }

                out.close();

                return code;
                
            }
            catch (IOException e)
            {
                //error ("System: " + "Connection to server lost!");
                System.exit (1);
                break;
            }
        }
        return "";
    }



    /*Wraper for general download*/
	public void download(final TreeItem item) throws IOException{
		
		BoxResource br =  item.res;
		
		if(br instanceof BoxFolder){
			System.out.println("Download Folder:" + item.path);
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					downloadFileTree(item, null);
				}
			}).start();
			
		}else if(br instanceof BoxItem){
			System.out.println("Download Single file");
			downloadItem(item);
		}
	}

	private void downloadItem(final TreeItem  item) throws IOException{
		
		
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				final String id = item.id;
				
				try {
					treeDownloadSemaphore.acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try{

			    	BoxFile bfile = new BoxFile(api, id);
			    	BoxFile.Info info = bfile.getInfo();
	

			    	List<BoxFolder> parents = info.getPathCollection();
			    	String path = syncDir;
			    	for (BoxFolder boxFolder : parents) {
						path +="/" + boxFolder.getInfo().getName();
					}

			    	new File(path).mkdirs();
			    	path += "/" +info.getName();

			    	File file = new File(path);
			    	
			    	long diff = bfile.getInfo().getModifiedAt().getTime() - file.lastModified();
			    	
			    	if(file.exists() && (diff) <= 30*1000){
			    		System.out.println("The file is recent in local path. canceling: " + "::" +file.getAbsolutePath());
			    		treeDownloadSemaphore.release();
			    		return;
			    	}
			    	
			    	System.out.println("Baixando:" + file.getName());
			    	FileOutputStream stream = new FileOutputStream(file);
			    	bfile.download(stream,new ProgressListener() {
						
						@Override
						public void onProgressChanged(long arg0, long arg1) {
							if(arg0%20 ==0)
								System.out.print(".");
						}
					});
			    	
			    	file.setLastModified(bfile.getInfo().getModifiedAt().getTime());

			    	stream.close();
			    	
			    	
			    	ajusteCreationTime(bfile.getInfo().getCreatedAt(),path);
			    	item.prefix = "";
			    	item.relatedFile = file;
			    	System.out.println("\nFile:" + info.getName() + " Done");
			    	notifyItemDownloaded(item);
				}catch(Exception ex){
					System.out.println("Erro ao realizar o download:@" + id);
					ex.printStackTrace();
					
				}
				
				treeDownloadSemaphore.release();
			}



			private void ajusteCreationTime(Date createdAt,String file) {
				if(SYSTEM==1){ //Unix
					
					String command = "touch -t" + sdf.format(createdAt) +" \"" + file + "\"";
					System.out.println(command);
					try {
						Runtime.getRuntime().exec(command );
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}).start();
    	
    } 
    
    
	private void notifyItemDownloaded(TreeItem item) {

		for (ActionListener actionListener : downloadedEvents) {
			actionListener.actionPerformed(new ActionEvent(item, 0, null));
		}
	}
    private TreeItem downloadFileTree(TreeItem curTop, String id){
		BoxFolder folder = (BoxFolder) curTop.res;
		    	
		System.out.println("Baixando nó:" + folder.getInfo().getName());
		
		TreeItem curDir = curTop;
		
		for (int i=0;i<curTop.getChildCount();i++) {
			TreeItem ti = (TreeItem) curTop.getChildAt(i);
			
			BoxResource br = ti.res;
			
			if(br == null) continue;
			
			
			if(br instanceof BoxFolder){
				downloadFileTree(ti,br.getID());
				
			} else if(br instanceof BoxItem){
				BoxItem item = (BoxItem) br;
				
				try {
					
					downloadItem(ti);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	
		return curDir;   	
	}

	public void upload(final TreeItem item) throws IOException{
    	BoxResource br = item.res;
    	
    	if(br == null){ //The file only exists locally
	    	if(item.relatedFile.isDirectory()){
	    		System.out.println("Upload Folder:" + item.path);
	    		uploadFileTree(item, null);
	    	}else if(item.relatedFile.isFile()){
	    		System.out.println("Upload Single file");
	    		uploadNewItem(item);
	    	}
    	}else{
    		if(br instanceof BoxFolder){
    			System.out.println("Upload Folder:" + item.path);
    			uploadFileTree(item, null);
    		}else if(br instanceof BoxItem){
    			System.out.println("Update File");
    			updateRemoteFile(item);
    		}   		
    	}
    }

	private void updateRemoteFile(final TreeItem item) throws FileNotFoundException {
		BoxFile bf = (BoxFile) item.res;
		
		long diff = item.relatedFile.lastModified() - ((BoxFile)item.res).getInfo().getModifiedAt().getTime();
		
		if(diff<=30*1000){
			System.out.println("O arquivo remoto é o mais atualizado. cancelando: " + item.name);
			return;
		}
		
		bf.uploadVersion(new FileInputStream(item.relatedFile),
						 new Date(item.relatedFile.lastModified()),
						 1024, 
						 new ProgressListener() {
							    public void onProgressChanged(long numBytes, long totalBytes) {
								       System.out.print(".");
								    }
						 });
		
		item.prefix = "";
		
		System.out.println("Atualizando : "  + item.name);
	}
   
	private void uploadNewItem(TreeItem item) throws IOException {
		TreeItem folder = (TreeItem) item.getParent();
		File f = item.relatedFile;
		FileInputStream stream = new FileInputStream(f);
		
		BoxFolder rootFolder = (BoxFolder) folder.res;
		
		FileUploadParams fup = new FileUploadParams();
		fup.setContent(stream);
		fup.setCreated(new Date(f.lastModified()));
		fup.setModified(new Date(f.lastModified()));
		fup.setName(f.getName());
		fup.setSize(1024);
		fup.setProgressListener( new ProgressListener() {
		    public void onProgressChanged(long numBytes, long totalBytes) {
			       System.out.print(".");
			    }
			});
		
		BoxFile.Info info = rootFolder.uploadFile(fup);
		
		item.id = info.getID();
		item.res = info.getResource();
		item.prefix = "";
		System.out.println("Done");
		
		stream.close();
	}
	
    private void uploadFileTree(TreeItem item, Object object) throws IOException {
    	BoxResource br = item.res;

    	boxMakeDirExtended(item);
    	
    	Enumeration<TreeItem> en = item.children();
    	
    	while(en.hasMoreElements()){
    		TreeItem it = en.nextElement();
    		
    		if(it.res == null){ //The file only exists locally
    	    	if(it.relatedFile.isDirectory()){
    	    		System.out.println("upload Folder:" + it.path);
    	    		uploadFileTree(it, null);
    	    	}else if(it.relatedFile.isFile()){
    	    		System.out.println("Upload Single file");
    	    		uploadNewItem(it);
    	    	}
        	}else{
        		if(it.res instanceof BoxFolder){
        			System.out.println("Download Folder:" + item.path);
        			uploadFileTree(it, null);
        		}else if(it.res instanceof BoxItem){
        			System.out.println("Update File");
        			updateRemoteFile(it);
        		}   		
        	}		
    		
    		
    		
    	}
    	
    

	}

	private void boxMakeDirExtended(TreeItem item) {
		TreeItem parent = item;
    	List<String> upFolders = new ArrayList<String>();
    	
    	while(parent.res == null){
    		 upFolders.add(parent.name);
    		 parent = (TreeItem) parent.getParent();
    	}
    	
    	for (int i = upFolders.size()-1; i >=0 ; i--) {
    		BoxFolder.Info childFolderInfo = ((BoxFolder) parent.res).createFolder(upFolders.get(i));
    		
    		parent = (TreeItem) parent.getChildAt(parent.getChildIndexByName(upFolders.get(i)));
    		parent.id = childFolderInfo.getID();
    		parent.res = childFolderInfo.getResource();
    		parent.prefix = "";
		}
	}

	/*Loads the tree in curTop node*/
    private TreeItem getFileTree(TreeItem curTop, String id) throws IOException{

    	BoxFolder folder=null;
    	
    	if(curTop==null){
    		return null;
    	}else{
    		folder = (BoxFolder) curTop.res;
    	}
    	
		TreeItem curDir = curTop;

		//Feching server info.
		//For each file or folder add to curtop
		//if folder, recursively load inners files
		BufferedWriter remoteDirInfoStream = null;
	
		remoteDirInfoStream =  new BufferedWriter(new FileWriter(curDir.path + "/.boxSync.remoteInfo"));
		
		
		for (BoxItem.Info itemInfo : folder) {
			remoteDirInfoStream.write(itemInfo.getName() + "\n");
			
			BoxItem item = (BoxItem) itemInfo.getResource();	
			TreeItem folderNode = new TreeItem(item);
			curDir.add(folderNode); //Adding to cur top
			
			if(itemInfo instanceof BoxFile.Info){
				//as the file aready it was load nothing left to do
				System.out.println("Adicionando item: " + itemInfo.getName());
			
			}else if(itemInfo instanceof BoxFolder.Info){
				//recursive adding in another thread
				System.out.println("Pasta adicionada." + itemInfo.getName());
				FileTreeSearcher fts = new FileTreeSearcher(folderNode, null);
				fts.start();
			}
		}
		remoteDirInfoStream.close();
		
		System.out.println("Verify new local files in: " + curDir.path);
		//Procurando por arquivos existentes apenas no diretorio local
		
		loadLocalFiles(curDir);

		//Search for remote deleted filest

		notifyFolderLoaded(curDir);
		return curDir;
    }

	private void loadLocalFiles(TreeItem curDir) throws IOException {
		BufferedWriter localDirInfoStream = null;
		localDirInfoStream =  new BufferedWriter(new FileWriter(curDir.path + "/.boxSync.localInfo"));
		
		File folderFile = new File(curDir.path);
		
		if(folderFile.exists()){
			File files[] = folderFile.listFiles();
			System.out.println(Arrays.toString(folderFile.list()));
			for (File f : files) {
				if(f.getName().compareTo(".boxSync.localInfo")==0 || f.getName().compareTo(".boxSync.remoteInfo")==0)
					continue;
				
				
				localDirInfoStream.write(f.getName() + "\n");
				
				System.out.println("Verifying : " + f.getName());
				boolean found = false;
				
				for (int i = 0; i < curDir.getChildCount(); i++) {
					TreeItem remoteFileNode = (TreeItem) curDir.getChildAt(i);
					
					if(remoteFileNode.name.compareTo(f.getName())==0){
						found = true;
						System.out.println("Local Copy found: " + f.getName());
						break;
					}
				}
				
				if(!found && files.length>0){
					System.out.println("new local file: " + f.getName());
					TreeItem newNode = new TreeItem(f);
					curDir.add(newNode);
					
					if(f.isFile()){
						continue;
					}else{
						loadLocalFiles(newNode);
					}
				}
			}
		}
		localDirInfoStream.close();
	}

    /*FIXME adicionar os parâmetros do action Listenner*/
    private void notifyFolderLoaded(TreeItem curDir){
    	for (ActionListener actionListener : folderLoadedActionListenner) {
			actionListener.actionPerformed(new ActionEvent(curDir, 0, null));
		}
    }
    // FIXME verify if a semaphore is needed
    private class FileTreeSearcher extends Thread{
    	
    	
    	TreeItem root;
    	String id;
    	
    	public FileTreeSearcher(TreeItem aroot, String aid) {
    		root = aroot;
    		id = aid;
    				
		}

		@Override
		public void run() {
			try {
				nInstances++;
				treeCreationSemaphore.acquire();
				System.out.println("N Instances " + nInstances);
				getFileTree(root, id);
				nInstances--;
			} catch (Exception e) {
			   e.printStackTrace();
			}
			treeCreationSemaphore.release();
		}
    }
    
    
	public class TreeItem extends DefaultMutableTreeNode{
		public String path;
		public String id;
		public String name;
		public BoxResource res;
		public String prefix = "";
		public File relatedFile;
		
		public TreeItem(BoxResource r) {
			
			String aName = "";
			String aId = r.getID();

			if(r instanceof BoxFolder){
				BoxFolder bf = (BoxFolder) r;
				aName = bf.getInfo().getName();
				res = bf;
				
			}else if(r instanceof BoxItem){
				BoxItem bi = (BoxItem) r;
				aName = bi.getInfo().getName();
				res = bi;
			}
			
			name = aName;
			id = aId;
		}

		public int getChildIndexByName(String string) {
			Enumeration en = this.children();
			int i = -1;
			while(en.hasMoreElements()){
				i++;
				TreeItem ti = (TreeItem) en.nextElement();
				if(ti.name.compareTo(string)==0)
					break;
			}
			return i;
		}

		public TreeItem(File r) {
			
			String aName = r.getName();
			String aId = "";

			res = null;
			name = aName;
			id = aId;
		}
		
		@Override
		public String toString() {
			return prefix + name + " @" + id;
		}
		
		@Override
		public void add(MutableTreeNode newChild) {
			super.add(newChild);
			String aprefix = "";
			
			TreeItem it = (TreeItem) newChild;
			it.path = this.path + "/" + it.name;

			File f = new File(it.path);
			
			if(!f.exists()){
				aprefix = "[*]";
				newRemoteNodes.addElement((TreeItem) newChild);
				System.out.println(f.getAbsolutePath() + " does not exists in local disk.");
				
			}else{
				((TreeItem)newChild).relatedFile = f;
				System.out.println(f.getAbsolutePath() + " exists in local disk.");
				long remoteFileModificationDate = 0;
				
				if(it.res != null){
					if((it.res instanceof BoxFolder) == false ){
						BoxItem bi = (BoxItem) it.res;
						remoteFileModificationDate = bi.getInfo().getModifiedAt().getTime();
					
						long localFileModificationDate = f.lastModified();
						
						if(localFileModificationDate > remoteFileModificationDate){
							aprefix = "[u]";
							changedLocal.addElement((TreeItem) newChild);
							System.out.println(f.getAbsolutePath() + " modified local. Date:" + sdf.format(f.lastModified()));
							
						} else if(localFileModificationDate < remoteFileModificationDate){
							aprefix = "[d]";
							changedRemote.addElement((TreeItem) newChild);
							System.out.println(f.getAbsolutePath() + " modified remote. Date:" + sdf.format(f.lastModified()));
						}
					}
				}else{
					aprefix = "[*L]";
					newLocalNodes.addElement((TreeItem) newChild);
				}
				
				
			}
			
			
			((TreeItem) newChild).prefix = aprefix;
		}
	}
 

	public void setRootNodeID(String id) {
    	BoxFolder folder = new BoxFolder(api, id);
    	
		System.out.println("Adicionando nó raiz:" + folder.getInfo().getName());
		rootNode = new TreeItem(folder);
		
		
		
		//Construindo path
		List<BoxFolder> parents = folder.getInfo().getPathCollection();
		String path = syncDir;
		
    	for (BoxFolder boxFolder : parents) {
			path +="/" + boxFolder.getInfo().getName();
		}
		
    	path+="/" + folder.getInfo().getName();
    	rootNode.path = path;
    	rootNode.relatedFile = new File(path);
	}

	public void loadFileTree() throws IOException {
		this.getFileTree(rootNode, null);
	}
}
