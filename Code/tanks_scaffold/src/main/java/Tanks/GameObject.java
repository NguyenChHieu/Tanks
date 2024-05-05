package Tanks;

/**
 * Parent class of in-game objects
 * such as Tanks, trees, air
 */
public class GameObject {
    /** X-position of the object */
    protected int xPos;
    /** Y-position of the object */
    protected float yPos;
    /** Type of the object, represented by a letter */
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