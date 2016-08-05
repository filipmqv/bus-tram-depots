package btd.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Depot {

	private final IntegerProperty idZajezdni;
    private final StringProperty adres;
    private final StringProperty typ;
    private ObservableList<Driver> driversData = FXCollections.observableArrayList();
    private ObservableList<Vehicle> vehiclesData = FXCollections.observableArrayList();
    
    public Depot() {
    	this(0, null, null);
    }
    
    public Depot(int idZajezdni, String adres, String typ) {
        this.idZajezdni = new SimpleIntegerProperty(idZajezdni);
        this.adres = new SimpleStringProperty(adres);
        this.typ = new SimpleStringProperty(typ);
    }
    
    @Override
    public String toString() {
		return idZajezdni.get()+" ("+typ.get()+") "+adres.get();	
    }
    
    public int getIdZajezdni() {
        return idZajezdni.get();
    }

    public void setIdZajezdni(int idZajezdni) {
        this.idZajezdni.set(idZajezdni);
    }

    public IntegerProperty idZajezdniProperty() {
        return idZajezdni;
    }

    public String getAdres() {
        return adres.get();
    }

    public void setAdres(String adres) {
        this.adres.set(adres);
    }

    public StringProperty adresProperty() {
        return adres;
    }
    
    public String getTyp() {
        return typ.get();
    }

    public void setTyp(String typ) {
        this.typ.set(typ);
    }

    public StringProperty typProperty() {
        return typ;
    }
	
    public ObservableList<Driver> getDriversData() {
        return driversData;
    }
    
    public ObservableList<Vehicle> getVehiclesData() {
        return vehiclesData;
    }
}
