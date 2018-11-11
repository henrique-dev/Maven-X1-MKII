package com.br.phdev;

import com.br.phdev.cmp.ServoData;
import com.br.phdev.cmp.ServoDataRepo;
import com.br.phdev.driver.PCA9685;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;


public class Maven {
    
    public static void main(String[] args) {

    	try {
			ServoDataRepo servoDataRepo = new ServoDataRepo();
			List<ServoData> servoDataList = servoDataRepo.loadData();
			for (ServoData servoData : servoDataList) {
				System.out.println(servoData.toString());
			}
    	} catch (Exception e) {
    		System.err.println( e.getClass().getName() + ": " + e.getMessage() );    		
    	}
        
	}
}
