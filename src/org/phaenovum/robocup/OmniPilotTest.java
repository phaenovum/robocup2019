package org.phaenovum.robocup;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.HiTechnicIRSeekerV2;
import lejos.robotics.navigation.OmniPilot;

public class OmniPilotTest {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		EV3LargeRegulatedMotor motorRight = new EV3LargeRegulatedMotor(MotorPort.B);
		EV3LargeRegulatedMotor motorLeft = new EV3LargeRegulatedMotor(MotorPort.A);
		EV3LargeRegulatedMotor motorBack = new EV3LargeRegulatedMotor(MotorPort.C);
		OmniPilot pilot = new OmniPilot(89f, 64f, motorBack, motorLeft, motorRight, false, true,
				LocalEV3.get().getPower());

		pilot.setLinearSpeed(400);
		long time = System.currentTimeMillis();
		int state = 0;
		boolean go = true;

		while (Button.ESCAPE.isUp() && go) {

			if (time + 3000 < System.currentTimeMillis()) {
				time = System.currentTimeMillis();
				LCD.clear(3);
				Sound.beep();
				state++;
			}

			switch (state) {
			case 0:
				LCD.drawString("FORWARD", 0, 3);
				pilot.forward();
				break;

			case 1:
				LCD.drawString("BACKWARD", 0, 3);
				pilot.backward();
				break;

			case 2:
				LCD.drawString("RIGHT", 0, 3);
				pilot.moveStraight(400, -90);
				break;
			case 3:
				LCD.drawString("LEFT", 0, 3);
				pilot.moveStraight(400, 90);
				break;

			case 4:
				LCD.drawString("ROTATE LEFT 90", 0, 3);
				pilot.rotate(90);
				break;
				
			case 5:
				LCD.drawString("ROTATE RIGHT 90", 0, 3);
				pilot.rotate(-90);
				break;
				
			case 6:
				LCD.drawString("SPIN MOVE LEFT", 0, 3);
				pilot.spinningMove(400, 60, 90);
//				LCD.drawString("ROTATE LEFT", 0, 3);
//				pilot.rotateLeft();
				break;
				
			case 7:
				LCD.drawString("SPIN MOVE RIGHT", 0, 3);
				pilot.spinningMove(400, 60, -90);
//				LCD.drawString("ROTATE RIGHT", 0, 3);
//				pilot.rotateRight();
				break;
				
			default:
				go = false;
				break;
			}

		}
		pilot.stop();
	}

}
