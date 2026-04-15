import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


public class KiviGame implements Serializable {
    Board board;
    ArrayList<Player> players;

    public KiviGame() {
        board = new Board();
        players = new ArrayList<>();
    }

    public void saveGame(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static KiviGame loadGame(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (KiviGame) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}