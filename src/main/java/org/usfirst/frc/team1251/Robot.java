package org.usfirst.frc.team1251;

import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Created by Eric Engelhart on 1/27/2017.
 */

public class Robot extends IterativeRobot {

    //Define Joystick inputs
    public static final int CONTROLLER_LEFT_AXIS = 1;
    public static final int CONTROLLER_RIGHT_AXIS = 3;
    public static final int CONTROLLER_X_BUTTON = 1;
    public static final int CONTROLLER_A_BUTTON = 2;
    public static final int CONTROLLER_B_BUTTON = 3;
    public static final int CONTROLLER_Y_BUTTON = 4;
    public static final int CONTROLLER_LEFT_BUMPER = 5;
    public static final int CONTROLLER_RIGHT_BUMPER = 6;
    public static final int CONTROLLER_LEFT_TRIGGER = 7;
    public static final int CONTROLLER_RIGHT_TRIGGER = 8;
    public static final int CONTROLLER_SELECT_BUTTON = 9;
    public static final int CONTROLLER_START_BUTTON = 10;
    public static final int STICK_AXIS = 1;
    public static final int STICK_LEVEL = 2;
    public static final int STICK_TRIGGER = 1;
    public static final int STICK_BUTTON_2 = 2;
    public static final int STICK_BUTTON_3 = 3;
    public static final int STICK_BUTTON_4 = 4;
    public static final int STICK_BUTTON_5 = 5;
    public static final int STICK_BUTTON_6 = 6;
    public static final int STICK_BUTTON_7 = 7;
    public static final int STICK_BUTTON_8 = 8;
    public static final int STICK_BUTTON_9 = 9;

    //Define PWM ports
    private static final int PWM_PORT_0 = 0;
    private static final int PWM_PORT_1 = 1;
    private static final int PWM_PORT_2 = 2;
    private static final int PWM_PORT_3 = 3;
    private static final int PWM_PORT_4 = 4;
    private static final int PWM_PORT_5 = 5;
    private static final int PWM_PORT_6 = 6;
    private static final int PWM_PORT_7 = 7;
    private static final int PWM_PORT_8 = 8;
    private static final int PWM_PORT_9 = 9;

    //Define PCM ports
    private static final int PCM_PORT_0 = 0;
    private static final int PCM_PORT_1 = 1;
    private static final int PCM_PORT_2 = 2;
    private static final int PCM_PORT_3 = 3;
    private static final int PCM_PORT_4 = 4;
    private static final int PCM_PORT_5 = 5;
    private static final int PCM_PORT_6 = 6;
    private static final int PCM_PORT_7 = 7;

    //Define DIO ports
    private static final int DIO_PORT_0 = 0;
    private static final int DIO_PORT_1 = 1;
    private static final int DIO_PORT_2 = 2;
    private static final int DIO_PORT_3 = 3;
    private static final int DIO_PORT_4 = 4;
    private static final int DIO_PORT_5 = 5;
    private static final int DIO_PORT_6 = 6;
    private static final int DIO_PORT_7 = 7;
    private static final String GRIP_TABLE_NAME = "GRIP";
    //Define Speed controllers
    public static RobotDrive driveBase;
    //Define Solenoids
    public static DoubleSolenoid driveBaseShifter;
    //Define encoder
    public static Encoder driveEncoderLeft;
    public static Encoder driveEncoderRight;
    //Define booleans
    public static boolean lockControls = false;
    public static ADXRS450_Gyro gyro;
    public static PIDController leftDrive;
    public static PIDController rightDrive;
    public static TT_DoubleTalonPID leftDriveTalons;
    public static TT_DoubleTalonPID rightDriveTalons;
    //Define Joystick ports
    public static Joystick controller;
    public static Talon gearPivot;
    public static DoubleSolenoid gearClaw;
    //Define PIDs
    private final double drive_P = 0.00003;
    //private final double drive_P = 0.0000001;
    private final double drive_I = 0.00000000;
    //private final double drive_I =  0.00000000095;
    private final double drive_D = 0.00001;
    //private final double drive_D = 0.0001;
    //private final double drive_F = 0.00002988505 * 8;
    private final double drive_F = 0;
    public int autoSelect;
    double lsetpoint = TT_Util.convertRPMsToTicks(100);
    double rsetpoint = TT_Util.convertRPMsToTicks(20);
    boolean isTracked;
    int trackingCounter = 0;
    double setpoint = TT_Util.convertRPMsToTicks(200);
    // Middle gear
    int methodNum = 1;
    int methodDone = 1;
    int counter = 0;
    //Define network table grip communicator
    //private TT_GRIP_Communicator gripCommunicator;
    //private TT_GearTracker gearTracker = new TT_GearTracker();
    //private TT_GearPegTracker gearPegTracker =
    MjpegServer mjpegServer;
    private Joystick stick;
    private Joystick rightStick;
    private Talon shooter;
    private Talon agitator;
    private Talon ballCollector;
    private Talon gearCollector;
    private Talon hanger;
    private DoubleSolenoid ballCollectorPivot;
    private Encoder shooterEncoder;
    private Encoder hangLimit;
    //Define Sensors
    private Potentiometer gearPot;

    @Override
    public void robotInit() {
        //Declare joystick
        controller = new Joystick(0);
        stick = new Joystick(1);
        rightStick = new Joystick(2);

        //Declare Speed controllers
        Talon leftTalon1 = new Talon(PWM_PORT_0);
        Talon leftTalon2 = new Talon(PWM_PORT_1);
        Talon rightTalon1 = new Talon(PWM_PORT_2);
        Talon rightTalon2 = new Talon(PWM_PORT_3);

        leftDriveTalons = new TT_DoubleTalonPID(leftTalon1, leftTalon2, true);
        rightDriveTalons = new TT_DoubleTalonPID(rightTalon1, rightTalon2, false);

        shooter = new Talon(PWM_PORT_8);
        agitator = new Talon(PWM_PORT_7);
        ballCollector = new Talon(PWM_PORT_6);
        gearCollector = new Talon(PWM_PORT_4);
        gearPivot = new Talon(PWM_PORT_5);
        hanger = new Talon(PWM_PORT_9);

        driveBase = new RobotDrive(leftTalon1, leftTalon2, rightTalon1, rightTalon2);

        //Declare Solenoids
        driveBaseShifter = new DoubleSolenoid(PCM_PORT_0, PCM_PORT_1);
        ballCollectorPivot = new DoubleSolenoid(PCM_PORT_4, PCM_PORT_5);
        gearClaw = new DoubleSolenoid(PCM_PORT_2, PCM_PORT_3);

        //Declare encoder
        driveEncoderLeft = new Encoder(DIO_PORT_0, DIO_PORT_1, false, CounterBase.EncodingType.k1X);
        driveEncoderRight = new Encoder(DIO_PORT_2, DIO_PORT_3, false, CounterBase.EncodingType.k1X);


        shooterEncoder = new Encoder(DIO_PORT_6, DIO_PORT_7);
        hangLimit = new Encoder(DIO_PORT_4, DIO_PORT_5);

        //Declare Sensors
        gearPot = new AnalogPotentiometer(0, 3600, 3);

        new TT_GRIP_Communicator(NetworkTable.getTable(GRIP_TABLE_NAME));

        // PIDs
        driveEncoderLeft.setPIDSourceType(PIDSourceType.kRate);
        driveEncoderRight.setPIDSourceType(PIDSourceType.kRate);

        driveEncoderLeft.setSamplesToAverage(20);
        driveEncoderRight.setSamplesToAverage(20);

        leftDrive = new PIDController(drive_P, drive_I, drive_D, drive_F, driveEncoderLeft, leftDriveTalons);
        rightDrive = new PIDController(drive_P - 0.000006, drive_I, drive_D, drive_F, driveEncoderRight, rightDriveTalons);

        //leftDrive.enable();
        //rightDrive.enable();

        gyro = new ADXRS450_Gyro();
        gyro.calibrate();
        SmartDashboard.putNumber("Auto", -1);

        UsbCamera camera = new UsbCamera("cam", 0);
        camera.setFPS(60);
        camera.setResolution(600, 450);

        //System.out.println(CameraServer.getInstance().addServer("cam", 5800).getSource().getName());
        //CameraServer.getInstance().addCamera(camera);
        mjpegServer = new MjpegServer("", 5800);
        mjpegServer.setSource(camera);

        new TT_DriveUtil(leftDrive, rightDrive, gyro, driveEncoderLeft, driveEncoderRight, driveBase);
        new TT_GearPegTracker();
        new TT_GearTracker();
    }

    @Override
    public void autonomousInit() {
        //leftDrive.disable();
        //rightDrive.disable();
        driveEncoderRight.setReverseDirection(true);
        driveEncoderRight.setDistancePerPulse(0.0005142918);
        driveEncoderLeft.setDistancePerPulse(0.0005142918);
        driveEncoderLeft.reset();
        driveEncoderRight.reset();
        driveBaseShifter.set(DoubleSolenoid.Value.kReverse);
        autoSelect = (int) SmartDashboard.getNumber("Auto", -1);
        SmartDashboard.putString("Autos", "1: Go past baseline\n2: Middle Gear\n3:Stay straight");
        TT_MainAuto.autoInit(autoSelect);

    }
/*
    @Override
    public void testInit() {
        driveEncoderRight.setReverseDirection(true);
        leftDrive.enable();
        rightDrive.enable();
        leftDrive.setSetpoint(0);
        rightDrive.setSetpoint(0);

    }

    @Override
    public void testPeriodic() {
        if (controller.getRawButton(CONTROLLER_A_BUTTON)) {
            gearTracker.track(gripCommunicator);
            leftDrive.setSetpoint(TT_Util.convertRPMsToTicks(gearTracker.getLeftTurning()));
            rightDrive.setSetpoint(TT_Util.convertRPMsToTicks(gearTracker.getRightTurning()));
        } else {
            leftDrive.setSetpoint(0);
            rightDrive.setSetpoint(0);
        }

        SmartDashboard.putNumber("left Encoder", driveEncoderLeft.getRate());
        SmartDashboard.putNumber("right Encoder", driveEncoderRight.getRate());

        SmartDashboard.putNumber("drive left", gearTracker.getLeftTurning());
        SmartDashboard.putNumber("drive right", gearTracker.getRightTurning());

        SmartDashboard.putNumber("left setpoint", leftDrive.getSetpoint());
        SmartDashboard.putNumber("right setpoint", rightDrive.getSetpoint());

    }*/

    /*

    @Override
    public void testInit() {
        leftDrive.disable();
        rightDrive.disable();
        driveEncoderRight.setReverseDirection(true);
        trackingCounter = 0;
        isTracked = false;
        leftDrive.enable();
        rightDrive.enable();

    }

    @Override
    public void testPeriodic() {
        if (controller.getRawButton(CONTROLLER_A_BUTTON)) {
            if (driveEncoderLeft.get() * 0.0005142918 < 1){
                leftDrive.setSetpoint(TT_Util.convertRPMsToTicks(150));
                rightDrive.setSetpoint(TT_Util.convertRPMsToTicks(150));
            }else {

                isTracked = gearPegTracker.track(gripCommunicator) || isTracked;
                if (!isTracked) {
                    trackingCounter = 0;
                }
                if (isTracked && trackingCounter < 50) {
                    trackingCounter++;
                    isTracked = false;
                }
                SmartDashboard.putBoolean("is tracked", isTracked);
                if (!isTracked) {

                    SmartDashboard.putBoolean("tracked", false);
                    leftDrive.setSetpoint(TT_Util.convertRPMsToTicks(gearPegTracker.getLeftTurning() * 35));
                    rightDrive.setSetpoint(TT_Util.convertRPMsToTicks(gearPegTracker.getRightTurning() * 35));

                } else {
                   /* SmartDashboard.putBoolean("tracked", true);
                    if (driveEncoderLeft.get() * 0.0005142918 < 1.85) {
                        leftDrive.setSetpoint(TT_Util.convertRPMsToTicks(150));
                        rightDrive.setSetpoint(TT_Util.convertRPMsToTicks(150));
                    } else {
                        leftDrive.disable();
                        rightDrive.disable();
                    }
                   leftDrive.disable();
                   rightDrive.disable();

}
            }

                    } else {
                    driveBase.tankDrive(0, 0);
                    }

                    SmartDashboard.putNumber("left Encoder", driveEncoderLeft.getRate());
                    SmartDashboard.putNumber("right Encoder", driveEncoderRight.getRate());

                    SmartDashboard.putNumber("drive left", gearPegTracker.getLeftTurning());
                    SmartDashboard.putNumber("drive right", gearPegTracker.getRightTurning());

                    }
     */
/* right gear auto
    @Override
    public void testInit() {
        driveEncoderRight.setReverseDirection(true);
        //driveEncoderRight.setDistancePerPulse(0.0005142918);
        //driveEncoderLeft.setDistancePerPulse(0.0005142918);

        driveEncoderLeft.reset();
        driveEncoderRight.reset();

        leftDrive.enable();
        rightDrive.enable();

        SmartDashboard.putData("PID", leftDrive);
        SmartDashboard.putNumber("Encoder", driveEncoderLeft.getRate());
        lsetpoint = TT_Util.convertRPMsToTicks(100);
        rsetpoint = TT_Util.convertRPMsToTicks(20);
    }

    @Override
    public void testPeriodic() {
        if (controller.getRawButton(CONTROLLER_A_BUTTON)) {
            leftDrive.setSetpoint(lsetpoint);
            rightDrive.setSetpoint(rsetpoint);
            leftDrive.enable();
            rightDrive.enable();
        } else {
            leftDrive.setSetpoint(0);
            rightDrive.setSetpoint(0);
            leftDrive.disable();
            rightDrive.disable();
        }
        SmartDashboard.putData("PID", leftDrive);
        SmartDashboard.putNumber("Encoder", TT_Util.convertTicksToRPMs(driveEncoderLeft.getRate()));
        SmartDashboard.putNumber("right", TT_Util.convertTicksToRPMs(driveEncoderRight.getRate()));
        if (driveEncoderLeft.get() * 0.0005142918 > 0.28 && driveEncoderRight.get() * 0.0005142918 < 0.1){
            lsetpoint = TT_Util.convertRPMsToTicks(200);
            rsetpoint = TT_Util.convertRPMsToTicks(200);
        }
        if (driveEncoderLeft.get() * 0.0005142918 > 2.5 && driveEncoderRight.get() * 0.0005142918 > 2.5){
            lsetpoint = 0;
            rsetpoint = 0;
        }

        SmartDashboard.putNumber("Left", leftDriveTalons.get());
        SmartDashboard.putNumber("Right", rightDriveTalons.get());

    }*/

    @Override
    public void autonomousPeriodic() {
        TT_MainAuto.auto(autoSelect, driveBase, driveBaseShifter, gearPivot, gearClaw, gyro, driveEncoderLeft, driveEncoderRight);

        SmartDashboard.putNumber("auto left enc", driveEncoderLeft.getDistance());
        SmartDashboard.putNumber("auto right enc", driveEncoderRight.getDistance());
        SmartDashboard.putNumber("Gyro", gyro.getAngle());
        //autoSelect = (int) SmartDashboard.getNumber("Auto", -1);
    }

    @Override
    public void teleopInit() {
        //this is due to reversed encoders in both bots
        driveEncoderRight.setReverseDirection(true);
        //it is 0.00050779561 meters per tick, as can be seen in the google sheets
        driveEncoderRight.setDistancePerPulse(1);
        driveEncoderLeft.setDistancePerPulse(1);
        //mjpegServer.free();
    }
/* Center Gear (2 Gear auto)
    @Override
    public void testInit() {
        driveEncoderRight.setReverseDirection(true);
        setpoint = TT_Util.convertRPMsToTicks(200);
        driveEncoderLeft.reset();
        driveEncoderRight.reset();
        methodNum = 1;
        methodDone = 1;
        leftDrive.setSetpoint(0);
        rightDrive.setSetpoint(0);
    }


    @Override
    public void testPeriodic() {
        if (methodDone == 0) {
            methodNum++;
        }
        // remove all button A code
        if (controller.getRawButton(CONTROLLER_A_BUTTON) && methodNum < 2) {
            methodDone = TT_DriveUtil.INSTANCE.driveStraight(350, 75);

        } else if (controller.getRawButton(CONTROLLER_A_BUTTON) && methodNum < 3) {
            methodDone = TT_Util.pause(15);
        } else if (controller.getRawButton(CONTROLLER_A_BUTTON) && methodNum < 4) {
            methodDone = TT_DriveUtil.INSTANCE.driveBackwards(450, 48);
        } else if (controller.getRawButton(CONTROLLER_A_BUTTON) && methodNum < 5) {
            methodDone = TT_DriveUtil.INSTANCE.turnRobot(300, 24);
        } else if (controller.getRawButton(CONTROLLER_A_BUTTON) && methodNum < 6) {
            methodDone = TT_DriveUtil.INSTANCE.trackGear();
        } else if (controller.getRawButton(CONTROLLER_A_BUTTON) && methodNum < 7) {
            methodDone = TT_DriveUtil.INSTANCE.forwardTrackGear();
        } else if (controller.getRawButton(CONTROLLER_A_BUTTON) && methodNum < 8) {
            methodDone = TT_DriveUtil.INSTANCE.driveStraightAndCoast(100, 10);
        } else if (controller.getRawButton(CONTROLLER_A_BUTTON) && methodNum < 9) {
            methodDone = TT_DriveUtil.INSTANCE.driveBackwards(450, 20);
        } else if (controller.getRawButton(CONTROLLER_A_BUTTON) && methodNum < 10) {
            methodDone = TT_DriveUtil.INSTANCE.turnRobot(-250, 30);
        } else if (controller.getRawButton(CONTROLLER_A_BUTTON) && methodNum < 11) {
            methodDone = TT_DriveUtil.INSTANCE.trackPeg(8);
        } else if (controller.getRawButton(CONTROLLER_A_BUTTON) && methodNum < 12) {
            methodDone = TT_DriveUtil.INSTANCE.driveStraightAndCoast(350, 48);
        } else if (controller.getRawButton(CONTROLLER_A_BUTTON) && methodNum < 13) {
            methodDone = 0;//TT_DriveUtil.INSTANCE.trackPeg(3);
        } else if (controller.getRawButton(CONTROLLER_A_BUTTON) && methodNum < 14) {
            methodDone = 0; //TT_DriveUtil.INSTANCE.driveStraightAndCoast(350, 15);
        } else {
            methodDone = 1;
            driveBase.tankDrive(0, 0);
        }
        SmartDashboard.putData("PID", leftDrive);
        SmartDashboard.putNumber("Encoder", TT_Util.convertTicksToRPMs(driveEncoderLeft.getRate()));
        SmartDashboard.putNumber("right", TT_Util.convertTicksToRPMs(driveEncoderRight.getRate()));


        SmartDashboard.putNumber("Left", leftDriveTalons.get());
        SmartDashboard.putNumber("Right", rightDriveTalons.get());
        SmartDashboard.putNumber("Right Encoder Val", driveEncoderRight.get());

    }
    */

// on right side, turn left, then go forwards

    @Override
    public void teleopPeriodic() {
        //Subsystem 1, Drivebase
        TT_Drive.drive(controller, driveBase);
        //TT_Drive.shifter(driveEncoderLeft, driveEncoderRight, driveBaseShifter);
        if (stick.getRawButton(CONTROLLER_SELECT_BUTTON) || stick.getRawButton(CONTROLLER_START_BUTTON)) {
            TT_Hanger.hang(stick, hanger, hangLimit, gearPivot);
        } else {
            hanger.set(0);
            TT_GearCollector.collectGearFloor(stick, gearCollector, gearPivot, gearClaw, gearPot);
        }

        SmartDashboard.putNumber("Left encoder rate", TT_Util.convertTicksToRPMs(driveEncoderLeft.getRate()));
        SmartDashboard.putNumber("Right encoder rate", driveEncoderRight.getRate());
        SmartDashboard.putNumber("Left motor power", leftDriveTalons.get());

        SmartDashboard.putNumber("Pot", gearPot.get());
    }

    @Override
    public void testInit() {
        TT_Auto.twoGearAutoInit();
    }

    @Override
    public void testPeriodic() {
        TT_Auto.twoGearAutoPeriodic(controller);
    }


}
