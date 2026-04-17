import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnakeGame {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GamePanel panel = new GamePanel();
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    enum GameState {
        START,
        PLAYING,
        GAME_OVER
    }

    static class GamePanel extends JPanel {
        private static final int GRID_SIZE = 20;
        private static final int CELL_SIZE = 30;
        private static final int BASE_TIMER_DELAY = 150;
        private static final int FOOD_COUNT = 5;

        private final List<Point> snake = new ArrayList<>();
        private final List<Point> foods = new ArrayList<>();
        private final Random random = new Random();
        private int dx = 1;
        private int dy = 0;
        private int score;
        private int highscore;
        private boolean running = true;
        private Timer timer;
        private GameState state = GameState.START;
        private int currentDelay = BASE_TIMER_DELAY;

        private final JButton startButton = new JButton("Start");
        private final JButton restartButton = new JButton("Restart");

        public GamePanel() {
            setPreferredSize(new Dimension(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE));
            setBackground(Color.DARK_GRAY);
            setFocusable(true);
            setLayout(null);

            loadHighscore();
            initButtons();
            initControls();
            initGame();
            initTimer();

            addHierarchyListener(e -> {
                if (isShowing()) {
                    requestFocusInWindow();
                }
            });
        }

        private void initButtons() {
            startButton.setBounds(200, 320, 200, 40);
            restartButton.setBounds(200, 320, 200, 40);

            startButton.addActionListener(e -> {
                state = GameState.PLAYING;
                startButton.setVisible(false);
                restartButton.setVisible(false);
                initGame();
                requestFocusInWindow();
                repaint();
            });

            restartButton.addActionListener(e -> {
                state = GameState.PLAYING;
                restartButton.setVisible(false);
                initGame();
                requestFocusInWindow();
                repaint();
            });

            add(startButton);
            add(restartButton);

            startButton.setVisible(true);
            restartButton.setVisible(false);
            startButton.setFocusable(false);
            restartButton.setFocusable(false);
        }

        private void loadHighscore() {
            try {
                File file = new File("highscore.txt");
                if (file.exists()) {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line = reader.readLine();
                    if (line != null) {
                        highscore = Integer.parseInt(line);
                    }
                    reader.close();
                }
            } catch (IOException | NumberFormatException e) {
                highscore = 0;
            }
        }

        private void saveHighscore() {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("highscore.txt"));
                writer.write(String.valueOf(highscore));
                writer.close();
            } catch (IOException e) {
                // Ignore
            }
        }

        private void playSound(String filename) {
            // Audio disabled - files not found in working directory
            // To enable: place gulping.wav and choking.wav in c:\Users\Engel\Final-Project\snake-game\
        }

        private void initGame() {
            score = 0;
            currentDelay = BASE_TIMER_DELAY;
            dx = 1;
            dy = 0;
            snake.clear();
            foods.clear();
            initSnake();
            spawnFoods();
            running = true;
        }

        private void initSnake() {
            int centerX = GRID_SIZE / 2;
            int centerY = GRID_SIZE / 2;
            snake.add(new Point(centerX - 1, centerY));
            snake.add(new Point(centerX, centerY));
            snake.add(new Point(centerX + 1, centerY));
        }

        private void spawnFoods() {
            while (foods.size() < FOOD_COUNT && snake.size() + foods.size() < GRID_SIZE * GRID_SIZE) {
                spawnFood();
            }
        }

        private void spawnFood() {
            if (snake.size() + foods.size() >= GRID_SIZE * GRID_SIZE) {
                return;
            }

            Point candidate;
            do {
                int x = random.nextInt(GRID_SIZE);
                int y = random.nextInt(GRID_SIZE);
                candidate = new Point(x, y);
            } while (snake.contains(candidate) || foods.contains(candidate));

            foods.add(candidate);
        }

        private void initControls() {
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (state == GameState.START) {
                        return;
                    }

                    if (state == GameState.GAME_OVER && e.getKeyCode() == KeyEvent.VK_R) {
                        state = GameState.PLAYING;
                        restartButton.setVisible(false);
                        initGame();
                        repaint();
                        return;
                    }

                    if (!running) {
                        return;
                    }

                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP:
                            if (dy == 0) {
                                dx = 0;
                                dy = -1;
                            }
                            break;
                        case KeyEvent.VK_DOWN:
                            if (dy == 0) {
                                dx = 0;
                                dy = 1;
                            }
                            break;
                        case KeyEvent.VK_LEFT:
                            if (dx == 0) {
                                dx = -1;
                                dy = 0;
                            }
                            break;
                        case KeyEvent.VK_RIGHT:
                            if (dx == 0) {
                                dx = 1;
                                dy = 0;
                            }
                            break;
                    }
                }
            });
        }

        private void initTimer() {
            timer = new Timer(currentDelay, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (state == GameState.PLAYING && running) {
                        moveSnake();
                        timer.setDelay(currentDelay);
                    }
                    repaint();
                }
            });
            timer.start();
        }

        private void moveSnake() {
            Point head = snake.get(snake.size() - 1);
            int nextX = head.x + dx;
            int nextY = head.y + dy;

            if (nextX < 0 || nextX >= GRID_SIZE || nextY < 0 || nextY >= GRID_SIZE) {
                endGame();
                return;
            }

            Point nextHead = new Point(nextX, nextY);

            if (snake.contains(nextHead)) {
                endGame();
                return;
            }

            snake.add(nextHead);

            int foodIndex = foods.indexOf(nextHead);
            if (foodIndex >= 0) {
                foods.remove(foodIndex);
                score++;
                spawnFood();
                if (random.nextDouble() < 0.1) {
                    playSound("choking.wav");
                } else {
                    playSound("gulping.wav");
                }
                currentDelay = Math.max(50, currentDelay - 2);
            } else {
                snake.remove(0);
            }
        }

        private void endGame() {
            running = false;
            state = GameState.GAME_OVER;
            restartButton.setVisible(true);
            if (score > highscore) {
                highscore = score;
                saveHighscore();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());

            if (state == GameState.START) {
                g.setColor(Color.WHITE);
                Font titleFont = g.getFont().deriveFont(Font.BOLD, 48f);
                g.setFont(titleFont);
                String title = "Snake";
                FontMetrics titleMetrics = g.getFontMetrics(titleFont);
                int titleX = (getWidth() - titleMetrics.stringWidth(title)) / 2;
                g.drawString(title, titleX, 150);

                Font infoFont = g.getFont().deriveFont(Font.PLAIN, 18f);
                g.setFont(infoFont);
                String highscoreText = "All-time Highscore: " + highscore;
                int highscoreX = (getWidth() - g.getFontMetrics(infoFont).stringWidth(highscoreText)) / 2;
                g.drawString(highscoreText, highscoreX, 220);

                String hintText = "Press Start to play";
                int hintX = (getWidth() - g.getFontMetrics(infoFont).stringWidth(hintText)) / 2;
                g.drawString(hintText, hintX, 260);
                return;
            }

            g.setColor(Color.GRAY);
            for (int i = 0; i <= GRID_SIZE; i++) {
                int coord = i * CELL_SIZE;
                g.drawLine(coord, 0, coord, GRID_SIZE * CELL_SIZE);
                g.drawLine(0, coord, GRID_SIZE * CELL_SIZE, coord);
            }

            for (Point pellet : foods) {
                drawDogFood(g, pellet.x * CELL_SIZE, pellet.y * CELL_SIZE);
            }

            for (int i = 0; i < snake.size(); i++) {
                Point segment = snake.get(i);
                int x = segment.x * CELL_SIZE;
                int y = segment.y * CELL_SIZE;
                float t = snake.size() > 1 ? (float) i / (snake.size() - 1) : 0f;
                Color bodyColor = blend(new Color(222, 160, 90), new Color(115, 74, 18), t);
                if (i == snake.size() - 1) {
                    drawDogHead(g, x, y, bodyColor);
                } else if (i == 0) {
                    drawDogTail(g, x, y, bodyColor);
                } else {
                    drawDogBody(g, x, y, bodyColor);
                }
            }

            if (running) {
                g.setColor(Color.WHITE);
                g.drawString("Score: " + score, 10, 20);
                Font smallFont = g.getFont().deriveFont(12f);
                g.setFont(smallFont);
                g.drawString("Highscore: " + highscore, 10, 35);
                g.setFont(g.getFont().deriveFont(Font.PLAIN));
            } else {
                String message = "Game Over - Score: " + score;
                Font originalFont = g.getFont();
                Font gameOverFont = originalFont.deriveFont(Font.BOLD, 36f);
                g.setFont(gameOverFont);
                FontMetrics metrics = g.getFontMetrics(gameOverFont);
                int messageWidth = metrics.stringWidth(message);
                int x = (getWidth() - messageWidth) / 2;
                int y = getHeight() / 2;
                g.setColor(Color.WHITE);
                g.drawString(message, x, y);

                Font resetFont = originalFont.deriveFont(Font.PLAIN, 18f);
                g.setFont(resetFont);
                String resetHint = "Press R or click Restart";
                int hintWidth = g.getFontMetrics(resetFont).stringWidth(resetHint);
                g.drawString(resetHint, (getWidth() - hintWidth) / 2, y + 30);
            }
        }

        private void drawDogBody(Graphics g, int x, int y, Color color) {
            g.setColor(color);
            g.fillRoundRect(x + 4, y + 8, CELL_SIZE - 8, CELL_SIZE - 16, 14, 14);
            g.setColor(color.darker());
            g.drawRoundRect(x + 4, y + 8, CELL_SIZE - 8, CELL_SIZE - 16, 14, 14);
        }

        private void drawDogHead(Graphics g, int x, int y, Color color) {
            g.setColor(color);
            g.fillRoundRect(x + 2, y + 6, CELL_SIZE - 4, CELL_SIZE - 12, 18, 18);
            g.setColor(color.darker());
            g.drawRoundRect(x + 2, y + 6, CELL_SIZE - 4, CELL_SIZE - 12, 18, 18);

            g.setColor(Color.BLACK);
            g.fillOval(x + 20, y + 10, 5, 5);
            g.fillOval(x + 20, y + 18, 5, 5);

            g.fillOval(x + 4, y + 8, 8, 8);
            g.fillOval(x + 4, y + 16, 8, 8);

            g.setColor(new Color(255, 220, 180));
            g.fillOval(x + 24, y + 14, 6, 4);
        }

        private void drawDogTail(Graphics g, int x, int y, Color color) {
            g.setColor(color);
            g.fillRoundRect(x + 16, y + 10, 12, 6, 12, 12);
            g.setColor(color.darker());
            g.drawRoundRect(x + 16, y + 10, 12, 6, 12, 12);
            drawDogBody(g, x, y, color);
        }

        private void drawDogFood(Graphics g, int x, int y) {
            g.setColor(new Color(139, 69, 19));
            g.fillOval(x + 6, y + 8, 6, 6);
            g.fillOval(x + 16, y + 10, 5, 5);
            g.fillOval(x + 10, y + 18, 7, 7);
            g.setColor(new Color(120, 60, 15));
            g.drawOval(x + 6, y + 8, 6, 6);
            g.drawOval(x + 16, y + 10, 5, 5);
            g.drawOval(x + 10, y + 18, 7, 7);
        }

        private Color blend(Color a, Color b, float t) {
            t = Math.min(1f, Math.max(0f, t));
            int r = (int) (a.getRed() + t * (b.getRed() - a.getRed()));
            int g = (int) (a.getGreen() + t * (b.getGreen() - a.getGreen()));
            int b2 = (int) (a.getBlue() + t * (b.getBlue() - a.getBlue()));
            return new Color(r, g, b2);
        }
    }
}