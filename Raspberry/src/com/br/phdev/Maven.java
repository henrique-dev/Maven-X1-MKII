package com.br.phdev;

import com.br.phdev.driver.PCA9685;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Maven {
    
    public static void main(String[] args) {

    	String url = "jdbc:mariadb://localhost:5432/maven";
    	String user = "maven";
    	String password = "root";
    	
    	try {
    		Connection con = DriverManager.getConnection(url, user, password);
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
        
    }
}