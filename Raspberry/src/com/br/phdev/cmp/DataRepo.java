package com.br.phdev.cmp;

import com.br.phdev.jdbc.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataRepo {

    public List<ServoData> loadServosData() {
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
            stmt.close();
            System.out.println("Carregamento completo");
        } catch (SQLException e) {
            System.out.println("Falha ao carregar as informações para os servos. " + e.getMessage());
            e.printStackTrace();
        }
        return servoDataList;
    }

    public void saveServoPosData(int globalChannel, String option, int pos) {
        String sql;
        switch (option) {
            case "min":
                sql = "update table servos_data set min=" + pos + " where global_channel=" + globalChannel;
                break;
            case "mid":
                sql = "update table servos_data set mid=" + pos + " where global_channel=" + globalChannel;
                break;
            case "max":
                sql = "update table servos_data set max=" + pos + " where global_channel=" + globalChannel;
                break;
            default:
                return;
        }
        try {
            PreparedStatement stmt = new ConnectionFactory().getConnection().prepareCall(sql);
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<LegData> loadLegsData() {
        System.out.println("Carregando informações para as pernas...");
        List<LegData> servoDataList = null;
        try {
            Connection connection = new ConnectionFactory().getConnection();
            String sql = "select * from legs_data";
            PreparedStatement stmt = connection.prepareStatement(sql);
            servoDataList = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LegData legData = new LegData(
                        rs.getInt("leg_number"),
                        rs.getInt("base_servo"),
                        rs.getInt("femur_servo"),
                        rs.getInt("tarsus_servo")
                );
                servoDataList.add(legData);
            }
            System.out.println("Carregamento completo");
        } catch (SQLException e) {
            System.out.println("Falha ao carregar as informações para as pernas " + e.getMessage());
            e.printStackTrace();
        }
        return servoDataList;
    }


}
