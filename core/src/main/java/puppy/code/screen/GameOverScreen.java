package puppy.code.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import puppy.code.GameAssets;
import puppy.code.GameConfig;
import puppy.code.GameLluviaMenu;

public final class GameOverScreen extends ScreenAdapter {
    private static final float BUTTON_WIDTH = 270.0f;
    private static final float BUTTON_HEIGHT = 48.0f;

    private final GameLluviaMenu game;
    private final Stage stage;

    public GameOverScreen(GameLluviaMenu game, int score) {
        this.game = game;
        GameAssets assets = game.getAssets();
        stage = new Stage(
                new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, new OrthographicCamera()),
                assets.getBatch());
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        Label.LabelStyle labelStyle = ScreenUi.labelStyle(assets.getFont());
        TextButton.TextButtonStyle buttonStyle =
                ScreenUi.buttonStyle(assets.getFont(), assets.getUiTexture());

        Label title = new Label("GAME OVER", labelStyle);
        title.setFontScale(1.5f);
        Label scoreLabel = new Label("Puntuación: " + score, labelStyle);

        TextButton retryButton = new TextButton("Reintentar", buttonStyle);
        retryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.startGame();
            }
        });

        TextButton menuButton = new TextButton("Menú principal", buttonStyle);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.showMainMenu();
            }
        });

        Table menu = new Table();
        menu.setFillParent(true);
        menu.add(title).padBottom(12.0f);
        menu.row();
        menu.add(scoreLabel).padBottom(20.0f);
        menu.row();
        menu.add(retryButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(6.0f);
        menu.row();
        menu.add(menuButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(6.0f);
        stage.addActor(menu);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.0f, 0.0f, 0.2f, 1.0f);
        stage.act(Math.min(delta, 1.0f / 30.0f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        if (Gdx.input.getInputProcessor() == stage) {
            Gdx.input.setInputProcessor(null);
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
