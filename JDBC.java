
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
    static String insertBook = "INSERT INTO books (groupname, bookTitle, publisherName, yearpublished, numberpages) values (?, ?, ?, ?, ?)";
    static String updateBooks = "UPDATE books SET publishername=? WHERE publishername=?";
    static String getBooks = "SELECT groupname, booktitle, publishername, yearpublished, numberpages FROM books";
    static String getPublishers = "SELECT publishername, publisheraddress, publisherphone, publisheremail FROM publishers";
    static String getWritingGroups = "SELECT groupname, headwriter, yearformed, subject FROM writinggroups";
    static String getBookData = "SELECT groupname, booktitle, publishername, yearpublished, numberpages FROM books WHERE groupName=? AND booktitle=?";
    static String getWritingGroupData = "SELECT groupName, headwriter, yearformed, subject FROM writinggroups WHERE groupname=?";
    static String getPublisherData = "SELECT publishername, publisheraddress, publisherphone, publisheremail FROM publishers where publisherName=?";
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
            System.out.println("\t(2)  List all the data for a specified group.");
            System.out.println("\t(3)  List all publishers.");
            System.out.println("\t(4)  List all the data for a specified publisher.");
            System.out.println("\t(5)  List all book titles.");
            System.out.println("\t(6)  List all the data for a specified book.");
            System.out.println("\t(7)  Insert a new book.");
            System.out.println(
                    "\t(8)  Insert a new publisher and update all book published by one publisher to be published by the new publisher.");
            System.out.println("\t(9)  Remove a specified book.");
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

    /**
     * Method to list all writing groups in a SQL DB.
     * @param conn The Connection to the SQL DB.
     */
    public static void ListAllWritingGroups(Connection conn) {
        boolean statement = false;
        boolean executeStatement = false;
        boolean releaseResource = false;
        try {
            // Prepare SQL statement
            PreparedStatement stmt = conn.prepareStatement(getWritingGroups);
            statement = true;
            ResultSet rs = stmt.executeQuery();
            executeStatement = true;
            // Extract data from result set
            int rows = 0;
            while (rs.next()) {
                rows++;
                // Retrieve by column name
                String gn = rs.getString("groupname");
                String hw = rs.getString("headwriter");
                String yf = rs.getString("yearformed");
                String sj = rs.getString("subject");
                // Alignment
                String indent = "                    ";
                gn += indent.substring(0, indent.length() - gn.length());
                hw += indent.substring(0, indent.length() - hw.length());
                yf += indent.substring(0, indent.length() - yf.length());
                sj += indent.substring(0, indent.length() - sj.length());
                // Display values
                if (rows == 1) {
                    System.out.println("-- Writing Groups:");
                    System.out.println("Group Name          Head Writer         Year Formed         Subject");
                    System.out.println("----------          -----------         -----------         -------");
                }
                System.out.printf(dispNull(gn) + dispNull(hw) + dispNull(yf) + dispNull(sj));
                System.out.println();
            }
            if (rows == 0) {
                System.out.println("-- ERROR: No rows in Publishers table!");
            } else {
                System.out.println();
            }
            // Clean-up environment
            stmt.close();
            releaseResource = true;
        } catch (SQLTimeoutException timeOut) {
            System.out.println("-- ERROR: Executing Statement Timed Out!");
        } catch (SQLException sql) {
            if (!statement) {
                System.out.println("-- ERROR: Preparing Statement Failed!");
            } else if (!executeStatement) {
                System.out.println("-- ERROR: Executing Statement Failed!");
            } else if (!releaseResource) {
                System.out.println("ERROR: Releasing Statement Resources Failed!");
            } else {
                System.out.println("-- ERROR: Unknown Error!");
            }
        }
    }

    /**
     * Method to list all datum for a specified writing group in a SQL DB.
     * @param conn The Connection to the SQL DB.
     * @param in The global System.in scanner.
     */
    public static void ListWritingGroupSpecified(Connection conn, Scanner in) {
        boolean statement = false;
        boolean setStatement = false;
        boolean executeStatement = false;
        boolean releaseResource = false;
        try {
            // Get user input
            in.nextLine();
            System.out.println("-- Enter the Writing Group Name");
            System.out.print("> ");
            String writingGroup = in.nextLine();
            // Prepare SQL statement
            PreparedStatement stmt = conn.prepareStatement(getWritingGroupData);
            statement = true;
            stmt.setString(1, writingGroup);
            setStatement = true;
            ResultSet rs = stmt.executeQuery();
            executeStatement = true;
            // Extract data from result set
            int rows = 0;
            while (rs.next()) {
                rows++;
                //Retrieve by column name      
                String gn = rs.getString("groupname");
                String hw = rs.getString("headwriter");
                String yf = rs.getString("yearformed");
                String sj = rs.getString("subject");
                // Alignment
                String indent = "                    ";
                gn += indent.substring(0, indent.length() - gn.length());
                hw += indent.substring(0, indent.length() - hw.length());
                yf += indent.substring(0, indent.length() - yf.length());
                sj += indent.substring(0, indent.length() - sj.length());
                // Display values
                System.out.println("Group Name          Head Writer         Year Formed         Subject");
                System.out.println("----------          -----------         -----------         -------");
                System.out.printf(dispNull(gn) + dispNull(hw) + dispNull(yf) + dispNull(sj));
                System.out.println();
            }
            if (rows == 0) {
                System.out.println("-- ERROR: The writing group you specified does not exist!");
            } else {
                System.out.println();
            }
            // Clean-up environment
            stmt.close();
            releaseResource = true;
        } catch (NoSuchElementException e) {
            System.out.println("-- ERROR: No line was found!");
        } catch (IllegalStateException state) {
            System.out.println("-- ERROR: Scanner is closed!");
        } catch (SQLTimeoutException timeOut) {
            System.out.println("-- ERROR: Executing Statement Timed Out!");
        } catch (SQLException sql) {
            if (!statement) {
                System.out.println("-- ERROR: Preparing Statement Failed!");
            } else if (!setStatement) {
                System.out.println("-- ERROR: Setting Statement Failed!");
            } else if (!executeStatement) {
                System.out.println("-- ERROR: Executing Statement Failed!");
            } else if (!releaseResource) {
                System.out.println("ERROR: Releasing Statement Resources Failed!");
            } else {
                System.out.println("-- ERROR: Unknown Error!");
            }
        }
    }

    /**
     * Method to list all publishers in a SQL DB.
     * @param conn The Connection to the SQL DB.
     */
    public static void ListAllPublishers(Connection conn) {
        boolean statement = false;
        boolean executeStatement = false;
        boolean releaseResource = false;
        try {
            // Prepare SQl statement
            PreparedStatement stmt = conn.prepareStatement(getPublishers);
            statement = true;
            ResultSet rs = stmt.executeQuery();
            executeStatement = true;
            // Extract data from result set
            int rows = 0;
            while (rs.next()) {
                rows++;
                // Retrieve by column name
                String gn = rs.getString("publishername");
                String hw = rs.getString("publisheraddress");
                String yf = rs.getString("publisherphone");
                String sj = rs.getString("publisheremail");
                // Alignment
                String indent = "                    ";
                gn += indent.substring(0, indent.length() - gn.length());
                hw += indent.substring(0, indent.length() - hw.length());
                yf += indent.substring(0, indent.length() - yf.length());
                sj += indent.substring(0, indent.length() - sj.length());
                if (rows == 1) {
                    System.out.println("-- Publishers:");
                    System.out.println("Publisher Name      Address             Phone               Email");
                    System.out.println("--------------      -------             ------              -----");
                }
                // Display values
                System.out.printf(dispNull(gn) + dispNull(hw) + dispNull(yf) + dispNull(sj));
                System.out.println();
            }
            if (rows == 0) {
                System.out.println("-- ERROR: No rows in Publishers table!");
            } else {
                System.out.println();
            }
            // Clean-up environment
            stmt.close();
            releaseResource = true;
        } catch (SQLTimeoutException timeOut) {
            System.out.println("-- ERROR: Executing Statement Timed Out!");
        } catch (SQLException sql) {
            if (!statement) {
                System.out.println("-- ERROR: Preparing Statement Failed!");
            } else if (!executeStatement) {
                System.out.println("-- ERROR: Executing Statement Failed!");
            } else if (!releaseResource) {
                System.out.println("ERROR: Releasing Statement Resources Failed!");
            } else {
                System.out.println("-- ERROR: Unknown Error!");
            }
        }
    }

    /**
     * Method to list all datum for a specified publisher in a SQL DB.
     * @param conn The Connection to the SQL DB.
     * @param in The global System.in scanner.
     */
    public static void ListPublisherSpecified(Connection conn, Scanner in) {
        boolean statement = false;
        boolean setStatement = false;
        boolean executeStatement = false;
        boolean releaseResource = false;
        try {
            // Get user input
            in.nextLine();
            System.out.println("-- Enter the Publisher Name.");
            System.out.print("> ");
            String publisherName = in.nextLine();
            // Prepare SQL statement
            PreparedStatement stmt = conn.prepareStatement(getPublisherData);
            statement = true;
            stmt.setString(1, publisherName);
            setStatement = true;
            ResultSet rs = stmt.executeQuery();
            executeStatement = true;
            //Extract data from result set
            int rows = 0;
            while (rs.next()) {
                rows++;
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
                // Display values
                if (rows == 1) {
                    System.out.println("Publisher Name      Address             Phone               Email");
                    System.out.println("--------------      -------             ------              -----");
                }
                System.out.printf(dispNull(gn) + dispNull(hw) + dispNull(yf) + dispNull(sj));
                System.out.println();
            }
            if (rows == 0) {
                System.out.println("-- ERROR: The publisher you specified does not exist!");
            } else {
                System.out.println();
            }
            // Clean-up environment
            stmt.close();
            releaseResource = true;
        } catch (NoSuchElementException e) {
            System.out.println("-- ERROR: No line was found!");
        } catch (IllegalStateException state) {
            System.out.println("-- ERROR: Scanner is closed!");
        } catch (SQLTimeoutException timeOut) {
            System.out.println("-- ERROR: Executing Statement Timed Out!");
        } catch (SQLException sql) {
            if (!statement) {
                System.out.println("-- ERROR: Preparing Statement Failed!");
            } else if (!setStatement) {
                System.out.println("-- ERROR: Setting Statement Failed!");
            } else if (!executeStatement) {
                System.out.println("-- ERROR: Executing Statement Failed!");
            } else if (!releaseResource) {
                System.out.println("ERROR: Releasing Statement Resources Failed!");
            } else {
                System.out.println("-- ERROR: Unknown Error!");
            }
        }

    }

    /**
     * Method to list all books in a SQL DB.
     * @param conn The Connection to the SQL DB.
     */
    public static void ListAllBooks(Connection conn) {
        boolean statement = false;
        boolean executeStatement = false;
        boolean releaseResource = false;
        try {
            // Prepare SQL statement
            PreparedStatement stmt = conn.prepareStatement(getBooks);
            statement = true;
            ResultSet rs = stmt.executeQuery();
            executeStatement = true;
            // Extract data from result set
            int rows = 0;
            while (rs.next()) {
                rows++;
                // Retrieve by column name
                String gn = rs.getString("booktitle");
                String hw = rs.getString("groupname");
                String yf = rs.getString("publishername");
                String sj = rs.getString("yearpublished");
                String np = rs.getString("numberpages");
                // Alignment   
                String indent = "                    ";
                gn += indent.substring(0, indent.length() - gn.length());
                hw += indent.substring(0, indent.length() - hw.length());
                yf += indent.substring(0, indent.length() - yf.length());
                sj += indent.substring(0, indent.length() - sj.length());
                np += indent.substring(0, indent.length() - np.length());
                if (rows == 1) {
                    System.out.println(
                            "Title               Author              Publisher           Year Published      Number Pages");
                    System.out.println(
                            "-----               ------              ---------           -------------       ------------");
                }
                System.out.printf(dispNull(gn) + dispNull(hw) + dispNull(yf) + dispNull(sj) + dispNull(np));
                System.out.println();
            }
            if (rows == 0) {
                System.out.println("-- ERROR: No rows in Books table!");
            } else {
                System.out.println();
            }
            // Clean-up environment
            stmt.close();
            releaseResource = true;
        } catch (SQLTimeoutException timeOut) {
            System.out.println("-- ERROR: Executing Statement Timed Out!");
        } catch (SQLException sql) {
            if (!statement) {
                System.out.println("-- ERROR: Preparing Statement Failed!");
            } else if (!executeStatement) {
                System.out.println("-- ERROR: Executing Statement Failed!");
            } else if (!releaseResource) {
                System.out.println("ERROR: Releasing Statement Resources Failed!");
            } else {
                System.out.println("-- ERROR: Unknown Error!");
            }
        }
    }

    /**
     * Method to list all datum for a specified book in a SQL DB.
     * @param conn The Connection to the SQL DB.
     * @param in The global System.in scanner.
     */
    public static void ListBookSpecified(Connection conn, Scanner in) {
        boolean statement = false;
        boolean setStatement = false;
        boolean executeStatement = false;
        boolean releaseResource = false;
        try {
            // Get user input
            in.nextLine();
            System.out.println("-- Enter the Book Title.");
            System.out.print("> ");
            String bookTitle = in.nextLine();
            System.out.println("-- Enter the Writing Group Name");
            System.out.print("> ");
            String writingGroup = in.nextLine();
            // Prepare SQL statement
            PreparedStatement stmt = conn.prepareStatement(getBookData);
            statement = true;
            stmt.setString(1, writingGroup);
            stmt.setString(2, bookTitle);
            setStatement = true;
            ResultSet rs = stmt.executeQuery();
            executeStatement = true;
            // Extract data from result set
            int rows = 0;
            while (rs.next()) {
                rows++;
                // Retrieve by column name
                String gn = rs.getString("booktitle");
                String hw = rs.getString("groupname");
                String yf = rs.getString("publishername");
                String sj = rs.getString("yearpublished");
                String np = rs.getString("numberpages");
                // Alignment
                String indent = "                    ";
                gn += indent.substring(0, indent.length() - gn.length());
                hw += indent.substring(0, indent.length() - hw.length());
                yf += indent.substring(0, indent.length() - yf.length());
                sj += indent.substring(0, indent.length() - sj.length());
                np += indent.substring(0, indent.length() - np.length());
                System.out.println(
                        "Title               Author              Publisher           Year Published      Number Pages");
                System.out.println(
                        "-----               ------              ---------           -------------       ------------");
                System.out.printf(dispNull(gn) + dispNull(hw) + dispNull(yf) + dispNull(sj) + dispNull(np));
                System.out.println();

            }
            if (rows == 0) {
                System.out.println("-- ERROR: The book you specified does not exist!");
            } else {
                System.out.println();
            }
            // Clean-up environment
            stmt.close();
            releaseResource = true;
        } catch (NoSuchElementException e) {
            System.out.println("-- ERROR: No line was found!");
        } catch (IllegalStateException state) {
            System.out.println("-- ERROR: Scanner is closed!");
        } catch (SQLTimeoutException timeOut) {
            System.out.println("-- ERROR: Executing Statement Timed Out!");
        } catch (SQLException sql) {
            if (!statement) {
                System.out.println("-- ERROR: Preparing Statement Failed!");
            } else if (!setStatement) {
                System.out.println("-- ERROR: Setting Statement Failed!");
            } else if (!executeStatement) {
                System.out.println("-- ERROR: Executing Statement Failed!");
            } else if (!releaseResource) {
                System.out.println("ERROR: Releasing Statement Resources Failed!");
            } else {
                System.out.println("-- ERROR: Unknown Error!");
            }
        }
    }

    /**
     * Takes in user input to delete a particular book.
     * @param in The global System.in Scanner
     * @return The user's arguments.
     */
    public static String[] DeleteBookUserInterface(Scanner in) {
        boolean correct_response = false;
        String[] arguments = new String[2];
        arguments[0] = "...";
        arguments[1] = "...";
        while (!correct_response) {
            System.out.println("-- Enter the Group Name.");
            System.out.print("> ");
            in.nextLine();
            try {
                arguments[0] = in.nextLine();
                System.out.println("-- Enter the Book Title.");
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

    /**
     * Takes in arguments to delete a particular book in the connected SQL DB.
     * @param conn The Connection to the SQl DB.
     * @param arguments User-specified arguments to be set in SQL statement.
     * @return Whether or not the method was successful.
     */
    public static boolean DeleteBook(Connection conn, String[] arguments) {
        boolean statement = false;
        boolean setStatement = false;
        boolean executeStatement = false;
        boolean deletion = false;
        String groupName = arguments[0];
        String bookTitle = arguments[1];
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

    /**
     * Takes in user input to insert a particular publisher.
     * @param in The global System.in Scanner
     * @return The user's arguments.
     */
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

    /**
     * Takes in arguments to insert a particular publisher in the connected SQL DB.
     * @param conn The Connection to the SQl DB.
     * @param arguments User-specified arguments to be set in SQL statement.
     * @return Whether or not the method was successful.
     */
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
                System.out.println("-- ERROR: Inserting the Publisher Failed!");
            } else {
                System.out.println("-- ERROR: Unknown Error!");
            }
        }
        return insertion;
    }

    /**
     * Takes in user input to insert a particular book.
     * @param in The global System.in Scanner
     * @return The user's arguments.
     */
    public static String[] InsertBookUserInterface(Scanner in) {
        boolean correct_response = false;
        String[] arguments = new String[5];
        for (int i = 0; i < 5; i++) {
            arguments[i] = "...";
        }
        while (!correct_response) {
            System.out.println("-- Enter the Group Name.");
            System.out.print("> ");
            in.nextLine();
            try {
                arguments[0] = in.nextLine();
                System.out.println("-- Enter the Book Title.");
                System.out.print("> ");
                arguments[1] = in.nextLine();
                System.out.println("-- Enter the Publisher Name.");
                System.out.print("> ");
                arguments[2] = in.nextLine();
                boolean validYear = false;
                while (!validYear) {
                    System.out.println("-- Enter the Year Published.");
                    System.out.print("> ");
                    if (in.hasNextInt()) {
                        Integer user_response = in.nextInt();
                        if (user_response >= 1983) {
                            arguments[3] = user_response.toString();
                            validYear = true;
                        } else {
                            System.out.println("-- ERROR: Please enter a valid year. Try again.");
                        }
                    } else {
                        System.out.println("-- ERROR: Please enter an integer response. Try again.");
                        in.next();
                    }
                }
                boolean intPageNum = false;
                while (!intPageNum) {
                    System.out.println("-- Enter the Number of Pages.");
                    System.out.print("> ");
                    if (in.hasNextInt()) {
                        Integer user_response = in.nextInt();
                        if (user_response > 0) {
                            arguments[4] = user_response.toString();
                            intPageNum = true;
                        } else {
                            System.out.println("-- ERROR: Please enter a page number greater than 0. Try again.");
                        }
                    } else {
                        System.out.println("-- ERROR: Please enter an integer response. Try again.");
                        in.next();
                    }
                }
                correct_response = true;
            } catch (InputMismatchException input) {
                // This Exception should never be raised (in theory).
                input.printStackTrace();
            } catch (NoSuchElementException e) {
                System.out.println("-- ERROR: No line was found!");
            } catch (IllegalStateException state) {
                System.out.println("-- ERROR: Scanner is closed!");
                return arguments;
            }
        }
        return arguments;
    }

    /**
     * Takes in arguments to insert a particular book in the connected SQL DB.
     * @param conn The Connection to the SQl DB.
     * @param arguments User-specified arguments to be set in SQL statement.
     * @return Whether or not the method was successful.
     */
    public static boolean NewBook(Connection conn, String[] arguments) {
        boolean statement = false;
        boolean setStatement = false;
        boolean executeStatement = false;
        boolean insertion = false;
        String writingGroup = arguments[0];
        String bookTitle = arguments[1];
        String publisherName = arguments[2];
        String yearPublished = arguments[3];
        String numPages = arguments[4];
        try {
            PreparedStatement insertStatement = conn.prepareStatement(insertBook);
            statement = true;
            insertStatement.setString(1, writingGroup);
            insertStatement.setString(2, bookTitle);
            insertStatement.setString(3, publisherName);
            insertStatement.setString(4, yearPublished);
            insertStatement.setString(5, numPages);
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

    /**
     * Takes in arguments to update books in the connected SQL DB.
     * @param conn The Connection to the SQl DB.
     * @param arguments User-specified arguments to be set in SQL statement.
     * @return Whether or not the method was successful.
     */
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
                    System.out.println("-- ERROR: Updating Books Failed!");
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
        // Constructing the database URL connection string
        DB_URL = DB_URL + DBNAME + ";user=" + USER + ";password=" + PASS;
        // Initialize the connection
        Connection conn = null;
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
                    ListAllWritingGroups(conn);
                    break;
                case 2:
                    ListWritingGroupSpecified(conn, in);
                    break;
                case 3:
                    ListAllPublishers(conn);
                    break;
                case 4:
                    ListPublisherSpecified(conn, in);
                    break;
                case 5:
                    ListAllBooks(conn);
                    break;
                case 6:
                    ListBookSpecified(conn, in);
                    break;
                case 7:
                    arguments = InsertBookUserInterface(in);
                    boolean insertion = NewBook(conn, arguments);
                    if (insertion) {
                        System.out.println("-- Successfully inserted \"" + arguments[1] + "\".");
                    } else {
                        System.out.println("-- Unable to insert \"" + arguments[1] + "\" into Books table.");
                    }
                    break;
                case 8:
                    arguments = NewPublisherUserInterface(in);
                    boolean update = UpdateBooks(conn, arguments);
                    if (update) {
                        System.out.println(
                                "-- Successfully updated books from \"" + arguments[4] + "\" to " + arguments[0] + ".");
                    } else {
                        System.out.println(
                                "-- Unable to update books from \"" + arguments[4] + "\" to " + arguments[0] + ".");
                    }
                    break;
                case 9:
                    arguments = DeleteBookUserInterface(in);
                    boolean deletion = DeleteBook(conn, arguments);
                    if (deletion) {
                        System.out.println("-- Successfully deleted \"" + arguments[1] + "\" by " + arguments[0] + ".");
                    } else {
                        System.out.println("-- Unable to delete \"" + arguments[1] + "\" by " + arguments[0] + ".");
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
        } catch (NoSuchElementException e) {
            System.out.println("-- ERROR: No line was found!");
        } catch (IllegalStateException state) {
            System.out.println("-- ERROR: Scanner is closed!");
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
}// end JDBC
