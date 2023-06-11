package com.gmail.x544647.bettermc;

public class Vector3 {
	public double X, Y, Z;
	
	public Vector3(double x, double y, double z) {
		X=x;
		Y=y;
		Z=z;
	}
	public String ToString() {
		return "(x="+X+", y="+Y+", z="+Z+")";
	}
	public Vector3 copy() {
		return new Vector3(X, Y, Z);
	}
	public Vector3 Mult(Vector3 v) {
		X *= v.X;
		Y *= v.Y;
		Z *= v.Z;
		return this;
	}
	public Vector3 Add(Vector3 v) {
		X += v.X;
		Y += v.Y;
		Z += v.Z;
		return this;
	}
	public Vector3 Decimals() {
		return new Vector3(X-(int)X, Y-(int)Y, Z-(int)Z);
	}
	
	public static Vector3 Zero() { return new Vector3(0, 0, 0); }
	public static Vector3 One() { return new Vector3(1, 1, 1); }
}
