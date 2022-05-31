package NotesEngine;

import Database.DatabaseWorker;
import Database.Models.Note;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class NotesEngine {

    private final DatabaseWorker dbWorker;

    public NotesEngine(DatabaseWorker dbWorker) {

        this.dbWorker = dbWorker;
    }

    public void Start() {
        Scanner in = new Scanner(System.in);

        int userId = Login();

        if (userId == 0) {
            System.out.println("Goodbye! Have a nice day!");
            return;
        }

        // PROCESSING USER COMMANDS
        String userCommand = "enter"; // the command entered further by the current user at the Command entry point

        System.out.println("-> List of possible commands - 'help', type 'quit' to quit the program\n");

        while (!userCommand.equals("quit")) {
            System.out.println("\nEnter the command:");

            // Command entry point
            userCommand = in.nextLine();

            try {
                switch (userCommand) {
                    case "list":
                        List(userId);
                        break;
                    case "add":
                        Add(userId);
                        List(userId);
                        break;
                    case "help":
                        Help();
                        break;

                    default:
                        if (IsDeleteCommand(userCommand)) {
                            Delete(userCommand, userId);
                            List(userId);
                            break;
                        }

                        if(!userCommand.equals("quit")) {
                            System.out.println("Not available command.\n" +
                                    "Use 'help' to see the list of possible commands, " +
                                    "type 'quit' to quit the program");
                            break;
                        }
                }
            } catch (Exception ex) {
                System.out.println("Incorrect command input: " + userCommand);
            }
        }
        System.out.println("\nLogging out of the system...");
    }

    private int Login() {
        int userId = 0;
        Scanner in = new Scanner(System.in);

        while (userId == 0) {
            System.out.println("Enter your name case-sensitive or type 'quit' to quit the program:");
            String userName = in.nextLine();
            if (userName.equals("quit"))
                break;

            if (CheckIfNameIsEmpty(userName))
                continue;

            // checking the existence of a user in the database
            try {
                if (!dbWorker.UserExists(userName)) {
                    // creating a new user if none is found
                    while (true) {
                        System.out.println("The user with the name "
                                + userName + " is not registered. Create a new user?\n"
                                + "Available commands: yes, no");

                        String agreementCommand = in.nextLine();

                        if (agreementCommand.equals("yes")) {
                            dbWorker.CreateUser(userName);
                            userId = dbWorker.GetUserId(userName);

                            System.out.println("A new user has been created: " + userName);
                            System.out.println("You entered as " + userName + "\n");

                            break;
                        }
                        if (agreementCommand.equals("no")) {
                            break;
                        }
                    }
                } else {
                    userId = dbWorker.GetUserId(userName);
                    break;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        return userId;
    }

    private void List(int userId) throws SQLException {
        List<Note> notes = dbWorker.GetListOfNotes(userId);

        System.out.println("\nYour list of notes:");

        for (Note note : notes) {
            System.out.println("# " + note.id + " - " + note.note);
        }

        if (notes.size() == 0) {
            System.out.println("EMPTY!\n");
        }
    }

    private void Add(int userId) throws SQLException {
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.println("Type a note:");
            String noteText = in.nextLine();

            if (noteText.isEmpty()) {
                System.out.println("You didn't type anything!");
            } else {
                dbWorker.AddNote(userId, noteText);
                System.out.println("New note was added");
                break;
            }
        }
    }

    private void Delete(String userCommand, int userId) throws Exception {
        String[] deleteString = userCommand.split(" ");

        if (deleteString.length < 2)
            throw new Exception("No number entered");

        dbWorker.DeleteNote(Integer.parseInt(deleteString[1]), userId); // check deletion of
    }

    private void Help() {
        System.out.println("\nAvailable commands:\n" +
                "list, add, delete #, quit \n" +
                "Example: enter 'delete 7' to delete note number 7.\n");
    }

    private boolean IsDeleteCommand(String command) {
        String[] str = command.split(" ");
        return str[0].equals("delete");
    }

    private boolean CheckIfNameIsEmpty(String userName) {
        if (userName.isEmpty()) {
            System.out.println("You didn't enter a name.\n");
            return true;
        }
        return false;
    }
}
