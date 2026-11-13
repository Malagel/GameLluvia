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
    protected void buildUi(Table root, ScreenUi ui) {
        root.add(ui.title("GAME OVER")).padBottom(12.0f);
        root.row();
        root.add(ui.label("Puntuación: " + score)).padBottom(20.0f);
        root.row();

        GameLluviaMenu game = getGame();
        ui.addMenuButton(root, "Reintentar", game::startGame);
        ui.addMenuButton(root, "Menú principal", game::showMainMenu);
    }
}
