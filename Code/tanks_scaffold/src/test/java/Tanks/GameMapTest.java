//package Tanks;
//
//
//import com.sun.org.apache.xpath.internal.operations.Equals;
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class GameMapTest {
//    // Getting the prompting function in the other class
//    private final ConfigLoaderTest get = new ConfigLoaderTest();
//
//    // POSITIVE
//    /**
//     * Tests the generateTerrain() method with valid text file input.
//     * This test verifies that the content from the txt file got
//     * extracted correctly to a 28x20 matrix.
//     */
//    @Test
//    public void testGenerateTerrainValid() {
//        get.printPrompt("testGenerateTerrainValid", false);
//
//        GameObject[][] map = new GameObject[20][28];
//        String fileName = "additionalFiles/layoutTest/validLayout.txt";
//
//        GameObject[][] generatedMap = GameMap.generateTerrain(map, fileName, );
//
//        assertNotNull(generatedMap);
//        assertEquals(20, generatedMap.length, "Wrong number of rows");
//        assertEquals(28, generatedMap[0].length, "Wrong number of columns.");
//        // Some checks on the map's contents
//        assertEquals("T", generatedMap[10][3].type, "Map wasn't extracted correctly");
//        assertEquals("X", generatedMap[10][13].type, "Map wasn't extracted correctly");
//        assertEquals("T", generatedMap[11][19].type, "Map wasn't extracted correctly");
//        System.out.println("testGenerateTerrainValid passed");
//    }
//
//    /**
//     * Tests the generateTerrain() method with valid text file
//     * input but the length of the text fine is less than 20.
//     * This test verifies that the content from the txt file got
//     * extracted correctly to a 28x20 matrix, with the missing lines
//     * filled correctly.
//     */
//    @Test
//    public void testGenerateTerrainValidMissingLines() {
//        get.printPrompt("testGenerateTerrainValidMissingLines", false);
//
//        GameObject[][] map = new GameObject[20][28];
//        String fileName = "additionalFiles/layoutTest/validLayout.txt";
//
//        GameObject[][] generatedMap = GameMap.generateTerrain(map, fileName);
//
//        // Test
//        assertNotNull(generatedMap);
//        assertEquals(20, generatedMap.length, "Wrong number of rows");
//        assertEquals(28, generatedMap[0].length, "Wrong number of columns.");
//
//        // Some checks on the map's contents
//        assertEquals("T", generatedMap[10][3].type, "Map wasn't extracted correctly");
//        assertEquals("X", generatedMap[10][13].type, "Map wasn't extracted correctly");
//        assertEquals("T", generatedMap[11][19].type, "Map wasn't extracted correctly");
//        System.out.println("testGenerateTerrainValidMissingLines passed");
//    }
//
//    /**
//     * Tests the findColHeight() method with valid input.
//     * This test verifies that the height of the terrain pixel
//     * gets successfully scaled up 32 times.
//     */
//    @Test
//    public void testFindColHeight(){
//        get.printPrompt("testFindColHeight", false);
//        int row = 5;
//        int height = GameMap.findColHeight(row);
//        assertEquals(160, height, "Wrong value of pixel height");
//        System.out.println("testFindColHeight passed");
//    }
//
//    /**
//     * Test
//     */
//    @Test
//    public void testInstantiateHeight(){
//        get.printPrompt("testInstantiateHeight", false);
//
//        GameObject[][] map = new GameObject[20][28];
//        String fileName = "additionalFiles/layoutTest/validLayout.txt";
//
//        GameObject[][] generatedMap = GameMap.generateTerrain(map, fileName);
//
//    }
//
//    /**
//     *  Tests the movingAverage() method with valid input.
//     *  This test verifies that the height terrain pixels
//     *  gets successfully calculated as the mean of the next 32px (self incl.)
//     */
//    @Test
//    public void testMovingAverage(){
//        get.printPrompt("testMovingAverage", false);
//
//        //Instantiate
//        int[] heightPixels = new int[896];
//
//    }
//
//    // NEGATIVE
//    /**
//     * Tests the generateTerrain() method with invalid text file input.
//     * This test verifies that the method correctly deals with the non-existent
//     * or unreadable files.
//     */
//    @Test
//    public void testGenerateTerrainInvalidFileNotFound() {
//        get.printPrompt("testGenerateTerrainInvalidFileNotFound", true);
//
//        GameObject[][] map = new GameObject[20][28];
//        String fileName = "non-existent";
//
//        GameObject[][] generatedMap = GameMap.generateTerrain(map, fileName, );
//
//        assertNull(generatedMap, "Handle unreadable .txt files.");
//        System.out.println("testGenerateTerrainInvalidFileNotFound passed");
//    }
//
//}
