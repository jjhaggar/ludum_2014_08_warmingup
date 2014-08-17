package com.mygdx.ludum;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.Input.Keys;

public class LudumGame extends ApplicationAdapter {

	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private Texture img;
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	private AssetManager assetManager;
	private float GRAVITY = -2.8f;
	private Animation stand;
	private Animation walk;
	private Animation jump;
	private Texture rayaTexture;
	private TextureAtlas atlas;
	private String TEXTURE_ATLAS_OBJECTS = "rayaman.pack";
	private Array<Rectangle> tiles = new Array<Rectangle>();
	Rectangle rayaRect;

	// Botones del mando / Gamepad Buttons
	private boolean leftPressed = false;
	private boolean rightPressed = false;
	private boolean jumpPressed = false;



	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject () {
		return new Rectangle();
		}
		};


	private RayaMan raya;

	static class RayaMan {

		static float WIDTH;
		static float HEIGHT;
		static float MAX_VELOCITY = 100f;
		static float JUMP_VELOCITY = 100f;
		static float DAMPING = 0.87f;
		enum State {
			Standing, Walking, Jumping
		}
		final Vector2 position = new Vector2();
		final Vector2 velocity = new Vector2();
		State state = State.Walking;
		float stateTime = 0;
		boolean facesRight = true;
		boolean grounded = false;

		}

	@Override
	public void create () {

		assetManager = new AssetManager();
		shapeRenderer = new ShapeRenderer();

		createAnimations();

		map = new TmxMapLoader().load("prueba.tmx");

		renderer = new OrthogonalTiledMapRenderer(map, 1);

		Gdx.graphics.setDisplayMode(400, 240, false);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 400, 240);
		camera.update();


		raya = new RayaMan();
		raya.position.set(0,130);



		// CODIGO DE PRUEBAS PARA LOS MANDOS / GAMEPAD TESTING CODE

		// print the currently connected controllers to the console
		System.out.println("Controllers: " + Controllers.getControllers().size);
		int i = 0;
		for (Controller controller : Controllers.getControllers()) {
			System.out.println("#" + i++ + ": " + controller.getName());
		}
		if (Controllers.getControllers().size == 0) System.out.println("No controllers attached");

		// setup the listener that prints events to the console
		Controllers.addListener(new ControllerListener() {
		public int indexOf (Controller controller) {
		return Controllers.getControllers().indexOf(controller, true);
		}
		@Override
		public void connected (Controller controller) {
			System.out.println("connected " + controller.getName());
		int i = 0;
		for (Controller c : Controllers.getControllers()) {
			System.out.println("#" + i++ + ": " + c.getName());
		}
		}
		@Override
		public void disconnected (Controller controller) {
			System.out.println("disconnected " + controller.getName());
		int i = 0;
		for (Controller c : Controllers.getControllers()) {
			System.out.println("#" + i++ + ": " + c.getName());
		}
		if (Controllers.getControllers().size == 0) System.out.println("No controllers attached");
		}
		@Override
		public boolean buttonDown (Controller controller, int buttonIndex) {
			System.out.println("#" + indexOf(controller) + ", button " + buttonIndex + " down");
			if (buttonIndex == 0){
				jumpPressed = true;
			}
		return false;
		}
		@Override
		public boolean buttonUp (Controller controller, int buttonIndex) {
			System.out.println("#" + indexOf(controller) + ", button " + buttonIndex + " up");
			if (buttonIndex == 0){
				jumpPressed = false;
			}
		return false;
		}
		@Override
		public boolean axisMoved (Controller controller, int axisIndex, float value) {
			System.out.println("#" + indexOf(controller) + ", axis " + axisIndex + ": " + value);
		return false;
		}
		@Override
		public boolean povMoved (Controller controller, int povIndex, PovDirection value) {
			System.out.println("#" + indexOf(controller) + ", pov " + povIndex + ": " + value);
			System.out.println(value);

			if (value.equals("west") || value == PovDirection.west){
				rightPressed = false;
				leftPressed = true;
			}
			else if (value.equals(PovDirection.east)){
				rightPressed = true;
				leftPressed = false;
			}
			else if (value.equals(PovDirection.center)){
				rightPressed = false;
				leftPressed = false;
			}
			else{
				System.out.println("else!!");
			}

		return false;
		}
		@Override
		public boolean xSliderMoved (Controller controller, int sliderIndex, boolean value) {
			System.out.println("#" + indexOf(controller) + ", x slider " + sliderIndex + ": " + value);
		return false;
		}
		@Override
		public boolean ySliderMoved (Controller controller, int sliderIndex, boolean value) {
			System.out.println("#" + indexOf(controller) + ", y slider " + sliderIndex + ": " + value);
		return false;
		}
		@Override
		public boolean accelerometerMoved (Controller controller, int accelerometerIndex, Vector3 value) {
		// not printing this as we get to many values
		return false;
		}
		});
	}

	private void createAnimations() {
		assetManager.load(TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);
		assetManager.finishLoading();

		atlas = assetManager.get(TEXTURE_ATLAS_OBJECTS);
		Array<AtlasRegion> regions;

		regions = atlas.findRegions("rayaman_walking");
		walk = new Animation(0.15f, regions);
		walk.setPlayMode(Animation.PlayMode.LOOP);

		regions = atlas.findRegions("rayaman_standing");
		stand = new Animation(0, regions);
		jump = new Animation(0, regions);

		RayaMan.WIDTH = 16f;
		RayaMan.HEIGHT = 21f;

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		float deltaTime = Gdx.graphics.getDeltaTime();

		updateRaya(deltaTime);

		camera.position.x = 200;//raya.position.x;
		camera.update();

		renderer.setView(camera);
		renderer.render();

		renderRayaMan(deltaTime);
	}

	private void renderRayaMan (float deltaTime) {
		// based on the koala state, get the animation frame
		TextureRegion frame = null;
		switch (raya.state) {
		case Standing:
			frame = stand.getKeyFrame(raya.stateTime);
			break;
		case Walking:
			frame = walk.getKeyFrame(raya.stateTime);
			break;
		case Jumping:
			frame = jump.getKeyFrame(raya.stateTime);
			break;
		}
		// draw the koala, depending on the current velocity
		// on the x-axis, draw the koala facing either right
		// or left
		Batch batch = renderer.getSpriteBatch();
		batch.begin();
		if (raya.facesRight) {
			if (frame.isFlipX())
				frame.flip(true, false);
			batch.draw(frame, raya.position.x, raya.position.y);
		} else {
			if (!frame.isFlipX())
				frame.flip(true, false);
			batch.draw(frame, raya.position.x, raya.position.y);
		}

		batch.end();
		shapeRenderer.begin(ShapeType.Filled);

		shapeRenderer.setColor(Color.BLACK);

		getTiles(0, 0, 25, 15, tiles);
		for (Rectangle tile : tiles) {
			shapeRenderer.rect(tile.x * 2, tile.y * 2, tile.width * 2, tile.height * 2);
		}
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.rect(rayaRect.x * 2, rayaRect.y * 2, rayaRect.width * 2, rayaRect.height * 2);

        shapeRenderer.end();
		}

	private void updateRaya(float deltaTime) {

		if (deltaTime == 0)
			return;
		raya.stateTime += deltaTime;

		if ((Gdx.input.isKeyPressed(Keys.S) || jumpPressed) && raya.grounded){
			moveJump();
		}

		if (Gdx.input.isKeyPressed(Keys.LEFT) || leftPressed){
			moveLeft();
		}

		if (Gdx.input.isKeyPressed(Keys.RIGHT) || rightPressed){
			moveRight();
		}

		raya.velocity.add(0, GRAVITY);

		// clamp the velocity to the maximum, x-axis only
//		if (Math.abs(koala.velocity.x) > Koala.MAX_VELOCITY) {
//		koala.velocity.x = Math.signum(koala.velocity.x) * Koala.MAX_VELOCITY;
//		}

		// clamp the velocity to 0 if it's < 1, and set the state to standign
		if (Math.abs(raya.velocity.x) < 1) {
			raya.velocity.x = 0;
			if (raya.grounded)
				raya.state = RayaMan.State.Standing;
		}

		raya.velocity.scl(deltaTime);

		//collision detection
		// perform collision detection & response, on each axis, separately
		// if the raya is moving right, check the tiles to the right of it's
		// right bounding box edge, otherwise check the ones to the left
		rayaRect = rectPool.obtain();

		rayaRect.set(raya.position.x, raya.position.y, RayaMan.WIDTH, RayaMan.HEIGHT);

		int startX, startY, endX, endY;

		if (raya.velocity.x > 0) {
			startX = endX = (int)((raya.position.x + raya.velocity.x + RayaMan.WIDTH) / 16);
		}
		else {
			startX = endX = (int)((raya.position.x + raya.velocity.x) / 16);
		}
		startY = (int)((240 - (raya.position.y)) / 16);
		endY = (int)((240 - raya.position.y + RayaMan.HEIGHT) / 16);

		getTiles(startX, startY, endX, endY, tiles);

		rayaRect.x += raya.velocity.x;

		for (Rectangle tile : tiles) {
			if (rayaRect.overlaps(tile)) {
				raya.velocity.x = 0;
				break;
				}
		}

		rayaRect.x = raya.position.x;

		// if the koala is moving upwards, check the tiles to the top of it's
		// top bounding box edge, otherwise check the ones to the bottom

		if (raya.velocity.y > 0) {
			startY = endY = (int)((240 - (raya.position.y + raya.velocity.y + RayaMan.HEIGHT)) / 16);
		}
		else {
			startY = endY = (int)((240 - (raya.position.y + raya.velocity.y)) / 16);
		}

		startX = (int)(raya.position.x / 16);					//16 tile size
		endX = (int)((raya.position.x + RayaMan.WIDTH) / 16);

		getTiles(startX, startY, endX, endY, tiles);

		rayaRect.y -= (int)(raya.velocity.y);

		for (Rectangle tile : tiles) {
			if (rayaRect.overlaps(tile)) {
				// we actually reset the koala y-position here
				// so it is just below/above the tile we collided with
				// this removes bouncing :)
				if (raya.velocity.y > 0) {
					raya.position.y = tile.y;
					// we hit a block jumping upwards, let's destroy it!
					}
				else {
					raya.position.y = tile.y;
					// if we hit the ground, mark us as grounded so we can jump
					raya.grounded = true;
					}
				raya.velocity.y = 0;
				break;
				}
			}
		rectPool.free(rayaRect);

		// unscale the velocity by the inverse delta time and set
		// the latest position
		raya.position.add(raya.velocity);
		raya.velocity.scl(1 / deltaTime);
		// Apply damping to the velocity on the x-axis so we don't
		// walk infinitely once a key was pressed
		raya.velocity.x *= RayaMan.DAMPING;
	}

	private void getTiles (int startX, int startY, int endX, int endY, Array<Rectangle> tiles) {

		TiledMapTileLayer layer = (TiledMapTileLayer)(map.getLayers().get(1));
		rectPool.freeAll(tiles);
		tiles.clear();
		for (int y = startY; y <= endY; y++) {
			for (int x = startX; x <= endX; x++) {
				Cell cell = layer.getCell(x, y);
				if (cell != null) {
					Rectangle rect = rectPool.obtain();
					rect.set(x * 16, y  * 16, 16, 16);
					tiles.add(rect);
					}
				}
			}
		}

	private void moveLeft(){
		raya.velocity.x = -RayaMan.MAX_VELOCITY;
		if (raya.grounded) raya.state = RayaMan.State.Walking;
		raya.facesRight = false;
	}

	private void moveRight(){
		raya.velocity.x = RayaMan.MAX_VELOCITY;
		if (raya.grounded) raya.state = RayaMan.State.Walking;
		raya.facesRight = true;
	}

	private void moveJump(){
		raya.velocity.y += RayaMan.JUMP_VELOCITY;
		raya.grounded = false;
		raya.state = RayaMan.State.Jumping;
	}
}
