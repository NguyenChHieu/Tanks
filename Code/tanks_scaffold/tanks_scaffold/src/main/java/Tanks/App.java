package Tanks;

import org.checkerframework.checker.units.qual.A;
import processing.core.PApplet;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

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
    public ArrayList<Tank> tankH = new ArrayList<>();

    // Gameplay attributes
    public List<Tank> order = new ArrayList<>();
    public List<Projectile> active = new ArrayList<>();
    public boolean showArrow = true;
    public int arrStartTime = millis();

    // TODO: TESTING
    public App() {
        this.configPath = "configtest.json";
    }

	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

	@Override
    public void setup() {
        frameRate(FPS);

        ConfigManager manager = ConfigLoader.loadConfig(configPath);
        if (manager == null) {
            System.out.println("Error setting up attributes");
            return;
        }
        // Setup map
        HashMap<String, int[]> colors = manager.getPlayerColours();
        List<LevelConfig> levels = manager.getLevels();

        LevelConfig level = levels.get(0);
        String layout = level.getLayoutFilePath();

        // Setup levels
        setUpLevels(level);
        extractAttributes(layout, colors);

        // Sort the tanks alphabetically in order
        order.addAll(tankH);
        Collections.sort(order);

        // Check if all the tanks has its color attribute
        for (Tank tank : order){
            if (!colors.containsKey(tank.type)){
                System.err.println("No color for player " + tank.type +" found");
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent event){
        Tank currentTank = order.get(0);
        int key = event.getKeyCode();

        if (event.getKey() == ' '){
            // Add a projectile to current active proj ls.
            active.add(currentTank.shoot());
            // Then switch turns.
            switchTurns();

            // Redisplay the arrow
            showArrow = true;
            arrStartTime = millis();
        }
        else if (key == LEFT || key == RIGHT) {
            if (currentTank.getFuelLevel() > 0) {
                currentTank.move(key, WIDTH, FPS, 60);
                // Update y
                currentTank.yPos = pixels[currentTank.xPos] -1;
                currentTank.useFuel();
            }
        }
        else if (key == 'W' || key == 'S'){
            currentTank.updatePower(key);
        }
        else if (key == UP || key == DOWN){
            currentTank.updateAngle(key, PI/FPS);
        }
    }

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
        drawTerrain();
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
        for (Tank tank : order){
            // Tank center 1 px above terrain
            tank.drawTank(this);
            tank.drawTurret(this);
        }

        // Projectile
        for (Projectile bullet : active){
            if (!bullet.outMap()){
                if (!bullet.collide(pixels)){
                    bullet.update();
                    bullet.drawProjectile(this);
                }
            }
        }
        active.removeIf(Projectile::isOut);
        active.removeIf(Projectile::isExplode);

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

    private void drawTerrain() {
        stroke(foregroundColor[0],foregroundColor[1],foregroundColor[2]);
        for (int i = 0; i < 864; i++){
            line(i, HEIGHT, i, pixels[i]);
        }
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
        PImage fuel = loadImage(getPathToImage("fuel.png"));
        fuel.resize(20,20);
        image(fuel,160, 10);
        // Fuel level
        fill(0);
        textSize(16);
        text(current.getFuelLevel(), 190, 10);

        // Player's parachute
        PImage parachute = loadImage(getPathToImage("parachute.png"));
        parachute.resize(20, 20);
        image(parachute, 160, 40);
        // TODO: Parachute

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

    public void switchTurns(){
        Tank before = order.remove(0);
        order.add(before);
    }
    private void extractAttributes(String layout, HashMap<String, int[]> colors) {
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
                    tankH.add((Tank) board[r][c]);
                    // The height of the tank will be 1 pixel above the terrain
                    board[r][c].yPos = pixels[board[r][c].xPos] -1;
                }
            }
        }
    }
    public void setUpLevels(LevelConfig level){
        // Set level's background
        if (level.getBackground() != null) {
            backgroundPNG = loadImage(getPathToImage(level.getBackground()));
        }
        // Set level's terrain
        foregroundColor = level.getForegroundColour();

        // Set level's trees
        if (level.getTrees() != null) {
            trees = loadImage(getPathToImage(level.getTrees()));
            // Resize tree img
            trees.resize(32,32);
        }
    }
    private String getPathToImage(String path) {
        return Objects.requireNonNull(this.getClass().getResource(path)).
                getPath().toLowerCase(Locale.ROOT).replace("%20", " ");
    }

    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }
}