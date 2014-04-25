package superSpaceBoat;

import java.awt.*;

public class Ship {
	private final double[] origXPts = { -15, -15, 5, 15, 5 }, origYPts = { -5, 5, 5,
			0, -5 };// define the shape of the ship
	private final double[] origFlameXPts = { -15, -15, -20 }, origFlameYPts = { -4, 4,
			0 };
	private final int radius = 15; // radius of circle used to approximate the ship
	private final int maxHp = 150; // full hp
	private final double acceleration = .025; // pixels per frame
	private final double boostSpeed = .08; // pixels per frame
	private final double rotationalSpeed = 3*Math.PI / 360;
	private int boostCapacity = 600, boostCost = 10, rightShotDelayRemaining,
			leftShotDelayRemaining, boostRemaining, hp;
	private int[] xPts, yPts, flameXPts, flameYPts; // store the current locations
	private double x, y, xVelocity, yVelocity, angle; // movement variables
	private boolean accelerating, decelerating, turningLeft, turningRight, boosting;
	private Color shipColor = Color.white;
	private int shieldTimer= 0, lastHp;

	public Ship(double x, double y, double angle) {
		this.x = x;
		this.y = y;
		this.angle = angle;
		xVelocity = 0;
		yVelocity = 0;
		hp = maxHp;
		lastHp = hp;
		turningLeft = false;
		turningRight = false;
		accelerating = false;
		xPts = new int[5];
		yPts = new int[5];
		flameXPts = new int[3];
		flameYPts = new int[3];
		rightShotDelayRemaining = 0;
		leftShotDelayRemaining = 0;
		boostRemaining = boostCapacity;
	}

	public void draw(Graphics g) {
		if(shieldTimer > 0){
			shieldTimer--;
			switch(shieldTimer){
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
				g.setColor(Color.cyan.darker().darker().darker().darker().darker());
				g.drawOval((int)(this.x - radius +.5), (int)(this.y-radius+.5), 2*radius, 2*radius);

				break;
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
			case 18:
			case 19:	
				g.setColor(Color.cyan.darker().darker().darker().darker());
				g.drawOval((int)(this.x - radius +.5), (int)(this.y-radius+.5), 2*radius, 2*radius);

				break;
			case 20:
			case 21:
			case 22:
			case 23:
			case 24:
			case 25:
			case 26:
			case 27:
			case 28:
			case 29:	
				g.setColor(Color.cyan.darker().darker().darker());
				g.drawOval((int)(this.x - radius +.5), (int)(this.y-radius+.5), 2*radius, 2*radius);

				break;
			case 30:
			case 31:
			case 32:
			case 33:
			case 34:
			case 35:
			case 36:
			case 37:
			case 38:
			case 39:
				g.setColor(Color.cyan.darker().darker());
				g.drawOval((int)(this.x - radius +.5), (int)(this.y-radius+.5), 2*radius, 2*radius);

				break;
			case 40:
			case 41:
			case 42:	
			case 43:	
			case 44:	
			case 45:	
			case 46:	
			case 47:	
			case 48:	
			case 49:
				g.setColor(Color.cyan.darker());
				g.drawOval((int)(this.x - radius +.5), (int)(this.y-radius+.5), 2*radius, 2*radius);

				break;
			case 50:
			case 51:
			case 52:	
			case 53:	
			case 54:	
			case 55:	
			case 56:	
			case 57:	
			case 58:	
			case 59:	
				g.setColor(Color.cyan);
				g.drawOval((int)(this.x - radius +.5), (int)(this.y-radius+.5), 2*radius, 2*radius);
				break;
			default:
				System.out.println("invalid shield timer");
		
			}
		}
		if(lastHp > hp){
			shieldTimer = 60;
			lastHp = hp;
		}

		if (boosting && boostRemaining > 4) { // draw flame if accelerating
			for (int i = 0; i < 3; i++) {
				flameXPts[i] = (int) (origFlameXPts[i] * Math.cos(angle)
						- origFlameYPts[i] * Math.sin(angle) + x + .5);
				flameYPts[i] = (int) (origFlameXPts[i] * Math.sin(angle)
						+ origFlameYPts[i] * Math.cos(angle) + y + .5);
			}
			g.setColor(Color.red); // set color of flame
			g.fillPolygon(flameXPts, flameYPts, 3); // 3 is # of points
		}
		for (int i = 0; i < 5; i++) {// calculate the polygon for the ship
			xPts[i] = (int) (origXPts[i] * Math.cos(angle) - // rotate
					origYPts[i] * Math.sin(angle) + x + .5); // translate and
																// round

			yPts[i] = (int) (origXPts[i] * Math.sin(angle) + // rotate
					origYPts[i] * Math.cos(angle) + y + .5); // translate and
																// round
		}
		g.setColor(shipColor);
		g.fillPolygon(xPts, yPts, 5); // 5 is the number of vertices of the ship
	}
	public void tetheredMove(int scrnWidth, int scrnHeight){
		if(leftShotDelayRemaining > 0)
			leftShotDelayRemaining--;
		if(rightShotDelayRemaining > 0)
			rightShotDelayRemaining--;
		if(boostRemaining < boostCapacity)
			boostRemaining++;
		if (turningLeft) // this is backwards from typical polar coordinates
			angle -= rotationalSpeed;
		if (turningRight)
			angle += rotationalSpeed;
		if (angle > (2 * Math.PI)) // Keep angle within bounds of 0 to 2*PI
			angle -= (2 * Math.PI);
		else if (angle < 0)
			angle += (2 * Math.PI);
	}
	public void move(int scrnWidth, int scrnHeight) {
		if(leftShotDelayRemaining > 0)
			leftShotDelayRemaining--;
		if(rightShotDelayRemaining > 0)
			rightShotDelayRemaining--;
		if(boostRemaining < boostCapacity)
			boostRemaining++;
		if (turningLeft) // this is backwards from typical polar coordinates
			angle -= rotationalSpeed;
		if (turningRight)
			angle += rotationalSpeed;
		if (angle > (2 * Math.PI)) // Keep angle within bounds of 0 to 2*PI
			angle -= (2 * Math.PI);
		else if (angle < 0)
			angle += (2 * Math.PI);
		if (boosting && boostRemaining >= boostCost) { // adds boostSpeed to velocity in direction pointed
			boostRemaining = boostRemaining - boostCost;	
			xVelocity += boostSpeed * Math.cos(angle);
			yVelocity += boostSpeed * Math.sin(angle);
		} 
		else if (accelerating) { // adds acceleration to velocity in direction pointed 
			xVelocity += acceleration * Math.cos(angle);
			yVelocity += acceleration * Math.sin(angle);
		}
		if (decelerating) { //reduces velocity by 5% per frame
			xVelocity = xVelocity * .98;
			yVelocity = yVelocity * .98;
		}
		x += xVelocity; // move the ship by adding velocity to position
		y += yVelocity;
		if (x < 0) // wrap the ship around to the opposite side of the screen
			x += scrnWidth; // when it goes out of the screen's bounds
		else if (x > scrnWidth)
			x -= scrnWidth;
		if (y < 0)
			y += scrnHeight;
		else if (y > scrnHeight)
			y -= scrnHeight;
		
	}
	
	public void changeColor(Color color){
		shipColor = color;
	}

	public Shot shootLeft() {
		leftShotDelayRemaining = 25;
		double shotAngle = (Math.random() * 0.261799388 - 0.130899694);
		return new Shot(x+6*Math.sin(angle), y+6*Math.cos(angle), this.angle - Math.PI/2 + shotAngle, xVelocity, yVelocity, shipColor);	}

	public Shot shootRight() {
		rightShotDelayRemaining = 25;
		double shotAngle = (Math.random() * 0.261799388 - 0.130899694);
		return new Shot(x-6*Math.sin(angle), y-6*Math.cos(angle), this.angle + Math.PI/2 + shotAngle, xVelocity, yVelocity, shipColor);
	}

	public void setAccelerating(boolean accelerating) {// start or stop
														// accelerating the ship
		this.accelerating = accelerating;
	}

	public void setDecelerating(boolean decelerating) {// start or stop
														// decelerating the ship
		this.decelerating = decelerating;
	}

	public void setBoosting(boolean boosting) {// start or stop boosting the ship
		this.boosting = boosting;
	}

	public void setTurningLeft(boolean turningLeft) {// start or stop turning
														// the ship left
		this.turningLeft = turningLeft;
	}

	public void setTurningRight(boolean turningRight) {// start or stop turning
														// the ship right
		this.turningRight = turningRight;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
	public void setPosition(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public double getHP(){
		return (double) hp/ (double)maxHp;
	}
	
	public void getHit(){
		hp = hp-10;
		if(hp < 0)
			hp = 0;
	}
	 public void setHP(double hp){ //used for collision damage
	        this.hp = (int) hp;
			if(this.hp < 0)
				this.hp = 0;
	  } 
	
	public double getBoostRemaining(){
		return (double) boostRemaining/ (double) boostCapacity;
	}
	public double getBoostMinimum(){
		return (double) boostCost/ (double) boostCapacity;
	}
	public double getBoostSpeed(){
		return boostSpeed;
	}
	public double getAcceleration(){
		return acceleration;
	}
	public double getXVel(){
		return xVelocity;
	}
	public double getYVel(){
		return yVelocity;
	}

	public double getRadius() { // returns radius of circle that approximates the ship
		return radius;
	}

	public boolean canShootLeft() {
		if (leftShotDelayRemaining > 0) 
			return false;
		else
			return true;
	}

	public boolean canShootRight() {
		if (rightShotDelayRemaining > 0)
			return false;
		else
			return true;
	}

	public void setVelocity(double xVelocity, double yVelocity) {
		this.xVelocity = xVelocity;
		this.yVelocity = yVelocity;
		
	}

	public boolean getAccelerating() {
		return this.accelerating;
	}

	public boolean getBraking() {
		return this.decelerating;
	}

	public boolean getBoosting() {
		if(boosting && boostRemaining >= boostCost)
			return true;
		else 
			return false;
	}

	public double getAngle() {
		return this.angle;
	}
}