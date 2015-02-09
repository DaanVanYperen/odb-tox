package net.mostlyoriginal.tox.system;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.VoidEntitySystem;
import net.mostlyoriginal.tox.Tox;
import net.mostlyoriginal.tox.component.Reward;

/**
 * @author Daan van Yperen
 */
public class ScoreSystem extends VoidEntitySystem {

    @Mapper
        ComponentMapper<Reward> rm;

    public ScoreSystem() {
        setPassive(true);
    }

    @Override
    protected void processSystem() {
    }

    public void score( int points )
    {
        Tox.score.points += points;
    }

    /**
     * Reward the accompanied score reward.
     * @param entity
     */
    public void scoreReward(Entity entity) {
        if ( rm.has(entity ) ) {
            score(rm.get(entity).points);
        }
    }
}
