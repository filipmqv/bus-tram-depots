package btd.model;

//import java.util.Comparator;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


/**
 * Model class of Route
 * @author filipmqv
 *
 */
public class Route /*implements Comparator<Route>*/{

    private final IntegerProperty lp; //ktory to przystanek z kolei na trasie
    private final IntegerProperty liczbaWsiadajacych;
    private final IntegerProperty liczbaWysiadajacych;
    private final IntegerProperty numer; //numer linii
    private final IntegerProperty idPrzystanku; 
    private final StringProperty nazwa; //nazwa przystanku
    private final StringProperty typ; //nazwa przystanku
    private final IntegerProperty czasOdPetli;
    

    /**
     * Constructor with some initial data.
     */
    public Route(int lp, int liczbaWsiadajacych, int liczbaWysiadajacych, int numer, 
    		int idPrzystanku, String nazwa, String typ) {
        this.lp = new SimpleIntegerProperty(lp);
        this.liczbaWsiadajacych = new SimpleIntegerProperty(liczbaWsiadajacych);
        this.liczbaWysiadajacych = new SimpleIntegerProperty(liczbaWysiadajacych);
        this.numer = new SimpleIntegerProperty(numer);
        this.idPrzystanku = new SimpleIntegerProperty(idPrzystanku);
        this.nazwa = new SimpleStringProperty(nazwa);
        this.typ = new SimpleStringProperty(typ);
        this.czasOdPetli = new SimpleIntegerProperty(0);
    }

    public int getLp() {
        return lp.get();
    }

    public void setLp(int lp) {
        this.lp.set(lp);
    }

    public IntegerProperty lpProperty() {
        return lp;
    }
    
    public int getLiczbaWsiadajacych() {
        return liczbaWsiadajacych.get();
    }

    public void setLiczbaWsiadajacych(int liczbaWsiadajacych) {
        this.liczbaWsiadajacych.set(liczbaWsiadajacych);
    }

    public IntegerProperty liczbaWsiadajacychProperty() {
        return liczbaWsiadajacych;
    }
    
    public int getLiczbaWysiadajacych() {
        return lp.get();
    }

    public void setLiczbaWysiadajacych(int liczbaWysiadajacych) {
        this.liczbaWysiadajacych.set(liczbaWysiadajacych);
    }

    public IntegerProperty liczbaWysiadajacychProperty() {
        return liczbaWysiadajacych;
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
    
    public int getIdPrzystanku() {
        return idPrzystanku.get();
    }

    public void setIdPrzystanku(int idPrzystanku) {
        this.idPrzystanku.set(idPrzystanku);
    }

    public IntegerProperty idPrzystankuProperty() {
        return idPrzystanku;
    }
    
    public String getNazwa() {
        return nazwa.get();
    }

    public void setNazwa(String nazwa) {
        this.nazwa.set(nazwa);
    }

    public StringProperty nazwaProperty() {
        return nazwa;
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
    
    public int getCzasOdPetli() {
        return czasOdPetli.get();
    }

    public void setCzasOdPetli(int czasOdPetli) {
        this.czasOdPetli.set(czasOdPetli);
    }

    public IntegerProperty czasOdPetliProperty() {
        return czasOdPetli;
    }

	/*@Override
	public int compare(Route o1, Route o2) {
	    return o1.getLp() - o2.getLp();
	}*/

    
}