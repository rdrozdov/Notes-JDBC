package Database;

import Database.Models.Note;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseWorker {
    private String url;
    private String user;
    private String password;

    public DatabaseWorker(String url, String user, String password) {

        this.url = url;
        this.user = user;
        this.password = password;
    }

    public boolean UserExists(String userName) throws SQLException {
        boolean result = false;

        Connection connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select id from users where name='" + userName + "'");

        if (resultSet.next()) {
            result = true;
        }

        resultSet.close();
        statement.close();
        connection.close();
        return result;
    }

    public void CreateUser(String userName) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement();

        statement.execute("insert into users (name) values('" + userName + "')");

        statement.close();
        connection.close();
    }

    public int GetUserId(String userName) throws SQLException {
        int result = 0;

        Connection connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement();

        // setting the ID of the new user to work with the database
        ResultSet checkUserId = statement.executeQuery("select id from users where name = '" + userName + "'");
        checkUserId.next();

        result = checkUserId.getInt(1);

        checkUserId.close();
        statement.close();
        connection.close();

        return result;
    }

    public List<Note> GetListOfNotes(int userId) throws SQLException {
        List<Note> result = new ArrayList<>();

        Connection connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement();
        ResultSet notesList = statement.executeQuery("select id, note from notes where users_id = " + userId);

        while (notesList.next()) {
            Note note = new Note();
            note.id = notesList.getInt("id");
            note.note = notesList.getString("note");

            result.add(note);
        }

        notesList.close();
        statement.close();
        connection.close();

        return result;
    }

    public void AddNote(int userId, String noteText) throws SQLException {

        Connection connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement();

        statement.execute("insert into notes (note, users_id) values ('"
                + noteText + "', " + userId + ")");

        statement.close();
        connection.close();
    }

    public void DeleteNote(int noteId, int userId) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        Statement statement = connection.createStatement();

        statement.execute("delete from notes where id=" + noteId + " and users_id=" + userId);
        System.out.println("Note # " + noteId + "has been deleted.");

        statement.close();
        connection.close();
    }
}
