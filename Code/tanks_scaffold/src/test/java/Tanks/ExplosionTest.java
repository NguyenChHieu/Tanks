package Tanks;

import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExplosionTest {
    private final ConfigManagerTest get = new ConfigManagerTest();

    // TANK COLLISIONS

    /**
     * Tests the tank's explosion radius (30)
     * when the tank gets to the bottom of the map.
     * This test is created by letting the B tank
     * create a hole and go straight in. This is
     * for the testing if the radius of the explosion
     * was correct.
     */
    @Test
    public void testTankFallToDeath() {
        get.printPrompt("testTankFallToDeath", false);

        App app = new App();
        app.loop();
        app.setConfigPath("additionalFiles/testMap.json");
        PApplet.runSketch(new String[]{"App"}, app);

        // Setup delay
        app.delay(4000);

        // Decrease angle to min of tank A
        for (int i = 0; i < 60; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 40));
        }
        // Shoots
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));

        // SET UP THE HOLE
        // Increase angle to max
        for (int i = 0; i < 30; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 38));
        }
        // Decrease power
        for (int i = 0; i < 40; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, 'S', 83));
        }

        // Take turns shooting, then the B tank will create a hole eventually
        for (int i = 0; i < 28; i++) {
            // Shoots, isolate wind effect to test the damage
            Projectile.setWindTest(0);
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));
            Projectile.setWindTest(0);
        }
        // Wait for the explosions to finish
        app.delay(5000);

        // Tank B move right +2 * 30
        for (int i = 0; i < 40; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 39));
        }
        // Wait for the explosion
        app.delay(2000);

        // Test
        assertEquals(1, app.getTanksAlive().size(), "Incorrect number of alive tanks.");
        System.out.println("testTankFallToDeath passed");
        app.noLoop();
    }

    // TANK COLLISIONS

    /**
     * Tests the tank's explosion radius (15)
     * when the tank falls to death.
     * This test is created by letting the A tank
     * shoot the terrain B tank.
     */
    @Test
    public void testTankFallToDeathByBullet() {
        get.printPrompt("testTankFallToDeathByBullet", false);

        App app = new App();
        app.loop();
        app.setConfigPath("additionalFiles/testMapHighGrounds.json");
        PApplet.runSketch(new String[]{"App"}, app);

        // Setup delay
        app.delay(5000);

        // Setup B tank to have no parachute and low health
        Tank tankB = app.getTanksAlive().get(1);
        tankB.tankLoseHP(60);
        tankB.setParachutes(0);

        // Adjust A's angle
        // Increase angle to max
        for (int i = 0; i < 30; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 38));
        }
        // Shoots
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));

        // Adjust B's angle
        // Increase angle to max
        for (int i = 0; i < 30; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 38));
        }
        // Move to the top
        for (int i = 0; i < 5; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 37));
        }

        // Take turns shooting
        for (int i = 0; i < 6; i++) {
            // Shoots, isolate wind effect to test the damage
            Projectile.setWindTest(0);
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));
            Projectile.setWindTest(0);
        }
        // Wait for created bullets to explode
        app.delay(500);

        // Tank B attempt to move on-air but couldn't
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 39));

        app.delay(2000);

        // Test
        int pointsForFallDamage = app.getTanksAlive().get(0).getPoints();
        boolean checkTrue = pointsForFallDamage == 40; // since B's health is 40
        assertTrue(checkTrue, "Wrong points.");
        assertEquals(1, app.getTanksAlive().size(), "Incorrect number of alive tanks.");
        System.out.println("testTankFallToDeathByBullet passed");
        app.noLoop();
    }

    /**
     * Tests the tank's explosion radius (15)
     * when the tank's health is 0 and touches
     * the ground.
     */
    @Test
    public void testTankExplosion() {
        App app = new App();
        app.loop();
        app.setConfigPath("additionalFiles/testMap.json");
        PApplet.runSketch(new String[]{"App"}, app);
        // Setup delay
        app.delay(4000);

        get.printPrompt("testTankExplosion", false);

        // Set health of the tank to low so that we can test the explosion quicker
        app.getTanksAlive().get(0).tankLoseHP(90);

        // Decrease power to 0
        for (int i = 0; i < 50; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, 'S', 83));
        }

        // Shoots
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));
        app.delay(2000);

        // Test
        Tank testTank = app.getTanksAlive().get(0);
        assertEquals("B", testTank.type, "The current tank should be B after A shoots.");
        assertEquals(1, app.getTanksAlive().size(), "There should be only 1 tank left.");
        System.out.println("testTankExplosion passed");
        app.noLoop();
    }
}
