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
        List<ServoData> servoDataList = null;
        try {
            Connection connection = new ConnectionFactory().getConnection();
            String sql = "select * from servos_data";
            PreparedStatement stmt = connection.prepareStatement(sql);
            servoDataList = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ServoData servoData = new ServoData(
                        (char)rs.getInt("mod_number"),
                        rs.getInt("global_channel"),
                        rs.getInt("local_channel"),
                        rs.getInt("min"),
                        rs.getInt("mid"),
                        rs.getInt("max")
                );
                servoDataList.add(servoData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return servoDataList;
    }

}
