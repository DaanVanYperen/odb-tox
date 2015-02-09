package net.mostlyoriginal.tox.component;

import com.artemis.Component;

/**
 * Lootable item
 *
 * @author Daan van Yperen
 */
public class Lootable extends Component {

    public Effect effect;
    public String pickupSfx;
    public String useSfx;

    public static enum Effect {
        HEAL,
        PHASE,
        SWAP, HAND_PICKUP, BODY_PICKUP, MISC_PICKUP;
    }

    public boolean consumeInstantly = true;

    public Lootable(Effect effect, String pickupSfx, String useSfx) {
        this.effect = effect;
        this.pickupSfx = pickupSfx;
        this.useSfx = useSfx;
    }

    public Lootable() {
    }
}
