package puppy.code.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import java.util.Objects;
import puppy.code.GameAssets;
import puppy.code.GameConfig;
import puppy.code.GameLluviaMenu;

/**
 * Defines the common Scene2D lifecycle for menu-style screens.
 *
 * <p>Subclasses provide only their actors in {@link #buildUi(Table, ScreenUi)}. The hook is
 * invoked lazily, after the concrete constructor has completed, while this class retains control
 * of the screen lifecycle, the root layout container, and the shared widget styles.</p>
 */
public abstract class BaseUiScreen extends ScreenAdapter {
    private static final float MAX_FRAME_DELTA = 1.0f / 30.0f;

    private final GameLluviaMenu game;
    private final Stage stage;
    private boolean uiInitialized;
    private boolean disposed;

    protected BaseUiScreen(GameLluviaMenu game) {
        this(game, createStage(game));
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    BaseUiScreen(GameLluviaMenu game, Stage stage) {
        this.game = Objects.requireNonNull(game, "game");
        this.stage = Objects.requireNonNull(stage, "stage");
    }

    private static Stage createStage(GameLluviaMenu game) {
        GameAssets assets = Objects.requireNonNull(game, "game").getAssets();
        return new Stage(
                new FitViewport(
                        GameConfig.WORLD_WIDTH,
                        GameConfig.WORLD_HEIGHT,
                        new OrthographicCamera()),
                assets.getBatch());
    }

    /**
     * Fills the screen's centred root table. The root is created, sized to the stage, and
     * added to it by this class; {@code ui} creates the shared styled widgets.
     */
    protected abstract void buildUi(Table root, ScreenUi ui);

    protected final GameLluviaMenu getGame() {
        return game;
    }

    @Override
    public final void show() {
        initializeUi();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public final void render(float delta) {
        initializeUi();
        ScreenUtils.clear(0.0f, 0.0f, 0.2f, 1.0f);
        stage.act(Math.min(delta, MAX_FRAME_DELTA));
        stage.draw();
    }

    @Override
    public final void resize(int width, int height) {
        requireActive();
        stage.getViewport().update(width, height, true);
    }

    @Override
    public final void hide() {
        unregisterInputProcessor();
    }

    @Override
    public final void dispose() {
        if (disposed) {
            return;
        }
        unregisterInputProcessor();
        stage.dispose();
        disposed = true;
    }

    private void initializeUi() {
        requireActive();
        if (!uiInitialized) {
            Table root = new Table();
            root.setFillParent(true);
            buildUi(root, new ScreenUi(game.getAssets()));
            stage.addActor(root);
            uiInitialized = true;
        }
    }

    private void unregisterInputProcessor() {
        if (Gdx.input.getInputProcessor() == stage) {
            Gdx.input.setInputProcessor(null);
        }
    }

    private void requireActive() {
        if (disposed) {
            throw new IllegalStateException("A disposed UI screen cannot be reused");
        }
    }
}
