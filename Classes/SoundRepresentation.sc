
/*
	This is a basic Mobile Unit with varying sound input
*/

SoundRepresentation : EntityRepresentation { 

	var >input, >release = 0.2, >color, >collisionColor;
	var <penWidth = 1.5;
	var encoderClass, <encoderProxy, summingProxy, <encoderProxyIndex;
							
	*new { arg  repManager, input, 
							release, color, collisionColor;  
		^super.new(repManager)
					.input_(input) 
					.release_(release) 
					.color_(color) 
					.collisionColor_(collisionColor);
	}

	init { 
		super.init;
		release = release ?? {0.2};

		color = color ?? {Color.green};
		collisionColor = collisionColor ?? {Color.red};

		/* decoder init */
		this.initializeDecoder;

		/* make some sound */
		this.play;

	}

	/* public */

	remove{
		 Routine{
			//clear everything with given realease time
			encoderProxy.clear(release);
			//wait for the release to finish
			release.wait;
			//remove the node from the summing bus
			summingProxy.removeAt(encoderProxyIndex);
			repManager.remove(this);
			attached = false;
	 	}.play(TempoClock.default);
	}

	draw{arg rect; 
		Pen.strokeOval(rect)
	}

	/* private */

	initializeDecoder{
		encoderClass = GameLoopDecoder.getEncoderClass;
		/* get the right proxy from the GameLoopDecoder class */
		encoderProxy = GameLoopDecoder.getEncoderProxy;
		encoderProxy.clock = TempoClock.default;
		/* plug the proxy to the proxy acting as summing bus */
		summingProxy = GameLoopDecoder.summingProxy;
		/* Always put the new Node in an extra slot of the Summing nodeRpoxy */
		encoderProxyIndex = summingProxy.sources.size - 1;
		summingProxy.put(encoderProxyIndex, encoderProxy);
	}
	
	play {
		var latency;
		latency = Server.default.latency;
		Routine{
			this.addSource;
			/* wait for 'latency' before adding to managers so that everything is in sync. */
			if(latency.notNil) {latency.wait};
			/* Add everything at exactly the same time as the bundle */
			if (entity.active.not){entity.add};
			repManager.add(this);
		}.play;
	}

	addSource{
			encoderProxy.source = { arg dt;
				var x , y;
				var rad, azim, elev, in, speed;

				dt = this.dt;

				/* Ramp is used to interpolate between updates */
				#x, y = Control.names(#[x, y]).kr([position[0], position[1]]);
				x = Ramp.kr(x, dt);
				y = Ramp.kr(y, dt);

				speed = Control.names(\speed).kr(entity.velocity.norm);
				speed = Ramp.kr(speed, dt); 

				/* play default if input is not supplied */
				if(input == nil,
					{
						in = Impulse.ar(speed.linlin(0,10, 5, rrand(50, 200.0)));
						in = BPF.ar(in, rrand(2000, 18000.0)*rrand(0.3, 2.0), 0.4);
					},
					{in = input.value(speed)}
				);

				/* calculate azimuth and radius */
				azim = atan2(y,x);
				rad = hypot(x,y);
				elev = 0;

				/* get and use the relevant encoder */
				encoderClass.ar(
					in, 
					azim, 
					rad, 
					elev: elev, 
					ampCenter: 0.9
				);
			};
	}

	color { if(entity.colliding, {^collisionColor },{^color})
	}

	preUpdate{ arg theChanged, transPosition;
		/* set the syth with the new position values */
		encoderProxy.set('speed',entity.velocity.norm);
		encoderProxy.set('x', transPosition[0]-20);
		encoderProxy.set('y', transPosition[1]-20);
	}

	collision{ arg message;
		/* message should have a list at [1] with the colliding with entities*/
		collisionFunc.value(this, message[1]);
	}

	
}   
