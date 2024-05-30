package FlappyRato.Game;

import java.util.ArrayList;

import FlappyRato.Asset;
import FlappyRato.FlappyBird;
import FlappyRato.GameObject;
import FlappyRato.Sprite;
import com.example.lostescape.MusicManager;
import javafx.scene.canvas.GraphicsContext;

public class GameOver implements GameObject {
    private int WIDTH = 205;
    private int HEIGHT = 55;
    private Asset asset = new Asset("/images/game_over.png", WIDTH, HEIGHT);
    private Sprite sprite;

    private boolean soundEffectPlayed = false;
    private MusicManager musicManager;

    public GameOver(double screenWidth, double screenHeight, GraphicsContext ctx, MusicManager musicManager) {
        sprite = new Sprite(asset);
        sprite.setPosX(screenWidth / 2 - WIDTH / 2);
        sprite.setPosY(40);
        sprite.setVel(0, 0);
        sprite.setCtx(ctx);

        this.musicManager = musicManager;
    }

    public void update(long now) {
    }

    public void render() {
        if (FlappyBird.gameEnded && !soundEffectPlayed) {
            sprite.render();
            musicManager.playSoundEffect("sound/dead.mp3");
            soundEffectPlayed = true;
        }
    }
}
