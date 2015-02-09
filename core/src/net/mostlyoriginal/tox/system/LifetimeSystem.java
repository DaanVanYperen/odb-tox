package net.mostlyoriginal.tox.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.Bag;
import net.mostlyoriginal.tox.component.Terminal;

import java.util.Iterator;

/**
 * @author Daan van Yperen
 */
public class LifetimeSystem extends EntityProcessingSystem {

    @Mapper
    ComponentMapper<Terminal> tm;

    public LifetimeSystem() {
        super(Aspect.getAspectForAll(Terminal.class));
    }

    public void addToWorldLater(Entity e, float delay) {
        delayedEntities.add(new Delayed(e, delay));
    }

    Bag<Delayed> delayedEntities = new Bag<Delayed>();

    @Override
    protected void end() {
        super.end();

        // auto introduce delayed entities.
        Iterator<Delayed> i = delayedEntities.iterator();
        while (i.hasNext()) {
            Delayed e = i.next();
            e.delay -= world.delta;
            if (e.delay <= 0) {
                i.remove();
                e.e.addToWorld();
            }
        }
    }

    @Override
    protected void process(Entity e) {
        Terminal terminal = tm.get(e);
        terminal.survivalDuration -= world.delta;
        if (terminal.survivalDuration <= 0) {
            e.deleteFromWorld();
        }
    }

    public static class Delayed {
        Entity e;
        float delay;

        public Delayed(Entity e, float delay) {
            this.e = e;
            this.delay = delay;
        }
    }
}
