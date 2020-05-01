package com.marc.cotxes;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.ArrayList;

public class Principal extends ApplicationAdapter implements Runnable{
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
	Sprite jugadorSprite;


	@Override
	public void create () {

		//Texture botonIniciar = new Texture("");
		//Drawable drawable = new TextureRegionDrawable(new TextureRegion(playTexture);
		//ImageButton playButton = new ImageButton(drawable);


		// GET Dimensions pantalla
		SCREEN_WIDTH = Gdx.graphics.getWidth();
		SCREEN_HEIGHT = Gdx.graphics.getHeight();

		// Tiempo inicial en ms
		startTime = System.currentTimeMillis();
		// Cronometro
		chronoFont = new BitmapFont();
		// Batch
		batch = new SpriteBatch();

		// Crea las carreteras del juego
		crearCarreteras();

		// Textura con la imagen del coche del jugador
		jugadorTexture = new Texture("player.png");
		// Sprite con la textura del coche del jugador
		jugadorSprite = new Sprite(jugadorTexture);
		jugadorSprite.setPosition(SCREEN_WIDTH - jugadorSprite.getWidth(), SCREEN_HEIGHT / 2);




		// Inicia el thread para mover las carreteras
		Thread t = new Thread(this);
		t.start();
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
		jugadorSprite.draw(batch);

		// Muestra el tiempo transcurrido en segundos por pantalla
		chronoFont.getData().setScale(4f);
		chronoFont.setColor(1,1,1,1);
		chronoFont.draw(batch, "Tiempo: " + currentTime, SCREEN_WIDTH / 2 - 110, SCREEN_HEIGHT - 10);

		batch.end();


	}

	@Override
	public void dispose () {
		batch.dispose();
		trozoCarretera.dispose();
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
}

