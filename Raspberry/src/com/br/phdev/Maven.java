package com.br.phdev;

import com.br.phdev.cmp.*;
import com.br.phdev.driver.PCA9685;
import com.pi4j.io.i2c.I2CFactory;

import java.util.List;
import java.util.Scanner;


public class Maven {

	private PCA9685 module1;
	private PCA9685 module2;

	private Leg[] legs;
	private Servo[] servos;

	private void initComponents() throws I2CFactory.UnsupportedBusNumberException {
		this.module1 = new PCA9685(0x40);
		this.module2 = new PCA9685(0x41);
		this.initLegs();
	}

	public void initLegs() {
		try {
			DataRepo dataRepo = new DataRepo();
			List<ServoData> servoDataList = dataRepo.loadServosData();
			List<LegData> legDataList = dataRepo.loadLegsData();
			legs = new Leg[6];
			servos = new Servo[18];

			System.out.println("Definindo os dados para todos os componentes...");
			for (int i=0; i<legDataList.size(); i++) {
				Base base = null;
				Femur femur = null;
				Tarsus tarsus = null;
				for (ServoData servoData : servoDataList) {
					if (legDataList.get(i).getBaseServo() == servoData.getGlobalChannel()) {
						this.servos[servoData.getGlobalChannel()] = new Servo(servoData.getModule() == '1' ? module1 : module2, servoData, 0);
						base = new Base(
								servos[servoData.getGlobalChannel()]
						);
						System.out.println("Servo da base da perna " + i + " carregado.");
					}
					if (legDataList.get(i).getFemurServo() == servoData.getGlobalChannel()) {
						this.servos[servoData.getGlobalChannel()] = new Servo(servoData.getModule() == '1' ? module1 : module2, servoData, 0);
						femur = new Femur(
								servos[servoData.getGlobalChannel()]
						);
						System.out.println("Servo do femur da perna " + i + " carregado.");
					}
					if (legDataList.get(i).getTarsusServo() == servoData.getGlobalChannel()) {
						this.servos[servoData.getGlobalChannel()] = new Servo(servoData.getModule() == '1' ? module1 : module2, servoData, 0);
						tarsus = new Tarsus(
								servos[servoData.getGlobalChannel()]
						);
						System.out.println("Servo do tarso da perna " + i + " carregado.");
					}
					legs[i] = new Leg(legDataList.get(i), base, femur, tarsus);
				}
				if (base == null || femur == null || tarsus == null)
					throw new RuntimeException("Falha ao inicializar as pernas");
			}
			System.out.println("Dados de todos os componentes definidos com sucesso :D");
		} catch (Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}

	private Servo[] getServos() {
		return servos;
	}

	private void resetAllServosPos() {
		for (int i=0; i<18; i++) {
			this.servos[i].setPosition(0);
		}
	}

	public static void main(String[] args) {
		try {
			Maven maven = new Maven();
			maven.initComponents();

			Scanner entrada = new Scanner(System.in);

			int servoChannel = 0;
			int servoPos = 0;

			while (true) {

				System.out.println("EM FUNCIONAMENTO PARCIAL\n\n");
				System.out.println("Escolha a opção desesajada: ");
				System.out.println("1 - Configuração das pernas");
				System.out.println("2 - Configuração dos servos");
				System.out.println("3 - Redefinir sinal de todos os servos");
				servoChannel = entrada.nextInt();
				if (servoChannel == -1) {
					maven.resetAllServosPos();
					continue;
				}
				System.out.println("Informe a posicao para o servo: ");
				servoPos = entrada.nextInt();

				if (servoChannel >= 0 && servoChannel < 16) {
					System.out.println("Movendo para " + servoPos);
					if (servoPos >= 150 && servoPos <= 600)
						maven.getServos()[servoChannel].setRawPosition(servoPos);
					else if (servoPos == 0)
						maven.getServos()[servoChannel].setRawPosition(0);
					else if (servoPos == -1)
						maven.getServos()[servoChannel].moveToMid();
				}
			}

		} catch (I2CFactory.UnsupportedBusNumberException e) {
			e.printStackTrace();
		}
	}
}
