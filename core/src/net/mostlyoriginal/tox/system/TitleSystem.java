package net.mostlyoriginal.tox.system;

import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.tox.Tox;

/**
 * @author Daan van Yperen
 */
public class TitleSystem extends VoidEntitySystem {

    private SpriteBatch batch = new SpriteBatch();
    private OrthographicCamera camera;
    private float age;

    public TitleSystem(OrthographicCamera camera) {
        this.camera = camera;
    }

    @Override
    protected void processSystem() {

        age += world.delta;
        float alpha = MathUtils.clamp(3 - age, 0, 1);

        if (alpha > 0) {
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            batch.setColor(1f, 1f, 1f, 1f);

            int offsetY = 0;
            BitmapFont font = Tox.resource.bigfont;
            font.setColor(1f, 1f, 1f, alpha);
            font.draw(batch, Tox.score.floorName(), Gdx.graphics.getWidth() / 2 - font.getBounds(Tox.score.floorName()).width / 2, 520 + offsetY);
            font = Tox.resource.middlefont;
            font.setColor(1f, 1f, 1f, alpha);
            final String instructions = Tox.score.floor==1 ? "defeat The Jailer" : "reach the exit";
            font.draw(batch, instructions, Gdx.graphics.getWidth() / 2 - font.getBounds(instructions).width / 2, 480 + offsetY);
            batch.end();
        }
    }

}
