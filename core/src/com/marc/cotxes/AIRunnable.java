package com.marc.cotxes;

import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.Random;

public class AIRunnable implements Runnable {
    // SCREEN DIMENSIONS
    private int SCREEN_WIDTH = Main.SCREEN_WIDTH;
    private int SCREEN_HEIGHT = Main.SCREEN_HEIGHT;

    int firstLine = SCREEN_HEIGHT / 6;
    private int[] possible_Spawn_Positions = { firstLine, firstLine * 2 - 30, firstLine * 3 - 40, firstLine * 4 - 50 };

    // Sprite for the vehicle
    private Sprite AI_Sprite;


    AIRunnable(Sprite AI_Sprite) {
        this.AI_Sprite = AI_Sprite;
        // Starts the thread
        new Thread (this).start();
    }


    @Override
    public void run() {
        // Picks a position
        pickLane();

        while (AI_Sprite.getX() <= SCREEN_WIDTH){
            moveAIForward();

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

    private void pickLane() {
        AI_Sprite.setX(-AI_Sprite.getWidth());
        int random = new Random().nextInt(possible_Spawn_Positions.length);
        AI_Sprite.setY(possible_Spawn_Positions[random]);
    }

    private void moveAIForward() {
        // Road position
        float position = AI_Sprite.getX() + Main.getSpeed() + 0.5f;
        AI_Sprite.setX(position);
    }
}
