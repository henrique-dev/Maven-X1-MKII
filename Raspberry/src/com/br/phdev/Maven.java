package com.br.phdev;

import com.br.phdev.driver.PCA9685;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Maven {
    
    public static void main(String[] args) {

    	try {
    		Class.forName("org.sqlite.JDBC");
    		Connection con = DriverManager.getConnection("jdbc:sqlite:maven.db");
    		System.out.println("Conexão estabelecida");
    	} catch (Exception e) {
    		System.err.println( e.getClass().getName() + ": " + e.getMessage() );    		
    	}
        
    }
}
