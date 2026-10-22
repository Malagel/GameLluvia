package puppy.code.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import puppy.code.GameAssets;
import puppy.code.GameLluviaMenu;

public final class MainMenuScreen extends BaseUiScreen {
    private static final float BUTTON_WIDTH = 270.0f;
    private static final float BUTTON_HEIGHT = 48.0f;

    public MainMenuScreen(GameLluviaMenu game) {
        super(game);
    }

    @Override
    protected void buildUi(Stage stage, GameAssets assets) {
        Label.LabelStyle labelStyle = ScreenUi.labelStyle(assets.getFont());
        TextButton.TextButtonStyle buttonStyle =
                ScreenUi.buttonStyle(assets.getFont(), assets.getUiTexture());

        Label title = new Label("Game Lluvia", labelStyle);
        title.setFontScale(1.6f);

        TextButton playButton = new TextButton("Jugar", buttonStyle);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGame().startGame();
            }
        });

        TextButton scoresButton = new TextButton("Puntuaciones", buttonStyle);
        scoresButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGame().showLeaderboard();
            }
        });

        TextButton optionsButton = new TextButton("Opciones - Próximamente", buttonStyle);
        optionsButton.setDisabled(true);

        TextButton exitButton = new TextButton("Salir", buttonStyle);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        Table menu = new Table();
        menu.setFillParent(true);
        menu.add(title).padBottom(28.0f);
        menu.row();
        menu.add(playButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(6.0f);
        menu.row();
        menu.add(scoresButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(6.0f);
        menu.row();
        menu.add(optionsButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(6.0f);
        menu.row();
        menu.add(exitButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(6.0f);
        stage.addActor(menu);
    }
}
