import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.RenderingHints;

public class PacmanView extends JPanel {
    private PacmanModel model;
    
    // UI Constants
    private final Color WALL_COLOR = new Color(33, 33, 222);
    private final Color PELLET_COLOR = Color.WHITE;
    private final Color PACMAN_COLOR = Color.YELLOW;

    private double visualPacmanX = -1;
    private double visualPacmanY = -1;
    private boolean isSpeedBoostActive = false;

    public void setSpeedBoostActive(boolean active) {
        this.isSpeedBoostActive = active;
    }

    public PacmanView(PacmanModel model) {
        this.model = model;
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (model == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        PacmanModel.GameState state = model.getCurrentState();
        
        if (state == PacmanModel.GameState.START_SCREEN) {
            drawStartScreen(g2d);
        } else if (state == PacmanModel.GameState.LEVEL_PICKER) {
            drawLevelPickerScreen(g2d);
        } else if (state == PacmanModel.GameState.PLAYING || state == PacmanModel.GameState.GAME_OVER || state == PacmanModel.GameState.LEVEL_TRANSITION) {
            if (model.getMaze() == null) return;
            drawGame(g2d);
            drawHUD(g2d);
            
            if (state == PacmanModel.GameState.GAME_OVER) {
                drawGameOverScreen(g2d);
            } else if (state == PacmanModel.GameState.LEVEL_TRANSITION) {
                drawLevelTransitionScreen(g2d);
            }
        }
    }

    private void drawStartScreen(Graphics2D g) {
        g.setColor(PACMAN_COLOR);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        String title = "PAC-MAN";
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(title)) / 2;
        int y = getHeight() / 2 - 50;
        g.drawString(title, x, y);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        String instruction = "Press ENTER to Start";
        FontMetrics fm2 = g.getFontMetrics();
        int instX = (getWidth() - fm2.stringWidth(instruction)) / 2;
        g.drawString(instruction, instX, y + 60);
    }

    private void drawLevelPickerScreen(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        String title = "Select Level";
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(title)) / 2;
        int y = getHeight() / 2 - 100;
        g.drawString(title, x, y);

        g.setFont(new Font("Arial", Font.PLAIN, 24));
        String options = "Press 1, 2, 3, 4, or 5 to begin.";
        FontMetrics fm2 = g.getFontMetrics();
        int optX = (getWidth() - fm2.stringWidth(options)) / 2;
        g.drawString(options, optX, y + 60);
    }

    private void drawGame(Graphics2D g) {
        int[][] maze = model.getMaze();
        int rows = maze.length;
        int cols = maze[0].length;
        
        // Calculate tile size so maze fits in the window with padding for HUD
        int panelWidth = getWidth();
        int panelHeight = getHeight() - 50; // Leave 50px for HUD at top
        
        int tileSize = Math.min(panelWidth / cols, panelHeight / rows);
        
        // Center the maze
        int offsetX = (panelWidth - (cols * tileSize)) / 2;
        int offsetY = 50 + (panelHeight - (rows * tileSize)) / 2;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                int tileX = offsetX + x * tileSize;
                int tileY = offsetY + y * tileSize;

                if (maze[y][x] == 1) {
                    g.setColor(WALL_COLOR);
                    g.fillRect(tileX, tileY, tileSize, tileSize);
                } else if (maze[y][x] == 2) {
                    g.setColor(PELLET_COLOR);
                    int pelletSize = tileSize / 4;
                    int pOffsetX = (tileSize - pelletSize) / 2;
                    int pOffsetY = (tileSize - pelletSize) / 2;
                    g.fillOval(tileX + pOffsetX, tileY + pOffsetY, pelletSize, pelletSize);
                }
            }
        }

        // Target pixel position
        int targetPx = offsetX + model.getPacmanX() * tileSize;
        int targetPy = offsetY + model.getPacmanY() * tileSize;
        
        if (visualPacmanX == -1 || Math.abs(visualPacmanX - targetPx) > tileSize * 1.5 || Math.abs(visualPacmanY - targetPy) > tileSize * 1.5) {
            visualPacmanX = targetPx;
            visualPacmanY = targetPy;
        } else {
            visualPacmanX += (targetPx - visualPacmanX) * 0.35;
            visualPacmanY += (targetPy - visualPacmanY) * 0.35;
        }

        int px = (int) visualPacmanX;
        int py = (int) visualPacmanY;
        
        if (isSpeedBoostActive) {
            g.setColor(new Color(255, 140, 0)); // Dark Orange
        } else {
            g.setColor(PACMAN_COLOR);
        }
        
        // Direction: 0=Up, 1=Right, 2=Down, 3=Left
        int startAngle = 0;
        int arcAngle = 300;
        
        switch (model.getCurrentDirection()) {
            case 0: startAngle = 120; break; // Up
            case 1: startAngle = 30; break;  // Right
            case 2: startAngle = 300; break; // Down
            case 3: startAngle = 210; break; // Left
        }
        
        g.fillArc(px + 2, py + 2, tileSize - 4, tileSize - 4, startAngle, arcAngle);
    }

    private void drawHUD(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        
        String scoreText = "Score: " + model.getScore();
        String levelText = "Level: " + model.getLevel() + "/5";
        String timeText = "Time: " + model.getTimeRemaining();
        
        g.drawString(scoreText, 20, 30);
        
        FontMetrics fm = g.getFontMetrics();
        int levelX = (getWidth() - fm.stringWidth(levelText)) / 2;
        g.drawString(levelText, levelX, 30);
        
        int timeX = getWidth() - fm.stringWidth(timeText) - 20;
        if (model.getTimeRemaining() <= 10) {
            g.setColor(Color.RED);
        }
        g.drawString(timeText, timeX, 30);

        if (isSpeedBoostActive) {
            g.setColor(new Color(255, 140, 0));
            String boostText = "SPEED BOOST!";
            int boostX = (getWidth() - fm.stringWidth(boostText)) / 2;
            g.drawString(boostText, boostX, 60);
        }
    }
    
    private void drawGameOverScreen(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 200)); // Semi-transparent overlay
        g.fillRect(0, 0, getWidth(), getHeight());
        
        g.setFont(new Font("Arial", Font.BOLD, 40));
        String mainText;
        if (model.getHasWon()) {
            g.setColor(Color.GREEN);
            mainText = "YOU WIN!";
        } else {
            g.setColor(Color.RED);
            mainText = "TIME'S UP!";
        }
        
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(mainText)) / 2;
        int y = getHeight() / 2;
        g.drawString(mainText, x, y);
        
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.setColor(Color.WHITE);
        String subText = "Final Score: " + model.getScore() + "  |  Press 'R' to Restart";
        FontMetrics subFm = g.getFontMetrics();
        int subX = (getWidth() - subFm.stringWidth(subText)) / 2;
        g.drawString(subText, subX, y + 40);
    }

    private void drawLevelTransitionScreen(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 200)); // Semi-transparent overlay
        g.fillRect(0, 0, getWidth(), getHeight());
        
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.setColor(Color.GREEN);
        String mainText = "Level " + model.getLevel() + " Complete!";
        
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(mainText)) / 2;
        int y = getHeight() / 2;
        g.drawString(mainText, x, y);
        
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.setColor(Color.WHITE);
        String subText = "Press ENTER to continue...";
        FontMetrics subFm = g.getFontMetrics();
        int subX = (getWidth() - subFm.stringWidth(subText)) / 2;
        g.drawString(subText, subX, y + 50);
    }

    public void refresh() {
        repaint();
    }
}
