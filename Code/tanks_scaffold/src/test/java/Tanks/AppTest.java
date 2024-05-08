package Tanks;

import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {
    private final ConfigManagerTest get = new ConfigManagerTest();

    /**
     * This test is created to cover the case when players died
     * at the same time - no players alive left in the current
     * level.
     * Then, we won't draw the HUD anymore.
     */
    @Test
    public void coverHUDWhenPlayersDiedSameTime(){
        get.printPrompt("testCoverHUDWhenPlayersDiedSameTime", false);

        App app = new App();
        app.loop();
        app.setConfigPath("additionalFiles/testMap.json");
        PApplet.runSketch(new String[]{"App"}, app);

        // Setup delay
        app.delay(2000);

        // Set up the tanks
        // Set initial health of each tank = 20
        for (Tank tank : app.getTanksAlive()){
            tank.tankLoseHP(80);
        }

        // Take turns shooting
        for (int i = 0; i < 2; i ++){
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
}
