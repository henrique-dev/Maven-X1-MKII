package com.br.phdev;

import com.br.phdev.cmp.*;
import com.br.phdev.driver.Module;
import com.br.phdev.driver.PCA9685;
import com.pi4j.io.i2c.I2CFactory;

import java.util.List;
import java.util.Scanner;


public class Maven {

	private List<Module> moduleList;
	private List<ServoData> servoDataList;
	private List<LegData> legDataList;

	private Leg[] legs;
	private Servo[] servos;

	private void initSystem() throws I2CFactory.UnsupportedBusNumberException {
		this.loadData();
		for (Module module : moduleList) {
			module.init();
			if (module instanceof PCA9685)
				((PCA9685) module).setPWMFreq(60);
		}
		this.initLegs();
	}

	private void loadData() {
		DataRepo dataRepo = new DataRepo();
		this.moduleList = dataRepo.loadModulesData();
		this.servoDataList = dataRepo.loadServosData();
		this.legDataList = dataRepo.loadLegsData();
	}

	private void initLegs() {
		try {
			this.legs = new Leg[6];
			this.servos = new Servo[18];

			System.out.println("Definindo os dados para todos os componentes...");
			for (int i=0; i<legDataList.size(); i++) {
				Base base = null;
				Femur femur = null;
				Tarsus tarsus = null;
				for (ServoData servoData : servoDataList) {
					if (legDataList.get(i).getBaseServo() == servoData.getGlobalChannel()) {

						this.servos[servoData.getGlobalChannel()] = new Servo(
								(PCA9685) Module.getModule(this.moduleList, this.servos[servoData.getGlobalChannel()].getServoData().getModuleAddress()), servoData, 0);
						base = new Base(
								this.servos[servoData.getGlobalChannel()]
						);
						System.out.println("Servo da base da perna " + i + " carregado.");
					}
					if (legDataList.get(i).getFemurServo() == servoData.getGlobalChannel()) {
						this.servos[servoData.getGlobalChannel()] = new Servo(
								(PCA9685) Module.getModule(this.moduleList, this.servos[servoData.getGlobalChannel()].getServoData().getModuleAddress()), servoData, 0);
						femur = new Femur(
								this.servos[servoData.getGlobalChannel()]
						);
						System.out.println("Servo do femur da perna " + i + " carregado.");
					}
					if (legDataList.get(i).getTarsusServo() == servoData.getGlobalChannel()) {
						this.servos[servoData.getGlobalChannel()] = new Servo(
								(PCA9685) Module.getModule(this.moduleList, this.servos[servoData.getGlobalChannel()].getServoData().getModuleAddress()), servoData, 0);
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
			e.printStackTrace();
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
			this.servos[i].moveToMid();
		}
	}

	private void moveAllBasesToMidPos() {
		for (int i=0; i<6; i++) {
			this.legs[i].getBase().moveToMid();
		}
	}


	public static void main(String[] args) {
		try {
			Maven maven = new Maven();

			Scanner in = new Scanner(System.in);

			boolean runningProgram = true;
			boolean initSystem = false;
			while (runningProgram) {

				String currentPath = "";
				System.out.print(currentPath + "> ");
				String command = in.nextLine();
				switch (command) {
					case "exit":
						runningProgram = false;
						break;
					case "init-system":
						maven.initSystem();
						initSystem = true;
						break;
					case "configure-legs":
						break;
					case "configure-servos": {
						if (initSystem) {
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
																				if (currentServoConfigName.equals("min"))
																					maven.getServos()[globalChannel].getServoData().setMinPosition(servoPos);
																				else if (currentServoConfigName.equals("mid"))
																					maven.getServos()[globalChannel].getServoData().setMidPosition(servoPos);
																				else if (currentServoConfigName.equals("max"))
																					maven.getServos()[globalChannel].getServoData().setMaxPosition(servoPos);
																				runningServoPosConfig = false;
																			} else
																				System.out.println(currentPath + "> Os dados não foram alterados. ");
																			break;
																		case "exit":
																			runningServoPosConfig = false;
																			break;
																		default:
																			try {
																				servoPos = Integer.parseInt(parameter);
																				if (servoPos >= 150 && servoPos <= 600 || servoPos == 0)
																					maven.getServos()[globalChannel].setRawPosition(servoPos);
																				else
																					servoPos = -1;
																			} catch (Exception e) {
																				System.out.println(currentPath + "> Erro. Os valaores estão corretos?");
																			}
																			break;
																	}
																} catch (Exception e) {
																	System.out.println(currentPath + "> Erro. Os valaores estão corretos?");
																}
															}
															break;
														case "general-values":
															currentPath = "configure-servos (servo " + globalChannel + " - general values) ";
															System.out.println(currentPath + "> ");
															break;
														case "exit":
															runningServoConfig = false;
															break;
														case "show":
															System.out.println(maven.getServos()[globalChannel].getServoData().toString());
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
						} else
							System.out.println(currentPath + "> Sistema não iniciado");
						break;
					}
					case "reset-signal":
						if (initSystem) {
							maven.resetAllServosPos();
						} else
							System.out.println(currentPath + "> Sistema não iniciado");
						break;
					case "all-tomid":
						if (initSystem)
							maven.moveAllServosToMidPos();
						else
							System.out.println(currentPath + "> Sistema não iniciado");
						break;
					case "base-tomid":
						if (initSystem)
							maven.moveAllBasesToMidPos();
						else
							System.out.println(currentPath + "> Sistema não iniciado");
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
