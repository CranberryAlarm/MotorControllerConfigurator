// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.ArrayList;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.IntegerArraySubscriber;
import edu.wpi.first.networktables.IntegerArrayTopic;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

// import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kBreak = "Brake";
  private static final String kCoast = "Coast";
  private final SendableChooser<String> m_modeChooser = new SendableChooser<>();
  private static final String kForward = "Forward";
  private static final String kReverse = "Reverse";
  private final SendableChooser<String> m_directionChooser = new SendableChooser<>();

  // private TalonSRX m_TalonSRX = new TalonSRX(0);
  IntegerArrayTopic idArrayTopic;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_modeChooser.setDefaultOption("Break Mode", kBreak);
    m_modeChooser.addOption("Coast Mode", kCoast);
    SmartDashboard.putData("Motor Mode", m_modeChooser);

    m_directionChooser.setDefaultOption("Forward", kForward);
    m_directionChooser.addOption("Reverse", kReverse);
    SmartDashboard.putData("Motor Direction", m_directionChooser);

    SmartDashboard.putNumberArray("ids", new double[] {});
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  ArrayList<Integer> idArray = new ArrayList<Integer>();
  ArrayList<CANSparkMax> sparkMaxs = new ArrayList<CANSparkMax>();
  String motorMode = "";
  String motorDir = "";

  @Override
  public void robotPeriodic() {
    double[] input = SmartDashboard.getNumberArray("ids", new double[] {});

    ArrayList<Integer> inputArray = new ArrayList<Integer>();
    for(int i = 0; i < input.length; i++) {
      inputArray.add((int)input[i]);
    }

    String mode = m_modeChooser.getSelected();
    String dir = m_directionChooser.getSelected();

    if(!inputArray.containsAll(idArray) ||
       !idArray.containsAll(inputArray) ||
       !mode.equals(motorMode) ||
       !dir.equals(motorDir)) {
      motorMode = mode;
      motorDir = dir;
      idArray.clear();
      idArray.addAll(inputArray);
      reInstantiateSparkMaxs();
    }
  }


  private void reInstantiateSparkMaxs() {
    System.out.println(idArray);
    
    for(CANSparkMax sparkMax : sparkMaxs) {
      sparkMax.close();
    }
    sparkMaxs.clear();

    for(int id : idArray) {
      CANSparkMax sparkMax = new CANSparkMax(id, MotorType.kBrushless);

      switch(motorDir) {
        case kForward:
          sparkMax.setInverted(false);
          break;
        case kReverse:
          sparkMax.setInverted(true);
          break;
      }

      switch(motorMode) {
        case kBreak:
          sparkMax.setIdleMode(IdleMode.kBrake);
          break;
        case kCoast:
          sparkMax.setIdleMode(IdleMode.kCoast);
          break;
      }
      sparkMaxs.add(sparkMax);
    }
  }

  /** This function is called once when auto is enabled. */
  @Override
  public void autonomousInit() {
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}
