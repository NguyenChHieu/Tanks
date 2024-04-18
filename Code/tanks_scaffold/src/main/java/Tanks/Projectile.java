package Tanks;

import processing.core.PVector;
import processing.core.PApplet;

import javax.swing.plaf.metal.MetalTheme;

public class Projectile {
    private float xPos;
    private float yPos;
    private int iniVelo;
    private float gravity = 3.6f/30;
    private PApplet app;

    public Projectile(int x, int y, int power){
        xPos = x;
        yPos = y;
        iniVelo = (Math.min(power/10, 9) == 0) ? 1 : Math.min(power/10, 9); // TODO check
    }

    public void update(float angle){
        xPos += (float) (iniVelo * Math.cos(angle)); // += Vx
        yPos += (float) (iniVelo * Math.sin(angle)) + gravity; //Vy
    }


    public void drawProjectile(int[] rgb, float x, float y, PApplet app){
        app.ellipse(x, y , 10, 10);
    }
}
