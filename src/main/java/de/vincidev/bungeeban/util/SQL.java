package de.vincidev.bungeeban.util;

import java.sql.*;

public class SQL {

    private boolean mysql = false;

    private String filename;

    private String host;
    private int port;
    private String username;
    private String password;
    private String database;

    private Connection conn;

    public SQL(String filename) {
        this.mysql = false;
        this.filename = filename;
    }

    public SQL(String host, int port, String username, String password, String database) {
        this.mysql = true;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    public boolean isConnected() {
        return this.conn != null;
    }

    public void openConnection() {
        if (!isConnected()) {
            try {
                if (!this.mysql) {
                    Class.forName("org.sqlite.JDBC");
                    this.conn = DriverManager.getConnection("jdbc:sqlite:" + this.filename);
                } else {
                    this.conn = java.sql.DriverManager.getConnection(
                            "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true",
                            this.username, this.password);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void closeConnection() {
        if (isConnected()) {
            try {
                this.conn.close();
                this.conn = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void update(final String query) {
        if (isConnected()) {
            try {
                PreparedStatement pst = conn.prepareStatement(query);
                pst.executeUpdate();
                pst.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public ResultSet getResult(String query) {
        if (isConnected()) {
            try {
                PreparedStatement pst = conn.prepareStatement(query);
                return pst.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void createTable(String tablename, String options) {
        update("CREATE TABLE " + tablename + "(" + options + ")");
    }

    public void createTableIfNotExists(String tablename, String options) {
        update("CREATE TABLE IF NOT EXISTS " + tablename + "(" + options + ")");
    }

    public void truncateTable(String tablename) {
        update("TRUNCATE " + tablename);
    }

    public int getTableRowAmount(String tablename) {
        int amount = 0;
        ResultSet rs = getResult("SELECT COUNT(*) FROM " + tablename);
        try {
            if (rs.next()) {
                amount = rs.getInt(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return amount;
    }

    public Connection getConn() {
        return conn;
    }
}
