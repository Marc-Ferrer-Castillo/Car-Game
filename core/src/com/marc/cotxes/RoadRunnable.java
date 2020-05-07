package com.marc.cotxes;

import com.badlogic.gdx.graphics.g2d.Sprite;

// Runnable that moves roads according to speed and Delta Time
public class RoadRunnable implements Runnable {
    // SCREEN DIMENSIONS
    private static int SCREEN_WIDTH;

    // Game Speed
    private static float speed = 2;
    private float backPosition;
    // The two roads
    private Sprite road1;
    private Sprite road2;


    RoadRunnable(int screen_width, Sprite road1, Sprite road2) {
        SCREEN_WIDTH = screen_width;
        this.road1 = road1;
        this.road2 = road2;
        backPosition = -SCREEN_WIDTH - 5;
    }

    public static void setSpeed(float speed) {
        RoadRunnable.speed = speed;
    }

    public void run(){
        // Second road position will be before the first one
        road2.setX(backPosition);

        while (true){
            move(road1);
            move(road2);
            // Thread sleep
            try{
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void move(Sprite road) {
        // Road position
        float position = 0;

        // Once the road reaches the end of the screen -5
        if (road.getX() >= SCREEN_WIDTH - 5){
            // Goes back to initial position
            position = backPosition;
        }else{
            // Moves the road to the new position
            position = road.getX() + speed;
        }
        road.setX(position);
    }
}
