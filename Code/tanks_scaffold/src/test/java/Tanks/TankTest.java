package Tanks;

import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TankTest {
    // Get prompt in other class
    private final ConfigManagerTest get = new ConfigManagerTest();

    /**
     * Tests the move(), useFuel() method and check
     * if the tank moves correctly and consume fuel as expected.
     */
    @Test
    public void testTankMovement() {
        get.printPrompt("testTankMovement", false);

        App app = new App();
        app.loop();
        PApplet.runSketch(new String[]{"App"}, app);
        app.setConfigPath("additionalFiles/testMap.json");
        app.setup();
        // Setup delay
        app.delay(1000);

        // Move right +2 * 10
        for (int i = 0; i < 10; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 39));
        }
        // Move left -2 * 10
        for (int i = 0; i < 10; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 37));
        }

        Tank testTank = app.getTanksAlive().get(0);
        assertEquals(128, testTank.xPos, "Incorrect ROC for tank speed.");
        assertEquals(210, testTank.getFuelLevel(), "Incorrect fuel level.");
        System.out.println("testTankMovement passed");
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
        PApplet.runSketch(new String[]{"App"}, app);
        app.setConfigPath("additionalFiles/testMap.json");
        app.setup();
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
        PApplet.runSketch(new String[]{"App"}, app);
        app.setConfigPath("additionalFiles/testMap.json");
        app.setup();
        // Setup delay
        app.delay(1000);

        // Increase angle to max
        for (int i = 0; i < 30; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 38));
        }
        // Increase angle to min
        for (int i = 0; i < 60; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 40));
        }

        Tank testTank = app.getTanksAlive().get(0);
        assertEquals(-app.PI / 2, testTank.getAngle(), "Incorrect ROC for tank angle.");
        System.out.println("testTankAngle passed");
    }

    // TEST POWER UPS
    /**
     * Tests the addFuel() power up and check if
     * the fuel level of the tank is updated correctly
     */
    @Test
    public void addFuel(){
        get.printPrompt("testAddFuel", false);

        App app = new App();
        app.loop();
        PApplet.runSketch(new String[]{"App"}, app);
        app.setConfigPath("additionalFiles/testMap.json");
        app.setup();
        // Setup delay
        app.delay(1000);

        // Move a bit to consume fuel
        for (int i = 0; i < 10; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 39));
        }

        // Tried to buy when have insufficient credits
        app.keyPressed(new KeyEvent(null, 0, 0, 0, 'F', 70));
        Tank testTank = app.getTanksAlive().get(0);
        testTank.addPoints(10);

        // Buy credits when have sufficient credits
        app.keyPressed(new KeyEvent(null, 0, 0, 0, 'F', 70));

        // Check fuel level
        assertEquals(250, testTank.getFuelLevel(), "Incorrect fuel level.");
        // Check credits
        assertEquals(0, testTank.getPoints(), "Incorrect amount of credits.");
        System.out.println("testAddFuel passed");
    }
}
