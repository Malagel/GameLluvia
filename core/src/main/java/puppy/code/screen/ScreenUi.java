package puppy.code.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

final class ScreenUi {
    private ScreenUi() {
    }

    static Label.LabelStyle labelStyle(BitmapFont font) {
        return new Label.LabelStyle(font, Color.WHITE);
    }

    static TextButton.TextButtonStyle buttonStyle(BitmapFont font, Texture texture) {
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
