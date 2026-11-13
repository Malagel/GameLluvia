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
    protected String getTitle() {
        return "Game Lluvia";
    }

    @Override
    protected void buildContent(Table content, ScreenUi ui) {
        Label objective = ui.label(OBJECTIVE);
        objective.setFontScale(0.75f);
        content.add(objective).padBottom(14.0f);
        content.row();

        GameLluviaMenu game = getGame();
        ui.addMenuButton(content, "Jugar", game::startGame);
        ui.addMenuButton(content, "Puntuaciones", game::showLeaderboard);
        ui.addMenuButton(content, "Opciones", game::showOptions);
        ui.addMenuButton(content, "Salir", () -> Gdx.app.exit());
    }
}
