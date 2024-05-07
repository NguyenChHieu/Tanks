package Tanks;

import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void testTankFallToDeath(){
        get.printPrompt("testTankFallToDeath", false);

        App app = new App();
        app.loop();
        app.setConfigPath("additionalFiles/testMap.json");
        PApplet.runSketch(new String[]{"App"}, app);

        // Setup delay
        app.delay(2000);

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
        for (int i = 0; i < 26; i ++){
            // Shoots, isolate wind effect to test the damage
            Projectile.setWindTest(0);
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));
            Projectile.setWindTest(0);
        }
        // Wait for the explosions to finish
        app.delay(5000);

        // Tank B move right +2 * 30
        for (int i = 0; i < 42; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 39));
        }
        // Wait for the explosion
        app.delay(2000);

        // Test
        assertEquals(1, app.getTanksAlive().size(), "Incorrect number of alive tanks.");
        System.out.println("testTankFallToDeath passed");
    }


    /**
     * Tests the tank's explosion radius (15)
     * when the tank's health is 0.
     */
    @Test
    public void testTankExplosion(){
        App app = new App();
        app.loop();
        app.setConfigPath("additionalFiles/testMap.json");
        PApplet.runSketch(new String[]{"App"}, app);
        // Setup delay
        app.delay(1000);

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
    }
}
