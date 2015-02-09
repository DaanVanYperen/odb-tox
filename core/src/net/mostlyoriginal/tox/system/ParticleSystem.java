package net.mostlyoriginal.tox.system;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import net.mostlyoriginal.tox.Tox;
import net.mostlyoriginal.tox.component.*;
import net.mostlyoriginal.tox.system.CameraShakeSystem;
import net.mostlyoriginal.tox.system.LifetimeSystem;
import net.mostlyoriginal.tox.system.PassiveSystem;
import net.mostlyoriginal.tox.system.SoundSystem;

/**
 * @author Daan van Yperen
 */
public class ParticleSystem extends PassiveSystem {


    public static final int EXPLOSION_FIRE_PARTICLES = 120;
    public static final int CLOUD_PARTICLES = 60;
    public static final int HEAL_PARTICLES = 4;
    public static final int EXPLOSION_BONUS_SOOT_PARTICLES = 5;
    public static final int AFTERMATH_SOOT_PARTICLES = 7;
    private static Vector2 tmp = new Vector2();
    public LifetimeSystem lifetimeSystem;
    public SoundSystem soundSystem;
    public CameraShakeSystem cameraShakeSystem;

    @Override
    protected void initialize() {
        lifetimeSystem = world.getSystem(LifetimeSystem.class);
        soundSystem = world.getSystem(SoundSystem.class);
        cameraShakeSystem = world.getSystem(CameraShakeSystem.class);
    }

    public void explosion(int cx, int cy, float radius)
    {
        float creationDelay = 0;

        soundSystem.play("tox_sfx_explosion");

        float sizeFactor = radius >= 150 ? 3 : 1;

        for ( int i=0;i< EXPLOSION_FIRE_PARTICLES*sizeFactor;i++)
        {
            if ( MathUtils.random(100) < 40 )
            {
                spawnFire(cx, cy, radius * 0.75f, creationDelay);
            } else {
                spawnSmoke(cx, cy, radius, creationDelay);
            }

            if ( MathUtils.random(0,100) < 25 )
                creationDelay += 0.01f;
        }
        for ( int i=0;i< EXPLOSION_BONUS_SOOT_PARTICLES*sizeFactor;i++)
        {
            spawnSmoke(cx, cy, radius * 0.75f, creationDelay);
            if ( MathUtils.random(0,100) < 25 )
                creationDelay += 0.01f;
        }


        //
        creationDelay += 0.1f;
        for ( int i=0;i< AFTERMATH_SOOT_PARTICLES;i++)
        {
            spawnSmoke(cx, cy + 5 * Animation.DEFAULT_SCALE, radius * 0.4f, creationDelay);
            spawnSmoke(cx, cy + 5 * Animation.DEFAULT_SCALE, radius * 0.4f, creationDelay);
            creationDelay += MathUtils.random(0.3f,0.8f);
        }
    }


    public void cloud(int cx, int cy, float radius)
    {
        float creationDelay = 0;
        soundSystem.play("tox_sfx_smokepuff");

        for ( int i=0;i< CLOUD_PARTICLES;i++)
        {
                spawnPuff(cx, cy, radius * 0.75f, creationDelay);

            if ( MathUtils.random(0,100) < 25 )
                creationDelay += 0.01f;
        }
    }

    public void combatCloud(int cx, int cy, float radius, float durationFactor)
    {
        float creationDelay = 0;
        float lastFightPuff = 0;

        for ( int i=0;i< CLOUD_PARTICLES * durationFactor;i++)
        {
            spawnPuff(cx, cy, radius, creationDelay);
            spawnPuff(cx, cy, radius, creationDelay);
            creationDelay += 0.016f;
            if ( lastFightPuff + 0.064f < creationDelay )
            {
                lastFightPuff = creationDelay;
                spawnFightPuff(cx, cy, radius * 0.75f, creationDelay);
            }
        }
    }


    public void heal(int cx, int cy)
    {
        float creationDelay = 0;

        for ( int i=0;i< HEAL_PARTICLES;i++)
        {
            spawnHeal(cx, cy + 12 * Animation.DEFAULT_SCALE, 10f, creationDelay, i % 2 == 0 ? "p-heal" : "p-zap");

            creationDelay += 0.3f;
        }
    }


    public void lvlup(int cx, int cy)
    {
        lifetimeSystem.addToWorldLater(addLevelupParticle((int) cx, (int) cy, "p-lvlup"), 1f);
    }

    private void spawnSmoke(int cx, int cy, float radius, float creationDelay) {
        tmp.set(MathUtils.random(radius),0).rotate(MathUtils.random(360)).add(cx, cy);
        lifetimeSystem.addToWorldLater(addParticle((int) tmp.x, (int) tmp.y, "p-soot"), creationDelay);
    }

    private void spawnPuff(int cx, int cy, float radius, float creationDelay) {
        tmp.set(MathUtils.random(radius),0).rotate(MathUtils.random(360)).add(cx, cy);
        lifetimeSystem.addToWorldLater(addParticle((int) tmp.x, (int) tmp.y, "p-cloud"), creationDelay);
    }

    private void spawnFightPuff(int cx, int cy, float radius, float creationDelay) {
        int deg = MathUtils.random(360);
        tmp.set(MathUtils.random(radius),0).rotate(deg).add(cx, cy);
        lifetimeSystem.addToWorldLater(addFightParticle((int) tmp.x, (int) tmp.y, "p-fight", deg), creationDelay);
    }

    private void spawnHeal(int cx, int cy, float radius, float creationDelay, String id) {
        tmp.set(MathUtils.random(radius),0).rotate(MathUtils.random(360)).add(cx, cy);
        Entity healParticle = addHealParticle((int) tmp.x, (int) tmp.y, id);
        lifetimeSystem.addToWorldLater(healParticle, creationDelay);
    }

    private void spawnFire(int cx, int cy, float radius, float creationDelay) {
        tmp.set(MathUtils.random(radius),0).rotate(MathUtils.random(360)).add(cx, cy);
        lifetimeSystem.addToWorldLater(addParticle((int) tmp.x, (int) tmp.y, "p-fire"), creationDelay);
    }


    private Entity addHealParticle(int cx, int cy, String name) {
        Animation animation = new Animation(name, Animation.Layer.PARTICLE);
        animation.age = MathUtils.random(4f);
        animation.color.a = 0.5f;
        animation.frozen=true;
        animation.scale = 2;
        final Physics physics = new Physics();
        physics.velocityX = MathUtils.random(-50f,50f);
        physics.velocityY = MathUtils.random(40f,45f);
        return Tox.world.createEntity().addComponent(new Position(cx - 5 * animation.scale, cy - 5 * animation.scale))
                .addComponent(animation)
                .addComponent(physics)
                .addComponent(new ColorAnimation(new Color(1, 1, 1, 0.9f), new Color(1, 1, 1, 0f), Interpolation.linear, 1/1.5f, 1.5f))
                .addComponent(new Terminal(1.5f));
    }


    public Entity addPetalParticle(int cx, int cy) {
        Animation animation = new Animation("p-petal", Animation.Layer.PARTICLE);
        animation.age = MathUtils.random(4f);
        animation.color.a = 0.5f;
        animation.frozen=false;
        animation.scale = MathUtils.random(0.5f,3);
        final Physics physics = new Physics();
        physics.velocityX = MathUtils.random(30f,70f);
        physics.velocityY = MathUtils.random(-40f,-45f);
        return Tox.world.createEntity().addComponent(new Position(cx - 5 * animation.scale, cy - 5 * animation.scale))
                .addComponent(animation)
                .addComponent(physics)
                .addComponent(new Terminal(10f));
    }

    private Entity addLevelupParticle(int cx, int cy, String name) {
        Animation animation = new Animation(name, Animation.Layer.PARTICLE2);
        animation.age = MathUtils.random(4f);
        animation.color.a = 0.5f;
        animation.frozen=true;
        animation.scale = 2;
        final Physics physics = new Physics();
        physics.velocityX = MathUtils.random(-10f,10f);
        physics.velocityY = 100f;
        return Tox.world.createEntity().addComponent(new Position(cx - 16 * animation.scale, cy - 4 * animation.scale))
                .addComponent(animation)
                .addComponent(physics)
                .addComponent(new ColorAnimation(new Color(1, 0, 1, 1f), new Color(0, 1, 0, 1f), Interpolation.bounce, 1f, 3f))
                .addComponent(new Terminal(3f));
    }

    private Entity addFightParticle(int cx, int cy, String name, int deg) {
        Animation animation = new Animation(name, Animation.Layer.PARTICLE2);
        animation.age = MathUtils.random(4f);
        animation.color.a = 1f;
        animation.frozen=true;
        animation.scale = 3;
        animation.rotation = deg;
        final Physics physics = new Physics();
        tmp.set(MathUtils.random(0,50f),0).rotate(deg);
        physics.velocityX = tmp.x;
        physics.velocityY = tmp.y;
        return Tox.world.createEntity().addComponent(new Position(cx - 5 * animation.scale, cy - 5 * animation.scale))
                .addComponent(animation)
                .addComponent(physics)
                .addComponent(new Terminal(0.5f));
    }

    private Entity addParticle(int cx, int cy, String name) {
        Animation animation = new Animation(name, Animation.Layer.PARTICLE);
        animation.age = MathUtils.random(4f);
        animation.color.a = 0.7f;
        animation.frozen=false;
        animation.speed = 0.5f;
        animation.scale = 3;
        final Physics physics = new Physics();
        physics.velocityX = MathUtils.random(-4f,4f);
        physics.velocityY = MathUtils.random(-4f,4f);
        return Tox.world.createEntity().addComponent(new Position(cx - 5 * animation.scale, cy - 5 * animation.scale))
                .addComponent(animation)
                .addComponent(physics)
                .addComponent(new Terminal(0.5f))
                .addComponent(new ColorAnimation(new Color(1, 1, 1, 0.6f), new Color(1, 1, 1, 0f), Interpolation.exp5, 2, 0.5f));
    }

    public Entity addMovingStarParticle(int cx, int cy) {
        Entity starParticle = addStarParticle(cx, cy);
        Animation anim = starParticle.getComponent(Animation.class);
        anim.layer = Animation.Layer.SCREEN_PARTICLE2;
        anim.scale = MathUtils.random(0.25f,0.75f);
        Physics physics = new Physics();
        physics.velocityX = MathUtils.random(-50f,50f) * 5f;
        physics.velocityY = MathUtils.random(25f,75f) * 5f;
        physics.gravity = 9.8f * 0.5f;
        starParticle.addComponent(physics);
        return starParticle;
    }

    public Entity addStarParticle(int cx, int cy) {
        Animation animation = new Animation("dialog-star", Animation.Layer.SCREEN_PARTICLE);
        animation.frozen= true;
        animation.scale = 2;
        return Tox.world.createEntity()
                .addComponent(new Position(cx, cy))
                .addComponent(animation)
                .addComponent(new Physics());
    }

    public void addSparticle(int x, int y) {
        soundSystem.play("tox_sfx_levelup");
        addStarParticle(x, y).addToWorld();
        cameraShakeSystem.shake(3);
        for ( int i=0; i< 20; i++)
        {
            addMovingStarParticle(x + 35, y + 25).addToWorld();
        }
    }
}
