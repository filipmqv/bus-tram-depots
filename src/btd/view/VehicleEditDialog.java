package btd.view;

import java.sql.Connection;

import org.controlsfx.dialog.Dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import btd.model.Depot;
import btd.model.Vehicle;
import btd.model.Driver;
import btd.util.DBUtil;
import btd.util.FieldUtil;


public class VehicleEditDialog {

	private Connection conn;
	
    @FXML private TextField nrRejestracyjnyField;
    @FXML private ComboBox<String> typComboBox;
    @FXML private ComboBox<String> zajezdniaComboBox;
    @FXML private ComboBox<String> kierowcaComboBox;
    @FXML private ComboBox<String> markaComboBox;
    @FXML private ComboBox<String> modelComboBox;
    @FXML private TextField rocznikField;
    @FXML private ComboBox<String> zuzucieSpalanieComboBox;
    @FXML private ComboBox<String> liczbaSiedzenComboBox;
    @FXML private ComboBox<String> maxLiczbaPasazerowComboBox;
    @FXML private CheckBox biletomatCheckBox;
    @FXML private CheckBox klimatyzacjaCheckBox;
    @FXML private CheckBox niskopodlogowyCheckBox;
    
    @FXML private Button okBtn;
    @FXML private Button generateBtn;

    private Stage dialogStage;
    private Vehicle vehicle;
    private boolean okClicked = false;
    private ObservableList<Depot> depotsData = FXCollections.observableArrayList();
    
    @FXML private Label spalanieLabel4tekst; //bus
    @FXML private Label zuzyciePraduLabel4tekst; //tram
    
    private boolean editMode;
    private boolean allowChanging=false;
    
    String tempNrRej=null;
    String tempTyp=null;
    int tempIdZajezdni = -1;
    String tempMarka=null;
    
    ObservableList<String> zajezdniaOptions = FXCollections.observableArrayList();
    ObservableList<String> markaOptions = FXCollections.observableArrayList();
    ObservableList<String> kierowcaOptions = FXCollections.observableArrayList();
    ObservableList<String> modelOptions = FXCollections.observableArrayList();
    ObservableList<String> spalzuzOptions = FXCollections.observableArrayList();
    ObservableList<String> siedzeniaOptions = FXCollections.observableArrayList();
    ObservableList<String> lpasazerowOptions = FXCollections.observableArrayList();
    
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
    public void setVehicle(Vehicle vehicle, Connection conn, boolean editMode, boolean setDepot, ObservableList<Depot> depotsData) {
        this.vehicle = vehicle;
        this.conn = conn;
        this.depotsData = depotsData;
        this.editMode=editMode;
        
        nrRejestracyjnyField.setText(vehicle.getNrRejestracyjny());
        nrRejestracyjnyField.setDisable(editMode);
        generateBtn.setDisable(editMode);
        
        
        ObservableList<String> typOptions = FXCollections.observableArrayList(
    	        "A (autobus)",
    	        "T (tramwaj)"
    	    );
	    typComboBox.setItems(typOptions);
	    
	    zuzyciePraduLabel4tekst.setVisible(false);
	    
	    if (vehicle.getTyp() != null) 
	    	if (vehicle.getTyp().equals("T")) {
	    		typComboBox.getSelectionModel().select("T (tramwaj)");	
	    		zuzyciePraduLabel4tekst.setVisible(true);
	    		spalanieLabel4tekst.setVisible(false);
	    	} else {
	    		typComboBox.getSelectionModel().select("A (autobus)");
	    		zuzyciePraduLabel4tekst.setVisible(false);
	    		spalanieLabel4tekst.setVisible(true);
	    	}
	    
	    
	    
	    if (editMode || setDepot) {
	    	for (Depot depot : depotsData) {
	    		if (depot.getTyp().equals(vehicle.getTyp())) {
	    			zajezdniaOptions.add(depot.toString());
					if (depot.getIdZajezdni() == vehicle.getIdZajezdni()) {
						zajezdniaComboBox.setValue(depot.toString());
						for (Driver driver : depot.getDriversData()) {
							if (driver.getNrRejestracyjny() != null && 
									driver.getNrRejestracyjny().equals(vehicle.getNrRejestracyjny())) {
								kierowcaOptions.add(driver.toString());
								kierowcaComboBox.setValue(driver.toString());
							} else {
								if (driver.getNrRejestracyjny() == null)
									kierowcaOptions.add(driver.toString());
							} 
						}
					}
	    		}
			}
	    	zajezdniaComboBox.setDisable(false);
	        zajezdniaComboBox.setItems(zajezdniaOptions);
	        kierowcaOptions.add("nie przypisuj kierowcy");
	        kierowcaComboBox.setDisable(false);
	        kierowcaComboBox.setItems(kierowcaOptions);
	        if (setDepot) 
	        	allowChanging = true;
	        tempTyp = vehicle.getTyp();
	        tempMarka = vehicle.getMarka();
	        setMarkaOptions();
	    }
        
	    if (editMode) {
	    	tempNrRej = vehicle.getNrRejestracyjny();
	        markaComboBox.setValue(vehicle.getMarka());
	        modelComboBox.setValue(vehicle.getModel());
	        rocznikField.setText(Integer.toString(vehicle.getRocznik()));
	        if (vehicle.getTyp().equals("T")) {
	        	zuzucieSpalanieComboBox.setValue(Double.toString(vehicle.getZuzyciePradu()));
	        	zuzyciePraduLabel4tekst.setVisible(true);
	        	spalanieLabel4tekst.setVisible(false);
	        } else {
	        	zuzucieSpalanieComboBox.setValue(Double.toString(vehicle.getSpalanie()));
	        	zuzyciePraduLabel4tekst.setVisible(false);
	        	spalanieLabel4tekst.setVisible(true);
	        }
	        liczbaSiedzenComboBox.setValue(Integer.toString(vehicle.getLiczbaSiedzen()));
	        maxLiczbaPasazerowComboBox.setValue(Integer.toString(vehicle.getMaxLiczbaPasazerow()));
	        if (vehicle.getBiletomat() != null && vehicle.getBiletomat().equals("T"))
	        	biletomatCheckBox.setSelected(true);
	        else
	        	biletomatCheckBox.setSelected(false);
	        if (vehicle.getKlimatyzacja() != null && vehicle.getKlimatyzacja().equals("T"))
	        	klimatyzacjaCheckBox.setSelected(true);
	        else
	        	klimatyzacjaCheckBox.setSelected(false);
	        if (vehicle.getNiskopodlogowy() != null && vehicle.getNiskopodlogowy().equals("T"))
	        	niskopodlogowyCheckBox.setSelected(true);
	        else
	        	niskopodlogowyCheckBox.setSelected(false);
	    }
        allowChanging = true;
    }

    private void setMarkaOptions() {
    	markaComboBox.setDisable(false);
        markaOptions.clear();
	    for (Depot depot : depotsData) {
	    	if (depot.getTyp().equals(tempTyp)) {
		    	for (Vehicle veh : depot.getVehiclesData()) {
		    		if (!markaOptions.contains(veh.getMarka())) {
		    			markaOptions.add(veh.getMarka());
		    			if (tempMarka != null && tempMarka.equals(veh.getMarka())) {
		    				markaComboBox.getSelectionModel().selectLast();
		    			}
		    		}
		    	}
	    	}
		}
	    markaComboBox.setItems(markaOptions);
    }
    
    @FXML
    private void handleTypChange() {
    	if (allowChanging) {
	    	if (typComboBox.getSelectionModel().getSelectedItem().equals("T (tramwaj)") ) {
	    		tempTyp = "T";
	        	zuzyciePraduLabel4tekst.setVisible(true);
	        	spalanieLabel4tekst.setVisible(false);
	    	} else {
	    		tempTyp = "A";
	        	zuzyciePraduLabel4tekst.setVisible(false);
	        	spalanieLabel4tekst.setVisible(true);
	    	}
	    	zajezdniaOptions.clear();
		    for (Depot depot : depotsData) {
		    	if (depot.getTyp().equals(tempTyp)) {
			    	zajezdniaOptions.add(depot.toString());
			    	if (tempIdZajezdni == depot.getIdZajezdni())
			    		zajezdniaComboBox.getSelectionModel().selectLast();
		    	}
			}
	        zajezdniaComboBox.setItems(zajezdniaOptions);
	        zajezdniaComboBox.setDisable(false);
	        
	        kierowcaComboBox.setDisable(true);
	        setMarkaOptions();
	        
	        zajezdniaComboBox.getSelectionModel().clearSelection();
	        kierowcaComboBox.getSelectionModel().clearSelection();
	        markaComboBox.getSelectionModel().clearSelection();
	        modelComboBox.getSelectionModel().clearSelection();
    	}
    }
    
    @FXML
    private void handleZajezdniaChange() {
    	if (allowChanging) {
    		
	    	for (Depot depot : depotsData) {
				if (depot.toString().equals(zajezdniaComboBox.getSelectionModel().getSelectedItem())) {
					tempIdZajezdni = depot.getIdZajezdni();
				}
			}
	    	
	    	kierowcaOptions.clear();
		    for (Depot depot : depotsData) {
		    	if (depot.getIdZajezdni() == tempIdZajezdni) {
		    		for (Driver drv : depot.getDriversData()) {
		    			if (!kierowcaOptions.contains(drv.getIdKierowcy()) && drv.getNrRejestracyjny() == null) {
		    				kierowcaOptions.add(drv.toString());
		    			} else if (editMode && !kierowcaOptions.contains(drv.getIdKierowcy()) 
		    					&& drv.getNrRejestracyjny() != null && drv.getNrRejestracyjny().equals(tempNrRej)) {
		    				kierowcaOptions.add(drv.toString());
		    				kierowcaComboBox.getSelectionModel().selectLast();
		    			}
		    		}
		    	}
		    }
		    kierowcaOptions.add("nie przypisuj kierowcy");
		    kierowcaComboBox.setItems(kierowcaOptions);
		    kierowcaComboBox.setDisable(false);
    	}
    }
    
    @FXML
    private void handleMarkaChange() {
    	if (allowChanging) {
	    	modelOptions.clear();
	    	for (Depot depot : depotsData) {
		    	if (depot.getTyp().equals(tempTyp)) {
			    	for (Vehicle veh : depot.getVehiclesData()) {
			    		if (veh.getMarka().equals(markaComboBox.getValue()) && !modelOptions.contains(veh.getModel())) {
			    			modelOptions.add(veh.getModel());
			    			if (tempMarka != null && tempMarka.equals(veh.getMarka())) {
			    				modelComboBox.getSelectionModel().selectLast();
			    			}
			    		}
			    	}
		    	}
			}
	    	modelComboBox.setItems(modelOptions);
	    	modelComboBox.setDisable(false);
    	}
    }
    
    @FXML
    private void handleModelChange() {
    	if (allowChanging) {
	    	spalzuzOptions.clear();
	    	for (Depot depot : depotsData) {
		    	if (depot.getTyp().equals(tempTyp)) {
			    	for (Vehicle veh : depot.getVehiclesData()) {
			    		if (veh.getMarka().equals(markaComboBox.getValue()) 
			    				&& veh.getModel().equals(modelComboBox.getValue()) ) {
			    			if (veh.getTyp().equals("T") && !spalzuzOptions.contains(Double.toString(veh.getZuzyciePradu()))) {
			    				spalzuzOptions.add(Double.toString(veh.getZuzyciePradu()));
			    			}
			    			else if (veh.getTyp().equals("A") && !spalzuzOptions.contains(Double.toString(veh.getSpalanie()))) {
			    				spalzuzOptions.add(Double.toString(veh.getSpalanie()));
			    			}
			    		}
			    	}
		    	}
			}
	    	zuzucieSpalanieComboBox.setItems(spalzuzOptions);
	    	
	    	siedzeniaOptions.clear();
	    	for (Depot depot : depotsData) {
		    	if (depot.getTyp().equals(tempTyp)) {
			    	for (Vehicle veh : depot.getVehiclesData()) {
			    		if (veh.getMarka().equals(markaComboBox.getValue()) 
			    				&& veh.getModel().equals(modelComboBox.getValue()) 
			    				&& !siedzeniaOptions.contains(Integer.toString(veh.getLiczbaSiedzen()))) {
			    			siedzeniaOptions.add(Integer.toString(veh.getLiczbaSiedzen()));
			    		}
			    	}
		    	}
			}
	    	liczbaSiedzenComboBox.setItems(siedzeniaOptions);
	    	
	    	lpasazerowOptions.clear();
	    	for (Depot depot : depotsData) {
		    	if (depot.getTyp().equals(tempTyp)) {
			    	for (Vehicle veh : depot.getVehiclesData()) {
			    		if (veh.getMarka().equals(markaComboBox.getValue()) 
			    				&& veh.getModel().equals(modelComboBox.getValue()) 
			    				&& !lpasazerowOptions.contains(Integer.toString(veh.getMaxLiczbaPasazerow()))) {
			    			lpasazerowOptions.add(Integer.toString(veh.getMaxLiczbaPasazerow()));
			    		}
			    	}
		    	}
			}
	    	maxLiczbaPasazerowComboBox.setItems(lpasazerowOptions);
    	}
    }
    
    @FXML
    private void handleGenerate() {
    	boolean t = true;
    	String n="";
    	while (t) {
    		boolean again = false;
    		n = DBUtil.callLosowyNumerRejestracyjny(conn, nrRejestracyjnyField.getText());
    		for (Depot depot : depotsData) {
				for (Vehicle veh : depot.getVehiclesData()) {
					if (veh.getNrRejestracyjny().equals(n)) {
						again = true;
					}
				}
			}
    		if (!again) t = false;
    	}
    	nrRejestracyjnyField.setText(n);
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
        	vehicle.setNrRejestracyjny(nrRejestracyjnyField.getText());
        	
        	if (typComboBox.getSelectionModel().getSelectedItem().equals("T (tramwaj)") ) {
        		vehicle.setTyp("T");
        		vehicle.setZuzyciePradu(Double.parseDouble(zuzucieSpalanieComboBox.getValue().replaceAll(",",".")));
        	} else { 
            	vehicle.setTyp("A");
            	vehicle.setSpalanie(Double.parseDouble(zuzucieSpalanieComboBox.getValue().replaceAll(",",".")));
        	}
            
        	for (Depot depot : depotsData) {
		    	if (depot.toString().equals(zajezdniaComboBox.getValue())) {
			    	vehicle.setIdZajezdni(depot.getIdZajezdni());
			    	if (kierowcaComboBox.getValue() != null 
			    			&& !kierowcaComboBox.getValue().equals("nie przypisuj kierowcy")) {
			    		for (Driver dr : depot.getDriversData()) {
			    			if (dr.toString().equals(kierowcaComboBox.getValue())) {
			    				dr.setNrRejestracyjny(vehicle.getNrRejestracyjny());
			    				vehicle.setNumer(dr.getNumer());
			    			}
			    		}
			    	} else {
			    		for (Driver dr : depot.getDriversData()) {
			    			if (dr.getNrRejestracyjny() != null 
			    					&& dr.getNrRejestracyjny().equals(vehicle.getNrRejestracyjny())) {
			    				dr.setNrRejestracyjny(null);
			    				DBUtil.updateDriverInDB(conn, dr);
			    				vehicle.setNumer(0);
			    			}
			    		}
			    	}
		    	}
			}
        	vehicle.setMarka(markaComboBox.getValue());
        	vehicle.setModel(modelComboBox.getValue());
        	vehicle.setRocznik(Integer.parseInt(rocznikField.getText()));
        	vehicle.setLiczbaSiedzen(Integer.parseInt(liczbaSiedzenComboBox.getValue()));
        	vehicle.setMaxLiczbaPasazerow(Integer.parseInt(maxLiczbaPasazerowComboBox.getValue()));
        	if (biletomatCheckBox.isSelected())
        		vehicle.setBiletomat("T");
            else 
            	vehicle.setBiletomat("N");
        	if (klimatyzacjaCheckBox.isSelected())
        		vehicle.setKlimatyzacja("T");
            else 
            	vehicle.setKlimatyzacja("N");
        	if (niskopodlogowyCheckBox.isSelected())
        		vehicle.setNiskopodlogowy("T");
            else 
            	vehicle.setNiskopodlogowy("N");
        	
        	vehicle.setNrBoczny(DBUtil.getNextNumerBoczny(conn));

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
        if (nrRejestracyjnyField.getText() == null || nrRejestracyjnyField.getText().length() == 0) 
            errorMessage += "Nie podano numeru rejestracyjnego.\n"; 
        else if (nrRejestracyjnyField.getText() != null && nrRejestracyjnyField.getText().length() != 7) 
            errorMessage += "Podany numer rejestracyjny ma nieprawid³ow¹ d³ugoœæ.\n"; 
        boolean nrRejSiePowtorzyl=false;
        for (Depot depot : depotsData) {
			for (Vehicle veh : depot.getVehiclesData()) {
				if (veh.getNrRejestracyjny().equals(nrRejestracyjnyField.getText())) {
					nrRejSiePowtorzyl = true;
				}
			}
		}
        if (!editMode && nrRejSiePowtorzyl)
        	errorMessage += "Numer rejestracyjny jest zajêty. Proszê wpisaæ inny.\n";
        if (typComboBox.getSelectionModel().getSelectedItem() == null) 
        	errorMessage += "Nie wybrano typu.\n"; 
        if (zajezdniaComboBox.getValue() == null || zajezdniaComboBox.getValue().length() == 0) 
            errorMessage += "Nie podano zajezdni.\n"; 
        if (markaComboBox.getValue() == null || markaComboBox.getValue().length() == 0) 
            errorMessage += "Nie podano marki.\n"; 
        if (modelComboBox.getValue() == null || modelComboBox.getValue().length() == 0) 
            errorMessage += "Nie podano modelu.\n"; 
        if (!FieldUtil.isInteger(rocznikField.getText())) {
        	errorMessage += "Rocznik musi byæ liczb¹ naturaln¹.\n"; 
        }
        else if (Integer.parseInt(rocznikField.getText())<1) {
        	errorMessage += "Rocznik musi byæ liczb¹ dodatni¹.\n"; 
        }
        
        if (zuzucieSpalanieComboBox.getValue() == null || zuzucieSpalanieComboBox.getValue().length() == 0)
        	errorMessage += "Nie podano zu¿ycia pr¹du / spalania.\n"; 
        else if (!FieldUtil.isDouble(zuzucieSpalanieComboBox.getValue().replaceAll(",","."))) {
        	errorMessage += "Zu¿ycie pr¹du / spalanie musi byæ liczb¹.\n"; 
        }
        else if (Double.parseDouble(zuzucieSpalanieComboBox.getValue().replaceAll(",","."))<1) {
        	errorMessage += "Zu¿ycie pr¹du / spalanie musi byæ liczb¹ dodatni¹.\n"; 
        }
        
        if (!FieldUtil.isInteger(liczbaSiedzenComboBox.getValue())) {
        	errorMessage += "Liczba siedzieñ musi byæ liczb¹ naturaln¹.\n"; 
        }
        else if (Integer.parseInt(liczbaSiedzenComboBox.getValue())<1) {
        	errorMessage += "Liczba siedzieñ musi byæ liczb¹ dodatni¹.\n"; 
        }
        
        if (!FieldUtil.isInteger(maxLiczbaPasazerowComboBox.getValue())) {
        	errorMessage += "Maksymalna liczba pasa¿erów musi byæ liczb¹ naturaln¹.\n"; 
        }
        else if (Integer.parseInt(maxLiczbaPasazerowComboBox.getValue())<1) {
        	errorMessage += "Maksymalna liczba pasa¿erów musi byæ liczb¹ dodatni¹.\n"; 
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