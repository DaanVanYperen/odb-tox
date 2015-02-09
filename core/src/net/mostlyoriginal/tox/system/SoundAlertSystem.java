package net.mostlyoriginal.tox.system;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.audio.Sound;
import net.mostlyoriginal.tox.Tox;
import net.mostlyoriginal.tox.ToxResource;
import net.mostlyoriginal.tox.ToxUtil;
import net.mostlyoriginal.tox.component.Animation;
import net.mostlyoriginal.tox.component.Health;
import net.mostlyoriginal.tox.component.Toxication;

/**
 * Alert the player to problems.
 *
 * @author Daan van Yperen
 */
public class SoundAlertSystem extends VoidEntitySystem {

    public final Sound healthLowSfx;
    public Long healthLoop;



    @Mapper
    ComponentMapper<Animation> am;


    @Mapper
    ComponentMapper<Health> hm;

    @Mapper
    ComponentMapper<Toxication> tm;

    public boolean healthAlarm;
    public boolean overdoseAlarm;
    public boolean withdrawalAlarm;
    public SoundSystem soundSystem;
    public CombatSystem combatSystem;

    public SoundAlertSystem() {

        healthLowSfx = Tox.resource.getSfx("health_low_alarm_loop");
    }

    @Override
    protected void initialize() {
        soundSystem = world.getSystem(SoundSystem.class);
        combatSystem = world.getSystem(CombatSystem.class);
    }

    private float alarmCooldown;
    private float healthAlarmCooldown;

    @Override
    protected void processSystem() {

        Entity player = ToxUtil.getPlayer();

        float overdoseAlarm = Tox.resource.universe == ToxResource.Universe.BUNNY ? 0.5f : Tox.resource.universe == ToxResource.Universe.PHASE ? 0.25f : 0.7f;

        healthAlarm = player != null && hm.has(player) && hm.get(player).damage > hm.get(player).maxHealth * 0.75f;
        this.overdoseAlarm = player != null && tm.has(player) && tm.get(player).toxication >= tm.get(player).maxToxication * overdoseAlarm;
        withdrawalAlarm = player != null && tm.has(player) && tm.get(player).toxication <= tm.get(player).maxToxication * 0.25f;

        alarmCooldown -= world.delta;
        healthAlarmCooldown -= world.delta;

        boolean playerAlive = combatSystem.isAlive(ToxUtil.getPlayer());

        if ( this.overdoseAlarm && alarmCooldown <= 0 )
        {
            if (playerAlive) {
                soundSystem.play("tox_sfx_fast_breathing");
            }
            replacePlayerSprites("player-overdose");
            alarmCooldown = 1.5f;
        }

        if ( withdrawalAlarm && alarmCooldown <= 0 )
        {
            if (playerAlive) {
                soundSystem.play("tox_sfx_withdrawal_alarm");
            }
            replacePlayerSprites("player-withdrawal");
            alarmCooldown = 3;
        }

        if ( !withdrawalAlarm && !this.overdoseAlarm)
        {
            replacePlayerSprites("player");
        }

        if ( !this.overdoseAlarm)
        {
            if ( Tox.resource.universe != ToxResource.Universe.REAL )
            {
                Tox.resource.loadUniverse(ToxResource.Universe.REAL);
                world.getSystem(PetalSystem.class).setEnabled(false);
            }
        }


        if ( this.healthAlarm && healthAlarmCooldown <= 0 )
        {
            if (playerAlive) {
                soundSystem.play("tox_sfx_overdose_alarm");
            }
            healthAlarmCooldown = 1;
        }

    }

    /**
     * Replace player sprite with status related sprite.
     * Except when dead. ;)
     *
     * @param animId
     */
    private void replacePlayerSprites(String animId) {
        Entity player = ToxUtil.getPlayer();
        if ( player != null )
        {
            Animation animation = am.get(player);
            if ( animation.id.equals("player-dead"))
                return;
            animation.id = animId;
        }

    }
}
