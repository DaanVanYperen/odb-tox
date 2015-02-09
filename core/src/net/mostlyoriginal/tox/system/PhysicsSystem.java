package net.mostlyoriginal.tox.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import net.mostlyoriginal.tox.component.Physics;
import net.mostlyoriginal.tox.component.Position;

/**
 * @author Daan van Yperen
 */
public class PhysicsSystem extends EntityProcessingSystem {

    @Mapper ComponentMapper<Physics> phm;
    @Mapper ComponentMapper<Position> pm;

    public PhysicsSystem()
    {
        super(Aspect.getAspectForAll(Physics.class, Position.class));
    }

    @Override
    protected void process(Entity e) {
        final Physics physics = phm.get(e);
        final Position position = pm.get(e);

        physics.velocityY -= physics.gravity * world.delta * 100f;

        position.x = position.x + physics.velocityX * world.delta;
        position.y = position.y + physics.velocityY * world.delta;
    }
}
