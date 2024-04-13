package Tanks;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConfigLoaderTest {
    // helper function
    public void printPrompt(String testName, boolean specifyOutput){
        System.out.println("______");
        System.out.println("Begin: " + testName);
        if (specifyOutput)
            System.out.println("Output: ");
    }

    // VALID
    /**
     * Tests the loadConfiguration (ConfigLoader) with valid JSON input.
     * This test verifies that the configuration is correctly loaded from the provided JSON string.
     */
    @Test
    public void testLoadConfigurationValid() {
        printPrompt("testLoadConfigurationValid", false);
        String configJson = "config.json";
        ConfigManager actual = ConfigLoader.loadConfig(configJson);

        int expectedNumberOfLevels = 3;
        assert actual != null;
        int actualNumberOfLevels = actual.getLevels().size();
        int expectedNumberOfColours = 19;
        int actualNumberOfColours = actual.getPlayerColours().size();

        assertNotNull(actual, "ConfigManager should not be null");
        assertEquals(expectedNumberOfLevels, actualNumberOfLevels,
                "Expected number of levels");
        assertEquals(expectedNumberOfColours, actualNumberOfColours,
                "Expected number of player colors");
        System.out.println("testLoadConfigurationValid passed");
    }

    /**
     * Tests the loadConfiguration (ConfigLoader) with valid JSON input, but on a different order
     * of levels 1-> 3 ->2.
     * This test verifies that the configuration is correctly loaded from the provided JSON string.
     */
    @Test
    public void testLoadConfigurationValidDifferentOrder() {
        printPrompt("testLoadConfigurationValidDifferentOrder", false);
        String configJson = "additionalFiles/validDifferentOrder.json";
        ConfigManager actual = ConfigLoader.loadConfig(configJson);

        int expectedNumberOfLevels = 3;
        assert actual != null;
        int actualNumberOfLevels = actual.getLevels().size();
        int expectedNumberOfColours = 5;
        int actualNumberOfColours = actual.getPlayerColours().size();

        assertNotNull(actual, "ConfigManager should not be null");
        assertEquals(expectedNumberOfLevels, actualNumberOfLevels,
                "Expected number of levels");
        assertEquals(expectedNumberOfColours, actualNumberOfColours,
                "Expected number of player colors");
        System.out.println("testLoadConfigurationValidDifferentOrder passed");
    }

    // INVALID
    /**
     * Tests the loadConfiguration (ConfigLoader) with non-existent JSON input.
     * This test verifies that the method handles the invalid input text file correctly.
     */
    @Test
    public void testLoadConfigurationInvalidNonExist() {
        printPrompt("testLoadConfigurationInvalidNonExist", true);
        String configJson = "lmao";
        ConfigManager actual = ConfigLoader.loadConfig(configJson);

        assertNull(actual, "Expected null type");
        System.out.println("testLoadConfigurationInvalidNonExist() passed");
    }

    /**
     * Tests the loadConfiguration (ConfigLoader) with empty JSON input.
     * This test verifies that the method handles the invalid input text file correctly.
     */
    @Test
    public void testLoadConfigurationInvalidEmpty(){
        printPrompt("testLoadConfigurationInvalidEmpty", true);
        String configJson = "additionalFiles/empty.json";
        ConfigManager actual = ConfigLoader.loadConfig(configJson);
        assertNull(actual,"Expected null type");
        System.out.println("testLoadConfigurationInvalidEmpty passed");
    }

    /**
     * Tests the loadConfiguration (ConfigLoader) with invalid JSON input - missing "levels" label.
     * This test verifies that the method handles the invalid input text file correctly.
     */
    @Test
    public void testLoadConfigurationInvalidLevels(){
        printPrompt("testLoadConfigurationInvalidLevels", true);
        String config = "additionalFiles/noLevelsFound.json";
        ConfigManager actual = ConfigLoader.loadConfig(config);

        assertNull(actual, "Missing levels label in JSON file should be handled.");
        System.out.println("testLoadConfigurationInvalidLevels passed");
    }

    /**
     * Tests the loadConfiguration (ConfigLoader) with invalid JSON input - missing "player-colours" label.
     * This test verifies that the method handles the invalid input text file correctly.
     */
    @Test
    public void testLoadConfigurationInvalidColours(){
        printPrompt("testLoadConfigurationInvalidColours", true);
        String config = "additionalFiles/noColoursFound.json";
        ConfigManager actual = ConfigLoader.loadConfig(config);

        assertNull(actual, "Missing player-colours label in JSON file should be handled.");
        System.out.println("testLoadConfigurationInvalidColours passed");
    }
}
