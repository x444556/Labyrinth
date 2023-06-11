package com.gmail.x544647.bettermc;

import java.awt.Color;

public class RenderThread3 extends RenderThread{
	private Program program;
	private int offset, length;
	
	RenderThread3(Program program, int offset, int length){
		super(program, offset, length);
		this.program = program;
		this.offset = offset;
		this.length = length;
	}
	
	public void run() {
		double radPerPixX = Math.toRadians(program.fovX / Program.imageX);
		
		double imgXHalf = Program.imageX/2.0;
		double imgYHalf = Program.imageY/2.0;

		double maxDistToCenterScreen = Math.sqrt(imgXHalf*imgXHalf + imgYHalf*imgYHalf);
		
		double c = Math.sqrt(2.0*(1.0/3.0)*(1.0/3.0));
		
		for(int pX=offset; pX<offset+length; pX++) {
			for(int pY = 0; pY<Program.imageY/2; pY++) {
				program.pixels[(Program.imageY - pY - 1) * Program.imageX + pX] = ApplyMult(new Color(100, 100, 100), 
						Math.sqrt((pX - imgXHalf)*(pX - imgXHalf) + (pY - imgYHalf)*(pY - imgYHalf)) / maxDistToCenterScreen).getRGB();
			}
			for(int pY = Program.imageY/2; pY<Program.imageY; pY++) {
				program.pixels[(Program.imageY - pY - 1) * Program.imageX + pX] = ApplyMult(new Color(50, 50, 255), 
						Math.sqrt((pX - imgXHalf)*(pX - imgXHalf) + (pY - imgYHalf)*(pY - imgYHalf)) / maxDistToCenterScreen).getRGB();
			}
			
			double angle = radPerPixX * (pX - imgXHalf) + program.CamRot.Y;
			double stepZ = Math.tan(angle) / 3.0;
			double stepX = Math.cos(angle) * c;
			
			double currX = program.CamPos.X - Math.IEEEremainder(program.CamPos.X, 1.0/3.0);
			double currZ = program.CamPos.Z - Math.IEEEremainder(program.CamPos.Z, 1.0/3.0);
			double step = 1.0 / 3.0;
			if(stepZ > stepX) {
				while(currX >= 0 && currX < program.Map.length && currZ >= 0 && currZ < program.Map[0].length) {
					
					if(program.Map[(int) currX][(int) currZ].HasRayHit(currX - (int) currX, currZ - (int) currZ)) {
						double distance = Math.sqrt(Math.pow(currX - program.CamPos.X, 2) + Math.pow(currZ - program.CamPos.Z, 2));
						
						int height = (int) (Program.imageY / (distance * Math.cos(radPerPixX * (pX - imgXHalf))) / 2.0);
						//int height = (int) (Program.imageY / distance / 2.0);
						if(height >= Program.imageY) {
							for(int i=0; i<Program.imageY; i++) {
								program.pixels[i * Program.imageX + pX] = ApplyMult(new Color(200, 75, 75), 
										Math.sqrt((pX - imgXHalf)*(pX - imgXHalf) + (i - imgYHalf)*(i - imgYHalf)) / maxDistToCenterScreen).getRGB();
							}
						}
						else if(height > 0) {
							int off = (Program.imageY - height) / 2;
							for(int i=0; i<height; i++) {
								program.pixels[(Program.imageY - (i + off) - 1) * Program.imageX + pX] = ApplyMult(new Color(200, 75, 75), 
										Math.sqrt((pX - imgXHalf)*(pX - imgXHalf) + (i - imgYHalf)*(i - imgYHalf)) / maxDistToCenterScreen).getRGB();
							}
						}
						break;
					}
					currX += step;
					currZ += stepX;
				}
			}
			else {
				while(currX >= 0 && currX < program.Map.length && currZ >= 0 && currZ < program.Map[0].length) {
					
					if(program.Map[(int) currX][(int) currZ].HasRayHit(currX - (int) currX, currZ - (int) currZ)) {
						double distance = Math.sqrt(Math.pow(currX - program.CamPos.X, 2) + Math.pow(currZ - program.CamPos.Z, 2));
						
						int height = (int) (Program.imageY / (distance * Math.cos(radPerPixX * (pX - imgXHalf))) / 2.0);
						//int height = (int) (Program.imageY / distance / 2.0);
						if(height >= Program.imageY) {
							for(int i=0; i<Program.imageY; i++) {
								program.pixels[i * Program.imageX + pX] = ApplyMult(new Color(200, 75, 75), 
										Math.sqrt((pX - imgXHalf)*(pX - imgXHalf) + (i - imgYHalf)*(i - imgYHalf)) / maxDistToCenterScreen).getRGB();
							}
						}
						else if(height > 0) {
							int off = (Program.imageY - height) / 2;
							for(int i=0; i<height; i++) {
								program.pixels[(Program.imageY - (i + off) - 1) * Program.imageX + pX] = ApplyMult(new Color(200, 75, 75), 
										Math.sqrt((pX - imgXHalf)*(pX - imgXHalf) + (i - imgYHalf)*(i - imgYHalf)) / maxDistToCenterScreen).getRGB();
							}
						}
						break;
					}
					currX += stepZ;
					currZ += step;
				}
			}
		}
	}
	public Color ApplyMult(Color c, double mult) {
		//return c;
		return new Color((int)(c.getRed() * (1.0 - mult)), (int)(c.getGreen() * (1.0 - mult)), (int)(c.getBlue() * (1.0 - mult)));
	}
}
