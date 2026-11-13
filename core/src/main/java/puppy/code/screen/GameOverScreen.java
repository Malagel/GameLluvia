package puppy.code.screen;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import puppy.code.GameLluviaMenu;

public final class GameOverScreen extends BaseUiScreen {
    private final int score;

    public GameOverScreen(GameLluviaMenu game, int score) {
        super(game);
        this.score = score;
    }

    @Override
    protected String getTitle() {
        return "GAME OVER";
    }

    @Override
    protected void buildContent(Table content, ScreenUi ui) {
        content.add(ui.label("Puntuación: " + score)).padBottom(20.0f);
        content.row();

        GameLluviaMenu game = getGame();
        ui.addMenuButton(content, "Reintentar", game::startGame);
        ui.addMenuButton(content, "Menú principal", game::showMainMenu);
    }
}
