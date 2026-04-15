import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


public class Board implements Serializable {
    Square[][] squares = new Square[7][7];
    private static final String[][] BOARD_CONTENTS = {
        {"AABB", "ABCDE", "≤12", "AAA", "1,3,5", "2,4,6", "AAA"},
        {"2,4,6", "AAAABB", "AAA", "AABBCC", "ABCD", "AAABBB", "≥30"},
        {"ABCD", "AAAA",  "≥30",  "ABCDE", "AAAABB", "1,3,5", "AAABB"},
        {"≤12",  "AAABB", "2,4,6","AAABBB","≤12",   "AABB",   "ABCDE"},
        {"AAA",  "ABCDE", "AABBCC","1,3,5","AAAA",  "≥30",    "AABB"},
        {"1,3,5","AAABBB","ABCD", "AAAABB","AAABB", "AABBCC", "≤12"},
        {"ABCD", "≥30",   "AAAA", "AABB",  "1,3,5", "AAAA",   "AAABB"}
    };

    static class ComboMapping implements Serializable {
        String combo;
        int baseScore;
        ComboMapping(String combo, int baseScore) {
            this.combo = combo;
            this.baseScore = baseScore;
        }
    }

    private static ComboMapping mapContent(String content) {
        switch (content) {
            case "AABB":      return new ComboMapping("Two Pairs", 1);
            case "ABCDE":     return new ComboMapping("Large Straight", 2);
            case "≤12":       return new ComboMapping("12 or Fewer", 2);
            case "AAA":       return new ComboMapping("Three of a Kind", 1);
            case "1,3,5":     return new ComboMapping("All Odd", 2);
            case "2,4,6":     return new ComboMapping("All Even", 2);
            case "AAAABB":    return new ComboMapping("Four of a Kind and a Pair", 3);
            case "AABBCC":    return new ComboMapping("Three Pairs", 3);
            case "AAABBB":    return new ComboMapping("Two Times Three of a Kind", 3);
            case "≥30":       return new ComboMapping("30 or More", 2);
            case "ABCD":      return new ComboMapping("Little Straight", 1);
            case "AAAA":      return new ComboMapping("Four of a Kind", 2);
            case "AAABB":     return new ComboMapping("Full House", 1);
            default:          return new ComboMapping("Unknown", 1);
        }
    }

    public Board() {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                String content = BOARD_CONTENTS[i][j];
                ComboMapping mapping = mapContent(content);
                squares[i][j] = new Square(content, mapping.combo, mapping.baseScore);
            }
        }
    }

    public java.util.List<int[]> getAllFreeSquares() {
        java.util.List<int[]> list = new java.util.ArrayList<>();
        for (int i = 0; i < 7; i++){
            for (int j = 0; j < 7; j++){
                if (squares[i][j].isEmpty()){
                    list.add(new int[]{i, j});
                }
            }
        }
        return list;
    }

    public boolean placeStone(int row, int col, Player p) {
        if (row < 0 || row >= 7 || col < 0 || col >= 7) return false;
        if (!squares[row][col].isEmpty()) return false;
        squares[row][col].owner = p;
        return true;
    }

    public boolean forcePlaceStone(int row, int col, Player p) {
        if (row < 0 || row >= 7 || col < 0 || col >= 7) return false;
        squares[row][col].owner = p;
        return true;
    }

    public int scoreForPlayer(Player p) {
        int score = 0;
        // Horizontal groups
        for (int i = 0; i < 7; i++){
            int j = 0;
            while (j < 7) {
                if (!squares[i][j].isEmpty() && squares[i][j].owner == p) {
                    int groupLen = 0, groupSum = 0;
                    while (j < 7 && !squares[i][j].isEmpty() && squares[i][j].owner == p) {
                        groupSum += squares[i][j].baseScore;
                        groupLen++;
                        j++;
                    }
                    score += groupSum * groupLen;
                } else {
                    j++;
                }
            }
        }
        // Vertical groups
        for (int j = 0; j < 7; j++){
            int i = 0;
            while (i < 7) {
                if (!squares[i][j].isEmpty() && squares[i][j].owner == p) {
                    int groupLen = 0, groupSum = 0;
                    while (i < 7 && !squares[i][j].isEmpty() && squares[i][j].owner == p) {
                        groupSum += squares[i][j].baseScore;
                        groupLen++;
                        i++;
                    }
                    score += groupSum * groupLen;
                } else {
                    i++;
                }
            }
        }
        return score;
    }
}