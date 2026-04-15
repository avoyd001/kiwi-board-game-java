
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


public class Square implements Serializable {
    String label;
    String combo;
    int baseScore;
    Player owner;

    public Square(String label, String combo, int baseScore) {
        this.label = label;
        this.combo = combo;
        this.baseScore = baseScore;
        this.owner = null;
    }

    public boolean isEmpty() {
        return owner == null;
    }
}    