package org.phaenovum.robocup;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.navigation.OmniPilot;

@SuppressWarnings("deprecation")
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
		EV3LargeRegulatedMotor motorRight = new EV3LargeRegulatedMotor(MotorPort.B);
		EV3LargeRegulatedMotor motorLeft = new EV3LargeRegulatedMotor(MotorPort.A);
		EV3LargeRegulatedMotor motorBack = new EV3LargeRegulatedMotor(MotorPort.C);
		OmniPilot pilot = new OmniPilot(89f, 64f, motorBack, motorLeft, motorRight, false, true,
				LocalEV3.get().getPower());
		EV3LargeRegulatedMotor motor = new EV3LargeRegulatedMotor(MotorPort.D);
		Dribbler dribbler = new Dribbler(motor);

		pilot.setLinearSpeed(100);
		pilot.setAngularSpeed(10);
		long time = System.currentTimeMillis();
		int state = 0;
		boolean go = true;

		dribbler.dribble();

		while (Button.ESCAPE.isUp() && go) {

			if (time + 3000 < System.currentTimeMillis()) {
				time = System.currentTimeMillis();
				LCD.clear(3);
				Sound.beep();
				state++;
			}

			switch (state) {
			case 0:
				LCD.drawString("STAND", 0, 3);
				break;
			case 1:
				LCD.drawString("FORWARD", 0, 3);
				pilot.travel(100);
				break;
			case 2:
				LCD.drawString("BACKWARD", 0, 3);
				pilot.travel(-100);
				break;
			case 3:
				LCD.drawString("LEFT", 0, 3);
				pilot.rotate(360);
				break;
			case 4:
				LCD.drawString("RIGHT", 0, 3);
				pilot.rotate(-360);
				break;
			default:
				dribbler.release();
				Sound.beepSequenceUp();
				go = false;
				break;
			}
		}
		pilot.stop();
	}
}
