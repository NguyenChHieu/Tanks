package Tanks;

import org.json.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class ConfigLoader {
    /** Load the JSON file's attributes into
     * ConfigManager object.
     */
//    private static final String CONFIG_FILENAME = "config.json";

    /**
     * Parse the JSON content into the ConfigManager object,
     * consisting a level's attributes and Player x AI tank colors
     * @param configFileName Configuration file name.
     * @return ConfigManager object
     */
    public static ConfigManager loadConfig(String configFileName){
        File file = new File(configFileName);
        try {
            // Parse the JSON file.
            String content = new String(Files.readAllBytes(file.toPath()));

            // Check if the JSON file is in valid format
            try{
                JSONObject config = new JSONObject(content);
            }
            catch (JSONException e){
                System.out.println("Invalid JSON format: " + e.getMessage());
                return null;
            }
            // Create the JSON object
            JSONObject config = getJSONObject(content);
            if (config == null){
                return null;
            }

            // Get the items specified in JSON file
            JSONArray levelsArray =  config.getJSONArray("levels");
            JSONObject playerColoursObj = config.getJSONObject("player_colours");

            // Create a list to hold extracted items.
            List<LevelConfig> levels = new ArrayList<>();
            HashMap<String, String> playerColours = new HashMap<>();

            // Get the level's attributes and match it to the level object
            for (Object level : levelsArray){
                JSONObject levelObj = (JSONObject) level;
                String layoutFile = (String) levelObj.get("layout");
                String background = (String) levelObj.get("background");
                String foregroundColour = (String) levelObj.get("foreground-colour");
                String trees = levelObj.has("trees") ? (String) levelObj.get("trees") : null;
                levels.add(new LevelConfig(layoutFile, background, foregroundColour, trees));
            }
            // Get the player color - RGB pairs
            for (String player: playerColoursObj.keySet()){
                playerColours.put(player, playerColoursObj.get(player).toString());
            }

            return new ConfigManager(levels, playerColours);
        }
        catch (IOException e){
            System.out.println("Unable to read file.");
            return null;
        }
    }

    /**
     * Check validity of the JSON file. Verifies that a
     * JSON file must have levels and player_colours key.
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
}
