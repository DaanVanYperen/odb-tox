package net.mostlyoriginal.tox.component;

import com.artemis.Component;
import com.artemis.Entity;

/**
 * Conceal entity until interacted.
 *
 * @author Daan van Yperen
 */
public class Conceal extends Component {

    public Entity entity;

    public Conceal(Entity entity) {
        this.entity = entity;
    }
}
