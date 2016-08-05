package btd.view;

import java.sql.Connection;

import org.controlsfx.dialog.Dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import btd.model.Line;
import btd.model.Route;
import btd.model.Stop;
import btd.util.DBUtil;
import btd.util.DateTimeUtil;
import btd.util.FieldUtil;


public class LineEditDialog {

	private Connection conn;
	
	@FXML private TableView<Route> trasaTab1;
    @FXML private TableColumn<Route, Number> lpCol1;
    @FXML private TableColumn<Route, Number> czasCol1;
    @FXML private TableColumn<Route, String> nazwaCol1;
	
    @FXML private TextField numerField;
    @FXML private ComboBox<String> typComboBox;
    @FXML private TextField czestotliwoscField;
    @FXML private TextField hrozpField;
    @FXML private TextField minrozpField;
    @FXML private TextField hzakField;
    @FXML private TextField minzakField;
    @FXML private AnchorPane przystanekAddAnchorPane2;
    @FXML private Canvas przystanekAddCanvas2;
    @FXML private Button okBtn;
    private GraphicsContext przystanekgc;

    private Stage dialogStage;
    private Line line;
    private ObservableList<Stop> stopsData = FXCollections.observableArrayList();
    private boolean okClicked = false;
    
    private double lastXclicked;
    private double lastYclicked;
    private Stop selectedStopOnMap;
    private Stop lastSelectedStopOnMap;
    
    private Image mapImg;
    private int firstMove;
    
    private boolean editMode;
    
    // draw stops of this size on map and move them up and left by MOVE px
 	private final double STOPSIZE=16.0;
 	private final double MOVE=STOPSIZE/2;
 	private final double DIST=STOPSIZE;
 	
 	public double distance(double X1, double Y1, double X2, double Y2) {
        return Math.sqrt(
            (X1 - X2) *  (X1 - X2) + 
            (Y1 - Y2) *  (Y1 - Y2)
        );
    }

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
                    	lastSelectedStopOnMap = selectedStopOnMap;
                    	for (Stop stop : stopsData) {
							if (distance(lastXclicked, lastYclicked, stop.getX(), stop.getY()) < DIST) {
								selectedStopOnMap = stop;
								break;
							}
						}
                    	if (lastSelectedStopOnMap == selectedStopOnMap) selectedStopOnMap = null;
                    	
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
    	
    	// Draw stops and line between them
    	przystanekgc.setFill(Color.WHITESMOKE);
    	przystanekgc.setStroke(Color.RED);
    	przystanekgc.setLineWidth(5);
    	Stop lastStop=null;
    	for (int i=0; i<line.getRouteData().size(); i++) {
    		for (Stop s : stopsData ) {
				if (s.getIdPrzystanku() == line.getRouteData().get(i).getIdPrzystanku()) {
					if (lastStop == null) { // it's first stop
						lastStop = s;
					} else {
						przystanekgc.strokeLine(lastStop.getX(), lastStop.getY(), s.getX(), s.getY());
						przystanekgc.fillOval(lastStop.getX()-MOVE, lastStop.getY()-MOVE, STOPSIZE, STOPSIZE);
						przystanekgc.strokeOval(lastStop.getX()-MOVE, lastStop.getY()-MOVE, STOPSIZE, STOPSIZE);
						przystanekgc.fillOval(s.getX()-MOVE, s.getY()-MOVE, STOPSIZE, STOPSIZE);
						przystanekgc.strokeOval(s.getX()-MOVE, s.getY()-MOVE, STOPSIZE, STOPSIZE);
		        		lastStop = s;
					}		
				}
			}	
		}
    	
    	
    	//draw selected stop
    	if (selectedStopOnMap != null) {
	    	przystanekgc.setFill(Color.WHITESMOKE);
	    	przystanekgc.setStroke(Color.RED);
	    	przystanekgc.fillOval(selectedStopOnMap.getX()-MOVE, selectedStopOnMap.getY()-MOVE, STOPSIZE, STOPSIZE);
			przystanekgc.strokeOval(selectedStopOnMap.getX()-MOVE, selectedStopOnMap.getY()-MOVE, STOPSIZE, STOPSIZE);
    	}
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
    public void setLine(Line line, ObservableList<Stop> stopsData, Connection conn, boolean disableNumber) {
        this.line = line;
        this.stopsData = stopsData;
        this.conn = conn;
        numerField.setDisable(!disableNumber);
        editMode=disableNumber;
        
        numerField.setText(Integer.toString(line.getNumer()));
        ObservableList<String> options = FXCollections.observableArrayList(
        	        "A (autobusowa)",
        	        "T (tramwajowa)"
        	    );
        typComboBox.setItems(options);
        typComboBox.getSelectionModel().select(0);
        if (line.getTyp() != null) 
        	if (line.getTyp().equals("T")) 
        		typComboBox.getSelectionModel().select(1);	
        czestotliwoscField.setText(Integer.toString(line.getCzestotliwosc()));
        hrozpField.setText(Integer.toString(line.getPierwszyKurs().getHour()));
        minrozpField.setText(Integer.toString(line.getPierwszyKurs().getMinute()));
        hzakField.setText(Integer.toString(line.getOstatniKurs().getHour()));
        minzakField.setText(Integer.toString(line.getOstatniKurs().getMinute()));
        
        lpCol1.setCellValueFactory(cellData -> cellData.getValue().lpProperty());
    	nazwaCol1.setCellValueFactory(cellData -> cellData.getValue().nazwaProperty());
    	
    	// disable sorting
    	lpCol1.setSortable(false);
    	nazwaCol1.setSortable(false);

    	trasaTab1.setItems(line.getRouteData());
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
            line.setNumer(Integer.parseInt(numerField.getText()));
            if (typComboBox.getSelectionModel().getSelectedItem().equals("T (tramwajowa)") )
            	line.setTyp("T");
            else 
            	line.setTyp("A");
            line.setCzestotliwosc(Integer.parseInt(czestotliwoscField.getText()));
            if (Integer.parseInt(hrozpField.getText())<10) hrozpField.setText("0"+Integer.parseInt(hrozpField.getText()));
            if (Integer.parseInt(minrozpField.getText())<10) minrozpField.setText("0"+Integer.parseInt(minrozpField.getText()));
            if (Integer.parseInt(hzakField.getText())<10) hzakField.setText("0"+Integer.parseInt(hzakField.getText()));
            if (Integer.parseInt(minzakField.getText())<10) minzakField.setText("0"+Integer.parseInt(minzakField.getText()));
            line.setPierwszyKurs(DateTimeUtil.parse("1993-10-24 "+hrozpField.getText()+":"+minrozpField.getText()+":00"));
            line.setOstatniKurs(DateTimeUtil.parse("1993-10-24 "+hzakField.getText()+":"+minzakField.getText()+":00"));
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
        if (numerField.getText() == null || numerField.getText().length() == 0) {
            errorMessage += "Nie podano numeru linii.\n"; 
        } 
        else if (!FieldUtil.isInteger(numerField.getText())) {
        	errorMessage += "Numer musi byæ liczb¹ naturaln¹.\n";
        } 
        else if (Integer.parseInt(numerField.getText())<=0) {
        	errorMessage += "Numer musi byæ liczb¹ wiêksz¹ od 0 [zera].\n";
        }
        else {
        	if (editMode && DBUtil.checkNumer(conn, Integer.parseInt(numerField.getText()))) {
        		errorMessage += "Numer linii jest zajêty! Proszê wpisaæ inny.\n"; 
        	}
        }
        
        if (czestotliwoscField.getText() == null || czestotliwoscField.getText().length() == 0) {
        	errorMessage += "Nie podano czêstotliwoœci kursowania.\n"; 
        }
        
        if (!FieldUtil.isInteger(czestotliwoscField.getText())) {
        	errorMessage += "Czêstotliwoœæ kursowania musi byæ liczb¹ naturaln¹.\n"; 
        }
        else if (Integer.parseInt(czestotliwoscField.getText())<1) {
        	errorMessage += "Czêstotliwoœæ kursowania musi byæ liczb¹ dodatni¹.\n"; 
        }
        
        if (hrozpField.getText() == null || hrozpField.getText().length() == 0) {
        	errorMessage += "Nie podano godziny pierwszego kursu.\n"; 
        }
        
        if (!FieldUtil.isInteger(hrozpField.getText())) {
        	errorMessage += "Godzina pierwszego kursu musi byæ liczb¹.\n"; 
        } 
        else if (!FieldUtil.isIntBetween(0, Integer.parseInt(hrozpField.getText()), 23)) {
        	errorMessage += "Niepoprawna godzina pierwszego kursu.\n"; 
        }
        
        if (minrozpField.getText() == null || minrozpField.getText().length() == 0) {
        	errorMessage += "Nie podano godziny pierwszego kursu.\n"; 
        }
        
        if (!FieldUtil.isInteger(minrozpField.getText())) {
        	errorMessage += "Godzina pierwszego kursu musi byæ liczb¹.\n"; 
        }
        else if (!FieldUtil.isIntBetween(0, Integer.parseInt(minrozpField.getText()), 59)) {
        	errorMessage += "Niepoprawna godzina pierwszego kursu.\n"; 
        }
        
        if (hzakField.getText() == null || hzakField.getText().length() == 0) {
        	errorMessage += "Nie podano godziny ostatniego kursu.\n"; 
        }
        
        if (!FieldUtil.isInteger(hzakField.getText())) {
        	errorMessage += "Godzina ostatniego kursu musi byæ liczb¹.\n"; 
        }
        else if (!FieldUtil.isIntBetween(0, Integer.parseInt(hzakField.getText()), 23)) {
        	errorMessage += "Niepoprawna godzina ostatniego kursu.\n"; 
        }
        
        if (minzakField.getText() == null || minzakField.getText().length() == 0) {
        	errorMessage += "Nie podano godziny ostatniego kursu.\n"; 
        }
        
        if (!FieldUtil.isInteger(minzakField.getText())) {
        	errorMessage += "Godzina ostatniego kursu musi byæ liczb¹.\n"; 
        }
        else if (!FieldUtil.isIntBetween(0, Integer.parseInt(minzakField.getText()), 59)) {
        	errorMessage += "Niepoprawna godzina ostatniego kursu.\n"; 
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
    /*
    @FXML
    private void validatehrozpField() {
    	validateHourField(hrozpField);
    }
    
    @FXML
    private void validatehzakField() {
    	validateHourField(hzakField);
    }
    
    @FXML
    private void validateminrozpField() {
    	validateMinField(minrozpField);
    }
    
    @FXML
    private void validateminzakField() {
    	validateMinField(minzakField);
    }
    
    @FXML
    private void validateczestotliwoscField() {
    	final PseudoClass errorClass = PseudoClass.getPseudoClass("error");
    	if (validateInt(czestotliwoscField)) {
    		if (Integer.parseInt(czestotliwoscField.getText())>0) {
    			czestotliwoscField.pseudoClassStateChanged(errorClass, false); // nie ma bledu
    		} else {
    			czestotliwoscField.pseudoClassStateChanged(errorClass, true); // blad
    		}
    	} 
    }
    
    private void validateHourField(TextField field) {
    	final PseudoClass errorClass = PseudoClass.getPseudoClass("error");
    	if (validateInt(field)) {
    		if (FieldUtil.isIntBetween(0, Integer.parseInt(field.getText()), 23)) {
    			field.pseudoClassStateChanged(errorClass, false); // nie ma bledu
    		} else {
    			field.pseudoClassStateChanged(errorClass, true); // blad
    		}
    	} 
    }
    
    private void validateMinField(TextField field) {
    	final PseudoClass errorClass = PseudoClass.getPseudoClass("error");
    	if (validateInt(field)) {
    		if (FieldUtil.isIntBetween(0, Integer.parseInt(field.getText()), 59)) {
    			field.pseudoClassStateChanged(errorClass, false); // nie ma bledu
    		} else {
    			field.pseudoClassStateChanged(errorClass, true); // blad
    		}
    	} 
    }
    
    @FXML
    private void validatenumerField() {
    	final PseudoClass errorClass = PseudoClass.getPseudoClass("error");
    	if (editMode) {
	    	if (validateInt(numerField) && Integer.parseInt(numerField.getText())>0 &&
	    			!DBUtil.checkNumer(conn, Integer.parseInt(numerField.getText()))) {
	    		numerField.pseudoClassStateChanged(errorClass, false); // nie ma bledu
	    	} else {
	    		numerField.pseudoClassStateChanged(errorClass, true); // or false to unset it
	    	}
    	}
    }
    
    private boolean validateInt(TextField field) { //true- jest intem
    	final PseudoClass errorClass = PseudoClass.getPseudoClass("error");
    	if (FieldUtil.isInteger(field.getText())) { //wszystko ok
    		field.pseudoClassStateChanged(errorClass, false); // or false to unset it
    		return true;
    	} else { //error
    		field.pseudoClassStateChanged(errorClass, true); // or false to unset it
    		return false;
    	}
    }*/
    
    /**
     * Add stop from map to route
     */
    @FXML
    private void handleDodaj() {
    	if (selectedStopOnMap != null) {
	        line.getRouteData().add(new Route(
	        		line.getRouteData().size()+1, 0, 0, 0, selectedStopOnMap.getIdPrzystanku(), 
	        		selectedStopOnMap.getNazwa(), ""));
	        draw();
    	}
    }
    
    @FXML
    private void handleWGore() {
    	handleWGoreWDol(-1);
    }
    
    @FXML
    private void handleWDol() {
    	handleWGoreWDol(1);
    }
    
    private void handleWGoreWDol(int idx) { //idx =1 w dol, idx=-1 w gore
    	// sortowanie na klikniecie w naglówek jest zablokowane
    	Route wybrany = trasaTab1.getSelectionModel().getSelectedItem();
    	if (wybrany!=null) {
    		boolean ok=true;
    		if (trasaTab1.getSelectionModel().getSelectedIndex() == line.getRouteData().size()-1 && 
    				idx == 1) ok=false;
    		if (trasaTab1.getSelectionModel().getSelectedIndex() == 0 && idx == -1) ok=false;
    		if (ok) {
		    	Route doZmiany = line.getRouteData().get(line.getRouteData().indexOf(wybrany)+idx);
		    	wybrany.setLp(wybrany.getLp()+idx);
		    	doZmiany.setLp(doZmiany.getLp()-idx);
		    	line.getRouteData().set(wybrany.getLp()-1, wybrany);
		    	line.getRouteData().set(doZmiany.getLp()-1, doZmiany);
		    	draw();
		    	trasaTab1.getSelectionModel().select( trasaTab1.getSelectionModel().getSelectedIndex()+idx );
    		}
    	}
    }
    
    /**
     * Remove selected stop from route
     */
    @FXML
    private void handleUsun() {
    	Route selected = trasaTab1.getSelectionModel().getSelectedItem();
    	if (selected != null) {
	        line.getRouteData().remove(selected);
	        for (Route r : line.getRouteData()) {
				r.setLp(line.getRouteData().indexOf(r)+1);
			}
	        draw();
    	}
    }
    
    
}