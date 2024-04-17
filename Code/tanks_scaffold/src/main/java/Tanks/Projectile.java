package Tanks;

import processing.core.PVector;
import processing.core.PApplet;

public class Projectile {
    private PVector position;
    private PVector velocity;
    private PApplet app;

    public Projectile(int x, int y, PVector velocity){
        this.position = new PVector(x, y);
        this.velocity = velocity;
    }

    public void update(){
        velocity.y += 3.6F;

        position.add(velocity);
    }

}
