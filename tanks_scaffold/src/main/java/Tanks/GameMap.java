package Tanks;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

/** Methods generating, modifying terrain*/
public class GameMap {
    /**
     * Map objects in the text file to a 2D array.
     * @param map 2D array storing objects as GameObject objects.
     * @param fileName JSON file name.
     * @return 2D GameObject array.
     */
    public static GameObject[][] generateTerrain(GameObject[][] map,
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
                        // Scaled the tree root initially
                        map[row][col] = new GameObject(col * 32, row * 32, "T");
                        col++;
                    }
                    else if (String.valueOf(line.charAt(i)).equals("X")) {
                        map[row][col] = new GameObject(col, row, "X");
                        col++;
                    }
                    else if (String.valueOf(line.charAt(i)).matches("[A-SU-WYZ]")){
                        map[row][col] = new Tank(col, row, String.valueOf(line.charAt(i)));
                        col++;
                    }
                }
                // fill the rest with the line
                while (col < 28){
                    map[row][col] = new GameObject(col, row, " ");
                    col++;
                }
                row++;
            }
            // fill the rest with the missing lines
            while (row < 20){
                for(int col = 0; col < 28; col++){
                    map[row][col] = new GameObject(col, row, " ");
                }
                row++;
            }
            sc.close();
            return map;
        }
        catch (FileNotFoundException e){
            System.err.println("File not found");
            return null;
        }
    }

    public static void print(GameObject[][] board){
        for (GameObject[] row: board){
            for (GameObject cell: row){
                if (cell.type.equals(" "))
                    System.out.print(" "); // could be "-"
                else
                    System.out.print(cell.type);
            }
            System.out.println();
        }
    }

    /**
     *
     * @param row
     * @return
     */
    // Draw from top left -> bottom right
    public static int findColHeight(int row){
        return row*32;
    }
    public static int[] instantiateHeight(GameObject[][] board){
        int[] height = new int[896];
        int innerLoop = 0;
        for (int c = 0; c < 28; c++){
            for(int r = 0 ; r < 20; r++){
                if (board[r][c].getType().equals("X")){
                    for(int i = 0; i < 32; i++){
                        height[i + innerLoop] = findColHeight(r);
                    }
                    innerLoop += 32;
                }
            }
        }
        return height;
    }

    public static int[] movingAverage(int[] heightPixels){
        int[] result = new int[896];
        for (int i = 0; i < 864; i++){
            int sum = 0;
            for(int j = i; j < i+32; j++){
                sum += heightPixels[j];
            }
            result[i] = sum/32;
        }
        for (int i = 864; i < 896; i++){
            result[i] = heightPixels[i];
        }
        return result;
    }
}
