package Tanks;

import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.*;

public class TankTest {
    // Get prompt in other class
    private final ConfigManagerTest get = new ConfigManagerTest();

    /**
     * Tests the move(), useFuel() method and check
     * if the tank moves correctly and consume fuel as expected.
     * This includes test when tank has no fuel and tried to move,
     * Move when has fuel, and try to move until reached the borders.
     * Mimic the behaviour of the player when they mis-click a non-function
     * key.
     */
    @Test
    public void testTankMovement() {
        get.printPrompt("testTankMovement", false);

        App app = new App();
        app.loop();
        app.setConfigPath("additionalFiles/testMap.json");
        PApplet.runSketch(new String[]{"App"}, app);
        // Setup delay
        app.delay(1000);

        Tank testTank = app.getTanksAlive().get(0);
        // Exhaust the fuel
        testTank.setFuel(0);
        // Try to move
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 39));
        assertEquals(128, testTank.xPos, "Tank should not move when fuel is exhausted.");

        // Click a key which has no function
        app.keyPressed(new KeyEvent(null, 0, 0, 0, 'J', 74));

        // Set fuel for testing
        testTank.setFuel(250);
        // Move right +2 * 10
        for (int i = 0; i < 10; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 39));
        }
        // Move left -2 * 10
        for (int i = 0; i < 10; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 37));
        }
        // Test normal
        assertEquals(128, testTank.xPos, "Incorrect ROC for tank speed.");
        assertEquals(210, testTank.getFuelLevel(), "Incorrect fuel level.");

        // Test tank move to borders
        testTank.setFuel(2000);
        // Right border
        while (testTank.xPos != 864) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 39));
        }
        assertEquals(864, testTank.xPos, "Incorrect tank location.");

        testTank.setFuel(2000);
        // Left border
        while (testTank.xPos != 0) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 37));
        }
        assertEquals(0, testTank.xPos, "Incorrect tank location.");

        System.out.println("testTankMovement passed");
        app.noLoop();
    }

    /**
     * Tests the updatePower() method and check if
     * the power attribute of the corresponding tank
     * is updated correctly.
     */
    @Test
    public void testTankPower() {
        get.printPrompt("testTankPower", false);

        App app = new App();
        app.loop();
        app.setConfigPath("additionalFiles/testMap.json");
        PApplet.runSketch(new String[]{"App"}, app);
        // Setup delay
        app.delay(1000);

        // Increase power to 100
        for (int i = 0; i < 50; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, 'W', 87));
        }
        // Decrease power to 0
        for (int i = 0; i < 100; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, 'S', 83));
        }

        Tank testTank = app.getTanksAlive().get(0);
        assertEquals(0, testTank.getPower(), "Incorrect ROC for tank power.");
        System.out.println("testTankPower passed");
        app.noLoop();
    }

    /**
     * Tests the updateAngle() method and check if
     * the angle attribute of the corresponding tank
     * is updated correctly.
     */
    @Test
    public void testTankAngle() {
        get.printPrompt("testTankAngle", false);

        App app = new App();
        app.loop();
        app.setConfigPath("additionalFiles/testMap.json");
        PApplet.runSketch(new String[]{"App"}, app);
        // Setup delay
        app.delay(1000);

        // Decrease angle to min
        for (int i = 0; i < 30; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 38));
        }
        // Increase angle to max
        for (int i = 0; i < 60; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 40));
        }

        Tank testTank = app.getTanksAlive().get(0);
        assertEquals(app.PI / 2, testTank.getAngle(), "Incorrect ROC for tank angle.");
        System.out.println("testTankAngle passed");
        app.noLoop();
    }

    /**
     * Tests the setPoints() function which use to set
     * the points for the tanks on the new level.
     */
    @Test
    public void testSetPoints() {
        get.printPrompt("testSetPoints", false);
        // Create a dummy tank and add points
        Tank testTank = new Tank(0, 0, "O");
        testTank.setPoints(10);

        // Test
        assertEquals(10, testTank.getPoints(), "Wrong number of points.");
        System.out.println("testSetPoints passed");
    }

    // TEST POWER UPS

    /**
     * Tests the addFuel() power up and check if
     * the fuel level of the tank is updated correctly
     */
    @Test
    public void addFuel() {
        get.printPrompt("testAddFuel", false);

        App app = new App();
        app.loop();
        app.setConfigPath("additionalFiles/testMap.json");
        PApplet.runSketch(new String[]{"App"}, app);
        // Setup delay
        app.delay(1000);

        // Move a bit to consume fuel
        for (int i = 0; i < 10; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 39));
        }

        // Test when have insufficient credits
        app.keyPressed(new KeyEvent(null, 0, 0, 0, 'F', 70));

        // Add credits
        Tank testTank = app.getTanksAlive().get(0);
        testTank.addPoints(10);

        // Test when have sufficient credits
        app.keyPressed(new KeyEvent(null, 0, 0, 0, 'F', 70));

        // Check fuel level
        assertEquals(250, testTank.getFuelLevel(), "Incorrect fuel level.");
        // Check credits
        assertEquals(0, testTank.getPoints(), "Incorrect amount of credits.");
        System.out.println("testAddFuel passed");
        app.noLoop();
    }

    /**
     * Tests the repair() power up and check if
     * health is replenished correctly
     */
    @Test
    public void testRepair() {
        get.printPrompt("testRepair", false);

        App app = new App();
        app.loop();
        app.setConfigPath("additionalFiles/testMap.json");
        PApplet.runSketch(new String[]{"App"}, app);
        // Setup delay
        app.delay(1000);

        // Test repair() when have insufficient credits
        app.keyPressed(new KeyEvent(null, 0, 0, 0, 'R', 82));

        // Decrease health and add points for testing
        Tank testTank = app.getTanksAlive().get(0);
        testTank.tankLoseHP(10);
        testTank.addPoints(20);

        // Test repair() when have sufficient credits
        app.keyPressed(new KeyEvent(null, 0, 0, 0, 'R', 82));

        // Check health level
        assertEquals(100, testTank.getHealth(), "Incorrect health level.");
        // Check credits
        assertEquals(0, testTank.getPoints(), "Incorrect amount of credits.");
        System.out.println("testRepair passed");
        app.noLoop();
    }

    /**
     * Tests the addParachute() power up and check
     * if the parachute was correctly added
     */
    @Test
    public void testAddParachute() {
        get.printPrompt("testAddParachute", false);

        App app = new App();
        app.loop();
        app.setConfigPath("additionalFiles/testMap.json");
        PApplet.runSketch(new String[]{"App"}, app);
        // Setup delay
        app.delay(1000);

        // Test when have insufficient credits
        app.keyPressed(new KeyEvent(null, 0, 0, 0, 'P', 80));

        // Add credits
        Tank testTank = app.getTanksAlive().get(0);
        int initialNumParachute = testTank.getParachutes();
        testTank.addPoints(15);

        // Test when have sufficient credits
        app.keyPressed(new KeyEvent(null, 0, 0, 0, 'P', 80));


        // Check number of parachutes
        assertEquals(initialNumParachute + 1, testTank.getParachutes(),
                "Incorrect number of parachutes.");
        // Check credits
        assertEquals(0, testTank.getPoints(), "Incorrect amount of credits.");
        System.out.println("testAddParachute passed");
        app.noLoop();
    }

    /**
     * Tests the ultimate() power up and check if
     * the drawUlt() and the projectile has the correct
     * behaviour.
     */
    @Test
    public void testUlt() {
        get.printPrompt("testUlt", false);

        App app = new App();
        app.loop();
        app.setConfigPath("additionalFiles/testMap.json");
        PApplet.runSketch(new String[]{"App"}, app);
        // Setup delay
        app.delay(1000);

        // Test when have insufficient credits
        app.keyPressed(new KeyEvent(null, 0, 0, 0, 'X', 88));

        // Add credits
        Tank testTank = app.getTanksAlive().get(0);
        testTank.addPoints(20);

        // Test when have sufficient credits
        app.keyPressed(new KeyEvent(null, 0, 0, 0, 'X', 88));
        app.delay(200);
        assertTrue(testTank.getUltStatus(), "Ult status should be true before shoots.");

        // Decrease power
        for (int i = 0; i < 100; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, 'S', 83));
        }

        // Shoots
        Projectile.setWindTest(0);
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));
        Projectile.setWindTest(0);
        app.delay(4000);

        // Check fuel level
        assertFalse(testTank.getUltStatus(), "Ult status should be false after shoots.");
        // Check credits
        assertEquals(0, testTank.getPoints(), "Incorrect amount of credits.");
        System.out.println("testUlt passed");
        app.noLoop();
    }
}
