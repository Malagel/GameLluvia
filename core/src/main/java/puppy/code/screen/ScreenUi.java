package puppy.code.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import java.util.Objects;
import puppy.code.GameAssets;

/**
 * Creates the consistently styled widgets shared by the menu-style screens. One instance is
 * created per screen by {@link BaseUiScreen}; it only builds actors and owns no resources.
 */
final class ScreenUi {
    static final float MENU_BUTTON_WIDTH = 270.0f;
    static final float BACK_BUTTON_WIDTH = 220.0f;
    static final float BUTTON_HEIGHT = 48.0f;

    private static final float TITLE_SCALE = 1.5f;
    private static final float MENU_BUTTON_PAD = 6.0f;

    private final Label.LabelStyle labelStyle;
    private final TextButton.TextButtonStyle buttonStyle;

    ScreenUi(GameAssets assets) {
        BitmapFont font = assets.getFont();
        labelStyle = new Label.LabelStyle(font, Color.WHITE);
        buttonStyle = buttonStyle(font, assets.getUiTexture());
    }

    Label label(String text) {
        return new Label(text, labelStyle);
    }

    Label title(String text) {
        Label title = label(text);
        title.setFontScale(TITLE_SCALE);
        return title;
    }

    TextButton button(String text, Runnable action) {
        Objects.requireNonNull(action, "action");
        TextButton button = new TextButton(text, buttonStyle);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        return button;
    }

    void addMenuButton(Table table, String text, Runnable action) {
        table.add(button(text, action))
                .width(MENU_BUTTON_WIDTH)
                .height(BUTTON_HEIGHT)
                .pad(MENU_BUTTON_PAD);
        table.row();
    }

    private static TextButton.TextButtonStyle buttonStyle(BitmapFont font, Texture texture) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;
        style.disabledFontColor = Color.LIGHT_GRAY;
        style.up = coloredDrawable(texture, new Color(0.12f, 0.32f, 0.58f, 1.0f));
        style.over = coloredDrawable(texture, new Color(0.18f, 0.45f, 0.75f, 1.0f));
        style.down = coloredDrawable(texture, new Color(0.08f, 0.23f, 0.43f, 1.0f));
        style.disabled = coloredDrawable(texture, new Color(0.25f, 0.27f, 0.31f, 1.0f));
        return style;
    }

    private static Drawable coloredDrawable(Texture texture, Color color) {
        return new TextureRegionDrawable(new TextureRegion(texture)).tint(color);
    }
}
