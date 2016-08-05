package btd.model;

import java.time.LocalDateTime;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Model class of Line
 * @author filipmqv
 *
 */
public class Line {

    private final IntegerProperty numer;
    private final StringProperty typ;
    private final IntegerProperty czestotliwosc;
    private final DoubleProperty dlugosc;
    private final IntegerProperty czasPrzejazdu;
    private final IntegerProperty liczbaPojazdow;
    private final ObjectProperty<LocalDateTime> pierwszyKurs;
    private final ObjectProperty<LocalDateTime> ostatniKurs;
    private ObservableList<Route> routeData = FXCollections.observableArrayList();
    
    /**
     * Default constructor.
     */
    public Line() {
        this(0, null, 0, LocalDateTime.of(1993, 10, 24, 7, 30), LocalDateTime.of(1993, 10, 24, 22, 30));
    }
    
    /**
     * Constructor with some data passed.
     */
    public Line(int numer, String typ, int czestotliwosc, LocalDateTime pierwszyKurs, LocalDateTime ostatniKurs) {
        this.numer = new SimpleIntegerProperty(numer);
        this.typ = new SimpleStringProperty(typ);
        this.czestotliwosc = new SimpleIntegerProperty(czestotliwosc);
        this.dlugosc = new SimpleDoubleProperty(0);
        this.czasPrzejazdu = new SimpleIntegerProperty(0);
        this.liczbaPojazdow = new SimpleIntegerProperty(0);
        //this.pierwszyKurs = new SimpleObjectProperty<LocalDateTime>(LocalDateTime.of(1994, 7, 15, 7, 30));
        if (pierwszyKurs != null) 
        	this.pierwszyKurs = new SimpleObjectProperty<LocalDateTime>(pierwszyKurs);
        else this.pierwszyKurs = null;
        if (ostatniKurs != null) 
        	this.ostatniKurs = new SimpleObjectProperty<LocalDateTime>(ostatniKurs);
        else this.ostatniKurs = null;
    }

    /**
     * Constructor with all data passed.
     */
    public Line(int numer, String typ, int czestotliwosc, double dlugosc, int czasPrzejazdu, 
    		int liczbaPojazdow, LocalDateTime pierwszyKurs, LocalDateTime ostatniKurs) {
        this.numer = new SimpleIntegerProperty(numer);
        this.typ = new SimpleStringProperty(typ);
        this.czestotliwosc = new SimpleIntegerProperty(czestotliwosc);
        this.dlugosc = new SimpleDoubleProperty(dlugosc);
        this.czasPrzejazdu = new SimpleIntegerProperty(czasPrzejazdu);
        this.liczbaPojazdow = new SimpleIntegerProperty(liczbaPojazdow);
        //this.pierwszyKurs = new SimpleObjectProperty<LocalDateTime>(LocalDateTime.of(1994, 7, 15, 7, 30));
        if (pierwszyKurs != null) 
        	this.pierwszyKurs = new SimpleObjectProperty<LocalDateTime>(pierwszyKurs);
        else this.pierwszyKurs = null;
        if (ostatniKurs != null) 
        	this.ostatniKurs = new SimpleObjectProperty<LocalDateTime>(ostatniKurs);
        else this.ostatniKurs = null;
    }
    
    public double distance(double X1, double Y1, double X2, double Y2) {
        return Math.sqrt(
            (X1 - X2) *  (X1 - X2) + 
            (Y1 - Y2) *  (Y1 - Y2)
        );
    }
    
    // 20km/h, 180px - 1km, 180px - 3min, 60px - 1min
    /**
     * 
     * @param stopsData
     * @param kierunek true - oryginalny, false - odwrócony
     */
    public void wyliczCzasyPrzystankow(ObservableList<Stop> stopsData, boolean kierunek) {
    	double odleglosc = 0.0;
    	int czas=0;
    	int lastIDprzyst;
    	int currIDprzyst;
    	Stop lastStop=null;
    	Stop currStop=null;
    	int poczatek=0;
    	int koniec=routeData.size();
    	if (kierunek && koniec>0) {
	    	for (int i=poczatek; i<koniec; i++) {
	    		if (i==poczatek) {
	    			routeData.get(i).setCzasOdPetli(0);
	    			lastIDprzyst = routeData.get(i).getIdPrzystanku();
	    			for (Stop stop : stopsData) {
						if (lastIDprzyst == stop.getIdPrzystanku()){
							lastStop = stop;
						}	
					}
	    		}
	    		else {
	    			currIDprzyst = routeData.get(i).getIdPrzystanku();
	    			for (Stop stop : stopsData) {
						if (currIDprzyst == stop.getIdPrzystanku()){
							currStop = stop;
						}	
					}
	    			odleglosc = distance(currStop.getX(), currStop.getY(), lastStop.getX(), lastStop.getY());
	    			czas = (int) Math.ceil(odleglosc/60.0);
	    			routeData.get(i).setCzasOdPetli( routeData.get(i-1).getCzasOdPetli() + czas );
	    			lastStop = currStop;
	    		}
	    	}
    	} else {
    		poczatek=koniec-1;
    		koniec=-1;
    		for (int i=poczatek; i>koniec; i--) {
	    		if (i==poczatek) {
	    			routeData.get(i).setCzasOdPetli(0);
	    			lastIDprzyst = routeData.get(i).getIdPrzystanku();
	    			for (Stop stop : stopsData) {
						if (lastIDprzyst == stop.getIdPrzystanku()){
							lastStop = stop;
						}	
					}
	    		}
	    		else {
	    			currIDprzyst = routeData.get(i).getIdPrzystanku();
	    			for (Stop stop : stopsData) {
						if (currIDprzyst == stop.getIdPrzystanku()){
							currStop = stop;
						}	
					}
	    			odleglosc = distance(currStop.getX(), currStop.getY(), lastStop.getX(), lastStop.getY());
	    			czas = (int) Math.ceil(odleglosc/60.0);
	    			routeData.get(i).setCzasOdPetli( routeData.get(i+1).getCzasOdPetli() + czas );
	    			lastStop = currStop;
	    		}
	    	}
    	}
    }
    
    @Override
    public String toString() {
    	return numer.get() + " (" + typ.get() + ")";
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

    public String getTyp() {
        return typ.get();
    }

    public void setTyp(String typ) {
        this.typ.set(typ);
    }

    public StringProperty typProperty() {
        return typ;
    }

    public int getCzestotliwosc() {
        return czestotliwosc.get();
    }

    public void setCzestotliwosc(int czestotliwosc) {
        this.czestotliwosc.set(czestotliwosc);
    }

    public IntegerProperty czestotliwoscProperty() {
        return czestotliwosc;
    }

    public double getDlugosc() {
        return dlugosc.get();
    }

    public void setDlugosc(double dlugosc) {
        this.dlugosc.set(dlugosc);
    }

    public DoubleProperty dlugoscProperty() {
        return dlugosc;
    }
    
    public int getCzasPrzejazdu() {
        return czasPrzejazdu.get();
    }

    public void setCzasPrzejazdu(int czasPrzejazdu) {
        this.czasPrzejazdu.set(czasPrzejazdu);
    }

    public IntegerProperty czasPrzejazduProperty() {
        return czasPrzejazdu;
    }
    
    public int getLiczbaPojazdow() {
        return liczbaPojazdow.get();
    }

    public void setLiczbaPojazdow(int liczbaPojazdow) {
        this.liczbaPojazdow.set(liczbaPojazdow);
    }

    public IntegerProperty liczbaPojazdowProperty() {
        return liczbaPojazdow;
    }
    
    public LocalDateTime getPierwszyKurs() {
        return pierwszyKurs.get();
    }

    public void setPierwszyKurs(LocalDateTime pierwszyKurs) {
        this.pierwszyKurs.set(pierwszyKurs);
    }

    public ObjectProperty<LocalDateTime> pierwszyKursProperty() {
        return pierwszyKurs;
    }
    
    public LocalDateTime getOstatniKurs() {
        return ostatniKurs.get();
    }

    public void setOstatniKurs(LocalDateTime ostatniKurs) {
        this.ostatniKurs.set(ostatniKurs);
    }

    public ObjectProperty<LocalDateTime> ostatniKursProperty() {
        return ostatniKurs;
    }
    
    public ObservableList<Route> getRouteData() {
        return routeData;
    }
    
}