package net.mostlyoriginal.tox.component;

import com.artemis.Component;

/**
 * @author Daan van Yperen
 */
public class Health extends Component {

    public float maxHealth;
    public float damage;

    public Health(int maxHealth) {
        this.maxHealth = maxHealth;
    }
}
