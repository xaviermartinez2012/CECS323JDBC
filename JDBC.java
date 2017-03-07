
//package CECS323JDBCProject;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 *
 * @author Mimi Opkins with some tweaking from Dave Brown
 */
public class JDBC {

    // Database credentials.
    static String USER;
    static String PASS;
    static String DBNAME;
    
    /* 
    Each % denotes the start of a new field.
    The - denotes left justification.
    The number indicates how wide to make the field.
    The "s" denotes that it's a string.
    */
    static final String displayFormat = "%-5s%-15s%-15s%-15s\n";
    
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.apache.derby.jdbc.ClientDriver";
    static String DB_URL = "jdbc:derby://localhost:1527/";

    /**
     * Takes the input string and outputs "N/A" if the string is empty or null.
     *
     * @param input The string to be mapped.
     * @return Either the input string or "N/A" as appropriate.
     */
    public static String dispNull(String input) {
        //because of short circuiting, if it's null, it never checks the length.
        if (input == null || input.length() == 0) {
            return "N/A";
        } else {
            return input;
        }
    }
    /**
     * Text-based user interface.
     * @param in The global System.in Scanner.
     * @return The user's choice.
     */
    public static int UserInterface(Scanner in) {
        boolean correct_response = false;
        int user_response = 10;
        while (!correct_response) {
            System.out.println("-- What would you like to do:");
            System.out.println("\t(1)  List all writing groups.");
            System.out.println("\t(2)  List all the data for a group specified by the user.");
            System.out.println("\t(3)  List all publishers.");
            System.out.println("\t(4)  List all the data for a publisher specified by the user.");
            System.out.println("\t(5)  List all book titles.");
            System.out.println("\t(6)  List all the data for a book specified by the user.");
            System.out.println("\t(7)  Insert a new book.");
            System.out.println(
                    "\t(8)  Insert a new publisher and update all book published by one publisher to be published by the new publisher.");
            System.out.println("\t(9)  Remove a book specified by the user.");
            System.out.println("\t(10) Exit.");
            System.out.print("> ");
            try {
                if (in.hasNextInt()) {
                    user_response = in.nextInt();
                } else {
                    System.out.println("-- ERROR: Please enter an integer response. Try again.");
                    in.next();
                    continue;
                }
                if (user_response > 0 && user_response < 11) {
                    correct_response = true;
                } else {
                    System.out.println("-- ERROR: Please enter a valid choice. Try again.");
                }
            } catch (InputMismatchException input) {
                System.out.println("-- ERROR: Please enter an integer response. Try again.");
                in.next();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("-- ERROR: Unexpected error, exiting...");
                System.exit(-1);
            }
        }
        return user_response;
    }

    public static void main(String[] args) {
        /* 
        Prompt the user for the database name, and the credentials.
        If your database has no credentials, you can update this code to 
        remove that from the connection string.
        */
        Scanner in = new Scanner(System.in);
        System.out.print("-- Enter the name of the database: ");
        DBNAME = in.nextLine();
        System.out.print("-- Enter your user name at \"" + DBNAME + "\": ");
        USER = in.nextLine();
        System.out.print("-- Enter your password at \"" + DBNAME + "\": ");
        PASS = in.nextLine();
        //Constructing the database URL connection string
        DB_URL = DB_URL + DBNAME + ";user=" + USER + ";password=" + PASS;

        Connection conn = null; //initialize the connection
        try {
            // Register JDBC driver.
            Class.forName("org.apache.derby.jdbc.ClientDriver");

            // Open a connection.
            System.out.println("-- Connecting to database...");
            conn = DriverManager.getConnection(DB_URL);

            // User Interface.
            boolean exit = false;
            while (!exit) {
                int userChoice = UserInterface(in);
                switch (userChoice) {
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    break;
                case 7:
                    break;
                case 8:
                    break;
                case 9:
                    break;
                case 10:
                    exit = true;
                    break;
                }
            }
            // Clean-up environment
            conn.close();
            in.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            } // end finally
        } // end try
    }// end main
}// end JDBC
