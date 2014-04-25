package superSpaceBoat;

import java.awt.*;

public class Grapple{
	private final double[] origXPts = { 0, 2, -2}, origYPts = { 0, 3, 3};// define the shape of the ship
	private final int rStep = 4;
	private final int rMax = 100;
	private final int duration = 300;
	private static double accelSpeed;
	private static double boostSpeed;
	private int[] xPts, yPts;
	private double xVector;
	private double yVector;
	private int r;
	private boolean active;
	private double tetherXVel;
	private double tetherYVel;
	private double tetherAngVel;
	private double tetherX;
	private double tetherY;
	private double tetherAng;
	private int remaining;
	private boolean tethered;

	public Grapple(){
		this.active = false;
		this.tethered = false;
	}
	public Grapple(int piEighths){
		xPts = new int[3];
		yPts = new int[3];
		this.active= true;
		this.tethered = false;
		this.remaining = 0;
		this.r = this.rStep;
		tetherXVel = 0;
		tetherYVel = 0;
		tetherAngVel = 0;
		switch(piEighths){
		case 0:
			xVector = 0;
			yVector = -1;
			tetherAng = 0;
			break;
		case 1:
			xVector = .5;
			yVector = -.5;
			tetherAng = Math.PI/4;
			break;
		case 2:
			xVector = 1;
			yVector = 0;
			tetherAng = Math.PI/2;
			break;
		case 3:
			xVector = .5;
			yVector = .5;
			tetherAng = 3 * Math.PI/4;
			break;
		case 4:
			xVector = 0;
			yVector = 1;
			tetherAng = Math.PI;
			break;
		case 5:
			xVector = -.5;
			yVector = .5;
			tetherAng = 5*Math.PI/4;
			break;
		case 6:
			xVector = -1;
			yVector = 0;
			tetherAng = 3*Math.PI/2;
			break;
		case 7:
			xVector = -.5;
			yVector = -.5;
			tetherAng = 7*Math.PI/4;
			break;
		default:
			System.out.println("Something went wrong: Invalid grapple angle");
		}
		
	}
	public void drawGrapple(Graphics g, double shipX, double shipY){
		if(active){
			g.setColor(Color.white);
			g.drawLine((int)shipX, (int)shipY, getX(shipX), getY(shipY) );
			for(int i=0; i<3; i++){
				xPts[i] = (int) (origXPts[i]* Math.cos(tetherAng) - // rotate
						origYPts[i] * Math.sin(tetherAng) + getX(shipX) + .5);// translate and round
				yPts[i] = (int) (origXPts[i] * Math.sin(tetherAng) + // rotate
						origYPts[i] * Math.cos(tetherAng) + getY(shipY) + .5); // translate and round													
			}					

			g.drawPolygon(xPts, yPts, 3);
		}
	}
	public void extend(){
		if(active){
			this.r += rStep;
			if(this.r>=rMax){
				active=false;
			}
		}
	}
	public int getX(double shipX){
		return (int)(shipX+r*xVector);
	}
	public int getY(double shipY){
		return (int)(shipY+r*yVector);
	}
	public void setShipSpeeds(double acceleration, double boost){
		Grapple.accelSpeed = acceleration;
		Grapple.boostSpeed = boost;
	}
	public boolean getTethered(){
		return this.tethered;
	}
	public void attach(double ownerXVel, double ownerYVel, double targetXVel, double targetYVel, double ownerX, double ownerY){
			this.tethered = true;
			this.remaining = duration;
			this.tetherX = (int)(ownerX+r*xVector*.5);
			this.tetherY = (int)(ownerY+r*yVector*.5);
			double newOwnerXVel = ownerXVel * Math.cos(tetherAng) + ownerYVel * Math.sin(tetherAng);
			double newOwnerYVel = ownerXVel * Math.sin(tetherAng) - ownerYVel * Math.cos(tetherAng);
			double newTargetXVel = targetXVel * Math.cos(tetherAng) + targetYVel * Math.sin(tetherAng);
			double newTargetYVel = targetXVel * Math.sin(tetherAng) - targetYVel * Math.cos(tetherAng);
			this.tetherAngVel = (newTargetXVel-newOwnerXVel)/2;
			double systemXVel = (newOwnerXVel + newTargetXVel)/2;
			double systemYVel = (newOwnerYVel + newTargetYVel)/2;
			this.tetherXVel = systemXVel*Math.cos(-tetherAng) - systemYVel*Math.sin(-tetherAng);
			this.tetherYVel = -(systemXVel*Math.sin(-tetherAng) + systemYVel * Math.cos(-tetherAng));

	}
	public void drawTether(Graphics g){
		if(tethered){
				g.setColor(Color.white);
				g.drawLine((int)getOwnerX(), (int)getOwnerY(), (int)getTargetX(), (int)getTargetY() );
				for(int i=0; i<3; i++){
					xPts[i] = (int) (origXPts[i]* Math.cos(tetherAng) - // rotate
							origYPts[i] * Math.sin(tetherAng) + getTargetX() + .5);// translate and round
					yPts[i] = (int) (origXPts[i] * Math.sin(tetherAng) + // rotate
							origYPts[i] * Math.cos(tetherAng) + getTargetY() + .5); // translate and round													
				}					

				g.drawPolygon(xPts, yPts, 3);
		}

	}
	public boolean move(int scrnWidth, int scrnHeight, boolean ownerAccel, boolean targetAccel, boolean ownerBraking, boolean targetBraking, boolean ownerBoosting, boolean targetBoosting, double ownerAngle, double targetAngle){
		if(this.remaining>0){
			this.remaining--;
			tetherX = tetherX + tetherXVel;
			tetherY = tetherY +tetherYVel;
			double angularVel = 2 * Math.PI * tetherAngVel / (2*Math.PI *r);
			tetherAng += angularVel;
			if (tetherAng > (2 * Math.PI)) // Keep angle within bounds of 0 to 2*PI
				tetherAng -= (2 * Math.PI);
			else if (tetherAng < 0)
				tetherAng += (2 * Math.PI);
			if (tetherX < 0) // wrap the ship around to the opposite side of the screen
				tetherX += scrnWidth; // when it goes out of the screen's bounds
			else if (tetherX > scrnWidth)
				tetherX -= scrnWidth;
			if (tetherY < 0)
				tetherY += scrnHeight;
			else if (tetherY > scrnHeight)
				tetherY -= scrnHeight;
			double ownerXVel = 0, ownerYVel = 0, targetXVel = 0, targetYVel = 0, newOwnerXVel, newOwnerYVel, newTargetXVel, newTargetYVel;
			if(ownerBoosting){
				ownerXVel = boostSpeed * Math.cos(ownerAngle);
				ownerYVel = boostSpeed * Math.sin(ownerAngle);
			}
			else if(ownerAccel){
				ownerXVel = accelSpeed * Math.cos(ownerAngle);
				ownerYVel = accelSpeed * Math.sin(ownerAngle);
			}
			else if(ownerBraking){
				tetherXVel *= .99;
				tetherYVel *=  .99;
				tetherAngVel *= .99;
			}
			if(targetBoosting){
				targetXVel = boostSpeed * Math.cos(targetAngle);
				targetYVel = boostSpeed * Math.sin(targetAngle);
			}
			else if(targetAccel){
				targetXVel = accelSpeed * Math.cos(targetAngle);
				targetYVel = accelSpeed * Math.sin(targetAngle);
			}
			else if(targetBraking){
				tetherXVel *= .99;
				tetherYVel *= .99;
				tetherAngVel *= .99;
			}
			
			newOwnerXVel = ownerXVel * Math.cos(tetherAng) + ownerYVel * Math.sin(tetherAng);
			newOwnerYVel = ownerXVel * Math.sin(tetherAng) - ownerYVel * Math.cos(tetherAng);
			newTargetXVel = targetXVel * Math.cos(tetherAng) + targetYVel * Math.sin(tetherAng);
			newTargetYVel = targetXVel * Math.sin(tetherAng) - targetYVel * Math.cos(tetherAng);
			this.tetherAngVel += (newTargetXVel-newOwnerXVel)/2;
			double systemXVel = (newOwnerXVel + newTargetXVel)/2;
			double systemYVel = (newOwnerYVel + newTargetYVel)/2;
			this.tetherXVel += systemXVel*Math.cos(-tetherAng) - systemYVel*Math.sin(-tetherAng);
			this.tetherYVel += -(systemXVel*Math.sin(-tetherAng) + systemYVel * Math.cos(-tetherAng));
			return true;
		}
		else{
			return false;
		}
	}
	public void detach(){
		this.tethered = false;
	}
	public double getOwnerX(){
		return tetherX- (r/2) * Math.sin(tetherAng);
	}
	public double getOwnerY(){
		return tetherY+ (r/2) * Math.cos(tetherAng);
	}
	public double getOwnerXVel(){
		double vX = tetherAngVel *.5* Math.cos((tetherAng-Math.PI/2));
		return tetherXVel- vX; 
	}
	public double getOwnerYVel(){
		double vY = tetherAngVel *.5* Math.sin((tetherAng-Math.PI/2));
		return tetherYVel +vY;
	}
	public double getTargetX(){
		return tetherX + (r/2) * Math.sin(tetherAng);        
	}                
	public double getTargetY(){
		return tetherY - (r/2) * Math.cos(tetherAng);    
	}                
	public double getTargetXVel(){
		double vX = tetherAngVel * .5* Math.sin((tetherAng+Math.PI/2));
		return tetherXVel + vX;           
	}              
	public double getTargetYVel(){
		double vY = tetherAngVel *.5* Math.cos((tetherAng+Math.PI/2));
		return tetherYVel -vY;
	}
	public boolean getActive() {
		return this.active;
	}
	public double getTetherTime(){
		return (double)remaining/(double)duration;
	}
}
