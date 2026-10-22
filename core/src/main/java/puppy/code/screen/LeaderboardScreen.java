package puppy.code.screen;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import java.util.List;
import puppy.code.GameAssets;
import puppy.code.GameLluviaMenu;

public final class LeaderboardScreen extends BaseUiScreen {
    public LeaderboardScreen(GameLluviaMenu game) {
        super(game);
    }

    @Override
    protected void buildUi(Stage stage, GameAssets assets) {
        Label.LabelStyle labelStyle = ScreenUi.labelStyle(assets.getFont());
        TextButton.TextButtonStyle buttonStyle =
                ScreenUi.buttonStyle(assets.getFont(), assets.getUiTexture());

        Label title = new Label("Puntuaciones", labelStyle);
        title.setFontScale(1.5f);

        Table content = new Table();
        content.setFillParent(true);
        content.add(title).padBottom(18.0f);
        content.row();

        List<Integer> scores = getGame().getHighScoreManager().getScores();
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
                getGame().showMainMenu();
            }
        });
        content.add(backButton).width(220.0f).height(48.0f).padTop(5.0f);
        stage.addActor(content);
    }
}
