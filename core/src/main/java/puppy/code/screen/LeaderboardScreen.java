package puppy.code.screen;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import puppy.code.GameLluviaMenu;
import puppy.code.score.HighScoreManager;

public final class LeaderboardScreen extends BaseUiScreen {
    public LeaderboardScreen(GameLluviaMenu game) {
        super(game);
    }

    @Override
    protected void buildUi(Table root, ScreenUi ui) {
        root.add(ui.title("Puntuaciones")).padBottom(18.0f);
        root.row();

        HighScoreManager ranking = HighScoreManager.getInstance();
        int count = ranking.getScoreCount();
        if (count == 0) {
            root.add(ui.label("Aún no hay puntuaciones.")).padBottom(20.0f);
            root.row();
        } else {
            for (int position = 0; position < count; position++) {
                root.add(ui.label((position + 1) + ".  " + ranking.getScore(position)))
                        .pad(3.0f);
                root.row();
            }
            root.add().height(14.0f);
            root.row();
        }

        root.add(ui.button("Volver", getGame()::showMainMenu))
                .width(ScreenUi.BACK_BUTTON_WIDTH)
                .height(ScreenUi.BUTTON_HEIGHT)
                .padTop(5.0f);
    }
}
