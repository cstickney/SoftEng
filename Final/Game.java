package superSpaceBoat;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class Game extends Applet implements Runnable, KeyListener {
	final int framesPerSecond = 60;
	final double collisionDmgMod = 5;
	final int grappleCooldown = 60;
	final int gameSize = 500;
	final int uiSize = 125;
	final int maxShots = 10;
	Thread thread;
	long startTime, endTime, framePeriod;
	Dimension dim;
	Image img;
	Graphics g;
	String startText;
	Ship shipA;
	Ship shipB;
	Shot[] shotsA;
	Shot[] shotsB;
	Grapple grappleA;
	Grapple grappleB;
	int numShotsA;
	int numShotsB;
	int hAdjust;
	int cooldownA;
	int cooldownB;
	int nextGameDelay = 0;
	boolean active;
	boolean aimingLeftA;
	boolean aimingRightA;
	boolean aimingUpA;
	boolean aimingDownA;
	boolean grapplingA;
	boolean tetheredA;
	boolean aimingLeftB;
	boolean aimingRightB;
	boolean aimingUpB;
	boolean aimingDownB;
	boolean grapplingB;
	boolean tetheredB;
	boolean shootingLeftA;
	boolean shootingRightA;
	boolean shootingLeftB;
	boolean shootingRightB;
	double boostMin;
	double xA, yA, xB, yB;

	public void init() {
		resize(gameSize, gameSize + uiSize);
		startText = "WELCOME TO SUPER SPACE BOAT!";
		shipA = new Ship(50, 50, Math.PI / 2);
		shipA.changeColor(Color.green);
		shotsA = new Shot[10];
		grappleA = new Grapple();
		grappleA.setShipSpeeds(shipA.getAcceleration(), shipA.getBoostSpeed());
		shipB = new Ship(450, 450, 3 * Math.PI / 2);
		shipB.changeColor(Color.red);
		shotsB = new Shot[10];
		grappleB = new Grapple();
		boostMin = shipA.getBoostMinimum();
		addKeyListener(this);
		framePeriod = 1000 / framesPerSecond; // in ms
		dim = getSize();
		img = createImage(dim.width, dim.height);
		g = img.getGraphics();
		thread = new Thread(this);
		thread.start();
	}

	public void reset() {
		active = false;
		hAdjust = 50;
		shipA = new Ship(50, 50, Math.PI / 2);
		shipA.changeColor(Color.green);
		shotsA = new Shot[10];
		grappleA = new Grapple();
		numShotsA = 0;
		shootingLeftA = false;
		shootingRightA = false;
		aimingLeftA = false;
		aimingRightA = false;
		aimingUpA = false;
		aimingDownA = false;
		shipB = new Ship(450, 450, 3 * Math.PI / 2);
		shipB.changeColor(Color.red);
		shotsB = new Shot[10];
		grappleB = new Grapple();
		numShotsB = 0;
		shootingLeftB = false;
		shootingRightB = false;
		aimingLeftB = false;
		aimingRightB = false;
		aimingUpB = false;
		aimingDownB = false;
		nextGameDelay = 180;
	}

	public void paint(Graphics gfx) {
		if (active && nextGameDelay == 0) {
			xA = shipA.getX();
			xB = shipB.getX();
			yA = shipA.getY();
			yB = shipB.getY();
			g.setColor(Color.black);
			g.fillRect(0, 0, gameSize, gameSize); // draw background
			shipA.draw(g);
			shipB.draw(g);
			if (!grappleA.getTethered() && !grappleB.getTethered()) {
				grappleA.drawGrapple(g, xA, yA);
				grappleB.drawGrapple(g, xB, yB);
			} else if (grappleA.getTethered()) {
				grappleA.drawTether(g);
			} else if (grappleB.getTethered()) {
				grappleB.drawTether(g);
			} else {
				shipA.draw(g);
				shipB.draw(g);
				grappleA.drawGrapple(g, xA, yA);
				grappleB.drawGrapple(g, xB, yB);
			}
			for (int i = 0; i < numShotsA; i++)
				shotsA[i].draw(g);
			for (int i = 0; i < numShotsB; i++)
				shotsB[i].draw(g);
			g.setColor(Color.white);
			g.fillRect(0, gameSize, gameSize, uiSize);
			g.setColor(Color.black);
			g.drawString("Player 1", 90, 512);
			g.drawString("Player 2", 370, 512);
			g.drawString("HP", 244, 530);
			g.drawString("Boost", 235, 550);
			g.drawString("Shots Fired", 219, 570);
			g.drawString("Grapple", 225, 588);
			g.drawString("Cooldown", 220, 601);
			g.drawString(
					String.valueOf(numShotsA) + "  /  "
							+ String.valueOf(maxShots), 50, 570);
			g.drawString(
					String.valueOf(numShotsB) + "  /  "
							+ String.valueOf(maxShots), 420, 570);
			double p1Boost = shipA.getBoostRemaining(), p1Hp = shipA.getHP();
			double p2Boost = shipB.getBoostRemaining(), p2Hp = shipB.getHP();
			g.setColor(Color.green);
			g.fillRect(5, 520, (int) (200 * p1Hp), 10);
			g.fillRect(495 - (int) (200 * p2Hp), 520, (int) (200 * p2Hp), 10);

			if (p1Boost < boostMin)
				g.setColor(Color.red);
			else
				g.setColor(Color.yellow);
			g.fillRect(5, 540, (int) (p1Boost * 200), 10);
			if (p2Boost < boostMin)
				g.setColor(Color.red);
			else
				g.setColor(Color.yellow);
			g.fillRect(495 - (int) (p2Boost * 200), 540, (int) (p2Boost * 200),
					10);
			g.setColor(Color.cyan);
			g.fillRect(5, 580, (int) (200 * cooldownA / grappleCooldown), 10);
			g.fillRect(495 - (int) (200 * cooldownB / grappleCooldown), 580, (int) (200 * cooldownB / grappleCooldown), 10);

			g.setColor(Color.black);
			if (grappleA.getTethered()) {
				g.drawString("P1 GRAPPLE ENGAGED", 10, 620);
				g.fillRect(150, 610, (int) (300 * grappleA.getTetherTime()), 10);
			}
			if (grappleB.getTethered()) {
				g.drawString("P2 GRAPPLE ENGAGED", 10, 620);
				g.fillRect(150, 610, (int) (300 * grappleB.getTetherTime()), 10);
			}
			gfx.drawImage(img, 0, 0, this);
		} else {
			if (nextGameDelay > 0)
				--nextGameDelay;
			g.setColor(Color.black);
			g.clearRect(0, 0, gameSize, gameSize + uiSize);
			g.drawString(startText, 150 + hAdjust, 50);

			if (nextGameDelay <= 0) {
				g.drawString("Press any button to begin!", 175, 200);
			} else {
				g.drawString("Wait " + (int) (1 + nextGameDelay / 60)
						+ " seconds...", 200, 200);
			}
			gfx.drawImage(img, 0, 0, this);
		}
	}

	public void update(Graphics gfx) {
		paint(gfx);
	}

	public void run() {
		while (true) {
			startTime = System.currentTimeMillis();
			if (active) {
				boolean collide = collision(shipA, shipB);
				if (!grappleA.getTethered() && !grappleB.getTethered()
						&& !collide) {
					shipA.move(dim.width, dim.height - uiSize);
					shipB.move(dim.width, dim.height - uiSize);
					grappleA.extend();
					grappleB.extend();
				} else if (!grappleA.getTethered() && !grappleB.getTethered()) {
					shipA.tetheredMove(dim.width, dim.height - uiSize);
					shipB.tetheredMove(dim.width, dim.height - uiSize);
					grappleA.extend();
					grappleB.extend();
				} else {
					if (grappleA.getTethered()) {
						if (!grappleA.move(dim.width, dim.height - uiSize,
								shipA.getAccelerating(),
								shipB.getAccelerating(), shipA.getBraking(),
								shipB.getBraking(), shipA.getBoosting(),
								shipB.getBoosting(), shipA.getAngle(),
								shipB.getAngle())) {
							shipA.setPosition(grappleA.getOwnerX(),
									grappleA.getOwnerY());
							shipB.setPosition(grappleA.getTargetX(),
									grappleA.getTargetY());
							shipA.setVelocity(grappleA.getOwnerXVel(),
									grappleA.getOwnerYVel());
							shipB.setVelocity(grappleA.getTargetXVel(),
									grappleA.getTargetYVel());
							shipA.move(dim.width, dim.height - uiSize);
							shipB.move(dim.width, dim.height - uiSize);
							grappleA = new Grapple();
							grappleB.extend();
						} else {
							shipA.tetheredMove(dim.width, dim.height - uiSize);
							shipB.tetheredMove(dim.width, dim.height - uiSize);
							shipA.setPosition(grappleA.getOwnerX(),
									grappleA.getOwnerY());
							shipB.setPosition(grappleA.getTargetX(),
									grappleA.getTargetY());
						}
					} else if (grappleB.getTethered()) {
						if (!grappleB.move(dim.width, dim.height - uiSize,
								shipB.getAccelerating(),
								shipA.getAccelerating(), shipB.getBraking(),
								shipA.getBraking(), shipB.getBoosting(),
								shipA.getBoosting(), shipB.getAngle(),
								shipA.getAngle())) {
							shipA.setPosition(grappleB.getTargetX(),
									grappleB.getTargetY());
							shipB.setPosition(grappleB.getOwnerX(),
									grappleB.getOwnerY());
							shipA.setVelocity(grappleB.getTargetXVel(),
									grappleB.getTargetYVel());
							shipB.setVelocity(grappleB.getOwnerXVel(),
									grappleB.getOwnerYVel());
							shipA.move(dim.width, dim.height - uiSize);
							shipB.move(dim.width, dim.height - uiSize);
							grappleB = new Grapple();
							grappleA.extend();
						} else {
							shipA.tetheredMove(dim.width, dim.height - uiSize);
							shipB.tetheredMove(dim.width, dim.height - uiSize);
							shipA.setPosition(grappleB.getTargetX(),
									grappleB.getTargetY());
							shipB.setPosition(grappleB.getOwnerX(),
									grappleB.getOwnerY());
						}

					}
				}
				if (cooldownA > 0)
					cooldownA--;
				if (cooldownB > 0)
					cooldownB--;
				xA = shipA.getX();
				xB = shipB.getX();
				yA = shipA.getY();
				yB = shipB.getY();
			}
			repaint();
			if (active) {
				for (int i = 0; i < numShotsA; i++) {
					shotsA[i].move(dim.width, dim.height - uiSize);
					double x = shotsA[i].getX();
					double y = shotsA[i].getY();
					if (x < 0 || y < 0 || x > dim.width
							|| y > dim.height - uiSize) {
						deleteShotA(i);
						i--; // move the outer loop back one so the shot shifted
								// up is not skipped
					} else if (Math.sqrt(Math.pow(x - xB, 2)
							+ Math.pow(y - yB, 2)) < shipB.getRadius()) {// B
																			// hit
																			// by
																			// A
						shipB.getHit();
						deleteShotA(i);
						i--;
						if (shipB.getHP() <= 0) {
							startText = "PLAYER 1 WINS!";
							reset();
						}
					}
				}
				for (int i = 0; i < numShotsB; i++) {
					shotsB[i].move(dim.width, dim.height - uiSize);
					int x = (int) shotsB[i].getX();
					int y = (int) shotsB[i].getY();
					if (x < 0 || y < 0 || x > dim.width
							|| y > dim.height - uiSize) {
						deleteShotB(i);
						i--; // move the outer loop back one so the shot shifted
								// up is not skipped

					} else if (Math.sqrt(Math.pow(x - xA, 2)
							+ Math.pow(y - yA, 2)) < shipB.getRadius()) {// B
																			// hit
																			// by
																			// A
						shipA.getHit();
						deleteShotB(i);
						i--;
						if (shipA.getHP() <= 0) {
							startText = "PLAYER 2 WINS!";
							reset();
						}
					}
				}

				if (shipA.getHP() <= 0 && shipB.getHP() <= 0) {
					startText = "NOBODY WINS!!";
					reset();
				}
				if (grappleA.getActive()
						&& !grappleA.getTethered()
						&& Math.sqrt(Math.pow(grappleA.getX(xA) - xB, 2)
								+ Math.pow(grappleA.getY(yA) - yB, 2)) < shipB
									.getRadius()) {
					grappleA.attach(shipA.getXVel(), shipA.getYVel(),
							shipB.getXVel(), shipB.getYVel(), shipA.getX(),
							shipA.getY());
				} else if (grappleB.getActive()
						&& !grappleB.getTethered()
						&& Math.sqrt(Math.pow(grappleB.getX(xB) - xA, 2)
								+ Math.pow(grappleB.getY(yB) - yA, 2)) < shipA
									.getRadius()) {
					grappleB.attach(shipB.getXVel(), shipB.getYVel(),
							shipA.getXVel(), shipA.getYVel(), shipB.getX(),
							shipB.getY());

				}

				if (!grappleA.getTethered() && !grappleB.getTethered()
						&& grapplingA && aimingUpA && !aimingDownA
						&& aimingLeftA == aimingRightA && cooldownA == 0
						&& !tetheredA && !tetheredB) {
					grappleA = new Grapple(0);
					cooldownA = grappleCooldown;
				}
				if (!grappleA.getTethered() && !grappleB.getTethered()
						&& grapplingA && aimingUpA && !aimingDownA
						&& !aimingLeftA && aimingRightA && cooldownA == 0
						&& !tetheredA && !tetheredB) {
					grappleA = new Grapple(1);
					cooldownA = grappleCooldown;
				}
				if (!grappleA.getTethered() && !grappleB.getTethered()
						&& grapplingA && aimingUpA == aimingDownA
						&& !aimingLeftA && aimingRightA && cooldownA == 0
						&& !tetheredA && !tetheredB) {
					grappleA = new Grapple(2);
					cooldownA = grappleCooldown;
				}
				if (!grappleA.getTethered() && !grappleB.getTethered()
						&& grapplingA && !aimingUpA && aimingDownA
						&& !aimingLeftA && aimingRightA && cooldownA == 0
						&& !tetheredA && !tetheredB) {
					grappleA = new Grapple(3);
					cooldownA = grappleCooldown;
				}
				if (!grappleA.getTethered() && !grappleB.getTethered()
						&& grapplingA && !aimingUpA && aimingDownA
						&& aimingLeftA == aimingRightA && cooldownA == 0
						&& !tetheredA && !tetheredB) {
					grappleA = new Grapple(4);
					cooldownA = grappleCooldown;
				}
				if (!grappleA.getTethered() && !grappleB.getTethered()
						&& grapplingA && !aimingUpA && aimingDownA
						&& aimingLeftA && !aimingRightA && cooldownA == 0
						&& !tetheredA && !tetheredB) {
					grappleA = new Grapple(5);
					cooldownA = grappleCooldown;
				}
				if (!grappleA.getTethered() && !grappleB.getTethered()
						&& grapplingA && aimingUpA == aimingDownA
						&& aimingLeftA && !aimingRightA && cooldownA == 0
						&& !tetheredA && !tetheredB) {
					grappleA = new Grapple(6);
					cooldownA = grappleCooldown;
				}
				if (!grappleA.getTethered() && !grappleB.getTethered()
						&& grapplingA && aimingUpA && !aimingDownA
						&& aimingLeftA && !aimingRightA && cooldownA == 0
						&& !tetheredA && !tetheredB) {
					grappleA = new Grapple(7);
					cooldownA = grappleCooldown;
				}
				if (!grappleB.getTethered() && !grappleA.getTethered()
						&& grapplingB && aimingUpB && !aimingDownB
						&& aimingLeftB == aimingRightB && cooldownB == 0
						&& !tetheredA && !tetheredB) {
					grappleB = new Grapple(0);
					cooldownB = grappleCooldown;
				}
				if (!grappleB.getTethered() && !grappleA.getTethered()
						&& grapplingB && aimingUpB && !aimingDownB
						&& !aimingLeftB && aimingRightB && cooldownB == 0
						&& !tetheredA && !tetheredB) {
					grappleB = new Grapple(1);
					cooldownB = grappleCooldown;
				}
				if (!grappleB.getTethered() && !grappleA.getTethered()
						&& grapplingB && aimingUpB == aimingDownB
						&& !aimingLeftB && aimingRightB && cooldownB == 0
						&& !tetheredA && !tetheredB) {
					grappleB = new Grapple(2);
					cooldownB = grappleCooldown;
				}
				if (!grappleB.getTethered() && !grappleA.getTethered()
						&& grapplingB && !aimingUpB && aimingDownB
						&& !aimingLeftB && aimingRightB && cooldownB == 0
						&& !tetheredA && !tetheredB) {
					grappleB = new Grapple(3);
					cooldownB = grappleCooldown;
				}
				if (!grappleB.getTethered() && !grappleA.getTethered()
						&& grapplingB && !aimingUpB && aimingDownB
						&& aimingLeftB == aimingRightB && cooldownB == 0
						&& !tetheredA && !tetheredB) {
					grappleB = new Grapple(4);
					cooldownB = grappleCooldown;
				}
				if (!grappleB.getTethered() && !grappleA.getTethered()
						&& grapplingB && !aimingUpB && aimingDownB
						&& aimingLeftB && !aimingRightB && cooldownB == 0
						&& !tetheredA && !tetheredB) {
					grappleB = new Grapple(5);
					cooldownB = grappleCooldown;
				}
				if (!grappleB.getTethered() && !grappleA.getTethered()
						&& grapplingB && aimingUpB == aimingDownB
						&& aimingLeftB && !aimingRightB && cooldownB == 0
						&& !tetheredA && !tetheredB) {
					grappleB = new Grapple(6);
					cooldownB = grappleCooldown;
				}
				if (!grappleB.getTethered() && !grappleA.getTethered()
						&& grapplingB && aimingUpB && !aimingDownB
						&& aimingLeftB && !aimingRightB && cooldownB == 0
						&& !tetheredA && !tetheredB) {
					grappleB = new Grapple(7);
					cooldownB = grappleCooldown;
				}
				if (shootingLeftA && numShotsA < maxShots
						&& shipA.canShootLeft()) {// add a shot on to the array
													// if the ship is shooting
					shotsA[numShotsA] = shipA.shootLeft();
					numShotsA++;
				}
				if (shootingRightA && numShotsA < maxShots
						&& shipA.canShootRight()) {// add a shot on to the array
													// if the ship is shooting
					shotsA[numShotsA] = shipA.shootRight();
					numShotsA++;
				}
				if (shootingLeftB && numShotsB < maxShots
						&& shipB.canShootLeft()) {// add a shot on to the array
													// if the ship is shooting
					shotsB[numShotsB] = shipB.shootLeft();
					numShotsB++;
				}
				if (shootingRightB && numShotsB < maxShots
						&& shipB.canShootRight()) {// add a shot on to the array
													// if the ship is shooting
					shotsB[numShotsB] = shipB.shootRight();
					numShotsB++;
				}

			}

			try {
				endTime = System.currentTimeMillis();
				if (framePeriod - (endTime - startTime) > 0)
					Thread.sleep(framePeriod - (endTime - startTime));
			} catch (InterruptedException e) {
			}
		}
	}

	private boolean collision(Ship shipA, Ship shipB) {
		double t = 1;
		double xA = shipA.getX(), xB = shipB.getX(), yA = shipA.getY(), yB = shipB
				.getY();
		double vXA = shipA.getXVel(), vXB = shipB.getXVel(), vYA = shipA
				.getYVel(), vYB = shipB.getYVel();
		double r = shipA.getRadius();
		double a = Math.pow((vXA - vXB), 2) + Math.pow((vYA - vYB), 2);
		double b = 2 * ((vXA - vXB) * (xA - xB) + (vYA - vYB) * (yA - yB));
		double c = Math.pow((xA - xB), 2) + Math.pow((yA - yB), 2);
		double f = Math.pow((2 * r), 2);
		double t1, t2;

		t1 = (-b + Math.sqrt(Math.pow(b, 2) - 4 * a * (c - f))) / (2 * a);
		t2 = (-b - Math.sqrt(Math.pow(b, 2) - 4 * a * (c - f))) / (2 * a);
		if (0 <= t1 && t1 <= 1 && 0 <= t2 && t2 <= 1) {
			if (t1 <= t2)
				t = t1;
			else
				t = t2;
		} else if (0 <= t1 && t1 <= 1) {
			t = t1;
		} else if (0 <= t2 && t2 <= 1) {
			t = t2;
		} else if (Math.sqrt(Math.pow(xA - xB, 2) + Math.pow(yA - yB, 2)) <= 30.000001) {
			t = 0;
		} else {
			return false;
		}
		double newXA = shipA.getX() + t * shipA.getXVel();
		double newYA = shipA.getY() + t * shipA.getYVel();
		double newXB = shipB.getX() + t * shipB.getXVel();
		double newYB = shipB.getY() + t * shipB.getYVel();
		while (Math.sqrt(Math.pow(newXA - newXB, 2)
				+ Math.pow(newYA - newYB, 2)) <= 30.1) {
			//double angle = Math.atan((newXB - newXA) / (newYB - newYA));
			double difference = 30.2 - Math.sqrt(Math.pow(newXA - newXB, 2)
					+ Math.pow(newYA - newYB, 2));
			if (newXA > newXB) {
				newXA += difference;
				newXB -= difference;
			} else {
				newXA -= difference;
				newXB += difference;
			}
			if (newYA > newYB) {
				newYA += difference;
				newYB -= difference;
			} else {
				newYA -= difference;
				newYB += difference;
			}
		}
		shipA.setPosition(newXA, newYA);
		shipB.setPosition(newXB, newYB);
		double d = Math.sqrt(Math.pow(shipA.getX() - shipB.getX(), 2)
				+ Math.pow(shipA.getY() - shipB.getY(), 2));
		double origAVel = Math.sqrt(Math.pow(shipA.getXVel(), 2)
				+ Math.pow(shipA.getYVel(), 2));
		double origBVel = Math.sqrt(Math.pow(shipB.getXVel(), 2)
				+ Math.pow(shipB.getYVel(), 2));
		double normalX = (shipA.getX() - shipB.getX()) / d;
		double normalY = (shipA.getY() - shipB.getY()) / d;
		double normalXVelA = (shipA.getXVel() * -normalX + shipA.getYVel()
				* -normalY)
				* -normalX;
		double normalXVelB = (shipB.getXVel() * normalX + shipB.getYVel()
				* normalY)
				* normalX;
		double normalYVelA = (shipA.getXVel() * -normalX + shipA.getYVel()
				* -normalY)
				* -normalY;
		double normalYVelB = (shipB.getXVel() * normalX + shipB.getYVel()
				* normalY)
				* normalY;
		double tangentialXVelA = shipA.getXVel() - normalXVelA;
		double tangentialXVelB = shipB.getXVel() - normalXVelB;
		double tangentialYVelA = shipA.getYVel() - normalYVelA;
		double tangentialYVelB = shipB.getYVel() - normalYVelB;
		double newXVelA = tangentialXVelA + normalXVelB;
		double newXVelB = tangentialXVelB + normalXVelA;
		double newYVelA = tangentialYVelA + normalYVelB;
		double newYVelB = tangentialYVelB + normalYVelA;
		double newAVel = Math.sqrt(Math.pow(newXVelA, 2)
				+ Math.pow(newYVelA, 2));
		double newBVel = Math.sqrt(Math.pow(newXVelB, 2)
				+ Math.pow(newYVelB, 2));
		shipA.setVelocity(newXVelA, newYVelA);
		shipB.setVelocity(newXVelB, newYVelB);

		if (Math.abs(origAVel - newAVel) > 2) {
			shipA.setHP(shipA.getHP() * 150 - collisionDmgMod
					* Math.abs(origAVel - newAVel));
		}
		if (Math.abs(origBVel - newBVel) > 2) {
			shipB.setHP(shipB.getHP() * 150 - collisionDmgMod
					* Math.abs(origBVel - newBVel));
		}
		if (shipB.getHP() <= 0 && shipA.getHP() <= 0) {
			startText = "NOBODY WINS!!";
			reset();
		} else if (shipB.getHP() <= 0) {
			startText = "PLAYER 1 WINS!";
			reset();
		} else if (shipA.getHP() <= 0) {
			startText = "PLAYER 2 WINS!";
			reset();
		}
		return true;

	}

	private void deleteShotB(int index) {
		// delete shot and move all shots after it up in the array
		numShotsB--;
		for (int i = index; i < numShotsB; i++)
			shotsB[i] = shotsB[i + 1];
		shotsB[numShotsB] = null;

	}

	private void deleteShotA(int index) {
		// delete shot and move all shots after it up in the array
		numShotsA--;
		for (int i = index; i < numShotsA; i++)
			shotsA[i] = shotsA[i + 1];
		shotsA[numShotsA] = null;
	}

	public void keyPressed(KeyEvent e) {
		if (active) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_2:// P1 AIM GRAPPLE UP
				aimingUpA = true;
				break;
			case KeyEvent.VK_Q:// P1 AIM GRAPPLE LEFT
				aimingLeftA = true;
				break;
			case KeyEvent.VK_W:// P1 AIM GRAPPLE DOWN
				aimingDownA = true;
				break;
			case KeyEvent.VK_E:// P1 AIM GRAPPLE RIGHT
				aimingRightA = true;
				break;
			case KeyEvent.VK_R:// P1 FIRE GRAPPLE
				grapplingA = true;
				break;
			case KeyEvent.VK_6:// P1 ACCELERATE
				shipA.setAccelerating(true);
				break;
			case KeyEvent.VK_Y:// P1 DECELERATE
				shipA.setDecelerating(true);
				break;
			case KeyEvent.VK_T:// P1 ROTATE LEFT
				shipA.setTurningLeft(true);
				break;
			case KeyEvent.VK_U:// P1 ROTATE RIGHT
				shipA.setTurningRight(true);
				break;
			case KeyEvent.VK_4:// P1 BOOST
				shipA.setBoosting(true);
				break;
			case KeyEvent.VK_7:// P1 FIRE RIGHT
				shootingRightA = true;
				break;
			case KeyEvent.VK_5:// P1 FIRE LEFT
				shootingLeftA = true;
				break;
			case KeyEvent.VK_S:// P2 AIM GRAPPLE UP
				aimingUpB = true;
				break;
			case KeyEvent.VK_Z:// P2 AIM GRAPPLE LEFT
				aimingLeftB = true;
				break;
			case KeyEvent.VK_X:// P2 AIM GRAPPLE DOWN
				aimingDownB = true;
				break;
			case KeyEvent.VK_C:// P2 AIM GRAPPLE RIGHT
				aimingRightB = true;
				break;
			case KeyEvent.VK_V:// P2 FIRE GRAPPLE
				grapplingB = true;
				break;
			case KeyEvent.VK_H:// P2 ACCELERATE
				shipB.setAccelerating(true);
				break;
			case KeyEvent.VK_N:// P2 DECELERATE
				shipB.setDecelerating(true);
				break;
			case KeyEvent.VK_B:// P2 ROTATE LEFT
				shipB.setTurningLeft(true);
				break;
			case KeyEvent.VK_M:// P2 ROTATE RIGHT
				shipB.setTurningRight(true);
				break;
			case KeyEvent.VK_F:// P2 BOOST
				shipB.setBoosting(true);
				break;
			case KeyEvent.VK_J:// P2 FIRE RIGHT
				shootingRightB = true;
				break;
			case KeyEvent.VK_G:// P2 FIRE LEFT
				shootingLeftB = true;
				break;
			default:// other key
			}
		} else if ((e.getKeyCode() == KeyEvent.VK_2
				|| e.getKeyCode() == KeyEvent.VK_Q
				|| e.getKeyCode() == KeyEvent.VK_W
				|| e.getKeyCode() == KeyEvent.VK_E
				|| e.getKeyCode() == KeyEvent.VK_4
				|| e.getKeyCode() == KeyEvent.VK_5
				|| e.getKeyCode() == KeyEvent.VK_6
				|| e.getKeyCode() == KeyEvent.VK_7
				|| e.getKeyCode() == KeyEvent.VK_R
				|| e.getKeyCode() == KeyEvent.VK_T
				|| e.getKeyCode() == KeyEvent.VK_Y
				|| e.getKeyCode() == KeyEvent.VK_U
				|| e.getKeyCode() == KeyEvent.VK_S
				|| e.getKeyCode() == KeyEvent.VK_Z
				|| e.getKeyCode() == KeyEvent.VK_X
				|| e.getKeyCode() == KeyEvent.VK_C
				|| e.getKeyCode() == KeyEvent.VK_F
				|| e.getKeyCode() == KeyEvent.VK_G
				|| e.getKeyCode() == KeyEvent.VK_H
				|| e.getKeyCode() == KeyEvent.VK_J
				|| e.getKeyCode() == KeyEvent.VK_V
				|| e.getKeyCode() == KeyEvent.VK_B
				|| e.getKeyCode() == KeyEvent.VK_N || e.getKeyCode() == KeyEvent.VK_M)
				&& nextGameDelay == 0) {
			active = true;
		}
	}

	public void keyReleased(KeyEvent e) {
		if (active) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_2:// P1 AIM GRAPPLE UP
				aimingUpA = false;
				break;
			case KeyEvent.VK_Q:// P1 AIM GRAPPLE LEFT
				aimingLeftA = false;
				break;
			case KeyEvent.VK_W:// P1 AIM GRAPPLE DOWN
				aimingDownA = false;
				break;
			case KeyEvent.VK_E:// P1 AIM GRAPPLE RIGHT
				aimingRightA = false;
				break;
			case KeyEvent.VK_R:// P1 FIRE GRAPPLE
				grapplingA = false;
				break;
			case KeyEvent.VK_6:// P1 ACCELERATE
				shipA.setAccelerating(false);
				break;
			case KeyEvent.VK_Y:// P1 DECELERATE
				shipA.setDecelerating(false);
				break;
			case KeyEvent.VK_T:// P1 ROTATE LEFT
				shipA.setTurningLeft(false);
				break;
			case KeyEvent.VK_U:// P1 ROTATE RIGHT
				shipA.setTurningRight(false);
				break;
			case KeyEvent.VK_4:// P1 BOOST
				shipA.setBoosting(false);
				break;
			case KeyEvent.VK_7:// P1 FIRE RIGHT
				shootingRightA = false;
				break;
			case KeyEvent.VK_5:// P1 FIRE LEFT
				shootingLeftA = false;
				break;
			case KeyEvent.VK_S:// P2 AIM GRAPPLE UP
				aimingUpB = false;
				break;
			case KeyEvent.VK_Z:// P2 AIM GRAPPLE LEFT
				aimingLeftB = false;
				break;
			case KeyEvent.VK_X:// P2 AIM GRAPPLE DOWN
				aimingDownB = false;
				break;
			case KeyEvent.VK_C:// P2 AIM GRAPPLE RIGHT
				aimingRightB = false;
				break;
			case KeyEvent.VK_V:// P2 FIRE GRAPPLE
				grapplingB = false;
				break;
			case KeyEvent.VK_H:// P2 ACCELERATE
				shipB.setAccelerating(false);
				break;
			case KeyEvent.VK_N:// P2 DECELERATE
				shipB.setDecelerating(false);
				break;
			case KeyEvent.VK_B:// P2 ROTATE LEFT
				shipB.setTurningLeft(false);
				break;
			case KeyEvent.VK_M:// P2 ROTATE RIGHT
				shipB.setTurningRight(false);
				break;
			case KeyEvent.VK_F:// P2 BOOST
				shipB.setBoosting(false);
				break;
			case KeyEvent.VK_J:// P2 FIRE RIGHT
				shootingRightB = false;
				break;
			case KeyEvent.VK_G:// P2 FIRE LEFT
				shootingLeftB = false;
				break;
			default:// other key
			}
		}

	}

	public void keyTyped(KeyEvent e) { // empty. needed to implement the
										// KeyListener interface
	}
}