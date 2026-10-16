package puppy.code.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import puppy.code.GameLluviaMenu;

public final class MainMenuScreen extends ScreenAdapter {
    private final GameLluviaMenu game;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final OrthographicCamera camera;

    public MainMenuScreen(GameLluviaMenu game) {
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
        font.draw(batch, "Bienvenido a Recolecta Gotas!!! ", 100.0f, camera.viewportHeight / 2.0f + 50.0f);
        font.draw(batch, "Toca en cualquier lugar para comenzar!", 100.0f, camera.viewportHeight / 2.0f - 50.0f);
        batch.end();

        if (Gdx.input.isTouched()) {
            game.startGame();
        }
    }
}
