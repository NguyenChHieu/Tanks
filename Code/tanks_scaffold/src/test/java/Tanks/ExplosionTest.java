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
}
