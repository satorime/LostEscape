package Gunner;

import Gunner.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class EnemyBullet extends GameObject {

    private static final double IMAGE_DELAY = 0.2;
    private double timeSinceLastImage = 0;
    private long lastUpdateTime = System.currentTimeMillis();
    public static final int WIDTH = 25;
    public static final int HEIGHT = 25;
    private static final double SPEED = 1;
    private Image[] images;
    private int currentFrame = 0;

    public EnemyBullet(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        images = new Image[] {
                new Image("bossbullet.png"),
                new Image("bossbullet1.png"),
                new Image("bossbullet2.png"),
                new Image("bossbullet3.png"),
        };
    }

    @Override
    public void update() {
        long currentTime = System.currentTimeMillis();
        double deltaTime = (currentTime - lastUpdateTime) / 1000.0;
        lastUpdateTime = currentTime;

        y += SPEED;
        timeSinceLastImage += deltaTime;
        if (timeSinceLastImage >= IMAGE_DELAY) {
            currentFrame = (currentFrame + 1) % images.length;
            timeSinceLastImage = 0;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(images[currentFrame], x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);
    }

    @Override
    public double getWidth() {
        return WIDTH;
    }

    @Override
    public double getHeight() {
        return HEIGHT;
    }

    private boolean dead = false;

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    @Override
    public boolean isDead() {
        return dead;
    }
}
