EntityRepresentation { var entity, repManager, <position, <radius;

	*new { arg  entity, repManager;  
	^super.newCopyArgs(entity, repManager)
	} 

	// the dependancy passes in the changed and any additional arguments 
	update { arg entity, message;  //entity var is theChanger
		//in the subclass call super.update to do this and then add your own like:
		switch (message)//a typical use of a .changed notification (could be case for multiple)
		{\update} {position = entity.position; radius = entity.radius};
		//in subclass you can use:
		//switch (message) 
		//{\remove} {this.remove}
 		//{message == \collision} {this.collision};
	} 
	
	run { // method to run the sound representaion without adding to the manager.
		  // Usefull for compensating for the bundle time.
		"Implement run in subclass".error;
		"Then add something like this in the init method:
		Routine{
			this.run;
			if(latency.notNil) {latency.wait};
			//Add everything at exactly the same time as the bundle
			entity.add;
			repManager.add(this);
		}.play;
		".postln;
	}

	/*
	add{ repManager.add(this);
			//Main.elapsedTime.debug("I'm alive!!")
	}
	
	remove {
		repManager.remove(this);
	}

	activate { // this method adds the rep to the manager but does not
			   // run the sound representation.
		"I am in the actiavete method of EntityRepresentation".postln;
		this.add;
	
	}
	activateRun{ // this method adds the rep to the manager and then calls run
			 // run should be implemented in the subclass and will contain 
			 // all the things relevenat to the particular representation
		this.add;
		this.run;
	}
	*/
}   
