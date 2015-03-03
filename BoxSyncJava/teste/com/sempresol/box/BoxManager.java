package com.sempresol.box;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;

import org.junit.Test;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;
import com.box.sdk.BoxResource;
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
    
    public List<TreeItem> updateNodes = new ArrayList<BoxManager.TreeItem>();
    public List<TreeItem> deprecatedNodes = new ArrayList<BoxManager.TreeItem>();
    public List<TreeItem> newRemoteNodes = new ArrayList<BoxManager.TreeItem>();
    public List<TreeItem> newLocalNodes = new ArrayList<BoxManager.TreeItem>();
    
    public void connect() {

    		String code = "";
            String url = "https://www.box.com/api/oauth2/authorize?" +
            			 "response_type=code" +
            			 "&client_id=" + key;
            try {
                Desktop.getDesktop().browse(java.net.URI.create(url));
                code = getCode();
            } catch (IOException e) {
                e.printStackTrace();
            }

            api = new BoxAPIConnection(key, secret, code);
	}
    
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

    
    public void download(String path,String id) throws IOException{
    	BoxFile file = new BoxFile(api, id);
    	
    	BoxFile.Info info = file.getInfo();

    	FileOutputStream stream = new FileOutputStream(path + info.getName());
    	file.download(stream);
    	stream.close();
    }

    public void download(final String id) throws IOException{
    	
    	new Thread(new Runnable() {
			
			@Override
			public void run() {

				try{
			    	BoxFile bfile = new BoxFile(api, id);
			    	BoxFile.Info info = bfile.getInfo();
	
			    	List<BoxFolder> parents = info.getPathCollection();
			    	String path = syncDir;
			    	for (BoxFolder boxFolder : parents) {
						path +="/" + boxFolder.getInfo().getName();
					}
			    	
			    	System.out.println("Path:" + path);
			    	System.out.println("File:" + info.getName());
			    	
			    	new File(path).mkdirs();
			    	path += "/" +info.getName();

			    	ajusteCreationTime(bfile.getInfo().getCreatedAt(),path);
			    	
			    	
			    	File file = new File(path);
			    	FileOutputStream stream = new FileOutputStream(file);
			    	bfile.download(stream,new ProgressListener() {
						
						@Override
						public void onProgressChanged(long arg0, long arg1) {
							System.out.print(".");
						}
					});
			    	
			    	file.setLastModified(bfile.getInfo().getModifiedAt().getTime());

			    	stream.close();
				}catch(Exception ex){
					System.out.println("Erro ao realizar o download:@" + id);
					ex.printStackTrace();
					
				}
				
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
    
    
    public TreeItem downloadFileTree(TreeItem curTop, String id){
    	BoxFolder folder = (BoxFolder) curTop.res;
    	    	
		System.out.println("Baixando nó:" + folder.getInfo().getName());
		
		
		TreeItem curDir = curTop;
		
		for (int i=0;i<curTop.getChildCount();i++) {
			TreeItem ti = (TreeItem) curTop.getChildAt(i);
			
			BoxResource br = ti.res;
			
			
			if(br instanceof BoxFolder){
				downloadFileTree(ti,br.getID());
				
			} else if(br instanceof BoxItem){
				BoxItem item = (BoxItem) br;
				
				try {
					System.out.println("Baixando item:" + item.getInfo().getName());
					download(item.getID());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}

		return curDir;   	
    }
    
    
    
    public TreeItem getFileTree(TreeItem curTop, String id){
    	
    	BoxFolder folder = new BoxFolder(api, id);
    	    	
		System.out.println("Adicionando nó:" + folder.getInfo().getName());
		TreeItem curDir = new TreeItem(folder);

		if (curTop != null) { // should only be null at root
			curTop.add(curDir);
		}else{
			curDir = rootNode;
	    	List<BoxFolder> parents = folder.getInfo().getPathCollection();
	    	String path = syncDir;
	    	for (BoxFolder boxFolder : parents) {
				path +="/" + boxFolder.getInfo().getName();
			}
			
	    	path+="/" + folder.getInfo().getName();
	    	curDir.path = path;
		}
		
		for (BoxItem.Info itemInfo : folder) {
  
			if(itemInfo instanceof BoxFile.Info){
				BoxItem item = (BoxItem) itemInfo.getResource();	
				curDir.add(new TreeItem(item));
				System.out.println("Adicionando item: " + item.getInfo().getName());
			}else if(itemInfo instanceof BoxFolder.Info){
				//getFileTree(curDir,itemInfo.getID());
				FileTreeSearcher fts = new FileTreeSearcher(curDir, itemInfo.getID());
				fts.start();
			}
		}
		
		System.out.println("Verify new local files in: " + curDir.path);
		//Procurando por arquivos existentes apenas no diretorio local
		File folderFile = new File(curDir.path);
		
		if(folderFile.exists()){
			File files[] = folderFile.listFiles();
			
			for (File f : files) {
				System.out.println("Verifying : " + f.getAbsolutePath());
				boolean found = false;
				
				for (int i = 0; i < curDir.getChildCount(); i++) {
					TreeItem remoteFileNode = (TreeItem) curDir.getChildAt(i);
					
					if(remoteFileNode.name.compareTo(f.getName())==0){
						found = true;
						System.out.println("Local Copy found: " + f.getAbsolutePath());
						break;
					}else{
						System.out.println("|" + remoteFileNode.name + "<>" + f.getName() + "|");
					}
				}
				
				if(!found && files.length>0){
					System.out.println("new local file: " + f.getAbsolutePath());
					TreeItem newNode = new TreeItem(f);
					curDir.add(newNode);
				}
			}
		}
		
		
		
		
		//Search for remote deleted filest

		return curDir;   	
    }

    private class FileTreeSearcher extends Thread{
    	TreeItem root;
    	String id;
    	
    	public FileTreeSearcher(TreeItem aroot, String aid) {
    		root = aroot;
    		id = aid;
    				
		}

		@Override
		public void run() {
			getFileTree(root, id);
		}
    }
    
    
	public class TreeItem extends DefaultMutableTreeNode{
		public String path;
		public String id;
		public String name;
		public BoxResource res;
		public String prefix = "";
		
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
				newRemoteNodes.add((TreeItem) newChild);
			}else{
				
				long remoteFileModificationDate = 0;
				
				if(it.res != null){
					BoxItem bi = (BoxItem) it.res;
					remoteFileModificationDate = bi.getInfo().getModifiedAt().getTime();
				
					long localFileModificationDate = f.lastModified();
					
					if(localFileModificationDate > remoteFileModificationDate){
						aprefix = "[u]";
						updateNodes.add((TreeItem) newChild);
						
					} else if(localFileModificationDate < remoteFileModificationDate){
						aprefix = "[d]";
						deprecatedNodes.add((TreeItem) newChild);
					}
					
					if(it.res instanceof BoxFolder)
						aprefix = "";				
				
				}else{
					aprefix = "[*u]";
				}

			}
			
			prefix = aprefix;
		}
	}
  
	@Test
	public void teste() {
		connect();
		getFileTree(null, "0");

	}

	public void setRootNodeID(String id) {
    	BoxFolder folder = new BoxFolder(api, id);
    	
		System.out.println("Adicionando nó raiz:" + folder.getInfo().getName());
		rootNode = new TreeItem(folder);
		
	}

	public void loadFileTree(String id) {
		this.getFileTree(null, id);
	}
}
