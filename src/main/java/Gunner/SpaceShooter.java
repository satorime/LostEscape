package Gunner;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SpaceShooter extends Application {

    public static final int WIDTH = 300;
    public static final int HEIGHT = 600;
    public static int numLives = 3;

    private int score = 0;
    private boolean bossExists = false;
    private boolean reset = false;
    private final Label scoreLabel = new Label("Score: " + score);
    private final Label lifeLabel = new Label("Lives: " + numLives);
    private final List<GameObject> gameObjects = new ArrayList<>();
    private final List<GameObject> newObjects = new ArrayList<>();
    private Player player = new Player(WIDTH / 2, HEIGHT - 40);
    private Pane root = new Pane();
    private Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK);
    private boolean levelUpMessageDisplayed = false;
    private boolean levelUpShown = false;
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.initStyle(StageStyle.UNDECORATED); // This line hides the close, minimize, and maximize buttons
        primaryStage.setScene(scene);
        primaryStage.setTitle("Space Shooter");
        primaryStage.setResizable(false);
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        scoreLabel.setTranslateX(10);
        scoreLabel.setTranslateY(10);
        scoreLabel.setTextFill(Color.BLACK);
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        root.getChildren().addAll(canvas, scoreLabel, lifeLabel);
        lifeLabel.setTranslateX(10);
        lifeLabel.setTranslateY(40);
        lifeLabel.setTextFill(Color.BLACK);
        lifeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gameObjects.add(player);
        initEventHandlers(scene);

        // Disable close button
        primaryStage.setOnCloseRequest(event -> {
            if (score < 200) {
                event.consume();
            }
        });

        AnimationTimer gameLoop = new AnimationTimer() {
            private long lastEnemySpawned = 0;
            private long lastPowerUpSpawned = 0;

            @Override
            public void handle(long now) {
                if (reset) {
                    this.start();
                    reset = false;
                }
                gc.clearRect(0, 0, WIDTH, HEIGHT);

                if (now - lastEnemySpawned > 1_000_000_000) {
                    spawnEnemy();
                    lastEnemySpawned = now;
                }

                if (now - lastPowerUpSpawned > 10_000_000_000L) {
                    spawnPowerUp();
                    lastPowerUpSpawned = now;
                }

                if (score >= 100 && score % 100 == 0) {
                    boolean bossExists = false;
                    for (GameObject obj : gameObjects) {
                        if (obj instanceof BossEnemy) {
                            bossExists = true;
                            break;
                        }
                    }
                    if (!bossExists) {
                        spawnBossEnemy();
                    }
                }

                checkCollisions();
                checkEnemiesReachingBottom();
                gameObjects.addAll(newObjects);
                newObjects.clear();

                for (GameObject obj : gameObjects) {
                    obj.update();
                    obj.render(gc);

                    if (obj instanceof BossEnemy) {
                        ((BossEnemy) obj).shoot(newObjects);
                    }
                }

                for (GameObject obj : gameObjects) {
                    obj.update();
                    obj.render(gc);
                }

                Iterator<GameObject> iterator = gameObjects.iterator();
                while (iterator.hasNext()) {
                    GameObject obj = iterator.next();
                    if (obj.isDead()) {
                        iterator.remove();
                    }
                }

                if (score >= 300) {
                    stop();
//                    primaryStage.close();
                    primaryStage.hide();
                    showTempMessage("You Win!", WIDTH / 2 - 40, HEIGHT / 2, 5);
                }
            }
        };

        gameLoop.start();
        primaryStage.show();
    }

    private void spawnEnemy() {
        Random random = new Random();
        int x = random.nextInt(WIDTH - 50) + 25;

        boolean bossExists = false;

        for (GameObject obj : gameObjects) {
            if (obj instanceof BossEnemy) {
                bossExists = true;
                break;
            }
        }

        if (!bossExists && score % 100 == 0 && score > 0) {
            BossEnemy boss = new BossEnemy(x, -50);
            gameObjects.add(boss);
            showTempMessage("How's your day, everyone?!", WIDTH / 3 - 30, HEIGHT / 2 - 100, 5);
        } else {
            Enemy enemy = new Enemy(x, -40);
            gameObjects.add(enemy);
        }
    }

    private void checkCollisions() {
        List<Bullet> bullets = new ArrayList<>();
        List<Enemy> enemies = new ArrayList<>();
        List<PowerUp> powerUps = new ArrayList<>();
        List<EnemyBullet> enemyBullets = new ArrayList<>();

        for (GameObject obj : gameObjects) {
            if (obj instanceof Bullet) {
                bullets.add((Bullet) obj);
            } else if (obj instanceof Enemy) {
                enemies.add((Enemy) obj);
            }else if (obj instanceof EnemyBullet) {
                enemyBullets.add((EnemyBullet) obj);
            } else if (obj instanceof PowerUp) {
                powerUps.add((PowerUp) obj);
            }
        }

        for (Bullet bullet : bullets) {
            for (Enemy enemy : enemies) {
                if (bullet.getBounds().intersects(enemy.getBounds())) {
                    bullet.setDead(true);
                    if (enemy instanceof BossEnemy) {
                        ((BossEnemy) enemy).takeDamage();
                        score += 20;
                    } else {
                        enemy.setDead(true);
                        score += 10;
                    }
                    scoreLabel.setText("Score: " + score);

                    if (score % 100 == 0) {
                        Enemy.SPEED += 1;
                    }
                }
            }

            for (PowerUp powerUp : powerUps) {
                if (bullet.getBounds().intersects(powerUp.getBounds())) {
                    bullet.setDead(true);
                    powerUp.setDead(true);
                    score += 30;
                    scoreLabel.setText("Score: " + score);
                }
            }
        }


        for (EnemyBullet bullet : enemyBullets) {
            if (bullet.getBounds().intersects(player.getBounds())) {
                handlePlayerDamage();
            }
        }

        if (score % 100 == 0 && score > 0 && !levelUpShown) {
//            showTempMessage("Level Up!", WIDTH / 2 - 40, HEIGHT * 2, 2);
            levelUpShown = true;
        } else if (score % 100 != 0) {
            levelUpShown = false;
        }

        checkScore();
    }

    private void handlePlayerDamage() {
        new Thread(() -> {
            Platform.runLater(() -> {
                showTempMessage("Agoi, hagbongonon", WIDTH / 3 - 30, HEIGHT / 2, 1.5);
            });
        }).start();
    }

    private void checkEnemiesReachingBottom() {
        List<Enemy> enemies = new ArrayList<>();
        for (GameObject obj : gameObjects) {
            if (obj instanceof Enemy) {
                enemies.add((Enemy) obj);
            }
        }

        for (Enemy enemy : enemies) {
            if (enemy.getY() + enemy.getHeight() / 2 >= HEIGHT) {
                enemy.setDead(true);
                enemy.SPEED = enemy.SPEED + 0.1;
                numLives--;
                score -= 10;
                lifeLabel.setText("Lives: " + numLives);
                if (numLives < 0) {
                    enemy.SPEED = 1;
                    resetGame();
                }
            }
        }
    }

    private void resetGame() {
        gameObjects.clear();
        numLives = 3;
        score = 0;
        lifeLabel.setText("Lives: " + numLives);
        scoreLabel.setText("Score: " + score);
        gameObjects.add(player);
        reset = true;
        Text lostMessage = new Text("You lost! Skill Issue~~");
        lostMessage.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        lostMessage.setFill(Color.RED);
        lostMessage.setX((WIDTH - lostMessage.getLayoutBounds().getWidth()) / 2);
        lostMessage.setY(HEIGHT / 2);
        root.getChildren().add(lostMessage);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
            root.getChildren().remove(lostMessage);
            initEventHandlers(scene);
        });
        pause.play();
    }

    private void initEventHandlers(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case A:
                case LEFT:
                    player.setMoveLeft(true);
                    break;
                case D:
                case RIGHT:
                    player.setMoveRight(true);
                    break;
                case S:
                    player.setMoveBackward(true);
                    break;
                case W:
                    player.setMoveForward(true);
                    break;
                case SPACE:
                    player.shoot(newObjects);
                    break;
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case A:
                case LEFT:
                    player.setMoveLeft(false);
                    break;
                case D:
                case RIGHT:
                    player.setMoveRight(false);
                    break;
                case S:
                    player.setMoveBackward(false);
                    break;
                case W:
                    player.setMoveForward(false);
                    break;
            }
        });
    }

    private void spawnPowerUp() {
        Random random = new Random();
        int x = random.nextInt(WIDTH - PowerUp.WIDTH) + PowerUp.WIDTH / 2;
        PowerUp powerUp = new PowerUp(x, -PowerUp.HEIGHT / 2);
        gameObjects.add(powerUp);
    }

    private void spawnBossEnemy() {
        if (gameObjects.stream().noneMatch(obj -> obj instanceof BossEnemy)) {
            BossEnemy bossEnemy = new BossEnemy(WIDTH / 2, -40);
            gameObjects.add(bossEnemy);
        }
    }

    private void checkScore() {
        if (this.score >= 100) {
            Text lostMessage = new Text("Reach 1500 to win!:)");
            lostMessage.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            lostMessage.setFill(Color.RED);
            lostMessage.setX((WIDTH - lostMessage.getLayoutBounds().getWidth()) / 2);
            lostMessage.setY(HEIGHT - 50);
            root.getChildren().add(lostMessage);
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event -> {
                root.getChildren().remove(lostMessage);
            });
            pause.play();
        }
    }

    private void showTempMessage(String message, double x, double y, double duration) {
        Text tempMessage = new Text(message);
        tempMessage.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        tempMessage.setFill(Color.RED);
        tempMessage.setX(x);
        tempMessage.setY(y);
        root.getChildren().add(tempMessage);

        PauseTransition pause = new PauseTransition(Duration.seconds(duration));
        pause.setOnFinished(event -> root.getChildren().remove(tempMessage));
        pause.play();
    }
}
