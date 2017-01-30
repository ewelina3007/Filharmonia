package main;

import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;

import java.sql.*;

public class Db_connection {

	private static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
	private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";

	private static String USER;
	private static String PASS;

	public static Connection conn;
	public static Statement stmt;
	ObservableList<Employee> worker = FXCollections.observableArrayList();

	public Db_connection() {

		getDBUserFromFile();
		try {
			// STEP 2: Register JDBC driver
			Class.forName(JDBC_DRIVER);

			// STEP 3: Open a connection
			System.out.println("��czenie z baz� danych...");

			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Po��czono");

		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (ClassNotFoundException ee) {
			ee.printStackTrace();
		}

	}

	public void getDBUserFromFile() {
		try {
			BufferedReader in = new BufferedReader(new FileReader(
					"C:\\Users\\Patryk Milczarek\\git\\Projekt-Filharmonia\\wbd_projekt\\src\\main\\dbinfo.txt"));

			setUSER(in.readLine());
			setPASS(in.readLine());

			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static HashMap<String, String> getCurrentUserInfo() {

		HashMap<String, String> map = new HashMap<String, String>();

		PreparedStatement preparedStatement;

		String query = "SELECT * from Pracownicy where id_pracownika=?";

		try {
			preparedStatement = conn.prepareStatement(query);

			preparedStatement.setString(1, CurrentUser.id_current_user + "");

			ResultSet resultSet = preparedStatement.executeQuery();

			resultSet.next();

			map.put("imie", resultSet.getString("imie"));
			map.put("nazwisko", resultSet.getString("nazwisko"));
			map.put("ulica", resultSet.getString("ulica"));
			map.put("miasto", resultSet.getString("miasto"));
			map.put("nr_budynku", resultSet.getString("nr_budynku"));
			map.put("nr_mieszkania", resultSet.getString("nr_mieszkania"));
			map.put("nr_telefonu", resultSet.getString("nr_telefonu"));
			map.put("e_mail", resultSet.getString("e_mail"));
			System.out.println("lol");
			resultSet.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}

	public static HashMap<Integer, String> getEmployeeSalaryDB() {

		HashMap<Integer, String> map = new HashMap<Integer, String>();

		PreparedStatement preparedStatement;

		String query = "SELECT * from Wynagrodzenie where id_pracownika=?";

		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, CurrentUser.id_current_user + "");

			ResultSet resultSet = preparedStatement.executeQuery();

			int i = 1;
			while (resultSet.next()) {

				map.put(i++, resultSet.getString("pensja"));
				map.put(i++, resultSet.getString("data_wyplaty"));

			}

			resultSet.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;

	}

	public static HashMap<Integer, String> getSymphonyDBlist() {

		HashMap<Integer, String> map = new HashMap<Integer, String>();

		PreparedStatement preparedStatement;

		String query = "select nazwa from Filharmonie ";

		try {
			preparedStatement = conn.prepareStatement(query);
			// preparedStatement.setString(1, CurrentUser.id_current_user+"");

			ResultSet resultSet = preparedStatement.executeQuery();

			int i = 0;
			while (resultSet.next()) {

				map.put(i++, resultSet.getString("nazwa"));

			}
			resultSet.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;

	}

	public static HashMap<String, String> findSymphony(String name) {

		HashMap<String, String> map = new HashMap<String, String>();

		PreparedStatement preparedStatement;

		String query = "select nazwa, filharmonie.ulica, filharmonie.nr_budynku, filharmonie.miasto, filharmonie.nr_telefonu, wlasciciele.imie, wlasciciele.nazwisko from Filharmonie  join Wlasciciele_Filharmonie using(id_filharmonii) join Wlasciciele using(id_wlasciciela) where filharmonie.nazwa=?";

		try {

			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, name);

			ResultSet resultSet = preparedStatement.executeQuery();

			resultSet.next();

			map.put("nazwa", resultSet.getString("nazwa"));
			map.put("ulica", resultSet.getString("ulica"));
			map.put("nr_budynku", resultSet.getString("nr_budynku"));
			map.put("miasto", resultSet.getString("miasto"));
			map.put("nr_telefonu", resultSet.getString("nr_telefonu"));
			map.put("wlasciciele", resultSet.getString("imie") + " " + resultSet.getString("nazwisko"));

			resultSet.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;

	}

	public void closeConnection() {
		try {
			conn.close();
			System.out.println("Zako�czono po��czenie");
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	public static boolean verifyLogin(String login, String pass) {

		PreparedStatement preparedStatement;

		String query = "SELECT * from Konta where login=? and haslo=?";
		boolean check = false;
		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, login);
			preparedStatement.setString(2, pass);
			ResultSet resultSet = preparedStatement.executeQuery();

			check = resultSet.next();
			resultSet.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return check;
	}

	public static String checkUserType(String login, String pass) {
		PreparedStatement preparedStatement;

		String query = "SELECT typ_konta from Konta where login=? and haslo=?";
		String type = "unknown";
		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, login);
			preparedStatement.setString(2, pass);
			ResultSet resultSet = preparedStatement.executeQuery();

			resultSet.next();

			type = resultSet.getString(1);
			resultSet.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return type;

	}

	public static long getUserId(String login, String pass) {
		long id = 0;

		PreparedStatement preparedStatement;

		String query = "SELECT id_pracownika from Konta where login=? and haslo=?";

		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, login);
			preparedStatement.setString(2, pass);
			ResultSet resultSet = preparedStatement.executeQuery();

			resultSet.next();

			id = resultSet.getLong(1);
			resultSet.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return id;
	}

	public static void add_symphony_db(String symph_name, String symph_address, String symph_num_house,
			String symph_town, String symph_tel_num, String symph_owner) {
		PreparedStatement preparedStatement;

		String query = "INSERT INTO Filharmonie values(?,?,?,?,?,?,?)";

		try {
			preparedStatement = conn.prepareStatement(query);
			preparedStatement.setString(1, symph_name);
			preparedStatement.setString(2, symph_town);
			preparedStatement.setString(3, "xxx");
			preparedStatement.setString(4, symph_address);
			preparedStatement.setString(5, symph_num_house);
			preparedStatement.setString(6, symph_tel_num);
			preparedStatement.setString(7, "yyy");
			preparedStatement.executeQuery();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void modify_symph(String symph_name, String symph_address, String symph_num_house, String symph_town,
			String symph_tel_num, String symph_owner, String nazw, String nazw_symph) {

		PreparedStatement preparedStatement, preparedStatement2;

		String query = "UPDATE Filharmonie SET nazwa=?, miasto=?, ulica=?, nr_budynku=?, nr_telefonu=? where nazwa=?";
		String query2 = "UPDATE Wlasciciele SET imie=?, nazwisko=? where nazwisko=?";
		try {

			conn.setAutoCommit(false);
			preparedStatement = conn.prepareStatement(query);
			preparedStatement2 = conn.prepareStatement(query2);
			preparedStatement.setString(1, symph_name);
			preparedStatement.setString(2, symph_town);
			preparedStatement.setString(3, symph_address);
			preparedStatement.setString(4, symph_num_house);
			preparedStatement.setString(5, symph_tel_num);
			preparedStatement.setString(6, nazw_symph);

			String[] parts = symph_owner.split(" ");
			String symph_name_owner = parts[0];
			String symph_surname_owner = parts[1];

			preparedStatement2.setString(1, symph_name_owner);
			preparedStatement2.setString(2, symph_surname_owner);
			preparedStatement2.setString(3, nazw);
			preparedStatement.executeUpdate();

			preparedStatement2.executeUpdate();

			conn.commit();

			conn.setAutoCommit(true);
			preparedStatement.close();
			preparedStatement2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void setUSER(String uSER) {
		USER = uSER;
	}

	public static void setPASS(String pASS) {
		PASS = pASS;
	}

	public static ObservableList<Employee> getEmployeeInfo() {

		ObservableList<Employee> worker = FXCollections.observableArrayList();

		try {
			System.out.println("Creating statement...");
			stmt = conn.createStatement();

			String sql = "SELECT imie, nazwisko,pracownicy.ulica,"
					+ " pracownicy.nr_budynku, pracownicy.miasto, pesel, nazwa_stanowiska, nazwa "
					+ "FROM Pracownicy join stanowiska using(id_stanowiska) "
					+ "join filharmonie using(id_filharmonii) ";
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {

				String name_worker = rs.getString("imie");

				String surname_worker = rs.getString("nazwisko");
				String address_worker = rs.getString("ulica");

				String house_num_worker = rs.getString("nr_budynku");
				String town_worker = rs.getString("miasto");

				String pesel_worker = rs.getString("pesel");
				String profession_worker = rs.getString("nazwa_stanowiska");
				String symphony_worker = rs.getString("nazwa");

				worker.add(new Employee(name_worker, surname_worker, address_worker, house_num_worker, town_worker,
						pesel_worker, profession_worker, symphony_worker));

			}

			rs.close();
			stmt.close();

		} catch (SQLException se) {
			se.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
				se2.printStackTrace();
			}

		}

		return worker;
	}

	public static ObservableList<Event> getEventsInfo() {

		ObservableList<Event> event_list = FXCollections.observableArrayList();

		String query = "select * from lista_wystepow join wystepy using(id_wystepu) join filharmonie using(id_filharmonii) ";

		try {

			stmt = conn.createStatement();

			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {

				long id = rs.getLong("id_wydarzenia");

				String name = rs.getString("nazwa");

				String starthour = rs.getString("godz_rozpoczecia");

				String time = rs.getString("czas_trwania");
				Date date = rs.getDate("data");
				int max_seats_number = rs.getInt("max_liczba_miejsc");
				String symphony = rs.getString(10);

				event_list.add(new Event(id, name, starthour, date, time, max_seats_number, symphony));

			}

			rs.close();
			stmt.close();

		} catch (SQLException se) {
			se.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
				se2.printStackTrace();
			}

		}

		return event_list;
	}

	public static ObservableList<Employee> getEmployeeInfoByAttribute(String attribute,String option){

        ObservableList<Employee> worker = FXCollections.observableArrayList();



        try{
          
            stmt = conn.createStatement();

            String sql="";
      
            
            switch(option) {
          
                case "Nazwisko":
        		
        		
                	
                    sql="SELECT imie, nazwisko,pracownicy.ulica, pracownicy.nr_budynku,"
            		+ " pracownicy.miasto, pesel, nazwa_stanowiska, nazwa "
            		+ " FROM Pracownicy join stanowiska using(id_stanowiska) join filharmonie using(id_filharmonii) ON upper(nazwisko) LIKE UPPER('%" + attribute.toUpperCase() + "%')";
                    
                    System.out.println(sql);
                    break;
                case "Filharmonia":
                	 sql="SELECT imie, nazwisko,pracownicy.ulica, pracownicy.nr_budynku,"
                     		+ " pracownicy.miasto, pesel, nazwa_stanowiska, nazwa "
                     		+ " FROM Pracownicy where join stanowiska using(id_stanowiska) on upper(nazwa) LIKE " + "UPPER('%" + attribute.toUpperCase() + "%')"
                     		+ " join filharmonie using(id_filharmonii)";
                    break;
                case "Miasto":
                	 sql="SELECT imie, nazwisko,pracownicy.ulica, pracownicy.nr_budynku,"
                      		+ " pracownicy.miasto, pesel, nazwa_stanowiska, nazwa "
                      		+ "FROM Pracownicy join stanowiska using(id_stanowiska) join filharmonie using(id_filharmonii) ON upper(nazwa_stanowiska) LIKE UPPER('%" + attribute.toUpperCase() + "%') ";
            

                default:
        			sql = "SELECT imie, nazwisko,pracownicy.ulica,"
        					+ " pracownicy.nr_budynku, pracownicy.miasto, pesel, nazwa_stanowiska, nazwa "
        					+ " FROM Pracownicy join stanowiska using(id_stanowiska) "
        					+ " join filharmonie using(id_filharmonii) ";

            }
            ResultSet rs = stmt.executeQuery(sql);




            while (rs.next()) {

				String name_worker = rs.getString("imie");

				String surname_worker = rs.getString("nazwisko");
				String address_worker = rs.getString("ulica");

				String house_num_worker = rs.getString("nr_budynku");
				String town_worker = rs.getString("miasto");

				String pesel_worker = rs.getString("pesel");
				String profession_worker = rs.getString("nazwa_stanowiska");
				String symphony_worker = rs.getString("nazwa");

				worker.add(new Employee(name_worker, surname_worker, address_worker, house_num_worker, town_worker,
						pesel_worker, profession_worker, symphony_worker));

			}

			rs.close();
			stmt.close();



        }catch(SQLException se){
            se.printStackTrace();
        }finally{
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException se2){
                se2.printStackTrace();
            }


        }

        return worker;

    }

}
