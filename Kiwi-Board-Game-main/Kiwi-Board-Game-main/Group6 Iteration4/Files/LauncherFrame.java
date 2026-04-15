
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


public class LauncherFrame extends JFrame {
    public LauncherFrame() {
        super("Kivi Game Launcher");
        setSize(500, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JPanel bgPanel = new JPanel();
        bgPanel.setBackground(new Color(150, 80, 200));
        bgPanel.setLayout(null);
        bgPanel.setBounds(0, 0, 500, 300);
        add(bgPanel);

        JLabel welcomeLabel = new JLabel("Welcome to Kivi Game");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setBounds(50, 30, 400, 50);
        bgPanel.add(welcomeLabel);

        JButton newGameBtn = new JButton("New Game");
        newGameBtn.setFont(new Font("Arial", Font.BOLD, 16));
        newGameBtn.setBounds(150, 100, 200, 40);
        newGameBtn.addActionListener(e -> {
            PlayerSettingsDialog dialog = new PlayerSettingsDialog(this);
            dialog.setVisible(true);
            KiviGame newGame = dialog.getCreatedGame();
            if (newGame != null) {
                new KiviGameGUI(newGame);
                dispose();
            }
        });
        bgPanel.add(newGameBtn);

        JButton loadGameBtn = new JButton("Load Game");
        loadGameBtn.setFont(new Font("Arial", Font.BOLD, 16));
        loadGameBtn.setBounds(150, 160, 200, 40);
        loadGameBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(".");
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filename = chooser.getSelectedFile().getAbsolutePath();
                KiviGame loaded = KiviGame.loadGame(filename);
                if (loaded != null) {
                    new KiviGameGUI(loaded);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to load game from " + filename,
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        bgPanel.add(loadGameBtn);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}