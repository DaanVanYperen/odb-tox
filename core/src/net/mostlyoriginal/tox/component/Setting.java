package net.mostlyoriginal.tox.component;

import com.artemis.Component;

/**
 * @author Daan van Yperen
 */
public class Setting extends Component {

    public final SettingType type;

    public static enum SettingType
    {
        MUSIC,
        SFX
    }

    public Setting(SettingType type) {
        this.type = type;
    }
}
