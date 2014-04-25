Group Members: 
	Chris Stickney, Ryan Bergquist, Kaden Buckbee
	
Included files:
	-Game.java
	-Ship.java
	-Shot.java
	-Grapple.java
	-Controls.jpg
	-ssb.jar
	-SSB.html
	-Feedback Response.txt
	-Readme.txt
	-superSpaceBoat
		-Game.class
		-Ship.class
		-Shot.class
		-Grapple.class
	
Running:
	Open SSB.html in a web browser, allow the applet to run, and then click inside the game window (not the image on the right)
	
How to play:
	The image to the right of the game shows the controls and their control mappings.
	press any of the defined keys to begin a game
	
	Both players control a ship which has the following capabilities:
		accelerate: accelerates in the direction the ship is facing
		brake: reduces all velocities associated with this ship by 5% per frame
		turn left/right: rotates the ship left or right
		boost: accelerates faster than accelerate in the direction the ship is facing
		shoot left/right: shoots left or right of the ship
		grapple: shoots a grapple if a grapple direction is pressed in conjunction with the grapple button.
			if the grapple hits the other ship, both ships and the grapple become a new system which applies the forces on each ship to the new system
			when the grapple expires, the grapple system's forces are reapplied to the ships
			the following functionalities change when the ships are attached by the grapple:
				accelerate: the ship accelerates in the direction faced, which applies that acceleration to the grapple system
				brake: all velocities associated with the grapple system are reduced by 5% per frame
				boost:the ship boosts in the direction faced, which applies that acceleration to the grapple system
	
	Players win the game by hitting the opposing ship with enough shots to bring the enemy ship's HP to zero
	once the game is over, press any defined key to begin another game
	