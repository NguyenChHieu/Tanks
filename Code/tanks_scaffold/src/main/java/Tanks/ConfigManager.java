package Tanks;
import java.util.*;
public class ConfigManager {
    private final List<Level> levels;
    private final HashMap<String, int[]> playerColours;

    /** Creates a ConfigManager object.
     * @param levels List contain level objects.
     * @param playerColours HashMap contains "player tag - color" pairs.
     */
    public ConfigManager(List<Level> levels,
                         HashMap<String, int[]> playerColours){
        this.levels = levels;
        this.playerColours = playerColours;
    }

    /** Gets level list.
     * @return list contain levels in JSON file.
     */
    public List<Level> getLevels() {
        return levels;
    }

    /** Gets player colours.
     * @return HashMap contains "player tag - RGB" pairs.
     */
    public HashMap<String, int[]> getPlayerColours() {
        return playerColours;
    }
}