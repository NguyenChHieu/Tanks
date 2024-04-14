package Tanks;

public class GameObject {
    /** Parent class of all game objects
     */
    protected int xPos;
    protected int yPos;
    protected String type;

    /** Creates a game Object.
     * @param x object's X position.
     * @param y object's Y position.
     * @param type the object's type - represent by a character.
     */

    public GameObject(int x, int y, String type){
        xPos = x;
        yPos = y;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
