package Gunner;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class UltimateBullet extends GameObject {

    protected static final int WIDTH = 150; // Increased size
    protected static final int HEIGHT = 150; // Increased size
    private static final double SPEED = 2; // Decreased speed
    private Image ultimateBulletImage;
    private boolean dead;

    public UltimateBullet(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        ultimateBulletImage = new Image("ultimate.png");
    }

    @Override
    public double getWidth() {
        return WIDTH;
    }

    @Override
    public double getHeight() {
        return HEIGHT;
    }

    @Override
    public void update() {
        y -= SPEED;
        if (y + HEIGHT < 0) {
            setDead(true);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(ultimateBulletImage, x - (WIDTH / 2), y - (HEIGHT / 2), WIDTH, HEIGHT);
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isDead() {
        return dead;
    }
}
