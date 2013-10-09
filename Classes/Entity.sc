
Entity {
		var <>world, <>position, <>radius, <>mass;
		var <>colliding, <active, >collisionFunc; 
	*new{ arg world, position = RealVector2D[15,15], radius = 1.0, mass = 1.0;
		  ^super.newCopyArgs(world, 
												position, 
												radius, 
												mass
		  ).init;
	}

	/* The init method is called in the subclass by using super.init. Using super.init
	in all the init methods assures that everything will be called. Of course remember 
	to call init in the subclass new method to start the domino effect */
	init{ 	
			position = position ?? {world.center};
			radius = radius ?? {1.0}; 
			mass = mass ?? {1.0};
			colliding = false;
			active = false;
			collisionFunc = {};
	}

	attach{arg rep;
		this.addDependant(rep);
		this.changed([\attach])
	}

	detach{arg rep;
		this.changed([\detach, rep]);
		this.removeDependant(rep);
	}

	detachAll{ var list;
		list = this.dependants.asList;
		list.do{arg i; this.detach(i)};
	}
	
	add{ 
		world.add(this);
		active = true;
		this.changed([\add]);
	}
	
	remove { 
		world.remove(this);
		active = false;
		this.changed([\remove]);
		this.releaseDependants;
	}
	
	collision { arg entitiesArray; 
					colliding = true;
					collisionFunc.value(this, entitiesArray);
					this.changed([\collision, entitiesArray]);
	}

	dt{
		^world.dt;
	}

}     

MobileEntity : Entity { var <>velocity, <>collisionType;
												var <>force = 0;

	*new{ arg world, position = RealVector2D[15,15], 
	          radius = 1.0, mass = 1.0, velocity = RealVector2D[0,0], 
	          collisionType = \free;
		  ^super.new(world, 
					 position, 
					 radius, 
					 mass
		  ).velocity_(velocity)
		   .collisionType_(collisionType).init;
	}

	init{
		super.init;
		velocity = velocity ?? {RealVector2D[0,0]};
		collisionType  = collisionType ?? {\free};
	}

	integrateEuler{ arg force = 0;
		velocity = velocity + ((force/mass) * this.dt);
		position = position + (velocity *this.dt);
	}
	
	//implement update in subclass if needed
	//Typical:
	update {
		/* calling update on the dependants ensure that we always get set
		by the integration of the last cycle */
		this.changed([\update]);
		this.integrateEuler(force.value(this));
		/* and here we update with the future value in case we want to 
		use it for prediction as in the case of interpolation (lag) of sound 
		units */
		this.changed([\preUpdate]);
	}

}

Vehicle : MobileEntity { var <>heading, <>side, <>maxSpeed, <>maxForce, <>maxTurnRate; 
	
	*new{ arg world, position= RealVector2D[15,15], radius = 1.0, mass = 1.0, 
						velocity = RealVector2D[0, 0], collisionType = \free, maxSpeed = 100, 
						maxForce = 40, heading, side, maxTurnRate = 2;
		  ^super.new(world, 
					 position, 
					 radius, 
					 mass
		  ).velocity_(velocity)
		   .collisionType_(collisionType)
		   .maxSpeed_(maxSpeed)
		   .maxForce_(maxForce)
		   .heading_(heading)
		   .side_(side)
		   .maxTurnRate_(maxTurnRate).init;
	}
	
	init{
		super.init;
		maxSpeed = maxSpeed ?? {100};
		maxForce = maxForce ?? {40};
		maxTurnRate = maxTurnRate ?? {2};
	}

	integrateEuler{ arg force = 0; 
		velocity = velocity + ((force/mass) * this.dt);
		velocity = velocity.limit(maxSpeed);
		position = position + (velocity * this.dt);
		// update the heading and side (only if velocity is greater than *from AI by example book*)
		if (velocity.magSq > 0.00000001)
			{
			heading = velocity.normalize;
			side = heading.perp;
			};
	}

}
