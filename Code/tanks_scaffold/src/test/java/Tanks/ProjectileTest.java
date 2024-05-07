package Tanks;

import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectileTest {

    private final ConfigManagerTest get = new ConfigManagerTest();

    /**
     * Tests the shoot() method with max/min power and check
     * if the tank fires a projectile correctly and then switch turn.
     */
    @Test
    public void testShoots(){
        App app = new App();
        app.loop();
        app.setConfigPath("additionalFiles/testMap.json");
        PApplet.runSketch(new String[]{"App"}, app);
        // Setup delay
        app.delay(1000);

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

        // Max power
        for (int i = 0; i < 50; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, 'W', 87));
        }

        // Player B Shoots
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));
        app.delay(2000);

        testTank = app.getTanksAlive().get(0);
        assertEquals("A", testTank.type, "The current tank should be A after B shoots.");
        System.out.println("testShoots passed");
    }
}
