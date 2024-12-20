import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Objects;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    private final int boardWidth = 360;
    private final int boardHeight = 640;

    // Images
    private final Image backgroundImg;
    private final Image birdImg;
    private final Image topPipeImg;
    private final Image bottomPipeImg;

    // Bird
    private final int birdX = boardWidth / 8;
    private final int birdY = boardHeight / 2;
    private final int birdWidth = 34;
    private final int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image image;

        Bird(Image image) {
            this.image = image;
        }

    }

    // Pipes
    private final int pipeX = boardWidth;
    private final int pipeY = 0;
    private final int pipeWidth = 64; // scaled by 1/6
    private final int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image image) {
            this.img = image;
        }
    }

    // game logic
    Bird bird;
    int velocityX = -4; // move pipe to left (simulate bird moving right)
    int velocityY = -0;
    int gravity = 1;

    private final ArrayList<Pipe> pipes;

    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    double score = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        //load image
        backgroundImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("Image/flappybirdbg.png"))).getImage();
        birdImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("Image/flappybird.png"))).getImage();
        topPipeImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("Image/toppipe.png"))).getImage();
        bottomPipeImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("Image/bottompipe.png"))).getImage();

        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<>();

        //place pipe timer
        placePipeTimer = new Timer(1500, e -> placePipes());
        placePipeTimer.start();

        //game timer
        gameLoop = new Timer(1000 / 60, this); // draw 60 frames/s
        gameLoop.start();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        draw(graphics);
    }

    public void placePipes() {
        /*
         * placing random topPipes and calculate the size and position of topPipe
         * using position the pipeY and pipeHeight to generate random pipe Size and
         * position every 1,5s
         */
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        //creates opening between top and bottom pipe
        int openingSpace = boardHeight / 4;

        Pipe toPipe = new Pipe(topPipeImg);
        toPipe.y = randomPipeY;
        pipes.add(toPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = toPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);

    }

    public void draw(Graphics graphics) {
        // draw image
        graphics.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        // bird
        graphics.drawImage(bird.image, bird.x, bird.y, bird.width, bird.height, null);

        for (Pipe pipe : pipes) {
            graphics.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //draw score and gameOver text at top left corner 
        graphics.setColor(Color.white);
        graphics.setFont(new Font("Arial", Font.PLAIN,32));
        if (gameOver) {
            graphics.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
        }
        else{
            graphics.drawString(String.valueOf((int) score),10,35);
        }
    }

    public void move() {
        // bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        // pipes
        for (Pipe pipe : pipes) {
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5; //0.5 because there 2 pipes(top and bottom) == 1point 
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        // Game over condition if bird position(Y) lower then boardHeight == if bird
        // falls out off-screen
        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }

    // check if bird collide with pipe then run gameOver condition
    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width && // a's top left corner doesn't reach b's top left corner
                a.x + a.width > b.x && // a's top right corner passes b's top left corner
                a.y < b.y + b.height && // a's top left corner doesn't reach b's bottom left corner
                a.y + a.height > b.y; // a's bottom left corner pass b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipeTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -6;
            if (gameOver) {
                //restart the game by resetting the conditions
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipeTimer.start();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}
