import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * GameController.java
 * This class acts as the controller for the Space Invaders game.
 * It contains the main method to start the application.
 * It wires together the GameModel and GameView, handles user input, and manages the game loop.
 */
public class GameController {
    private GameModel model;
    private GameView view;
    private JFrame frame;
    private Timer timer;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    public GameController() {
        model = new GameModel();
        view = new GameView(model);

        frame = new JFrame("Space Invaders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(view);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setFocusable(true);

        // Set up input listeners
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT) {
                    leftPressed = true;
                } else if (key == KeyEvent.VK_RIGHT) {
                    rightPressed = true;
                } else if (key == KeyEvent.VK_SPACE) {
                    model.firePlayerBullet();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT) {
                    leftPressed = false;
                } else if (key == KeyEvent.VK_RIGHT) {
                    rightPressed = false;
                }
            }
        });

        // Start the game loop
        timer = new Timer(50, e -> {
            if (leftPressed) model.movePlayerLeft();
            if (rightPressed) model.movePlayerRight();
            model.update();
            view.repaint();
            if (model.isGameOver()) {
                if (model.getScore() > model.getHighScore()) {
                    model.setHighScore(model.getScore());
                }
                timer.stop();
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        new GameController();
    }
}