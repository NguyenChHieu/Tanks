package Tanks;

public class Level {
    /** Matching level's attributes for better modification
     */
    private final String layoutFilePath;
    private final String background;
    private final int[] foregroundColour;
    private final String trees;

    /** Creates a level Object.
     * @param layout The level's layout file name.
     * @param background The level’s background.
     * @param foregroundColour The terrain's RGB color - string parsed to an int[]
     * @param trees (If exist) The level's graphics for trees.
     */
    public Level(String layout, String background,
                 String foregroundColour, String trees){
        layoutFilePath = layout;
        this.background = background;
        this.trees = trees;

        // Parse the string into an RGB array
        String[] fgColorSplit = foregroundColour.split(",");
        int[] fgColor = new int[3];
        for(int i = 0; i < 3; i++ ){
            fgColor[i] = Integer.parseInt(fgColorSplit[i]);
        }
        this.foregroundColour = fgColor;
    }

    /** Gets the level's layout file name.
     * @return layout filename.
     */
    public String getLayoutFilePath(){
        return layoutFilePath;
    }

    /** Gets the level’s background.
     * @return background img.
     */
    public String getBackground() {
        return background;
    }

    /** Gets level's terrain color.
     * @return terrain RGB color array.
     */
    public int[] getForegroundColour() {
        return foregroundColour;
    }

    /** Gets the level's graphics for trees.
     * @return tree's graphics.
     */
    public String getTrees() {
        return trees;
    }
}