package com.thalesians.linear;

public final class ArithmeticsUtils {
	private static float machineepsilonfloat = calcMachineEpsilonFloat();
	private static double machineepsilondouble = calcMachineEpsilonDouble();
	
	private ArithmeticsUtils() {
		throw new AssertionError("This class cannot be instantiated");
	}
	
	private static float calcMachineEpsilonFloat() {
		float macheps = 1.0f;
		do {
			macheps /= 2.0f;
		} while ((float) (1.0 + macheps / 2.0) != 1.0f);
		return macheps;
	}
	
	private static double calcMachineEpsilonDouble() {
		double macheps = 1.0;
		do {
			macheps /= 2.0;
		} while (1.0 + macheps / 2.0 != 1.0);
		return macheps;
	}
	
	public static float getMachineEpsilonFloat() {
		return machineepsilonfloat;
	}
	
	public static double getMachineEpsilonDouble() {
		return machineepsilondouble;
	}
	
	public static boolean isEqual(double a, double b, double epsilon) {
		if (Double.compare(a, b) == 0) {
			return true;
		}
		return Math.abs(a - b) <= epsilon * Math.abs(a);
	}
	
	public static boolean isEqual(double a, double b) {
		return isEqual(a, b, getMachineEpsilonDouble());
	}
	
	public static boolean isGreaterThan(double a, double b, double epsilon) {
		return a - b > epsilon;
	}
	
	public static boolean isGreaterThan(double a, double b) {
		return isGreaterThan(a, b, getMachineEpsilonDouble());
	}
}
