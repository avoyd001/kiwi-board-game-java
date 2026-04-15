import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


public class PlayerSettingsDialog extends JDialog {
    private JComboBox<Integer> numPlayersCombo;
    private JCheckBox[] compBoxes;
    private JComboBox<String> difficultyCombo;
    private KiviGame createdGame = null;

    public PlayerSettingsDialog(Frame parent) {
        super(parent, "Player Settings", true);

        // Increase the dialog size so it's not cramped
        setSize(400, 300);
        setLayout(new GridLayout(0, 1, 10, 10));
        setLocationRelativeTo(parent);

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Select number of players:"));
        numPlayersCombo = new JComboBox<>(new Integer[]{2, 3, 4});
        topPanel.add(numPlayersCombo);
        add(topPanel);

        JPanel compPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        compPanel.setBorder(BorderFactory.createTitledBorder("Select computer players:"));
        compBoxes = new JCheckBox[4];
        compBoxes[0] = new JCheckBox("Player 2 is Computer");
        compBoxes[1] = new JCheckBox("Player 3 is Computer");
        compBoxes[2] = new JCheckBox("Player 4 is Computer");
        compPanel.add(compBoxes[0]);
        compPanel.add(compBoxes[1]);
        compPanel.add(compBoxes[2]);
        add(compPanel);

        JPanel diffPanel = new JPanel(new FlowLayout());
        diffPanel.add(new JLabel("Difficulty for computer players:"));
        difficultyCombo = new JComboBox<>(new String[]{"Easy", "Hard"});
        diffPanel.add(difficultyCombo);
        add(diffPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(e -> {
            createGameFromSettings();
            dispose();
        });
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel);
    }

    private void createGameFromSettings() {
        int numPlayers = (int) numPlayersCombo.getSelectedItem();
        createdGame = new KiviGame();

        // Always create Player 1 (human)
        Player p1 = new Player("Player 1", false);
        createdGame.players.add(p1);

        if (numPlayers >= 2) {
            boolean isComp = compBoxes[0].isSelected();
            Player p2 = isComp
                ? new Player("Player 2", true, (String) difficultyCombo.getSelectedItem())
                : new Player("Player 2", false);
            createdGame.players.add(p2);
        }
        if (numPlayers >= 3) {
            boolean isComp = compBoxes[1].isSelected();
            Player p3 = isComp
                ? new Player("Player 3", true, (String) difficultyCombo.getSelectedItem())
                : new Player("Player 3", false);
            createdGame.players.add(p3);
        }
        if (numPlayers >= 4) {
            boolean isComp = compBoxes[2].isSelected();
            Player p4 = isComp
                ? new Player("Player 4", true, (String) difficultyCombo.getSelectedItem())
                : new Player("Player 4", false);
            createdGame.players.add(p4);
        }
    }

    public KiviGame getCreatedGame() {
        return createdGame;
    }
}