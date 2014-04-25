package superSpaceBoat;

import java.awt.*;

public class Shot {
	private final double shotSpeed = 3; //pixels per frame
	private double x, y, xVelocity, yVelocity; //movement variables
	private Color shotColor;	
	
	public Shot(double x, double y, double angle, double shipXVel, double shipYVel, Color shotColor) {
		this.x = x;
		this.y = y;
		this.shotColor = shotColor;
		if (angle > (2 * Math.PI)) // Keep angle within bounds of 0 to 2*PI
			angle -= (2 * Math.PI);
		else if (angle < 0)
			angle += (2 * Math.PI);
		xVelocity = shotSpeed * Math.cos(angle);// + shipXVel; //removed because shots were just going off screen immediately if the ship was moving too fast.
		yVelocity = shotSpeed * Math.sin(angle);// + shipYVel;
	}

	public void move(int scrnWidth, int scrnHeight) { // move the shot
		x += xVelocity;
		y += yVelocity;
	}

	public void draw(Graphics g) {
		g.setColor(shotColor); 
		// draw circle of radius 3 centered at the closest point
		// with integer coordinates (.5 added to x-1 and y-1 for rounding)
		g.fillOval((int) (x - .5), (int) (y - .5), 3, 3);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
}