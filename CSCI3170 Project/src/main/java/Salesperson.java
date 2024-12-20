import java.util.Scanner;
import java.sql.*;
import java.util.InputMismatchException;
import java.time.LocalDate;
import java.util.Random;


public class Salesperson extends Application{

    public void run() {
        Scanner in = new Scanner(System.in);
        int choice = 0;
        try {
            MasterPrint();
            choice = in.nextInt();
            while(choice != 3) {
                if (choice < 1 || choice > 3) {
                    System.out.println("Please enter a number between 1 and 3!");
                } else if (choice == 1) {
                    search();
                } else if (choice == 2) {
                    sell();
                }
                MasterPrint();
                choice = in.nextInt();
            }
        }
        catch (InputMismatchException e) {
            System.out.println("Please enter an actual number!\n");

            try{
                Thread.sleep(500);
            }
            catch (Exception x){
                System.out.println("Error: " + x.getMessage());
            }
        }
    }

    //Option 1
    public void search(){
        Scanner in = new Scanner(System.in);
        int choice = 0;


        SearchPrint();
        choice = in.nextInt();
        while(choice < 1 || choice > 3) {
            System.out.println("Please enter a number between 1 and 2!");
            System.out.println();
            SearchPrint();
            in.nextLine();
            choice = in.nextInt();
        }
        in.nextLine();
        System.out.print("Type in the Search Keyword: ");
        String keyword = in.nextLine();

        int choice2 = 0;

        OrderPrint();
        choice2 = in.nextInt();
        while(!(choice2 >= 1 && choice2 <= 2)) {
            if(choice2 < 1 || choice2 > 3) {
                System.out.println("Please enter a number between 1 and 2!");
                System.out.println();
            }
            OrderPrint();
            choice2 = in.nextInt();
        }

        String criterion;
        String order;

        if(choice == 1)	// Part name is p.name
            criterion = "p.name";
        else			// Manu name
            criterion = "m.name";

        if(choice2 == 1)// arranging price order
            order = "ASC";
        else
            order = "DESC";

        try (Connection conn = DriverManager.getConnection(DBMSLink, username, password)) {
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("select p.ID, p.Name, m.name as Manufacturer, c.Name as Category, p.available_quantity as Quantity, p.warranty, p.price from part p, category c, manufacturer m where p.categoryid = c.ID and p.manufacturerid = m.id and "+criterion+" LIKE \'"+keyword+"\' order by p.price "+order);
            System.out.println("| ID | Name | Manufacturer | Category | Quantity | Warranty | Price |");

            while (rs.next()) {
                System.out.println("| " + rs.getInt(1) +" | " + rs.getString(2) +" | "+ rs.getString(3)+
                        " | " + rs.getString(4) + " | " + rs.getInt(5) + " | "+ rs.getInt(6) +
                        " | " + rs.getInt(7));
            }
            System.out.println("End of Query");

        } catch (SQLException e) {
            System.err.println("Error in Salesperon search parts " +e);

            try{
                Thread.sleep(500);
            }
            catch (Exception x){
                System.out.println("Error: " + x.getMessage());
            }
        }

    }

    //Option 2
    public void sell(){
        // Count the number of parts
        int Tid = 0;

        int Pid=0;
        Scanner in = new Scanner(System.in);
        System.out.print("Enter the Part ID: ");
        Pid = in.nextInt();

        int Sid=0;
        System.out.print("Enter the Salesperson ID: ");
        Sid = in.nextInt();

        int state1 =1, state2 = 1, state3 = 1;

        try (Connection conn = DriverManager.getConnection(DBMSLink, username, password)) {
            Statement stmt = conn.createStatement();
            ResultSet rs1 = stmt.executeQuery("select ID from part where ID = " + Pid);
            rs1.next();
            try {
                Integer.parseInt(rs1.getString(1));
            } catch(Exception e) {
                state1 = 0;
            }

            ResultSet rs2 = stmt.executeQuery("select ID from salesperson where ID = " + Sid);
            rs2.next();
            try {
                Integer.parseInt(rs2.getString(1));
            } catch(Exception e) {
                state2 = 0;
            }

            if (state1 == 0 || state2 == 0) {
                System.out.println("Invalid part ID or salesperson ID");
                System.out.println("Transaction rejected");
                System.out.println();
                return;
            }

            Random rand = new Random();

            Tid = rand.nextInt(1000);
            ResultSet rs3 = stmt.executeQuery("select ID from transaction where ID = " +  Tid);
            rs3.next();

            try {
                Integer.parseInt(rs3.getString(1));
            } catch(Exception e) {
                state3 = 0;
            }

            while(state3 == 1) {
                Tid = rand.nextInt(1000);
                rs3 = stmt.executeQuery("select ID from transaction where ID = " +  Tid);
                rs3.next();

                try {
                    Integer.parseInt(rs3.getString(1));
                    state3 = 1;
                } catch(Exception e) {
                    state3 = 0;
                }
            }

        } catch (SQLException e) {
            System.out.println(e);

            try{
                Thread.sleep(500);
            }
            catch (Exception x){
                System.out.println("Error: " + x.getMessage());
            }
        }

        String Name ="";
        int ID=0;
        int Original_Quantity=0;
        int New_Quantity=0;
        try (Connection conn = DriverManager.getConnection(DBMSLink, username, password)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select Name, ID, Available_Quantity from Part where ID = "+Pid);
            rs.next();

            Name = rs.getString(1);
            ID = rs.getInt(2);
            Original_Quantity = rs.getInt(3);
            New_Quantity = Original_Quantity-1;

            if(New_Quantity < 0) {
                System.out.println("Product: " + Name + "(id: " + ID + ")"+ " is out of stock");
                System.out.println("Transaction rejected");
                System.out.println();
            } else {
                stmt.execute("Update Part set Available_Quantity = " + New_Quantity + "where ID = " + ID);
                System.out.println("Product: " + Name + "(id: " + ID + ")"+ " Remaining Quatity: " + New_Quantity);
                System.out.println();

                //Insert transaction record
                LocalDate currentDate = LocalDate.now();
                String today = currentDate.toString(); //2024-12-17

                //System.out.println("Insert into transaction values ( " + Tid + "," + Pid + "," + Sid + ", TO_DATE(\'" + today +"\', \'YYYY-MM-DD\'))");

                stmt.execute("Insert into transaction values ( " + Tid + "," + Pid + "," + Sid + ", TO_DATE(\'" + today +"\', \'YYYY-MM-DD\'))" );

                // Update part set Available_Quantity = 99 where ID = 1;
                // insert into transaction values (37, 1, 1, TO_DATE('2024-12-17', 'YYYY-MM-DD'));
            }

        } catch(SQLException e) {
            System.err.println("Error in selling part");

            try{
                Thread.sleep(500);
            }
            catch (Exception x){
                System.out.println("Error: " + x.getMessage());
            }
        }


    }


    private void MasterPrint() {
        System.out.print("-----Operations for salesperson menu-----\n" +
                "What kinds of operation would you like to perform?\n" +
                "1. Search for parts\n" +
                "2. Sell a part\n" +
                "3. Return to the main menu\n" +
                "Enter your choice: ");
    }

    private void SearchPrint(){
        System.out.println("Choose the search criterion: ");
        System.out.println("1. Part Name");
        System.out.println("2. Manufacturer Name");
        System.out.print("Choose the search criterion: ");


    }

    private void OrderPrint() {
        System.out.println("Choose ordering: ");
        System.out.println("1. By price, ascending order");
        System.out.println("2. By price, descending order");
        System.out.print("Choose the search ordering: ");

    }

}
