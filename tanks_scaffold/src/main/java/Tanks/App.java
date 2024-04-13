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

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 30;
    public String configPath;
    public GameObject[][] board;
    public int[] foregroundColor;
    public int[] pixels;
    public ArrayList<Integer> treeX = new ArrayList<>();
    public PImage trees;
    public static Random random = new Random();
	
	// Feel free to add any additional methods or attributes you want. Please put classes in different files.

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
		//See PApplet javadoc:
		//loadJSONObject(configPath)
		//loadImage(this.getClass().getResource(filename).getPath().toLowerCase(Locale.ROOT).replace("%20", " "));

        ConfigManager manager = ConfigLoader.loadConfig(configPath);

        // Setup map
        assert manager != null;
        List<LevelConfig> levels = manager.getLevels();
        LevelConfig level = levels.get(0);
        String layout = level.getLayoutFilePath();

        // attributes
//        background(loadImage("Tanks/" +level.getBackground()));
        background(loadImage(this.getClass().getResource(level.getBackground()).
                getPath().toLowerCase(Locale.ROOT).replace("%20", " ")));
        foregroundColor = level.getForegroundColour();
        if (level.getTrees() != null) {
            trees = loadImage(this.getClass().getResource(level.getTrees()).
                    getPath().toLowerCase(Locale.ROOT).replace("%20", " "));
            trees.resize(32,32);
        }


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
                    // randomize the location of the tree up to 30 pixels
                    int rand = (int) random(-15,15);
                    treeX.add(board[r][c].xPos + rand);
                }
            }
        }
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        
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
        // draw the cols:
        stroke(foregroundColor[0],foregroundColor[1],foregroundColor[2]);
        for (int i = 0; i < 864; i++){
            line(i, HEIGHT, i, pixels[i]);
        }
        for (int i = 0; i <treeX.size(); i++){
            if (i == 0 && treeX.get(0) -16 <0)
                image(trees, 0,pixels[0] -32 );
            else
                image(trees, treeX.get(i) -16 , pixels[treeX.get(i)] -32 );
            //  MIGHT HAVE TO HANDLE Y

//            int actualX = Math.max(treeX.get(i) - 16, 0);
//            int actualY = Math.max(pixels[actualX] - 32, 0);
//            image(trees, actualX, actualY);
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

    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }

}
