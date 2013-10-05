
GameLoopGUI{
			 classvar <instance;
			 var gameloop, <entManager, <repManager;
			 var dimensions, gridSize, cellSize, <mainView;
			 var leftRotationRoutine, rightRotationRoutine, fwdRotationRoutine, backRotationRoutine;
			 var visualiser;

	*new{ arg gameloop;
			if(instance.isNil, 
				{
				^super.newCopyArgs(gameloop).init;
				},
				{"There is already an active instance of GameLoopGUI".error;}
			);		
	}

	init{
		entManager = gameloop.entManager;
		repManager = gameloop.repManager;
		visualiser   = GameLoopVisualisation(entManager,repManager);
		instance = this;
		CmdPeriod.add({this.clear});
		this.initCameraRoutines;
		visualiser.gui;
		this.gui;
	}
	
	update{
		{visualiser.render}.defer;
	}

	render {
		if(mainView.notNil, {mainView.refresh});
	}

	close {
		if(mainView.notNil, {mainView.close}, {"There is no view open for GameLoopGUI".error});
	}

	clear{
		if(mainView.notNil, {mainView.close});
		instance = nil;
		visualiser.clear;
	}

	gui{

		mainView ?? {

		 var   h = 700, v = 400,run = true;
		 mainView = Window("GameLoop", Rect(-1350, 600, h, v), false);
		 mainView.view.background = Color.black;
		 mainView.onClose = { run = false; mainView = nil; }; // stop the thread on close
		 mainView.front;
		 mainView.alwaysOnTop = true;
		 this.createVisualiserButton;

		 //Pen.image("/Users/dathinaios/Develop/supercollider/gameloop/Classes/Logo.png");
		 mainView.drawFunc = { };
		 this.setWindowKeyActions;
    }
	}

	createVisualiserButton{ var visButton;
		 visButton = Button(mainView, Rect(10,10,150,30)).states_([
			     ["Visualiser",Color.grey,Color.black],
			     ["Close Visualiser",Color.green,Color.black],
		 ]);

		 if (visualiser.mainView != nil, 
				{visButton.value = 1},
				{visButton.value = 0}
			);

		 visButton.action_({arg butt;
		 	 switch (butt.value)
			 {1}{visualiser.gui}
			 {0}{visualiser.close};
		 });
		 ^visButton.canFocus = false;
	}

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

