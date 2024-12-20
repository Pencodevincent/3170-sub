import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

public class Application {
    String DBMSLink = "jdbc:oracle:thin:@//db18.cse.cuhk.edu.hk:1521/oradb.cse.cuhk.edu.hk";
    String username = "h082";
    String password = "DruFryWo";

    public static void start() {
        Application app = new Application();
        Administrator admin = new Administrator();
        Manager manager = new Manager();
        Salesperson salesperson = new Salesperson();

        try {
            //Postgresql is for testing, modify the database link, user and password variable to log in other databases
            Connection conn = DriverManager.getConnection(app.DBMSLink, app.username, app.password);
        }
        catch(Exception x) {
            System.err.println("Error!\n" + x.getMessage());
            System.exit(1);
        }

        //Debug purposes
        //System.out.println("Successfully connected to the database");

        //Initialization
        int choice = 0;
        Scanner in = new Scanner(System.in);
        System.out.println("Welcome to sales system!");
        print();

        while (choice != 4) {
            if (in.hasNextInt()) {
                choice = in.nextInt();
                if (choice < 1 || choice > 4)
                    System.out.println("Invalid choice! Please try again.");

                else if (choice == 1)
                    admin.run();

                else if (choice == 2)
                    salesperson.run();

                else if (choice == 3)
                    manager.run();

            }
            else{
                System.out.println("Your input should be an integer! Please try again.\n");
                in.next();
            }

            if(choice != 4)
                print();
        }

        System.out.println("Thank you for using our application!");
    }

    public static void print(){
        System.out.print("-----Main Menu-----\n" +
                "What kind of operation would you like to perform?\n" +
                "1. Operations for administrator\n" +
                "2. Operations for salesperson\n" +
                "3. Operations for manager\n" +
                "4. Exit this program\n" +
                "Enter your choice: ");
    }
}