package Tanks;

import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectileTest {

    private final ConfigManagerTest get = new ConfigManagerTest();

    /**
     * Tests the shoot() method with max/min power and check
     * if the tank fires a projectile correctly and then switch turn.
     */
    @Test
    public void testShoots() {
        App app = new App();
        app.loop();
        app.setConfigPath("additionalFiles/testMap.json");
        PApplet.runSketch(new String[]{"App"}, app);
        // Setup delay
        app.delay(2000);

        get.printPrompt("testShoots", false);
        // Decrease power to 0
        for (int i = 0; i < 50; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, 'S', 83));
        }

        // Player A Shoots
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));

        // Test
        Tank testTank = app.getTanksAlive().get(0);
        assertEquals("B", testTank.type, "The current tank should be B after A shoots.");

        // TEST TANK B SHOOT OUTSIDE border
        // Move right +2 * 10
        for (int i = 0; i < 125; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 39));
        }

        // Increase angle
        for (int i = 0; i < 8; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 38));
        }

        // Max power
        for (int i = 0; i < 50; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, 'W', 87));
        }

        // Player B Shoots
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));
        app.delay(4000);

        testTank = app.getTanksAlive().get(0);
        assertEquals("A", testTank.type, "The current tank should be A after B shoots.");
        System.out.println("testShoots passed");
    }

    /**
     * Test the shoot() method when a player hits the opponent
     * This test verifies that the damage is added and points
     * are added correctly.
     */
    @Test
    public void testBulletHitsOpponent() {
        App app = new App();
        app.loop();
        app.setConfigPath("additionalFiles/testMap.json");
        PApplet.runSketch(new String[]{"App"}, app);
        // Setup delay
        app.delay(2000);

        get.printPrompt("testBulletHitsOpponent", false);
        // Max angle
        for (int i = 0; i < 30; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 38));
        }

        // Shoots, isolate wind effect to test the damage
        Projectile.setWindTest(0);
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));
        Projectile.setWindTest(0);

        app.delay(2000);

        int damagedTankHealth = app.getTanksAlive().get(0).getHealth();
        // This is because of the float difference +- 1 pixel of the bullet trajectory.
        boolean checkHealth = damagedTankHealth >= 80 && damagedTankHealth <= 82;
        int shooterPoints = app.getTanksAlive().get(1).getPoints();
        boolean checkPoints = shooterPoints >= 18 && shooterPoints <= 20;

        // Check if points and damage is added correctly
        assertTrue(checkHealth, "Incorrect damage applied on damage tank (B).\n"
                + "Actual damage: " + shooterPoints);
        assertTrue(checkPoints, "Incorrect points applied on shooter tank (A).");

        System.out.println("testBulletHitsOpponent passed");
    }

    /**
     * Test the shoot() method when a player hits the opponent
     * but has no parachute -> additional fall damage
     * This test verifies that the damage is added and points
     * are added correctly.
     */
    @Test
    public void testBulletHitsOpponentNoParachute() {
        App app = new App();
        app.loop();
        app.setConfigPath("additionalFiles/testMap.json");
        PApplet.runSketch(new String[]{"App"}, app);
        // Setup delay
        app.delay(2000);

        get.printPrompt("testBulletHitsOpponentNoParachute", false);

        // Set the targeted tank to have no parachutes
        app.getTanksAlive().get(1).setParachutes(0);

        // Max angle
        for (int i = 0; i < 30; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 38));
        }

        // Shoots, isolate wind effect to test the damage
        Projectile.setWindTest(0);
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));
        Projectile.setWindTest(0);

        app.delay(2000);

        int damagedTankHealth = app.getTanksAlive().get(0).getHealth();
        // This is because of the float difference +- 1 pixel of the bullet trajectory (explosion damage varies)
        boolean checkHealth = damagedTankHealth >= 56 && damagedTankHealth <= 58;
        int shooterPoints = app.getTanksAlive().get(1).getPoints();
        boolean checkPoints = shooterPoints <= 44 && shooterPoints >= 42;

        // Check if points and damage is added correctly
        assertTrue(checkHealth, "Incorrect damage applied on damage tank (B).");
        assertTrue(checkPoints, "Incorrect points applied on shooter tank (A).");

        System.out.println("testBulletHitsOpponentNoParachute passed");
    }

    /**
     * Test the setWindLevel() and getWindLevel() method
     * which mainly used for testing and isolating the
     * wind (setting them to 0)
     * Covers the all 3 drawWind() cases - left, right,
     * no wind.
     */
    @Test
    public void testWind() {
        App app = new App();
        app.loop();
        app.setConfigPath("additionalFiles/testMap.json");
        PApplet.runSketch(new String[]{"App"}, app);
        // Setup delay
        app.delay(1000);

        get.printPrompt("testWind", false);

        Projectile.setWindTest(0);
        assertEquals(0, Projectile.getWindTest(), "Wrong wind level.");
        app.delay(100);

        Projectile.setWindTest(20);
        assertEquals(20, Projectile.getWindTest(), "Wrong wind level.");
        app.delay(100);

        Projectile.setWindTest(-20);
        assertEquals(-20, Projectile.getWindTest(), "Wrong wind level.");
        app.delay(100);

        // Set random wind level
        Projectile.setWindLevel();
        boolean inInitialRange = Projectile.getWindTest() >= -35 && Projectile.getWindTest() <= 35;
        assertTrue(inInitialRange, "Wind level is not in initial range [-35,35]");

        System.out.println("testWind passed");
    }
}
