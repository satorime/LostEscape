package Gunner;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bullet extends GameObject {

    public static final int WIDTH = 4;
    public static final int HEIGHT = 20;
    private static final double SPEED = 7;

    public Bullet(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
    }

    @Override
    public void update() {
        y -= SPEED;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);
    }

    // In Bullet class
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