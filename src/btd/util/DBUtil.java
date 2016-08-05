package btd.util;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import btd.model.Depot;
import btd.model.Line;
import btd.model.Route;
import btd.model.Stop;
import btd.model.Driver;
import btd.model.Vehicle;

public class DBUtil {

	//private Connection conn;
	private static Statement stmt;
	private static PreparedStatement pstmt;
	private static CallableStatement cstmt;
	private static ResultSet rs;
	
	//public static 
	public static int getNextID(Connection conn) {
		int id=-1;
		String statement = "select ID_SEQ.nextval from dual";
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
	    	rs = stmt.executeQuery(statement);
	    	rs.first();
	    	id = rs.getInt(1);
		} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeResultSet(rs);
    		closeStatement(stmt);
    	} 
		return id;
	}
	
	public static int getNextNumerBoczny(Connection conn) {
		int id=-1;
		String statement = "select NBOCZNY_SEQ.nextval from dual";
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
	    	rs = stmt.executeQuery(statement);
	    	rs.first();
	    	id = rs.getInt(1);
		} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeResultSet(rs);
    		closeStatement(stmt);
    	} 
		return id;
	}
	
	/**
	 * Check if line number is unique
	 * @param conn
	 * @param numer
	 * @return true if this number exists in DB, false - number can be inserted
	 */
	public static boolean checkNumer(Connection conn, int numer) {
		boolean result=true;
		String statement = "select * from linie where numer = "+numer;
		try {
	    	stmt = conn.createStatement();
	    	rs = stmt.executeQuery(statement);
	    	if (rs.next()) result=true; else result=false;
    	} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeResultSet(rs);
    		closeStatement(stmt);
    	}
		return result;
	}
	
	
	
	public static void insertLineToDB(Connection conn, Line line) {
		String statement = "Insert into LINIE (NUMER,TYP,CZESTOTLIWOSC,PIERWSZY_KURS,OSTATNI_KURS) "
				+ "values (" + line.getNumer() + ", '" + line.getTyp() + "', " + line.getCzestotliwosc() 
				+ ",to_date('" + DateTimeUtil.format(line.getPierwszyKurs()) + "','YYYY-MM-DD HH24:MI:SS'), "
				+ "to_date('" + DateTimeUtil.format(line.getOstatniKurs()) + "','YYYY-MM-DD HH24:MI:SS'))";
		try {
	    	stmt = conn.createStatement();
	    	int changes;
	    	changes = stmt.executeUpdate(statement);
	    	if (changes==0) System.out.println("nie dodano zadnego rekordu");
    	} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeResultSet(rs);
    		closeStatement(stmt);
    	} 
	}
	
	public static void insertRouteToDB(Connection conn, int numer, ObservableList<Route> routeData) {
		String statement = "INSERT INTO TRASY (LP, LINIE_NUMER, PRZYSTANKI_ID_PRZYSTANKU) VALUES (?,?,?)";
		try {
	    	pstmt = conn.prepareStatement(statement);
	    	for (Route route : routeData) {
	    		pstmt.setInt(1, route.getLp());
		    	pstmt.setInt(2, numer);
		    	pstmt.setInt(3, route.getIdPrzystanku());
		    	pstmt.addBatch();
			} 	
	    	pstmt.executeBatch();
    	} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeStatement(pstmt);
    	} 
	}
	
	public static void insertStopToDB(Connection conn, Stop stop) {
		String statement = "insert into przystanki(ID_PRZYSTANKU, NAZWA, X, Y) values "
				+ "(" + stop.getIdPrzystanku() + ", " + "'" + stop.getNazwa() + "', " 
				+ stop.getX() + ", " + stop.getY() + ")";
		try {
	    	stmt = conn.createStatement();
	    	int changes;
	    	changes = stmt.executeUpdate(statement);
	    	if (changes==0) System.out.println("nie dodano zadnego rekordu");
    	} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeResultSet(rs);
    		closeStatement(stmt);
    	} 
	}
	
	public static void insertDepotToDB(Connection conn, Depot depot) {
		String statement = "INSERT INTO ZAJEZDNIE (ID_ZAJEZDNI, ADRES, TYP) VALUES (?, ?, ?)";
		try {
			pstmt = conn.prepareStatement(statement);
    		pstmt.setInt(1, depot.getIdZajezdni());
	    	pstmt.setString(2, depot.getAdres());
	    	pstmt.setString(3, depot.getTyp());
	    	pstmt.addBatch();
	    	pstmt.executeBatch();
    	} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeStatement(pstmt);
    	} 
	}
	
	public static void insertVehicleToDB(Connection conn, Vehicle vehicle) {
		String statement = "INSERT INTO POJAZDY (NR_REJESTRACYJNY, TYP, MARKA, MODEL, "
				+ "NUMER_BOCZNY, ROCZNIK, LICZBA_SIEDZEN, MAX_LICZBA_PASAZEROW, MA_BILETOMAT, "
				+ "MA_KLIMATYZACJE, SPALANIE, ZUZYCIE_PRADU, ZAJEZDNIE_ID_ZAJEZDNI, CZY_NISKOPODLOGOWY, LINIE_NUMER) "
				+ "VALUES (?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?)";
		try {
	    	pstmt = conn.prepareStatement(statement);
    		pstmt.setString(1, vehicle.getNrRejestracyjny());
	    	pstmt.setString(2, vehicle.getTyp());
	    	pstmt.setString(3, vehicle.getMarka());
	    	pstmt.setString(4, vehicle.getModel());
	    	pstmt.setInt(5, vehicle.getNrBoczny());
	    	pstmt.setInt(6, vehicle.getRocznik());
	    	pstmt.setInt(7, vehicle.getLiczbaSiedzen());
	    	pstmt.setInt(8, vehicle.getMaxLiczbaPasazerow());
	    	pstmt.setString(9, vehicle.getBiletomat());
	    	pstmt.setString(10, vehicle.getKlimatyzacja());
	    	if (vehicle.getTyp().equals("A")) {
	    		pstmt.setDouble(11, vehicle.getSpalanie());
	    		pstmt.setNull(12, Types.DOUBLE);
	    	} else {
	    		pstmt.setNull(11, Types.DOUBLE);
	    		pstmt.setDouble(12, vehicle.getZuzyciePradu());
	    	}
	    	pstmt.setInt(13, vehicle.getIdZajezdni());
	    	pstmt.setString(14, vehicle.getNiskopodlogowy());
	    	if (vehicle.getNumer()!=0) pstmt.setInt(15, vehicle.getNumer()); 
	    	else pstmt.setNull(15, Types.INTEGER); 
		    pstmt.executeUpdate();
    	} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeStatement(pstmt);
    	} 
	}
	
	public static void insertDriverToDB(Connection conn, Driver driver) {
		String statement = "INSERT INTO KIEROWCY (ID_KIEROWCY, IMIE, NAZWISKO, PRAWO_JAZDY, ZAJEZDNIE_ID_ZAJEZDNI, "
				+ "POJAZDY_NR_REJESTRACYJNY, LINIE_NUMER) VALUES (?,?,?,?,?,?,?)";
		try {
			pstmt = conn.prepareStatement(statement);
    		pstmt.setInt(1, driver.getIdKierowcy());
	    	pstmt.setString(2, driver.getImie());
	    	pstmt.setString(3, driver.getNazwisko());
	    	pstmt.setString(4, driver.getPrawoJazdy());
	    	pstmt.setInt(5, driver.getIdZajezdni());
	    	if (driver.getNrRejestracyjny() != null) pstmt.setString(6, driver.getNrRejestracyjny());
	    	else pstmt.setNull(6, Types.VARCHAR); 
	    	if (driver.getNumer() != 0) pstmt.setInt(7, driver.getNumer());
	    	else pstmt.setNull(7, Types.INTEGER); 
	    	pstmt.executeUpdate();
    	} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeStatement(pstmt);
    	} 
	}
	
	
	
	public static void updateDriversOnLinesInDB(Connection conn, int numer) {
		String statement = "update kierowcy set LINIE_NUMER = null where LINIE_NUMER = ?";
		try {
			pstmt = conn.prepareStatement(statement);
    		pstmt.setInt(1, numer);
	    	pstmt.addBatch();
	    	pstmt.executeBatch();
    	} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeStatement(pstmt);
    	} 
	}
	
	public static void updateDriverInDB(Connection conn, Driver driver) {
		String statement = "UPDATE KIEROWCY SET IMIE = ?, NAZWISKO = ?, PRAWO_JAZDY = ?, "
				+ "ZAJEZDNIE_ID_ZAJEZDNI = ?, POJAZDY_NR_REJESTRACYJNY = ?, LINIE_NUMER = ? WHERE ID_KIEROWCY = ?";
		try {
	    	pstmt = conn.prepareStatement(statement);
	    	pstmt.setString(1, driver.getImie());
	    	pstmt.setString(2, driver.getNazwisko());
	    	pstmt.setString(3, driver.getPrawoJazdy());
	    	pstmt.setInt(4, driver.getIdZajezdni());
	    	if (driver.getNrRejestracyjny() != null) 
	    		pstmt.setString(5, driver.getNrRejestracyjny());
	    	else
	    		pstmt.setNull(5, Types.VARCHAR); 
	    	if (driver.getNumer() != 0)
	    		pstmt.setInt(6, driver.getNumer());
	    	else 
	    		pstmt.setNull(6, Types.INTEGER); 
	    	pstmt.setInt(7, driver.getIdKierowcy());
		    pstmt.executeUpdate();
    	} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeStatement(pstmt);
    	} 
	}
	
	public static void updateCzasPrzejazdu(Connection conn, Line line) {
		String statement = "update linie set CZAS_PRZEJAZDU = " + line.getCzasPrzejazdu() + " where NUMER = " + line.getNumer();
		try {
	    	stmt = conn.createStatement();
	    	int changes;
	    	changes = stmt.executeUpdate(statement);
	    	if (changes==0) System.out.println("nie dodano zadnego rekordu");
    	} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeResultSet(rs);
    		closeStatement(stmt);
    	} 
	}
	
	public static void updateLineInDB(Connection conn, Line line) {
		String statement = "UPDATE LINIE SET NUMER = ?, TYP = ?, CZESTOTLIWOSC = ?, "
				+ "PIERWSZY_KURS = TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS'), "
				+ "OSTATNI_KURS = TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS') WHERE NUMER = ?";
		try {
	    	pstmt = conn.prepareStatement(statement);
	    	pstmt.setInt(1, line.getNumer());
	    	pstmt.setString(2, line.getTyp());
	    	pstmt.setInt(3, line.getCzestotliwosc());
	    	pstmt.setString(4, DateTimeUtil.format(line.getPierwszyKurs()));
	    	pstmt.setString(5, DateTimeUtil.format(line.getOstatniKurs()));
	    	pstmt.setInt(6, line.getNumer());
	    	pstmt.executeUpdate();
    	} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeStatement(pstmt);
    	} 
	}
	
	public static void updateStopInDB(Connection conn, Stop stop) {
		String statement = "UPDATE PRZYSTANKI SET NAZWA = '"+ stop.getNazwa() 
				+ "', X = " + stop.getX() + ", Y = " + stop.getY() + " WHERE ID_PRZYSTANKU = " 
				+ stop.getIdPrzystanku();
		try {
	    	stmt = conn.createStatement();
	    	int changes;
	    	changes = stmt.executeUpdate(statement);
	    	if (changes==0) System.out.println("nie dodano zadnego rekordu");
    	} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeStatement(stmt);
    	} 
	}
	
	public static void updateVehiclesOnLinesInDB(Connection conn, Vehicle vehicle) {
		String statement = "update pojazdy set LINIE_NUMER = ? where NR_REJESTRACYJNY = ?";
		try {
			pstmt = conn.prepareStatement(statement);
    		if (vehicle.getNumer() != 0) pstmt.setInt(1, vehicle.getNumer());
    		else pstmt.setNull(1, Types.INTEGER);
    		pstmt.setString(2, vehicle.getNrRejestracyjny());
	    	pstmt.executeUpdate();
    	} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeStatement(pstmt);
    	} 
	}
	
	public static void updateDepotInDB(Connection conn, Depot depot) {
		String statement = "update ZAJEZDNIE set ADRES = ?, TYP = ? where ID_ZAJEZDNI = ?";
		try {
			pstmt = conn.prepareStatement(statement);
    		pstmt.setInt(3, depot.getIdZajezdni());
	    	pstmt.setString(1, depot.getAdres());
	    	pstmt.setString(2, depot.getTyp());
	    	pstmt.addBatch();
	    	pstmt.executeBatch();
    	} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeStatement(pstmt);
    	} 
	}
	
	public static void updateVehicleInDB(Connection conn, Vehicle vehicle) {
		String statement = "UPDATE POJAZDY SET TYP = ?, MARKA = ?, MODEL = ?, "
				+ "ROCZNIK = ?, LICZBA_SIEDZEN = ?, MAX_LICZBA_PASAZEROW = ?, MA_BILETOMAT = ?, "
				+ "MA_KLIMATYZACJE = ?, SPALANIE = ?, ZUZYCIE_PRADU = ?, ZAJEZDNIE_ID_ZAJEZDNI = ?, LINIE_NUMER = ?, "
				+ "CZY_NISKOPODLOGOWY = ? WHERE NR_REJESTRACYJNY = ?";
		try {
	    	pstmt = conn.prepareStatement(statement);
	    	pstmt.setString(1, vehicle.getTyp());
	    	pstmt.setString(2, vehicle.getMarka());
	    	pstmt.setString(3, vehicle.getModel());
	    	pstmt.setInt(4, vehicle.getRocznik());
	    	pstmt.setInt(5, vehicle.getLiczbaSiedzen());
	    	pstmt.setInt(6, vehicle.getMaxLiczbaPasazerow());
	    	pstmt.setString(7, vehicle.getBiletomat());
	    	pstmt.setString(8, vehicle.getKlimatyzacja());
	    	if (vehicle.getTyp().equals("A")) {
	    		pstmt.setDouble(9, vehicle.getSpalanie());
	    		pstmt.setNull(10, Types.DOUBLE);
	    	} else {
	    		pstmt.setNull(9, Types.DOUBLE);
	    		pstmt.setDouble(10, vehicle.getZuzyciePradu());
	    	}
	    	pstmt.setInt(11, vehicle.getIdZajezdni());
	    	if (vehicle.getNumer()!=0) pstmt.setInt(12, vehicle.getNumer()); 
	    	else pstmt.setNull(12, Types.INTEGER); 
	    	pstmt.setString(13, vehicle.getNiskopodlogowy());
	    	pstmt.setString(14, vehicle.getNrRejestracyjny());
	    	pstmt.executeUpdate();
    	} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeStatement(pstmt);
    	} 
	}
	

	
	public static void deleteLineFromDB(Connection conn, int numer) {
		String statement = "delete from linie where numer = " + numer;
		deleteFromDB(conn, statement);
	}
	
	public static void deleteRouteFromDB(Connection conn, int numer) {
		String statement = "delete from trasy where linie_numer=" + numer;
		deleteFromDB(conn, statement);
	}
	
	public static void deleteStopFromDB(Connection conn, int numer) {
		String statement = "delete from przystanki where ID_PRZYSTANKU=" + numer;
		deleteFromDB(conn, statement);
	}
	
	public static void deleteDepotFromDB(Connection conn, int numer) {
		String statement = "delete from zajezdnie where ID_ZAJEZDNI=" + numer;
		deleteFromDB(conn, statement);
	}
	
	public static void deleteDriverFromDB(Connection conn, int numer) {
		String statement = "delete from kierowcy where ID_KIEROWCY=" + numer;
		deleteFromDB(conn, statement);
	}
	
	public static void deleteVehicleFromDB(Connection conn, String numer) {
		String statement = "delete from pojazdy where NR_REJESTRACYJNY='" + numer +"'";
		deleteFromDB(conn, statement);
	}
	
	public static void deleteFromDB(Connection conn, String statement) {
		try {
	    	stmt = conn.createStatement();
	    	stmt.executeUpdate(statement);
	    	//if (changes==0) System.out.println("nie usunieto zadnego rekordu " + statement);
    	} catch (SQLException ex) {
    		System.out.println("blad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeStatement(stmt);
    	}
	}
	
	
	
	public static int callWyliczLiczbePojazdow(Connection conn, int numer) {
		String statement = "{call WyliczLiczbePojazdow(?)}";//numer linii
		try {
			cstmt = conn.prepareCall(statement); 
			cstmt.setInt(1, numer);
			cstmt.execute();
    	} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeStatement(cstmt);
    	} 
		
		statement = "select LICZBA_POJAZDOW from linie where numer = "+numer;
		int result=0;
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
	    	rs = stmt.executeQuery(statement);
	    	rs.first();
	    	result = rs.getInt(1);
    	} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeResultSet(rs);
    		closeStatement(stmt);
    	}
		return result;
	}
	
	public static String callLosowyNumerRejestracyjny(Connection conn, String woj) {
		String statement = "{ ? = call losowyNumerRejestracyjny(?) }";//wojewodztwo 1-3 znaki
		String nrrej="";
		try {
			cstmt = conn.prepareCall(statement); 
			cstmt.registerOutParameter(1, Types.VARCHAR);
			cstmt.setString(2, woj);
			cstmt.execute();
			nrrej = cstmt.getString(1);
    	} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeStatement(cstmt);
    	} 
		//System.out.println(nrrej);
		return nrrej;
	}
	
	public static void callLosowaLiczbaPasazerow(Connection conn, int numer) {
		String statement = "{call LosowaLiczbaPasazerow(?,?)}";//numer linii, max l. pas
		try {
			cstmt = conn.prepareCall(statement); 
			cstmt.setInt(1, numer);
			cstmt.setInt(2, 100); //max liczba pasazerow
			cstmt.execute();
    	} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeStatement(cstmt);
    	} 
	}
	
	public static double callWyliczDlugoscTrasy(Connection conn, int numer) {
		String statement = "{call WyliczDlugoscTrasy(?)}";//numer linii
		try {
			cstmt = conn.prepareCall(statement); 
			cstmt.setInt(1, numer);
			cstmt.execute();
    	} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeStatement(cstmt);
    	} 
		
		statement = "select DLUGOSC from linie where numer = "+numer;
		double result=0.0;
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
	    	rs = stmt.executeQuery(statement);
	    	rs.first();
	    	result = (double)Math.round(rs.getFloat(1)*1000)/1000;
    	} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeResultSet(rs);
    		closeStatement(stmt);
    	}
		return result;
	}
	
	
	
	public static void loadLinesFromDB(Connection conn, ObservableList<Line> lineList) {
		String statement = "select * from linie";
		try {
	    	stmt = conn.createStatement();
	    	rs = stmt.executeQuery(statement);
	    	while (rs.next()) {
	    		lineList.add(new Line(rs.getInt(1), rs.getString(2), rs.getInt(3),
	    				(double)Math.round(rs.getFloat(4)*1000)/1000, rs.getInt(5), rs.getInt(6), 
	    				DateTimeUtil.parse(rs.getString(7)), DateTimeUtil.parse(rs.getString(8))
	    				));	
	    	}
    	} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeResultSet(rs);
    		closeStatement(stmt);
    	}
	}
	
	public static void loadDepotsFromDB(Connection conn, ObservableList<Depot> depotList) {
		String statement = "select * from zajezdnie";
		try {
	    	stmt = conn.createStatement();
	    	rs = stmt.executeQuery(statement);
	    	while (rs.next()) {
	    		depotList.add(new Depot(rs.getInt(1), rs.getString(2), rs.getString(3)));	
	    	}
    	} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeResultSet(rs);
    		closeStatement(stmt);
    	}
	}
	
	public static void loadDriversFromDB(Connection conn, ObservableList<Depot> depotList) {
		String statement = "select * from kierowcy where zajezdnie_id_zajezdni = ? ";
		try {
			pstmt = conn.prepareStatement(statement);
			for (Depot depot : depotList) {
				pstmt.setInt(1, depot.getIdZajezdni());
		    	rs = pstmt.executeQuery();
		    	while (rs.next()) {
		    		depot.getDriversData().add(new Driver(
		    				rs.getInt(1), rs.getString(2), rs.getString(3),
		    				rs.getString(4), rs.getInt(5), rs.getString(6), rs.getInt(7)));	
		    	}
		    	rs.close();
			}
		} catch (SQLException ex) {
			System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
		} finally {
			closeResultSet(rs);
			closeStatement(pstmt);
		}
	}
	
	public static void loadVehiclesFromDB(Connection conn, ObservableList<Depot> depotList) {
		String statement = "select * from pojazdy where zajezdnie_id_zajezdni=?";
		try {
			pstmt = conn.prepareStatement(statement);
			for (Depot depot : depotList) {
				pstmt.setInt(1, depot.getIdZajezdni());
		    	rs = pstmt.executeQuery();
		    	while (rs.next()) {
		    		depot.getVehiclesData().add(new Vehicle(
		    				rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), 
		    				rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8),
		    				rs.getString(9), rs.getString(10), (double)Math.round(rs.getFloat(11)*1000)/1000, 
		    				(double)Math.round(rs.getFloat(12)*1000)/1000, rs.getString(16), rs.getInt(13), rs.getInt(15)));	
		    	}
		    	rs.close();
			}
		} catch (SQLException ex) {
			System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
		} finally {
			closeResultSet(rs);
			closeStatement(pstmt);
		}
	}
	
	public static void loadStopsFromDB(Connection conn, ObservableList<Stop> stopList) {
		String statement = "select * from przystanki";
		try {
	    	stmt = conn.createStatement();
	    	rs = stmt.executeQuery(statement);
	    	while (rs.next()) {
	    		stopList.add(new Stop(rs.getInt(1), rs.getString(2), rs.getFloat(3),
	    				rs.getFloat(4)));	
	    	}
    	} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeResultSet(rs);
    		closeStatement(stmt);
    	}
	}
	
	public static void loadRoutesFromDB(Connection conn, ObservableList<Line> lineList) {
		String statement = "select t.lp, t.LICZBA_WSIADAJACYCH, "
						+ "t.LICZBA_WYSIADAJACYCH, t.LINIE_NUMER, "
						+ "t.PRZYSTANKI_ID_PRZYSTANKU, p.NAZWA, (select typ from linie "
						+ "where numer = ? ) as TYP from trasy "
						+ "t join przystanki p on p.ID_PRZYSTANKU=t.PRZYSTANKI_ID_PRZYSTANKU "
						+ "where t.LINIE_NUMER = ? order by t.LP asc";
		try {
			pstmt = conn.prepareStatement(statement);
	    	for (Line line : lineList) {
	    		pstmt.setInt(1, line.getNumer());
	    		pstmt.setInt(2, line.getNumer());
		    	rs = pstmt.executeQuery();
		    	while (rs.next()) {
		    		line.getRouteData().add(new Route(rs.getInt(1), rs.getInt(2), rs.getInt(3),
		    				rs.getInt(4), rs.getInt(5), rs.getString(6), rs.getString(7)));	
		    	}
		    	rs.close();
	    	}
    	} catch (SQLException ex) {
    		System.out.println("B³ad wykonania polecenia " + statement + " " + ex.toString());
    	} finally {
    		closeResultSet(rs);
    		closeStatement(pstmt);
    	}
	}
	
	
	
	public static void closeResultSet(ResultSet rs) {
    	if (rs != null) {
	    	try {
	    		rs.close();
	    	} catch (SQLException e) { 
	    		Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, "blad zamykania ResultSet", e);
	    	}
    	}
	}
	
    public static void closeStatement(Statement stmt) {
    	if (stmt != null) {
	    	try {
	    		stmt.close();
	    	} catch (SQLException e) { 
	    		Logger.getLogger(DBUtil.class.getName()).log(Level.SEVERE, "blad zamykania Statement", e); 
	    	}
    	}
    }
}
