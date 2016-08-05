package btd.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model class of Stop
 * @author filipmqv
 *
 */
public class Stop {

    private final IntegerProperty idPrzystanku;
    private final StringProperty nazwa;
    private final DoubleProperty x;
    private final DoubleProperty y;
    
    public Stop() {
    	this(0, null, 0, 0);
    }
    
    /**
     * Constructor with some initial data.
     */
    public Stop(int idPrzystanku, String nazwa, double x, double y) {
        this.idPrzystanku = new SimpleIntegerProperty(idPrzystanku);
        this.nazwa = new SimpleStringProperty(nazwa);
        this.x = new SimpleDoubleProperty(x);
        this.y = new SimpleDoubleProperty(y);
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

    public double getX() {
        return x.get();
    }

    public void setX(double x) {
        this.x.set(x);
    }

    public DoubleProperty xProperty() {
        return x;
    }

    public double getY() {
        return y.get();
    }

    public void setY(double y) {
        this.y.set(y);
    }

    public DoubleProperty yProperty() {
        return y;
    }
    
}