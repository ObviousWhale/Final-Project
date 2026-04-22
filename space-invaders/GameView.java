import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;

/**
 * GameView.java
 * This class represents the view of the Space Invaders game.
 * It extends JPanel and is responsible for rendering the game graphics.
 * It receives updates from the GameModel and draws the player, invaders, bullets, score, etc.
 */
public class GameView extends JPanel {
    private GameModel model;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public GameView(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw player
        g.setColor(Color.GREEN);
        int px = model.getPlayerX();
        int py = 600 - 30;
        int[] playerXPoints = {px, px + 25, px + 50};
        int[] playerYPoints = {py + 30, py, py + 30};
        g.fillPolygon(playerXPoints, playerYPoints, 3);

        // Draw aliens
        g.setColor(Color.RED);
        boolean[][] aliens = model.getAliens();
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 11; c++) {
                if (aliens[r][c]) {
                    int ax = model.getAlienX() + c * (40 + 10);
                    int ay = model.getAlienY() + r * (30 + 10);
                    int[] alienXPoints = {ax, ax + 20, ax + 40};
                    int[] alienYPoints = {ay, ay + 30, ay};
                    g.fillPolygon(alienXPoints, alienYPoints, 3);
                }
            }
        }

        // Draw player bullets
        g.setColor(Color.YELLOW);
        for (int[] b : model.getPlayerBullets()) {
            g.fillRect(b[0] - 2, b[1] - 5, 4, 10);
        }

        // Draw alien bullets
        g.setColor(Color.ORANGE);
        for (int[] b : model.getAlienBullets()) {
            g.fillRect(b[0] - 2, b[1] - 5, 4, 10);
        }

        // Draw score
        g.setColor(Color.WHITE);
        g.drawString("Score: " + model.getScore(), 10, 20);

        // Draw high score
        g.drawString("High Score: " + model.getHighScore(), 150, 20);

        // Draw lives
        g.drawString("Lives: " + model.getLives(), 350, 20);

        // Game over message
        if (model.isGameOver()) {
            g.setColor(Color.RED);
            g.drawString("Game Over", getWidth() / 2 - 50, getHeight() / 2);
        }
    }
}