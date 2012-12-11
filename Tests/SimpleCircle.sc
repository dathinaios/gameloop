

SimpleCircleEnt : Vehicle { var  >collisionFunc, <>type;
	
	initHook1 { 
		controller =SimpleCircleController(this, entityParams);
	}

	collision { arg entList; colliding = true;
				collisionFunc.value(entList, this);
	}
}

SimpleCircleRep : EntityRepresentation { var <penWidth = 1.5;
							   	   var <position, <radius;
								   var color, collisionColor;
							
	*new { arg  entity, entityParams;  
	^super.newCopyArgs(entity, entityParams).init
	}

	init {  this.getData;
	/*	color = Color.white; 
			collisionColor = Color.white;*/
		color = entityParams.get['color'] ?? Color.white; 
		collisionColor = entityParams.get['collisionColor'] ?? Color.white;
	}
	
	color { if(entity.colliding, {^collisionColor },{^color})
	}

	getData{ 
		position = entity.position;
		radius = entity.radius;
	}
	
	draw{arg rect; Pen.strokeOval(rect)}
	
	setFR{}
	
}   

SimpleCircleController : Controller {
	
	getForce { arg entity;
			  if (entityParams.get['steering'].isNil,
					{^0},
			  		{^entityParams.get['steering'].value(entity)}
			  );
	}

}
