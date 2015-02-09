package net.mostlyoriginal.tox.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import net.mostlyoriginal.tox.Tox;
import net.mostlyoriginal.tox.ToxResource;
import net.mostlyoriginal.tox.ToxUtil;
import net.mostlyoriginal.tox.component.*;

/**
 * Allows players to step back to an earlier location.
 *
 * @author Daan van Yperen
 */
public class LootSystem extends EntityProcessingSystem {

    @Mapper
    ComponentMapper<Position> pm;

    @Mapper
    ComponentMapper<Lootable> lm;

    @Mapper
    ComponentMapper<Toxication> tm;

    @Mapper
    ComponentMapper<Selectable> sm;

    private CombatSystem combatSystem;
    public ParticleSystem particleSystem;
    public InventorySystem inventorySystem;
    public UniverseSystem universeSystem;
    private SoundSystem soundSystem;
    public ScoreSystem scoreSystem;


    public LootSystem() {
        super(Aspect.getAspectForAll(Position.class, Selectable.class, Lootable.class));
    }

    @Override
    protected void initialize() {
        combatSystem = world.getSystem(CombatSystem.class);
        particleSystem = world.getSystem(ParticleSystem.class);
        inventorySystem = world.getSystem(InventorySystem.class);
        universeSystem = world.getSystem(UniverseSystem.class);
        soundSystem = world.getSystem(SoundSystem.class);
        scoreSystem = world.getSystem(ScoreSystem.class);
    }

    @Override
    protected void process(Entity e) {
        if (sm.get(e).selected) {
            scoreSystem.scoreReward(e);

            Lootable lootable = lm.get(e);

            Lootable.Effect effect = lm.get(e).effect;

            // quick fix to deal with the player withdrawal death as he steps on a needle tile.
            if ( lootable.effect == Lootable.Effect.HEAL && tm.has(ToxUtil.getPlayer()) && tm.get(ToxUtil.getPlayer()).toxication <= 0.1 )
            {
                // don't let the player die from stepping on a needle tile.
                tm.get(ToxUtil.getPlayer()).toxication = 0.15f;
            }

            Tox.world.getSystem(PlayerSystem.class).claimTile(e,
                    effect != Lootable.Effect.BODY_PICKUP &&
                    effect != Lootable.Effect.HAND_PICKUP &&
                    effect != Lootable.Effect.MISC_PICKUP);

            if (lootable.consumeInstantly) {
                consume(e);
            }
        }
    }

    private void consume(Entity e) {
        Lootable lootable = lm.get(e);
        if ( lootable.pickupSfx != null ) soundSystem.playLater(lootable.pickupSfx, 0.1f);
        switch (lootable.effect) {
            case HEAL:
                combatSystem.heal(ToxUtil.getPlayer());
                break;
            case PHASE:
                phaseDrug();
                break;
            case SWAP:
                swapDrug();
                break;
            case HAND_PICKUP:
                inventorySystem.equip(e, Inventory.Slot.HAND);
                break;
            case BODY_PICKUP:
                inventorySystem.equip(e, Inventory.Slot.BODY);
                break;
            case MISC_PICKUP:
                inventorySystem.equip(e, Inventory.Slot.MISC);
                break;
        }
    }

    private void phaseDrug() {
        combatSystem.intoxicate(ToxUtil.getPlayer(), 0.99f, 0.5f);
        universeSystem.activate(ToxResource.Universe.PHASE);
    }

    private void swapDrug() {
        combatSystem.intoxicate(ToxUtil.getPlayer(),0.99f, 0.5f);
        universeSystem.activate(ToxResource.Universe.BUNNY);
    }
}
