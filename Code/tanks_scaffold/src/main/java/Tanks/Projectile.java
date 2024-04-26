package Tanks;

import processing.core.PApplet;
import processing.core.PImage;
import java.util.List;
import java.util.Objects;

public class Projectile {
    private float xPos;
    private float yPos;
    private float vX;
    private  float vY;
    private static int windLevel = (int) (Math.random() * 71) -35;
    private final float wind = windLevel;
    private final Tank shooter;
    private boolean isExplode;
    private boolean isOut = false;


    // Game Functions
    public Projectile(int x, float y, int power, Tank shooter){
        xPos = x;
        yPos = y;
        this.shooter = shooter;
        float iniVelocity = (Math.min(power/10, 9) == 0) ? 1 : Math.min(power/10, 9);
        vX = iniVelocity * PApplet.sin(shooter.getAngle());
        vY = -iniVelocity * PApplet.cos(shooter.getAngle());
    }
    public static void windChange (){
        windLevel += (int) (Math.random() * 11) - 5;
    }
    public void update(){
        accelerate();
        xPos += vX;
        yPos += vY;
    }
    private void accelerate(){
        float gravity = 3.6f / 30f;
        vY += gravity;
        vX += wind * 0.03f / 30;
    }
    public boolean collide(float[] terrainHeight){
        if (yPos >= terrainHeight[(int)xPos]){
            isExplode = true;
            return true;
        }
        return false;
    }
    public void explode(float[] terrainHeight, List<Tank> tanks){
        int EXPLODE_RADIUS = 30;
        // Explode within the window
        int left = Math.max((int) (xPos - EXPLODE_RADIUS), 0);
        int right = Math.min((int) (xPos + EXPLODE_RADIUS), 864);

        for (int x = left; x < right; x++) {

            float ySemiCircle = (float) Math.sqrt(Math.pow(EXPLODE_RADIUS, 2) -
                    Math.pow(x - xPos, 2));
            float lowerBound = yPos - ySemiCircle;
            float upperBound = yPos + ySemiCircle;

            if (terrainHeight[x] < lowerBound) {
                terrainHeight[x] += 2 * ySemiCircle;
            } else if (terrainHeight[x] >= lowerBound &&
                    terrainHeight[x] <= upperBound) {
                terrainHeight[x] = upperBound;
            }
        }
        explosionDamage(tanks);
        fallDamage(terrainHeight, tanks);
    }
    private void explosionDamage(List<Tank> tanks){
        for (Tank tank: tanks){
            float distance = (float) Math.sqrt(Math.pow(tank.xPos - xPos, 2) +
                                                Math.pow(tank.yPos - yPos, 2));
            if (distance >= 0 && distance <= 30){
                int damage = 60 - (int) distance * 2;

                System.out.println(tank.type+ " explode " + damage +" shooter " + shooter.type);

                tank.tankLoseHP(damage);
                // Avoid + points for self-destruct
                if (!Objects.equals(tank.getType(), shooter.getType()))
                    shooter.addPoints(damage);
            }
        }
    }
    private void fallDamage(float[] terrainHeight, List<Tank> tanks){
        for (Tank tank : tanks){
            if (tank.yPos < terrainHeight[tank.xPos] - 1){
                // If the tank is dead = explosion, skip
                if (!tank.isDead(terrainHeight[tank.xPos])) {
                    // Tank has no parachutes
                    if (tank.getParachutes() == 0) {
                        int fallDMG = Math.min((int) (terrainHeight[tank.xPos] - tank.yPos), tank.getHealth());
                        tank.tankLoseHP(fallDMG);

                        System.out.println(tank.type+ " fall "+ fallDMG + " shooter " + shooter.type);

                        if (!tank.isDead(terrainHeight[tank.xPos])){
                            if (!Objects.equals(tank.getType(), shooter.getType()))
                                shooter.addPoints(fallDMG);
                        }
                    }
                }
            }
        }
    }

    // DRAW
    public void drawProjectile(PApplet app){
//        app.stroke(0);
//        app.point(xPos, yPos);

        int[] rgb = shooter.getColorTank();
        app.fill(rgb[0], rgb[1], rgb[2]);
        app.ellipse(xPos, yPos , 10, 10);
    }
    // Explicitly get X, Y because tanks can also explode
    public static void drawExplosion(PApplet app, float x, float y, float startTime, boolean isLargeExplode) {
        float explosionRadius = (isLargeExplode) ? 30: 15;
        float animationDuration = 0.2f;
        float elapsedTime = PApplet.constrain((app.millis() - startTime) / 1000.0f, 0, animationDuration);

        float currentRedRadius = Math.min(explosionRadius * elapsedTime / animationDuration, explosionRadius);
        float currentOrangeRadius = Math.min(explosionRadius * 0.5f * elapsedTime / animationDuration, explosionRadius * 0.5f);
        float currentYellowRadius = Math.min(explosionRadius * 0.2f * elapsedTime / animationDuration, explosionRadius * 0.2f);

        int[] colors = {app.color(255, 0, 0), app.color(255, 165, 0), app.color(255, 255, 0)};

        app.noStroke();
        for (int i = 0; i < 3; i++) {
            app.fill(colors[i]);
            float currentDiameter = 2 * ((i == 0) ? currentRedRadius : (i == 1) ? currentOrangeRadius : currentYellowRadius);
            app.ellipse(x, y, currentDiameter, currentDiameter);
        }
    }
    public static void drawWind(PApplet app, String wR, String wL){
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


    // GETTER & SETTERS
    public boolean outMap(){
        if (yPos > 639|| xPos > 863 || xPos <0){
            isOut = true;
            return true;
        }
        return false;
    }
    public boolean isOut() {
        return isOut;
    }
    public boolean isExplode() {
        return isExplode;
    }
    public float getXPos() {
        return xPos;
    }
    public float getYPos() {
        return yPos;
    }
    public static void setWindLevel(){
        windLevel = (int) (Math.random() * 71) -35;
    }
}
