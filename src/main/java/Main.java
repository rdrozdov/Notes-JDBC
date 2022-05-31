import Database.DatabaseWorker;
import NotesEngine.NotesEngine;

public class Main {

    public static void main(String[] args) {
        // enter your database settings here!
        String url = "jdbc:mysql://localhost:3306/notesjdbc";
        String user = "";
        String password = "";

        DatabaseWorker dbWorker = new DatabaseWorker(url, user, password);
        NotesEngine engine = new NotesEngine(dbWorker);
        engine.Start();
    }
}
