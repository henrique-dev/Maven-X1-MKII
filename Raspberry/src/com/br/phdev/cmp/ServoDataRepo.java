package com.br.phdev.cmp;

import com.br.phdev.driver.PCA9685;
import com.br.phdev.jdbc.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServoDataRepo {

    public List<ServoData> loadData() {
        System.out.println("Carregando informações para os servos...");
        List<ServoData> servoDataList = null;
        try {
            Connection connection = new ConnectionFactory().getConnection();
            String sql = "select * from servos_data";
            PreparedStatement stmt = connection.prepareStatement(sql);
            servoDataList = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ServoData servoData = new ServoData(
                        rs.getString("mod_number").charAt(0),
                        rs.getInt("global_channel"),
                        rs.getInt("local_channel"),
                        rs.getInt("min"),
                        rs.getInt("mid"),
                        rs.getInt("max")
                );
                servoDataList.add(servoData);
            }
            System.out.println("Carregamento completo");
        } catch (SQLException e) {
            System.out.println("Falha ao carregas as informações para os servos. " + e.getMessage());
            e.printStackTrace();
        }
        return servoDataList;
    }

}
