package btd;

import java.io.IOException;

import btd.model.Depot;
import btd.model.Line;
import btd.model.Stop;
import btd.model.Vehicle;
import btd.model.Driver;
import btd.view.*;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.controlsfx.dialog.Dialogs;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private Connection conn = null;
    Layout layoutController;

    public MainApp() {
    }
    
    /**
     * Connects to DataBase
     */
    public void connectToDB() {
		Properties connectionProps = new Properties();
		connectionProps.put("user", "inf109765");
		connectionProps.put("password", "haslo112");
		try {
			conn = DriverManager.getConnection(
					"jdbc:oracle:thin:@localhost:1521/xe",
					connectionProps);
			System.out.println("Po³¹czono z baz¹ danych");
		} catch (SQLException ex) {
			Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE,
					"nie uda³o siê po³¹czyæ z baz¹ danych", ex);
			Dialogs.create()
            .title("B³¹d bazy danych")
            .masthead("")
            .message("Nie uda³o siê po³¹czyæ z baz¹ danych.")
            .showError();
			System.exit(-1);
		}
    }
    
    /**
     * Disconnects from DataBase
     */
    public void disconnectFromDB() {
    	if (conn != null) {
	    	try {
				conn.close();
			} catch (SQLException ex) { 
				Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
			}
			System.out.println("Od³¹czono od bazy danych");
    	}
    }
    
    /**
     * Initializes the root layout.
     */
    public void initLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/Layout.fxml"));
            rootLayout = (BorderPane) loader.load();
            
            layoutController = loader.getController();
            layoutController.setMainApp(this);

            // Show the scene containing the root layout.
            //Scene scene = new Scene(rootLayout);
            //primaryStage.setScene(scene);
            //primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Shows the root layout.
     */
    public void showLayout() {
    	// Show the scene containing the root layout.
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Initializes and shows the splash layout while connecting to DB.
     */
    public void initSplashLayout() {
    	StackPane sp = new StackPane();
        Image img = new Image("file:img/javafx_logo_color_1.jpg");
        ImageView imgView = new ImageView(img);
        sp.getChildren().add(imgView);

        //Adding HBox to the scene
        Scene scene = new Scene(sp);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Executes on app start
     */
    @Override
    public void start(Stage primaryStage) {
    	// TODO tu mozna dac jakis ekran ze sie laczy z baza
    
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Zajezdnie");    
        this.primaryStage.getIcons().add(new Image("file:img/icon.png"));
        
        initLayout();
        
        //initSplashLayout();
        
        connectToDB();
        layoutController.loadFromDB();
        
        showLayout();
    }
    
    
    /**
     * Opens a dialog to edit details for the specified Line. If the user
     * clicks OK, the changes are saved into the provided Line object and true
     * is returned.
     * 
     * @param line the line object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showLineEditDialog(Line line, ObservableList<Stop> stopsData, boolean disableNumber) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/LineEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edytuj Liniê");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            LineEditDialog controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setLine(line, stopsData, conn, disableNumber);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Executes before app stops
     */
    @Override 
    public void stop() throws Exception {
    	disconnectFromDB();
      }
    
    /**
     * Opens a dialog to edit details for the specified Stop. If the user
     * clicks OK, the changes are saved into the provided Stop object and true
     * is returned.
     * 
     * @param stop the stop object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showStopEditDialog(Stop stop, ObservableList<Stop> stopsData) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/StopEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edytuj Przystanek");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            StopEditDialog controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setStop(stop, stopsData, conn);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Opens a dialog to edit details for the specified Stop. If the user
     * clicks OK, the changes are saved into the provided Stop object and true
     * is returned.
     * 
     * @param stop the stop object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showDepotEditDialog(Depot depot, boolean editTyp) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/DepotEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edytuj Zajezdniê");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            DepotEditDialog controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setDepot(depot, editTyp, conn);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean showVehicleEditDialog(Vehicle vehicle, boolean editMode, boolean setDepot, ObservableList<Depot> depotsData) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/VehicleEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edytuj Pojazd");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            VehicleEditDialog controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setVehicle(vehicle, conn, editMode, setDepot, depotsData);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean showDriverEditDialog(Driver dr, boolean editMode, boolean setDepot, ObservableList<Depot> depotsData, ObservableList<Line> linesData) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/DriverEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edytuj Kierowcê");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            DriverEditDialog controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setDriver(dr, conn, editMode, setDepot, depotsData, linesData);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    public Connection getConn() {
        return conn;
    }
}