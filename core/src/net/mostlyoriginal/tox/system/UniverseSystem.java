package net.mostlyoriginal.tox.system;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.managers.GroupManager;
import com.artemis.systems.VoidEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.tox.EntityFactory;
import net.mostlyoriginal.tox.Tox;
import net.mostlyoriginal.tox.ToxResource;
import net.mostlyoriginal.tox.component.*;

/**
 * @author Daan van Yperen
 */
public class UniverseSystem extends VoidEntitySystem {

    public static final int PHASE_LEVEL_DIFFERENCE = 3;
    private static final int BUNNY_LEVEL_DIFFERENCE = -6;

    @Mapper
    ComponentMapper<Level> lem;

    @Mapper
    ComponentMapper<Position> pm;

    @Mapper
    ComponentMapper<Health> hm;

    @Mapper
    ComponentMapper<Animation> am;

    @Mapper
    ComponentMapper<Fightable> fm;
    private ParticleSystem particleSystem;
    public ConcealSystem concealSystem;

    public UniverseSystem() {
        setPassive(true);
    }

    @Override
    protected void initialize() {
        particleSystem = world.getSystem(ParticleSystem.class);
        concealSystem = world.getSystem(ConcealSystem.class);
    }

    @Override
    protected void processSystem() {

    }

    public void activate( ToxResource.Universe universe )
    {
        int levelChange = 0;
        if ( Tox.resource.universe == ToxResource.Universe.PHASE ) levelChange -= PHASE_LEVEL_DIFFERENCE;
        if ( Tox.resource.universe == ToxResource.Universe.BUNNY ) levelChange -= BUNNY_LEVEL_DIFFERENCE;
        switch ( universe )
        {

            case REAL:
                break;
            case BUNNY:
                activateBunny();
                levelChange += BUNNY_LEVEL_DIFFERENCE;
                break;
            case PHASE:
                activatePhase();
                levelChange += PHASE_LEVEL_DIFFERENCE;
                break;
        }
        updateMonsters(universe, levelChange);
    }

    private void activateBunny() {

        Tox.resource.loadUniverse(ToxResource.Universe.BUNNY);
        world.getSystem(PetalSystem.class).setEnabled(true);
    }

    private void updateMonsters(ToxResource.Universe universe, int levelChange) {
        ImmutableBag<Entity> monsters = Tox.world.getManager(GroupManager.class).getEntities("monster");
        for (Entity monster : monsters) {
            if (am.has(monster)) {
                final Animation animation = am.get(monster);

                String vanillaSfx = "tox_sfx_grunt_male" + MathUtils.random(1, 3);

                if (animation.id.equals("monster1")) { fm.get(monster).deathSfx= universe == ToxResource.Universe.BUNNY ? "tox_sfx_bunny_dies" : universe == ToxResource.Universe.PHASE ? "tox_sfx_monster_dies1" : vanillaSfx;  }
                if (animation.id.equals("monster2")) { fm.get(monster).deathSfx= universe == ToxResource.Universe.BUNNY ? "tox_sfx_wasp_dies" :  universe == ToxResource.Universe.PHASE ? "tox_sfx_monster_dies2" :vanillaSfx;  }
                if (animation.id.equals("monster3")) { fm.get(monster).deathSfx= universe == ToxResource.Universe.BUNNY ? "tox_sfx_octopus_dies" : universe == ToxResource.Universe.PHASE ? "tox_sfx_monster_dies3" :vanillaSfx;  }
            }

            Level level = lem.get(monster);
            level.level += levelChange;

            Position pos = pm.get(monster);
            particleSystem.cloud(
                    (int) (pos.x + Animation.DEFAULT_SCALE * ToxResource.TILE_SIZE * 0.5f),
                    (int) (pos.y + Animation.DEFAULT_SCALE * ToxResource.TILE_SIZE * 0.5f), ToxResource.TILE_SIZE);

            // healthup with levelup!
            if (hm.has(monster)) {
                Health health = hm.get(monster);
                health.maxHealth = level.level;
            }

        }
    }

    private void activatePhase() {
        ImmutableBag<Entity> conceals = Tox.world.getManager(GroupManager.class).getEntities(EntityFactory.CONCEAL_GROUP);
        for (Entity conceal : conceals) {
            concealSystem.unconceal(conceal, false);
        }

        Tox.resource.loadUniverse(ToxResource.Universe.PHASE);
        world.getSystem(PetalSystem.class).setEnabled(false);
    }


}
