package Tanks;

import processing.core.PApplet;

public class Explosion {
    private final PApplet app;
    private final float xPos;
    private final float yPos;
    private float startTime = 0;
    private final int radius;
    private boolean finishedExplode = false;

    public Explosion (PApplet app, float xPos, float yPos, int radius){
        this.app = app;
        this.xPos = xPos;
        this.yPos = yPos;
        this.radius = radius;
    }

    public void drawExplosion(){
        if (startTime == 0){
            startTime = app.millis();
        }
        float animationDuration = 200f;
        float elapsedTime = app.millis()-startTime;

        float currentRedRadius = Math.min(radius * elapsedTime / animationDuration, radius);
        float currentOrangeRadius = Math.min(radius * 0.5f * elapsedTime / animationDuration, radius * 0.5f);
        float currentYellowRadius = Math.min(radius * 0.2f * elapsedTime / animationDuration, radius * 0.2f);

        int[] colors = {app.color(255, 0, 0), app.color(255, 165, 0), app.color(255, 255, 0)};
        if (!finishedExplode) {
            app.noStroke();

            if (elapsedTime <= animationDuration) {
                app.fill(colors[0]);
                app.ellipse(xPos, yPos, currentRedRadius * 2, currentRedRadius * 2);
                app.fill(colors[1]);
                app.ellipse(xPos, yPos, currentOrangeRadius * 2, currentOrangeRadius * 2);
                app.fill(colors[2]);
                app.ellipse(xPos, yPos, currentYellowRadius * 2, currentYellowRadius * 2);

            } else finishedExplode = true;

            //reset
            app.stroke(0);
        }
    }

    public boolean getFinishedExplode(){
        return finishedExplode;
    }
}