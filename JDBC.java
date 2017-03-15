
//package CECS323JDBCProject;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 *
 * @author Xavier Martinez and Brian Lombardo
 */
public class JDBC {

    // Database credentials.
    static String USER;
    static String PASS;
    static String DBNAME;

    // Prepared Strings
    static String deleteBookStatement = "DELETE FROM books WHERE groupname=? AND booktitle=?";
    static String insertPublisher = "INSERT INTO publishers (publishername, publisheraddress, publisherphone, publisheremail) values (?, ?, ?, ?)";
    static String updateBooks = "UPDATE books SET publishername=? WHERE publishername=?";

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
     *
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
            System.out.println("\t(8)  Insert a new publisher and update all book published by one publisher to be published by the new publisher.");
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

    public static String[] DeleteBookUserInterface(Scanner in) {
        boolean correct_response = false;
        String[] arguments = new String[2];
        arguments[0] = "...";
        arguments[1] = "...";
        while (!correct_response) {
            System.out.println("-- Enter the groupname.");
            System.out.print("> ");
            in.nextLine();
            try {
                arguments[0] = in.nextLine();
                System.out.println("-- Enter the book title.");
                System.out.print("> ");
                arguments[1] = in.nextLine();
                correct_response = true;
            } catch (NoSuchElementException e) {
                System.out.println("-- ERROR: No line was found!");
            } catch (IllegalStateException state) {
                System.out.println("-- ERROR: Scanner is closed!");
                return arguments;
            }
        }
        return arguments;
    }

    public static boolean DeleteBook(Connection conn, String groupName, String bookTitle) {
        boolean statement = false;
        boolean setStatement = false;
        boolean executeStatement = false;
        boolean deletion = false;
        try {
            PreparedStatement deleteStatement = conn.prepareStatement(deleteBookStatement);
            statement = true;
            deleteStatement.setString(1, groupName);
            deleteStatement.setString(2, bookTitle);
            setStatement = true;
            int status = deleteStatement.executeUpdate();
            executeStatement = true;
            if (status != 0) {
                deletion = true;
            }
        } catch (SQLTimeoutException timeOut) {
            System.out.println("-- ERROR: Executing Statement Timed Out!");
        } catch (SQLException sql) {
            if (!statement) {
                System.out.println("-- ERROR: Preparing Statement Failed!");
            } else if (!setStatement) {
                System.out.println("-- ERROR: Setting Statement Failed!");
            } else if (!executeStatement) {
                System.out.println("-- ERROR: Executing Statement Failed!");
            } else {
                System.out.println("-- ERROR: Unknown Error!");
            }
        }
        return deletion;
    }

    public static String[] NewPublisherUserInterface(Scanner in) {
        boolean correct_response = false;
        String[] arguments = new String[5];
        for (int i = 0; i < 5; i++) {
            arguments[i] = "...";
        }
        while (!correct_response) {
            System.out.println("-- Enter the Publisher Name.");
            System.out.print("> ");
            in.nextLine();
            try {
                arguments[0] = in.nextLine();
                System.out.println("-- Enter the Publisher Address.");
                System.out.print("> ");
                arguments[1] = in.nextLine();
                System.out.println("-- Enter the Publisher Phone.");
                System.out.print("> ");
                arguments[2] = in.nextLine();
                System.out.println("-- Enter the Publisher Email.");
                System.out.print("> ");
                arguments[3] = in.nextLine();
                System.out.println("-- Enter the Publisher to Modify.");
                System.out.print("> ");
                arguments[4] = in.nextLine();
                correct_response = true;
            } catch (NoSuchElementException e) {
                System.out.println("-- ERROR: No line was found!");
            } catch (IllegalStateException state) {
                System.out.println("-- ERROR: Scanner is closed!");
                return arguments;
            }
        }
        return arguments;
    }

    public static boolean NewPublisher(Connection conn, String[] arguments) {
        boolean statement = false;
        boolean setStatement = false;
        boolean executeStatement = false;
        boolean insertion = false;
        String publisherName = arguments[0];
        String publisherAddress = arguments[1];
        String publisherPhone = arguments[2];
        String publisherEmail = arguments[3];
        try {
            PreparedStatement insertStatement = conn.prepareStatement(insertPublisher);
            statement = true;
            insertStatement.setString(1, publisherName);
            insertStatement.setString(2, publisherAddress);
            insertStatement.setString(3, publisherPhone);
            insertStatement.setString(4, publisherEmail);
            setStatement = true;
            int status = insertStatement.executeUpdate();
            executeStatement = true;
            if (status != 0) {
                insertion = true;
            }
        } catch (SQLTimeoutException timeOut) {
            System.out.println("-- ERROR: Executing Statement Timed Out!");
        } catch (SQLException sql) {
            if (!statement) {
                System.out.println("-- ERROR: Preparing Statement Failed!");
            } else if (!setStatement) {
                System.out.println("-- ERROR: Setting Statement Failed!");
            } else if (!executeStatement) {
                System.out.println("-- ERROR: Executing Statement Failed!");
            } else {
                System.out.println("-- ERROR: Unknown Error!");
            }
        }
        return insertion;
    }

    public static boolean UpdateBooks(Connection conn, String[] arguments) {
        boolean updated = false;
        if (NewPublisher(conn, arguments)) {
            boolean statement = false;
            boolean setStatement = false;
            boolean executeStatement = false;
            String publisherName = arguments[0];
            String publisherReplace = arguments[4];
            System.out.println("-- Publisher \"" + publisherName + "\" inserted into publishers successfully.");
            try {
                PreparedStatement insertStatement = conn.prepareStatement(updateBooks);
                statement = true;
                insertStatement.setString(1, publisherName);
                insertStatement.setString(2, publisherReplace);
                setStatement = true;
                int status = insertStatement.executeUpdate();
                executeStatement = true;
                if (status != 0) {
                    updated = true;
                }
            } catch (SQLTimeoutException timeOut) {
                System.out.println("-- ERROR: Executing Statement Timed Out!");
            } catch (SQLException sql) {
                if (!statement) {
                    System.out.println("-- ERROR: Preparing Statement Failed!");
                } else if (!setStatement) {
                    System.out.println("-- ERROR: Setting Statement Failed!");
                } else if (!executeStatement) {
                    System.out.println("-- ERROR: Executing Statement Failed!");
                } else {
                    System.out.println("-- ERROR: Unknown Error!");
                }
            }
        }
        return updated;
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
        boolean getConnection = false;
        boolean closeConnection = false;
        try {
            // Register JDBC driver.
            Class.forName("org.apache.derby.jdbc.ClientDriver");

            // Open a connection.
            System.out.println("-- Connecting to database...");
            conn = DriverManager.getConnection(DB_URL);
            getConnection = true;

            // User Interface.
            boolean exit = false;
            while (!exit) {
                int userChoice = UserInterface(in);
                String[] arguments;
                switch (userChoice) {
                    case 1:
                        ListAllWritingGroups();
                        break;
                    case 2:
                        break;
                    case 3:
                        ListAllPublishers();
                        break;
                    case 4:
                        break;
                    case 5:
                        ListAllBooks();
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    case 8:
                        arguments = NewPublisherUserInterface(in);
                        boolean update = UpdateBooks(conn, arguments);
                        if (update) {
                            System.out.println("-- Successfully updated books from \"" + arguments[4] + "\" to " + arguments[0] + ".");
                        } else {
                            System.out.println("-- Update failed!");
                        }
                        break;
                    case 9:
                        arguments = DeleteBookUserInterface(in);
                        boolean deletion = DeleteBook(conn, arguments[0], arguments[1]);
                        if (deletion) {
                            System.out.println("-- Successfully deleted \"" + arguments[1] + "\" by " + arguments[0] + ".");
                        } else {
                            System.out.println("-- Deletion failed!");
                        }
                        break;
                    case 10:
                        exit = true;
                        break;
                }
            }
            // Clean-up environment
            conn.close();
            closeConnection = true;
            in.close();
        } catch (SQLTimeoutException timeOut) {
            System.out.println("-- ERROR: Connection Timed Out!");
        } catch (SQLException se) {
            //Handle errors for JDBC
            if (!getConnection) {
                System.out.println("-- ERROR: Database access error occurred when attempting to connect!");
            } else if (!closeConnection) {
                System.out.println("-- ERROR: Closing connection to database failed!");
            } else {
                System.out.println("-- ERROR: Unknown Error!");
            }
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

    public static void ListAllWritingGroups() {

        Connection conn = null; //initialize the connection
        Statement stmt = null;  //initialize the statement that we're using

        try {
            //STEP 2: Register JDBC driver
            Class.forName("org.apache.derby.jdbc.ClientDriver");

            //STEP 3: Open a connection
            conn = DriverManager.getConnection(DB_URL);

            //STEP 4: Execute a query 
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT groupname,headwriter,yearformed,subject FROM writinggroups";
            ResultSet rs = stmt.executeQuery(sql);

            //STEP 5: Extract data from result set          
            System.out.println("-- Writing Groups:");
            System.out.println("Group name          Head writer         Year Formed         Subject");
            System.out.println("----------          -----------         -----------         -------");

            while (rs.next()) {
                //Retrieve by column name
                String gn = rs.getString("groupname");
                String hw = rs.getString("headwriter");
                String yf = rs.getString("yearformed");
                String sj = rs.getString("subject");

                //alignment
                String indent = "                    ";
                gn += indent.substring(0, indent.length() - gn.length());
                hw += indent.substring(0, indent.length() - hw.length());
                yf += indent.substring(0, indent.length() - yf.length());
                sj += indent.substring(0, indent.length() - sj.length());

                //Display values
                System.out.printf(dispNull(gn) + dispNull(hw) + dispNull(yf) + dispNull(sj));
                System.out.println();
            }
            System.out.println();

            //STEP 6: Clean-up environment
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
    }//end ListAllWritingGroups

    public static void ListAllPublishers() {

        Connection conn = null; //initialize the connection
        Statement stmt = null;  //initialize the statement that we're using

        try {
            //STEP 2: Register JDBC driver
            Class.forName("org.apache.derby.jdbc.ClientDriver");

            //STEP 3: Open a connection
            conn = DriverManager.getConnection(DB_URL);

            //STEP 4: Execute a query 
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT publishername,publisheraddress,publisherphone,publisheremail FROM publishers";
            ResultSet rs = stmt.executeQuery(sql);

            //STEP 5: Extract data from result set          
            System.out.println("-- Publishers:");
            System.out.println("Publisher name      Address             Phone               Email");
            System.out.println("--------------      -------             ------              -----");

            while (rs.next()) {
                //Retrieve by column name
                String gn = rs.getString("publishername");
                String hw = rs.getString("publisheraddress");
                String yf = rs.getString("publisherphone");
                String sj = rs.getString("publisheremail");

                //alignment
                String indent = "                    ";
                gn += indent.substring(0, indent.length() - gn.length());
                hw += indent.substring(0, indent.length() - hw.length());
                yf += indent.substring(0, indent.length() - yf.length());
                sj += indent.substring(0, indent.length() - sj.length());

                //Display values
                System.out.printf(dispNull(gn) + dispNull(hw) + dispNull(yf) + dispNull(sj));
                System.out.println();
            }
            System.out.println();

            //STEP 6: Clean-up environment
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
    }//end ListAllPublishers

    public static void ListAllBooks() {

        Connection conn = null; //initialize the connection
        Statement stmt = null;  //initialize the statement that we're using

        try {
            //STEP 2: Register JDBC driver
            Class.forName("org.apache.derby.jdbc.ClientDriver");

            //STEP 3: Open a connection
            conn = DriverManager.getConnection(DB_URL);

            //STEP 4: Execute a query 
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT groupname,booktitle,publishername,yearpublished,numberpages FROM books";
            ResultSet rs = stmt.executeQuery(sql);

            //STEP 5: Extract data from result set          
            System.out.println("-- Books:");
            System.out.println("Title               Author              Publisher           Year Published      Number Pages");
            System.out.println("-----               ------              ---------           -------------       ------------");

            while (rs.next()) {
                //Retrieve by column name
                String gn = rs.getString("booktitle");
                String hw = rs.getString("groupname");
                String yf = rs.getString("publishername");
                String sj = rs.getString("yearpublished");
                String np = rs.getString("numberpages");

                //alignment
                String indent = "                    ";
                gn += indent.substring(0, indent.length() - gn.length());
                hw += indent.substring(0, indent.length() - hw.length());
                yf += indent.substring(0, indent.length() - yf.length());
                sj += indent.substring(0, indent.length() - sj.length());
                np += indent.substring(0, indent.length() - np.length());

                //Display values
                System.out.printf(dispNull(gn) + dispNull(hw) + dispNull(yf) + dispNull(sj) + dispNull(np));
                System.out.println();
            }
            System.out.println();

            //STEP 6: Clean-up environment
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
    }// end ListAllBooks
}// end JDBC
