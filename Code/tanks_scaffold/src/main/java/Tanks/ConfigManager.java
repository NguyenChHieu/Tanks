package Tanks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ConfigManager {
    private final List<Level> LEVELS;
    private final HashMap<String, int[]> PLAYER_COLORS;

    /**
     * Creates a ConfigManager object.
     *
     * @param levels        List contain level objects.
     * @param playerColours HashMap contains "player tag - color" pairs.
     */
    public ConfigManager(List<Level> levels,
                         HashMap<String, int[]> playerColours) {
        this.LEVELS = levels;
        this.PLAYER_COLORS = playerColours;
    }

    /**
     * Parse the JSON content into the ConfigManager object,
     * consisting a level's attributes and Player x AI tank colors
     *
     * @param configFileName Configuration file name.
     * @return ConfigManager object
     */
    public static ConfigManager loadConfig(String configFileName) {
        Random rand = new Random();
        File file = new File(configFileName);

        try {
            // Parse the JSON file.
            String content = new String(Files.readAllBytes(file.toPath()));

            // Check if the JSON file is in valid format
            JSONObject config;
            try {
                new JSONObject(content);
            } catch (JSONException e) {
                System.out.println("Invalid JSON format: " + e.getMessage());
                return null;
            }
            // Create the JSON object
            config = getJSONObject(content);
            if (config == null) {
                return null;
            }

            // Get the items specified in JSON file
            JSONArray levelsArray = config.getJSONArray("levels");
            JSONObject playerColoursObj = config.getJSONObject("player_colours");

            // Create a list to hold extracted items.
            List<Level> levels = new ArrayList<>();
            HashMap<String, int[]> playerColours = new HashMap<>();

            // Get the level's attributes and match it to the level object
            for (Object level : levelsArray) {
                JSONObject levelObj = (JSONObject) level;
                String layoutFile = (String) levelObj.get("layout");
                String background = (String) levelObj.get("background");
                String foregroundColour = (String) levelObj.get("foreground-colour");
                String trees = levelObj.has("trees") ? (String) levelObj.get("trees") : null;
                levels.add(new Level(layoutFile, background, foregroundColour, trees));
            }
            // Get the player color - RGB pairs
            for (String player : playerColoursObj.keySet()) {

                // Parse the RGB into int[]
                int[] rgbExtracted = new int[3];
                String str = playerColoursObj.get(player).toString();

                // Randomize colors
                if (str.equals("random")) {
                    rgbExtracted[0] = rand.nextInt(256); // Red component (0-255)
                    rgbExtracted[1] = rand.nextInt(256); // Green component (0-255)
                    rgbExtracted[2] = rand.nextInt(256);
                } else {
                    String[] rgb = str.split(",");
                    for (int i = 0; i < 3; i++) {
                        rgbExtracted[i] = Integer.parseInt(rgb[i]);
                    }
                }

                playerColours.put(player, rgbExtracted);
            }

            return new ConfigManager(levels, playerColours);
        } catch (IOException e) {
            System.out.println("Unable to read file.");
            return null;
        }
    }

    /**
     * Check validity of the JSON file. Verifies that a
     * JSON file must have levels and player_colours key.
     *
     * @param content JSON file content
     * @return JSON file content
     */
    private static JSONObject getJSONObject(String content) {
        JSONObject config = new JSONObject(content);

        // Check if the "levels" key is present
        if (!config.has("levels")) {
            System.out.println("Missing 'levels' key in configuration file.");
            return null;
        }
        // Check if the "player_colours" key is present
        if (!config.has("player_colours")) {
            System.out.println("Missing 'player_colours' key in configuration file.");
            return null;
        }
        return config;
    }


    // GETTERS

    /**
     * Gets level list.
     *
     * @return list contain levels in JSON file.
     */
    public List<Level> getLevels() {
        return LEVELS;
    }

    /**
     * Gets player colours.
     *
     * @return HashMap contains "player tag - RGB" pairs.
     */
    public HashMap<String, int[]> getPlayerColours() {
        return PLAYER_COLORS;
    }
}