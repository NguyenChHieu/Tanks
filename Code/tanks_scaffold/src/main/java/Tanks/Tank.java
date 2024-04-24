package Tanks;

import processing.core.PApplet;
import processing.core.PImage;

public class Tank extends GameObject implements Comparable<Tank>{
    private int health = 100;
    private int power = 50;
    private int fuel = 250;
    private int[] colorTank;
    private float angle = 0;
    private int parachutes = 1;
    private boolean parachuteFall;
    private boolean deployed;
    private boolean isAlive = true;
    private int points = 0;


    public Tank(int xPos, int yPos, String type){
        super(xPos, yPos, type);
    }


    // Game functions
    public void move(int key, int WINDOW_WIDTH, int FPS, int pxPS){
        // Only move when have fuel
            if (fuel >= 0) {
                switch (key) {
                    // left
                    case 37:
                        // Exceed left window border
                        if (xPos >= pxPS / FPS) {
                            xPos -= pxPS / FPS;
                            break;
                        }
                    case 39:
                        // Exceed right window border
                        if (xPos <= WINDOW_WIDTH - pxPS / FPS) {
                            xPos += pxPS / FPS;
                            break;
                        }
                }
        }
    }
    public void useFuel(){
        fuel -= 2;
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
        angle = PApplet.constrain(angle, -PApplet.PI/2, PApplet.PI/2);
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
    }
    public Projectile shoot(){
        // Start point = end of turret
        return new Projectile(xPos + (int) (15 * PApplet.sin(angle)),
                                yPos - 8 - (15 * PApplet.cos(angle)),
                                    power, this);
    }
    public void addPoints(int point){
        points += point;
    }
    public void deployParachute(){
        if (parachutes > 0){
            parachutes--;
            parachuteFall = true;
            deployed = true;
        }
    }
    // TODO TANK OUT MAP



    // POWER UPS
    public void repair(){
        int cost = 20;
        if (points >= cost){
            points -= cost;
            health = Math.min(100, health + 20);
        }
    }
    public void addFuel(){
        int cost = 10;
        if (points >= cost){
            points -= cost;
            fuel = Math.min(250, fuel + 200);
        }
    }

    // Draw
        // In-game objects
    public void drawTank(PApplet app) {
        // check center point
//        app.stroke(0);
//        app.point(xPos, yPos);

        // Black border
        app.stroke(0);
        // Lower
        app.fill(colorTank[0], colorTank[1], colorTank[2]);
        app.rect(xPos-11, yPos-4, 21, 4,10);

        // Upper
        app.fill(colorTank[0], colorTank[1], colorTank[2]);
        app.rect(xPos-8, yPos-8, 15, 4,10);

    }
    public void drawTurret(PApplet app){
        int xEnd = xPos + (int) (15 * PApplet.sin(angle));
        float yEnd = yPos - 8 - (15 * PApplet.cos(angle));
        app.fill(0);
        app.strokeWeight(3);
        app.line(xPos, yPos-8, xEnd, yEnd);
        // Reset
        app.strokeWeight(1);
    }
    public void drawTankFall(PApplet app, String parachuteIMG, float terrainHeight){
        PImage parachute = app.loadImage(parachuteIMG);
        parachute.resize(40, 40);

        // Finished falling
        if (deployed && this.yPos >= terrainHeight){
            deployed = false;
            parachuteFall = false;
        }
        // Tank mid-air
        if (this.yPos < terrainHeight) {
            // Use parachutes + continue falling
            if (parachutes > 0 || parachuteFall) {
                app.image(parachute, (float) this.xPos - 20, this.yPos - 8 - 40);
                drawTank(app);
                this.yPos += 60f/ 30; // 30FPS

                if (!deployed)
                    deployParachute();
            } else {
                drawTank(app);
                this.yPos += 120f/ 30;
            }
        }
    }
    // HUD
    public void drawFuel(PApplet app, String fuelIMG){
        PImage fuelImage = app.loadImage(fuelIMG);
        fuelImage.resize(20,20);
        app.image(fuelImage,160, 10);
        app.fill(0);
        app.textSize(16);
        app.text(fuel, 190, 10);
    }
    public void drawPower(PApplet app){
        String tankPowerText = "Power: " + power;
        app.fill(0);
        app.textSize(16);
        app.text(tankPowerText, 380, 35);
    }
    public void drawHP (PApplet app){
        String tankPowerText = "Health: ";
        app.fill(0);
        app.textSize(16);
        app.text(tankPowerText, 380, 10);
        app.text(health,600, 10);

        int MAX_HEALTH = 100;
        int actualW = 150 * health / MAX_HEALTH; // Bar width = 150

        // Health bar
            // HP frame
        app.fill(255);
        app.strokeWeight(4);
        app.rect(440, 10, 150, 20);
                // reset
        app.strokeWeight(1);

            // Current HP frame
        app.fill(colorTank[0], colorTank[1], colorTank[2]);
        app.rect(440, 10,
                actualW, 20);

            // Grey border rect
        app.stroke(80, 80, 80);
        app.strokeWeight(5);
            // adjust grey border by MAX_HEALTH
        app.rect(440, 10,
                (float) (actualW * power) / health, 20);

            // Red indicator
        app.stroke(255, 0, 0);
        app.strokeWeight(2);
                // adjust bar by power
        app.line(440 +((float) (actualW * power) / health), 5,
                440 +((float) (actualW * power) / health), 35);
                // reset
        app.strokeWeight(1);
        app.stroke(0);
    }
    public void drawParachute(PApplet app, String parachuteIMG){
        PImage parachute = app.loadImage(parachuteIMG);
        parachute.resize(20, 20);
        app.image(parachute, 160, 40);
        app.text(parachutes, 190, 40);
    }



    // GETTER & SETTERS
    public float getAngle() {
        return angle;
    }
    public int getFuelLevel(){
        return fuel;
    }
    public int getPoints(){
        return points;
    }
    public int getParachutes(){
        return parachutes;
    }
    public int[] getColorTank(){
        return colorTank;
    }
    public int getHealth(){
        return health;
    }
    public boolean isDead(){
        if (health == 0){
            isAlive = false;
        }
        return !isAlive;
    }
    public boolean isFalling(float terrainHeight) {
        return parachuteFall || yPos < terrainHeight;
    }
    public boolean isOutMap(){
        return yPos > 639;
    }

    public void setColorTank(int[] rgb){
        colorTank = rgb;
    }
    public void setPoints(int point){
        points = point;
    }
    public void tankLoseHP(int hp){
        health = Math.max(0, health-hp);
        power = Math.min(power, health);
    }
    // Overriding the Comparable method -> The tanks are comparable in order

    // Leverage this to sort the Tanks alphabetically by using queue ds.
    @Override
    public int compareTo(Tank otherTank) {
        return this.getType().compareTo(otherTank.getType());
    }
}
