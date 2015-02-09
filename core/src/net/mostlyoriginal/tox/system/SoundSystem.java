package net.mostlyoriginal.tox.system;

import com.artemis.systems.VoidEntitySystem;
import com.artemis.utils.Bag;
import net.mostlyoriginal.tox.Tox;

import java.util.Iterator;

/**
 * @author Daan van Yperen
 */
public class SoundSystem extends VoidEntitySystem {

    Bag<Delayed> delayedEntities = new Bag<Delayed>();

    public static class Delayed {
        String sfxid;
        float delay;

        public Delayed(String sfxid, float delay) {
            this.sfxid = sfxid;
            this.delay = delay;
        }
    }

    public void play( String sfxid)
    {
        Tox.resource.playSfx(sfxid);
    }

    public void playLater( String sfxid, float delay)
    {
        delayedEntities.add(new Delayed(sfxid,delay));
    }


    @Override
    protected void processSystem() {

        // auto introduce delayed entities.
        Iterator<Delayed> i = delayedEntities.iterator();
        while (i.hasNext()) {
            Delayed e = i.next();
            e.delay -= world.delta;
            if (e.delay <= 0) {
                Tox.resource.playSfx(e.sfxid);
                i.remove();
            }
        }

    }
}
