package net.mostlyoriginal.tox.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import net.mostlyoriginal.tox.component.DissolvesOnTouch;
import net.mostlyoriginal.tox.component.Selectable;

/**
 * @author Daan van Yperen
 */
public class DissolveSystem extends EntityProcessingSystem {

    @Mapper
    ComponentMapper<Selectable> sm;

    public DissolveSystem() {
        super(Aspect.getAspectForAll(DissolvesOnTouch.class, Selectable.class));
    }

    @Override
    protected void process(Entity e) {
        if ( sm.get(e).selected )
        {
            e.deleteFromWorld();
        }
    }
}
