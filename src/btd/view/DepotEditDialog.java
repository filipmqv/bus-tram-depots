package btd.view;

import java.sql.Connection;

import org.controlsfx.dialog.Dialogs;

import btd.model.Depot;
import btd.util.DBUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DepotEditDialog {

	private Connection conn;

	@FXML private ComboBox<String> typComboBox;
    @FXML private TextField adresField;
    
    private Stage dialogStage;
    private Depot depot;
    private boolean okClicked = false;
    boolean editTyp; //true tryb edycji, false- tryb nowa zajezdnia
    
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
    public void setDepot(Depot depot, boolean editTyp, Connection conn) {
        this.depot = depot;
        this.editTyp = editTyp;
        this.conn = conn;
        
        ObservableList<String> options = FXCollections.observableArrayList(
    	        "A (autobusowa)",
    	        "T (tramwajowa)"
    	    );
	    typComboBox.setItems(options);
	    typComboBox.getSelectionModel().select(0);
	    if (depot.getTyp() != null) 
	    	if (depot.getTyp().equals("T")) 
	    		typComboBox.getSelectionModel().select(1);	
    
        typComboBox.setDisable(editTyp);
        adresField.setText(depot.getAdres());
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
        	if (depot.getIdZajezdni() == 0) depot.setIdZajezdni(DBUtil.getNextID(conn));
        	depot.setAdres(adresField.getText());
        	if (!editTyp) {
        		if (typComboBox.getSelectionModel().getSelectedItem().equals("T (tramwajowa)") )
                	depot.setTyp("T");
                else 
                	depot.setTyp("A");
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

        if (adresField.getText() == null || adresField.getText().length() == 0) {
            errorMessage += "Nie podano adresu!\n"; 
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
