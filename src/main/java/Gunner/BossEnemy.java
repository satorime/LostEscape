package Gunner;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;

public class BossEnemy extends Enemy {

    private int health = 3;

    private int WIDTH;

    private int HEIGHT;

    private int numHits = 5;

    private Image boss;

    public BossEnemy(double x, double y) {
        super(x, y);
        SPEED = 1.0;
        WIDTH = 50;
        HEIGHT = 50;

        boss = new Image("boss.png");
    }


    @Override
    public void update() {
        if (y < 40) {
            y += SPEED;
        }
    }

    public void takeDamage() {
        health--;
        if (health <= 0) {
            setDead(true);
        }
    }

    public void shoot(List<GameObject> newObjects) {
        if (Math.random() < 0.015) {
            newObjects.add(new EnemyBullet(x, y + HEIGHT / 2));
        }
    }

    public boolean isDead() {
        return health <= 0;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(boss, x - WIDTH / 2, y - HEIGHT / 2, WIDTH * 2, HEIGHT * 2);
    }

}
