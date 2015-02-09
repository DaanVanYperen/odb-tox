package net.mostlyoriginal.tox.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.tox.ToxResource;
import net.mostlyoriginal.tox.ToxUtil;
import net.mostlyoriginal.tox.component.*;

/**
 * @author Daan van Yperen
 */
public class TrapSystem extends EntityProcessingSystem {

    @Mapper
    ComponentMapper<Selectable> sm;
    @Mapper
    ComponentMapper<Position> pm;

    @Mapper
    ComponentMapper<Level> lm;

    private PlayerSystem playerSystem;
    private SelectableSystem selectableSystem;
    private CombatSystem combatSystem;
    private ParticleSystem particleSystem;
    public CameraShakeSystem cameraShakeSystem;

    public TrapSystem() {
        super(Aspect.getAspectForAll(Level.class, Selectable.class, Trap.class));
    }

    @Override
    protected void initialize() {
        playerSystem = world.getSystem(PlayerSystem.class);
        selectableSystem = world.getSystem(SelectableSystem.class);
        combatSystem = world.getSystem(CombatSystem.class);
        particleSystem = world.getSystem(ParticleSystem.class);
        cameraShakeSystem = world.getSystem(CameraShakeSystem.class);
    }

    @Override
    protected void process(Entity monster) {
        if (sm.get(monster).selected)
        {
            Position mpos = pm.get(monster);
            particleSystem.explosion((int) (mpos.x + Animation.DEFAULT_SCALE * ToxResource.TILE_SIZE * 0.5f), (int) (mpos.y + Animation.DEFAULT_SCALE * ToxResource.TILE_SIZE * 0.5f), ToxResource.TILE_SIZE * 1.5f);
            Entity player = ToxUtil.getPlayer();
            final float monsterDamage = CombatSystem.getDamageByMonsterLevel(lm.get(monster).level);
            combatSystem.damage(player, monsterDamage);

            cameraShakeSystem.shake(MathUtils.clamp(monsterDamage, 1, 5));
            if(!combatSystem.isAlive(player))
            {
                playerSystem.killPlayer("Level " + lm.get(monster).level + " trap");
            }
            playerSystem.claimTile(monster);
        }
    }
}
