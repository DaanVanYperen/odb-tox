package net.mostlyoriginal.tox;

import com.artemis.Entity;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.tox.component.*;
import net.mostlyoriginal.tox.system.PlayerSystem;

/**
 * @author Daan van Yperen
 */
public class EntityFactory {

    public static final String PLAYER_TAG = "player";
    public static final String BOSS_TAG = "boss";
    private static final float BACKGROUND_SCALE = 2;
    public static final String MONSTER_GROUP = "monster";
    public static final String INDICATOR_TAG = "indicator";
    public static final String CONCEAL_GROUP = "conceal";

    public static Entity createBackground() {
        Animation animation = new Animation("background", Animation.Layer.GLOBAL_BACKGROUND);
        animation.scale = BACKGROUND_SCALE;
        animation.frozen = true;
        return Tox.world
                .createEntity()
                .addComponent(new Position(0, 0))
                .addComponent(animation);
    }

    public static Entity createPlayer(int x, int y, int level) {
        Entity player = createTile(x, y, "player", Animation.Layer.TILE_COVER)
                .addComponent(new Level(1))
                .addComponent(new Inventory())
                .addComponent(new Toxication(1f))
                .addComponent(new Health(PlayerSystem.BONUS_PLAYER_HITPOINTS + level));
        Tox.world.getManager(TagManager.class).register(PLAYER_TAG, player);
        return player;
    }

    public static Entity createIndicator(int x, int y) {
        Entity tile = createTile(x, y, "indicator", Animation.Layer.TILE_UNDER_COVER);
        tile.addComponent(new ColorAnimation(Color.valueOf("f3d15e00"), Color.valueOf("f3d15eff"), Interpolation.fade, 0.5f, -1));
        Tox.world.getManager(TagManager.class).register(INDICATOR_TAG, tile);
        return tile;
    }

    protected static Entity createTile(int x, int y, String spriteId) {
        return createTile(x, y, spriteId, Animation.Layer.TILE);
    }

    protected static Entity createTile(int x, int y, String spriteId, Animation.Layer layer) {
        Animation animation = new Animation(spriteId, layer);
        animation.age = MathUtils.random(0,1f);
        return Tox.world
                .createEntity()
                .addComponent(new Position(x, y))
                .addComponent(animation);
    }

    public static Entity createBullet(int x, int y) {
        return Tox.world
                .createEntity()
                .addComponent(new Position(x, y))
                .addComponent(new Physics())
                .addComponent(new Terminal(20))
                .addComponent(new Bullet())
                .addComponent(new Animation("bullet", Animation.Layer.PARTICLE2));
    }

    public static Entity createBackgroundTile(int x, int y, String spriteId) {
        Animation component = new Animation(spriteId, Animation.Layer.TILE_BACKGROUND);
        component.frozen = true;
        component.age = MathUtils.random(0, Tox.resource.get(component.id).getAnimationDuration()); // pick a random sprite.
        return Tox.world
                .createEntity()
                .addComponent(new Position(x, y))
                .addComponent(component);
    }

    public static Entity createExit(int x, int y) {
        return createTile(x, y, "exit")
                .addComponent(new Reward(Score.POINTS_NEXT_LEVEL))
                .addComponent(new Exit()).addComponent(new Selectable());
    }

    public static Entity createMonster(int x, int y, int level) {
        String id = "monster1";
        switch (MathUtils.random(0, 2)) {
            case 0:
                break;
            case 1:
                id = "monster2";
                break;
            case 2:
                id = "monster3";
                break;
        }

        Entity monster = createTile(x, y, id)
                .addComponent(new Health(level))
                .addComponent(new Level(level))
                .addComponent(new Fightable())
                .addComponent(new Reward(Score.POINTS_MONSTER_KILL))
                .addComponent(new Selectable());
        Tox.world.getManager(GroupManager.class).add(monster, MONSTER_GROUP);
        return monster;
    }

    public static Entity createTrap(int x, int y, int level) {
        return createTile(x, y, "trap")
                .addComponent(new Selectable()).addComponent(new Trap()).addComponent(new Level(level));
    }

    public static Entity createHealth(int x, int y) {
        Lootable lootable = new Lootable();
        lootable.consumeInstantly = true;
        lootable.pickupSfx="tox_sfx_consume_health";
        lootable.effect = Lootable.Effect.HEAL;
        return createTile(x, y, "health")
                .addComponent(new Selectable())
                .addComponent(new Reward(Score.POINTS_GRAB_ITEM))
                .addComponent(lootable);
    }

    public static Entity createRandomWeapon(int x, int y) {

        String animId = "knuckles";
        String pickupSfx = "tox_sfx_knuckles_pickup";
        String useSfx = "tox_sfx_knuckles_fight";
        int attackBonus = 1;
        int randomBonus = 0;
        switch ( MathUtils.random(0, 3) )
        {
            case 1:
                animId = "handgun";
                attackBonus = 1; randomBonus =1;
                pickupSfx = "tox_sfx_glock_pickup";
                useSfx = "tox_sfx_glock_fight";
                break;
            case 2:
                animId = "assaultrifle";
                attackBonus = 1; randomBonus =2;
                pickupSfx = "tox_sfx_ak47_pickup";
                useSfx = "tox_sfx_ak47_fight";
                break;
            case 3:
                animId = "shotgun";
                pickupSfx = "tox_sfx_sawed-off_pickup";
                useSfx = "tox_sfx_sawed-off_fight";
                attackBonus = 2; randomBonus =1;
                break;
        }

        Animation animation = new Animation(animId, Animation.Layer.TILE);

        return
                Tox.world
                                .createEntity()
                                .addComponent(new Reward(Score.POINTS_GRAB_ITEM))
                                .addComponent(new Position(x, y))
                                .addComponent(new EquipBonus( attackBonus + MathUtils.random(0,randomBonus),0))
                                .addComponent(animation)
                                .addComponent(new Lootable(Lootable.Effect.HAND_PICKUP, pickupSfx,useSfx)).addComponent(new Selectable());
    }

    public static Entity createRandomTrinket(int x, int y) {

        String animId = "misc1";
        String pickupSfx = "tox_sfx_armor_pickup";
        int attackBonus = 0;
        int randomAttackBonus = 0;
        int defenseBonus = 0;
        int randomBonus = 0;
        EquipBonus.Effect effect = EquipBonus.Effect.NONE;
        switch ( MathUtils.random(0, 3) )
        {
            case 0:
                // Teddybear (passive, found in bunny world only, calms you down when you move, gets you off peak of your high quickly)
                effect = EquipBonus.Effect.TEDDY;
                break;
            case 1:
                // Stash (Intoxication drops slower at the low end)
                animId = "misc2";
                effect = EquipBonus.Effect.STASH;
                break;
            case 2:
                // Grenade (Saves you from enemy's killing blow, discarded after)
                animId = "misc3";
                effect = EquipBonus.Effect.GRENADE;
                break;
            case 3:
                // Medpack (On death fully heals you, puts intoxication at 50%, discarded after).
                animId = "misc4";
                effect = EquipBonus.Effect.MEDPACK;
                break;
            /*case 4:
                // Pet Cat / Dog (Roguelike Trope! Follows the player around an shit all beyond a small attack bonus. Story building!)
                animId = "misc5";
                effect = EquipBonus.Effect.PET;
                break;
            case 5:
                // Spiders & Ants (Cursed Item, PCP world, They're crawling all over you! D: Drain health for a couple of steps, will never kill though)
                animId = "misc6";
                effect = EquipBonus.Effect.SPIDERS_ANTS;
                break; */
        }

        final Animation animation = new Animation(animId, Animation.Layer.TILE);

        final EquipBonus bonus = new EquipBonus(attackBonus + MathUtils.random(0, randomAttackBonus), defenseBonus + MathUtils.random(0, randomBonus));
        bonus.effect = effect;
        return
                Tox.world
                                .createEntity()
                                .addComponent(new Reward(Score.POINTS_GRAB_ITEM))
                                .addComponent(new Position(x, y))
                                .addComponent(bonus)
                                .addComponent(animation)
                                .addComponent(new Lootable(Lootable.Effect.MISC_PICKUP, pickupSfx,null)).addComponent(new Selectable());
    }

    public static Entity createRandomArmor(int x, int y) {

        String animId = "vest1";
        String pickupSfx = "tox_sfx_armor_pickup";
        int defenseBonus = 1;
        int randomBonus = 0;
        switch ( MathUtils.random(0, 2) )
        {
            case 1:
                animId = "vest2";
                defenseBonus = 1; randomBonus =1;
                break;
        }

        Animation animation = new Animation(animId, Animation.Layer.TILE);

        return
                Tox.world
                                .createEntity()
                                .addComponent(new Reward(Score.POINTS_GRAB_ITEM))
                                .addComponent(new Position(x, y))
                                .addComponent(new EquipBonus(0, defenseBonus + MathUtils.random(0,randomBonus)))
                                .addComponent(animation)
                                .addComponent(new Lootable(Lootable.Effect.BODY_PICKUP, pickupSfx, null)).addComponent(new Selectable());
    }

    public static Entity createConcealment(int x, int y, Entity concealedEntity) {
        Entity conceal = createTile(x, y, "mystery", Animation.Layer.TILE_COVER)
                .addComponent(new Selectable())
                .addComponent(new Conceal(concealedEntity));
        Tox.world.getManager(GroupManager.class).add(conceal, CONCEAL_GROUP);
        return conceal;
    }

    public static Entity createClearedLocation(int x, int y) {
        Selectable selectable = new Selectable();
        selectable.maxDistance = 99999; // we allow players to return to cleared locations from anywhere.
        return createTile(x, y, "cleared", Animation.Layer.TILE)
                .addComponent(selectable)
                .addComponent(new Cleared());
    }

    public static Entity createDrugSwap(int x, int y) {
        Lootable lootable = new Lootable();
        lootable.pickupSfx="tox_sfx_consume_drugs";
        lootable.consumeInstantly = true;
        lootable.effect = Lootable.Effect.SWAP;
        return createTile(x, y, "bunnydrug")
                .addComponent(new Selectable())
                .addComponent(lootable);
    }

    public static Entity createDrugOtherWorld(int x, int y) {
        Lootable lootable = new Lootable();
        lootable.pickupSfx="tox_sfx_consume_drugs";
        lootable.consumeInstantly = true;
        lootable.effect = Lootable.Effect.PHASE;
        return createTile(x, y, "phasedrug")
                .addComponent(new Selectable())
                .addComponent(lootable);
    }

    public static Entity createGradientBackground() {
        Animation animation = new Animation("dialog-background", Animation.Layer.GLOBAL_BACKGROUND);
        animation.scale = BACKGROUND_SCALE;
        animation.frozen = true;
        animation.scale = 50;
        return Tox.world
                .createEntity()
                .addComponent(new Position(0, 0))
                .addComponent(animation);
    }

    public static Entity createScreen( String id ) {
        Animation animation = new Animation(id, Animation.Layer.SCREEN);
        animation.scale = BACKGROUND_SCALE;
        animation.frozen = true;
        return Tox.world
                .createEntity()
                .addComponent(new Position(0, 0))
                .addComponent(animation);
    }

    public static Entity createBoss(int x, int y, int level) {
        Entity boss = createMonster(x, y, level);
        boss.addComponent(new Named("The Jailer"));
        boss.addComponent(new Reward(Score.POINTS_KILL_JAILER));
        boss.getComponent(Animation.class).id = "boss";
        Color color = boss.getComponent(Animation.class).color;
        Tox.world.getManager(TagManager.class).register(BOSS_TAG, boss);
        return boss;
    }

    public static Entity addSfxToggle(int x, int y) {
        Animation animation = new Animation("sfx-on", Animation.Layer.SCREEN_PARTICLE2);
        animation.scale=2;
        Selectable selectable = new Selectable();
        selectable.maxDistance=9999;
        return Tox.world
                   .createEntity()
                   .addComponent(new Setting(Setting.SettingType.SFX))
                   .addComponent(selectable)
                   .addComponent(new Position(x, y))
                   .addComponent(animation);
    }


    public static Entity addMusicToggle(int x, int y) {
        Animation animation = new Animation("music-on", Animation.Layer.SCREEN_PARTICLE2);
        animation.scale=2;
        Selectable selectable = new Selectable();
        selectable.maxDistance=9999;
        return Tox.world
                   .createEntity()
                   .addComponent(new Setting(Setting.SettingType.MUSIC))
                   .addComponent(selectable)
                   .addComponent(new Position(x, y))
                   .addComponent(animation);
    }
}
