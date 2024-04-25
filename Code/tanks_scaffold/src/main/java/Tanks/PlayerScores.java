package Tanks;

import java.util.HashMap;
import java.util.List;

public class PlayerScores {
    private HashMap<String, Integer> points = new HashMap<>();

    public PlayerScores(List<Tank> tanks){
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
    public HashMap<String, Integer> getScore(){
        return points;
    }

    public void resetPlayerScores(){
        points.clear();
    }
}
