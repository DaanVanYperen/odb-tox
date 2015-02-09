package net.mostlyoriginal.tox.component;

import com.artemis.Component;

/**
 * @author Daan van Yperen
 */
public class EquipBonus extends Component {
    public int attack=0;
    public int defense=0;
    public Effect effect = Effect.NONE;

    public static enum Effect {
        NONE,
        TEDDY,
        STASH,
        GRENADE,
        MEDPACK,
        PET,
        SPIDERS_ANTS
    }

    public EquipBonus() {
    }

    public EquipBonus(int attack, int defense) {
        this.attack = attack;
        this.defense = defense;
    }

    public void stack( EquipBonus bonus )
    {
        attack += bonus.attack;
        defense += bonus.defense;
    }

    @Override
    public String toString() {
        return "EquipBonus{" +
                "attack=" + attack +
                ", defense=" + defense +
                '}';
    }
}
