package org.phaenovum.robocup;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.I2CPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.HiTechnicIRSeekerV2;
import lejos.hardware.sensor.SensorMode;

public class HiTechnicIRSeekerV2Improved extends HiTechnicIRSeekerV2 {
	private int size = 6;
	private byte[] buf = new byte[size];

	public HiTechnicIRSeekerV2Improved(I2CPort port) {
		super(port);
		init();
	}

	public HiTechnicIRSeekerV2Improved(Port port) {
		super(port);
		init();
	}

	protected void init() {
		setModes(new SensorMode[] { super.getMode(0), super.getMode(1), new ModulatedDistanceMode() });
	}

	public SensorMode getModulatedDistanceMode() {
		return getMode(2);
	}

	private class ModulatedDistanceMode implements SensorMode {
		@Override
		public int sampleSize() {
			return size;
		}

		@Override
		public void fetchSample(float[] sample, int offset) {
			getData(0x49, buf, size);

			for (int i = 0; i < size; i++) {
				sample[offset + i] = buf[i];
			}
		}

		@Override
		public String getName() {
			return "ModulatedStrength";
		}
	}

	public static void main(String[] args) {
		HiTechnicIRSeekerV2Improved seeker = new HiTechnicIRSeekerV2Improved(SensorPort.S3);
		float[] sample = new float[seeker.getModulatedDistanceMode().sampleSize()];

		while (Button.ESCAPE.isUp()) {
			seeker.getModulatedDistanceMode().fetchSample(sample, 0);

			for (int i = 0; i < seeker.getModulatedDistanceMode().sampleSize(); i++) {
				LCD.drawString("" + i + ": " + sample[i], 0, i);
			}
		}
	}
}
