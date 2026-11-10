package puppy.code;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import puppy.code.screen.GameOverScreen;
import puppy.code.screen.GameScreen;
import puppy.code.screen.LeaderboardScreen;
import puppy.code.screen.MainMenuScreen;
import puppy.code.screen.PauseScreen;
import puppy.code.score.HighScoreManager;
import puppy.code.score.PreferencesScoreStore;

public class GameLluviaMenu extends Game {
    private static final String SCORE_PREFERENCES = "game-lluvia-high-scores";

    private GameAssets assets;
    private long nextGameId;

    @Override
    public void create() {
        assets = new GameAssets();
        // The singleton is created once here, the only place that knows how scores are stored.
        // From then on every screen reaches it through HighScoreManager.getInstance().
        HighScoreManager.initialize(
                new PreferencesScoreStore(com.badlogic.gdx.Gdx.app.getPreferences(SCORE_PREFERENCES)));
        setScreen(new MainMenuScreen(this));
    }

    public GameAssets getAssets() {
        return assets;
    }

    public void startGame() {
        replaceScreen(new GameScreen(this, ++nextGameId));
    }

    public void showGameOver(long completedGameId, int score) {
        HighScoreManager.getInstance().recordScore(completedGameId, score);
        replaceScreen(new GameOverScreen(this, score));
    }

    public void showMainMenu() {
        replaceScreen(new MainMenuScreen(this));
    }

    public void showLeaderboard() {
        replaceScreen(new LeaderboardScreen(this));
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
        HighScoreManager.shutdown();
    }
}
