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

public class Principal extends ApplicationAdapter implements Runnable, InputProcessor {
	// PLAYER MOVEMENT
	private final boolean MOVE_PLAYER_LEFT = true;
	private final boolean MOVE_PLAYER_RIGHT = false;
	// ROAD LIMITS THAT PLAYER CAN'T SURPASS
	private final int RIGHT_LIMIT = 390;
	private final int LEFT_LIMIT = 150;
	// SCREEN DIMENSIONS
	private static int SCREEN_WIDTH;
	private int SCREEN_HEIGHT;
	private int SCREEN_CENTER;



	// Game started?
	private static boolean game_started = false;
	// Player moving?
	private boolean player_moving = false;
	// Player moving left or right
	private boolean player_moving_direction;
	// Start time and current time in ms
	private static long startTime;
	private static long currentTime;
	// Chronometer
	private BitmapFont chronoFont;



	// SpriteBatch
	SpriteBatch batch;
	// Sprites
	private Sprite player_Sprite, instructions_Sprite;
	private static ArrayList<Sprite> road_Sprites;
	private ArrayList<Sprite> IA_Sprites;
	// Textures
	private Texture road_Texture, player_Texture, instructions_Texture;
	private ArrayList<Texture> IA_Textures;
	private FreeTypeFontGenerator generator;

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
		RoadRunnable roadRunnable = new RoadRunnable();
		new Thread(roadRunnable).start();

		// Starts player movement
		PlayerRunnable playerRunnable = new PlayerRunnable();
		new Thread(playerRunnable).start();
	}


	// Creates the IA cars
	private void createIA() {
		// Vehicle textures
		IA_Textures = new ArrayList<>();
		IA_Textures.add(new Texture("Ambulance.png"));
		IA_Textures.add(new Texture("Audi.png"));
		IA_Textures.add(new Texture("Car.png"));
		IA_Textures.add(new Texture("Mini_truck.png"));
		IA_Textures.add(new Texture("taxi.png"));
		IA_Textures.add(new Texture("truck.png"));
		IA_Textures.add(new Texture("van.png"));

		// Vehicle Sprites
		IA_Sprites = new ArrayList<>();
		for (Texture texture: IA_Textures) {
			Sprite sprite = new Sprite(texture);
			sprite.setPosition(-SCREEN_WIDTH, SCREEN_CENTER);
			IA_Sprites.add(sprite);
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
		instructions_Texture = new Texture("instrucciones.png");
		// Sprite con la textura del coche del jugador
		instructions_Sprite = new Sprite(instructions_Texture);
		// Posición
		instructions_Sprite.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
	}
	// Creates the player's car
	private void createPlayer() {
		// Textura con la imagen del coche del jugador
		player_Texture = new Texture("player.png");
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
		road1_Sprite.setSize(SCREEN_WIDTH + 5, SCREEN_HEIGHT);
		road_Sprites.add(road1_Sprite);
		// Road 2
		Sprite road2_Sprite = new Sprite(road_Texture);
		road2_Sprite.setSize(SCREEN_WIDTH + 5, SCREEN_HEIGHT);
		road_Sprites.add(road2_Sprite);
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
			for (Sprite vehicle: IA_Sprites) {
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
		/*// The two roads
		Sprite road1 = road_Sprites.get(0);
		Sprite road2 = road_Sprites.get(1);

		// Speed
		float speed = 1;
		// Prevents from increasing the speed more than once in the same second
		int puntoDeIncremento = 0;

		// Second road position
		road2.setX(-SCREEN_WIDTH );


		float posicionC1 = 0, posicionC2 = 0;

		while (true){
			// Tiempo actual en segundos
			if (game_started){
				currentTime = (System.currentTimeMillis() - startTime) / 1000;
			}
			else{
				// Tiempo inicial en ms
				startTime = System.currentTimeMillis();
			}


			// Incrementa la velocidad
			if (currentTime % 5 == 0){
				// Evita incrementar la velocidad mas de una vez en el mismo segundo
				if (puntoDeIncremento != currentTime){
					speed += 0.1;
					puntoDeIncremento = (int) currentTime;
				}
			}


			// Si la primera carretera llega al final, vuelve al principio
			if (road1.getX() >= SCREEN_WIDTH){
				posicionC1 = -SCREEN_WIDTH;
			}else{
				// Avanza la carretera
				posicionC1 = road1.getX() + speed;
			}
			road1.setX(posicionC1);

			// Si la segunda carretera llega al final, vuelve al principio
			if (road2.getX() >= SCREEN_WIDTH){
				posicionC2 = -SCREEN_WIDTH;
			}else{
				// Avanza la carretera
				posicionC2 = road2.getX() + speed;
			}
			road2.setX(posicionC2);

			// Retardo
			try{
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		game_started = true;
		player_moving = true;

		// Touching over upper half screen moves player to the right
		if (screenY >= SCREEN_CENTER){
			player_moving_direction = MOVE_PLAYER_LEFT;
		}
		else{
			player_moving_direction = MOVE_PLAYER_RIGHT;
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		player_moving = false;

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

	// Runnable that moves the player to left or right
	// Also limits the player max and min position on the Y axis
	private class PlayerRunnable implements Runnable {

		public void run() {
			while (true){
				if (player_moving){
					// Movimiento hacia la izquierda
					if (player_moving_direction == MOVE_PLAYER_LEFT){
						player_Sprite.setY(player_Sprite.getY() - 1);
					}
					// Movimiento hacia la derecha
					else if(player_moving_direction == MOVE_PLAYER_RIGHT){
						player_Sprite.setY(player_Sprite.getY() + 1);
					}
				}
				// Si el jugador rebasa por el lado derecho
				if (player_Sprite.getY() > SCREEN_HEIGHT - RIGHT_LIMIT){
					player_Sprite.setY(player_Sprite.getY() - 1);
				}
				// Si el jugador rebasa por el lado izquierdo
				if ( player_Sprite.getY() < LEFT_LIMIT){
					player_Sprite.setY(player_Sprite.getY() + 1);
				}
				// Retardo
				try{
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static class RoadRunnable implements Runnable {
		// Speed
		float speed = 1;
		// Prevents increasing the speed more than once in the same second
		int incrementation_sec = 0;
		// Roads positions
		float position = 0, backPosition = -SCREEN_WIDTH - 2;
		// The two roads
		Sprite road1 = road_Sprites.get(0);
		Sprite road2 = road_Sprites.get(1);


		public void run(){
			road2.setX(backPosition);

			while (true){
				manageSpeed();
				moveRoad(road1);
				moveRoad(road2);
			}
		}

		private void moveRoad(Sprite road) {
			// Si la primera carretera llega al final, vuelve al principio
			if (road.getX() >= SCREEN_WIDTH + 2){
				position = backPosition;
			}else{
				// Avanza la carretera
				position = road.getX() + speed;
			}
			road.setX(position);

			// Retardo
			try{
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		private void manageSpeed() {
			// Tiempo actual en segundos
			if (game_started){
				currentTime = (System.currentTimeMillis() - startTime) / 1000;
			}
			else{
				// Tiempo inicial en ms
				startTime = System.currentTimeMillis();
			}

			// Incrementa la velocidad
			if (currentTime % 5 == 0){
				// Evita incrementar la velocidad mas de una vez en el mismo segundo
				if (incrementation_sec != currentTime){
					speed += 0.01 * Gdx.graphics.getDeltaTime();
					incrementation_sec = (int) currentTime;
				}
			}
		}
	}
}

