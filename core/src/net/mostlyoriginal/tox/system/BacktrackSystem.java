package net.mostlyoriginal.tox.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import net.mostlyoriginal.tox.Tox;
import net.mostlyoriginal.tox.ToxUtil;
import net.mostlyoriginal.tox.component.*;

/**
 * Allows players to step back to an earlier location.
 *
 * @author Daan van Yperen
 */
public class BacktrackSystem extends EntityProcessingSystem {

    @Mapper
    ComponentMapper<Position> pm;

    @Mapper
    ComponentMapper<Selectable> sm;

    public BacktrackSystem() {
        super(Aspect.getAspectForAll(Position.class, Selectable.class, Cleared.class));
    }

    @Override
    protected void process(Entity e) {
        if (sm.get(e).selected) {
            final Position p = pm.get(e);
            Entity player = ToxUtil.getPlayer();
            Tox.world.getSystem(PlayerSystem.class).walkTo(player, p);
        }
    }
}
