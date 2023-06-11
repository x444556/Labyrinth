package com.gmail.x544647.bettermc;

public class Point {
	public double X, Y;

	public Point(double x, double y) {
		X = x;
		Y = y;
	}
	
	public static Point Parse(String str) {
		Point P = new Point(0, 0);
		String[] sp = str.split("\\|");
		P.X = Double.parseDouble(sp[0].replace(',', '.'));
		P.Y = Double.parseDouble(sp[1].replace(',', '.'));
		return P;
	}

	public Point Clone() {
		return new Point(X, Y);
	}
	public double DistanceTo(Point p) {
		double a = p.X - X;
		double b = p.Y - Y;
		return Math.sqrt(a*a + b*b);
	}
}
