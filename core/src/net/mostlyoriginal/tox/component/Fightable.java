package net.mostlyoriginal.tox.component;

import com.artemis.Component;
import com.badlogic.gdx.math.MathUtils;

/**
 * @author Daan van Yperen
 */
public class Fightable extends Component {
    public String deathSfx = "tox_sfx_grunt_male" + MathUtils.random(1, 3);
}
