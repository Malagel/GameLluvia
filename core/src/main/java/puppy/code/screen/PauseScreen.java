package puppy.code.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import puppy.code.GameConfig;
import puppy.code.GameLluviaMenu;

public final class PauseScreen extends ScreenAdapter {
    private final GameLluviaMenu game;
    private final GameScreen gameScreen;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final GlyphLayout layout;

    public PauseScreen(GameLluviaMenu game, GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
        batch = game.getAssets().getBatch();
        font = game.getAssets().getFont();
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        layout = new GlyphLayout();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.0f, 0.0f, 1.0f, 0.5f);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawCentered("Juego en Pausa", GameConfig.WORLD_HEIGHT / 2.0f + 20.0f);
        drawCentered("Toca en cualquier lado para continuar !!!", GameConfig.WORLD_HEIGHT / 2.0f - 30.0f);
        batch.end();

        if (Gdx.input.isTouched()) {
            game.resumeGame(gameScreen);
        }
    }

    private void drawCentered(String text, float y) {
        layout.setText(font, text);
        font.draw(batch, layout, (GameConfig.WORLD_WIDTH - layout.width) / 2.0f, y);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}
