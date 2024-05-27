package Gunner;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class PowerUp extends GameObject {

    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;

    private static final double SPEED = 2;

    private boolean isDead = false;
    private Image powerup;

    public PowerUp(double x, double y) {
        super(x, y, 30, 30);
        powerup = new Image("powerup.png");
    }

    @Override
    public void update() {
        y += SPEED;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(powerup, x - (WIDTH / 2), y - (HEIGHT / 2), WIDTH * 1.5, HEIGHT * 1.5);
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
    public boolean isDead() {
        return this.isDead;
    }

    public void setDead(boolean b) {
        this.isDead = b;
    }
    //
}
