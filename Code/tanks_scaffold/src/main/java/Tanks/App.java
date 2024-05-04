package Tanks;

import processing.core.PApplet;
import processing.core.PImage;
import processing.event.KeyEvent;

import java.util.*;
import java.util.List;

public class App extends PApplet {

    public static int WIDTH = 864; // CELL_SIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELL_SIZE+TOP_BAR;
    public static final int FPS = 30;


    //File attributes
    private String configPath;
    private int[] foregroundColor;
    private PImage backgroundPNG;
    private PImage trees;



    // Level attributes
    private GameMap currentMap;
    private ConfigManager manager;
    private int currentLevelIndex = 0;
    private long delaySwitch = 0;
    private boolean currentlyDelayedLevel = false;
    private boolean isEndGame = false;



    // Gameplay attributes
    private PlayerScores scoreSave;
    private final List<Tank> correctOrder = new ArrayList<>();
    private final List<Tank> order = new ArrayList<>();
    private final List<Projectile> active = new ArrayList<>();
    private final List<Explosion> explosionDraw = new ArrayList<>();
    private boolean showArrow = true;
    private int arrStartTime = millis();


    public App() {
        this.configPath = "additionalFiles/configtest.json";
    }

    /**
     * Initialize the window.
     */
    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Set up game initially, including setting up first
     * level for the game and initialize the scores
     */
    @Override
    public void setup() {
        // INITIAL SETUP
        frameRate(FPS);
        manager = ConfigManager.loadConfig(configPath);
        if (manager == null) {
            System.out.println("Error setting up attributes");
            return;
        }
        // Setup first level
        setupFirstLevel();

        // ONLY CREATE THE PLAYER SCORE ONCE
        scoreSave = new PlayerScores(correctOrder);
    }

    /**
     * Check for any key events and performs
     * the corresponding function.
     * @param event key event received.
     */
    @Override
    public void keyPressed(KeyEvent event){
        int key = event.getKeyCode();

        if (!isEndGame){
            Tank currentTank = order.get(0);
            if (key == ' '){
                if (levelEnds(order)) {
                    // If game has 1 tank left, user click space, switch
                    if (!currentlyDelayedLevel)
                        currentlyDelayedLevel = true;
                    }
                else {
                    // Add a projectile to current active proj ls.
                    active.add(currentTank.shoot());
                    // Then switch turns.
                    switchTurns();
                    // New wind
                    Projectile.windChange();
                    // Redisplay the arrow
                    showArrow = true;
                    arrStartTime = millis();
                }
            } else if (key == LEFT || key == RIGHT) {
                // Only allow to move when the tank's done falling
                if (currentTank.doneFalling(currentMap.getPixels()[currentTank.xPos])) {
                    if (currentTank.getFuelLevel() > 0) {
                        currentTank.move(key, WIDTH, FPS, 60);
                        // Update tank's y position
                        currentTank.yPos = currentMap.getPixels()[currentTank.xPos];
                        currentTank.useFuel();
                    }
                }
            } else if (key == 'W' || key == 'S'){
                currentTank.updatePower(key);
            } else if (key == UP || key == DOWN){
                currentTank.updateAngle(key, PI/frameRate);
            } else if (key == 'F'){
                currentTank.addFuel();
            } else if (key == 'R'){
                currentTank.repair();
            } else if (key == 'P') {
                currentTank.addParachute();
            } else if (key == 'X'){
                currentTank.ultimate();
            }
        } else {
            if (key == 'R') {
                resetGameAttributes(true);
                setupFirstLevel();
                scoreSave = new PlayerScores(correctOrder);
                isEndGame = false;
            }
        }
    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        // Reset background every frame (clean old drawings)
        background(backgroundPNG);
        // Draw terrain & trees
        currentMap.drawTerrain(this, foregroundColor, HEIGHT);
        currentMap.drawTree(this, trees);

        if (!order.isEmpty()) {
            // Draw tanks & their turrets
            for (Tank tank : order) {
                tank.drawTank(this);
                tank.drawTurret(this);
            }
        }
        // Add active projectiles for drawing - delete them after finished
        if (!active.isEmpty()) {
            for (Projectile bullet : active) {
                // Bullet gone out of the map
                if (!bullet.outMap()) {
                    // If bullet hasn't collided with the terrain, update it
                    if (!bullet.collide(currentMap.getPixels())) {
                        bullet.update();
                        bullet.drawProjectile(this);
                        // else check for type of explosion.
                    } else {
                        if (!bullet.isPoweredUp()) {
                            explosionDraw.add(new Explosion(this, bullet.getXPos(), bullet.getYPos(), 30));
                            bullet.explode(currentMap.getPixels(),
                                    correctOrder, 30);
                        } else {
                            explosionDraw.add(new Explosion(this, bullet.getXPos(), bullet.getYPos(), 60));
                            bullet.explode(currentMap.getPixels(),
                                    correctOrder, 60);
                        }
                    }
                }
            }
            // Remove the bullets that had exploded
            active.removeIf(proj -> proj.isExplode() || proj.isOut());
        }
        //  Draw tank fall
        if (!order.isEmpty()) {
            for (Tank tank : order) {
                tank.drawTankFall(this,
                        getPathToImage("parachute.png"),
                        currentMap.getPixels()[tank.xPos]);
            }

            // Remove dead tanks/ tanks fall to death.
            for (Tank tank : order) {
                if (tank.isDead(currentMap.getPixels()[tank.xPos])) {
                    explosionDraw.add(new Explosion(this, tank.xPos, tank.yPos, 15));
                } else if (tank.isOutMap()) {
                    explosionDraw.add(new Explosion(this, tank.xPos, tank.yPos, 30));
                }
            }
            order.removeIf(tank -> tank.isOutMap() || tank.isDead(currentMap.getPixels()[tank.xPos]));
        }

        // UPDATE SCORE
        scoreSave.updatePlayerScores(correctOrder);

        // Draw explosion
        if (!explosionDraw.isEmpty()) {
            for (Explosion e : explosionDraw) {
                e.drawExplosion();
            }
            explosionDraw.removeIf(Explosion::getFinishedExplode);
        }
        //Display HUD
        drawHUD();

        // Indicate current player
        if (showArrow && millis() - arrStartTime < 2000) {
            if (!order.isEmpty()) {
                drawArrow(order.get(0).xPos, order.get(0).yPos - 100);
            }
        }
        else showArrow = false;

        //Display scoreboard
        if (!isEndGame) {
            scoreSave.drawScoreboard(this);
        }

        // Check if the level ends, if ends then start the timer.
        if (levelEnds(order)){
            if (delaySwitch == 0){
                delaySwitch = millis();
            }
            if (millis() - delaySwitch >= 1000){
                currentlyDelayedLevel = true;
            }
            // Check for both 1s elapsed or user pressed Space
            if (currentlyDelayedLevel){
                switchLevels();
            }
        }
//        System.out.println(frameRate);
    }


    // DRAW GAME METHODS
    /**
     * Draw different parts of the HUD, indicates the
     * info related to the current player.
     * if the case all players died at the same
     * time, stop drawing the HUD.
     */
    private void drawHUD(){
        if (order.isEmpty()){
            return;}

        Tank current = order.get(0);

        // Player turn
        String playerTurnText = "Player " + current.type + "'s turn";
        fill(0); // Black text
        textSize(16);
        textAlign(LEFT, TOP);
        text(playerTurnText, 10, 10);

        // Player's fuel
        current.drawFuel(this, getPathToImage("fuel.png"));
        // Power
        current.drawPower(this);
        // Health bar
        current.drawHP(this);
        // Draw ult sign if ult
        if (current.getUltStatus()){
            current.drawUlt(this);
        }
        // Wind
        Projectile.drawWind(this,
                            getPathToImage("wind.png"),
                            getPathToImage("wind-1.png"));
        // Player's parachute
        current.drawParachute(this, getPathToImage("parachute.png"));
    }

    /**
     * Draw the arrow indicates the current player
     * @param x current player's tank X position
     * @param y current player's tank Y position
     */
    private void drawArrow(int x, float y) {
        strokeWeight(4); // Bold line
        // body
        line(x, y - 25, x, y + 50);

        // head
        line(x - 15, y + 25, x, y + 50); // left
        line(x, y + 50, x + 15, y + 25); //right

        strokeWeight(1); // Reset to normal
    }

    // SWITCH
    /**
     * Check if the number of alive tanks
     * is smaller than 1 to switch level.
     * @param tanks list of alive tanks
     * @return true if the list's size smaller than 1.
     */
    private boolean levelEnds(List<Tank> tanks){
        return tanks.size() <= 1;
    }

    /**
     * Pop the first tank in list and append
     * it to the end, simulating a queue for
     * switching turns.
     */
    private void switchTurns(){
        Tank before = order.remove(0);
        order.add(before);
    }

    /**
     * Function for switching levels after there's less
     * than or equal to 1 alive tank on the field.
     * Resetting game attributes for next level, or if its
     * end game then reset whole game for the replaying.
     */
    private void switchLevels(){
        currentLevelIndex++;
        List<Level> levels = manager.getLevels();

        if (currentLevelIndex < levels.size()){
            // Clear the current objects
            resetGameAttributes(false);

            setupFirstLevel();
            // Update points from last level:
            for (Tank tank: order){
                tank.setPoints(scoreSave.getScore().get(tank.type));
            }
            // Reset the switch level delay
            currentlyDelayedLevel = false;
            delaySwitch = 0;
        }
        else {
            currentlyDelayedLevel = false;
            isEndGame = true;
            scoreSave.timerFinal(this);
            scoreSave.drawFinal(this);
        }
    }


    // SETUP METHODS
    /**
     * Set up the level and parse game data into objects,
     * Add tanks in order for switching turns.
     */
    private void setupFirstLevel() {
        Level level = manager.getLevels().get(currentLevelIndex);
        setUpLevel(level);
        extractGameAttributes(level.getLayoutFilePath(), manager.getPlayerColours());
        if(currentMap != null) {
            // Sort the tanks alphabetically in order
            order.addAll(currentMap.getTanksList());
            Collections.sort(order);
            correctOrder.addAll(order);
        }
    }

    /**
     * Load related images, set the level's current terrain color
     * @param level level object contains setup data - background, foreground color...
     */
    private void setUpLevel(Level level){
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

    /**
     * Reset game attributes when a level ends or game ends
     * @param resetWholeGame boolean value indicates resetting the game
     *                       partially (for levels) or all attributes
     *                       (for replaying the whole game)
     */
    private void resetGameAttributes(boolean resetWholeGame) {
        order.clear();
        active.clear();
        explosionDraw.clear();
        Projectile.setWindLevel();
        correctOrder.clear();
        if (resetWholeGame){
            scoreSave.resetPlayerScores();
            currentLevelIndex = 0;
            delaySwitch = 0;
        }
    }

    /**
     * Set up a map base on the txt file, extract
     * game objects such as trees and tanks.
     * @param layout path to map txt file
     * @param colors player colors extracted from config file
     */
    private void extractGameAttributes(String layout, HashMap<String, int[]> colors) {
        // Create a new map object for reference
        GameMap gameMap = new GameMap();
        currentMap = gameMap;
        // Create board
        gameMap.generateMapFromConfig(gameMap.getBoard(), layout);
        // Moving average
        gameMap.instantiateHeight();
        gameMap.movingAverage(gameMap.getPixels());
        gameMap.movingAverage(gameMap.getPixels());
        // Extract the trees from the matrix
        gameMap.extractTree(this);
        gameMap.extractTanks(colors);

        gameMap.updateTanksY();

    }
    private String getPathToImage(String path) {
        return Objects.requireNonNull(this.getClass().getResource(path)).
                getPath().toLowerCase(Locale.ROOT).replace("%20", " ");
    }
    public void setConfigPath(String path){
        this.configPath = path;
    }
    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }
}