import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class Administrator extends Application{

    public void run() {
        Scanner in = new Scanner(System.in);
        int choice = 0;
        print();
        while (choice != 5) {
            if (in.hasNextInt()) {  // Check if an integer is available
                choice = in.nextInt();
                if (choice < 1 || choice > 5) {
                    System.out.println("Please enter a number between 1 and 5!");

                } else if (choice == 1) {
                    create();

                } else if (choice == 2) {
                    delete();

                } else if (choice == 3) {
                    load();

                } else if (choice == 4) {
                    display();
                }

            } else {
                System.out.println("Your input should be an integer! Please try again.");
                in.next(); // Consume the invalid input
            }
            if (choice != 5) { // Only print the menu if the choice isn't to exit
                print();
            }
        }
    }

    // Option 1: Create tables
    public void create() {
        try (Connection conn = DriverManager.getConnection(DBMSLink, username, password);
             Statement stmt = conn.createStatement()) {
            // Combined SQL statements for creating tables
            stmt.executeUpdate("CREATE TABLE Category (ID INT PRIMARY KEY CHECK (ID BETWEEN 1 AND 9), Name VARCHAR(20) NOT NULL)");
            stmt.executeUpdate("CREATE TABLE Manufacturer (ID INT PRIMARY KEY CHECK (ID BETWEEN 1 AND 99), Name VARCHAR(20) NOT NULL, Address VARCHAR(50) NOT NULL, PhoneNumber INT NOT NULL CHECK (PhoneNumber BETWEEN 10000000 AND 99999999))");
            stmt.executeUpdate("CREATE TABLE Part (ID INT PRIMARY KEY CHECK (ID BETWEEN 1 AND 999), Name VARCHAR(20) NOT NULL, Price INT NOT NULL CHECK (Price BETWEEN 1 AND 99999), ManufacturerID INT REFERENCES Manufacturer(ID), CategoryID INT REFERENCES Category(ID), Warranty INT NOT NULL CHECK (Warranty BETWEEN 1 AND 99), Available_Quantity INT NOT NULL CHECK (Available_Quantity BETWEEN 0 AND 99))");
            stmt.executeUpdate("CREATE TABLE Salesperson (ID INT PRIMARY KEY CHECK (ID BETWEEN 1 AND 99), Name VARCHAR(20) NOT NULL, Address VARCHAR(50) NOT NULL, PhoneNumber INT NOT NULL CHECK (PhoneNumber BETWEEN 10000000 AND 99999999), Experience INT NOT NULL CHECK (Experience BETWEEN 1 AND 9))");
            stmt.executeUpdate("CREATE TABLE Transaction (ID INT PRIMARY KEY CHECK (ID BETWEEN 1 AND 9999), PartID INT REFERENCES Part(ID), SalespersonID INT REFERENCES Salesperson(ID), \"date\" DATE NOT NULL)");
            System.out.println("Tables created successfully!");
        } catch (Exception e) {
            System.err.println("Error creating tables: " + e.getMessage());
            try{
                Thread.sleep(500);
            }
            catch (Exception x){
                System.out.println("Error: " + x.getMessage());
            }
        }
    }

    // Option 2: Delete tables
    public void delete() {
        Scanner in = new Scanner(System.in);
        System.out.println("Are you sure? (Yes or No)");
        while(true) {
            String temp = in.nextLine();
            if (temp.equalsIgnoreCase("No")) {
                return;
            } else if (!temp.equalsIgnoreCase("Yes")) {
                System.out.println("Please enter Yes or No!");
                continue;
            }
            break;
        }

        try{
            Connection conn = DriverManager.getConnection(DBMSLink, username, password);
            Statement stmt = conn.createStatement();
            String databaseProductName = conn.getMetaData().getDatabaseProductName(); // Get database name

            String cascadeSyntax = "";
            if (databaseProductName.equalsIgnoreCase("Oracle"))
                cascadeSyntax = "CASCADE CONSTRAINTS";
            else if (databaseProductName.equalsIgnoreCase("PostgreSQL"))
                cascadeSyntax = "CASCADE";

            stmt.executeUpdate("DROP TABLE Transaction " + cascadeSyntax);
            stmt.executeUpdate("DROP TABLE Salesperson " + cascadeSyntax);
            stmt.executeUpdate("DROP TABLE Part " + cascadeSyntax);
            stmt.executeUpdate("DROP TABLE Manufacturer " + cascadeSyntax);
            stmt.executeUpdate("DROP TABLE Category " + cascadeSyntax);
            System.out.println("Tables deleted successfully!");
        } catch (Exception e) {
            System.err.println("Error deleting tables: " + e.getMessage());
            try{
                Thread.sleep(500);
            }
            catch (Exception x){
                System.out.println("Error: " + x.getMessage());
            }
        }
    }

    //Option 3
    public static void load(){
        String path = "src/main/java/xxx/";
        Scanner in = new Scanner(System.in);
        System.out.print("Type in the source data path: ");
        path = path.replace("xxx", in.nextLine());

        try{
            Application app = new Application();
            Connection conn = DriverManager.getConnection(app.DBMSLink, app.username, app.password);
            Statement stmt = conn.createStatement();
            String sql;
            in = new Scanner(new File(path + "category.txt"));

            String a, b, c, d, e, f, g;
            String[] line;
            while (in.hasNext()) {
                line = in.nextLine().split("\t");
                a = line[0];
                b = line[1];
                sql = String.format("INSERT INTO category (id, name) VALUES ('%s', '%s')", a, b);
                stmt.executeUpdate(sql);
            }

            in = new Scanner(new File(path + "manufacturer.txt"));
            line = new String[4];
            while (in.hasNext()) {
                line = in.nextLine().split("\t");
                a = line[0];
                b = line[1];
                c = line[2];
                d = line[3];
                sql = String.format("INSERT INTO manufacturer (id, name, address, phonenumber) VALUES ('%s', '%s', '%s', '%s')", a, b, c, d);
                stmt.executeUpdate(sql);
            }
            in = new Scanner(new File(path + "part.txt"));
            line = new String[7];
            while (in.hasNext()) {
                line = in.nextLine().split("\t");
                a = line[0];
                b = line[1];
                c = line[2];
                d = line[3];
                e = line[4];
                f = line[5];
                g = line[6];
                sql = String.format("INSERT INTO part (id, name, price, manufacturerid, categoryid, warranty, available_quantity)" +
                        " VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s')", a, b, c, d, e, f, g);
                stmt.executeUpdate(sql);
            }

            line = new String[5];
            in = new Scanner(new File(path + "salesperson.txt"));
            while (in.hasNext()) {
                line = in.nextLine().split("\t");
                a = line[0];
                b = line[1];
                c = line[2];
                d = line[3];
                e = line[4];
                sql = String.format("INSERT INTO salesperson (id, name, address, phonenumber, experience)" +
                        " VALUES ('%s', '%s', '%s', '%s', '%s')", a, b, c, d, e);
                stmt.executeUpdate(sql);
            }
            sql = "INSERT INTO transaction (ID, PARTID, SALESPERSONID, \"date\") VALUES (?, ?, ?, TO_DATE(?, 'DD/MM/YYYY'))";
            line = new String[4];
            in = new Scanner(new File(path + "transaction.txt"));
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                while (in.hasNextLine()) {
                    line = in.nextLine().split("\t");
                    pstmt.setInt(1, Integer.parseInt(line[0]));
                    pstmt.setInt(2, Integer.parseInt(line[1]));
                    pstmt.setInt(3, Integer.parseInt(line[2]));
                    pstmt.setString(4, line[3]);
                    pstmt.executeUpdate();
                }
            }
            catch(SQLException exception){
                System.out.println("SQL Error: " + exception.getMessage());
            }
        }
        catch (Exception e){
            System.err.println("Error: " + e);
            try{
                Thread.sleep(500);
            }
            catch (Exception x){
                System.out.println("Error: " + x.getMessage());
            }
        }

        System.out.println("File loaded successfully!");
    }

    //Option 4
    public static void display(){
        try{
            Application app = new Application();
            Connection conn = DriverManager.getConnection(app.DBMSLink, app.username, app.password);
            Statement stmt = conn.createStatement();
            Scanner in = new Scanner(System.in);
            String sql = "select * from xxx";

            System.out.print("Which table would you like to show: ");
            String temp = in.nextLine();
            if(!(temp.equals("category") || temp.equals("manufacturer") || temp.equals("part") || temp.equals("salesperson") || temp.equals("transaction"))){
                System.err.println("This schema does not exist!");
                try{
                    Thread.sleep(500);
                }
                catch (Exception x){
                    System.out.println("Error: " + x.getMessage());
                }
            }
            sql = sql.replace("xxx", temp);
            ResultSet rs = stmt.executeQuery(sql);

            switch (sql) {
                case "select * from category" -> {
                    System.out.println("| ID | Name |");
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        System.out.printf("| %d | %s |", id, name);
                        System.out.println();
                    }
                }
                case "select * from manufacturer" -> {
                    System.out.println("| ID | Name | Address | Phone Number |");
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        String address = rs.getString("address");
                        Integer phonenumber = rs.getInt("phonenumber");
                        System.out.printf("| %d | %s | %s | %d", id, name, address, phonenumber);
                        System.out.println();
                    }
                }
                case "select * from part" -> {
                    System.out.println("| ID | Name | Price | ManufacturerID | CategoryID | Warranty | Available Quantity |");
                    while (rs.next()) {
                        Integer id = rs.getInt("id");
                        String name = rs.getString("name");
                        Integer price = rs.getInt("price");
                        Integer manufacturerid = rs.getInt("manufacturerid");
                        Integer categoryid = rs.getInt("categoryid");
                        Integer warranty = rs.getInt("warranty");
                        Integer available_quantity = rs.getInt("available_quantity");
                        System.out.printf("| %d | %s | %d | %d | %d | %d | %d",
                                id, name, price, manufacturerid, categoryid, warranty, available_quantity);
                        System.out.println();
                    }
                }
                case "select * from salesperson" -> {
                    System.out.println("| ID | Name | Address | Phone Number | Experience |");
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        String address = rs.getString("address");
                        Integer phonenumber = rs.getInt("phonenumber");
                        Integer experience = rs.getInt("experience");
                        System.out.printf("| %d | %s | %s | %d | %d", id, name, address, phonenumber, experience);
                        System.out.println();
                    }
                }
                default -> {
                    System.out.println("| ID | PartID | SalespersonID | DATE |");
                    while (rs.next()) {
                        Integer id = rs.getInt("id");
                        Integer partid = rs.getInt("partid");
                        Integer salespersonid = rs.getInt("salespersonid");
                        Date date = rs.getDate("date");
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        String dateString = formatter.format(date);
                        System.out.printf("| %d | %d | %d | %s", id, partid, salespersonid, dateString);
                        System.out.println();
                    }
                }
            }
        }
        catch(Exception e){
            System.err.println("Error: " + e);
            try{
                Thread.sleep(500);
            }
            catch (Exception x){
                System.out.println("Error: " + x.getMessage());
            }
        }
    }

    //Helper functions
    public static void print() {
        System.out.print("\n-----Operations for administrator menu-----\n" +
                "What kinds of operation would you like to perform?\n" +
                "1. Create all tables\n" +
                "2. Delete all tables\n" +
                "3. Load from datafile\n" +
                "4. Show content of a table\n" +
                "5. Return to the main menu\n" +
                "Enter Your Choice: ");
    }

}
