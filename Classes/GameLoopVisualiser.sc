
GameLoopVisualiser{
			 classvar <instance;
			 var <entManager, <repManager;
			 var dimensions, gridSize, cellSize, <mainView;
			 /* shrtcuts for control of camera for focused window. These are going to be moved somewhere else */
			 var leftRotationRoutine, rightRotationRoutine, fwdRotationRoutine, backRotationRoutine;

	*new{ arg entManager, repManager;
			if(instance.isNil, 
				{
				^super.newCopyArgs(entManager, repManager).init;
				},
				{"There is already an active instance of GameLoopVisualiser".error;}
			);		
	}

	init{
		instance = this;
		CmdPeriod.add({this.clear});
		dimensions = [0, entManager.center[0]*2];
		this.initCameraRoutines;
	}

	render {
		if(mainView.notNil, {mainView.refresh});
	}

	close {
		if(mainView.notNil, {mainView.close}, {"There is no view open for GameLoopVisualiser".error});
	}

	clear{
		if(mainView.notNil, {mainView.close});
		instance = nil;
	}

	gui{

		mainView ?? {

		 var   h = 400, v = 400, seed, run = true,  spaceUnits, spaceUnits2, meterInPixels,  speakerRadInPixels;
		 var text;
		 mainView = Window("Visualiser", Rect(-450, 400, h, v), false);
		 mainView.view.background = Color.black;
		 mainView.onClose = { run = false; mainView = nil; }; // stop the thread on close
		 mainView.front;
		 mainView.alwaysOnTop = true;
		 //for  space of 700 pixels is 20 meters one meter is 35 pixels
		 meterInPixels = h/(dimensions[1]-dimensions[0]); //assumes h = v
		 speakerRadInPixels = 2 * meterInPixels;
		 /* display some useful info */
		 text = StaticText(mainView, Rect(3, 3, 200, 20)).stringColor_(Color.grey);
		 
		 mainView.drawFunc = {
		 	/* Pen.use { */
		 	var divisions, subOrAdd;
		 	var repList;
		 	
		 	//to draw the obstacles 
		 	Pen.width = 2;
		 	text.string = "Ents: " + entManager.activeEntities.asString + 
		 							  "- Reps: " + repManager.activeReps.asString;
		 	repList = repManager.repList;
				repList.size.do { 
					arg index; 
					var spaceIn, currentObst, curRadPix, curWidth, obstacle, obstacPos; 
					obstacle = repList[index]; //get the current object
					if(obstacle.type == 'visual')
					{
						//get position using camera if active
						obstacPos = obstacle.position;
						Pen.width = obstacle.penWidth;
						Pen.color = obstacle.color.alpha_(0.7);
						Pen.beginPath;
						//find the radius in meters and then in pixels
						currentObst = obstacle.radius;
						curRadPix = currentObst*meterInPixels;
						curWidth = curRadPix + curRadPix;
						//Pen.strokeOval(Rect((obstacle.position[0]*meterInPixels)-curRadPix, ((obstacle.position[1]*meterInPixels).linlin(0, 700, 700, 0))-curRadPix, curWidth, curWidth));
						obstacle.draw((Rect((obstacPos[0]*meterInPixels)-curRadPix, ((obstacPos[1]*meterInPixels).linlin(0, h, v, 0))-curRadPix, curWidth, curWidth)))
					}
				};
		 };
		 this.setWindowKeyActions;
    }
	}

	/* Shortcuts for control of camera from focused window */

	initCameraRoutines{
		leftRotationRoutine = Routine{ 
			loop{
			Camera2D.instance.rotateLeft;
			0.05.wait;
			};
		};

		rightRotationRoutine = Routine{
			loop{
			Camera2D.instance.rotateRight;
			0.05.wait;
			};
		};

		fwdRotationRoutine = Routine{
			loop{
			Camera2D.instance.forceFwd;
			0.05.wait;
			};
		};

		backRotationRoutine = Routine{
			loop{
			Camera2D.instance.forceBack;
			0.05.wait;
			};
		};
	}

	setWindowKeyActions{
			//Specific mainview setting and keyboard controls
			mainView.view.keyDownAction = 
				{arg view, char, modifiers, unicode, keycode;
					switch (keycode)
					{126}
					{
						if(fwdRotationRoutine.isPlaying.not)
						  {fwdRotationRoutine.reset.play};
					}
					{125}
					{
						if(backRotationRoutine.isPlaying.not)
						  {backRotationRoutine.reset.play};
					}
					{123}
					{
						if(leftRotationRoutine.isPlaying.not)
						  {leftRotationRoutine.reset.play};
					}
					{124}
					{
						if(rightRotationRoutine.isPlaying.not)
						  {rightRotationRoutine.reset.play};
					}
				};

			mainView.view.keyUpAction = 
				{arg view, char, modifiers, unicode, keycode;
					switch (keycode)
					{123}{leftRotationRoutine.stop}
					{124}{rightRotationRoutine.stop}
					{125}{backRotationRoutine.stop}
					{126}{fwdRotationRoutine.stop}
				};

	}

}
