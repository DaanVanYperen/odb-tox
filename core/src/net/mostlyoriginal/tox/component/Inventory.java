package net.mostlyoriginal.tox.component;

import com.artemis.Component;
import com.artemis.Entity;

/**
 * @author Daan van Yperen
 */
public class Inventory extends Component {

    public static enum Slot {
        BODY,
        HAND,
        MISC
    }

    public Entity[] carried = new Entity[3];
}
