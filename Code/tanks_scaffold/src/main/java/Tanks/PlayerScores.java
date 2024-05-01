package Tanks;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerScores {
    private final HashMap<String, Integer> points = new HashMap<>();
    private final List<Tank> playersList;
    private int index = 0;
    private float startDrawDelay = 0 ;
    private HashMap<String,Integer> drawPlayersInFinal = new HashMap<>();

    /**
     * Create an object to store points for each player.
     * Parse the points + its player to a key-value pair.
     * @param tanks List of tank objects (sorted)
     */
    public PlayerScores(List<Tank> tanks){
        playersList = tanks;
        for (Tank tank: tanks){
            points.put(tank.type, tank.getPoints());
        }
    }

    /**
     * Draw the scoreboard next to the HUD,
     * display the players and their scores
     * @param app refer to Main
     */
    public void drawScoreboard(App app){
        app.textSize(12);
        // Upper rect
        app.strokeWeight(3);
        app.noFill();
        app.rect(720, 55, 135, 14);
        app.text("Scores", 765, 54);

        // Points
        app.rect(720, 69, 135, 15 * playersList.size());
        int i = 0;
        for (Tank tank: playersList){
            app.fill(tank.getColorTank()[0],
                    tank.getColorTank()[1],
                    tank.getColorTank()[2]);
            app.text("Player "+ tank.type, 723, 69 + 14 * i);

            app.fill(0);
            app.text(points.get(tank.type), 835, 69 + 14 * i);
            i++;
        }

        // reset
        app.fill(0);
        app.strokeWeight(1);
        app.textSize(14);

    }

    /**
     * Draw the text indicating the winner,
     * the scoreboard with player scores, ranking
     * from the highest to the lowest.
     * @param app refer to Main
     */
    public void drawFinal(App app){
        // Cite
        //Stack Overflow. (2011). Sorting HashMap by values.
        //Available at: https://stackoverflow.com/questions/8119366/sorting-hashmap-by-values
        //[Accessed 25 Apr. 2024].

        // Sort the player-points pairs by values from high to low
        drawPlayersInFinal = drawPlayersInFinal.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
        // Get the winner
        String winner = Collections.max(points.entrySet(), Map.Entry.comparingByValue()).getKey();
        int numberOfPlayers = playersList.size();
        // Get the winner's color
        int[] winnerColor = new int[3];
        for (Tank tank : playersList){
            if (Objects.equals(tank.type, winner)){
                winnerColor = tank.getColorTank();
                break;
            }
        }
        // Draw the winner on screen
        app.textSize(24);
        app.fill(winnerColor[0], winnerColor[1], winnerColor[2]);
        app.text("Player " + winner + " wins!", 300, 100);

        // Upper rect
        app.strokeWeight(4);
        // Fill salmon
        app.fill(250, 128,114);
        app.rect(280, 140, 350, 35);
        app.fill(0);
        app.text( "Final Scores", 300, 145);

        // Lower rect
        app.fill(250, 128,114);
        app.rect(280, 175, 350, 40 * numberOfPlayers);

        // Draw the players after the delay animation in timerFinal()
        int i = 0;
        for (String player : drawPlayersInFinal.keySet()){
            int[] playerColor = new int[3];
            int points = drawPlayersInFinal.get(player);

            for (Tank tank:playersList){
                if (Objects.equals(tank.type, player)){
                    playerColor = tank.getColorTank();
                    break;
                }
            }
            app.fill(playerColor[0], playerColor[1], playerColor[2]);
            app.text("Player " + player, 300, 180 + 35 * i);
            app.fill(0);
            app.text(points, 570, 180 + 35 * i);
            i+=1;
        }
        // reset
        app.fill(0);
        app.strokeWeight(1);
        app.textSize(14);
    }

    /**
     * Timer implement the 0.7 delay showing players.
     * @param app refer to Main
     */
    public void timerFinal(App app){
        HashMap<String, Integer> scoresSorted = points.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
        ArrayList<String> sortedKeys = new ArrayList<>();
        ArrayList<Integer> sortedValues = new ArrayList<>();


        for (String player:scoresSorted.keySet()){
            sortedKeys.add(player);
            sortedValues.add(scoresSorted.get(player));
        }

        int[] playerColor = new int[3];
        int points = sortedValues.get(index);
        String player = sortedKeys.get(index);
        for (Tank tank:playersList){
            if (Objects.equals(tank.type, player)){
                playerColor = tank.getColorTank();
                break;
            }
        }
        if (startDrawDelay == 0){
            startDrawDelay = app.millis();
        }
        app.textSize(24);
        app.fill(playerColor[0], playerColor[1], playerColor[2]);
        app.text("Player " + player, 300, 180 + 35 * index);
        app.fill(0);
        app.text(points, 570, 180 + 35 * index);

        drawPlayersInFinal.put(player, points);

        if (app.millis() - startDrawDelay >= 700){
            index = (index < sortedKeys.size()-1) ? index + 1: index;
            startDrawDelay = 0;
        }
        // reset
        app.fill(0);
        app.strokeWeight(1);
        app.textSize(14);
    }


    // STATE FUNCTIONS
    /**
     * Update player scores during the game
     * @param tanks update points which align with tanks
     */
    public void updatePlayerScores(List<Tank> tanks){
        for (Tank tank: tanks){
            if (points.containsKey(tank.type))
                points.put(tank.type, tank.getPoints());
        }
    }

    /**
     * Reset the scores to the new state with default values.
     */
    public void resetPlayerScores(){
        points.clear();
        playersList.clear();
        index = 0;
        startDrawDelay = 0;
        drawPlayersInFinal.clear();
    }

    public HashMap<String, Integer> getScore(){
        return points;
    }
}