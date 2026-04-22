import java.lang.reflect.Field;

/**
 * ModelTester.java
 * This class tests the GameModel for various behaviors.
 * It uses reflection to access private fields for testing purposes.
 */
public class ModelTester {
    public static void main(String[] args) {
        GameModel model = new GameModel();

        try {
            // Get fields using reflection
            Field playerXField = GameModel.class.getDeclaredField("playerX");
            playerXField.setAccessible(true);

            Field playerBulletsField = GameModel.class.getDeclaredField("playerBullets");
            playerBulletsField.setAccessible(true);

            Field bulletXField = GameModel.class.getDeclaredField("bulletX");
            bulletXField.setAccessible(true);

            Field bulletYField = GameModel.class.getDeclaredField("bulletY");
            bulletYField.setAccessible(true);

            Field aliensField = GameModel.class.getDeclaredField("aliens");
            aliensField.setAccessible(true);

            Field alienXField = GameModel.class.getDeclaredField("alienX");
            alienXField.setAccessible(true);

            Field alienYField = GameModel.class.getDeclaredField("alienY");
            alienYField.setAccessible(true);

            Field scoreField = GameModel.class.getDeclaredField("score");
            scoreField.setAccessible(true);

            Field livesField = GameModel.class.getDeclaredField("lives");
            livesField.setAccessible(true);

            // Test 1: Player cannot move past left edge
            playerXField.setInt(model, 0);
            model.movePlayerLeft();
            int newX = (int) playerXField.get(model);
            if (newX == 0) {
                System.out.println("PASS: Player cannot move past left edge");
            } else {
                System.out.println("FAIL: Player moved past left edge to " + newX);
            }

            // Test 2: Player cannot move past right edge
            playerXField.setInt(model, 800 - 50);
            model.movePlayerRight();
            newX = (int) playerXField.get(model);
            if (newX == 800 - 50) {
                System.out.println("PASS: Player cannot move past right edge");
            } else {
                System.out.println("FAIL: Player moved past right edge to " + newX);
            }

            // Test 3: Firing adds bullets up to limit
            ArrayList<int[]> playerBullets = (ArrayList<int[]>) playerBulletsField.get(model);
            playerBullets.clear();
            model.firePlayerBullet();
            if (playerBullets.size() == 1) {
                System.out.println("PASS: Firing adds a bullet");
            } else {
                System.out.println("FAIL: Bullet not added");
            }

            // Test 4: Bullet that reaches the top is removed
            playerBullets.clear();
            playerBullets.add(new int[]{400, 0});
            model.update();
            if (playerBullets.isEmpty()) {
                System.out.println("PASS: Bullet reaching top is removed");
            } else {
                System.out.println("FAIL: Bullet not removed at top");
            }

            // Test 5: Destroying an alien increases the score
            boolean[][] aliens = (boolean[][]) aliensField.get(model);
            aliens[0][0] = true;
            int alienX = (int) alienXField.get(model);
            int alienY = (int) alienYField.get(model);
            playerBullets.clear();
            playerBullets.add(new int[]{alienX + 20, alienY + 15});
            scoreField.setInt(model, 0); // reset score
            model.update();
            int newScore = (int) scoreField.get(model);
            boolean alienDestroyed = !aliens[0][0];
            if (newScore == 10 && alienDestroyed) {
                System.out.println("PASS: Destroying an alien increases the score");
            } else {
                System.out.println("FAIL: Score=" + newScore + ", Alien destroyed=" + alienDestroyed);
            }

            // Test 6: Losing all lives triggers game-over state
            livesField.setInt(model, 0);
            boolean gameOver = model.isGameOver();
            if (gameOver) {
                System.out.println("PASS: Losing all lives triggers game-over");
            } else {
                System.out.println("FAIL: Game not over with 0 lives");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}