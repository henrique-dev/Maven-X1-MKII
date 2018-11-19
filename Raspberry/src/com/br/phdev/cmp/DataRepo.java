package com.br.phdev.cmp;

import com.br.phdev.driver.Module;
import com.br.phdev.driver.PCA9685;
import com.br.phdev.exceptions.MavenDataException;
import com.br.phdev.jdbc.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataRepo {

    public List<Module> loadModulesData() throws MavenDataException {
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
            throw new MavenDataException("Falha ao carregar as informações para os módulos.", e);
        }
        return moduleList;
    }

    public List<ServoData> loadServosData() throws MavenDataException {
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
                        rs.getFloat("min"),
                        rs.getFloat("mid"),
                        rs.getFloat("max"),
                        rs.getInt("limit_min"),
                        rs.getInt("limit_max"),
                        rs.getInt("degrees_opening")
                );
                servoDataList.add(servoData);
            }
            stmt.close();
        } catch (SQLException e) {
            throw new MavenDataException("Falha ao carregar as informações para os servos", e);
        }
        return servoDataList;
    }

    public void saveServoPosData(int globalChannel, String option, float pos) throws MavenDataException {
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
            case "limit-min":
                sql = "update servos_data set limit_min=" + (int)pos + " where global_channel=" + globalChannel;
                break;
            case "limit-max":
                sql = "update servos_data set limit_max=" + (int)pos + " where global_channel=" + globalChannel;
                break;
            case "opening":
                sql = "update servos_data set degrees_opening=" + (int)pos + " where global_channel=" + globalChannel;
                break;
            default:
                return;
        }
        try {
            PreparedStatement stmt = new ConnectionFactory().getConnection().prepareStatement(sql);
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new MavenDataException("Falha ao salvar os dados para os servos", e);
        }

    }

    public List<LegData> loadLegsData() throws MavenDataException {
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
        } catch (SQLException e) {
            throw new MavenDataException("Falha ao carregar as informações para as pernas", e);
        }
        return servoDataList;
    }


}
