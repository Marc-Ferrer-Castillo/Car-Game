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
	// Movimiento del jugador a izquierda o derecha
	private final boolean MOVE_PLAYER_LEFT = true;
	private final boolean MOVE_PLAYER_RIGHT = false;
	// Movimiento del jugador
	private boolean player_moving = false;
	// Dirección de movimiento
	private boolean player_moving_direction;
	// Límites de la carretera
	private final int LIMITE_DERECHO_CARRETERA = 390;
	private final int LIMITE_IZQUIERDO_CARRETERA = 150;
	// Dimensions pantalla
	private int SCREEN_WIDTH;
	private int SCREEN_HEIGHT;
	// Tiempo inicial
	long startTime, currentTime;
	// Cronometro
	private BitmapFont chronoFont;
	// Contiene trozos de carretera
	private ArrayList<Sprite> carreteras;



	SpriteBatch batch;
	Sprite fondo;
	Texture trozoCarretera;
	// textura del coche del jugador
	Texture jugadorTexture;
	// Sprite con la textura del coche del jugador
	Sprite player_Sprite;
	FreeTypeFontGenerator generator;

	@Override
	public void create () {
		// GET Dimensions pantalla
		SCREEN_WIDTH = Gdx.graphics.getWidth();
		SCREEN_HEIGHT = Gdx.graphics.getHeight();

		// Tiempo inicial en ms
		startTime = System.currentTimeMillis();
		// Cronometro
		chronoFont = new BitmapFont();


		// Fuente de texto
		generator = new FreeTypeFontGenerator(Gdx.files.internal("pdark.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 60;
		parameter.shadowColor = Color.BLACK;
		parameter.shadowOffsetX = 3;
		parameter.shadowOffsetY = 3;
		chronoFont = generator.generateFont(parameter); // font size 12 pixels

		// Batch
		batch = new SpriteBatch();

		// Aquesta clase rep els events d'entrada de teclat
		Gdx.input.setInputProcessor(this);

		// Crea las carreteras del juego
		crearCarreteras();

		// Textura con la imagen del coche del jugador
		jugadorTexture = new Texture("player.png");
		// Sprite con la textura del coche del jugador
		player_Sprite = new Sprite(jugadorTexture);
		// Posición inicial del jugador
		player_Sprite.setPosition(SCREEN_WIDTH - player_Sprite.getWidth(), SCREEN_HEIGHT / 2);




		// Inicia el thread para mover las carreteras
		Thread t = new Thread(this);
		t.start();

		// Runnable para mover al jugador
		PlayerRunnable playerRunnable = new PlayerRunnable();
		new Thread(playerRunnable).start();
	}

	// Crea las carreteras del juego
	private void crearCarreteras() {
		// ArrayList de carreteras
		carreteras = new ArrayList<>();
		// Carretera 1
		trozoCarretera = new Texture("bg.jpg");
		Sprite carreteraSprite = new Sprite(trozoCarretera);
		carreteraSprite.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		carreteras.add(carreteraSprite);
		// Carretera 2
		Sprite carreteraSprite2 = new Sprite(trozoCarretera);
		carreteraSprite2.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		carreteras.add(carreteraSprite2);
		// Carretera de fondo
		fondo = new Sprite(trozoCarretera);
		fondo.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
	}

	@Override
	public void render () {


		batch.begin();



		// Dibuja el fondo
		fondo.draw(batch);
		// Pinta trozos de carretera
		for (Sprite carretera: carreteras) {
			carretera.draw(batch);
		}

		// Dibuja el coche del jugador
		player_Sprite.draw(batch);

		// Muestra el tiempo transcurrido en segundos por pantalla
		chronoFont.setColor(1,1,1,1);
		chronoFont.draw(batch, "" + currentTime, SCREEN_WIDTH / 2 - 30, SCREEN_HEIGHT - 75);

		batch.end();


	}

	@Override
	public void dispose () {
		batch.dispose();
		trozoCarretera.dispose();
		generator.dispose();
	}

	@Override
	public void run() {
		// Los dos trozos de carretera
		Sprite carretera1 = carreteras.get(0);
		Sprite carretera2 = carreteras.get(1);

		// Velocidad
		float velocidad = 1;
		// Evita incrementar la velocidad mas de una vez en el mismo segundo
		int puntoDeIncremento = 0;

		// El segundo tramo de carretera estará antes que el otro
		carretera2.setX(-SCREEN_WIDTH );


		float posicionC1 = 0, posicionC2 = 0;

		while (true){
			// Tiempo actual en segundos
			currentTime = (System.currentTimeMillis() - startTime) / 1000;

			// Incrementa la velocidad
			if (currentTime % 10 == 0){
				// Evita incrementar la velocidad mas de una vez en el mismo segundo
				if (puntoDeIncremento != currentTime){
					velocidad += 0.1;
					puntoDeIncremento = (int) currentTime;
				}
			}


			// Si la primera carretera llega al final, vuelve al principio
			if (carretera1.getX() >= SCREEN_WIDTH){
				posicionC1 = -SCREEN_WIDTH;
			}else{
				// Avanza la carretera
				posicionC1 = carretera1.getX() + velocidad;
			}
			carretera1.setX(posicionC1);

			// Si la segunda carretera llega al final, vuelve al principio
			if (carretera2.getX() >= SCREEN_WIDTH){
				posicionC2 = -SCREEN_WIDTH;
			}else{
				// Avanza la carretera
				posicionC2 = carretera2.getX() + velocidad;
			}
			carretera2.setX(posicionC2);

			// Retardo
			try{
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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
		player_moving = true;

		// Si se pulsa sobre la parte superior de la pantalla el jugador
		// se mueve hacia la derecha, sino, hacia la izquierda
		if (screenY >= SCREEN_HEIGHT / 2){
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

	// Runnable encargador de mover el jugador hacia derecha o izquierda
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
				if (player_Sprite.getY() > SCREEN_HEIGHT - LIMITE_DERECHO_CARRETERA){
					player_Sprite.setY(player_Sprite.getY() - 1);
				}
				// Si el jugador rebasa por el lado izquierdo
				if ( player_Sprite.getY() < LIMITE_IZQUIERDO_CARRETERA){
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
}

