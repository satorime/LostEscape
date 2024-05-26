package Gunner;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Enemy extends GameObject {

    protected static final int WIDTH = 30;
    protected static final int HEIGHT = 30;
    public static double SPEED = 1;
    //enemy
    private Image enemy;

    public Enemy(double x, double y) {
        super(x, y, 30, 30);
        enemy = new Image("enemy.png");
    }
    @Override
    public void update() {
        y += SPEED;
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(enemy, x - (WIDTH / 2), y - (HEIGHT / 2), WIDTH * 1.5, HEIGHT * 1.5);
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