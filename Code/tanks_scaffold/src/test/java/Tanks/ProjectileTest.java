package Tanks;

import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectileTest {

    private final ConfigManagerTest get = new ConfigManagerTest();

    /**
     * Tests the shoot() method and check if the tank
     * fires a projectile correctly and then switch turn
     */
    @Test
    public void testShootMinPower(){
        get.printPrompt("testShoot", false);

        App app = new App();
        app.loop();
        app.setConfigPath("additionalFiles/testMap.json");
        PApplet.runSketch(new String[]{"App"}, app);
        // Setup delay
        app.delay(1000);

        // Increase angle to min
        for (int i = 0; i < 60; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 40));
        }
        // Decrease power to 0
        for (int i = 0; i < 50; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, 'S', 83));
        }

        // Shoots
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));

        Tank testTank = app.getTanksAlive().get(0);
        assertEquals("B", testTank.type, "The current tank should be B after A shoots.");
        System.out.println("testShoot passed");
    }

    /**
     * Tests the shoot() method and check if the tank
     * fires a projectile correctly and then switch turn
     */
    @Test
    public void testShootMaxPower(){
        get.printPrompt("testShootMaxPower", false);

        App app = new App();
        app.loop();
        app.setConfigPath("additionalFiles/testMap.json");
        PApplet.runSketch(new String[]{"App"}, app);
        // Setup delay
        app.delay(1000);

        // Increase angle to min
        for (int i = 0; i < 60; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 40));
        }
        // Max power
        for (int i = 0; i < 50; i++) {
            app.keyPressed(new KeyEvent(null, 0, 0, 0, 'W', 87));
        }

        // Shoots
        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 32));

        Tank testTank = app.getTanksAlive().get(0);
        assertEquals("B", testTank.type, "The current tank should be B after A shoots.");
        System.out.println("testShootMaxPower passed");
    }
}
