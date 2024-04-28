package Tanks;

public class GameObject {
    protected int xPos;
    protected float yPos;
    protected String type;

    /** Creates a game Object.
     * @param x object's X position.
     * @param y object's Y position.
     * @param type the object's type - represent by a character.
     */

    public GameObject(int x, float y, String type){
        xPos = x;
        yPos = y;
        this.type = type;
    }
}