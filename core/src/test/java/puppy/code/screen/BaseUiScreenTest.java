package puppy.code.screen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import puppy.code.GameAssets;
import puppy.code.GameLluviaMenu;

class BaseUiScreenTest {
    private Input input;
    private GameAssets assets;
    private Stage stage;
    private Viewport viewport;
    private TestUiScreen screen;

    @BeforeEach
    void setUp() {
        input = mock(Input.class);
        Gdx.input = input;
        Gdx.gl = mock(GL20.class);
        Gdx.gl20 = Gdx.gl;
        // Scene2D table cells cache their defaults per Gdx.files instance.
        Gdx.files = mock(Files.class);

        assets = mock(GameAssets.class);
        when(assets.getUiTexture()).thenReturn(mock(Texture.class));
        // The template creates a real title label, which measures its text with the font.
        BitmapFont font = headlessFont();
        when(assets.getFont()).thenReturn(font);
        GameLluviaMenu game = mock(GameLluviaMenu.class);
        when(game.getAssets()).thenReturn(assets);

        viewport = mock(Viewport.class);
        stage = mock(Stage.class);
        when(stage.getViewport()).thenReturn(viewport);
        screen = new TestUiScreen(game, stage);
    }

    @AfterEach
    void clearLibGdxStatics() {
        Gdx.input = null;
        Gdx.gl = null;
        Gdx.gl20 = null;
        Gdx.files = null;
    }

    @Test
    void showBuildsUiOnceAndRegistersStageForInput() {
        screen.show();
        screen.show();

        assertEquals(1, screen.buildCount);
        verify(input, times(2)).setInputProcessor(stage);
    }

    @Test
    void templateBuildsTheTitleThenTheScreenContentUnderIt() {
        screen.show();
        screen.render(0.016f);

        ArgumentCaptor<Actor> added = ArgumentCaptor.forClass(Actor.class);
        verify(stage, times(1)).addActor(added.capture());
        Table root = (Table) added.getValue();
        assertEquals(2, root.getCells().size);
        Label title = (Label) root.getCells().get(0).getActor();
        assertEquals("Pantalla de prueba", title.getText().toString());
        assertSame(screen.receivedContent, root.getCells().get(1).getActor());
        assertNotNull(screen.receivedUi);
    }

    @Test
    void renderUsesTheSharedCappedUpdateAndDrawSequence() {
        screen.render(0.5f);

        assertEquals(1, screen.buildCount);
        verify(Gdx.gl).glClearColor(0.0f, 0.0f, 0.2f, 1.0f);
        verify(stage).act(1.0f / 30.0f);
        verify(stage).draw();
    }

    @Test
    void resizeUpdatesTheViewportAndCentersItsCamera() {
        screen.resize(1280, 720);

        verify(viewport).update(1280, 720, true);
    }

    @Test
    void hideOnlyClearsInputWhenThisScreenOwnsIt() {
        when(input.getInputProcessor()).thenReturn(stage);
        screen.hide();
        verify(input).setInputProcessor(null);

        InputProcessor otherProcessor = mock(InputProcessor.class);
        when(input.getInputProcessor()).thenReturn(otherProcessor);
        screen.hide();
        verify(input, times(1)).setInputProcessor(null);
    }

    @Test
    void disposeReleasesInputAndOwnedStageOnlyOnce() {
        when(input.getInputProcessor()).thenReturn(stage);

        screen.dispose();
        screen.dispose();

        verify(input).setInputProcessor(null);
        verify(stage).dispose();
        verify(assets, never()).dispose();
    }

    @Test
    void disposedScreenCannotBeRenderedOrResized() {
        screen.dispose();

        assertThrows(IllegalStateException.class, () -> screen.render(0.0f));
        assertThrows(IllegalStateException.class, () -> screen.resize(640, 360));
        verify(stage, never()).draw();
    }

    @Test
    void lifecycleMethodsAreFinalTemplateOperations() throws NoSuchMethodException {
        assertFinal("show");
        assertFinal("render", float.class);
        assertFinal("resize", int.class, int.class);
        assertFinal("hide");
        assertFinal("dispose");
        assertTrue(Modifier.isAbstract(BaseUiScreen.class.getModifiers()));
        assertEquals(BaseUiScreen.class, MainMenuScreen.class.getSuperclass());
        assertEquals(BaseUiScreen.class, LeaderboardScreen.class.getSuperclass());
        assertEquals(BaseUiScreen.class, GameOverScreen.class.getSuperclass());
        assertEquals(BaseUiScreen.class, OptionsScreen.class.getSuperclass());
    }

    /**
     * LibGDX's bundled default font data with a mocked page texture: enough to measure text
     * without a graphics context.
     */
    private static BitmapFont headlessFont() {
        FileHandle fontFile =
                new FileHandle("com/badlogic/gdx/utils/lsans-15.fnt", Files.FileType.Classpath) {
                };
        return new BitmapFont(new BitmapFont.BitmapFontData(fontFile, false),
                new TextureRegion(mock(Texture.class)), false);
    }

    private static void assertFinal(String name, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        Method method = BaseUiScreen.class.getMethod(name, parameterTypes);
        assertTrue(Modifier.isFinal(method.getModifiers()), name + " must remain final");
    }

    private static final class TestUiScreen extends BaseUiScreen {
        private int buildCount;
        private Table receivedContent;
        private ScreenUi receivedUi;

        private TestUiScreen(GameLluviaMenu game, Stage stage) {
            super(game, stage);
        }

        @Override
        protected String getTitle() {
            return "Pantalla de prueba";
        }

        @Override
        protected void buildContent(Table content, ScreenUi ui) {
            buildCount++;
            receivedContent = content;
            receivedUi = ui;
        }
    }
}
