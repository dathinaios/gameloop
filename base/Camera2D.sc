
Camera2D : Vehicle { classvar <>fwd, <>back, <>rotLeft, <>rotRight, <instance;
					 var <>collisionFunc, <>arrivePosition;


*new{ arg world, position, radius, mass, velocity, controller,
		  heading, side, maxSpeed, maxForce, maxTurnRate, collisionFunc;

		if(instance.isNil, 
			{
			^super.new(world, 
						position, 
						radius, 
						mass
			).velocity_(velocity)
			.controller_(controller)
			.heading_(heading)
			.side_(side)
			.maxSpeed_(maxSpeed)
			.maxForce_(maxForce)
			.maxTurnRate_(maxTurnRate)
			.collisionFunc_(collisionFunc)
			.init;		
			},
			{"There is already an active instance of Camera2D".error;}
		);		
}

init{
	//"simplecircle init".postln;
	super.init;
	heading = heading ?? {RealVector2D[0,0]};
	instance = this;
	arrivePosition = position;
}

remove {
	"WARNING: To remove the camera you will need to call .removeCamera on the EntityManager".postln;
}

*applyTransformation{ arg ent;
	^instance.applyTransformation(ent);
}

*position{
	^instance.position
}

*position_{ arg i;
	instance.position_(i);
}

applyTransformation{ arg entity; 
					 var entPos, newPos, theta, thetaSin, thetaCos, rad, x,y;
					 var xMinusx, yMinusy;
	//translate position according to rotation and camera position	
	entPos = entity.position;
	theta = heading.norm;
	thetaSin = theta.sin;
	thetaCos = theta.cos;
	xMinusx = entPos[0] - position[0];
	yMinusy = entPos[1] - position[1];
	x = (xMinusx * thetaCos) - (yMinusy * thetaSin);
	y = (xMinusx * thetaSin) + (yMinusy * thetaCos);
	^RealVector2D[x,y];
}

moveFwd{arg amount = 0.02; var theta, x, y;
	theta = heading;
	//theta.debug("theta");
	y = theta.cos;
	x = theta.sin;
	//y.debug("y"); x.debug("y");
	//position = position + (amount *RealVector2D[x, y]);
	arrivePosition = arrivePosition + (amount *RealVector2D[x, y]);
	arrivePosition.debug("FINAL");
}

moveBack{arg amount = 0.02; var theta, x, y;
	theta = heading;
	y = theta.cos;
	x = theta.sin;
	//position = position - (amount *RealVector2D[x, y]);
	arrivePosition = arrivePosition - (amount *RealVector2D[x, y]);
}

moveLeft{arg amount = 0.23; 
	position = position - RealVector2D[amount, 0]
}

rotateLeft{arg amount = 0.001pi;
	heading = heading - amount;
}

rotateRight{arg amount = 0.001pi;
	heading = heading + amount;
}

reset{
	arrivePosition = world.center;
	heading = 0;
}

}

Camera2DController : Controller{
	
	getForce { arg entity;
		//I could use an arrive behavior and change the 
		//position according to the move forward etc.
			//entity.position.debug("position");
			//^RealVector2D[0,0];
			^Arrive.calculate( 
				entity, 
				entity.arrivePosition
			);
	}

	/*
	getForce { arg entity, amount = 9, rotAmount = 0.025pi; 
			var theta, x,y, fwd, back, rotLeft, rotRight;
			fwd = Camera2D.fwd;
			back = Camera2D.back;
			rotLeft = Camera2D.rotLeft;
			rotRight = Camera2D.rotRight;
			if(fwd || back || rotLeft || rotRight,
				{
				  case
					{fwd}
					{ 	
						Camera2D.fwd_(false);
						theta = heading;
						y = theta.cos;
						x = theta.sin;
						^(entity.velocity + (amount *RealVector2D[x, y].normalize));
					}
					{back}
					{ 	
						Camera2D.back_(false);
						theta = heading;
						y = theta.cos;
						x = theta.sin;
						^(entity.velocity - (amount *RealVector2D[x, y].normalize));
					}
					{rotLeft}
					{ 	Camera2D.rotLeft_(false);
						heading = heading - rotAmount;
						^RealVector2D[0,0]
					}
					{rotRight}
					{ 	Camera2D.rotRight_(false);
						heading = heading + rotAmount;
						^RealVector2D[0,0]
					};
				},
				{
					entity.velocity = entity.velocity * 0.5;
					^RealVector2D[0,0]
				}
			)
	}
	*/

}

Camera2DRepresentation : SimpleCircleRepresentation{

	init { 
		position = entity.position;
		radius = entity.radius;
		color = color ?? {Color.white};
		collisionColor = collisionColor ?? {Color.red};
	}

	draw{arg rect; Pen.strokeRect(rect)}

}

/*
*initClass{
	//initialise the values for the keyboard hack
	fwd = false;
	back = false;
	rotLeft = false;
	rotRight = false;
	//make the menus with the shortcuts for the keyboard hack
	if(GUI.current.asSymbol == 'CocoaGUI',	
		{
		MenuItem.add(["Camera2D motion", "frw"],
			{Camera2D.fwd_(true)})
			.setShortCut ("i", true);
		MenuItem.add(["Camera2D motion", "back"],
			{Camera2D.back_(true)})
			.setShortCut (",", true);
		MenuItem.add(["Camera2D motion", "left"],
			{Camera2D.rotLeft_(true)})
			.setShortCut ("j", true);
		MenuItem.add(["Camera2D motion", "right"],
			{Camera2D.rotRight_(true)})
			.setShortCut ("l", true);
	});

}
*/
