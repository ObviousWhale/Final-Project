import java.util.ArrayList;

/**
 * GameModel.java
 * This class represents the model of the Space Invaders game.
 * It contains the game state, including positions of invaders, player, bullets, etc.
 * It handles game logic such as movement, collision detection, scoring, and game over conditions.
 * This class has no Swing imports and is independent of the view.
 */
public class GameModel {
    // Constants
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    private static final int PLAYER_WIDTH = 50;
    private static final int PLAYER_HEIGHT = 30;
    private static final int PLAYER_SPEED = 7;
    private static final int ALIEN_WIDTH = 40;
    private static final int ALIEN_HEIGHT = 30;
    private static final int ALIEN_ROWS = 5;
    private static final int ALIEN_COLS = 11;
    private static final int ALIEN_SPEED = 2;
    private static final int ALIEN_DROP = 20;
    private static final int BULLET_SPEED = 5;

    // Game state variables
    private int playerX;
    private boolean[][] aliens;
    private int alienX;
    private int alienY;
    private int alienDir;
    private int bulletX;
    private int bulletY;
    private boolean bulletActive;
    private ArrayList<int[]> alienBullets;
    private ArrayList<int[]> playerBullets;
    private int score;
    private int lives;
    private int highScore;

    public GameModel() {
        playerX = SCREEN_WIDTH / 2;
        aliens = new boolean[ALIEN_ROWS][ALIEN_COLS];
        alienX = 50;
        alienY = 50;
        alienDir = 1;
        bulletActive = false;
        alienBullets = new ArrayList<>();
        playerBullets = new ArrayList<>();
        score = 0;
        lives = 3;
        highScore = 0;
        // Initialize all aliens as alive
        for (int r = 0; r < ALIEN_ROWS; r++) {
            for (int c = 0; c < ALIEN_COLS; c++) {
                aliens[r][c] = true;
            }
        }
    }

    // Move player left
    public void movePlayerLeft() {
        playerX -= PLAYER_SPEED;
        if (playerX < 0) playerX = 0;
    }

    // Move player right
    public void movePlayerRight() {
        playerX += PLAYER_SPEED;
        if (playerX > SCREEN_WIDTH - PLAYER_WIDTH) playerX = SCREEN_WIDTH - PLAYER_WIDTH;
    }

    // Fire player bullet if not too many already
    public void firePlayerBullet() {
        if (playerBullets.size() < 3) {
            int bx = playerX + PLAYER_WIDTH / 2;
            int by = SCREEN_HEIGHT - PLAYER_HEIGHT;
            playerBullets.add(new int[]{bx, by});
        }
    }

    // Update game state each tick
    public void update() {
        // Advance player bullets
        for (int i = playerBullets.size() - 1; i >= 0; i--) {
            int[] b = playerBullets.get(i);
            b[1] -= BULLET_SPEED;
            if (b[1] < 0) playerBullets.remove(i);
        }

        // Advance alien bullets
        for (int i = alienBullets.size() - 1; i >= 0; i--) {
            int[] b = alienBullets.get(i);
            b[1] += BULLET_SPEED;
            if (b[1] > SCREEN_HEIGHT) alienBullets.remove(i);
        }

        // Move aliens
        moveAliens();

        // Check collisions
        checkCollisions();

        // Randomly fire alien bullet
        if (Math.random() < 0.01) { // 1% chance per tick
            fireAlienBullet();
        }
    }

    private void moveAliens() {
        alienX += alienDir * ALIEN_SPEED;
        boolean hitEdge = false;
        for (int r = 0; r < ALIEN_ROWS; r++) {
            for (int c = 0; c < ALIEN_COLS; c++) {
                if (aliens[r][c]) {
                    int ax = alienX + c * (ALIEN_WIDTH + 10);
                    if (alienDir == 1 && ax + ALIEN_WIDTH >= SCREEN_WIDTH) hitEdge = true;
                    if (alienDir == -1 && ax <= 0) hitEdge = true;
                }
            }
        }
        if (hitEdge) {
            alienDir = -alienDir;
            alienY += ALIEN_DROP;
        }
    }

    private void fireAlienBullet() {
        ArrayList<int[]> alive = new ArrayList<>();
        for (int r = 0; r < ALIEN_ROWS; r++) {
            for (int c = 0; c < ALIEN_COLS; c++) {
                if (aliens[r][c]) {
                    int ax = alienX + c * (ALIEN_WIDTH + 10);
                    int ay = alienY + r * (ALIEN_HEIGHT + 10);
                    alive.add(new int[]{ax + ALIEN_WIDTH / 2, ay + ALIEN_HEIGHT});
                }
            }
        }
        if (!alive.isEmpty()) {
            int[] pos = alive.get((int) (Math.random() * alive.size()));
            alienBullets.add(new int[]{pos[0], pos[1]});
        }
    }

    private void checkCollisions() {
        // Player bullets vs aliens
        for (int i = playerBullets.size() - 1; i >= 0; i--) {
            int[] b = playerBullets.get(i);
            for (int r = 0; r < ALIEN_ROWS; r++) {
                for (int c = 0; c < ALIEN_COLS; c++) {
                    if (aliens[r][c]) {
                        int ax = alienX + c * (ALIEN_WIDTH + 10);
                        int ay = alienY + r * (ALIEN_HEIGHT + 10);
                        if (b[0] >= ax && b[0] <= ax + ALIEN_WIDTH &&
                            b[1] >= ay && b[1] <= ay + ALIEN_HEIGHT) {
                            aliens[r][c] = false;
                            playerBullets.remove(i);
                            score += 10;
                            return; // Only one collision per bullet per tick
                        }
                    }
                }
            }
        }

        // Alien bullets vs player
        for (int i = alienBullets.size() - 1; i >= 0; i--) {
            int[] b = alienBullets.get(i);
            if (b[0] >= playerX && b[0] <= playerX + PLAYER_WIDTH &&
                b[1] >= SCREEN_HEIGHT - PLAYER_HEIGHT && b[1] <= SCREEN_HEIGHT) {
                alienBullets.remove(i);
                lives--;
            }
        }
    }

    // Getters
    public int getPlayerX() { return playerX; }
    public boolean[][] getAliens() { return aliens; }
    public int getAlienX() { return alienX; }
    public int getAlienY() { return alienY; }
    public int getBulletX() { return bulletX; }
    public int getBulletY() { return bulletY; }
    public boolean isBulletActive() { return bulletActive; }
    public ArrayList<int[]> getAlienBullets() { return alienBullets; }
    public ArrayList<int[]> getPlayerBullets() { return playerBullets; }
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public int getHighScore() { return highScore; }
    public void setHighScore(int hs) { highScore = hs; }
    public boolean isGameOver() { return lives <= 0 || alienY + ALIEN_ROWS * (ALIEN_HEIGHT + 10) >= SCREEN_HEIGHT - PLAYER_HEIGHT; }
}