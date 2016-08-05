package btd.view;

import java.sql.Connection;

import org.controlsfx.dialog.Dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import btd.model.Depot;
import btd.model.Line;
import btd.model.Vehicle;
import btd.model.Driver;
import btd.util.DBUtil;


public class DriverEditDialog {

	private Connection conn;
	
    @FXML private TextField imieField;
    @FXML private TextField nazwiskoField;
    @FXML private ComboBox<String> prawoJazdyComboBox;
    @FXML private ComboBox<String> zajezdniaComboBox;
    @FXML private ComboBox<String> pojazdComboBox;
    @FXML private ComboBox<String> numerComboBox;
    
    @FXML private Button okBtn;

    private Stage dialogStage;
    private Driver driver;
    private boolean okClicked = false;
    private ObservableList<Depot> depotsData = FXCollections.observableArrayList();
    private ObservableList<Line> linesData = FXCollections.observableArrayList();
    
    //private boolean editMode;
    private boolean allowChanging=false;
    
    ObservableList<String> prawoJazdyOptions = FXCollections.observableArrayList();
    ObservableList<String> zajezdniaOptions = FXCollections.observableArrayList();
    ObservableList<String> pojazdOptions = FXCollections.observableArrayList();
    ObservableList<String> numerOptions = FXCollections.observableArrayList();
    
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	
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
    public void setDriver(Driver driver, Connection conn, boolean editMode, boolean setDepot, ObservableList<Depot> depotsData, ObservableList<Line> linesData) {
        this.driver = driver;
        this.conn = conn;
        this.depotsData = depotsData;
        //this.editMode=editMode;
        this.linesData=linesData;
        
        imieField.setText(driver.getImie());
        nazwiskoField.setText(driver.getNazwisko());
        
        prawoJazdyOptions.add("A (autobus)");
        prawoJazdyOptions.add("T (tramwaj)");
        prawoJazdyOptions.add("AT (oba)");
	    prawoJazdyComboBox.setItems(prawoJazdyOptions);
	    
	    if (driver.getPrawoJazdy() != null) 
	    	if (driver.getPrawoJazdy().equals("T "))
	    		prawoJazdyComboBox.getSelectionModel().select("T (tramwaj)");	
	    	else if (driver.getPrawoJazdy().equals("A "))
	    		prawoJazdyComboBox.getSelectionModel().select("A (autobus)");
	    	else if (driver.getPrawoJazdy().equals("AT"))
	    		prawoJazdyComboBox.getSelectionModel().select("AT (oba)");
	    		
	    if (editMode || setDepot) {
	    	//String toChooseDepot="";
	    	Depot toChooseDepotObj=null;
	    	//String toChooseDriver="";
	    	for (Depot depot : depotsData) {
				if (driver.getPrawoJazdy().equals(depot.getTyp()+" ") || driver.getPrawoJazdy().equals("AT")) {
					zajezdniaOptions.add(depot.toString());
					if (depot.getIdZajezdni() == driver.getIdZajezdni()){
						zajezdniaComboBox.setValue(depot.toString());
						toChooseDepotObj = depot;
						for (Vehicle veh : depot.getVehiclesData()) {
							boolean noDriver=true;
							boolean thisDriver=false;
							for (Driver d : depot.getDriversData()) {
								if (d.getNrRejestracyjny() != null && d.getNrRejestracyjny().equals(veh.getNrRejestracyjny()))
									noDriver=false;
								if (driver.getNrRejestracyjny() != null && driver.getNrRejestracyjny().equals(veh.getNrRejestracyjny()))
									thisDriver=true;	
							}
							if (noDriver) 
								pojazdOptions.add(veh.toString());
							if (thisDriver){
								pojazdOptions.add(veh.toString());
								pojazdComboBox.setValue(veh.toString());
							}
						}
					}
				}
			}
	    	zajezdniaComboBox.setDisable(false);
	        zajezdniaComboBox.setItems(zajezdniaOptions);
	        //if (!toChooseDepot.equals("")) zajezdniaComboBox.getSelectionModel().select(toChooseDepot);
	        
	        pojazdOptions.add("nie przypisuj pojazdu");
	        pojazdComboBox.setDisable(false);
	        pojazdComboBox.setItems(pojazdOptions);
	        //if (!toChooseDriver.equals("")) pojazdComboBox.getSelectionModel().select(toChooseDriver);
	        
	        String toChooseLine=null;
	        for (Line line : linesData) {
				if (toChooseDepotObj != null && line.getTyp().equals(toChooseDepotObj.getTyp())) {
					numerOptions.add(line.toString());
					if (editMode && driver.getNumer()!=0 && driver.getNumer()==line.getNumer()){
						toChooseLine = line.toString();
					}
				}
			}
	        numerOptions.add("nie przypisuj linii");
	        numerComboBox.setDisable(false);
	        numerComboBox.setItems(numerOptions);
	        if (toChooseLine != null) numerComboBox.getSelectionModel().select(toChooseLine);
	    }
        allowChanging = true;
    }
    
    @FXML
    private void handleTypChange() {
    	if (allowChanging) {
    		String tempPrawko=null;
    		if (prawoJazdyComboBox.getSelectionModel().getSelectedItem().equals("T (tramwaj)")) 
    			tempPrawko="T ";
	    	else if (prawoJazdyComboBox.getSelectionModel().getSelectedItem().equals("A (autobus)"))
	    		tempPrawko="A ";
	    	else if (prawoJazdyComboBox.getSelectionModel().getSelectedItem().equals("AT (oba)"))
	    		tempPrawko="AT";
    		
	    	zajezdniaOptions.clear();
	    	for (Depot depot : depotsData) {
				if (tempPrawko != null && (tempPrawko.equals(depot.getTyp()+" ") || tempPrawko.equals("AT"))) {
					zajezdniaOptions.add(depot.toString());
				}
			}
	        zajezdniaComboBox.setItems(zajezdniaOptions);
	        zajezdniaComboBox.setDisable(false);
	        zajezdniaComboBox.getSelectionModel().clearSelection();
	        pojazdComboBox.getSelectionModel().clearSelection();
	        pojazdComboBox.setDisable(true);
	        numerComboBox.getSelectionModel().clearSelection();
	        numerComboBox.setDisable(true);
    	}
    }
    
    @FXML
    private void handleZajezdniaChange() {
    	if (allowChanging) {
    		int tempIdZajezdni=-1;
    		Depot selectedDepot=null;
	    	for (Depot depot : depotsData) {
				if (depot.toString().equals(zajezdniaComboBox.getSelectionModel().getSelectedItem())) {
					tempIdZajezdni = depot.getIdZajezdni();
					selectedDepot = depot;
				}
			}
	    	
	    	pojazdOptions.clear();
	    	for (Depot depot : depotsData) {
				if (tempIdZajezdni == depot.getIdZajezdni()) {
					for (Vehicle veh : depot.getVehiclesData()) {
						boolean noDriver=true;
						boolean thisDriver=false;
						for (Driver d : depot.getDriversData()) {
							if (d.getNrRejestracyjny() != null && d.getNrRejestracyjny().equals(veh.getNrRejestracyjny()))
								noDriver=false;
							if (driver.getNrRejestracyjny() != null && driver.getNrRejestracyjny().equals(veh.getNrRejestracyjny()))
								thisDriver=true;	
						}
						if (noDriver) 
							pojazdOptions.add(veh.toString());
						if (thisDriver){
							pojazdOptions.add(veh.toString());
							pojazdComboBox.setValue(veh.toString());
						}
					}
				}
			}
    		pojazdOptions.add("nie przypisuj pojazdu");
		    pojazdComboBox.setItems(pojazdOptions);
		    pojazdComboBox.setDisable(false);
		    pojazdComboBox.getSelectionModel().clearSelection();
		    
		    numerOptions.clear();
		    if (selectedDepot != null) {
		        for (Line line : linesData) {
					if (line.getTyp().equals(selectedDepot.getTyp())) {
						numerOptions.add(line.toString());
					}
				}
		    }
	        numerOptions.add("nie przypisuj linii");
		    numerComboBox.setItems(numerOptions);
		    numerComboBox.setDisable(false);
		    numerComboBox.getSelectionModel().clearSelection();
    	}
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
        	if (driver.getIdKierowcy() == 0) driver.setIdKierowcy(DBUtil.getNextID(conn));
        	driver.setImie(imieField.getText());
        	driver.setNazwisko(nazwiskoField.getText());
        	
        	if (prawoJazdyComboBox.getSelectionModel().getSelectedItem().equals("T (tramwaj)")) 
	    		driver.setPrawoJazdy("T ");
	    	else if (prawoJazdyComboBox.getSelectionModel().getSelectedItem().equals("A (autobus)"))
	    		driver.setPrawoJazdy("A ");
	    	else if (prawoJazdyComboBox.getSelectionModel().getSelectedItem().equals("AT (oba)"))
	    		driver.setPrawoJazdy("AT");
            
        	Vehicle selectedVeh=null;
        	for (Depot depot : depotsData) {
		    	if (depot.toString().equals(zajezdniaComboBox.getValue())) {
			    	driver.setIdZajezdni(depot.getIdZajezdni());
			    	if (pojazdComboBox.getValue() != null 
			    			&& !pojazdComboBox.getValue().equals("nie przypisuj pojazdu")) {
			    		for (Vehicle veh : depot.getVehiclesData()) {
			    			if (veh.toString().equals(pojazdComboBox.getValue())) {
			    				driver.setNrRejestracyjny(veh.getNrRejestracyjny());
			    				selectedVeh = veh;
			    			}
			    		}
			    	} else if (driver.getNrRejestracyjny() != null){
			    		for (Vehicle veh : depot.getVehiclesData()) {
			    			if (veh.getNrRejestracyjny().equals(driver.getNrRejestracyjny())) {
			    				veh.setNumer(0);
			    				selectedVeh = veh;
			    				DBUtil.updateVehiclesOnLinesInDB(conn, veh);
			    				driver.setNrRejestracyjny(null);
			    			}
			    		}
			    	}
			    	
			    	if (numerComboBox.getValue() != null && !numerComboBox.getValue().equals("nie przypisuj linii")) {
			    		for (Line l : linesData) {
			    			if (numerComboBox.getValue().equals(l.toString())) {
		    					driver.setNumer(l.getNumer());
		    					if (selectedVeh != null) {
			    					selectedVeh.setNumer(driver.getNumer());
			    					DBUtil.updateVehiclesOnLinesInDB(conn, selectedVeh);
		    					}
			    			}
						}
			    	} else {
			    		driver.setNumer(0);
			    	}
		    	}
			}
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
        if (imieField.getText() == null || imieField.getText().length() == 0) 
            errorMessage += "Nie podano imienia.\n"; 
        if (nazwiskoField.getText() == null || nazwiskoField.getText().length() == 0) 
            errorMessage += "Nie podano nazwiska.\n"; 
        if (prawoJazdyComboBox.getSelectionModel().getSelectedItem() == null) 
        	errorMessage += "Nie wybrano typu prawa jazdy.\n"; 
        if (zajezdniaComboBox.getValue() == null || zajezdniaComboBox.getValue().length() == 0) 
            errorMessage += "Nie podano zajezdni.\n"; 
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
    
    /*@FXML
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
    }
*/
    
}