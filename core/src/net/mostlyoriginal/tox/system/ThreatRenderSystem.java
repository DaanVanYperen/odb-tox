package net.mostlyoriginal.tox.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.mostlyoriginal.tox.Tox;
import net.mostlyoriginal.tox.ToxUtil;
import net.mostlyoriginal.tox.component.Animation;
import net.mostlyoriginal.tox.component.Health;
import net.mostlyoriginal.tox.component.Level;
import net.mostlyoriginal.tox.component.Position;

/**
 * @author Daan van Yperen
 */
public class ThreatRenderSystem extends EntityProcessingSystem {

    public final com.badlogic.gdx.graphics.g2d.Animation skullSmall;
    public final com.badlogic.gdx.graphics.g2d.Animation skullBig;
    @Mapper
    ComponentMapper<Position> pm;
    @Mapper
    ComponentMapper<Level> lm;
    @Mapper
    ComponentMapper<Health> hm;

    public SpriteBatch batch;
    public CombatSystem combatSystem;

    // damage monsters deal per monster level.

    public ThreatRenderSystem() {
        super(Aspect.getAspectForAll(Level.class, Position.class));
        setPassive(false);
        setEnabled(false);
        skullSmall = Tox.resource.get("skull-small");
        skullBig = Tox.resource.get("skull-big");
    }

    @Override
    protected void initialize() {
        combatSystem = world.getSystem(CombatSystem.class);
    }

    @Override
    protected void begin() {
        //batch.setProjectionMatrix(camera.combined);
        //batch.begin();
        //batch.setColor(1f, 1f, 1f, 1f);
    }

    @Override
    protected void end() {
       // batch.end();
    }

    @Override
    protected void process(Entity e) {

        // we call this system from inside the AnimationRenderSystem, to ensure order.

        final Entity player = ToxUtil.getPlayer();
        if (e == player || !hm.has(player) ) return;

        final Position p = pm.get(e);
        final Level level = lm.get(e);
        final Level playerLevel = lm.get(player);

//        lm.get(player).level = 40;
//        hm.get(player).maxHealth = 40;

        float expectedDamage = Math.max(0, CombatSystem.getDamageByMonsterLevel(level.level));
        float expectedHealth = Math.max(1,hm.has(player) ? hm.get(player).maxHealth : 0);

        float factor = expectedDamage / expectedHealth;
        int threat = 0;
        if ( factor > 0.8 ) threat = 4;
        else if ( factor > 0.6 ) threat = 3;
        else if ( factor > 0.4 ) threat = 2;
        else if ( factor > 0.2 ) threat = 1;

        batch.setColor(1f,1f,1f,1f);

        if (threat > 0) {
            com.badlogic.gdx.graphics.g2d.Animation skullAnim = threat > 3 ? skullBig : skullSmall;
            TextureRegion frame = skullAnim.getKeyFrame(0);

            if (threat > 3) threat = 1; // we use different skulls to signify dangerous targets.
            for (int i = 0, s = threat; i < s; i++) {
                batch.draw(frame, p.x + 40 * Animation.DEFAULT_SCALE - (frame.getRegionWidth()+2)*(i+1)* Animation.DEFAULT_SCALE, p.y, frame.getRegionWidth() * Animation.DEFAULT_SCALE, frame.getRegionHeight() * Animation.DEFAULT_SCALE);
            }
        }
    }
}
