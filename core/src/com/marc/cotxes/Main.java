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
	private final boolean MOVE_PLAYER_LEFT = true;
	private final boolean MOVE_PLAYER_RIGHT = false;
	// SCREEN DIMENSIONS
	static int SCREEN_WIDTH;
	static int SCREEN_HEIGHT;
	private int SCREEN_CENTER;



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
	// Player points to calculate the final score
	private static int points = 0;



	// SpriteBatch
	private SpriteBatch batch;
	// Sprites
	private static Sprite player_Sprite, instructions_Sprite;
	private static ArrayList<Sprite> road_Sprites;
	private ArrayList<Sprite> AI_Sprites;
	private static ArrayList<Sprite> heart_Sprites;
	// Textures
	private Texture road_Texture;
	private FreeTypeFontGenerator generator;



	// Runnables
	private RoadRunnable roadRunnable;
	private static PlayerRunnable playerRunnable;



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
			heart.setSize(50, 50);
			heart_Sprites.add(heart);
		}
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
		// BitmapFont
		chronoFont = new BitmapFont();
		// Parameters
		generator = new FreeTypeFontGenerator(Gdx.files.internal("pdark.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 40;
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
		parameter.size = 80;
		parameter.shadowColor = Color.BLACK;
		parameter.shadowOffsetX = 3;
		parameter.shadowOffsetY = 3;
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
		// Texture
		Texture player_Texture = new Texture("player.png");
		// Sprite
		player_Sprite = new Sprite(player_Texture);
		// Initial position
		player_Sprite.setPosition(SCREEN_WIDTH - player_Sprite.getWidth() - (SCREEN_WIDTH/11f), SCREEN_CENTER);
		// Starts player movement
		playerRunnable = new PlayerRunnable(SCREEN_HEIGHT, player_Sprite);
		new Thread(playerRunnable).start();
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
			if (currentTime % 10 == 0){
				// Prevents from increasing the speed more than once per second
				if (incrementation_sec != currentTime){
					speed += 0.1;
					incrementation_sec = (int) currentTime;
				}
			}
		}
		else{
			// Starting time
			startTime = System.currentTimeMillis();
		}
	}

	// Speed Getter
	static float getSpeed() {
		return speed;
	}

	// Player Getter
	static Sprite getPlayer_Sprite() {
		return player_Sprite;
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
			chronoFont.draw(batch, "" + currentTime, player_Sprite.getX() + player_Sprite.getWidth() / 2, player_Sprite.getY() + player_Sprite.getHeight() + 30);
		}
	}

	private void renderHearts() {
		int newPosition = 100;
		for (int i = 0; i < PlayerRunnable.getLives() ; i++) {
			heart_Sprites.get(i).setPosition(SCREEN_WIDTH -100, SCREEN_CENTER + newPosition);
			heart_Sprites.get(i).draw(batch);
			newPosition += 50;
		}
	}

	private void renderScore() {
		if (game_Finished){
			// Renders the score
			score_Font.setColor(1,1,1,1);
			score_Font.draw(batch, "YOUR SCORE IS\n" + currentTime * points, SCREEN_CENTER, SCREEN_HEIGHT / 2f + SCREEN_HEIGHT / 15f);
		}
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
			launchAI();
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
			playerRunnable = new PlayerRunnable(SCREEN_HEIGHT, player_Sprite);
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
					while(AI_Sprites.get(random).getX() > 0){
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

