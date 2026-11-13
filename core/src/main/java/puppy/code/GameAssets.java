package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import puppy.code.model.FallingItem;
import puppy.code.model.GameplayEvent;
import puppy.code.settings.AudioSettings;

/**
 * Owns the shared LibGDX resources and is the single presentation mapping from domain
 * identities and outcomes to those resources. Screens ask for the texture of a drop type
 * or the sound of a gameplay event and never decide it themselves, so a new drop type or
 * event only needs a new entry here.
 */
public final class GameAssets implements Disposable {
    private final AudioSettings audioSettings;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final Texture playerTexture;
    private final Texture goodDropTexture;
    private final Texture badDropTexture;
    private final Texture uiTexture;
    private final Sound collectSound;
    private final Sound hurtSound;
    private final Music rainMusic;
    private final Map<FallingItem.Type, Texture> dropTextures;
    private final Map<GameplayEvent, Sound> eventSounds;

    public GameAssets(AudioSettings audioSettings) {
        this.audioSettings = Objects.requireNonNull(audioSettings, "audioSettings");
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        playerTexture = new Texture(Gdx.files.internal("bucket.png"));
        goodDropTexture = new Texture(Gdx.files.internal("drop.png"));
        badDropTexture = new Texture(Gdx.files.internal("dropBad.png"));
        Pixmap uiPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        uiPixmap.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        uiPixmap.fill();
        uiTexture = new Texture(uiPixmap);
        uiPixmap.dispose();
        collectSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        hurtSound = Gdx.audio.newSound(Gdx.files.internal("hurt.ogg"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        rainMusic.setLooping(true);

        dropTextures = new EnumMap<>(FallingItem.Type.class);
        dropTextures.put(FallingItem.Type.GOOD, goodDropTexture);
        dropTextures.put(FallingItem.Type.BAD, badDropTexture);
        requireComplete(dropTextures, FallingItem.Type.values());

        eventSounds = new EnumMap<>(GameplayEvent.class);
        eventSounds.put(GameplayEvent.DROP_COLLECTED, collectSound);
        eventSounds.put(GameplayEvent.PLAYER_DAMAGED, hurtSound);
        requireComplete(eventSounds, GameplayEvent.values());
    }

    /**
     * Fails at startup, rather than mid-game, when a domain value has no resource.
     */
    private static <K> void requireComplete(Map<K, ?> mapping, K[] keys) {
        for (K key : keys) {
            if (!mapping.containsKey(key)) {
                throw new IllegalStateException("No asset registered for " + key);
            }
        }
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

    public Texture getDropTexture(FallingItem.Type type) {
        return dropTextures.get(Objects.requireNonNull(type, "type"));
    }

    public Texture getUiTexture() {
        return uiTexture;
    }

    public void playCollectSound() {
        collectSound.play(audioSettings.getEffectsVolume());
    }

    public void playEventSound(GameplayEvent event) {
        eventSounds.get(Objects.requireNonNull(event, "event"))
                .play(audioSettings.getEffectsVolume());
    }

    public void playRainMusic() {
        rainMusic.setVolume(audioSettings.getMusicVolume());
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
        uiTexture.dispose();
        goodDropTexture.dispose();
        playerTexture.dispose();
        font.dispose();
        batch.dispose();
    }
}
