package Tanks;


import org.junit.jupiter.api.Test;
import processing.core.PApplet;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameMapTest {
    // Getting the prompting function in the other class
    private final ConfigManagerTest get = new ConfigManagerTest();
    private final GameMap testMap = new GameMap();
    private final PApplet app = new PApplet();


    // POSITIVE
    /**
     * Tests the generateTerrain() method with valid text file input.
     * This test verifies that the content from the txt file got
     * extracted correctly to a 28x20 matrix.
     */
    @Test
    public void testGenerateTerrainValid() {
        get.printPrompt("testGenerateTerrainValid", false);

        String fileName = "additionalFiles/layoutTest/validLayout.txt";

        testMap.generateMapFromConfig(testMap.getBoard(), fileName);
        GameObject[][] board = testMap.getBoard();

        assertNotNull(testMap);
        assertEquals(20, board.length, "Wrong number of rows");
        assertEquals(28, board[0].length, "Wrong number of columns.");
        // Some checks on the map's contents
        assertEquals("T", board[10][3].type, "Map wasn't extracted correctly");
        assertEquals("X", board[10][13].type, "Map wasn't extracted correctly");
        assertEquals("T", board[11][19].type, "Map wasn't extracted correctly");
        System.out.println("testGenerateTerrainValid passed");
    }

    /**
     * Tests the generateTerrain() method with valid text file
     * input but the length of the text fine is less than 20.
     * This test verifies that the content from the txt file got
     * extracted correctly to a 28x20 matrix, with the missing lines
     * filled correctly.
     */
    @Test
    public void testGenerateTerrainValidMissingLines() {
        get.printPrompt("testGenerateTerrainValidMissingLines", false);

        String fileName = "additionalFiles/layoutTest/layoutMissingLines.txt";

        testMap.generateMapFromConfig(testMap.getBoard(), fileName);
        GameObject[][] board = testMap.getBoard();

        // Test
        assertNotNull(board);
        assertEquals(20, board.length, "Wrong number of rows");
        assertEquals(28, board[0].length, "Wrong number of columns.");

        // Some checks on the map's contents to see if they extracted correctly
        assertEquals("X", board[5][1].type, "Map wasn't extracted correctly");
        assertEquals("X", board[6][2].type, "Map wasn't extracted correctly");
        assertEquals("X", board[7][0].type, "Map wasn't extracted correctly");
        assertEquals("A", board[5][2].type, "Map wasn't extracted correctly");
        assertEquals("1", board[6][0].type, "Map wasn't extracted correctly");
        System.out.println("testGenerateTerrainValidMissingLines passed");
    }


    /**
     * Test the instantiateHeight
     */
    @Test
    public void testInstantiateHeight(){
        get.printPrompt("testInstantiateHeight", false);

        String fileName = "additionalFiles/layoutTest/validLayout.txt";

        testMap.generateMapFromConfig(testMap.getBoard(), fileName);
        testMap.instantiateHeight();

        assertEquals(416, testMap.getPixels()[0],
                "Terrain height wasn't instantiated correctly");
        assertEquals(352, testMap.getPixels()[32],
                "Terrain height wasn't instantiated correctly");
        System.out.println("testInstantiateHeight passed");
    }

    /**
     *  Tests the movingAverage() method with valid input.
     *  This test verifies that the height terrain pixels
     *  gets successfully calculated as the mean of the next 32px (self incl.)
     */
    @Test
    public void testMovingAverage(){
        get.printPrompt("testMovingAverage", false);

        String fileName = "additionalFiles/layoutTest/validLayout.txt";

        //Instantiate
        testMap.generateMapFromConfig(testMap.getBoard(), fileName);
        testMap.instantiateHeight();
        testMap.movingAverage(testMap.getPixels());

        assertEquals(414, testMap.getPixels()[1],
                "Terrain height wasn't instantiated correctly");
        assertEquals(412, testMap.getPixels()[2],
                "Terrain height wasn't instantiated correctly");
        System.out.println("testMovingAverage passed");

    }

    /**
     * Tests the extractTree() function
     */
    @Test
    public void testExtractTree(){
        get.printPrompt("testExtractTree", false);
        String fileName = "additionalFiles/layoutTest/validLayout.txt";

        //Instantiate
        testMap.generateMapFromConfig(testMap.getBoard(), fileName);
        testMap.instantiateHeight();
        testMap.movingAverage(testMap.getPixels());

        // Set random seed
        app.randomSeed(0);

        testMap.extractTree(app);
        ArrayList<Integer> treeX = testMap.getTreeX();

        assertEquals(118, treeX.get(0), "Wrong root location of 0-position tree");
        assertEquals(153, treeX.get(1), "Wrong root location of 1-position tree");
        assertEquals(754, treeX.get(8), "Wrong root location of 8-position tree");
        System.out.println("testExtractTree passed");
    }

    // NEGATIVE
    /**
     * Tests the generateTerrain() method with invalid text file input.
     * This test verifies that the method correctly deals with the non-existent
     * or unreadable files.
     */
    @Test
    public void testGenerateTerrainInvalidFileNotFound() {
        get.printPrompt("testGenerateTerrainInvalidFileNotFound", true);

        String fileName = "non-existent";

        testMap.generateMapFromConfig(testMap.getBoard(),fileName);

        assertNull(testMap.getBoard(), "Handle unreadable .txt files.");
        System.out.println("testGenerateTerrainInvalidFileNotFound passed");
    }

}
