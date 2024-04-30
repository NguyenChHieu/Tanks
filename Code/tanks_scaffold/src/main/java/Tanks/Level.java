package Tanks;

public class Level {
    /** Matching level's attributes for better modification
     */
    private final String LAYOUT_FILE_PATH;
    private final String BACKGROUND;
    private final int[] FOREGROUND_COLOR;
    private final String TREES_IMG_PATH;

    /** Creates a level Object.
     * @param layout The level's layout file name.
     * @param background The level’s background.
     * @param foregroundColour The terrain's RGB color - string parsed to an int[]
     * @param trees (If exist) The level's graphics for trees.
     */
    public Level(String layout, String background,
                 String foregroundColour, String trees){
        LAYOUT_FILE_PATH = layout;
        this.BACKGROUND = background;
        this.TREES_IMG_PATH = trees;

        // Parse the string into an RGB array
        String[] fgColorSplit = foregroundColour.split(",");
        int[] fgColor = new int[3];
        for(int i = 0; i < 3; i++ ){
            fgColor[i] = Integer.parseInt(fgColorSplit[i]);
        }
        this.FOREGROUND_COLOR = fgColor;
    }

    /** Gets the level's layout file name.
     * @return layout filename.
     */
    public String getLayoutFilePath(){
        return LAYOUT_FILE_PATH;
    }

    /** Gets the level’s background.
     * @return background img.
     */
    public String getBackground() {
        return BACKGROUND;
    }

    /** Gets level's terrain color.
     * @return terrain RGB color array.
     */
    public int[] getForegroundColour() {
        return FOREGROUND_COLOR;
    }

    /** Gets the level's graphics for trees.
     * @return tree's graphics.
     */
    public String getTrees() {
        return TREES_IMG_PATH;
    }
}