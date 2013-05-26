
GameLoopDecoder { classvar <encoderProxy, <decoderProxy, <decoderBus, 
				 		  encoderChannels, decoderChannels,
				 		  <>library, <>type, <>dp, order,
				 		  kernel;

	*new{ arg library = 'AmbIEM', type = 'binaural', dp = true; /*{{{*/
		

		this.library_(library);
		this.type_(type);
		this.dp_(dp);

		//First let's get some information that we need
		case 
		//binaural
		{library =='AmbIEM' && (type == 'binaural')} {decoderChannels = 2; order = 3}
		{library =='ATK' && (type == 'newListen')} {decoderChannels = 2; order = 1};

		//encoder channels depend on the Ambisonics order (1st,2nd or 3d)
		//use by the getEncoderProxy method
		switch (order)
		{1}{ encoderChannels = 4 }
		{2}{ encoderChannels = 9 }
		{3}{ encoderChannels = 16};


		//decoded channels depend on the decoder. Create the NodeProxy if it does not exist in the right form
		if(decoderProxy.numChannels != decoderChannels)
		{decoderProxy = NodeProxy(Server.default, 'audio', decoderChannels).fadeTime_(0.5)};

		//Create the decoder according to the type given
		case 
		//binaural
		{library =='AmbIEM' && (type == 'binaural')} {
			BinAmbi3O.kemarPath_("/Applications/SuperCollider/KemarHRTF");
			BinAmbi3O.init('1_4_7_4');
			decoderProxy.source = {
				var in, out;
				//in = Control.names(\in).ar(0);
				in = \in.ar(0!encoderChannels);
				out = BinAmbi3O.ar(in);
				Out.ar(0, out);
			};
		}
		{library =='ATK' && (type == 'newStereo')} {
			//get the kernel (in this case it is a matrix)
			kernel = FoaDecoderMatrix.newStereo(131/2 * pi/180, 0.5); // Cardioids at 131 deg
				decoderProxy.source = {
				var in, out;
				in = \in.ar(0!encoderChannels);
				out = FoaDecode.ar(in, kernel);
				Out.ar(0, out);
				};
		}
		//check here for auto choosing correct decoder: chttp://www.ambisonictoolkit.net/Help/Guides/Intro-to-the-ATK.html
		{library =='ATK' && (type == 'newListen')} {
			//get the kernel
			kernel = FoaDecoderKernel.newListen(1013);
			Routine{
				2.5.wait; //take the time to load the kernel
				decoderProxy.source = {
				var in, out;
				in = \in.ar(0!encoderChannels);
				out = FoaDecode.ar(in, kernel);
				Out.ar(0, out);
				};
			}.play;
		};

		"A decoder was created through GameLoopDecoder".postln;

		// create the summing NodeProxy that will act as the summation bus
		// see http://new-supercollider-mailing-lists-forums-use-these.2681727.n2.nabble.com/Many-to-One-Audio-Routing-in-Jitlib-td7594874.html

		decoderBus = NodeProxy(Server.default, 'audio', encoderChannels);
		//route the summation bus to the decoder
		decoderBus <>> decoderProxy;

	}/*}}}*/

	*getEncoderProxy{/*{{{*/
		//return proxies with the channels needed for the given order
		^NodeProxy(Server.default, 'audio', encoderChannels);
	}/*}}}*/

	*getEncoder{/*{{{*/
		//return the right encoder class
		case 
		//binaural
		{library =='AmbIEM' && (type == 'binaural')} {
			if (dp == true,
				{^SpacePolarAmbIEMDp},
				{^SpacePolarAmbIEM}
			);
		}
		{library =='ATK' && (type == 'newListen')} {
			if (dp == true,
				{^SpacePolarATKDp},
				{^SpacePolarATK}
			);
		};
	}/*}}}*/

}
