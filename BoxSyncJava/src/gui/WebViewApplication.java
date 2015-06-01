package gui;


import java.io.IOException;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.JFrame;


public class WebViewApplication {

    public static void show(final String url){
    	Platform.runLater(new Runnable() {  
            @Override
            public void run() {  
		        StackPane root = new StackPane();
		 
		        JFXPanel jfxPanel = new JFXPanel();  
		        
		        WebView view = new WebView();
		        WebEngine engine = view.getEngine();
		        System.out.println("Loading:" + url);
		        engine.load(url);
		        root.getChildren().add(view);
		        
		        Scene scene = new Scene(root, 800, 600);
		
		        jfxPanel.setScene(scene);
		        
		        JFrame f = new JFrame();
		        f.add(jfxPanel);
		        f.setVisible(true);
            }});
    }
 
    public static void main(String[] args) throws IOException {
    	

    	WebViewApplication.show("http://www.google.com.br/");
 
        
		
		System.out.println("Main thread done");
	}
}