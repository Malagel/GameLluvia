package puppy.code.screen;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.Objects;
import puppy.code.GameAssets;
import puppy.code.GameConfig;
import puppy.code.GameLluviaMenu;
import puppy.code.gameplay.FallingItemConsumer;
import puppy.code.gameplay.GameSession;
import puppy.code.input.PlayerInput;
import puppy.code.model.FallingItem;
import puppy.code.model.Player;

public final class GameScreen extends ScreenAdapter {
    private static final String STORM_LABEL = "¡Tormenta! Gotas x2";

    private final GameLluviaMenu game;
    private final long gameId;
    private final GameAssets assets;
    private final GameSession session;
    private final PlayerInput playerInput;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final GlyphLayout layout;
    // Created once so drawing the items allocates nothing per frame.
    private final FallingItemConsumer itemDrawer = this::drawFallingItem;

    public GameScreen(GameLluviaMenu game, long gameId, PlayerInput playerInput) {
        this.game = game;
        this.gameId = gameId;
        this.playerInput = Objects.requireNonNull(playerInput, "playerInput");
        assets = game.getAssets();
        session = new GameSession(assets::playEventSound);
        batch = assets.getBatch();
        font = assets.getFont();
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        layout = new GlyphLayout();
    }

    @Override
    public void render(float delta) {
        session.update(delta, playerInput.isMovingLeft(), playerInput.isMovingRight());

        if (session.isStormActive()) {
            ScreenUtils.clear(0.04f, 0.04f, 0.09f, 1.0f);
        } else {
            ScreenUtils.clear(0.0f, 0.0f, 0.2f, 1.0f);
        }
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawPlayer();
        session.forEachFallingItem(itemDrawer);
        // Drawn last so falling drops never hide the score, lives, or storm indicator.
        drawHud();
        batch.end();

        if (session.isGameOver()) {
            game.showGameOver(gameId, session.getScore());
        }
    }

    private void drawHud() {
        float top = GameConfig.WORLD_HEIGHT - 6.0f;
        font.draw(batch, "Gotas totales: " + session.getScore(), 6.0f, top);

        String lives = "Vidas : " + session.getLives();
        layout.setText(font, lives);
        font.draw(batch, layout, GameConfig.WORLD_WIDTH - layout.width - 6.0f, top);

        if (session.isStormActive()) {
            layout.setText(font, STORM_LABEL, Color.GOLD, 0.0f, Align.left, false);
            font.draw(batch, layout, (GameConfig.WORLD_WIDTH - layout.width) / 2.0f, top);
        }
    }

    private void drawPlayer() {
        float y = session.getPlayerY();
        if (session.isPlayerHurt()) {
            y += MathUtils.random(-5, 5);
        }
        batch.draw(assets.getPlayerTexture(), session.getPlayerX(), y, Player.SIZE, Player.SIZE);
    }

    private void drawFallingItem(FallingItem.Type type, float x, float y) {
        batch.draw(assets.getDropTexture(type), x, y, FallingItem.SIZE, FallingItem.SIZE);
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

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}
