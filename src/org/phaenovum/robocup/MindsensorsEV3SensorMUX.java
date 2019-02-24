package org.phaenovum.robocup;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.I2CPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.I2CSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;

public class MindsensorsEV3SensorMUX extends I2CSensor {

	private static final int I2C_C1 = 0xA0;
	private static final int I2C_C2 = 0xA2;
	private static final int I2C_C3 = 0xA4;
	private static final int REG_MODE = 0x52;
	private static final int REG_READ = 0x54;
	private static final int REG_READY = 0x74;
	protected static final int DEFAULT_I2C_ADDRESS = I2C_C1;

	public MindsensorsEV3SensorMUXPort C1;
	public MindsensorsEV3SensorMUXPort C2;
	public MindsensorsEV3SensorMUXPort C3;

	protected void init() {
		setModes(new SensorMode[] { new DistanceMode(), new ListenMode() });
		C1 = new MindsensorsEV3SensorMUXPort(I2C_C1);
		C2 = new MindsensorsEV3SensorMUXPort(I2C_C2);
		C3 = new MindsensorsEV3SensorMUXPort(I2C_C3);
	}

	private MindsensorsEV3SensorMUX(I2CPort port, int address) {
		super(port, address);
		init();
	}

	public MindsensorsEV3SensorMUX(I2CPort port) {
		super(port);
		init();
	}

	private MindsensorsEV3SensorMUX(Port port, int address) {
		super(port, address);
		init();
	}

	public MindsensorsEV3SensorMUX(Port port) {
		super(port);
		init();
	}

	private void switchMode(byte mode) {
		byte[] test = new byte[1];

		sendData(REG_MODE, mode);
		getData(REG_MODE, test, 1);
		if (test[0] != mode)
			throw new IllegalArgumentException("Invalid sensor mode");
	}

	/**
	 * Represents a Ultrasonic sensor in distance mode
	 */
	private class DistanceMode implements SampleProvider, SensorMode {
		private static final byte MODE = 0;
		private static final float toSI = 0.001f;

		@Override
		public String getName() {
			return "Distance";
		}

		@Override
		public int sampleSize() {
			return 1;
		}

		@Override
		public void fetchSample(float[] sample, int offset) {
			final int DATA_SIZE = 2;
			byte[] data = new byte[DATA_SIZE];
			int[] distance = new int[DATA_SIZE];

			switchMode(MODE);
			getData(REG_READY, data, 1);
			if (data[0] == 1) {
				getData(REG_READ, data, DATA_SIZE);

				for (int i = 0; i < DATA_SIZE; i++) {
					if (data[i] < 0) {
						distance[i] = 256 + data[i];
					} else {
						distance[i] = data[i];
					}
					// LCD.drawString("" + distance[i], 0, i);
				}
				int raw = distance[0] + (distance[1] << 8);
				sample[offset] = (raw == 2550) ? Float.POSITIVE_INFINITY : (float) raw * toSI;
			} else {
				sample[offset] = Float.NaN;
			}
		}
	}

	/**
	 * Represents a Ultrasonic sensor in listen mode
	 */
	private class ListenMode implements SampleProvider, SensorMode {
		private static final byte MODE = 2;

		@Override
		public int sampleSize() {
			return 1;
		}

		@Override
		public void fetchSample(float[] sample, int offset) {
			byte[] data = new byte[1];
			switchMode(MODE);
			getData(REG_READY, data, 1);
			if (data[0] == 1) {
				getData(REG_READ, data, 1);
				sample[offset] = data[0];
			} else {
				sample[offset] = Float.NaN;
			}
		}

		@Override
		public String getName() {
			return "Listen";
		}

	}

	class MindsensorsEV3SensorMUXPort {
		private int i2c_address;

		public MindsensorsEV3SensorMUXPort(int i2c_address) {
			this.i2c_address = i2c_address;
		}

		/**
		 * <b>Lego EV3 Ultrasonic sensor, Listen mode</b><br>
		 * Listens for the presence of other ultrasonic sensors.
		 * 
		 * <p>
		 * <b>Size and content of the sample</b><br>
		 * The sample contains one elements indicating the presence of another
		 * ultrasonic sensor. A value of 1 indicates that the sensor detects
		 * another ultrasonic sensor.
		 * 
		 * @return A sampleProvider
		 */
		public SampleProvider getListenMode() {
			address = i2c_address;
			return getMode(1);
		}

		/**
		 * <b>Lego EV3 Ultrasonic sensor, Distance mode</b><br>
		 * Measures distance to an object in front of the sensor
		 * 
		 * <p>
		 * <b>Size and content of the sample</b><br>
		 * The sample contains one elements representing the distance (in
		 * metres) to an object in front of the sensor. unit).
		 * 
		 * @return A sampleProvider
		 */
		public SampleProvider getDistanceMode() {
			address = i2c_address;
			return getMode(0);
		}

	}

	public static void main(String[] args) {
		MindsensorsEV3SensorMUX mux = new MindsensorsEV3SensorMUX(SensorPort.S3);

		float[] sample = new float[mux.C1.getDistanceMode().sampleSize() * 3];

		while (Button.ESCAPE.isUp()) {
			if (System.currentTimeMillis() % 200 == 0) {
				mux.C1.getDistanceMode().fetchSample(sample, 0);
				mux.C2.getDistanceMode().fetchSample(sample, 1);
				mux.C3.getDistanceMode().fetchSample(sample, 2);
				LCD.drawString("Distance: " + sample[0], 0, 1);
				LCD.drawString("Distance: " + sample[1], 0, 2);
				LCD.drawString("Distance: " + sample[2], 0, 3);
				mux.C1.getListenMode().fetchSample(sample, 0);
				mux.C2.getListenMode().fetchSample(sample, 1);
				mux.C3.getListenMode().fetchSample(sample, 2);
				LCD.drawString("Listen: " + sample[0], 0, 4);
				LCD.drawString("Listen: " + sample[1], 0, 5);
				LCD.drawString("Listen: " + sample[2], 0, 6);
			}
		}
		mux.close();
	}
}
