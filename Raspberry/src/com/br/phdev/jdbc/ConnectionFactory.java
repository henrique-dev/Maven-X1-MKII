package com.br.phdev.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    public Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:../db_files/maven.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}