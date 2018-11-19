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
		SYSTEM_NOT_STARTED, ERROR_ON_LOAD_DATA, INVALID_COMMAND, INVALID_INPUT, SERVO_NOT_FOUND, COMMAND_DISABLED
	}

	private enum Warning {
		SYSTEM_ALLREADY_STARTED
	}

	private List<Module> moduleList;
	private List<ServoData> servoDataList;
	private List<LegData> legDataList;

	private Leg[] legs;
	private Servo[] servos;

	private void initSystem() throws I2CFactory.UnsupportedBusNumberException {
		if (this.loadData(true)) {
			for (Module module : moduleList) {
				module.init();
				if (module instanceof PCA9685)
					((PCA9685) module).setPWMFreq(60);
			}
			this.initLegs();
		} else
			Log.e("Falha ao iniciar o sistema");
	}

	private boolean loadData(boolean loadModules) {
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

	private Leg[] getLegs() {
		return this.legs;
	}

	private static void waitFor(long howMuch) {
		try {
			Thread.sleep(howMuch);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
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
					case "exit-f":
					case "exit":
						runningProgram = false;
						break;
					case "init-system":
						if (!initSystem) {
							maven.initSystem();
							initSystem = true;
						} else
							show(Warning.SYSTEM_ALLREADY_STARTED);
						break;
					case "reload-system":
						break;
					case "reload-servos":
						if (initSystem) {
							maven.loadData(false);
							maven.initLegs();
						} else
							show(Error.SYSTEM_NOT_STARTED);
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
									case "exit-m":
										runningAllServosConfig = false;
										break;
									case "show-s":
										showTHIS(maven.getLegs());
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
													} else if (parameter.endsWith(" -limit-max")) {
														maven.getServos()[globalChannel].moveToLimitMax();
													} else if (parameter.endsWith(" -limit-min")) {
														maven.getServos()[globalChannel].moveToLimitMin();
													} else if (parameter.endsWith(" -max-up")) {
														maven.getServos()[globalChannel].moveMaxUp();
													} else if (parameter.endsWith(" -max-down")) {
														maven.getServos()[globalChannel].moveMaxDown();
													} else {
														boolean runningServoConfig = true;
														while (runningServoConfig) {
															currentPath = "configure-servos (servo " + globalChannel + ") ";
															System.out.print(currentPath + "> ");
															parameter = in.nextLine();
															switch (parameter.trim()) {
																case "opening": case "limit-min": case "limit-max": case "min": case "mid": case "max":
																case "inverted": {
																	String currentServoConfigName = parameter.trim();
																	boolean runningServoPosConfig = true;
																	float valueForServo = -1;
																	while (runningServoPosConfig) {
																		currentPath = "configure-servos (servo " + globalChannel + " - " + currentServoConfigName + ") ";
																		System.out.print(currentPath + "> ");
																		try {
																			parameter = in.nextLine();
																			switch (parameter.trim()) {
																				case "save":
																					if (valueForServo != -1) {
																						DataRepo dataRepo = new DataRepo();
																						dataRepo.saveServoPosData(globalChannel, currentServoConfigName, valueForServo);
																						switch (currentServoConfigName) {
																							case "min": maven.getServos()[globalChannel].getServoData().setMinPosition(valueForServo); break;
																							case "mid": maven.getServos()[globalChannel].getServoData().setMidPosition(valueForServo); break;
																							case "max": maven.getServos()[globalChannel].getServoData().setMaxPosition(valueForServo); break;
																							case "limit-min": maven.getServos()[globalChannel].getServoData().setLimitMin((int)valueForServo); break;
																							case "limit-max": maven.getServos()[globalChannel].getServoData().setLimitMax((int)valueForServo); break;
																							case "opening":
																								maven.getServos()[globalChannel].getServoData().setDegreesOpening((int)valueForServo);
																								maven.getServos()[globalChannel].getServoData().setStep();
																								break;
																							case "inverted": maven.getServos()[globalChannel].getServoData().setInverted(valueForServo == 1);
																						}
																						Log.s("A configuração foi salva");
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
																				case "exit-m":
																					runningAllServosConfig = false;
																					runningServoConfig = false;
																					runningServoPosConfig = false;
																				case "-min":
																					maven.getServos()[globalChannel].moveToMin();
																					break;
																				case "-mid":
																					maven.getServos()[globalChannel].moveToMid();
																					break;
																				case "-max":
																					maven.getServos()[globalChannel].moveToMax();
																					break;
																				case "-limit-min":
																					maven.getServos()[globalChannel].moveToLimitMin();
																					break;
																				case "-limit-max":
																					maven.getServos()[globalChannel].moveToLimitMax();
																					break;
																				case "-max-down":
																					maven.getServos()[globalChannel].moveMaxDown();
																					break;
																				case "-max-up":
																					maven.getServos()[globalChannel].moveMaxUp();
																					break;
																				case "":
																					break;
																				default:
																					try {

																						switch (currentServoConfigName) {
																							case "min": case "mid": case "max":
																								valueForServo = Float.parseFloat(parameter);
																								if (valueForServo >= 100 && valueForServo <= 650 || valueForServo == 0)
																									maven.getServos()[globalChannel].setRawPosition(valueForServo);
																								else
																									valueForServo = -1;
																								break;
																							case "limit-min": case "limit-max":
																								valueForServo = Integer.parseInt(parameter);
																								float step = maven.getServos()[globalChannel].getServoData().getStep();
																								float max = maven.getServos()[globalChannel].getServoData().getMaxPosition();
																								float mid = maven.getServos()[globalChannel].getServoData().getMidPosition();
																								float min = maven.getServos()[globalChannel].getServoData().getMinPosition();
																								float servoPos = valueForServo * step;
																								boolean inverted = maven.getServos()[globalChannel].getServoData().isInverted();
																								float newServoPos = inverted ? mid - servoPos : mid + servoPos;

																								if (newServoPos >= min && newServoPos <= max) {
																									Log.w("DENTRO DOS VALORES");
																									Log.w("Posição do servo correspondente ao grau: " + newServoPos);
																									maven.getServos()[globalChannel].setRawPosition(newServoPos);
																								} else {
																									Log.e("FORA DOS VALORES");
																									Log.w("Posição do servo correspondente ao grau: " + newServoPos);
																									valueForServo = -1;
																								}
																								break;
																							case "opening":
																								valueForServo = Integer.parseInt(parameter);
																								break;
																							case "inverted":
																								valueForServo = Boolean.parseBoolean(parameter) ? 1 : 0;
																								break;
																						}
																					} catch (Exception e) {
																						show(Error.INVALID_INPUT);
																					}
																					break;
																			}
																		} catch (Exception e) {
																			show(Error.INVALID_INPUT);
																		}
																	}
																	break;
																}
																case "general-values":
																	currentPath = "configure-servos (servo " + globalChannel + " - general values) ";
																	System.out.println(currentPath + "> ");
																	break;
																case "show":
																	Log.i(maven.getServos()[globalChannel].getServoData().toString());
																	break;
																case "reload-servos":
																	maven.loadData(false);
																	maven.initLegs();
																	break;
																case "-min":
																	maven.getServos()[globalChannel].moveToMin();
																	break;
																case "-mid":
																	maven.getServos()[globalChannel].moveToMid();
																	break;
																case "-max":
																	maven.getServos()[globalChannel].moveToMax();
																	break;
																case "-limit-min":
																	maven.getServos()[globalChannel].moveToLimitMin();
																	break;
																case "-limit-max":
																	maven.getServos()[globalChannel].moveToLimitMax();
																	break;
																case "-max-down":
																	maven.getServos()[globalChannel].moveMaxDown();
																	break;
																case "-max-up":
																	maven.getServos()[globalChannel].moveMaxUp();
																	break;
																case "exit":
																	runningServoConfig = false;
																	break;
																case "exit-f":
																	runningAllServosConfig = false;
																	runningProgram = false;
																	runningServoConfig = false;
																	break;
																case "exit-m":
																	runningServoConfig = false;
																	runningAllServosConfig = false;
																	break;
																case "":
																	break;
																default:
																	show(Error.INVALID_COMMAND);
																	break;
															}
														}
													}
												} else {
													show(Error.SERVO_NOT_FOUND);
												}
											} else
												show(Error.INVALID_COMMAND);
										} catch (Exception e) {
											show(Error.INVALID_INPUT);
										}
										break;
									}
								}
							}
						} else
							show(Error.SYSTEM_NOT_STARTED);
						break;
					}
					case "system-restart-modules":
						if (initSystem) {
							for (Module module : maven.getModuleList()) {
								if (module instanceof PCA9685)
									module.restartDriver();
							}
						} else
							show(Error.SYSTEM_NOT_STARTED);
						break;
					case "run-script":
						if (initSystem) {
							boolean runningScript = true;
							while (runningScript) {
								currentPath = "run-script ";
								System.out.print(currentPath + "> ");
								String script = in.nextLine();
								switch (script) {
									case "exit":
										runningScript = false;
										break;
									default:
										boolean servoFind = false;
										StringBuilder currentServoNum = new StringBuilder();
										for (int i = 0; i < script.length(); i++) {
											char c = script.charAt(i);
											if (c == 's') {
												servoFind = true;
											} else if (c == 'm') {
												int channelGlobal = Integer.parseInt(currentServoNum.toString());
												if (script.charAt(i + 1) == 'i' && script.charAt(i + 2) == 'n') {
													maven.getServos()[channelGlobal].moveToMin();
													i += 3;
												} else if (script.charAt(i + 1) == 'i' && script.charAt(i + 2) == 'd') {
													maven.getServos()[channelGlobal].moveToMid();
													i += 3;
												} else if (script.charAt(i + 1) == 'a' && script.charAt(i + 2) == 'x') {
													maven.getServos()[channelGlobal].moveToMax();
													i += 3;
												}
												currentServoNum = new StringBuilder();
											} else if (c == '-' && servoFind) {
												servoFind = false;
											} else if (c == ' ') {

											} else if (c == '@') {
												Maven.waitFor(500);
											} else if (servoFind)
												currentServoNum.append(c);
										}
										break;
								}
							}
						} else
							show(Error.SYSTEM_NOT_STARTED);
						break;
					case "":
						break;
					default:
						show(Error.INVALID_COMMAND);
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

	private static void show(Error error) {
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
			case COMMAND_DISABLED:
				Log.e("Comando temporiariamente desabilitado");
				break;
		}
	}

	private static void show(Warning warning) {
		switch (warning) {
			case SYSTEM_ALLREADY_STARTED:
				Log.w("O sistema já está iniciado");
				break;
		}
	}



	private static void showTHIS(Leg[] legs) {
		int l1t = legs[0].getTarsus().getServo().getServoData().getGlobalChannel();
		String l1f = legs[0].getFemur().getServo().getServoData().getGlobalChannel() + "";
		String l1b = legs[0].getBase().getServo().getServoData().getGlobalChannel() + "";
		System.out.println();
		System.out.println();
		System.out.println("                           FRONT                    ");
		System.out.println();
		System.out.println("       _____                                   _____");
		System.out.println("      |     |                                 |     |");
		System.out.println("      |  " + s_(legs[0].getTarsus().getServo().getServoData().getGlobalChannel())  +
				" |                                 |  " + s_(legs[1].getTarsus().getServo().getServoData().getGlobalChannel()) + " |");
		System.out.println("      |_____|                                 |_____|");
		System.out.println("             o                               o");
		System.out.println("              o                             o");
		System.out.println("               o_____                 _____o");
		System.out.println("               |     |               |     |");
		System.out.println("               |  " + s_(legs[0].getFemur().getServo().getServoData().getGlobalChannel()) +
				" |               |  " + s_(legs[1].getFemur().getServo().getServoData().getGlobalChannel()) + " |");
		System.out.println("               |_____|_____     _____|_____|");
		System.out.println("                     |     |mmm|     |");
		System.out.println("                     | " + s_(legs[0].getBase().getServo().getServoData().getGlobalChannel()) +
				"  |   |  " + s_(legs[1].getBase().getServo().getServoData().getGlobalChannel()) + " |");
		System.out.println("                     |_____|   |_____|");
		System.out.println("                       m           m");
		System.out.println("                       m M A V E N m");
		System.out.println("        LEFT           m           m              RIGHT");
		System.out.println("                       m           m");
		System.out.println("   _____       _____  _m___     ___m_ _____       _____");
		System.out.println("  |     |     |     ||     |   |     |     |     |     |");
		System.out.println("  | " + s_(legs[2].getTarsus().getServo().getServoData().getGlobalChannel()) +
				"  |o o o| " + s_(legs[2].getFemur().getServo().getServoData().getGlobalChannel()) +
				"  || " + s_(legs[2].getBase().getServo().getServoData().getGlobalChannel()) + "  |   | " +
				s_(legs[3].getBase().getServo().getServoData().getGlobalChannel())+"  | " +
				s_(legs[3].getFemur().getServo().getServoData().getGlobalChannel())+"  |o o o| " +
				s_(legs[3].getTarsus().getServo().getServoData().getGlobalChannel())+"  |");
		System.out.println("  |_____|     |_____||_____|   |_____|_____|     |_____|");
		System.out.println("                       m           m");
		System.out.println("                       m           m");
		System.out.println("                      _m___     ___m_");
		System.out.println("                     |     |   |     |");
		System.out.println("                     | " + s_(legs[4].getBase().getServo().getServoData().getGlobalChannel()) +
				"  |   | " + s_(legs[5].getBase().getServo().getServoData().getGlobalChannel()) + "  |");
		System.out.println("                _____|_____|mmm|_____|_____");
		System.out.println("               |     |               |     |");
		System.out.println("               | " + s_(legs[4].getFemur().getServo().getServoData().getGlobalChannel()) +
				"  |               | " + s_(legs[5].getFemur().getServo().getServoData().getGlobalChannel()) + "  |");
		System.out.println("               |_____|               |_____|");
		System.out.println("               o                            o");
		System.out.println("              o                              o");
		System.out.println("       _____ o                                o_____");
		System.out.println("      |     |                                 |     |");
		System.out.println("      | " +s_(legs[4].getTarsus().getServo().getServoData().getGlobalChannel()) +
				"  |                                 | " + s_(legs[5].getTarsus().getServo().getServoData().getGlobalChannel()) + "  |");
		System.out.println("      |_____|                                 |_____|");
		System.out.println();
		System.out.println();
		System.out.println("                           BACK                      ");
		System.out.println();
	}

	private static String s_(int value) {
		if (value > 9)
			return value + "";
		return value + " ";
	}

}
