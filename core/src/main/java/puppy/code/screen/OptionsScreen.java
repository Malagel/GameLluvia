package puppy.code.screen;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import java.util.function.IntSupplier;
import puppy.code.GameAssets;
import puppy.code.GameLluviaMenu;
import puppy.code.settings.AudioSettings;

/**
 * Lets the player adjust music and sound effect volumes. Changes are saved as soon as
 * they are made, so leaving the screen needs no confirmation.
 */
public final class OptionsScreen extends BaseUiScreen {
    private static final float STEP_BUTTON_SIZE = 44.0f;
    private static final float NAME_WIDTH = 150.0f;
    private static final float VALUE_WIDTH = 90.0f;

    public OptionsScreen(GameLluviaMenu game) {
        super(game);
    }

    @Override
    protected void buildUi(Table root, ScreenUi ui) {
        AudioSettings settings = getGame().getAudioSettings();
        GameAssets assets = getGame().getAssets();

        root.add(ui.title("Opciones")).colspan(4).padBottom(24.0f);
        root.row();

        addVolumeRow(root, ui, "Música", settings::getMusicPercent,
                settings::decreaseMusic, settings::increaseMusic);
        addVolumeRow(root, ui, "Efectos", settings::getEffectsPercent,
                () -> {
                    settings.decreaseEffects();
                    assets.playCollectSound();
                },
                () -> {
                    settings.increaseEffects();
                    assets.playCollectSound();
                });

        root.add(ui.button("Volver", getGame()::showMainMenu))
                .colspan(4)
                .width(ScreenUi.BACK_BUTTON_WIDTH)
                .height(ScreenUi.BUTTON_HEIGHT)
                .padTop(24.0f);
    }

    private static void addVolumeRow(
            Table table,
            ScreenUi ui,
            String name,
            IntSupplier currentPercent,
            Runnable decrease,
            Runnable increase) {
        Label valueLabel = ui.label(formatPercent(currentPercent.getAsInt()));
        valueLabel.setAlignment(Align.center);
        Runnable refresh = () -> valueLabel.setText(formatPercent(currentPercent.getAsInt()));

        table.add(ui.label(name)).width(NAME_WIDTH).left();
        table.add(ui.button("-", () -> {
            decrease.run();
            refresh.run();
        })).size(STEP_BUTTON_SIZE).pad(4.0f);
        table.add(valueLabel).width(VALUE_WIDTH);
        table.add(ui.button("+", () -> {
            increase.run();
            refresh.run();
        })).size(STEP_BUTTON_SIZE).pad(4.0f);
        table.row();
    }

    private static String formatPercent(int percent) {
        return percent + "%";
    }
}
