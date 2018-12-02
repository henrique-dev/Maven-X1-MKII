package com.br.phdev;

import com.br.phdev.cmp.RobotSystem;
import com.br.phdev.cmp.ScriptCommand;
import com.br.phdev.cmp.servo.ServoTask;
import com.br.phdev.cmp.task.FlavorTaskGroup;
import com.br.phdev.cmp.task.Task;
import com.br.phdev.cmp.task.TaskGroup;
import com.br.phdev.data.DataRepo;
import com.br.phdev.driver.Module;
import com.br.phdev.driver.PCA9685;
import com.br.phdev.exceptions.ScriptException;
import com.br.phdev.members.Leg;
import com.br.phdev.misc.*;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;
import java.util.*;


public class Maven {

	private enum Error {
		SYSTEM_NOT_STARTED, ERROR_ON_LOAD_DATA, INVALID_COMMAND, INVALID_INPUT, SERVO_NOT_FOUND, COMMAND_DISABLED, ERROR_ON_SCRIPT, MISSING_DEP
	}

	private enum Warning {
		SYSTEM_ALL_READY_STARTED
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
			RobotSystem robotSystem = new RobotSystem();

			LinkedList<String> argsList = new LinkedList<>(Arrays.asList(args));

			Scanner in = new Scanner(System.in);

			boolean runningProgram = true;
			boolean initSystem = false;
			boolean moveSystem = false;
			boolean taskSystem = false;
			while (runningProgram) {

				String currentPath = "";
				System.out.print(currentPath + "> ");
				String command;
				if (!argsList.isEmpty())
					command = argsList.pop();
				else
					command = in.nextLine();
				switch (command.trim()) {
					case "exit-f":
					case "exit":
						runningProgram = false;
						if (initSystem)
							robotSystem.stopLegsControl();
						break;
					case "init-system":
						if (!initSystem) {
							robotSystem.initSystem(true, true);
							initSystem = true;
						} else
							show(Warning.SYSTEM_ALL_READY_STARTED);
						break;
					case "init-system-nm":
						if (!initSystem) {
							robotSystem.initSystem(true, false);
							initSystem = true;
						} else
							show(Warning.SYSTEM_ALL_READY_STARTED);
						break;
					case "init-system-d":
						if (!initSystem) {
							robotSystem.initSystem(false, false);
							initSystem = true;
						} else
							show(Warning.SYSTEM_ALL_READY_STARTED);
						break;
					case "reload-system":
						break;
					case "show-s":
						showTHIS(robotSystem.getLegs());
						break;
					case "reload-servos":
						if (initSystem) {
							robotSystem.loadData(false);
							robotSystem.injectData(false);
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
										showTHIS(robotSystem.getLegs());
										break;
									default: {
										try {
											int globalChannel;
											if (parameter.startsWith("servo ")) {
												int indexOfMark = parameter.indexOf("-");
												globalChannel = Integer.parseInt(parameter.substring(6, indexOfMark != -1 ? indexOfMark -1 : parameter.length()));
												if (robotSystem.findServo(globalChannel)) {
													if (parameter.endsWith(" -show")) {
														Log.i(robotSystem.getServos()[globalChannel].getServoData().toString());
													} else if (parameter.endsWith(" -min")) {
														robotSystem.getServos()[globalChannel].moveToMin();
													} else if (parameter.endsWith(" -mid")) {
														robotSystem.getServos()[globalChannel].moveToMid();
													} else if (parameter.endsWith(" -max")) {
														robotSystem.getServos()[globalChannel].moveToMax();
													} else if (parameter.endsWith(" -limit-max")) {
														robotSystem.getServos()[globalChannel].moveToLimitMax();
													} else if (parameter.endsWith(" -limit-min")) {
														robotSystem.getServos()[globalChannel].moveToLimitMin();
													} else if (parameter.endsWith(" -max-up")) {
														robotSystem.getServos()[globalChannel].moveMaxUp();
													} else if (parameter.endsWith(" -max-down")) {
														robotSystem.getServos()[globalChannel].moveMaxDown();
													} else {
														boolean runningServoConfig = true;
														while (runningServoConfig) {
															currentPath = "configure-servos (servo " + globalChannel + ") ";
															System.out.print(currentPath + "> ");
															parameter = in.nextLine();
															switch (parameter.trim()) {
																case "opening": case "limit-min": case "limit-max": case "min": case "mid": case "max":
																case "inverted": case "move": {
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
																							case "min": robotSystem.getServos()[globalChannel].getServoData().setMinPosition(valueForServo); break;
																							case "mid": robotSystem.getServos()[globalChannel].getServoData().setMidPosition(valueForServo); break;
																							case "max": robotSystem.getServos()[globalChannel].getServoData().setMaxPosition(valueForServo); break;
																							case "limit-min": robotSystem.getServos()[globalChannel].getServoData().setLimitMin((int)valueForServo); break;
																							case "limit-max": robotSystem.getServos()[globalChannel].getServoData().setLimitMax((int)valueForServo); break;
																							case "opening":
																								robotSystem.getServos()[globalChannel].getServoData().setDegreesOpening((int)valueForServo);
																								robotSystem.getServos()[globalChannel].getServoData().setStep();
																								break;
																							case "inverted": robotSystem.getServos()[globalChannel].getServoData().setInverted(valueForServo == 1);
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
																					robotSystem.getServos()[globalChannel].moveToMin();
																					break;
																				case "-mid":
																					robotSystem.getServos()[globalChannel].moveToMid();
																					break;
																				case "-max":
																					robotSystem.getServos()[globalChannel].moveToMax();
																					break;
																				case "-limit-min":
																					robotSystem.getServos()[globalChannel].moveToLimitMin();
																					break;
																				case "-limit-max":
																					robotSystem.getServos()[globalChannel].moveToLimitMax();
																					break;
																				case "-max-down":
																					robotSystem.getServos()[globalChannel].moveMaxDown();
																					break;
																				case "-max-up":
																					robotSystem.getServos()[globalChannel].moveMaxUp();
																					break;
																				case "":
																					break;
																				default:
																					try {

																						switch (currentServoConfigName) {
																							case "min": case "mid": case "max":
																								valueForServo = Float.parseFloat(parameter);
																								if (valueForServo >= 100 && valueForServo <= 650 || valueForServo == 0)
																									robotSystem.getServos()[globalChannel].setRawPosition(valueForServo);
																								else
																									valueForServo = -1;
																								break;
																							case "limit-min": case "limit-max":
																								valueForServo = Integer.parseInt(parameter);
																								float step = robotSystem.getServos()[globalChannel].getServoData().getStep();
																								float max = robotSystem.getServos()[globalChannel].getServoData().getMaxPosition();
																								float mid = robotSystem.getServos()[globalChannel].getServoData().getMidPosition();
																								float min = robotSystem.getServos()[globalChannel].getServoData().getMinPosition();
																								float servoPos = valueForServo * step;
																								boolean inverted = robotSystem.getServos()[globalChannel].getServoData().isInverted();
																								float newServoPos = inverted ? mid - servoPos : mid + servoPos;

																								if (newServoPos >= min && newServoPos <= max) {
																									Log.w("DENTRO DOS VALORES");
																									Log.w("Posição do servo correspondente ao grau: " + newServoPos);
																									robotSystem.getServos()[globalChannel].setRawPosition(newServoPos);
																								} else {
																									Log.e("FORA DOS VALORES");
																									Log.w("Posição do servo correspondente ao grau: " + newServoPos);
																									valueForServo = -1;
																								}
																								break;
																							case "move":
																								valueForServo = Float.parseFloat(parameter);
																								robotSystem.getServos()[globalChannel].move(valueForServo);
																								break;
																							case "opening":
																								valueForServo = Integer.parseInt(parameter);
																								break;
																							case "inverted":
																								valueForServo = Boolean.parseBoolean(parameter) ? 1 : 0;
																								break;
																						}
																					} catch (Exception e) {
																						show(Error.INVALID_INPUT, e.getMessage());
																					}
																					break;
																			}
																		} catch (Exception e) {
																			show(Error.INVALID_INPUT, e.getMessage());
																		}
																	}
																	break;
																}
																case "general-values":
																	currentPath = "configure-servos (servo " + globalChannel + " - general values) ";
																	System.out.println(currentPath + "> ");
																	break;
																case "show":
																	Log.i(robotSystem.getServos()[globalChannel].getServoData().toString());
																	break;
																case "reload-servos":
																	robotSystem.loadData(false);
																	robotSystem.injectData(false);
																	break;
																case "-min":
																	robotSystem.getServos()[globalChannel].moveToMin();
																	break;
																case "-mid":
																	robotSystem.getServos()[globalChannel].moveToMid();
																	break;
																case "-max":
																	robotSystem.getServos()[globalChannel].moveToMax();
																	break;
																case "-limit-min":
																	robotSystem.getServos()[globalChannel].moveToLimitMin();
																	break;
																case "-limit-max":
																	robotSystem.getServos()[globalChannel].moveToLimitMax();
																	break;
																case "-max-down":
																	robotSystem.getServos()[globalChannel].moveMaxDown();
																	break;
																case "-max-up":
																	robotSystem.getServos()[globalChannel].moveMaxUp();
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
											show(Error.INVALID_INPUT, e.getMessage());
											Log.w(e.getMessage());
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
							for (Module module : robotSystem.getModuleList()) {
								if (module instanceof PCA9685)
									module.restartDriver();
							}
						} else
							show(Error.SYSTEM_NOT_STARTED);
						break;
                    case "reset-pwm":
                        if (initSystem) {
                            robotSystem.clearPWM();
                        }
                        break;
					case "run-script":
						if (initSystem) {
							boolean runningScript = true;
							List<ScriptCommand> scriptCommandList = new ArrayList<>();
							while (runningScript) {
								currentPath = "run-script ";
								System.out.print(currentPath + "> ");
								String script = in.nextLine();
								try {
									switch (script) {
										case "exit":
											runningScript = false;
											scriptCommandList.clear();
											scriptCommandList = null;
											break;
										case "":
											break;
										default:
											boolean commandOpenFound = false;
											boolean commandCloseFound = false;
											boolean timerFound = false;
											boolean posFound = false;
											boolean servoFound = false;
											StringBuilder currentTimer = new StringBuilder();
											StringBuilder currentServoNum = new StringBuilder();
											StringBuilder currentPos = new StringBuilder();
											int scriptGroup = 0;
											for (int i = 0; i < script.length(); i++) {
												char c = script.charAt(i);
												switch (c) {
													case '<':
														if (commandCloseFound)
															throw new ScriptException("fechamento achado antes de abertura");
														commandOpenFound = true;
														break;
													case '>':
														if (!commandOpenFound)
															throw new ScriptException("abertura antes de fechamento não achada");
														commandOpenFound = false;
														ScriptCommand.ScriptPos scriptPos;
														switch (currentPos.toString()) {
															case "max":
																scriptPos = ScriptCommand.ScriptPos.UP;
																break;
															case "mid":
																scriptPos = ScriptCommand.ScriptPos.MID;
																break;
															case "min":
																scriptPos = ScriptCommand.ScriptPos.DOWN;
																break;
															default:
																throw new ScriptException("posição invalida");
														}
														int servoNum = Integer.parseInt(currentServoNum.toString().trim());
														long delay = Long.parseLong(currentTimer.toString().trim());
														scriptCommandList.add(new ScriptCommand(servoNum, delay, scriptPos, scriptGroup));
														currentTimer = new StringBuilder();
														currentServoNum = new StringBuilder();
														currentPos = new StringBuilder();
														break;
													case 's':
														servoFound = true;
														break;
													case 't':
														timerFound = true;
														break;
													case 'p':
														posFound = true;
														break;
													case '-':
														servoFound = false;
														timerFound = false;
														posFound = false;
														break;
													case '@':
														scriptGroup++;
														break;
													case '%':
														scriptCommandList.add(new ScriptCommand(500, scriptGroup));
														break;
													default: {
														if (servoFound && !timerFound && !posFound) {
															currentServoNum.append(c);
														} else if (timerFound && !servoFound && !posFound) {
															currentTimer.append(c);
														} else if (posFound && !servoFound && !timerFound) {
															currentPos.append(c);
														} else {
															//throw new ScriptException("caractere invalido");
														}
														break;
													}
												}
											}

											int taskGroups[] = new int[scriptGroup+1];
											int currentGroupFound = 0;
											int currentTask = 1;
											for (ScriptCommand sc : scriptCommandList) {
												if (sc.getScriptGroup() > currentGroupFound) {
													currentGroupFound++;
													currentTask = 1;
												}
												taskGroups[currentGroupFound] = currentTask++;
											}

											Log.w("Quantidade de grupos encontrados: " + taskGroups.length);
											Log.w("Quantidade de comandos por grupo: ");
											for (int i=0; i<taskGroups.length; i++) {
												Log.w("Grupo " + i + ": " + taskGroups[i]);
											}

											TaskGroup taskGroup = new TaskGroup(taskGroups);

											for (ScriptCommand sc : scriptCommandList) {
												List<Task> taskList = new ArrayList<>();
												if (!sc.isJustForDelay()) {
													switch (sc.getScriptPos()) {
														case UP:
															taskList.add(new ServoTask(
																	robotSystem.getServos()[sc.getServoNum()],
																	robotSystem.getServos()[sc.getServoNum()].getServoData().getLimitMax(), sc.getDelay(),
																	null,
																	new FlavorTaskGroup(sc.getScriptGroup(), taskGroup)));
															break;
														case MID:
															taskList.add(new ServoTask(
																	robotSystem.getServos()[sc.getServoNum()],
																	0, sc.getDelay(),
																	null,
																	new FlavorTaskGroup(sc.getScriptGroup(), taskGroup)));
															break;
														case DOWN:
															taskList.add(new ServoTask(
																	robotSystem.getServos()[sc.getServoNum()],
																	robotSystem.getServos()[sc.getServoNum()].getServoData().getLimitMin(), sc.getDelay(),
																	null,
																	new FlavorTaskGroup(sc.getScriptGroup(), taskGroup)));
															break;
													}
												} else {
													taskList.add(new ServoTask(new FlavorTaskGroup(sc.getScriptGroup(), taskGroup), 500));
												}
												robotSystem.getServoTaskController().addTasks(taskList);
											}

											scriptCommandList.clear();

											break;
									}
								} catch (ScriptException e) {
									show(Error.ERROR_ON_SCRIPT);
									Log.e(e.getMessage());
								}
							}
						} else
							show(Error.SYSTEM_NOT_STARTED);
						break;
					case "init-task-system":
						robotSystem.initServoTaskController();
						break;
					case "init-move-system":
						robotSystem.initServoTaskController();
						robotSystem.initMovementSystem(true);
						moveSystem = true;
						break;
					case "init-move-system-nm":
						robotSystem.initServoTaskController();
						robotSystem.initMovementSystem(false);
						moveSystem = true;
						break;
					case "move-system":
						if (moveSystem && initSystem) {
							boolean runningMoveSystem = true;
							boolean runningGravitySystem = false;
							while (runningMoveSystem) {
								currentPath = "move-system ";
								System.out.print(currentPath + "> ");
								if (!argsList.isEmpty())
									command = argsList.pop();
								else
									command = in.nextLine();
								switch (command) {
                                    case "exit":
                                        runningMoveSystem = false;
                                        break;
									case "exit-f":
										runningMoveSystem = false;
										runningProgram = false;
										robotSystem.stopLegsControl();
										break;
									case "":
										break;
									default:
										try {
											if (command.startsWith("move")) {
												String values = command.substring(5);
												int index = values.indexOf(" ");
												String value = values.substring(0, index);
												double stepSizeX = Double.parseDouble(value);
												values = values.substring(index+1);
												index = values.indexOf(" ");
												value = values.substring(0, index);
												double stepSizeY = Double.parseDouble(value);
												values = values.substring(index+1);
												index = values.indexOf(" ");
												value = values.substring(0, index);
												int gaitSpeed = Integer.parseInt(value);
												value = values.substring(index+1);
												int stepAmount = Integer.parseInt(value);
												robotSystem.getMovementSystem().move(stepSizeX, stepSizeY, stepAmount, gaitSpeed);
                                            } else if (command.startsWith("elevate")) {
												String values = command.substring(8);
												int elevationType = Integer.parseInt(values);
												robotSystem.getMovementSystem().elevate(elevationType);
                                            } else if (command.startsWith("init-gravity-system") || command.startsWith("igs")) {
                                                if (command.endsWith("init-gravity-system") || command.endsWith("igs")) {
                                                    Log.w("Iniciando sistema de centro de gravidade com medida padrão 430mmx430mm e precisão de 0.5mm");
                                                    if (!runningGravitySystem) {
														robotSystem.getMovementSystem().startGravitySystem(390, 390, 0.5, 2000);
														runningGravitySystem = true;
													} else
														robotSystem.getMovementSystem().adjustGravitySystem(390, 390, 0.5, 2000);
                                                } else {
                                                    String values = command.startsWith("igs") ? command.substring(4) : command.substring(20);
                                                    int index = values.indexOf(" ");
                                                    String value = values.substring(0, index);
                                                    double width = Double.parseDouble(value);
                                                    values = values.substring(index+1);
                                                    index = values.indexOf(" ");
                                                    value = values.substring(0, index);
                                                    double height = Double.parseDouble(value);
													values = values.substring(index+1);
													index = values.indexOf(" ");
													value = values.substring(0, index);
                                                    double precision = Double.parseDouble(value);
													value = values.substring(index+1);
													int gaitSpeed = Integer.parseInt(value);
                                                    Log.w("Iniciando sistema de centro de gravidade com medida padrão " + width +
                                                            "mmx" + height + "mm e precisão de " + precision + "mm com velocidade de passo de " + gaitSpeed);
                                                    if (!runningGravitySystem) {
														robotSystem.getMovementSystem().startGravitySystem(width, height, precision, gaitSpeed);
														runningGravitySystem = true;
													} else {
														robotSystem.getMovementSystem().adjustGravitySystem(width, height, precision, gaitSpeed);
													}
                                                }
                                            } else
                                                show(Error.INVALID_COMMAND);
										} catch (Exception e) {
											show(Error.INVALID_INPUT, e.getMessage());
										}
										break;
								}
							}
						}
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
			case ERROR_ON_SCRIPT:
				Log.e("Script invalido");
				break;
		}
	}

	private static void show(Error error, String msg) {
		switch (error) {
			case SYSTEM_NOT_STARTED:
				Log.e("Sistema não iniciado" + msg);
				break;
			case INVALID_COMMAND:
				Log.e("Comando inválido\n" + msg);
				break;
			case INVALID_INPUT:
				Log.e("Entrada inválida\n" + msg);
				break;
			case SERVO_NOT_FOUND:
				Log.e("Servo não encontrado\n" + msg);
				break;
			case COMMAND_DISABLED:
				Log.e("Comando temporiariamente desabilitado\n" + msg);
				break;
			case ERROR_ON_SCRIPT:
				Log.e("Script invalido\n" + msg);
				break;
		}
	}

	private static void show(Warning warning) {
		switch (warning) {
			case SYSTEM_ALL_READY_STARTED:
				Log.w("O sistema já está iniciado");
				break;
		}
	}



	private static void showTHIS(Leg[] legs) {
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
