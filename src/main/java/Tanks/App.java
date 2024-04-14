package Tanks;

import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;
import java.util.List;

public class App extends PApplet {

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;
    public static final int CELLAVG = 32;
    public static final int TOPBAR = 0;
    public static int WIDTH = 864; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;
    public static final int FPS = 30;
    public static Random random = new Random();



    public static final int INITIAL_PARACHUTES = 1;
    public String configPath;
    public GameObject[][] board;
    public int[] foregroundColor;
    public int[] pixels;
    public ArrayList<Integer> treeX = new ArrayList<>();
    public HashMap<Tank, Integer> tankHeights = new HashMap<>();
    public PImage trees;

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
		//loadImage(this.getClass().getResource(filename).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));

        ConfigManager manager = ConfigLoader.loadConfig(configPath);

        // Setup map
        List<LevelConfig> levels = manager.getLevels();
        HashMap<String, int[]> colors = manager.getPlayerColours();

        LevelConfig level = levels.get(0);
        String layout = level.getLayoutFilePath();

        // attributes
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
                }
            }
        }
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        if (event.getKeyCode() == UP) {
        } else if (event.getKeyCode() == DOWN) {
        } else if (event.getKeyCode() == LEFT) {
        } else if (event.getKeyCode() == RIGHT) {
        } else if (event.getKey() == ' ') {
        }
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


    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        stroke(foregroundColor[0],foregroundColor[1],foregroundColor[2]);
        for (int i = 0; i < 864; i++){
            line(i, HEIGHT, i, pixels[i]);
        }

        for (int i = 0; i <treeX.size(); i++){
            // The X point, Y point has to scaled -16 and -32 respectively
            // since the img is drawn from top-left corner.
            if (i == 0 && treeX.get(0) -16 <0)
                image(trees, 0,pixels[0] -32);
            else
                image(trees, treeX.get(i) - 16 , pixels[treeX.get(i)] -32);
            //  MIGHT HAVE TO HANDLE Y
        }

        for (Tank tank : tankHeights.keySet()){
            drawTank(tankHeights.get(tank),
                    pixels[tankHeights.get(tank)],
                    tank.getColorTank());
        }
        //----------------------------------
        //display HUD:
        //----------------------------------
        //TODO
        

        //----------------------------------
        //display scoreboard:
        //----------------------------------
        //TODO
        
		//----------------------------------
        //----------------------------------

        //TODO: Check user action
    }

    void drawTank(float x, float y, int[] rgb) {
//        float tankWidth = 32;
//        float tankHeight = 32;
//
//        // Draw lower rectangle (tank body)
//        rectMode(CENTER);
//        fill(rgb[0], rgb[1], rgb[2]);
//        rect(x, y, tankWidth, tankHeight);
//
//        // Draw upper rectangle (turret)
//        float turretWidth = 25.6f;
//        float turretHeight = 25.6f;
//        float turretY = y - 22.4f;
//        fill(rgb[0], rgb[1], rgb[2]);
//        rect(x, turretY, turretWidth, turretHeight);
        stroke(0); // Set stroke color to black
        point(x, y);
    }
    public void setUpLevels(LevelConfig level){
        // Set level's background
        if (level.getBackground() != null) {
            background(loadImage(this.getClass().getResource(level.getBackground()).
                    getPath().toLowerCase(Locale.ROOT).replace("%20", " ")));
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

    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }

}
