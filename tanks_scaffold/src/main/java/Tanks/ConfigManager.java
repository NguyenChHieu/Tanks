package Tanks;
import java.util.*;
public class ConfigManager {
    /** Holds the JSON file's levels, player colours
     * after extracted.
     */
    private List<LevelConfig> levels;
    private HashMap<String, String> playerColours;

    /** Creates a level Object.
     * @param levels List contain level objects.
     * @param playerColours HashMap contains "player tag - color" pairs.
     */
    public ConfigManager(List<LevelConfig> levels,
                         HashMap<String, String> playerColours){
        this.levels = levels;
        this.playerColours = playerColours;
    }

    /** Gets level list.
     * @return list contain levels in JSON file.
     */
    public List<LevelConfig> getLevels() {
        return levels;
    }

    /** Gets player colours.
     * @return HashMap contains "player tag - RGB" pairs.
     */
    public Map<String, String> getPlayerColours() {
        return playerColours;
    }
}
