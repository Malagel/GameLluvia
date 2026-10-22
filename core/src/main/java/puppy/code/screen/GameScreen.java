package puppy.code.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import puppy.code.GameAssets;
import puppy.code.GameConfig;
import puppy.code.GameLluviaMenu;
import puppy.code.gameplay.GameSession;
import puppy.code.model.FallingItem;
import puppy.code.model.GameplayEvent;
import puppy.code.model.Player;

public final class GameScreen extends ScreenAdapter {
    private final GameLluviaMenu game;
    private final long gameId;
    private final GameAssets assets;
    private final GameSession session;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final GlyphLayout layout;

    public GameScreen(GameLluviaMenu game, long gameId) {
        this.game = game;
        this.gameId = gameId;
        assets = game.getAssets();
        session = new GameSession();
        batch = assets.getBatch();
        font = assets.getFont();
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
        drawHud();

        GameSession.UpdateResult result = session.update(delta, isMovingLeft(), isMovingRight());
        playEventSounds(result);
        drawPlayer();
        drawFallingItems();
        batch.end();

        if (result.isGameOver()) {
            game.showGameOver(gameId, session.getScore());
        }
    }

    private boolean isMovingLeft() {
        return Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
    }

    private boolean isMovingRight() {
        return Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
    }

    private void drawHud() {
        float top = GameConfig.WORLD_HEIGHT - 6.0f;
        font.draw(batch, "Gotas totales: " + session.getScore(), 6.0f, top);

        String lives = "Vidas : " + session.getLives();
        layout.setText(font, lives);
        font.draw(batch, layout, GameConfig.WORLD_WIDTH - layout.width - 6.0f, top);
    }

    private void playEventSounds(GameSession.UpdateResult result) {
        for (GameplayEvent event : result.getEvents()) {
            switch (event) {
                case DROP_COLLECTED:
                    assets.playCollectSound();
                    break;
                case PLAYER_DAMAGED:
                    assets.playHurtSound();
                    break;
                default:
                    throw new IllegalStateException("Unhandled gameplay event: " + event);
            }
        }
    }

    private void drawPlayer() {
        float y = session.getPlayerY();
        if (session.isPlayerHurt()) {
            y += MathUtils.random(-5, 5);
        }
        batch.draw(assets.getPlayerTexture(), session.getPlayerX(), y, Player.SIZE, Player.SIZE);
    }

    private void drawFallingItems() {
        for (GameSession.FallingItemView item : session.getFallingItems()) {
            Texture texture = item.getType() == FallingItem.Type.BAD
                    ? assets.getBadDropTexture()
                    : assets.getGoodDropTexture();
            batch.draw(texture, item.getX(), item.getY(), FallingItem.SIZE, FallingItem.SIZE);
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

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}
