package Tanks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LevelTest {
    /**
     * Tests the Level class's constructor with valid input.
     * This test includes verifying the attributes (layout, background,
     * foregroundColour, trees) and test the class's Constructor.
     */

    @Test
    public void testLevelInit() {
        ConfigManagerTest get = new ConfigManagerTest();
        get.printPrompt("testLevelConfigInit", false);
        // Instantiate
        String layout = "layout.txt";
        String background = "background.txt";
        String foregroundColor = "255,255,255";
        String trees = "tree.png";
        Level level = new Level(layout, background, foregroundColor, trees);

        // Test
        assertEquals(layout, level.getLayoutFilePath(), "Wrong layout return value");
        assertEquals(background, level.getBackground(), "Wrong background return value");
        assertArrayEquals(new int[]{255, 255, 255}, level.getForegroundColour(),
                "Wrong terrain color return value");
        assertEquals(trees, level.getTrees(), "Wrong trees return value");
        System.out.println("testLevelConfigInit passed");
    }
}
