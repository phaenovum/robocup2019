package org.phaenovum.robocup;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.navigation.OmniPilot;

@SuppressWarnings("deprecation")
public class Striker {
	OmniPilot pilot;
	EV3GyroSensor gyro;
	private float[] sample;

	Striker(EV3GyroSensor gyro, OmniPilot pilot, Dribbler dribbler) {
		this.gyro = gyro;
		this.gyro.reset();
		Sound.beep();
		Sound.pause(1000);
		sample = new float[gyro.getAngleMode().sampleSize()];
		this.pilot = pilot;
	}

	boolean strike() {

		gyro.getAngleMode().fetchSample(sample, 0);
		int angle = (int) sample[0];
		angle = angle % 360;
		if (sample[0] < 0) {
			angle = angle - 360;
		}
		if (0 == System.currentTimeMillis() % 100) {
			LCD.drawString("Gyro: " + angle, 0, 5);
		}

		pilot.setAngularSpeed(20);
		if (Math.abs(angle) <= 1) {
			Sound.beep();
			pilot.setLinearSpeed(200);
			pilot.forward();
		} else {
			pilot.setAngularSpeed(20);
			pilot.rotate(angle);
		}
		// pilot.spinningMove(200, 10, (int) angle);
		return false;
	}

	public static void main(String[] args) {
		EV3GyroSensor gyro = new EV3GyroSensor(SensorPort.S1);
		EV3LargeRegulatedMotor motorRight = new EV3LargeRegulatedMotor(MotorPort.B);
		EV3LargeRegulatedMotor motorLeft = new EV3LargeRegulatedMotor(MotorPort.A);
		EV3LargeRegulatedMotor motorBack = new EV3LargeRegulatedMotor(MotorPort.C);
		EV3LargeRegulatedMotor motorDribbler = new EV3LargeRegulatedMotor(MotorPort.D);
		OmniPilot pilot = new OmniPilot(89f, 64f, motorBack, motorLeft, motorRight, false, true,
				LocalEV3.get().getPower());
		Dribbler dribbler = new Dribbler(motorDribbler);

		Striker str = new Striker(gyro, pilot, dribbler);

		pilot.rotate(90);

		while (Button.ESCAPE.isUp()) {
			if (str.strike()) {
				break;
			}
		}
	}
}
