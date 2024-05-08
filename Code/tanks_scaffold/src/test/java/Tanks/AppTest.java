package Tanks;

import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppTest {
    private final ConfigManagerTest get = new ConfigManagerTest();

    /**
     * This test is created to cover the case when players died
     * at the same time - no players alive left in the current
     * level.
     * Then, we won't draw the HUD anymore.
     */
    @Test
    public void coverHUDWhenPlayersDiedSameTime() {
        get.printPrompt("testCoverHUDWhenPlayersDiedSameTime", false);

        App app = new App();
        app.loop();
        app.setConfigPath("additionalFiles/testMap.json");
        PApplet.runSketch(new String[]{"App"}, app);

        // Setup delay
        app.delay(2000);

        // Set up the tanks
        // Set initial health of each tank = 20
        for (Tank tank : app.getTanksAlive()) {
            tank.tankLoseHP(80);
        }

        // Take turns shooting
        for (int i = 0; i < 2; i++) {
            // Shoots, isolate wind effect to test the damage
            Projectile.setWindTest(0);
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));
            Projectile.setWindTest(0);
        }

        app.delay(3000);

        // Test
        assertEquals(0, app.getTanksAlive().size(), "Incorrect number of alive tanks.");
        System.out.println("testCoverHUDWhenPlayersDiedSameTime passed");
    }

    /**
     * This test is created to test the switching level option
     * - after the currentLevel has only 1 player, then the game will
     * add a 1s delay before switching to the next level.
     */
    @Test
    public void testSwitchLevel1sDelay() {
        get.printPrompt("testSwitchLevel1sDelay", false);

        App app = new App();
        app.loop();
        app.setConfigPath("additionalFiles/testFullMap.json");
        PApplet.runSketch(new String[]{"App"}, app);

        // Setup delay
        app.delay(2000);

        // Set up the current tank to have low health to self-destruct.
        // This is made to test the 1s delay.

        app.getTanksAlive().get(0).tankLoseHP(80);

        // Shoots, isolate wind effect to test the damage
        Projectile.setWindTest(0);
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));
        Projectile.setWindTest(0);
        app.delay(1500);

        // Set a timer
        int start = app.millis();

        app.delay(1000);
        boolean checkTime = app.millis() - start >= 1000;

        // Add a delay to see the change of new level
        app.delay(1000);
        // Test
        assertTrue(checkTime, "Time is less than 1s");
        assertEquals(2, app.getTanksAlive().size(), "New level should have 2 tanks.");

        System.out.println("testSwitchLevel1sDelay passed");
    }

    /**
     * This test is created to test the switching level option
     * - click space then the game will switch levels instantly.
     */
    @Test
    public void testSwitchLevelInstantly() {
        get.printPrompt("testSwitchLevelInstantly", false);

        App app = new App();
        app.loop();
        app.setConfigPath("additionalFiles/testFullMap.json");
        PApplet.runSketch(new String[]{"App"}, app);

        // Setup delay
        app.delay(2000);

        // Set up the current tank to have low health to self-destruct.
        // This is made to test the 1s delay.

        app.getTanksAlive().get(0).tankLoseHP(80);

        // Shoots, isolate wind effect to test the damage
        Projectile.setWindTest(0);
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));
        Projectile.setWindTest(0);
        app.delay(1500);

        // Set a timer
        int start = app.millis();

        // Press space again to switch to new level instantly
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));

        boolean checkTimeElapsed = app.millis() - start <= 1000;

        // Add a delay to see the change of new level
        app.delay(2000);

        // Test
        assertTrue(checkTimeElapsed, "Switching between levels instantly should be done in less than 1s.");
        assertEquals(2, app.getTanksAlive().size(), "New level should have 2 tanks.");

        System.out.println("testSwitchLevelInstantly passed");
    }

    /**
     * This test is created to test the reset whole game function
     * when player finished the game and clicking "R". It also
     * mimics the behaviour of a player, when they mis-click a
     * non-function key.
     */
    @Test
    public void testResetGame() {
        get.printPrompt("testResetGame", false);

        App app = new App();
        app.loop();
        app.setConfigPath("additionalFiles/testMap.json");
        PApplet.runSketch(new String[]{"App"}, app);

        // Setup delay
        app.delay(2000);

        // Set health of the tank to low so that we can test the explosion quicker
        app.getTanksAlive().get(0).tankLoseHP(90);

        // Decrease power to 0
        for (int i = 0; i < 50; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, 'S', 83));
        }

        // Shoots
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));
        app.delay(5000);

        // Click a key which has no function
        app.keyPressed(new KeyEvent(null, 0, 0, 0, 'J', 74));
        // Resets the game
        app.keyPressed(new KeyEvent(null, 0, 0, 0, 'R', 82));
        app.delay(3000);

        // Test
        boolean checkPointsReset = true;
        boolean checkTankHealth = true;
        boolean checkInitialParachutes = true;
        for (Tank tank : app.getTanksAlive()) {
            if (tank.getPoints() != 0) {
                checkPointsReset = false;
                break;
            }
            if (tank.getHealth() != 100) {
                checkTankHealth = false;
                break;
            }
            // Currently in App initial parachutes = 3
            if (tank.getParachutes() != 3) {
                checkInitialParachutes = false;
                break;
            }
        }

        assertTrue(checkPointsReset, "Points should be reset.");
        assertTrue(checkTankHealth, "Health should be reset.");
        assertTrue(checkInitialParachutes, "Parachutes should be reset.");
        assertEquals(0, app.getCurrentLevelIndex(), "New game should start at level 1 (index 0)");

        System.out.println("testResetGame passed");
    }

    /**
     * This test is simply calling main.
     */
    @Test
    public void otherTests() {
        get.printPrompt("otherTests", false);

        App.main(new String[]{});
        System.out.println("Successfully called .main");
    }
}
