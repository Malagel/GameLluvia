package puppy.code.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import puppy.code.GameAssets;
import puppy.code.GameLluviaMenu;
import puppy.code.gameplay.GameSession;
import puppy.code.model.FallingItem;

public final class GameScreen extends ScreenAdapter {
    private final GameLluviaMenu game;
    private final GameAssets assets;
    private final GameSession session;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final OrthographicCamera camera;

    public GameScreen(GameLluviaMenu game) {
        this.game = game;
        assets = game.getAssets();
        session = new GameSession();
        batch = assets.getBatch();
        font = assets.getFont();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800.0f, 480.0f);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.0f, 0.0f, 0.2f, 1.0f);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        drawHud();

        GameSession.UpdateResult result = session.update(
                delta,
                Gdx.input.isKeyPressed(Input.Keys.LEFT),
                Gdx.input.isKeyPressed(Input.Keys.RIGHT));
        playEventSounds(result);
        drawPlayer();
        drawFallingItems();
        batch.end();

        if (result.isGameOver()) {
            game.showGameOver(session.getScore());
        }
    }

    private void drawHud() {
        font.draw(batch, "Gotas totales: " + session.getScore(), 5.0f, 475.0f);
        font.draw(batch, "Vidas : " + session.getLives(), 670.0f, 475.0f);
        font.draw(batch, "HighScore : " + game.getHighScore(), camera.viewportWidth / 2.0f - 50.0f, 475.0f);
    }

    private void playEventSounds(GameSession.UpdateResult result) {
        for (GameSession.GameplayEvent event : result.getEvents()) {
            if (event == GameSession.GameplayEvent.DROP_COLLECTED) {
                assets.playCollectSound();
            } else {
                assets.playHurtSound();
            }
        }
    }

    private void drawPlayer() {
        float y = session.getPlayerY();
        if (session.isPlayerHurt()) {
            y += MathUtils.random(-5, 5);
        }
        batch.draw(assets.getPlayerTexture(), session.getPlayerX(), y);
        session.advancePlayerHurtAnimation();
    }

    private void drawFallingItems() {
        for (FallingItem item : session.getFallingItems()) {
            Texture texture = item.getType() == FallingItem.Type.BAD
                    ? assets.getBadDropTexture()
                    : assets.getGoodDropTexture();
            batch.draw(texture, item.getX(), item.getY());
        }
    }

    @Override
    public void show() {
        assets.playRainMusic();
    }

    @Override
    public void hide() {
        assets.stopRainMusic();
    }

    @Override
    public void pause() {
        assets.stopRainMusic();
        game.pauseGame(this);
    }
}
