package Gunner;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.List;

public class Player extends GameObject {

    private static final int WIDTH = 20;
    private static final int HEIGHT = 20;
    private static final double SPEED = 3;
    private boolean moveLeft;
    private boolean moveRight;
    private boolean moveForward;
    private boolean moveBackward;
    private int health = 20;
    private Image playerImage;
    private int hitCount = 0;
    private static final int MAX_HITS = 2;
    private long lastUltTime; // To track the time of the last ultimate shot
    private static final long ULT_COOLDOWN = 5000; // Cooldown time in milliseconds

    @Override
    public double getWidth() {
        return WIDTH;
    }

    @Override
    public double getHeight() {
        return HEIGHT;
    }

    public Player(double x, double y) {
        super(x, y, 30, 30);
        playerImage = new Image("Up2.png");
        lastUltTime = System.currentTimeMillis() - ULT_COOLDOWN; // Initialize to allow immediate first shot
    }

    @Override
    public void update() {
        if (moveLeft && x - SPEED > 0) {
            x -= SPEED;
        }

        if (moveRight && x + width + SPEED < SpaceShooter.WIDTH) {
            x += SPEED;
        }

        if (moveForward && y - SPEED > 0) {
            y -= SPEED;
        }

        if (moveBackward && y + height + SPEED < SpaceShooter.HEIGHT) {
            y += SPEED;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(playerImage, x - (WIDTH / 2), y - (HEIGHT / 2), WIDTH * 1.5, HEIGHT * 1.5);
    }

    public void setMoveLeft(boolean moveLeft) {
        this.moveLeft = moveLeft;
    }

    public void setMoveRight(boolean moveRight) {
        this.moveRight = moveRight;
    }

    public void setMoveForward(boolean moveForward) {
        this.moveForward = moveForward;
    }

    public void setMoveBackward(boolean moveBackward) {
        this.moveBackward = moveBackward;
    }

    public void shoot(List<GameObject> newObjects) {
        Bullet bullet = new Bullet(x, y - HEIGHT / 2 - Bullet.HEIGHT);
        newObjects.add(bullet);
    }

    private boolean dead = false;

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    @Override
    public boolean isDead() {
        return dead;
    }

    public void takeDamage() {
        hitCount++;
        if (hitCount >= MAX_HITS) {
            setDead(true);
        }
    }

    public void shootULT(List<GameObject> newObjects) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUltTime >= ULT_COOLDOWN) {
            UltimateBullet ultimateBullet = new UltimateBullet(x, y - HEIGHT / 2 - UltimateBullet.HEIGHT / 2);
            newObjects.add(ultimateBullet);
            lastUltTime = currentTime; // Update the last shot time
        }
    }
}
