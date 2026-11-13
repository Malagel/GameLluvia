package puppy.code.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import puppy.code.GameLluviaMenu;

public final class MainMenuScreen extends BaseUiScreen {
    private static final String OBJECTIVE =
            "Flechas o A/D: atrapa las gotas azules y esquiva las rojas";

    public MainMenuScreen(GameLluviaMenu game) {
        super(game);
    }

    @Override
    protected void buildUi(Table root, ScreenUi ui) {
        Label title = ui.title("Game Lluvia");
        title.setFontScale(1.6f);
        root.add(title).padBottom(8.0f);
        root.row();

        Label objective = ui.label(OBJECTIVE);
        objective.setFontScale(0.75f);
        root.add(objective).padBottom(14.0f);
        root.row();

        GameLluviaMenu game = getGame();
        ui.addMenuButton(root, "Jugar", game::startGame);
        ui.addMenuButton(root, "Puntuaciones", game::showLeaderboard);
        ui.addMenuButton(root, "Opciones", game::showOptions);
        ui.addMenuButton(root, "Salir", () -> Gdx.app.exit());
    }
}
