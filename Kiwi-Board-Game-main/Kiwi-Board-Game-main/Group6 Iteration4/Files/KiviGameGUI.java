import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class KiviGameGUI extends JFrame {
    private KiviGame game;
    private JPanel boardPanel;
    private JLabel statusLabel;
    private JPanel dicePanel;
    private JLabel[] diceLabels;
    private JButton rollButton, rerollButton;
    private JButton nextTurnButton;
    private JMenuItem saveMenuItem;

    private JButton[][] boardButtons = new JButton[7][7];

    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 18);
    private static final Font STONE_FONT = new Font("Arial", Font.BOLD, 54);

    private int currentRound = 1;
    private int currentPlayerIndex = 0;
    private int[] currentDice;
    private boolean waitingForMove = false;
    private int rethrowsLeft = 2;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LauncherFrame();
        });
    }

    public KiviGameGUI(KiviGame game) {
        this.game = game;
        setTitle("Kivi Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(220, 220, 250));
        add(mainPanel, BorderLayout.CENTER);

        boardPanel = new JPanel(new GridLayout(7, 7, 2, 2));
        boardPanel.setBackground(new Color(200, 200, 240));
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                JButton btn = new JButton(game.board.squares[i][j].label);
                btn.setFocusPainted(false);
                btn.setFont(LABEL_FONT);
                btn.setBackground(Color.WHITE);

                final int row = i, col = j;
                btn.addActionListener(e -> {
                    if (waitingForMove && isValidMove(row, col)) {
                        Player currentPlayer = game.players.get(currentPlayerIndex);
                        game.board.placeStone(row, col, currentPlayer);
                        btn.setText(currentPlayer.stoneSymbol);
                        btn.setForeground(Color.BLACK);
                        btn.setFont(STONE_FONT);
                        btn.setBackground(Color.LIGHT_GRAY);

                        waitingForMove = false;
                        statusLabel.setText("Move accepted. Next turn...");
                        advanceTurn();
                    }
                });
                boardButtons[i][j] = btn;
                boardPanel.add(btn);
            }
        }
        mainPanel.add(boardPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(new Color(220, 220, 250));

        statusLabel = new JLabel("Welcome to Kivi Game GUI");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        controlPanel.add(statusLabel, BorderLayout.NORTH);

        dicePanel = new JPanel(new FlowLayout());
        dicePanel.setBackground(new Color(220, 220, 250));
        diceLabels = new JLabel[6];
        for (int i = 0; i < 6; i++) {
            diceLabels[i] = new JLabel("?");
            diceLabels[i].setFont(new Font("Arial", Font.BOLD, 20));
            diceLabels[i].setOpaque(true);
            diceLabels[i].setBackground(Color.WHITE);
            diceLabels[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            diceLabels[i].setPreferredSize(new Dimension(40, 40));
            final int dieIndex = i;
            diceLabels[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (rethrowsLeft > 0 && currentDice != null) {
                        diceLabels[dieIndex].setBackground(
                            diceLabels[dieIndex].getBackground().equals(Color.YELLOW) ? Color.WHITE : Color.YELLOW);
                    }
                }
            });
            dicePanel.add(diceLabels[i]);
        }

        rollButton = new JButton("Roll Dice");
        rollButton.setFont(new Font("Arial", Font.BOLD, 14));
        rollButton.addActionListener(e -> rollDiceForHuman());
        dicePanel.add(rollButton);

        rerollButton = new JButton("Reroll Selected");
        rerollButton.setFont(new Font("Arial", Font.BOLD, 14));
        rerollButton.setEnabled(false);
        rerollButton.addActionListener(e -> rerollSelectedDice());
        dicePanel.add(rerollButton);

        controlPanel.add(dicePanel, BorderLayout.CENTER);

        nextTurnButton = new JButton("Next Turn");
        nextTurnButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextTurnButton.setBackground(new Color(170, 200, 255));
        nextTurnButton.addActionListener(e -> nextTurn());
        controlPanel.add(nextTurnButton, BorderLayout.SOUTH);

        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        saveMenuItem = new JMenuItem("Save Game");
        saveMenuItem.addActionListener(e -> saveGameDialog());
        fileMenu.add(saveMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        updateBoardButtons();
        updateStatus();
    }

    // Fixed: Explicitly use java.util.List
    private java.util.List<int[]> getValidMoves() {
        java.util.List<int[]> valid = new java.util.ArrayList<>();
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                Square sq = game.board.squares[i][j];
                if (sq.isEmpty() && CombinationChecker.checkCombo(sq.combo, currentDice)) {
                    valid.add(new int[]{i, j});
                }
            }
        }
        return valid;
    }

    private boolean isValidMove(int row, int col) {
        java.util.List<int[]> valid = getValidMoves();
        for (int[] move : valid) {
            if (move[0] == row && move[1] == col) {
                return true;
            }
        }
        return false;
    }

    private void nextTurn() {
        if (currentRound > 10) {
            showFinalScores();
            return;
        }
        Player currentPlayer = game.players.get(currentPlayerIndex);
        statusLabel.setText("Round " + currentRound + " - " + currentPlayer.name + "'s turn");
        resetDicePanel();
        if (currentPlayer.isComputer) {
            processComputerTurn(currentPlayer);
        } else {
            processHumanTurn(currentPlayer);
        }
    }

    private void showFinalScores() {
        int highScore = -1;
        String winner = "";
        StringBuilder msg = new StringBuilder("<html>Final Scores:<br/>");
        for (Player p : game.players) {
            int s = game.board.scoreForPlayer(p);
            msg.append(p.name).append(": ").append(s).append("<br/>");
            if (s > highScore) {
                highScore = s;
                winner = p.name;
            }
        }
        msg.append("<br/><b>Winner: ").append(winner).append(" with ").append(highScore).append(" points!</b></html>");
        JOptionPane.showMessageDialog(this, msg.toString(), "Game Over", JOptionPane.INFORMATION_MESSAGE);
        nextTurnButton.setEnabled(false);
    }

    private void processComputerTurn(Player p) {
        currentDice = rollDice();
        String diceStr = "Computer roll: " + Arrays.toString(currentDice);
        if (p.difficulty.equalsIgnoreCase("Hard")) {
            int currentScore = getMaxValidSquareBaseScore(currentDice);
            int[] newDice = rollDice();
            int newScore = getMaxValidSquareBaseScore(newDice);
            if (newScore > currentScore) {
                currentDice = newDice;
                diceStr += "\n(Hard) rethrow: " + Arrays.toString(currentDice);
            }
        }
        updateDiceDisplay(diceStr);

        // Fixed: Explicitly use java.util.List
        if (CombinationChecker.isSixOfAKind(currentDice)) {
            java.util.List<int[]> choices = new java.util.ArrayList<>();
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 7; j++) {
                    choices.add(new int[]{i, j});
                }
            }
            int[] choice = choices.get(new Random().nextInt(choices.size()));
            game.board.forcePlaceStone(choice[0], choice[1], p);
            JButton btn = boardButtons[choice[0]][choice[1]];
            btn.setText(p.stoneSymbol);
            btn.setForeground(Color.BLACK);
            btn.setFont(STONE_FONT);
            btn.setBackground(Color.LIGHT_GRAY);
            JOptionPane.showMessageDialog(this, p.name + " (Computer) placed stone at (Row " + (choice[0] + 1) +
                    ", Col " + (choice[1] + 1) + ")");
        } else if (CombinationChecker.isFiveOfAKind(currentDice) ||
                   CombinationChecker.isStraightOfSix(currentDice)) {
            java.util.List<int[]> free = game.board.getAllFreeSquares();
            if (!free.isEmpty()) {
                int[] choice = free.get(new Random().nextInt(free.size()));
                game.board.placeStone(choice[0], choice[1], p);
                JButton btn = boardButtons[choice[0]][choice[1]];
                btn.setText(p.stoneSymbol);
                btn.setForeground(Color.BLACK);
                btn.setFont(STONE_FONT);
                btn.setBackground(Color.LIGHT_GRAY);
                JOptionPane.showMessageDialog(this, p.name + " (Computer) placed stone at (Row " + (choice[0] + 1) +
                        ", Col " + (choice[1] + 1) + ")");
            } else {
                JOptionPane.showMessageDialog(this, p.name + " (Computer) has no free squares. Stone returned.");
            }
        } else {
            java.util.List<int[]> valid = getValidMoves();
            if (valid.isEmpty()) {
                JOptionPane.showMessageDialog(this, p.name + " (Computer) has no valid moves. Stone returned.");
            } else {
                int[] choice = valid.get(new Random().nextInt(valid.size()));
                game.board.placeStone(choice[0], choice[1], p);
                JButton btn = boardButtons[choice[0]][choice[1]];
                btn.setText(p.stoneSymbol);
                btn.setForeground(Color.BLACK);
                btn.setFont(STONE_FONT);
                btn.setBackground(Color.LIGHT_GRAY);
                JOptionPane.showMessageDialog(this, p.name + " (Computer) placed stone at (Row " + (choice[0] + 1) +
                        ", Col " + (choice[1] + 1) + ")");
            }
        }
        p.stonesLeft--;
        advanceTurn();
    }

    private void processHumanTurn(Player p) {
        rethrowsLeft = 2;
        rollButton.setEnabled(true);
        rerollButton.setEnabled(false);
        waitingForMove = false;
    }

    private void rollDiceForHuman() {
        currentDice = rollDice();
        updateDiceDisplay("Your roll: " + Arrays.toString(currentDice));
        rollButton.setEnabled(false);
        rerollButton.setEnabled(true);
        checkDiceResult();
    }

    private void rerollSelectedDice() {
        if (rethrowsLeft > 0 && currentDice != null) {
            // Fixed: Explicitly use java.util.List
            java.util.List<Integer> indicesToReroll = new java.util.ArrayList<>();
            for (int i = 0; i < 6; i++) {
                if (diceLabels[i].getBackground().equals(Color.YELLOW)) {
                    indicesToReroll.add(i);
                }
            }
            Random r = new Random();
            for (int idx : indicesToReroll) {
                currentDice[idx] = r.nextInt(6) + 1;
            }
            rethrowsLeft--;
            updateDiceDisplay("After reroll (" + rethrowsLeft + " left): " + Arrays.toString(currentDice));
            if (rethrowsLeft == 0) {
                rerollButton.setEnabled(false);
            }
            checkDiceResult();
        }
    }

    private void checkDiceResult() {
        if (CombinationChecker.isSixOfAKind(currentDice)) {
            JOptionPane.showMessageDialog(this,
                    "Special: Six-of-a-Kind! You may place on ANY square (override).");
            waitingForMove = true;
        } else if (CombinationChecker.isFiveOfAKind(currentDice) ||
                   CombinationChecker.isStraightOfSix(currentDice)) {
            JOptionPane.showMessageDialog(this,
                    "Special: Five-of-a-Kind or Straight-of-6! Place on any free square.");
            waitingForMove = true;
        } else {
            java.util.List<int[]> valid = getValidMoves();
            if (valid.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No valid square. Stone returned to box.");
                advanceTurn();
            } else {
                waitingForMove = true;
                JOptionPane.showMessageDialog(this, "Choose a valid square for your stone.");
            }
        }
    }

    private int[] rollDice() {
        int[] dice = new int[6];
        Random r = new Random();
        for (int i = 0; i < 6; i++) {
            dice[i] = r.nextInt(6) + 1;
        }
        return dice;
    }

    private int getMaxValidSquareBaseScore(int[] dice) {
        int maxScore = 0;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                Square sq = game.board.squares[i][j];
                if (sq.isEmpty() && CombinationChecker.checkCombo(sq.combo, dice)) {
                    maxScore = Math.max(maxScore, sq.baseScore);
                }
            }
        }
        return maxScore;
    }

    private void advanceTurn() {
        currentPlayerIndex++;
        if (currentPlayerIndex >= game.players.size()) {
            currentPlayerIndex = 0;
            currentRound++;
        }
        if (currentRound > 10) {
            showFinalScores();
            return;
        }
        resetDicePanel();
        updateBoardButtons();
        updateStatus();
    }

    private void updateBoardButtons() {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                Square sq = game.board.squares[i][j];
                JButton btn = boardButtons[i][j];
                if (!sq.isEmpty()) {
                    btn.setText(sq.owner.stoneSymbol);
                    btn.setForeground(Color.BLACK);
                    btn.setFont(STONE_FONT);
                    btn.setBackground(Color.LIGHT_GRAY);
                } else {
                    btn.setText(sq.label);
                    btn.setForeground(Color.BLACK);
                    btn.setFont(LABEL_FONT);
                    btn.setBackground(Color.WHITE);
                }
            }
        }
    }

    private void updateStatus() {
        statusLabel.setText("Round " + currentRound + " - Next turn ready.");
    }

    private void saveGameDialog() {
        JFileChooser chooser = new JFileChooser(".");
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filename = chooser.getSelectedFile().getAbsolutePath();
            game.saveGame(filename);
            JOptionPane.showMessageDialog(this, "Game saved to " + filename);
        }
    }

    private void updateDiceDisplay(String text) {
        statusLabel.setText(text);
        if (currentDice != null) {
            for (int i = 0; i < 6; i++) {
                diceLabels[i].setText(String.valueOf(currentDice[i]));
            }
        }
    }

    private void resetDicePanel() {
        for (int i = 0; i < 6; i++) {
            diceLabels[i].setText("?");
            diceLabels[i].setBackground(Color.WHITE);
        }
        rollButton.setEnabled(false);
        rerollButton.setEnabled(false);
    }
}