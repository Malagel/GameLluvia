package puppy.code.screen;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import puppy.code.GameAssets;
import puppy.code.GameLluviaMenu;

public final class GameOverScreen extends BaseUiScreen {
    private static final float BUTTON_WIDTH = 270.0f;
    private static final float BUTTON_HEIGHT = 48.0f;

    private final int score;

    public GameOverScreen(GameLluviaMenu game, int score) {
        super(game);
        this.score = score;
    }

    @Override
    protected void buildUi(Stage stage, GameAssets assets) {
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
                getGame().startGame();
            }
        });

        TextButton menuButton = new TextButton("Menú principal", buttonStyle);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGame().showMainMenu();
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
}
