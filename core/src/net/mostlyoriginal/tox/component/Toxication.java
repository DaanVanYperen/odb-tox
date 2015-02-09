package net.mostlyoriginal.tox.component;

import com.artemis.Component;

/**
 * @author Daan van Yperen
 */
public class Toxication extends Component {

    public float maxToxication;
    public float toxication;

    public Toxication(float maxToxication) {
        this.maxToxication = maxToxication;
        this.toxication = this.maxToxication * 0.5f;
    }
}
