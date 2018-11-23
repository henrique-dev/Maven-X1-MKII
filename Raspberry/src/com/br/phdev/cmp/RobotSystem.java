package com.br.phdev.cmp;

import com.br.phdev.cmp.servo.Servo;
import com.br.phdev.cmp.servo.ServoData;
import com.br.phdev.cmp.servo.ServoTaskController;
import com.br.phdev.driver.Module;
import com.br.phdev.driver.PCA9685;
import com.br.phdev.exceptions.MavenDataException;
import com.br.phdev.misc.Log;
import com.br.phdev.misc.Vector2D;
import com.pi4j.io.i2c.I2CFactory;

import java.util.List;

public class RobotSystem {

    private ServoTaskController servoTaskController;

    private List<Module> moduleList;
    private BodyData bodyData;
    private List<LegData> legDataList;
    private List<ServoData> servoDataList;

    private Body body;
    private Leg[] legs;
    private Servo[] servos;

    public void initSystem() throws I2CFactory.UnsupportedBusNumberException {
        if (this.loadData(true)) {
            for (Module module : moduleList) {
                module.init();
                if (module instanceof PCA9685)
                    ((PCA9685) module).setPWMFreq(60);
            }
            this.injectData();
        } else
            Log.e("Falha ao iniciar o sistema");
    }

    public boolean loadData(boolean loadModules) {
        DataRepo dataRepo = new DataRepo();
        try {
            if (loadModules) {
                Log.i("Carregando informações para os módulos...");
                this.moduleList = dataRepo.loadModulesData();
            }
            Log.i("Carregando informações para os servos...");
            this.servoDataList = dataRepo.loadServosData();
            Log.i("Carregando informações para as pernas...");
            this.legDataList = dataRepo.loadLegsData();
            Log.i("Carregando informações para o corpo...");
            this.bodyData = dataRepo.loadBodyData();
        } catch (MavenDataException e) {
            Log.e("Falha ao carregar as informações. " + e.getMessage());
            return false;
        }
        Log.s("Informações carregadas");
        return true;
    }

    public void injectData() {
        try {
            this.legs = new Leg[legDataList.size()];
            this.servos = new Servo[servoDataList.size()];

            Log.i("Definindo os dados para todos os componentes...");
            for (int i=0; i<legDataList.size(); i++) {
                Base base = null;
                Femur femur = null;
                Tarsus tarsus = null;

                for (ServoData servoData : servoDataList) {

                    if (legDataList.get(i).getBaseServo() == servoData.getGlobalChannel()) {

                        this.servos[servoData.getGlobalChannel()] = new Servo((PCA9685) Module.getModule(this.moduleList, servoData.getModuleAddress()), servoData, 0);
                        base = new Base(
                                this.servos[servoData.getGlobalChannel()]
                        );
                        Log.s("Servo da base da perna " + i + " carregado");
                    }
                    if (legDataList.get(i).getFemurServo() == servoData.getGlobalChannel()) {
                        this.servos[servoData.getGlobalChannel()] = new Servo((PCA9685) Module.getModule(this.moduleList, servoData.getModuleAddress()), servoData, 0);
                        femur = new Femur(
                                this.servos[servoData.getGlobalChannel()]
                        );
                        Log.s("Servo do femur da perna " + i + " carregado");
                    }
                    if (legDataList.get(i).getTarsusServo() == servoData.getGlobalChannel()) {
                        this.servos[servoData.getGlobalChannel()] = new Servo((PCA9685) Module.getModule(this.moduleList, servoData.getModuleAddress()), servoData, 0);
                        tarsus = new Tarsus(
                                this.servos[servoData.getGlobalChannel()]
                        );
                        Log.s("Servo do tarso da perna " + i + " carregado");
                    }
                    legs[i] = new Leg(legDataList.get(i), base, femur, tarsus);
                }
                if (base == null || femur == null || tarsus == null)
                    throw new RuntimeException("Falha ao inicializar as pernas");
            }
            this.body = new Body(legs, bodyData);
            Log.i("Dados de todos os componentes definidos com sucesso");
        } catch (Exception e) {
            Log.e("Falha ao inicializar as pernas. " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void injectVectors() {
        this.body.setWidth(new Vector2D(bodyData.getBodyWidth(), 0));
        this.body.setHeight(new Vector2D(0, -bodyData.getBodyHeight()));
        this.body.setLength(new Vector2D(0, bodyData.getBodyLength()));

        this.legs[Body.LEG_FRONT_LEFT].setOriginVector(new Vector2D(0, bodyData.getBodyLength()));

        this.legs[Body.LEG_FRONT_RIGHT].setOriginVector(new Vector2D(bodyData.getBodyWidth(), bodyData.getBodyLength()));


        this.legs[Body.LEG_MID_LEFT].setOriginVector(new Vector2D(0, 100.73));
        this.legs[Body.LEG_MID_RIGHT].setOriginVector(new Vector2D(bodyData.getBodyWidth(), 100.73));


        this.legs[Body.LEG_BACK_LEFT].setOriginVector(new Vector2D(0, 0));
        this.legs[Body.LEG_BACK_RIGHT].setOriginVector(new Vector2D(bodyData.getBodyWidth(), 0));
    }

    public void initServoTaskController() {
        Log.i("Iniciando controlador das pernas");
        this.servoTaskController = new ServoTaskController();
        this.servoTaskController.start();
        Log.s("Controlador das pernas iniciado");
        Log.s("Aguardando tarefas");
    }

    public void stopLegsControl() {
        if (this.servoTaskController != null)
            this.servoTaskController.stop();
    }

    public boolean findServo(int globalChannel) {
        for (Servo servo : this.servos) {
            if (globalChannel == servo.getServoData().getGlobalChannel())
                return true;
        }
        return false;
    }

    public ServoTaskController getServoTaskController() {
        return this.servoTaskController;
    }

    public List<Module> getModuleList() {
        return this.moduleList;
    }

    public Servo[] getServos() {
        return servos;
    }

    public Leg[] getLegs() {
        return this.legs;
    }

}
