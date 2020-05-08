//
package com.marc.cotxes;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import java.util.ArrayList;
import java.util.Random;

public class Main extends ApplicationAdapter implements Runnable, InputProcessor {
	// PLAYER MOVEMENT
	private final boolean MOVE_PLAYER_LEFT = true;
	private final boolean MOVE_PLAYER_RIGHT = false;
	// SCREEN DIMENSIONS
	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;
	private int SCREEN_CENTER;



	// Game started?
	private static boolean game_started = false;
	// Start time and current time in ms
	private static long startTime;
	private static long currentTime;
	// Chronometer
	private BitmapFont chronoFont;
	// Game Speed
	private static float speed = 0.5f;
	// Prevents from increasing the speed more than once in the same second
	private int incrementation_sec = 0;
	// Prevents from launching AI more than once in the same second
	private int launching_sec = 0;



	// SpriteBatch
	private SpriteBatch batch;
	// Sprites
	private Sprite player_Sprite, instructions_Sprite;
	private static ArrayList<Sprite> road_Sprites;
	private ArrayList<Sprite> AI_Sprites;
	// Textures
	private Texture road_Texture;
	private FreeTypeFontGenerator generator;



	// Runnables
	private RoadRunnable roadRunnable;
	private PlayerRunnable playerRunnable;




	@Override
	public void create () {
		// Screen size
		SCREEN_WIDTH = Gdx.graphics.getWidth();
		SCREEN_HEIGHT = Gdx.graphics.getHeight();
		SCREEN_CENTER = SCREEN_HEIGHT / 2;

		// SpriteBatch
		batch = new SpriteBatch();

		// This class receives controller inputs
		Gdx.input.setInputProcessor(this);

		// Creates the roads
		createRoads();
		// Creates the chrono
		createChrono();
		// Creates the player's car
		createPlayer();
		// Creates the instructions
		createInstructions();
		// Creates the IA cars
		createIA();


		// Starts roads movement
		roadRunnable = new RoadRunnable(road_Sprites.get(0), road_Sprites.get(1));
		new Thread(roadRunnable).start();

		// Starts time thread
		new Thread(this).start();

		// Starts player movement
		playerRunnable = new PlayerRunnable(SCREEN_HEIGHT, player_Sprite);
		new Thread(playerRunnable).start();
	}


	// Creates the IA cars
	private void createIA() {
		// Vehicle textures
		ArrayList<Texture> AI_Textures = new ArrayList<>();
		AI_Textures.add(new Texture("Ambulance.png"));
		AI_Textures.add(new Texture("Audi.png"));
		AI_Textures.add(new Texture("Car.png"));
		AI_Textures.add(new Texture("Mini_truck.png"));
		AI_Textures.add(new Texture("taxi.png"));
		AI_Textures.add(new Texture("truck.png"));
		AI_Textures.add(new Texture("van.png"));

		// Vehicle Sprites
		AI_Sprites = new ArrayList<>();
		for (Texture texture: AI_Textures) {
			Sprite sprite = new Sprite(texture);
			sprite.setPosition(-sprite.getWidth(), SCREEN_CENTER);
			AI_Sprites.add(sprite);
		}
	}
	// Creates the chrono
	private void createChrono() {
		// Cronometro
		chronoFont = new BitmapFont();
		// Fuente de texto
		generator = new FreeTypeFontGenerator(Gdx.files.internal("pdark.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 60;
		parameter.shadowColor = Color.BLACK;
		parameter.shadowOffsetX = 3;
		parameter.shadowOffsetY = 3;
		chronoFont = generator.generateFont(parameter);
	}
	// Creates the instructions
	private void createInstructions() {
		// Textura con la imagen
		Texture instructions_Texture = new Texture("instrucciones.png");
		// Sprite con la textura del coche del jugador
		instructions_Sprite = new Sprite(instructions_Texture);
		// Posición
		instructions_Sprite.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
	}
	// Creates the player's car
	private void createPlayer() {
		// Textura con la imagen del coche del jugador
		Texture player_Texture = new Texture("player.png");
		// Sprite con la textura del coche del jugador
		player_Sprite = new Sprite(player_Texture);
		// Posición inicial del jugador
		player_Sprite.setPosition(SCREEN_WIDTH - player_Sprite.getWidth(), SCREEN_CENTER);
	}
	// Creates the roads
	private void createRoads() {
		// ArrayList of road_Sprites
		road_Sprites = new ArrayList<>();
		// Roads texture
		road_Texture = new Texture("bg.jpg");
		// Road 1
		Sprite road1_Sprite = new Sprite(road_Texture);
		road1_Sprite.setSize(SCREEN_WIDTH + 10, SCREEN_HEIGHT);
		road_Sprites.add(road1_Sprite);
		// Road 2
		Sprite road2_Sprite = new Sprite(road_Texture);
		road2_Sprite.setSize(SCREEN_WIDTH + 10, SCREEN_HEIGHT);
		road_Sprites.add(road2_Sprite);
	}
	// Increases game speed over time
	private void manageSpeed() {
		// Current time in ms
		if (game_started){
			currentTime = (System.currentTimeMillis() - startTime) / 1000;
		}
		else{
			// Starting time
			startTime = System.currentTimeMillis();
		}

		// Speed increasing
		if (currentTime % 10 == 0){
			// Prevents from increasing the speed more than once per second
			if (incrementation_sec != currentTime){
				speed += 0.1;
				incrementation_sec = (int) currentTime;
			}
		}
	}

	// Speed Getter
	public static float getSpeed() {
		return speed;
	}

	@Override
	public void render () {
		batch.begin();

		// Renders roads
		for (Sprite road: road_Sprites) {
			road.draw(batch);
		}

		// Renders player
		player_Sprite.draw(batch);

		// If game hasn't started
		if (!game_started){
			// Renders instructions
			instructions_Sprite.draw(batch);
		}
		else{
			// Renders IA vehicles
			for (Sprite vehicle: AI_Sprites) {
				vehicle.draw(batch);
			}
		}

		// Renders the chrono
		chronoFont.setColor(1,1,1,1);
		chronoFont.draw(batch, "" + currentTime, SCREEN_WIDTH / 2f - 30, SCREEN_HEIGHT - 75);

		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
		road_Texture.dispose();
		generator.dispose();
	}

	@Override
	public void run() {
		while(true){
			// Updates speed over time
			manageSpeed();
			// Updates road speed
			roadRunnable.setSpeed(speed);
			// AI launcher
			if (game_started) {
				// Launching AI
				if (currentTime % 2 == 0 | currentTime % 3 == 0){
					// Prevents from launching AI more than once in the same second
					if (launching_sec != currentTime){
						launchAI();
						launching_sec = (int) currentTime;
					}
				}
			}
		}
	}
	// Launches an AI with a random sprite from the arraylist
	private void launchAI() {
		int random = new Random().nextInt(AI_Sprites.size());

		// Prevents spawning a sprite that is currently being used
		while(AI_Sprites.get(random).getX() > 0){
			random = new Random().nextInt(AI_Sprites.size());
		}

		new AIRunnable(AI_Sprites.get(random));
	}

	@Override
	public boolean keyDown(int keycode) {

		return true;
	}

	@Override
	public boolean keyUp(int keycode) {

		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		game_started = true;
		playerRunnable.setPlayer_moving(true);

		// Touching over upper half screen moves player to the right
		if (screenY >= SCREEN_CENTER){
			playerRunnable.setPlayer_moving_direction(MOVE_PLAYER_LEFT);
		}
		else{
			playerRunnable.setPlayer_moving_direction(MOVE_PLAYER_RIGHT);
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		playerRunnable.setPlayer_moving(false);
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}

