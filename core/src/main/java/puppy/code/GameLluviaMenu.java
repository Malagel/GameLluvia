package puppy.code;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import puppy.code.input.KeyboardPlayerInput;
import puppy.code.screen.GameOverScreen;
import puppy.code.screen.GameScreen;
import puppy.code.screen.LeaderboardScreen;
import puppy.code.screen.MainMenuScreen;
import puppy.code.screen.OptionsScreen;
import puppy.code.screen.PauseScreen;
import puppy.code.score.HighScoreManager;
import puppy.code.score.PreferencesScoreStore;
import puppy.code.settings.AudioSettings;
import puppy.code.settings.PreferencesSettingsStore;

public class GameLluviaMenu extends Game {
    private static final String SCORE_PREFERENCES = "game-lluvia-high-scores";
    private static final String SETTINGS_PREFERENCES = "game-lluvia-settings";

    private AudioSettings audioSettings;
    private GameAssets assets;
    private long nextGameId;

    @Override
    public void create() {
        audioSettings = new AudioSettings(
                new PreferencesSettingsStore(com.badlogic.gdx.Gdx.app.getPreferences(SETTINGS_PREFERENCES)));
        assets = new GameAssets(audioSettings);

        HighScoreManager.initialize(
                new PreferencesScoreStore(com.badlogic.gdx.Gdx.app.getPreferences(SCORE_PREFERENCES)));
        setScreen(new MainMenuScreen(this));
    }

    public GameAssets getAssets() {
        return assets;
    }

    public AudioSettings getAudioSettings() {
        return audioSettings;
    }

    public void startGame() {
        replaceScreen(new GameScreen(this, ++nextGameId, KeyboardPlayerInput.standard(Gdx.input)));
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

    public void showOptions() {
        replaceScreen(new OptionsScreen(this));
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
