package view;
	
import java.util.List;

import ModelPackeg.MyModel;
import ModelPackeg.iModel;
import javafx.application.Application;
import javafx.stage.Stage;
import model.db.SokobanDBManager;
import model.db.iDBManager;
import server.ServerController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;

/**
 *@param  args arg[0] = -Djava.library.path="lib" connect to DB
 *@param  args args[1] = port , num port to server 
 * */

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Sample.fxml"));				 
			BorderPane root = (BorderPane) loader.load();
			ServerWindowController view= loader.getController();
			
			//server
			iDBManager DBM =new SokobanDBManager();
			List<String> args=getParameters().getRaw();
			int port = Integer.parseInt(args.get(1));
			System.out.println(port);
			iModel model=new MyModel(DBM);
			ServerController server=new ServerController(port, model);
			
			view.start(primaryStage,server);
			
			server.addObserver(view);
			
			try {
				server.start();
				primaryStage.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
