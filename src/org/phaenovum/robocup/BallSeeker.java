package org.phaenovum.robocup;

import org.phaenovum.robocup.MindsensorsEV3SensorMUX.MindsensorsEV3SensorMUXPort;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.HiTechnicIRSeekerV2;
import lejos.robotics.navigation.OmniPilot;

@SuppressWarnings("deprecation")
public class BallSeeker {

	HiTechnicIRSeekerV2 seeker;
	MindsensorsEV3SensorMUXPort mux_port;
	OmniPilot pilot;
	float[] sample_us;
	float[] sample_ir;
	final float SPEED = 500;
	final float LIMIT = 0.05f;
	final float FACTOR = 1.7f;
	// private float prev_angle = 0;

	BallSeeker(HiTechnicIRSeekerV2 seeker, MindsensorsEV3SensorMUXPort mux_port, OmniPilot pilot) {
		this.seeker = seeker;
		this.mux_port = mux_port;
		this.pilot = pilot;
		sample_ir = new float[this.seeker.getModulatedMode().sampleSize()];
		sample_us = new float[this.mux_port.getDistanceMode().sampleSize()];
	}

	boolean seekBall() {
		mux_port.getDistanceMode().fetchSample(sample_us, 0);
		float dist = sample_us[0];
		if (dist < LIMIT) {
			pilot.stop();
			LCD.clear();
			return true;
		}
		/**
		 * angle with zero forward, anti-clockwise positive
		 * 
		 * <pre>
		 *        0
		 *        ^
		 *        |
		 * 90 <-- X --> -90
		 *      /   \
		 *    |/_   _\|
		 *   150     -150
		 * </pre>
		 */
		seeker.getModulatedMode().fetchSample(sample_ir, 0);
		int angle = (int) sample_ir[0];
		if (angle == 0) {
			pilot.moveStraight(SPEED, 0);
		} else if (angle <= 150 && angle >= -150 /* && angle != prev_angle */) {
			int angSpeed = Math.abs(angle);
			if (angSpeed > pilot.getMaxAngularSpeed()) {
				angSpeed = (int) pilot.getMaxAngularSpeed();
			}
			pilot.spinningMove(SPEED, angSpeed, angle);
		} else {
			Sound.beep();
			int dir = (int) (Math.random() * 2) - 1;
			pilot.rotate(dir * 90);
		}
		// prev_angle = angle;
		return false;
	}

	public static void main(String[] args) {
		HiTechnicIRSeekerV2 seeker = new HiTechnicIRSeekerV2(SensorPort.S3);
		MindsensorsEV3SensorMUX mux = new MindsensorsEV3SensorMUX(SensorPort.S2);
		EV3LargeRegulatedMotor motorRight = new EV3LargeRegulatedMotor(MotorPort.B);
		EV3LargeRegulatedMotor motorLeft = new EV3LargeRegulatedMotor(MotorPort.A);
		EV3LargeRegulatedMotor motorBack = new EV3LargeRegulatedMotor(MotorPort.C);
		EV3LargeRegulatedMotor motorDribbler = new EV3LargeRegulatedMotor(MotorPort.D);
		OmniPilot pilot = new OmniPilot(89f, 64f, motorBack, motorLeft, motorRight, false, true,
				LocalEV3.get().getPower());

		BallSeeker bs = new BallSeeker(seeker, mux.C3, pilot);
		Dribbler dribbler = new Dribbler(motorDribbler);

		dribbler.dribble();

		while (Button.ESCAPE.isUp()) {

			if (bs.seekBall()) {
				Sound.beepSequenceUp();
				break;
			}
		}
	}
}
