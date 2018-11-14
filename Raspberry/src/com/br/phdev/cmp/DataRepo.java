package com.br.phdev.cmp;

import com.br.phdev.driver.Module;
import com.br.phdev.driver.PCA9685;
import com.br.phdev.jdbc.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataRepo {

    public List<Module> loadModulesData() {
        System.out.println("Carregando informações para os módulos...");
        List<Module> moduleList = null;
        try {
            Connection connection = new ConnectionFactory().getConnection();
            String sql = "select * from modules_data";
            PreparedStatement stmt = connection.prepareStatement(sql);
            moduleList = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                moduleList.add(new PCA9685(rs.getString("mod_address")));
            }
        } catch (SQLException e) {
            System.out.println("Falha ao carregar as informações para os módulos. " + e.getMessage());
            e.printStackTrace();
        }
        if (moduleList == null)
            System.out.println("ETA CUZA1");
        return moduleList;
    }

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
                        rs.getString("mod_address"),
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
        if (servoDataList == null)
            System.out.println("ETA PORRA");
        return servoDataList;
    }

    public void saveServoPosData(int globalChannel, String option, int pos) {
        String sql;
        switch (option) {
            case "min":
                sql = "update servos_data set min=" + pos + " where global_channel=" + globalChannel;
                break;
            case "mid":
                sql = "update servos_data set mid=" + pos + " where global_channel=" + globalChannel;
                break;
            case "max":
                sql = "update servos_data set max=" + pos + " where global_channel=" + globalChannel;
                break;
            default:
                return;
        }
        try {
            PreparedStatement stmt = new ConnectionFactory().getConnection().prepareStatement(sql);
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
        if (servoDataList == null)
            System.out.println("ETA CARA");
        return servoDataList;
    }


}
