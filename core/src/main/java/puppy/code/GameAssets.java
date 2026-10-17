package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public final class GameAssets implements Disposable {
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final Texture playerTexture;
    private final Texture goodDropTexture;
    private final Texture badDropTexture;
    private final Sound collectSound;
    private final Sound hurtSound;
    private final Music rainMusic;

    public GameAssets() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        playerTexture = new Texture(Gdx.files.internal("bucket.png"));
        goodDropTexture = new Texture(Gdx.files.internal("drop.png"));
        badDropTexture = new Texture(Gdx.files.internal("dropBad.png"));
        collectSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        hurtSound = Gdx.audio.newSound(Gdx.files.internal("hurt.ogg"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        rainMusic.setLooping(true);
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public BitmapFont getFont() {
        return font;
    }

    public Texture getPlayerTexture() {
        return playerTexture;
    }

    public Texture getGoodDropTexture() {
        return goodDropTexture;
    }

    public Texture getBadDropTexture() {
        return badDropTexture;
    }

    public void playCollectSound() {
        collectSound.play();
    }

    public void playHurtSound() {
        hurtSound.play();
    }

    public void playRainMusic() {
        rainMusic.play();
    }

    public void stopRainMusic() {
        rainMusic.stop();
    }

    @Override
    public void dispose() {
        rainMusic.dispose();
        hurtSound.dispose();
        collectSound.dispose();
        badDropTexture.dispose();
        goodDropTexture.dispose();
        playerTexture.dispose();
        font.dispose();
        batch.dispose();
    }
}
