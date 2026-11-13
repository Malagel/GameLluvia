package puppy.code.screen;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
    protected void buildUi(Stage stage, GameAssets assets) {
        Label.LabelStyle labelStyle = ScreenUi.labelStyle(assets.getFont());
        TextButton.TextButtonStyle buttonStyle =
                ScreenUi.buttonStyle(assets.getFont(), assets.getUiTexture());
        AudioSettings settings = getGame().getAudioSettings();

        Label title = new Label("Opciones", labelStyle);
        title.setFontScale(1.5f);

        Table content = new Table();
        content.setFillParent(true);
        content.add(title).colspan(4).padBottom(24.0f);
        content.row();

        addVolumeRow(content, "Música", settings::getMusicPercent,
                settings::decreaseMusic, settings::increaseMusic, labelStyle, buttonStyle);
        addVolumeRow(content, "Efectos", settings::getEffectsPercent,
                () -> {
                    settings.decreaseEffects();
                    assets.playCollectSound();
                },
                () -> {
                    settings.increaseEffects();
                    assets.playCollectSound();
                },
                labelStyle, buttonStyle);

        TextButton backButton = new TextButton("Volver", buttonStyle);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGame().showMainMenu();
            }
        });
        content.add(backButton).colspan(4).width(220.0f).height(48.0f).padTop(24.0f);
        stage.addActor(content);
    }

    private static void addVolumeRow(
            Table table,
            String name,
            IntSupplier currentPercent,
            Runnable decrease,
            Runnable increase,
            Label.LabelStyle labelStyle,
            TextButton.TextButtonStyle buttonStyle) {
        Label valueLabel = new Label(formatPercent(currentPercent.getAsInt()), labelStyle);
        valueLabel.setAlignment(Align.center);

        TextButton decreaseButton = stepButton("-", buttonStyle, decrease, valueLabel, currentPercent);
        TextButton increaseButton = stepButton("+", buttonStyle, increase, valueLabel, currentPercent);

        table.add(new Label(name, labelStyle)).width(NAME_WIDTH).left();
        table.add(decreaseButton).size(STEP_BUTTON_SIZE).pad(4.0f);
        table.add(valueLabel).width(VALUE_WIDTH);
        table.add(increaseButton).size(STEP_BUTTON_SIZE).pad(4.0f);
        table.row();
    }

    private static TextButton stepButton(
            String text,
            TextButton.TextButtonStyle style,
            Runnable action,
            Label valueLabel,
            IntSupplier currentPercent) {
        TextButton button = new TextButton(text, style);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
                valueLabel.setText(formatPercent(currentPercent.getAsInt()));
            }
        });
        return button;
    }

    private static String formatPercent(int percent) {
        return percent + "%";
    }
}
