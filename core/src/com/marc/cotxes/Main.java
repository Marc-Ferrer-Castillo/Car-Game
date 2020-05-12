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
	private static final float MINIMUM_SPEED = 0.5f;
	// When its true, the score is shown
	private static boolean game_Finished = false;
	// PLAYER MOVEMENT
	private final boolean MOVE_PLAYER_LEFT = false;
	private final boolean MOVE_PLAYER_RIGHT = true;
	// SCREEN DIMENSIONS
	static int SCREEN_WIDTH;
	static int SCREEN_HEIGHT;
	private int SCREEN_CENTER_Y, SCREEN_CENTER_X;



	// Game started?
	private static boolean game_started = false;
	// Start time and current time in ms
	private static long startTime;
	private static long currentTime;
	// Chronometer
	private BitmapFont chronoFont;
	// Score
	private BitmapFont score_Font;
	// Game Speed
	private static float speed = MINIMUM_SPEED;
	// Prevents from increasing the speed more than once in the same second
	private int incrementation_sec = 0;
	// Prevents from launching AI more than once in the same second
	private int launching_sec = 0;
	// Prevents from changing the light more than once in the same second
	private long changingTime = 0;
	// Player points to calculate the final score
	private static int points = 0;



	// SpriteBatch
	private SpriteBatch batch;
	// Sprites
	private static Sprite player_Sprite, instructions_Sprite, lights_Sprite;
	private static ArrayList<Sprite> road_Sprites;
	private ArrayList<Sprite> AI_Sprites;
	private static ArrayList<Sprite> heart_Sprites;
	// Textures
	private Texture dayRoad_Texture,  nighRoad_Texture, lights_Texture;
	private FreeTypeFontGenerator generator;



	// Runnables
	private RoadRunnable roadRunnable;
	private static PlayerRunnable playerRunnable;
	private boolean isDayTime = true;


	// Speed Getter
	static float getSpeed() {
		return speed;
	}
	// Player Getter
	static Sprite getPlayer_Sprite() {
		return player_Sprite;
	}


	@Override
	public void create () {
		// Screen size
		SCREEN_WIDTH = Gdx.graphics.getWidth();
		SCREEN_HEIGHT = Gdx.graphics.getHeight();
		SCREEN_CENTER_Y = SCREEN_HEIGHT / 2;
		SCREEN_CENTER_X = SCREEN_WIDTH / 2;

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
		createAI();
		// Creats hearts that represent players lives
		createHearts();
		// Creates the score text
		createScore();


		// Starts time thread
		new Thread(this).start();
	}
	// Creats hearts that represent players lives
	private static void createHearts() {
		// Texture
		Texture heart_Texture = new Texture("heart.png");
		heart_Sprites = new ArrayList<>();
		// Adds as many sprites as player lives
		for (int i = 0; i < PlayerRunnable.getLives() ; i++){
			Sprite heart = new Sprite(heart_Texture);
			heart.rotate90(true);
			heart.setSize(50, 50);
			heart_Sprites.add(heart);
		}
	}
	// Creates the AI cars
	private void createAI() {
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
			sprite.setY(SCREEN_HEIGHT);
			AI_Sprites.add(sprite);
		}
	}
	// Creates the chrono
	private void createChrono() {
		// BitmapFont
		chronoFont = new BitmapFont();
		// Parameters
		generator = new FreeTypeFontGenerator(Gdx.files.internal("pdark.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 55;
		parameter.shadowColor = Color.BLACK;
		parameter.shadowOffsetX = 3;
		parameter.shadowOffsetY = 3;
		chronoFont = generator.generateFont(parameter);
	}
	// Creates the score text
	private void createScore() {
		// Bitmapfont
		score_Font = new BitmapFont();
		// Parameters
		generator = new FreeTypeFontGenerator(Gdx.files.internal("pdark.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 100;
		parameter.shadowColor = Color.BLACK;
		parameter.shadowOffsetX = 6;
		parameter.shadowOffsetY = 6;
		score_Font = generator.generateFont(parameter);
	}
	// Creates the instructions
	private void createInstructions() {
		// Texture
		Texture instructions_Texture = new Texture("instrucciones.png");
		// Sprite
		instructions_Sprite = new Sprite(instructions_Texture);
		// Position
		instructions_Sprite.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
	}
	// Creates the player's car
	private void createPlayer() {
		// Textures
		Texture player_Texture = new Texture("player.png");
		lights_Texture = new Texture("lights.png");
		// Sprites
		player_Sprite = new Sprite(player_Texture);
		lights_Sprite = new Sprite(lights_Texture);
		// Initial positions
		player_Sprite.setPosition(SCREEN_CENTER_X, player_Sprite.getHeight() + (SCREEN_HEIGHT/20f));
		lights_Sprite.setPosition(SCREEN_CENTER_X - SCREEN_WIDTH / 17f, player_Sprite.getY());
		// Starts player movement
		playerRunnable = new PlayerRunnable(player_Sprite, lights_Sprite);
		new Thread(playerRunnable).start();
	}
	// Creates the roads
	private void createRoads() {
		// ArrayList of road_Sprites
		road_Sprites = new ArrayList<>();
		// Roads textures
		dayRoad_Texture = new Texture("background.jpg");
		nighRoad_Texture = new Texture("backgroundNight.jpg");

		// Road 1
		Sprite road1_Sprite = new Sprite(dayRoad_Texture);
		road1_Sprite.setSize(SCREEN_WIDTH, SCREEN_HEIGHT + SCREEN_HEIGHT / 5f);
		road_Sprites.add(road1_Sprite);
		// Road 2
		Sprite road2_Sprite = new Sprite(dayRoad_Texture);
		road2_Sprite.setSize(SCREEN_WIDTH, SCREEN_HEIGHT + SCREEN_HEIGHT / 5f);
		road_Sprites.add(road2_Sprite);

		// Starts roads movement
		roadRunnable = new RoadRunnable(road_Sprites.get(0), road_Sprites.get(1));
		new Thread(roadRunnable).start();
	}
	// Increases game speed over time
	private void manageSpeed() {
		// Current time in ms
		if (game_started){
			currentTime = (System.currentTimeMillis() - startTime) / 1000;
			// Speed increasing
			if (currentTime % 2 == 0){
				// Prevents from increasing the speed more than once per second
				if (incrementation_sec != currentTime){
					speed += 5 * Gdx.graphics.getDeltaTime();
					incrementation_sec = (int) currentTime;
				}
			}
		}
		else{
			// Starting time
			startTime = System.currentTimeMillis();
		}
	}





	@Override
	public void render () {
		batch.begin();

		// Renders
		renderRoads();
		renderPlayer();
		renderChrono();
		renderInstructionsOrAI();
		renderHearts();
		renderScore();

		batch.end();
	}

	private void renderRoads() {
		for (Sprite road: road_Sprites) {
			road.draw(batch);
		}
	}

	private void renderPlayer() {
		if (game_started) {
			player_Sprite.draw(batch);
			if (!isDayTime){
				lights_Sprite.draw(batch);
			}
		}
	}

	private void renderInstructionsOrAI() {
		// If game hasn't started
		if (!game_started & !game_Finished){
			// Renders instructions
			instructions_Sprite.draw(batch);
		}
		else{
			// Renders IA vehicles
			for (Sprite vehicle: AI_Sprites) {
				vehicle.draw(batch);
			}
		}
	}

	private void renderChrono() {
		if (game_started & !game_Finished){
			// Renders the chrono
			chronoFont.setColor(1,1,1,1);
			chronoFont.draw(batch, "" + currentTime, SCREEN_WIDTH /5f + SCREEN_WIDTH / 5f, SCREEN_HEIGHT / 20f);
		}
	}

	private void renderHearts() {
		if (game_started & !game_Finished) {
			int newPosition = (int) SCREEN_CENTER_X + SCREEN_WIDTH / 39;
			for (int i = 0; i < PlayerRunnable.getLives(); i++) {
				heart_Sprites.get(i).setPosition(newPosition, SCREEN_HEIGHT / 40f);
				heart_Sprites.get(i).draw(batch);
				newPosition += SCREEN_WIDTH / 30;
			}
		}
	}

	private void renderScore() {
		if (game_Finished){
			// Renders the score
			score_Font.setColor(1,1,1,1);
			score_Font.draw(batch, "SCORE\n" + points + currentTime, SCREEN_CENTER_X - SCREEN_WIDTH / 6f, SCREEN_CENTER_Y + SCREEN_HEIGHT / 10f );
		}
	}
	@Override
	public void dispose () {
		batch.dispose();
		dayRoad_Texture.dispose();
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
			launchAI();
			// Changes textures sometimes faking day and night
			changeLights();
		}
	}

	private void changeLights() {
		if (currentTime % 20 == 0 & changingTime != currentTime){

			for (Sprite road: road_Sprites) {
				// If its day
				if (road.getTexture().equals(dayRoad_Texture)){
					// Changes to night
					road.setTexture(nighRoad_Texture);
					isDayTime = false;
				}
				// If it's night
				else{
					// Changes to day
					road.setTexture(dayRoad_Texture);
					isDayTime = true;
				}
			}
			changingTime = currentTime;
		}
	}

	// Restarts game
	static void restart(){
		if(PlayerRunnable.getLives() == 0){
			// Shows score
			game_Finished = true;
			// Restarts
			game_started = false;
			playerRunnable.setPlayer_moving(false);
			speed = MINIMUM_SPEED;
			// Starts player movement
			playerRunnable = new PlayerRunnable(player_Sprite, lights_Sprite);
			playerRunnable.restartLives();
			PlayerRunnable.setPlayer_Alive(true);
			new Thread(playerRunnable).start();
		}
	}

	// Launches an AI with a random sprite from the arraylist
	private void launchAI() {
		if (game_started) {
			// Launching AI
			if (currentTime % 2 == 0 | currentTime % 3 == 0 | currentTime % 5 == 0 | currentTime % 7 == 0){
				// Prevents from launching AI more than once in the same second
				if (launching_sec != currentTime){
					// Random number to choose from the sprites array
					int random = new Random().nextInt(AI_Sprites.size());

					// Prevents spawning a sprite that is currently being used
					while(AI_Sprites.get(random).getY() < SCREEN_HEIGHT){
						random = new Random().nextInt(AI_Sprites.size());
					}
					// Starts a thread that moves the sprite
					new AIRunnable(AI_Sprites.get(random));
					// Prevents from launching multiple cars at the same time
					launching_sec = (int) currentTime;
					// Increments points for each car launched
					points++;
				}
			}
		}
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
		if (game_Finished){
			points = 0;
			game_Finished = false;
		}

		game_started = true;
		playerRunnable.setPlayer_moving(true);

		// Touching over the right half screen moves player to the right
		if (screenX >= SCREEN_CENTER_X){
			playerRunnable.setPlayer_moving_direction(MOVE_PLAYER_RIGHT);
		}
		else{
			playerRunnable.setPlayer_moving_direction(MOVE_PLAYER_LEFT);
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

