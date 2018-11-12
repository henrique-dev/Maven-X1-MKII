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

	private void initLegs() {
		try {
			DataRepo dataRepo = new DataRepo();
			List<ServoData> servoDataList = dataRepo.loadServosData();
			List<LegData> legDataList = dataRepo.loadLegsData();
			this.legs = new Leg[6];
			this.servos = new Servo[18];

			System.out.println("Definindo os dados para todos os componentes...");
			for (int i=0; i<legDataList.size(); i++) {
				Base base = null;
				Femur femur = null;
				Tarsus tarsus = null;
				for (ServoData servoData : servoDataList) {
					if (legDataList.get(i).getBaseServo() == servoData.getGlobalChannel()) {
						this.servos[servoData.getGlobalChannel()] = new Servo(servoData.getModule() == '1' ? module1 : module2, servoData, 0);
						base = new Base(
								this.servos[servoData.getGlobalChannel()]
						);
						System.out.println("Servo da base da perna " + i + " carregado.");
					}
					if (legDataList.get(i).getFemurServo() == servoData.getGlobalChannel()) {
						this.servos[servoData.getGlobalChannel()] = new Servo(servoData.getModule() == '1' ? module1 : module2, servoData, 0);
						femur = new Femur(
								this.servos[servoData.getGlobalChannel()]
						);
						System.out.println("Servo do femur da perna " + i + " carregado.");
					}
					if (legDataList.get(i).getTarsusServo() == servoData.getGlobalChannel()) {
						this.servos[servoData.getGlobalChannel()] = new Servo(servoData.getModule() == '1' ? module1 : module2, servoData, 0);
						tarsus = new Tarsus(
								this.servos[servoData.getGlobalChannel()]
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

	private void moveAllServosToMidPos() {
		for (int i=0; i<18; i++) {
			this.servos[i].setRawPosition(375);
		}
	}


	public static void main(String[] args) {
		try {
			Maven maven = new Maven();
			maven.initComponents();

			Scanner in = new Scanner(System.in);

			System.out.println("\nEM FUNCIONAMENTO PARCIAL\n\n");

			while (true) {

				String currentPath = "";
				System.out.print(currentPath + "> ");
				String command = in.nextLine();
				switch (command) {
					case "configure-legs":
						break;
					case "configure-servos": {
						boolean runningAllServosConfig = true;
						while (runningAllServosConfig) {
							currentPath = "configure-servos ";
							System.out.print(currentPath + "> ");
							String parameter = in.nextLine();
							switch (parameter) {
								case "exit":
									runningAllServosConfig = false;
									break;
								default: {
									try {
										int globalChannel = Integer.parseInt(parameter.substring(6));
										if (globalChannel >= 0 && globalChannel <= 17) {
											boolean runningServoConfig = true;
											while (runningServoConfig) {
												currentPath = "configure-servos (servo " + globalChannel + ") ";
												System.out.print(currentPath + "> ");
												parameter = in.nextLine();
												switch (parameter.trim()) {
													case "min":
													case "mid":
													case "max":
														String currentServoConfigName = parameter.trim();
														boolean runningServoPosConfig = true;
														int servoPos = -1;
														while (runningServoPosConfig) {
															currentPath = "configure-servos (servo " + globalChannel + " - " + currentServoConfigName + ") ";
															System.out.print(currentPath + "> ");
															try {
																parameter = in.nextLine();
																switch (parameter.trim()) {
																	case "save":
																		if (servoPos != -1) {
																			DataRepo dataRepo = new DataRepo();
																			dataRepo.saveServoPosData(globalChannel, currentServoConfigName, servoPos);
																			runningServoPosConfig = false;
																		} else
																			System.out.println(currentPath + "> Os dados não foram alterados. ");
																		break;
																	case "exit":
																		runningServoPosConfig = false;
																		break;
																	default:
																		try {
																			servoPos = Integer.parseInt(in.nextLine());
																			if (servoPos >= 150 && servoPos <= 600 || servoPos == 0)
																				maven.getServos()[globalChannel].setRawPosition(servoPos);
																			else
																				servoPos = -1;
																			break;
																		} catch (Exception e) {
																			System.out.println(currentPath + "> Erro. Os valaores estão corretos?");
																		}
																}
															} catch (Exception e) {
																System.out.println(currentPath + "> Erro. Os valaores estão corretos?");
															}
															break;
														}
														break;
													case "general-values":
														currentPath = "configure-servos (servo " + globalChannel + " - general values) ";
														System.out.println(currentPath + "> ");
														break;
													case "exit":
														runningServoConfig = false;
														break;
													default:
														System.out.println(currentPath + "> Comando inválido");
														break;
												}
											}
										} else {
											System.out.println(currentPath + "> Canal global fora do range");
										}
									} catch (Exception e) {
										System.out.println(currentPath + "> Erro. Os valaores estão corretos?");
									}
									break;
								}
							}
						}
						break;
					}
					case "reset-signal":
						maven.resetAllServosPos();
						break;
					case "move-tomid":
						maven.moveAllServosToMidPos();
						break;
				}
/*
				servoChannel = in.nextInt();
				if (servoChannel == -1) {
					maven.resetAllServosPos();
					continue;
				}
				//System.out.println("Informe a posicao para o servo: ");
				servoPos = in.nextInt();

				if (servoChannel >= 0 && servoChannel < 16) {
					System.out.println("Movendo para " + servoPos);
					if (servoPos >= 150 && servoPos <= 600)
						maven.getServos()[servoChannel].setRawPosition(servoPos);
					else if (servoPos == 0)
						maven.getServos()[servoChannel].setRawPosition(0);
					else if (servoPos == -1)
						maven.getServos()[servoChannel].moveToMid();
				}*/
			}

		} catch (I2CFactory.UnsupportedBusNumberException e) {
			e.printStackTrace();
		}
	}
}
