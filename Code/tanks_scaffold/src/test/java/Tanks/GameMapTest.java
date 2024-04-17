package Tanks;


import com.sun.org.apache.xpath.internal.operations.Equals;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameMapTest {
    // Getting the prompting function in the other class
    private final ConfigLoaderTest get = new ConfigLoaderTest();

    // POSITIVE
    /**
     * Tests the generateTerrain() method with valid text file input.
     * This test verifies that the content from the txt file got
     * extracted correctly to a 28x20 matrix.
     */
    @Test
    public void testGenerateTerrainValid() {
        get.printPrompt("testGenerateTerrainValid", false);

        // Instantiate
        GameObject[][] map = new GameObject[20][28];
        String fileName = "additionalFiles/layoutTest/validLayout.txt";

        // Act
        GameObject[][] generatedMap = GameMap.generateTerrain(map, fileName);

        // Test
        assertNotNull(generatedMap);
        assertEquals(20, generatedMap.length, "Wrong number of rows");
        assertEquals(28, generatedMap[0].length, "Wrong number of columns.");
        System.out.println("testGenerateTerrainValid passed");
    }

    /**
     * Tests the findColHeight() method with valid input.
     * This test verifies that the height of the terrain pixel
     * gets successfully scaled up 32 times.
     */
    @Test
    public void testFindColHeight(){
        // Instantiate
        int row = 5;

        // Act
        int height = GameMap.findColHeight(row);

        // Assert
        assertEquals(160, height, "Wrong value of pixel height");
    }

    /**
     *
     */
    @Test
    public void testInstantiateHeight(){
        GameObject[][] map = new GameObject[20][28];
        String fileName = "additionalFiles/layoutTest/validLayout.txt";

        // Act
        GameObject[][] generatedMap = GameMap.generateTerrain(map, fileName);
    }

    /**
     *  Tests the movingAverage() method with valid input.
     *  This test verifies that the height terrain pixels
     *  gets successfully calculated as the mean of the next 32px (self incl.)
     */
    @Test
    public void testMovingAverage(){
        //Instantiate
        int[] heightPixels = new int[896];

    }
}
