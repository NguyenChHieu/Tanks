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
     */
//    @Test
//    public void testTankFallToDeath(){
//        get.printPrompt("testTankFallToDeath", false);
//
//        App app = new App();
//        app.loop();
//        app.setConfigPath("additionalFiles/testMap.json");
//        PApplet.runSketch(new String[]{"App"}, app);
//
//        // Setup delay
//        app.delay(1000);
//
//        // Move left
//        app.keyPressed(new KeyEvent(null, 0, 0, 0, ' ', 37));
//
//        assertEquals(1, app.getTanksAlive().size(), "Incorrect number of alive tanks.");
//    }


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
