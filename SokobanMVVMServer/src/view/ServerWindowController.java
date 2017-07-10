package view;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import server.ServerController;
/**
 * the view in architecture MVVM Observer the VM
 * <br>if need add observable or use bind to other side
 * @see start(Stage primaryStage,ServerController vm) get the VM and sate and init them
 * 
 * */
public class ServerWindowController implements Observer,iView{
	
	 List<String[]> data;
	private TableView<String[]> clientList;
	private Stage primaryStage;
	private Scene scene;
	private ServerController vm;
	

	public ServerWindowController() {
		
		this.data= new SimpleListProperty<>();
        
		// Create the table and columns
        clientList = new TableView();
        TableColumn<String[],String> nameColumn = new TableColumn();
        nameColumn.setText("ID");

        TableColumn<String[],String> valueColumn = new TableColumn();
        valueColumn.setText("Client");
        clientList.getColumns().addAll(nameColumn,valueColumn);

        
        nameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<String[], String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<String[], String> p) {
                String[] x = p.getValue();
                if (x != null && x.length>0) {
                    return new SimpleStringProperty(x[0]);
                } else {
                    return new SimpleStringProperty("<no name>");
                }
            }
        });

        valueColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<String[], String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<String[], String> p) {
                String[] x = p.getValue();
                if (x != null && x.length>1) {
                    return new SimpleStringProperty(x[1]);
                } else {
                    return new SimpleStringProperty("<no value>");
                }
            }
        });
        
      //Insert Button
        TableColumn col_action = new TableColumn<>("Action");
        
        col_action.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<String[],String[]>, 
                ObservableValue<String[]>>() {

            @Override
            public ObservableValue<String[]> call(TableColumn.CellDataFeatures<String[],String[]> p) {
                return new ReadOnlyObjectWrapper<String[]>(p.getValue());
            }
        });
        
        col_action.setCellFactory(
                new Callback<TableColumn<String[],String[]>, TableCell<String[], String[]>>() {

            @Override
            public TableCell<String[],String[]> call(TableColumn<String[],String[]> clientName) {
            	ClientAction btn= new ClientAction();
                return btn;
            }
        
        });
        
        clientList.getColumns().add(col_action);

        // Finish setting the stage
        StackPane root = new StackPane();
        
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.getChildren().addAll(clientList);
        root.getChildren().add(vBox);
        
        //root.getChildren().addAll(btnNew,clientList);
       this.scene = new Scene(root, 300, 250);
        

	}
	/**get the VM and sate and init them*/
	public void start(Stage primaryStage,ServerController vm){
	
		//resultLabel.textProperty().bind(vm.result.asString());
		this.vm=vm;
		
		this.primaryStage=primaryStage;
		this.primaryStage.setTitle("Client list");
        this.primaryStage.setScene(this.scene);
        
		
		
	}
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	
	private class ClientAction extends TableCell<String[],String[]> {
	      final Button cellButton = new Button("user level");
	      String[] clienName;
	      
	      ClientAction(){
	          
	      	//Action when the button is pressed
	          cellButton.setOnAction(new EventHandler<ActionEvent>(){

	              @Override
	              public void handle(ActionEvent t) {
	              	System.out.println(clienName[0]);
	              	vm.killClient(Integer.parseInt(clienName[0]));
	              }
	          });
	      }

	      //Display button if the row is not empty
	      @Override
	      protected void updateItem(String[] client, boolean empty) {
	         super.updateItem(client, empty);
	         if (client!=null){
	      	   setGraphic(cellButton);
	      	   cellButton.setText("kill");
	      	   this.clienName=client;
	         }
	      }
	}
	
	public void update(Observable o, Object arg) {
		this.data=this.vm.getData();
		clientList.getItems().clear();
    	clientList.refresh();
    	clientList.getItems().addAll(data);
	}

	
}
