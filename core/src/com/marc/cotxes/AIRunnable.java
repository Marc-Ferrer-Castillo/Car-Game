package com.marc.cotxes;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class AIRunnable implements Runnable {
    // SCREEN DIMENSIONS
    private int SCREEN_WIDTH = Main.SCREEN_WIDTH;
    private int SCREEN_HEIGHT = Main.SCREEN_HEIGHT;

    // Sprite for the vehicle
    private Sprite AI_Sprite;

    // Positions for the AI vehicles
    int firstLine = (SCREEN_HEIGHT / 5) + 40;
    private int[] possible_Spawn_Positions = { firstLine, firstLine + firstLine / 2 + 40, firstLine * 2 + 90 ,firstLine * 3 - 10};



    // Constructor
    AIRunnable(Sprite AI_Sprite) {
        this.AI_Sprite = AI_Sprite;
        // Starts the thread
        new Thread (this).start();
    }


    @Override
    public void run() {
        // Picks a position
        pickLane();

        // Until end of screen
        while (AI_Sprite.getX() <= SCREEN_WIDTH){
            // Moves forward
            moveAIForward();
            // Check for collisions
            checkCollision();
            // Thread sleep
            try{
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Returns the sprite to a starting point
        pickLane();
    }

    private void checkCollision() {
        // Player shape
        Rectangle player_Rectangle = Main.getPlayer_Sprite().getBoundingRectangle();
        // This AI shape
        Rectangle AI_Rectangle = AI_Sprite.getBoundingRectangle();

        // If collides
        if (player_Rectangle.overlaps(AI_Rectangle)){
            // Moves the AI to the end in order to prevent multiple collisions
            AI_Sprite.setX(SCREEN_WIDTH);
            // Updates lives from the player
            PlayerRunnable.subtractLive();
        }
    }

    // Positions the car in a lane
    private void pickLane() {
        AI_Sprite.setX(-AI_Sprite.getWidth());
        int random = new Random().nextInt(possible_Spawn_Positions.length);
        AI_Sprite.setY(possible_Spawn_Positions[random]);
    }

    // Moves cars forward
    private void moveAIForward() {
        // Road position
        float position = AI_Sprite.getX() + Main.getSpeed() + 0.5f;
        AI_Sprite.setX(position);
    }
}
