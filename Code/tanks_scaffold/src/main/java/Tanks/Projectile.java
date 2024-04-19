package Tanks;

import processing.core.PVector;
import processing.core.PApplet;

import javax.swing.*;
import javax.swing.plaf.metal.MetalTheme;

public class Projectile {
    private float xPos;
    private float yPos;
    private int iniVelo;
    private float gravity = (float) (3.6/30f);
    private Tank shooter;
    private boolean isExplode;

    public Projectile(int x, int y, int power, Tank shooter){
        xPos = x;
        yPos = y;
        iniVelo = (Math.min(power/10, 9) == 0) ? 1 : Math.min(power/10, 9); // TODO check
        this.shooter = shooter;
    }

    public void update(){
        float vX = iniVelo * PApplet.sin(shooter.getAngle());
        float vY = - iniVelo * PApplet.cos(shooter.getAngle());
        accelerate();
        vY += gravity;

        xPos += vX;
        yPos += vY;

        System.out.println(xPos);
        System.out.println(yPos);
    }
    private void accelerate(){
        gravity += (float) (3.6/30f);
    }
    public boolean collide(int[] terrainHeight){
        if (yPos >= terrainHeight[(int)xPos]){
            isExplode = true;
            return true;
        }
        return false;
    }

    public void drawProjectile(PApplet app){
        int[] rgb = shooter.getColorTank();
        app.fill(rgb[0], rgb[1], rgb[2]);
        app.ellipse(xPos, yPos , 10, 10);
    }

    public boolean isExplode() {
        return isExplode;
    }
}
