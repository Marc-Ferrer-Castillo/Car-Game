package com.marc.cotxes;

import com.badlogic.gdx.graphics.g2d.Sprite;

// Runnable that moves the player to left or right
// Also limits the player max and min position on the Y axis
public class PlayerRunnable implements Runnable {
    // MAX PLAYER LIVES
    private static final byte MAX_LIVES = 3;
    // PLAYER MOVEMENT
    private final boolean MOVE_PLAYER_LEFT = true;
    private final boolean MOVE_PLAYER_RIGHT = false;
    // ROAD LIMITS THAT CARS CAN'T SURPASS
    static final int RIGHT_LIMIT = 340;
    static final int LEFT_LIMIT = 235;
    // SCREEN DIMENSIONS
    private int SCREEN_HEIGHT;

    // Player alive
    private static boolean player_Alive = true;
    // Player lives
    private static byte lives = MAX_LIVES;
    // Player sprite
    private Sprite player_Sprite;
    // Player moving left or right
    private boolean player_moving_direction;
    // Player moving?
    private boolean player_moving = false;

    // Constructor
    PlayerRunnable(int SCREEN_HEIGHT, Sprite player_Sprite) {
        this.SCREEN_HEIGHT = SCREEN_HEIGHT;
        this.player_Sprite = player_Sprite;
    }

    // Called when the player collides with an AI
    static void subtractLive(){
        // Ends player live
        if (lives == 1){
            --lives;
            player_Alive = false;
            Main.restart();
        }
        // Subtracts a live if player is alive
        else{
            --lives;
        }
    }
    // Restarts lives from the player
    void restartLives(){
        lives = MAX_LIVES;
    }

    // Getters
    static byte getLives() {
        return lives;
    }

    // Setters
    void setPlayer_moving(boolean player_moving) {
        this.player_moving = player_moving;
    }
    void setPlayer_moving_direction(boolean player_moving_direction) {
        this.player_moving_direction = player_moving_direction;
    }
    static void setPlayer_Alive(boolean player_Alive) {
        PlayerRunnable.player_Alive = player_Alive;
    }

    public void run() {
        while (player_Alive){
            if (player_moving){
                // Left movement
                if (player_moving_direction == MOVE_PLAYER_LEFT){
                    player_Sprite.setY(player_Sprite.getY() - 1);
                }
                // Right movement
                else if(player_moving_direction == MOVE_PLAYER_RIGHT){
                    player_Sprite.setY(player_Sprite.getY() + 1);
                }
            }
            // If the player reaches max right side
            if (player_Sprite.getY() > SCREEN_HEIGHT - RIGHT_LIMIT){
                player_Sprite.setY(player_Sprite.getY() - 1);
            }
            // If the player reaches max left side
            if ( player_Sprite.getY() < LEFT_LIMIT){
                player_Sprite.setY(player_Sprite.getY() + 1);
            }
            // Thread sleep
            try{
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
