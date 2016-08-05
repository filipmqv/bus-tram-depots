package btd.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Vehicle {

	private final StringProperty nrRejestracyjny;
    private final StringProperty typ;
    private final StringProperty marka;
    private final StringProperty model;
    private final IntegerProperty nrBoczny; //nBoczny_SEQ
    private final IntegerProperty rocznik;
    private final IntegerProperty liczbaSiedzen;
    private final IntegerProperty maxLiczbaPasazerow;
    private final StringProperty biletomat; //'T' / 'N'
    private final StringProperty klimatyzacja;//'T' / 'N'
    private final DoubleProperty spalanie; //bus
    private final DoubleProperty zuzyciePradu; //tram
    private final StringProperty niskopodlogowy;//'T' / 'N'
    private final IntegerProperty idZajezdni; //// klucz obcy
    private final IntegerProperty numer; //// klucz obcy numer linii
    
    public Vehicle() {
    	this(null, null, null, null, 0, 0, 
    			0, 0, null, null, 0.0, 0.0, null, 0, 0);
    }
    /**
     * Constructor with all data passed.
     */
    public Vehicle(String nrRejestracyjny, String typ, String marka, String model,
    		int nrBoczny, int rocznik, int liczbaSiedzen, int maxLiczbaPasazerow,
    		String biletomat, String klimatyzacja, double spalanie, double zuzyciePradu,
    		String niskopodlogowy, int idZajezdni, int numerLinii) {
    	this.nrRejestracyjny = new SimpleStringProperty(nrRejestracyjny);
        this.typ = new SimpleStringProperty(typ);
        this.marka = new SimpleStringProperty(marka);
        this.model = new SimpleStringProperty(model);
        this.nrBoczny = new SimpleIntegerProperty(nrBoczny);
        this.rocznik = new SimpleIntegerProperty(rocznik);
        this.liczbaSiedzen = new SimpleIntegerProperty(liczbaSiedzen);
        this.maxLiczbaPasazerow = new SimpleIntegerProperty(maxLiczbaPasazerow);
        this.biletomat = new SimpleStringProperty(biletomat);
        this.klimatyzacja = new SimpleStringProperty(klimatyzacja);
        this.spalanie = new SimpleDoubleProperty(spalanie);
        this.zuzyciePradu = new SimpleDoubleProperty(zuzyciePradu);
        this.niskopodlogowy = new SimpleStringProperty(niskopodlogowy);
        this.idZajezdni = new SimpleIntegerProperty(idZajezdni);
        this.numer = new SimpleIntegerProperty(numerLinii);
    }
    
    @Override
    public String toString() {
    	return nrRejestracyjny.get() + " (" + typ.get() + ") " + marka.get() + " " + model.get();
    }
    
    public String getNrRejestracyjny() {
		return nrRejestracyjny.get();
	}

	public String getTyp() {
		return typ.get();
	}

	public String getMarka() {
		return marka.get();
	}

	public String getModel() {
		return model.get();
	}

	public int getNrBoczny() {
		return nrBoczny.get();
	}

	public int getRocznik() {
		return rocznik.get();
	}

	public int getLiczbaSiedzen() {
		return liczbaSiedzen.get();
	}

	public int getMaxLiczbaPasazerow() {
		return maxLiczbaPasazerow.get();
	}

	public String getBiletomat() {
		return biletomat.get();
	}

	public String getKlimatyzacja() {
		return klimatyzacja.get();
	}

	public double getSpalanie() {
		return spalanie.get();
	}

	public double getZuzyciePradu() {
		return zuzyciePradu.get();
	}

	public String getNiskopodlogowy() {
		return niskopodlogowy.get();
	}
	
	////////////////////////
	public void setNrRejestracyjny(String a) {
		this.nrRejestracyjny.set(a);
	}

	public void setTyp(String a) {
		this.typ.set(a);
	}

	public void setMarka(String a) {
		this.marka.set(a);
	}

	public void setModel(String a) {
		this.model.set(a);
	}

	public void setNrBoczny(int a) {
		this.nrBoczny.set(a);
	}

	public void setRocznik(int a) {
		this.rocznik.set(a);
	}

	public void setLiczbaSiedzen(int a) {
		this.liczbaSiedzen.set(a);
	}

	public void setMaxLiczbaPasazerow(int a) {
		this.maxLiczbaPasazerow.set(a);
	}

	public void setBiletomat(String a) {
		this.biletomat.set(a);
	}

	public void setKlimatyzacja(String a) {
		this.klimatyzacja.set(a);
	}

	public void setSpalanie(double a) {
		this.spalanie.set(a);
	}

	public void setZuzyciePradu(double a) {
		this.zuzyciePradu.set(a);
	}

	public void setNiskopodlogowy(String a) {
		this.niskopodlogowy.set(a);
	}
    
	////////////////////////
	
	public StringProperty nrRejestracyjnyProperty() {
		return nrRejestracyjny;
	}

	public StringProperty typProperty() {
		return typ;
	}

	public StringProperty markaProperty() {
		return marka;
	}

	public StringProperty modelProperty() {
		return model;
	}

	public IntegerProperty nrBocznyProperty() {
		return nrBoczny;
	}

	public IntegerProperty rocznikProperty() {
		return rocznik;
	}

	public IntegerProperty liczbaSiedzenProperty() {
		return liczbaSiedzen;
	}

	public IntegerProperty maxLiczbaPasazerowProperty() {
		return maxLiczbaPasazerow;
	}

	public StringProperty biletomatProperty() {
		return biletomat;
	}

	public StringProperty klimatyzacjaProperty() {
		return klimatyzacja;
	}

	public DoubleProperty spalanieProperty() {
		return spalanie;
	}

	public DoubleProperty zuzyciePraduProperty() {
		return zuzyciePradu;
	}

	public StringProperty niskopodlogowyProperty() {
		return niskopodlogowy;
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
