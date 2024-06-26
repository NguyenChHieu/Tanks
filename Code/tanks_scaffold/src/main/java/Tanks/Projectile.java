package Tanks;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.List;
import java.util.Objects;

/**
 * Class of projectiles, calculate the location
 * of the projectile and measure the damages.
 */
public class Projectile {
    private static int windLevel = (int) (Math.random() * 71) - 35;
    private float WIND = windLevel;
    private final Tank SHOOTER;
    private final boolean POWERED_UP;
    private float xPos;
    private float yPos;
    private float vX;
    private float vY;
    private boolean isExplode;
    private boolean isOut = false;


    // Game Functions

    /**
     * Create a projectile object.
     * The projectile's velocity got split into
     * 2 parts: Vx and Vy, and these parts are
     * calculated initially in the constructor.
     *
     * @param x       initial x position
     * @param y       initial y position
     * @param power   initial power
     * @param shooter refers to the tank that shoot the projectile
     * @param ult     boolean value indicates whether the projectile is
     *                in ultimate mode (double the normal radius).
     */
    public Projectile(int x, float y, int power, Tank shooter, boolean ult) {
        xPos = x;
        yPos = y;
        this.SHOOTER = shooter;
        POWERED_UP = ult;

        float iniVelocity = (Math.min(power / 10, 9) == 0) ? 1 : Math.min(power / 10, 9);
        vX = iniVelocity * PApplet.sin(shooter.getAngle());
        vY = -iniVelocity * PApplet.cos(shooter.getAngle());
    }

    /**
     * Modify the wind level by +5/-5 after each player fires.
     * @return return the current wind level
     */
    public static int windChange() {
        windLevel += (int) (Math.random() * 11) - 5;
        return windLevel;
    }

    /**
     * Draw the wind icon on the top left side of HUD.
     *
     * @param app refer to Main
     * @param wR  load left wind image
     * @param wL  load right wind image
     */
    public static void drawWind(PApplet app, String wR, String wL) {
        PImage windR = app.loadImage(wR);
        PImage windL = app.loadImage(wL);
        windR.resize(50, 50);
        windL.resize(50, 50);
        if (windLevel > 0)
            app.image(windR, 760, 5);
        else if (windLevel < 0)
            app.image(windL, 760, 5);
        app.fill(0);
        app.text(Math.abs(windLevel), 815, 17);
    }

    /**
     * Set the new initial wind at the start of the level.
     */
    public static void setWindLevel() {
        windLevel = (int) (Math.random() * 71) - 35;
    }

    /**
     * Update the current x, y of the projectile.
     */
    public void update() {
        accelerate();
        xPos += vX;
        yPos += vY;
    }

    /**
     * Implement the effects of gravity and wind on the projectile
     */
    private void accelerate() {
        float gravity = 3.6f / 30f;
        vY += gravity;
        vX += WIND * 0.03f / 30;
    }

    /**
     * Check if projectile hits the terrain.
     *
     * @param terrainHeight the height of the terrain at interaction point
     * @return true if projectile hits.
     */
    public boolean collide(float[] terrainHeight) {
        if (yPos >= terrainHeight[(int) xPos]) {
            isExplode = true;
            return true;
        }
        return false;
    }

    /**
     * Update the terrain base on the impact of the explosion,
     * calculate the explosion damage on tanks (if in range).
     *
     * @param terrainHeight  list of height for each pixel
     * @param tanks          list of alive tanks
     * @param EXPLODE_RADIUS radius of the explosion
     */
    public void explode(float[] terrainHeight, List<Tank> tanks, int EXPLODE_RADIUS) {
        // Explode within the window
        int left = Math.max((int) (xPos - EXPLODE_RADIUS), 0);
        int right = Math.min((int) (xPos + EXPLODE_RADIUS), 864);
        // Check in range of the explosion
        for (int x = left; x < right; x++) {
            float ySemiCircle = (float) Math.sqrt(Math.pow(EXPLODE_RADIUS, 2) -
                    Math.pow(x - xPos, 2));
            float lowerBound = yPos - ySemiCircle;
            float upperBound = yPos + ySemiCircle;
            // Terrain on top the explosion
            if (terrainHeight[x] < lowerBound) {
                terrainHeight[x] += 2 * ySemiCircle;
                // Terrain in range of the explosion
            } else if (terrainHeight[x] >= lowerBound &&
                    terrainHeight[x] <= upperBound) {
                terrainHeight[x] = upperBound;
            }
        }
        explosionDamage(terrainHeight, tanks, EXPLODE_RADIUS);
        fallDamage(terrainHeight, tanks, EXPLODE_RADIUS);
    }

    // DRAW

    /**
     * Calculate the damage cause by the explosion on tanks.
     * @param tanks          list of alive tanks
     * @param EXPLODE_RADIUS radius of the explosion
     */
    private void explosionDamage(float[] terrainHeight, List<Tank> tanks, int EXPLODE_RADIUS) {
        for (Tank tank : tanks) {
            float distance = (float) Math.sqrt(Math.pow(tank.xPos - xPos, 2) +
                    Math.pow(tank.yPos - yPos, 2));

            // If target tank is not dead
            if (!tank.isDead(terrainHeight[tank.xPos])) {
                // If in range of the explosion
                if (distance >= 0 && distance <= EXPLODE_RADIUS) {
                    int damage = 60 - (int) distance * 60 / EXPLODE_RADIUS;
                    damage = Math.min(tank.getHealth(), damage);
//                System.out.println(tank.type+ " explode " + damage +" shooter " + shooter.type);

                    tank.tankLoseHP(damage);
                    if (tank.getHealth() == 0) {
                        tank.setDeadByExplode();
                    }
                    // Avoid adding points for self-destruct
                    if (!Objects.equals(tank.type, SHOOTER.type))
                        SHOOTER.addPoints(damage);
                }
            }
        }
    }

    /**
     * Calculate the fall damage on tanks which cause
     * by the projectile explosion
     *
     * @param terrainHeight  list of height for each pixel.
     * @param tanks          list of alive tanks
     * @param EXPLODE_RADIUS radius of the explosion
     */
    private void fallDamage(float[] terrainHeight, List<Tank> tanks, int EXPLODE_RADIUS) {
        int left = Math.max((int) (xPos - EXPLODE_RADIUS), 0);
        int right = Math.min((int) (xPos + EXPLODE_RADIUS), 864);

        for (Tank tank : tanks) {
            // If the tank is in the range of the explosion -> which might made them fall
            if (left <= tank.xPos && tank.xPos <= right) {
                // Tank is above the terrain
                if (tank.yPos < terrainHeight[tank.xPos] - 1) {
                    // If target tank is dead, skip
                    if (!tank.isDead(terrainHeight[tank.xPos])) {
                        tank.setShooter(SHOOTER);
                    }
                }
            }
        }
    }

    /**
     * Draw the projectile's current position.
     * @param app refer to Main
     */
    public void drawProjectile(PApplet app) {
        int[] rgb = SHOOTER.getColorTank();
        app.fill(rgb[0], rgb[1], rgb[2]);
        app.ellipse(xPos, yPos, 10, 10);
    }

    // GETTER & SETTERS

    /**
     * Check if the projectile is an ultimate
     * @return true if the projectile is an ultimate
     */
    public boolean isPoweredUp() {
        return POWERED_UP;
    }

    /**
     * Get the isOut status of the projectile
     * @return true if the projectile goes beyond the game's border
     */
    public boolean isOut() {
        return isOut;
    }

    /**
     * Get the explosion status of the projectile
     * @return true if the projectile had exploded
     */
    public boolean isExplode() {
        return isExplode;
    }

    /**
     * Get the X-position of the projectile
     * @return xPos of projectile
     */
    public float getXPos() {
        return xPos;
    }

    /**
     * Get the Y-position of the projectile
     * @return yPos of projectile
     */
    public float getYPos() {
        return yPos;
    }

    /**
     * Check if the projectile goes outside the map.
     * @return true if the projectile go beyond the border.
     */
    public boolean outMap() {
        if (yPos > 642 || xPos > 864 || xPos < 0) {
            isOut = true;
            return true;
        }
        return false;
    }

    /**
     * Set the current wind to affect the projectile
     * @param wind represents current wind
     */
    public void setWindProjectile(int wind){
        WIND = wind;
    }

    /**
     * Set the current wind level. Mainly use for
     * testing - such as isolating the wind from
     * the trajectory.
     * @param wind int represent wind level
     */
    public static void setWindTest(int wind){
        windLevel = wind;
    }

    /**
     * Get current wind level. (For testing)
     * @return int represent current wind level
     */
    public static int getWindTest(){
        return windLevel;
    }
}
