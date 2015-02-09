package net.mostlyoriginal.tox.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.managers.TagManager;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.tox.*;
import net.mostlyoriginal.tox.component.*;

/**
 * @author Daan van Yperen
 */
public class CombatSystem extends EntityProcessingSystem {

    public static final float PLAYER_DAMAGE_FACTOR = 2;
    public static final Color TRANSPARENT_BLINK_COLOR = new Color(1f, 1f, 1f, 0f);
    @Mapper
    ComponentMapper<Position> pm;
    @Mapper
    ComponentMapper<Selectable> sm;
    @Mapper
    ComponentMapper<Health> hm;
    @Mapper
    ComponentMapper<Toxication> tm;
    @Mapper
    ComponentMapper<Animation> am;
    @Mapper
    ComponentMapper<Named> nm;
    @Mapper
    ComponentMapper<Fightable> fm;
    @Mapper
    ComponentMapper<Lootable> lom;

    @Mapper
    ComponentMapper<Level> lm;

    private PlayerSystem playerSystem;
    private SelectableSystem selectableSystem;

    public static final int DEFEAT_MONSTER_LEVEL_REWARD = 1;

    // damage monsters deal per monster level.
    public static final float MONSTER_DAMAGE_FACTOR = 0.3f;
    public static final float TRAP_DAMAGE_FACTOR = MONSTER_DAMAGE_FACTOR;
    public ParticleSystem particleSystem;
    public InventorySystem inventorySystem;
    public CameraShakeSystem cameraShakeSystem;
    public SoundSystem soundSystem;
    public ScoreSystem scoreSystem;

    public CombatSystem() {
        super(Aspect.getAspectForAll(Health.class, Level.class, Selectable.class, Fightable.class));
    }


    @Override
    protected void initialize() {
        playerSystem = world.getSystem(PlayerSystem.class);
        selectableSystem = world.getSystem(SelectableSystem.class);
        particleSystem = world.getSystem(ParticleSystem.class);
        inventorySystem = world.getSystem(InventorySystem.class);
        cameraShakeSystem = world.getSystem(CameraShakeSystem.class);
        soundSystem = world.getSystem(SoundSystem.class);
        scoreSystem = world.getSystem(ScoreSystem.class);
    }

    public static float getDamageByMonsterLevel(int monsterLevel) {
        return (monsterLevel * CombatSystem.MONSTER_DAMAGE_FACTOR);
    }

    public static float getDamageByPlayerLevel(int playerLevel) {
        return playerLevel * CombatSystem.MONSTER_DAMAGE_FACTOR * CombatSystem.PLAYER_DAMAGE_FACTOR;
    }


    @Override
    protected void process(Entity monster) {
        if (sm.get(monster).selected) {
            playerAttack(monster);
        }
    }

    private void playerAttack(Entity monster) {

        final Entity player = ToxUtil.getPlayer();
        final EquipBonus playerBonus = inventorySystem.getTotalBonus(player);

        // get effective monster damage. Consider player defensive items.
        final float monsterDamage =
                getDamageByMonsterLevel(
                        Math.max(1, lm.get(monster).level - playerBonus.defense));

        // get effective player attack damage. Consider player offensive items.
        final float playerDamage =  1 + getDamageByPlayerLevel(
                lm.get(player).level + playerBonus.attack);


        String fightSfx = combatSfx(player);

        if ( fightSfx != null ) soundSystem.play(fightSfx);
        //System.out.println("player attack: " + playerDamage + " monster attack: " + monsterDamage + playerBonus + " MONSTER HP: " + hm.get(monster).maxHealth);
        damage(monster, playerDamage);
        if (isAlive(monster)) {
            Position ppos = pm.get(player);

            particleSystem.combatCloud((int) (ppos.x + Animation.DEFAULT_SCALE * ToxResource.TILE_SIZE * 0.5f), (int) (ppos.y + Animation.DEFAULT_SCALE * ToxResource.TILE_SIZE * 0.5f), ToxResource.TILE_SIZE, 0.25f);
            Position mpos = pm.get(monster);
            particleSystem.combatCloud((int) (mpos.x + Animation.DEFAULT_SCALE * ToxResource.TILE_SIZE * 0.5f), (int) (mpos.y + Animation.DEFAULT_SCALE * ToxResource.TILE_SIZE * 0.5f), ToxResource.TILE_SIZE, 0.25f);

            cameraShakeSystem.push(
                    MathUtils.clamp(mpos.x - ppos.x, -5, 5),
                    MathUtils.clamp(mpos.y - ppos.y, -5, 5) );

            // saved by the grenade!
            final Health health = hm.get(player);
            if ( health.damage + monsterDamage >= health.maxHealth && inventorySystem.hasEffect(EquipBonus.Effect.GRENADE))
            {
                inventorySystem.discardByEffect(EquipBonus.Effect.GRENADE);
                particleSystem.explosion((int) (mpos.x + Animation.DEFAULT_SCALE * ToxResource.TILE_SIZE * 0.5f), (int) (mpos.y + Animation.DEFAULT_SCALE * ToxResource.TILE_SIZE * 0.5f),150f);
                damage(monster,9999);
                monsterDeath(monster);
            } else {
                damage(player, monsterDamage);
            }

            if(!isAlive(player))
            {
                playerSystem.killPlayer("level " + lm.get(monster).level + " " + (nm.has(monster) ? nm.get(monster).name : "Baddy") );
            } else {
                playerSystem.tickDrug();
            }
        } else {
            monsterDeath(monster);
        }
    }

    // fetch sound that belongs with using the hand weapon
    private String combatSfx(Entity player) {
        Entity hand = inventorySystem.get(Inventory.Slot.HAND);
        if ( hand != null && lom.has(hand) )
        {
            Lootable lootable = lom.get(hand);
            return lootable.useSfx;
        }
        return "tox_sfx_knuckles_fight";
    }

    private void monsterDeath(Entity monster) {
        scoreSystem.scoreReward(monster);
        scoreSystem.score(lm.get(monster).level * Score.POINTS_MONSTER_KILL_PER_LEVEL);
        checkBossVictory(monster);
        cameraShakeSystem.shake(2);
        playerSystem.claimTile(monster);
        rewardKill(monster);
    }

    private void checkBossVictory(Entity monster) {
        Entity boss = world.getManager(TagManager.class).getEntity(EntityFactory.BOSS_TAG);
        if ( boss != null && monster == boss )
        {
            selectableSystem.selectionCooldown(3);
            Tox.gameScreen.isWinCooldown = 3;
        }
    }

    public boolean isAlive(Entity monster) {
        return hm.has(monster);
    }

    public void damage(Entity victim, float damage) {
        if (hm.has(victim)) {
            selectableSystem.selectionCooldown(0.25f);

            final Health health = hm.get(victim);
            health.damage += damage;

            if (health.damage >= health.maxHealth) {
                kill(victim);
            } else if (health.damage >= health.maxHealth / 2 && ToxUtil.isPlayer(victim)) {
                blink(victim, Color.WHITE, Color.ORANGE,Interpolation.pow2,2,-1);
            } else if (health.damage >= health.maxHealth / 4 * 3) {
                blink(victim, Color.WHITE, Color.RED,Interpolation.pow2,2,-1);
            } else blink(victim, TRANSPARENT_BLINK_COLOR, Color.WHITE, Interpolation.linear, 4f, 0.5f);
        }
    }

    private void blink(Entity victim, Color color1, Color color2, Interpolation in, float speed, float duration) {
        victim.addComponent(new ColorAnimation(color1, color2, in, speed, duration)).changedInWorld();
    }

    private void kill(Entity e) {

        if ( e == ToxUtil.getPlayer() && inventorySystem.hasEffect(EquipBonus.Effect.MEDPACK))
        {
            playerSystem.useMedpack();
            return;
        }

        e.removeComponent(Health.class).removeComponent(Selectable.class).removeComponent(ColorAnimation.class).changedInWorld();
    }

    private void rewardKill(Entity monster) {
        if (pm.has(monster)) {
            Position mpos = pm.get(monster);
            soundSystem.playLater(fm.get(monster).deathSfx, 0.5f);
            particleSystem.combatCloud((int) (mpos.x + Animation.DEFAULT_SCALE * ToxResource.TILE_SIZE * 0.5f), (int) (mpos.y + Animation.DEFAULT_SCALE * ToxResource.TILE_SIZE * 0.5f), ToxResource.TILE_SIZE, 1);
            playerSystem.levelUp();
        }
    }

    /**
     *
     * @param victim
     * @param min Minimum level of intoxication.
     * @param amount boost intoxication by this amount. (can cause overdose)
     */
    public void intoxicate(Entity victim, float min, float amount)
    {
        final Toxication tox = tm.get(victim);
        tox.toxication += amount;
        if ( tox.toxication <= min ) tox.toxication=min;
        if ( tox.toxication >= tox.maxToxication )
        {
            playerSystem.killPlayer("Drug overdose");
        }
    }

    public void heal(Entity victim) {
        if (hm.has(victim)) {
            Position pos = pm.get(victim);
            selectableSystem.selectionCooldown(MathUtils.random(0.2f,0.3f));
            particleSystem.heal((int)(pos.x +(Animation.DEFAULT_SCALE * ToxResource.TILE_SIZE * 0.5f)),
                                (int)(pos.y +(Animation.DEFAULT_SCALE * ToxResource.TILE_SIZE * 0.5f)));
            final Health health = hm.get(victim);
            health.damage = 0;
            intoxicate(victim, 0, 0.3f);
            blink(victim, Color.GREEN, Color.WHITE,Interpolation.swingOut,0.5f,0.5f);
        }
    }
}
