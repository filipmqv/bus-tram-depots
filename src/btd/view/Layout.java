package btd.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.dialog.Dialogs;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import btd.MainApp;
import btd.model.Depot;
import btd.model.Driver;
import btd.model.Line;
import btd.model.Route;
import btd.model.Stop;
import btd.model.Vehicle;
import btd.util.DBUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class Layout implements Initializable {

	// draw stops of this size on map and move them up and left by MOVE px
	private final double STOPSIZE=16.0;
	private final double MOVE=STOPSIZE/2;
	private final double DIST=STOPSIZE;
	
    // Reference to the main application.
    private MainApp mainApp;
    
    // Data with all objects
    private ObservableList<Line> linesData = FXCollections.observableArrayList();
    private ObservableList<Driver> driversData = FXCollections.observableArrayList();
    private ObservableList<Depot> depotsData = FXCollections.observableArrayList();
    private ObservableList<Stop> stopsData = FXCollections.observableArrayList();
    private ObservableList<Vehicle> vehiclesData = FXCollections.observableArrayList();
    
    private Image mapImg;
    
    @FXML private TabPane tabPane;
    
    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public Layout() {
    	// Load map image
    	mapImg = new Image("file:img/mymap2560.png", true);
    	busTramFilter1 = new SimpleStringProperty("");
    	busTramFilter3 = new SimpleStringProperty("");
    	busTramFilter4 = new SimpleStringProperty("");
    	busTramFilter5 = new SimpleStringProperty("");
    	//if (mapImg != null) System.out.println("zaladowano obrazek");
    }
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {		
		initLinesOverview();
		initStopsOverview();
		initRouteOverview();
			initLinesOnStopOverview();
		initDepotsOverview();
			initDriversInDepotTableview();
	        initVehiclesInDepotTableview();
	        initLinesInDepotTableview();
		initDriversOverview();
		initVehiclesOverview();
	}
	
	/**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp; 
    }
    
    public void loadFromDB() {
        DBUtil.loadLinesFromDB(mainApp.getConn(), linesData);
        DBUtil.loadStopsFromDB(mainApp.getConn(), stopsData);
        DBUtil.loadRoutesFromDB(mainApp.getConn(), linesData);
        DBUtil.loadDepotsFromDB(mainApp.getConn(), depotsData);
        DBUtil.loadDriversFromDB(mainApp.getConn(), depotsData);
        DBUtil.loadVehiclesFromDB(mainApp.getConn(), depotsData);
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////    
    // LINIE 1 ////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    
    @FXML private TableView<Line> liniaTab1;
    @FXML private TableColumn<Line, Number> numerCol1;
    @FXML private TableColumn<Line, String> typCol1;
    @FXML private Label numerLabel1;
    @FXML private Label typLabel1;
    @FXML private Label dlugoscLabel1;
    @FXML private Label czestotliwoscKursowLabel1;
    @FXML private Label czasPrzejazduLabel1;
    @FXML private Label liczbaPojazdowLabel1;
    
    @FXML private TableView<Route> trasaTab1;
    @FXML private TableColumn<Route, Number> lpCol1;
    @FXML private TableColumn<Route, Number> czasCol1;
    @FXML private TableColumn<Route, String> nazwaCol1;
    
    @FXML private Canvas liniaCanvas1;
    @FXML private AnchorPane liniaAnchorPane1;
    @FXML private Button liniaAnchorPlus1;
    @FXML private Button liniaAnchorMinus1;
    
    @FXML private TextField szukajTextField1;
    @FXML private CheckBox tramCheckBox1;
    @FXML private CheckBox busCheckBox1;
    private StringProperty busTramFilter1;
    
    @FXML private Tab linesTabPane1;
    
    @FXML private ComboBox<String> kierunekComboBox1;
    
    private GraphicsContext liniagc;
    private boolean first=true;
    
    @FXML
    public void showLineMap() {
    	if (first) 
    		first=false; 
    	else 
    		showLineDetails(liniaTab1.getSelectionModel().getSelectedItem());
    }
    
    public void initLinesOverview() {
    	// Initialize the line table with the two columns.
    	numerCol1.setCellValueFactory(cellData -> cellData.getValue().numerProperty());
    	typCol1.setCellValueFactory(cellData -> cellData.getValue().typProperty());
    	
    	
    	// 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<Line> filteredLinesData = new FilteredList<>(linesData, p -> true);
        
        // 2. Set the filter Predicate whenever the filter changes.
        szukajTextField1.textProperty().addListener((observable, oldValue, newValue) -> {
        	filteredLinesData.setPredicate(line -> {
                // If filter text is empty, display all.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare
                String lowerCaseFilter = newValue.toLowerCase();

                if (Integer.toString( line.getNumer() ).indexOf( lowerCaseFilter ) != -1) {
                    return true; // Filter matches first name.
                } 
                return false; // Does not match.
            });
        });
        
        FilteredList<Line> filteredCheckedLinesData = new FilteredList<>(filteredLinesData, p -> true);
        
        busTramFilter1.addListener((observable, oldValue, newValue) -> {
        	filteredCheckedLinesData.setPredicate(line -> {
                // If filter text is empty, display all.
                if (newValue == null || newValue.isEmpty()) { // ""
                    return true;
                }
                if (line.getTyp().indexOf( newValue ) != -1) { // A or T
                    return true; // Filter matches first name.
                } 
                return false; // AT Does not match.
            });
        });
        
        // 3. Wrap the FilteredList in a SortedList. 
        SortedList<Line> filteredCheckedSortedLinesData = new SortedList<>(filteredCheckedLinesData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        filteredCheckedSortedLinesData.comparatorProperty().bind(liniaTab1.comparatorProperty());
        
    	// Add observable list data to the table
        liniaTab1.setItems(filteredCheckedSortedLinesData);
        
        liniagc = liniaCanvas1.getGraphicsContext2D();
        
        // Clear line details.
        showLineDetails(null);

        // Listen for selection changes and show the person details when changed.
        liniaTab1.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showLineDetails(newValue));
    }
    
    @FXML public void busCheckBox1Chenged() {
    	busTramFilter1.set(busTramCheckBoxChenged(tramCheckBox1, busCheckBox1));
    }
    
    @FXML public void tramCheckBox1Chenged() {
    	busTramFilter1.set(busTramCheckBoxChenged(tramCheckBox1, busCheckBox1));
    }
    
    public void initRouteOverview() {
    	// Initialize the table with the columns.
    	lpCol1.setCellValueFactory(cellData -> cellData.getValue().lpProperty());
    	czasCol1.setCellValueFactory(cellData -> cellData.getValue().czasOdPetliProperty());
    	nazwaCol1.setCellValueFactory(cellData -> cellData.getValue().nazwaProperty());
    	lpCol1.setSortable(false);
        czasCol1.setSortable(false);
        nazwaCol1.setSortable(false);
    }
    
    
    /**
     * Fills all text fields to show details about the line.
     * If the specified line is null, all text fields are cleared.
     * 
     * @param line the line or null
     */
    private void showLineDetails(Line line) {
        if (line != null) {
            // Fill the labels with info from the line object.
        	numerLabel1.textProperty().bind(Bindings.convert(line.numerProperty()));
        	typLabel1.textProperty().bind(Bindings.convert(line.typProperty()));
            dlugoscLabel1.textProperty().bind(Bindings.convert(line.dlugoscProperty()));
            czestotliwoscKursowLabel1.textProperty().bind(Bindings.convert(line.czestotliwoscProperty()));
            czasPrzejazduLabel1.textProperty().bind(Bindings.convert(line.czasPrzejazduProperty()));
            //liczbaPojazdowLabel1.textProperty().bind(Bindings.convert(line.liczbaPojazdowProperty()));
            int lPojazdow = 0;
            for (Depot dep : depotsData) {
            	for (Driver d : dep.getDriversData()) {
            		if (d.getNumer() == line.getNumer())
            			lPojazdow++;
            	}
            }
            liczbaPojazdowLabel1.setText(Integer.toString(lPojazdow)+"/"+line.getLiczbaPojazdow());
            
            ObservableList<String> kierunki = FXCollections.observableArrayList();
            if (line.getRouteData().size()>0) {
            	kierunki.add(line.getRouteData().get(0).getNazwa());
            	kierunki.add(line.getRouteData().get(line.getRouteData().size()-1).getNazwa());
            	kierunekComboBox1.setItems(kierunki);
            	kierunekComboBox1.getSelectionModel().select(1);
            	line.wyliczCzasyPrzystankow(stopsData, true);
            } else {
            	kierunki.clear();
            	kierunekComboBox1.setItems(kierunki);
            }
            // Fill the Route TableView
        	trasaTab1.setItems(line.getRouteData());
        	
        	// Fill the map
        	liniagc.setFill(Color.WHITESMOKE);
        	liniagc.fillRect(0,0,12560,12560);
        	liniagc.drawImage(mapImg, 0, 0);
        	
        	// Draw stops and line between them
        	liniagc.setFill(Color.WHITESMOKE);
        	liniagc.setStroke(Color.RED);
        	liniagc.setLineWidth(5);
        	Stop lastStop=null;
        	for (int i=0; i<line.getRouteData().size(); i++) {
        		for (Stop s : stopsData ) {
    				if (s.getIdPrzystanku() == line.getRouteData().get(i).getIdPrzystanku()) {
						if (lastStop == null) { // it's first stop
							lastStop = s;
						} else {
							liniagc.strokeLine(lastStop.getX(), lastStop.getY(), s.getX(), s.getY());
							liniagc.fillOval(lastStop.getX()-MOVE, lastStop.getY()-MOVE, STOPSIZE, STOPSIZE);
			        		liniagc.strokeOval(lastStop.getX()-MOVE, lastStop.getY()-MOVE, STOPSIZE, STOPSIZE);
							liniagc.fillOval(s.getX()-MOVE, s.getY()-MOVE, STOPSIZE, STOPSIZE);
			        		liniagc.strokeOval(s.getX()-MOVE, s.getY()-MOVE, STOPSIZE, STOPSIZE);
			        		lastStop = s;
						}
						
					}
				}
        		
			}
        } else {
            // line is null, remove all the text.
        	numerLabel1.textProperty().unbind();
        	numerLabel1.setText("");	
        	typLabel1.textProperty().unbind();
        	typLabel1.setText("");
        	dlugoscLabel1.textProperty().unbind();
        	dlugoscLabel1.setText("");
        	czestotliwoscKursowLabel1.textProperty().unbind();
        	czestotliwoscKursowLabel1.setText("");
        	czasPrzejazduLabel1.textProperty().unbind();
        	czasPrzejazduLabel1.setText("");          
        	//liczbaPojazdowLabel1.textProperty().unbind();
        	liczbaPojazdowLabel1.setText("");
        	trasaTab1.setItems(null);
        	kierunekComboBox1.setItems(null);
        	// Fill the map
        	liniagc.setFill(Color.WHITESMOKE);
        	liniagc.fillRect(0,0,12560,12560);
        	liniagc.drawImage(mapImg, 0, 0);
        }
    }
    
    /**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new line.
     */
    @FXML
    private void handleNewLine() {
        Line tempLine = new Line();
        boolean okClicked = mainApp.showLineEditDialog(tempLine, stopsData, true);
        if (okClicked) {
        	DBUtil.insertLineToDB(mainApp.getConn(), tempLine);
        	DBUtil.insertRouteToDB(mainApp.getConn(), tempLine.getNumer(), tempLine.getRouteData());
        	DBUtil.callLosowaLiczbaPasazerow(mainApp.getConn(), tempLine.getNumer());
        	tempLine.setDlugosc( DBUtil.callWyliczDlugoscTrasy(mainApp.getConn(), tempLine.getNumer()) );
        	tempLine.wyliczCzasyPrzystankow(stopsData, true);
        	if (tempLine.getRouteData().size()>0) {
        		tempLine.setCzasPrzejazdu(tempLine.getRouteData().get(tempLine.getRouteData().size()-1).getCzasOdPetli());
        		DBUtil.updateCzasPrzejazdu(mainApp.getConn(), tempLine);
        		tempLine.setLiczbaPojazdow(DBUtil.callWyliczLiczbePojazdow(mainApp.getConn(), tempLine.getNumer()));
        	}
        		linesData.add(tempLine);
        }
    }
    
    
    @FXML
    private void handleEditLineFromLineTab() {
    	handleEditLine(liniaTab1);
    }
    
    @FXML
    private void handleEditLineFromDepotTab() {
    	handleEditLine(linesInDepotTab3a);
    }
    /**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new line.
     */
    private void handleEditLine(TableView<Line> tab) {
        Line tempLine = tab.getSelectionModel().getSelectedItem();
        if (tempLine != null) {
	        boolean okClicked = mainApp.showLineEditDialog(tempLine, stopsData, false);
	        if (okClicked) {
	        	DBUtil.updateLineInDB(mainApp.getConn(), tempLine);
	        	DBUtil.deleteRouteFromDB(mainApp.getConn(), tempLine.getNumer());
	        	DBUtil.insertRouteToDB(mainApp.getConn(), tempLine.getNumer(), tempLine.getRouteData());
	        	DBUtil.callLosowaLiczbaPasazerow(mainApp.getConn(), tempLine.getNumer());
	        	tempLine.setDlugosc( DBUtil.callWyliczDlugoscTrasy(mainApp.getConn(), tempLine.getNumer()) );
	        	tempLine.wyliczCzasyPrzystankow(stopsData, true);
	        	tempLine.setCzasPrzejazdu(tempLine.getRouteData().get(tempLine.getRouteData().size()-1).getCzasOdPetli());
	        	DBUtil.updateCzasPrzejazdu(mainApp.getConn(), tempLine);
	        	tempLine.setLiczbaPojazdow(DBUtil.callWyliczLiczbePojazdow(mainApp.getConn(), tempLine.getNumer()));
	        	showLineDetails(tempLine);
	        }
        }
    }
    
    @FXML
    private void handleDeleteLineFromLineTab() {
    	handleDeleteLine(liniaTab1);
    }
    
    @FXML
    private void handleDeleteLineFromDepotTab() {
    	handleDeleteLine(linesInDepotTab3a);
    	showDepotDetails(zajezdniaTab3.getSelectionModel().getSelectedItem());
    }
    
    /**
     * Called when the user clicks on the delete button.
     */
    private void handleDeleteLine(TableView<Line> tab) {
    	Line tempLine = tab.getSelectionModel().getSelectedItem();
    	String changes = "Pojazdy:\n";
    	int numOfChanges=0;
        if (tempLine != null) {
        	for (Depot depot : depotsData) {
				for (Vehicle veh : depot.getVehiclesData()) {
					if (veh.getNumer() == tempLine.getNumer()) {
						numOfChanges++;
						changes = changes + veh.getNrRejestracyjny() +"\n";
						veh.setNumer(0);
						DBUtil.updateVehiclesOnLinesInDB(mainApp.getConn(), veh);
					}
				}
			}
        	DBUtil.updateDriversOnLinesInDB(mainApp.getConn(), tempLine.getNumer());
        	changes += "Kierowcy:\n";
        	for (Depot depot : depotsData) {
				for (Driver veh : depot.getDriversData()) {
					if (veh.getNumer() == tempLine.getNumer()) {
						numOfChanges++;
						changes = changes + veh.getIdKierowcy() + " " + veh.getImie() + " " + veh.getNazwisko() +"\n";
						veh.setNumer(0);
					}
				}
			}
        	DBUtil.deleteRouteFromDB(mainApp.getConn(), tempLine.getNumer());
        	DBUtil.deleteLineFromDB(mainApp.getConn(), tempLine.getNumer());
	        linesData.remove(tempLine);
	        if (numOfChanges>0) Dialogs.create()
	            .title("Usuniêto klucze obce")
	            .masthead("Podczas usuwania linii usuniêto "+numOfChanges+" nastêpuj¹cych kluczy obcych:")
	            .message(changes)
	            .showInformation();
        }
    }
    
    @FXML
    private void handleLiniaAnchorPlus1Clicked() {
    	double scale1 = 2.0;
    	double scale2 = 2.0;
    	if (liniaAnchorPane1.getWidth()*scale2 <= liniaAnchorPane1.getMaxWidth()) {
    		liniagc.scale(scale1, scale1);
    		liniaCanvas1.resize(liniaCanvas1.getWidth()*scale2, liniaCanvas1.getHeight()*scale2);
	    	liniaAnchorPane1.setPrefSize(liniaAnchorPane1.getWidth()*scale2, liniaAnchorPane1.getHeight()*scale2);
	    	showLineDetails(liniaTab1.getSelectionModel().getSelectedItem());
    	}
    }
    
    @FXML
    private void handleLiniaAnchorMinus1Clicked() {
    	double scale1 = 0.5;
    	double scale2 = 0.5;
    	if (liniaAnchorPane1.getWidth()*scale2 >= liniaAnchorPane1.getMinWidth()) {
    		liniagc.scale(scale1, scale1);
    		liniaCanvas1.resize(liniaCanvas1.getWidth()*scale2, liniaCanvas1.getHeight()*scale2);
	    	liniaAnchorPane1.setPrefSize(liniaAnchorPane1.getWidth()*scale2, liniaAnchorPane1.getHeight()*scale2);
	    	showLineDetails(liniaTab1.getSelectionModel().getSelectedItem());
    	}
    }
    
    @FXML
    private void handleKierunekChange() {
    	boolean kier;
    	if (kierunekComboBox1.getItems() != null) {
    		if (kierunekComboBox1.getSelectionModel().getSelectedIndex()==0) kier = false; else kier=true;
    		liniaTab1.getSelectionModel().getSelectedItem().wyliczCzasyPrzystankow(stopsData, kier);
    	}
    }
    
    
    
    
    
    
    
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    // PRZYSTANKI 2 ////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////
    
    @FXML private TableView<Stop> przystanekTab2;
    @FXML private TableColumn<Stop, Number> idPrzystankuCol2;
    @FXML private TableColumn<Stop, String> nazwaCol2;
    @FXML private Label idPrzystankuLabel2;
    @FXML private Label nazwaLabel2;
    @FXML private Label xLabel2;
    @FXML private Label yLabel2;
    
    @FXML private TableView<Route> linesOnStopTab2;
    @FXML private TableColumn<Route, Number> numerCol2;
    @FXML private TableColumn<Route, String> typCol2;
    @FXML private TableColumn<Route, Number> wsiadajacyCol2;
    @FXML private TableColumn<Route, Number> wysiadajacyCol2;
    
    @FXML private Canvas przystanekCanvas2;
    @FXML private AnchorPane przystanekAnchorPane2;
    @FXML private Button przystanekAnchorPlus2;
    @FXML private Button przystanekAnchorMinus2;
    
    @FXML private TextField szukajTextField2;
    
    private GraphicsContext przystanekgc;
    private double lastXclicked;
    private double lastYclicked;
    private Stop selectedStopOnMap;
    private Stop lastSelectedStopOnMap;
    
    private double currScale = 1.0;
    
    private FilteredList<Stop> filteredStopData = new FilteredList<>(stopsData, p -> true);
    
    public double distance(double X1, double Y1, double X2, double Y2) {
        return Math.sqrt(
            (X1 - X2) *  (X1 - X2) + 
            (Y1 - Y2) *  (Y1 - Y2)
        );
    }
    
    @FXML
    public void showStopMap() {
    	showStopDetails(przystanekTab2.getSelectionModel().getSelectedItem());
    }
    
    public void initStopsOverview() {
    	// Initialize the line table with the two columns.
    	idPrzystankuCol2.setCellValueFactory(cellData -> cellData.getValue().idPrzystankuProperty());
    	nazwaCol2.setCellValueFactory(cellData -> cellData.getValue().nazwaProperty());
    	
    	// 1. Wrap the ObservableList in a FilteredList (initially display all data).
    	//filteredStopData.clear();
        
        // 2. Set the filter Predicate whenever the filter changes.
        szukajTextField2.textProperty().addListener((observable, oldValue, newValue) -> {
        	filteredStopData.setPredicate(stop -> {
                // If filter text is empty, display all.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare
                String lowerCaseFilter = newValue.toLowerCase();
                
                if (stop.getNazwa().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches first name.
                } else if (Integer.toString( stop.getIdPrzystanku() ).indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches last name.
                }
                return false; // Does not match.
            });
        });
        
        // 3. Wrap the FilteredList in a SortedList. 
        SortedList<Stop> filteredSortedStopData = new SortedList<>(filteredStopData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        filteredSortedStopData.comparatorProperty().bind(przystanekTab2.comparatorProperty());
        
    	// Add observable list data to the table
    	przystanekTab2.setItems(filteredSortedStopData);
        
    	przystanekgc = przystanekCanvas2.getGraphicsContext2D();
    	
        // Clear line details.
        showStopDetails(null);

        // Listen for selection changes and show the person details when changed.
        przystanekTab2.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showStopDetails(newValue));
        
        przystanekCanvas2.addEventHandler(MouseEvent.MOUSE_CLICKED, 
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                    	lastXclicked = t.getX();
                    	lastYclicked = t.getY();
                    	lastSelectedStopOnMap = selectedStopOnMap;
                    	for (Stop stop : stopsData) {
							if (distance(lastXclicked*currScale, lastYclicked*currScale, 
									stop.getX(), stop.getY()) < DIST/currScale) {
								selectedStopOnMap = stop;
								break;
							}
						}
                    	if (lastSelectedStopOnMap == selectedStopOnMap) selectedStopOnMap = null;
                    	
                    	if (selectedStopOnMap != null) {
                			showStopDetails(selectedStopOnMap);
                			przystanekTab2.getSelectionModel().select(selectedStopOnMap);
                    	}
                    }
                });
    }
    
    public void initLinesOnStopOverview() {
    	// Initialize the table with the columns.
    	numerCol2.setCellValueFactory(cellData -> cellData.getValue().numerProperty());
    	typCol2.setCellValueFactory(cellData -> cellData.getValue().typProperty());
    	wsiadajacyCol2.setCellValueFactory(cellData -> cellData.getValue().liczbaWsiadajacychProperty());
    	wysiadajacyCol2.setCellValueFactory(cellData -> cellData.getValue().liczbaWysiadajacychProperty());
    }
    
    /**
     * Fills all text fields to show details about the stop.
     * If the specified stop is null, all text fields are cleared.
     * 
     * @param stop the stop or null
     */
    private void showStopDetails(Stop stop) {
        if (stop != null) {
            // Fill the labels with info from the line object.
        	idPrzystankuLabel2.textProperty().bind(Bindings.convert(stop.idPrzystankuProperty()));
        	nazwaLabel2.textProperty().bind(Bindings.convert(stop.nazwaProperty()));
        	xLabel2.textProperty().bind(Bindings.convert(stop.xProperty()));
        	yLabel2.textProperty().bind(Bindings.convert(stop.yProperty()));
        	
        	// Fill the LinesOnStop Tableview
        	ObservableList<Route> linesOnStopData = FXCollections.observableArrayList();
        	for (Line line : linesData) {
				for (Route route : line.getRouteData()) {
					if (route.getIdPrzystanku() == stop.getIdPrzystanku()) {
						Route r = route;
						r.setNazwa(stop.getNazwa());
						r.setTyp(line.getTyp());
						linesOnStopData.add(r);
					}
				}
			}
        	// Add observable list data to the table LinesOnStop
        	linesOnStopTab2.setItems(linesOnStopData);
        	
        	// Fill the map
        	przystanekgc.setFill(Color.WHITESMOKE);
        	przystanekgc.fillRect(0,0,12560,12560);
        	przystanekgc.drawImage(mapImg, 0, 0);
        	
        	// Draw stops
        	przystanekgc.setFill(Color.WHITESMOKE);
        	przystanekgc.setStroke(Color.DARKSLATEGRAY);
        	przystanekgc.setLineWidth(5);
        	for (Stop s : filteredStopData) {
        		przystanekgc.fillOval(s.getX()-MOVE, s.getY()-MOVE, STOPSIZE, STOPSIZE);
        		przystanekgc.strokeOval(s.getX()-MOVE, s.getY()-MOVE, STOPSIZE, STOPSIZE);
			}
        	
        	// Draw selected stop
        	przystanekgc.setFill(Color.WHITESMOKE);
        	przystanekgc.setStroke(Color.RED);
        	przystanekgc.fillOval(stop.getX()-MOVE, stop.getY()-MOVE, STOPSIZE, STOPSIZE);
    		przystanekgc.strokeOval(stop.getX()-MOVE, stop.getY()-MOVE, STOPSIZE, STOPSIZE);
    		
        } else {
            // stop is null, remove all the text.
        	idPrzystankuLabel2.textProperty().unbind();
        	idPrzystankuLabel2.setText("");	
        	nazwaLabel2.textProperty().unbind();
        	nazwaLabel2.setText("");
        	xLabel2.textProperty().unbind();
        	xLabel2.setText("");
        	yLabel2.textProperty().unbind();
        	yLabel2.setText("");
        	linesOnStopTab2.setItems(null);
        	// Fill the map
        	przystanekgc.setFill(Color.WHITESMOKE);
        	przystanekgc.fillRect(0,0,12560,12560);
        	przystanekgc.drawImage(mapImg, 0, 0);
        	
        	// Draw stops
        	przystanekgc.setFill(Color.WHITESMOKE);
        	przystanekgc.setStroke(Color.DARKSLATEGRAY);
        	przystanekgc.setLineWidth(5);
        	for (Stop s : filteredStopData) {
        		przystanekgc.fillOval(s.getX()-MOVE, s.getY()-MOVE, STOPSIZE, STOPSIZE);
        		przystanekgc.strokeOval(s.getX()-MOVE, s.getY()-MOVE, STOPSIZE, STOPSIZE);
			}
        }
    }
    
    /**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new stop.
     */
    @FXML
    private void handleNewStop() {
        Stop tempStop = new Stop();
        boolean okClicked = mainApp.showStopEditDialog(tempStop, stopsData);
        if (okClicked) {
        	DBUtil.insertStopToDB(mainApp.getConn(), tempStop);
        	stopsData.add(tempStop);
        }
        showStopMap();
    }
    
    /**
     * Called when the user clicks the edit button. Opens a dialog to edit
     * details for the selected person.
     */
    @FXML
    private void handleEditStop() {
    	Stop selectedStop = przystanekTab2.getSelectionModel().getSelectedItem();
        if (selectedStop != null) {
            boolean okClicked = mainApp.showStopEditDialog(selectedStop, stopsData);
            if (okClicked) {
            	//modify DB
            	DBUtil.updateStopInDB(mainApp.getConn(), selectedStop);
                showStopDetails(selectedStop);
                for (Line line : linesData) {
                	for (Route route : line.getRouteData()) {
                		if (route.getIdPrzystanku() == selectedStop.getIdPrzystanku()) {
                			route.setNazwa(selectedStop.getNazwa());
                			DBUtil.deleteRouteFromDB(mainApp.getConn(), line.getNumer());
            	        	DBUtil.insertRouteToDB(mainApp.getConn(), line.getNumer(), line.getRouteData());
            	        	line.setDlugosc( DBUtil.callWyliczDlugoscTrasy(mainApp.getConn(), line.getNumer()) );
            	        	line.wyliczCzasyPrzystankow(stopsData, true);
            	        	line.setCzasPrzejazdu(line.getRouteData().get(line.getRouteData().size()-1).getCzasOdPetli());
            	        	DBUtil.updateCzasPrzejazdu(mainApp.getConn(), line);
            	        	line.setLiczbaPojazdow(DBUtil.callWyliczLiczbePojazdow(mainApp.getConn(), line.getNumer()));
                		}
                	}
				}
                showLineDetails(null);
                liniaTab1.getSelectionModel().clearSelection();
            }
        } 
        showStopMap();
    }
    
    
    /*private boolean isStopInAnyRoute(Stop stop) {
    	for (Line line : linesData) {
			for (Route r : line.getRouteData()) {
				if (r.getIdPrzystanku() == stop.getIdPrzystanku()) return true;
			}
		}
    	return false;
    }*/
    
    /**
     * Called when the user clicks the delete button. 
     */
    @FXML
    private void handleDeleteStop() {
    	Stop selectedStop = przystanekTab2.getSelectionModel().getSelectedItem();
        if (selectedStop != null) {
        	//check if stop is on any route
        	int numberOfLines = 0;
        	String which="";
        	for (Line line : linesData) {
    			for (Route r : line.getRouteData()) {
    				if (r.getIdPrzystanku() == selectedStop.getIdPrzystanku()) {
    					numberOfLines++;
    					which += line.getNumer() + ", ";
    				}
    			}
    		}
        	if (numberOfLines>0) {
        		//error - remove stop from routes first
        		Dialogs.create()
	                .title("Przystanek przypisany do linii")
	                .masthead("Przystanek, który chcesz usun¹æ jest przypisany do "+numberOfLines+" linii: " + which.substring(0, which.length()-2))
	                .message("Zanim usuniesz przystanek, usuñ go najpierw z tras wszystkich linii (edytuj liniê).")
	                .showError();
        	} else {
            	//modify DB
            	DBUtil.deleteStopFromDB(mainApp.getConn(), selectedStop.getIdPrzystanku());
            	stopsData.remove(selectedStop);
	            
        	}
        } 
        showStopMap();
    }
   
    
    @FXML
    private void handlePrzystanekAnchorPlus2Clicked() {
    	double scale1 = 2.0;
    	double scale2 = 2.0;
    	if (przystanekAnchorPane2.getWidth()*scale2 <= przystanekAnchorPane2.getMaxWidth()) {
    		currScale *= 0.5;
    		przystanekgc.scale(scale1, scale1);
    		przystanekCanvas2.resize(przystanekCanvas2.getWidth()*scale2, przystanekCanvas2.getHeight()*scale2);
        	przystanekAnchorPane2.setPrefSize(przystanekAnchorPane2.getWidth()*scale2, przystanekAnchorPane2.getHeight()*scale2);
        	showStopDetails(przystanekTab2.getSelectionModel().getSelectedItem());
    	} 
    }
    
    @FXML
    private void handlePrzystanekAnchorMinus2Clicked() {
    	double scale1 = 0.5;
    	double scale2 = 0.5;
    	if (przystanekAnchorPane2.getWidth()*scale2 >= przystanekAnchorPane2.getMinWidth()) {
    		currScale *= 2.0;
	    	przystanekgc.scale(scale1, scale1);
	    	przystanekCanvas2.resize(przystanekCanvas2.getWidth()*scale2, przystanekCanvas2.getHeight()*scale2);
	    	przystanekAnchorPane2.setPrefSize(przystanekAnchorPane2.getWidth()*scale2, przystanekAnchorPane2.getHeight()*scale2);
	    	showStopDetails(przystanekTab2.getSelectionModel().getSelectedItem());
    	}
    }
    
    
    
    
    
    
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////    
    // ZAJEZDNIE 3 ////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////    
    
    @FXML private TableView<Depot> zajezdniaTab3;
    @FXML private TableColumn<Depot, Number> idZajezdniCol3;
    @FXML private TableColumn<Depot, String> typCol3;
    @FXML private TableColumn<Depot, String> adresCol3;
    
    @FXML private TableView<Driver> driversInDepotTab3;
    @FXML private TableColumn<Driver, Number> idKierowcyCol3a;
    @FXML private TableColumn<Driver, String> imieCol3a;
    @FXML private TableColumn<Driver, String> nazwiskoCol3a;
    
    @FXML private TableView<Line> linesInDepotTab3a;
    @FXML private TableColumn<Line, Number> numerCol3a;
    @FXML private TableColumn<Line, String> typLiniiCol3a;
    
    @FXML private TableView<Vehicle> vehiclesInDepotTab3a;
    @FXML private TableColumn<Vehicle, String> nrRejestracyjnyCol3a;
    @FXML private TableColumn<Vehicle, String> typCol3a;
    @FXML private TableColumn<Vehicle, String> markaCol3a;
    @FXML private TableColumn<Vehicle, String> modelCol3a;
    
    @FXML private Label idZajezdniLabel3;
    @FXML private Label adresLabel3;
    @FXML private Label typLabel3;
    private StringProperty busTramFilter3;
    
    @FXML private TextField szukajTextField3;
    @FXML private CheckBox tramCheckBox3;
    @FXML private CheckBox busCheckBox3;
    
    
    public void initDepotsOverview() {
    	// Initialize the line table with the two columns.
    	idZajezdniCol3.setCellValueFactory(cellData -> cellData.getValue().idZajezdniProperty());
        typCol3.setCellValueFactory(cellData -> cellData.getValue().typProperty());
        adresCol3.setCellValueFactory(cellData -> cellData.getValue().adresProperty());
    	
        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<Depot> filteredDepotsData = new FilteredList<>(depotsData, p -> true);
        
        // 2. Set the filter Predicate whenever the filter changes.
        szukajTextField3.textProperty().addListener((observable, oldValue, newValue) -> {
        	filteredDepotsData.setPredicate(depot -> {
                // If filter text is empty, display all.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare
                String lowerCaseFilter = newValue.toLowerCase();
                
                if (Integer.toString( depot.getIdZajezdni() ).toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches
                } else if (depot.getAdres().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches 
                } 
                return false; // Does not match.
            });
        });
        
        
        FilteredList<Depot> filteredCheckedDepotsData = new FilteredList<>(filteredDepotsData, p -> true);
        
        busTramFilter3.addListener((observable, oldValue, newValue) -> {
        	filteredCheckedDepotsData.setPredicate(depot -> {
                // If filter text is empty, display all.
                if (newValue == null || newValue.isEmpty()) { // ""
                    return true;
                }
                if (depot.getTyp().indexOf( newValue ) != -1) { // A or T
                    return true; // Filter matches
                } 
                return false; // AT Does not match.
            });
        });
        
        // 3. Wrap the FilteredList in a SortedList. 
        SortedList<Depot> filteredCheckedSortedDepotsData = new SortedList<>(filteredCheckedDepotsData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        filteredCheckedSortedDepotsData.comparatorProperty().bind(zajezdniaTab3.comparatorProperty());
        
    	// Add observable list data to the table
        zajezdniaTab3.setItems(filteredCheckedSortedDepotsData);
        
        // Clear line details.
        showDepotDetails(null);

        // Listen for selection changes and show the person details when changed.
        zajezdniaTab3.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showDepotDetails(newValue));
    }
    
    @FXML public void busCheckBox3Chenged() {
    	busTramFilter3.set(busTramCheckBoxChenged(tramCheckBox3, busCheckBox3));
    }
    
    @FXML public void tramCheckBox3Chenged() {
    	busTramFilter3.set(busTramCheckBoxChenged(tramCheckBox3, busCheckBox3));
    }
    
    public void initDriversInDepotTableview() {
    	// Initialize the table with the columns.
    	idKierowcyCol3a.setCellValueFactory(cellData -> cellData.getValue().idKierowcyProperty());
    	imieCol3a.setCellValueFactory(cellData -> cellData.getValue().imieProperty());
    	nazwiskoCol3a.setCellValueFactory(cellData -> cellData.getValue().nazwiskoProperty());
    	idKierowcyCol3a.setSortable(false);
        imieCol3a.setSortable(false);
        nazwiskoCol3a.setSortable(false);
    }
    
    public void initVehiclesInDepotTableview() {
    	// Initialize the table with the columns.
    	nrRejestracyjnyCol3a.setCellValueFactory(cellData -> cellData.getValue().nrRejestracyjnyProperty());
    	typCol3a.setCellValueFactory(cellData -> cellData.getValue().typProperty());
    	markaCol3a.setCellValueFactory(cellData -> cellData.getValue().markaProperty());
    	modelCol3a.setCellValueFactory(cellData -> cellData.getValue().modelProperty());
    	nrRejestracyjnyCol3a.setSortable(false);
    	typCol3a.setSortable(false);
    	markaCol3a.setSortable(false);
    	modelCol3a.setSortable(false);
    }
    
    public void initLinesInDepotTableview() {
    	// Initialize the table with the columns.
    	numerCol3a.setCellValueFactory(cellData -> cellData.getValue().numerProperty());
    	typLiniiCol3a.setCellValueFactory(cellData -> cellData.getValue().typProperty());
    	numerCol3a.setSortable(false);
    	typLiniiCol3a.setSortable(false);
    }
    
    /**
     * Fills all text fields to show details about the depot.
     * If the specified depot is null, all text fields are cleared.
     * 
     * @param depot the depot or null
     */
    private void showDepotDetails(Depot depot) {
        if (depot != null) {
            // Fill the labels with info from the line object.
        	idZajezdniLabel3.textProperty().bind(Bindings.convert(depot.idZajezdniProperty()));
            adresLabel3.textProperty().bind(Bindings.convert(depot.adresProperty()));
            typLabel3.textProperty().bind(Bindings.convert(depot.typProperty()));
            
        	// Add observable list data to the table driversInDepotTab3
        	driversInDepotTab3.setItems(depot.getDriversData());
            
        	// Add observable list data to the table vehiclesInDepotTab3a
        	vehiclesInDepotTab3a.setItems(depot.getVehiclesData());
        	
        	// Add observable list data to the table vehiclesInDepotTab3a
        	ObservableList<Line> tempLinesData = FXCollections.observableArrayList();
        	for (Line line : linesData) {
				for (Driver driver : depot.getDriversData()) {
					if (driver.getNumer() == line.getNumer()) {
						if (!tempLinesData.contains(line))
								tempLinesData.add(line);
					}
				}
			}
        	linesInDepotTab3a.setItems(tempLinesData);
        } else {
            // line is null, remove all the text.
        	idZajezdniLabel3.textProperty().unbind();
        	idZajezdniLabel3.setText("");	
        	adresLabel3.textProperty().unbind();
        	adresLabel3.setText("");
        	typLabel3.textProperty().unbind();
        	typLabel3.setText("");
        	driversInDepotTab3.setItems(null);
        	vehiclesInDepotTab3a.setItems(null);
        	linesInDepotTab3a.setItems(null);
        }
    }
    
    @FXML
    public void showStuffWhenDriverInDepotClicked() {
    	showDepotDetails(zajezdniaTab3.getSelectionModel().getSelectedItem());
    	Driver selectedDriver= driversInDepotTab3.getSelectionModel().getSelectedItem();
    	if (selectedDriver != null) {
    		if (selectedDriver.getNrRejestracyjny() != null){
    			Vehicle matchingVehicle=null;
        		for (Vehicle vehicle : zajezdniaTab3.getSelectionModel().getSelectedItem().getVehiclesData()) {
    				if (vehicle.getNrRejestracyjny().equals(selectedDriver.getNrRejestracyjny()) )
    					matchingVehicle = vehicle;
    			}
        		if (matchingVehicle != null)
        			vehiclesInDepotTab3a.getSelectionModel().select(matchingVehicle);
    		} else
    			vehiclesInDepotTab3a.getSelectionModel().clearSelection();
    			
    		if (selectedDriver.getNumer() != 0) {
	    		Line matchingLine=null;
	    		for (Line line : linesData) {
					if (line.getNumer() == selectedDriver.getNumer())
						matchingLine = line;
				}
	    		if (matchingLine != null)
        			linesInDepotTab3a.getSelectionModel().select(matchingLine);
    		} else
    			linesInDepotTab3a.getSelectionModel().clearSelection();
    	} else {
    		vehiclesInDepotTab3a.getSelectionModel().clearSelection();
    		linesInDepotTab3a.getSelectionModel().clearSelection();
    		driversInDepotTab3.getSelectionModel().clearSelection();
    	}	
    }
    
    @FXML
    public void showStuffWhenVehicleInDepotClicked() {
    	showDepotDetails(zajezdniaTab3.getSelectionModel().getSelectedItem());
    	Vehicle selectedVehicle= vehiclesInDepotTab3a.getSelectionModel().getSelectedItem();
    	if (selectedVehicle != null) {
    		if (selectedVehicle.getNrRejestracyjny() != null){
    			Driver matchingDriver=null;
        		for (Driver driver : zajezdniaTab3.getSelectionModel().getSelectedItem().getDriversData()) {
        			if (driver.getNrRejestracyjny() != null 
        					&& driver.getNrRejestracyjny().equals(selectedVehicle.getNrRejestracyjny()) )
    					matchingDriver = driver;
    			}
        		if (matchingDriver != null)
        			driversInDepotTab3.getSelectionModel().select(matchingDriver);
        		else
        			driversInDepotTab3.getSelectionModel().clearSelection();
    		} else
    			driversInDepotTab3.getSelectionModel().clearSelection();
    			
    		if (selectedVehicle.getNumer() != 0) {
	    		Line matchingLine=null;
	    		for (Line line : linesData) {
					if (line.getNumer() == selectedVehicle.getNumer())
						matchingLine = line;
				}
	    		if (matchingLine != null)
        			linesInDepotTab3a.getSelectionModel().select(matchingLine);
	    		else
	    			linesInDepotTab3a.getSelectionModel().clearSelection();
    		} else
    			linesInDepotTab3a.getSelectionModel().clearSelection();
    	} else {
    		vehiclesInDepotTab3a.getSelectionModel().clearSelection();
    		linesInDepotTab3a.getSelectionModel().clearSelection();
    		driversInDepotTab3.getSelectionModel().clearSelection();
    	}	
    }
    
    @FXML
    public void showDriverInTabPane() {
    	Driver selectedDriver= driversInDepotTab3.getSelectionModel().getSelectedItem();
    	if (selectedDriver != null) {
    		tabPane.getSelectionModel().select(driversTabPane5);
    		kierowcaTab5.getSelectionModel().select(selectedDriver);
    		showDriverDetails(selectedDriver);
    	}
    }
    
    @FXML
    public void showLineInTabPane() {
    	Line selectedLine= linesInDepotTab3a.getSelectionModel().getSelectedItem();
    	if (selectedLine != null) {
    		tabPane.getSelectionModel().select(linesTabPane1);
    		liniaTab1.getSelectionModel().select(selectedLine);
    		showLineDetails(selectedLine);
    	}
    }
    
    @FXML
    public void showVehicleInTabPane() {
    	Vehicle selectedvehicle= vehiclesInDepotTab3a.getSelectionModel().getSelectedItem();
    	if (selectedvehicle != null) {
    		tabPane.getSelectionModel().select(vehiclesTabPane4);
    		pojazdTab4.getSelectionModel().select(selectedvehicle);
    		showVehicleDetails(selectedvehicle);
    	}
    }
    /**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new depot.
     */
    @FXML
    private void handleNewDepot() {
        Depot tempDepot = new Depot();
        boolean okClicked = mainApp.showDepotEditDialog(tempDepot, false);
        if (okClicked) {
        	DBUtil.insertDepotToDB(mainApp.getConn(), tempDepot);
        	depotsData.add(tempDepot);
        }
    }
    
    /**
     * Called when the user clicks the edit button. Opens a dialog to edit
     * details for the selected depot.
     */
    @FXML
    private void handleEditDepot() {
    	Depot selectedDepot = zajezdniaTab3.getSelectionModel().getSelectedItem();
        if (selectedDepot != null) {
            boolean okClicked = mainApp.showDepotEditDialog(selectedDepot, isAnyDriverOrVehicleInDepot(selectedDepot));
            if (okClicked) {
            	//modify DB
            	DBUtil.updateDepotInDB(mainApp.getConn(), selectedDepot);
                showDepotDetails(selectedDepot);
            }

        } 
    }
    
    private boolean isAnyDriverOrVehicleInDepot(Depot depot) {
    	if (depot.getDriversData().size()>0 || depot.getVehiclesData().size()>0) return true;
    	return false;
    }
    
    /**
     * Called when the user clicks the delete button. 
     */
    @FXML
    private void handleDeleteDepot() {
    	Depot selectedDepot = zajezdniaTab3.getSelectionModel().getSelectedItem();
        if (selectedDepot != null) {
        	//check if stop is on any route
        	if (isAnyDriverOrVehicleInDepot(selectedDepot)) {
        		//error - remove or move drivers and vehicles
        		Dialogs.create()
	                .title("Zajeznia nie jest pusta")
	                .masthead("Do zajezdni, któr¹ chcesz usun¹æ jest przypisany co najmniej jeden kierowca lub pojazd.")
	                .message("Zanim usuniesz zajezdniê, zwolnij lub przenieœ kierowców do innej zajezdni oraz sprzedaj lub przenieœ do innej zajezdni wszystkie pojazdy.")
	                .showError();
        	} else {
            	//modify DB
            	DBUtil.deleteDepotFromDB(mainApp.getConn(), selectedDepot.getIdZajezdni());
            	depotsData.remove(selectedDepot);
	            
        	}
        } 
    }
    
    
    
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////    
    // POJAZDY 4 ////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////    
    
    @FXML private TableView<Vehicle> pojazdTab4;
    @FXML private TableColumn<Vehicle, String> nrRejestracyjnyCol4;
    @FXML private TableColumn<Vehicle, String> typCol4;
    @FXML private TableColumn<Vehicle, String> markaCol4;
    @FXML private TableColumn<Vehicle, String> modelCol4;
    
    @FXML private TextField szukajTextField4;
    @FXML private CheckBox tramCheckBox4;
    @FXML private CheckBox busCheckBox4;
    private StringProperty busTramFilter4;
    
    @FXML private Label nrRejestracyjnyLabel4;
    @FXML private Label typLabel4;
    @FXML private Label markaLabel4;
    @FXML private Label modelLabel4;
    @FXML private Label nrBocznyLabel4;
    @FXML private Label rocznikLabel4;
    @FXML private Label liczbaSiedzenLabel4;
    @FXML private Label maxLiczbaPasazerowLabel4;
    @FXML private CheckBox biletomatCheckBox4;
    @FXML private CheckBox klimatyzacjaCheckBox4;
    @FXML private Label spalanieLabel4; //bus
    @FXML private Label zuzyciePraduLabel4; //tram
    @FXML private Label spalanieLabel4tekst; //bus
    @FXML private Label zuzyciePraduLabel4tekst; //tram
    @FXML private CheckBox niskopodlogowyCheckBox4;
    @FXML private Label kierowcaLabel4; 
    @FXML private Label zajezdniaLabel4; 
    @FXML private Label liniaLabel4; 
    
    
    @FXML private Tab vehiclesTabPane4;
    
    @FXML
    public void refreshVehiclesData() {
    	vehiclesData.clear();
    	for (Depot depot : depotsData) {
			for (Vehicle veh : depot.getVehiclesData()) {
				vehiclesData.add(veh);
			}
		}
    }
    
    public void initVehiclesOverview() {
    	// Initialize the line table with the two columns.
    	nrRejestracyjnyCol4.setCellValueFactory(cellData -> cellData.getValue().nrRejestracyjnyProperty());
    	typCol4.setCellValueFactory(cellData -> cellData.getValue().typProperty());
    	markaCol4.setCellValueFactory(cellData -> cellData.getValue().markaProperty());
    	modelCol4.setCellValueFactory(cellData -> cellData.getValue().modelProperty());
    	
    	// 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<Vehicle> filteredVehiclesData = new FilteredList<>(vehiclesData, p -> true);
        
        // 2. Set the filter Predicate whenever the filter changes.
        szukajTextField4.textProperty().addListener((observable, oldValue, newValue) -> {
        	filteredVehiclesData.setPredicate(vehicle -> {
                // If filter text is empty, display all.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare
                String lowerCaseFilter = newValue.toLowerCase();
                
                if (vehicle.getNrRejestracyjny().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches
                } else if (vehicle.getTyp().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches 
                } else if (vehicle.getMarka().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                	return true; // Filter matches
                } else if (vehicle.getModel().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                	return true; // Filter matches
                }
                return false; // Does not match.
            });
        });
        
        FilteredList<Vehicle> filteredCheckedVehiclesData = new FilteredList<>(filteredVehiclesData, p -> true);
        
        busTramFilter4.addListener((observable, oldValue, newValue) -> {
        	filteredCheckedVehiclesData.setPredicate(vehicle -> {
                // If filter text is empty, display all.
                if (newValue == null || newValue.isEmpty()) { // ""
                    return true;
                }
                if (vehicle.getTyp().indexOf( newValue ) != -1) { // A or T
                    return true; // Filter matches
                } 
                return false; // AT Does not match.
            });
        });
        
        // 3. Wrap the FilteredList in a SortedList. 
        SortedList<Vehicle> filteredCheckedSortedVehiclesData = new SortedList<>(filteredCheckedVehiclesData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        filteredCheckedSortedVehiclesData.comparatorProperty().bind(pojazdTab4.comparatorProperty());
        
    	// Add observable list data to the table
        pojazdTab4.setItems(filteredCheckedSortedVehiclesData);
        
        // Clear line details.
        showVehicleDetails(null);

        // Listen for selection changes and show the person details when changed.
        pojazdTab4.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showVehicleDetails(newValue));
        
    }
    
    private void showVehicleDetails(Vehicle vehicle) {
        if (vehicle != null) {
            // Fill the labels with info from the line object.
        	nrRejestracyjnyLabel4.textProperty().bind(Bindings.convert(vehicle.nrRejestracyjnyProperty()));
            typLabel4.textProperty().bind(Bindings.convert(vehicle.typProperty()));
            markaLabel4.textProperty().bind(Bindings.convert(vehicle.markaProperty()));
            modelLabel4.textProperty().bind(Bindings.convert(vehicle.modelProperty()));
            nrBocznyLabel4.textProperty().bind(Bindings.convert(vehicle.nrBocznyProperty()));
            rocznikLabel4.textProperty().bind(Bindings.convert(vehicle.rocznikProperty()));
            liczbaSiedzenLabel4.textProperty().bind(Bindings.convert(vehicle.liczbaSiedzenProperty()));
            maxLiczbaPasazerowLabel4.textProperty().bind(Bindings.convert(vehicle.maxLiczbaPasazerowProperty()));
            biletomatCheckBox4.setVisible(true);
            klimatyzacjaCheckBox4.setVisible(true);
            niskopodlogowyCheckBox4.setVisible(true);
            if (vehicle.getBiletomat().equals("T"))
            	biletomatCheckBox4.setSelected(true);
            else 
            	biletomatCheckBox4.setSelected(false);
            if (vehicle.getKlimatyzacja().equals("T"))
            	klimatyzacjaCheckBox4.setSelected(true);
            else 
            	klimatyzacjaCheckBox4.setSelected(false);
            if (vehicle.getNiskopodlogowy().equals("T"))
            	niskopodlogowyCheckBox4.setSelected(true);
            else 
            	niskopodlogowyCheckBox4.setSelected(false);
            spalanieLabel4.textProperty().bind(Bindings.convert(vehicle.spalanieProperty()));
            zuzyciePraduLabel4.textProperty().bind(Bindings.convert(vehicle.zuzyciePraduProperty()));
            if (vehicle.getTyp().equals("A") ) {
            	spalanieLabel4.setVisible(true);
            	spalanieLabel4tekst.setVisible(true);
            	zuzyciePraduLabel4.setVisible(false);
	            zuzyciePraduLabel4tekst.setVisible(false);
            } else {      
            	spalanieLabel4.setVisible(false);
            	spalanieLabel4tekst.setVisible(false);
            	zuzyciePraduLabel4.setVisible(true);
	            zuzyciePraduLabel4tekst.setVisible(true);
            }
            kierowcaLabel4.setText(""); 
            zajezdniaLabel4.setText("");
            for (Depot dep : depotsData) {
            	if (vehicle.getIdZajezdni() == dep.getIdZajezdni()) {
            		zajezdniaLabel4.setText(dep.toString());
            		for (Driver dr : dep.getDriversData()) {
            			if (dr.getNrRejestracyjny()!= null && dr.getNrRejestracyjny().equals(vehicle.getNrRejestracyjny())) {
            				kierowcaLabel4.setText(dr.toString());
            			}
            		}
            	}
            }
            if (vehicle.getNumer()!=0) liniaLabel4.textProperty().bind(Bindings.convert(vehicle.numerProperty()));
            else {
            	liniaLabel4.textProperty().unbind();
                liniaLabel4.setText("");
            }
        } else {
            // line is null, remove all the text.
        	nrRejestracyjnyLabel4.textProperty().unbind();
        	nrRejestracyjnyLabel4.setText("");	
        	typLabel4.textProperty().unbind();
        	typLabel4.setText("");
        	markaLabel4.textProperty().unbind();
        	markaLabel4.setText("");
        	modelLabel4.textProperty().unbind();
        	modelLabel4.setText("");
        	nrBocznyLabel4.textProperty().unbind();
        	nrBocznyLabel4.setText("");          
        	rocznikLabel4.textProperty().unbind();
        	rocznikLabel4.setText("");
        	liczbaSiedzenLabel4.textProperty().unbind();
        	liczbaSiedzenLabel4.setText("");
        	maxLiczbaPasazerowLabel4.textProperty().unbind();
        	maxLiczbaPasazerowLabel4.setText("");
        	spalanieLabel4.textProperty().unbind();
        	spalanieLabel4.setText("");
        	zuzyciePraduLabel4.textProperty().unbind();
        	zuzyciePraduLabel4.setText("");
        	biletomatCheckBox4.setSelected(false);
        	klimatyzacjaCheckBox4.setSelected(false);
        	niskopodlogowyCheckBox4.setSelected(false);
        	spalanieLabel4.setVisible(false);
        	spalanieLabel4tekst.setVisible(false);
        	zuzyciePraduLabel4.setVisible(false);
            zuzyciePraduLabel4tekst.setVisible(false);
            biletomatCheckBox4.setVisible(false);
            klimatyzacjaCheckBox4.setVisible(false);
            niskopodlogowyCheckBox4.setVisible(false);
            kierowcaLabel4.setText(""); 
            zajezdniaLabel4.setText("");
            liniaLabel4.textProperty().unbind();
            liniaLabel4.setText("");
        }
    }
    
    @FXML public void busCheckBox4Chenged() {
    	busTramFilter4.set(busTramCheckBoxChenged(tramCheckBox4, busCheckBox4));
    }
    
    @FXML public void tramCheckBox4Chenged() {
    	busTramFilter4.set(busTramCheckBoxChenged(tramCheckBox4, busCheckBox4));
    }
    
    @FXML public void ignoreSelectionKlima() {
    	klimatyzacjaCheckBox4.setSelected(!klimatyzacjaCheckBox4.isSelected());
    }
    
    @FXML public void ignoreSelectionBiletomat() {
    	biletomatCheckBox4.setSelected(!biletomatCheckBox4.isSelected());
    }
    
    @FXML public void ignoreSelectionNisko() {
    	niskopodlogowyCheckBox4.setSelected(!niskopodlogowyCheckBox4.isSelected());
    }
    
    @FXML
    private void handleNewVehicleFromDepot() {
    	Vehicle tempVehicle = new Vehicle();
    	Depot depot = zajezdniaTab3.getSelectionModel().getSelectedItem();
    	if (depot != null) {
    		tempVehicle.setIdZajezdni(depot.getIdZajezdni());
    		tempVehicle.setTyp(depot.getTyp());
    		handleNewVehicle(tempVehicle, true);
    	} else 
    		handleNewVehicle(tempVehicle, false);
    }
    
    @FXML
    private void handleNewVehicleFromVehicleTabPane() {
    	Vehicle tempVehicle = new Vehicle();
    	handleNewVehicle(tempVehicle, false);
    	refreshVehiclesData();
    }

    private void handleNewVehicle(Vehicle tempVehicle, boolean setDepot) {
        boolean okClicked = mainApp.showVehicleEditDialog(tempVehicle, false, setDepot, depotsData);
        if (okClicked) {
        	DBUtil.insertVehicleToDB(mainApp.getConn(), tempVehicle);
        	for (Depot depot : depotsData) {
				if (tempVehicle.getIdZajezdni() == depot.getIdZajezdni()) {
					depot.getVehiclesData().add(tempVehicle);
					for (Driver dr : depot.getDriversData()) {
						if (dr.getNrRejestracyjny() != null 
								&& dr.getNrRejestracyjny().equals(tempVehicle.getNrRejestracyjny())) {
							DBUtil.updateDriverInDB(mainApp.getConn(), dr);
						}
					}
				}
			}
        }
        showStuffWhenVehicleInDepotClicked();
    }
    
    @FXML
    private void handleEditVehicleFromDepot() {
    	Vehicle tempVehicle = vehiclesInDepotTab3a.getSelectionModel().getSelectedItem();
    	handleEditVehicle(tempVehicle);
    }
    
    @FXML
    private void handleEditVehicleFromVehiclePane() {
    	Vehicle tempVehicle = pojazdTab4.getSelectionModel().getSelectedItem();
    	handleEditVehicle(tempVehicle);
    	refreshVehiclesData();
    }
    
    private void handleEditVehicle(Vehicle tempVehicle) {
        boolean okClicked = false;
        if (tempVehicle != null)
        	okClicked = mainApp.showVehicleEditDialog(tempVehicle, true, false, depotsData);
        if (okClicked) {
        	Vehicle toRemove=null;
        	DBUtil.updateVehicleInDB(mainApp.getConn(), tempVehicle);
        	for (Depot depot : depotsData) {
				for (Vehicle veh : depot.getVehiclesData()) {
					if (veh.getIdZajezdni() != depot.getIdZajezdni())
						toRemove = veh;
				}
				if (toRemove!=null) depot.getVehiclesData().remove(toRemove);
			}
        	if (toRemove!=null) {
	        	for (Depot depot : depotsData) {
	        		if (depot.getIdZajezdni() == tempVehicle.getIdZajezdni())
	        			depot.getVehiclesData().add(tempVehicle);
	        	}
        	}
        }
        showStuffWhenVehicleInDepotClicked();
    }
    
    @FXML
    private void handleDeleteVehicleFromDepot() {
    	Vehicle selectedVehicle = vehiclesInDepotTab3a.getSelectionModel().getSelectedItem();
    	handleVehicleDriver(selectedVehicle);
    	showStuffWhenVehicleInDepotClicked();
    }
    
    @FXML
    private void handleDeleteVehicleFromVehiclePane() {
    	Vehicle selectedVehicle = pojazdTab4.getSelectionModel().getSelectedItem();
    	handleVehicleDriver(selectedVehicle);
    	showStuffWhenVehicleInDepotClicked();
    	refreshVehiclesData();
    }
    
    private void handleVehicleDriver(Vehicle selectedVehicle) {
        if (selectedVehicle != null) {
        	for (Depot dep : depotsData) {
        		if (dep.getIdZajezdni()==selectedVehicle.getIdZajezdni()) {
        			for (Driver dr : dep.getDriversData()) {
        				if (dr.getNrRejestracyjny()!=null 
        						&& dr.getNrRejestracyjny().equals(selectedVehicle.getNrRejestracyjny())) {
        					dr.setNrRejestracyjny(null);
        					DBUtil.updateDriverInDB(mainApp.getConn(), dr);
        				}
        			}
        			dep.getVehiclesData().remove(selectedVehicle);  
        		}
        	}
        	DBUtil.deleteVehicleFromDB(mainApp.getConn(), selectedVehicle.getNrRejestracyjny());
        } 
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////    
    // KIEROWCY 5 ////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////    
    
    @FXML private TableView<Driver> kierowcaTab5;
    @FXML private TableColumn<Driver, Number> idKierowcyCol5;
    @FXML private TableColumn<Driver, String> imieCol5;
    @FXML private TableColumn<Driver, String> nazwiskoCol5;
    
    @FXML private Label idKierowcyLabel5;
    @FXML private Label imieLabel5;
    @FXML private Label nazwiskoLabel5;
    @FXML private Label prawoJazdyLabel5;
    @FXML private Label idZajezdniLabel5; //// klucz obcy
    @FXML private Label nrRejestracyjnyLabel5; //// klucz obcy
    @FXML private Label numerLabel5; //numer linii  //// klucz obcy
    
    @FXML private TextField szukajTextField5;
    @FXML private CheckBox tramCheckBox5;
    @FXML private CheckBox busCheckBox5;
    private StringProperty busTramFilter5;
    
    @FXML private Tab driversTabPane5;

    @FXML
    public void refreshDriversData() {
    	driversData.clear();
    	for (Depot depot : depotsData) {
			for (Driver driver : depot.getDriversData()) {
				driversData.add(driver);
			}
		}
    }
    
    public void initDriversOverview() {
    	// Initialize the line table with the two columns.
    	idKierowcyCol5.setCellValueFactory(cellData -> cellData.getValue().idKierowcyProperty());
    	imieCol5.setCellValueFactory(cellData -> cellData.getValue().imieProperty());
    	nazwiskoCol5.setCellValueFactory(cellData -> cellData.getValue().nazwiskoProperty());

    	
    	// 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<Driver> filteredDriversData = new FilteredList<>(driversData, p -> true);
        
        // 2. Set the filter Predicate whenever the filter changes.
        szukajTextField5.textProperty().addListener((observable, oldValue, newValue) -> {
        	filteredDriversData.setPredicate(driver -> {
                // If filter text is empty, display all.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare
                String lowerCaseFilter = newValue.toLowerCase();
                
                if (driver.getImie().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches
                } else if (driver.getNazwisko().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches 
                } else if (Integer.toString( driver.getIdKierowcy() ).indexOf( lowerCaseFilter ) != -1) {
                	return true; // Filter matches
                }
                return false; // Does not match.
            });
        });
        
        FilteredList<Driver> filteredCheckedDriversData = new FilteredList<>(filteredDriversData, p -> true);
        
        busTramFilter5.addListener((observable, oldValue, newValue) -> {
        	filteredCheckedDriversData.setPredicate(driver -> {
                // If filter text is empty, display all.
                if (newValue == null || newValue.isEmpty()) { // ""
                    return true;
                }
                if (driver.getPrawoJazdy().indexOf( newValue ) != -1) { // A or T
                    return true; // Filter matches
                } 
                return false; // AT Does not match.
            });
        });
        
        // 3. Wrap the FilteredList in a SortedList. 
        SortedList<Driver> filteredCheckedSortedDriversData = new SortedList<>(filteredCheckedDriversData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        filteredCheckedSortedDriversData.comparatorProperty().bind(kierowcaTab5.comparatorProperty());
        
    	// Add observable list data to the table
        kierowcaTab5.setItems(filteredCheckedSortedDriversData);
        
        // Clear line details.
        showDriverDetails(null);

        // Listen for selection changes and show the person details when changed.
        kierowcaTab5.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showDriverDetails(newValue));
        
    }
    
    private void showDriverDetails(Driver driver) {
        if (driver != null) {
            // Fill the labels with info from the line object.
        	idKierowcyLabel5.textProperty().bind(Bindings.convert(driver.idKierowcyProperty()));
            imieLabel5.textProperty().bind(Bindings.convert(driver.imieProperty()));
            nazwiskoLabel5.textProperty().bind(Bindings.convert(driver.nazwiskoProperty()));
            prawoJazdyLabel5.textProperty().bind(Bindings.convert(driver.prawoJazdyProperty()));
            idZajezdniLabel5.setText("");          
        	nrRejestracyjnyLabel5.setText("");
            for (Depot dep : depotsData) {
            	if (dep.getIdZajezdni() == driver.getIdZajezdni()) {
            		idZajezdniLabel5.setText(dep.toString());
            		if (driver.getNrRejestracyjny()!=null) {
	            		for (Vehicle veh : dep.getVehiclesData()) {
	            			if (veh.getNrRejestracyjny().equals(driver.getNrRejestracyjny()))
	            				nrRejestracyjnyLabel5.setText(veh.toString());
	            		}
            		}
            	}
            }
            if (driver.getNumer()!=0) numerLabel5.textProperty().bind(Bindings.convert(driver.numerProperty()));
            else {
            	numerLabel5.textProperty().unbind();
            	numerLabel5.setText("");
            }
        } else {
            // line is null, remove all the text.
        	idKierowcyLabel5.textProperty().unbind();
        	idKierowcyLabel5.setText("");	
        	imieLabel5.textProperty().unbind();
        	imieLabel5.setText("");
        	nazwiskoLabel5.textProperty().unbind();
        	nazwiskoLabel5.setText("");
        	prawoJazdyLabel5.textProperty().unbind();
        	prawoJazdyLabel5.setText("");
        	//idZajezdniLabel5.textProperty().unbind();
        	idZajezdniLabel5.setText("");          
        	//nrRejestracyjnyLabel5.textProperty().unbind();
        	nrRejestracyjnyLabel5.setText("");
        	numerLabel5.textProperty().unbind();
        	numerLabel5.setText("");
        }
    }
    
    @FXML public void busCheckBox5Chenged() {
    	busTramFilter5.set(busTramCheckBoxChenged(tramCheckBox5, busCheckBox5));
    }
    
    @FXML public void tramCheckBox5Chenged() {
    	busTramFilter5.set(busTramCheckBoxChenged(tramCheckBox5, busCheckBox5));
    }
    
    @FXML
    private void handleNewDriverFromDepot() {
    	Driver tempDriver = new Driver();
    	Depot depot = zajezdniaTab3.getSelectionModel().getSelectedItem();
    	if (depot != null) {
    		tempDriver.setIdZajezdni(depot.getIdZajezdni());
    		tempDriver.setPrawoJazdy(depot.getTyp()+" ");
    		handleNewDriver(tempDriver, true);
    	} else 
    		handleNewDriver(tempDriver, false);
    }
    
    @FXML
    private void handleNewDriverFromDriverPane() {
    	Driver tempDriver = new Driver();
    	handleNewDriver(tempDriver, false);
    	refreshDriversData();
    }
    
    private void handleNewDriver(Driver tempDriver, boolean setDepot) {
        boolean okClicked = mainApp.showDriverEditDialog(tempDriver, false, setDepot, depotsData, linesData);
        if (okClicked) {
        	DBUtil.insertDriverToDB(mainApp.getConn(), tempDriver);
        	for (Depot depot : depotsData) {
				if (tempDriver.getIdZajezdni() == depot.getIdZajezdni()) {
					depot.getDriversData().add(tempDriver);
				}
			}
        }
        checkIfVehicleHasNumberWithoutDriver();
        showStuffWhenDriverInDepotClicked();
    }
    
    @FXML
    private void handleEditDriverFromDepot() {
    	Driver tempDriver = driversInDepotTab3.getSelectionModel().getSelectedItem();
    	handleEditDriver(tempDriver);
    }
    
    @FXML
    private void handleEditDriverFromDriverPane() {
    	Driver tempDriver = kierowcaTab5.getSelectionModel().getSelectedItem();
    	handleEditDriver(tempDriver);
    	refreshDriversData();
    }
    
    private void handleEditDriver(Driver tempDriver) {
    	boolean okClicked = false;
    	if (tempDriver != null)
    		okClicked = mainApp.showDriverEditDialog(tempDriver, true, false, depotsData, linesData);
        if (okClicked) {
        	DBUtil.updateDriverInDB(mainApp.getConn(), tempDriver);
        	Driver toRemove=null;
        	for (Depot depot : depotsData) {
				for (Driver drv : depot.getDriversData()) {
					if (drv.getIdZajezdni() != depot.getIdZajezdni())
						toRemove = drv;
				}
				if (toRemove!=null) depot.getDriversData().remove(toRemove);
			}
        	if (toRemove!=null) {
	        	for (Depot depot : depotsData) {
	        		if (depot.getIdZajezdni() == tempDriver.getIdZajezdni())
	        			depot.getDriversData().add(tempDriver);
	        	}
        	}
        }
        checkIfVehicleHasNumberWithoutDriver();
        showStuffWhenDriverInDepotClicked();
    }
    
    public void checkIfVehicleHasNumberWithoutDriver() {
    	for (Depot dep : depotsData) {
    		for (Vehicle veh : dep.getVehiclesData()) {
    			if (veh.getNumer()!=0) {
    				boolean hasNoDriver=true;
    				for (Driver dr : dep.getDriversData()) {
    					if (dr.getNrRejestracyjny()!=null 
    							&& dr.getNrRejestracyjny().equals(veh.getNrRejestracyjny())){
    						hasNoDriver=false;
    						veh.setNumer(dr.getNumer());
    						DBUtil.updateVehiclesOnLinesInDB(mainApp.getConn(), veh);
    					}
    				}
    				if (hasNoDriver) {
    					veh.setNumer(0);
    					DBUtil.updateVehiclesOnLinesInDB(mainApp.getConn(), veh);
    				}
    			}
    		}
    	}
    }
    
    @FXML
    private void handleDeleteDriverFromDepot() {
    	Driver selectedDriver = driversInDepotTab3.getSelectionModel().getSelectedItem();
    	handleDeleteDriver(selectedDriver);
    	showStuffWhenDriverInDepotClicked();
    }
    
    @FXML
    private void handleDeleteDriverFromDriverPane() {
    	Driver selectedDriver = kierowcaTab5.getSelectionModel().getSelectedItem();
    	handleDeleteDriver(selectedDriver);
    	showStuffWhenDriverInDepotClicked();
    	refreshDriversData();
    }
    
    private void handleDeleteDriver(Driver selectedDriver) {
        if (selectedDriver != null) {
        	for (Depot dep : depotsData) {
        		if (dep.getIdZajezdni()==selectedDriver.getIdZajezdni()) {
        			dep.getDriversData().remove(selectedDriver);  
        		}
        	}
        	DBUtil.deleteDriverFromDB(mainApp.getConn(), selectedDriver.getIdKierowcy());
        	checkIfVehicleHasNumberWithoutDriver();
        } 
    }

	////////////////////////////////////////////////////////////////////////////////////////////////
	// AZERSTAF ///////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////

	public String busTramCheckBoxChenged(CheckBox tram, CheckBox bus) {
		if (tram.isSelected() && bus.isSelected()) {
			return "";
		}
		if (!tram.isSelected() && bus.isSelected()) {
			return "A";
		}
		if (tram.isSelected() && !bus.isSelected()) {
			return "T";
		}
		if (!tram.isSelected() && !bus.isSelected()) {
			return "AT";
		}
		return "";
	}
	
}