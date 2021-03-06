package org.usfirst.frc.team1251;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.interfaces.Gyro;

/**
 * Created by Jared on 2/10/2017.
 */
public class TT_Util {

    static double lDriveSpeed = 0;
    static double rDriveSpeed = 0;
    static boolean firstDrive = true;

    static boolean firstDriveBackward = true;

    static double lRotateSpeed = 0;
    static double rRotateSpeed = 0;
    static double startAngle = 0;
    static double endAngle = 0;
    static boolean firstRotate = true;

    static int pauseCount = 0;

    public static double convertTicksToRPMsHanger(double speed){
        // divide by ticks per revolution
        speed /= 1024;
        // divide by seconds per minute
        speed *= 60;
        return speed;
    }
    
    public static double convertTicksToRPMs(double speed){
        // divide by ticks per revolution
        speed /= 550;
        // divide by seconds per minute
        speed *= 60;
        return speed;
    }
    public static double convertRPMsToTicks(double speed){
        // multiply by ticks per revolution
        speed *= 552.6666666;
        // divide by seconds per minute
        speed /= 60;
        return  speed;
    }

    // return 1 if still driving, return 0 if done
    public static int driveForward(RobotDrive baseDrive, DoubleSolenoid baseShifter, Encoder lEncoder, Encoder rEncoder, double distance) {
        if (firstDrive){
            lEncoder.reset();
            rEncoder.reset();
            lDriveSpeed = -0.5;
            rDriveSpeed = -0.5;
            firstDrive = false;
        }

        if (lEncoder.getRate()<1.2) {
            lDriveSpeed -= 0.01;
        }
        else if (lEncoder.getRate()>1.25) {
            lDriveSpeed += 0.01;
        }

        if (rEncoder.getRate()<1.2) {
            rDriveSpeed -= 0.01;
        }
        else if (rEncoder.getRate()>1.25) {
            rDriveSpeed += 0.01;
        }

        baseShifter.set(DoubleSolenoid.Value.kReverse);
        if (lEncoder.getDistance() < distance && lEncoder.getDistance() < distance) {
            baseDrive.tankDrive(lDriveSpeed, rDriveSpeed);
            return 1;
        }
        else {
            firstDrive = true;
            baseDrive.tankDrive(0, 0);
            return 0;
        }
    }

    // return 1 if still driving, return 0 if done
    public static int driveBackward(RobotDrive baseDrive, DoubleSolenoid baseShifter, Encoder lEncoder, Encoder rEncoder, double distance) {
        if (firstDriveBackward) {
            lEncoder.reset();
            rEncoder.reset();
            lDriveSpeed = 0.5;
            rDriveSpeed = 0.5;
            firstDriveBackward = false;
        }

        if (lEncoder.getRate() > -1.2) {
            lDriveSpeed += 0.01;
        } else if (lEncoder.getRate() < -1.3) {
            lDriveSpeed -= 0.01;
        }

        if (rEncoder.getRate() > -1.2) {
            rDriveSpeed += 0.01;
        } else if (rEncoder.getRate() < -1.3) {
            rDriveSpeed -= 0.01;
        }

        baseShifter.set(DoubleSolenoid.Value.kReverse);
        if (lEncoder.getDistance() >= -distance && lEncoder.getDistance() >= -distance) {
            baseDrive.tankDrive(lDriveSpeed, rDriveSpeed);
            return 1;
        } else {
            firstDriveBackward = true;
            baseDrive.tankDrive(0, 0);
            return 0;
        }
    }

    /**
     *
     * @param rotation rotation of bot from current state
     * @param baseDrive
     * @param gyro
     * @return
     */
    public static int rotateBot(double rotation, RobotDrive baseDrive, Gyro gyro) {
        if (firstRotate){
            startAngle = gyro.getAngle();
            endAngle = gyro.getAngle() + rotation;
            firstRotate = false;
        }

        lRotateSpeed = -(Math.abs(gyro.getAngle() - endAngle) * (1.0 / rotation)); // get difference between now and end and multiply by 1/ total degrees to travel
        rRotateSpeed = Math.abs(gyro.getAngle() - endAngle) * (1.0 / rotation);

        if (!(Math.abs(gyro.getAngle()) < Math.abs(endAngle) + 1 && Math.abs(gyro.getAngle()) > Math.abs(endAngle) - 1)) {
            baseDrive.tankDrive(lRotateSpeed, rRotateSpeed);
            return 1;
        } else {
            firstRotate = true;
            return 0;
        }

    }

    public static int pause(int loops){
        if (pauseCount < loops){
            TT_DriveUtil.INSTANCE.resetPIDs();
            pauseCount++;
            return 1;
        } else {
            pauseCount = 0;
            return 0;
        }
    }

    public static double inchesToMeters(double inches) {
        return inches * 0.02539999983236;
    }

}
