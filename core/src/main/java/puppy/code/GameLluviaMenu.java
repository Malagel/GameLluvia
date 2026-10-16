package puppy.code;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import puppy.code.screen.GameOverScreen;
import puppy.code.screen.GameScreen;
import puppy.code.screen.MainMenuScreen;
import puppy.code.screen.PauseScreen;

public class GameLluviaMenu extends Game {
    private GameAssets assets;
    private int highScore;

    @Override
    public void create() {
        assets = new GameAssets();
        setScreen(new MainMenuScreen(this));
    }

    public GameAssets getAssets() {
        return assets;
    }

    public int getHighScore() {
        return highScore;
    }

    public void startGame() {
        replaceScreen(new GameScreen(this));
    }

    public void showGameOver(int score) {
        highScore = Math.max(highScore, score);
        replaceScreen(new GameOverScreen(this));
    }

    public void pauseGame(GameScreen gameScreen) {
        setScreen(new PauseScreen(this, gameScreen));
    }

    public void resumeGame(GameScreen gameScreen) {
        replaceScreen(gameScreen);
    }

    private void replaceScreen(Screen nextScreen) {
        Screen previousScreen = getScreen();
        setScreen(nextScreen);
        if (previousScreen != null) {
            previousScreen.dispose();
        }
    }

    @Override
    public void dispose() {
        Screen currentScreen = getScreen();
        if (currentScreen != null) {
            currentScreen.dispose();
        }
        assets.dispose();
    }
}
