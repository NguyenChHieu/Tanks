package Tanks;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * Class of Tank object, handle actions and render.
 */
public class Tank extends GameObject implements Comparable<Tank> {
    private int[] colorTank;
    private int fuel = 250;
    private int points = 0;

    private int health = 100;
    private int fallDamage = 0;
    private Tank shooter;
    private boolean isAlive = true;

    private int power = 50;
    private float angle = 0;
    private boolean ult = false;

    private int parachutes;
    private boolean parachuteFall;
    private boolean deployed;

    /**
     * Create a tank object.
     *
     * @param xPos X-position of the tank center
     * @param yPos Y-position of the tank center
     * @param type Single letter distinguish tanks.
     */
    public Tank(int xPos, int yPos, String type) {
        super(xPos, yPos, type);
    }

    // Game functions

    /**
     * Move the Tank object relative to
     * the game's FPS and pixel per frame.
     *
     * @param key          UP / DOWN on keyboard
     * @param WINDOW_WIDTH game's window width
     * @param FPS          window's FPS
     * @param pxPS         parameter to adjust how fast the tank moves
     */
    public void move(int key, int WINDOW_WIDTH, int FPS, int pxPS) {
        // Only move when have fuel
        if (fuel > 0) {
            if (key == 37){
                int newPos = xPos - (pxPS / FPS);
                // Exceed left window border
                xPos = Math.max(newPos, 0);
            } else {
                int newPos = xPos + (pxPS / FPS);
                // Exceed right window border
                xPos = Math.min(newPos, WINDOW_WIDTH);
            }
        }
    }

    /**
     * Decrease fuel level every time the tank moves.
     */
    public void useFuel() {
        fuel = Math.max(0, fuel-2);
    }

    /**
     * Create a projectile when player shoots,
     * adjust the "ult" variable when the player
     * bought the ultimate.
     *
     * @return Projectile Object
     */
    public Projectile shoot() {
        // Start point = end of turret
        Projectile bullet = new Projectile(xPos + (int) (15 * PApplet.sin(angle)),
                yPos - 8 - (15 * PApplet.cos(angle)),
                power, this, ult);

        // Turn off ult
        ult = false;
        return bullet;
    }

    /**
     * Update the power level base
     * on the key event.
     *
     * @param key RIGHT/LEFT
     */
    public void updatePower(int key) {
        if (key == 87)
            power = Math.min(power + 1, health);
        else power = Math.max(power - 1, 0);
    }

    /**
     * Update the angle of the turret with relative
     * to the y-axis base on the key events
     *
     * @param key           W/S
     * @param rotationSpeed base on FPS
     */
    public void updateAngle(int key, float rotationSpeed) {
        if (key == 38)
            angle += rotationSpeed;
        else angle -= rotationSpeed;

        // Keep the angle between 0 and 180 deg
        angle = PApplet.constrain(angle, -PApplet.PI / 2, PApplet.PI / 2);
    }

    /**
     * Add points for player's tank to
     * perform buying power-ups
     *
     * @param point points received
     */
    public void addPoints(int point) {
        points += point;
    }

    /**
     * Decrease the number of parachutes by 1,
     * enter the parachuting phase by switch
     * parachuteFall and deployed to true.
     */
    public void deployParachute() {
        parachutes--;
        parachuteFall = true;
        deployed = true;
    }


    // POWER UPS

    /**
     * Repair kit: (key: r, cost: 20) – repairs the player’s
     * tank by increasing health by 20 (maximum health is 100).
     */
    public void repair() {
        int cost = 20;
        if (points >= cost) {
            points -= cost;
            health = Math.min(100, health + 20);
        }
    }

    /**
     * Additional fuel (key: f, cost: 10)
     * increase the player’s remaining fuel by 200.
     */
    public void addFuel() {
        int cost = 10;
        if (points >= cost) {
            points -= cost;
            fuel = Math.min(250, fuel + 200);
        }
    }

    /**
     * Additional parachute (key: p, cost: 15)
     * increase the player’s remaining parachutes by 1.
     */
    public void addParachute() {
        int cost = 15;
        if (points >= cost) {
            points -= cost;
            parachutes++;
        }
    }

    /**
     * Larger projectile (key: x, cost: 20)
     * the next shot fired by this player will
     * have double the radius (60 instead of 30).
     */
    public void ultimate() {
        int cost = 20;
        if (points >= cost) {
            points -= cost;
            ult = true;
        }
    }


    // Draw
    // In-game objects

    /**
     * Draw tank base on its x, y coordinates.
     * The tank would be in a shape of 2 rectangles
     * stacked on top of each other.
     *
     * @param app refer to Main
     */
    public void drawTank(PApplet app) {
        // check center point
//        app.stroke(0);
//        app.point(xPos, yPos);

        // Black border
        app.stroke(0);
        // Lower
        app.fill(colorTank[0], colorTank[1], colorTank[2]);
        app.rect(xPos - 11, yPos - 4, 21, 4, 10);

        // Upper
        app.fill(colorTank[0], colorTank[1], colorTank[2]);
        app.rect(xPos - 8, yPos - 8, 15, 4, 10);

    }

    /**
     * Draw tank's turret.
     * Tank's turret would be in a
     * shape of a bold line.
     *
     * @param app refer to Main
     */
    public void drawTurret(PApplet app) {
        int xEnd = xPos + (int) (15 * PApplet.sin(angle));
        float yEnd = yPos - 8 - (15 * PApplet.cos(angle));
        app.fill(0);
        app.strokeWeight(3);
        app.line(xPos, yPos - 8, xEnd, yEnd);
        // Reset
        app.strokeWeight(1);
    }

    /**
     * Draw tank's position when falling with
     * parachutes/ free-falling
     *
     * @param app           refer to Main
     * @param parachuteIMG  load parachute's image
     * @param terrainHeight current terrain's height at
     *                      index x, with x is the x
     *                      position of the terrain
     */
    public void drawTankFall(PApplet app, String parachuteIMG, float terrainHeight) {
        PImage parachute = app.loadImage(parachuteIMG);
        parachute.resize(40, 40);

        // If tank has done falling, start to deduct health and add points
        if (doneFalling(terrainHeight)) {
            // Initially the shooter has not been set.
            if (shooter != null) {
                // Points = max health of the dead tank (health too low)
                int points = Math.min(fallDamage, health);
                // Avoid self-destruct points
                if (shooter != this) {
                    shooter.addPoints(points);
                }
                tankLoseHP(fallDamage);

                // reset state
                shooter = null;
                fallDamage = 0;
            }
        }

        // Finished falling
        if (deployed && this.yPos >= terrainHeight) {
            deployed = false;
            parachuteFall = false;
        }
        // Tank mid-air
        if (this.yPos < terrainHeight) {
            // Use parachutes + continue falling
            if (parachutes > 0 || parachuteFall) {
                app.image(parachute, (float) this.xPos - 20, this.yPos - 8 - 40);
                drawTank(app);
                this.yPos += 60f / 30; // 30FPS

                if (!deployed) deployParachute();
            } else {
                drawTank(app);
                this.yPos += 120f / 30;
                fallDamage += 4;
            }
        }
    }

    // HUD

    /**
     * Draw tank's ult status (if available).
     * Located next to the fuel level.
     *
     * @param app refer to Main
     */
    public void drawUlt(PApplet app) {
        app.fill(255, 0, 0);
        app.textSize(16);
        app.text("ULTIMATE", 255, 10);

        // reset
        app.fill(0);
        app.textSize(14);
    }

    /**
     * Draw tank's fuel level and fuel icon.
     *
     * @param app     refer to Main
     * @param fuelIMG load fuel icon
     */
    public void drawFuel(PApplet app, String fuelIMG) {
        PImage fuelImage = app.loadImage(fuelIMG);
        fuelImage.resize(20, 20);
        app.image(fuelImage, 160, 10);
        app.fill(0);
        app.textSize(16);
        app.text(fuel, 190, 10);
    }

    /**
     * Draw tank's power level.
     *
     * @param app refer to Main
     */
    public void drawPower(PApplet app) {
        String tankPowerText = "Power: " + power;
        app.fill(0);
        app.textSize(16);
        app.text(tankPowerText, 380, 35);
    }

    /**
     * Draw tank's health bar.
     * Also indicates max power cap (= current health).
     *
     * @param app refer to Main
     */
    public void drawHP(PApplet app) {
        String tankPowerText = "Health: ";
        app.fill(0);
        app.textSize(16);
        app.text(tankPowerText, 380, 10);
        app.text(health, 600, 10);

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
        app.rect(440, 10, actualW, 20);

        // Grey border rect
        app.stroke(80, 80, 80);
        app.strokeWeight(5);
        // adjust grey border by MAX_HEALTH
        app.rect(440, 10, (float) (actualW * power) / health, 20);

        // Red indicator
        app.stroke(255, 0, 0);
        app.strokeWeight(2);
        // adjust bar by power
        app.line(440 + ((float) (actualW * power) / health), 5,
                440 + ((float) (actualW * power) / health), 35);
        // reset
        app.strokeWeight(1);
        app.stroke(0);
    }

    /**
     * Draw tank's parachute with
     * relative to tank's position.
     *
     * @param app          refer to Main.
     * @param parachuteIMG load parachute image.
     */
    public void drawParachute(PApplet app, String parachuteIMG) {
        PImage parachute = app.loadImage(parachuteIMG);
        parachute.resize(20, 20);
        app.image(parachute, 160, 40);
        app.text(parachutes, 190, 40);
    }


    // GETTER & SETTERS

    /**
     * Get the RGB set of the tank
     *
     * @return length-3 array contain RGB values
     */
    public int[] getColorTank() {
        return colorTank;
    }

    /**
     * Set the tank's color
     *
     * @param rgb length-3 RGB array
     */
    public void setColorTank(int[] rgb) {
        colorTank = rgb;
    }

    /**
     * Get the current max health of the tank
     *
     * @return int represent the tank's health
     */
    public int getHealth() {
        return health;
    }

    /**
     * Get the fuel level
     *
     * @return int represent fuel level
     */
    public int getFuelLevel() {
        return fuel;
    }

    /**
     * Get the current turret angle relative to the Y-axis
     *
     * @return float value of the angle
     */
    public float getAngle() {
        return angle;
    }

    /**
     * Get the ultimate status of the tank
     *
     * @return true if tank is in ultimate mode
     */
    public boolean getUltStatus() {
        return ult;
    }

    /**
     * Get current number of parachutes
     *
     * @return int represent number of parachutes
     */
    public int getParachutes() {
        return parachutes;
    }

    /**
     * Set the current number of parachutes of the tank.
     * Use for loading initial number of parachutes or
     * load the tank's number of parachutes in the
     * previous level.
     *
     * @param number number of parachutes
     */
    public void setParachutes(int number) {
        parachutes = number;
    }

    /**
     * Get current points
     *
     * @return int represent points
     */
    public int getPoints() {
        return points;
    }

    /**
     * Set the tank's current points
     *
     * @param point points in int value
     */
    public void setPoints(int point) {
        points = point;
    }

    /**
     * Get the power level
     *
     * @return int represent the power level
     */
    public int getPower() {
        return power;
    }

    /**
     * Return a boolean value indicates
     * if the tank fall below the limit.
     *
     * @return true if yPos > 640
     */
    public boolean isOutMap() {
        return yPos > 640;
    }

    /**
     * Return tank's falling state
     *
     * @param terrainHeight terrain's height at index
     *                      (tank.xPos)
     * @return true if tank is not during the parachute fall (parachuteFall = true)
     * and tank.yPos is not above terrain's Height.
     */
    public boolean doneFalling(float terrainHeight) {
        return !parachuteFall && !(yPos < terrainHeight);
    }

    /**
     * Check if the tank's dead.
     * If tank has no health and not in a falling state.
     *
     * @param terrainHeight terrain's height at index (tank.xPos)
     * @return true if isAlive is false.
     */
    public boolean isDead(float terrainHeight) {
        if (health == 0 && doneFalling(terrainHeight)) {
            isAlive = false;
        }
        return !isAlive;
    }

    /**
     * Set the culprit of the explosion which
     * damage the tank. Use for points tracking
     * purposes.
     *
     * @param tank Tank object which damage the tank (could be itself)
     */
    public void setShooter(Tank tank) {
        shooter = tank;
    }

    /**
     * Set the tank's alive status to false
     */
    public void setDeadByExplode() {
        isAlive = false;
    }

    /**
     * Set the tank's fuel for testing
     * @param fuel int represent fuel level
     */
    public void setFuel(int fuel){
        this.fuel = fuel;
    }

    /**
     * Decrease tank's health, the health is limit at 0
     * to avoid the health gets to negative.
     *
     * @param hp health points to deduct.
     */
    public void tankLoseHP(int hp) {
        health = Math.max(0, health - hp);
        power = Math.min(power, health);
    }

    /**
     * Overriding the Comparable method -> The tanks are comparable in order
     * Leverage this to sort the Tanks lexicographically by using queue data structure.
     *
     * @param otherTank the object to be compared.
     * @return 0 if the string = other string (= characters).
     * Less than 0 - string has fewer characters other string
     * Greater than - if string has more characters other string.
     */
    @Override
    public int compareTo(Tank otherTank) {
        return this.type.compareTo(otherTank.type);
    }
}
