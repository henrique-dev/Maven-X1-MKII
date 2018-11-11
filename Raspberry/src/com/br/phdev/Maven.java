package com.br.phdev;

import com.br.phdev.cmp.LegData;
import com.br.phdev.cmp.ServoData;
import com.br.phdev.cmp.DataRepo;

import java.util.List;


public class Maven {
    
    public static void main(String[] args) {

    	try {
			DataRepo dataRepo = new DataRepo();
			List<ServoData> servoDataList = dataRepo.loadServosData();
			for (ServoData servoData : servoDataList) {
				System.out.println(servoData.toString());
			}

			List<LegData> legDataList = dataRepo.loadLegsData();
			for (LegData legData : legDataList) {
				System.out.println(legData.toString());
			}
    	} catch (Exception e) {
    		System.err.println( e.getClass().getName() + ": " + e.getMessage() );    		
    	}
        
	}
}
