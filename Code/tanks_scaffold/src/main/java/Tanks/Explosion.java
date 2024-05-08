package Tanks;

import processing.core.PApplet;

/**
 * Class to draw explosions
 */
public class Explosion {
    private final PApplet APP;
    private final int RADIUS;
    private final float xPos;
    private final float yPos;
    private float startTime = 0;
    private boolean finishedExplode = false;

    /**
     * Create an explosion object
     * @param app    refer to Main
     * @param xPos   X-position of the center
     * @param yPos   Y-position of the center
     * @param radius radius of the explosion
     */
    public Explosion(PApplet app, float xPos, float yPos, int radius) {
        this.APP = app;
        this.xPos = xPos;
        this.yPos = yPos;
        this.RADIUS = radius;
    }

    /**
     * Draw the explosion with specified radius.
     */
    public void drawExplosion() {
        // Set start time
        if (startTime == 0) {
            startTime = APP.millis();
        }
        float animationDuration = 200f;
        float elapsedTime = APP.millis() - startTime;

        float currentRedRadius = RADIUS * elapsedTime / animationDuration;
        float currentOrangeRadius = RADIUS * 0.5f * elapsedTime / animationDuration;
        float currentYellowRadius = RADIUS * 0.2f * elapsedTime / animationDuration;

        int[] colors = {APP.color(255, 0, 0), APP.color(255, 165, 0), APP.color(255, 255, 0)};

        APP.noStroke();

        if (elapsedTime <= animationDuration) {
            APP.fill(colors[0]);
            APP.ellipse(xPos, yPos, currentRedRadius * 2, currentRedRadius * 2);
            APP.fill(colors[1]);
            APP.ellipse(xPos, yPos, currentOrangeRadius * 2, currentOrangeRadius * 2);
            APP.fill(colors[2]);
            APP.ellipse(xPos, yPos, currentYellowRadius * 2, currentYellowRadius * 2);

        } else finishedExplode = true;

        //reset
        APP.stroke(0);

    }

    /**
     * Return the state of the explosion (finished or not)
     * @return whether the explosion has finished
     */
    public boolean getFinishedExplode() {
        return finishedExplode;
    }
}