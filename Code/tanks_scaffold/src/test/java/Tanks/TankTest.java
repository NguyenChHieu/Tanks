package Tanks;

import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.*;

public class TankTest {
    // Get prompt in other class
    private final ConfigManagerTest get = new ConfigManagerTest();

    /**
     * Tests the move() method and check if the tank
     * moves correctly and consume fuel as expected.
     */
    @Test
    public void testTankMovement(){
        get.printPrompt("testTankMovement", true);

        App app = new App();
        app.loop();
        PApplet.runSketch(new String[] {"App"}, app);
        app.setConfigPath("additionalFiles/testMap.json");
        app.setup();
        // Setup delay
        app.delay(1000);

        // Move right +2 * 10
        for (int i = 0; i < 10; i++){
            app.keyPressed(new KeyEvent(null, 0, 0 ,0, ' ', 39));
        }
        // Move left -2 * 10
        for (int i = 0; i < 10; i++){
            app.keyPressed(new KeyEvent(null, 0, 0 ,0, ' ', 37));
        }

        Tank testTank = app.getTanksAlive().get(0);
        assertEquals(128, testTank.xPos, "Incorrect ROC for tank speed.");
        assertEquals(210, testTank.getFuelLevel(), "Incorrect fuel level.");
        System.out.println("testTankMovement passed");
    }

}
