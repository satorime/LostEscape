package com.example.lostescape.Server;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {
    private static DatabaseManager instance;
    private DatabaseManager() {}

    public static DatabaseManager getInstance(){
        if(instance == null){
            instance = new DatabaseManager();
        }
        return instance;
    }

    public boolean initializeDB() throws SQLException {
        Statement statement = null;
        try(Connection connection = MySQLConnection.getConnection("")){
            statement = connection.createStatement();
            String createDBQuery  = "CREATE DATABASE IF NOT EXISTS dblost;";
            statement.executeUpdate(createDBQuery);

            connection.setCatalog("dblost"); // Toggle to the created DB
            connection.setAutoCommit(false);
            statement = connection.createStatement();

            String createUserTableQuery = "CREATE TABLE IF NOT EXISTS user (" +
                    "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(100) NOT NULL, " +
                    "password VARCHAR(100) NOT NULL)";

            statement.executeUpdate(createUserTableQuery);

            // New high_score table
            String createHighScoreTableQuery = "CREATE TABLE IF NOT EXISTS high_score (" +
                    "high_score_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT NOT NULL, " +
                    "time_taken DOUBLE NOT NULL, " +
                    "FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE)";

            statement.executeUpdate(createHighScoreTableQuery);

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return true;
    }

    public Status createUser(String username, String password) {
        try(Connection c = MySQLConnection.getConnection("dblost");
            PreparedStatement statement = c.prepareStatement("INSERT INTO user(username, password) VALUES(?, ?)")) {

            statement.setString(1, username);
            statement.setString(2, password);

            int res = statement.executeUpdate();
            if(res == 0){
                return Status.ACCOUNT_CREATION_FAILED;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Status.ACCOUNT_CREATION_FAILED;
        }
        return Status.ACCOUNT_CREATED_SUCCESSFULLY;
    }

    public Status deleteUser(int userID) {
        try(Connection c = MySQLConnection.getConnection("dblost");
            PreparedStatement statement = c.prepareStatement("DELETE FROM user WHERE user_id = ?")) {

            statement.setInt(1, userID);
            int res = statement.executeUpdate();

            if(res == 0){
                return Status.ACCOUNT_DELETION_FAILED;
            }

            // Reset current user
            CurrentUser.userID = -1;
            CurrentUser.username = "";

        } catch(SQLException e) {
            e.printStackTrace();
            return Status.ACCOUNT_DELETION_FAILED;
        }
        return Status.ACCOUNT_DELETED_SUCCESSFULLY;
    }

    public Status updateUser(String field, String value, int userID) {
        try(Connection c = MySQLConnection.getConnection("dblost");
            PreparedStatement statement = c.prepareStatement("UPDATE user SET " + field + " = ? WHERE user_id = ?")) {

            statement.setString(1, value);
            statement.setInt(2, userID);

            int res = statement.executeUpdate();

            if(res == 0){
                return Status.ACCOUNT_UPDATE_FAILED;
            }

            PreparedStatement getUserStatement = c.prepareStatement("SELECT * FROM user WHERE user_id = ?");
            getUserStatement.setInt(1, userID);

            ResultSet userData = getUserStatement.executeQuery();

            if(userData.next()){
                CurrentUser.username = userData.getString("username");
                CurrentUser.userID = userData.getInt("user_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Status.ACCOUNT_UPDATE_FAILED;
        }
        return Status.ACCOUNT_UPDATED_SUCCESSFULLY;
    }

    public Status validate(String username, String password) {
        try(Connection c = MySQLConnection.getConnection("dblost");
            PreparedStatement statement = c.prepareStatement("SELECT * FROM user where username = ?")) {

            statement.setString(1, username);
            ResultSet res = statement.executeQuery();

            if(!res.next()) return Status.USERNAME_NOT_FOUND;

            String passwordFromDB = res.getString("password");
            if(!passwordFromDB.equals(password)) return Status.INCORRECT_PASSWORD;

            // Setting the current user
            CurrentUser.userID = res.getInt("user_id");
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return Status.LOGIN_SUCCESS;
    }

    // New method to add high scores
    public Status addHighScore(int userID, double timeTaken) {
        try(Connection c = MySQLConnection.getConnection("dblost");
            PreparedStatement statement = c.prepareStatement("INSERT INTO high_score(user_id, time_taken) VALUES(?, ?)")) {

            statement.setInt(1, userID);
            statement.setDouble(2, timeTaken);

            int res = statement.executeUpdate();

            if(res == 0){
                return Status.HIGH_SCORE_ADDITION_FAILED;
            }

        } catch(SQLException e) {
            e.printStackTrace();
            return Status.HIGH_SCORE_ADDITION_FAILED;
        }
        return Status.HIGH_SCORE_ADDED_SUCCESSFULLY;
    }

    // New method to get high scores
    public List<Map<String, String>> getHighScores() {
        try(Connection c = MySQLConnection.getConnection("dblost");
            PreparedStatement statement = c.prepareStatement(
                    "SELECT hs.high_score_id, u.username, hs.time_taken " +
                            "FROM high_score hs " +
                            "JOIN user u ON hs.user_id = u.user_id " +
                            "ORDER BY hs.time_taken ASC")) {

            ResultSet res = statement.executeQuery();
            List<Map<String, String>> highScores = new ArrayList<>();

            while(res.next()){
                Map<String, String> temp = new HashMap<>();
                temp.put("high_score_id", res.getString("high_score_id"));
                temp.put("username", res.getString("username"));
                temp.put("time_taken", res.getString("time_taken"));
                highScores.add(temp);
            }
            return highScores;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Status updatePassword(int userID, String oldPassword, String newPassword) {
        try(Connection c = MySQLConnection.getConnection("dblost");
            PreparedStatement validateStatement = c.prepareStatement("SELECT * FROM user WHERE user_id = ?")) {

            validateStatement.setInt(1, userID);
            ResultSet res = validateStatement.executeQuery();

            if (!res.next()) return Status.USERNAME_NOT_FOUND;

            String passwordFromDB = res.getString("password");
            if (!passwordFromDB.equals(oldPassword)) return Status.INCORRECT_PASSWORD;

            PreparedStatement updateStatement = c.prepareStatement("UPDATE user SET password = ? WHERE user_id = ?");
            updateStatement.setString(1, newPassword);
            updateStatement.setInt(2, userID);

            int updateRes = updateStatement.executeUpdate();
            if (updateRes == 0) return Status.ACCOUNT_UPDATE_FAILED;

        } catch (SQLException e) {
            e.printStackTrace();
            return Status.ACCOUNT_UPDATE_FAILED;
        }
        return Status.ACCOUNT_UPDATED_SUCCESSFULLY;
    }
}
