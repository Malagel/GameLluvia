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

public final class GameOverScreen extends ScreenAdapter {
    private final GameLluviaMenu game;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final GlyphLayout layout;

    public GameOverScreen(GameLluviaMenu game) {
        this.game = game;
        batch = game.getAssets().getBatch();
        font = game.getAssets().getFont();
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        layout = new GlyphLayout();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.0f, 0.0f, 0.2f, 1.0f);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawCentered("GAME OVER", GameConfig.WORLD_HEIGHT / 2.0f + 20.0f);
        drawCentered("Toca en cualquier lado para reiniciar.", GameConfig.WORLD_HEIGHT / 2.0f - 55.0f);
        batch.end();

        if (Gdx.input.isTouched()) {
            game.startGame();
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
