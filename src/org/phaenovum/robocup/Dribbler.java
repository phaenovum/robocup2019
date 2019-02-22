package org.phaenovum.robocup;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;

public class Dribbler {

	EV3LargeRegulatedMotor motor;

	public Dribbler(EV3LargeRegulatedMotor motor) {
		this.motor = motor;
		this.motor.setSpeed(motor.getMaxSpeed());
	}

	public void dribble() {
		motor.forward();
	}

	public void release() {
		motor.rotate(-1440, true);
	}

	public static void main(String[] args) {
		EV3LargeRegulatedMotor motor = new EV3LargeRegulatedMotor(MotorPort.D);
		Dribbler dribbler = new Dribbler(motor);
		while (Button.ESCAPE.isUp()) {
			dribbler.dribble();
			Sound.pause(3000);
			Sound.beep();
			dribbler.release();
			Sound.beepSequenceUp();
		}
	}

}
