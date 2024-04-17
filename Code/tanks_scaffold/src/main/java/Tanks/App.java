package Tanks;

import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;
import java.util.List;

public class App extends PApplet {

    public static final int CELL_SIZE = 32; //8;
    public static final int CELL_HEIGHT = 32;
    public static final int CELL_AVG = 32;
    public static final int TOP_BAR = 0;
    public static int WIDTH = 864; // CELL_SIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELL_SIZE+TOP_BAR;
    public static final int BOARD_WIDTH = WIDTH/CELL_SIZE;
    public static final int BOARD_HEIGHT = 20;
    public static final int FPS = 30;

    public static final int INITIAL_PARACHUTES = 1;

    //File attributes
    public String configPath;
    public int[] foregroundColor;
    public  PImage backgroundPNG;
    public PImage trees;

    // Map attributes
    public GameObject[][] board;
    public int[] pixels;
    public ArrayList<Integer> treeX = new ArrayList<>();
    public HashMap<Tank, Integer> tankHeights = new HashMap<>();

    // Gameplay attributes
    public List<Tank> order = new ArrayList<>();
    public boolean showArrow = true;
    public int arrStartTime = millis();

    // TODO: TESTING
    public App() {
        this.configPath = "config.json";
    }
    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
	@Override
    public void setup() {
        frameRate(FPS);

        ConfigManager manager = ConfigLoader.loadConfig(configPath);
        if (manager == null) {
            System.out.println("Error setting up attributes");
            return;
        }
        // Setup map
        List<LevelConfig> levels = manager.getLevels();
        HashMap<String, int[]> colors = manager.getPlayerColours();

        LevelConfig level = levels.get(0);
        String layout = level.getLayoutFilePath();
        setUpLevels(level);

        // Create board
        board = new GameObject[BOARD_HEIGHT][BOARD_WIDTH+1];
        board = GameMap.generateTerrain(board, layout);
        // Moving average
        pixels = GameMap.instantiateHeight(board);
        pixels = GameMap.movingAverage(pixels);
        pixels = GameMap.movingAverage(pixels);

        // Extract the trees from the matrix
        for (int c = 0; c < 28; c++) {
            for (int r = 0; r < 20; r++) {
                if (board[r][c].getType().equals("T")) {
                    // Randomize the location of the tree up to 30 pixels [-15,15]
                    int rand = (int) random(-15,15);
                    // At first, the cell is scaled to 32x32 matrix, hence the tree root
                    // pixel would be in the bottom left cell of the matrix. To align the
                    // root correctly, +16 to align the root to the center.
                    treeX.add(board[r][c].xPos + 16 + rand);
                }
                if (board[r][c] instanceof Tank){
                    int[] rgb = colors.get(board[r][c].type);
                    ((Tank) board[r][c]).setColorTank(rgb);
                    // Tank is alr scaled, according to the demo, no need to align to middle
                    tankHeights.put((Tank) board[r][c], board[r][c].xPos);
                    // The height of the tank will be 1 pixel above the terrain
                    board[r][c].yPos = pixels[board[r][c].xPos-1];
                }
            }
        }
        // Sort the tanks alphabetically in order
        order.addAll(tankHeights.keySet());
        Collections.sort(order);

        // Check
//        for (Tank tank: order){
//            System.out.println(tank.getType());
//        }
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        Tank currentTank = order.get(0);
        int key = event.getKeyCode();

        if (event.getKey() == ' '){
            switchTurns();

            // Update the conditions to display the arrow
            showArrow = true;
            arrStartTime = millis();

            // TODO: FIRE PROJECTILE
        }
        else if (key == LEFT || key == RIGHT) {
            if (currentTank.getFuelLevel() > 0) {
                currentTank.move(key);
                currentTank.yPos = pixels[currentTank.xPos - 1];

                currentTank.useFuel();
                // update new x
                tankHeights.put(currentTank, currentTank.xPos);
            }
        }
        else if (key == 'W' || key == 'S'){
            currentTank.updatePower(key);
        }

//        if (event.getKeyCode() == UP) {
//        } else if (event.getKeyCode() == DOWN) {

    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //TODO - powerups, like repair and extra fuel and teleport

        // TEST projectile
//        Projectile projectile = new Projectile(mouseX, mouseY)
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        // Clear old drawings
        background(backgroundPNG);
        // Terrain
        stroke(foregroundColor[0],foregroundColor[1],foregroundColor[2]);
        for (int i = 0; i < 864; i++){
            line(i, HEIGHT, i, pixels[i]);
        }
        // Tree
        for (int i = 0; i <treeX.size(); i++){
            // The X point, Y point has to scaled -16 and -32 respectively
            // since the img is drawn from top-left corner.
            if (i == 0 && treeX.get(0) -16 <0)
                image(trees, 0,pixels[0] -32);
            else
                image(trees, treeX.get(i) - 16 , pixels[treeX.get(i)] -32);
            //  MIGHT HAVE TO HANDLE Y
        }
        // Tank
        for (Tank tank : tankHeights.keySet()){
            // Tank center 1 px above terrain
            drawTank(tankHeights.get(tank),
                    pixels[tankHeights.get(tank)]-1,
                    tank.getColorTank());
        }
        //----------------------------------
        //display HUD:
        //----------------------------------
        drawHUD();
        // Indicate current player
        if (showArrow && millis() - arrStartTime < 2000)
            drawArrow(order.get(0).xPos, order.get(0).yPos - 100);
        else showArrow = false;

        //----------------------------------
        //display scoreboard:
        //----------------------------------
        //TODO
		//----------------------------------
        //----------------------------------

        //TODO: Check user action
//        System.out.println(frameRate);

    }

    void drawHUD(){
        if (order.isEmpty()){
            System.out.println("No players found");
            return;}

        // Player turn
        Tank current = order.get(0);

        String playerTurnText = "Player " + current.getType() + "'s turn";
        fill(0); // Black text
        textSize(16);
        textAlign(LEFT, TOP);
        text(playerTurnText, 10, 10);

        // Player's fuel
        PImage fuel = loadImage(this.getClass().getResource("fuel.png").
                getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        fuel.resize(20,20);
        image(fuel,160, 10);
            // Fuel level
        fill(0);
        textSize(16);
        text(current.getFuelLevel(), 190, 10);

        // Power
        String tankPowerText = "Power: " + current.getPowerLevel();
        fill(0);
        textSize(16);
        text(tankPowerText, 380, 35);

        //TODO: DISPLAY HEALTH BAR

        // Health bar
        fill(0);
        textSize(16);
        text("Health: ", 380, 10);

    }
    void drawArrow(int x, int y) {
        strokeWeight(4); // Bold line
        // body
        line(x, y - 25, x, y + 50);

        // head
        line(x - 15, y + 25, x, y + 50); // left
        line(x, y + 50, x + 15, y + 25); //right

        strokeWeight(1); // Reset to normal
    }
    void drawTank(float x, float y, int[] rgb) {
        // check center point
//        stroke(0);
//        point(x, y);

        // Black border
        stroke(0);
        fill(rgb[0], rgb[1], rgb[2]);
        // Lower
        rect(x-11, y-4, 21, 4,10);

        fill(rgb[0], rgb[1], rgb[2]);
        // Upper
        rect(x-8, y-8, 15, 4,10);

    }
    void drawProjectile(float x, float y, float radius, int[] rgb){
        fill(rgb[0], rgb[1], rgb[2]);
        ellipse(x, y, radius, radius);
    }
    public void setUpLevels(LevelConfig level){
        // Set level's background
        if (level.getBackground() != null) {
            backgroundPNG = loadImage(this.getClass().getResource(level.getBackground()).
                    getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
        }
        // Set level's terrain
        foregroundColor = level.getForegroundColour();

        // Set level's trees
        if (level.getTrees() != null) {
            trees = loadImage(this.getClass().getResource(level.getTrees()).
                    getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
            // Resize tree img
            trees.resize(32,32);
        }
    }
    // Leverage queue
    public void switchTurns(){
        Tank before = order.remove(0);
        order.add(before);
    }

    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }

}
