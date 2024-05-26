package com.example.lostescape.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {
    public static final String URL = "jdbc:mysql://localhost:3306/";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "";
    public static Connection getConnection(String databaseName) {
        Connection c = null;
        try{
            c = DriverManager.getConnection(URL + databaseName, USERNAME, PASSWORD);
            System.out.println("Database connected successfully.");
        }catch(SQLException e){
            e.printStackTrace();
        }
        return c;
    }
}