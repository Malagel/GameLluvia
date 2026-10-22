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
import java.util.List;
import puppy.code.GameAssets;
import puppy.code.GameConfig;
import puppy.code.GameLluviaMenu;

public final class LeaderboardScreen extends ScreenAdapter {
    private final GameLluviaMenu game;
    private final Stage stage;

    public LeaderboardScreen(GameLluviaMenu game) {
        this.game = game;
        GameAssets assets = game.getAssets();
        stage = new Stage(
                new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, new OrthographicCamera()),
                assets.getBatch());
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        Label.LabelStyle labelStyle = ScreenUi.labelStyle(assets.getFont());
        TextButton.TextButtonStyle buttonStyle =
                ScreenUi.buttonStyle(assets.getFont(), assets.getUiTexture());

        Label title = new Label("Puntuaciones", labelStyle);
        title.setFontScale(1.5f);

        Table content = new Table();
        content.setFillParent(true);
        content.add(title).padBottom(18.0f);
        content.row();

        List<Integer> scores = game.getHighScoreManager().getScores();
        if (scores.isEmpty()) {
            content.add(new Label("Aún no hay puntuaciones.", labelStyle)).padBottom(20.0f);
            content.row();
        } else {
            for (int index = 0; index < scores.size(); index++) {
                content.add(new Label((index + 1) + ".  " + scores.get(index), labelStyle))
                        .pad(3.0f);
                content.row();
            }
            content.add().height(14.0f);
            content.row();
        }

        TextButton backButton = new TextButton("Volver", buttonStyle);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.showMainMenu();
            }
        });
        content.add(backButton).width(220.0f).height(48.0f).padTop(5.0f);
        stage.addActor(content);
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
