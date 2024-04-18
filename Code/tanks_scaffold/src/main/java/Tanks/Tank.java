package Tanks;

import processing.core.PApplet;

import static processing.core.PApplet.constrain;
import static processing.core.PConstants.PI;

public class Tank extends GameObject implements Comparable<Tank>{
    private int health = 100;
    private int power = 50;
    private int fuel = 250;
    private int[] colorTank;
    private float angle = 0;
    private PApplet app; //TODO: INJECT APP

    public Tank(int xPos, int yPos, String type){
        super(xPos, yPos, type);
    }

    // TODO: CHECK UPDATE POWER
    public Projectile shoot(int x, int y){
        Projectile bullet = new Projectile(x, y, power);
        return bullet;
    }
    public void updatePower(int key){
        if (power <= health){
            switch (key){
                // W
                case 87:
                    power = Math.min(power + 1, health);
                    break;
                // S
                case 83:
                    power = Math.max(power - 1, 0);
                    break;
            }
        }
//        System.out.println(power);
    }
    public void updateAngle(int key, float rotationSpeed){
        switch (key){
            // Up
            case 38:
                angle += rotationSpeed;
                break;
            // Down
            case 40:
                angle -= rotationSpeed;
                break;
        }
        // Keep the angle between 0 and 180 deg
        angle = constrain(angle, -PI/2, PI/2);
//        System.out.println(angle);
    }

    public void useFuel(){
        fuel -= 2;
    }
    public void move(int key, int WINDOW_WIDTH, int FPS, int pxPS){
        // Only move when have fuel
        if (fuel >= 0) {
            switch (key) {
                // left
                case 37:
                    // Exceed left window border
                    if (xPos >= pxPS/FPS) {
                        xPos -= pxPS/FPS;
                        break;
                    }
                case 39:
                    // Exceed right window border
                    if (xPos <= WINDOW_WIDTH - pxPS/FPS) {
                        xPos += pxPS/FPS;
                        break;
                    }
            }
        }
    }

    // GETTER & SETTERS
    public float getAngle() {
        return angle;
    }
    public int getPowerLevel(){
        return power;
    }
    public int getFuelLevel(){
        return fuel;
    }
    public void setColorTank(int[] rgb){
        colorTank = rgb;
    }
    public int[] getColorTank(){
        return colorTank;
    }

    // Overriding the Comparable method -> The tanks are comparable in order
    // Leverage this to sort the Tanks alphabetically by using Queue
    @Override
    public int compareTo(Tank otherTank) {
        return this.getType().compareTo(otherTank.getType());
    }
}
