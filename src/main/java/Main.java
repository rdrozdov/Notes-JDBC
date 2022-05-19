import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class Main {

    public static void main(String[] args)
    {
        // enter your database settings here!
        String url = "jdbc:mysql://localhost:3306/notesjdbc";
        String user = "";
        String pass = "";

        try {
            Connection connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();

            // LOG IN

            int enter = 0;      // cycle completion flag
            int userId = 0;     // ID of current user
            String userName;    // name of current user

            Scanner in = new Scanner(System.in);

            while (enter != 1) {
                System.out.println("Enter your name case-sensitive:");
                userName = in.nextLine();

                // checking for empty input
                if (userName.isEmpty()) {
                    System.out.println("You didn't enter a name.\n");
                    continue;
                }

                // checking the existence of a user in the database
                ResultSet checkName = statement.executeQuery("select name from users");
                while (checkName.next()) {
                    String name = checkName.getString("name");
                    if (userName.equals(name))
                    {
                        // setting the ID of the found user to work with the database
                        ResultSet checkUserId = statement.executeQuery("select id from users where name = '"
                                + userName + "';");
                        checkUserId.next();
                        userId = checkUserId.getInt(1);
                        checkUserId.close();
                        System.out.println("You entered as " + userName);
                        enter = 1;
                        break;
                    }
                }
                checkName.close();

                // creating a new user if none is found
                while (enter != 1) {
                    System.out.println("The user with the name " + userName
                            + " is not registered. Create a new user?\n" +
                            "Available commands: yes, no");
                    String agreementCommand = in.nextLine();
                    if (agreementCommand.equals("yes")) {

                        // adding a new user to the database
                        statement.execute("insert into users (name) values('" + userName + "');");

                        // setting the ID of the new user to work with the database
                        ResultSet checkUserId = statement.executeQuery("select id from users where name = '"
                                + userName + "';");
                        checkUserId.next();
                        userId = checkUserId.getInt(1);
                        checkUserId.close();
                        System.out.println("A new user has been created: " + userName);
                        System.out.println("You entered as " + userName + "\n");
                        enter = 1;
                    }
                    if (agreementCommand.equals("no")) {
                        System.out.println();
                        break;
                    }
                }
            }

            // PROCESSING USER COMMANDS

            String userCommand = "enter"; // the command entered further by the current user at the Command entry point

            System.out.println("-> List of possible commands - help\n");
            while (!userCommand.equals("quit"))
            {
                System.out.println("Enter the command:");

                // Command entry point
                userCommand = in.nextLine();

                // [COMMAND] displaying a list of notes
                if (userCommand.equals("list")) {
                    int emptyString = 0;    // empty list flag
                    System.out.println("Your list of notes:");
                    ResultSet notesList = statement.executeQuery("select * from notes where users_id = " + userId);
                    while (notesList.next()) {
                        System.out.println(notesList.getString("id") + " - "
                                + notesList.getString("note"));
                        emptyString++;
                    }
                    System.out.println();
                    notesList.close();
                    if (emptyString == 0)
                    {
                        System.out.println("EMPTY!\n");
                    }
                    continue;
                }

                // [COMMAND] adding a new note
                if (userCommand.equals("add")) {
                    int addEnter = 0;   // cycle completion flag
                    while (addEnter != 1) {
                        System.out.println("Type a note:");
                        String noteText = in.nextLine();

                        // checking for empty input
                        if (noteText.isEmpty()) {
                                System.out.println("You didn't type anything!");
                        } else {

                            // adding a new note of the current user to the database
                            statement.execute("insert into notes (note, users_id) values ('"
                                    + noteText + "', " + userId + ")");
                            addEnter = 1;

                            // displaying the new note number
                            ResultSet numberOfNote = statement.executeQuery("select id from notes where note = '"
                                    + noteText + "' and users_id = " + userId + ";");
                            numberOfNote.next();
                            int currentNumberOfNote = numberOfNote.getInt("id");
                            numberOfNote.close();
                            System.out.println("New note added with the number " + currentNumberOfNote + "\n");
                        }
                    }
                    continue;
                }

                // [COMMAND] deleting a note
                String[] deleteString = userCommand.split(" "); // splitting the command to check the note number
                if (deleteString[0].equals("delete")) {
                    ResultSet checkList = statement.executeQuery("select * from notes where users_id = "
                            + userId + ";");

                    // checking for an empty note list of the current user
                    if (!checkList.next()) {
                        checkList.close();
                        System.out.println("The list is empty. There is nothing to delete.\n");
                        continue;
                    } else {
                        int doNumber = 0;   //  variable for comparing the entered note number

                        // checking the input of an empty note number
                        if (deleteString.length > 1) {
                            doNumber = Integer.parseInt(deleteString[1]);
                        }
                        int switcher = 0;   // non-existent note number flag
                        ResultSet notesList = statement.executeQuery("select * from notes where users_id = "
                                + userId + ";");
                        while (notesList.next())
                        {
                            int deleteNumber = notesList.getInt("id");

                            // deleting the requested note
                            if (deleteNumber == doNumber)
                            {
                                statement.execute("delete from notes where id = " + doNumber + ";");
                                System.out.println("Note with the number " + doNumber + " has been deleted.\n");
                                switcher = 1;
                                checkList.close();
                                break;
                            }
                        }
                        checkList.close();
                        if (switcher == 0) {
                            System.out.println("There is no such note number. Nothing has been deleted!\n");
                        }
                    }
                }

                // [COMMAND] log out
                if (userCommand.equals("quit")) {
                    System.out.println("\nLog out of the system...");
                    continue;
                }

                // [COMMAND] help
                if (userCommand.equals("help")) {
                    System.out.println("Available commands:\n" +
                            "list, add, delete #, quit \n" +
                            "Example: enter 'delete 7' to delete note number 7.\n");
                }
            }
            in.close();
            statement.close();
            connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
