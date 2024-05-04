package Tanks;


import org.junit.jupiter.api.Test;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class GameMapTest {
    // Getting the prompting function in the other class
    private final ConfigManagerTest get = new ConfigManagerTest();
    private final GameMap testMap = new GameMap();

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
     * Test the instantiateHeight() method with valid input.
     * This test verifies that all the terrain pixels are
     * instantiated correctly.
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
     * Tests the extractTree() function with valid input
     * This verifies tree root locations are extracted correctly.
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
        PApplet app = new PApplet();
        app.randomSeed(0);

        testMap.extractTree(app);
        ArrayList<Integer> treeX = testMap.getTreeX();

        assertEquals(118, treeX.get(0), "Wrong root location of 0-position tree");
        assertEquals(153, treeX.get(1), "Wrong root location of 1-position tree");
        assertEquals(754, treeX.get(8), "Wrong root location of 8-position tree");
        System.out.println("testExtractTree passed");
    }

    /**
     * Tests the extractTanks() function with valid input
     * This verifies the tank objects are extracted correctly,
     * which got automatically called inside.
     */
    @Test
    public void testExtractTank(){
        get.printPrompt("testExtractTank", false);
        String fileName = "additionalFiles/layoutTest/validLayout.txt";

        HashMap<String, int[]> colors = new HashMap<>();
        colors.put("A", new int[]{0, 0, 255});
        colors.put("B", new int[]{255,0,0});
        colors.put("C", new int[]{0,255,255});
        colors.put("D", new int[]{255,255,0});

        //Instantiate
        testMap.generateMapFromConfig(testMap.getBoard(), fileName);
        testMap.instantiateHeight();
        testMap.extractTanks(colors);
        testMap.updateTanksY();

        ArrayList<Tank> tanks = testMap.getTanksList();

        assertEquals("A", tanks.get(0).type, "Incorrect tank type");
        assertEquals("B", tanks.get(1).type, "Incorrect tank type");
        assertEquals(384, tanks.get(0).yPos, "Incorrect tank y-position");
        assertEquals(320, tanks.get(1).yPos, "Incorrect tank y-position");
        System.out.println("testExtractTank passed");
    }

    /**
     * This test is to add the coverage of the draw functions
     * in GameMap including drawTerrain(), drawTrees()
     */

    // SOME OTHER RENDER FUNCTIONS SUCH AS drawTurret(), drawTanks(), drawHUD()
    // are also covered, and since the draw functions are not required,
    // so I will keep it here.

    @Test
    public void addRenderFunctions(){
        get.printPrompt("addRenderFunction", false);

        // Create a new sketch
        App app = new App();
        PApplet.runSketch(new String[] {"App"}, app);
        // Set to a custom map
        // This map has a tree with cell-X at index 0, this is to
        // test whether the image of the tree has been drawn correctly.
        app.setConfigPath("additionalFiles/testMap.json");
        app.setup();


        System.out.println("Successfully add the render functions to coverage.");
    }

    /**
     * This test is to add the coverage of the draw functions
     * in GameMap including drawTerrain(), drawTrees(),which
     * get automatically called inside.
     */
    @Test
    public void anotherRenderTest(){
        get.printPrompt("anotherRenderTest", false);

        // Create a new sketch
        App app = new App();
        PApplet.runSketch(new String[] {"App"}, app);
        // Set to a custom map
        // This map is similar to level 1.
        app.setConfigPath("additionalFiles/configtest.json");
        app.setup();

        System.out.println("Successfully add the render functions to coverage.");
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
