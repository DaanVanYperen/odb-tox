package net.mostlyoriginal.tox.component;

import com.artemis.Component;

/**
 * Patient is terminal and will be removed after X.
 *
 * @author Daan van Yperen
 */
public class Terminal extends Component {

    public float survivalDuration;

    public Terminal(float survivalDuration) {
        this.survivalDuration = survivalDuration;
    }
}
