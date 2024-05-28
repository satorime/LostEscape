package FlappyRato.Game;

import java.util.ArrayList;

import FlappyRato.Asset;
import FlappyRato.FlappyBird;
import FlappyRato.GameObject;
import FlappyRato.Sprite;
import javafx.scene.canvas.GraphicsContext;

public class GameOver implements GameObject {
    private int WIDTH = 205;
    private int HEIGHT = 55;
    private Asset asset = new Asset("/images/game_over.png", WIDTH, HEIGHT);
    private Sprite sprite;

    public GameOver(double screenWidth, double screenHeight, GraphicsContext ctx) {
        sprite = new Sprite(asset);
        sprite.setPosX(screenWidth / 2 - WIDTH / 2);
        sprite.setPosY(40);
        sprite.setVel(0, 0);
        sprite.setCtx(ctx);
    }

    public void update(long now) {
    }

    public void render() {
        if (FlappyBird.gameEnded)
            sprite.render();
    }
}