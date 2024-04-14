package Tanks;

public class Tank extends GameObject{
    private int health = 100;
    private int power = 50;
    private int fuel = 250;
    private int[] colorTank;

    public Tank(int xPos, int yPos, String type){
        super(xPos, yPos, type);
    }
    public void setColorTank(int[] rgb){
        colorTank = rgb;
    }

    public int[] getColorTank(){
        return colorTank;
    }

}
