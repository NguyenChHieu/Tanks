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
    public String configPath;
    public int[] foregroundColor;
    public  PImage backgroundPNG;
    public PImage trees;



    // Level attributes
    public GameMap currentMap;
    public ConfigManager manager;
    public int currentLevelIndex = 0;
    private long delaySwitch = 0;
    public boolean currentlyDelayedLevel = false;
    public boolean isEndGame = false;



    // Gameplay attributes
    public PlayerScores scoreSave;
    public List<Tank> correctOrder = new ArrayList<>();
    public List<Tank> order = new ArrayList<>();
    public List<Projectile> active = new ArrayList<>();
    public boolean showArrow = true;
    private int arrStartTime = millis();
    private final float explodeStart = millis();


    public App() {
        this.configPath = "configtest.json";
    }

	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }
	@Override
    public void setup() {
        // INITIAL SETUP
        frameRate(FPS);
        manager = ConfigLoader.loadConfig(configPath);
        if (manager == null) {
            System.out.println("Error setting up attributes");
            return;}

        // Setup first level
        setupFirstLevel();

        // ONLY CREATE THE PLAYER SCORE ONCE
        scoreSave = new PlayerScores(correctOrder);
    }


    @Override
    public void keyPressed(KeyEvent event){
        int key = event.getKeyCode();

        if (!isEndGame){
            Tank currentTank = order.get(0);
            if (event.getKey() == ' ')
                if (currentlyDelayedLevel) {
                    currentlyDelayedLevel = false;
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
            else if (key == LEFT || key == RIGHT) {
                if (!currentTank.isFalling(currentMap.getPixels()[currentTank.xPos])) {
                    if (currentTank.getFuelLevel() > 0) {
                        currentTank.move(key, WIDTH, FPS, 60);
                        currentTank.yPos = currentMap.getPixels()[currentTank.xPos];
                        currentTank.useFuel();
                    }
                }
            }
            else if (key == 'W' || key == 'S'){
                currentTank.updatePower(key);
            }
            else if (key == UP || key == DOWN){
                currentTank.updateAngle(key, PI/FPS);
            }
            else if (key == 'F'){
                currentTank.addFuel();
            }
            else if (key == 'R'){
                currentTank.repair();
            }
        }
        else {
            if (key == 'R') {
                //TODO RESET GAME
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
        if (!order.isEmpty()) {

            // Reset background every frame (clean old drawings)
            background(backgroundPNG);
            // Draw terrain & trees
            currentMap.drawTerrain(this, foregroundColor, HEIGHT);
            currentMap.drawTree(this, trees);
            // Draw tanks & their turrets
            for (Tank tank : order) {
                tank.drawTank(this);
                tank.drawTurret(this);
            }
            // Draw active projectiles
            for (Projectile bullet : active) {
                if (!bullet.outMap()) {
                    if (!bullet.collide(currentMap.getPixels())) {
                        bullet.update();
                        bullet.drawProjectile(this);
                    } else {
                        Projectile.drawExplosion(this,
                                bullet.getXPos(), bullet.getYPos(),
                                explodeStart, true);

                        // calc both explosion dmg
                        bullet.explode(currentMap.getPixels(),
                                currentMap.getTanksList());

                    }
                }
            }
            active.removeIf(proj -> proj.isExplode() || proj.isOut());

            // UPDATE SCORE
            scoreSave.updatePlayerScores(correctOrder);

            for (Tank tank : order) {
                tank.drawTankFall(this,
                        getPathToImage("parachute.png"),
                        currentMap.getPixels()[tank.xPos]);
            }
            // Remove dead tanks + explosion
            for (Tank tank : order) {
                if (tank.isDead(currentMap.getPixels()[tank.xPos])) {
                    Projectile.drawExplosion(this,
                            tank.xPos,
                            tank.yPos,
                            explodeStart,
                            false);
                } else if (tank.isOutMap()) {
                    Projectile.drawExplosion(this,
                            tank.xPos,
                            tank.yPos,
                            explodeStart,
                            true);
                }
            }
            order.removeIf(tank -> tank.isOutMap() || tank.isDead(currentMap.getPixels()[tank.xPos]));


            //----------------------------------
            //Display HUD
            //----------------------------------
            drawHUD();

            // Indicate current player
            if (showArrow && millis() - arrStartTime < 2000) {
                if (!order.isEmpty()) {
                    drawArrow(order.get(0).xPos, order.get(0).yPos - 100);
                }
            }
            else showArrow = false;

            //----------------------------------
            //Display scoreboard
            //----------------------------------
            if (!isEndGame) {
                scoreSave.drawScore(correctOrder, scoreSave.getScore(), this);
            }
        }
        if (levelEnds(order)){
            if (delaySwitch == 0){
                delaySwitch = millis();
            }
            if (millis() - delaySwitch >= 1000){
                currentlyDelayedLevel = true;
            }
            if (currentlyDelayedLevel){
                switchLevels();
            }
        }
    }



    // DRAW GAME METHODS
    void drawHUD(){
        if (order.isEmpty()){
//            System.out.println("No players found");
            return;}

        Tank current = order.get(0);

        // Player turn
        String playerTurnText = "Player " + current.getType() + "'s turn";
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
        // Wind
        Projectile.drawWind(this,
                            getPathToImage("wind.png"),
                            getPathToImage("wind-1.png"));
        // Player's parachute
        current.drawParachute(this, getPathToImage("parachute.png"));
    }
    void drawArrow(int x, float y) {
        strokeWeight(4); // Bold line
        // body
        line(x, y - 25, x, y + 50);

        // head
        line(x - 15, y + 25, x, y + 50); // left
        line(x, y + 50, x + 15, y + 25); //right

        strokeWeight(1); // Reset to normal
    }

    // SWITCH
    private boolean levelEnds(List<Tank> tanks){
        return tanks.size() <= 1;
    }
    private void switchTurns(){
        Tank before = order.remove(0);
        order.add(before);
    }
    private void switchLevels(){
        currentLevelIndex++;
        List<Level> levels = manager.getLevels();

        if (currentLevelIndex < levels.size()){
            // Clear the current objects
            resetGameAttributes(false);

            Level level = levels.get(currentLevelIndex);
            setUpLevel(level);
            extractGameAttributes(level.getLayoutFilePath(), manager.getPlayerColours());
            if(currentMap != null) {
                // Clear correctOrder before adding players
                order.addAll(currentMap.getTanksList());
                Collections.sort(order);
                correctOrder.addAll(order);

                // Update points from last level:
                for (Tank tank: order){
                    tank.setPoints(scoreSave.getScore().get(tank.type));
                }
            }
            currentlyDelayedLevel = false;
            delaySwitch = 0;
        }
        else {
            currentlyDelayedLevel = false;
            isEndGame = true;
            scoreSave.timerFinal(correctOrder, scoreSave.getScore(), this);
            scoreSave.drawFinal(correctOrder, scoreSave.getScore(), this);
        }
    }


    // SETUP METHODS
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
    private void resetGameAttributes(boolean resetWholeGame) {
        order.clear();
        active.clear();
        Projectile.setWindLevel();
        correctOrder.clear();
        if (resetWholeGame){
            scoreSave.resetPlayerScores();
            currentLevelIndex = 0;
            delaySwitch = 0;
        }
    }
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

    public static void main(String[] args) {
        PApplet.main("Tanks.App");
    }
}