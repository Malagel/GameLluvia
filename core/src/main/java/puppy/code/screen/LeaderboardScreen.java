package puppy.code.screen;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import puppy.code.GameLluviaMenu;
import puppy.code.score.HighScoreManager;

public final class LeaderboardScreen extends BaseUiScreen {
    public LeaderboardScreen(GameLluviaMenu game) {
        super(game);
    }

    @Override
    protected String getTitle() {
        return "Puntuaciones";
    }

    @Override
    protected void buildContent(Table content, ScreenUi ui) {
        HighScoreManager ranking = HighScoreManager.getInstance();
        int count = ranking.getScoreCount();
        if (count == 0) {
            content.add(ui.label("Aún no hay puntuaciones.")).padBottom(20.0f);
            content.row();
        } else {
            for (int position = 0; position < count; position++) {
                content.add(ui.label((position + 1) + ".  " + ranking.getScore(position)))
                        .pad(3.0f);
                content.row();
            }
            content.add().height(14.0f);
            content.row();
        }

        content.add(ui.button("Volver", getGame()::showMainMenu))
                .width(ScreenUi.BACK_BUTTON_WIDTH)
                .height(ScreenUi.BUTTON_HEIGHT)
                .padTop(5.0f);
    }
}
