package Tanks;

import processing.core.PApplet;
import processing.core.PImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class GameMap {
    private final int BOARD_HEIGHT = 20;
    private final int BOARD_WIDTH = 28;

    private final GameObject[][] board;
    private final ArrayList<Integer> treeX;
    private final float[] pixels;
    private final ArrayList<Tank> tanksList;

    public GameMap(){
        board = new GameObject[BOARD_HEIGHT][BOARD_WIDTH];
        treeX = new ArrayList<>();
        //TODO: careful the width - actual width is 864
        int WIDTH = 896;
        pixels = new float[WIDTH];
        tanksList = new ArrayList<>();
    }


    // TERRAIN INSTANTIATION
    /**
     * Scale the height of the pixel to 32px
     * @param row previous value of the pixel before scaling
     * @return actual height of the pixel after scaling
     */
    public int findColHeight(int row){
        return row*32;
    }

    /**
     * Map objects in the text file to a 2D array.
     *
     * @param map      2D array storing objects as GameObject objects.
     * @param fileName JSON file name.
     */
    public void generateMapFromConfig(GameObject[][] map,
                                      String fileName){
        File f = new File(fileName);

        try {
            Scanner sc = new Scanner(f);
            int row = 0;
            while (sc.hasNextLine() && row < 20){
                String line = sc.nextLine();
                int col = 0;
                for (int i = 0; i < line.length() && col < 28; i++){
                    if (String.valueOf(line.charAt(i)).equals(" ")){
                        map[row][col] = new GameObject(col, row, " ");
                        col++;
                    }
                    else if (String.valueOf(line.charAt(i)).equals("T")) {
                        // Randomize the location of the tree
                        // Scaled the tree root initially by 32px
                        map[row][col] = new GameObject(col * 32, row * 32, "T");
                        col++;
                    }
                    else if (String.valueOf(line.charAt(i)).equals("X")) {
                        map[row][col] = new GameObject(col, row, "X");
                        col++;
                    }
                    else if (String.valueOf(line.charAt(i)).matches("[A-SU-WYZ]")){
                        // Scaled the tank initially by 32px
                        map[row][col] = new Tank(col * 32, row * 32, String.valueOf(line.charAt(i)));
                        col++;
                    }
                }
                // Fill the missing columns
                while (col < 28){
                    map[row][col] = new GameObject(col, row, " ");
                    col++;
                }
                row++;
            }
            // Fill the missing lines
            while (row < 20){
                for(int col = 0; col < 28; col++){
                    map[row][col] = new GameObject(col, row, " ");
                }
                row++;
            }
            sc.close();
        }
        catch (FileNotFoundException e){
            System.err.println("File not found");
        }
    }

    /**
     * Instantiate the length-896 array to
     * process the movingAverage method later.
     */
    public void instantiateHeight(){
        int innerLoop = 0;
        for (int c = 0; c < 28; c++){
            for(int r = 0 ; r < 20; r++){
                if (board[r][c].getType().equals("X")){
                    for(int i = 0; i < 32; i++){
                        pixels[i + innerLoop] = findColHeight(r);
                    }
                    innerLoop += 32;
                }
            }
        }
    }

    /**
     * Arithmetic method to smooth out the pixels
     * using the 896-length array (contains the 32px
     * which are not visible in the window)
     *
     * @param heightPixels array contain pixel heights
     *                     (terrain heights) before smoothing.
     */
    public void movingAverage(float[] heightPixels){
        for (int i = 0; i < 864; i++){
            float sum = 0;
            for(int j = i; j < i+32; j++){
                sum += heightPixels[j];
            }
            pixels[i] = sum/32;
        }
        System.arraycopy(heightPixels, 864, pixels, 864, 32);
    }
    public void updateTanksY(){
        for (Tank tank: tanksList){
            tank.yPos = pixels[tank.xPos];
        }
    }


    // OBJECT EXTRACTION
    public void extractTree(PApplet app){
        for (int c = 0; c < BOARD_WIDTH; c++) {
            for (int r = 0; r < BOARD_HEIGHT; r++) {
                if (board[r][c].getType().equals("T")) {
                    // Randomize the location of the tree up to 30 pixels [-15,15]
                    int rand = (int) app.random(-15, 15);
                    // At first, the cell is scaled to 32x32 matrix, hence the tree root
                    // pixel would be in the bottom left cell of the matrix. To align the
                    // root correctly, +16 to align the root to the center.
                    treeX.add(board[r][c].xPos + 16 + rand);
                }
            }
        }
    }
    public void extractTanks(HashMap<String, int[]> colors){
        for (int c = 0; c < BOARD_WIDTH; c++) {
            for (int r = 0; r < BOARD_HEIGHT; r++) {
                if (board[r][c] instanceof Tank) {
                    int[] rgb = colors.get(board[r][c].type);
                    ((Tank) board[r][c]).setColorTank(rgb);
                    // Tank is alr scaled, according to the demo, no need to align to middle
                    tanksList.add((Tank) board[r][c]);
                    // The height of the tank will be 1 pixel above the terrain
                    board[r][c].yPos = pixels[board[r][c].xPos] - 1;
                }
            }
        }
    }


    // DRAW
    public void drawTerrain(PApplet app, int[] RGB, int height) {
        app.stroke(RGB[0], RGB[1], RGB[2]);
        for (int i = 0; i < 864; i++){
            app.line(i, height, i, pixels[i]);
        }
        // Reset
        app.stroke(0);
    }
    //TODO check tree at borders
    public void drawTree(PApplet app, PImage trees){
        for (int i = 0; i <treeX.size(); i++){
            // The X point, Y point has to scaled -16 and -32 respectively
            // since the img is drawn from top-left corner.
            if (i == 0 && treeX.get(0) -16 <0)
                app.image(trees, 0,pixels[0] -32);
            else
                app.image(trees, treeX.get(i) - 16 , pixels[treeX.get(i)] -32);
            //  MIGHT HAVE TO HANDLE Y
        }
    }



    // GETTER & SETTERS
    public GameObject[][] getBoard(){
        return board;
    }
    public float[] getPixels() {
        return pixels;
    }
    public ArrayList<Tank> getTanksList() {
        return tanksList;
    }
}