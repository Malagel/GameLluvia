package puppy.code.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import puppy.code.GameLluviaMenu;

public final class GameOverScreen extends ScreenAdapter {
    private final GameLluviaMenu game;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final OrthographicCamera camera;

    public GameOverScreen(GameLluviaMenu game) {
        this.game = game;
        batch = game.getAssets().getBatch();
        font = game.getAssets().getFont();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800.0f, 480.0f);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.0f, 0.0f, 0.2f, 1.0f);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.draw(batch, "GAME OVER ", 100.0f, 200.0f);
        font.draw(batch, "Toca en cualquier lado para reiniciar.", 100.0f, 100.0f);
        batch.end();

        if (Gdx.input.isTouched()) {
            game.startGame();
        }
    }
}
