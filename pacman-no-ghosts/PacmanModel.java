import java.util.ArrayList;
import java.util.List;

public class PacmanModel {
    public enum GameState { START_SCREEN, LEVEL_PICKER, PLAYING, LEVEL_TRANSITION, GAME_OVER }
    private GameState currentState = GameState.START_SCREEN;
    
    private int[][] maze;
    private int pacmanX;
    private int pacmanY;
    
    // 0=Up, 1=Right, 2=Down, 3=Left
    private int currentDirection = 1;
    private int bufferedDirection = 1;
    
    private int score;
    private int level = 1;
    private int timeRemaining;
    private int totalPellets;
    private boolean isGameOver;
    private boolean hasWon;

    private List<String[]> levels;

    public PacmanModel() {
        initLevels();
        initGame();
    }

    private void initLevels() {
        levels = new ArrayList<>();
        
        levels.add(new String[]{
            "   #####   ",
            "   #...#   ",
            "   #.#.#   ",
            "####.#.####",
            "#.........#",
            " .## P ##. ",
            "#.........#",
            "####.#.####",
            "   #.#.#   ",
            "   #...#   ",
            "   #####   "
        });

        levels.add(new String[]{
            "      #      ",
            "     #.#     ",
            "    #...#    ",
            "   #.#.#.#   ",
            "  #.......#  ",
            " #.#.###.#.# ",
            " .....P..... ",
            " #.#.###.#.# ",
            "  #.......#  ",
            "   #.#.#.#   ",
            "    #...#    ",
            "     #.#     ",
            "      #      "
        });

        levels.add(new String[]{
            "######       ######",
            "#....#       #....#",
            "#.####       ####.#",
            "#.###############.#",
            "#.................#",
            "#.#####.###.#####.#",
            "#.#####.#.#.#####.#",
            "#.......#.#.......#",
            "#.#####.#...#####.#",
            " .........P....... ",
            "#.#####.#...#####.#",
            "#.......#.#.......#",
            "#.#####.#.#.#####.#",
            "#.#####.###.#####.#",
            "#.................#",
            "#.###############.#",
            "#.####       ####.#",
            "#....#       #....#",
            "######       ######"
        });

        levels.add(new String[]{
            "          ###          ",
            "        ###.###        ",
            "      ###.....###      ",
            "    ###.........###    ",
            "  ###.............###  ",
            "###.###.###.###.###.###",
            " ........#...#........ ",
            "#.######.#...#.######.#",
            "#.#.................#.#",
            "#.#.###.###.###.###.#.#",
            "#.#...#.........#...#.#",
            "#.#...#.###.###.#...#.#",
            " .#...#....P....#...#. ",
            "#.#...#.###.###.#...#.#",
            "#.#...#.........#...#.#",
            "#.#.###.###.###.###.#.#",
            "#.#.................#.#",
            "#.######.#...#.######.#",
            " ........#...#........ ",
            "###.###.###.###.###.###",
            "  ###.............###  ",
            "    ###.........###    ",
            "      ###.....###      ",
            "        ###.###        ",
            "          ###          "
        });

        levels.add(new String[]{
            "       #############       ",
            "      #.............#      ",
            "     #.#..###.###..#.#     ",
            "    #...#....#....#...#    ",
            "   #.#..#..##.##..#..#.#   ",
            "  #.....................#  ",
            " #.##.###.#.#.#.#.###.##.# ",
            " ....#.....#.#.#.....#.... ",
            "#.##.#.###.#.#.#.###.#.##.#",
            " ........#.......#........ ",
            "####.###.#.#####.#.###.####",
            "   ###...................###   ",
            "####.#.#.###.#.###.#.#.####",
            "#......#.....#.....#......#",
            "#.####.#####.#.#####.####.#",
            " ............P............ ",
            "#.####.#####.#.#####.####.#",
            "#......#.....#.....#......#",
            "####.#.#.###.#.###.#.#.####",
            "   ###...................###   ",
            "####.###.#.#####.#.###.####",
            " ........#.......#........ ",
            "#.##.#.###.#.#.#.###.#.##.#",
            " ....#.....#.#.#.....#.... ",
            " #.##.###.#.#.#.#.###.##.# ",
            "  #.....................#  ",
            "   #.#..#..##.##..#..#.#   ",
            "    #...#....#....#...#    ",
            "     #.#..###.###..#.#     ",
            "      #.............#      ",
            "       #############       "
        });
    }

    public void initGame() {
        score = 0;
        level = 1;
        isGameOver = false;
        hasWon = false;
        currentState = GameState.START_SCREEN;
    }

    public void startLevel(int levelIndex) {
        score = 0;
        level = levelIndex;
        isGameOver = false;
        hasWon = false;
        loadLevel(levelIndex);
        currentState = GameState.PLAYING;
    }

    public void loadLevel(int levelIndex) {
        if (levelIndex > levels.size()) {
            isGameOver = true;
            hasWon = true;
            currentState = GameState.GAME_OVER;
            return;
        }
        
        String[] template = levels.get(levelIndex - 1);
        int rows = template.length;
        int cols = template[0].length();
        maze = new int[rows][cols];
        totalPellets = 0;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                char c = template[y].charAt(x);
                if (c == '#') {
                    maze[y][x] = 1; // Wall
                } else if (c == '.') {
                    maze[y][x] = 2; // Pellet
                    totalPellets++;
                } else if (c == 'P') {
                    maze[y][x] = 0; // Empty
                    pacmanX = x;
                    pacmanY = y;
                } else {
                    maze[y][x] = 0; // Empty
                }
            }
        }
        
        currentDirection = 1;
        bufferedDirection = 1;
        
        // Time limits: 30s for level 1, +15s per additional level
        timeRemaining = 30 + (levelIndex - 1) * 15;
    }

    public void decrementTime() {
        if (currentState == GameState.PLAYING && timeRemaining > 0) {
            timeRemaining--;
            if (timeRemaining <= 0) {
                isGameOver = true;
                currentState = GameState.GAME_OVER;
            }
        }
    }

    public boolean updateMovement() {
        if (isGameOver) return false;

        // Try buffered direction first
        if (canMove(bufferedDirection)) {
            currentDirection = bufferedDirection;
        }

        // Move in current direction if possible
        if (canMove(currentDirection)) {
            switch (currentDirection) {
                case 0: pacmanY--; break; // Up
                case 1: pacmanX++; break; // Right
                case 2: pacmanY++; break; // Down
                case 3: pacmanX--; break; // Left
            }
            
            // Handle horizontal wrapping (portals)
            if (pacmanX < 0) {
                pacmanX = maze[0].length - 1;
            } else if (pacmanX >= maze[0].length) {
                pacmanX = 0;
            }
        }
        
        return checkCollisions();
    }

    private boolean canMove(int direction) {
        int nextX = pacmanX;
        int nextY = pacmanY;
        switch (direction) {
            case 0: nextY--; break;
            case 1: nextX++; break;
            case 2: nextY++; break;
            case 3: nextX--; break;
        }
        
        // Bounds check
        if (nextX < 0 || nextX >= maze[0].length) {
            return true; // Allow horizontal wrapping
        }
        if (nextY < 0 || nextY >= maze.length) {
            return false; // No vertical portals
        }
        
        return maze[nextY][nextX] != 1; // 1 is wall
    }

    public void setPacmanDirection(int newDirection) {
        this.bufferedDirection = newDirection;
    }

    private boolean checkCollisions() {
        if (maze[pacmanY][pacmanX] == 2) { // Pellet
            maze[pacmanY][pacmanX] = 0;
            score += 10;
            totalPellets--;
            return true;
        }
        return false;
    }

    public boolean isLevelComplete() {
        return totalPellets == 0 && !hasWon;
    }
    
    public void advanceLevel() {
        level++;
        loadLevel(level);
        if (!isGameOver) {
            currentState = GameState.PLAYING;
        }
    }

    public boolean isTimeUp() {
        return timeRemaining <= 0;
    }

    // --- Getters ---
    public int[][] getMaze() { return maze; }
    public int getPacmanX() { return pacmanX; }
    public int getPacmanY() { return pacmanY; }
    public int getScore() { return score; }
    public int getLevel() { return level; }
    public int getTimeRemaining() { return timeRemaining; }
    public boolean getIsGameOver() { return isGameOver; }
    public boolean getHasWon() { return hasWon; }
    public int getCurrentDirection() { return currentDirection; }
    public GameState getCurrentState() { return currentState; }
    public void setCurrentState(GameState state) { this.currentState = state; }
}
