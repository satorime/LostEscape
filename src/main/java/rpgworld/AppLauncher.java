package rpgworld;

import rpgworld.Server.DatabaseManager;

import java.sql.SQLException;

public class AppLauncher {
    public static void main(String[] args) throws SQLException {
        DatabaseManager.getInstance().initializeDB();
        World.main(args);
    }
}