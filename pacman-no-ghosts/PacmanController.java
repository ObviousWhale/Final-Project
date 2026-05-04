import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.BorderLayout;

public class PacmanController {
    private PacmanModel model;
    private PacmanView view;
    private Timer gameLoop;
    private Timer secondTimer;
    private Timer renderLoop;
    private static final int MOVEMENT_DELAY = 150; // ms per grid movement step
    
    private boolean isSpeedBoostActive = false;
    private long boostEndTime = 0;
    private long boostCooldownEndTime = 0;

    public PacmanController(PacmanModel model, PacmanView view) {
        this.model = model;
        this.view = view;
        
        setupKeyBindings();
        setupGameLoop();
    }

    public void start() {
        JFrame frame = new JFrame("Pac-Man (No Ghosts)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.add(view, BorderLayout.CENTER);
        frame.setVisible(true);
        
        view.requestFocusInWindow();
        
        gameLoop.start();
        secondTimer.start();
        renderLoop.start();
    }

    private void setupGameLoop() {
        gameLoop = new Timer(MOVEMENT_DELAY, e -> updateGame());
        
        // Timer for countdown limit (runs every 1000ms / 1 second)
        secondTimer = new Timer(1000, e -> {
            if (model.getCurrentState() == PacmanModel.GameState.PLAYING) {
                model.decrementTime();
            }
        });
        
        // Smooth render loop (~60 FPS)
        renderLoop = new Timer(16, e -> view.refresh());
    }

    private void setupKeyBindings() {
        view.setFocusable(true);
        view.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleInput(e);
            }
        });
    }

    private void handleInput(KeyEvent e) {
        int key = e.getKeyCode();
        char keyChar = Character.toLowerCase(e.getKeyChar());
        PacmanModel.GameState state = model.getCurrentState();
        
        if (state == PacmanModel.GameState.START_SCREEN) {
            if (key == KeyEvent.VK_ENTER) {
                model.setCurrentState(PacmanModel.GameState.LEVEL_PICKER);
            }
        } else if (state == PacmanModel.GameState.LEVEL_PICKER) {
            if (keyChar >= '1' && keyChar <= '5') {
                int levelIndex = keyChar - '0';
                resetBoost();
                model.startLevel(levelIndex);
            }
        } else if (state == PacmanModel.GameState.LEVEL_TRANSITION) {
            if (key == KeyEvent.VK_ENTER) {
                model.advanceLevel();
            }
        } else if (state == PacmanModel.GameState.PLAYING) {
            // Handle movement input
            if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W || keyChar == 'w') {
                model.setPacmanDirection(0);
            } else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D || keyChar == 'd') {
                model.setPacmanDirection(1);
            } else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S || keyChar == 's') {
                model.setPacmanDirection(2);
            } else if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A || keyChar == 'a') {
                model.setPacmanDirection(3);
            } 
        } else if (state == PacmanModel.GameState.GAME_OVER) {
            // Handle restart
            if (key == KeyEvent.VK_R) {
                model.setCurrentState(PacmanModel.GameState.LEVEL_PICKER);
            }
        }
    }

    private void updateGame() {
        if (model.getCurrentState() == PacmanModel.GameState.PLAYING) {
            long currentTime = System.currentTimeMillis();

            if (isSpeedBoostActive && currentTime >= boostEndTime) {
                isSpeedBoostActive = false;
                view.setSpeedBoostActive(false);
                gameLoop.setDelay(MOVEMENT_DELAY);
                boostCooldownEndTime = currentTime + 2000;
            }

            boolean pelletEaten = model.updateMovement();
            
            if (pelletEaten && !isSpeedBoostActive && currentTime >= boostCooldownEndTime) {
                if (Math.random() < 0.05) { // 5% chance
                    isSpeedBoostActive = true;
                    view.setSpeedBoostActive(true);
                    boostEndTime = currentTime + 1000;
                    gameLoop.setDelay(MOVEMENT_DELAY / 2);
                }
            }
            
            if (model.isLevelComplete()) {
                resetBoost();
                model.setCurrentState(PacmanModel.GameState.LEVEL_TRANSITION);
            }
        }
    }

    private void resetBoost() {
        isSpeedBoostActive = false;
        view.setSpeedBoostActive(false);
        gameLoop.setDelay(MOVEMENT_DELAY);
        boostEndTime = 0;
        boostCooldownEndTime = 0;
    }

    // Main method to construct the MVC and run the game
    public static void main(String[] args) {
        PacmanModel model = new PacmanModel();
        PacmanView view = new PacmanView(model);
        PacmanController controller = new PacmanController(model, view);
        
        controller.start();
    }
}
