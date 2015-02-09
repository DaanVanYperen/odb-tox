package net.mostlyoriginal.tox.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.tox.ToxResource;
import net.mostlyoriginal.tox.ToxUtil;
import net.mostlyoriginal.tox.component.*;

/**
 * Bullets auto-target the player!
 *
 * @author Daan van Yperen
 */
@Wire
public class BulletSystem extends EntityProcessingSystem {

    @Mapper ComponentMapper<Bullet> sm;
    @Mapper ComponentMapper<Position> pm;
    @Mapper ComponentMapper<Physics> phm;
    @Mapper ComponentMapper<Health> hm;
    @Mapper ComponentMapper<Animation> am;
    public ParticleSystem particleSystem;
    public CombatSystem combatSystem;
    private PlayerSystem playerSystem;

    public BulletSystem() {
        super(Aspect.getAspectForAll(Bullet.class, Position.class, Physics.class));
    }

    private Vector2 tmp = new Vector2();

    @Override
    protected void initialize() {
        particleSystem = world.getSystem(ParticleSystem.class);
        combatSystem = world.getSystem(CombatSystem.class);
        playerSystem = world.getSystem(PlayerSystem.class);
    }

    @Override
    protected void process(Entity e) {
        Position position = pm.get(e);
        Position playerPos = pm.get(ToxUtil.getPlayer());
        Physics physics = phm.get(e);

        // create vector that points from bullet to player;
        int playerX = (int) (playerPos.x + Animation.DEFAULT_SCALE * ToxResource.TILE_SIZE * 0.5f);
        int playerY = (int) (playerPos.y + Animation.DEFAULT_SCALE * ToxResource.TILE_SIZE * 0.5f);
        tmp.set(playerX, playerY)
                .sub(position.x + Animation.DEFAULT_SCALE * 5, position.y + Animation.DEFAULT_SCALE * 5);

        if (tmp.len() > 20) {
            tmp.nor().scl(1200);
            am.get(e).rotation = tmp.angle();
            physics.velocityX = tmp.x;
            physics.velocityY = tmp.y;
        } else {
            particleSystem.explosion(playerX, playerY, Animation.DEFAULT_SCALE * ToxResource.TILE_SIZE);
            if (combatSystem.isAlive(ToxUtil.getPlayer())) {
                combatSystem.damage(ToxUtil.getPlayer(), Math.max(0.1f, hm.get(ToxUtil.getPlayer()).maxHealth / 10));
                if (!combatSystem.isAlive(ToxUtil.getPlayer())) {
                    playerSystem.killPlayer("The Jailer");
                }
            }
            e.deleteFromWorld();
        }
    }
}
