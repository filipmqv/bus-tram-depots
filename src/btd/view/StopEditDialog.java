package btd.view;

import java.sql.Connection;

import org.controlsfx.dialog.Dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import btd.model.Stop;
import btd.util.DBUtil;


public class StopEditDialog {
	
	private Connection conn;

    @FXML private TextField nazwaField;
    @FXML private AnchorPane przystanekAddAnchorPane2;
    @FXML private Canvas przystanekAddCanvas2;
    private GraphicsContext przystanekgc;

    private Stage dialogStage;
    private Stop stop;
    private boolean okClicked = false;
    
    private double lastXclicked;
    private double lastYclicked;
    private ObservableList<Stop> stopsData = FXCollections.observableArrayList();
    
    private Image mapImg;
    private int firstMove;
    
    // draw stops of this size on map and move them up and left by MOVE px
 	private final double STOPSIZE=16.0;
 	private final double MOVE=STOPSIZE/2;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	firstMove=0; 
    	
    	mapImg = new Image("file:img/mymap2560.png", true);
    	
    	przystanekgc = przystanekAddCanvas2.getGraphicsContext2D();
    	
    	przystanekAddCanvas2.addEventHandler(MouseEvent.MOUSE_CLICKED, 
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                    	lastXclicked = t.getX();
                    	lastYclicked = t.getY();
                    	//System.out.println(t.getX()+ " " +t.getY() );
                    	draw(); 
                    }
                });
    }
    
    public void draw() {
    	przystanekgc.setLineWidth(5);
    	przystanekgc.setFill(Color.WHITESMOKE);
    	przystanekgc.fillRect(0,0,12560,12560);
    	przystanekgc.drawImage(mapImg, 0, 0);
    	
    	// Draw all stops
    	przystanekgc.setFill(Color.WHITESMOKE);
    	przystanekgc.setStroke(Color.DARKSLATEGRAY);
    	przystanekgc.setLineWidth(5);
    	for (Stop s : stopsData) {
    		przystanekgc.fillOval(s.getX()-MOVE, s.getY()-MOVE, STOPSIZE, STOPSIZE);
    		przystanekgc.strokeOval(s.getX()-MOVE, s.getY()-MOVE, STOPSIZE, STOPSIZE);
		}
    	
    	//draw selected stop
    	przystanekgc.setFill(Color.WHITESMOKE);
    	przystanekgc.setStroke(Color.RED);
    	przystanekgc.fillOval(lastXclicked-MOVE, lastYclicked-MOVE, STOPSIZE, STOPSIZE);
		przystanekgc.strokeOval(lastXclicked-MOVE, lastYclicked-MOVE, STOPSIZE, STOPSIZE);
    }
    
    @FXML
    public void drawFirst() {
    	if (firstMove<100) {
    		draw();
    		firstMove++;
    	}
    }

    /**
     * Sets the stage of this dialog.
     * 
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Sets the person to be edited in the dialog.
     * 
     * @param person
     */
    public void setStop(Stop stop, ObservableList<Stop> stopsData, Connection conn) {
        this.stop = stop;
        this.conn = conn;
        this.stopsData = stopsData;
        lastXclicked = stop.getX();
        lastYclicked = stop.getY();
        
        nazwaField.setText(stop.getNazwa());
        
        //birthdayField.setText(DateUtil.format(person.getBirthday()));
        //birthdayField.setPromptText("dd.mm.yyyy");
    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     * 
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handleOk() {
        if (isInputValid()) {
        	if (stop.getIdPrzystanku() == 0) stop.setIdPrzystanku(DBUtil.getNextID(conn));
            stop.setNazwa(nazwaField.getText());
            stop.setX(lastXclicked);
            stop.setY(lastYclicked);

            okClicked = true;
            dialogStage.close();
        }
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * Validates the user input in the text fields.
     * 
     * @return true if the input is valid
     */
    private boolean isInputValid() {
        String errorMessage = "";

        if (nazwaField.getText() == null || nazwaField.getText().length() == 0) {
            errorMessage += "Podana nazwa przystanku jest niepoprawna!\n"; 
        }
        
        if (nazwaField.getText() != null && nazwaField.getText().length() > 30) {
            errorMessage += "Podana nazwa przystanku jest za d³uga! Dopuszczalne max 30 znaków\n"; 
        }
        
        if (lastXclicked==0 || lastYclicked == 0) {
        	errorMessage += "Nie ustawiono lokalizacji przystanku! Kliknij na mapie, aby ustawiæ przystanek.\n"; 
        }
        

        if (errorMessage.length() == 0) {
            return true;
        }
        else {
            // Show the error message.
        	Dialogs.create()
	            .title("Niepoprawne dane")
	            .masthead("Popraw b³êdne dane")
	            .message(errorMessage)
	            .showError();
            return false;
        }
    }
    
    
}