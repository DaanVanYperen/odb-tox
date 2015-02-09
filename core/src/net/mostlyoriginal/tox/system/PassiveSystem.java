package net.mostlyoriginal.tox.system;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.ImmutableBag;

/**
 * @author Daan van Yperen
 */
public class PassiveSystem extends EntitySystem {

    public PassiveSystem() {
        super(Aspect.getEmpty());
    }

    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
    }

    @Override
    protected boolean checkProcessing() {
        return false;
    }
}
