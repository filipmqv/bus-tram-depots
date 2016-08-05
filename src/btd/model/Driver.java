package btd.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Driver {

	//musi miec idzajezdni i numerlinii i nrrejestracyjny
	private final IntegerProperty idKierowcy;
	private final StringProperty imie;
    private final StringProperty nazwisko;
    private final StringProperty prawoJazdy;
    private final IntegerProperty idZajezdni; //// klucz obcy
    private final StringProperty nrRejestracyjny; //// klucz obcy
    private final IntegerProperty numer; //numer linii  //// klucz obcy
    
    public Driver() {
    	this(0, null, null, null, 0, null, 0);
    }
    
    
    public Driver(int idKierowcy, String imie, String nazwisko, String prawoJazdy, int idZajezdni,
    		String nrRejestracyjny, int numer) {
    	this.idKierowcy = new SimpleIntegerProperty(idKierowcy);
    	this.imie = new SimpleStringProperty(imie);
    	this.nazwisko = new SimpleStringProperty(nazwisko);
    	this.prawoJazdy = new SimpleStringProperty(prawoJazdy);
    	this.idZajezdni = new SimpleIntegerProperty(idZajezdni);
    	this.nrRejestracyjny = new SimpleStringProperty(nrRejestracyjny);
    	this.numer = new SimpleIntegerProperty(numer);
    }
    
    @Override
    public String toString() {
		return idKierowcy.get()+" ("+prawoJazdy.get()+") "+imie.get()+" "+nazwisko.get();	
    }
    
    public int getIdKierowcy() {
        return idKierowcy.get();
    }

    public void setIdKierowcy(int idKierowcy) {
        this.idKierowcy.set(idKierowcy);
    }

    public IntegerProperty idKierowcyProperty() {
        return idKierowcy;
    }
    
    public String getImie() {
        return imie.get();
    }

    public void setImie(String imie) {
        this.imie.set(imie);
    }

    public StringProperty imieProperty() {
        return imie;
    }
    public String getNazwisko() {
        return nazwisko.get();
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko.set(nazwisko);
    }

    public StringProperty nazwiskoProperty() {
        return nazwisko;
    }
    public String getPrawoJazdy() {
        return prawoJazdy.get();
    }

    public void setPrawoJazdy(String prawoJazdy) {
        this.prawoJazdy.set(prawoJazdy);
    }

    public StringProperty prawoJazdyProperty() {
        return prawoJazdy;
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
    
    public String getNrRejestracyjny() {
        return nrRejestracyjny.get();
    }

    public void setNrRejestracyjny(String nrRejestracyjny) {
        this.nrRejestracyjny.set(nrRejestracyjny);
    }

    public StringProperty nrRejestracyjnyProperty() {
        return nrRejestracyjny;
    }
    public int getNumer() {
        return numer.get();
    }

    public void setNumer(int numer) {
        this.numer.set(numer);
    }

    public IntegerProperty numerProperty() {
        return numer;
    }
    
    
    
}
