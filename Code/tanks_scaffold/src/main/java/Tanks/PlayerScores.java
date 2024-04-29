package Tanks;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerScores {
    private HashMap<String, Integer> points = new HashMap<>();
    private List<Tank> playersList = new ArrayList<>();
    private int index = 0;
    private float startDrawDelay = 0 ;
    private HashMap<String,Integer> drawPlayersInFinal = new HashMap<>();


    public PlayerScores(List<Tank> tanks){
        playersList = tanks;
        for (Tank tank: tanks){
            points.put(tank.type, tank.getPoints());
        }
    }

    public void updatePlayerScores(List<Tank> tanks){
        for (Tank tank: tanks){
            if (points.containsKey(tank.type))
                points.put(tank.type, tank.getPoints());
        }
    }
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

    void drawScore(List<Tank> players, HashMap<String, Integer> scores, App app){
        app.textSize(12);
        // Upper rect
        app.strokeWeight(3);
        app.noFill();
        app.rect(720, 55, 135, 14);
        app.text("Scores", 765, 54);

        // Points
        app.rect(720, 69, 135, 15 * players.size());
        int i = 0;
        for (Tank tank: players){
            app.fill(tank.getColorTank()[0],
                    tank.getColorTank()[1],
                    tank.getColorTank()[2]);
            app.text("Player "+ tank.type, 723, 69 + 14 * i);

            app.fill(0);
            app.text(scores.get(tank.type), 835, 69 + 14 * i);
            i++;
        }

        // reset
        app.fill(0);
        app.strokeWeight(1);
        app.textSize(14);

    }
    void timerFinal(List<Tank> players, HashMap<String, Integer> scores, App app){
        HashMap<String, Integer> scoresSorted = scores.entrySet().stream()
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
        for (Tank tank:players){
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
    void drawFinal(List<Tank> players, HashMap<String, Integer> scores, App app){
        // Cite
        //Stack Overflow. (2011). Sorting HashMap by values.
        //Available at: https://stackoverflow.com/questions/8119366/sorting-hashmap-by-values
        //[Accessed 25 Apr. 2024].
        drawPlayersInFinal = drawPlayersInFinal.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
        String winner = Collections.max(scores.entrySet(), Map.Entry.comparingByValue()).getKey();
        int numberOfPlayers = players.size();

        int[] winnerColor = new int[3];
        for (Tank tank:players){
            if (Objects.equals(tank.type, winner)){
                winnerColor = tank.getColorTank();
                break;
            }
        }
        app.textSize(24);
        app.fill(winnerColor[0], winnerColor[1], winnerColor[2]);
        app.text("Player " + winner + " wins!", 300, 100);

        // Upper
        app.strokeWeight(4);
        // Fill salmon
        app.fill(250, 128,114);
        app.rect(280, 140, 350, 35);
        app.fill(0);
        app.text( "Final Scores", 300, 145);

        // Lower
        app.fill(250, 128,114);
        app.rect(280, 175, 350, 40 * numberOfPlayers);

        int i = 0;
        for (String player : drawPlayersInFinal.keySet()){
            int[] playerColor = new int[3];
            int points = drawPlayersInFinal.get(player);

            for (Tank tank:players){
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
}