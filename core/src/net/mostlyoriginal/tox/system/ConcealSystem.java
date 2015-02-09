package net.mostlyoriginal.tox.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import net.mostlyoriginal.tox.ToxResource;
import net.mostlyoriginal.tox.component.Animation;
import net.mostlyoriginal.tox.component.Conceal;
import net.mostlyoriginal.tox.component.Position;
import net.mostlyoriginal.tox.component.Selectable;

/**
 * @author Daan van Yperen
 */
public class ConcealSystem extends EntityProcessingSystem {

    @Mapper
    private ComponentMapper<Selectable> sm;

    @Mapper
    private ComponentMapper<Conceal> cm;

    @Mapper
    private ComponentMapper<Position> pm;

    private SelectableSystem selectableSystem;
    private LifetimeSystem lifetimeSystem;
    private ParticleSystem particleSystem;


    public ConcealSystem() {
        super(Aspect.getAspectForAll(Conceal.class, Selectable.class));
    }

    @Override
    protected void initialize() {
        selectableSystem = world.getSystem(SelectableSystem.class);
        lifetimeSystem = world.getSystem(LifetimeSystem.class);
        particleSystem = world.getSystem(ParticleSystem.class);
    }

    @Override
    protected void process(Entity e) {
        if ( sm.get(e).selected )
        {
            unconceal(e,true);
        }
    }

    public void unconceal(Entity e, boolean forceSelect) {
        final Conceal conceal = cm.get(e);

        final Position position = pm.get(e);

        // cover the switch with a cloud.
        particleSystem.cloud((int)(position.x + ToxResource.TILE_SIZE* Animation.DEFAULT_SCALE*0.5f), (int)(position.y+ ToxResource.TILE_SIZE* Animation.DEFAULT_SCALE*0.5f), 20);

        // set entity selected, so it gets instantly selected (and triggered) after being revealed.
        lifetimeSystem.addToWorldLater(conceal.entity, 0.10f);
        if ( forceSelect )
        {
            selectableSystem.forceSelect(conceal.entity, 0.8f);
        }
        conceal.entity = null;

        // no more questionmark required
        e.deleteFromWorld();
    }

}
