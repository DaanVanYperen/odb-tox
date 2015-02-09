package net.mostlyoriginal.tox.screen;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import net.mostlyoriginal.tox.EntityFactory;
import net.mostlyoriginal.tox.system.ParticleSystem;
import net.mostlyoriginal.tox.Tox;
import net.mostlyoriginal.tox.system.*;

/**
 * @author Daan van Yperen
 */
public class DialogScreen implements Screen {

    private final OrthographicCamera camera;

    public DialogScreen(String messageSpriteId, boolean startNewGame, boolean showPerformance) {

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();

        Tox.world = new World();

        Tox.world.setSystem(new CameraShakeSystem(camera));

        Tox.world.setSystem(new SelectableSystem());
        Tox.world.setSystem(new SettingSystem());

        Tox.world.setSystem(new PhysicsSystem());
        Tox.world.setSystem(new ParticleSystem());

        Tox.world.setSystem(new AnimationRenderSystem(camera));
        if ( showPerformance ) {
            Tox.world.setSystem(new TombstoneSystem(camera));
        }
        Tox.world.setSystem(new ContinueSystem(startNewGame));
        Tox.world.setSystem(new SoundSystem());

        Tox.world.setSystem(new StarPlopSystem());

        Tox.world.initialize();

        EntityFactory.createGradientBackground().addToWorld();
        EntityFactory.createScreen(messageSpriteId).addToWorld();
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        Tox.world.setDelta(delta);
        Tox.world.process();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
