package net.mostlyoriginal.tox.system;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.managers.TagManager;
import net.mostlyoriginal.tox.*;
import net.mostlyoriginal.tox.component.*;

/**
 * @author Daan van Yperen
 */
public class PlayerSystem extends PassiveSystem {

    public static final int BONUS_PLAYER_HITPOINTS = 3;
    public static final float DEATH_COOLDOWN = 2f;
    @Mapper
    ComponentMapper<Position> pm;
    @Mapper
    ComponentMapper<Level> lm;
    @Mapper
    ComponentMapper<Health> hm;
    @Mapper
    ComponentMapper<Animation> am;

    @Mapper
    private ComponentMapper<Toxication> tm;
    private ParticleSystem particleSystem;
    public SelectableSystem selectableSystem;
    public LifetimeSystem lifetimeSystem;
    public InventorySystem inventorySystem;
    public CombatSystem combatSystem;
    public SoundSystem soundSystem;

    @Override
    protected void initialize() {
        particleSystem = world.getSystem(ParticleSystem.class);
        selectableSystem = world.getSystem(SelectableSystem.class);
        lifetimeSystem = world.getSystem(LifetimeSystem.class);
        inventorySystem = world.getSystem(InventorySystem.class);
        combatSystem = world.getSystem(CombatSystem.class);
        soundSystem = world.getSystem(SoundSystem.class);
    }

    public void claimTile(Entity tile) {
        claimTile(tile, true);
    }

    public void claimTile(Entity tile, boolean deleteClaimedTile ) {
        final Entity player = ToxUtil.getPlayer();

        final Position tileP = pm.get(tile);

        walkTo(player, tileP);

        // replace location with 'cleared' so player can still click to move there.
        if ( deleteClaimedTile ) {
            tile.deleteFromWorld();
        }
        EntityFactory.createClearedLocation((int) tileP.x, (int) tileP.y).addToWorld();

        tickDrug();
    }

    public void tickDrug() {

        fireBossBullet();

        Entity player = ToxUtil.getPlayer();
        Toxication toxication = tm.get(player);
        if (toxication.toxication > 0) {

            float tickSpeed = 0.05f;

            if ( inventorySystem.hasEffect(EquipBonus.Effect.STASH) && toxication.toxication < 0.3f )
                tickSpeed = 0.01f;

            toxication.toxication -= tickSpeed;
            if (toxication.toxication <= 0) {
                // DEAD!
                killPlayer("Withdrawal");
            }
        }
    }

    private void fireBossBullet() {
        Entity boss = world.getManager(TagManager.class).getEntity(EntityFactory.BOSS_TAG);
        if ( boss != null && pm.has(boss) )
        {
            soundSystem.playLater("tox_sfx_ak47_fight", 1.2f);
            Position position = pm.get(boss);
            Entity bullet = EntityFactory.createBullet((int) (position.x + Animation.DEFAULT_SCALE * ToxResource.TILE_SIZE * 0.5f),
                    (int) (position.y + Animation.DEFAULT_SCALE * ToxResource.TILE_SIZE * 0.5f));
            lifetimeSystem.addToWorldLater(bullet, 1.2f);
        }
    }

    public void walkTo(Entity player, Position pos) {
        final Position playerP = pm.get(player);

        // move to conquered tile.
        playerP.x = pos.x;
        playerP.y = pos.y;

        Entity indicator = Tox.world.getManager(TagManager.class).getEntity(EntityFactory.INDICATOR_TAG);
        Position indicatorPos = pm.get(indicator);
        indicatorPos.x = playerP.x;
        indicatorPos.y = playerP.y;


        Toxication toxication = tm.get(player);
        if ( inventorySystem.hasEffect(EquipBonus.Effect.TEDDY) && toxication.toxication > 0.5f )
        {
            tickDrug();
        }
    }

    public void levelUp() {
        Entity player = getPlayer();
        Level level = lm.get(player);
        level.level++;
        Tox.score.playerLevel = level.level;

        Position pos = pm.get(player);
        soundSystem.playLater("tox_sfx_levelup", 1.1f);
        particleSystem.lvlup(
                (int) (pos.x + Animation.DEFAULT_SCALE * ToxResource.TILE_SIZE * 0.5f),
                (int) (pos.y + Animation.DEFAULT_SCALE * ToxResource.TILE_SIZE * 0.5f));

        // healthup with levelup!
        if (hm.has(player)) {
            Health health = hm.get(player);
            health.maxHealth = BONUS_PLAYER_HITPOINTS + level.level;
        }
    }

    private Entity getPlayer() {
        return ToxUtil.getPlayer();
    }

    public void killPlayer(String tombstone1) {
        final Entity player = getPlayer();


        if (inventorySystem.hasEffect(EquipBonus.Effect.MEDPACK))
        {
            useMedpack();
            return;
        }


        if (am.has(player)) {
            am.get(player).id = "player-dead";
            selectableSystem.selectionCooldown(DEATH_COOLDOWN);
            Tox.gameScreen.isLossCooldown = DEATH_COOLDOWN;
            Tox.score.killer = tombstone1;

            soundSystem.play("tox_sfx_player_dies_male");
        }
    }

    public void useMedpack() {
        Entity player = ToxUtil.getPlayer();
        tm.get(player).toxication = 0f;
        combatSystem.heal(player);
        tm.get(player).toxication = 0.5f;
        inventorySystem.discardByEffect(EquipBonus.Effect.MEDPACK);
    }
}
