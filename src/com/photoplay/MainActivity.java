package com.photoplay;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Vector2;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.RepeatingSpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.util.Log;
import android.widget.Toast;

public class MainActivity extends SimpleBaseGameActivity {


	private static final int CAMERA_WIDTH = 1280; //720 is default
	private static final int CAMERA_HEIGHT = 720; //480 is default (default aspect ratio dus niet 16:9)

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private ITextureRegion mBoxTextureRegion;
	private RepeatingSpriteBackground mBackground;
	private Grid grid;
	private Score Score;
	private boolean moved;
	@Override
	public EngineOptions onCreateEngineOptions() {
		Toast.makeText(this, "Touch & Drag the boxes!", Toast.LENGTH_LONG).show();

		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 80, 80, TextureOptions.BILINEAR);
		this.mBoxTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "box.png", 0, 0);
		this.mBackground = new RepeatingSpriteBackground(CAMERA_WIDTH, CAMERA_HEIGHT, this.getTextureManager(), AssetBitmapTextureAtlasSource.create(this.getAssets(), "gfx/cats.jpg"), this.getVertexBufferObjectManager());
		this.mBitmapTextureAtlas.load();
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene();
		scene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		int s= 1000;
		this.Score = new Score(s);
		Log.v("Score gemaakt!", "Punten = "+Score.getScore());
		moved = false;
		grid = new Grid(CAMERA_WIDTH, CAMERA_HEIGHT, this.mBoxTextureRegion.getWidth(), this.mBoxTextureRegion.getHeight());
		while (!grid.isFinished()) {
			drawBoxSprite(scene,grid.currentPosition);
			grid.UpdatePosition();
		}
		
		
		scene.setTouchAreaBindingOnActionDownEnabled(true);
		scene.setBackground(this.mBackground);
                  
        scene.registerUpdateHandler(new IUpdateHandler() {                    
            public void reset() {        
            }             
            public void onUpdate(float pSecondsElapsed) {
                //HERE IS THE GAME LOOP
            	scene.sortChildren(); //update zindex of sprites
//				 for (int i = 0; i < scene.getChildCount(); i++) {
//					 
//				 }
            	
            	/*TODO : 	draw/update score
            	       		(hoeft niet hier maar iig moet hier de text op de voorgrond gezet worden elke frame)
            	       		(setzindex en dan sortchildren) */
            }
        });
		return scene;
	}
	
	public void drawBoxSprite (Scene scene, Vector2 v) { //nieuwe class maken
		final Sprite box = new Sprite(v.x, v.y, this.mBoxTextureRegion, this.getVertexBufferObjectManager()) {
			float x = -1, y = -1; 
			
			@Override
			public boolean onAreaTouched(final TouchEvent touchEvent, float touchAreaLocalX, float touchAreaLocalY) {
				
			    switch(touchEvent.getAction()){ //TODO : na aanmaken class: alle cases zijn methods, dubbele code bv score is apparte method
			                case TouchEvent.ACTION_MOVE:{
			    				Vector2 snap = grid.getNearestSnapPoint(new Vector2(touchEvent.getX(),touchEvent.getY()));
			    				if (grid.snapPointIsAvailible(snap)){
				    				this.setPosition(snap.x, snap.y);
				    				this.setColor(0.0f, 0.7f, 0.0f, 0.5f);
				    				this.setZIndex(999999);
				                	Log.v("tag", "Action_Move");
				                	
			    				}
			                    return true;
			                }
			                case TouchEvent.ACTION_DOWN:{
			                    // TODO: score minus 50
			                	// TODO: sla systeemtijd op voor timer in action up
			                	x = this.getX();
			                	y = this.getY();
			                	grid.removeUnavailibleSnappoint(new Vector2(x,y));
			                	Log.v("tag", "Action_Down");
			                	if(Score.getScore() < 0){
			                		Log.v("Score op 0?", "Punten = "+Score.getScore());
			                		return false;
			                	}else if(moved=true){
			                		Score.draggedMove(Score.getScore());
			                		moved =false;
			                		Log.v("Score eraf?", "draggedMove Punten = "+Score.getScore());
			                		return true;
			                	}
			                	else{
			                		Score.normalMove(Score.getScore());
			                		Log.v("Score eraf?", "normalMove Punten = "+Score.getScore());
			                		return true; 
			                	}
			                	
			                }
			                case TouchEvent.ACTION_UP:{
			                	if (this.getX() == x && this.getY() == y) { /*TODO : 	timer in de vorm van or (||) statement erbij zetten 
			                															(heel simpel, als verschil in systeemtijd van action down 
			                															bv slechts 100ms is dan telt het ook als zelfde positie,
			                															dus delete, dus fix tegen het random verspringen ipv 
			                															deleten van boxes bij tappen) */                       
			                		//Current position is same as start position
			                		//detach sprite
				                	Log.v("tag", "Action_UP");
			                		this.detachSelf();
			                		//TODO : score minus 50 (so total will be minus 100)
			                		//TODO : particles
			                		
			                		System.out.println("current position is same as starting position");
			                	}
			                	grid.addUnavailibleSnappoint(new Vector2(this.getX(),this.getY()));
			                	this.setColor(1.0f, 0.0f, 0.0f, 1f);
			                    return true; 
			                }
			                case TouchEvent.ACTION_CANCEL:{
			                	
			                	this.setColor(1.0f, 0.0f, 0.0f, 1f);
			                	Log.v("tag", "Action_Cancel");
			                    return true; 
			                }
			                default:{
			                    // none of the above
			                    return false;
			                }
			    }
			}
		};
		scene.attachChild(box);
		scene.registerTouchArea(box);
	}
}
