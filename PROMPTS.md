// I'm starting a Java game project in VS Code using Swing. Create a single file called SnakeGame.java with a main method that opens a JFrame window (600x600 pixels) titled "Snake". Add a JPanel subclass called GamePanel inside the frame. No game logic yet — just get the window to open.
// Add a 20x20 grid to GamePanel. Represent the snake as a sequence of grid cells and start it with 3 segments near the center, facing right. Each cell should be drawn as a 30x30 pixel square. Draw the snake in green and the background in dark gray.
// Make the snake move automatically using a Swing timer that ticks every 150 milliseconds — the snake should advance one cell per tick in its current direction. Add arrow key controls so the player can steer, but don't allow the snake to reverse direction. For now, have the snake wrap around the edges instead of dying. Make sure the panel can receive keyboard input.
//Add a food pellet that spawns at a random empty cell. When the snake eats it, grow by one segment and spawn new food. Add collision detection: hitting a wall or the snake's own body should end the game, stop movement, and show a "Game Over" message with the final score in the center of the screen. Display the current score in the top-left corner during play. When the game is over, let the player press R to reset everything and play again.
//i want you to add an all time highscore that sits right under where the score sits but i want it to be a slightly samller text size(its nice to see the highscore when playing games like this)

//make the speed increase a tiny tiny bit whenever you eat food (make it speed up a ton near the end and when you go to restart it feels deathly slow)

//add a start screen that also shows the all time highscore and a restart button for when you die(just so it doesnt insta go into the game when launched or insta go into a new game when you die )

//could you make the snake look like a weiner dog and the food little things of dog food(i have my weiner dog right next to me so it seemed fitting there also long little guys)

//make it so there are multiple things of food on the map(make the game progress a little quicker)