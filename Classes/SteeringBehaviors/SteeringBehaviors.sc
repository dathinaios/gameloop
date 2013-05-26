//SteeringBehaviors{ var <steeringList;
//
//
//
//	*new {
//
//	^super.new.init;
//
//	} 
//
//
//
//	init {
//		steeringList = List.new;
//	}
//	
//	//**\\
//
//
//
//	calculate { arg entity; //this method sums the forces from all the active behaviours
//			   var force = 0;	 //and returns a force
//			 steeringList.do{arg i;
//			 	force = force + i.calculate(entity);
//			 }			 
//			 ^force;
//			   
//	}
//	
//	add { arg behavior;
//		steeringList = steeringList.add(behavior);
//	}
//}

/* ======================= */
/* = Steering Behaviours = */
/* ======================= */

// For implementation details refer to the book Game AI by example by Mat Buckland

Seek {

	*new{ "You can not have an instance of this".error;
	}

	*calculate{ arg entity, targetPos = RealVector2D[10,13]; //^force
		   var desiredVelocity; //, maxSpeed = 30;
		
		//seek steering behaviour
		desiredVelocity = targetPos - entity.position;
		desiredVelocity = desiredVelocity.normalize;
		desiredVelocity = desiredVelocity * entity.maxSpeed;
		^(desiredVelocity - entity.velocity);
	}
}

Arrive { //Deceleration{slow = 3, normal = 2, fast = 1};

	*new{ "You can not have an instance of this".error;
	}

	*calculate{ arg entity, targetPos = RealVector2D[10,13], deceleration = 2, tweak = 0.3; //^force
		   	   var desiredVelocity, toTarget, speed, dist;
		
			toTarget = targetPos - entity.position;
			//toTarget.class.debug("what type is that?");
			//distance to target
			//
			//entity.position.debug("current position"); //delete
			//
			dist = toTarget.norm;
			//dist.debug("distance"); //delete
			
			if ( dist > 0,
				{
				speed = dist / (deceleration * tweak);
				speed = speed.min(entity.maxSpeed);
				//speed.debug("speed"); //de;ete
				desiredVelocity = (toTarget*speed)/dist;
				//desiredVelocity.debug("desired velocity");//delete
				^(desiredVelocity - entity.velocity); //.debug("resulting force");
				},
				{
				^RealVector2D[0,0];
				}
			);
			
			
	}
}

PathFollowing{ //Deceleration{slow = 3, normal = 2, fast = 1};

	*new{ "You can not have an instance of this".error;
	}

	*calculate{ arg entity, path, seekDistance = 0.5;
			 var wayPoint;
			wayPoint = path.wayPoint;
			
			if (entity.position.distanceSq(wayPoint) < seekDistance) {path.setNextWayPoint};
			
			if (path.finished,
				{
				^Arrive.calculate(entity, wayPoint);
				},
				{
				^Seek.calculate(entity, wayPoint);
				}
			);	
	}
}

//RealVector2D[10, 10].distanceSq(RealVector2D[10.5, 10.5])
//************\\
//related classes

Path{ var <wayPoints, <>loop, curWayPoint = 0; 

	*new { arg  wayPoints, loop = false;  
	^super.newCopyArgs(wayPoints, loop).init
	}
	
	init{
		//PathsManager.add(this)
	}
	
	setNextWayPoint{
		if (wayPoints[curWayPoint] == wayPoints.last,
				{if(loop, {curWayPoint = 0})},
				{curWayPoint = curWayPoint+1}
		);
	}
	
	finished{ 
		if(loop, 
			{^false},
			{^wayPoints[curWayPoint] == wayPoints.last}
		);
	}
	
	wayPoint{ ^wayPoints[curWayPoint]
	}
	
//	draw{ arg meterInPixels; //quick hack to draw the path for demonstration purposes
//			
//				Pen.color = Color.white.alpha_(0.3);
//				Pen.width = 1;
//				Pen.moveTo((Point(wayPoints[0][0],wayPoints[0][1]))*meterInPixels);
//				wayPoints.do{ arg item, i;
//					if(i != (wayPoints.size - 1))
//						{Pen.lineTo((Point(wayPoints[i+1][0],wayPoints[i+1][1]))*meterInPixels)}
//						{
//						if(loop) {Pen.lineTo((Point(wayPoints[0][0],wayPoints[0][1]))*meterInPixels)}
//						};
//				};
//	}
	
//	set{ arg index; 
//	}
//	
//	setAll{newList}

}

PathsManager{ classvar <paths;

	*initClass{
		paths = List.new;
	}
	
	*add{ arg path; paths = paths.add(path);
	}
	
	*clear{ paths.clear;
	}

}


//Red Universe extensions

RedSeek {

	*new{ "You can not have an instance of this".error;
	}

	*calculate{ arg entity, targetPos = RedVector2D[10,13], maxSpeed; //^force
		   var desiredVelocity; //, maxSpeed = 30;
		
		//seek steering behaviour
		desiredVelocity = targetPos - entity.loc;
		desiredVelocity = desiredVelocity.normalize;
		desiredVelocity = desiredVelocity * maxSpeed;
		^(desiredVelocity - entity.vel);
	}
}

