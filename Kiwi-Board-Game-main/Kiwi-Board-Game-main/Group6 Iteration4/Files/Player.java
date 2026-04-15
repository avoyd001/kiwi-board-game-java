
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


public class Player implements Serializable {
    // Distinct black symbols for up to 4 players:
    private static String[] symbolList = { "●", "▲", "■", "◆" };
    private static int symbolIndex = 0;

    // This player's stone symbol (one of the above)
    String stoneSymbol;

    // Always black
    String name;
    boolean isComputer;
    int stonesLeft = 10;
    String difficulty; // "Easy" or "Hard"

    public Player(String name, boolean isComputer) {
        this.name = name;
        this.isComputer = isComputer;
        this.difficulty = "Easy";

        // Assign the next symbol
        this.stoneSymbol = symbolList[symbolIndex % symbolList.length];
        symbolIndex++;
    }

    public Player(String name, boolean isComputer, String difficulty) {
        this.name = name;
        this.isComputer = isComputer;
        this.difficulty = difficulty;

        // Assign the next symbol
        this.stoneSymbol = symbolList[symbolIndex % symbolList.length];
        symbolIndex++;
    }
}