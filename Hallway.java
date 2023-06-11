package com.gmail.x544647.bettermc;

public class Hallway {
	public boolean[][] Layout;
	
	public enum Face{
		North,
		South,
		East,
		West,
		NONE
	}
	
	public static Hallway Empty = new Hallway(new boolean[][] {
		{false, false, false},
		{false, false, false},
		{false, false, false}});
	public static Hallway Solid = new Hallway(new boolean[][] {
		{true, true, true},
		{true, true, true},
		{true, true, true}});
	public static Hallway Straight = new Hallway(new boolean[][] {
		{true, false, true},
		{true, false, true},
		{true, false, true}});
	public static Hallway Cross = new Hallway(new boolean[][] {
		{true, false, true},
		{false, false, false},
		{true, false, true}});

	public Hallway(boolean[][] layout) {
		Layout = layout;
	}

	public boolean HasRayHit(double rayX, double rayZ) {
		return Layout[(int) (rayX * 3)][(int) (rayZ * 3)];
	}
	public Hallway Clone() {
		Hallway h = new Hallway(new boolean[3][3]);
		for(int z=0; z<3; z++) {
			for(int x=0; x<3; x++) {
				h.Layout[x][z] = Layout[x][z];
			}
		}
		return h;
	}
	public Hallway Rotate90Once() {
		boolean temp = Layout[0][1];
		Layout[0][1] = Layout[1][0];
		Layout[1][0] = Layout[2][1];
		Layout[2][1] = Layout[1][2];
		Layout[1][2] = temp;
		return this;
	}
}
