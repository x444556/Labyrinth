package com.gmail.x544647.bettermc;

import java.awt.Color;

public class RenderThread extends Thread{
	private Program program;
	private int offset, length;
	
	RenderThread(Program program, int offset, int length){
		this.offset = offset;
		this.length = length;
		this.program = program;
	}
	
	public void run() {
		double radPerPixX = Math.toRadians(program.fovX / Program.imageX);
		double radPerPixY = Math.toRadians(program.fovY / Program.imageY);
		
		double imgYHalf = Program.imageY/2;
		double imgXHalf = Program.imageX/2;
		
		int pYe = offset + length;
		for(int pY=offset; pY<pYe; pY++) {
			double ayc = Math.cos(radPerPixY * (pY - imgYHalf) + program.CamRot.Z);
			double ays = Math.sin(radPerPixY * (pY - imgYHalf) + program.CamRot.Z);
			double ya = ays * program.stepLen;
			for(int pX=0; pX<Program.imageX; pX++) {
				double distance = 0;
				
				double axc = Math.cos(radPerPixX * (pX - imgXHalf) + program.CamRot.Y) * ayc;
				double axs = Math.sin(radPerPixX * (pX - imgXHalf) + program.CamRot.Y) * ayc;
				
				double xa = axc * program.stepLen;
				double za = axs * program.stepLen;
				double currX = program.CamPos.X;
				double currY = program.CamPos.Y;
				double currZ = program.CamPos.Z;
				while(distance < program.ViewDistInBlocks) {
					distance += program.stepLen;
					
					currX += xa;
					currY += ya;
					currZ += za;

					if(currY < 0) {
						program.pixels[(Program.imageY - pY - 1) * Program.imageX + pX] = new Color(100, 100, 100).getRGB();
						break;
					}
					else if(currY > 0.5) {
						program.pixels[(Program.imageY - pY - 1) * Program.imageX + pX] = new Color(50, 50, 255).getRGB();
						break;
					}
					else if(program.Map[(int) currX][(int) currZ].HasRayHit(currX - (int) currX, currZ - (int) currZ)) {
						program.pixels[(Program.imageY - pY - 1) * Program.imageX + pX] = new Color(200, 75, 75).getRGB();
						break;
					}
				}
			}
		}
	}
}
