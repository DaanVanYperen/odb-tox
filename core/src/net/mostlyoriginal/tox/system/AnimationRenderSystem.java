package net.mostlyoriginal.tox.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Mapper;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.mostlyoriginal.tox.Tox;
import net.mostlyoriginal.tox.component.Animation;
import net.mostlyoriginal.tox.component.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Daan van Yperen
 */
public class AnimationRenderSystem extends EntitySystem {

    @Mapper
    ComponentMapper<Position> pm;

    @Mapper
    ComponentMapper<Animation> sm;

    private SpriteBatch batch = new SpriteBatch();
    private OrthographicCamera camera;
    private List<Entity> sortedEntities = new ArrayList<Entity>();
    private boolean sortedDirty = false;
    public Comparator<Entity> layerSortComperator = new Comparator<Entity>() {
        @Override
        public int compare(Entity e1, Entity e2) {
            Animation s1 = sm.get(e1);
            Animation s2 = sm.get(e2);
            return s1.layer.compareTo(s2.layer);
        }
    };

    private CombatSystem combatSystem;
    public ThreatRenderSystem threatRenderSystem;

    public AnimationRenderSystem(OrthographicCamera camera) {
        super(Aspect.getAspectForAll(Position.class, Animation.class));
        this.camera = camera;
    }

    @Override
    protected void initialize() {
        super.initialize();

        combatSystem = world.getSystem(CombatSystem.class);
        threatRenderSystem = world.getSystem(ThreatRenderSystem.class);
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
    protected void processEntities(ImmutableBag<Entity> entities) {

        if (sortedDirty) {
            sortedDirty = false;
            Collections.sort(sortedEntities, layerSortComperator);
        }

        boolean renderedThreat=false;

        for (Entity entity : sortedEntities) {

            if ( threatRenderSystem != null && sm.get(entity).layer.ordinal() > Animation.Layer.TILE_COVER.ordinal() )
            {
                renderedThreat=true;
                renderThreat();
            }
            process(entity);
        }

        if ( !renderedThreat )
        {
            renderThreat();
        }
    }

    private void renderThreat() {
        if ( threatRenderSystem != null )
        {
            threatRenderSystem.batch = this.batch;
            threatRenderSystem.setEnabled(true);
            threatRenderSystem.process();
            threatRenderSystem.setEnabled(false);
        }
    }

    protected void process(final Entity entity) {

        final Animation animation = sm.get(entity);
        final Position position = pm.get(entity);

        if (!animation.frozen) {
            animation.age += world.getDelta() * animation.speed;
        }



        batch.setColor( animation.color );

        if (  animation.behindId != null ) drawAnimation(animation, position, animation.behindId);
        drawAnimation(animation, position, animation.id);
    }

    private void drawAnimation(final Animation animation, final Position position, String id) {

        final com.badlogic.gdx.graphics.g2d.Animation gdxanim = Tox.resource.get(id);
        TextureRegion frame = gdxanim.getKeyFrame(animation.age, true);

        if ( animation.rotation != 0 )
        {
            batch.draw(frame,
                    position.x,
                    position.y,
                    frame.getRegionWidth() * animation.scale * 0.5f * animation.stretchX,
                    frame.getRegionHeight() * animation.scale * 0.5f,
                    frame.getRegionWidth() * animation.scale * animation.stretchX,
                    frame.getRegionHeight() * animation.scale, 1, 1,
                    animation.rotation);
        } else {
            batch.draw(frame,
                    position.x,
                    position.y,
                    frame.getRegionWidth() * animation.scale * animation.stretchX,
                    frame.getRegionHeight() * animation.scale);
        }
    }

    @Override
    protected boolean checkProcessing() {
        return true;
    }


    @Override
    protected void inserted(Entity e) {
        sortedEntities.add(e);
        sortedDirty = true;
    }

    @Override
    protected void removed(Entity e) {
        sortedEntities.remove(e);
    }
}
