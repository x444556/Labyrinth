package com.gmail.x544647.bettermc;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Program extends JFrame implements Runnable, MouseInputListener, KeyListener {
	private static final long serialVersionUID = 1L;
	private Thread thread;
	private boolean running;
	private BufferedImage image;
	public int[] pixels;
	public static int windowX = 600, windowY = 600;
	public static int imageX = 200, imageY = 200;
	public double targetFPS = -1;
	public double fovX = 70;
	public double fovY = 70;
	public double ViewDistInBlocks = 12;
	public double stepLen = 1.0 / 512;
	public Hallway[][] Map = new Hallway[25][25];
	public boolean rendering = false;
	public boolean enableLookUpDown = false;
	public boolean useRenderThread2insteadOf1 = true;
	
	public double deltaTime = 0;
	
	public int CamTileX = 12, CamTileZ = 12;
	public Vector3 CamPos = new Vector3(CamTileX+0.5, 0.25, CamTileZ+0.5);
	public Vector3 CamRot = new Vector3(0, Math.toRadians(90.0), 0);
	public double CamSpeed = 10;
	public double CamRotSpeed = 360;
	public Vector3 LastCamPos;
	
	public Program() {
		thread = new Thread(this);
		image = new BufferedImage(imageX, imageY, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		
		setSize(windowX, windowY);
		setResizable(false);
		setTitle("Play");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.black);
		setLocationRelativeTo(null);
		setLayout(null);
		
		setVisible(true);
		
		getContentPane().addMouseListener(this);
		getContentPane().addMouseMotionListener(this);
		addKeyListener(this);
	}
	private synchronized void start() {
		running = true;
		thread.start();
	}
	public synchronized void stop() {
		running = false;
		try {
			thread.join();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	private BufferStrategy bs;
	private Graphics g;
	public void render() {
		if(bs == null) {
			createBufferStrategy(1);
			bs = getBufferStrategy();
			g = bs.getDrawGraphics();
		}
		g.drawImage(image, 0, 0, windowX, windowY, null);
		bs.show();
	}
	private void simToImg() {
		rendering = true;
		int threads = 4;
    	RenderThread[] AAThreads = new RenderThread[threads];
    	int ppt = (int) Math.ceil(imageY * 1.0 / threads);
    	if(!enableLookUpDown) ppt = (int) Math.ceil(imageX * 1.0 / threads);
    	for(int i=0; i<threads; i++) {
    		RenderThread aat = (enableLookUpDown ? (useRenderThread2insteadOf1 ? new RenderThread(this, i*ppt, ppt) : 
    			new RenderThread(this, i*ppt, ppt)) : new RenderThread3(this, i*ppt, ppt));
    		AAThreads[i] = aat;
    		aat.start();
    	}
    	for(int i=0; i<threads; i++) {
    		try {
				AAThreads[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	rendering = false;
	}
	private int randomInt(int min, int max) {
		return (int)(Math.random() * (max - min) + min);
	}
	private void drawLine(Point p0, Point p1, Color c) {
		int x = (int) (p0.X * imageX);
		int y = (int) (p0.Y * imageY);
		int x2 = (int) (p1.X * imageX);
		int y2 = (int) (p1.Y * imageY);
		
		int w = (int) (x2 - x) ;
	    int h = (int) (y2 - y) ;
	    int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0 ;
	    if (w<0) dx1 = -1 ; else if (w>0) dx1 = 1 ;
	    if (h<0) dy1 = -1 ; else if (h>0) dy1 = 1 ;
	    if (w<0) dx2 = -1 ; else if (w>0) dx2 = 1 ;
	    int longest = Math.abs(w) ;
	    int shortest = Math.abs(h) ;
	    if (!(longest>shortest)) {
	        longest = Math.abs(h) ;
	        shortest = Math.abs(w) ;
	        if (h<0) dy2 = -1 ; else if (h>0) dy2 = 1 ;
	        dx2 = 0 ;            
	    }
	    int numerator = longest >> 1 ;
	    for (int i=0;i<=longest;i++) {
	    	pixels[x + imageX*y] = c.getRGB();

	        numerator += shortest ;
	        if (!(numerator<longest)) {
	            numerator -= longest ;
	            x += dx1 ;
	            y += dy1 ;
	        } else {
	            x += dx2 ;
	            y += dy2 ;
	        }
	    }
	}
	public void run() {
		requestFocus();
		int frameCount = 0;
		long startMillis = System.currentTimeMillis();
		while(true) {
			long sn = System.nanoTime();
			long sms = System.nanoTime();
			simToImg();
			render();
			long ems = System.nanoTime();
			
			if(targetFPS > 0) {
				if((1/targetFPS*1000) - ((ems-sms)/1000000.0) > 0){
					try {
						Thread.sleep((long) ((1/targetFPS*1000) - ((ems-sms)/1000000.0)));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			if(frameCount % 30 == 0) setTitle("last Frame: " + ((ems-sms)/1000.0) + " us    AVG(total): " + 
					(Math.round(frameCount / ((System.currentTimeMillis() - startMillis) / 1000.0) * 100.0) / 100.0) + " fps    Target: " + targetFPS + " fps");
			
			frameCount++;
			long deltaNanos = System.nanoTime() - sn;
			deltaTime = deltaNanos / 1000000000.0;
		}
	}
	
	public static Program sim;
	public static void main(String [] args) {
		sim = new Program();
		for(int z=0; z<sim.Map.length; z++) {
			for(int x=0; x<sim.Map[0].length; x++) {
				sim.Map[x][z] = Hallway.Solid;
			}
		}
		sim.Map[12][12] = Hallway.Empty;
		sim.Map[12][13] = Hallway.Empty;
		sim.Map[12][14] = Hallway.Straight.Clone().Rotate90Once();
		sim.Map[12][15] = Hallway.Cross;
		sim.Map[13][13] = Hallway.Straight.Clone();
		sim.start();
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		double x = (e.getX() * ((float)imageX / (float)windowX) / imageX);
		double y = (e.getY() * ((float)imageY / (float)windowY) / imageY);

		if(SwingUtilities.isLeftMouseButton(e)) {
			
		}
		else if(SwingUtilities.isRightMouseButton(e)) {
			
		}
		else if(SwingUtilities.isMiddleMouseButton(e)) {
			
		}
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		int windX = e.getX();
		int windY = e.getY();
	}
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	@Override
	public void keyPressed(KeyEvent e) {
		while(rendering) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			
		}
		if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			
		}
		if(e.getKeyCode() == KeyEvent.VK_DELETE) {
			
		}
		
		if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			CamRot.Y -= Math.toRadians(360*deltaTime);
		}
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			CamRot.Y += Math.toRadians(360*deltaTime);
		}
		
		if(e.getKeyCode() == KeyEvent.VK_W) {
			double move = CamSpeed * deltaTime;
			double moveZ = Math.sin(CamRot.Y) * move;
			double moveX = Math.cos(CamRot.Y) * move;
			CamPos.Z += moveZ;
			CamPos.X += moveX;
		}
		if(e.getKeyCode() == KeyEvent.VK_S) {
			double move = CamSpeed * deltaTime;
			double moveZ = Math.sin(CamRot.Y) * move;
			double moveX = Math.cos(CamRot.Y) * move;
			CamPos.Z -= moveZ;
			CamPos.X -= moveX;
		}

		if(e.getKeyCode() == KeyEvent.VK_UP) {
			targetFPS++;
		}
		if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			if(targetFPS > 1) targetFPS--;
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
