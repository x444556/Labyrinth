package com.gmail.x544647.bettermc;

import java.awt.Color;

public class RenderThread2 extends RenderThread{
	private Program program;
	private int offset, length;
	
	RenderThread2(Program program, int offset, int length){
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
		
		for(int pX=offset; pX<offset+length; pX++) {
			for(int pY = 0; pY<Program.imageY/2; pY++) {
				program.pixels[(Program.imageY - pY - 1) * Program.imageX + pX] = ApplyMult(new Color(100, 100, 100), 
						Math.sqrt((pX - imgXHalf)*(pX - imgXHalf) + (pY - imgYHalf)*(pY - imgYHalf)) / maxDistToCenterScreen).getRGB();
			}
			for(int pY = Program.imageY/2; pY<Program.imageY; pY++) {
				program.pixels[(Program.imageY - pY - 1) * Program.imageX + pX] = ApplyMult(new Color(50, 50, 255), 
						Math.sqrt((pX - imgXHalf)*(pX - imgXHalf) + (pY - imgYHalf)*(pY - imgYHalf)) / maxDistToCenterScreen).getRGB();
			}
			
			double distance = 0;
			
			double axc = Math.cos(radPerPixX * (pX - imgXHalf) + program.CamRot.Y);
			double axs = Math.sin(radPerPixX * (pX - imgXHalf) + program.CamRot.Y);
			
			double xa = axc * program.stepLen;
			double za = axs * program.stepLen;
			double currX = program.CamPos.X;
			double currZ = program.CamPos.Z;
			while(distance < program.ViewDistInBlocks) {
				distance += program.stepLen;
				
				currX += xa;
				currZ += za;

				if(program.Map[(int) currX][(int) currZ].HasRayHit(currX - (int) currX, currZ - (int) currZ)) {
					
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
			}
		}
	}
	public Color ApplyMult(Color c, double mult) {
		//return c;
		return new Color((int)(c.getRed() * (1.0 - mult)), (int)(c.getGreen() * (1.0 - mult)), (int)(c.getBlue() * (1.0 - mult)));
	}
}
