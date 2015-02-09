package net.mostlyoriginal.tox.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import net.mostlyoriginal.tox.Tox;
import net.mostlyoriginal.tox.component.Label;
import net.mostlyoriginal.tox.component.Position;

/**
 * @author Daan van Yperen
 */
public class LabelRenderSystem extends EntityProcessingSystem {

    @Mapper
    ComponentMapper<Position> pm;

    @Mapper
    ComponentMapper<Label> lm;

    private SpriteBatch batch = new SpriteBatch();
    private OrthographicCamera camera;

    public LabelRenderSystem(OrthographicCamera camera) {
        super(Aspect.getAspectForAll(Position.class, Label.class));
        this.camera = camera;
    }

    @Override
    protected void begin() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);
    }

    @Override
    protected void end() {
        batch.end();
    }

    @Override
    protected void process(final Entity entity) {

        final Label label = lm.get(entity);
        final Position position = pm.get(entity);

        Tox.resource.font.setColor(1f,1f,1f,1f);
        Tox.resource.font.setScale(1f);
        Tox.resource.font.draw(batch, label.label, position.x + 20, position.y + 16 );

    }

    @Override
    protected boolean checkProcessing() {
        return true;
    }
}
