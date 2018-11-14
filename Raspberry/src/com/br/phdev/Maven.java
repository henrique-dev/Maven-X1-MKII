package com.br.phdev;

import com.br.phdev.cmp.*;
import com.br.phdev.driver.Module;
import com.br.phdev.driver.PCA9685;
import com.br.phdev.exceptions.MavenDataException;
import com.br.phdev.misc.Log;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;


public class Maven {

	private enum Error {
		SYSTEM_NOT_STARTED, ERROR_ON_LOAD_DATA, INVALID_COMMAND, INVALID_INPUT, SERVO_NOT_FOUND
	}

	private List<Module> moduleList;
	private List<ServoData> servoDataList;
	private List<LegData> legDataList;

	private Leg[] legs;
	private Servo[] servos;

	private void initSystem() throws I2CFactory.UnsupportedBusNumberException {
		if (this.loadData()) {
			for (Module module : moduleList) {
				module.init();
				if (module instanceof PCA9685)
					((PCA9685) module).setPWMFreq(60);
			}
			this.initLegs();
		} else
			Log.e("Falha ao iniciar o sistema");
	}

	private boolean loadData() {
		DataRepo dataRepo = new DataRepo();
		try {
			Log.i("Carregando informações para os módulos...");
			this.moduleList = dataRepo.loadModulesData();
			Log.i("Carregando informações para os servos...");
			this.servoDataList = dataRepo.loadServosData();
			Log.i("Carregando informações para as pernas...");
			this.legDataList = dataRepo.loadLegsData();
		} catch (MavenDataException e) {
			Log.e("Falha ao carregar as informações. " + e.getMessage());
			return false;
		}
		Log.s("Informações carregadas");
		return true;
	}

	private void initLegs() {
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
			Log.i("Dados de todos os componentes definidos com sucesso");
		} catch (Exception e) {
			Log.e("Falha ao inicializar as pernas. " + e.getMessage());
			e.printStackTrace();
		}
	}

	private boolean findServo(int globalChannel) {
		for (Servo servo : this.servos) {
			if (globalChannel == servo.getServoData().getGlobalChannel())
				return true;
		}
		return false;
	}

	private List<Module> getModuleList() {
		return this.moduleList;
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

	private void moveAllFemursToMidPos() {
		for (int i=0; i<6; i++) {
			this.legs[i].getFemur().moveToMid();
		}
	}

	private void moveAllTarsosToMidPos() {
		for (int i=0; i<6; i++) {
			this.legs[i].getTarsus().moveToMid();
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
				switch (command.trim()) {
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
									case "exit-f":
										runningAllServosConfig = false;
										runningProgram = false;
										break;
									default: {
										try {
											int globalChannel;
											if (parameter.startsWith("servo ")) {
												int indexOfMark = parameter.indexOf("-");
												globalChannel = Integer.parseInt(parameter.substring(6, indexOfMark != -1 ? indexOfMark -1 : parameter.length()));
												if (maven.findServo(globalChannel)) {
													if (parameter.endsWith(" -show")) {
														Log.i(maven.getServos()[globalChannel].getServoData().toString());
													} else if (parameter.endsWith(" -min")) {
														maven.getServos()[globalChannel].moveToMin();
													} else if (parameter.endsWith(" -mid")) {
														maven.getServos()[globalChannel].moveToMid();
													} else if (parameter.endsWith(" -max")) {
														maven.getServos()[globalChannel].moveToMax();
													} else {
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
																						Log.s("A informação foi salva");
																						if (currentServoConfigName.equals("min"))
																							maven.getServos()[globalChannel].getServoData().setMinPosition(servoPos);
																						else if (currentServoConfigName.equals("mid"))
																							maven.getServos()[globalChannel].getServoData().setMidPosition(servoPos);
																						else if (currentServoConfigName.equals("max"))
																							maven.getServos()[globalChannel].getServoData().setMaxPosition(servoPos);
																					} else
																						Log.w("Os dados não foram alterados");
																					runningServoPosConfig = false;
																					break;
																				case "exit":
																					runningServoPosConfig = false;
																					break;
																				case "exit-f":
																					runningAllServosConfig = false;
																					runningProgram = false;
																					runningServoConfig = false;
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
																						showError(Error.INVALID_INPUT);
																					}
																					break;
																			}
																		} catch (Exception e) {
																			showError(Error.INVALID_INPUT);
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
																	Log.i(maven.getServos()[globalChannel].getServoData().toString());
																	break;
																case "exit-f":
																	runningAllServosConfig = false;
																	runningProgram = false;
																	runningServoConfig = false;
																	break;
																default:
																	showError(Error.INVALID_COMMAND);
																	break;
															}
														}
													}
												} else {
													showError(Error.SERVO_NOT_FOUND);
												}
											}
										} catch (Exception e) {
											showError(Error.INVALID_INPUT);
										}
										break;
									}
								}
							}
						} else
							showError(Error.SYSTEM_NOT_STARTED);
						break;
					}
					case "reset-signal":
						if (initSystem) {
							maven.resetAllServosPos();
						} else
							showError(Error.SYSTEM_NOT_STARTED);
						break;
					case "all-tomid":
						if (initSystem)
							maven.moveAllServosToMidPos();
						else
							showError(Error.SYSTEM_NOT_STARTED);
						break;
					case "base-tomid":
						if (initSystem)
							maven.moveAllBasesToMidPos();
						else
							showError(Error.SYSTEM_NOT_STARTED);
						break;
					case "femur-tomid":
						if (initSystem)
							maven.moveAllFemursToMidPos();
						else
							showError(Error.SYSTEM_NOT_STARTED);
						break;
					case "tarso-tomid":
						if (initSystem)
							maven.moveAllTarsosToMidPos();
						else
							showError(Error.SYSTEM_NOT_STARTED);
						break;
					case "system-restart-modules":
						for (Module module : maven.getModuleList()) {
							if (module instanceof PCA9685)
								module.restartDriver();
						}
						break;
					case "run-script":
						boolean runningScript = true;
						while (runningScript) {
							String script = in.nextLine();
							switch (script) {
								case "exit":
									runningScript = false;
									break;
								default:
									boolean posFind = false;
									boolean servoFind = false;
									StringBuilder currentServoNum = new StringBuilder();

									int servoNum = -1;
									for (int i=0; i<script.length(); i++) {
										char c = script.charAt(i);
										if (c == 's') {
											servoFind = true;
										} else if (c == 'm') {
											servoFind = false;
											if (script.charAt(i+1) == 'i' && script.charAt(i+2) == 'n') {
												Log.w("Movendo servo " + currentServoNum.toString() + " para min");
											} else if (script.charAt(i+1) == 'i' && script.charAt(i+2) == 'd') {
												Log.w("Movendo servo " + currentServoNum.toString() + " para mid");
											} else if (script.charAt(i+1) == 'a' && script.charAt(i+2) == 'x') {
												Log.w("Movendo servo " + currentServoNum.toString() + " para max");
											}
											currentServoNum = null;
											i += 4;
										} else if (c == '-' && servoFind){
											servoFind = false;
										} else if (servoFind)
											currentServoNum.append(c);
									}
									break;
							}
						}
						break;
					case "":
						break;
					default:
						System.out.println(command);
						showError(Error.INVALID_COMMAND);
						break;
				}
			}

		} catch (I2CFactory.UnsupportedBusNumberException e) {
			Log.e(e.getMessage());
			//e.printStackTrace();
		} catch (IOException e) {
			Log.e(e.getMessage());
			//e.printStackTrace();
		}
	}

	private static void showError(Error error) {
		switch (error) {
			case SYSTEM_NOT_STARTED:
				Log.e("Sistema não iniciado");
				break;
			case INVALID_COMMAND:
				Log.e("Comando inválido");
				break;
			case INVALID_INPUT:
				Log.e("Entrada inválida");
				break;
			case SERVO_NOT_FOUND:
				Log.e("Servo não encontrado");
				break;
		}
	}

}
