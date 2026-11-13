package puppy.code.screen;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import java.util.List;
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

        List<Integer> scores = HighScoreManager.getInstance().getScores();
        if (scores.isEmpty()) {
            root.add(ui.label("Aún no hay puntuaciones.")).padBottom(20.0f);
            root.row();
        } else {
            for (int index = 0; index < scores.size(); index++) {
                root.add(ui.label((index + 1) + ".  " + scores.get(index))).pad(3.0f);
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
