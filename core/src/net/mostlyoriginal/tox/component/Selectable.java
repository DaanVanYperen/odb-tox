package net.mostlyoriginal.tox.component;

import com.artemis.Component;

/**
 * @author Daan van Yperen
 */
public class Selectable extends Component {
    public boolean selected;
    public float maxDistance = 40 * Animation.DEFAULT_SCALE;
}
