package Tanks;

public class Tank extends GameObject{
    private int health;
    private int power;
    private int fuel;
    private String colorTank;

    public Tank(int xPos, int yPos, String type){
        super(xPos, yPos, type);
    }


}
